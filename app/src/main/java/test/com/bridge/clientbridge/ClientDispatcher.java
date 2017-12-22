package test.com.bridge.clientbridge;

/**
 * Created by Amit on 8/13/2017.
 */


import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.model.LocationCoordinate2D;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.DataFrame;
import io.swagger.client.model.InlineResponse2002;
import io.swagger.client.model.InlineResponse2003;
import io.swagger.client.model.InlineResponse2006;
import io.swagger.client.model.TrajectoryPoint;
import io.swagger.client.model.V1dataData;
import test.com.bridge.BatteryTuple;
import test.com.bridge.callback.RepeatableNetworkCallback;
import test.com.bridge.service.PaaSSession;
import test.com.bridge.UserProfile;
import test.com.bridge.callback.EventCallback.NetworkCallback;
import test.com.bridge.callback.AllEvents.NetworkCallbackEvent;
import test.com.bridge.utils.HelpMes;

/**
 * Is a singleton class.  All calls the app wants to do to the android-client are done through
 * this class' instance.
 */
public class ClientDispatcher {
    private static String CLASSNAME = ClientDispatcher.class.getSimpleName();


    private static ClientDispatcher instance = null;

    private DefaultApi mApi; //Should not be used.  My goal is to use AsyncDefaultApi for every request (unless there is a Good reason not too)
    private AsyncDefaultApi asyncApi;

    private ClientDispatcher(){
        mApi = new DefaultApi(HelpMes.getApplicationContext());
        asyncApi = new AsyncDefaultApi(mApi);
    }

    /**
     * Returns the single instance of ClientDispatcher in this application.  If one does not exists, then
     * a new ClientDispatcher is created and then returned
     * @return the single ClientDispatcher instance of this app
     */
    public static ClientDispatcher getInstance(){
        if(instance == null){
            instance = new ClientDispatcher();
        }
        return instance;
    }

    /**
     * Posts a frame of telemetry data to the PaaS server.
     * @param callback the NetworkCallback whose onEvent() method is called once the request is successfully completed or encountered an error
     * @param tupleSet The frame of data to post, represented by an array of BatteryTuple objects.
     *                   This array holds all the telemetry data that will be put in a new DataFrame object.  See userP description
     * @param paaSSession The instance of a paas session.  Contains info like session id
     *
     */
    public void  httpPOSTDataFrame(final NetworkCallback<String> callback, BatteryTuple [] tupleSet, final PaaSSession paaSSession){
        Response.Listener<String> responseListener = getWrapperListener(callback);
        Response.ErrorListener errorListener = getWrapperErrorListener(callback);
        UserProfile sessionUser = paaSSession.getSessionUser();
        DataFrame frame = constructDataFrame(tupleSet);
        asyncApi.dataAddV1(sessionUser.getEmail(),paaSSession.getSessionStatus().getId(),frame,responseListener,errorListener);
    }

    /**
     * Obtain the server's InlineResponse2002 userInfo on the user of the passed in session object
     * @pre session must have a signed-in UserProfile object
     * @param session The session whose user you are fetching data for
     * @param callback A NetworkCallback whose onEvent() will be called once a response or error is received.
     *                 The response will contain everything the server has on the user (like a list of vehicles attached to this user)
     */
    public void httpGETUserInfo(final PaaSSession session,final NetworkCallback<InlineResponse2002> callback){
        Response.Listener<InlineResponse2002> responseListener = getWrapperListener(callback);
        Response.ErrorListener errorListener = getWrapperErrorListener(callback);
        UserProfile userP = session.getSessionUser();

        asyncApi.userInfoV1(userP.getEmail(),responseListener,errorListener);
    }

