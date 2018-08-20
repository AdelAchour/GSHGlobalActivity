package com.production.achour_ar.gshglobalactivity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class TabLayoutActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    String session_token, nameUser, idUser, firstnameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);

        getSupportActionBar().setTitle("Liste des tickets");
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        idUser = i.getStringExtra("id");

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putString("session", session_token);
        bundle.putString("nom", nameUser);
        bundle.putString("prenom", firstnameUser);
        bundle.putString("id", idUser);

        ListTickets listTickets = new ListTickets();
        ListTicketsClos listTicketsClos  = new ListTicketsClos();
        ListTicketsResolu listTicketsResolu  = new ListTicketsResolu();
        listTickets.setArguments(bundle);
        listTicketsClos.setArguments(bundle);
        listTicketsResolu.setArguments(bundle);

        viewPagerAdapter.addFragment(listTickets, "En cours");
        viewPagerAdapter.addFragment(listTicketsClos, "Clos");
        viewPagerAdapter.addFragment(listTicketsResolu, "Résolu");
        viewPager.setAdapter(viewPagerAdapter);
    }

}
