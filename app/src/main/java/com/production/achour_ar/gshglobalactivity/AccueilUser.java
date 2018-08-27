package com.production.achour_ar.gshglobalactivity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.production.achour_ar.gshglobalactivity.FirstEverActivity.App_Token;
import static com.production.achour_ar.gshglobalactivity.FirstEverActivity.GLPI_URL;

public class AccueilUser extends AppCompatActivity {

    TextView welcomeView, headertitle;
    Button ticketButton, rendementButton;
    String session_token, nameUser, idUser, firstnameUser;
    static String nbCount ;
    ProgressBar progressBar;
    RequestQueue queue;
    private DrawerLayout mDrawerLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accueil_user);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        queue = Volley.newRequestQueue(this);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        headertitle = (TextView)headerView.findViewById(R.id.header_nav_ID);


        welcomeView = (TextView)findViewById(R.id.welcomeTextView);
        progressBar = (ProgressBar)findViewById(R.id.progressBarList);
        ticketButton = (Button) findViewById(R.id.ticketButton);
        rendementButton = (Button) findViewById(R.id.rendementButton);

       Intent i = getIntent();
       session_token = i.getStringExtra("session");
       nameUser = i.getStringExtra("nom");
       firstnameUser = i.getStringExtra("prenom");
       idUser = i.getStringExtra("id");

       welcomeView.setText("Bienvenue "+firstnameUser+" "+nameUser);


       headertitle.setText("Profil de "+firstnameUser+" "+nameUser);
       
       getTicketsByTechnicien(idUser);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
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
                params.put("App-Token",FirstEverActivity.App_Token);
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
}
