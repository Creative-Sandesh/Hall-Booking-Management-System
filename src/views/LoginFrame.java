package views;

import models.Customer;
import models.User;
import services.FileHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // PALETTE COLORS
    private final Color PRIMARY_COLOR = new Color(30, 58, 138); // #1E3A8A
    private final Color NEUTRAL_LIGHT = new Color(243, 244, 246); // #F3F4F6
    private final Color NEUTRAL_DARK = new Color(55, 65, 81); // #374151

    public LoginFrame() {
        setTitle("Hall Booking Management System - Login");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel with Padding
        JPanel mainPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        mainPanel.setBackground(NEUTRAL_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // 1. Title Label
        JLabel titleLabel = new JLabel("Welcome Back", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(NEUTRAL_DARK);
        mainPanel.add(titleLabel);

        // 2. Username
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(NEUTRAL_LIGHT);
        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(NEUTRAL_DARK);
        usernameField = new JTextField();
        userPanel.add(userLabel, BorderLayout.NORTH);
        userPanel.add(usernameField, BorderLayout.CENTER);
        mainPanel.add(userPanel);

        // 3. Password
        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setBackground(NEUTRAL_LIGHT);
        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(NEUTRAL_DARK);
        passwordField = new JPasswordField();
        passPanel.add(passLabel, BorderLayout.NORTH);
        passPanel.add(passwordField, BorderLayout.CENTER);
        mainPanel.add(passPanel);

        // 4. Login Button
        loginButton = new JButton("Login");
        styleButton(loginButton); // Apply custom style
        mainPanel.add(loginButton);

        // Add main panel to frame
        add(mainPanel);

        // Login Logic
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        setVisible(true);
    }

    private void styleButton(JButton btn) {
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
    }

    private void performLogin() {
        String inputUser = usernameField.getText();
        String inputPass = new String(passwordField.getPassword());

        List<User> users = FileHandler.loadUsers();
        boolean found = false;

        for (User u : users) {
            if (u.getUsername().equals(inputUser) && u.getPassword().equals(inputPass)) {
                found = true;
                if (u.getRole().equalsIgnoreCase("CUSTOMER")) {
                    new CustomerDashboard((models.Customer) u);
                } else if (u.getRole().equalsIgnoreCase("ADMIN")) {
                    new AdminDashboard((models.Administrator) u);
                }// else if (u.getRole().equalsIgnoreCase("SCHEDULER")) {
//                    new SchedulerDashboard((models.Scheduler) u);
//                } else if (u.getRole().equalsIgnoreCase("MANAGER")) {
//                    new ManagerDashboard((models.Manager) u);
//                }
                this.dispose();
                return;
            }
        }
        if (!found) {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password!", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}