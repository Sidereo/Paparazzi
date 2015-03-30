package com.sidereo.paparazzi.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.sidereo.paparazzi.R;
import com.sidereo.paparazzi.adapter.PictureAdapter;
import com.sidereo.paparazzi.listener.Redaction;
import com.sidereo.paparazzi.utils.CameraUtils;

import java.io.File;
import java.io.IOException;

public class Paparazzi extends RecyclerView implements PictureAdapter.Listener {

    public static final String IMAGE_MIME_TYPE = "image/*";
    public static final int SCAN_CAMERA_INTENT = 1986;
    public static final int SCAN_FILES_INTENT = 1987;

    private int pictureNumber;
    private PictureAdapter adapter;

    private Activity activity;
    private Redaction redaction;

    private LinearLayoutManager layoutManager;
    private boolean wantsCamera = true;
    private boolean wantsGallery = true;
    private CameraUtils cameraUtils;
    private File destFile;

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

    /**
     * Prepare Paparazzi using Activity.
     * @param activity must implement com.sidereo.paparazzi.listener.Redaction
     * @return the Paparazi instance for chaining method
     */
    public Paparazzi prepare(Activity activity) {
        this.activity = activity;
        setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        cameraUtils = CameraUtils.newInstance(activity);
        return this;
    }

    // Not really ready yet.
    private Paparazzi usingOrientation(int orientation) {
        layoutManager.setOrientation(orientation);
        return this;
    }

    /**
     * Build method. Use when the Paparazzi instance has been prepared.
     */
    public void shoot() {
        setLayoutManager(layoutManager);
        if (activity instanceof Redaction) {
            this.redaction = (Redaction) activity;
            adapter = new PictureAdapter(activity, (Redaction) activity, this, pictureNumber, wantsCamera, wantsGallery);
        } else {
            throw new RuntimeException("Either the activity is null or it doesn't implement Redaction, consider using shoot(Activity, Redaction) instead");
        }
        setAdapter(adapter);
    }

    /**
     * Build method. Use when the Paparazzi instance hasn't been prepared.
     * @param activity the activity. Needed to startActivityForResult (camera intent)
     * @param redaction the listener.
     */
    public void shoot(Activity activity, Redaction redaction) {
        setLayoutManager(layoutManager);
        if (redaction == null && activity instanceof Redaction) {
            this.redaction = (Redaction) activity;
            adapter = new PictureAdapter(activity, (Redaction) activity, this, pictureNumber, wantsCamera, wantsGallery);
        } else if (redaction != null) {
            this.redaction = redaction;
            adapter = new PictureAdapter(activity, redaction, this, pictureNumber, wantsCamera, wantsGallery);
        } else {
            throw new RuntimeException("Either the activity is null or the Redaction is null and the activity doesn't implement Redaction");
        }
        setAdapter(adapter);
    }

    /**
     * Call from onActivityResult of your activity with the same params.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void printPhotos(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_CAMERA_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                onCameraResult();
            } else {
                if (redaction != null)
                    redaction.cancelEverySelection();
            }
        } else if (requestCode == SCAN_FILES_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                onFilesResult(data);
            } else {
                if (redaction != null)
                    redaction.cancelEverySelection();
            }
        }
    }

    /**
     * Remove the camera item from the list.
     * @return the Paparazi instance for chaining method
     */
    public Paparazzi disableCamera() {
        wantsCamera = false;
        return this;
    }

    /**
     * Remove the gallery item from the list.
     * @return the Paparazi instance for chaining method
     */
    public Paparazzi disableGallery() {
        wantsGallery = false;
        return this;
    }

    @Override
    public void openFiles() {
        final Intent intent = new Intent();
        intent.setType(IMAGE_MIME_TYPE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, SCAN_FILES_INTENT);
    }

    @Override
    public void openCamera() {
        try {
            destFile = cameraUtils.createCameraImageFile();
        } catch (IOException e) {
            e.printStackTrace();
            if (redaction != null) {
                redaction.cancelEverySelection();
            }
            return;
        }

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destFile));
        activity.startActivityForResult(intent, SCAN_CAMERA_INTENT);
    }

    private void onCameraResult() {
        if (destFile != null && destFile.exists()) {
            cameraUtils.addPictureToGallery();
            adapter.add(0, destFile);
            if (redaction != null)
                redaction.pictureSelected(destFile);
        } else if (redaction != null) {
            redaction.cancelEverySelection();
        }
    }

    private void onFilesResult(Intent data) {
        final Uri selectedImage = data.getData();
        String filePath;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            filePath = cameraUtils.getPicturesDocumentsPathFromKitKat(selectedImage);
        } else {
            filePath = cameraUtils.getPicturesDocumentsPathBeforeKitKat(selectedImage);
        }

        if (TextUtils.isEmpty(filePath) || filePath.startsWith("content://")) {
            if (redaction != null) {
                redaction.cancelEverySelection();
            }
            return;
        }

        if (redaction != null) {
            redaction.pictureSelected(new File(filePath));
        }
    }
}
