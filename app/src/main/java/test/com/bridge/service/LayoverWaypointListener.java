package test.com.bridge.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import dji.common.error.DJIError;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import test.com.bridge.utils.Log;

/**
 * Created by Amit on 8/31/2017.
 */

/**
 * This class' sole job is to listen for when a new WaypointMission is uploaded to the drone, when
 * that happens and a current PaaSSession  is not active, it will tell ConnectionService to start a new PaaSSession
 *
 */
public class LayoverWaypointListener implements WaypointMissionOperatorListener {
    private static String CLASSNAME = LayoverWaypointListener.class.getSimpleName();

    private ConnectionService parent;

    public LayoverWaypointListener(ConnectionService parent) {
        this.parent = parent;
    }

    @Override
    public void onDownloadUpdate(@NonNull WaypointMissionDownloadEvent waypointMissionDownloadEvent) {
        //Don't Care
    }

    @Override
    public void onUploadUpdate(@NonNull WaypointMissionUploadEvent waypointMissionUploadEvent) {
        //Don't Care
    }

    @Override
    public void onExecutionUpdate(@NonNull WaypointMissionExecutionEvent waypointMissionExecutionEvent) {
        //Don't Care
    }

    @Override
    public void onExecutionStart() {

        /*We do care, b/c its possible that a PaaSSession started and ended, and now ConnectionService
          is waiting for a new WaypointMission to uploaded to the drone*/
        if(parent.getActivePaaSSession()==null){
            //At this point, there is no active session, so start one
            Log.d(CLASSNAME,"New waypoint mission is starting, so starting a new PaaSSession");
            parent.startNewPaaSSession();
        }

    }

    @Override
    public void onExecutionFinish(@Nullable DJIError djiError) {
        //Don't Care, the active PaaSSession will handle this one.
    }
}
