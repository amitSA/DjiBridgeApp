package test.com.bridge.callback;

import android.os.Handler;
import android.os.Looper;

import test.com.bridge.callback.EventCallback.NetworkCallback;
import test.com.bridge.callback.AllEvents.NetworkCallbackEvent;

/**
 * Created by Amit on 8/24/2017.
 */

/**
 * Convenient class that extends from NetworkCallback.  Basically it offers
 * a way for subclasses to easily repeat a network request if the current one failed.
 *
 * How To Use: Create an anonymous subclass and write your regular onEvent() you would do for a
 * NetworkCallback in your implemented onEventCall() method.  Then implement toRun() to run
 * a network request (or do some task) when the current network request failed and onEventCall() returned true.
 *
 * (Basically, to indicate that you want to try a network request again (like because it failed), you return true in the overrode onEventCall() method )
 *
 * Future Work: implement a way to set a max number of times the toRun() method can be called (this should not be hard)
 * @param <E>
 */
public abstract class RepeatableNetworkCallback<E> implements NetworkCallback<E> {

    private long delayTime; //current delayTime for repeated requests
    final private long INCREMENT; //increment that will be added to delayTime for each failed request
    final private long MAX_DELAY_TIME; //the max value that delayTime can be
    private Handler handler;

    private RepeatableNetworkCallback instance;

    public RepeatableNetworkCallback(long initialDelayTime, long increment, long maxDelayTime){
        this.delayTime = initialDelayTime;
        this.INCREMENT = increment;
        this.MAX_DELAY_TIME = maxDelayTime;

        handler = new Handler(Looper.getMainLooper());
        instance = this;
    }

    /**
     * This NetworkCallback's implementation of onEvent().  Here we do the logic
     * of checking whether onEventCall() indicated we should call toRun() again.
     *
     * NOTE: subclasses should not override this method, instead they should override onEventCall()
     * @param event
     */
    @Override
    public void onEvent(NetworkCallbackEvent<E> event){
        delayTime = Math.min(delayTime,MAX_DELAY_TIME);
        boolean runAgain  = onEventCall(event);
        
        if(runAgain){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toRun(instance);
                    delayTime+=INCREMENT;
                }
            },delayTime);
        }
    }



    /**
     * The method that should be run repeatably
     * @param myself A convenient instance of this RepeatableNetworkCallback.  It's assumed that
     *               you will use this NetworkCallback object again in some async task( to keep it 'repeating' )
     */
    public abstract void toRun(RepeatableNetworkCallback<E> myself);

    /**
     *
     * @param event
     * @return true if you want to run the network callback again, false otherwise
     */
    public abstract boolean onEventCall(NetworkCallbackEvent<E> event);

    /**
     * Get the current delayTime of this object
     * @return a long representing the delayTime
     */
    public long getDelayTime() {
        return delayTime;
    }

    /**
     * Get the increment that will be added to delayTime for each failed request
     * @return A long representing the increment
     */
    public long getIncrement(){
        return INCREMENT;
    }
}
