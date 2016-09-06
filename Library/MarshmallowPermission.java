import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * <p/>
 * Beginning in Android 6.0 (API level 23),
 * users grant permissions to apps while the app is running,
 * not when they install the app.
 * <p/>
 * Check For Permissions :
 * If your app needs a dangerous permission,
 * you must check whether you have that permission
 * every time you perform an operation that requires that permission.
 * <p/>
 * Request Permissions :
 * If your app needs a dangerous permission that was listed in the
 * app manifest, it must ask the user to grant the permission.
 * Android provides several methods you can use to request a permission.
 * Calling these methods brings up a standard Android dialog,
 * which you cannot customize.
 */
public class MarshmallowPermission {

    public static int ALL_PERMISSION_REQUEST_CODE = 1;
    public static final int RECORD_PERMISSION_REQUEST_CODE = 1;
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 4;
    public static final int LOCATION_COARSE_PERMISSION_REQUEST_CODE = 5;

    public static ArrayList<String> checkAllPermissions(Context context) {
        ArrayList<String> permissions = new ArrayList<>();

        if (!checkPermissionForRecord(context)) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!checkPermissionForExternalStorage(context)) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!checkPermissionForLocation(context)) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!checkPermissionForLocationCoarse(context)) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!checkPermissionForCamera(context)) {
            permissions.add(Manifest.permission.CAMERA);
        }

        return permissions;
    }

    public static int requestPermissions(Activity activity, ArrayList<String> permissions) {
        int requestCode = -1;
        if (permissions.size() > 0) {
            String[] permissionList = new String[permissions.size()];
            int i = 0;
            for (String permission : permissions) {
                permissionList[i++] = permission;
            }
            requestCode = ALL_PERMISSION_REQUEST_CODE++;
            ActivityCompat.requestPermissions(activity, permissionList, requestCode);
        }
        return requestCode;
    }


    public static ArrayList<String> checkPermissionsForStorageAndCamera(Context context) {
        ArrayList<String> permissions = new ArrayList<>();

        if (!checkPermissionForExternalStorage(context)) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!checkPermissionForCamera(context)) {
            permissions.add(Manifest.permission.CAMERA);
        }

        return permissions;
    }


    public static boolean checkPermissionForRecord(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkPermissionForCalendar(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkPermissionForExternalStorage(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkPermissionForCamera(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkPermissionForSensor(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.BODY_SENSORS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkPermissionForLocation(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkPermissionForLocationCoarse(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static int requestPermissionForRecord(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(activity, "Microphone permission needed for recording. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_PERMISSION_REQUEST_CODE);
        }
        return RECORD_PERMISSION_REQUEST_CODE;
    }

    public static int requestPermissionForExternalStorage(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        return EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE;
    }

    public static int requestPermissionForCamera(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        return CAMERA_PERMISSION_REQUEST_CODE;
    }

    public static int requestPermissionForSensor(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BODY_SENSORS)) {
            Toast.makeText(activity, "Sensor permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BODY_SENSORS}, CAMERA_PERMISSION_REQUEST_CODE);
        }
        return CAMERA_PERMISSION_REQUEST_CODE;

    }

    public static int requestPermissionForLocation(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        return LOCATION_PERMISSION_REQUEST_CODE;
    }

    public static int requestPermissionForLocationCoarse(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_COARSE_PERMISSION_REQUEST_CODE);
        return LOCATION_COARSE_PERMISSION_REQUEST_CODE;
    }
}