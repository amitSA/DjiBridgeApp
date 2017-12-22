package test.com.bridge.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import dji.common.mission.waypoint.WaypointMissionState;
import test.com.bridge.callback.AllEvents.ConnectionType;

/**
 * Created by Amit on 6/26/2017.
 */

/**
 * Utility class for general/miscellaneous operations you may want to do in the app
 */
public class HelpMes {
    private static final String CLASSNAME = HelpMes.class.getSimpleName();
    public static String NEW_LINE = System.getProperty("line.separator");
    private static Context appContext;

    /**
     * A static method that should be used on application start up to give the HelpMes class object
     * a reference to any context object of the application.  The application context will be obtained
     * from the passed in context, and this context will be will be used for class-level utility methods
     * that require contexts.
     * @param c The context used to get the application context and store it as class-level field
     */
    public static void initialize(Context c){
        appContext = c.getApplicationContext(); //calling getApplicationContext() is probably redundant on a object you know is the Application object of an app.  In this case, the method call probably just returns the calling object
    }

    /**
     * see @return
     * @return Returns the application context.  In order for a valid non-null object to be returned
     * giveContext() must be been called first with a passed in non-null context
     */
    public static Context getApplicationContext(){
        return appContext;
    }
    /**
     * Utility method for getting a string representation of a WaypointMissionState b/c
     * this class did not effing implement it's toString() method
     * @param state the WaypointMissionState to translate to string form
     * @return the string equivalent of the WaypointMissionState
     */
    public static String getWaypointStateString(WaypointMissionState state){
        if(state==WaypointMissionState.DISCONNECTED){
            return "DISCONNECTED";
        }else if(state==WaypointMissionState.EXECUTING){
            return "EXECUTING";
        } else if(state==WaypointMissionState.EXECUTION_PAUSED){
            return "EXECUTION_PAUSED";
        }else if(state==WaypointMissionState.NOT_SUPPORTED){
            return "NOT_SUPPORTED";
        }else if(state==WaypointMissionState.READY_TO_EXECUTE){
            return "READY_TO_EXECUTE";
        }else if(state==WaypointMissionState.READY_TO_UPLOAD){
            return "READY_TO_UPLOAD";
        }else if(state==WaypointMissionState.RECOVERING){
            return "RECOVERING";
        }else if(state==WaypointMissionState.UNKNOWN){
            return "UNKNOWN";
        }else if(state==WaypointMissionState.UPLOADING){
            return "UPLOADING";
        }
        return null; //NOTE: Program should never reach this statement in this method,
                     //      program should have returned from method before reaching this statement
    }
    /*An array of permissions that this app requires.  I think the normal level permissions
     * don't have to have to be included in this array, however I included them
     * b/c the DJISample app included them*/
    final public static String [] perms = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
             /*   Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, */
            Manifest.permission.READ_EXTERNAL_STORAGE,
             /*   Manifest.permission.SYSTEM_ALERT_WINDOW, */
            Manifest.permission.READ_PHONE_STATE,
    };

    /**
     * see @return
     * @return a boolean indicating whether the application has
     *         all the permissions that I (the developer) need the application to have
     */
    public static boolean app_hasAllRequiredPermissions(){
        boolean flag = Settings.canDrawOverlays(appContext);
        for(int i = 0;i<perms.length && flag;i++){
            int perm = ContextCompat.checkSelfPermission(appContext, perms[i]);
            flag = perm==PackageManager.PERMISSION_GRANTED;
            if(!flag){
                Log.d(CLASSNAME,"in app_hasAllRequiredPermissions() flag turned false at index i: " + i);
            }
        }
        return flag;
    }

    /**
     * See @return
     * @return a boolean indicating whether the application has
     *         the Overlay's permission
     */
    public static boolean app_hasOverlayPermission(){
        return Settings.canDrawOverlays(appContext);
    }

    /**
     * This method will request all required permissions of this app
     * @param activity The activity that will be used to request overlay permissions and
     *                 the remaining permissions.  This activity's onRequestPermissionsResult() will be called
     * @param requestCode The requestCode that the passed in activity's onRequestPermissionsResult() will be called with
     */
    public static void app_requestRequiredPermissions(Activity activity, int requestCode){
        boolean permission = app_hasOverlayPermission();
        if(!permission){
            app_requestOverlaysPermission(activity);
        }
        ActivityCompat.requestPermissions(activity,perms,requestCode);
    }

    /**
     * Utility method used to start the overlaid-permissions activity
     * @param activity The activity used to start the overlaid-permissions activity
     */
    public static void app_requestOverlaysPermission(Activity activity){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        activity.startActivity(intent);
    }

    /**
     * Utility method to display a toast
     * @param message The string that will be the message of the toast

     */
    public static void displayToast(final String message){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(appContext,message,Toast.LENGTH_LONG).show();
            }
        });
    }
    /**
     * Utility method to display a toast
     * @param message The string that will be the message of the toast
     * @param milisec A long denoting the amount of time before the toast is displayed
     */
    public static void displayToast(final String message, long milisec){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(appContext,message,Toast.LENGTH_LONG).show();
            }
        },milisec);
    }
    /**
     * Utility method that returns whether the passed in ConnectionType object indicates
     * a successful connection
     */
    public static boolean isConnectedType(ConnectionType type){
        return type==ConnectionType.INITIAL_PRODUCT_CONNECTION || type== ConnectionType.CONNECTION_RECONNECTED;
    }
    /**
     * Utility method that returns whether the passed in ConnectionType object indicates
     * a connection
     */
    public static boolean isDisconnectedType(ConnectionType type){
        return type==ConnectionType.PRODUCT_DISCONNECTED || type== ConnectionType.CONNECTION_DISCONNECTED;
    }

    /**
     * Convenience utility method to obtain the String value of a string resource id.
     * This method is used like everywhere. LIKE EVERY EVERYWHERE THAT EVER EXISTED.
     * @param resId The string resource id whose String value the caller wants to obtain
     * @return the String value of the resource id
     */
    public static String getStr(int resId){
        return appContext.getResources().getString(resId);
    }

    /**
     * Convenience utility method to obtain the int rgb value of a color resource id.
     * @param resId The color resource id whose rgb value the caller wants to obtain
     * @return an int representing the rgb value of the resource id
     */
    public static int getColor(int resId){
        return appContext.getResources().getColor(resId,null);
    }

    /**
     * Utility method to compute the passed in millisecond time value as a second value
     * @param mili Value representing amount of milliseconds
     * @return integer rounded to the nearest whole number representing the time in seconds (I think this is how the rounding works)
     */
    public static long miliToSec(long mili){
        return TimeUnit.SECONDS.convert(mili,TimeUnit.MILLISECONDS);
    }
}



/*
NOT_SUPPORTED
final
READY_TO_UPLOAD
final
UPLOADING
final
READY_TO_EXECUTE
final
EXECUTING
final
EXECUTION_PAUSED
final
DISCONNECTED
final
RECOVERING
final
UNKNOWN

 */