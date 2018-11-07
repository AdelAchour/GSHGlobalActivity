package com.production.achour_ar.gshglobalactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoTicket extends AppCompatActivity {

    String session_token, nameUser, idUser, firstnameUser;

    String titreTicket, slaTicket, urgenceTicket,
            categorieTicket, etatTicket, dateDebutTicket, idTicket,
            dateEchanceTicket, dateResolutionTicket, descriptionTicket, lieuTicket, dateClotureTicket;
    String observateur;

    String usernameDemandeur, emailDemandeur, telephoneDemandeur, prenomDemandeur, nomDemandeur, lieuDemandeur,posteDemandeur;

    String usernameObservateur, emailObservateur, telephoneObservateur, prenomObservateur, nomObservateur, lieuObservateur,posteObservateur;

    TextView titreTV, slaTV, infoRetardTV,
            demandeurTV, categorieTV, etatTV, dateDebutTV, ObservateurTV,
            dateEchanceTV, descriptionTV, lieuTV, dateClotureTicketTV, dateResolutionTicketTV;

    RequestQueue queue;

    ProgressBar progressBarInfo;
    ProgressDialog pd;
    boolean ticketEnretard;
    String[] tabObs;
    String[][] tabInfoObs;
    int indexObs;
    ArrayList<ObservateurModel> listObservateur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_ticket);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Information du ticket");
        //actionBar.setHomeButtonEnabled(true); //show a caret only if android:parentActivityName is specified.
        actionBar.setDisplayHomeAsUpEnabled(true); //show a caret even if android:parentActivityName is not specified.

        listObservateur =new ArrayList<ObservateurModel>();

        pd = new ProgressDialog(InfoTicket.this);
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
        dateResolutionTicketTV = (TextView)findViewById(R.id.ResolutionAnswer);
        lieuTV = (TextView)findViewById(R.id.LieuAnswer);
        dateClotureTicketTV = (TextView)findViewById(R.id.ClotureAnswer);
        infoRetardTV = (TextView)findViewById(R.id.retardtimerAnswer);

        infoRetardTV.setVisibility(View.GONE);

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
        paramsTicket.add(new KeyValuePair("forcedisplay[14]","17"));


        String urlTicket = Constants.GLPI_URL+"search/Ticket";

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
                                    dateResolutionTicket = oneTicket.getString("17");
                                    ticketEnretard = getBooleanFromSt(oneTicket.getString("82"));
                                    observateur = oneTicket.getString("66");

                                    JSONArray JObs = oneTicket.getJSONArray("66");
                                    tabObs = new String[JObs.length()];
                                    for (int j=0; j < JObs.length(); j++) {
                                        try {
                                            String oneObservateur = JObs.getString(j);
                                            tabObs[j] = oneObservateur;
                                            Log.i("One obs", tabObs[j]);

                                        } catch (JSONException e) {
                                            Log.e("Error Observateur ", e.getMessage());
                                        }

                                    }

                                } catch (JSONException e) {
                                    Log.e("Error ticket ", e.getMessage());
                                }

                            }

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
                            dateResolutionTicketTV.setText(ResolutionText(dateResolutionTicket));

                            Log.d("get Observateur",observateur);
                            if (tabObs==null){
                                getObservateurInfo(observateur);
                            }
                            else if((tabObs!=null)&&(tabObs.length>1)){
                                Log.i("not null", "Plusieurs observateurs");
                                getAllObservateursInfo(tabObs);
                            }

                            getDemandeurInfo(iddemandeur);

                            if (dateClotureTicket.equals("null")){
                                dateClotureTicketTV.setTypeface(dateClotureTicketTV.getTypeface(), Typeface.ITALIC);
                            }
                            if (dateResolutionTicket.equals("null")){
                                dateResolutionTicketTV.setTypeface(dateResolutionTicketTV.getTypeface(), Typeface.ITALIC);
                            }

                            infoRetardTV.setPaintFlags(infoRetardTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            infoRetardTV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(ticketEnretard){
                                        DialogTimerRetard alert = new DialogTimerRetard();
                                        alert.showDialog(InfoTicket.this, dateEchanceTicket, true);
                                    }
                                    else{
                                        DialogTimerRetard alert = new DialogTimerRetard();
                                        alert.showDialog(InfoTicket.this, dateEchanceTicket, false);
                                    }
                                }
                            });


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
                params.put("App-Token",Constants.App_Token);
                params.put("Session-Token",session_token);
                return params;
            }

        };
        queue.add(getRequestTicket);


    }

    private void getObservateurInfo(final String observateur) {
        //Récupération des informations de l'observateur
        String urlObs = Constants.GLPI_URL+"search/User";

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
                            Log.e("0 obs ou Groupe",e.getMessage());
                        }

                        final String NomPrenomObs = nomObservateur+" "+prenomObservateur;
                        System.out.println("nom obs "+nomObservateur);
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
                                    alert.showDialog(InfoTicket.this, NomPrenomObs, emailObservateur, telephoneObservateur, lieuObservateur, posteObservateur);
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
                params.put("App-Token",Constants.App_Token);
                params.put("Session-Token",session_token);
                return params;
            }

        };

        queue.add(getRequestDemandeur);
    }

    private void getAllObservateursInfo(final String[] observateur) {
        //Récupération des informations de tous les observateurs
        String urlObs = Constants.GLPI_URL+"search/User";

        System.out.println("taille obs = "+observateur.length);
        tabInfoObs = new String[observateur.length][7];

        for (indexObs = 0; indexObs < observateur.length; indexObs++){
            System.out.println("Obs n°"+indexObs);
            List<KeyValuePair> paramsObs = new ArrayList<>();
            paramsObs.add(new KeyValuePair("criteria[0][field]","2"));
            paramsObs.add(new KeyValuePair("criteria[0][searchtype]","equals"));
            paramsObs.add(new KeyValuePair("criteria[0][value]",observateur[indexObs]));
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
                            System.out.println("dans response all observateur");
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
                                Log.e("JSON Error response",e.getMessage());
                            }

                            ObservateurModel obsM = new ObservateurModel(usernameObservateur, emailObservateur,
                                    telephoneObservateur,prenomObservateur,nomObservateur,
                                    lieuObservateur, posteObservateur);

                            listObservateur.add(obsM);
                            
                            ObservateurTV.setText("Afficher les observateurs");
                            ObservateurTV.setPaintFlags(ObservateurTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


                            ObservateurTV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AfficheArrayList(listObservateur);

                                    Intent intent = new Intent(InfoTicket.this, ObservateurList.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelableArrayList("Obs", listObservateur);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
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
                    params.put("App-Token",Constants.App_Token);
                    params.put("Session-Token",session_token);
                    return params;
                }

            };

            queue.add(getRequestDemandeur);
            
        }
    }

    private void AfficheTab(String[][] ticketTab) {
        System.out.println("\n --- Tableau de ticket --- \n");
        for (int i = 0; i < ticketTab.length; i++){
            for(int j = 0; j<ticketTab[0].length; j++){
                System.out.print(ticketTab[i][j]+" ");
            }
            System.out.println("\n");
        }
    }

    private void AfficheArrayList(ArrayList listObservateur) {
        System.out.println("\n --- ArrayList --- \n");
        for (int i = 0; i < listObservateur.size(); i++){
            //System.out.println(ticketTab.get(i));
            ObservateurModel oneObs = (ObservateurModel)listObservateur.get(i);
            System.out.println(oneObs.getNomObs());
        }
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

    private String ClotureText(String dateClotureTicket) {
        if (dateClotureTicket.equals("null")){
            return "Non clos pour l'instant";
        }
        else{
            return dateClotureTicket;
        }
    }

    private String ResolutionText(String dateResolutionTicket) {
        if (dateResolutionTicket.equals("null")){
            return "Non résolu pour l'instant";
        }
        else{
            return dateResolutionTicket;
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
        String urlDemandeur = Constants.GLPI_URL+"search/User";

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
                                alert.showDialog(InfoTicket.this, NomPrenomAsker, emailDemandeur, telephoneDemandeur, lieuDemandeur, posteDemandeur);
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
                params.put("App-Token",Constants.App_Token);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
