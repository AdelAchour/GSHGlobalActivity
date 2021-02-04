package com.production.achour_ar.gshglobalactivity.ITs.data_model;

public class UserModel {
    private String fullname, firstname, lastname, company, department, jobTitle, ad2000, pic, email, phone;
    private static UserModel currentUserModel;

    public UserModel(String name, String firstname, String lastname, String company, String department, String job, String ad2000, String pic, String email, String phone) {
        this.fullname = name;
        this.firstname = firstname;
        this.lastname = lastname;
        this.company = company;
        this.department = department;
        this.jobTitle = job;
        this.email = email;
        this.phone = phone;
        this.ad2000 = ad2000;
        this.pic = pic;
    }

    public String getFullname() {
        return fullname;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getAd2000() {
        return ad2000;
    }

    public String getPic() {
        return pic;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getCompany() {
        return company;
    }

    public String getDepartment() {
        return department;
    }

    public static void setCurrentUser(UserModel userModel) {
        currentUserModel = userModel;
    }

    public static UserModel getCurrentUserModel() {
        return currentUserModel;
    }


    @Override
    public String toString() {
        return "UserModel{" +
                "fullname='" + fullname + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", ad2000='" + ad2000 + '\'' +
                ", pic='" + pic + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
