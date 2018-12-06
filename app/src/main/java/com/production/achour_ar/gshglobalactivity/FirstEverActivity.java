package com.production.achour_ar.gshglobalactivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

import static com.production.achour_ar.gshglobalactivity.ListTickets.generateUrl;


public class FirstEverActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText usernameEdit, passwordEdit;
    private ProgressDialog pdAuth;
    private RequestQueue queue;
    private Button connect;
    private String session_token ;
    private String directionUser;
    private String jobUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_ever);

        initView();
        setupActionBar();
        setupProgressD();
        setListener();

    }


    private void setListener() {
        connect.setOnClickListener(this);
    }

    private void setupProgressD() {
        pdAuth.setMessage("Authentification");
    }

    private void setupActionBar() {
        getSupportActionBar().hide();
    }

    private void initView() {
        queue = Volley.newRequestQueue(this);
        usernameEdit = findViewById(R.id.usernameText);
        passwordEdit = findViewById(R.id.passwordText);
        connect = findViewById(R.id.connectButton);
        pdAuth = new ProgressDialog(FirstEverActivity.this);
    }


    private void initSession() {

        final String username = usernameEdit.getText().toString();
        final String password = passwordEdit.getText().toString();
        pdAuth.show();

        //String url = Constants.GLPI_URL+"initSession/?App_Token="+Constants.App_Token+"&login="+username+"&password="+password;

        String url = Constants.GLPI_URL+"initSession";

        List<KeyValuePair> params = new ArrayList<>();

        params.add(new KeyValuePair("app_token",Constants.App_Token));
        params.add(new KeyValuePair("login",username));
        params.add(new KeyValuePair("password",password));

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URLGenerator.generateUrl(url, params), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Log.d("Response = ", response.toString());
                        try {
                            session_token = response.getString("session_token");
                            MyPreferences.SaveString(Constants.KEY_USERNAME, username);
                            MyPreferences.SaveString(Constants.KEY_PASSWORD, password);
                            Log.d("SavePreferences", "Savec the IDs");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        getUser(session_token);
                        System.out.println("session : "+session_token);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pdAuth.dismiss();
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(getApplicationContext(), "Connexion échouée. \nProblème d'identifiants ou de connexion réseaux.",
                                Toast.LENGTH_SHORT).show();
                    }

                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                //String creds = String.format("%s:%s",username,password);
                //String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                //params.put("Authorization", auth);
                //params.put("App-Token",Constants.App_Token);
                return params;
            }
        };


        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // add it to the RequestQueue
        queue.add(getRequest);

    }



    private void getUser(final String Token_Session) {
        String url = Constants.GLPI_URL+"getFullSession";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        String nameUser = "";
                        String idUser = "";
                        String firstnameUser = "";

                        System.out.println("Dans get User");
                        Log.d("Response User : ", response.toString());

                        try {
                            JSONObject sessionObj = response.getJSONObject("session");
                            nameUser = sessionObj.getString("glpirealname");
                            firstnameUser = sessionObj.getString("glpifirstname");
                            idUser = sessionObj.getString("glpiID");
                            System.out.println("Nom & ID: "+nameUser+idUser);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //getJobUser(idUser);
                        pdAuth.dismiss();
                        Intent i = new Intent(getApplicationContext(), AccueilUser.class);
                        i.putExtra("nom", nameUser);
                        i.putExtra("prenom", firstnameUser);
                        i.putExtra("id", idUser);
                        i.putExtra("session", Token_Session);
                        startActivity(i);
                        finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pdAuth.dismiss();
                        Log.e("Error.Response", error.toString());
                    }
                }

        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("App-Token", Constants.App_Token);
                params.put("Session-Token", Token_Session);

                return params;
            }
        };

        queue.add(getRequest);
    }

    private void getJobUser(String idUser) {
        String url = Constants.GLPI_URL+"search/User";

        List<KeyValuePair> params = new ArrayList<>();
        //TECHNICIEN = IDUSER
        params.add(new KeyValuePair("criteria[0][field]","2"));
        params.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        params.add(new KeyValuePair("criteria[0][value]",idUser));

        //AFFICHAGE
        params.add(new KeyValuePair("forcedisplay[0]","82"));
        params.add(new KeyValuePair("forcedisplay[0]","81"));

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(url, params), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray Jdata = response.getJSONArray("data");
                            JSONObject user = Jdata.getJSONObject(0);
                            jobUser = user.getString("81");

                        } catch (JSONException e) {
                            Log.e("Error User ",e.getMessage());
                            //progressBar.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressBar.setVisibility(View.GONE);
                        Log.e("Error. Direction", error.toString());
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

        queue.add(getRequest);
    }

    private void UIRedirection(String idUser) {
        String url = Constants.GLPI_URL+"search/User";

        List<KeyValuePair> params = new ArrayList<>();
        //TECHNICIEN = IDUSER
        params.add(new KeyValuePair("criteria[0][field]","2"));
        params.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        params.add(new KeyValuePair("criteria[0][value]",idUser));

        //AFFICHAGE
        params.add(new KeyValuePair("forcedisplay[0]","82"));

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(url, params), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray Jdata = response.getJSONArray("data");
                            JSONObject user = Jdata.getJSONObject(0);
                            directionUser = user.getString("82");

                        } catch (JSONException e) {
                            Log.e("Error User ",e.getMessage());
                            //progressBar.setVisibility(View.GONE);
                        }

                        //progressBar.setVisibility(View.GONE);

                        System.out.println("DIRECTION "+directionUser);

                        if(directionUser.equals("DSI")){
                            System.out.println("Parmi nous MAN !");
                        }
                        else{
                            System.out.println("Etranger ! "+directionUser);
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressBar.setVisibility(View.GONE);
                        Log.e("Error. Direction", error.toString());
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

      /*  private void initSession() {

        System.out.println("Dans la fonction !");
        final String username = usernameEdit.getText().toString();
        final String password = passwordEdit.getText().toString();
        //progressBar.setVisibility(View.VISIBLE);
        pdAuth.show();
                //String url = Constants.GLPI_URL+"initSession/?Constants.App_Token="+Constants.App_Token+"&login="+username+"&password="+password;

        String url = Constants.GLPI_URL+"initSession";

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Log.d("Response = ", response.toString());
                        try {
                            session_token = response.getString("session_token");
                            editor.putString("session", session_token);
                            editor.commit();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        getUser(session_token);
                        System.out.println("session = "+session_token);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressBar.setVisibility(View.GONE);
                        pdAuth.dismiss();
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(getApplicationContext(), "Connexion échouée. \nProblème d'identifiants ou de connexion réseaux.",
                                Toast.LENGTH_SHORT).show();
                    }

                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s",username,password);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                params.put("App-Token",Constants.App_Token);
                return params;
            }
        };

        // add it to the RequestQueue
        queue.add(getRequest);

    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.connectButton:
                initSession();
                break;
        }
    }
}
