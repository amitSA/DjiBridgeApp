package test.com.bridge.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
//import android.util.Log;
import java.util.List;
import java.util.Locale;

import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.sdkmanager.DJISDKManager;
import test.com.bridge.utils.HelpMes;
import test.com.bridge.utils.Log;

import dji.common.error.DJIError;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;

/**
 * Created by Amit on 7/6/2017.
 */

/**
 * This class was just used for me test and see how waypoints were pushed from the DJIDrone to device
 *
 * It is not used in the app
 */
public class TestWaypointMissionListener implements WaypointMissionOperatorListener {

    private final String CLASSNAME = this.getClass().getSimpleName();
    private Context appContext;

    public TestWaypointMissionListener(){
        appContext = HelpMes.getApplicationContext();
        final WaypointMissionOperator missionOperator = DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();

        Log.d6(CLASSNAME,"mission state: " + HelpMes.getWaypointStateString(missionOperator.getCurrentState()));


        missionOperator.downloadMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if(djiError!=null){
                    Log.d6(CLASSNAME,"Error in downloading waypoint mission: djiError: " + djiError.getDescription());
                    return;
                }
                Log.d6(CLASSNAME,"Waypoint mission downloaded succesfully to controller.  Going to download to app");
                WaypointMission mission = missionOperator.getLoadedMission();
                List<Waypoint> waypointList = mission.getWaypointList();
                for(int i = 0;i<waypointList.size();i++){
                    LocationCoordinate2D coord = waypointList.get(i).coordinate;
                    String msg = String.format(Locale.US,"(coordinate %d) lat: %d  long: %d",i,(int)(coord.getLatitude()+0.5),(int)(coord.getLongitude()+0.5));
                    Log.d6(CLASSNAME,msg);
                }

            }
        });

    }
    @Override
    public void onUploadUpdate(@NonNull WaypointMissionUploadEvent uploadEvent){
        String message = "WaypointMissionListener::onUploadUpdate() called";
        Log.d(CLASSNAME,message);
        HelpMes.displayToast(message);
    }

    @Override
    public void onDownloadUpdate(@NonNull WaypointMissionDownloadEvent downloadEvent){
        String message = "WaypointMissionListener::onDownloadUpdate() called";
        Log.d(CLASSNAME,message);
        HelpMes.displayToast(message);
    }
    @Override
    public void onExecutionStart(){
        String message = "WaypointMissionListener::onExecutionStart() called";
        Log.d(CLASSNAME,message);
        HelpMes.displayToast(message);
    }
    @Override
    public void onExecutionUpdate(@NonNull WaypointMissionExecutionEvent executionEvent){
        String message = "WaypointMissionListener::onExecutionUpdate() called";
        Log.d7(CLASSNAME,message);
        //HelpMes.displayToast("Waypoint mission in progress");

    }

    @Override
    public void onExecutionFinish(@Nullable DJIError error){
        String message = "WaypointMissionListener::onExecutionFinish() called";
        Log.d(CLASSNAME,message);
        HelpMes.displayToast(message);
    }
}
