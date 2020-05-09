package com.example.blood_donation_project.Models;

public class Slots {

    private String slotID;
    private String blood_bank_name;
    private String slot_day;
    private String timing;
    private String status;

    public Slots() {}

    public String getSlotID() {
        return slotID;
    }

    public void setSlotID(String slotID) {
        this.slotID = slotID;
    }

    public String getBlood_bank_name() {
        return blood_bank_name;
    }

    public void setBlood_bank_name(String blood_bank_name) {
        this.blood_bank_name = blood_bank_name;
    }

    public String getSlot_day() {
        return slot_day;
    }

    public void setSlot_day(String slot_day) {
        this.slot_day = slot_day;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
