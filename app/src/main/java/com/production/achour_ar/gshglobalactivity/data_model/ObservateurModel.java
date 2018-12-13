package com.production.achour_ar.gshglobalactivity.data_model;

import android.os.Parcel;
import android.os.Parcelable;

public class ObservateurModel implements Parcelable {
    String username, email, telephone, prenomObs, nomObs, lieu, poste;

    public ObservateurModel(String username, String email, String telephone, String prenom, String nom, String lieu, String poste) {
        this.username = username;
        this.email = email;
        this.telephone = telephone;
        this.prenomObs = prenom;
        this.nomObs = nom;
        this.lieu = lieu;
        this.poste = poste;
    }

    protected ObservateurModel(Parcel in) {
        username = in.readString();
        email = in.readString();
        telephone = in.readString();
        prenomObs = in.readString();
        nomObs = in.readString();
        lieu = in.readString();
        poste = in.readString();
    }

    public static final Creator<ObservateurModel> CREATOR = new Creator<ObservateurModel>() {
        @Override
        public ObservateurModel createFromParcel(Parcel in) {
            return new ObservateurModel(in);
        }

        @Override
        public ObservateurModel[] newArray(int size) {
            return new ObservateurModel[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getPrenomObs() {
        return prenomObs;
    }

    public String getNomObs() {
        return nomObs;
    }

    public String getLieu() {
        return lieu;
    }

    public String getPoste() {
        return poste;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(telephone);
        dest.writeString(prenomObs);
        dest.writeString(nomObs);
        dest.writeString(lieu);
        dest.writeString(poste);
    }
}
