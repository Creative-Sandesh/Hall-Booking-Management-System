package models;

import java.time.LocalDate;

public class Issue {
    private String issueId;
    private String bookingId;    // Link to the specific booking
    private String customerEmail; // Who reported it
    private String description;   // The complaint text
    private String status;        // "OPEN" or "RESOLVED"
    private LocalDate dateReported;

    public Issue(String issueId, String bookingId, String customerEmail, String description, String status, LocalDate dateReported) {
        this.issueId = issueId;
        this.bookingId = bookingId;
        this.customerEmail = customerEmail;
        this.description = description;
        this.status = status;
        this.dateReported = dateReported;
    }

    // Getters
    public String getIssueId() { return issueId; }
    public String getBookingId() { return bookingId; }
    public String getCustomerEmail() { return customerEmail; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDate getDateReported() { return dateReported; }

    public void setStatus(String status) { this.status = status; }

    public String toFileString() {
        // CSV Format: I001,B005,ali@gmail.com,AC not working,OPEN,2024-01-30
        return issueId + "," + bookingId + "," + customerEmail + "," + description + "," + status + "," + dateReported;
    }
}