    /**
     * Obtain the server's session info on the vehicle selected by the passed in PaasSession object.  (Note: the server's session and PaaSSession are 2 different objects representing the same concept, but on different machines )
     * On the server side: the relationship between users, vehicles, and sessions is as follows:
     *      A user has a list of vehicles where each vehicle may have a session attached to it
     * @param session The session that contains the data necessary for the request (like what user, and what vehicle of the specified user are we getting the session for)
     * @param callback NetworkCallback whose onEvent() will be called with a response or error
     */
    public void httpGETSessionStatus(final PaaSSession session, final NetworkCallback<InlineResponse2006> callback){
        Response.Listener<InlineResponse2006> responseListener = getWrapperListener(callback);
        Response.ErrorListener errorListener = getWrapperErrorListener(callback);
        UserProfile userP = session.getSessionUser();

        asyncApi.sessionStatusV1(userP.getEmail(),session.getVehicleID(),responseListener,errorListener);
    }

    /**
     * Obtain the server's info on the specified vehicle
     * @param session The session that contains the data necessary for the request
     * @param callback NetworkCallback whose onEvent() will be called with a response or error
     */
    public void httpGETVehicleStatus(final PaaSSession session, final NetworkCallback<InlineResponse2003> callback){
        Response.Listener<InlineResponse2003> responseListener = getWrapperListener(callback);
        Response.ErrorListener errorListener = getWrapperErrorListener(callback);
        UserProfile userP = session.getSessionUser();

        asyncApi.vehicleStatusV1(userP.getEmail(),session.getVehicleID(),responseListener,errorListener);
    }


    /**
     * TODO: This method must be unit tested (As does all waypoint stuff)
     *
     * POST a set of waypoints to the server for the current vehicle the PaaSSession is for
     * Note: this method should be unit tested
     * @param session The session that contains the data necessary for the request
     * @param mission Contains the set of waypoints needed to POST
     * @param callback NetworkCallback whose onEvent() will be called with a response or error
     */
    public void httpPOSTWaypointMission(final PaaSSession session,final WaypointMission mission,final NetworkCallback<String> callback){
        final List<Waypoint> waypointList = mission.getWaypointList();
        final UserProfile userP = session.getSessionUser();
        long delayTime = 0 * 1000;
        long increment = 0 * 1000;
        long maxDelayTime = 0 * 1000;

        //Using RepeatableNetworkCallback to make N network posts, where N is the number of waypoints (RNC was not meant to be used this way, but it works)
        RepeatableNetworkCallback<String> repeatableCallback = new RepeatableNetworkCallback<String>(delayTime,increment,maxDelayTime) {
            Response.Listener<String> responseListener = getWrapperListener(this);
            Response.ErrorListener errorListener = getWrapperErrorListener(this);
            int index = 0;
            @Override
            public void toRun(RepeatableNetworkCallback<String> myself) {
                //Pushing the ith waypoint
                LocationCoordinate2D coord = waypointList.get(index).coordinate;
                TrajectoryPoint point = new TrajectoryPoint();

                //TODO: ask chris if we need to set vehicleID on a trajectory point
                point.setVehicleId(session.getVehicleID()); //setting vehicle id to trajectory point
                point.setLatitude(coord.getLatitude()); //setting latitude to trajectory point
                point.setLongitude(coord.getLongitude()); //setting longitude to trajectory point
                //point.setAltitude(null); //TODO: what do I set altitude to?
                //point.setEta(null); //TODO: what do I set altitude to?

                if(session.isActivePaaSSession()){ //Possibly the session ended in the middle of sending all the waypoints, in that case we should stop

                    asyncApi.trajectorySetV1(userP.getEmail(),point,session.getVehicleID(),responseListener,errorListener);
                }else{
                    RuntimeException err = new RuntimeException("PaaSSession stopped in the middle of sending waypoints");
                    callback.onEvent(new NetworkCallbackEvent<String>(err,null));
                }
             }

            @Override
            public boolean onEventCall(NetworkCallbackEvent<String> event) {
                Exception err = event.getException();
                String response = event.getResponse();
                if(err!=null){
                    //There was an error pushing the ith waypoint
                    //Lets just quit this entire transaction
                    callback.onEvent(new NetworkCallbackEvent<String>(err,null)); //By passing null to response argument and a non-null value to exception element, this indicates failure
                    return false; //So toRun() does not get called again
                }
                if(index==waypointList.size()-1){

                    //Calling the callback object, passing null into exception argument and a non-null value to response argument indicates success
                    callback.onEvent(new NetworkCallbackEvent<String>(null,"All waypoints pushed"));

                    //We have successfully pushed all waypoints to server, so return false to not call onRun() again.
                    return false;
                }
                //The ith waypoint was pushed, so lets return true to push the i+1th waypoint
                index++;
                return true;
            }
        };
    }


