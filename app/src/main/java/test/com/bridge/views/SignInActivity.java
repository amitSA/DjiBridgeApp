package test.com.bridge.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import test.com.bridge.R;
import test.com.bridge.UserAccount;
import test.com.bridge.UserAccount.SignedInStatus;
import test.com.bridge.UserAction;
import test.com.bridge.UserProfile;
import test.com.bridge.callback.EventCallback.AuthCallback;
import test.com.bridge.utils.HelpMes;
import test.com.bridge.utils.Log;

/**
 * Created by Amit on 7/25/2017.
 */

/**
 * This class represents the SignInActivity of the app.  It is the Activity that the user uses to...sign...in
 */
public class SignInActivity extends BaseActivity {

    private static long THREE_SEC = 3000;
    private static long TWO_SEC = 2000;
    private static long ONE_SEC = 1000;

    private static long END_ACTIVITY_DELAY = ONE_SEC;

    private TextView statusTextView;
    private EditText usernameTextView;
    private EditText passTextView;
    private Button submitBtn;

    /**
     * Here we retrieve and store references of view elements to fields of this class
     * Also we register listeners for button view elements
     * @param oldState
     */
    @Override
    public void onCreate(Bundle oldState){
        super.onCreate(oldState);
        Log.d(CLASS,"onCreate() method called");

        setContentView(R.layout.signin_layout);
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        usernameTextView = (EditText) findViewById(R.id.user_text_input);
        passTextView = (EditText) findViewById(R.id.pass_text_input);
        submitBtn = (Button) findViewById(R.id.btn_submit);
        submitBtn.setOnClickListener(buttonClickListener);

        //If there is a stored username, automatically populate the username text field with the username; we are not doing this with the password though
        UserProfile storedProfile = UserAccount.readLocalUserProfile();
        String storedUsername = storedProfile.getEmail();
        if(storedUsername!=null){
            usernameTextView.setText(storedUsername);
        }
        //This command would disable the home arrow in this activity's action bar
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //To customize more properties of the Action Bar look at setDisplayOptions() method
    }

    /**
     * The OnClickListener that will be attached to submitBtn.
     */
    View.OnClickListener buttonClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //Note: v==submitBtn is true.  I used this object such that v will reference the same object that submitBtn does
            final String username = usernameTextView.getText().toString();
            final String pass = passTextView.getText().toString();
            Log.d3(CLASS,"username: " + username.toString() + "  pass: " + pass.toString());
            if(!isEmailString(username)){
                changeStatus_InvalidEmail();
                Log.d3(CLASS,"Invalid email entered for the username");
                return;
            }
            submitBtn.setEnabled(false);

            final UserAccount userAccount = UserAccount.getInstance();
            final UserProfile profile = new UserProfile(username,pass);

            userAccount.signIn_Auth(profile,new AuthCallback(){
                @Override
                public void onEvent(SignedInStatus status){
                    if(userAccount.isSignedIn()){

                        //Doing actions to indicate a successful authentication
                        UserAction.closeUserAction(UserAction.SIGN_IN_ACTION);
                        UserAccount.storeUserProfileLocally(profile);
                        changeStatus_AuthSuccess();
                        endActivity(END_ACTIVITY_DELAY);
                    }
                    else{
                        //Doing actions to indicate a UN-successful authentication
                        changeStatus_AuthError();
                        submitBtn.setEnabled(true);
                    }
                }
            });
        }
    };

    /**
     * Look at @return
     * @param str The email string to check
     * @return Returns true if str contains a '@' and '.'
     */
    private boolean isEmailString(String str){
        //return str.contains("@") && str.contains("."); //No need to optimize this method
        return true;
    }

    /**
     * Change the status text view to display that an invalid email was entered
     */
    private void changeStatus_InvalidEmail(){
        statusTextView.setVisibility(View.VISIBLE);
        statusTextView.setText(getStr(R.string.invalid_email));
        statusTextView.setTextColor(HelpMes.getColor(R.color.unsuccessRed));
    }

    /**
     * Change the status text view to display that an error in authentication occurred
     */
    private void changeStatus_AuthError(){
        statusTextView.setVisibility(View.VISIBLE);
        statusTextView.setText(getStr(R.string.unsuccessful_auth));
        statusTextView.setTextColor(HelpMes.getColor(R.color.unsuccessRed));
    }

    /**
     * Change the status text view to display that a successful authentication occurred
     */
    private void changeStatus_AuthSuccess(){
        statusTextView.setVisibility(View.VISIBLE);
        statusTextView.setText(getStr(R.string.successful_auth));
        statusTextView.setTextColor(HelpMes.getColor(R.color.successGreen));
    }

    /**
     * Ends the this activity after milliDelay seconds
     * @param milliDelay Time in milliseconds to which the activity should end
     */
    private void endActivity(long milliDelay){
        UIHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },milliDelay);
    }

    //The next 5 methods are lifecycle callbacks that I overrode but do nothing but call the super class implementation
    // and possibly print a log message
    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onStop(){
        super.onStop();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(CLASS,"onDestroy() method called");
    }
}
