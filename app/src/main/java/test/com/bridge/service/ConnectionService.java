package test.com.bridge.service;

import android.app.Service;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
//import android.util.Log;

import dji.sdk.mission.waypoint.WaypointMissionOperator;
import test.com.bridge.UserAccount;
import test.com.bridge.UserProfile;
import test.com.bridge.callback.AllEvents.BatteryFrame;
import test.com.bridge.callback.CallbackList;
import test.com.bridge.callback.EventCallback.AuthCallback;
import test.com.bridge.utils.HelpMes;
import test.com.bridge.utils.Log;
import test.com.bridge.callback.EventCallback.ConnectionChangedCallback;
import test.com.bridge.callback.EventCallback.BatteryValuesCallback;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;
import test.com.bridge.callback.AllEvents.ConnectionType;

/**
 * Created by Amit on 7/11/2017.
 */

/**
 * This class which extends from android's Service class acts as the central hub for
 * all things to do with the connection to the DJI Drone.
 *
 * This class manages the connection with the DJI Drone, receives connection state changes from
 *  the DJI SDK and then broadcasts those state changes to its list of registered ConnectionChangedCallback's
 *
 *  Even if the MainActivity is destroyed, the Service will continue to exist and send telemetry data to the server
 *  Technically, DataFramePusher sends the telemetry data to the server and receives telemetry data from the drone,
 *  but DataFramePusher pretty much acts like an inner class of ConnectionService
 */
public class ConnectionService extends Service {
    private static String CLASSNAME = ConnectionService.class.getSimpleName();

    private ConnectionBinder m_binder;
    private boolean have_started_connecting;

    //private UserCallbacks user_callbacks;
    //TODO: ITS KIND OF REDUNDANT TO HAVE TO SPECIFY "BatteryValuesCallback" and "BatteryFrame" SEE IF THEERE IS A BETTER WAY TO USE GENERICS...
    //TODO: ...such that I only have to specify BatteryValuesCallback, and java implicitly knows that its callback element is BatteryFrame
    private CallbackList<BatteryValuesCallback,BatteryFrame>  batteryCallbs;

    //Same thing above applies for this CallbackList instance^
    private CallbackList<ConnectionChangedCallback,ConnectionType>  connectionCallbs;

    private static ConnectionService instance; //This is used for shorthand, in replace of 'ConnectionService.this'

    private ConnectionType lastConnectionType;

    private PaaSSession activeSession;

    private AuthCallback signInCallback;

    private ConnectionChangedCallback connectionCallback;

    /**
     *This method is called only once in the life-time of a Service.
     * This method initialize fields of this class to initial values, and adds an DataFramePusher instance
     * as registered ConnectionChangedCallback listener
     */
    @Override
    public void onCreate(){
        m_binder = new ConnectionBinder();
        have_started_connecting = false;

        connectionCallbs = new CallbackList<>();
        batteryCallbs = new CallbackList<>();

        lastConnectionType = ConnectionType.SERVICE_NOT_STARTED; //By default we will just assume the product is UNCONNECTED when this callback is passed to onRegister

        //The only reason this class has a reference to batteryCallbs is so other parts of the program can register BatteryValuesCallbacks with the Service.
        //Really, DataFramePusher will be the one triggering the callbacks in batteryCallbs
        DataFramePusher pusher = new DataFramePusher(this,batteryCallbs);

        activeSession = null;

        connectionCallbs.add(pusher); //pusher will get onEvent(ConnectionType) calls this class launches
        instance = this;

        //Setting this class' signInCallback object to listen for any sign-in or sign-out event
        signInCallback = new AuthCallback() {
            @Override
            public void onEvent(UserAccount.SignedInStatus event) {
                UserAccount account = UserAccount.getInstance();

                //If signed-out, then run ConnectionService's signout procedures
                if(account.isSignedOut()){
                    onSignOutOccured();
                }
            }
        };

        /**Right now I am using this class's connectionCallback listener to just register and deregister
        *  the LayoverWaypointListener.  Read about it to see what it does
        */
        connectionCallback = new ConnectionChangedCallback() {
            private LayoverWaypointListener layoverListener = new LayoverWaypointListener(instance);
            @Override
            public void onEvent(ConnectionType event) {
                //If we are connected, then register it as a waypoint listener
                if(HelpMes.isConnectedType(event)){
                    WaypointMissionOperator operator = DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
                    operator.addListener(layoverListener);
                }
                //If we are disconnected, then de-register it as a waypoint listener
                else if(HelpMes.isDisconnectedType(event)){
                    WaypointMissionOperator operator = DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
                    operator.removeListener(layoverListener);
                }
            }
        };
        addConnectionCallback(connectionCallback);
    }


