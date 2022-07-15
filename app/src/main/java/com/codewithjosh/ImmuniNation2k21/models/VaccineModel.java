package com.codewithjosh.ImmuniNation2k21.models;

public class VaccineModel
{

    private int vaccine_image;
    private String vaccine_name;

    public VaccineModel()
    {

    }

    public VaccineModel(final int vaccine_image, final String vaccine_name)
    {

        this.vaccine_image = vaccine_image;
        this.vaccine_name = vaccine_name;

    }

    public int getVaccine_image()
    {

        return vaccine_image;

    }

    public String getVaccine_name()
    {

        return vaccine_name;

    }

}
