package com.production.achour_ar.gshglobalactivity.adapter;

public class TicketSearchModel {
    String TitreTicket, dateTicket, idTicket, statutTicket;

    public TicketSearchModel(String titreTicket, String dateTicket, String idTicket, String statutTicket) {
        TitreTicket = titreTicket;
        this.dateTicket = dateTicket;
        this.idTicket = idTicket;
        this.statutTicket = statutTicket;
    }

    public String getTitreTicket() {
        return TitreTicket;
    }

    public String getDateTicket() {
        return dateTicket;
    }

    public String getIdTicket() {
        return idTicket;
    }

    public String getStatutTicket() {
        return statutTicket;
    }
}
