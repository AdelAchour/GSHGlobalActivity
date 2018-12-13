package com.production.achour_ar.gshglobalactivity.data_model;

import android.os.Bundle;

import java.util.ArrayList;

public class TicketModel {
    private String titreTicket;
    private String slaTicket;
    private String DateTicket;
    private String UrgenceTicket;
    private boolean ticketEnRetard;
    private String TempsRestantTicket;
    private String idTicket;
    private String Statut;
    private String TempsResolution;
    private String TempsRetard;
    private String description;
    private String demandeurID;
    private ArrayList<String> observerIDsss;
    private Bundle bundleArray;


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


    public void setObserverIDs(ArrayList<String> observerIDs) {
        this.observerIDsss = observerIDs;
        System.out.println("J'ai reçu une Array_______");
        AfficheArrayList(observerIDs);
    }

    public ArrayList<String> getObserverIDs() {
        System.out.println("Je vais renvoyer l'ArrayList_______");
        AfficheArrayList(observerIDsss);
        return this.observerIDsss;
    }

    public Bundle getBundleArray() {
        return bundleArray;
    }

    public void setBundleArray(Bundle bundleArray) {
        this.bundleArray = bundleArray;
    }

    private void AfficheArrayList(ArrayList listObservateur) {
        System.out.println("\n --- ArrayListModel --- \n");
        for (int i = 0; i < listObservateur.size(); i++){
            //System.out.println(ticketTab.get(i));
            String oneObs = (String)listObservateur.get(i);
            System.out.println(oneObs);
        }


    }
}