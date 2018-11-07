package com.production.achour_ar.gshglobalactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

public class ListTicketsResolu extends Fragment {

    ArrayList<TicketModel> TicketModels;
    ListView listView;
    private static TicketResoluAdapter adapter;
    String session_token, nameUser, idUser, firstnameUser;
    RequestQueue queue;
    String titreTicket, slaTicket, urgenceTicket, idTicket,
            demandeurTicket, categorieTicket, dateDebutTicket, statutTicket,
            dateEchanceTicket, dateClotureTicket, dateResolutionTicket, descriptionTicket, lieuTicket;
    boolean ticketEnretard;

    public int nbTicketTab = 8;

    String nbCount;

    public String[][] ticketTab ;

    SwipeRefreshLayout swipeLayout;

    ProgressDialog pd;

    public static Handler handlerticketResolu;
    private int range;

    public ListTicketsResolu() {
        handlerticketResolu = new HandlerTicketResolu();
        TicketModels = new ArrayList<>();
        Log.d("INITIALIZATION","J'ai intialisé le handler résolu !");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_tickets, container, false);

        pd = new ProgressDialog(getActivity());
        pd.setTitle("Tickets résolus");
        pd.setMessage("Chargement des tickets...");

        queue = Volley.newRequestQueue(getActivity());

        swipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
        swipeLayout.setColorScheme(android.R.color.holo_green_light,
                android.R.color.holo_blue_dark);

