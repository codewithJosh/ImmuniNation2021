package com.codewithjosh.ImmuniNation2k21.models;

import java.util.Date;

public class SlotModel {

    private String slot_id;
    private Date vaccine_first_dose_date;
    private String vaccine_image;
    private String vaccine_name;
    private Date vaccine_second_dose_date;
    private String vaccine_site;
    private int vaccine_slots;

    public SlotModel() {

    }

    public SlotModel(final String slot_id, final Date vaccine_first_dose_date, final String vaccine_image, final String vaccine_name, final Date vaccine_second_dose_date, final String vaccine_site, final int vaccine_slots) {

        this.slot_id = slot_id;
        this.vaccine_first_dose_date = vaccine_first_dose_date;
        this.vaccine_image = vaccine_image;
        this.vaccine_name = vaccine_name;
        this.vaccine_second_dose_date = vaccine_second_dose_date;
        this.vaccine_site = vaccine_site;
        this.vaccine_slots = vaccine_slots;

    }

    public String getSlot_id() {

        return slot_id;

    }

    public Date getVaccine_first_dose_date() {

        return vaccine_first_dose_date;

    }

    public String getVaccine_image() {

        return vaccine_image;

    }

    public String getVaccine_name() {

        return vaccine_name;

    }

    public Date getVaccine_second_dose_date() {

        return vaccine_second_dose_date;

    }

    public String getVaccine_site() {

        return vaccine_site;

    }

    public int getVaccine_slots() {

        return vaccine_slots;

    }

}
