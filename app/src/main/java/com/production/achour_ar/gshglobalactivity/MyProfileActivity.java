package com.production.achour_ar.gshglobalactivity;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.net.Uri;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView profilPic;
    private Button pickImageButton;
    private Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile_act);

        initView();
        setListener();
    }

    private void setListener() {
        pickImageButton.setOnClickListener(this);
    }

    private void initView() {
        profilPic = findViewById(R.id.profilePic);
        pickImageButton = findViewById(R.id.buttonPickImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonPickImage:
                openGallery();
                break;
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, Constants.PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.PICK_IMAGE){
            imageURI = data.getData();
            profilPic.setImageURI(imageURI);
            Log.d("Uri", ""+imageURI);
        }
    }
}
