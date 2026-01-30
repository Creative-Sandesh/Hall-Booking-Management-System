package models;

public class Manager extends User {

    // Constructor matches the new User format
    public Manager(String id, String username, String password, String name, String email) {
        super(id, username, password, name, email, "MANAGER");
    }

    @Override
    public String toFileString() {
        return super.toFileString()+ "|MANAGER";
    }
    @Override
    public void openDashboard() {
        System.out.println("Opening Manager Dashboard for: " + getName());
    }
}