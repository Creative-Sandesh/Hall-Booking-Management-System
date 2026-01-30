package models;

public class Administrator extends User {

    //constructor
    public Administrator(String id, String username, String password, String name, String email) {
        super(id, username, password, name, email);
    }

    @Override
    public String getRole() {
        return "ADMIN";
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
