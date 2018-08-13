package com.production.achour_ar.gshglobalactivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListTickets extends AppCompatActivity {

    ArrayList<TicketModel> TicketModels;
    ListView listView;
    private static TicketAdapter adapter;
    String session_token, nameUser, idUser, firstnameUser, nbTicket;
    RequestQueue queue;
    String titreTicket, slaTicket, urgenceTicket,
    demandeurTicket, categorieTicket, etatTicket, dateDebutTicket,
            dateEchanceTicket, dateClotureTicket, descriptionTicket, lieuTicket;
    boolean ticketEnretard;

    public static int nbTicketTab = 6;
    public static int nbInfoTicket = 12;

    public static String[][] ticketTab ;
    public static String[][] infoTicket ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_tickets);

        queue = Volley.newRequestQueue(this);

        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        nbTicket = i.getStringExtra("nb");
        nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        idUser = i.getStringExtra("id");

        listView=(ListView)findViewById(R.id.list);


        TicketModels = new ArrayList<>();
        ticketTab = new String[Integer.valueOf(nbTicket)][nbTicketTab];
        infoTicket = new String[Integer.valueOf(nbTicket)][nbInfoTicket];


        String url = FirstEverActivity.GLPI_URL+"search/Ticket";



        List<KeyValuePair> params = new ArrayList<>();
        params.add(new KeyValuePair("criteria[0][field]","5"));
        params.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        params.add(new KeyValuePair("criteria[0][value]",idUser));
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

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(url, params), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray Jdata = response.getJSONArray("data");
                            for (int i=0; i < Jdata.length(); i++) {
                                    try {
                                        JSONObject oneTicket = Jdata.getJSONObject(i);
                                        // Récupération des items pour le row_item
                                        titreTicket = oneTicket.getString("1");
                                        slaTicket = oneTicket.getString("30");
                                        dateDebutTicket = oneTicket.getString("15");
                                        urgenceTicket = oneTicket.getString("10");

                                        //Récupération du reste
                                        demandeurTicket = oneTicket.getString("4");
                                        categorieTicket = oneTicket.getString("7");
                                        etatTicket = oneTicket.getString("12");
                                        dateEchanceTicket = oneTicket.getString("18");
                                        descriptionTicket = oneTicket.getString("21");

                                        lieuTicket = oneTicket.getString("83");
                                        dateClotureTicket = oneTicket.getString("16");
                                        ticketEnretard = getBooleanFromSt(oneTicket.getString("82"));


                                        System.out.println("Direct = " + oneTicket.getString("82") + "\n After f(x) = " + getBooleanFromSt(oneTicket.getString("82")));
                                    } catch (JSONException e) {
                                        Log.e("Nb of data: "+Jdata.length()+" || "+"Error JSONArray at "+i+" : ", e.getMessage());
                                    }
                                    // ------------------------


                                /* Remplissage du tableau des tickets pour le row item */
                                ticketTab[i][0] = titreTicket;
                                ticketTab[i][1] = slaTicket;
                                ticketTab[i][2] = dateDebutTicket;
                                ticketTab[i][3] = urgenceText(urgenceTicket);
                                ticketTab[i][4] = calculTempsRestant(dateDebutTicket, slaTicket);
                                ticketTab[i][5] = String.valueOf(ticketEnretard);



                                /* Remplissage du tableau des tickets pour tout */
                                infoTicket[i][0] = demandeurTicket;
                                infoTicket[i][1] = urgenceText(urgenceTicket);
                                infoTicket[i][2] = categorieTicket;
                                infoTicket[i][3] = etatText(etatTicket);
                                infoTicket[i][4] = dateDebutTicket;
                                infoTicket[i][5] = slaTicket;
                                infoTicket[i][6] = dateEchanceTicket;
                                infoTicket[i][7] = titreTicket;
                                infoTicket[i][8] = descriptionTicket;
                                infoTicket[i][9] = lieuTicket;
                                infoTicket[i][10] = calculTempsRestant(dateDebutTicket, slaTicket);
                                infoTicket[i][11] = dateClotureTicket;

                                System.out.println("Temps restant = "+calculTempsRestant(dateDebutTicket, slaTicket));
                                System.out.println("SLA = "+slaTicket);
                                System.out.println("Between : "+getBetweenBrackets(slaTicket));
                                System.out.println("Minimum : "+getMinTemps(slaTicket));
                                System.out.println("Maximim : "+getMaxTemps(slaTicket));

                                // -----------------------------

                            }

