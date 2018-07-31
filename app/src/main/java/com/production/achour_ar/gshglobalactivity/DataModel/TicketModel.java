package com.production.achour_ar.gshglobalactivity.DataModel;

public class TicketModel {
    String titreTicket;
    String slaTicket;
    String DateTicket;

    String UrgenceTicket;

    public TicketModel(String titreTicket, String slaTicket, String dateTicket) {
        this.titreTicket = titreTicket;
        this.slaTicket = slaTicket;
        DateTicket = dateTicket;
    }

    public String getTitreTicket() {
        return titreTicket;
    }

    public String getSlaTicket() {
        return slaTicket;
    }

    public String getDateTicket() {
        return DateTicket;
    }

    public String getUrgenceTicket() {
        return UrgenceTicket;
    }

    public void setUrgenceTicket(String urgenceTicket) {
        UrgenceTicket = urgenceTicket;
    }
}
