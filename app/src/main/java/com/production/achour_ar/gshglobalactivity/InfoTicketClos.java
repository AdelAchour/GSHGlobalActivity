package com.production.achour_ar.gshglobalactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoTicketClos extends AppCompatActivity {


    String session_token, nameUser, idUser, firstnameUser;

    String titreTicket, slaTicket, urgenceTicket,
            categorieTicket, etatTicket, dateDebutTicket, idTicket, tempsResolution, tempsRetard,
            dateEchanceTicket, descriptionTicket, lieuTicket, dateClotureTicket;
    boolean ticketEnretard;

    String usernameDemandeur, emailDemandeur, telephoneDemandeur, prenomDemandeur, nomDemandeur, lieuDemandeur,posteDemandeur;
    String usernameObservateur, emailObservateur, telephoneObservateur, prenomObservateur, nomObservateur, lieuObservateur,posteObservateur;

    TextView titreTV, slaTV, detailtempsTV,
            demandeurTV, categorieTV, etatTV, dateDebutTV, ObservateurTV,
            dateEchanceTV, descriptionTV, lieuTV, dateClotureTicketTV;

    RequestQueue queue;
    String observateur;

    ProgressBar progressBarInfo;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_ticket_clos);

        pd = new ProgressDialog(InfoTicketClos.this);
        pd.setMessage("Chargement...");
        pd.show();

        queue = Volley.newRequestQueue(this);

        titreTV = (TextView)findViewById(R.id.titreAnswer);
        slaTV = (TextView)findViewById(R.id.SLAAnswer);
        descriptionTV = (TextView)findViewById(R.id.DescriptionAnswer);
        demandeurTV = (TextView)findViewById(R.id.demandeurAnswer);
        ObservateurTV = (TextView)findViewById(R.id.ObsAnswer);
        categorieTV = (TextView)findViewById(R.id.categorieAnswer);
        etatTV = (TextView)findViewById(R.id.StatutAnswer);
        dateDebutTV = (TextView)findViewById(R.id.DebutAnswer);
        dateEchanceTV = (TextView)findViewById(R.id.EcheanceAnswer);
        lieuTV = (TextView)findViewById(R.id.LieuAnswer);
        dateClotureTicketTV = (TextView)findViewById(R.id.ClotureAnswer);
        detailtempsTV = (TextView)findViewById(R.id.DetailTimeAnswer);


        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        idUser = i.getStringExtra("id");

        idTicket = i.getStringExtra("idTicket");

        System.out.println("id t = "+idTicket);

        //Récupération du ticket
        List<KeyValuePair> paramsTicket = new ArrayList<>();
        paramsTicket.add(new KeyValuePair("criteria[0][field]","2"));
        paramsTicket.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        paramsTicket.add(new KeyValuePair("criteria[0][value]",idTicket));

        paramsTicket.add(new KeyValuePair("forcedisplay[0]","4"));
        paramsTicket.add(new KeyValuePair("forcedisplay[1]","10"));
        paramsTicket.add(new KeyValuePair("forcedisplay[2]","7"));
        paramsTicket.add(new KeyValuePair("forcedisplay[3]","12"));
        paramsTicket.add(new KeyValuePair("forcedisplay[4]","15"));
        paramsTicket.add(new KeyValuePair("forcedisplay[5]","30"));
        paramsTicket.add(new KeyValuePair("forcedisplay[6]","18"));
        paramsTicket.add(new KeyValuePair("forcedisplay[7]","21"));
        paramsTicket.add(new KeyValuePair("forcedisplay[8]","83"));
        paramsTicket.add(new KeyValuePair("forcedisplay[9]","82"));
        paramsTicket.add(new KeyValuePair("forcedisplay[10]","16"));
        paramsTicket.add(new KeyValuePair("forcedisplay[11]","12"));
        paramsTicket.add(new KeyValuePair("forcedisplay[12]","2"));
        paramsTicket.add(new KeyValuePair("forcedisplay[13]","66"));


        String urlTicket = FirstEverActivity.GLPI_URL+"search/Ticket";

        final JsonObjectRequest getRequestTicket = new JsonObjectRequest(Request.Method.GET, generateUrl(urlTicket, paramsTicket), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String iddemandeur = "";

                            JSONArray Jdata = response.getJSONArray("data");
                            for (int i=0; i < Jdata.length(); i++) {
                                try {
                                    JSONObject oneTicket = Jdata.getJSONObject(i);
                                    titreTicket = oneTicket.getString("1");
                                    slaTicket = oneTicket.getString("30");
                                    dateDebutTicket = oneTicket.getString("15");
                                    urgenceTicket = oneTicket.getString("10");
                                    idTicket = oneTicket.getString("2");

                                    iddemandeur = oneTicket.getString("4");
                                    categorieTicket = oneTicket.getString("7");
                                    etatTicket = oneTicket.getString("12");
                                    dateEchanceTicket = oneTicket.getString("18");
                                    descriptionTicket = oneTicket.getString("21");

                                    lieuTicket = oneTicket.getString("83");
                                    dateClotureTicket = oneTicket.getString("16");
                                    ticketEnretard = getBooleanFromSt(oneTicket.getString("82"));
                                    observateur = oneTicket.getString("66");

                                } catch (JSONException e) {
                                    Log.e("Error ticket ", e.getMessage());
                                }

                            }

                            tempsResolution = calculTempsResolution(dateClotureTicket, dateDebutTicket);
                            tempsRetard = calculTempsRetard(dateEchanceTicket, dateClotureTicket);

                            titreTV.setText(titreTicket);
                            categorieTV.setText(categorieTicket);
                            etatTV.setText(StatutTexte(etatTicket));
                            dateDebutTV.setText(dateDebutTicket);
                            slaTV.setText(slaTicket);
                            dateEchanceTV.setText(dateEchanceTicket);
                            titreTV.setText(titreTicket);
                            descriptionTV.setText(descriptionTicket);
                            lieuTV.setText(lieuTicket);
                            dateClotureTicketTV.setText(ClotureText(dateClotureTicket));

                            detailtempsTV.setPaintFlags(detailtempsTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            detailtempsTV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(ticketEnretard){
                                        DialogTemps alert = new DialogTemps();
                                        alert.showDialog(InfoTicketClos.this, "Ticket clos en retard", tempsResolution, tempsRetard, true);
                                    }
                                    else{
                                        DialogTemps alert = new DialogTemps();
                                        alert.showDialog(InfoTicketClos.this, "Ticket clos à temps", tempsResolution, tempsRetard, false);
                                    }

                                }
                            });

                            getObservateurInfo(observateur);
                            getDemandeurInfo(iddemandeur);

                            if (dateClotureTicket.equals("null")){
                                dateClotureTicketTV.setTypeface(dateClotureTicketTV.getTypeface(), Typeface.ITALIC);
                            }

                        } catch (JSONException e) {
                            Log.e("Error ticket ",e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressBar.setVisibility(View.GONE);
                        Log.e("Eeeeeh ticket ", error.toString());
                    }

                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("App-Token",FirstEverActivity.App_Token);
                params.put("Session-Token",session_token);
                return params;
            }

        };
        queue.add(getRequestTicket);


    }

    private void getObservateurInfo(final String observateur) {
        //Récupération des informations de l'observateur
        String urlObs = FirstEverActivity.GLPI_URL+"search/User";

        List<KeyValuePair> paramsObs = new ArrayList<>();
        paramsObs.add(new KeyValuePair("criteria[0][field]","2"));
        paramsObs.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        paramsObs.add(new KeyValuePair("criteria[0][value]",observateur));
        paramsObs.add(new KeyValuePair("forcedisplay[0]","9"));
        paramsObs.add(new KeyValuePair("forcedisplay[1]","34"));
        paramsObs.add(new KeyValuePair("forcedisplay[2]","5"));
        paramsObs.add(new KeyValuePair("forcedisplay[3]","6"));
        paramsObs.add(new KeyValuePair("forcedisplay[4]","81"));

        final JsonObjectRequest getRequestDemandeur = new JsonObjectRequest(Request.Method.GET, generateUrl(urlObs, paramsObs), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("dans response observateur");
                        try {
                            JSONArray Jdata = response.getJSONArray("data");
                            try {
                                JSONObject userInfo = Jdata.getJSONObject(0);
                                // Récupération des données de l'observateur
                                usernameObservateur = userInfo.getString("1");
                                emailObservateur = userInfo.getString("5");
                                telephoneObservateur = userInfo.getString("6");
                                prenomObservateur = userInfo.getString("9");
                                nomObservateur = userInfo.getString("34");
                                lieuObservateur = userInfo.getString("80");
                                posteObservateur = userInfo.getString("81");

                            } catch (JSONException e) {
                                Log.e("Error JSONArray : ", e.getMessage());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final String NomPrenomObs = nomObservateur+" "+prenomObservateur;
                        if (nomObservateur == null){
                            ObservateurTV.setText("Aucun observateur");
                        }
                        else {
                            ObservateurTV.setText(NomPrenomObs);
                            ObservateurTV.setPaintFlags(ObservateurTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


                            ObservateurTV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    DialogDemandeur alert = new DialogDemandeur();
                                    alert.showDialog(InfoTicketClos.this, NomPrenomObs, emailObservateur, telephoneObservateur, lieuObservateur, posteObservateur);
                                }
                            });
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressBar.setVisibility(View.GONE);
                        Log.e("Error.Response", error.toString());
                    }

                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("App-Token",FirstEverActivity.App_Token);
                params.put("Session-Token",session_token);
                return params;
            }

        };

        queue.add(getRequestDemandeur);
    }

    private boolean getBooleanFromSt(String string) {
        boolean bool = false;
        if(string.equals("0")){
            bool = false;
        }
        else if (string.equals("1")){
            bool = true;
        }
        return bool;
    }

    private String calculTempsRetard(String dateEchanceTicket, String dateClotureTicket) {
        if (dateEchanceTicket.equals("null")){
            return "-1";
        }
        long echeance = getDateDebutMS(dateEchanceTicket);
        long cloture = getDateDebutMS(dateClotureTicket);

        long tmps = cloture - echeance;
        return String.valueOf(tmps);
    }

    private long getDateDebutMS(String dateDebutTicket) {
        long dateDebutMS = 0;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //"2018-07-17 11:58:47
        formatter.setLenient(false);

        String oldTime = dateDebutTicket;
        Date oldDate = null;
        try {
            oldDate = formatter.parse(oldTime);
        } catch (ParseException e) { e.printStackTrace(); }
        dateDebutMS = oldDate.getTime();

        return dateDebutMS;
    }

    private String calculTempsResolution(String dateClotureTicket, String dateDebutTicket) {
        if (dateClotureTicket.equals("null")){
            return "-1";
        }
        long debut = getDateDebutMS(dateDebutTicket);
        long cloture = getDateDebutMS(dateClotureTicket);

        long tmps = cloture - debut;
        return String.valueOf(tmps);
    }

    private String ClotureText(String dateClotureTicket) {
        if (dateClotureTicket.equals("null")){
            return "Non clos pour l'instant";
        }
        else{
            return dateClotureTicket;
        }
    }

    private String StatutTexte(String etatTicket) {
        String etat = "";
        int et = Integer.valueOf(etatTicket);
        switch (et){
            case 1:
                etat = "Nouveau";
                break;
            case 2:
                etat = "En cours (Attribué)";
                break;
            case 3:
                etat = "En cours (Planifié)";
                break;
            case 4:
                etat = "En attente";
                break;
            case 5:
                etat = "Résolu";
                break;
            case 6:
                etat = "Clos";
                break;
        }

        return etat;
    }

    private void getDemandeurInfo(String iddemandeur) {
        //Récupération des informations du demandeur
        String urlDemandeur = FirstEverActivity.GLPI_URL+"search/User";

        List<KeyValuePair> paramsDemandeur = new ArrayList<>();
        paramsDemandeur.add(new KeyValuePair("criteria[0][field]","2"));
        paramsDemandeur.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        paramsDemandeur.add(new KeyValuePair("criteria[0][value]",iddemandeur));
        paramsDemandeur.add(new KeyValuePair("forcedisplay[0]","9"));
        paramsDemandeur.add(new KeyValuePair("forcedisplay[1]","34"));
        paramsDemandeur.add(new KeyValuePair("forcedisplay[2]","5"));
        paramsDemandeur.add(new KeyValuePair("forcedisplay[3]","6"));
        paramsDemandeur.add(new KeyValuePair("forcedisplay[4]","81"));

        final JsonObjectRequest getRequestDemandeur = new JsonObjectRequest(Request.Method.GET, generateUrl(urlDemandeur, paramsDemandeur), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("dans response demandeur");
                        try {
                            JSONArray Jdata = response.getJSONArray("data");
                            try {
                                JSONObject userInfo = Jdata.getJSONObject(0);
                                // Récupération des données du demandeur
                                usernameDemandeur = userInfo.getString("1");
                                emailDemandeur = userInfo.getString("5");
                                telephoneDemandeur = userInfo.getString("6");
                                prenomDemandeur = userInfo.getString("9");
                                nomDemandeur = userInfo.getString("34");
                                lieuDemandeur = userInfo.getString("80");
                                posteDemandeur = userInfo.getString("81");

                            } catch (JSONException e) {
                                Log.e("Error JSONArray : ", e.getMessage());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final String NomPrenomAsker = nomDemandeur+" "+prenomDemandeur;
                        demandeurTV.setText(NomPrenomAsker);
                        demandeurTV.setPaintFlags(demandeurTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        //progressBarInfo.setVisibility(View.GONE);
                        pd.dismiss();


                        demandeurTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DialogDemandeur alert = new DialogDemandeur();
                                alert.showDialog(InfoTicketClos.this, NomPrenomAsker, emailDemandeur, telephoneDemandeur, lieuDemandeur, posteDemandeur);
                            }
                        });

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressBar.setVisibility(View.GONE);
                        Log.e("Error.Response", error.toString());
                    }

                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("App-Token",FirstEverActivity.App_Token);
                params.put("Session-Token",session_token);
                return params;
            }

        };

        queue.add(getRequestDemandeur);
    }


    public static String generateUrl(String baseUrl, List<KeyValuePair> params) {
        if (params.size() > 0) {
            int cpt = 1 ;
            for (KeyValuePair parameter: params) {
                if (cpt==1){
                    baseUrl += "?" + parameter.getKey() + "=" + parameter.getValue();
                }
                else{
                    baseUrl += "&" + parameter.getKey() + "=" + parameter.getValue();
                }
                cpt++;
            }
        }
        return baseUrl;
    }
}
