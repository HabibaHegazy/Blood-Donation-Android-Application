package com.example.blood_donation_project.Models;

public class BloodBanks {

    private String name;
    private Double Longtitude, Latitude;
    private String number;
    private String working_hours;
    private String detail_location_description;
    private String working_From;
    private String working_To;

    public BloodBanks() {}

    public String getWorking_From() {
        return working_From;
    }

    public void setWorking_From(String working_From) {
        this.working_From = working_From;
    }

    public String getWorking_To() {
        return working_To;
    }

    public void setWorking_To(String working_To) {
        this.working_To = working_To;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongtitude() {
        return Longtitude;
    }

    public void setLongtitude(Double longtitude) {
        Longtitude = longtitude;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getWorking_hours() {
        return working_hours;
    }

    public void setWorking_hours(String working_hours) {
        this.working_hours = working_hours;
    }

    public String getDetail_location_description() {
        return detail_location_description;
    }

    public void setDetail_location_description(String detail_location_description) {
        this.detail_location_description = detail_location_description;
    }
}
