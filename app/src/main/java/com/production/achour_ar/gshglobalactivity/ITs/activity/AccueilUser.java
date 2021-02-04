package com.production.achour_ar.gshglobalactivity.ITs.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.Constants;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.UserModel;
import com.production.achour_ar.gshglobalactivity.ITs.dialog.DialogLogout;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.KeyValuePair;
import com.production.achour_ar.gshglobalactivity.ITs.manager.LoadProfilePic;
import com.production.achour_ar.gshglobalactivity.ITs.manager.MyPreferences;
import com.production.achour_ar.gshglobalactivity.R;
import com.production.achour_ar.gshglobalactivity.ITs.manager.URLGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.production.achour_ar.gshglobalactivity.ITs.data_model.Constants.GLPI_URL;

public class AccueilUser extends AppCompatActivity implements View.OnClickListener {

    private TextView welcomeView, headertitle, jobuserTV;
    private ImageView profilePicNav, profilePicHome;
    private CardView ticketCard, messagesCard, projectCard, rendementCard, interventionCard;
    private String session_token, idUser;
    static String nbCount;
    private RequestQueue queue;
    private DrawerLayout mDrawerLayout;
    public static Handler handler;
    private ProgressDialog pdlogout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private View headerView;
    private ArrayList<String> picsBG;
    private ImageView picBG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accueil_user);

        initView();
        getArguments();
        setListeners();
        setupToolbar();
        populateTVs();
        populateIVs();
        subscribeTopic();
        navigationListener();
        setImageBGRandomly();

    }


    private void subscribeTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("GSHITsMessage").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String msg = "DONE!";
                if (!task.isSuccessful()) {
                    msg = "FAILED...";
                }
                Log.d("TOPIC-GSHITsMessage", msg);
                //Toast.makeText(AccueilUser.this, msg, Toast.LENGTH_SHORT).show();
            }
        });


        String topic_notif = "tech_" + idUser;
        System.out.println("topic : " + topic_notif);
        FirebaseMessaging.getInstance().subscribeToTopic("tech_" + idUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String msg = "DONE!";
                if (!task.isSuccessful()) {
                    msg = "FAILED...";
                    System.out.println("subscribed to new notif");
                }
                Log.d("TOPIC-tech_id", msg);
                //Toast.makeText(AccueilUser.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateIVs() {
        String pic = UserModel.getCurrentUserModel().getPic();
        System.out.println("pic " + pic);
        if (pic != null)
            if (!pic.equals("null")) {
                Bitmap bitmap = decodeSampleBitmap(Base64.decode(pic, Base64.DEFAULT), 60, 60);
                profilePicHome.setImageBitmap(bitmap);
                profilePicNav.setImageBitmap(bitmap);
            }
    }

    public static Bitmap decodeSampleBitmap(byte[] bitmap, int reqHeight, int reqWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
        options.inSampleSize = calculateInSampleSize(options, reqHeight, reqWidth);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private void setImageBGRandomly() {
        picsBG = new ArrayList<>();
        picsBG.add("home01");
        picsBG.add("home02");
        picsBG.add("home03");
        picsBG.add("home04");
        picsBG.add("home05");
        picsBG.add("home06");
        picsBG.add("home07");
        picsBG.add("home08");
        picsBG.add("home09");
        picsBG.add("home10");
        picsBG.add("home11");
        picsBG.add("home12");
        picsBG.add("home13");
        picsBG.add("home14");


        int picId = getResources().getIdentifier(picsBG.get(new Random().nextInt(14)), "drawable", getPackageName());

        if (picId != 0) {
            picBG.setImageResource(picId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setImageBGRandomly();
    }


    private void setListeners() {
        /*ticketButton.setOnClickListener(this);
        projectButton.setOnClickListener(this);
        rendementButton.setOnClickListener(this);*/

        ticketCard.setOnClickListener(this);
        messagesCard.setOnClickListener(this);
        //projectCard.setOnClickListener(this);
        //rendementCard.setOnClickListener(this);
        //interventionCard.setOnClickListener(this);
    }

    private void navigationListener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setChecked(true);
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_setting:
                        startActivity(new Intent(AccueilUser.this, Setting.class));
                        break;

                    case R.id.nav_about:
                        startActivity(new Intent(AccueilUser.this, AboutActivity.class));
                        break;

                    case R.id.nav_logout:
                        DialogLogout alert = new DialogLogout();
                        alert.showDialog(AccueilUser.this, UserModel.getCurrentUserModel().getFirstname());
                        break;

                    case R.id.nav_profile:
                        Intent i = new Intent(AccueilUser.this, MyProfileActivity.class);
                        i.putExtra("session", session_token);
                        i.putExtra("id", idUser);

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

    private void populateTVs() {
        //String name = firstnameUser+" "+nameUser;
        String name = UserModel.getCurrentUserModel().getFullname();
        welcomeView.setText(name);
        headertitle.setText(name);

        String job = UserModel.getCurrentUserModel().getJobTitle();
        jobuserTV.setText(job);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbarDrawer);
        pdlogout = new ProgressDialog(AccueilUser.this);
        handler = new HandlerAccueil();
        queue = Volley.newRequestQueue(this);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        picBG = findViewById(R.id.picBGHome);

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
        messagesCard = findViewById(R.id.messagesCard);
        //projectCard = findViewById(R.id.projectCard);
        rendementCard = findViewById(R.id.rendementCard);
        //interventionCard = findViewById(R.id.interventionCard);
        pdlogout.setMessage("Déconnexion...");
    }


    private void setupToolbar() {
        toolbar.setTitle("Accueil");
        setSupportActionBar(toolbar);
    }


    private void getArguments() {
        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        idUser = i.getStringExtra("id");
        //nameUser = i.getStringExtra("nom");
        //firstnameUser = i.getStringExtra("prenom");
        //ad2000 = i.getStringExtra("ad2000");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ticketCard:
                Intent i = new Intent(getApplicationContext(), TabLayoutActivity.class);
                i.putExtra("session", session_token);
                i.putExtra("id", idUser);
               /* i.putExtra("nom",nameUser);
                i.putExtra("prenom",firstnameUser);
                i.putExtra("nb",nbCount);
                i.putExtra("email",emailUser);*/

                startActivity(i);
                break;

            case R.id.messagesCard:
                startActivity(new Intent(getApplicationContext(), MessageAct.class));
                break;

            /*case R.id.projectCard:
                break;

            case R.id.rendementCard:
                break;

            case R.id.interventionCard:
                break;*/


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


    private void killsession() {
        String url = GLPI_URL + "killSession";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        pdlogout.dismiss();
                        finish();
                        MyPreferences.deletePreference(Constants.KEY_USERNAME);
                        MyPreferences.deletePreference(Constants.KEY_PASSWORD);
                        Log.v("MyPref", "DELETED THE IDS RPEFS");

                        Toast.makeText(getApplicationContext(), "Déconnecté",
                                Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(getApplicationContext(), "Déconnexion impossible",
                                Toast.LENGTH_SHORT).show();
                    }

                }
        ) {
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
                params.put("App-Token", Constants.App_Token);
                params.put("Session-Token", session_token);
                return params;
            }
        };
        // add it to the RequestQueue
        queue.add(getRequest);

    }

    private void loadProfilePic() {
        Bitmap profilePic;

        String path = Constants.PROFILE_PIC_PATH;
        String picname = MyPreferences.getMyProfilPicName(this, Constants.PROFILE_PIC_NAME__KEY, Constants.PROFILE_PIC_NAME_DEF);

        if (picname.equals(Constants.PROFILE_PIC_NAME_DEF)) {
            //CASE : NO SHARED PREFERENCE (DOWNLOAD FROM SERVER)
            //temp case: put a default profile pic
            loadDefaultProfilePic();
        } else {
            // SHARED PREFERENCE EXISTS
            File picToLoad = new File(path + "/" + picname);
            if (picToLoad.exists()) {
                // PIC EXISTS
                Log.d("DOES_PIC_EXIST", "YES IT EXISTS !");
                profilePic = LoadProfilePic.loadImageFromStorage(path, picname);
                profilePicNav.setImageBitmap(profilePic);
                profilePicHome.setImageBitmap(profilePic);

            } else {
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

        if (picname.equals(Constants.PROFILE_PIC_NAME_DEF)) {
            //CASE : NO SHARED PREFERENCE (DOWNLOAD FROM SERVER)
            //temp case: put a default profile pic
            loadDefaultProfilePicHome();
        } else {
            // SHARED PREFERENCE EXISTS
            File picToLoad = new File(path + "/" + picname);
            if (picToLoad.exists()) {
                // PIC EXISTS
                Log.d("DOES_PIC_EXIST", "YES IT EXISTS !");
                profilePic = LoadProfilePic.loadImageFromStorage(path, picname);
                profilePicHome.setImageBitmap(profilePic);
            } else {
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
