package org.cryse.novelreader.util;

import android.content.Context;
import android.content.res.Resources;

import org.cryse.novelreader.R;
import org.cryse.novelreader.qualifier.ApplicationContext;

import javax.inject.Inject;

public class ToastTextGenerator {
    Context mContext;
    Resources mResource;


    @Inject
    public ToastTextGenerator(@ApplicationContext Context context) {
        this.mContext = context;
        this.mResource = mContext.getResources();
    }

    public String getNetworkErrorText() {
        return mResource.getString(R.string.toast_network_error);
    }

    public String getNoConnectionText() {
        return mResource.getString(R.string.toast_network_no_connection);
    }

    public String getGenericErrorText() {
        return mResource.getString(R.string.toast_generic_error);
    }

    public String getChapterLostText() {
        return mResource.getString(R.string.toast_chapter_content_lost);
    }
}
