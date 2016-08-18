package org.cryse.novelreader.application;

import android.Manifest;
import android.os.Build;

import java.util.HashSet;
import java.util.Set;

public class AppPermissions {
    public static final Set<String> PERMISSIONS_SET;
    public static final String[] PERMISSIONS;
    public static final int RC_PERMISSION_STORAGE = 101;
    static {
        PERMISSIONS_SET = new HashSet<>();
        PERMISSIONS_SET.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            PERMISSIONS_SET.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        PERMISSIONS = PERMISSIONS_SET.toArray(new String[PERMISSIONS_SET.size()]);
    }
}

