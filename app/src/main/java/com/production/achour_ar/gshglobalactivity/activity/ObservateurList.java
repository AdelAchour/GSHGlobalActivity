package com.production.achour_ar.gshglobalactivity.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.production.achour_ar.gshglobalactivity.data_model.ObservateurModel;
import com.production.achour_ar.gshglobalactivity.R;
import com.production.achour_ar.gshglobalactivity.adapter.ObservateurAdapter;
import com.production.achour_ar.gshglobalactivity.dialog.DialogDemandeur;

import java.util.ArrayList;

public class ObservateurList extends AppCompatActivity {

    ListView list;
    ArrayList<ObservateurModel> observateurModels;

    ArrayList<ObservateurModel> listobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_observateur);

        listobs = this.getIntent().getExtras().getParcelableArrayList("Obs");

        observateurModels = new ArrayList<>();

        list = (ListView)findViewById(R.id.listObservateurLAYOUT);

        ObservateurAdapter adapter = new ObservateurAdapter(listobs,getApplicationContext());
        list.setAdapter(adapter);

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
}
