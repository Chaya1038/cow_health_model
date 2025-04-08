package com.aadhya.herdhealth;

public class CowModel {
    private String cowId;
    private String cowName;

    public CowModel(String cowId, String cowName) {
        this.cowId = cowId;
        this.cowName = cowName;
    }

    public String getCowId() {
        return cowId;
    }

    public String getCowName() {
        return cowName;
    }
}
