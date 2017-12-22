package test.com.bridge.views;


import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import test.com.bridge.views.BaseFragment.OnFragmentCreated;
import test.com.bridge.UserAccount.SignedInStatus;
import test.com.bridge.UserAccount;
import test.com.bridge.UserAction;
import test.com.bridge.callback.AllEvents.BatteryFrame;
import test.com.bridge.callback.AllEvents.ConnectionType;
import test.com.bridge.callback.EventCallback.AuthCallback;
import test.com.bridge.utils.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import android.content.Intent;

import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;
import test.com.bridge.service.ConnectionService;
import test.com.bridge.utils.HelpMes;
import test.com.bridge.R;
import test.com.bridge.BridgeApplication;
import test.com.bridge.callback.EventCallback.BatteryValuesCallback;
import test.com.bridge.callback.EventCallback.ConnectionChangedCallback;

/**
 * This class is the MainActivity of the app.  There are only 2 activities in the whole application: SignInActivity and MainActivity
 * This class has two fragments: DisplayFragment and LoggerFragment.
 *     - Together, those compose the UI of this activity
 */
public class MainActivity extends BaseActivity implements ServiceConnection,OnFragmentCreated {


    private final int PERMISSION_REQ_ID = 1;
    private ConnectionService service;

    private DisplayFragment displayFragment;
    private Menu optionsMenu;

    private AuthCallback signInCallback;

    private Object lock = new Object(); //To provide mutual exclusion when creating the battery text views and when a frame of battery data wants to populate the views

