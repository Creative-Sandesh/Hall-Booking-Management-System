package models;

public class Scheduler extends User {

    // Constructor matches the new User format (Name + Email)
    public Scheduler(String id, String username, String password, String name, String email) {
        super(id, username, password, name, email);
    }

    @Override
    public String getRole() {
        return "SCHEDULER";
    }

    @Override
    public String toFileString() {
        return getId() + "," +
                getUsername() + "," +
                getPassword() + "," +
                getName() + "," +
                getEmail() + "," +
                getRole();
    }
}