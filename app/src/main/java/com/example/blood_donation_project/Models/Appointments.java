package com.example.blood_donation_project.Models;

public class Appointments {

    private String appointment_id;
    private Slots slot_reserved_data;

    private String appointment_date;
    private String appointment_state;
    private String donor_id;
    private String slot_reserved;

    public Appointments() {}

    public Appointments(String donor_id, String slot_reserved, String appointment_date, String appointment_state) {
        this.donor_id = donor_id;
        this.slot_reserved = slot_reserved;
        this.appointment_date = appointment_date;
        this.appointment_state = appointment_state;
    }

    public void setAppointment_id(String appointment_id) {
        this.appointment_id = appointment_id;
    }

    public String getAppointment_date() {
        return appointment_date;
    }

    public void setAppointment_date(String appointment_date) {
        this.appointment_date = appointment_date;
    }

    public String getDonor_id() {
        return donor_id;
    }

    public void setDonor_id(String donor_id) {
        this.donor_id = donor_id;
    }

    public String getSlot_reserved() {
        return slot_reserved;
    }

    public void setSlot_reserved(String slot_reserved) {
        this.slot_reserved = slot_reserved;
    }

    public Slots getSlot_reserved_data() {
        return slot_reserved_data;
    }

    public void setSlot_reserved_data(Slots slot_reserved_data) {
        this.slot_reserved_data = slot_reserved_data;
    }

    public String getAppointment_state() {
        return appointment_state;
    }

    public void setAppointment_state(String appointment_state) {
        this.appointment_state = appointment_state;
    }

    public String getAppointment_id() {
        return appointment_id;
    }
}
