package com.sidereo.picturepicker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
