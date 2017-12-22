package test.com.bridge.service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import java.util.concurrent.atomic.AtomicBoolean;

import dji.common.error.DJIError;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionState;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.common.util.CommonCallbacks;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.sdkmanager.DJISDKManager;
import io.swagger.client.model.InlineResponse2002;
import io.swagger.client.model.InlineResponse2003;
import io.swagger.client.model.InlineResponse2006;
import test.com.bridge.R;
import test.com.bridge.UserProfile;
import test.com.bridge.callback.AllEvents.ConnectionType;
import test.com.bridge.callback.AllEvents.NetworkCallbackEvent;
import test.com.bridge.callback.EventCallback.ConnectionChangedCallback;
import test.com.bridge.callback.RepeatableNetworkCallback;
import test.com.bridge.clientbridge.ClientDispatcher;
import test.com.bridge.utils.HelpMes;
import test.com.bridge.utils.Log;

/**
 * Created by Amit on 8/27/2017.
 */

public class PaaSSession implements ConnectionChangedCallback {

    private static String CLASSNAME = PaaSSession.class.getSimpleName();

    private PaaSSession instance;
    private ConnectionService parent;
    private UserProfile signedUser; //technically I can just call UserAccount.getLoggedInUser(),
                                       // but for the sake of reducing coupling, I am storing the
                                       //signed-in-user as the user this PaaSession object was initialized with

    private InlineResponse2002 userInfo;
    private InlineResponse2006 sessionStatus;
    private InlineResponse2003 vehicleStatus;  //NOTE: I'm not even sure if we need vehicleStatus

    private long choosenVehicleID;

    private boolean isActivePaaSSession;

    final static long INIT_DELAY_TIME = 5 * 1000;
    final static long INCREMENT = 5 * 1000;
    final static long MAX_DELAY_TIME = 30 * 1000;


    //Fields for waypoints


    public PaaSSession(ConnectionService parent, UserProfile signedUser){
        this.instance = this;

        this.parent = parent;
        this.signedUser = signedUser;

        userInfo = null;
        sessionStatus = null;
        vehicleStatus = null;
        isActivePaaSSession = true;

        //add this PaaSSession instance as a listener for connection changes with the drone
        parent.addConnectionCallback(this);

        //Getting the server's info on the user
        httpGETUserInfo(INIT_DELAY_TIME);
    }

    /**NOTE: PaasSession’s GET request chain should have been implemented using Future’s, not a chain of callbacks b/c this could really quickly eat up the stack.
     *
     * Gets the user's server data.  This data must be retrieved before any telemetry or waypoint data is pushed to the server
     * @post If all is goes right, userInfo will be initialized
     */
    private void httpGETUserInfo(long initialDelay){
        final ClientDispatcher httpClient = ClientDispatcher.getInstance();
        RepeatableNetworkCallback<InlineResponse2002> callback = new RepeatableNetworkCallback<InlineResponse2002>(initialDelay,INCREMENT,MAX_DELAY_TIME) {
            @Override
            public void toRun(RepeatableNetworkCallback<InlineResponse2002> myself) {
                if(isActivePaaSSession()){
                    httpClient.httpGETUserInfo(instance,myself);
                }
            }
            @Override
            public boolean onEventCall(NetworkCallbackEvent<InlineResponse2002> event) {
                boolean isError = false;
                StringBuilder prettyMsg = new StringBuilder();
                Exception e = event.getException();
                InlineResponse2002 response1 = event.getResponse(); //response1 is the userInfo
                if(e!=null){
                    prettyMsg.append(HelpMes.getStr(R.string.load_users_info_err));
                    isError = true;
                }else if(response1.getVehicles().size()<1){
                    prettyMsg.append(HelpMes.getStr(R.string.users_has_no_vehicle_err));
                    isError = true;
                }
                if(isError){
                    prettyMsg.append("  Retrying in ").append(HelpMes.miliToSec(getDelayTime())).append(" seconds");
                    Log.prettyE(prettyMsg.toString());
                }else{
                    userInfo = response1;
                    httpGETSessionStatus(getDelayTime()); //Go to the next GET request in this sequence
                }
                return isError;
            }
        };

        httpClient.httpGETUserInfo(instance,callback);
    }

