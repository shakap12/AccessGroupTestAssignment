package com.example.hotels;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** The Availability examples from the brief. Availability ignores today's date. */
class AvailabilityTest {

    private static final LocalDate ANY_DAY = LocalDate.of(2024, 9, 1);

    private final CommandRunner runner = TestData.runner();

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "'Availability(H1, 20240901, SGL)', '2'",
            "'Availability(H1, 20240901-20240903, DBL)', '1'",
            "'Availability(H1, 20240902, SGL)', '1'",
            "'Availability(H1, 20240903, SGL)', '-1'",
            "'Availability(H1, 20240904, SGL)', '1'",
            "'Availability(H1, 20240906, SGL)', '2'",
            "'Availability(H1, 20240902-20240903, SGL)', '1'",
            "'Availability(H1, 20240902-20240905, SGL)', '-1'",
            "'Availability(H1, 20240904-20240906, SGL)', '1'",
            "'Availability(H1, 20240903, DBL)', '2'",
            "'Availability(H2, 20240901, SGL)', '0'",
            "'Availability(H2, 20240907, SGL)', '1'",
    })
    void examplesFromTheBrief(String input, String expected) {
        assertEquals(expected, runner.run(input, ANY_DAY));
    }

    @Test
    void theDepartureNightIsFreeAgain() {
        // One SGL leaves on the 5th as another arrives, then both are gone by the 6th.
        assertEquals("1", runner.run("Availability(H1, 20240905, SGL)", ANY_DAY));
        assertEquals("2", runner.run("Availability(H1, 20240906, SGL)", ANY_DAY));
    }

    @Test
    void aRangeTakesItsWorstNight() {
        assertEquals("-1", runner.run("Availability(H1, 20240902-20240904, SGL)", ANY_DAY));
    }

    @Test
    void unknownHotelOrRoomTypeIsZero() {
        assertEquals("0", runner.run("Availability(H9, 20240901, SGL)", ANY_DAY));
        assertEquals("0", runner.run("Availability(H1, 20240901, PENTHOUSE)", ANY_DAY));
    }

    @Test
    void datesAwayFromAnyBookingGiveTheFullCount() {
        assertEquals("2", runner.run("Availability(H1, 20250101, SGL)", ANY_DAY));
    }

    @Test
    void unreadableLinesStillGiveOneLineOfOutput() {
        assertEquals("", runner.run("nonsense", ANY_DAY));
        assertEquals("", runner.run("Availability(H1, notadate, SGL)", ANY_DAY));
        assertEquals("", runner.run("Availability(H1, 20240901)", ANY_DAY));
        assertEquals("", runner.run("Search(H1, many, SGL)", ANY_DAY));
        assertEquals("", runner.run("Unknown(H1, 20240901, SGL)", ANY_DAY));
    }

    @Test
    void spacingAroundArgumentsDoesNotMatter() {
        assertEquals("2", runner.run("  Availability( H1 ,20240901,  SGL )  ", ANY_DAY));
    }
}
