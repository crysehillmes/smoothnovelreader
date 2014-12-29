package org.cryse.novelreader.util;

public enum ToastType{
    TOAST_INFO(0),
    TOAST_ALERT(1),
    TOAST_CONFIRM(2);

    int mToastType;

    private ToastType(int toastType) {
        this.mToastType = toastType;
    }

    public int getToastType() {
        return this.mToastType;
    }
}
