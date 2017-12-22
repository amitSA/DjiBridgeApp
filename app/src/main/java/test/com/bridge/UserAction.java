package test.com.bridge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;

import test.com.bridge.utils.HelpMes;
import test.com.bridge.utils.NotificationPusha;
import test.com.bridge.views.MainActivity;
import test.com.bridge.views.SignInActivity;

/**
 * Created by Amit on 8/14/2017.
 */

/**
 * This class's userAction() method is used to indicate to the application that there is some
 * task that needs the user's involvement.  (Like for signing in, the userAction() method will send a sign-in notification)
 * As the developer, if you need to implement some way of alerting to the app that the user needs to do something,
 * consider adding another user action that you will support in the userAction() method.
 *
 *
 */
public class UserAction {

    public static final int NULL_VALUE = -1;

    //NOTE: the following are not related to android intent actions
    public static final int SIGN_IN_ACTION = 1;
    public static final int USER_DOWNLOADED_INVALID_ACTION = 2;
    //

    public static final String USER_ACTION_BUNDLE = "_user action JohnCena...WWEEEEE Bundle_";

    public static final String MESSAGE_EXTRA = "_message_extra_";
    public static final String UACTION_TYPE = "_user_action_type";

    private static final String GENERIC_MSG = "User Action Needed!";

    /**
     * Method will execute the action specified by the passed id.
     *
     * Descriptions of the different actions:
     * SIGN_IN_ACTION: This will prompt a notification asking the user to sign in
     * USER_DOWNLOADED_INVALID_ACTION: This will pass a new intent to MainActivity signaling to it that the user needs to
     *                                 manually refresh their server profile data once it becomes complete
     *                                 because right now it is not complete
     *
     * NOTE: As of now, USER_DOWNLOADED_INVALID_ACTION is not supported by MainActivity and will never be (I decided not to use that UserAction any more)
     * NOTE: As of now, SIGN_IN_ACTION is the only action being used
     *
     * @param id Can be any of the above id's (SIGN_IN_ACTION,USER_DOWNLOAD_INVALID_ACTION)
     * @param extraMessage A string holding an additional message that will be passed to the executing action, if that action supports extra messages
     */
    public static void userAction(int id,String extraMessage){
        Context context = HelpMes.getApplicationContext();

        if(id==SIGN_IN_ACTION){
            //push a notification asking the user to sign-in
            sendSignInNotification(extraMessage);
        }
        else if(id==USER_DOWNLOADED_INVALID_ACTION){
            //we will prompt MainActivity to tell the User that their downloaded information is not completed
            Bundle bundle = new Bundle();
            bundle.putInt(UACTION_TYPE,USER_DOWNLOADED_INVALID_ACTION);
            bundle.putString(MESSAGE_EXTRA,extraMessage);

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(USER_ACTION_BUNDLE,bundle);

            //NOTE: MainActivity does not support this User Action, it will do nothing with the new intent
            context.startActivity(intent);
        }
    }

    /**
     * Run any closing tasks on the action specified by the past in integer
     * @param id integer representing the action to close on
     */
    public static void closeUserAction(int id){
        if(id==SIGN_IN_ACTION){
            cancelSignInNotification();
        }
        else if(id==USER_DOWNLOADED_INVALID_ACTION){
            //Is there anything we want to close for this that this class started ?
        }
    }

    /**
     * Run any closing tasks on any actions that were associated with this intent
     * @param intent
     */
    public static void closeUserAction(Intent intent){
        Bundle bundle =  intent.getBundleExtra(USER_ACTION_BUNDLE);
        if(bundle!=null){
            int id = bundle.getInt(UACTION_TYPE);
            closeUserAction(id);
            /*int id = bundle.getInt(UserAction.EXTRA_KEY,UserAction.NULL_VALUE);
            if(id!=NULL_VALUE){
                closeUserAction(id);
            }*/
        }
    }


    /**
     * Called by userAction() when the SIGN_IN_ACTION is received.  This method sends the sign-in notification
     * @param extraMessage  the extraMessage that will be put in the bundle passed to SigninActivity's intent
     */
    private static void sendSignInNotification(String extraMessage){
        Bundle bundle = new Bundle();
        bundle.putInt(UACTION_TYPE,SIGN_IN_ACTION);
        bundle.putString(MESSAGE_EXTRA,extraMessage);

        Context appContext = HelpMes.getApplicationContext();
        //'fgIntent' stands for foreground intent
        Intent fgIntent = new Intent(appContext, SignInActivity.class); //QUESTION: What type of context should I pass? Does it matter if its an application context or component context
        fgIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK); //TODO: After my tests, SignInActivity is still being recreated, that should'nt be happening with the NEW_TASK flag
        fgIntent.putExtra(USER_ACTION_BUNDLE,bundle);
        //fgIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        TaskStackBuilder sBuilder = TaskStackBuilder.create(appContext);
        sBuilder.addParentStack(SignInActivity.class);
        sBuilder.addNextIntent(fgIntent);
        NotificationPusha.getInstance().sendSimpleNotification( //TODO: You have some final static variables in R and some in this file, and there seems to be no convention for what types of variables belong where.  You should consolidate all static variables to just one place, or maybe have some convention (by type/purpose of static variable) that explains where a variable should belong
                R.integer.SIGNIN_NOTIFICATION_ID,
                GENERIC_MSG,
                HelpMes.getStr(R.string.signin_notification_content),
                sBuilder);
    }
    /**
     * see sendSignInNotification(String)
     * @param extraMessage an integer representing a resource string id
     */
    private static void sendSignInNotification(int extraMessage){
        sendSignInNotification(HelpMes.getStr(extraMessage));
    }


    /**
     * Remove an existing signin notification from the android system's notification drawer
     * if it exists, else does nothing
     */
    private static void cancelSignInNotification(){
        NotificationPusha.getInstance().cancelNotification(R.integer.SIGNIN_NOTIFICATION_ID);
    }
}
