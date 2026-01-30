package models;

public class Administrator extends User {

    //constructor
    public Administrator(String id, String username, String password, String name, String email) {
        super(id, username, password, name, email, "ADMIN");
    }

    @Override
    public String toFileString() {
        return super.toFileString()+ "|ADMIN";
    }

    @Override
    public void openDashboard(){
        System.out.println("Opening Admin Dashboard...");
    }
}
