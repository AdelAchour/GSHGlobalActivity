package com.production.achour_ar.gshglobalactivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class LoadProfilePic {

    public static Bitmap profilPic;

    public static Bitmap loadImageFromStorage(String path, String picName) {

        try {
            File f = new File(path, picName);
            profilPic = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return profilPic;
    }
}