//                            AfficheTab(ticketTab);
//                            System.out.println("*** Après tri ***");
//                            triTableauTicket(ticketTab);
//                            AfficheTab(ticketTab);

                            //triTableauTicketParUrgence(ticketTab);
                            //triInfoTicketParUrgence(infoTicket);



                            System.out.println("*** Tab Ticket ***");
                            System.out.println("isLate: "+ticketTab[0][5]);
                            System.out.println("\n\n*** Info Ticket ***");
                            System.out.println("Titre: "+infoTicket[0][7]);

                            //triInfoTicket(infoTicket);
                            addModelsFromTab(ticketTab);

                            adapter = new TicketAdapter(TicketModels,getApplicationContext());

                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    TicketModel TicketModel= TicketModels.get(position);

                                    Snackbar.make(view, "Index = "+position, Snackbar.LENGTH_LONG)
                                            .setAction("No action", null).show();

                                    Intent i = new Intent(getApplicationContext(), InfoTicket.class);
                                    i.putExtra("session",session_token);
                                    i.putExtra("nom",nameUser);
                                    i.putExtra("prenom",firstnameUser);
                                    i.putExtra("id",idUser);
                                    i.putExtra("infoTicket", infoTicket[position]);

                                    startActivity(i);

                                }
                            });


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

    private String getBetweenBrackets(String slaTicket) {
        String between = "";

        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(slaTicket);
        while (matcher.find()){
            between = matcher.group();
        }

        return between;
    }

    private String getDigit(String text) {
        String digit = "";

        Pattern pattern = Pattern.compile("([\\d]+)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()){
            digit = matcher.group();
        }

        return digit;
    }

    private String getMinTemps(String slaTicket) {
        String between = getBetweenBrackets(slaTicket);
        String minTemps = "";

        Pattern pattern = Pattern.compile("^(.*?)\\/");
        Matcher matcher = pattern.matcher(between);
        while (matcher.find()){
            minTemps = matcher.group();
        }

        return getDigit(minTemps);
    }

    private String getMaxTemps(String slaTicket) {
        String between = getBetweenBrackets(slaTicket);
        String maxTemps = "";

        Pattern pattern = Pattern.compile("([\\d]+)(?=[^\\/]*$)");
        Matcher matcher = pattern.matcher(between);
        while (matcher.find()){
            maxTemps = matcher.group();
        }


        return maxTemps;
    }

    private String calculTempsRestant(String dateDebutTicket, String slaTicket) {
        String minTemps = getMinTemps(slaTicket);
        String maxTemps = getMaxTemps(slaTicket);

        long dateDebutMS = getDateDebutMS(dateDebutTicket);
        long currentTimeMS = CurrentTimeMS();

        long minTempsMS = hourToMSConvert(minTemps);

        long differenceCurrentDebut = currentTimeMS - dateDebutMS;

        long tempsRestant = minTempsMS - differenceCurrentDebut;

        return String.valueOf(tempsRestant);
    }

    private long hourToMSConvert(String minTemps) {
        long time = Long.valueOf(minTemps)*3600000;
        return time;
    }

    private long CurrentTimeMS() {
        long time = System.currentTimeMillis();
        return time;
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

    private void addModelsFromTab(String[][] ticketTab) {
        for (int i = 0; i < ticketTab.length; i++){
                TicketModel ticket = new TicketModel(ticketTab[i][0], ticketTab[i][1], ticketTab[i][2], ticketTab[i][4]);
                ticket.setUrgenceTicket(ticketTab[i][3]);
                ticket.setTicketEnRetard(Boolean.parseBoolean(ticketTab[i][5]));
                //ticket.setTempsRestantTicket(ticketTab[i][4]);

                TicketModels.add(ticket);
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

    private String urgenceText(String urgenceTicket) {
        String urgence = "";
        int urg = Integer.valueOf(urgenceTicket);
        switch (urg){
            case 1:
                urgence = "Très basse";
            break;
            case 2:
                urgence = "Basse";
                break;
            case 3:
                urgence = "Moyenne";
                break;
            case 4:
                urgence = "Haute";
                break;
            case 5:
                urgence = "Très haute";
                break;
        }

        return urgence;
    }

    private String etatText(String etatTicket) {
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

    public static void triTableauTicketParUrgence(String tableau[][]) {
        int longueur = tableau.length;
        boolean foundHaute = false;
        boolean foundMoyenne = false;

            // ---- Tri des urgences hautes/très hautes ---
            for (int i = 0; i < longueur; i++) {
                if ((!tableau[i][3].equals("Haute"))||(!tableau[i][3].equals("Très haute"))) {
                    for (int k = i+1; k< longueur; k++){
                        if (((tableau[k][3].equals("Haute"))||(tableau[k][3].equals("Très haute")))&&(!foundHaute)){
                            foundHaute = true;
                            permuter(k,i,tableau);
                        }
                    }
                    foundHaute = false;
                }
            }

            // ---- Tri des urgences moyennes ---
        for (int i = nbHauteUrgences(tableau); i < longueur; i++) {
            if (!tableau[i][3].equals("Moyenne")) {
                for (int k = i+1; k< longueur; k++){
                    if ((tableau[k][3].equals("Moyenne"))&&(!foundMoyenne)){
                        foundMoyenne = true;
                        permuter(k,i,tableau);
                    }
                }
                foundMoyenne = false;
            }
        }
    }

    public static void triInfoTicketParUrgence(String tableau[][]) {
        int longueur = tableau.length;
        boolean foundHaute = false;
        boolean foundMoyenne = false;

        // ---- Tri des urgences hautes/très hautes ---
        for (int i = 0; i < longueur; i++) {
            if ((!tableau[i][1].equals("Haute"))||(!tableau[i][1].equals("Très haute"))) {
                for (int k = i+1; k< longueur; k++){
                    if (((tableau[k][1].equals("Haute"))||(tableau[k][1].equals("Très haute")))&&(!foundHaute)){
                        foundHaute = true;
                        permuter(k,i,tableau);
                    }
                }
                foundHaute = false;
            }
        }

        // ---- Tri des urgences moyennes ---
        for (int i = nbHauteUrgences(tableau); i < longueur; i++) {
            if (!tableau[i][1].equals("Moyenne")) {
                for (int k = i+1; k< longueur; k++){
                    if ((tableau[k][1].equals("Moyenne"))&&(!foundMoyenne)){
                        foundMoyenne = true;
                        permuter(k,i,tableau);
                    }
                }
                foundMoyenne = false;
            }
        }
    }

    private static int nbHauteUrgences(String[][] tableau) {
        int nbTickets = 0;
        for(int i = 0; i < tableau.length; i++){
            if ((tableau[i][3].equals("Haute"))||(tableau[i][3].equals("Très haute"))){
                nbTickets++;
            }
        }

        return nbTickets;
    }

    private static void permuter(int k, int i, String[][] tableau) {
        String[] tampon = new String[4] ;

        tampon[0] = tableau[k][0]; tampon[1] = tableau[k][1]; tampon[2] = tableau[k][2]; tampon[3] = tableau[k][3];

        tableau[k][0] = tableau[i][0]; tableau[k][1] = tableau[i][1]; tableau[k][2] = tableau[i][2]; tableau[k][3] = tableau[i][3];

        tableau[i][0] = tampon[0]; tableau[i][1] = tampon[1]; tableau[i][2] = tampon[2]; tableau[i][3] = tampon[3];
    }

    private static void permuterInfo(int k, int i, String[][] tableau) {
        String[] tampon = new String[10] ;

        tampon[0] = tableau[k][0]; tampon[1] = tableau[k][1]; tampon[2] = tableau[k][2]; tampon[3] = tableau[k][3]; tampon[4] = tableau[k][4]; tampon[5] = tableau[k][5]; tampon[6] = tableau[k][6]; tampon[7] = tableau[k][7]; tampon[8] = tableau[k][8]; tampon[9] = tableau[k][9];

        tableau[k][0] = tableau[i][0]; tableau[k][1] = tableau[i][1]; tableau[k][2] = tableau[i][2]; tableau[k][3] = tableau[i][3]; //ajouter until 9

        tableau[i][0] = tampon[0]; tableau[i][1] = tampon[1]; tableau[i][2] = tampon[2]; tableau[i][3] = tampon[3]; //ajouter till 9
    }


}
