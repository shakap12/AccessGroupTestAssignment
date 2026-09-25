package com.example.hotels.model;

import com.example.hotels.DateRange;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * One reservation. It is for a room type rather than a named room, so it uses
 * up one room of that type. roomRate has no effect on availability.
 */
public record Booking(
        String hotelId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd") LocalDate arrival,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd") LocalDate departure,
        String roomType,
        String roomRate) {

    public DateRange stay() {
        return new DateRange(arrival, departure);
    }
}
