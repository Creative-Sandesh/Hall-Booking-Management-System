package views;

import models.Booking;
import models.Customer;
import models.Hall;
import models.Issue;
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
        super("Customer Dashboard", customer, COLOR_PRIMARY);
        this.customer = customer;

        // --- SIDEBAR MENU ---
        addSidebarButton("View Available Halls", e -> showHallsView());
        addSidebarButton("My Bookings", e -> showBookingsView());
        addSidebarButton("My Issues", e -> showIssuesView()); // <--- NEW BUTTON
        addSidebarButton("My Profile", e -> showProfileView());

        addLogoutButton();

        // Default Page
        showHallsView();

        setVisible(true);
    }

    // ==========================================
    // VIEW 1: AVAILABLE HALLS (Read-Only Table)
    // ==========================================
    private void showHallsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("  Available Halls");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        List<Hall> halls = FileHandler.loadHalls();
        String[] columns = {"ID", "Hall Name", "Rate (RM/hr)", "Capacity", "Status"};

        // FIX: Make Table Non-Editable
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (Hall h : halls) {
            String status = h.isMaintenance() ? "Maintenance" : "Available";
            model.addRow(new Object[]{h.getId(), h.getName(), String.format("%.2f", h.getPricePerHour()), h.getCapacity(), status});
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBook = new JButton("Book Selected Hall");
        btnBook.setBackground(new Color(40, 167, 69));
        btnBook.setForeground(Color.WHITE);

        btnBook.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a hall."); return; }

            String hallId = (String) model.getValueAt(row, 0);
            String status = (String) model.getValueAt(row, 4);

            if (status.equalsIgnoreCase("Maintenance")) {
                JOptionPane.showMessageDialog(this, "Hall is under maintenance.");
                return;
            }

            Hall selectedHall = halls.stream().filter(h -> h.getId().equals(hallId)).findFirst().orElse(null);
            if (selectedHall != null) new BookingForm(customer, selectedHall);
        });

        bottomPanel.add(btnBook);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        setPage(panel);
    }

    // ==========================================
    // VIEW 2: MY BOOKINGS (Read-Only Table)
    // ==========================================
    private void showBookingsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

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

        String[] columns = {"Booking ID", "Hall Name", "Date", "Time", "Status"};

        // FIX: Make Table Non-Editable
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);

        List<Booking> allBookings = FileHandler.loadBookings();
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
                String hallName = allHalls.stream()
                        .filter(h -> h.getId().equals(b.getHallId())).map(Hall::getName).findFirst().orElse(b.getHallId());
                model.addRow(new Object[]{b.getBookingId(), hallName, b.getDate(), b.getStartTime() + " - " + b.getEndTime(), b.getStatus()});
            }
        };

        refreshTable.run();
        filterBox.addActionListener(e -> refreshTable.run());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnIssue = new JButton("Report Issue");
        btnIssue.setBackground(new Color(255, 193, 7)); // Amber

        JButton btnCancel = new JButton("Cancel Booking");
        btnCancel.setBackground(new Color(220, 53, 69)); // Red
        btnCancel.setForeground(Color.WHITE);

        // Logic for Report Issue
        btnIssue.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select a booking first."); return; }
            String bookingId = (String) model.getValueAt(row, 0);
            Booking b = allBookings.stream().filter(bk -> bk.getBookingId().equals(bookingId)).findFirst().orElse(null);
            if (b != null) new IssueForm(customer, b);
        });

        // Logic for Cancel
        btnCancel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select a booking first."); return; }
            String dateStr = (String) model.getValueAt(row, 2);
            String status = (String) model.getValueAt(row, 4);
            if ("CANCELLED".equalsIgnoreCase(status)) { JOptionPane.showMessageDialog(this, "Already cancelled."); return; }

            if (ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(dateStr)) < 3) {
                JOptionPane.showMessageDialog(this, "Must cancel 3 days in advance.");
            } else {
                if(JOptionPane.showConfirmDialog(this, "Are you sure?") == JOptionPane.YES_OPTION)
                    JOptionPane.showMessageDialog(this, "Cancellation Successful (Logic pending)");
            }
        });

        bottomPanel.add(btnIssue);
        bottomPanel.add(btnCancel);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        setPage(panel);
    }

    // ==========================================
    // VIEW 3: MY ISSUES (New & Read-Only)
    // ==========================================
    private void showIssuesView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("  Reported Issues Status");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        List<Issue> allIssues = FileHandler.loadIssues();

        // Filter: Only show issues for THIS customer
        List<Issue> myIssues = allIssues.stream()
                .filter(i -> i.getCustomerEmail().equalsIgnoreCase(customer.getEmail()))
                .collect(Collectors.toList());

        String[] columns = {"Issue ID", "Booking ID", "Description", "Status", "Date Reported"};

        // FIX: Make Table Non-Editable
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (Issue i : myIssues) {
            model.addRow(new Object[]{
                    i.getIssueId(),
                    i.getBookingId(),
                    i.getDescription(),
                    i.getStatus(),
                    i.getDateReported().toString()
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        setPage(panel);
    }

    // ==========================================
    // VIEW 4: PROFILE
    // ==========================================
    private void showProfileView() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);
        gbc.gridwidth = 1;

        gbc.gridy++; gbc.gridx = 0; panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; JTextField txtName = new JTextField(customer.getName(), 20); txtName.setEditable(false); panel.add(txtName, gbc);

        gbc.gridy++; gbc.gridx = 0; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; JTextField txtEmail = new JTextField(customer.getEmail(), 20); txtEmail.setEditable(false); panel.add(txtEmail, gbc);

        gbc.gridy++; gbc.gridx = 0; panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; JTextField txtPhone = new JTextField(customer.getContactNumber(), 20); txtPhone.setEditable(false); panel.add(txtPhone, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnEdit = new JButton("Edit Profile");
        JButton btnSave = new JButton("Save Changes");
        btnSave.setEnabled(false); btnSave.setBackground(new Color(40, 167, 69)); btnSave.setForeground(Color.WHITE);

        btnEdit.addActionListener(e -> {
            boolean edit = btnSave.isEnabled();
            txtName.setEditable(!edit); txtEmail.setEditable(!edit); txtPhone.setEditable(!edit);
            btnSave.setEnabled(!edit); btnEdit.setText(edit ? "Edit Profile" : "Cancel");
        });

        btnSave.addActionListener(e -> {
            customer.setName(txtName.getText());
            customer.setEmail(txtEmail.getText());
            customer.setContactNumber(txtPhone.getText());
            if(FileHandler.updateUser(customer)) {
                JOptionPane.showMessageDialog(this, "Saved!");
                txtName.setEditable(false); txtEmail.setEditable(false); txtPhone.setEditable(false);
                btnSave.setEnabled(false); btnEdit.setText("Edit Profile");
            }
        });

        btnPanel.add(btnEdit); btnPanel.add(btnSave);
        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2; panel.add(btnPanel, gbc);

        JPanel c = new JPanel(new FlowLayout()); c.add(panel);
        setPage(c);
    }
}