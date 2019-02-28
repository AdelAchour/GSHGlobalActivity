package com.production.achour_ar.gshglobalactivity.ITs.data_model;

public class RemoteMessageModel {

    private String name;
    private String email;
    private String title;
    private String message;
    private String date;


    public RemoteMessageModel(String name, String email, String title, String message, String date) {
        this.name = name;
        this.email = email;
        this.title = title;
        this.message = message;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

}
