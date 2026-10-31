package com.testtribe.api.utils;

import com.testtribe.api.pojo.Booking;
import com.testtribe.api.pojo.BookingDates;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public final class BookingPayloadFactory {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";

    private BookingPayloadFactory() {
    }

    public static Booking validBooking() {
        return baseBooking().additionalneeds("Breakfast").build();
    }

    public static Booking validBookingWithoutAdditionalNeeds() {
        return baseBooking().build();
    }

    private static Booking.BookingBuilder baseBooking() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        LocalDate today = LocalDate.now();

        return Booking.builder()
                .firstname(randomName("First"))
                .lastname(randomName("Last"))
                .totalprice(random.nextInt(100, 1000))
                .depositpaid(random.nextBoolean())
                .bookingdates(BookingDates.builder()
                        .checkin(today.plusDays(1).format(DATE_FORMAT))
                        .checkout(today.plusDays(5).format(DATE_FORMAT))
                        .build());
    }

    private static String randomName(String prefix) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder name = new StringBuilder(prefix);
        for (int i = 0; i < 8; i++) {
            name.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
        }
        return name.toString();
    }
}
