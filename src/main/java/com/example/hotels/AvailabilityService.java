package com.example.hotels;

import com.example.hotels.model.Booking;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Free rooms on one night = rooms of that type, minus bookings covering that
 * night. Both commands are built on that.
 */
public class AvailabilityService {

    /** One entry in a Search result. */
    public record Range(DateRange dates, int count) {
        @Override
        public String toString() {
            return "(" + dates + ", " + count + ")";
        }
    }

    private final HotelStore store;

    public AvailabilityService(HotelStore store) {
        this.store = store;
    }

    /**
     * A guest needs the same room for every night of the stay, so the answer is
     * the worst night, not a total or an average. It can be negative, since
     * hotels accept overbookings.
     */
    public int availableFor(String hotelId, String roomType, DateRange stay) {
        if (!store.hasHotel(hotelId)) {
            return 0;
        }
        int rooms = store.roomCount(hotelId, roomType);
        List<Booking> bookings = store.bookingsFor(hotelId, roomType);

        int fewest = rooms;
        for (LocalDate night = stay.start(); night.isBefore(stay.end()); night = night.plusDays(1)) {
            fewest = Math.min(fewest, rooms - booked(bookings, night));
        }
        return fewest;
    }

    /**
     * Every stretch of nights in the next {@code daysAhead} nights with a room
     * free. Today is passed in rather than read from the clock here so the
     * examples in the brief, which each name a date, can be run as tests.
     */
    public List<Range> search(String hotelId, String roomType, LocalDate today, int daysAhead) {
        List<Range> found = new ArrayList<>();
        if (!store.hasHotel(hotelId) || daysAhead <= 0) {
            return found;
        }
        LocalDate windowEnd = today.plusDays(daysAhead);
        int rooms = store.roomCount(hotelId, roomType);
        List<Booking> bookings = store.bookingsFor(hotelId, roomType);

        // Walk night by night, holding on to the run we are part way through.
        // A run continues only while the count stays the same, so a fully
        // booked night both ends a run and keeps the next one separate.
        LocalDate runStart = null;
        int runCount = 0;

        for (LocalDate night = today; night.isBefore(windowEnd); night = night.plusDays(1)) {
            int free = rooms - booked(bookings, night);
            if (runStart != null && free == runCount) {
                continue;
            }
            if (runStart != null) {
                found.add(new Range(new DateRange(runStart, night), runCount));
                runStart = null;
            }
            if (free > 0) {
                runStart = night;
                runCount = free;
            }
        }
        if (runStart != null) {
            found.add(new Range(new DateRange(runStart, windowEnd), runCount));
        }
        return found;
    }

    private static int booked(List<Booking> bookings, LocalDate night) {
        int count = 0;
        for (Booking booking : bookings) {
            if (booking.stay().covers(night)) {
                count++;
            }
        }
        return count;
    }
}
