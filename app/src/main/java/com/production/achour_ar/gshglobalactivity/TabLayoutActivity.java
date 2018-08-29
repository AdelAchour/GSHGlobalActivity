package com.production.achour_ar.gshglobalactivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class TabLayoutActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    String session_token, nameUser, idUser, firstnameUser;
    public static Handler handler;
    ProgressDialog pd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);

        pd = new ProgressDialog(TabLayoutActivity.this);
        pd.setMessage("Chargement des tickets...");

        handler = new HandlerTab();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); //show a caret even if android:parentActivityName is not specified.
        //actionBar.setDisplayShowHomeEnabled(true); //just controls whether to show the Activity icon/logo or not.
        //actionBar.setHomeButtonEnabled(true); //show a caret only if android:parentActivityName is specified.
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("Tickets");
        //actionBar.setIcon(R.drawable.call); //set the Activity icon/logo

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.setupactionbar, null);

        actionBar.setCustomView(v);

        ImageView homebutton = (ImageView) findViewById(R.id.homeiconID);
        ImageView refreshbutton = (ImageView) findViewById(R.id.refreshIconID);
        homebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FirstEverActivity.class));
                finish();
            }
        });


        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        idUser = i.getStringExtra("id");

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        pd.show();


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putString("session", session_token);
        bundle.putString("nom", nameUser);
        bundle.putString("prenom", firstnameUser);
        bundle.putString("id", idUser);

        ListTickets listTickets = new ListTickets();
        ListTicketsClos listTicketsClos = new ListTicketsClos();
        ListTicketsResolu listTicketsResolu = new ListTicketsResolu();

        listTickets.setArguments(bundle);
        listTicketsClos.setArguments(bundle);
        listTicketsResolu.setArguments(bundle);

        viewPagerAdapter.addFragment(listTickets, "En cours");
        viewPagerAdapter.addFragment(listTicketsClos, "Clos");
        viewPagerAdapter.addFragment(listTicketsResolu, "Résolu");
        viewPager.setAdapter(viewPagerAdapter);

    }


    private class HandlerTab extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (pd.isShowing()){
                        pd.dismiss();
                    }
                    break;
            }
        }
    }
}
