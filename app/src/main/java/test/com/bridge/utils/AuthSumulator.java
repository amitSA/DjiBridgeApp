package test.com.bridge.utils;

import android.os.Handler;
import android.os.Looper;

import test.com.bridge.UserProfile;


/**
 * Created by Amit on 7/26/2017.
 */

/**
 * Utility class that offers methods to simulate authentication requests
 */
public class AuthSumulator {
    /*Famous Quote: "Sike, thats the wrong number" */

    //A random number from 0 to this value is determined as the response-time
    //of this fake Authentication request
    private static long AUTH_RESPONSE_TIME_RANGE = 4000;

    // The length of a password is the only determiner if a password is right or not
    // Thus, usernames are not used in determining if an UserProfile authenticated or not
    private static int PASS_MAX_LENGTH = 6;

    /**
     Simulates an NDC Authentication request.  This method is only used for testing, as eventually
     the actual http Authenticate request will replace this method
     @param profile The UserProfile object to try the authentication on
     @param res The Callback object whose onEvent() method will be called when a response is received.
                Either SignedInStatus.SIGNED_IN or SignedInStatus.SIGNED_OUT
    */
    public static void dummyNDCAuthenticate(UserProfile profile, final Callbacks res ){
        final String username = profile.getEmail();
        final String pass = profile.getPassword();
        Handler UIhandler = new Handler(Looper.getMainLooper());
        UIhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean success = pass.length()<=PASS_MAX_LENGTH;
                //SignedInStatus status = success ? SignedInStatus.SIGNED_IN : SignedInStatus.SIGNED_OUT;
                res.onResponse(success);
            }
        },(int)(Math.random()*AUTH_RESPONSE_TIME_RANGE+0.5));
    }
    /*
     Implement this callback if you want to support its onResponse() method.
     This callback is mainly used in dummyNDCAuthenticate()
    */
    public interface Callbacks{
        public void onResponse(boolean authSuccesful);
    }
}
