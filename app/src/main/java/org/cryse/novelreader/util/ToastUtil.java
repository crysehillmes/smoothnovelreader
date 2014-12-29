package org.cryse.novelreader.util;

import org.cryse.novelreader.view.ContentView;

import javax.inject.Inject;

import retrofit.RetrofitError;

public class ToastUtil {
    ToastTextGenerator mToastTextGenerator;
    @Inject
    public ToastUtil(ToastTextGenerator toastTextGenerator) {
        this.mToastTextGenerator = toastTextGenerator;
    }

    public void showExceptionToast(ContentView view, Throwable throwable) {
        if(throwable instanceof RetrofitError) {
            RetrofitError error = (RetrofitError)throwable;
            if(error.getKind() == RetrofitError.Kind.HTTP || error.getKind() == RetrofitError.Kind.NETWORK)
                view.showToast(mToastTextGenerator.getNetworkErrorText(), ToastType.TOAST_ALERT);
            else
                view.showToast(mToastTextGenerator.getGenericErrorText(), ToastType.TOAST_ALERT);
        } else if(throwable instanceof EmptyContentException) {
            view.showToast(mToastTextGenerator.getChapterLostText(), ToastType.TOAST_ALERT);
        } else {
            view.showToast(mToastTextGenerator.getGenericErrorText(), ToastType.TOAST_ALERT);
        }
    }

    public void showEmptyContentToast(ContentView view) {
        view.showToast(mToastTextGenerator.getGenericErrorText(), ToastType.TOAST_ALERT);
    }

    public String getEmptyContentText() {
        return mToastTextGenerator.getChapterLostText();
    }
}
