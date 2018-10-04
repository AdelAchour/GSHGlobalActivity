package com.production.achour_ar.gshglobalactivity;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.view.View;

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

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.production.achour_ar.gshglobalactivity.ListTickets.generateUrl;

public class ServiceNotificationNewTicket extends IntentService {
    public static boolean ServiceIsRunning = false;
    public int ticketID = 0;
    private String idUser;
    private String idTicket;
    RequestQueue queue;
    RequestQueue queueLast;
    private String session_token;
    private String idlastTicket;
    private String titreLastTicket;

    public ServiceNotificationNewTicket() {
        super("MyWebRequestService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    protected void onHandleIntent(Intent workIntent) {
        idUser = workIntent.getStringExtra("id");
        session_token = workIntent.getStringExtra("session");
        queue = Volley.newRequestQueue(this);
        queueLast = Volley.newRequestQueue(this);

        System.out.println("Service running... ");

        String url = FirstEverActivity.GLPI_URL + "search/Ticket";
        List<KeyValuePair> params = new ArrayList<>();
        //TECHNICIEN = IDUSER
        params.add(new KeyValuePair("criteria[0][field]", "5"));
        params.add(new KeyValuePair("criteria[0][searchtype]", "equals"));
        params.add(new KeyValuePair("criteria[0][value]", idUser));
        //AFFICHAGE ET SORT/ORDER
        params.add(new KeyValuePair("forcedisplay[0]", "2"));
        params.add(new KeyValuePair("sort", "2"));
        params.add(new KeyValuePair("order", "DESC"));
        params.add(new KeyValuePair("range", "0-0"));

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(url, params), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray Jdata = response.getJSONArray("data");
                            JSONObject ticket = Jdata.getJSONObject(0);
                            ticketID = Integer.valueOf(ticket.getString("2"));
                            System.out.println("FIRST TIME, ID = "+ticketID);

                        } catch (JSONException e) {
                            Log.e("Error User ", e.getMessage());
                        }


                        //THE REAL WORK
                        while (ServiceIsRunning) {
                            // get tickets
                            System.out.println("Another new ticket notif test...");

                            try {
                                Thread.sleep(5000);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error. Ticket", error.toString());
                    }

                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("App-Token", FirstEverActivity.App_Token);
                params.put("Session-Token", session_token);
                return params;
            }

        };

        // add it to the RequestQueue
        queue.add(getRequest);

    }
}

