
package test.com.bridge;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
//import android.util.Log;
import test.com.bridge.callback.EventCallback.AuthCallback;
import test.com.bridge.service.ConnectionService;
import test.com.bridge.utils.HelpMes;
import test.com.bridge.utils.Log;

import android.widget.Toast;
import test.com.bridge.UserAccount.SignedInStatus;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.log.DJILog;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;
import test.com.bridge.storage.MyLocalStorage;
import test.com.bridge.utils.NotificationPusha;

/**
 * Created by Amit on 6/27/2017.
 */

/**
 * This class represents the Application of this android app.
 * It's onCreate() method is called before anything else in the app is called (before activity's, background services, etc.).
 */
public class BridgeApplication extends Application {

    private static String CLASSNAME = BridgeApplication.class.getSimpleName();

    private static BridgeApplication instance;
    private Handler UIHandler;
    private boolean hasConnectedOnce;


    public boolean hasConnectedOnce(){
        return hasConnectedOnce;
    }

    /**
     * Here we initialize the static use-case classes and the singleton classes.
     * Also, we register a AuthSumulator.Callback to be fired every time a sign-in change event occurs.
     * We also try an initial sign-in with the locally stored UserProfile, if that fails or is not possible,
     * then we launch a notification asking the user to sign in.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        HelpMes.initialize(this);
        Log.initialize();

        //Initializing all the singleton instance classes
        MyLocalStorage.getInstance();
        NotificationPusha.getInstance();
        UserAccount.getInstance();

        
        UIHandler = new Handler(Looper.getMainLooper());
        hasConnectedOnce = false;

        boolean perms = HelpMes.app_hasAllRequiredPermissions();

        if (perms && Build.VERSION.SDK_INT <= 28) {
            //DJISDKManager.getInstance().registerApp(this, mDJISDKManagerCallback);
        }

        final UserAccount userAccount = UserAccount.getInstance();

        //Callback that will be fired every time a sign-in state change event occurs
        userAccount.addEventCallback(new AuthCallback() {
            @Override
            public void onEvent(SignedInStatus status) {
                //NOTE: status would point to the same object as UserAccount.getSignInStatus(). So its safe to use isSignedIn() and isSignedOut() and isSigningIn()
                if(userAccount.isSignedIn()){
                    Log.prettyI("Good: "+userAccount.getLoggedInUser().getEmail()+ " is signed in!");

                    //Now that someone is signed in, try to start the service
                    tryStartService();

                    //This statement was just for debugging without having to connect to a drone
                    //PaaSSession paaSSession = new PaaSSession(null,userAccount.getLoggedInUser());
                }else if(userAccount.isSignedOut()){
                    Log.prettyI("User is signed out");
                }
            }
        });
        UserProfile storedProfile = UserAccount.readLocalUserProfile();
        if(storedProfile.getEmail() == null){ //There was no value(username) associated with the above key
            //send the sign in notification
            UserAction.userAction(UserAction.SIGN_IN_ACTION,null);
        }
        else{
            //Authenticating using the current stored username and password, if it fails then send the sign in notification
            userAccount.signIn_Auth(storedProfile,new AuthCallback(){
                @Override
                public void onEvent(SignedInStatus status){
                    if(!userAccount.isSignedIn()){
                        //send the sign-in notification
                        UserAction.userAction(UserAction.SIGN_IN_ACTION,null);
                    }
                }
            });
        }

    }

    /**
     * Get the instance of BridgeApplication
     * @return the singleton instance of this app's Application
     */
    public static BridgeApplication getInstance() {
        return instance;
    }


    /**
     * This DJISDKManager.SDKManagerCallback is responsible for establishing an 'initial' connection with the DJI Drone.
     * I say 'initial' because after we have connected once, then the service will be started (via tryStartService())
     * and the service will handle all interactions with the DJI Drone.
     * This is why you see a SDKManagerCallback in both BridgeApplication and ConnectionService
     *
     * Note: I don't think the above is necessary anymore.  (Make sure you read the developer's guide on BridgeApplication)
     */
    private DJISDKManager.SDKManagerCallback mDJISDKManagerCallback = new DJISDKManager.SDKManagerCallback() {
        private int connectionTries;
        private final int MAX_CONN_TRIES = 2;

        /**
         * Called when the app is successfully registers with DJI's servers, or encounters an error doing so
         * @param error
         */
        @Override
        public void onRegister(DJIError error) {
            if (error == DJISDKError.REGISTRATION_SUCCESS) {
                connectionTries = 0;
                DJILog.e("App registration", DJISDKError.REGISTRATION_SUCCESS.getDescription());
                DJISDKManager.getInstance().startConnectionToProduct();
            } else {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error: Application class did not register succesfully", Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            Log.d(CLASSNAME, error.getDescription());
        }

        /**
         * Called whenever a new product has been connected to the DJI Drone.
         * @param oldProduct The old product, or null if there was no old product
         * @param newProduct The new product, or null of there is no new product
         */
        @Override
        public void onProductChange(BaseProduct oldProduct, BaseProduct newProduct) {

            Log.d(CLASSNAME,"onProductChange() in BridgeApplication object called");
            if (newProduct != null) {
                if(oldProduct == null){
                    hasConnectedOnce = true;

                    String message = null;
                    if(newProduct.getModel()!=null){
                        message = String.format("Connection succesfull to product: %s", newProduct.getModel().getDisplayName());
                    }else{
                        message = "connected to an unresolved named aircraft";
                    }

                    Log.d(CLASSNAME, message);
                    HelpMes.displayToast("(BridgeApplication) "+message);
                    tryStartService(); //runs code to start the service
                }
                //product.setBaseProductListener(mDJIBaseProductListener);
            }else{
                if(oldProduct == null){
                    connectionTries++;
                    if(connectionTries<MAX_CONN_TRIES){
                        String msg = "current call of onProductChange() indicated an unsuccesful connection";
                        Log.d(CLASSNAME,msg);
                        //do nothing
                    }else{
                        String msg = "Connection to product failed";
                        HelpMes.displayToast(msg);
                        Log.d(CLASSNAME,msg);
                    }
                }
                else{
                    String message = "Error: Connection disconnected";
                    Log.d(CLASSNAME,message);
                    HelpMes.displayToast(message);
                }
            }
        }
    };

    /**
     * This method starts ConnectionService once the following conditions have met
     *  1) An initial connection to the DJI Drone was established by BridgeApplication
     *  2) A user is signed in
     *  3) All permissions this app requires have been requested
     *
     *  Note I don't think 1) is needed, thus its commented out in the if-statement.  However,
     *  you might not be able to connect to the drone because 1) was needed, and I was wrong.
     *  So be aware of this.
     */
    public void tryStartService(){
        boolean userSignedIn = UserAccount.getInstance().isSignedIn();

        if(/*hasConnectedOnce &&*/ userSignedIn && HelpMes.app_hasAllRequiredPermissions()){
            Intent intent = new Intent(instance,ConnectionService.class);
            startService(intent);
        }
    }

}
