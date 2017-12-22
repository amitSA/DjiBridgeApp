package test.com.bridge.views;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import test.com.bridge.R;

/**
 * Created by Amit on 7/5/2017.
 */

/**
 * All Activities in this application should inherit from BaseActivity.  It defines some methods
 * and fields that base class implementations can use for convenience.
 *
 * In the future, any Activity resources/abilities should be added to this class if they should be
 * shared among all Activities
 */
public class BaseActivity extends AppCompatActivity {

    protected final String CLASS = this.getClass().getSimpleName();

    protected Handler UIHandler; //access specifier is protected so base class implementations can post to UI thread w/o always having to create a local handler object

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //If this boolean is true, then force the activity to have a landscape orientation
        //The boolean will be true for screen sizes that are large
        if(getResources().getBoolean(R.bool.activity_force_landscape)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        //Initializing Handler
        UIHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * See HelpMes.getStr(int).  This method does the same exact thing, except I included it in
     * BaseActivity for convenience
     */
    protected String getStr(int id){
        return getResources().getString(id);
    }

    /**
     * See HelpMes.displayToast(String).  This method does the same exact thing, except I included it in
     * BaseActivity for convenience
     * @param message
     */
    public void displayToast(final String message){
        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
        });
    }
    /**
     * See HelpMes.displayToast(String,long).  This method does the same exact thing, except I included it in
     * BaseActivity for convenience
     * @param message
     */
    public void displayToast(final String message, long milisec){
        UIHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
        },milisec);
    }
}
