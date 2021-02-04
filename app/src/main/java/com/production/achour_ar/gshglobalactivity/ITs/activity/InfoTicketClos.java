package com.production.achour_ar.gshglobalactivity.ITs.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
import com.production.achour_ar.gshglobalactivity.ITs.data_model.KeyValuePair;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.ObservateurModel;
import com.production.achour_ar.gshglobalactivity.R;
import com.production.achour_ar.gshglobalactivity.ITs.manager.URLGenerator;
import com.production.achour_ar.gshglobalactivity.ITs.manager.WorkTimeCalculator;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.Constants;
import com.production.achour_ar.gshglobalactivity.ITs.dialog.DialogDemandeur;
import com.production.achour_ar.gshglobalactivity.ITs.dialog.DialogTemps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoTicketClos extends AppCompatActivity {

    private String session_token, nameUser, idUser, firstnameUser;
    private String titreTicket, slaTicket, urgenceTicket,
            categorieTicket, etatTicket, dateDebutTicket, idTicket, tempsResolution, tempsRetard,
            dateEchanceTicket, descriptionTicket, lieuTicket, dateClotureTicket, dateResolutionTicket;
    private boolean ticketEnretard;
    private String usernameDemandeur, emailDemandeur, telephoneDemandeur, prenomDemandeur, nomDemandeur, lieuDemandeur,posteDemandeur;
    private String usernameObservateur, emailObservateur, telephoneObservateur, prenomObservateur, nomObservateur, lieuObservateur,posteObservateur;
    private TextView titreTV, slaTV, detailtempsTV, idTV,
            demandeurTV, categorieTV, etatTV, dateDebutTV, ObservateurTV,
            dateEchanceTV, descriptionTV, lieuTV, dateClotureTicketTV, dateResolutionTicketTV;
    private RequestQueue queue;
    private String observateur;
    private ProgressBar progressBarDia;
    private ProgressDialog pd, pdCalcul;
    private ArrayList<ObservateurModel> listObservateur;
    private String[] tabObs;
    private static DateFormat parser ;
    public static Handler handlerInfoClos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_ticket_clos);

        initView();
        setupActionBar();
        setupPDs();
        getArguments();
        getTicketInfo();

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Information du ticket");
        actionBar.setDisplayHomeAsUpEnabled(true); //show a caret even if android:parentActivityName is not specified.
    }

    private void getTicketInfo() {
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

        final JsonObjectRequest getRequestTicket = new JsonObjectRequest(Request.Method.GET, URLGenerator.generateUrl(urlTicket, paramsTicket), null,
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
                            idTV.setText(idTicket);
                            descriptionTV.setText(descriptionTicket);
                            lieuTV.setText(lieuTicket);
                            dateClotureTicketTV.setText(ClotureText(dateClotureTicket));
                            dateResolutionTicketTV.setText(dateResolutionTicket);

                            detailtempsTV.setPaintFlags(detailtempsTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            detailtempsTV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(ticketEnretard){
                                        pdCalcul.show();
                                        Thread mThread = new Thread() {
                                            @Override
                                            public void run() {
                                                tempsResolution = calculTempsResolution(dateClotureTicket, dateDebutTicket);
                                                tempsRetard = calculTempsRetard(dateEchanceTicket, dateClotureTicket);
                                                pdCalcul.dismiss();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("resolution",tempsResolution);
                                                bundle.putString("retard",tempsRetard);
                                                Message msg = new Message();
                                                msg.setData(bundle);
                                                msg.what = 0;
                                                handlerInfoClos.sendMessage(msg);
                                            }
                                        };
                                        mThread.start();


                                    }
                                    else{
                                        pdCalcul.show();
                                        Thread mThread = new Thread() {
                                            @Override
                                            public void run() {
                                                tempsResolution = calculTempsResolution(dateClotureTicket, dateDebutTicket);
                                                tempsRetard = calculTempsRetard(dateEchanceTicket, dateClotureTicket);
                                                pdCalcul.dismiss();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("resolution",tempsResolution);
                                                bundle.putString("retard",tempsRetard);
                                                Message msg = new Message();
                                                msg.setData(bundle);
                                                msg.what = 1;
                                                handlerInfoClos.sendMessage(msg);
                                            }
                                        };
                                        mThread.start();
                                    }

                                }
                            });


                            Log.d("get Observateur", ""+observateur);
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

    private void initView() {
        listObservateur = new ArrayList<ObservateurModel>();
        pd = new ProgressDialog(InfoTicketClos.this);
        pdCalcul = new ProgressDialog(InfoTicketClos.this);
        handlerInfoClos = new HandlerInfoClos();
        parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        queue = Volley.newRequestQueue(this);
        titreTV = findViewById(R.id.titreAnswer);
        idTV = findViewById(R.id.IDAnswer);
        slaTV = findViewById(R.id.SLAAnswer);
        descriptionTV = findViewById(R.id.DescriptionAnswer);
        demandeurTV = findViewById(R.id.demandeurAnswer);
        ObservateurTV = findViewById(R.id.ObsAnswer);
        categorieTV = findViewById(R.id.categorieAnswer);
        etatTV = findViewById(R.id.StatutAnswer);
        dateDebutTV = findViewById(R.id.DebutAnswer);
        dateEchanceTV = findViewById(R.id.EcheanceAnswer);
        lieuTV = findViewById(R.id.LieuAnswer);
        dateClotureTicketTV = findViewById(R.id.ClotureAnswer);
        detailtempsTV = findViewById(R.id.DetailTimeAnswer);
        dateResolutionTicketTV = findViewById(R.id.ResolutionAnswer);
        //progressBarDia = findViewById(R.id.progressBarDialog);
    }

    private void getArguments() {
        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        idUser = i.getStringExtra("id");
        idTicket = i.getStringExtra("idTicket");
    }

    private void setupPDs() {
        pdCalcul.setMessage("Calcul...");
        pd.setMessage("Chargement...");
        pd.show();
    }


    private void getAllObservateursInfo(String[] observateur) {
        //Récupération des informations de tous les observateurs
        String urlObs = Constants.GLPI_URL+"search/User";

        System.out.println("taille obs = "+observateur.length);

        for (int indexObs = 0; indexObs < observateur.length; indexObs++){
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


            final JsonObjectRequest getRequestDemandeur = new JsonObjectRequest(Request.Method.GET, URLGenerator.generateUrl(urlObs, paramsObs), null,
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

                                    Intent intent = new Intent(InfoTicketClos.this, ObservateurList.class);
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

    private void AfficheArrayList(ArrayList<ObservateurModel> listObservateur) {
        System.out.println("\n --- ArrayList --- \n");
        for (int i = 0; i < listObservateur.size(); i++){
            //System.out.println(ticketTab.get(i));
            ObservateurModel oneObs = (ObservateurModel)listObservateur.get(i);
            System.out.println(oneObs.getNomObs());
        }
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

        final JsonObjectRequest getRequestDemandeur = new JsonObjectRequest(Request.Method.GET, URLGenerator.generateUrl(urlObs, paramsObs), null,
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
                params.put("App-Token",Constants.App_Token);
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

        WorkTimeCalculator calculator = new WorkTimeCalculator(parse(dateEchanceTicket), parse(dateClotureTicket));
        System.out.println("Working time: "+calculator.getMinutes()+"\n\n\n");
        long tmps = calculator.getMinutes()*60*1000;
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

        WorkTimeCalculator calculator = new WorkTimeCalculator(parse(dateDebutTicket), parse(dateClotureTicket));
        System.out.println("Working time: "+calculator.getMinutes()+"\n\n\n");
        long tmps = calculator.getMinutes()*60*1000;
        return String.valueOf(tmps);
    }

    private Date parse(String date) {
        try {
            return parser.parse(date);
        } catch (ParseException e) {
            Log.e("Parse error",date);
            return null;
        }
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

        final JsonObjectRequest getRequestDemandeur = new JsonObjectRequest(Request.Method.GET, URLGenerator.generateUrl(urlDemandeur, paramsDemandeur), null,
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

    class HandlerInfoClos extends Handler{
        String tempsResolution;
        String tempsRetard;
        Bundle bundle;
        DialogTemps alert;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0: //Go to Dialog (Reso+Retard)
                    Log.i("Dialog","Going to Dialog");
                    bundle = msg.getData();
                    tempsResolution = bundle.getString("resolution");
                    tempsRetard = bundle.getString("retard");
                    alert = new DialogTemps();
                    alert.showDialog(InfoTicketClos.this, "Ticket clos en retard", tempsResolution, tempsRetard, true);
                    break;

                case 1: //stop chargement
                    Log.i("Stop charment","Je devrais arrêter de charger");
                    bundle = msg.getData();
                    tempsResolution = bundle.getString("resolution");
                    tempsRetard = bundle.getString("retard");
                    alert = new DialogTemps();
                    alert.showDialog(InfoTicketClos.this, "Ticket clos à temps", tempsResolution, tempsRetard, false);
                    break;
            }
        }
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
