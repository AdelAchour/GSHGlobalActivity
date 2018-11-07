package com.production.achour_ar.gshglobalactivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.production.achour_ar.gshglobalactivity.DataModel.TicketModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.production.achour_ar.gshglobalactivity.Constants.GLPI_URL;

public class ListTickets extends Fragment {

    private ArrayList<TicketModel> TicketModels;
    private ListView listView;
    private static TicketAdapter adapter;
    private String session_token, nameUser, idUser, firstnameUser;
    private RequestQueue queue;
    private String motifAttente;
    private String titreTicket, slaTicket, urgenceTicket, idTicket, demandeurTicket,
            categorieTicket, etatTicket, dateDebutTicket, statutTicket,
            dateEchanceTicket, dateClotureTicket, dateResolutionTicket, descriptionTicket, lieuTicket;
    private String nbCount;
    private int range;
    public static Handler handlerticket;
    boolean ticketEnretard;
    public int nbTicketTab = 9;
    public String[][] ticketTab;
    private SwipeRefreshLayout swipeLayout;
    private ProgressDialog pd;
    private ProgressDialog pdChangement;
    private String now;
    private String emailDemandeur;
    private String prenomDemandeur;
    private String nomDemandeur;
    private String nowAttente;

    public ListTickets() {
        handlerticket = new HandlerTicket();
        Log.d("INITIALIZATION","J'ai intialisé le handler en cours !");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_tickets, container, false);

        initView(view);
        SetupPDs();
        setListener();
        getArgmts();
        registerForContextMenu(listView);
        getTicketsHTTP();

