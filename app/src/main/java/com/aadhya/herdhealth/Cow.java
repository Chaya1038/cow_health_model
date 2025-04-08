package com.aadhya.herdhealth;

public class Cow {
    private String id, healthStatus, digitalTwinStatus;

    public Cow(String id, String healthStatus, String digitalTwinStatus) {
        this.id = id;
        this.healthStatus = healthStatus;
        this.digitalTwinStatus = digitalTwinStatus;
    }

    public String getId() {
        return id;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public String getDigitalTwinStatus() {
        return digitalTwinStatus;
    }
}
