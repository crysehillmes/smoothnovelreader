package org.cryse.novelreader.util;

public enum SimpleSnackbarType {
    INFO(0),
    CONFIRM(1),
    WARNING(2),
    ERROR(3);

    int mSnackbarType;

    private SimpleSnackbarType(int snackbarType) {
        this.mSnackbarType = snackbarType;
    }

    public int getSnackbarType() {
        return this.mSnackbarType;
    }
}
