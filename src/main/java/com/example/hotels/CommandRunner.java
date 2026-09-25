package com.example.hotels;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CommandRunner {

    private final AvailabilityService availability;

    public CommandRunner(AvailabilityService availability) {
        this.availability = availability;
    }

    /**
     * Anything unreadable gives back an empty line rather than throwing. The
     * results go to a checker reading one line out per line in, so failing on
     * one bad line would throw away every result after it.
     */
    public String run(String line, LocalDate today) {
        String text = line.trim();
        int open = text.indexOf('(');
        if (open < 0 || !text.endsWith(")")) {
            return "";
        }
        String name = text.substring(0, open).trim();
        String[] args = text.substring(open + 1, text.length() - 1).trim().split("\\s*,\\s*");
        if (args.length != 3) {
            return "";
        }
        try {
            return switch (name) {
                case "Availability" -> runAvailability(args);
                case "Search" -> runSearch(args, today);
                default -> "";
            };
        } catch (RuntimeException e) {
            return "";
        }
    }

    /** Availability(hotelId, date or date range, roomType) */
    private String runAvailability(String[] args) {
        return String.valueOf(availability.availableFor(args[0], args[2], DateRange.parse(args[1])));
    }

    /** Search(hotelId, daysAhead, roomType) */
    private String runSearch(String[] args, LocalDate today) {
        List<AvailabilityService.Range> ranges =
                availability.search(args[0], args[2], today, Integer.parseInt(args[1]));

        List<String> parts = new ArrayList<>();
        for (AvailabilityService.Range range : ranges) {
            parts.add(range.toString());
        }
        return String.join(", ", parts);
    }
}
