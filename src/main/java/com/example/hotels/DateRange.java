package com.example.hotels;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * A run of nights. The start is included, the end is not, which is how hotel
 * stays work: arrive on the 1st, leave on the 3rd, and you have slept there on
 * the 1st and the 2nd only.
 */
public record DateRange(LocalDate start, LocalDate end) {

    private static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static LocalDate date(String text) {
        return LocalDate.parse(text.trim(), YYYYMMDD);
    }

    public static DateRange night(LocalDate date) {
        return new DateRange(date, date.plusDays(1));
    }

    /** Reads either "20240901" or "20240901-20240903". */
    public static DateRange parse(String text) {
        int dash = text.indexOf('-');
        if (dash < 0) {
            return night(date(text));
        }
        LocalDate start = date(text.substring(0, dash));
        LocalDate end = date(text.substring(dash + 1));
        // "20240901-20240901" is zero nights by the rule above, which is not a
        // stay anyone can book, so read it as the single night.
        return end.isAfter(start) ? new DateRange(start, end) : night(start);
    }

    public int nights() {
        return (int) ChronoUnit.DAYS.between(start, end);
    }

    public boolean covers(LocalDate night) {
        return !night.isBefore(start) && night.isBefore(end);
    }

    @Override
    public String toString() {
        return start.format(YYYYMMDD) + "-" + end.format(YYYYMMDD);
    }
}
