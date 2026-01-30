package utils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;

public class DatePicker extends JDialog {
    private LocalDate selectedDate;
    private YearMonth currentMonth;
    private JLabel monthLabel;
    private JPanel daysPanel;
    private JTextField targetField;

    public DatePicker(Window parent, JTextField targetField) {
        // Use 'Window' instead of 'JFrame' so it works from other popups too
        super(parent, "Select Date", ModalityType.APPLICATION_MODAL);
        this.targetField = targetField;
        this.currentMonth = YearMonth.now();

        // FIX 1: Increased size so buttons aren't squashed
        setSize(400, 350);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        // 1. Header (Month/Year + Navigation)
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add some padding

        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 16));

        btnPrev.addActionListener(e -> { currentMonth = currentMonth.minusMonths(1); updateCalendar(); });
        btnNext.addActionListener(e -> { currentMonth = currentMonth.plusMonths(1); updateCalendar(); });

        header.add(btnPrev, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        header.add(btnNext, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // 2. Days Grid
        daysPanel = new JPanel(new GridLayout(0, 7, 5, 5)); // Increased gap between buttons
        daysPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(daysPanel, BorderLayout.CENTER);

        // 3. Populate
        updateCalendar();
    }

    private void updateCalendar() {
        daysPanel.removeAll();
        monthLabel.setText(currentMonth.getMonth().toString() + " " + currentMonth.getYear());

        // Header for Days of Week
        String[] days = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        for (String d : days) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            daysPanel.add(lbl);
        }

        // Calculate empty slots before the 1st of the month
        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        int emptySlots = (dayOfWeek == 7) ? 0 : dayOfWeek;

        for (int i = 0; i < emptySlots; i++) {
            daysPanel.add(new JLabel(""));
        }

        // Add Days
        int daysInMonth = currentMonth.lengthOfMonth();
        for (int i = 1; i <= daysInMonth; i++) {
            int day = i;
            JButton btn = new JButton(String.valueOf(day));

            // FIX 2: Remove internal padding so numbers are visible
            btn.setMargin(new Insets(2, 2, 2, 2));
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.PLAIN, 12));
            btn.setBackground(Color.WHITE);

            // Highlight today
            if (LocalDate.now().equals(currentMonth.atDay(day))) {
                btn.setBackground(new Color(220, 240, 255));
                btn.setFont(new Font("Arial", Font.BOLD, 12));
            }

            btn.addActionListener(e -> {
                selectedDate = currentMonth.atDay(day);
                if (targetField != null) {
                    targetField.setText(selectedDate.toString());
                }
                dispose();
            });
            daysPanel.add(btn);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }
}