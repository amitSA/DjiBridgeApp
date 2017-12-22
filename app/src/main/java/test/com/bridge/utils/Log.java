package test.com.bridge.utils;

/**
 * Created by Amit on 7/18/2017.
 */

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import test.com.bridge.callback.CallbackList;
import test.com.bridge.callback.EventCallback.PrettyLogger;
import test.com.bridge.callback.AllEvents.PrettyString;

/**
 * Utility class that offers methods for logging messages to console.
 * The TAG_# fields are values that are inserted into the tag parameters of the corresponding android.util.Log.d() method call
 *   For example...
 *   GENERAL is used in Log.d
 *   TAG_1 is used in Log.d1
 *   TAG_2 is used in Log.d2
 *   TAG_3 is used in Log.d3 and on...
 *
 *
 *   Also, this class keeps a List of PrettyLoggers registered to it.  A call to any of the
 *   Log.pretty...() methods will trigger the onEvent() methods of all the PrettyLoggers to be called.
 *      - Along with the list, this class defines a PrettyLogger implementation called MahPrettyLogger.
 *      This implementation keeps an internal List of all PrettyString events sent to it.  This is
 *      so that there is a record of every PrettyString event (caused by any Log.pretty...() call)
 *      that ever occurred.
 */
public class Log {


    public final static boolean debug = true;

    private static CallbackList<PrettyLogger,PrettyString> prettyLoggerList = new CallbackList<>();
    private static MahPrettyLogger globalLogger;

    private static Object lock = Log.class;

    //NOTE: all main logs are made using Log.d(), which uses the GENERAL tag
    public final static String GENERAL = "amit-";

    //All these other tags are for testing out sub-components of the app
    public final static String TAG_1 = "pusher-";  //Log.d1 used in DataFramePusher
    public final static String TAG_8 = "pusher-more-"; //Log.d1 also used in DataFramePusher

    public final static String TAG_2 = "store-"; //Log.d2 used in storage package
    public final static String TAG_3 = "signin-"; //Log.d3 used in SignInActivity
    public final static String TAG_4 = "bridging-"; //Log.d4 also used in DataFramePusher
    public final static String TAG_5 = "frag-"; //Log.d5 used in Fragment classes

    public final static String TAG_6 = "waypoints1-"; //Log.d6 and Log.d7 used in Waypoint classes in the service package
    public final static String TAG_7 = "waypoints2-";


    /**
     * Does preliminary initializations/setup.  This should be called before any static method
     * of this class is called
     * Note: Called in BridgeApplication's onCreate() method.
     */
    public static void initialize(){
        globalLogger = new MahPrettyLogger();
        prettyLoggerList.add(globalLogger);
    }

    private static int plain(String prefix, String text) {
        if(debug){
            return android.util.Log.d(prefix,text);
        }
        return -1;
    }
    /*All the following public methods should call Log.plain() in someway.  They should not
      call any android.util.Log methods explicitly (because Log.plain() does a check if debug is true or not)
    */
    public static int d(String prefix, String text) {
        return plain(GENERAL+prefix,text);
    }
    public static int d1(String prefix, String text) {
        return plain(TAG_1+prefix,text);
    }
    public static int d2(String prefix, String text) {
        return plain(TAG_2+prefix,text);
    }
    public static int d3(String prefix, String text) {
        return plain(TAG_3+prefix,text);
    }
    public static int d4(String prefix, String text) {
        return plain(TAG_4+prefix,text);
    }
    public static int d5(String prefix, String text) {
        return plain(TAG_5+prefix,text);
    }  //The fragments call the d5 method when they want to report a lifecycle change.  I decided not to use those fragment logs for Log.d to not clutter Log.d even more than it is
    public static int d6(String prefix, String text) {
        return plain(TAG_6+prefix,text);
    }
    public static int d7(String prefix, String text) {
        return plain(TAG_7+prefix,text);
    }
    public static int d8(String prefix, String text) {
        return plain(TAG_8+prefix,text);
    }
    public static int dd1(String prefix, String text) {
        return plain(GENERAL+TAG_1+prefix,text);
    }

    /**
     * Calls the onEvent(PrettyString) method of every PrettyLogger registered to this class
     * @param message
     * @param type
     */
    private static void pretty(String message, int type){
        synchronized (lock){
            prettyLoggerList.triggerEvents(new PrettyString(message,type));
        }
    }

    //All the following methods call Log.pretty(String,type) with different integer types passed to that method
    public static void prettyD(String message){
        pretty(message, android.util.Log.DEBUG);
    }
    public static void prettyE(String message){
        pretty(message, android.util.Log.ERROR);
    }
    public static void prettyI(String message){
        pretty(message, android.util.Log.INFO);
    }
    public static void prettyW(String message){
        pretty(message, android.util.Log.WARN);
    }

    /**
     * Adds the passed-in PrettyLogger to this class's list of PrettyLogger listeners.
     * As long as this logger is not removed, any Log.pretty...() call to this class will now trigger the passed-in PrettyLogger's onEvent() method
     * @param logger The PrettyLogger to register as a callback to Log.pretty...() events
     */
    public static void addPrettyLogger(PrettyLogger logger){
        prettyLoggerList.add(logger);
    }

    /**
     * Remove the passed-in PrettyLogger from this class's list of PrettyLogger listeners
     * @param logger The PrettyLogger to remove
     */
    public static void removePrettyLogger(PrettyLogger logger){
        prettyLoggerList.remove(logger);
    }

    /**
     * This compound method does two things: first it calls logger's onEvent() method for every PrettyString
     * event that has past since the beginning of this application.  And then it adds logger to this class's list
     * of registered PrettyLogger listeners.
     *
     * Since this method is internally synchronized with the Log.pretty...() methods,
     * the caller is guaranteed that if a PrettyString event occurs in the middle of this method's execution, then
     * the caller will still receive that PrettyString event
     * @param logger
     */
    public static void addWithAllPreviousLogs(PrettyLogger logger){
        synchronized (lock){
            Iterator<PrettyString> iterator = globalLogger.getLogList().iterator();
            while(iterator.hasNext()){
                logger.onEvent(iterator.next());
            }
            addPrettyLogger(logger);
        }
    }

    /**
     * This class's implementation of PrettyLogger.  An instance of this class is kept
     * to record every PrettyString event that occurs - this is necessary in order to
     * support the addWithAllPreviousLogs() method
     */
    private static class MahPrettyLogger implements PrettyLogger{
        private List<PrettyString> logList;
        public MahPrettyLogger  (){
            logList = new LinkedList<>();
        }
        @Override
        public void onEvent(PrettyString event) {
            logList.add(event);
        }
        public List<PrettyString> getLogList(){
            return logList;
        }
    }

}
