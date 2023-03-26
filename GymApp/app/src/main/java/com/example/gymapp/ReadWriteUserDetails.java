package com.example.gymapp;

public class ReadWriteUserDetails {

    public String name,dob,gender,mobile;

    ReadWriteUserDetails(){}

    public ReadWriteUserDetails(String txtName,String txtDob, String txtGender, String txtMobile) {
        this.dob = txtDob;
        this.gender = txtGender;
        this.mobile = txtMobile;
        this.name = txtName;
    }
}
