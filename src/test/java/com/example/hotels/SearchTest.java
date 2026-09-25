package com.example.hotels;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** The Search examples from the brief, each run against the date it names. */
class SearchTest {

    private static final LocalDate SEP_1 = LocalDate.of(2024, 9, 1);
    private static final LocalDate SEP_2 = LocalDate.of(2024, 9, 2);
    private static final LocalDate SEP_3 = LocalDate.of(2024, 9, 3);

    private final CommandRunner runner = TestData.runner();

    @Test
    void aYearAhead() {
        assertEquals(
                "(20240901-20240902, 2), (20240902-20240903, 1), (20240904-20240906, 1), (20240906-20250901, 2)",
                runner.run("Search(H1, 365, SGL)", SEP_1));
    }

    @Test
    void oneNightAhead() {
        assertEquals("(20240901-20240902, 2)", runner.run("Search(H1, 1, SGL)", SEP_1));
    }

    @Test
    void fourNightsAhead() {
        assertEquals(
                "(20240901-20240902, 2), (20240902-20240903, 1), (20240904-20240905, 1)",
                runner.run("Search(H1, 4, SGL)", SEP_1));
    }

    @Test
    void sevenNightsAtTheFullyBookedHotel() {
        // H2's only single is taken until the 7th, so that is the first free night.
        assertEquals("(20240907-20240908, 1)", runner.run("Search(H2, 7, SGL)", SEP_1));
    }

    @Test
    void nothingAvailableGivesAnEmptyLine() {
        assertEquals("", runner.run("Search(H2, 1, SGL)", SEP_1));
        assertEquals("", runner.run("Search(H1, 1, SGL)", SEP_3));
    }

    @Test
    void anOverbookedNightSplitsTheRangesEitherSideOfIt() {
        // The 3rd is at -1, so it is left out and the 2nd and 4th stay apart
        // even though both have one room free.
        assertEquals(
                "(20240902-20240903, 1), (20240904-20240905, 1)",
                runner.run("Search(H1, 3, SGL)", SEP_2));
    }

    @Test
    void nightsOnlyJoinWhenTheCountMatches() {
        assertEquals(
                "(20240901-20240902, 2), (20240902-20240903, 1)",
                runner.run("Search(H1, 2, SGL)", SEP_1));
    }

    @Test
    void unknownHotelOrRoomTypeGivesAnEmptyLine() {
        assertEquals("", runner.run("Search(H9, 365, SGL)", SEP_1));
        assertEquals("", runner.run("Search(H1, 365, PENTHOUSE)", SEP_1));
    }

    @Test
    void zeroDaysAheadGivesAnEmptyLine() {
        assertEquals("", runner.run("Search(H1, 0, SGL)", SEP_1));
    }
}