    /**
     * @pre userInfo should be initialized.  This method is meant to be called after userInfo is initialized
     * @post If all is goes right, sessionStatus will be initialized
     */
    private void httpGETSessionStatus(long initialDelay){
        final RepeatableNetworkCallback<InlineResponse2006> callback = new RepeatableNetworkCallback<InlineResponse2006>(initialDelay,INCREMENT,MAX_DELAY_TIME) {
            @Override
            public void toRun(RepeatableNetworkCallback<InlineResponse2006> myself) {
               if(isActivePaaSSession()){
                   //httpGETSessionStatus(); //call this method again in the case of a previous error in this method
                   httpGETUserInfo(getDelayTime()+INCREMENT); //actually, restart this sequence of GET requests from the beginning, since there was an error now, the user info's data on the server might be updated when the error is fixed on the server's side
               }
            }

            @Override
            public boolean onEventCall(NetworkCallbackEvent<InlineResponse2006> event) {
                boolean isError = false;
                StringBuilder prettyMsg = new StringBuilder();
                Exception e = event.getException();
                InlineResponse2006 response = event.getResponse();
                if(e==null && response==null){
                    //By my own convention, I know the person prematurely closed the dialog in this case
                    prettyMsg.append(HelpMes.getStr(R.string.vehicle_dialog_closed_err));
                    isError = true;
                }else if(e!=null){
                    prettyMsg.append(HelpMes.getStr(R.string.load_vehicle_session_err));
                    isError = true;
                }
                if(isError){
                    prettyMsg.append("  Retrying in ").append(HelpMes.miliToSec(getDelayTime())).append(" seconds");
                    Log.prettyE(prettyMsg.toString());
                }else{
                    sessionStatus = response;
                    httpGETVehicleStatus(getDelayTime());
                }
                return isError;
            }
        };

        String [] vehicleList = new String[userInfo.getVehicles().size()];
        for(int i = 0;i<vehicleList.length;i++){
            vehicleList[i] = userInfo.getVehicles().get(i).getName();
        }

        final AtomicBoolean flag = new AtomicBoolean(false);
        Context context = HelpMes.getApplicationContext();
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(R.string.choose_vehicle_dialog_title);
        dialogBuilder.setAdapter(new ArrayAdapter<String>(context, R.layout.vehicle_textview,vehicleList),
        new DialogInterface.OnClickListener(){
            @Override
            public void onClick(final DialogInterface dialog, final int index) {
                Log.d4(CLASSNAME,"index for clicked-list item: " + index);
                flag.set(true);
                dialog.dismiss();

                choosenVehicleID = userInfo.getVehicles().get(index).getId();

                ClientDispatcher client = ClientDispatcher.getInstance();
                client.httpGETSessionStatus(instance,callback);
            }
        });
        dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(!flag.get()){
                    callback.onEvent(new NetworkCallbackEvent<InlineResponse2006>(null,null));
                }
            }
        });
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = dialogBuilder.create();
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.show();
            }
        });
    }

    /**
     * @pre sessionStatus should be initialized.  This method is meant to be called after sessionData is initialized
     * @post If all is goes right, vehicleStatus will be initialized
     */
    private void httpGETVehicleStatus(long initialDelay){
        RepeatableNetworkCallback<InlineResponse2003> callback = new RepeatableNetworkCallback<InlineResponse2003>(initialDelay,INCREMENT,MAX_DELAY_TIME) {
            @Override
            public void toRun(RepeatableNetworkCallback<InlineResponse2003> myself) {
                if(isActivePaaSSession()){
                    httpGETUserInfo(getDelayTime()+INCREMENT); //estart this sequence of GET requests from the beginning, since there was an error now, the user info's data on the server might be updated when the error is fixed on the server's side
                }
            }

            @Override
            public boolean onEventCall(NetworkCallbackEvent<InlineResponse2003> event) {
                boolean isError = false;
                StringBuilder prettyMsg = new StringBuilder();
                Exception e = event.getException();
                InlineResponse2003 response = event.getResponse();
                if(e!=null){
                    prettyMsg.append(HelpMes.getStr(R.string.load_users_vehicle_err));
                    isError = true;
                }
                if(isError){
                    prettyMsg.append("  Retrying in ").append(HelpMes.miliToSec(getDelayTime())).append(" seconds");
                    Log.prettyE(prettyMsg.toString());
                }else{
                    vehicleStatus = response;
                    Log.prettyI(HelpMes.getStr(R.string.users_profile_bueno));
                }
                return isError;
            }
        };
        ClientDispatcher client = ClientDispatcher.getInstance();
        client.httpGETVehicleStatus(instance,callback);
    }

    private AtomicBoolean gettingPushingData;  //true if we are in the process of already getting and posting waypoints
    private AtomicBoolean waypointMissionPushed; //true if the current waypoint mission has been posted

    private CommonCallbacks.CompletionCallback downloadCallback;
    private NetworkCallback<String> postWaypointCallback;
    private WaypointMissionOperatorListener waypointListener;

    private  NetworkCallback<String> deleteWaypointCallback;

    private void initializeWaypoints(){
        WaypointMissionOperator operator = DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
        gettingPushingData = new AtomicBoolean(false);
        waypointMissionPushed = new AtomicBoolean(false);

        postWaypointCallback = getWaypointPOSTCallback();
        deleteWaypointCallback = getWaypointDELETECallback();

        downloadCallback = getDownloadCallback(operator);
        waypointListener = getWaypointListener(operator);

        //TODO: remove this boolean later
        boolean setTrueLater = true;  //I needed to debug without waypoints, so I created this.

        //First try to download loaded waypoints if they exist
        WaypointMissionState currentState = operator.getCurrentState();
        if(canDownloadAtThisState(currentState)){ //If I can download at this state
            gettingPushingData.set(true);
            if(setTrueLater){
                operator.downloadMission(downloadCallback);
            }
        }
        if(setTrueLater){
            operator.addListener(waypointListener);
        }

    }

    /**
     * @pre Should only be called once, its value should be set to downloadCallback.
     * @param operator a WaypointMissionOperator object.
     * @return a new CommonCallbacks.CompletionCallback
     * NOTE: Since this is only to be called once and set to downloadCallback, this object should only be used
     * for one downloadMission() request at a time.
     */
    private CommonCallbacks.CompletionCallback getDownloadCallback(final WaypointMissionOperator operator) {
        return new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if(djiError!=null){
                    Log.d6(CLASSNAME,"Error in downloadMission().  Error: " + djiError.getDescription());
                    gettingPushingData.set(false);
                    return;
                }
                //At this point the download was successful, so now get the waypoint mission
                WaypointMission mission = operator.getLoadedMission();
                ClientDispatcher client = ClientDispatcher.getInstance();

                //Must be active session and user's server data must have already been retrieved in order to post waypoint mission to server
                if(isActivePaaSSession() && allUserInfoReceived()){
                    client.httpPOSTWaypointMission(instance,mission,postWaypointCallback);
                }
            }
        };
    }

    /**
     * Should only be called one time to initialize postWaypointCallback
     * @return a NetworkCallback to be used to POST a waypoint mission
     */
    private NetworkCallback<String> getWaypointPOSTCallback(){
        return new NetworkCallback<String>() {
            /**
             * @pre When this method is called gettingPushingData should be true
             * @param event
             */
            @Override
            public void onEvent(NetworkCallbackEvent<String> event) {
                if(!gettingPushingData.get()){
                    throw new RuntimeException("Error in PaaSSession.  gettingPushData should be true when this method is called");
                }
                Exception e = event.getException();
                if(e!=null){
                    Log.d(CLASSNAME,"something went wrong in POSTING waypoints to server.  Will try again soon");
                }else{
                    String msg = "Waypoint mission succesfully posted to server!";
                    Log.d(CLASSNAME,msg);
                    Log.prettyI(msg);
                    gettingPushingData.set(false);
                    waypointMissionPushed.set(true);
                }
            }
        };
    }

    /**
     * Should only be called one time to initialize deleteWaypointCallback
     * @return a NetworkCallback to be used to DE:ETE a waypoint mission
     */
    private NetworkCallback<String> getWaypointDELETECallback(){
        long initTime = 0*0000;
        long increment = 0*1000;
        long maxTime = 5*1000;

        return new RepeatableNetworkCallback<String>(initTime,increment,maxTime) {
            int count = 0;
            final int MAXRETRY = 2; //Only retry this method twice
            @Override
            public void toRun(RepeatableNetworkCallback<String> myself) {
                ClientDispatcher client = ClientDispatcher.getInstance();
                client.httpDELETEWaypointMission(instance,myself);
            }

            @Override
            public boolean onEventCall(NetworkCallbackEvent<String> event) {
                Exception err = event.getException();
                if(err!=null){
                    Log.d(CLASSNAME,"Network error in deleting waypoint from server");
                    return (count++)<MAXRETRY; //run this callback again if count is less than MAXRETRY
                }
                return false;
            }
        };
    }

    /**
     * @pre This should only be called once to initialize a value for waypointListener
     * @return
     */
    private WaypointMissionOperatorListener getWaypointListener(final WaypointMissionOperator operator){
        return new WaypointMissionOperatorListener() {
            @Override
            public void onDownloadUpdate(@NonNull WaypointMissionDownloadEvent waypointMissionDownloadEvent) {
                //Don't care
            }

            @Override
            public void onUploadUpdate(@NonNull WaypointMissionUploadEvent waypointMissionUploadEvent) {
                //Don't care
            }

            @Override
            public void onExecutionUpdate(@NonNull WaypointMissionExecutionEvent waypointMissionExecutionEvent) {
                WaypointMissionState currentState = waypointMissionExecutionEvent.getCurrentState();
                tryStart(currentState);
            }

            @Override
            public void onExecutionStart() {
                WaypointMissionState currentState = operator.getCurrentState();
                tryStart(currentState);
            }

            @Override
            public void onExecutionFinish(@Nullable DJIError djiError) {
                StringBuilder msg = new StringBuilder("Waypoint mission has ended! ");
                if(djiError!=null){
                    msg.append(" However, mission ended with an error: ").append(djiError.getDescription());
                }
                String msgString = msg.toString();
                Log.d(CLASSNAME,msgString);
                Log.prettyI(msgString);

                parent.closePaaSSession();
                parent.startNewPaaSSession();
            }

            private void tryStart(WaypointMissionState currentState){
                if(     canDownloadAtThisState(currentState) &&
                        !gettingPushingData.get()            &&
                        !waypointMissionPushed.get()){
                    /*If program enters here, then we are in the right state to get data,
                                            we are currently not getting/pushing the waypoint mission,
                                            and and we haven't already pushed the waypoint mission */
                    gettingPushingData.set(true);
                    operator.downloadMission(downloadCallback);
                }
            }
        };
    }



    private boolean canDownloadAtThisState(WaypointMissionState state){
        return state==WaypointMissionState.EXECUTING || state==WaypointMissionState.EXECUTION_PAUSED;
    }




    /**
     * obtain the user-info of this PaaSSession, it is represented by a InlineResponse2002 object
     * @return the user-info of this PaaSSession
     */
    public InlineResponse2002 getUserInfo() {
        return userInfo;
    }

    /**
     * set the user-info of this PaaSSession
     * @param userInfo the new value to set as this PaaSSession's user-info
     */
    public void setUserInfo(InlineResponse2002 userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * obtain the session status of this PaaSSession, it is represented by a InlineResponse2006 object
     * @return the session status of this PaaSSession
     */
    public InlineResponse2006 getSessionStatus() {
        return sessionStatus;
    }

    /**
     * set the session status of this PaaSSession
     * @param sessionStatus the new value to set as this PaaSSession's session status
     */
    public void setSessionStatus(InlineResponse2006 sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    /**
     * obtain the vehicle status of this PaaSSession, it is represented by a InlineResponse2003 object
     * @return the vehicle status of this PaaSSession
     */
    public InlineResponse2003 getVehicleStatus() {
        return vehicleStatus;
    }

    /**
     * set the vehicle status of this PaaSSession
     * @param vehicleStatus the new value to set as this PaaSSession's vehicle status
     */
    public void setVehicleStatus(InlineResponse2003 vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    /**
     * This is called when the session this PaaSSession object represents has ended.
     * This method should be called to deallocate any resources this object is representing.
     * No other method of this object should be called again
     */
    public void sessionEnded(){
        isActivePaaSSession = false;

        MissionControl controller = DJISDKManager.getInstance().getMissionControl();

        //Stop this class from listening to waypoint mission events
        if(controller!=null && waypointListener!=null){
            controller.getWaypointMissionOperator().removeListener(waypointListener);
        }

        //If the waypoint mission was pushed, then I want to remove it from PaaS
        if(waypointMissionPushed.get()){
            ClientDispatcher client = ClientDispatcher.getInstance();
            client.httpDELETEWaypointMission(instance,deleteWaypointCallback);
        }

        //remove this PaaSSession instance as a listener for connection changes with the drone
        parent.removeConnectionCallback(this);
    }
    public boolean isActivePaaSSession(){
        return isActivePaaSSession;
    }


    /**
     * Determine if this PaaSSession is ready to push telemetry data to the server
     * @return true if this session can push telemetry data to the server
     *
     * Note: DataFramePusher uses this method to determine if it can push telemetry data to the server
     */
    public boolean canPushTelemetryData(){
        return waypointMissionPushed.get();
    }

    /**
     * Convenience method to determine if user info, session status, and vehicle status has all been set for this PaaSession.
     * @return true if all the 3 of the aforementioned member variables are non-null, false otherwise
     */
    public boolean allUserInfoReceived(){
        return userInfo!=null&&
                sessionStatus!=null&&
                vehicleStatus!=null;
    }

    public UserProfile getSessionUser(){
        return signedUser;
    }

    public long getVehicleID(){
        return choosenVehicleID;
    }

    @Override
    public void onEvent(ConnectionType event) {
        if(event==ConnectionType.INITIAL_PRODUCT_CONNECTION){
            //start doing waypoint stuff
            initializeWaypoints();
        }
    }
}
