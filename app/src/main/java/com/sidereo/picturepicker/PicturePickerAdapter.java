package com.sidereo.picturepicker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sidereo on 16/03/15.
 */
public class PicturePickerAdapter extends RecyclerView.Adapter<PicturePickerAdapter.ListItemViewHolder> {
    public static final String LOG = PicturePickerAdapter.class.getSimpleName();

    public static final int SCAN_CAMERA_INTENT = 1986;
    public static final int SCAN_FILES_INTENT = 1987;
    public static final String IMAGE_MIME_TYPE = "image/*";

    public static final int DEFAULT_POS = -1;

    public static final int CAMERA = 0;
    public static final int PREVIEW = 1;
    public static final int FILES = 2;

    public static final int DEFAULT_PREVIEW_PICTURE_NB = 10;

    private Activity context;
    private ImageLoader imageLoader;
    private final DisplayImageOptions imageLoaderOptions;

    private int count;

    private boolean  openCamera;
    private boolean  openFiles;

    private int localPreviews;
    private List<String> localPreviewPaths;

    private int selectedPos;
    private OnPictureSelection onPictureSelection;

    PicturePickerAdapter(Activity context, OnPictureSelection onPictureSelection) {
        this(context, DEFAULT_PREVIEW_PICTURE_NB, onPictureSelection);
    }

    PicturePickerAdapter(Activity context, int localPicturesPreview, OnPictureSelection onPictureSelection) {
        this.context = context;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        this.imageLoader = ImageLoader.getInstance();
        this.imageLoader.init(config);
        this.imageLoaderOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(new ColorDrawable(Color.TRANSPARENT))
                .build();

        this.onPictureSelection = onPictureSelection;

        if (localPicturesPreview < 0) {
            Log.e(LOG, "Invalid number of previewed pictures.");
            this.localPreviews = DEFAULT_PREVIEW_PICTURE_NB;
        } else {
            this.localPreviews = localPicturesPreview;
        }

        String[] paths = RecentPictureFactory.getPicturesPath(context, this.localPreviews);
        if (paths.length != this.localPreviews) {
            this.localPreviews = paths.length;
        }
        localPreviewPaths = new ArrayList<String>(Arrays.asList(paths));

        openCamera = true;
        openFiles = true;

        selectedPos = DEFAULT_POS;

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return CAMERA;
            // Open files
        else if (position == count - 1)
            return FILES;
            // Display recent files
        else
            return PREVIEW;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListItemViewHolder viewHolder, int position) {
        // Open Camera
        if (position == 0) {
            viewHolder.picture.setImageResource(R.drawable.ic_photo_camera_grey600_48dp);
        }
        // Open files
        else if (position == count - 1) {
            viewHolder.picture.setImageResource(R.drawable.ic_perm_media_grey600_48dp);
        }
        // Display recent files
        else {
            if (openCamera)
                position--;
            File file = new File(localPreviewPaths.get(position));
            imageLoader.displayImage(Uri.fromFile(file).toString(), viewHolder.picture, imageLoaderOptions);
        }
    }

    @Override
    public int getItemCount() {
        int count = localPreviews;

        if (openCamera)
            count++;

        if (openFiles)
            count++;

        this.count = count;
        return count;
    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView picture;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            picture = (ImageView) itemView.findViewById(R.id.picturepicker_item_picture);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int pos = getAdapterPosition();

            int viewType = getItemViewType();
            switch (viewType) {
                case CAMERA:
                    selectedPos = CAMERA;
                    openCamera();
                    break;

                case FILES:
                    selectedPos = FILES;
                    openFiles();
                    break;

                case PREVIEW:
                    if (selectedPos == pos) {
                        selectedPos = DEFAULT_POS;
                        if (onPictureSelection != null)
                            onPictureSelection.onPictureUnselected();
                    } else {
                        selectedPos = pos;
                        if (onPictureSelection != null)
                            onPictureSelection.onPictureSelected(localPreviewPaths.get(pos - 1));
                    }
                    break;
            }
        }

    }

    private void openFiles() {
        final Intent intent = new Intent();
        intent.setType(IMAGE_MIME_TYPE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        context.startActivityForResult(intent, SCAN_FILES_INTENT);
    }

    private void openCamera() {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context.startActivityForResult(intent, SCAN_CAMERA_INTENT);
    }

    public void onResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SCAN_CAMERA_INTENT) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }
        else if (requestCode == SCAN_FILES_INTENT) {
            if (resultCode == Activity.RESULT_OK) {

                final Uri selectedImage = data.getData();
                String filePath = null;

                Cursor cursor = null;
                try {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        if (DocumentsContract.isDocumentUri(context, selectedImage)) {
                            String wholeImageId = DocumentsContract.getDocumentId(data.getData());
                            String imageId = wholeImageId.split(":")[1];

                            String[] projection = {MediaStore.MediaColumns.DATA};
                            String whereClause = MediaStore.Images.Media._ID + "=?";

                            cursor = context.getContentResolver().query(RecentPictureFactory.getUri(), projection, whereClause, new String[]{imageId}, null);
                            if (cursor.moveToFirst()) {
                                final int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                                filePath = cursor.getString(columnIndex);
                            }
                        }
                    } else {
                        final String[] filePathColumn = { MediaStore.Images.Media.DATA };

                        cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        if (cursor.moveToFirst()) {
                            final int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                            filePath = cursor.getString(columnIndex);
                        }
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

                if (TextUtils.isEmpty(filePath)) {
                    if (onPictureSelection != null)
                        onPictureSelection.onPictureUnselected();
                    return;
                } else if (filePath.startsWith("content://")) {
                    if (onPictureSelection != null)
                        onPictureSelection.onPictureUnselected();
                    return;
                }

                if (onPictureSelection != null)
                    onPictureSelection.onPictureSelected(filePath);

            } else {
                if (onPictureSelection != null)
                    onPictureSelection.onPictureUnselected();
            }
        }
    }

}
