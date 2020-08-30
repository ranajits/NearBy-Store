package com.ranajit.nearbystore.utilitis;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {
    public static boolean checkPermissions(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (!checkPermission(activity, permission)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkPermission(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissions(Activity activity, int permissionId, String... permissions) {
        ActivityCompat.requestPermissions(activity, permissions, permissionId);
    }
}
