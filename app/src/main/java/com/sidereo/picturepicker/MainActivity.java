package com.sidereo.picturepicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sidereo.paparazzi.OnPictureSelection;
import com.sidereo.paparazzi.PicturePickerAdapter;

import java.io.File;

public class MainActivity extends ActionBarActivity {
    RecyclerView recyclerview;
    PicturePickerAdapter adapter;

    TextView textView;
    ImageView selectedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        textView = (TextView) findViewById(R.id.textview);
        selectedImageView = (ImageView) findViewById(R.id.selectedPicture);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview.setLayoutManager(layoutManager);

        // allows for optimizations if all items are of the same size:
        recyclerview.setHasFixedSize(true);

        OnPictureSelection onPictureSelection = new OnPictureSelection() {
            @Override
            public void onPictureSelected(String path) {
                textView.setText(path);
                File file = new File(path);
                if (file.exists()) {
                    Bitmap bm = BitmapFactory.decodeFile(path);
                    selectedImageView.setImageBitmap(bm);
                }
            }

            @Override
            public void onPictureUnselected() {
                textView.setText(R.string.hello_world);
                selectedImageView.setImageResource(R.drawable.ic_image_grey600_48dp);
            }
        };
        adapter = new PicturePickerAdapter(MainActivity.this, onPictureSelection);
        recyclerview.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter.onResult(requestCode, resultCode, data);
    }
}
