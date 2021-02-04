package com.production.achour_ar.gshglobalactivity.ITs.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.production.achour_ar.gshglobalactivity.ITs.data_model.UserModel;
import com.production.achour_ar.gshglobalactivity.R;
import com.production.achour_ar.gshglobalactivity.ITs.adapter.ViewPagerAdapter;
import com.production.achour_ar.gshglobalactivity.ITs.fragment.ListTicketBackLog;
import com.production.achour_ar.gshglobalactivity.ITs.fragment.ListTickets;
import com.production.achour_ar.gshglobalactivity.ITs.fragment.ListTicketsAttente;
import com.production.achour_ar.gshglobalactivity.ITs.fragment.ListTicketsClos;
import com.production.achour_ar.gshglobalactivity.ITs.fragment.ListTicketsResolu;

import java.util.Timer;
import java.util.TimerTask;

public class TabLayoutActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private String session_token, nameUser, idUser, firstnameUser;
    private int range, timeLoad;
    public static Handler handler;
    private ProgressDialog pd;
    private SharedPreferences app_preferences;
    private Timer timer = new Timer();
    private ImageView homebutton;
    private ImageView refreshbutton;
    private String emailUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);

        initOtherViews();
        SetupActionBar();
        initView();
        SetupPD();
        getArguments();
        getPrefs();
        SetupViewPagerTabLayout();
        TabSwitchListener();
        //setListener();
        //LaunchAutoTimerLoad();
        ShowPD();

    }

    private void ShowPD() {
        pd.show();
    }

    private void setListener() {
        refreshbutton.setOnClickListener(this);
        homebutton.setOnClickListener(this);
    }

    private void TabSwitchListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                System.out.println("Tab " + position + " selected ");
                switch (position) {
                    case 0: //ListTicket
                        ListTickets.handlerticket.sendEmptyMessage(0); //stp vérifie si la listview est vide et dabar rassek
                        break;

                    case 1: //ListTicketClos
                        ListTicketsClos.handlerticketClos.sendEmptyMessage(0); //stp vérifie si la listview est vide et dabar rassek
                        break;

                    case 2: //ListTicketRésolu
                        ListTicketsResolu.handlerticketResolu.sendEmptyMessage(0); //stp vérifie si la listview est vide et dabar rassek
                        break;

                    case 3: //ListTicketAttente
                        ListTicketsAttente.handlerticketAttente.sendEmptyMessage(0); //stp vérifie si la listview est vide et dabar rassek
                        break;

                    case 4: //ListTicketBackLog
                        ListTicketBackLog.handlerticketbackLog.sendEmptyMessage(0); //stp vérifie si la listview est vide et dabar rassek
                        break;


                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void LaunchAutoTimerLoad() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int position = tabLayout.getSelectedTabPosition();

                switch (position) {
                    case 0: //en cours
                        ListTickets.handlerticket.sendEmptyMessage(3);
                        break;
                    case 1: //clos
                        ListTicketsClos.handlerticketClos.sendEmptyMessage(3);
                        break;
                    case 2: //résolu
                        ListTicketsResolu.handlerticketResolu.sendEmptyMessage(3);
                        break;
                    case 3: //en attente
                        ListTicketsAttente.handlerticketAttente.sendEmptyMessage(3);
                        break;
                    case 4: //backlog
                        ListTicketBackLog.handlerticketbackLog.sendEmptyMessage(3);
                        break;
                }

            }
        }, 10000, timeLoad * 60 * 1000);
        Log.d("TIME LOAD", "" + timeLoad);
    }

    private void SetupViewPagerTabLayout() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void getPrefs() {
        range = app_preferences.getInt("range", 30);
        timeLoad = app_preferences.getInt("load", 3);
    }

    private void getArguments() {
        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        idUser = i.getStringExtra("id");
        /*nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        emailUser = i.getStringExtra("email");*/
    }

    private void SetupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); //show a caret even if android:parentActivityName is not specified.
        actionBar.setTitle("Tickets");

        //actionBar.setDisplayShowHomeEnabled(true); //just controls whether to show the Activity icon/logo or not.
        //actionBar.setHomeButtonEnabled(true); //show a caret only if android:parentActivityName is specified.
        //actionBar.setDisplayShowCustomEnabled(true);
        //actionBar.setDisplayShowTitleEnabled(false);
        //actionBar.setIcon(R.drawable.call); //set the Activity icon/logo
        //LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View v = inflator.inflate(R.layout.setupactionbar, null);
        //actionBar.setCustomView(v);
    }

    private void SetupPD() {
        pd.setMessage("Chargement des tickets...");
    }

    private void initOtherViews() {
        new ListTickets();
        new ListTicketsClos();
        new ListTicketsResolu();
        new ListTicketsAttente();
        new ListTicketBackLog();
    }

    private void initView() {
        //homebutton = findViewById(R.id.homeiconID);
        //refreshbutton = findViewById(R.id.refreshIconID);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabs);
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        handler = new HandlerTab();
        pd = new ProgressDialog(TabLayoutActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("\n\n\nOn est parti, stop timer\n\n\n");
        timer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("\n\n\nOn a stoppé, stop timer\n\n\n");
        timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("\n\n\nOn est revenu\n\n\n");
        /*timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                System.out.println("3 sec in onResume");
            }
        }, 0, 3000);*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("On a recommencé");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("3 sec in onRestart");

                int position = tabLayout.getSelectedTabPosition();

                switch (position) {
                    case 0: //en cours
                        ListTickets.handlerticket.sendEmptyMessage(3);
                        break;
                    case 1: //clos
                        ListTicketsClos.handlerticketClos.sendEmptyMessage(3);
                        break;
                    case 2: //résolu
                        ListTicketsResolu.handlerticketResolu.sendEmptyMessage(3);
                        break;
                    case 3: //en attente
                        ListTicketsAttente.handlerticketAttente.sendEmptyMessage(3);
                        break;
                    case 4: //backlog
                        ListTicketBackLog.handlerticketbackLog.sendEmptyMessage(3);
                        break;
                }
            }
        }, 30000, timeLoad * 60 * 1000);
        Log.d("TIME LOAD", "" + timeLoad);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.itemStatTicket:
                intent = new Intent(getApplicationContext(), StatsTickets.class);
                intent.putExtra("session", session_token);
                intent.putExtra("id", idUser);
                /*intent.putExtra("nom",nameUser);
                intent.putExtra("prenom",firstnameUser);
                intent.putExtra("email",emailUser);*/
                startActivity(intent);
                break;

            /*case R.id.itemCreationTicket:
                intent = new Intent(getApplicationContext(), CreationTicket.class);
                intent.putExtra("session",session_token);
                intent.putExtra("nom",nameUser);
                intent.putExtra("prenom",firstnameUser);
                intent.putExtra("id",idUser);
                startActivity(intent);
                break;*/

            case R.id.itemRechercheTicket:
                intent = new Intent(getApplicationContext(), SearchTicket.class);
                intent.putExtra("session", session_token);
                intent.putExtra("id", idUser);
                //intent.putExtra("nom",nameUser);
                //intent.putExtra("prenom",firstnameUser);
                startActivity(intent);
                break;

            case R.id.itemUpdateTicket:
                refreshCurrentFragmentsTicket();
                break;

            case R.id.itemGoHome:
                finish();
                break;


        }

        /*if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putString("session", session_token);
        bundle.putString("nom", UserModel.getCurrentUserModel().getLastname());
        bundle.putString("prenom", UserModel.getCurrentUserModel().getFirstname());
        bundle.putString("id", idUser);
        bundle.putInt("range", range);

        ListTickets listTickets = new ListTickets();
        ListTicketsClos listTicketsClos = new ListTicketsClos();
        ListTicketsResolu listTicketsResolu = new ListTicketsResolu();
        ListTicketsAttente listTicketsAttente = new ListTicketsAttente();
        ListTicketBackLog listTicketsBackLog = new ListTicketBackLog();

        listTickets.setArguments(bundle);
        listTicketsClos.setArguments(bundle);
        listTicketsResolu.setArguments(bundle);
        listTicketsAttente.setArguments(bundle);
        listTicketsBackLog.setArguments(bundle);

        viewPagerAdapter.addFragment(listTickets, "En cours");
        viewPagerAdapter.addFragment(listTicketsClos, "Clos");
        viewPagerAdapter.addFragment(listTicketsResolu, "Résolu");
        viewPagerAdapter.addFragment(listTicketsAttente, "En attente");
        viewPagerAdapter.addFragment(listTicketsBackLog, "BackLog");
        viewPager.setAdapter(viewPagerAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
          /*  case R.id.homeiconID:
                finish();
                break;
            case R.id.refreshIconID:
                refreshCurrentFragmentsTicket();
                break;*/
        }
    }

    private void refreshCurrentFragmentsTicket() {
        int position = tabLayout.getSelectedTabPosition();

        switch (position) {
            case 0: //en cours
                ListTickets.handlerticket.sendEmptyMessage(3);
                break;
            case 1: //clos
                ListTicketsClos.handlerticketClos.sendEmptyMessage(3);
                break;
            case 2: //résolu
                ListTicketsResolu.handlerticketResolu.sendEmptyMessage(3);
                break;
            case 3: //en attente
                ListTicketsAttente.handlerticketAttente.sendEmptyMessage(3);
                break;
            case 4: //backlog
                ListTicketBackLog.handlerticketbackLog.sendEmptyMessage(3);
                break;
        }
    }


    private class HandlerTab extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    break;

                case 1:
                    Bundle bundle = msg.getData();
                    int position = Integer.valueOf(bundle.getString("position")); //2
                    String count = bundle.getString("count"); //15
                    String title = bundle.getString("title"); //Résolu

                    tabLayout.getTabAt(position).setText(title + " (" + count + ")");
                    break;

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menutablayout, menu);

        return true;
    }


}
