package com.production.achour_ar.gshglobalactivity.ITs.activity;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private EditText usernameEdit, passwordEdit;
    //private ProgressDialog pdAuth;
    private ProgressBar progressBar;
    private RequestQueue queue;
    private Button connect;
    private String session_token;
    private String nameUser, firstnameUser, idUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_act);


        /*if (Build.VERSION.SDK_INT < 24){
            NukeSSLCerts.nuke();
        }*/

        initView();
        setupActionBar();
        //setupProgressD();
        setListener();

    }


    private void setListener() {
        connect.setOnClickListener(this);
    }

    /*private void setupProgressD() {
        pdAuth.setMessage("Authentification");
    }*/

    private void setupActionBar() {
        getSupportActionBar().hide();
    }

    private void initView() {
        queue = Volley.newRequestQueue(this);
        usernameEdit = findViewById(R.id.usernameText);
        passwordEdit = findViewById(R.id.passwordText);
        connect = findViewById(R.id.connectButton);
        progressBar = findViewById(R.id.progressBar);
        //pdAuth = new ProgressDialog(LoginActivity.this);
    }


    private void initSession() {
        final String username = usernameEdit.getText().toString();
        final String password = passwordEdit.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        //pdAuth.show();

        String url = Constants.GLPI_URL + "initSession";
        List<KeyValuePair> params = new ArrayList<>();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URLGenerator.generateUrl(url, params), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Log.d("Response = ", response.toString());
                        try {
                            session_token = response.getString("session_token");
                            MyPreferences.SaveString(Constants.KEY_USERNAME, username);
                            MyPreferences.SaveString(Constants.KEY_PASSWORD, password);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        System.out.println("Session : " + session_token);
                        getUser(session_token);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //pdAuth.dismiss();
                        progressBar.setVisibility(View.GONE);
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(getApplicationContext(), "Connexion échouée. \nProblème d'identifiants ou de connexion réseaux.",
                                Toast.LENGTH_LONG).show();
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


        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // add it to the RequestQueue
        queue.add(getRequest);

    }


    private void getUser(final String token_session) {
        String url = Constants.GLPI_URL + "getFullSession";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response User : ", response.toString());
                        try {
                            JSONObject sessionObj = response.getJSONObject("session");
                            nameUser = sessionObj.getString("glpirealname");
                            firstnameUser = sessionObj.getString("glpifirstname");
                            idUser = sessionObj.getString("glpiID");
                            System.out.println("Nom & ID: " + nameUser + " - " + idUser);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //pdAuth.dismiss();
                        progressBar.setVisibility(View.GONE);
                        getLdapUserInfo();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //pdAuth.dismiss();
                        progressBar.setVisibility(View.GONE);
                        Log.e("Error.Response", error.toString());
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("App-Token", Constants.App_Token);
                params.put("Session-Token", token_session);

                return params;
            }
        };

        queue.add(getRequest);
    }


    private void getLdapUserInfo() {
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        String url = Constants.API_URL_LOGIN;
        List<KeyValuePair> params = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLGenerator.generateUrl(url, params), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse - getLdap : " + response);
                try {
                    if (response.getBoolean("authenticated")) {

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
                Toast.makeText(LoginActivity.this, "Vérifiez votre connexion internet", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", usernameEdit.getText().toString(), passwordEdit.getText().toString());
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                params.put("App-Token", Constants.App_Token);
                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private void goToHome() {
        //pdAuth.dismiss();
        progressBar.setVisibility(View.GONE);
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
            case R.id.connectButton:
                initSession();
                break;
        }
    }
}
