package com.sidereo.paparazzi.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraUtils {

    private final Context context;
    private String cameraDestFile;

    public static CameraUtils newInstance(Context context) {

        return new CameraUtils(context);
    }

    private CameraUtils(Context context) {
        this.context = context;
    }

    public File createCameraImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "CAMERA_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        cameraDestFile = image.getAbsolutePath();
        return image;
    }

    public void addPictureToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(cameraDestFile);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPicturesDocumentsPathFromKitKat(Uri image) {
        String filePath = null;

        Cursor cursor = null;
        try {
            if (DocumentsContract.isDocumentUri(context, image)) {
                String wholeImageId = DocumentsContract.getDocumentId(image);
                String imageId = wholeImageId.split(":")[1];

                String[] projection = {MediaStore.MediaColumns.DATA};
                String whereClause = MediaStore.Images.Media._ID + "=?";

                cursor = context.getContentResolver().query(RecentPictureFactory.getUri(), projection, whereClause, new String[]{imageId}, null);

                if (cursor.moveToFirst()) {
                    final int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                    filePath = cursor.getString(columnIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return filePath;
    }

    public String getPicturesDocumentsPathBeforeKitKat(Uri selectedImage) {
        String filePath = null;

        Cursor cursor = null;
        try {
            final String[] filePathColumn = {MediaStore.Images.Media.DATA};

            cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                filePath = cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return filePath;
    }

}
