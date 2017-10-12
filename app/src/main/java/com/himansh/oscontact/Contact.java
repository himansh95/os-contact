package com.himansh.oscontact;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Himansh on 08-10-2017.
 */

public class Contact {
    private String name;
    private Date dob;
    private String mobile;
    private String whatsappMobile;
    private String company;
    private String title;
    private String workEmail;
    private String personalEmail;
    private Bitmap image;

    public Contact() {
    }

    public Contact(String name, Date dob, String mobile, String whatsappMobile, String company, String title, String workEmail, String personalEmail, Bitmap image) {
        this.name = name;
        this.dob = dob;
        this.mobile = mobile;
        this.whatsappMobile = whatsappMobile;
        this.company = company;
        this.title = title;
        this.workEmail = workEmail;
        this.personalEmail = personalEmail;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWhatsappMobile() {
        return whatsappMobile;
    }

    public void setWhatsappMobile(String whatsappMobile) {
        this.whatsappMobile = whatsappMobile;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", dob=" + dob +
                ", mobile='" + mobile + '\'' +
                ", whatsappMobile='" + whatsappMobile + '\'' +
                ", company='" + company + '\'' +
                ", title='" + title + '\'' +
                ", workEmail='" + workEmail + '\'' +
                ", personalEmail='" + personalEmail + '\'' +
                ", image=" + image +
                '}';
    }

}