        handlerticketResolu = new HandlerTicketResolu();

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!TicketModels.isEmpty()){
                    adapter.clear();
                    getTicketsHTTP();
                }
                else{
                    getTicketsHTTP();
                }

            }
        });

        session_token = getArguments().getString("session");
        nameUser = getArguments().getString("nom");
        firstnameUser = getArguments().getString("prenom");
        idUser = getArguments().getString("id");
        range = getArguments().getInt("range");


        listView=(ListView)view.findViewById(R.id.list);

        TicketModels = new ArrayList<>();

        getTicketsHTTP();

        return view;
    }

    private void getTicketsHTTP() {
        String url = Constants.GLPI_URL+"search/Ticket";

        int maxRange = range-1;
        List<KeyValuePair> params = new ArrayList<>();
        params.add(new KeyValuePair("criteria[0][field]","5"));
        params.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        params.add(new KeyValuePair("criteria[0][value]",idUser));

        params.add(new KeyValuePair("criteria[1][link]","AND"));
        params.add(new KeyValuePair("criteria[1][field]","12"));
        params.add(new KeyValuePair("criteria[1][searchtype]","equals"));
        params.add(new KeyValuePair("criteria[1][value]","5"));

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

        params.add(new KeyValuePair("sort","15"));
        params.add(new KeyValuePair("order","DESC"));
        params.add(new KeyValuePair("range","0-"+maxRange+""));

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(url, params), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            nbCount = response.getString("count");
                            ticketTab = new String[Integer.valueOf(nbCount)][nbTicketTab];

                            Bundle bundle = new Bundle();
                            bundle.putString("position","2");
                            bundle.putString("count",nbCount);
                            bundle.putString("title","Résolu");
                            Message msg = new Message();
                            msg.setData(bundle);
                            msg.what = 1;
                            TabLayoutActivity.handler.sendMessage(msg);

                            JSONArray Jdata = response.getJSONArray("data");
                            if (Jdata.length()==0){
                                System.out.println("pas de data");
                            }
                            for (int i=0; i < Jdata.length(); i++) {
                                try {
                                    JSONObject oneTicket = Jdata.getJSONObject(i);
                                    // Récupération des items pour le row_item
                                    titreTicket = oneTicket.getString("1");
                                    slaTicket = oneTicket.getString("30");
                                    dateDebutTicket = oneTicket.getString("15");
                                    urgenceTicket = oneTicket.getString("10");
                                    idTicket = oneTicket.getString("2");

                                    demandeurTicket = oneTicket.getString("4");
                                    categorieTicket = oneTicket.getString("7");
                                    statutTicket = oneTicket.getString("12");
                                    dateEchanceTicket = oneTicket.getString("18");
                                    descriptionTicket = oneTicket.getString("21");

                                    lieuTicket = oneTicket.getString("83");
                                    dateClotureTicket = oneTicket.getString("16");
                                    ticketEnretard = getBooleanFromSt(oneTicket.getString("82"));


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
                                ticketTab[i][6] = statutTicket;
                                ticketTab[i][7] = idTicket;

                            }

                            addModelsFromTab(ticketTab);


                            if (getActivity() != null){
                                adapter = new TicketResoluAdapter(TicketModels,getActivity());
                            }
                            else{
                                Log.e("STOP BEFORE ERROR", "Il allait y avoir une erreur man (RESOLU)");
                            }

                            listView.setAdapter(adapter);
                            handlerticketResolu.sendEmptyMessage(1);

                            if(swipeLayout.isRefreshing()){
                                swipeLayout.setRefreshing(false);
                            }


                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    TicketModel TicketModel= TicketModels.get(position);

                                   /* Snackbar.make(view, "Id = "+TicketModel.getIdTicket(), Snackbar.LENGTH_LONG)
                                            .setAction("No action", null).show();*/

                                    Intent i = new Intent(getActivity(), InfoTicket.class);
                                    i.putExtra("session",session_token);
                                    i.putExtra("nom",nameUser);
                                    i.putExtra("prenom",firstnameUser);
                                    i.putExtra("id",idUser);
                                    i.putExtra("idTicket",TicketModel.getIdTicket());

                                    startActivity(i);

                                }
                            });


                        } catch (JSONException e) {
                            Log.e("malkach",e.getMessage());
                            handlerticketResolu.sendEmptyMessage(2);
                            handlerticketResolu.sendEmptyMessage(4);
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Vérifiez votre connexion", Toast.LENGTH_LONG).show();
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

        // add it to the RequestQueue
        queue.add(getRequest);


    }

    private String calculTempsRetard(String dateEchanceTicket, String dateClotureTicket) {
        long echeance = getDateDebutMS(dateEchanceTicket);
        long cloture = getDateDebutMS(dateClotureTicket);

        long tmps = cloture - echeance;
        return String.valueOf(tmps);
    }

    private String calculTempsResolution(String dateClotureTicket, String dateDebutTicket) {
        long debut = getDateDebutMS(dateDebutTicket);
        long cloture = getDateDebutMS(dateClotureTicket);

        long tmps = cloture - debut;
        return String.valueOf(tmps);
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
        if (slaTicket.equals("null")){
            return "-1";
        }
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


        try {
            dateDebutMS = oldDate.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateDebutMS;
    }

    private void addModelsFromTab(String[][] ticketTab) {
        for (int i = 0; i < ticketTab.length; i++){
            TicketModel ticket = new TicketModel(ticketTab[i][0], ticketTab[i][1], ticketTab[i][2], ticketTab[i][4], ticketTab[i][7], ticketTab[i][6]);
            ticket.setUrgenceTicket(ticketTab[i][3]);
            ticket.setTicketEnRetard(Boolean.parseBoolean(ticketTab[i][5]));

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

    class HandlerTicketResolu extends Handler {
        boolean nodata = false;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if (TicketModels.isEmpty()){
                        System.out.println("listview vide");
                        pd.show();
                        if (nodata){
                            System.out.println("Enregistré, 0 data déjà");
                            pd.dismiss();
                        }
                    }
                    else{
                        System.out.println("listview non vide !!");
                    }
                    break;

                case 1:

                    if(pd.isShowing()){
                        System.out.println("Je dois arrêter le chargement de résolu");
                        pd.dismiss();
                    }
                    else{
                        System.out.println("Aucun chargement à arrêter résolu");
                    }
                    break;

                case 2:
                    nodata = true;
                    if(pd.isShowing()){
                        System.out.println("Nouvelle recherche, 0 data");
                        pd.dismiss();
                    }
                    break;

                case 3: //refresh LV
                    if (!TicketModels.isEmpty()){ //pleins
                        swipeLayout.setRefreshing(true);
                        adapter.clear();
                        getTicketsHTTP();
                    }
                    else{
                        swipeLayout.setRefreshing(true);
                        getTicketsHTTP();
                    }
                    break;

                case 4: //stop swipe
                    if(swipeLayout.isRefreshing()){
                        swipeLayout.setRefreshing(false);
                    }
                    break;
            }

        }
    }


}