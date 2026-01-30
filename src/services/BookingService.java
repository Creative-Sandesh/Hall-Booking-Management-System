package services;

import models.Booking;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class BookingService {

    // 1. Main method to attempt a booking
    public static String createBooking(Booking newBooking) {

        // RULE 1: Check Operating Hours (8 AM - 6 PM)
        LocalTime open = LocalTime.of(8, 0);
        LocalTime close = LocalTime.of(18, 0);

        if (newBooking.getStartTime().isBefore(open) || newBooking.getEndTime().isAfter(close)) {
            return "Error: Hall Symphony is only open from 8:00 AM to 6:00 PM.";
        }

        // RULE 2: Start time must be before End time
        if (!newBooking.getStartTime().isBefore(newBooking.getEndTime())) {
            return "Error: End time must be after Start time.";
        }

        // RULE 3: Check for Overlaps with existing bookings
        if (!isTimeSlotAvailable(newBooking)) {
            return "Error: This Hall is already booked for the selected time.";
        }

        // If all rules pass, Save it!
        boolean saved = FileHandler.saveBooking(newBooking);
        return saved ? "Success" : "Error: System failed to save file.";
    }

    // Helper: Check if the slot is free
    private static boolean isTimeSlotAvailable(Booking newBooking) {
        List<Booking> allBookings = FileHandler.loadBookings();

        for (Booking existing : allBookings) {
            // Only check bookings for the SAME Hall and SAME Date
            if (existing.getHallId().equals(newBooking.getHallId()) &&
                    existing.getDate().equals(newBooking.getDate()) &&
                    !existing.getStatus().equals("CANCELLED")) { // Ignore cancelled ones

                // Check for time overlap
                // (StartA < EndB) and (EndA > StartB) is the standard formula for overlap
                if (newBooking.getStartTime().isBefore(existing.getEndTime()) &&
                        newBooking.getEndTime().isAfter(existing.getStartTime())) {
                    return false; // Collision detected!
                }
            }
        }
        return true; // No collision found
    }
}