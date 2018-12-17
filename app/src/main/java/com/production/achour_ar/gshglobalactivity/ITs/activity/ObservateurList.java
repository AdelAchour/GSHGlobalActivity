package com.production.achour_ar.gshglobalactivity.ITs.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.production.achour_ar.gshglobalactivity.ITs.data_model.ObservateurModel;
import com.production.achour_ar.gshglobalactivity.R;
import com.production.achour_ar.gshglobalactivity.ITs.adapter.ObservateurAdapter;
import com.production.achour_ar.gshglobalactivity.ITs.dialog.DialogDemandeur;

import java.util.ArrayList;

public class ObservateurList extends AppCompatActivity {

    private ListView list;
    private ArrayList<ObservateurModel> observateurModels;
    private ArrayList<ObservateurModel> listobs;
    private ObservateurAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_observateur);

        initView();
        getListObs();
        setListViewStuff();
        setListOnClickListener();
        setupActionBar();

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Liste des observateurs");
        actionBar.setDisplayHomeAsUpEnabled(true); //show a caret even if android:parentActivityName is not specified.
    }

    private void setListViewStuff() {
        adapter = new ObservateurAdapter(listobs,ObservateurList.this);
        list.setAdapter(adapter);
    }

    private void initView() {
        observateurModels = new ArrayList<>();
        list = findViewById(R.id.listObservateurLAYOUT);
    }

    private void getListObs() {
        listobs = this.getIntent().getExtras().getParcelableArrayList("Obs");
    }

    private void setListOnClickListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ObservateurModel observateurModel = listobs.get(position);
                String NomPrenomObs = observateurModel.getNomObs()+" "+observateurModel.getPrenomObs();

                DialogDemandeur alert = new DialogDemandeur();
                alert.showDialog(ObservateurList.this, NomPrenomObs, observateurModel.getEmail(), observateurModel.getTelephone(),
                        observateurModel.getLieu(), observateurModel.getPoste());


            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
