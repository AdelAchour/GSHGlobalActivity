package com.production.achour_ar.gshglobalactivity.Users;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.android.volley.toolbox.Volley;
import com.production.achour_ar.gshglobalactivity.ITs.activity.AboutActivity;
import com.production.achour_ar.gshglobalactivity.ITs.activity.FirstEverActivity;
import com.production.achour_ar.gshglobalactivity.ITs.activity.MyProfileActivity;
import com.production.achour_ar.gshglobalactivity.ITs.activity.Setting;
import com.production.achour_ar.gshglobalactivity.ITs.activity.TabLayoutActivity;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.Constants;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.KeyValuePair;
import com.production.achour_ar.gshglobalactivity.ITs.dialog.DialogLogout;
import com.production.achour_ar.gshglobalactivity.ITs.manager.LoadProfilePic;
import com.production.achour_ar.gshglobalactivity.ITs.manager.MyPreferences;
import com.production.achour_ar.gshglobalactivity.ITs.manager.ServiceNotificationNewTicket;
import com.production.achour_ar.gshglobalactivity.ITs.manager.URLGenerator;
import com.production.achour_ar.gshglobalactivity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.production.achour_ar.gshglobalactivity.ITs.data_model.Constants.GLPI_URL;

public class HomeUser extends AppCompatActivity implements View.OnClickListener {

