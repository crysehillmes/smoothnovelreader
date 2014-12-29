package org.cryse.novelreader.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.cryse.novelreader.R;

public class PicassoHelper {
    public static void load(Context context, String url, int placeHolderResId, int errorResId, ImageView imageView) {
        Picasso.with(context)
                .load(url)
                .placeholder(placeHolderResId)
                .error(errorResId)
                .fit()
                .into(imageView);
    }

    public static void load(Context context, String url, ImageView imageView) {
        load(context, url, imageView, false);
    }

    public static void load(Context context, String url, ImageView imageView, boolean grayScale) {
        Picasso picasso = Picasso.with(context);
        if (url != null && TextUtils.isEmpty(url) == false) {
            RequestCreator requestCreator = picasso
                    .load(url)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .fit();
            if (grayScale)
                requestCreator = requestCreator.transform(new GrayscaleTransformation());
            requestCreator.into(imageView);
        } else {
            picasso
                    .load(R.drawable.image_placeholder)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .fit()
                    .into(imageView);
        }
    }
}