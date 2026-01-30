package views;

import models.Issue;
import models.Manager;
import services.FileHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ManagerDashboard extends BaseDashboard {

    public ManagerDashboard(Manager manager) {
        super("Manager Dashboard", manager, COLOR_MANAGER);

        // --- SIDEBAR ---
        addSidebarButton("Manage Issues", e -> showIssuesView());
        addSidebarButton("Approve Bookings", e -> showApprovals());
        addSidebarButton("Financial Reports", e -> showFinances());

        addLogoutButton();

        // Default Page
        showIssuesView();

        setVisible(true);
    }

    // ==========================================
    // VIEW 1: MANAGE CUSTOMER ISSUES
    // ==========================================
    private void showIssuesView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Header
        JLabel titleLabel = new JLabel("  Customer Issues Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // 1. Load Data
        List<Issue> issues = FileHandler.loadIssues();

        // 2. Table Setup (Now Non-Editable)
        String[] columns = {"Issue ID", "Booking ID", "Customer Email", "Description", "Status", "Date"};

        // FIX 1: Override isCellEditable to return false (Makes table read-only)
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // <--- This prevents editing directly in the table
            }
        };

        for (Issue i : issues) {
            model.addRow(new Object[]{
                    i.getIssueId(),
                    i.getBookingId(),
                    i.getCustomerEmail(),
                    i.getDescription(),
                    i.getStatus(),
                    i.getDateReported().toString()
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        // FIX 2: Add Mouse Listener for "Double Click" to open details
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double-click to open
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String issueId = (String) model.getValueAt(row, 0);
                        Issue selectedIssue = issues.stream()
                                .filter(i -> i.getIssueId().equals(issueId))
                                .findFirst()
                                .orElse(null);

                        if (selectedIssue != null) {
                            showIssueDetailsDialog(selectedIssue);
                        }
                    }
                }
            }
        });

        // Hint Label at the bottom
        JLabel hintLabel = new JLabel("  (Double-click a row to view details and update status)");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        hintLabel.setForeground(Color.GRAY);
        hintLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(hintLabel, BorderLayout.SOUTH);

        setPage(panel);
    }

    // ==========================================
    // POPUP: VIEW DETAILS & UPDATE STATUS
    // ==========================================
    private void showIssueDetailsDialog(Issue issue) {
        JDialog dialog = new JDialog(this, "Issue Details & Update", true);
        dialog.setSize(500, 450); // Larger size for details
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // --- READ ONLY FIELDS ---
        addLabelAndField(formPanel, gbc, 0, "Issue ID:", issue.getIssueId());
        addLabelAndField(formPanel, gbc, 1, "Booking ID:", issue.getBookingId());
        addLabelAndField(formPanel, gbc, 2, "Customer:", issue.getCustomerEmail());
        addLabelAndField(formPanel, gbc, 3, "Date Reported:", issue.getDateReported().toString());

        // --- DESCRIPTION AREA (Scrollable) ---
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridy = 5;
        gbc.weighty = 1.0; // Give it vertical space
        gbc.fill = GridBagConstraints.BOTH;

        JTextArea txtDesc = new JTextArea(issue.getDescription());
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false); // Read-only
        txtDesc.setBackground(new Color(245, 245, 245)); // Light gray
        txtDesc.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JScrollPane descScroll = new JScrollPane(txtDesc);
        descScroll.setPreferredSize(new Dimension(0, 100)); // Fixed height preference
        formPanel.add(descScroll, gbc);

        // --- UPDATE STATUS SECTION ---
        gbc.gridy = 6; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Update Status:"), gbc);

        gbc.gridx = 1;
        String[] options = {"In Progress", "Done", "Closed", "Cancelled"};
        JComboBox<String> statusBox = new JComboBox<>(options);
        statusBox.setSelectedItem(issue.getStatus());
        formPanel.add(statusBox, gbc);

        // --- BUTTONS ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Cancel");
        JButton btnSave = new JButton("Update Status");

        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);

        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String newStatus = (String) statusBox.getSelectedItem();
            issue.setStatus(newStatus);

            if (FileHandler.updateIssue(issue)) {
                JOptionPane.showMessageDialog(dialog, "Status updated to: " + newStatus);
                dialog.dispose();
                showIssuesView(); // Refresh the main table
            } else {
                JOptionPane.showMessageDialog(dialog, "Error updating status.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // Helper to keep code clean
    private void addLabelAndField(JPanel p, GridBagConstraints gbc, int y, String label, String value) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1;
        p.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        JTextField txt = new JTextField(value);
        txt.setEditable(false);
        txt.setBorder(null); // Look like a label
        txt.setBackground(p.getBackground());
        txt.setFont(new Font("Arial", Font.BOLD, 12));
        p.add(txt, gbc);
    }

    // --- Placeholders ---
    private void showApprovals() {
        setPage(new JLabel("Booking Approval Screen (Coming Soon)", SwingConstants.CENTER));
    }
    private void showFinances() {
        setPage(new JLabel("Financial Reports Screen (Coming Soon)", SwingConstants.CENTER));
    }
}