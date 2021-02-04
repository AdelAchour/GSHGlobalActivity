package com.production.achour_ar.gshglobalactivity.ITs.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.UserModel;
import com.production.achour_ar.gshglobalactivity.ITs.dialog.DialogChoixPersonStat;
import com.production.achour_ar.gshglobalactivity.R;
import com.production.achour_ar.gshglobalactivity.ITs.manager.URLGenerator;
import com.production.achour_ar.gshglobalactivity.ITs.manager.WorkTimeCalculator;
import com.production.achour_ar.gshglobalactivity.ITs.adapter.ChartAdapter;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.ChartModel;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.Constants;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.KeyValuePair;
import com.production.achour_ar.gshglobalactivity.ITs.dialog.DialogChoixDate;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsTickets extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<ChartModel> ChartModelsTicket, ChartModelsParTicket;
    private ListView listChartTickets, listChartParTickets;
    private static ChartAdapter chartTicketAdapter, chartParTicketAdapter;
    private String nbCount;
    private int nbTicketCours = 0, nbTicketCours_Retard = 0, nbTicketAttente = 0,
            nbTicketResolu = 0, nbTicketClos = 0,
            nbTicketClos_Temps = 0, nbTicketClos_Retard = 0, nbTicketCours_A_Temps = 0;

    private String session_token, nameUser, idUser, firstnameUser;
    private BarChart BarOuvertClos, BarParEtat;
    private String actualDate, debutMoisDate, finMoisDate;
    private TextView intervalDateTV, changeDateTV, presentationNameTV, presentationTextTV, nbTicketCoursTV, nbTicketsCoursRetardTV,
            nbTicketAttenteTV, nbTicketResoluTV, nbTicketClosATempsTV, nbTicketClosRetardTV, successRateTV,
    tempsResolutionMoyenTV, tempsRetardMoyenTV;

    private RequestQueue queue;
    private String idTicket, dateDebutTicket, dateResolutionTicket , statutTicket, dateEchanceTicket, dateClotureTicket;
    private boolean ticketEnretard;
    public static Handler handlerDate;
    private ProgressDialog pd, pdShare;
    private static DateFormat parser ;
    private ArrayList<String> listDateDebut;
    private ArrayList<String> listDateCloture;
    private ArrayList<Long> listTempsResolution;

    private ArrayList<String> listDateEcheanceRetard;
    private ArrayList<String> listDateClotureRetard;
    private ArrayList<Long> listTempsRetard;

    private String pourcentageReussite;
    private String emailUser;

    public static Handler handler;
    private String emailR = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_tickets);

        initView();
        setupActionBar();
        setupPD();
        getArguments();
        initDate();
        setupTVs();
        setListener();

        try { bigStatRequest(debutMoisDate, finMoisDate); }
        catch (UnsupportedEncodingException e) { e.printStackTrace(); }

    }

    private void setListener() {
        intervalDateTV.setOnClickListener(this);
        changeDateTV.setOnClickListener(this);
    }

    private void setupTVs() {
        //Initialiser le texte du début
        String fullname = UserModel.getCurrentUserModel().getFullname();
        presentationNameTV.setText(fullname);
        intervalDateTV.setText(initializeDateText(debutMoisDate, finMoisDate));
    }

    private void initDate() {
        //initialisation des dates (intervalle)
        //actualDate = getActualDate(); //yyyy-MM-dd HH:mm:ss
        debutMoisDate = getDebutDate(); //yyyy-MM-01 00:00:00
        finMoisDate = getActualDate(); //yyyy-MM-dd HH:mm:ss
    }

    private void getArguments() {
        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        idUser = i.getStringExtra("id");
        emailUser = i.getStringExtra("email");
    }

    private void setupPD() {
        //ProgressDialog
        pd.setMessage("Requete en cours...");
        pdShare.setMessage("Envoi du rapport...");
        pd.show();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Stats de mes tickets");
        actionBar.setHomeButtonEnabled(true); //show a caret only if android:parentActivityName is specified.
    }

    private void initView() {
        handler = new HandlerStats();
        pd = new ProgressDialog(this);
        pdShare = new ProgressDialog(this);
        queue = Volley.newRequestQueue(getApplicationContext());
        handlerDate = new HandlerDate();
        parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        listDateDebut = new ArrayList<String>();
        listDateCloture = new ArrayList<String>();
        listTempsResolution = new ArrayList<Long>();
        listDateEcheanceRetard = new ArrayList<String>();
        listDateClotureRetard = new ArrayList<String>();
        listTempsRetard = new ArrayList<Long>();

        intervalDateTV = findViewById(R.id.presentationDateTicket);
        changeDateTV = findViewById(R.id.changeDateStat);
        presentationNameTV = findViewById(R.id.presentationName);
        presentationTextTV = findViewById(R.id.presentationTextStat);
        nbTicketCoursTV = findViewById(R.id.nbticketencours);
        nbTicketsCoursRetardTV = findViewById(R.id.dontticketenretard);
        nbTicketAttenteTV = findViewById(R.id.nbticketenattente);
        nbTicketResoluTV = findViewById(R.id.nbticketresolu);
        nbTicketClosATempsTV = findViewById(R.id.nbticketclos);
        nbTicketClosRetardTV = findViewById(R.id.nbticketclosretard);
        successRateTV = findViewById(R.id.statSuccessTicket);

        //Initialisation des graphes
        BarOuvertClos = findViewById(R.id.barOuvertClos);
        BarParEtat = findViewById(R.id.barParEtat);
    }

    private void bigStatRequest(final String debutMoisDate, final String finMoisDate) throws UnsupportedEncodingException {
        nbCount = "0";
        nbTicketCours = 0;
        nbTicketCours_Retard = 0;
        nbTicketAttente = 0;
        nbTicketResolu = 0;
        nbTicketClos = 0;
        nbTicketClos_Temps = 0;
        nbTicketClos_Retard = 0;
        nbTicketCours_A_Temps = 0;
        BarOuvertClos.clearChart();
        BarParEtat.clearChart();

        String url = Constants.GLPI_URL+"search/Ticket";

        List<KeyValuePair> params = new ArrayList<>();
        //TECHNICIEN = IDUSER
        params.add(new KeyValuePair("criteria[0][field]","5"));
        params.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        params.add(new KeyValuePair("criteria[0][value]",idUser));
        //AND DATE SUPERIEUR A
        params.add(new KeyValuePair("criteria[1][link]","AND"));
        params.add(new KeyValuePair("criteria[1][field]","15"));
        params.add(new KeyValuePair("criteria[1][searchtype]","morethan"));
        params.add(new KeyValuePair("criteria[1][value]", URLEncoder.encode(debutMoisDate, "UTF-8")));
        //AND DATE INFERIEUR A
        params.add(new KeyValuePair("criteria[2][link]","AND"));
        params.add(new KeyValuePair("criteria[2][field]","15"));
        params.add(new KeyValuePair("criteria[2][searchtype]","lessthan"));
        params.add(new KeyValuePair("criteria[2][value]",URLEncoder.encode(finMoisDate, "UTF-8")));
        //AFFICHAGE
        params.add(new KeyValuePair("forcedisplay[0]","4"));
        params.add(new KeyValuePair("forcedisplay[1]","10"));
        params.add(new KeyValuePair("forcedisplay[2]","7"));
        params.add(new KeyValuePair("forcedisplay[3]","12"));
        params.add(new KeyValuePair("forcedisplay[4]","15"));
        params.add(new KeyValuePair("forcedisplay[5]","30"));
        params.add(new KeyValuePair("forcedisplay[6]","18"));
        params.add(new KeyValuePair("forcedisplay[7]","21"));
        params.add(new KeyValuePair("forcedisplay[8]","83"));
        params.add(new KeyValuePair("forcedisplay[9]","82"));
        params.add(new KeyValuePair("forcedisplay[10]","16"));
        params.add(new KeyValuePair("forcedisplay[11]","2"));
        params.add(new KeyValuePair("forcedisplay[12]","17"));
        //RANGE
        params.add(new KeyValuePair("range","0-300"));

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URLGenerator.generateUrl(url, params), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            nbCount = response.getString("totalcount");
                            //nbCount = response.getString("count");
                            System.out.println("nb t = "+nbCount);

                            JSONArray Jdata = response.getJSONArray("data");
                            for (int i=0; i < Jdata.length(); i++) {
                                try {
                                    JSONObject oneTicket = Jdata.getJSONObject(i);

                                    idTicket = oneTicket.getString("2");

                                    statutTicket = oneTicket.getString("12");
                                    dateDebutTicket = oneTicket.getString("15");
                                    dateEchanceTicket = oneTicket.getString("18");
                                    dateClotureTicket = oneTicket.getString("16");
                                    dateResolutionTicket = oneTicket.getString("17");
                                    ticketEnretard = getBooleanFromSt(oneTicket.getString("82"));

                                } catch (JSONException e) {
                                    Log.e("Nb of data: "+Jdata.length()+" || "+"Error JSONArray at "+i+" : ", e.getMessage());
                                }
                                // ------------------------

                                if (statutTicket.equals("6")){
                                    //Here, i'm gonna create a list of ticket_clos_date object (with date debut & date cloture variables)
                                    listDateDebut.add(dateDebutTicket);
                                    listDateCloture.add(dateClotureTicket);

                                    if (ticketEnretard){
                                        listDateEcheanceRetard.add(dateEchanceTicket);
                                        listDateClotureRetard.add(dateClotureTicket);
                                    }
                                }


                                NumberTicketPerState(statutTicket, ticketEnretard);

                                // -----------------------------

                            }

                            //Populate textviews and graphs
                            PopulateTextViews();
                            if (pd.isShowing()) pd.dismiss();

                            PopulateGraphs();

                        }
                        catch (JSONException e){
                            Log.e("malkach",e.getMessage());
                            PopulateZeroTextViews();
                            if (pd.isShowing()) pd.dismiss();
                            PopulateZeroGraphs();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response!", error.toString());
                        Toast.makeText(getApplicationContext(), "Vérifiez votre connexion", Toast.LENGTH_LONG).show();
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

        // add it to the RequestQueue
        queue.add(getRequest);
    }

    private void PopulateZeroGraphs() {
        //Graph par ticket
        BarOuvertClos.addBar(new BarModel(0.f, 0xFF247c8f));
        BarOuvertClos.addBar(new BarModel(0.f,  0xFF292929));
        BarOuvertClos.startAnimation();


        //Graphes par état
        BarParEtat.addBar(new BarModel(0.f, 0xFF247c8f));
        BarParEtat.addBar(new BarModel(0.f,  0xFF5e1212));
        BarParEtat.addBar(new BarModel(0.f,  0xFFa95516));
        BarParEtat.addBar(new BarModel(0.f,  0xFF1b2a6b));
        BarParEtat.addBar(new BarModel(0.f,  0xFF292929));
        BarParEtat.addBar(new BarModel(0.f,  0xFF7b1d6e));
        BarParEtat.startAnimation();
    }

    private void PopulateZeroTextViews() {
        presentationTextTV.setText("0 ticket");
        nbTicketCoursTV.setText("0");
        //nbTicketsCoursRetardTV.setText("("+0+" en retard)");
        nbTicketsCoursRetardTV.setText("");
        nbTicketAttenteTV.setText("0");
        nbTicketResoluTV.setText("0");
        nbTicketClosATempsTV.setText("0");
        //nbTicketClosRetardTV.setText("("+0+" à temps - "+0+" en retard)");
        nbTicketClosRetardTV.setText("");
        String successZero = "0 sur 0 - 0,00% de réussite" ;
        successRateTV.setText(successZero);
    }

    private void PopulateGraphs() {
        //Graph par ticket
        BarOuvertClos.addBar(new BarModel(Float.valueOf(nbCount), 0xFF247c8f));
        BarOuvertClos.addBar(new BarModel((float) nbTicketClos,  0xFF292929));
        BarOuvertClos.startAnimation();

        System.out.println("ticket en cours "+nbTicketCours);
        System.out.println("ticket clos "+nbTicketClos);
        float pourcentageCalcul ;
        //String pourcentageReussite = "" ;
        int nbOuvert = Integer.valueOf(nbCount);
        if (nbOuvert>0){
            pourcentageCalcul = (((float) nbTicketClos / (float) nbOuvert)*100);
            pourcentageReussite = String.format("%.02f", pourcentageCalcul);
        }
        else{
            Log.e("div 0", " = 0");
            pourcentageReussite = String.format("%.02f", 0f);
        }

        String success = nbTicketClos + " sur " +nbOuvert + " - " + pourcentageReussite + "% de réussite" ;
        successRateTV.setText(success);

        //Graphes par état
        BarParEtat.addBar(new BarModel(Float.valueOf(nbTicketCours_A_Temps), 0xFF41a378));
        BarParEtat.addBar(new BarModel(Float.valueOf(nbTicketCours_Retard),  0xFF5e1212));
        BarParEtat.addBar(new BarModel(Float.valueOf(nbTicketAttente),  0xFFa95516));
        BarParEtat.addBar(new BarModel(Float.valueOf(nbTicketResolu),  0xFF1b2a6b));
        BarParEtat.addBar(new BarModel(Float.valueOf(nbTicketClos_Temps),  0xFF292929));
        BarParEtat.addBar(new BarModel(Float.valueOf(nbTicketClos_Retard),  0xFF7b1d6e));
        BarParEtat.startAnimation();

    }

    private void PopulateTextViews() {
        presentationTextTV.setText(""+nbCount+" tickets");
        nbTicketCoursTV.setText(String.valueOf(nbTicketCours));
        //nbTicketsCoursRetardTV.setText("("+nbTicketCours_Retard+" en retard)");
        nbTicketsCoursRetardTV.setText("");
        nbTicketAttenteTV.setText(String.valueOf(nbTicketAttente));
        nbTicketResoluTV.setText(String.valueOf(nbTicketResolu));
        nbTicketClosATempsTV.setText(String.valueOf(nbTicketClos));
        //nbTicketClosRetardTV.setText("("+nbTicketClos_Temps+" à temps - "+nbTicketClos_Retard+" en retard)");
        nbTicketClosRetardTV.setText("");
    }

    private void shareStat() throws UnsupportedEncodingException {
        String url = Constants.URL_STAT_API;

        String dateDebut = editformatdate(debutMoisDate);
        String datefin = editformatdate(finMoisDate);

        String fullnameIngenieur = firstnameUser+ " " +nameUser;
        System.out.println("value of email : "+emailR);

        List<KeyValuePair> paramsEmail = new ArrayList<>();
        paramsEmail.add(new KeyValuePair("fullname", URLEncoder.encode(fullnameIngenieur, "UTF-8")));
        paramsEmail.add(new KeyValuePair("datedebut",URLEncoder.encode(dateDebut, "UTF-8")));
        paramsEmail.add(new KeyValuePair("datefin",URLEncoder.encode(datefin, "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbouvert",URLEncoder.encode(nbCount, "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbcours",URLEncoder.encode(String.valueOf(nbTicketCours), "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbattente",URLEncoder.encode(String.valueOf(nbTicketAttente), "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbresolu",URLEncoder.encode(String.valueOf(nbTicketResolu), "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbclos",URLEncoder.encode(String.valueOf(nbTicketClos), "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbcourstemps",URLEncoder.encode(String.valueOf(nbTicketCours_A_Temps), "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbcoursretard",URLEncoder.encode(String.valueOf(nbTicketCours_Retard), "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbclostemps",URLEncoder.encode(String.valueOf(nbTicketClos_Temps), "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbclosretard",URLEncoder.encode(String.valueOf(nbTicketClos_Retard), "UTF-8")));
        paramsEmail.add(new KeyValuePair("pourcentageReussite",URLEncoder.encode(pourcentageReussite, "UTF-8")));
        paramsEmail.add(new KeyValuePair("ad2000",URLEncoder.encode(UserModel.getCurrentUserModel().getAd2000(), "UTF-8")));
        paramsEmail.add(new KeyValuePair("to",URLEncoder.encode(emailR, "UTF-8")));
        //paramsEmail.add(new KeyValuePair("to",URLEncoder.encode(emailReceiver, "UTF-8")));
        //paramsEmail.add(new KeyValuePair("contentmsg",URLEncoder.encode(msgcontent, "UTF-8")));

        final StringRequest getRequestEmail = new StringRequest(Request.Method.POST, URLGenerator.generateUrl(url, paramsEmail),
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        System.out.println("yep ! response share stat :: "+response);
                        pdShare.dismiss();
                        Toast.makeText(StatsTickets.this, "Le rapport a été envoyé à avec succès", Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response Email", error.toString());
                        pdShare.dismiss();
                        Toast.makeText(StatsTickets.this, "Envoi du rapport impossible", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Content-type","application/json");
                return params;
            }
        };

        getRequestEmail.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        queue.add(getRequestEmail);

    }

    private String editformatdate(String date) {
        String finaldate = "";
        //yyyy-MM-dd HH:mm:ss
        finaldate = date.substring(0, 10);
        return finaldate;
    }



    private void CalculTempsMoyenResolution() {
        //progressBarTempsMoyen.setVisibility(View.VISIBLE);
        Thread mThread = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < listDateDebut.size(); i++){
                    String dateDebut, dateCloture;
                    dateDebut = listDateDebut.get(i);
                    dateCloture = listDateCloture.get(i);
                    listTempsResolution.add(calculTempsResolution(dateCloture,dateDebut)); //faut faire sauter ça en API, cette fct...
                }
                long somme = 0;
                for (int j = 0; j < listTempsResolution.size(); j++){
                    somme = somme + listTempsResolution.get(j);
                }
                long tempsMoyen;
                if (listTempsResolution.size()==0){
                    Log.d("SIZE = 0", "Je vais appeler le handler");
                    Bundle bundle = new Bundle();
                    bundle.putString("zero", "00h 00m 00s");
                    Message msg = new Message();
                    msg.what = 1;
                    msg.setData(bundle);
                    handlerDate.sendMessage(msg);
                }
                else {
                    Log.d("SIZE NORMAL", "Je vais appeler le handler");
                    tempsMoyen = somme/listTempsResolution.size();
                    Bundle bundle = new Bundle();
                    bundle.putString("tempsrestant", TransformInTime(tempsMoyen));
                    Message msg = new Message();
                    msg.what = 2;
                    msg.setData(bundle);
                    handlerDate.sendMessage(msg);
                }

            }
        };
        mThread.start();

    }

    private void CalculTempsMoyenRetard() {
        //progressBarTempsMoyen.setVisibility(View.VISIBLE);
        Thread mThread = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < listDateEcheanceRetard.size(); i++){
                    String dateEcheance, dateCloture;
                    dateEcheance = listDateEcheanceRetard.get(i);
                    dateCloture = listDateClotureRetard.get(i);
                    listTempsRetard.add(calculTempsRetard(dateEcheance, dateCloture));
                }
                long somme = 0;
                for (int j = 0; j < listTempsRetard.size(); j++){
                    somme = somme + listTempsRetard.get(j);
                }
                long tempsMoyen;
                if (listTempsRetard.size()==0){
                    Log.d("SIZE = 0", "Appel handler Late");
                    Bundle bundle = new Bundle();
                    bundle.putString("zero", "00h 00m 00s");
                    Message msg = new Message();
                    msg.what = 3;
                    msg.setData(bundle);
                    handlerDate.sendMessage(msg);
                }
                else {
                    Log.d("SIZE NORMAL", "Appel handler Late");
                    tempsMoyen = somme/listTempsRetard.size();
                    Bundle bundle = new Bundle();
                    bundle.putString("tempsretard", TransformInTime(tempsMoyen));
                    Message msg = new Message();
                    msg.what = 4;
                    msg.setData(bundle);
                    handlerDate.sendMessage(msg);
                }

            }
        };
        mThread.start();
    }

    private long calculTempsRetard(String dateEchanceTicket, String dateClotureTicket) {
        if (dateEchanceTicket.equals("null")){
            return 0;
        }

        WorkTimeCalculator calculator = new WorkTimeCalculator(parse(dateEchanceTicket), parse(dateClotureTicket));
        long tmps = calculator.getMinutes()*60*1000;
        return tmps;
    }

    private void NumberTicketPerState(String statutTicket, boolean isLate) {
        switch (statutTicket){
            case "2": //En cours
                nbTicketCours++;
                if (isLate){ //En cours (en retard)
                    nbTicketCours_Retard++;
                }
                else { //En cours (à temps)
                    nbTicketCours_A_Temps++;
                }
                break;
            case "4": //Attente
                nbTicketAttente++;
                break;
            case "5": //Résolu
                nbTicketResolu++;
                break;
            case "6": //Clos
                nbTicketClos++;
                if (isLate){
                    nbTicketClos_Retard++;
                }
                else {
                    nbTicketClos_Temps++;
                }
                break;

        }
    }

    private long calculTempsResolution(String dateClotureTicket, String dateDebutTicket) {
        if (dateClotureTicket.equals("null")){
            return 0;
        }

        WorkTimeCalculator calculator = new WorkTimeCalculator(parse(dateDebutTicket), parse(dateClotureTicket));
        //System.out.println("Working time: "+calculator.getMinutes()+"\n\n\n");
        long tmps = calculator.getMinutes()*60*1000;
        return tmps;
    }

    private Date parse(String date) {
        try {
            return parser.parse(date);
        } catch (ParseException e) {
            Log.e("Parse error",date);
            return null;
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

    private String initializeDateText(String debutMoisDate, String actualDate) {
        //yyyy-MM-dd HH:mm:ss
        String debutWithoutTime = debutMoisDate.substring(0, 10);
        String finWithoutTime = actualDate.substring(0, 10);

        String newDebut, newFin;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date ddebut = null;
        Date dfin = null;

        try {

            ddebut = sdf.parse(debutWithoutTime);
            dfin = sdf.parse(finWithoutTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        sdf.applyPattern("dd/MM/yyyy");
        newDebut = sdf.format(ddebut);
        newFin = sdf.format(dfin);

        return newDebut+" - "+newFin;
    }

    private String getDateWithoutTime(String date) {
        return date.substring(0, 10);
    }

    private String getActualDate() {
        String Actualdate = "";

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Actualdate = formatter.format(todayDate);

        return Actualdate;
    }

    public String getDebutDate() {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String todayString = formatter.format(todayDate);
        todayString = todayString.substring(0, 8)+"01"+" 00:00:00";

        return todayString;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.presentationDateTicket:
                changeIntervalle();
                break;

            case R.id.changeDateStat:
                changeIntervalle();
                break;
        }
    }

    private void changeIntervalle() {
        DialogChoixDate alert = new DialogChoixDate();
        alert.showDialog(StatsTickets.this, debutMoisDate, finMoisDate);
    }

    class HandlerDate extends Handler{
        Bundle bundle;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    bundle = msg.getData();
                    String datedebut = bundle.getString("debut");
                    String datefin = bundle.getString("fin");

                    debutMoisDate = bundle.getString("debutRequest");
                    finMoisDate = bundle.getString("finRequest");
                    Log.d("RequestDate", debutMoisDate+" | "+finMoisDate);
                    initializeDateTextAfter(datedebut, datefin);

                    pd.show();
                    try {
                        bigStatRequest(debutMoisDate, finMoisDate);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;

            }

        }

    }

    private void initializeDateTextAfter(String datedebut, String datefin) {
        intervalDateTV.setText(datedebut+" - "+datefin);
    }

    private String TransformInTime(long tempsMS) {
        //int months = (int) ((timeLeftMS / (30*24*60*60*1000)));
        int day = (int) ((tempsMS / (24*60*60*1000))); //%30
        int hour = (int) ((tempsMS / (1000*60*60)) % 24);
        int minute = (int) ((tempsMS / (60*1000)) % 60);
        int seconde = (int)tempsMS % 60000 / 1000;

        String timeLeftText = "";

        if (day<10) timeLeftText += "0";
        timeLeftText += day;
        timeLeftText += "j ";
        if (hour<10) timeLeftText += "0";
        timeLeftText += hour;
        timeLeftText += "h ";
        if (minute<10) timeLeftText += "0";
        timeLeftText += minute;
        timeLeftText += "m ";
        if (seconde<10) timeLeftText += "0";
        timeLeftText += seconde;
        timeLeftText += "s";

        return timeLeftText;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            case R.id.itemShareStat:
                emailR = "";
                //openShareStatDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openShareStatDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(StatsTickets.this);
        builderSingle.setTitle("Partage de vos statistiques");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(StatsTickets.this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Votre responsable hiérarchique");
        arrayAdapter.add("Choisir une autre personne");


        builderSingle.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                switch (strName){
                    case "Votre responsable hiérarchique":
                        pdShare.show();
                        try {
                            emailR = "";
                            shareStat();
                        }
                        catch (UnsupportedEncodingException e) { e.printStackTrace(); }
                        break;
                    case "Choisir une autre personne":
                        //open dialog for another person
                        DialogChoixPersonStat alert = new DialogChoixPersonStat();
                        alert.showDialog(StatsTickets.this);
                        break;
                }
                //Toast.makeText(getActivity(), TicketModel.getTitreTicket(), Toast.LENGTH_SHORT).show();
            }
        });
        builderSingle.show();
    }

    private void shareStatToMe(String firstnameUser, String emailUser, String prenomReceiver, String emailReceiver) throws UnsupportedEncodingException {
        String url = Constants.URL_STAT_API;

        String dateDebut = editformatdate(debutMoisDate);
        String datefin = editformatdate(finMoisDate);

        String fullnameIngenieur = firstnameUser+ " " +nameUser;
        final String msgcontent = "<h2>Rapport Helpdesk</h2> <br><br>"+firstnameUser+",<br><br>Vous trouverez en pièce jointe le fichier PDF contenant votre rapport " +
                "Helpdesk du " +
                "<u><font color=#2e3e68>"+dateDebut+"</font></u> au <u><font color=#2e3e68>"+datefin+"</font></u>.<br><br>" +
                "Ce fichier a été également envoyé à "+prenomReceiver+" dont l'adresse est la suivante : "+emailReceiver+".<br><br><br>" +
                "L'équipe Helpdesk Mobile.<br><br><br>" +
                "<i>P.S: Ce mail a été généré automatiquement, prière de ne pas répondre.</i>";

        List<KeyValuePair> paramsEmail = new ArrayList<>();
        paramsEmail.add(new KeyValuePair("fullname", URLEncoder.encode(fullnameIngenieur, "UTF-8")));
        paramsEmail.add(new KeyValuePair("datedebut",URLEncoder.encode(dateDebut, "UTF-8")));
        paramsEmail.add(new KeyValuePair("datefin",URLEncoder.encode(datefin, "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbouvert",URLEncoder.encode(nbCount, "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbcours",URLEncoder.encode(String.valueOf(nbTicketCours), "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbattente",URLEncoder.encode(String.valueOf(nbTicketAttente), "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbresolu",URLEncoder.encode(String.valueOf(nbTicketResolu), "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbclos",URLEncoder.encode(String.valueOf(nbTicketClos), "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbcourstemps",URLEncoder.encode(String.valueOf(nbTicketCours_A_Temps), "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbcoursretard",URLEncoder.encode(String.valueOf(nbTicketCours_Retard), "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbclostemps",URLEncoder.encode(String.valueOf(nbTicketClos_Temps), "UTF-8")));
        paramsEmail.add(new KeyValuePair("nbclosretard",URLEncoder.encode(String.valueOf(nbTicketClos_Retard), "UTF-8")));
        paramsEmail.add(new KeyValuePair("pourcentageReussite",URLEncoder.encode(pourcentageReussite, "UTF-8")));
        paramsEmail.add(new KeyValuePair("to",URLEncoder.encode(emailUser, "UTF-8")));
        paramsEmail.add(new KeyValuePair("contentmsg",URLEncoder.encode(msgcontent, "UTF-8")));

        final JsonObjectRequest getRequestEmail = new JsonObjectRequest(Request.Method.POST, URLGenerator.generateUrl(url, paramsEmail), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            String state = response.getString("state");
                            String from = response.getString("from");
                            String to = response.getString("to");
                            String content = response.getString("content");
                            String filename = response.getString("filename");
                            Log.d("RESPONSE FROM", "from = "+from);
                            Log.d("RESPONSE TO", "to = "+to);
                            Log.d("RESPONSE STATE", "state = "+state);
                            Log.d("RESPONSE CONTENT", "content = "+content);
                            Log.d("RESPONSE FILENAME", "filename = "+filename);
                            //Toast.makeText(getActivity(), "Un email a été envoyé au demandeur", Toast.LENGTH_SHORT).show();
                            try {
                                Toast.makeText(StatsTickets.this, "Un PDF vous a été envoyé", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("Toast Email", "Impossible de notifier");
                            }

                        } catch (JSONException e) { e.printStackTrace(); }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response Email", error.toString());
                        Toast.makeText(StatsTickets.this, "Envoi du PDF impossible", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Content-type","application/json");
                return params;
            }
        };

        getRequestEmail.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        queue.add(getRequestEmail);
    }

    private class HandlerStats extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.SEND_EMAIL_PERSON: //send email
                    pdShare.show();
                    Bundle bundle;
                    bundle = msg.getData();
                    emailR = bundle.getString("emailR");

                    try {
                        shareStat();
                    } catch (UnsupportedEncodingException e) { e.printStackTrace(); }

                    break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stat_ticket, menu);
        return true;
    }

}