    /**
     * Delete the set of trajectories associated with a vehicle on the server
     * @param session The session that contains the data necessary for the request
     * @param callback NetworkCallback whose onEvent() will be called with a response or error
     */
    public void httpDELETEWaypointMission(final PaaSSession session,final NetworkCallback<String> callback){
        Response.Listener<String> responseListener = getWrapperListener(callback);
        Response.ErrorListener errorListener = getWrapperErrorListener(callback);
        UserProfile userP = session.getSessionUser();

        asyncApi.trajectoryRemoveV1(userP.getEmail(),session.getVehicleID(),responseListener,errorListener);
    }


    //The following private static fields are all string or char constants that will be used in constructing
    // the DataFrame's metadata
    private static final String VOLTS_TAG = "volts";
    private static final String CURRENT_TAG = "current";
    private static final String TEMP_TAG = "temp";
    private static final char b = 'b';
    private static final char period = '.';

    /**
     * Constructs and returns a DataFrame object
     * @param tuples Holds the telemetry data that will be put in the returned DataFrame object
     * @return a newly constructed DataFrame object
     */
    private DataFrame constructDataFrame(BatteryTuple [] tuples){
        List<V1dataData> dataList = new ArrayList<>(tuples.length);
        long time = System.currentTimeMillis();

        for(int i = 0;i<tuples.length;i++){
            if(tuples[i]==null){ //if true, then there was no telemetry data received for the ith battery
                continue;
            }
            //prefix will essentially be "b0." or "b1." or "b2." ...
            //It is used to distinguish which battery the attached telemetry data came from
            String prefix = new StringBuilder().append(b).append(i).append(period).toString();
            //Creating element to hold voltage data
            V1dataData voltage = new V1dataData();
            //voltage.setSystemId(sysId); //system id's should not be used in V1dataData elements.  Chris said they will be removed from the spec
            voltage.setTimestamp(time);
            voltage.setTag(prefix+VOLTS_TAG);
            voltage.setValue((double)tuples[i].getVoltage());

            //Creating element to hold current data
            V1dataData current = new V1dataData();
            //current.setSystemId(sysId);  //see above message on system ids^
            current.setTimestamp(time);
            current.setTag(prefix+CURRENT_TAG);
            current.setValue((double)tuples[i].getCurrent());

            //Creating element to hold temperature data
            V1dataData temp = new V1dataData();
            //temp.setSystemId(sysId);    //see above message on system ids^
            temp.setTimestamp(time);
            temp.setTag(prefix+TEMP_TAG);
            temp.setValue((double)tuples[i].getTemperature());
            dataList.add(voltage);
            dataList.add(current);
            dataList.add(temp);
        }
        DataFrame frame = new DataFrame();
        frame.setData(dataList);
        frame.setIsFuture(false); //TODO: what does this method call do ?
        return frame;
    }

    private <E> Response.Listener<E> getWrapperListener(final NetworkCallback<E> callback){
        return new Response.Listener<E>() {
            @Override
            public void onResponse(E response) {
                callback.onEvent(new NetworkCallbackEvent<E>(null,response));
            }
        };
    }

    private Response.ErrorListener getWrapperErrorListener(final NetworkCallback callback){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onEvent(new NetworkCallbackEvent<Object>(error,null)); //Generic type doesn't matter b/c if the request encountered an error, then we will always pass null into the 'response' argument
            }
        };
    }

}
