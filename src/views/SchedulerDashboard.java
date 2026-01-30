package views;

import models.Scheduler;
import javax.swing.*;

public class SchedulerDashboard extends BaseDashboard {

    public SchedulerDashboard(Scheduler scheduler) {
        super("Scheduler Dashboard", scheduler, COLOR_SCHEDULER);


        addSidebarButton("View Schedule", e -> showSchedule());
        addSidebarButton("Maintenance Tasks", e -> showMaintenance());


        addLogoutButton();

        setVisible(true);
    }


    private void showSchedule() {
        setPage(new JLabel("Weekly Booking Schedule View (Coming Soon)", SwingConstants.CENTER));
    }

    private void showMaintenance() {
        setPage(new JLabel("Hall Maintenance & Cleaning Log (Coming Soon)", SwingConstants.CENTER));
    }
}