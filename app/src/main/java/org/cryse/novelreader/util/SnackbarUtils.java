package org.cryse.novelreader.util;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import org.cryse.novelreader.view.ContentView;

import javax.inject.Inject;

import retrofit.RetrofitError;

public class SnackbarUtils {

    public static int typeToFontColor(SimpleSnackbarType type) {
        int fontColor = Color.WHITE;
        switch (type) {
            case CONFIRM:
                fontColor = Color.GREEN;
                break;
            case WARNING:
                fontColor = Color.YELLOW;
                break;
            case ERROR:
                fontColor = Color.RED;
                break;
            case INFO:
            default:
                fontColor = Color.WHITE;
                break;
        }
        return fontColor;
    }

    public static Snackbar makeSimple(View view, CharSequence text, SimpleSnackbarType type, int duration) {
        SpannableString content = null;
        if(text instanceof SpannableString) {
            content = (SpannableString) text;
        } else {
            content = new SpannableString(text);
        }
        int start = 0;
        int end = content.length();
        int foregroundColor = typeToFontColor(type);
        content.setSpan(new ForegroundColorSpan(foregroundColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return Snackbar.make(view, content, duration);
    }

    public static Snackbar makeSimple(View view, int resId, SimpleSnackbarType type, int duration) {
        return makeSimple(view, view.getResources().getText(resId), type, duration);
    }

    SnackbarTextDelegate mSnackbarTextDelegate;
    @Inject
    public SnackbarUtils(SnackbarTextDelegate snackbarTextDelegate) {
        this.mSnackbarTextDelegate = snackbarTextDelegate;
    }

    public void showExceptionToast(ContentView view, Throwable throwable) {
        if(throwable instanceof RetrofitError) {
            RetrofitError error = (RetrofitError)throwable;
            if(error.getKind() == RetrofitError.Kind.HTTP || error.getKind() == RetrofitError.Kind.NETWORK)
                view.showSnackbar(mSnackbarTextDelegate.getNetworkErrorText(), SimpleSnackbarType.WARNING);
            else
                view.showSnackbar(mSnackbarTextDelegate.getGenericErrorText(), SimpleSnackbarType.WARNING);
        } else if(throwable instanceof EmptyContentException) {
            view.showSnackbar(mSnackbarTextDelegate.getChapterLostText(), SimpleSnackbarType.WARNING);
        } else {
            view.showSnackbar(mSnackbarTextDelegate.getGenericErrorText(), SimpleSnackbarType.WARNING);
        }
    }

    public void showEmptyContentToast(ContentView view) {
        view.showSnackbar(mSnackbarTextDelegate.getGenericErrorText(), SimpleSnackbarType.WARNING);
    }

    public String getEmptyContentText() {
        return mSnackbarTextDelegate.getChapterLostText();
    }
}
