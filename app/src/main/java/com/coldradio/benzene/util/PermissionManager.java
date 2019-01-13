package com.coldradio.benzene.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionManager {
    public enum PermissionCode {
        WRITE_EXTERNAL_STORAGE
    }

    private static PermissionManager msInstance = new PermissionManager();
    private final String[] PERMISSION_MAP = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean[] mPermissionResults = new boolean[]{false};

    public static PermissionManager instance() {
        return msInstance;
    }

    public void checkAndRequestPermission(Activity activity, PermissionCode permissionCode) {
        String permission = PERMISSION_MAP[permissionCode.ordinal()];

        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, permissionCode.ordinal());
        } else {
            mPermissionResults[permissionCode.ordinal()] = true;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        if (requestCode == PermissionCode.WRITE_EXTERNAL_STORAGE.ordinal()) {
            mPermissionResults[requestCode] = (grantResults[requestCode] == PackageManager.PERMISSION_GRANTED);
        }
    }

    public boolean hasPermission(PermissionCode permissionCode) {
        return mPermissionResults[permissionCode.ordinal()];
    }
}
