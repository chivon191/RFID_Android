package com.example.controlhome.models;

public class AccessRecord {
    private String name;
    private String time;

    public AccessRecord(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }
}

