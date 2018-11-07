package com.production.achour_ar.gshglobalactivity;

import android.support.v7.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckAuth extends AppCompatActivity {

    private String username;
    private String password;
    private ProgressDialog pdAuth;
    private String session_token;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_auth);

        initView();
        setupActionBar();
        setupPDs();
        checkAutoAuth();

    }

    private void setupPDs() {
        pdAuth.setMessage("Récupération des identifiants...");
        pdAuth.show();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    private void initView() {
        queue = Volley.newRequestQueue(this);
        pdAuth = new ProgressDialog(CheckAuth.this);
    }

    private void checkAutoAuth() {
        username = MyPreferences.getMyString(this, Constants.KEY_USERNAME, Constants.USERNAME_DEF_VALUE);
        password = MyPreferences.getMyString(this, Constants.KEY_PASSWORD, Constants.PASSWORD_DEF_VALUE);

        if ((username.equals(Constants.USERNAME_DEF_VALUE))||(password.equals(Constants.PASSWORD_DEF_VALUE))){ //no auto auth
            pdAuth.dismiss();
            startActivity(new Intent(CheckAuth.this, FirstEverActivity.class));
        }
        else{ //got the ids successfully
            Login(username, password);
        }

    }

    private void Login(String username, String password) {
        pdAuth.setMessage("Authentification...");

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
                        Toast.makeText(getApplicationContext(), "Connexion échouée. \nProblème de connexion réseaux.",
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

                        try {
                            JSONObject sessionObj = response.getJSONObject("session");
                            nameUser = sessionObj.getString("glpirealname");
                            firstnameUser = sessionObj.getString("glpifirstname");
                            idUser = sessionObj.getString("glpiID");
                            System.out.println("Nom & ID: "+nameUser+idUser);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //UIRedirection(idUser);
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
}
