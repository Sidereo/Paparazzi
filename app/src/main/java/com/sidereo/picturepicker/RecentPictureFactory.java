package com.sidereo.picturepicker;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

/**
 * Created by sidereo on 16/03/15.
 */
public class RecentPictureFactory {
    public static final String[] PROJECTIONS_MAX = new String[]{
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
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTIONS_MAX, null,
                            null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

            count = cursor.getCount();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }
}
