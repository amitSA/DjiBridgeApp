package test.com.bridge.views;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import test.com.bridge.BatteryTuple;
import test.com.bridge.R;
import test.com.bridge.UserAccount;
import test.com.bridge.callback.AllEvents.BatteryFrame;
import test.com.bridge.utils.HelpMes;
import test.com.bridge.utils.Log;

/**
 * Created by Amit on 8/15/2017.
 */

/**
 * The DisplayFragment occupies most of the UI space on the screen.  It is responsible with
 * showing the connection state of the DJI drone and battery state telemetry data to the screen
 */
public class DisplayFragment extends BaseFragment {

    private TextView statustextV;

    private LinearLayout battery_layout;


    //The following 2 methods were just overridden so I could Log lifecycle events of this Fragment
    @Override
    public void onAttach(Context c){
        Log.d5(CLASS,"onAttach() called");
        super.onAttach(c);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.d5(CLASS,"onCreate() called");
        super.onCreate(savedInstanceState);
    }

    /**
     * Returns an inflated view.  The caller of this method (the system) will set that view as the root view of this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
        Log.d5(CLASS,"onCreateView() called");
        return inflator.inflate(R.layout.display_frag,container,false);
    }

    /**
     * Here we retrieve and store references of view elements to fields of this class
     * @param savedInstanceState
     */
    @Override
    public void onViewStateRestored(Bundle savedInstanceState){
        Log.d5(CLASS,"onViewStateRestored() called");


        statustextV = (TextView) getView().findViewById(R.id.status_textview);
        battery_layout = (LinearLayout) getView().findViewById(R.id.textview_battery_group);

        super.onViewStateRestored(savedInstanceState);

    }

    /**
     * Run procedures for the onStart() lifecycle callback
     */
    @Override
    public void onStart(){
        super.onStart();
        refreshSignInStatus(); //Whenever the fragment is resuming,we should update the UI's sign-in status to resemble the app's internal sign-in status
    }

    private static String FORMAT_TELEMETRY = "voltage: %d current: %d temperature: %f";
    private static String NO_DATA = "voltage: NaN current: NaN temperature: NaN";

    /**
     * Updates the UI with the passed-in frame of telemetry data
     * @pre This method should be called in the UI Thread and the most recent call to reinit_ListandLayout(int) must have been made with the correct number of batteries
     * @param frame Represents one frame of telemetry data
     */
    public void updateBatteryLayout(BatteryFrame frame){
        BatteryTuple[] batteryAvgs = frame.batteryAverages;
        int cnt = battery_layout.getChildCount();
        if(cnt != batteryAvgs.length){
            String msg = "in updateBatteryLayout(), batteryAvgs array is of different lengths than the battery_textview.  They should be in-sync soon.  battery layout length: " + cnt + "  batteryAvgs length: " + batteryAvgs.length;
            Log.d(CLASS,msg);
            return;
            //throw new IllegalArgumentException();
        }
        for(int i = 0;i<cnt;i++){
            TextView textView = (TextView)battery_layout.getChildAt(i); //TODO:QUESTION. If I do a getChildAt() call on a view, should the view be in the UI thread

            String buff = NO_DATA;
            if(batteryAvgs[i]!=null) {
                buff = String.format(Locale.US, FORMAT_TELEMETRY, batteryAvgs[i].getVoltage(), batteryAvgs[i].getCurrent(), batteryAvgs[i].getTemperature());
            }
            textView.setText(buff);
        }

    }

    /**
     * Sets the connection status text
     * This needs to be called on UI Thread
     * @param message
     */
    public void setStatusText(String message){
        statustextV.setText(message);
    }

    /**
     * Sets the connection status text.  Use setStatusText(String) if you are allready running on the UI Thread
     * this method doesn't need to be called in the UI thread, it uses the UI thread internally
     * @param message
     */
    public void setStatusTextUI(final String message){
        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                statustextV.setText(message);
            }
        });
    }

    /**
     * Same as setStatusTextUI(String) except argument is an integer resource string id
     * @param id The integer resource string id
     */
    public void setStatusTextUI(int id){
        setStatusTextUI(HelpMes.getStr(id));
    }

    /**
     * re-initializes the TextView's that will display the Telemetry data.
     * If you want to remove all TextViews, then call this method with numBatteries=0 (useful if disconnected from DJI Drone)
     * else, anytime you want to set up the UI to receive Battery telemetry data, this method
     *       must be called with the number of batteries passed as an argument
     *
     * This method does not need to be called in UI Thread
     * @param numBatteries
     */
    public void reinit_ListandLayout(int numBatteries){
        //numBatteries = numBatteries>0 ? 1 : 0; //NEW CODE

        Context appContext = HelpMes.getApplicationContext();
        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                battery_layout.removeAllViews();
            }
        });
        for(int i = 0;i<numBatteries;i++){
            final TextView battery_view = (TextView) LayoutInflater.from(appContext).inflate(R.layout.batterytv,null);
            UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    battery_layout.addView(battery_view);
                }
            });
        }

    }

    /**
     * Call to update the UI so it displays the current sign-in status
     */
    public void refreshSignInStatus(){
        UserAccount userAccount = UserAccount.getInstance();
        UserAccount.SignedInStatus status = userAccount.getSignInStatus();
        if(status== UserAccount.SignedInStatus.SIGNED_IN){
            this.setStatusTextUI(R.string.not_connected);
        } else if (status== UserAccount.SignedInStatus.SIGNED_OUT){
            this.setStatusTextUI(R.string.sign_in);
        }else if (status== UserAccount.SignedInStatus.SIGNING_IN) {
            this.setStatusTextUI(R.string.signing_in);
        }
    }

}