package views;

import models.Customer;
import javax.swing.*;
import java.awt.*;

public class CustomerDashboard extends JFrame {

    private JPanel mainContentPanel;
    private Customer customer;

    // PALETTE
    private final Color PRIMARY_COLOR = new Color(30, 58, 138); // Header
    private final Color SIDEBAR_COLOR = new Color(55, 65, 81); // Neutral Dark
    private final Color BG_COLOR = new Color(243, 244, 246); // Neutral Light

    public CustomerDashboard(Customer customer) {
        this.customer = customer;
        setTitle("Customer Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(800, 60));

        JLabel welcomeLabel = new JLabel("Customer Portal | " + customer.getName());
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(welcomeLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- SIDEBAR ---
        JPanel sidebarPanel = new JPanel(new GridLayout(10, 1, 10, 10));
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(220, 600));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnViewHalls = createSidebarButton("View Available Halls");
        JButton btnBookings = createSidebarButton("My Bookings");
        JButton btnProfile = createSidebarButton("My Profile");
        JButton btnLogout = createSidebarButton("Logout");

        sidebarPanel.add(btnViewHalls);
        sidebarPanel.add(btnBookings);
        sidebarPanel.add(btnProfile);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(btnLogout);
        add(sidebarPanel, BorderLayout.WEST);

        // --- CONTENT ---
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(BG_COLOR); // Neutral Light background
        mainContentPanel.add(new JLabel("Select an option from the sidebar.", SwingConstants.CENTER));
        add(mainContentPanel, BorderLayout.CENTER);

        // Actions
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        // (Other buttons can have logic added later)

        setVisible(true);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(SIDEBAR_COLOR); // Matches sidebar
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }
}