package test.com.bridge;

/**
 * Created by Amit on 7/31/2017.
 */

/**
 * This class represents an instance of a user's profile.  So it contains member variables for a users
 * email and password.  Additionally it contains member variables for a person's profile data that can be
 * obtained from a server.  (See getters and setters for InlineResponse#### member variables)
 */
public class UserProfile {

    private String email;
    private String pass;

    /**
     * Construct a new UserProfile object with the passed in email and password.  All InlineResponse#### member variables
     * are initialized to null
     * @param email
     * @param pass
     */
    public UserProfile(String email, String pass){
        this.email = email;
        this.pass = pass;

    }

    /**
     * obtain the email of this UserProfile
     * @return email of this UserProfile
     */
    public String getEmail() {
        return email;
    }

    /**
     * set the email of this UserProfile;
     * @param email the new value to set as this UserProfile's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * obtain the password of this UserProfile
     * @return the password of this UserProfile
     */
    public String getPassword() {
        return pass;
    }

    /**
     * set the password of this UserProfile
     * @param pass the new value to set as this UserProfiles' password
     */
    public void setPassword(String pass) {
        this.pass = pass;
    }


    public boolean isSignedInUser(){
        UserAccount account = UserAccount.getInstance();
        return account.getLoggedInUser()==this;
    }

}
