package views;

import models.Booking;
import models.Customer;
import models.Hall;
import services.FileHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerDashboard extends BaseDashboard {

    private Customer customer;

    public CustomerDashboard(Customer customer) {
        // Pass title, user, and the specific color for Customers (Blue)
        super("Customer Dashboard", customer, COLOR_PRIMARY);
        this.customer = customer;

        // --- SIDEBAR MENU ---
        addSidebarButton("View Available Halls", e -> showHallsView());
        addSidebarButton("My Bookings", e -> showBookingsView());
        addSidebarButton("My Profile", e -> showProfileView());

        // Add Logout (Helper from BaseDashboard)
        addLogoutButton();

        // Default Page on Launch
        showHallsView();

        setVisible(true);
    }

    // ==========================================
    // VIEW 1: AVAILABLE HALLS (Booking)
    // ==========================================
    private void showHallsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("  Available Halls for Reservation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // Load Data
        List<Hall> halls = FileHandler.loadHalls();

        // Table Setup
        String[] columns = {"ID", "Hall Name", "Rate (RM/hr)", "Capacity", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Hall h : halls) {
            String status = h.isMaintenance() ? "Maintenance" : "Available";
            model.addRow(new Object[]{
                    h.getId(),
                    h.getName(),
                    String.format("%.2f", h.getPricePerHour()),
                    h.getCapacity(),
                    status
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setDefaultEditor(Object.class, null); // Disable editing
        JScrollPane scrollPane = new JScrollPane(table);

        // Actions Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBook = new JButton("Book Selected Hall");
        btnBook.setBackground(new Color(40, 167, 69)); // Green
        btnBook.setForeground(Color.WHITE);

        btnBook.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a hall first.");
                return;
            }

            String hallId = (String) model.getValueAt(row, 0);
            String status = (String) model.getValueAt(row, 4);

            if (status.equalsIgnoreCase("Maintenance")) {
                JOptionPane.showMessageDialog(this, "This hall is under maintenance.");
                return;
            }

            // Find full Hall object
            Hall selectedHall = halls.stream()
                    .filter(h -> h.getId().equals(hallId))
                    .findFirst()
                    .orElse(null);

            if (selectedHall != null) {
                new BookingForm(customer, selectedHall); // Open Booking Popup
            }
        });

        bottomPanel.add(btnBook);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        setPage(panel);
    }

    // ==========================================
    // VIEW 2: MY BOOKINGS (Filter, Cancel, Issue)
    // ==========================================
    private void showBookingsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // --- TOP BAR: Title + Filter ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel titleLabel = new JLabel("My Booking History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        String[] filters = {"All Bookings", "Upcoming", "Past History"};
        JComboBox<String> filterBox = new JComboBox<>(filters);

        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(new JLabel("Filter: "));
        filterPanel.add(filterBox);

        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(filterPanel, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // --- TABLE SETUP ---
        String[] columns = {"Booking ID", "Hall Name", "Date", "Time", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);

        // --- DATA LOADING LOGIC (Runnable to support refresh) ---
        List<Booking> allBookings = FileHandler.loadBookings(); // Load once
        List<Hall> allHalls = FileHandler.loadHalls();

        Runnable refreshTable = () -> {
            model.setRowCount(0);
            String selectedFilter = (String) filterBox.getSelectedItem();
            LocalDate today = LocalDate.now();

            List<Booking> filtered = allBookings.stream()
                    .filter(b -> b.getCustomerEmail().equalsIgnoreCase(customer.getEmail()))
                    .filter(b -> {
                        if ("Upcoming".equals(selectedFilter)) return !b.getDate().isBefore(today);
                        if ("Past History".equals(selectedFilter)) return b.getDate().isBefore(today);
                        return true;
                    })
                    .collect(Collectors.toList());

            for (Booking b : filtered) {
                // Resolve Hall Name
                String hallName = allHalls.stream()
                        .filter(h -> h.getId().equals(b.getHallId()))
                        .map(Hall::getName)
                        .findFirst()
                        .orElse(b.getHallId());

                model.addRow(new Object[]{
                        b.getBookingId(),
                        hallName,
                        b.getDate().toString(),
                        b.getStartTime() + " - " + b.getEndTime(),
                        b.getStatus()
                });
            }
        };

        // Initial Load & Listener
        refreshTable.run();
        filterBox.addActionListener(e -> refreshTable.run());

        // --- BOTTOM BAR: Actions ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // 1. REPORT ISSUE
        JButton btnIssue = new JButton("Report Issue");
        btnIssue.setBackground(new Color(255, 193, 7)); // Amber
        btnIssue.setForeground(Color.BLACK);

        btnIssue.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a booking first.");
                return;
            }
            String bookingId = (String) model.getValueAt(row, 0);
            Booking selectedBooking = allBookings.stream()
                    .filter(b -> b.getBookingId().equals(bookingId)).findFirst().orElse(null);

            if (selectedBooking != null) new IssueForm(customer, selectedBooking);
        });

        // 2. CANCEL BOOKING
        JButton btnCancel = new JButton("Cancel Selected Booking");
        btnCancel.setBackground(new Color(220, 53, 69)); // Red
        btnCancel.setForeground(Color.WHITE);

        btnCancel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a booking first.");
                return;
            }

            String dateStr = (String) model.getValueAt(row, 2);
            String status = (String) model.getValueAt(row, 4);

            if ("CANCELLED".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this, "Already cancelled.");
                return;
            }

            LocalDate bookingDate = LocalDate.parse(dateStr);
            if (ChronoUnit.DAYS.between(LocalDate.now(), bookingDate) < 3) {
                JOptionPane.showMessageDialog(this, "Bookings must be cancelled 3 days in advance.");
            } else {
                if (JOptionPane.showConfirmDialog(this, "Are you sure?") == JOptionPane.YES_OPTION) {
                    // Note: We'd typically call a service here to update the file status to "CANCELLED"
                    JOptionPane.showMessageDialog(this, "Cancellation Successful (Mock)!");
                }
            }
        });

        bottomPanel.add(btnIssue);
        bottomPanel.add(btnCancel);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        setPage(panel);
    }

    // ==========================================
    // VIEW 3: PROFILE (View & Edit)
    // ==========================================
    private void showProfileView() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(COLOR_PRIMARY);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        // Fields
        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        JTextField txtName = new JTextField(customer.getName(), 20);
        txtName.setEditable(false);
        panel.add(txtName, gbc);

        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(customer.getEmail(), 20);
        txtEmail.setEditable(false);
        panel.add(txtEmail, gbc);

        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        JTextField txtPhone = new JTextField(customer.getContactNumber(), 20);
        txtPhone.setEditable(false);
        panel.add(txtPhone, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.WHITE);

        JButton btnEdit = new JButton("Edit Profile");
        JButton btnSave = new JButton("Save Changes");
        btnSave.setEnabled(false);
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);

        btnEdit.addActionListener(e -> {
            boolean editing = btnSave.isEnabled();
            txtName.setEditable(!editing);
            txtEmail.setEditable(!editing);
            txtPhone.setEditable(!editing);
            btnSave.setEnabled(!editing);
            btnEdit.setText(editing ? "Edit Profile" : "Cancel");
        });

        btnSave.addActionListener(e -> {
            customer.setName(txtName.getText());
            customer.setEmail(txtEmail.getText());
            customer.setContactNumber(txtPhone.getText());

            if (FileHandler.updateUser(customer)) {
                JOptionPane.showMessageDialog(this, "Profile Updated!");
                txtName.setEditable(false);
                txtEmail.setEditable(false);
                txtPhone.setEditable(false);
                btnSave.setEnabled(false);
                btnEdit.setText("Edit Profile");
            } else {
                JOptionPane.showMessageDialog(this, "Error saving profile.");
            }
        });

        btnPanel.add(btnEdit);
        btnPanel.add(btnSave);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        // Wrapper
        JPanel container = new JPanel(new FlowLayout());
        container.add(panel);
        setPage(container);
    }
}