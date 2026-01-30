package views;

import models.*;
import services.BookingService;
import utils.IdGenerator;
import utils.DatePicker; // Import the new tool

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class BookingForm extends JFrame {

    private JTextField dateField; // Plain Text field, but we will lock it
    private JComboBox<String> startBox, endBox;
    private Customer customer;
    private Hall hall;

    public BookingForm(Customer customer, Hall hall) {
        this.customer = customer;
        this.hall = hall;

        setTitle("Book Hall: " + hall.getName());
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(8, 1, 10, 10)); // Added extra row for button
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Hall: " + hall.getName()));
        panel.add(new JLabel("Rate: RM " + hall.getPricePerHour() + "/hr"));

        // --- NEW DATE SECTION ---
        panel.add(new JLabel("Date:"));
        JPanel datePanel = new JPanel(new BorderLayout(5,0));

        dateField = new JTextField();
        dateField.setEditable(false); // User cannot type manually
        dateField.setText(LocalDate.now().toString()); // Default to today

        JButton btnPickDate = new JButton("📅");
        btnPickDate.addActionListener(e -> {
            // Open our custom calendar
            new DatePicker(this, dateField).setVisible(true);
        });

        datePanel.add(dateField, BorderLayout.CENTER);
        datePanel.add(btnPickDate, BorderLayout.EAST);
        panel.add(datePanel);

        // --- TIME SELECTION ---
        String[] timeSlots = {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"};

        panel.add(new JLabel("Start Time:"));
        startBox = new JComboBox<>(timeSlots);
        panel.add(startBox);

        panel.add(new JLabel("End Time:"));
        endBox = new JComboBox<>(timeSlots);
        endBox.setSelectedIndex(1);
        panel.add(endBox);

        JButton btnConfirm = new JButton("Confirm Booking");
        btnConfirm.setBackground(new Color(30, 58, 138));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.addActionListener(e -> attemptBooking());

        panel.add(btnConfirm);
        add(panel);
        setVisible(true);
    }

    private void attemptBooking() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText()); // Read from our field
            LocalTime start = LocalTime.parse((String) startBox.getSelectedItem());
            LocalTime end = LocalTime.parse((String) endBox.getSelectedItem());

            if (!start.isBefore(end)) {
                JOptionPane.showMessageDialog(this, "End time must be after start time!");
                return;
            }

            long duration = java.time.Duration.between(start, end).toHours();
            if (duration < 1) duration = 1;
            double total = duration * hall.getPricePerHour();

            int choice = JOptionPane.showConfirmDialog(this,
                    "Total: RM " + total + "\nProceed?", "Confirm", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                String newId = IdGenerator.generateNextId("BOOKING");
                Booking b = new Booking(newId, customer.getEmail(), hall.getId(), date, start, end, total, "PAID");

                String result = BookingService.createBooking(b);
                if (result.equals("Success")) {
                    JOptionPane.showMessageDialog(this, "Booking Successful!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, result);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}