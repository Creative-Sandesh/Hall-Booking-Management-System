package views;

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

    public LoginFrame() {
        // 1. Setup the Window
        setTitle("Hall Booking Management System - Login");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setLayout(new GridLayout(4, 1, 10, 10)); // Simple layout

        // 2. Add Title Label
        JLabel titleLabel = new JLabel("Welcome to Hall Booking Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel);

        // 3. Username Panel
        JPanel userPanel = new JPanel();
        userPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        userPanel.add(usernameField);
        add(userPanel);

        // 4. Password Panel
        JPanel passPanel = new JPanel();
        passPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        passPanel.add(passwordField);
        add(passPanel);

        // 5. Login Button
        loginButton = new JButton("Login");
        add(loginButton);

        // 6. Button Action (The Logic)
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        setVisible(true);
    }

    private void performLogin() {
        String inputUser = usernameField.getText();
        String inputPass = new String(passwordField.getPassword());

        // Load users from file
        List<User> users = FileHandler.loadUsers();
        boolean found = false;

        for (User u : users) {
            if (u.getUsername().equals(inputUser) && u.getPassword().equals(inputPass)) {
                found = true;
                JOptionPane.showMessageDialog(this, "Login Successful!\nWelcome " + u.getName() + "\nRole: " + u.getRole());

                // TODO: Later we will open the specific dashboard window here
                //u.openDashboard();

                this.dispose(); // Close login window
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password!", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}