package com.sidereo.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sidereo.paparazzi.listener.Redaction;
import com.sidereo.paparazzi.view.Paparazzi;

import java.io.File;

public class MainActivity extends ActionBarActivity implements Redaction {
    Paparazzi paparazzi;

    ImageView selectedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.sidereo.sample.R.layout.activity_main);

        selectedImageView = (ImageView) findViewById(com.sidereo.sample.R.id.selectedPicture);

        paparazzi = (Paparazzi) findViewById(com.sidereo.sample.R.id.paparazzi);

        paparazzi.prepare(this).shoot();
//        paparazzi.prepare(this).disableCamera().disableGallery().shoot();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        paparazzi.printPhotos(requestCode, resultCode, data);
    }

    @Override
    public void pictureSelected(File file) {
        Glide.with(MainActivity.this).load(file).into(selectedImageView);
    }

    @Override
    public void cancelEverySelection() {
        selectedImageView.setImageResource(com.sidereo.sample.R.drawable.ic_image_grey600_48dp);
    }

}
