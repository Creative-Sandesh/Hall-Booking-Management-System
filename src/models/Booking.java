package models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Booking {
    private String bookingId;
    private String customerEmail; // Connects to Customer
    private String hallId;        // Connects to Hall
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private double totalAmount;
    private String status;        // "PAID", "CANCELLED", "PENDING"

    public Booking(String bookingId, String customerEmail, String hallId, LocalDate date, LocalTime startTime, LocalTime endTime, double totalAmount, String status) {
        this.bookingId = bookingId;
        this.customerEmail = customerEmail;
        this.hallId = hallId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // Getters
    public String getBookingId() { return bookingId; }
    public String getCustomerEmail() { return customerEmail; }
    public String getHallId() { return hallId; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public void setCustomerEmail(String customerEmail) {this.customerEmail = customerEmail;}

    public String toFileString() {
        return bookingId + "," +
                customerEmail + "," +
                hallId + "," +
                date.toString() + "," +
                startTime.toString() + "," +
                endTime.toString() + "," +
                totalAmount + "," +
                status;
    }
}