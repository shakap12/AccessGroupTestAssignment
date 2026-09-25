package com.example.hotels;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateRangeTest {

    private static final LocalDate SEP_1 = LocalDate.of(2024, 9, 1);
    private static final LocalDate SEP_2 = LocalDate.of(2024, 9, 2);
    private static final LocalDate SEP_3 = LocalDate.of(2024, 9, 3);

    @Test
    void coversTheArrivalNightButNotTheDepartureNight() {
        DateRange stay = new DateRange(SEP_1, SEP_3);

        assertTrue(stay.covers(SEP_1));
        assertTrue(stay.covers(SEP_2));
        assertFalse(stay.covers(SEP_3));
        assertEquals(2, stay.nights());
    }

    @Test
    void aSingleDateIsOneNight() {
        assertEquals(new DateRange(SEP_1, SEP_2), DateRange.parse("20240901"));
    }

    @Test
    void aRangeKeepsItsEndExclusive() {
        assertEquals(new DateRange(SEP_1, SEP_3), DateRange.parse("20240901-20240903"));
    }

    @Test
    void aRangeEndingWhereItStartsIsOneNight() {
        assertEquals(1, DateRange.parse("20240901-20240901").nights());
    }

    @Test
    void printsTheWaySearchNeedsIt() {
        assertEquals("20240901-20240903", new DateRange(SEP_1, SEP_3).toString());
    }
}
