package com.production.achour_ar.gshglobalactivity.ITs.activity;

import android.graphics.Color;
import android.os.Build;

import androidx.appcompat.app.ActionBar;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.KeyValuePair;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.UserModel;
import com.production.achour_ar.gshglobalactivity.ITs.manager.MyPreferences;
import com.production.achour_ar.gshglobalactivity.R;
import com.production.achour_ar.gshglobalactivity.ITs.manager.URLGenerator;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckAuth extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CheckAuth";
    private String username;
    private String password;
    private ProgressDialog pdAuth;
    private String session_token;
    private RequestQueue queue;
    private TextView noConnection;
    private Button retryConnection;
    private Button goLogin;
    private String nameUser, idUser, firstnameUser;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_auth);

        /*if (Build.VERSION.SDK_INT < 24){
            NukeSSLCerts.nuke();
        }*/

        initView();
        setListener();
        setupActionBar();
        setupPDs();
        checkAutoAuth();

    }

    private void showPD() {
        pdAuth.show();
    }

    private void setListener() {
        retryConnection.setOnClickListener(this);
        goLogin.setOnClickListener(this);
    }

    private void setupPDs() {
        pdAuth.setMessage("Récupération des identifiants...");
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    private void initView() {
        retryConnection = findViewById(R.id.retry_button);
        goLogin = findViewById(R.id.goto_login);
        noConnection = findViewById(R.id.no_connection_tv);
        progressBar = findViewById(R.id.progressBar);
        queue = Volley.newRequestQueue(this);
        pdAuth = new ProgressDialog(CheckAuth.this);
    }

    private void checkAutoAuth() {
        //showPD();
        Log.d("CheckAuth", "Checking the auto auth...");
        noConnection.setText("Authentification en cours...");
        username = MyPreferences.getMyString(this, Constants.KEY_USERNAME, Constants.USERNAME_DEF_VALUE);
        password = MyPreferences.getMyString(this, Constants.KEY_PASSWORD, Constants.PASSWORD_DEF_VALUE);

        if ((username.equals(Constants.USERNAME_DEF_VALUE)) || (password.equals(Constants.PASSWORD_DEF_VALUE))) { //no auto auth
            pdAuth.dismiss();
            Log.d("CheckAuth", "No auto auth.");
            startActivity(new Intent(CheckAuth.this, LoginActivity.class));
        } else { //got the ids successfully
            Log.d("CheckAuth", "Got the IDs successfully - " + username);
            Login(username, password);
        }

    }

    private void Login(final String username, final String password) {
        //pdAuth.setMessage("Authentification...");
        String url = Constants.GLPI_URL + "initSession";
        List<KeyValuePair> params = new ArrayList<>();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URLGenerator.generateUrl(url, params), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    session_token = response.getString("session_token");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getUser(session_token);
                System.out.println("session : " + session_token);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdAuth.dismiss();
                Log.e("Error.Response", error.toString());
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Connexion échouée. \nProblème de connexion réseaux.", Toast.LENGTH_LONG).show();
                connectionError();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", username, password);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                params.put("App-Token", Constants.App_Token);
                return params;
            }
        };

        // add it to the RequestQueue
        queue.add(getRequest);
    }

    private void connectionSuccess() {
        //noConnection.setText(R.string.connection_success);
        noConnection.setText("Authentifié avec succès!");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            noConnection.setTextColor(getColor(R.color.green_notif));
        } else {
            noConnection.setTextColor(Color.parseColor("#3b8d26"));
        }
    }

    private void connectionError() {
        noConnection.setText(R.string.no_connection);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            noConnection.setTextColor(getColor(R.color.red_notif));
        } else {
            noConnection.setTextColor(Color.parseColor("#ae2424"));
        }
    }

    private void getUser(final String Token_Session) {
        String url = Constants.GLPI_URL + "getFullSession";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject sessionObj = response.getJSONObject("session");
                    nameUser = sessionObj.getString("glpirealname");
                    firstnameUser = sessionObj.getString("glpifirstname");
                    idUser = sessionObj.getString("glpiID");
                    System.out.println("Nom & ID: " + nameUser + idUser);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                getLdapUserInfo();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //pdAuth.dismiss();
                progressBar.setVisibility(View.GONE);
                Log.e("Error.Response", error.toString());
                Toast.makeText(getApplicationContext(), "Connexion échouée. \nProblème de connexion réseaux.", Toast.LENGTH_LONG).show();
            }
        }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("App-Token", Constants.App_Token);
                params.put("Session-Token", Token_Session);

                return params;
            }
        };

        queue.add(getRequest);

    }

    private void getLdapUserInfo() {
        RequestQueue requestQueue = Volley.newRequestQueue(CheckAuth.this);
        List<KeyValuePair> params = new ArrayList<>();
        String url = Constants.API_URL_LOGIN;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLGenerator.generateUrl(url, params), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse - getLdap : " + response);
                try {
                    if (response.getBoolean("authenticated")) {
                        connectionSuccess();

                        JSONObject userinfo = response.getJSONObject("userinfo");
                        String fullname = userinfo.getString("fullname");
                        String fname = userinfo.getString("fname");
                        String name = userinfo.getString("name");
                        String company = userinfo.getString("company");
                        String department = userinfo.getString("department");
                        String job = userinfo.getString("title");
                        String ad2000 = userinfo.getString("ad2000");
                        String pic = userinfo.getString("photo");
                        String email = userinfo.getString("mail");
                        String phone = userinfo.getString("phonenumber");

                        UserModel userModel = new UserModel(fullname, fname, name, company, department, job, ad2000, pic, email, phone);
                        UserModel.setCurrentUser(userModel);

                        System.out.println("userinfo : " + userModel);
                        goToHome();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "onErrorResponse: ", error.getMessage());
                Toast.makeText(CheckAuth.this, "Vérifiez votre connexion internet", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", username, password);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                params.put("App-Token", Constants.App_Token);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void goToHome() {
        pdAuth.dismiss();
        Intent i = new Intent(getApplicationContext(), AccueilUser.class);
        i.putExtra("nom", nameUser);
        i.putExtra("prenom", firstnameUser);
        i.putExtra("id", idUser);
        i.putExtra("session", session_token);
        startActivity(i);
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_button:
                checkAutoAuth();
                break;
            case R.id.goto_login:
                startActivity(new Intent(CheckAuth.this, LoginActivity.class));
                finish();
        }
    }
}
