package com.codewithjosh.ImmuniNation2k21.models;

import java.util.Date;

public class RequestModel
{

    private String request_id;
    private int request_status;
    private String slot_id;
    private Date user_birth_date;
    private String user_category;
    private String user_contact;
    private String user_id;
    private String user_name;
    private String user_selfie_with_id;
    private String user_selfie_with_id_image;
    private String user_street;
    private String user_valid_id;
    private String user_valid_id_image;

    public RequestModel()
    {

    }

    public RequestModel(final String request_id, final int request_status, final String slot_id, final Date user_birth_date, final String user_category, final String user_contact, final String user_id, final String user_name, final String user_selfie_with_id, final String user_selfie_with_id_image, final String user_street, final String user_valid_id, final String user_valid_id_image)
    {

        this.request_id = request_id;
        this.request_status = request_status;
        this.slot_id = slot_id;
        this.user_birth_date = user_birth_date;
        this.user_category = user_category;
        this.user_contact = user_contact;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_selfie_with_id = user_selfie_with_id;
        this.user_selfie_with_id_image = user_selfie_with_id_image;
        this.user_street = user_street;
        this.user_valid_id = user_valid_id;
        this.user_valid_id_image = user_valid_id_image;

    }

    public String getRequest_id()
    {

        return request_id;

    }

    public int getRequest_status()
    {

        return request_status;

    }

    public String getSlot_id()
    {

        return slot_id;

    }

    public Date getUser_birth_date()
    {

        return user_birth_date;

    }

    public String getUser_category()
    {

        return user_category;

    }

    public String getUser_contact()
    {

        return user_contact;

    }

    public String getUser_id()
    {

        return user_id;

    }

    public String getUser_name()
    {

        return user_name;

    }

    public String getUser_selfie_with_id()
    {

        return user_selfie_with_id;

    }

    public String getUser_selfie_with_id_image()
    {

        return user_selfie_with_id_image;

    }

    public String getUser_street()
    {

        return user_street;

    }

    public String getUser_valid_id()
    {

        return user_valid_id;

    }

    public String getUser_valid_id_image()
    {

        return user_valid_id_image;

    }

}
