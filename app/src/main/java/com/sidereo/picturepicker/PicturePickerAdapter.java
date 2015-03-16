package com.sidereo.picturepicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    private int localPreviews;
    private List<String> localPreviewPaths;

    PicturePickerAdapter(Context context) {
        this(context, DEFAULT_PREVIEW_PICTURE_NB);
    }

    PicturePickerAdapter(Context context, int localPicturesPreview) {
        this.context = context;
        if (localPicturesPreview < 0) {
            Log.e(LOG, "Invalid number of previewed pictures.");
            this.localPreviews = DEFAULT_PREVIEW_PICTURE_NB;
        } else {
            this.localPreviews = localPicturesPreview;
        }

        int maxLocalPreviews = RecentPictureFactory.getMaxAvailablePreview(context);
        if (this.localPreviews > maxLocalPreviews)
            this.localPreviews = maxLocalPreviews;

        String[] paths = RecentPictureFactory.getPicturesPath(context, this.localPreviews);
        if (paths.length != this.localPreviews)
            this.localPreviews = paths.length;
        localPreviewPaths = new ArrayList<String>(Arrays.asList(paths));
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        String path = localPreviewPaths.get(position);
        Bitmap bm = BitmapFactory.decodeFile(path);
        viewHolder.picture.setImageBitmap(bm);
    }

    @Override
    public int getItemCount() {
        return localPreviews;
    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder {
        ImageView picture;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            picture = (ImageView) itemView.findViewById(R.id.picturepicker_item_picture);
        }
    }
}
