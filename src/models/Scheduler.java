package models;

public class Scheduler extends User {

    // Constructor matches the new User format (Name + Email)
    public Scheduler(String id, String username, String password, String name, String email) {
        super(id, username, password, name, email, "SCHEDULER");
    }

    @Override
    public String toFileString() {
        return super.toFileString()+ "|SCHEDULER";
    }
    @Override
    public void openDashboard() {
        System.out.println("Opening Scheduler Dashboard for: " + getName());
    }
}