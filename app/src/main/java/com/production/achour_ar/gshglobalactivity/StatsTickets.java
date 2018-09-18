package com.production.achour_ar.gshglobalactivity;

import android.content.Intent;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.production.achour_ar.gshglobalactivity.DataModel.TicketModel;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.production.achour_ar.gshglobalactivity.ListTickets.generateUrl;

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
    String actualDate, debutMoisDate;
    TextView intervalDateTV, changeDateTV, presentationTextTV, nbTicketCoursTV, nbTicketsCoursRetardTV, nbTicketAttenteTV, nbTicketResoluTV,
    nbTicketClosATempsTV, nbTicketClosRetardTV;

    RequestQueue queue;
    String idTicket, dateDebutTicket, dateResolutionTicket , statutTicket, dateEchanceTicket, dateClotureTicket;
    boolean ticketEnretard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_tickets);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Stats de mes tickets");

        queue = Volley.newRequestQueue(getApplicationContext());

        intervalDateTV = (TextView)findViewById(R.id.presentationDateTicket);
        changeDateTV = (TextView)findViewById(R.id.changeDateStat);
        presentationTextTV = (TextView)findViewById(R.id.presentationTextStat);
        nbTicketCoursTV = (TextView)findViewById(R.id.nbticketencours);
        nbTicketsCoursRetardTV = (TextView)findViewById(R.id.dontticketenretard);
        nbTicketAttenteTV = (TextView)findViewById(R.id.nbticketenattente);
        nbTicketResoluTV = (TextView)findViewById(R.id.nbticketresolu);
        nbTicketClosATempsTV = (TextView)findViewById(R.id.nbticketclos);
        nbTicketClosRetardTV = (TextView)findViewById(R.id.nbticketclosretard);

        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        idUser = i.getStringExtra("id");

        //Création du graphe "tickets"
        BarOuvertClos = (BarChart) findViewById(R.id.barOuvertClos);

        BarOuvertClos.addBar(new BarModel(4.f, 0xFF247c8f));
        BarOuvertClos.addBar(new BarModel(2.f,  0xFF292929));
        BarOuvertClos.startAnimation();

        //Initialisation de sa chart
        listChartTickets = (ListView)findViewById(R.id.ListChartTicket);
        ChartModelsTicket = new ArrayList<>();

        ChartModelsTicket.add(new ChartModel("Ouvert","Ticket ouvert"));
        ChartModelsTicket.add(new ChartModel("Clos","Ticket clos"));


        chartTicketAdapter = new ChartAdapter(ChartModelsTicket,getApplicationContext());
        listChartTickets.setAdapter(chartTicketAdapter);


        //Création du graphe "tickets par état"
        BarParEtat = (BarChart) findViewById(R.id.barParEtat);

        BarParEtat.addBar(new BarModel(2.f, 0xFF247c8f));
        BarParEtat.addBar(new BarModel(3.f,  0xFF5e1212));
        BarParEtat.addBar(new BarModel(5.f,  0xFFa95516));
        BarParEtat.addBar(new BarModel(1.f,  0xFF1b2a6b));
        BarParEtat.addBar(new BarModel(6.f,  0xFF292929));
        BarParEtat.addBar(new BarModel(4.f,  0xFF7b1d6e));
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


        //initialisation des dates (intervalle)
        actualDate = getActualDate(); //yyyy-MM-dd HH:mm:ss
        debutMoisDate = getDebutDate(); //yyyy-MM-01 00:00:00

        //Initialiser le texte du début
        intervalDateTV.setText(initializeDateText(debutMoisDate, actualDate));

        //Call the big HTTP REQUEST BRO
        bigStatRequest(debutMoisDate, actualDate);

    }

    private void bigStatRequest(String debutMoisDate, String actualDate) {
        String url = FirstEverActivity.GLPI_URL+"search/Ticket";

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
        params.add(new KeyValuePair("criteria[2][value]",actualDate));
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

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(url, params), null,
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
                                NumberTicketPerState(statutTicket, ticketEnretard);

                                // -----------------------------

                            }

                            //AfficheTab(ticketTab);

                            //Populate textviews and graphs
                            PopulateTextViews();

                        }
                        catch (JSONException e){
                            Log.e("malkach",e.getMessage());
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
                params.put("App-Token",FirstEverActivity.App_Token);
                params.put("Session-Token",session_token);
                return params;
            }

        };

        // add it to the RequestQueue
        queue.add(getRequest);
    }

    private void PopulateTextViews() {
        presentationTextTV.setText(nameUser+" "+firstnameUser+" - "+nbCount+" tickets");
        nbTicketCoursTV.setText(String.valueOf(nbTicketCours));
        nbTicketsCoursRetardTV.setText("("+nbTicketCours_Retard+" en retard)");
        nbTicketAttenteTV.setText(String.valueOf(nbTicketAttente));
        nbTicketResoluTV.setText(String.valueOf(nbTicketResolu));
        nbTicketClosATempsTV.setText(String.valueOf(nbTicketClos));
        nbTicketClosRetardTV.setText("("+nbTicketClos_Temps+" à temps - "+nbTicketClos_Retard+" en retard)");
    }

    private void NumberTicketPerState(String statutTicket, boolean isLate) {
        //rani hna, j'pense ylik nzid 2 param, ta3 date deb & ech, w f en cours w clos, bihoum nchouf la rahom late or not, w biha na3raf cha n'incrémenter
        switch (statutTicket){
            case "2": //En cours
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
                if (isLate){
                    nbTicketClos_Retard++;
                }
                else {
                    nbTicketClos++;
                }
                break;

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

    private String  initializeDateText(String debutMoisDate, String actualDate) {
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
}
