package com.production.achour_ar.gshglobalactivity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.production.achour_ar.gshglobalactivity.DataModel.TicketModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoTicket extends Activity {

    String session_token, nameUser, idUser, firstnameUser;

    String titreTicket, slaTicket, urgenceTicket,
            demandeurTicket, categorieTicket, etatTicket, dateDebutTicket,
            dateEchanceTicket, descriptionTicket, lieuTicket, tempsRestantTicket, dateClotureTicket;

    String usernameDemandeur, emailDemandeur, telephoneDemandeur, prenomDemandeur, nomDemandeur, lieuDemandeur;

    TextView titreTV, slaTV, urgenceTV,
            demandeurTV, categorieTV, etatTV, dateDebutTV,
            dateEchanceTV, descriptionTV, lieuTV, dateClotureTicketTV;

    RequestQueue queue;



    public static String[] infoTicket = new String[ListTickets.nbInfoTicket];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_ticket);

        queue = Volley.newRequestQueue(this);

        titreTV = (TextView)findViewById(R.id.TitreAnswer);
        slaTV = (TextView)findViewById(R.id.SLAAnswer);
        descriptionTV = (TextView)findViewById(R.id.DescriptionAnswer);
        urgenceTV = (TextView)findViewById(R.id.UrgenceAnswer);
        demandeurTV = (TextView)findViewById(R.id.DemandeurAnswer);
        categorieTV = (TextView)findViewById(R.id.CategorieAnswer);
        etatTV = (TextView)findViewById(R.id.EtatAnswer);
        dateDebutTV = (TextView)findViewById(R.id.DateAnswer);
        dateEchanceTV = (TextView)findViewById(R.id.DateEchAnswer);
        lieuTV = (TextView)findViewById(R.id.LieuAnswer);
        dateClotureTicketTV = (TextView)findViewById(R.id.dateClosAnswer);



        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        idUser = i.getStringExtra("id");

        infoTicket = i.getStringArrayExtra("infoTicket");

        demandeurTicket = infoTicket[0];
        urgenceTicket = infoTicket[1];
        categorieTicket = infoTicket[2];
        etatTicket = infoTicket[3];
        dateDebutTicket = infoTicket[4];
        slaTicket = infoTicket[5];
        dateEchanceTicket = infoTicket[6];
        titreTicket = infoTicket[7];
        descriptionTicket = infoTicket[8];
        lieuTicket = infoTicket[9];
        tempsRestantTicket = infoTicket[10];
        dateClotureTicket = infoTicket[11];



        //Récupération des informations du demandeur
        String url = FirstEverActivity.GLPI_URL+"search/User";



        List<KeyValuePair> params = new ArrayList<>();
        params.add(new KeyValuePair("criteria[0][field]","2"));
        params.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        params.add(new KeyValuePair("criteria[0][value]",demandeurTicket));
        params.add(new KeyValuePair("forcedisplay[0]","9"));
        params.add(new KeyValuePair("forcedisplay[1]","34"));
        params.add(new KeyValuePair("forcedisplay[2]","5"));
        params.add(new KeyValuePair("forcedisplay[3]","6"));

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(url, params), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
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

                                    //System.out.println("Titre = " + titreTicket + "\n SLA = " + slaTicket + "\n Date = " + dateTicket);
                                } catch (JSONException e) {
                                    Log.e("Error JSONArray : ", e.getMessage());
                                }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

        // add it to the RequestQueue
        queue.add(getRequest);


        //Set text
        demandeurTV.setText(nomDemandeur+" "+prenomDemandeur);
        urgenceTV.setText(urgenceTicket);
        categorieTV.setText(categorieTicket);
        etatTV.setText(etatTicket);
        dateDebutTV.setText(dateDebutTicket);
        slaTV.setText(slaTicket);
        dateEchanceTV.setText(dateEchanceTicket);
        titreTV.setText(titreTicket);
        descriptionTV.setText(descriptionTicket);
        lieuTV.setText(lieuTicket);
        dateClotureTicketTV.setText(dateClotureTicket);


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
