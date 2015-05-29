package org.cryse.novelreader.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;


public class ToastProxy {
    public static void showToast(Context context, @StringRes int textResId, ToastType toastType) {
        showToast(context, context.getString(textResId), toastType);
    }

    public static void showToast(Context context, String text, ToastType toastType) {
        int textColor;
        int backgroundColor;
        int actionColor;
        switch (toastType) {
            case TOAST_ALERT:
                textColor = Color.RED;
                break;
            case TOAST_CONFIRM:
                textColor = Color.BLUE;
                break;
            case TOAST_INFO:
                textColor = Color.WHITE;
                break;
            default:
                textColor = Color.WHITE;
        }

        if(context instanceof Activity)
            Snackbar.with(context) // context
                    .text(text) // text to be displayed
                    .textColor(textColor) // change the text color
                            //.color(Color.BLUE) // change the background color
                    .show((Activity)context); // activity where it is displayed
        else
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

    }
}
