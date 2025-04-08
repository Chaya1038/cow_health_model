package com.aadhya.herdhealth;

public class VetCowModel {
    private String id;
    private String name;
    private String status;
    private String farmerName;
    private String farmerContact;
    private String lameness;
    private String estrus;
    private String digitalTwinPrediction;

    // Constructor
    public VetCowModel(String id, String name, String status, String farmerName, String farmerContact, String lameness, String estrus, String digitalTwinPrediction) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.farmerName = farmerName;
        this.farmerContact = farmerContact;
        this.lameness = lameness;
        this.estrus = estrus;
        this.digitalTwinPrediction = digitalTwinPrediction;
    }

    // Default Constructor (if needed)
    public VetCowModel() {}

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public String getFarmerName() { return farmerName; }
    public String getFarmerContact() { return farmerContact; }
    public String getLameness() { return lameness; }
    public String getEstrus() { return estrus; }
    public String getDigitalTwinPrediction() { return digitalTwinPrediction; }
}
