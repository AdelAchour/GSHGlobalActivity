package com.production.achour_ar.gshglobalactivity;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
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
    RequestQueue queue;
    RequestQueue queueLast;
    private String session_token;
    private int lastTicketID;
    private String titreLastTicket;
    private String descriptionLastTicket;
    private String dateLastTicket;

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
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                while (ServiceIsRunning) {
                                    System.out.println("Another new ticket notif test...");
                                    CheckNewTicket();
                                    try { Thread.sleep(25000); }
                                    catch (Exception ex) { ex.printStackTrace(); }
                                }
                            }
                        };
                        thread.start();

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




    private void CheckNewTicket() {
        String url = FirstEverActivity.GLPI_URL + "search/Ticket";
        List<KeyValuePair> params = new ArrayList<>();
        //TECHNICIEN = IDUSER
        params.add(new KeyValuePair("criteria[0][field]", "5"));
        params.add(new KeyValuePair("criteria[0][searchtype]", "equals"));
        params.add(new KeyValuePair("criteria[0][value]", idUser));
        //AFFICHAGE ET SORT/ORDER
        params.add(new KeyValuePair("forcedisplay[0]", "2"));
        params.add(new KeyValuePair("forcedisplay[1]", "21"));
        params.add(new KeyValuePair("forcedisplay[2]", "15"));
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
                            lastTicketID = Integer.valueOf(ticket.getString("2"));
                            titreLastTicket = ticket.getString("1");
                            descriptionLastTicket = ticket.getString("21");
                            dateLastTicket = ticket.getString("15");
                            System.out.println("NEW REQUEST, ID = "+lastTicketID);

                            NotifyOrNot(ticketID, lastTicketID, titreLastTicket, descriptionLastTicket);

                        } catch (JSONException e) {
                            Log.e("Error User ", e.getMessage());
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

    private void NotifyOrNot(int ticketID, int lastTicketID, String titreLastTicket, String descriptionLastTicket) {
        if (lastTicketID > ticketID){
            //WE'VE GOT A NEW TICKET MAN, LET'S HANDLE IT !
            System.out.println("we've got a new ticket!");
            Bundle bundle = new Bundle();

            bundle.putString("titre", titreLastTicket);
            bundle.putString("content", descriptionLastTicket);
            bundle.putString("date", dateLastTicket);
            bundle.putInt("id", lastTicketID);

            System.out.println("I'm sending the title: "+titreLastTicket+" and the id: "+lastTicketID);
            Intent intent = new Intent();
            intent.setAction("com.example.Broadcast");
            intent.putExtra("bundle", bundle);
            sendBroadcast(intent);
            this.ticketID = this.lastTicketID ;
        }
        else {
            System.out.println("Aucun nouveau ticket");
        }
    }
}

