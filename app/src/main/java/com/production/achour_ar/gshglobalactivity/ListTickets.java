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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListTickets extends AppCompatActivity {

    ArrayList<TicketModel> TicketModels;
    ListView listView;
    private static TicketAdapter adapter;
    String session_token, nameUser, idUser, firstnameUser, nbTicket;
    RequestQueue queue;
    String titreTicket, slaTicket, dateTicket, urgenceTicket;
    public static String[][] ticketTab ;

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
        ticketTab = new String[Integer.valueOf(nbTicket)][3];


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

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(url, params), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray Jdata = response.getJSONArray("data");
                            for (int i=0; i < Jdata.length(); i++) {
                                    try {
                                        JSONObject oneTicket = Jdata.getJSONObject(i);
                                        // Pulling items from the array
                                        titreTicket = oneTicket.getString("1");
                                        slaTicket = oneTicket.getString("30");
                                        dateTicket = oneTicket.getString("15");
                                        urgenceTicket = oneTicket.getString("10");


                                        System.out.println("Titre = " + titreTicket + "\n SLA = " + slaTicket + "\n Date = " + dateTicket);
                                    } catch (JSONException e) {
                                        Log.e("Error JSONArray : ", e.getMessage());
                                    }

                                    // ------------------------

                                TicketModel ticket = new TicketModel(titreTicket, slaTicket, dateTicket);
                                    ticket.setUrgenceTicket(urgenceText(urgenceTicket));

                                TicketModels.add(ticket);
                                adapter = new TicketAdapter(TicketModels,getApplicationContext());

                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        TicketModel TicketModel= TicketModels.get(position);

                                        Snackbar.make(view, TicketModel.getTitreTicket()+"\n"+TicketModel.getSlaTicket(), Snackbar.LENGTH_LONG)
                                                .setAction("No action", null).show();
                                    }
                                });

                                // -----------------------------

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
                urgence = "Très basse";
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



}
