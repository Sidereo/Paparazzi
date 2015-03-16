package com.sidereo.picturepicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
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

    public static final int DEFAULT_PREVIEW_PICTURE_NB = 10;

    private Context context;
    private ImageLoader imageLoader;
    private final DisplayImageOptions imageLoaderOptions;

    private int count;

    private boolean  openCamera;
    private boolean  openFiles;

    private int localPreviews;
    private List<String> localPreviewPaths;

    PicturePickerAdapter(Context context) {
        this(context, DEFAULT_PREVIEW_PICTURE_NB);
    }

    PicturePickerAdapter(Context context, int localPicturesPreview) {
        this.context = context;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        this.imageLoader = ImageLoader.getInstance();
        this.imageLoader.init(config);

        this.imageLoaderOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(new ColorDrawable(Color.TRANSPARENT))
                .build();

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

    public class ListItemViewHolder extends RecyclerView.ViewHolder {
        ImageView picture;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            picture = (ImageView) itemView.findViewById(R.id.picturepicker_item_picture);
        }
    }

}
