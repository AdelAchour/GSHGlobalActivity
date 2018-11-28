package com.production.achour_ar.gshglobalactivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

public class ListTicketsAttente extends Fragment {


    private ArrayList<TicketModel> TicketModels;
    private ListView listView;
    private static TicketAttenteAdapter adapter;
    private String session_token, nameUser, idUser, firstnameUser;
    private RequestQueue queue;
    private String titreTicket, slaTicket, urgenceTicket, idTicket,
            demandeurTicket, categorieTicket, etatTicket, dateDebutTicket, statutTicket, tempsResolution,
            dateEchanceTicket, dateClotureTicket, descriptionTicket, lieuTicket;
    private boolean ticketEnretard;
    public int nbTicketTab = 10;
    private String nbCount;
    private ProgressDialog pd;
    private ProgressDialog pdChangement;
    public String[][] ticketTab;
    private SwipeRefreshLayout swipeLayout;
    public static Handler handlerticketAttente;
    private int range;
    private String emailDemandeur;
    private String prenomDemandeur;
    private String nomDemandeur;
    private String nowEnCours;
    private String observateur;
    private String emailObservateur;
    private String prenomObservateur;
    private String nomObservateur;

    public ListTicketsAttente() {
        handlerticketAttente = new HandlerTicketAttente();
        Log.d("INITIALIZATION","J'ai intialisé le handler attente !");
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
                if (!TicketModels.isEmpty()) {
                    adapter.clear();
                    getTicketsHTTP();
                } else {
                    getTicketsHTTP();
                }
            }
        });
    }

    private void SetupPDs() {
        pd.setTitle("Tickets en attente");
        pd.setMessage("Chargement des tickets...");
        pdChangement.setMessage("Changement de l'état...");
    }

    private void initView(View view) {
        queue = Volley.newRequestQueue(getActivity());
        swipeLayout = view.findViewById(R.id.swipe_container);
        swipeLayout.setColorScheme(android.R.color.holo_green_light,
                android.R.color.holo_blue_dark);

        handlerticketAttente = new HandlerTicketAttente();
        pdChangement = new ProgressDialog(getActivity());
        pd = new ProgressDialog(getActivity());
        listView = view.findViewById(R.id.list);
        TicketModels = new ArrayList<>();
    }


    private void getTicketsHTTP() {
        String url = Constants.GLPI_URL + "search/Ticket";

        int maxRange = range - 1;
        List<KeyValuePair> params = new ArrayList<>();
        params.add(new KeyValuePair("criteria[0][field]", "5"));
        params.add(new KeyValuePair("criteria[0][searchtype]", "equals"));
        params.add(new KeyValuePair("criteria[0][value]", idUser));

        params.add(new KeyValuePair("criteria[1][link]", "AND"));
        params.add(new KeyValuePair("criteria[1][field]", "12"));
        params.add(new KeyValuePair("criteria[1][searchtype]", "equals"));
        params.add(new KeyValuePair("criteria[1][value]", "4"));

        params.add(new KeyValuePair("forcedisplay[0]", "4"));
        params.add(new KeyValuePair("forcedisplay[1]", "10"));
        params.add(new KeyValuePair("forcedisplay[2]", "7"));
        params.add(new KeyValuePair("forcedisplay[3]", "12"));
        params.add(new KeyValuePair("forcedisplay[4]", "15"));
        params.add(new KeyValuePair("forcedisplay[5]", "30"));
        params.add(new KeyValuePair("forcedisplay[6]", "18"));
        params.add(new KeyValuePair("forcedisplay[7]", "21"));
        params.add(new KeyValuePair("forcedisplay[8]", "83"));
        params.add(new KeyValuePair("forcedisplay[9]", "82"));
        params.add(new KeyValuePair("forcedisplay[10]", "16"));
        params.add(new KeyValuePair("forcedisplay[11]", "2"));
        params.add(new KeyValuePair("forcedisplay[12]", "66"));

        params.add(new KeyValuePair("sort", "15"));
        params.add(new KeyValuePair("order", "DESC"));
        params.add(new KeyValuePair("range", "0-" + maxRange + ""));

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(url, params), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            nbCount = response.getString("count");
                            ticketTab = new String[Integer.valueOf(nbCount)][nbTicketTab];

                            Bundle bundle = new Bundle();
                            bundle.putString("position", "3");
                            bundle.putString("count", nbCount);
                            bundle.putString("title", "En attente");
                            Message msg = new Message();
                            msg.setData(bundle);
                            msg.what = 1;
                            TabLayoutActivity.handler.sendMessage(msg);

                            JSONArray Jdata = response.getJSONArray("data");
                            for (int i = 0; i < Jdata.length(); i++) {
                                ArrayList<String> obsID = new ArrayList<>();
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

                                    observateur = oneTicket.getString("66");

                                    //observersIDs.clear();
                                    try {
                                        JSONArray JObs = oneTicket.getJSONArray("66");
                                        Log.d("JSONARRAY OBS N°"+i+" ("+titreTicket+")","Observer converted to JSONArray !");
                                        for (int j=0; j < JObs.length(); j++) {
                                            try {
                                                String oneObservateur = JObs.getString(j);
                                                obsID.add(oneObservateur);
                                            } catch (JSONException e) {
                                                Log.e("Error Observateur ", e.getMessage());
                                            }
                                        }
                                    } catch (JSONException e) {
                                        Log.e("JSONARRAY OBSERVER ("+titreTicket+")","Observer cannot be converted to JSONArray...");
                                        obsID.add(observateur);
                                        //e.printStackTrace();
                                    }


                                } catch (JSONException e) {
                                    Log.e("Nb of data: " + Jdata.length() + " || " + "Error JSONArray at " + i + " : ", e.getMessage());
                                }
                                // ------------------------

                                /* Remplissage du tableau des tickets pour le row item */
//                                ticketTab[i][0] = titreTicket;
//                                ticketTab[i][1] = slaTicket;
//                                ticketTab[i][2] = dateDebutTicket;
//                                ticketTab[i][3] = urgenceText(urgenceTicket);
//                                ticketTab[i][4] = calculTempsRestant(dateDebutTicket, slaTicket);
//                                ticketTab[i][5] = String.valueOf(ticketEnretard);
//                                ticketTab[i][6] = statutTicket;
//                                ticketTab[i][7] = idTicket;
//                                ticketTab[i][8] = calculTempsResolution(dateClotureTicket, dateDebutTicket);
//                                ticketTab[i][9] = calculTempsRetard(dateEchanceTicket, dateClotureTicket);


                                /* ---------  Creating a TicketModel object  --------- */

                                TicketModel ticket = new TicketModel(titreTicket, slaTicket, dateDebutTicket,
                                        calculTempsRestant(dateEchanceTicket), idTicket, statutTicket);

                                ticket.setUrgenceTicket(urgenceText(urgenceTicket));
                                ticket.setTicketEnRetard(Boolean.parseBoolean(String.valueOf(ticketEnretard)));
                                ticket.setDescription(descriptionTicket);
                                ticket.setDemandeurID(demandeurTicket);

                                Bundle bundleList = new Bundle();
                                bundleList.putStringArrayList(Constants.KEY_ARRAYLIST_OBSERVERS_ATTENTE, obsID);

                                ticket.setBundleArray(bundleList);

                                TicketModels.add(ticket);

                                /* ---------  Creating a TicketModel object  --------- */

                            }


                            //addModelsFromTab(ticketTab);

                            if (getActivity() != null) {
                                adapter = new TicketAttenteAdapter(TicketModels, getActivity());
                            } else {
                                Log.e("STOP BEFORE ERROR", "Il allait y avoir une erreur man (ATTENTE)");
                            }

                            listView.setAdapter(adapter);
                            handlerticketAttente.sendEmptyMessage(1);

                            if (swipeLayout.isRefreshing()) {
                                swipeLayout.setRefreshing(false);
                            }


                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    TicketModel TicketModel = TicketModels.get(position);

                                    Snackbar.make(view, "Id = " + TicketModel.getIdTicket(), Snackbar.LENGTH_LONG)
                                            .setAction("No action", null).show();

                                    Intent i = new Intent(getActivity(), InfoTicket.class);
                                    i.putExtra("session", session_token);
                                    i.putExtra("nom", nameUser);
                                    i.putExtra("prenom", firstnameUser);
                                    i.putExtra("id", idUser);
                                    i.putExtra("idTicket", TicketModel.getIdTicket());

                                    startActivity(i);

                                    Bundle bundleList = TicketModel.getBundleArray();
                                    ArrayList<String> obsID = bundleList.getStringArrayList(Constants.KEY_ARRAYLIST_OBSERVERS_ATTENTE);
                                    AfficheArrayList(obsID);
                                    System.out.println("Titre: "+TicketModel.getTitreTicket());

                                }
                            });

                            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    final TicketModel TicketModel= TicketModels.get(position);


                                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                                    builderSingle.setTitle("Faites votre choix");

                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);
                                    arrayAdapter.add("Mettre le ticket en cours");


                                    builderSingle.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Bundle bundleList = TicketModel.getBundleArray();
                                            ArrayList<String> obsID = bundleList.getStringArrayList(Constants.KEY_ARRAYLIST_OBSERVERS_ATTENTE);

                                            String strName = arrayAdapter.getItem(which);
                                            switch (strName){
                                                case "Mettre le ticket en cours":

                                                    pdChangement.show();
                                                    TicketEnCoursHTTP(TicketModel.getIdTicket(), TicketModel.getDescription(),
                                                            TicketModel.getDemandeurID(), TicketModel.getTitreTicket(),
                                                            obsID);

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
                            Log.e("malkach", e.getMessage());
                            handlerticketAttente.sendEmptyMessage(4);
                            handlerticketAttente.sendEmptyMessage(2);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Vérifiez votre connexion", Toast.LENGTH_LONG).show();
                        Log.e("Error.Response", error.toString());
                    }

                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("App-Token", Constants.App_Token);
                params.put("Session-Token", session_token);
                return params;
            }

        };

        // add it to the RequestQueue
        queue.add(getRequest);


    }

    private void AfficheArrayList(ArrayList listObservateur) {
        System.out.println("\n --- ArrayList --- \n");
        for (int i = 0; i < listObservateur.size(); i++){
            //System.out.println(ticketTab.get(i));
            String oneObs = (String)listObservateur.get(i);
            System.out.println(oneObs);
        }
    }

    private void TicketEnCoursHTTP(final String idTicket, final String descriptionTicket, final String demandeurID, final String titreTicket, final ArrayList<String> observerIDs) {
        String url = Constants.GLPI_URL+"Ticket/"+idTicket;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.PUT, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Do something with response
                        handlerticketAttente.sendEmptyMessage(5);
                        getDemandeurInfoThenSendEmail(nowEnCours, demandeurID, titreTicket);

                        if (isArrayNull(observerIDs)){
                            Log.e("OBS PRESENT", "Aucun observateur, pas de mail");
                        }
                        else {
                            Log.d("OBS PRESENT", "Obs present ("+observerIDs.size()+" mails...)");
                            getObserversInfoThenSendEmail(nowEnCours, observerIDs, titreTicket);
                        }

                        Toast.makeText(getActivity(), "Ticket remis en cours!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Log.e("Error.Response!", error.toString());
                        handlerticketAttente.sendEmptyMessage(5);
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

                nowEnCours = getNowTime();

                //String a = "Salut,\\n\\nMerci de vérifier la connexion quant à l'ERP RH à St-Remy\\n\\nMerci";

                String b = descriptionTicket.replaceAll("\r\n","\\\\r\\\\n");
                b = b.replaceAll("\n", "\\\\n");
                b = b.replaceAll("\r", "\\\\r");
                b = b.replaceAll("\"","\\\\\"");

                String motif = "[Ticket remis en cours le "+nowEnCours+"]";
                String nouv = b + "\\r\\n\\r\\n\\r\\n " + motif ;


                String Json_Payload = "{\"input\":{\"status\": \"2\",\"content\": \""+nouv+"\"}}"; // put your json

                return Json_Payload.getBytes();

            }
        };

        // Add JsonArrayRequest to the RequestQueue
        queue.add(jsonArrayRequest);

    }

    private void getObserversInfoThenSendEmail(final String nowEnCours, final ArrayList<String> observerIDs, final String titreTicket) {
        //Récupération des informations de tous les observateurs
        String urlObs = Constants.GLPI_URL+"search/User";

        for (int indexObs = 0; indexObs < observerIDs.size(); indexObs++){
            System.out.println("Obs n°"+indexObs);
            List<KeyValuePair> paramsObs = new ArrayList<>();
            paramsObs.add(new KeyValuePair("criteria[0][field]","2"));
            paramsObs.add(new KeyValuePair("criteria[0][searchtype]","equals"));
            paramsObs.add(new KeyValuePair("criteria[0][value]",observerIDs.get(indexObs)));
            paramsObs.add(new KeyValuePair("forcedisplay[0]","9"));
            paramsObs.add(new KeyValuePair("forcedisplay[1]","34"));
            paramsObs.add(new KeyValuePair("forcedisplay[2]","5"));


            final JsonObjectRequest getRequestObserver = new JsonObjectRequest(Request.Method.GET, URLGenerator.generateUrl(urlObs, paramsObs), null,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray Jdata = response.getJSONArray("data");
                                try {
                                    JSONObject userInfo = Jdata.getJSONObject(0);
                                    // Récupération des données de l'observateur
                                    emailObservateur = userInfo.getString("5");
                                    prenomObservateur = userInfo.getString("9");
                                    nomObservateur = userInfo.getString("34");

                                } catch (JSONException e) {
                                    Log.e("Error JSONArray : ", e.getMessage());
                                }

                            } catch (JSONException e) {
                                Log.e("JSON Error response",e.getMessage());
                            }

                            SendEmailEnCoursToObservers(nowEnCours, prenomObservateur,emailObservateur, titreTicket);

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

            queue.add(getRequestObserver);
        }

    }

    private void SendEmailEnCoursToObservers(String nowEnCours, String prenomObservateur, String emailObservateur, String titreTicket) {
        String url = Constants.URL_EMAIL_API;

        final String content = "<h2>Notification Helpdesk</h2> <br>"+prenomObservateur+",<br><br>" +
                "Le ticket \""+titreTicket+"\" dont vous êtes l'observateur a été <b>remis en cours</b> le "+nowEnCours+".<br><br>" +
                "Ingénieur chargé du ticket : "+firstnameUser+" "+nameUser+".<br><br><br>L'équipe Helpdesk Mobile.<br><br><br>" +
                "<i>P.S: Ce mail a été généré automatiquement, prière de ne pas répondre.</i>";

        List<KeyValuePair> paramsEmail = new ArrayList<>();
        paramsEmail.add(new KeyValuePair("from","helpdesk-mobile@groupe-hasnaoui.com"));
        paramsEmail.add(new KeyValuePair("to",emailObservateur)); //emailObservateur
        paramsEmail.add(new KeyValuePair("subject","Ticket en cours"));
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
                                Toast.makeText(getActivity(), "Un email a été envoyé à l'observateur", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("Toast Email", "Impossible de notifier");
                            }

                            notifyAdminByEmail(state, from, to, content);

                        } catch (JSONException e) { e.printStackTrace(); }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response Email", error.toString());
                        //Toast.makeText(getActivity(), "Envoi de l'email au demandeur impossible", Toast.LENGTH_SHORT).show();
                        notifyAdminErrorByEmail(content, error.toString(), error.getMessage(), error.getLocalizedMessage());
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

    private void notifyAdminErrorByEmail(String content, String errortoString, String message, String localizedMessage) {
        String url = Constants.URL_EMAIL_API;

        final String ContentMessage = "<h2>--- LOG Admin ERROR ---</h2> <br><br><br><br>" +
                "Un problème est survenu lors de l'envoi d'un email via l'API. <br><br><br>" +
                "Error to string: "+errortoString+"<br><br>" +
                "Error Message: "+message+"<br><br>" +
                "Localized Message: "+localizedMessage+"<br><br><br>" +
                "Content: <br> __________________ <br> "+content+" <br> __________________ <br><br><br>";

        List<KeyValuePair> paramsEmail = new ArrayList<>();
        paramsEmail.add(new KeyValuePair("from","helpdesk-mobile@groupe-hasnaoui.com"));
        paramsEmail.add(new KeyValuePair("to","adel.achour@groupe-hasnaoui.com")); //Admin
        paramsEmail.add(new KeyValuePair("subject","LOG ERROR Admin"));
        paramsEmail.add(new KeyValuePair("content",ContentMessage));

        final JsonObjectRequest getRequestEmail = new JsonObjectRequest(Request.Method.POST, generateUrl(url, paramsEmail), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String state = response.getString("state");
                            String from = response.getString("from");
                            String to = response.getString("to");
                            Log.d("RESPONSE FROM", "from = "+from);
                            Log.d("RESPONSE TO", "to = "+to);
                            Log.d("RESPONSE STATE", "state = "+state);
                            //Toast.makeText(getActivity(), "Un email a été envoyé au demandeur", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) { e.printStackTrace(); }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error Email Notif Admin", error.toString());
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

    private boolean isArrayNull(ArrayList<String> obsID) {
        if (obsID.get(0).equals("null")) return true;
        else return false;
    }
    private void getDemandeurInfoThenSendEmail(final String nowEnCours, final String demandeurID, final String titreTicket) {
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

                        SendEmailEnCoursToRequester(nowEnCours, prenomDemandeur,emailDemandeur, titreTicket);

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

    private void SendEmailEnCoursToRequester(String nowEnCours, String prenomDemandeur, String emailDemandeur, String titreTicket) {
        String url = Constants.URL_EMAIL_API;

        //final String content = prenomDemandeur+",\\n\\nVotre ticket "+titreTicket+" a été résolu à "+now+".\\n\\nMerci,\\n\\nL'équipe Helpdesk mobile.";
        final String content = "<h2>Notification Helpdesk</h2> <br>"+prenomDemandeur+",<br><br>" +
                "Votre ticket \""+titreTicket+"\" a été <b>remis en cours</b> le "+nowEnCours+".<br><br>" +
                "Ingénieur chargé du ticket : "+firstnameUser+" "+nameUser+".<br><br><br>L'équipe Helpdesk Mobile.<br><br><br>" +
                "<i>P.S: Ce mail a été généré automatiquement, prière de ne pas répondre.</i>";

        List<KeyValuePair> paramsEmail = new ArrayList<>();
        paramsEmail.add(new KeyValuePair("from","helpdesk-mobile@groupe-hasnaoui.com"));
        paramsEmail.add(new KeyValuePair("to",emailDemandeur)); //emailDemandeur
        paramsEmail.add(new KeyValuePair("subject","Ticket en cours"));
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

                            notifyAdminByEmail(state, from, to, content);

                        } catch (JSONException e) { e.printStackTrace(); }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response Email", error.toString());
                        //Toast.makeText(getActivity(), "Envoi de l'email au demandeur impossible", Toast.LENGTH_SHORT).show();
                        notifyAdminErrorByEmail(content, error.toString(), error.getMessage(), error.getLocalizedMessage());
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

    private void notifyAdminByEmail(String state, String from, String to, String content) {
        String url = Constants.URL_EMAIL_API;

        final String ContentMessage = "<h2>--- Message Admin ---</h2> <br><br><br>" +
                "Un mail a été envoyé avec succès via l'API. <br><br>" +
                "State: "+state+"<br><br>" +
                "From: "+from+"<br><br>" +
                "To: "+to+"<br><br><br>" +
                "Content: <br> __________________ <br> "+content+" <br> __________________ <br><br><br>";

        List<KeyValuePair> paramsEmail = new ArrayList<>();
        paramsEmail.add(new KeyValuePair("from","helpdesk-mobile@groupe-hasnaoui.com"));
        paramsEmail.add(new KeyValuePair("to","adel.achour@groupe-hasnaoui.com")); //Admin
        paramsEmail.add(new KeyValuePair("subject","Notif Admin"));
        paramsEmail.add(new KeyValuePair("content",ContentMessage));

        final JsonObjectRequest getRequestEmail = new JsonObjectRequest(Request.Method.POST, generateUrl(url, paramsEmail), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String state = response.getString("state");
                            String from = response.getString("from");
                            String to = response.getString("to");
                            Log.d("RESPONSE FROM", "from = "+from);
                            Log.d("RESPONSE TO", "to = "+to);
                            Log.d("RESPONSE STATE", "state = "+state);
                            //Toast.makeText(getActivity(), "Un email a été envoyé au demandeur", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) { e.printStackTrace(); }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error Email Notif Admin", error.toString());
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
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);

        return strDate;
    }

    private String calculTempsRetard(String dateEchanceTicket, String dateClotureTicket) {
        if ((dateEchanceTicket.equals("null")) || (dateClotureTicket.equals("null"))) {
            return "-1";
        }

        long echeance = getDateDebutMS(dateEchanceTicket);
        long cloture = getDateDebutMS(dateClotureTicket);

        long tmps = cloture - echeance;
        return String.valueOf(tmps);
    }

    private String calculTempsResolution(String dateClotureTicket, String dateDebutTicket) {
        if ((dateDebutTicket.equals("null")) || (dateClotureTicket.equals("null"))) {
            return "-1";
        }
        long debut = getDateDebutMS(dateDebutTicket);
        long cloture = getDateDebutMS(dateClotureTicket);

        long tmps = cloture - debut;
        return String.valueOf(tmps);
    }

    private boolean getBooleanFromSt(String string) {
        boolean bool = false;
        if (string.equals("0")) {
            bool = false;
        } else if (string.equals("1")) {
            bool = true;
        }
        return bool;
    }

    private String getBetweenBrackets(String slaTicket) {
        String between = "";

        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(slaTicket);
        while (matcher.find()) {
            between = matcher.group();
        }

        return between;
    }

    private String getDigit(String text) {
        String digit = "";

        Pattern pattern = Pattern.compile("([\\d]+)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            digit = matcher.group();
        }

        return digit;
    }

    private String getMinTemps(String slaTicket) {
        String between = getBetweenBrackets(slaTicket);
        String minTemps = "";

        Pattern pattern = Pattern.compile("^(.*?)\\/");
        Matcher matcher = pattern.matcher(between);
        while (matcher.find()) {
            minTemps = matcher.group();
        }

        return getDigit(minTemps);
    }

    private String getMaxTemps(String slaTicket) {
        String between = getBetweenBrackets(slaTicket);
        String maxTemps = "";

        Pattern pattern = Pattern.compile("([\\d]+)(?=[^\\/]*$)");
        Matcher matcher = pattern.matcher(between);
        while (matcher.find()) {
            maxTemps = matcher.group();
        }


        return maxTemps;
    }

    private String calculTempsRestant2(String dateDebutTicket, String slaTicket) {
        if ((slaTicket.equals("null")) || (slaTicket.equals(""))) {
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
        long time = Long.valueOf(minTemps) * 3600000;
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
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateDebutMS = oldDate.getTime();

        return dateDebutMS;
    }

    private void addModelsFromTab(String[][] ticketTab) {
        for (int i = 0; i < ticketTab.length; i++) {
            TicketModel ticket = new TicketModel(ticketTab[i][0], ticketTab[i][1], ticketTab[i][2], ticketTab[i][4], ticketTab[i][7], ticketTab[i][6]);
            ticket.setUrgenceTicket(ticketTab[i][3]);
            ticket.setTicketEnRetard(Boolean.parseBoolean(ticketTab[i][5]));
            ticket.setTempsResolution(ticketTab[i][8]);
            ticket.setTempsRetard(ticketTab[i][9]);

            TicketModels.add(ticket);
        }
    }

    private void AfficheTab(String[][] ticketTab) {
        System.out.println("\n --- Tableau de ticket --- \n");
        for (int i = 0; i < ticketTab.length; i++) {
            for (int j = 0; j < ticketTab[0].length; j++) {
                System.out.print(ticketTab[i][j] + " ");
            }
            System.out.println("\n");
        }
    }

    private String urgenceText(String urgenceTicket) {
        String urgence = "";
        int urg = Integer.valueOf(urgenceTicket);
        switch (urg) {
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
            int cpt = 1;
            for (KeyValuePair parameter : params) {
                if (cpt == 1) {
                    baseUrl += "?" + parameter.getKey() + "=" + parameter.getValue();
                } else {
                    baseUrl += "&" + parameter.getKey() + "=" + parameter.getValue();
                }
                cpt++;
            }
        }
        return baseUrl;
    }

    class HandlerTicketAttente extends Handler {
        boolean nodata = false;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (TicketModels.isEmpty()) {
                        System.out.println("listview vide");
                        pd.show();
                        if (nodata) {
                            System.out.println("Enregistré, 0 data déjà");
                            pd.dismiss();
                        }
                    } else {
                        System.out.println("listview no nvide !!");
                    }
                    break;

                case 1:
                    System.out.println("Je dois arrêter le chargement de attente");
                    if (pd.isShowing()) {
                        pd.dismiss();
                    } else {
                        System.out.println("Aucun chargement à arrêter attente");
                    }
                    break;

                case 2:
                    nodata = true;
                    if (pd.isShowing()) {
                        System.out.println("Nouvelle recherche, 0 data");
                        pd.dismiss();
                    }
                    break;

                case 3: //refresh LV
                    if (!TicketModels.isEmpty()) { //pleins
                        swipeLayout.setRefreshing(true);
                        adapter.clear();
                        getTicketsHTTP();
                    } else {
                        swipeLayout.setRefreshing(true);
                        getTicketsHTTP();
                    }
                    break;

                case 4: //stop swipe
                    if (swipeLayout.isRefreshing()) {
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
            }

        }
    }


}
