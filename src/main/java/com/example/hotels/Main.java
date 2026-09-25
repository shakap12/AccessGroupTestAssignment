package com.example.hotels;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * myapp --hotels hotels.json --bookings bookings.json
 *
 * <p>Reads commands from standard input, one per line, and writes one line of
 * output for each. Stops on a blank line or at the end of the input.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        String hotelsFile = valueOf(args, "--hotels");
        String bookingsFile = valueOf(args, "--bookings");

        if (hotelsFile == null || bookingsFile == null) {
            System.err.println("Usage: myapp --hotels <hotels.json> --bookings <bookings.json>");
            System.exit(1);
        }

        HotelStore store = HotelStore.load(new File(hotelsFile), new File(bookingsFile));
        CommandRunner runner = new CommandRunner(new AvailabilityService(store));

        LocalDate today = LocalDate.now();

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line;
        while ((line = input.readLine()) != null) {
            if (line.trim().isEmpty()) {
                break;
            }
            System.out.print(runner.run(line, today) + "\n");
            System.out.flush();
        }
    }

    private static String valueOf(String[] args, String flag) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(flag)) {
                return args[i + 1];
            }
        }
        return null;
    }
}
