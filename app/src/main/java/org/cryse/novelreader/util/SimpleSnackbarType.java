package org.cryse.novelreader.util;

import android.support.design.widget.Snackbar;

public enum SimpleSnackbarType {
    INFO(0),
    CONFIRM(1),
    WARNING(2),
    ERROR(3);

    public static final int LENGTH_SHORT = Snackbar.LENGTH_SHORT;
    public static final int LENGTH_LONG = Snackbar.LENGTH_LONG;
    int mSnackbarType;

    private SimpleSnackbarType(int snackbarType) {
        this.mSnackbarType = snackbarType;
    }

    public int getSnackbarType() {
        return this.mSnackbarType;
    }
}
