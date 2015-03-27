package com.sidereo.paparazzi.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.sidereo.paparazzi.R;
import com.sidereo.paparazzi.listener.Redaction;
import com.sidereo.paparazzi.utils.RecentPictureFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String LOG = PictureAdapter.class.getSimpleName();


    public static final int DEFAULT_POS = -1;

    public static final int CAMERA = 0;
    public static final int PREVIEW = 1;
    public static final int GALLERY = 2;

    public static final int DEFAULT_PREVIEW_PICTURE_NB = 10;
    private final RequestManager glide;

    private final Listener listener;

    private int count;

    private boolean cameraVisible;
    private boolean galleryVisible;

    private int localPreviews;
    private List<String> localPreviewPaths;

    private int selectedPos;
    private Redaction redaction;

    public PictureAdapter(Activity context, Redaction onPictureSelection, Listener listener, int localPicturesPreview, boolean wantsCamera, boolean wantsGallery) {

        this.glide = Glide.with(context);

        this.redaction = onPictureSelection;

        if (localPicturesPreview < 0) {
            Log.e(LOG, "Invalid number of previewed pictures. Using default " + DEFAULT_PREVIEW_PICTURE_NB);
            this.localPreviews = DEFAULT_PREVIEW_PICTURE_NB;
        } else {
            this.localPreviews = localPicturesPreview;
        }

        String[] paths = RecentPictureFactory.getPicturesPath(context, this.localPreviews);
        if (paths.length != this.localPreviews) {
            this.localPreviews = paths.length;
        }
        localPreviewPaths = new ArrayList<>(Arrays.asList(paths));

        cameraVisible = wantsCamera;
        galleryVisible = wantsGallery;

        selectedPos = DEFAULT_POS;

        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (cameraVisible && position == 0)
            return CAMERA;
        else if (galleryVisible && position == count - 1)
            return GALLERY;
        else
            return PREVIEW;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case CAMERA:
                View cameraView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_camera, parent, false);
                return new CameraViewHolder(cameraView);

            case PREVIEW:
                View previewView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new PreviewPicViewHolder(previewView);

            case GALLERY:
                View galleryView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_gallery, parent, false);
                return new OpenPicsViewHolder(galleryView);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {

        // Open Camera
        if (viewHolder instanceof CameraViewHolder) {
            // nothing to do here. for now.
        }
        // Open files
        else if (viewHolder instanceof OpenPicsViewHolder) {
            // nothing to do here. for now.
        }
        // Display recent files
        else {
            if (cameraVisible) {
                position--;
            }
            File file = new File(localPreviewPaths.get(position));
            glide.load(file).centerCrop().fitCenter().into(((PreviewPicViewHolder)viewHolder).picture);

            ((PreviewPicViewHolder) viewHolder).setView(localPreviewPaths.get(position));
        }
    }

    @Override
    public int getItemCount() {
        int count = localPreviews;

        if (cameraVisible)
            count++;

        if (galleryVisible)
            count++;

        this.count = count;
        return count;
    }

    public void add(int position, String item) {
        localPreviewPaths.add(position, item);
        notifyItemInserted(position);
    }

    public class CameraViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CameraViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            selectedPos = CAMERA;
            listener.openCamera();
        }
    }

    public class PreviewPicViewHolder extends RecyclerView.ViewHolder {
        ImageView picture;

        public PreviewPicViewHolder(View itemView) {
            super(itemView);
            picture = (ImageView) itemView.findViewById(R.id.picturepicker_item_picture);
        }

        public void setView(final String path) {
            File file = new File(path);
            glide.load(file).fitCenter().centerCrop().into(picture);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (redaction != null) {
                        redaction.pictureSelected(Uri.parse(path));
                    }
                    setNewPositionOrCancelSelection();
                }
            });
        }

        private void setNewPositionOrCancelSelection() {
            final int pos = getAdapterPosition();

            if (selectedPos == pos) {
                cancelSelection();
            } else {
                selectedPos = pos;
            }
        }

        private void cancelSelection() {
            selectedPos = DEFAULT_POS;
            if (redaction != null)
                redaction.cancelEverySelection();
        }
    }

    public class OpenPicsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public OpenPicsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.openFiles();
        }
    }

    public interface Listener {

        void openCamera();

        void openFiles();
    }
}
