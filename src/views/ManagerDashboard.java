package views;
import models.Manager;
import javax.swing.*;

public class ManagerDashboard extends BaseDashboard {
    public ManagerDashboard(Manager manager){
        super("Manger Dashboard", manager, COLOR_MANAGER);

        addSidebarButton("Approve Booking", e -> showApprovals());
        addSidebarButton("Financial Reports", e -> showFinances());
        addSidebarButton("System Logs", e-> showLogs());

        addLogoutButton();
        setVisible(true);
    }


private void showApprovals() {
    setPage(new JLabel("List of Pending Bookings to Approve (Coming Soon)", SwingConstants.CENTER));
}

private void showFinances() {
    setPage(new JLabel("Monthly Financial Reports & Charts (Coming Soon)", SwingConstants.CENTER));
}

private void showLogs() {
    setPage(new JLabel("System Activity Logs (Coming Soon)", SwingConstants.CENTER));
}
}