    /** @pre: All permissions that this activity needs should be already granted (see HelpMes.java for utility methods to help dealing with permissions )
        onStartCommand() is called every time startService() is called for this service.
            This method will initiate the connection to the DJI Drone given it has not already started to do so
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        Log.d(CLASSNAME,"onStartCommand() called");
        if( !have_started_connecting){

            have_started_connecting = true;

            startNewPaaSSession();
        }
        return START_NOT_STICKY; //TODO(NOTE): For debugging purposes, if the system kills this process b/c of memory pressure, then I don't want the system to later on implicitly re-start this service if there are no pending intents (no pending service start requests) to give it
    }

    /**
     * When some other component (like MainActivity) calls bindService() this method is called
     * @param intent The intent passed to bindService()
     * @return an IBinder implementation from which the binded component can use to obtain an instance
     * of this Service and thus call methods of this Service
     */
    @Override
    public IBinder onBind(Intent intent){
        Log.d(CLASSNAME,"onBind() called");
        return m_binder;
    }


    //TODO: I don't think I need the synchronized keyword in the below getters and setters.  They were put in the past when I was doing all the synchronization in ConnectionService (this was messy)
    //Now I have each listener handle synchronization themselves

    /**
     * This Service's implementation of IBinder.  All it has is one method which can be used
     * to obtain an instance of this Service
     */
    public class ConnectionBinder extends Binder {
        public ConnectionService getConnectionService(){
            return ConnectionService.this;
        }
    }

    /**
     * returns the most recent connection state with the drone
     * @return a ConnectionType representing the current state of the connection with the Drone
     */
    public synchronized ConnectionType getLastConnectionType(){
        return lastConnectionType;
    }

    /**
     * Set the lastConnectionType field of this class.  (This class is synchronized on this Service instance)
     * @param type the new ConnectionType to set
     */
    private synchronized void setLastConnectionType(ConnectionType type){
        lastConnectionType = type;
    }

    /**
     * Adds a ConnectionChangedCallback to this class's list of registered ConnectionChangedCallback's
     * @param cb The ConnectionChangedCallback to add
     */
    public synchronized void addConnectionCallback(ConnectionChangedCallback cb){
        connectionCallbs.add(cb);
        cb.onEvent(getLastConnectionType());
    }

    /**
     * Remove the specified ConnectionChangedCallback from this class's list of these callbacks
     * @param cb The ConnectionChangedCallback callback object to remove
     */
    public synchronized void removeConnectionCallback(ConnectionChangedCallback cb){
        connectionCallbs.remove(cb);
    }

    /**
     * Adds a BatteryValuesCallback to this class's list of registered BatteryValuesCallback's
     * Note: DataFramePusher has a reference to the CallbackList of BatteryValuesCallback (batteryCallbs).
     *       DataFramePusher uses BatteryValuesCallbacks, all this class did was pass a reference of this CallbackList to DataFramePusher's constructor
     * @param cb The BatteryValuesCallback to add
     */
    public synchronized void addBatteryValuesCallback(BatteryValuesCallback cb){
        batteryCallbs.add(cb);
    }

    /**
     * Remove the specified BatteryValuesCallback from this class's list of these callbacks
     * @param cb The BatteryValuesCallback callback object to remove
     */
    public synchronized  void removeBatteryValuesCallback(BatteryValuesCallback cb){
        batteryCallbs.remove(cb);
    }

    /**
     * Initiate a connection to the DJI Drone
     */
    private void registerApp(){
        DJISDKManager.getInstance().registerApp(this,sdkManagerCallback);
    }

