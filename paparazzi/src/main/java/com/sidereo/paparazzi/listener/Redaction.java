package com.sidereo.paparazzi.listener;

import android.net.Uri;

public interface Redaction {

    public void pictureSelected(Uri fileUri);
    public void cancelEverySelection();

}
