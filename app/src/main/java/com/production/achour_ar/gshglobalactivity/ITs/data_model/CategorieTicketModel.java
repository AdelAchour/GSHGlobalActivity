package com.production.achour_ar.gshglobalactivity.ITs.data_model;


public class CategorieTicketModel {
    String Name, CompleteName, id;

    public CategorieTicketModel(String name, String completeName, String id) {
        Name = name;
        CompleteName = completeName;
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public String getCompleteName() {
        return CompleteName;
    }

    public String getId() {
        return id;
    }
}