    /**
     * implemnts the onRegister() and onProductChange() methods stipulated by the DJISDKManager.SDKManagerCallback interface
     */
    private DJISDKManager.SDKManagerCallback sdkManagerCallback = new DJISDKManager.SDKManagerCallback() {
        /**
         * Called when the app is successfully registers with DJI's servers, or encounters an error doing so
         * @param error
         */
        @Override
        public void onRegister(DJIError error) {
            if(error == DJISDKError.REGISTRATION_SUCCESS){
                DJISDKManager.getInstance().startConnectionToProduct();
            }else{
                String regFailedMessage = "Error: DJI Registration failed-- " + error.getDescription();
                Log.d(CLASSNAME,regFailedMessage);
                Log.prettyE(regFailedMessage);
                //HelpMes.displayToast(regFailedMessage);
            }

        }

        /**
         * Called when there is a connection change.  Like when a new drone is connected to.
         * Note: see where and what the ConnectionType's I'm pushing in this method are,
         *       to see the different connection types this method can be called for
         * @param oldProduct
         * @param newProduct
         */
        @Override
        public void onProductChange(BaseProduct oldProduct, final BaseProduct newProduct) {
            Log.d(CLASSNAME,"onProductChange() called.  "/*thread- name: "+Thread.currentThread().getName() + "  id: "+Thread.currentThread().getId()*/);
            if(newProduct!=null){
                //TODO: check if there is a better way to get a unique identifier for a product that I can use to check if two product objects describe the same physical object
                if(oldProduct==null || !oldProduct.getModel().name().equals(newProduct.getModel().name())){
                    //Log.d(CLASSNAME,"newProduct was not null, looks like a connection is established");

                    String modelName = newProduct.getModel().getDisplayName();
                    String message = "(connection established)  product model name: "+ modelName;
                    Log.d(CLASSNAME,message);
                    Log.prettyI(message);
                    HelpMes.displayToast("Connected to " + modelName);


                    callConnectionChangedCB(ConnectionType.INITIAL_PRODUCT_CONNECTION);

                    //TestWaypointListener was used just to understand how waypoint callbacks worked, now waypoints are handled by PaaSSession
                    //DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator().addListener(new TestWaypointMissionListener());

                    newProduct.setBaseProductListener(new BaseProduct.BaseProductListener() {
                        @Override
                        public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent baseComponent, BaseComponent baseComponent1) {/*Do Nothing*/}

                        @Override
                        public void onConnectivityChange(boolean b) {
                            ConnectionType type = (b) ? ConnectionType.CONNECTION_RECONNECTED : ConnectionType.CONNECTION_DISCONNECTED;
                            callConnectionChangedCB(type);
                        }
                    });
                }
                else{
                    Log.d(CLASSNAME,"oldProduct and newProduct refer to the same product");
                }
            }
            else{
                if(oldProduct != null){
                    String message = "[connection ended] newProduct is null and oldProduct not null";
                    Log.d(CLASSNAME,message);
                    HelpMes.displayToast(message);
                    callConnectionChangedCB(ConnectionType.PRODUCT_DISCONNECTED);
                }
                else{ //NOTE: I don't know if this else statement will ever get called
                    String message = "[connection not started] newProduct and oldProduct are null";
                    Log.d(CLASSNAME,message);
                    HelpMes.displayToast(message);
                    //callConnectionChangedCB(ConnectionType.UNCONNECTED_T);
                    //throw new RuntimeException(message);
                }
            }
        }
    };
    /**
     * Triggers all connectionCallbs callback's
     * @param type
     */
    private void callConnectionChangedCB(final ConnectionType type){

        Log.d(CLASSNAME,"onConnectionChanged() event beginning; connection type: " + type.toString());

        //This should be the only place in this class (in the program) where setLastConnectionType() should be called
        setLastConnectionType(type);
        connectionCallbs.triggerEvents(type);
    }

    /**
     * Call this method when this class detects a sign-out event occured
     */
    private void onSignOutOccured(){
        closePaaSSession();
    }

    /**
     * This method is called to end the current DJI connection to the drone, if there is one
     * Example use cases are when a user signs out or the service is being destroyed (for whatever reason)
     */
    private void endCurrentDJIConnection (){
        //Log.d(CLASSNAME,"in endCurrentDJIConnection()");
        DJISDKManager djiManager = DJISDKManager.getInstance();
        BaseProduct product = djiManager.getProduct();
        if(product!=null && product.isConnected()){
            djiManager.stopConnectionToProduct(); //DOES THIS SEND A PRODUCT_DISCONNECTED OR CONNECTION_DISCONNECTED MSG ? Answer: no
            if(HelpMes.isConnectedType(lastConnectionType)){
                callConnectionChangedCB(ConnectionType.PRODUCT_DISCONNECTED);
            }
        }

        have_started_connecting = false;
        callConnectionChangedCB(ConnectionType.SERVICE_NOT_STARTED);

    }


    //TODO: USE THIS METHOD TO DEALLOCATE ANY RESOURCES YOU WON'T NEED ONCE THIS SERVICE INSTANCE IS BEING DESTROYED
    //(There is nothing to-do, I just wanted the bold zazzy blue text^ )
    /**
     * Called when the service is being destroyed. Deallocate all resources this server takes up here
     *
     * Note: The android system does not guarantee to call this method when it destroys the service.
     * Like if the service's linux process is being destroyed, then the service will be destroyed without calling this method
     */
    @Override
    public void onDestroy(){
        Log.d(CLASSNAME,"in onDestroy()");
        //endCurrentDJIConnection();
        closePaaSSession();
        UserAccount.getInstance().removeCallback(signInCallback);
    }

    /**
     * Called by system whenever the android device reaches certain memory usage thresholds
     * @param level
     */
    @Override
    public void onTrimMemory(int level){
        //If level is equal to this TRIM_MEMORY_COMPLETE, then the app is really running out of memory resources,
        // and this application's process is in danger of being terminated (this service runs in that process as of now 8/22/17, however I might change the process where the service runs (and forget to update these comments))
        if(level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE){
            Log.d(CLASSNAME,"NOTE: the Service's process is at memory status: TRIM_MEMORY_COMPLETE.  This means that if more memory isn't free'd the service's process will be killed");
        }
    }

    /**
     * @pre closeOldPaaSession() should have been called before to properly
     */
    public void startNewPaaSSession(){

        if(activeSession!=null){
            String msg = "ERROR: Can't create PaaSSession because it is already active.  Please end current PaaSSession first";
            Log.d(CLASSNAME,msg);

            throw new RuntimeException(msg); //TODO: This RuntimeException is only for debugging
            //return;
        }
        Log.d(CLASSNAME,"starting a new PaaS session");

        UserAccount account = UserAccount.getInstance();
        UserProfile loggedUser = account.getLoggedInUser();

        //Now signInCallback will listen to sign-in events (when I say 'sign-in', i mean 'signing-in' and 'signed-out' events as well)
        account.addEventCallback(signInCallback);

        //First get the server's data of the signed in user
        activeSession = new PaaSSession(this,loggedUser); //initializing a new PaaSSession instance for the service (and for the app in general)

        //Try to establish a connection to a usb-connected DJI Drone
        registerApp();
    }


    public void closePaaSSession(){

        /*The reason I have to post this on a handler is b/c since this methods stacktrace looked like this CallbackList.triggerEvents() -> signInCallback's onEvent() -> closePaaSSession()
          Thus calling UserAccount.removeCallback() while CallbackList.triggerEvents() is still executing will cause a ConcurrentModificationException in the iterator of CallbackList.triggerEvents()
          Thus a solution is to post removeCallback() on another thread so we can let CallbackList.triggerEvent() finish */

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                UserAccount.getInstance().removeCallback(signInCallback); //removing this class's field as a listener for sign-in events
            }
        });

        if(activeSession!=null){
            Log.d(CLASSNAME,"closing current PaaS session");
            activeSession.sessionEnded();
        }
        activeSession = null;
        endCurrentDJIConnection();
    }

    public PaaSSession getActivePaaSSession(){
        return activeSession;
    }
}
