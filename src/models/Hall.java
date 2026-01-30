package models;

public class Hall {
    private String id;
    private String name;
    private double pricePerHour;
    private int capacity;
    private boolean isMaintenance; // "true" if under maintenance, "false" if available

    // Constructor
    public Hall(String id, String name, double pricePerHour, int capacity, boolean isMaintenance) {
        this.id = id;
        this.name = name;
        this.pricePerHour = pricePerHour;
        this.capacity = capacity;
        this.isMaintenance = isMaintenance;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPricePerHour() { return pricePerHour; }
    public int getCapacity() { return capacity; }

    public boolean isMaintenance() { return isMaintenance; }
    public void setMaintenance(boolean maintenance) { isMaintenance = maintenance; }

    // Helper for saving to text file
    // Format: H001,Grand Ballroom,300.0,1000,false
    public String toFileString() {
        return id + "," + name + "," + pricePerHour + "," + capacity + "," + isMaintenance;
    }

    // Useful for debugging or ComboBoxes
    @Override
    public String toString() {
        return name + " (Cap: " + capacity + ")";
    }
}