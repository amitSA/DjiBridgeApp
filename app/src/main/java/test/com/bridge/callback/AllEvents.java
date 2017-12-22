package test.com.bridge.callback;

import android.util.Log;

import test.com.bridge.BatteryTuple;

/**
 * Created by Amit on 8/21/2017.
 */

/**
 * This class has a bunch of member static classes that are all events for the interfaces that extend
 * from EventCallback
 */
public class AllEvents {

    /**
     * I just overrode the constructor so I could set its access-qualifier to private.
     * Since no instance of AllEvents is created in this class, no instance of AllEvents can ever exist.  This class is solely used for its static member classes
     */
    private AllEvents(){

    }

    /**
     * Instances of this type are passed to BatteryValuesCallback's onEvent() method
     */
    public static class BatteryFrame /*extends Event*/{

        public BatteryTuple[] batteryAverages;
        public BatteryFrame(BatteryTuple [] b){
            batteryAverages = b;
        }
    }

    /**
     * Enums of this type are passed to ConnectionChangedCallback's onEvent() method.
     * A list of ConnectionChangedCallbacks is a field of ConnectionService, and  ConnectionChangedCallback
     * is implemented in DataFramePusher and MainActivity
     */
    public static enum ConnectionType {
        INITIAL_PRODUCT_CONNECTION, //passed on the initial connection to a new aircraft
        CONNECTION_DISCONNECTED,    //passed when the connection to a aircrafts disconnects (potentially temporarily)
        CONNECTION_RECONNECTED,     //passed when the dji sdk reconnects to an aircraft that it just previously had disconnected from
        PRODUCT_DISCONNECTED,       //passed when the connection to a aircraft disconnects, but more strong;y (NOTE: I'm not sure if this enum even gets passed by ConnectionService)

        SERVICE_NOT_STARTED         //passed to the callback when before the service got the message to connect to the drone (see ConnectionService.onStartCommand() )
    }

    /**
     * Instances of this type are passed to NetworkCallback's onEvent() method.
     * Instances of this class just act as a compound data type that stores both an Exception and UserProfile object.
     * Other than moving its composite data types from place to place, this class does nothing
     */
    public static class NetworkCallbackEvent<E> {
        private Exception exception;
        //private UserProfile userProfile;
        private E response;

        /**
         * Creates a new NetworkCallbackEvent whose corresponding fields are initialized to the passed in arguments
         * @param exception Exception object
         * @param response the response object
         */
        public NetworkCallbackEvent(Exception exception, E response) {
            this.exception = exception;
            this.response = response;
        }

        /**
         * Returns this instances Exception member variable
         * @return an Exception object
         */
        public Exception getException() {
            return exception;
        }

        /**
         * Returns this instances UserProfile member variable
         * @return an UserProfile object
         */
        public E getResponse() {
            return response;
        }
    }

    /**
     * * Instances of this type are passed to PrettyLogger's onEvent() method.
     */
    public static class PrettyString {

        private String string;
        private int type;

        /**
         * Creates a new PrettyString instance, initializing its fields to the corresponding arguments
         * @param string the string message of this instance
         * @param type this value should be one of android.util.Log's DEBUG,ERROR,INFO,WARN constants.
         *             If not then android.util.Log.INFO is stored as this instances type
         */
        public PrettyString(String string, int type) {
            this.string = string;
            this.type = getValidType(type);
        }

        //The following 4 methods constitute the Getters and setters for string and type
        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public int getType() {
            return type;
        }

        /**
         * Sets the type of this instance with the value passed in. If the passed in type
         * matches none of the supported android.util.Log constants, then android.util.Log.INFO is
         * stored as this instances's type
         * @param type this value should be one of android.util.Log's DEBUG,ERROR,INFO,WARN constants
         */
        public void setType(int type) {
            this.type = getValidType(type);
        }

        /**
         * Gets a possible type value and returns it if it is valid, else android.util.Log.INFO is returned
         * @param t A passed in type to check
         * @return A valid type value
         */
        private int getValidType(int t){
            if(t== Log.DEBUG ||
                    t== Log.ERROR ||
                    t== Log.INFO ||
                    t== Log.WARN){
                return t;
            }
            return Log.INFO; //I picked the most neutral Log constant to return if the passed in type was not
            // a type that is supported
        }
    }
}
