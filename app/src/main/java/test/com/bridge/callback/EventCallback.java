package test.com.bridge.callback;

/**
 * Created by Amit on 8/9/2017.
 */

import test.com.bridge.UserAccount.SignedInStatus;
import test.com.bridge.callback.AllEvents.ConnectionType;
import test.com.bridge.callback.AllEvents.BatteryFrame;
import test.com.bridge.callback.AllEvents.PrettyString;
import test.com.bridge.callback.AllEvents.NetworkCallbackEvent;

/**
 * Most of all Listeners used in the app are declared in this file and extend from EventCallback<I>
 * @param <I> the type of event this EventCallback takes
 */
public interface EventCallback<I extends Object> {
    public void onEvent(I event);

    /**
     * NOTE: Below, I have mentioned how each Listener is currently being used, but that is no means
     * supposed to be their only use cases. If this app grows, feel free to use existing Listener types
     */

    /**
     * Used to signal from ConnectionService to other parts of the app when
     * connection changes with the DJI drone happen.
     *
     * Member variable in ConnectionService.  Implemented in MainActivity and DataFramePusher
     */
    public interface ConnectionChangedCallback extends EventCallback<ConnectionType> {

    }

    /**
     * Used to pass a recurrent frame of battery telemetry data from ConnectionService/DataFramePusher
     * to other parts of the app
     * Member variable in ConnectionService and DataFramePusher.  Implemented in MainActivity
     */
    public interface BatteryValuesCallback extends EventCallback<BatteryFrame> {

    }
    /**
     * A callback used to signal the outcome of some Network request.
     * Used as a variable in ClientDispatcher and implemented in the classes that call ClientDispatcher methods
     * (ie. ClientDispatcher and BridgeApplication)
     */
    public interface NetworkCallback<G> extends EventCallback<NetworkCallbackEvent<G>>{

    }

    /**
     * Used in test.com.bridge.utils.Log (referred to as 'Log' in the rest of this comment).
     *
     * Along with android's Logcat logger, I wanted to create a logger that
     * displayed messages the user is meant to see. See the Log.pretty...() methods.
     *
     * Used as member variable in Log.  Implemented in Log and LoggerFragment.
     *
     */
    public interface PrettyLogger extends EventCallback<PrettyString>{

    }

    /**
     * Callback used by UserAccount to notify users when a sign-in change occurs in the app.
     *
     * Classes like MainActivity, BridgeApplication, and ConnectionService all implement this callback and
     * register themselves with UserAccount so whenever there is a sign-in change, those classes will be notified
     *
     * Note: Sign-out occurrences are also included as "sign-in changes"
     */
    public interface AuthCallback extends EventCallback<SignedInStatus>{

    }


}
