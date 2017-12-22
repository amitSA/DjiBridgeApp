package test.com.bridge;

import android.content.Context;

import test.com.bridge.callback.CallbackList;
import test.com.bridge.callback.EventCallback.AuthCallback;
import test.com.bridge.storage.MyLocalStorage;
import test.com.bridge.utils.AuthSumulator;
import test.com.bridge.utils.HelpMes;

/**
 * Created by Amit on 7/31/2017.
 */

/**
 * This is a singleton class that was created to offer actions on Authenticating a user
 * and keeping track of the currently signed-in user
 * It does a bunch of related actions as well (i.e. storing the credentials of the currently sign-in user
 * to persistant storage)
 */
public class UserAccount {
    public static String CLASSNAME = UserAccount.class.getSimpleName();

    private static UserAccount instance;

    private Context appContext;
    volatile private SignedInStatus status;  //TODO: DO I REALLY NEED VOLATILE?
    private UserProfile loggedInUser;

    private CallbackList<AuthCallback,SignedInStatus> eventCallbacks;

    final private Object lock = new Object(); //Used for synchronization in this class's methods

    /**
     * Creates a UserAccount object
     * @param c
     */
    private UserAccount(Context c){
        appContext = c.getApplicationContext();
        status = SignedInStatus.SIGNED_OUT;
        loggedInUser = null;  //if status == SIGNED_OUT or SIGNING_IN, then loggedInUser should equal null
        eventCallbacks = new CallbackList<>();
    }

    /**
     * Returns the singleton UserAccount instance, creating it if it does not currently exist.
     * @return the singleton UserAccount instance
     */
    public static UserAccount getInstance(){
        if(instance == null){
            instance = new UserAccount(HelpMes.getApplicationContext());
        }
        return instance;
    }

    /**
    * Call this method to retrieve a UserProfile object stored in persistent storage, if it exists
    * @return returns the UserProfile that is locally stored(only one UserProfile can be stored at a time ).
    *         If a valid UserProfile was found, then the email and password fields of the returned UserProfile
    *         will be initialized, else they will be null
    */
    public static UserProfile readLocalUserProfile(){
        MyLocalStorage ls = MyLocalStorage.getInstance();
        String stored_username = ls.getValue(R.string.USERNAME_KEY);
        if(stored_username == null){
            return new UserProfile(null,null);
        }
        String stored_pass = ls.getEncryptedValue(R.string.PASSWORD_KEY);
        return new UserProfile(stored_username,stored_pass);
    }

    /**
     * stores a UserProfile into persistent storage.  The passed in user profile will override any existing user profile
     * Note: Only the email and the password of the user profile gets saved into persistant storage
     * @param prof the UserProfile to save
     */
    public static void storeUserProfileLocally(UserProfile prof){
        MyLocalStorage ls = MyLocalStorage.getInstance();
        ls.storeKeyValue(R.string.USERNAME_KEY,prof.getEmail());
        ls.storeEncryptedKeyValue(R.string.PASSWORD_KEY,prof.getPassword());
    }

    /**
     * Remove the currently stored UserProfile object from persistent storage if it exists
     */
    public static void removeUserProfileLocally(){
        MyLocalStorage ls = MyLocalStorage.getInstance();
        ls.removeKey(R.string.USERNAME_KEY);
        ls.removeEncryptedKey(R.string.PASSWORD_KEY);
    }

    /**
     * Sign in the passed-in UserProfile object. If an error occurred during the initial signing-in
     * checks (like if a user is already signed-in or we are currently signing in some other user) then this
     * method will immediately return false, else this method will return true

     * Note: All registered AuthSimulator.Callbacks will get called if there is a sign-in change due to this call
     * @param prof The UserProfile to sign-in
     * @param cb The callback to call once the sign-in was successful or failed
     * @return False if there was a problem in the initial sign-in checks, true otherwise
     */
    public boolean signIn_Auth(final UserProfile prof, final AuthCallback cb){
        //Only one thread can be signing-in at a time.  If a second thread calls this method, when
        // a first thread is signing-in, then it will immediately return false
        synchronized (lock) {
            if (isSignedIn() || isSigningIn()) { //If we are already signed in or we are signing in, then don't sign in again and fail this method
                return false;
            }
            status = SignedInStatus.SIGNING_IN;
        }
        eventCallbacks.triggerEvents(status); //status will always be SIGNING_IN at at this point

        AuthSumulator.dummyNDCAuthenticate(prof, new AuthSumulator.Callbacks() {
            @Override
            public void onResponse(boolean result) {
                SignedInStatus tempStatus = result ? SignedInStatus.SIGNED_IN : SignedInStatus.SIGNED_OUT;
                synchronized (lock) {
                    status = tempStatus;
                    loggedInUser = isSignedIn() ? prof : null;
                    eventCallbacks.triggerEvents(status);
                    cb.onEvent(status);
                }
            }
        });
        return true;
    }

    /**
     * Sign out the currently signed user.
     * @return False is returned if that is not possible due to no user being signed in.
     * Else true is returned to indicate a successful sign-out
     */
    public boolean signOut_Auth(){

        //To ensure mutual exclusion between this code block and the above code blocks in signIn_Auth().
        //Specifically, I don't want to be in the middle of the onResponse() method above when I sign-out
        synchronized (lock){
            if(isSignedOut() || isSigningIn()){
                return false;
            }
        }
        removeUserProfileLocally();
        status=SignedInStatus.SIGNED_OUT;
        loggedInUser = null;
        eventCallbacks.triggerEvents(status);
        return true;
    }

    /**
     * Adds the specified callback to this class's list of AuthCallback listeners.  Whenever
     * the sign-in status changes, the callback will be fired
     * @param cb
     */
    public void addEventCallback(AuthCallback cb){
        eventCallbacks.add(cb);
    }


    /**
     * Removes the specified callback from this class's list of AuthCallback listeners
     * @param cb
     */
    public void removeCallback(AuthCallback cb){
        eventCallbacks.remove(cb);
    }


    //The following methods are all helper methods for retreiving the signed-in status of the app
    public SignedInStatus getSignInStatus(){
        return status;
    }
    public boolean isSignedIn(){
        return status==SignedInStatus.SIGNED_IN;
    }
    public boolean isSigningIn(){
        return status==SignedInStatus.SIGNING_IN;
    }
    public boolean isSignedOut(){
        return status==SignedInStatus.SIGNED_OUT;
    }

    public UserProfile getLoggedInUser(){
        return loggedInUser;
    }

    /**
     * Enum that is used to indicate the sign-in status of the application
     */
    public enum SignedInStatus{SIGNING_IN, SIGNED_IN,SIGNED_OUT};

}
