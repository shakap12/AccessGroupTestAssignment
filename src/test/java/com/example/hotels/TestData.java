package com.example.hotels;

import java.io.File;

/** A runner over the example files from the brief. */
final class TestData {

    private TestData() {
    }

    static CommandRunner runner() {
        try {
            HotelStore store = HotelStore.load(file("hotels.json"), file("bookings.json"));
            return new CommandRunner(new AvailabilityService(store));
        } catch (Exception e) {
            throw new IllegalStateException("Could not load the test data", e);
        }
    }

    private static File file(String name) throws Exception {
        return new File(TestData.class.getClassLoader().getResource(name).toURI());
    }
}
