package com.sidereo.paparazzi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sidereo.paparazzi.adapter.PicturePickerAdapter;
import com.sidereo.paparazzi.listener.Redaction;
import com.sidereo.paparazzi.view.Paparazzi;

public class MainActivity extends ActionBarActivity implements Redaction {
    Paparazzi paparazzi;
    PicturePickerAdapter adapter;

    ImageView selectedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.sidereo.picturepicker.R.layout.activity_main);

        selectedImageView = (ImageView) findViewById(com.sidereo.picturepicker.R.id.selectedPicture);

        paparazzi = (Paparazzi) findViewById(com.sidereo.picturepicker.R.id.paparazzi);

        paparazzi.prepare(this).disableCamera().shoot();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        paparazzi.printPhotos(requestCode, resultCode, data);
    }

    @Override
    public void pictureSelected(Uri uri) {
        Glide.with(MainActivity.this).load(uri.getPath()).into(selectedImageView);
    }

    @Override
    public void cancelEverySelection() {
        selectedImageView.setImageResource(com.sidereo.picturepicker.R.drawable.ic_image_grey600_48dp);
    }

}
