package com.sidereo.paparazzi.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.sidereo.paparazzi.R;
import com.sidereo.paparazzi.adapter.PicturePickerAdapter;
import com.sidereo.paparazzi.listener.Redaction;

public class Paparazzi extends RecyclerView {

    private int pictureNumber;
    private PicturePickerAdapter adapter;
    private Activity activity;
    private LinearLayoutManager layoutManager;

    public Paparazzi(Context context) {
        super(context);
    }

    public Paparazzi(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public Paparazzi(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.paparazzi);
        try {
            pictureNumber = a.getInt(R.styleable.paparazzi_paparazzi__pictureNumber, 10);
        } finally {
            a.recycle();
        }
    }

    public Paparazzi prepare(Activity activity) {
        this.activity = activity;
        setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        return this;
    }

    // Not really ready yet.
    public Paparazzi usingOrientation(int orientation) {
        layoutManager.setOrientation(orientation);
        return this;
    }

    public void shoot() {
        setLayoutManager(layoutManager);
        if (activity instanceof Redaction) {
            adapter = new PicturePickerAdapter(activity, (Redaction) activity, pictureNumber);
        } else {
            throw new RuntimeException("Either the activity is null or it doesn't implement Redaction, consider using shoot(Activity, Redaction) instead");
        }
        setAdapter(adapter);
    }

    public void shoot(Activity activity, Redaction redaction) {
        setLayoutManager(layoutManager);
        if (redaction == null && activity instanceof Redaction) {
            adapter = new PicturePickerAdapter(activity, (Redaction) activity, pictureNumber);
        } else if (redaction != null) {
            adapter = new PicturePickerAdapter(activity, redaction, pictureNumber);
        } else {
            throw new RuntimeException("Either the activity is null or the Redaction is null and the activity doesn't implement Redaction");
        }
        setAdapter(adapter);
    }

    public void printPhotos(int requestCode, int resultCode, Intent data) {
        adapter.onResult(requestCode, resultCode, data);
    }

    public Paparazzi disableCamera() {
        return this;
    }
}
