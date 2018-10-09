package com.production.achour_ar.gshglobalactivity.DataModel;

public class TicketModel {
    String titreTicket;
    String slaTicket;
    String DateTicket;
    String UrgenceTicket;
    boolean ticketEnRetard;
    String TempsRestantTicket;
    String idTicket;
    String Statut;
    String TempsResolution;
    String TempsRetard;
    String description;
    String demandeurID;

    public TicketModel(String titreTicket, String slaTicket, String dateTicket, String tempsRestantTicket, String idticket, String statut) {
        this.titreTicket = titreTicket;
        this.slaTicket = slaTicket;
        DateTicket = dateTicket;
        TempsRestantTicket = tempsRestantTicket;
        idTicket = idticket;
        Statut = statut;
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

    public void setTempsRestantTicket(String tempsRestantTicket) { TempsRestantTicket = tempsRestantTicket; }

    public String getTempsRestantTicket() {
        return TempsRestantTicket;
    }

    public boolean isTicketEnRetard() {
        return ticketEnRetard;
    }

    public void setTicketEnRetard(boolean ticketEnRetard) {
        this.ticketEnRetard = ticketEnRetard;
    }

    public String getIdTicket() { return idTicket; }

    public String getStatut() { return Statut; }

    public String getTempsResolution() {
        return TempsResolution;
    }

    public void setTempsResolution(String tempsResolution) {
        TempsResolution = tempsResolution;
    }

    public String getTempsRetard() {
        return TempsRetard;
    }

    public void setTempsRetard(String tempsRetard) {
        TempsRetard = tempsRetard;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDemandeurID(String demandeurID) {
        this.demandeurID = demandeurID;
    }

    public String getDemandeurID() {
        return demandeurID;
    }
}