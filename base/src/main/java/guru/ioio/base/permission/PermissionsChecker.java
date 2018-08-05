package guru.ioio.base.permission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import guru.ioio.base.frame.BaseApp;

/**
 * 检查权限的工具类
 * <p/>
 * Created by wangchenlong on 16/1/26.
 */
public class PermissionsChecker {
    // 判断权限集合
    public static boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    // 判断是否缺少权限
    public static boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(BaseApp.getInstance(), permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    public static final String[] DANGER_PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS
    };
}
