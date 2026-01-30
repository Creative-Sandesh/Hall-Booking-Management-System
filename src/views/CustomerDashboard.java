package views;

import models.Customer;
import javax.swing.*;

public class CustomerDashboard extends BaseDashboard {

    private Customer customer;

    public CustomerDashboard(Customer customer) {
        // Pass the specifics to the Parent: (User, Title, Color)
        super("Customer Dashboard", customer, COLOR_PRIMARY);
        this.customer = customer;

        // Add Menu Buttons
        addSidebarButton("View Available Halls", e -> showHalls());
        addSidebarButton("My Bookings", e -> showBookings());
        addSidebarButton("My Profile", e -> showProfile());

        // Add Logout at bottom
        addLogoutButton();

        setVisible(true);
    }

    private void showHalls() {
        setPage(new JLabel("Display Table of Halls Here...", SwingConstants.CENTER));
    }

    private void showBookings() {
        setPage(new JLabel("Display Customer Bookings Here...", SwingConstants.CENTER));
    }

    private void showProfile() {
        // Simple Profile Panel
        JPanel p = new JPanel();
        p.add(new JLabel("Name: " + customer.getName()));
        p.add(new JLabel("Email: " + customer.getEmail()));
        setPage(p);
    }
}