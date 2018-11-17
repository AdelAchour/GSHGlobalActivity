package com.production.achour_ar.gshglobalactivity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView profilPicIV;
    private Button pickImageButton;
    private Uri imageURI;
    private String session_token, nameUser, idUser, firstnameUser;
    private String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile_act);

        SDKTestFileURI();
        initView();
        setupActionBar();
        setListener();
        getArguments();
        loadProfilePic();

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); //show a caret even if android:parentActivityName is not specified.
        actionBar.setTitle("Mon profil");

    }

    private void loadProfilePic() {
        Bitmap profilePic;

        String path = Constants.PROFILE_PIC_PATH;
        String picname = MyPreferences.getMyProfilPicName(this, Constants.PROFILE_PIC_NAME__KEY, Constants.PROFILE_PIC_NAME_DEF);

        profilePic = LoadProfilePic.loadImageFromStorage(path,picname);
        profilPicIV.setImageBitmap(profilePic);

    }

    private void getArguments() {
        Intent i = getIntent();
        session_token = i.getStringExtra("session");
        nameUser = i.getStringExtra("nom");
        firstnameUser = i.getStringExtra("prenom");
        idUser = i.getStringExtra("id");
    }

    private void SDKTestFileURI() {
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void setListener() {
        pickImageButton.setOnClickListener(this);
    }

    private void initView() {
        profilPicIV = findViewById(R.id.profilePic);
        pickImageButton = findViewById(R.id.buttonPickImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonPickImage:
                if (isStoragePermissionGranted()) openGallery();
                else askForPermission();
                break;
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(getApplicationContext(), GalleryUtil.class);
        startActivityForResult(gallery, Constants.PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PICK_IMAGE) {
            if(resultCode == Activity.RESULT_OK){
                picturePath = data.getStringExtra(Constants.PicturePath);
                performCrop(picturePath);
            }
        }

        if (requestCode == Constants.RESULT_CROP) {
            if(resultCode == Activity.RESULT_OK){
                Bundle extras ;
                Bitmap selectedBitmap = null;

                if(!(data.getExtras()==null)){
                    try {
                        extras = data.getExtras();
                        selectedBitmap = extras.getParcelable("data");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Set The Bitmap Data To ImageView
                profilPicIV.setImageBitmap(selectedBitmap);
                AccueilUser.handler.sendEmptyMessage(Constants.UPLOAD_PROFILE_PIC_NAV_HEADER);
                profilPicIV.setScaleType(ScaleType.FIT_XY);
                System.out.println("Height: "+profilPicIV.getHeight());
                System.out.println("Width: "+profilPicIV.getWidth());

                String pathToPic = saveToInternalStorage(selectedBitmap);
                System.out.println("Path: "+pathToPic);
            }
        }

    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        //ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        //File directory = cw.getDir("fourStarsPics", Context.MODE_PRIVATE);
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "FourStarsPics");

        if (!mediaStorageDir.exists()) {
            //mediaStorageDir.mkdir();
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory");
            }
        }
        // Create imageDir
        String picName = nameUser+"_"+firstnameUser+"_"+getNowTime()+"_profilePic.jpg";
        File mypath = new File(mediaStorageDir,picName);



        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            Toast.makeText(this, "Pas de permission", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String path = mediaStorageDir.getAbsolutePath();
        //MyPreferences.SaveProfilPicPath(Constants.PROFILE_PIC_PATH_KEY, path);

        MyPreferences.deletePreference(Constants.PROFILE_PIC_NAME__KEY);
        MyPreferences.SaveProfilPicName(Constants.PROFILE_PIC_NAME__KEY, picName);

        return path;
    }

    private String getNowTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);

        return strDate;
    }

    private void performCrop(String picturePath) {
        try {
            //Start Crop Activity
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picturePath);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 300);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, Constants.RESULT_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void askForPermission() {

        Log.v("Permission", "Asking for permission");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL);

    }

    public boolean isStoragePermissionGranted() {
        boolean permission = true;
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission", "Permission is granted");
                permission = true;

            } else {
                Log.v("Permission", "Permission is revoked");
                permission = false;
            }
        }
        return permission;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // La permission est garantie
                    openGallery();
                } else {
                    // La permission est refusée
                    Toast.makeText(this, R.string.external_permission_revoked, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

    }

}
