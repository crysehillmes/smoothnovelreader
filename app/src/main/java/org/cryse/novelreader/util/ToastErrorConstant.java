package org.cryse.novelreader.util;

import org.cryse.novelreader.R;

public class ToastErrorConstant {
    public static final int TOAST_FAILURE_SIGNIN = 17;
    public static final int TOAST_FAILURE_USER_INFO = 19;
    public static final int TOAST_FAILURE_FORUM_LIST = 20;
    public static final int TOAST_FAILURE_RATE_POST = 55;

    public static int errorCodeToStringRes(int errorCode) {
        switch (errorCode) {
            default:
                return R.string.toast_generic_error;
        }
    }
}