    private TextView welcomeView, headertitle, jobuserTV;
    private ImageView profilePicNav, profilePicHome;
    private Button ticketButton, projectButton, rendementButton;
    private CardView ticketCard, projectCard, rendementCard, interventionCard;
    private String session_token, nameUser, idUser, firstnameUser;
    static String nbCount ;
    private RequestQueue queue;
    private DrawerLayout mDrawerLayout;
    public static Handler handler;
    private ProgressDialog pdlogout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private View headerView;
    private String emailUser;
    private String telephoneUser;
    private String lieuUser;
    private String posteUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_user);

        initView();
        getArguments();
        serviceNotificationManagement();
        setupToolbar();
        setupPDs();
        setupTVs();
        setListeners();
        setupButtons();
        navigationListener();
        loadProfilePic();
        loadProfilePicHome();
        getInfoUser();
        getTicketsByTechnicien(idUser);

    }

    private void getInfoUser() {
        String url = Constants.GLPI_URL+"search/User";

        List<KeyValuePair> paramsObs = new ArrayList<>();
        paramsObs.add(new KeyValuePair("criteria[0][field]","2"));
        paramsObs.add(new KeyValuePair("criteria[0][searchtype]","equals"));
        paramsObs.add(new KeyValuePair("criteria[0][value]",idUser));
        paramsObs.add(new KeyValuePair("forcedisplay[0]","9"));
        paramsObs.add(new KeyValuePair("forcedisplay[1]","34"));
        paramsObs.add(new KeyValuePair("forcedisplay[2]","5"));
        paramsObs.add(new KeyValuePair("forcedisplay[3]","6"));
        paramsObs.add(new KeyValuePair("forcedisplay[4]","81"));


        final JsonObjectRequest getRequestDemandeur = new JsonObjectRequest(Request.Method.GET, URLGenerator.generateUrl(url, paramsObs), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray Jdata = response.getJSONArray("data");
                            try {
                                JSONObject userInfo = Jdata.getJSONObject(0);

                                emailUser = userInfo.getString("5");
                                telephoneUser = userInfo.getString("6");
                                lieuUser = userInfo.getString("80");
                                posteUser = userInfo.getString("81");


                            } catch (JSONException e) {
                                Log.e("Error JSONArray : ", e.getMessage());
                            }

                        } catch (JSONException e) {
                            Log.e("JSON Error response",e.getMessage());
                        }

                        jobuserTV.setText(posteUser);
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

    private void setupButtons() {
        //projectButton.setEnabled(false);
        //rendementButton.setEnabled(false);
    }

    private void setListeners() {
        /*ticketButton.setOnClickListener(this);
        projectButton.setOnClickListener(this);
        rendementButton.setOnClickListener(this);*/

        ticketCard.setOnClickListener(this);
        projectCard.setOnClickListener(this);
        rendementCard.setOnClickListener(this);
        interventionCard.setOnClickListener(this);
    }

    private void navigationListener() {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        int id = menuItem.getItemId();
                        switch (id){
                            case R.id.nav_setting:
                                startActivity(new Intent(HomeUser.this, Setting.class));
                                break;

                            case R.id.nav_about:
                                startActivity(new Intent(HomeUser.this, AboutActivity.class));
                                break;

                            case R.id.nav_logout:
                                DialogLogout alert = new DialogLogout();
                                alert.showDialog(HomeUser.this, firstnameUser);
                                break;

                            case R.id.nav_profile:
                                Intent i = new Intent(HomeUser.this, MyProfileActivity.class);
                                i.putExtra("session",session_token);
                                i.putExtra("nom",nameUser);
                                i.putExtra("prenom",firstnameUser);
                                i.putExtra("id",idUser);

                                i.putExtra(Constants.EMAIL_USER,emailUser);
                                i.putExtra(Constants.TEL_USER,telephoneUser);
                                i.putExtra(Constants.LIEU_USER,lieuUser);
                                i.putExtra(Constants.POSTE_USER,posteUser);

                                startActivity(i);
                                break;
                        }

                        mDrawerLayout.closeDrawers();
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        return true;
                    }
                });
    }

    private void setupTVs() {
        String name = firstnameUser+" "+nameUser;
        welcomeView.setText(name);
        headertitle.setText(name);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbarDrawer);
        pdlogout = new ProgressDialog(HomeUser.this);
        handler = new HandlerAccueil();
        queue = Volley.newRequestQueue(this);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        headertitle = headerView.findViewById(R.id.header_nav_ID);
        profilePicNav = headerView.findViewById(R.id.profilePicNav);

        profilePicHome = findViewById(R.id.imageviewbgprofile);

        welcomeView = findViewById(R.id.welcomeTextView);
        jobuserTV = findViewById(R.id.jobuserTV);

        /*ticketButton = findViewById(R.id.ticketButton);
        rendementButton = findViewById(R.id.rendementButton);
        projectButton = findViewById(R.id.projectButton);*/

        ticketCard = findViewById(R.id.ticketCard);
        projectCard = findViewById(R.id.projectCard);
        rendementCard = findViewById(R.id.rendementCard);
        interventionCard = findViewById(R.id.interventionCard);
    }

    private void setupPDs() {
        pdlogout.setMessage("Déconnexion...");
    }

    private void setupToolbar() {
        toolbar.setTitle("Accueil");
        setSupportActionBar(toolbar);
    }

    private void serviceNotificationManagement() {
        if(ServiceNotificationNewTicket.ServiceIsRunning == false ) {
            System.out.println("Service not running");
            ServiceNotificationNewTicket.ServiceIsRunning = true ;
            //register the services to run in background
            Intent intent = new Intent(HomeUser.this, ServiceNotificationNewTicket.class);
            intent.putExtra("id",idUser);
            intent.putExtra("session",session_token);
            // start the services
            startService(intent);
            System.out.println("Service started with id = "+idUser);
        }
    }

    private void getArguments() {
        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        idUser = i.getStringExtra("id");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ticketCard:
                Intent i = new Intent(getApplicationContext(), TabLayoutActivity.class);
                i.putExtra("session",session_token);
                i.putExtra("nom",nameUser);
                i.putExtra("prenom",firstnameUser);
                i.putExtra("id",idUser);
                i.putExtra("nb",nbCount);

                startActivity(i);
                break;

            case R.id.projectCard:
                break;

            case R.id.rendementCard:
                break;

            case R.id.interventionCard:
                break;



        }
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

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URLGenerator.generateUrl(url, params), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            nbCount = response.getString("totalcount");
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
                params.put("App-Token",Constants.App_Token);
                params.put("Session-Token",session_token);
                return params;
            }
        };

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
                        MyPreferences.deletePreference(Constants.KEY_USERNAME);
                        MyPreferences.deletePreference(Constants.KEY_PASSWORD);
                        Log.v("MyPref", "DELETED THE IDS RPEFS");

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
        Intent intent = new Intent(HomeUser.this, ServiceNotificationNewTicket.class);
        // stop the services
        stopService(intent);
        System.out.println("Service stopped");

    }

    private void loadProfilePic() {
        Bitmap profilePic;

        String path = Constants.PROFILE_PIC_PATH;
        String picname = MyPreferences.getMyProfilPicName(this, Constants.PROFILE_PIC_NAME__KEY, Constants.PROFILE_PIC_NAME_DEF);

        if (picname.equals(Constants.PROFILE_PIC_NAME_DEF)){
            //CASE : NO SHARED PREFERENCE (DOWNLOAD FROM SERVER)
            //temp case: put a default profile pic
            loadDefaultProfilePic();
        }
        else {
            // SHARED PREFERENCE EXISTS
            File picToLoad = new File(path+"/"+picname);
            if (picToLoad.exists()){
                // PIC EXISTS
                Log.d("DOES_PIC_EXIST", "YES IT EXISTS !");
                profilePic = LoadProfilePic.loadImageFromStorage(path,picname);
                profilePicNav.setImageBitmap(profilePic);
                profilePicHome.setImageBitmap(profilePic);

            }
            else {
                //PIC DOES NOT EXIST
                Log.e("DOES_PIC_EXIST", "NO IT DOES NOT EXIST !");
                File folderPics = new File(Environment.getExternalStorageDirectory(), "FourStarsPics");
                if (!folderPics.exists()) {
                    if (folderPics.mkdirs()) {
                        Log.d("FOLDER_PIC", "DIRECTORY CREATED SUCCESSFULLY !");
                    }
                }
                //download from server and put it in the folder
                //temp case: put a default profile pic
                loadDefaultProfilePic();

            }

        }

    }

    private void loadProfilePicHome() {
        Bitmap profilePic;

        String path = Constants.PROFILE_PIC_PATH;
        String picname = MyPreferences.getMyProfilPicName(this, Constants.PROFILE_PIC_NAME__KEY, Constants.PROFILE_PIC_NAME_DEF);

        if (picname.equals(Constants.PROFILE_PIC_NAME_DEF)){
            //CASE : NO SHARED PREFERENCE (DOWNLOAD FROM SERVER)
            //temp case: put a default profile pic
            loadDefaultProfilePicHome();
        }
        else {
            // SHARED PREFERENCE EXISTS
            File picToLoad = new File(path+"/"+picname);
            if (picToLoad.exists()){
                // PIC EXISTS
                Log.d("DOES_PIC_EXIST", "YES IT EXISTS !");
                profilePic = LoadProfilePic.loadImageFromStorage(path,picname);
                profilePicHome.setImageBitmap(profilePic);
            }
            else {
                //PIC DOES NOT EXIST
                Log.e("DOES_PIC_EXIST", "NO IT DOES NOT EXIST !");
                File folderPics = new File(Environment.getExternalStorageDirectory(), "FourStarsPics");
                if (!folderPics.exists()) {
                    if (folderPics.mkdirs()) {
                        Log.d("FOLDER_PIC", "DIRECTORY CREATED SUCCESSFULLY !");
                    }
                }
                //download from server and put it in the folder
                //temp case: put a default profile pic
                loadDefaultProfilePicHome();

            }

        }

    }

    private void loadDefaultProfilePic() {
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.man);
        profilePicNav.setImageBitmap(icon);
    }

    private void loadDefaultProfilePicHome() {
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.man);
        profilePicHome.setImageBitmap(icon);
    }


    private class HandlerAccueil extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: //logout
                    pdlogout.show();
                    killsession();
                    break;

                case Constants.UPDATE_PROFILE_PIC_NAV_HEADER: //logout
                    loadProfilePic();
                    break;

                case Constants.UPDATE_DEFAULT_PROFILE_PIC_NAV_HEADER: //logout
                    loadDefaultProfilePic();
                    break;
            }
        }
    }



}