        return view;
    }

    private void SetupPDs() {
        pd.setTitle("Tickets en cours");
        pd.setMessage("Chargement des tickets...");
        pdChangement.setMessage("Changement de l'état...");
    }

    private void getArgmts() {
        session_token = getArguments().getString("session");
        nameUser = getArguments().getString("nom");
        firstnameUser = getArguments().getString("prenom");
        idUser = getArguments().getString("id");
        range = getArguments().getInt("range");
    }

    private void setListener() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!TicketModels.isEmpty()){ //pleins
                    adapter.clear();
                    getTicketsHTTP();
                }
                else{ //vide
                    getTicketsHTTP();
                }
            }
        });
    }

    private void initView(View view) {
        swipeLayout = view.findViewById(R.id.swipe_container);
        swipeLayout.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_green_light);

        handlerticket = new HandlerTicket();
        pdChangement = new ProgressDialog(getActivity());
        pd = new ProgressDialog(getActivity());
        TicketModels = new ArrayList<>();
        queue = Volley.newRequestQueue(getActivity());
        listView = view.findViewById(R.id.list);
    }

    private void getTicketsHTTP() {
        String url = Constants.GLPI_URL+"search/Ticket";

        int maxRange = range-1;
        List<KeyValuePair> params = new ArrayList<>();
        //TECHNICIEN = IDUSER
        params.add(new KeyValuePair("criteria[0][field]","5"));
        params.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        params.add(new KeyValuePair("criteria[0][value]",idUser));
        //AND STATUT EST EN COURS (ATTIRIBUE)
        params.add(new KeyValuePair("criteria[1][link]","AND"));
        params.add(new KeyValuePair("criteria[1][field]","12"));
        params.add(new KeyValuePair("criteria[1][searchtype]","equals"));
        params.add(new KeyValuePair("criteria[1][value]","2"));
        //OU TECHNICIEN = IDUSER
        params.add(new KeyValuePair("criteria[2][link]","OR"));
        params.add(new KeyValuePair("criteria[2][field]","5"));
        params.add(new KeyValuePair("criteria[2][searchtype]","equals"));
        params.add(new KeyValuePair("criteria[2][value]",idUser));
        //AND STATUT EST EN COURS (PLANIFIE)
        params.add(new KeyValuePair("criteria[3][link]","AND"));
        params.add(new KeyValuePair("criteria[3][field]","12"));
        params.add(new KeyValuePair("criteria[3][searchtype]","equals"));
        params.add(new KeyValuePair("criteria[3][value]","3"));
//        //OU TECHNICIEN = IDUSER
//        params.add(new KeyValuePair("criteria[4][link]","OR"));
//        params.add(new KeyValuePair("criteria[4][field]","5"));
//        params.add(new KeyValuePair("criteria[4][searchtype]","equals"));
//        params.add(new KeyValuePair("criteria[4][value]",idUser));
//        //AND STATUT EST EN ATTENTE
//        params.add(new KeyValuePair("criteria[5][link]","AND"));
//        params.add(new KeyValuePair("criteria[5][field]","12"));
//        params.add(new KeyValuePair("criteria[5][searchtype]","equals"));
//        params.add(new KeyValuePair("criteria[5][value]","4"));
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
        //ORDRE ET RANGE
        params.add(new KeyValuePair("sort","15"));
        params.add(new KeyValuePair("order","DESC"));
        params.add(new KeyValuePair("range","0-"+maxRange+""));

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(url, params), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //nbCount = response.getString("totalcount");
                            nbCount = response.getString("count");
                            System.out.println("nb t = "+nbCount);
                            ticketTab = new String[Integer.valueOf(nbCount)][nbTicketTab];

                            Bundle bundle = new Bundle();
                            bundle.putString("position","0");
                            bundle.putString("count",nbCount);
                            bundle.putString("title","En cours");
                            Message msg = new Message();
                            msg.setData(bundle);
                            msg.what = 1;
                            TabLayoutActivity.handler.sendMessage(msg);

                            JSONArray Jdata = response.getJSONArray("data");
                            for (int i=0; i < Jdata.length(); i++) {
                                try {
                                    JSONObject oneTicket = Jdata.getJSONObject(i);
                                    // Récupération des items pour le row_item
                                    titreTicket = oneTicket.getString("1");
                                    slaTicket = oneTicket.getString("30");
                                    dateDebutTicket = oneTicket.getString("15");
                                    urgenceTicket = oneTicket.getString("10");
                                    statutTicket = oneTicket.getString("12");
                                    idTicket = oneTicket.getString("2");

                                    //Récupération du reste
                                    demandeurTicket = oneTicket.getString("4");
                                    categorieTicket = oneTicket.getString("7");
                                    etatTicket = oneTicket.getString("12");
                                    dateEchanceTicket = oneTicket.getString("18");
                                    descriptionTicket = oneTicket.getString("21");

                                    lieuTicket = oneTicket.getString("83");
                                    dateClotureTicket = oneTicket.getString("16");
                                    dateResolutionTicket = oneTicket.getString("17");
                                    ticketEnretard = getBooleanFromSt(oneTicket.getString("82"));

                                } catch (JSONException e) {
                                    Log.e("Nb of data: "+Jdata.length()+" || "+"Error JSONArray at "+i+" : ", e.getMessage());
                                }

                                /* ---------  Creating a TicketModel object  --------- */

                                TicketModel ticket = new TicketModel(titreTicket, slaTicket, dateDebutTicket,
                                        calculTempsRestant(dateEchanceTicket), idTicket, statutTicket);

                                ticket.setUrgenceTicket(urgenceText(urgenceTicket));
                                ticket.setTicketEnRetard(Boolean.parseBoolean(String.valueOf(ticketEnretard)));
                                ticket.setDescription(descriptionTicket);
                                ticket.setDemandeurID(demandeurTicket);
                                //ticket.setObserverID(observerTicket);

                                TicketModels.add(ticket);

                                /* ---------  Creating a TicketModel object  --------- */

                            }

                            //triTableauTicketParUrgence(ticketTab);
                            //AfficheTab(ticketTab);


                            //System.out.println("Je charge la listview");
                            if (getActivity() != null){
                                adapter = new TicketAdapter(TicketModels,getActivity());
                            }
                            else{
                                Log.e("STOP BEFORE ERROR", "Il allait y avoir une erreur man (EN COURS)");
                            }


                            listView.setAdapter(adapter);
                            //System.out.println("Listview chargée");
                            //TabLayoutActivity.handler.sendEmptyMessage(1);
                            handlerticket.sendEmptyMessage(1);
                            TabLayoutActivity.handler.sendEmptyMessage(0);

                            if(swipeLayout.isRefreshing()){
                                swipeLayout.setRefreshing(false);
                            }

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                    TicketModel TicketModel = TicketModels.get(position);

                                    /*Snackbar.make(view, "id = "+TicketModel.getIdTicket(), Snackbar.LENGTH_LONG)
                                            .setAction("No action", null).show();*/

                                    Intent i = new Intent(getActivity(), InfoTicket.class);
                                    i.putExtra("session",session_token);
                                    i.putExtra("nom",nameUser);
                                    i.putExtra("prenom",firstnameUser);
                                    i.putExtra("id",idUser);
                                    i.putExtra("idTicket", TicketModel.getIdTicket());

                                    startActivity(i);

                                }
                            });

                            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    final TicketModel TicketModel= TicketModels.get(position);

                                    //Snackbar.make(view, "id = "+TicketModel.getIdTicket(), Snackbar.LENGTH_LONG)
                                      //      .setAction("No action", null).show();

                                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                                    builderSingle.setTitle("Faites votre choix");

                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);
                                    arrayAdapter.add("Mettre le ticket en attente");
                                    arrayAdapter.add("Mettre le ticket en résolu");


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
                                                case "Mettre le ticket en attente":
                                                    DialogMotifAttente alert = new DialogMotifAttente();
                                                    alert.showDialog(getActivity(), TicketModel.getIdTicket(), TicketModel.getDescription(),
                                                            TicketModel.getDemandeurID(), TicketModel.getTitreTicket());
                                                    break;
                                                case "Mettre le ticket en résolu":
                                                    pdChangement.show();
                                                    TicketEnResoluHTTP(TicketModel.getIdTicket(), TicketModel.getDescription(),
                                                                       TicketModel.getDemandeurID(), TicketModel.getTitreTicket());
                                                    break;
                                            }
                                            //Toast.makeText(getActivity(), TicketModel.getTitreTicket(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    builderSingle.show();

                                    return true;
                                }
                            });



                        } catch (JSONException e) {
                            Log.e("malkach",e.getMessage());
                            handlerticket.sendEmptyMessage(2);
                            handlerticket.sendEmptyMessage(4);
                            TabLayoutActivity.handler.sendEmptyMessage(0);
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response!", error.toString());
                        Toast.makeText(getActivity(), "Vérifiez votre connexion", Toast.LENGTH_LONG).show();
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

    private void TicketEnAttenteHTTP(String idTicket, final String descriptionTicket, final String motifAttente, final String demandeurTicket, final String titreTicket, final String attente) {
        String url = Constants.GLPI_URL+"Ticket/"+idTicket;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.PUT, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Do something with response
                        handlerticket.sendEmptyMessage(5);
                        getDemandeurInfoThenSendEmailAttente(now, demandeurTicket, titreTicket, attente);
                        //getObserverInfoThenSendEmailAttente(now, observerTicket, titreTicket, attente);
                        Toast.makeText(getActivity(), "Ticket mis en attente !", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Log.e("Error.Response!", error.toString());
                        error.printStackTrace();
                        handlerticket.sendEmptyMessage(5);
                        Toast.makeText(getActivity(), "Tache impossible", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("App-Token",Constants.App_Token);
                params.put("Session-Token",session_token);
                params.put("Content-Type","application/json");
                return params;
            }

            @Override
            public byte[] getBody() {
                nowAttente = getNowTime();

                //String a = "Objet: CRM CONNEXION\\r\\nDate: 16.07.2018 09:46\\r\\nDe: \\\"ilyes TAIBI\\\" \\r\\nÀ: \\\"Sedik DRIFF\\\" \\r\\n\\r\\nBonjour,\\r\\n\\r\\nConcernant CRM, le problème persiste toujours que ce soit en réseau\\r\\nlocal ou bien par VPN, impossible de se connecter au serveur.\\r\\n\\r\\nCi-joint les captures écrans.\\r\\n\\r\\nCordialement\\r\\n\\r\\nILYES TAIBI\\r\\n\\r\\nIngénieur IT\\r\\n\\r\\nMob : +213 (0) 560-966-134\\r\\n\\r\\nE-mail : ilyes.taibi@grupopuma-dz.com";

                String b = descriptionTicket.replaceAll("\r\n","\\\\r\\\\n");
                b = b.replaceAll("\n", "\\\\n");
                b = b.replaceAll("\r", "\\\\r");
                b = b.replaceAll("\"","\\\\\"");


                //System.out.println("a : " + a + " | " + a.length());
                //System.out.println("b : " + b + " | " + b.length());

                String motif = "[Ticket mis en attente le "+now+".\\r\\nMotif : "+motifAttente+"]";
                String nouv = b + "\\r\\n\\r\\n\\r\\n " + motif ;


                String Json_Payload = "{\"input\":{\"status\": \"4\",\"content\": \""+nouv+"\"}}"; // put your json
                //String Json_Payload = "{\"input\":{\"status\": \"4\",\"content\": \""+b+"\"}}"; // put your json
                return Json_Payload.getBytes();
            }
        };

        // Add JsonArrayRequest to the RequestQueue
        queue.add(jsonArrayRequest);
    }

    private void getDemandeurInfoThenSendEmailAttente(final String now, final String demandeurTicket, final String titreTicket, final String attente) {
        //Récupération des informations du demandeur
        String urlDemandeur = Constants.GLPI_URL+"search/User";

        List<KeyValuePair> paramsDemandeur = new ArrayList<>();
        paramsDemandeur.add(new KeyValuePair("criteria[0][field]","2"));
        paramsDemandeur.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        paramsDemandeur.add(new KeyValuePair("criteria[0][value]",demandeurTicket));
        paramsDemandeur.add(new KeyValuePair("forcedisplay[0]","9"));
        paramsDemandeur.add(new KeyValuePair("forcedisplay[1]","34"));
        paramsDemandeur.add(new KeyValuePair("forcedisplay[2]","5"));

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
                                emailDemandeur = userInfo.getString("5");
                                prenomDemandeur = userInfo.getString("9");
                                nomDemandeur = userInfo.getString("34");

                            } catch (JSONException e) {
                                Log.e("Error JSONArray : ", e.getMessage());
                            }
                        } catch (JSONException e) { e.printStackTrace(); }

                        SendEmailAttente(now, prenomDemandeur,emailDemandeur, titreTicket, attente);

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

    private void SendEmailAttente(final String now, final String prenomDemandeur, final String emailDemandeur, final String titreTicket, final String attente) {
        String url = "http://10.10.10.18:8180/emailrestapi/mailapi/SendEmail/send";

        //final String content = prenomDemandeur+",\\n\\nVotre ticket "+titreTicket+" a été résolu à "+now+".\\n\\nMerci,\\n\\nL'équipe Helpdesk mobile.";
        final String content = "<h2>Notification Helpdesk</h2> <br>"+prenomDemandeur+",<br><br>" +
                "Votre ticket \""+titreTicket+"\" a été mis en attente le "+nowAttente+".<br><br>" +
                "Technicien chargé du ticket : "+firstnameUser+" "+nameUser+".<br><br>" +
                "Motif de mise en attente : "+attente+"." +
                "<br><br><br>L'équipe Helpdesk Mobile.<br><br><br>" +
                "<i>P.S: Ce mail a été généré automatiquement, prière de ne pas répondre.</i>";

        List<KeyValuePair> paramsEmail = new ArrayList<>();
        paramsEmail.add(new KeyValuePair("from","helpdesk-mobile@groupe-hasnaoui.com"));
        paramsEmail.add(new KeyValuePair("to",emailDemandeur)); //emailDemandeur
        paramsEmail.add(new KeyValuePair("subject","Ticket en attente"));
        paramsEmail.add(new KeyValuePair("content",content));

        final JsonObjectRequest getRequestEmail = new JsonObjectRequest(Request.Method.POST, generateUrl(url, paramsEmail), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String state = response.getString("state");
                            String from = response.getString("from");
                            String to = response.getString("to");
                            String content = response.getString("content");
                            Log.d("RESPONSE FROM", "from = "+from);
                            Log.d("RESPONSE TO", "to = "+to);
                            Log.d("RESPONSE STATE", "state = "+state);
                            Log.d("RESPONSE CONTENT", "content = "+content);
                            //Toast.makeText(getActivity(), "Un email a été envoyé au demandeur", Toast.LENGTH_SHORT).show();

                            try {
                                Toast.makeText(getActivity(), "Un email a été envoyé au demandeur", Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(getActivity(), "Envoi de l'email au demandeur impossible", Toast.LENGTH_SHORT).show();
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

    private String getNowTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);

        return strDate;
    }


    private void TicketEnResoluHTTP(String idTicket, final String descriptionTicket, final String demandeurID, final String titreTicket) {
        String url = Constants.GLPI_URL+"Ticket/"+idTicket;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.PUT, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Do something with response
                        handlerticket.sendEmptyMessage(5);
                        getDemandeurInfoThenSendEmail(now, demandeurID, titreTicket);
                        Toast.makeText(getActivity(), "Ticket mis en résolu !", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Log.e("Error.Response!", error.toString());
                        handlerticket.sendEmptyMessage(5);
                        Toast.makeText(getActivity(), "Tache impossible", Toast.LENGTH_SHORT).show();
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

            @Override
            public byte[] getBody() {

                now = getNowTime();

                //String a = "Salut,\\n\\nMerci de vérifier la connexion quant à l'ERP RH à St-Remy\\n\\nMerci";

                String b = descriptionTicket.replaceAll("\r\n","\\\\r\\\\n");
                b = b.replaceAll("\n", "\\\\n");
                b = b.replaceAll("\r", "\\\\r");
                b = b.replaceAll("\"","\\\\\"");


                //System.out.println("a : " + a + " | " + a.length());
                //System.out.println("b : " + b + " | " + b.length());

                String motif = "[Ticket mis en résolu le "+now+"]";
                String nouv = b + "\\r\\n\\r\\n\\r\\n " + motif ;


                    String Json_Payload = "{\"input\":{\"status\": \"5\",\"content\": \""+nouv+"\"}}"; // put your json
                //String Json_Payload = "{\"input\":{\"status\": \"5\",\"content\": \""+b+"\"}}"; // put your json
                return Json_Payload.getBytes();

            }
        };

        // Add JsonArrayRequest to the RequestQueue
        queue.add(jsonArrayRequest);


    }

    private void getDemandeurInfoThenSendEmail(final String now, final String demandeurID, final String titreTicket) {
        //Récupération des informations du demandeur
        String urlDemandeur = Constants.GLPI_URL+"search/User";

        List<KeyValuePair> paramsDemandeur = new ArrayList<>();
        paramsDemandeur.add(new KeyValuePair("criteria[0][field]","2"));
        paramsDemandeur.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        paramsDemandeur.add(new KeyValuePair("criteria[0][value]",demandeurID));
        paramsDemandeur.add(new KeyValuePair("forcedisplay[0]","9"));
        paramsDemandeur.add(new KeyValuePair("forcedisplay[1]","34"));
        paramsDemandeur.add(new KeyValuePair("forcedisplay[2]","5"));

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
                                emailDemandeur = userInfo.getString("5");
                                prenomDemandeur = userInfo.getString("9");
                                nomDemandeur = userInfo.getString("34");

                            } catch (JSONException e) {
                                Log.e("Error JSONArray : ", e.getMessage());
                            }
                        } catch (JSONException e) { e.printStackTrace(); }

                        SendEmailResolu(now, prenomDemandeur,emailDemandeur, titreTicket);

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



    private void SendEmailResolu(String now, String prenomDemandeur, String emailDemandeur, String titreTicket) {
        String url = "http://10.10.10.18:8180/emailrestapi/mailapi/SendEmail/send";

        //final String content = prenomDemandeur+",\\n\\nVotre ticket "+titreTicket+" a été résolu à "+now+".\\n\\nMerci,\\n\\nL'équipe Helpdesk mobile.";
        final String content = "<h2>Notification Helpdesk</h2> <br>"+prenomDemandeur+",<br><br>" +
                "Votre ticket \""+titreTicket+"\" a été résolu le "+now+".<br><br>" +
                "Technicien chargé du ticket : "+firstnameUser+" "+nameUser+".<br><br><br>L'équipe Helpdesk Mobile.<br><br><br>" +
                "<i>P.S: Ce mail a été généré automatiquement, prière de ne pas répondre.</i>";

        List<KeyValuePair> paramsEmail = new ArrayList<>();
        paramsEmail.add(new KeyValuePair("from","helpdesk-mobile@groupe-hasnaoui.com"));
        paramsEmail.add(new KeyValuePair("to",emailDemandeur)); //emailDemandeur
        paramsEmail.add(new KeyValuePair("subject","Ticket résolu"));
        paramsEmail.add(new KeyValuePair("content",content));

        final JsonObjectRequest getRequestEmail = new JsonObjectRequest(Request.Method.POST, generateUrl(url, paramsEmail), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String state = response.getString("state");
                            String from = response.getString("from");
                            String to = response.getString("to");
                            String content = response.getString("content");
                            Log.d("RESPONSE FROM", "from = "+from);
                            Log.d("RESPONSE TO", "to = "+to);
                            Log.d("RESPONSE STATE", "state = "+state);
                            Log.d("RESPONSE CONTENT", "content = "+content);
                            //Toast.makeText(getActivity(), "Un email a été envoyé au demandeur", Toast.LENGTH_SHORT).show();
                            try {
                                Toast.makeText(getActivity(), "Un email a été envoyé au demandeur", Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(getActivity(), "Envoi de l'email au demandeur impossible", Toast.LENGTH_SHORT).show();
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

   /* private String calculTempsRestantANCIEN(String dateDebutTicket, String slaTicket, String dateEcheance) {
        String minTemps = getMinTemps(slaTicket);
        String maxTemps = getMaxTemps(slaTicket);

        long dateDebutMS = getDateDebutMS(dateDebutTicket);
        long dateEcheanceMS = getDateDebutMS(dateEcheance);

        long currentTimeMS = CurrentTimeMS();

        long differenceEcheanceCurrent = dateEcheanceMS - currentTimeMS;

        long minTempsMS = hourToMSConvert(minTemps);
        long maxTempsMS = hourToMSConvert(maxTemps);

        long differenceCurrentDebut = currentTimeMS - dateDebutMS;

        long tempsRestant = maxTempsMS - differenceCurrentDebut;



        return String.valueOf(differenceEcheanceCurrent);

    }*/

    private String calculTempsRestant(String dateEcheance) {
        if (dateEcheance.equals("null")){
            return "-1";
        }

        long dateEcheanceMS = getDateDebutMS(dateEcheance);

        long currentTimeMS = CurrentTimeMS();

        long differenceEcheanceCurrent = dateEcheanceMS - currentTimeMS;


        return String.valueOf(differenceEcheanceCurrent);

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
        boolean foundRetard = false;

            // ---- Tri par retard ---
            for (int i = 0; i < longueur; i++) {
                if (Long.valueOf(tableau[i][4]) >= 0) {
                    for (int k = i+1; k< longueur; k++){
                        if ((Long.valueOf(tableau[k][4]) < 0)){
                            foundRetard = true;
                            permuter(k,i,tableau);
                        }
                    }
                    foundRetard = false;
                }
            }

            // ---- Tri par temps min ---


        int tampon = 0;
        boolean permut;

        do {
            // hypothèse : le tableau est trié
            permut = false;
            for (int i = nbRetard(tableau); i < longueur - 1; i++) {
                // Teste si 2 éléments successifs sont dans le bon ordre ou non
                if (Long.valueOf(tableau[i][4]) > Long.valueOf(tableau[i + 1][4])) {
                    // s'ils ne le sont pas, on échange leurs positions
                    permuter(i, i+1, tableau);
                    permut = true;
                }
            }
        } while (permut);

    }

    private static int nbRetard(String[][] tableau) {
        int nbTickets = 0;
        for(int i = 0; i < tableau.length; i++){
            if (Long.valueOf(tableau[i][4]) < 0){
                nbTickets++;
            }
        }

        return nbTickets;
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
        String[] tampon = new String[8] ;

        tampon[0] = tableau[k][0];
        tampon[1] = tableau[k][1];
        tampon[2] = tableau[k][2];
        tampon[3] = tableau[k][3];
        tampon[4] = tableau[k][4];
        tampon[5] = tableau[k][5];
        tampon[6] = tableau[k][6];
        tampon[7] = tableau[k][7];


        tableau[k][0] = tableau[i][0];
        tableau[k][1] = tableau[i][1];
        tableau[k][2] = tableau[i][2];
        tableau[k][3] = tableau[i][3];
        tableau[k][4] = tableau[i][4];
        tableau[k][5] = tableau[i][5];
        tableau[k][6] = tableau[i][6];
        tableau[k][7] = tableau[i][7];


        tableau[i][0] = tampon[0];
        tableau[i][1] = tampon[1];
        tableau[i][2] = tampon[2];
        tableau[i][3] = tampon[3];
        tableau[i][4] = tampon[4];
        tableau[i][5] = tampon[5];
        tableau[i][6] = tampon[6];
        tableau[i][7] = tampon[7];
    }

     class HandlerTicket extends Handler{
        Bundle bundle;
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
                        System.out.println("listview no nvide !!");
                    }
                    break;

                case 1:
                    System.out.println("Je dois arrêter le chargement de clos");
                    if(pd.isShowing()){
                        pd.dismiss();
                    }
                    else {
                        System.out.println("Aucun chargement à arrêter clos");
                    }
                    break;

                case 2: //stop refreshing after new research
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

                case 5: //stop loading
                    if(pdChangement.isShowing()){
                        pdChangement.dismiss();
                    }
                    adapter.clear();
                    getTicketsHTTP();
                    break;

                case 6: //set Motif
                    bundle = msg.getData();
                    String motifAttente = bundle.getString("motif");
                    String id = bundle.getString("id");
                    String desc = bundle.getString("description");
                    String demandeur = bundle.getString("demandeur");
                    String titre = bundle.getString("titre");
                    pdChangement.show();
                    TicketEnAttenteHTTP(id, desc, motifAttente, demandeur, titre, motifAttente);
                    break;

            }

        }
    }

   /* @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.list) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_contextual, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.alterAttente:
                //AlterTicketAttente();
                return true;
            case R.id.alterResolu:
                Toast.makeText(getActivity(), "Résolu", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }*/

}


