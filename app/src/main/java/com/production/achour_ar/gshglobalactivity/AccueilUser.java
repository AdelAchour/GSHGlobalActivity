package com.production.achour_ar.gshglobalactivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.production.achour_ar.gshglobalactivity.Constants.App_Token;
import static com.production.achour_ar.gshglobalactivity.Constants.GLPI_URL;

public class AccueilUser extends AppCompatActivity {

    TextView welcomeView, headertitle;
    ImageView ticketButton, projectButton, rendementButton;
    String session_token, nameUser, idUser, firstnameUser;
    static String nbCount ;
    RequestQueue queue;
    private DrawerLayout mDrawerLayout;
    public static Handler handler;
    ProgressDialog pdlogout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accueil_user);

        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        idUser = i.getStringExtra("id");

        System.out.println("Je vais vérifier.");


        if(ServiceNotificationNewTicket.ServiceIsRunning == false ) {
            System.out.println("Service not running");
            ServiceNotificationNewTicket.ServiceIsRunning = true ;
            //register the services to run in background
            Intent intent = new Intent(AccueilUser.this, ServiceNotificationNewTicket.class);
            intent.putExtra("id",idUser);
            intent.putExtra("session",session_token);
            // start the services
            startService(intent);
            System.out.println("Service started with id = "+idUser);
        }


        /*Thread thread = new Thread() {
            @Override
            public void run() {
                if(ServiceNotificationNewTicket.ServiceIsRunning == false ) {
                    System.out.println("Service not running");
                    ServiceNotificationNewTicket.ServiceIsRunning = true ;
                    //register the services to run in background
                    Intent intent = new Intent(AccueilUser.this, ServiceNotificationNewTicket.class);
                    intent.putExtra("id",idUser);
                    intent.putExtra("session",session_token);
                    // start the services
                    startService(intent);
                    System.out.println("Service started");
                }
            }
        };
        thread.start();*/


        /*ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Accueil");
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDrawer);
        toolbar.setTitle("Accueil");
        setSupportActionBar(toolbar);

        pdlogout = new ProgressDialog(AccueilUser.this);
        pdlogout.setMessage("Déconnexion...");

        handler = new HandlerAccueil();

        queue = Volley.newRequestQueue(this);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        headertitle = (TextView)headerView.findViewById(R.id.header_nav_ID);


        welcomeView = (TextView)findViewById(R.id.welcomeTextView);
        ticketButton = (ImageView) findViewById(R.id.ticketButton);
        rendementButton = (ImageView) findViewById(R.id.rendementButton);
        projectButton = (ImageView) findViewById(R.id.projectButton);


       welcomeView.setText("Bienvenue "+firstnameUser+" "+nameUser);


       headertitle.setText("Profil de "+firstnameUser+" "+nameUser);
       
       getTicketsByTechnicien(idUser);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        int id = menuItem.getItemId();
                        switch (id){
                            case R.id.nav_setting:
                                startActivity(new Intent(getApplicationContext(), Setting.class));
                                break;

                            case R.id.nav_logout:
                                DialogLogout alert = new DialogLogout();
                                alert.showDialog(AccueilUser.this, firstnameUser);
                                break;

                        }


                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

       ticketButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i = new Intent(getApplicationContext(), TabLayoutActivity.class);
               i.putExtra("session",session_token);
               i.putExtra("nom",nameUser);
               i.putExtra("prenom",firstnameUser);
               i.putExtra("id",idUser);
               i.putExtra("nb",nbCount);
               startActivity(i);
           }
       });

        rendementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        projectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getTicketsByTechnicien(final String idUser) {
        String url = Constants.GLPI_URL+"search/Ticket";

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
                            nbCount = response.getString("totalcount");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //System.out.println("Titre = "+titreTicket +"\n SLA = "+slaTicket);
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

        // add it to the RequestQueue
        queue.add(getRequest);

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
    private void killsession() {
        String url = GLPI_URL+"killSession";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                        startActivity(new Intent(getApplicationContext(), FirstEverActivity.class));
                        pdlogout.dismiss();
                        finish();
                        Toast.makeText(getApplicationContext(), "Déconnecté",
                                Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(getApplicationContext(), "Déconnexion impossible",
                                Toast.LENGTH_SHORT).show();
                    }

                }
        ){
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                    JSONObject result = null;

                    if (jsonString != null && jsonString.length() > 0)
                        result = new JSONObject(jsonString);

                    return Response.success(result,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }

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

        ServiceNotificationNewTicket.ServiceIsRunning = false;
        Intent intent = new Intent(AccueilUser.this, ServiceNotificationNewTicket.class);
        // stop the services
        stopService(intent);
        System.out.println("Service stopped");

    }


    private class HandlerAccueil extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: //logout
                    pdlogout.show();
                    killsession();
                    break;
            }
        }
    }


}
