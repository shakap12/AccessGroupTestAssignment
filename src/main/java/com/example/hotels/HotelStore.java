package com.example.hotels;

import com.example.hotels.model.Booking;
import com.example.hotels.model.Hotel;
import com.example.hotels.model.Room;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The loaded hotels and bookings. Both are grouped by hotel and room type as
 * they are read, since that is the only way availability ever looks them up.
 */
public class HotelStore {

    private final Map<String, Hotel> hotels = new HashMap<>();
    private final Map<String, Integer> roomCounts = new HashMap<>();
    private final Map<String, List<Booking>> bookings = new HashMap<>();

    public HotelStore(List<Hotel> hotelList, List<Booking> bookingList) {
        for (Hotel hotel : hotelList) {
            hotels.put(hotel.id(), hotel);
            for (Room room : hotel.rooms()) {
                roomCounts.merge(key(hotel.id(), room.roomType()), 1, Integer::sum);
            }
        }
        for (Booking booking : bookingList) {
            bookings.computeIfAbsent(key(booking.hotelId(), booking.roomType()), k -> new ArrayList<>())
                    .add(booking);
        }
    }

    public static HotelStore load(File hotelsFile, File bookingsFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        List<Hotel> hotels = mapper.readValue(hotelsFile, new TypeReference<>() {
        });
        List<Booking> bookings = mapper.readValue(bookingsFile, new TypeReference<>() {
        });
        return new HotelStore(hotels, bookings);
    }

    private static String key(String hotelId, String roomType) {
        return hotelId + "|" + roomType;
    }

    public boolean hasHotel(String hotelId) {
        return hotels.containsKey(hotelId);
    }

    public int roomCount(String hotelId, String roomType) {
        return roomCounts.getOrDefault(key(hotelId, roomType), 0);
    }

    /** Identical bookings are kept separately: two guests really do take two rooms. */
    public List<Booking> bookingsFor(String hotelId, String roomType) {
        return bookings.getOrDefault(key(hotelId, roomType), List.of());
    }
}
