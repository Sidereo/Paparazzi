package com.sidereo.picturepicker;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by sidereo on 16/03/15.
 */
public class PicturePickerAdapter extends RecyclerView.Adapter<PicturePickerAdapter.ListItemViewHolder> {
    public static final String LOG = PicturePickerAdapter.class.getSimpleName();

    public static final int DEFAULT_PREVIEW_PICTURE = 10;

    private int localPicturesPreview;

    PicturePickerAdapter() {
        this(DEFAULT_PREVIEW_PICTURE);
    }

    PicturePickerAdapter(int localPicturesPreview) {
        if (localPicturesPreview < 0) {
            Log.e(LOG, "Invalid number of previewd pictures.");
            this.localPicturesPreview = DEFAULT_PREVIEW_PICTURE;
        } else {
            this.localPicturesPreview = localPicturesPreview;
        }

    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        viewHolder.picture.setImageResource(R.drawable.ic_image_grey600_48dp);
    }

    @Override
    public int getItemCount() {
        return localPicturesPreview;
    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder {
        ImageView picture;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            picture = (ImageView) itemView.findViewById(R.id.picturepicker_item_picture);
        }
    }
}
