package models;

public class Customer extends User{
    private String contactNumber;

    public Customer(String id, String username, String password,String name, String email, String contactNumber) {
        super(id, username, password, name, email, "CUSTOMER");
        this.contactNumber = contactNumber;
    }


    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber){
        this.contactNumber =contactNumber;
    }

    @Override
    public String toFileString(){
        // add the phone number to the end
        return super.toFileString() + "|"+ contactNumber+"|CUSTOMER";
    }

    @Override
    public void openDashboard(){
        System.out.println("Opening Customer Dashboard..");
    }
}