    /**
     * Sets fields to point to views from the activity_main layout.
     * Also, creates a Sign-in callback that will be triggered when any change in the app's sign-in status occurs (the callback is registered in onStart() and de-registered in onStop() respectively)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //DEBUG
        super.onCreate(savedInstanceState);
        Log.d(CLASS,"onCreate() method called");
        setContentView(R.layout.activity_main);
        service = null;
        optionsMenu = null;

        displayFragment = (DisplayFragment) getFragmentManager().findFragmentById(R.id.display_fragment);
        displayFragment.setFragmentCreatedListener(this);
        signInCallback = new AuthCallback() {
            @Override
            public void onEvent(SignedInStatus status) {

                UserAccount userAccount = UserAccount.getInstance();
                //UserAccount.SignedInStatus status = userAccount.getSignInStatus();
                //Log.d(CLASS,"signInCallback's onResponse() method called; signInStatus: " + status.name());

                displayFragment.refreshSignInStatus();
                updateOptionsMenu();
            }
        };
        processIntentForUserAction();

        BridgeApplication.getInstance().tryStartService();
        HelpMes.app_requestRequiredPermissions(this,PERMISSION_REQ_ID);
    }

    /**
      Note: I'm not doing the refresh-button thing anymore.  Instead we will constantly try to get the user's
     * (incomplete)server data in the background until the server returns it completed
     * Thus, the refreshBtn mentioned in this method will never be visible, so its OnClickListener code will never be run
     */
    @Override
    public void onFragmentCreated(Fragment fragment){
        if (displayFragment==fragment){
            //Not doing anything right now
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment){

    }

    /**
     * Run procedures for this Activity when it is going to become visible to user
     */
    @Override
    public void onStart(){
        Log.d(CLASS,"onStart() called");
        super.onStart();
        updateOptionsMenu();  //Everytime we resume this activity, update menu options to resemble the current state of the application

        UserAccount.getInstance().addEventCallback(signInCallback);
        //BIND_AUTO_CREATE is used to start the service (if not started) and ensure that by approximetely the time
        // MainActivity has resumed, it will be binded to the service and registered itself as a
        // connection_changed event receiver with the service
        bindService(new Intent(this,ConnectionService.class),this, Context.BIND_AUTO_CREATE);

    }

    /**
     * Run procedures for this Activity right when it becomes visible to user.
     * Note: onResume() is always called after onStart().  Look at activity lifecycle callbacks
     */
    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    /**
     * Called when the Activity is being brought out of focus.
     * Here, we want to deregister from any callbacks this activity was registered with
     */
    @Override
    public void onStop(){
        Log.d(CLASS,"onStop() method called");
        super.onStop();
        if(service!= null){
            service.removeBatteryValuesCallback(battCallback);
            service.removeConnectionCallback(connCallback);
            service=null;
        }
        UserAccount.getInstance().removeCallback(signInCallback);
        unbindService(this);
    }

    /**
     * Called when Activity is being destroyed. Here we want to let go of any resources this Activity takes
     *
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(CLASS,"onDestroy() method called");

        displayFragment = null; //Probably does not matter that I do this.
    }

    /**
     * Called once the user has responded to all the permissions requested from the HelpMes.app_requestRequiredPermissions() call
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == PERMISSION_REQ_ID) {
            boolean hasAllPermissions = HelpMes.app_hasAllRequiredPermissions();
            if (hasAllPermissions) {
                //Now, all permissions this app has requested have been given by the user
                String message = "Excellent, All permissions granted";
                Log.d(CLASS, message);

            } else {
                String message = "All permissions were not granted.  Please restart app and grant all permissions";
                Log.prettyE(message);
                displayFragment.setStatusText(getStr(R.string.permissions_denied));
            }
        }
    }

    /**
     * Called by the system once the bindService() call successfully binds to the service
     * @param name
     * @param ibinder
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder ibinder) {
        Log.d(CLASS,"onServiceConnected() called");
        //This cast is always guaranteed to work, b/c there is only one service this ServiceConnection object is binding to, ConnectionService
        ConnectionService.ConnectionBinder binder = (ConnectionService.ConnectionBinder)ibinder;
        ConnectionService serv = binder.getConnectionService();
        service = serv;
        service.addConnectionCallback(connCallback);
        service.addBatteryValuesCallback(battCallback);

    }

    /**
     * Called by the system if the service unexpectedly disconnects from the activity (Like the process the service was running in terminates).
     * However, the service may still be binded to the Activity, so once the service is recreated, onServiceConnected() will automatically be called
     *
     * Note: As of now, the service runs the same process as the Activity, this method is probably never going to
     * be called, because if the service's process is destroyed (b/c of memory shortages) then the Activity would be destroyed to
     * @param name
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        //Log.d(CLASS_TAG,"onServiceDisconnected() called, note if the service this ServiceConnection was binded to disconnected, that means process hosting the service crashed or is killed (but the services process is this applications process).  However, the binding is still valid, so as soon as the service is restarted(but have you setup the Service to restart in these situations (ie. like when the system kills the process of the service due to low resources)), I will get a onServiceConnected() callback event");
        Log.d(CLASS,"------------onServiceDisconnected() called--------------");
        Log.d(CLASS,"NOTE NOTE NOTE: if onServiceDisconnected() called, then I think it means that the service and app forcly disconnected, like the service crashed or shutdown by system due to low resources");
        //
    }

    /**
     * The field representing this class's implementation of BatteryValuesCallback.
     * All we do is just pass the frame to this activity's displayFragment
     */
    private BatteryValuesCallback battCallback = new BatteryValuesCallback() {
        @Override
        public void onEvent(final BatteryFrame frame) {
            UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    synchronized (lock){
                        displayFragment.updateBatteryLayout(frame);
                    }
                }
            });
        }
    };
    /**
     * The field representing this class's implementation of ConnectionChangedCallback.
     * The onEvent() method handles every possible ConnectionType event that can be recieved.
     */
    private ConnectionChangedCallback connCallback = new ConnectionChangedCallback() {
        @Override
        public void onEvent(ConnectionType type) {
            synchronized (lock){
                if(type==ConnectionType.INITIAL_PRODUCT_CONNECTION || type == ConnectionType.CONNECTION_RECONNECTED){
                    //reinitializing each batteries individual text view
                    BaseProduct product = DJISDKManager.getInstance().getProduct();

                    //Log.d(CLASS,"in ConnectionChangedCallback;  product's battery count: " + product.getBatteries().size());

                    displayFragment.reinit_ListandLayout(product.getBatteries().size());

                    //Resetting the status text view to display the connected product's name
                    String modelName = null;
                    if(product.getModel()!=null){
                        modelName = product.getModel().getDisplayName();
                    }else{
                        modelName = "unresolved named aircraft";
                    }
                    displayFragment.setStatusTextUI("Connected to " + modelName);

                }else if( type==ConnectionType.PRODUCT_DISCONNECTED || type==ConnectionType.CONNECTION_DISCONNECTED){

                    displayFragment.reinit_ListandLayout(0);
                    displayFragment.setStatusTextUI(R.string.disconnected);
                }else if (type == ConnectionType.SERVICE_NOT_STARTED) { //This means the PaaS session has ended
                    displayFragment.reinit_ListandLayout(0);
                    displayFragment.refreshSignInStatus(); //We want status text view to probably say "Please Sign In!".  We don't want status text view to say "disconnected" b/c that would have been caused the above categories^
                }
            }
        }
    };

    /**
     * Update the ActionBar's option's menu to match the current sign-in status of the app
     */
    private void updateOptionsMenu(){
        SignedInStatus status = UserAccount.getInstance().getSignInStatus();
        Log.d(CLASS,"in updateMenuOptionsDisplay, sign-in-status: " + status);
        if(optionsMenu==null){
            return;
        }
        if(status==SignedInStatus.SIGNING_IN){
            mm_setOptionVisibility(0);  //setting no button visible
        }
        else if(status==SignedInStatus.SIGNED_IN){
            mm_setOptionVisibility(2);  //setting sign-out button visible
        }
        else if(status==SignedInStatus.SIGNED_OUT){
            mm_setOptionVisibility(1); //setting sign-in button visible
        }
    }

    /**
     * Helper method to set the sign-in button and sign-out button's visibility in one go, using the bit-mask argument
     * @param mask
     */
    private void mm_setOptionVisibility(final int mask){
        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                optionsMenu.findItem(R.id.sign_in_item).setVisible((mask&1)>0);
                optionsMenu.findItem(R.id.sign_out_item).setVisible((mask&2)>0);
            }
        });

    }

    /**
     * Called every time the ActionBar's option menu is being created by the system.
     * This method adds views to menu in order to customize it.
     * @param menu The Menu object representing the option's menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_toolbar_list,menu); //inflating the first argument to be a child of the menu
        optionsMenu = menu;
        //updateMenuOptionsDisplay();
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called every time an item in the option's menu is selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Log.d(CLASS,"in optionsItemSelected");
        switch(item.getItemId()){
            case R.id.sign_out_item:
                //Doing sign-out procedures
                doSignOutProcedures();
                return true;
            case R.id.sign_in_item:
                //Starting sign-in activity
                Intent intent = new Intent(this,SignInActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * Initiates sign-out procedures.
     *
     * Note: This method is called by onOptionsItemSelected(), thus by the time this method is called
     * onStart() method will have run and the activity should be (but might possibly be not) binded to the service,
     * so the ConnectionService field should be point to the ConnectionService object
     */
    private void doSignOutProcedures(){
        Log.d(CLASS,"called doSignOutProcedures()");
        UserAccount userAccount = UserAccount.getInstance();
        userAccount.signOut_Auth();
    }

    /**
     * Helper method to set the text of the specified TextView
     * Note: Does not need to be called in UI Thread.  Runs in UI Thread internally
     * @param text The text to set to the text view
     * @param textView The TextView which the text will be set on
     */
    private void setTextOfTextView(final String text, final TextView textView){
        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }
    /**
     * Helper method to set the text of the specified TextView
     * Note: Does not need to be called in UI Thread.  Runs in UI Thread internally
     * @param resID integer id of a resource string whose contents will be set to the text view
     * @param textView The TextView which the text will be set on
     */
    private void setTextOfTextView(final int resID, final TextView textView){
        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(getStr(resID));
            }
        });
    }

    /**
     * Called when this activity receives an intent from a startActivity() method after an instance of it
     * is already running.
     * Note: you need to call startActivity() with at least the Intent flag FLAG_ACTIVITY_SINGLE_TOP
     *      in order to make the system call onNewIntent() of an existing activity rather than starting another instance of the Activity
     * Note: This method was currently only used for the refresh button feature.  Since that
     *      feature is no longer being implemented, this method will never  be called
     * @param intent
     */
    @Override
    public void onNewIntent(Intent intent){
        setIntent(intent);
        processIntentForUserAction();
    }

    /**
     * Process's the current intent for any pending UserAction's
     * Note: This method will never actually be called
     */
    private void processIntentForUserAction(){
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(UserAction.USER_ACTION_BUNDLE);

        if(bundle!=null){
            if(bundle.getInt(UserAction.UACTION_TYPE)==UserAction.USER_DOWNLOADED_INVALID_ACTION){
                //NOTE: We are not supporting this User Action anymore

            }
        }
    }

}
