package models;

import java.io.Serializable;

public abstract class User implements Serializable {

    protected String id;
    protected String username;
    protected String password;
    protected String name;
    protected String email;
    protected String role;


    // constructor

    public User(String id, String username, String password,String name, String email, String role){
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;

    }


    // getter methods


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // format for writing to text file
    public String toFileString(){
        return id + "|" + username + "|" + password + "|" + name + "|" + email;    }

    // Abstract method: Every child must describe their specific dashboard
    // This isn't GUI code, just a placeholder for now

    public abstract void openDashboard();

}
