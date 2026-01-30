package views;

import models.Administrator;
import models.User;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends BaseDashboard{

    public AdminDashboard(Administrator admin) {
        super("Admin Dashboard", admin , COLOR_ADMIN);

        addSidebarButton("Manage Halls", e->showManageHalls());
        addSidebarButton("Manage Users", e -> showManageUsers());
        addSidebarButton("View Reports", e -> showReports());

        addLogoutButton();
        setVisible(true);
    }

    public void showManageHalls(){
        setPage(new JLabel("Admin Hall Management Screen",SwingConstants.CENTER));
    }
    private void showManageUsers() {
        setPage(new JLabel("Admin User Management Screen", SwingConstants.CENTER));
    }

    private void showReports() {
        setPage(new JLabel("System Reports Screen", SwingConstants.CENTER));
    }
}