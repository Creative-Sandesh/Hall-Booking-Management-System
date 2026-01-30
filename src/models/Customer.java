package models;

public class Customer extends User{
    private String contactNumber;

    public Customer(String id, String username, String password,String name, String email, String contactNumber) {
        super(id, username, password, name, email);
        this.contactNumber = contactNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber){
        this.contactNumber =contactNumber;
    }
    @Override
    public String getRole(){
        return "CUSTOMER";
    }

    @Override
    public String toFileString() {
        return getId() + "," +
                getUsername() + "," +
                getPassword() + "," +
                getName() + "," +
                getEmail() + "," +
                getContactNumber() + "," +
                getRole();
    }
}
