package com.production.achour_ar.gshglobalactivity.ITs.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.production.achour_ar.gshglobalactivity.ITs.adapter.MessageAdapter;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.RemoteMessageModel;
import com.production.achour_ar.gshglobalactivity.R;

import java.util.ArrayList;
import java.util.Collections;

public class MessageAct extends AppCompatActivity {

    private ArrayList<RemoteMessageModel> remoteMessageModels;
    private ListView listView;
    private static MessageAdapter adapter;
    private ProgressDialog progressDialog;
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_act);

        initView();
        setupActionBar();
        setupPD();
        populateListView();

    }

    private void setupPD() {
        progressDialog.setMessage("Chargement des messages");
        progressDialog.show();
    }

    private void initView() {
        listView = findViewById(R.id.listMessages);
        remoteMessageModels = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        handler = new HandlerMessages();
    }

    public void populateListView() throws NullPointerException{
        // Read from the database
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference chatSpaceRef = rootRef.child("messages");

        chatSpaceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                remoteMessageModels.clear();
                for (DataSnapshot child: dataSnapshot.getChildren()) {

                    Log.d("READFIRE", "NAME : "+child.child("name").getValue().toString());

                    String name = getFirebaseString(child, "name");
                    String email = getFirebaseString(child, "email");
                    String title = getFirebaseString(child, "title");
                    String message = getFirebaseString(child, "message");
                    String date = getFirebaseString(child, "date");


                    RemoteMessageModel messageModel = new RemoteMessageModel(name, email, title, message, date);
                    remoteMessageModels.add(messageModel);

                }

                Collections.reverse(remoteMessageModels);
                adapter = new MessageAdapter(remoteMessageModels, getApplicationContext());
                handler.sendEmptyMessage(0); //dismiss pd
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        RemoteMessageModel remoteMessageModel = remoteMessageModels.get(position);

                        Intent i = new Intent(MessageAct.this, SingleMessage.class);
                        i.putExtra("name", remoteMessageModel.getName());
                        i.putExtra("email", remoteMessageModel.getEmail());
                        i.putExtra("title", remoteMessageModel.getTitle());
                        i.putExtra("message", remoteMessageModel.getMessage());
                        i.putExtra("date", remoteMessageModel.getDate());

                        startActivity(i);

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                handler.sendEmptyMessage(0); //dismiss pd
            }
        });
    }

    private String getFirebaseString(DataSnapshot child, String childname) {
        String response = "";

        if (child.child(childname).exists()){
            if (child.child(childname).getValue() != null){
                response = child.child(childname).getValue().toString();
            }
            else {
                response = "null";
            }
        }
        else {
            response = "null" ;
        }


        return response;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); //show a caret even if android:parentActivityName is not specified.
        actionBar.setTitle("Messages");
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

    private class HandlerMessages extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: //dismiss pd
                    progressDialog.dismiss();
                    break;

            }
        }
    }
}