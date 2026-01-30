package views;

import models.Administrator;
import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    // PALETTE
    private final Color ADMIN_COLOR = new Color(220, 38, 38); // Error Red
    private final Color SIDEBAR_COLOR = new Color(55, 65, 81);
    private final Color BG_COLOR = new Color(243, 244, 246);

    public AdminDashboard(Administrator admin) {
        setTitle("Admin Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(ADMIN_COLOR); // Red Header

        JLabel title = new JLabel("Admin Control | " + admin.getName());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(title);
        add(headerPanel, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel(new GridLayout(10, 1, 10, 10));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(220, 600));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnManageHalls = createSidebarButton("Manage Halls");
        JButton btnManageUsers = createSidebarButton("Manage Users");
        JButton btnLogout = createSidebarButton("Logout");

        sidebar.add(btnManageHalls);
        sidebar.add(btnManageUsers);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);
        add(sidebar, BorderLayout.WEST);

        // Content
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG_COLOR);
        content.add(new JLabel("Admin Panel Area", SwingConstants.CENTER));
        add(content, BorderLayout.CENTER);

        btnLogout.addActionListener(e -> { dispose(); new LoginFrame(); });
        setVisible(true);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(SIDEBAR_COLOR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }
}