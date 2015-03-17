package com.sidereo.picturepicker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by sidereo on 16/03/15.
 */
public class RecentPictureFactory {
    public static final String[] PROJECTIONS = new String[]{
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.MIME_TYPE
    };

    public static int getMaxAvailablePreview(Context context) {
        int count = 0;
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver()
                    .query(getUri(), PROJECTIONS, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

            count = cursor.getCount();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    public static String[] getPicturesPath(Context context, int size) {
        String[] result = new String[size];
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver()
                    .query(getUri(), PROJECTIONS, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");


            for(int i=0 ; i<size && cursor.moveToNext() ; i++) {
                result[i] = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));

                // Check if image exist, otherwise go to the next row
                File imageFile = new File(result[i]);
                if (!imageFile.exists())
                    i--;
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

    public static Uri getUri() {
        String state = Environment.getExternalStorageState();
        if(!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

}
