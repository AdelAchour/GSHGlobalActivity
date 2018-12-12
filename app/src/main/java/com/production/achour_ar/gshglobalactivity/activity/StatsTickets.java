package com.production.achour_ar.gshglobalactivity.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.production.achour_ar.gshglobalactivity.R;
import com.production.achour_ar.gshglobalactivity.manager.URLGenerator;
import com.production.achour_ar.gshglobalactivity.manager.WorkTimeCalculator;
import com.production.achour_ar.gshglobalactivity.adapter.ChartAdapter;
import com.production.achour_ar.gshglobalactivity.data_model.ChartModel;
import com.production.achour_ar.gshglobalactivity.data_model.Constants;
import com.production.achour_ar.gshglobalactivity.data_model.KeyValuePair;
import com.production.achour_ar.gshglobalactivity.dialog.DialogChoixDate;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.production.achour_ar.gshglobalactivity.fragment.ListTickets.generateUrl;

public class StatsTickets extends AppCompatActivity {

    ArrayList<ChartModel> ChartModelsTicket, ChartModelsParTicket;
    ListView listChartTickets, listChartParTickets;
    private static ChartAdapter chartTicketAdapter, chartParTicketAdapter;
    String nbCount;
    int nbTicketCours = 0, nbTicketCours_Retard = 0, nbTicketAttente = 0,
            nbTicketResolu = 0, nbTicketClos = 0,
            nbTicketClos_Temps = 0, nbTicketClos_Retard = 0;

    String session_token, nameUser, idUser, firstnameUser;
    BarChart BarOuvertClos, BarParEtat;
    String actualDate, debutMoisDate, finMoisDate;
    TextView intervalDateTV, changeDateTV, presentationNameTV, presentationTextTV, nbTicketCoursTV, nbTicketsCoursRetardTV,
            nbTicketAttenteTV, nbTicketResoluTV, nbTicketClosATempsTV, nbTicketClosRetardTV,
    tempsResolutionMoyenTV, tempsRetardMoyenTV;

    RequestQueue queue;
    String idTicket, dateDebutTicket, dateResolutionTicket , statutTicket, dateEchanceTicket, dateClotureTicket;
    boolean ticketEnretard;
    public static Handler handlerDate;
    ProgressDialog pd;
    private static DateFormat parser ;
    ArrayList<String> listDateDebut;
    ArrayList<String> listDateCloture;
    ArrayList<Long> listTempsResolution;

    ArrayList<String> listDateEcheanceRetard;
    ArrayList<String> listDateClotureRetard;
    ArrayList<Long> listTempsRetard;

