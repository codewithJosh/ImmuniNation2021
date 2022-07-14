package com.codewithjosh.ImmuniNation2k21.models;

public class UserModel
{

    private String user_first_name;
    private String user_id;
    private String user_image;
    private boolean user_is_admin;
    private String user_last_name;
    private int user_vaccination_status;

    public UserModel()
    {

    }

    public UserModel(final String user_first_name, final String user_id, final String user_image, final boolean user_is_admin, final String user_last_name, final int user_vaccination_status)
    {

        this.user_first_name = user_first_name;
        this.user_id = user_id;
        this.user_image = user_image;
        this.user_is_admin = user_is_admin;
        this.user_last_name = user_last_name;
        this.user_vaccination_status = user_vaccination_status;

    }

    public String getUser_first_name()
    {

        return user_first_name;

    }

    public String getUser_id()
    {

        return user_id;

    }

    public String getUser_image()
    {

        return user_image;

    }

    public boolean isUser_is_admin()
    {

        return user_is_admin;

    }

    public String getUser_last_name()
    {

        return user_last_name;

    }

    public int getUser_vaccination_status()
    {

        return user_vaccination_status;

    }

}
