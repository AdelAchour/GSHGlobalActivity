package com.production.achour_ar.gshglobalactivity.ITs.activity;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.production.achour_ar.gshglobalactivity.R;

public class SingleMessage extends AppCompatActivity  {

    private TextView nameTV;
    private TextView emailTV;
    private TextView titleTV;
    private TextView messageTV;
    private TextView dateTV;
    private String name;
    private String email;
    private String title;
    private String message;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_message_act);

        initView();
        setupActionBar();
        getArguments();
        populateTVs();

    }

    private void getArguments() {
        Intent i = getIntent();
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        title = i.getStringExtra("title");
        message = i.getStringExtra("message");
        date = i.getStringExtra("date");
    }

    private void populateTVs() {
        nameTV.setText(name);
        emailTV.setText(email);
        titleTV.setText(title);
        messageTV.setText(message);
        dateTV.setText(date);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); //show a caret even if android:parentActivityName is not specified.
        actionBar.setTitle("Détail du message");
    }

    private void initView() {
        nameTV = findViewById(R.id.nameSenderSingle);
        emailTV = findViewById(R.id.emailSender);
        titleTV = findViewById(R.id.titleMessage);
        messageTV = findViewById(R.id.contentMessage);
        dateTV = findViewById(R.id.dateMessage);
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