    ProgressBar progressBarTempsMoyen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_tickets);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Stats de mes tickets");
        actionBar.setHomeButtonEnabled(true); //show a caret only if android:parentActivityName is specified.

        queue = Volley.newRequestQueue(getApplicationContext());

        handlerDate = new HandlerDate();
        parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        listDateDebut = new ArrayList<String>();
        listDateCloture = new ArrayList<String>();
        listTempsResolution = new ArrayList<Long>();

        listDateEcheanceRetard = new ArrayList<String>();
        listDateClotureRetard = new ArrayList<String>();
        listTempsRetard = new ArrayList<Long>();

        progressBarTempsMoyen = (ProgressBar) findViewById(R.id.progressBarTempsMoyen);

        intervalDateTV = (TextView)findViewById(R.id.presentationDateTicket);
        changeDateTV = (TextView)findViewById(R.id.changeDateStat);
        presentationNameTV = (TextView)findViewById(R.id.presentationName);
        presentationTextTV = (TextView)findViewById(R.id.presentationTextStat);
        nbTicketCoursTV = (TextView)findViewById(R.id.nbticketencours);
        nbTicketsCoursRetardTV = (TextView)findViewById(R.id.dontticketenretard);
        nbTicketAttenteTV = (TextView)findViewById(R.id.nbticketenattente);
        nbTicketResoluTV = (TextView)findViewById(R.id.nbticketresolu);
        nbTicketClosATempsTV = (TextView)findViewById(R.id.nbticketclos);
        nbTicketClosRetardTV = (TextView)findViewById(R.id.nbticketclosretard);
        tempsResolutionMoyenTV = (TextView)findViewById(R.id.tempsresolutionMoyen);
        tempsRetardMoyenTV = (TextView)findViewById(R.id.tempsretardMoyen);

        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        idUser = i.getStringExtra("id");

        //Initialisation des graphes
        BarOuvertClos = (BarChart) findViewById(R.id.barOuvertClos);
        BarParEtat = (BarChart) findViewById(R.id.barParEtat);


        //initialisation des dates (intervalle)
        //actualDate = getActualDate(); //yyyy-MM-dd HH:mm:ss

        debutMoisDate = getDebutDate(); //yyyy-MM-01 00:00:00
        finMoisDate = getActualDate(); //yyyy-MM-dd HH:mm:ss

        //ProgressDialog
        pd = new ProgressDialog(this);
        pd.setMessage("Requete en cours...");
        pd.show();

        //Initialiser le texte du début
        presentationNameTV.setText(nameUser+" "+firstnameUser);
        intervalDateTV.setText(initializeDateText(debutMoisDate, finMoisDate));

        //Call the big HTTP REQUEST BRO
        bigStatRequest(debutMoisDate, finMoisDate);

        intervalDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogChoixDate alert = new DialogChoixDate();
                alert.showDialog(StatsTickets.this, debutMoisDate, finMoisDate);
            }
        });

    }

    private void bigStatRequest(final String debutMoisDate, final String finMoisDate) {
        nbCount = "0";
        nbTicketCours = 0;
        nbTicketCours_Retard = 0;
        nbTicketAttente = 0;
        nbTicketResolu = 0;
        nbTicketClos = 0;
        nbTicketClos_Temps = 0;
        nbTicketClos_Retard = 0;
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
        params.add(new KeyValuePair("criteria[1][value]",debutMoisDate));
        //AND DATE INFERIEUR A
        params.add(new KeyValuePair("criteria[2][link]","AND"));
        params.add(new KeyValuePair("criteria[2][field]","15"));
        params.add(new KeyValuePair("criteria[2][searchtype]","lessthan"));
        params.add(new KeyValuePair("criteria[2][value]",finMoisDate));
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
        params.add(new KeyValuePair("range","0-200"));

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

                                // Remplissage du tableau pour le temps moyen de résolution/retard

                                //ticketTab[i][0] = titreTicket; //temps de résolution
                                //ticketTab[i][1] = slaTicket; //temps de retard

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
                            tempsResolutionMoyenTV.setText("00h 00m 00s");
                            tempsRetardMoyenTV.setText("00h 00m 00s");
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

        //Initialisation de sa chart
        listChartTickets = (ListView)findViewById(R.id.ListChartTicket);
        ChartModelsTicket = new ArrayList<>();

        ChartModelsTicket.add(new ChartModel("Ouvert","Ticket ouvert"));
        ChartModelsTicket.add(new ChartModel("Clos","Ticket clos"));

        chartTicketAdapter = new ChartAdapter(ChartModelsTicket,getApplicationContext());
        listChartTickets.setAdapter(chartTicketAdapter);

        //Graphes par état
        BarParEtat.addBar(new BarModel(0.f, 0xFF247c8f));
        BarParEtat.addBar(new BarModel(0.f,  0xFF5e1212));
        BarParEtat.addBar(new BarModel(0.f,  0xFFa95516));
        BarParEtat.addBar(new BarModel(0.f,  0xFF1b2a6b));
        BarParEtat.addBar(new BarModel(0.f,  0xFF292929));
        BarParEtat.addBar(new BarModel(0.f,  0xFF7b1d6e));
        BarParEtat.startAnimation();

        //Initialisation de sa chart
        listChartParTickets = (ListView)findViewById(R.id.ListChartParTicket);
        ChartModelsParTicket = new ArrayList<>();

        ChartModelsParTicket.add(new ChartModel("Ouvert","Ticket en cours"));
        ChartModelsParTicket.add(new ChartModel("Retard","Ticket en cours (en retard)"));
        ChartModelsParTicket.add(new ChartModel("Attente","Ticket en attente"));
        ChartModelsParTicket.add(new ChartModel("Resolu","Ticket résolus"));
        ChartModelsParTicket.add(new ChartModel("Clos","Ticket clos à temps"));
        ChartModelsParTicket.add(new ChartModel("ClosRetard","Ticket clos en retard"));

        chartParTicketAdapter = new ChartAdapter(ChartModelsParTicket,getApplicationContext());
        listChartParTickets.setAdapter(chartParTicketAdapter);
    }

    private void PopulateZeroTextViews() {
        presentationTextTV.setText("0 ticket");
        nbTicketCoursTV.setText("0");
        nbTicketsCoursRetardTV.setText("("+0+" en retard)");
        nbTicketAttenteTV.setText("0");
        nbTicketResoluTV.setText("0");
        nbTicketClosATempsTV.setText("0");
        nbTicketClosRetardTV.setText("("+0+" à temps - "+0+" en retard)");
    }

    private void PopulateGraphs() {
        //Graph par ticket
        BarOuvertClos.addBar(new BarModel(Float.valueOf(nbCount), 0xFF247c8f));
        BarOuvertClos.addBar(new BarModel(Float.valueOf(nbTicketClos),  0xFF292929));
        BarOuvertClos.startAnimation();

        //Initialisation de sa chart
        listChartTickets = (ListView)findViewById(R.id.ListChartTicket);
        ChartModelsTicket = new ArrayList<>();


        System.out.println("ticket en cours "+nbTicketCours);
        System.out.println("ticket clos "+nbTicketClos);
        float pourcentageCalcul ;
        String pourcentageReussite = "" ;
        int nbOuvert = Integer.valueOf(nbCount);
        if (nbOuvert>0){
            pourcentageCalcul = ((Float.valueOf(nbTicketClos)/Float.valueOf(nbOuvert))*100);
            pourcentageReussite = String.format("%.02f", pourcentageCalcul);
        }
        else{
            Log.e("div 0", " = 0");
            pourcentageReussite = String.format("%.02f", 0f);
        }

        String success = nbTicketClos + " sur " +nbOuvert + " - " + pourcentageReussite + "% de réussite" ;
        ChartModelsTicket.add(new ChartModel("Ouvert","Ticket ouvert"));
        ChartModelsTicket.add(new ChartModel("Clos","Ticket clos"));
        ChartModelsTicket.add(new ChartModel("Stat",success));

        chartTicketAdapter = new ChartAdapter(ChartModelsTicket,getApplicationContext());
        listChartTickets.setAdapter(chartTicketAdapter);

        //Graphes par état
        BarParEtat.addBar(new BarModel(Float.valueOf(nbTicketCours), 0xFF247c8f));
        BarParEtat.addBar(new BarModel(Float.valueOf(nbTicketCours_Retard),  0xFF5e1212));
        BarParEtat.addBar(new BarModel(Float.valueOf(nbTicketAttente),  0xFFa95516));
        BarParEtat.addBar(new BarModel(Float.valueOf(nbTicketResolu),  0xFF1b2a6b));
        BarParEtat.addBar(new BarModel(Float.valueOf(nbTicketClos_Temps),  0xFF292929));
        BarParEtat.addBar(new BarModel(Float.valueOf(nbTicketClos_Retard),  0xFF7b1d6e));
        BarParEtat.startAnimation();

        //Initialisation de sa chart
        listChartParTickets = (ListView)findViewById(R.id.ListChartParTicket);
        ChartModelsParTicket = new ArrayList<>();

        ChartModelsParTicket.add(new ChartModel("Ouvert","Ticket en cours"));
        ChartModelsParTicket.add(new ChartModel("Retard","Ticket en cours (en retard)"));
        ChartModelsParTicket.add(new ChartModel("Attente","Ticket en attente"));
        ChartModelsParTicket.add(new ChartModel("Resolu","Ticket résolus"));
        ChartModelsParTicket.add(new ChartModel("Clos","Ticket clos à temps"));
        ChartModelsParTicket.add(new ChartModel("ClosRetard","Ticket clos en retard"));

        chartParTicketAdapter = new ChartAdapter(ChartModelsParTicket,getApplicationContext());
        listChartParTickets.setAdapter(chartParTicketAdapter);

        //get temps résolution moyen
    }

    private void PopulateTextViews() {
        presentationTextTV.setText(""+nbCount+" tickets");
        nbTicketCoursTV.setText(String.valueOf(nbTicketCours));
        nbTicketsCoursRetardTV.setText("("+nbTicketCours_Retard+" en retard)");
        nbTicketAttenteTV.setText(String.valueOf(nbTicketAttente));
        nbTicketResoluTV.setText(String.valueOf(nbTicketResolu));
        nbTicketClosATempsTV.setText(String.valueOf(nbTicketClos));
        nbTicketClosRetardTV.setText("("+nbTicketClos_Temps+" à temps - "+nbTicketClos_Retard+" en retard)");

        progressBarTempsMoyen.setVisibility(View.VISIBLE);
        Thread mThread = new Thread() {
            @Override
            public void run() {
                CalculTempsMoyenResolution();
                CalculTempsMoyenRetard();
            }
        };
        mThread.start();

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
                if (isLate){
                    nbTicketCours_Retard++;
                }
                else {
                    nbTicketCours++;
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
                    bigStatRequest(debutMoisDate, finMoisDate);
                    break;

                case 1:
                    bundle = msg.getData();
                    tempsResolutionMoyenTV.setText(bundle.getString("zero"));
                    progressBarTempsMoyen.setVisibility(View.GONE);
                    break;

                case 2:
                    bundle = msg.getData();
                    tempsResolutionMoyenTV.setText(bundle.getString("tempsrestant"));
                    progressBarTempsMoyen.setVisibility(View.GONE);
                    break;

                case 3:
                    bundle = msg.getData();
                    tempsRetardMoyenTV.setText(bundle.getString("zero"));
                    progressBarTempsMoyen.setVisibility(View.GONE);
                    break;

                case 4:
                    bundle = msg.getData();
                    tempsRetardMoyenTV.setText(bundle.getString("tempsretard"));
                    progressBarTempsMoyen.setVisibility(View.GONE);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
