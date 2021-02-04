package com.production.achour_ar.gshglobalactivity.ITs.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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