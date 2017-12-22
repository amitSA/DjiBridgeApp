package test.com.bridge.service;



import test.com.bridge.BatteryTuple;
import test.com.bridge.callback.AllEvents.BatteryFrame;
import test.com.bridge.callback.CallbackList;
import test.com.bridge.callback.EventCallback;
import test.com.bridge.callback.AllEvents.NetworkCallbackEvent;
import test.com.bridge.clientbridge.ClientDispatcher;
import test.com.bridge.utils.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import dji.common.battery.BatteryState;
import dji.sdk.battery.Battery;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;
import test.com.bridge.callback.AllEvents.ConnectionType;


/**
 * Created by Amit on 7/13/2017.
 */

/**
 * This class receives Telemetry data from the DJI Drone and pushes that data to
 *  the PaaS server and all the callbacks in the field servicesCallbacks.
 *
 *  Practically, this class acts like an inner class of ConnectionService.
 *  I just made it its own class for readability
 */
public class DataFramePusher implements EventCallback.ConnectionChangedCallback{

    //TODO: I don't think any of the below fields need to be declared as volatile since they will all be accessed in the same process.  Verify if this statement is true

    private static String CLASSNAME = DataFramePusher.class.getSimpleName();

    private ConnectionService parentService;
    volatile private Timer timer;


    private CallbackList<BatteryValuesCallback,BatteryFrame> servicesCallbacks;

    volatile private ArrayList<List<BatteryTuple>> batteriesList;
    volatile private DataTally dataTally;

    private final static int MAX_TIMER_PERIOD = 1000;
    private final static int TIMER_PERIOD = 100;

    private long lastTimeFired;

    private TallyNetworkCallback<String> tallyCallback;

    private Object lock = new Object();

    /**
     * Constructs a new DataFramePusher object.  Needs an instance of the ConnectionService
     * where its being created, also needs an instance of a CallbackList for BatteryValuesCallback elements.
     *
     * It is expected that BatteryValuesCallback elements will be added to that list by the some
     * other object (like the object that created this object)
     * @param cs The parent ConnectionService.  (This object should be created within a ConnectionService)
     * @param cbs CallbackList for BatteryValuesCallbacks
     */
    public DataFramePusher(ConnectionService cs, CallbackList<BatteryValuesCallback,BatteryFrame> cbs) {
        parentService = cs;
        timer = new Timer(true);

        timer = null;
        servicesCallbacks = cbs;
        batteriesList = new ArrayList<List<BatteryTuple>>();
        dataTally = new DataTally();

        tallyCallback = new TallyNetworkCallback<>();
    }

    /**
     * re-initializes the timer
     * @return
     */
    private Timer getNewScheduledTimer(){
        tallyCallback.zeroTallies();
        lastTimeFired = System.currentTimeMillis();
        //lastLogTime = System.currentTimeMillis();

        Timer timer = new Timer(true); //Yes run this thread as a daemon so it does not prolong the life of the application
        timer.schedule(getFlushTimerTask(),0,TIMER_PERIOD);
        return timer;
    }

    /**
     * This runnable will call _flush_data_() if the time elapsed since
     * the last _flush_data_() call passes over the max wait-time threshold
     * @return The runnable that does this^
     */
    private TimerTask getFlushTimerTask(){
        return new TimerTask() {
            @Override
            public void run() {
                tallyCallback.timerLog();
                synchronized (lock){
                    /*ConnectionType currentType = parentService.getLastConnectionType();
                    if(!HelpMes.isConnectedType(currentType)){
                        return;
                    }*/
                    if(timer == null){ //If timer is equal to null, then the connection has disconnected, else the connection is still active
                        return;
                    }
                    if(System.currentTimeMillis()-lastTimeFired<=MAX_TIMER_PERIOD){
                        return;
                    }
                    Log.d1(CLASSNAME,"MAX_TIMER_PERIOD amount of time has passed since last flush, so calling _flush_data_()");
                    _flush_data_();
                }
            }
        };
    }
    /**
     * This method averages the 3 telemetry data-types for all batteries and
     * then packages that information into a 1 'frame' of telemetry data.
     *
     * Then this frame of data is passed to all the listeners in servicesCallbacks,
     * and then passed to the PaaS server (via ClientDispatcher)
     *
     * @pre This method should only be called for the specific purpose of processing batteriesList and calling UI callback and starting the async server push
     * @pre This method should be called by a thread that owns the lock of the field called 'lock'
     * */
    private void _flush_data_(){
        if(batteriesList.size()==0){ //A just in case check, not sure if this needed though (synchronization might even prevent this from ever happening)
            return;
        }
        lastTimeFired = System.currentTimeMillis();
        BatteryTuple [] batteryAvgs = new BatteryTuple[batteriesList.size()];
        boolean someDataRecieved = false; //this holds whether at least one tupleList had data.  I'm not using this variable though, SQUAW!
        for(int i = 0;i<batteriesList.size();i++){
            List<BatteryTuple> tupleList = batteriesList.get(i);
            int volt_avg = 0;
            int curr_avg = 0;
            float temp_avg = 0;
            synchronized (tupleList){
                for(int j = 0;j<tupleList.size();j++){
                    BatteryTuple tuple = tupleList.get(j);
                    volt_avg += tuple.getVoltage();
                    curr_avg += tuple.getCurrent();
                    temp_avg += tuple.getTemperature();
                }
                batteryAvgs[i] = null; //null symbolized we received no telemetry data for this battery

                if(tupleList.size()>0) { //to protect from divide by 0 exception, and override the null value at [i] if there was at least one package of telemetry data received
                    someDataRecieved = true;
                    volt_avg = (int)((float)volt_avg/tupleList.size()+0.5);
                    curr_avg = (int)((float)curr_avg/tupleList.size()+0.5);
                    temp_avg = temp_avg / tupleList.size(); //float division; so temp_avg will have a pretty accurate decimal value
                    batteryAvgs[i] = new BatteryTuple(volt_avg, curr_avg, temp_avg); //setting a BatteryTuple representing the averaged telemetry data of the ith battery
                }
                tupleList.clear(); //deleting all elements in Tuplelist
            }
        }
        dataTally.clear();

        servicesCallbacks.triggerEvents(new BatteryFrame(batteryAvgs));

        //pushing frame to server
        PaaSSession paasSession = parentService.getActivePaaSSession();
        if(paasSession!=null && paasSession.canPushTelemetryData()){
            Log.d4(CLASSNAME,"calling ClientDispatcher.httpPostDataFrame() to send frame of telemetry data");
            ClientDispatcher client = ClientDispatcher.getInstance();
            client.httpPOSTDataFrame(tallyCallback,batteryAvgs,paasSession);
        }
    }

    /**
     * NetworkCallback for the POST request made in _flush_data_()
     * @return
     */


    private NetworkCallback<String> getNewPOSTCallback(){
        return new NetworkCallback<String>(){
            @Override
            public void onEvent(NetworkCallbackEvent<String> event){
                Exception e = event.getException();
                if(e!=null){
                    Log.d(CLASSNAME,"error in posting data frame");
                    e.printStackTrace();
                }
            }
        };
    }


    public void onDestroy(){
        if(timer!=null){
            timer.cancel();
        }
    }

    /**
     * This class's implementation of ConnectionChangedCallback.
     * We handle what this object should do for every ConnectionType event received from the handler (*successive coughs* ConnectionService)
     * @param type
     */
    @Override
    public void onEvent(ConnectionType type){
        synchronized (lock) {
            if (type == ConnectionType.INITIAL_PRODUCT_CONNECTION) {
                BaseProduct product = DJISDKManager.getInstance().getProduct();
                List<Battery> battList = product.getBatteries();
                batteriesList.clear();
                for (int i = 0; i < product.getBatteries().size(); i++) {
                    boolean CHECK = product.getBatteries().get(i).getIndex() == i;
                    if (!CHECK) {
                        String errMsg = "ERROR IN REASONING FOR DJI SDK, it seems that the battery index in the returned list does not correspond to the batteries index in the list";
                        Log.d(CLASSNAME, errMsg);
                        throw new RuntimeException(errMsg);
                    }

                    batteriesList.add(new LinkedList<BatteryTuple>());
                    setBatteryStateCallback(battList.get(i), batteriesList.get(i));
                }
                dataTally.init(battList.size());
            }
            if (type == ConnectionType.INITIAL_PRODUCT_CONNECTION || type == ConnectionType.CONNECTION_RECONNECTED) {
                timer = getNewScheduledTimer();
            }
            if (type == ConnectionType.PRODUCT_DISCONNECTED || type == ConnectionType.CONNECTION_DISCONNECTED) {
                timer.cancel();
                timer = null; //This is important, because other parts of the class use the fact of timer equaling null to symbolize the connection disconnected
            }
            if (type == ConnectionType.PRODUCT_DISCONNECTED || type == ConnectionType.SERVICE_NOT_STARTED /*|| type == ConnectionType.UNCONNECTED_T*/) {
                batteriesList.clear(); //NOTE: This is for debugging, b/c if my snchronization works properly, then deleting all elements from batteriesList should not cause an arrayoutofbounds exception anywhere else
            }
        }
    }

    /**
     * This method receives battery telemetry data intermittently, and also checks
     * if the frame is full or not.
     * If the frame is full, then this method calls _flush_data_()
     * @param bat
     * @param list
     */
    private void setBatteryStateCallback(final Battery bat,     final List<BatteryTuple> list){
        bat.setStateCallback(new BatteryState.Callback() {
            @Override
            public void onUpdate(BatteryState battState) {
                BatteryTuple tuple = new BatteryTuple()
                        .setVoltage(battState.getVoltage())
                        .setCurrent(battState.getCurrent())
                        .setTemperature(battState.getTemperature());
                synchronized (list){
                    list.add(tuple);
                }
                int batIndex = bat.getIndex();
                dataTally.tallyBattery(batIndex);
                if(dataTally.isFull()){

                    if(timer == null){
                        return;
                    }
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            synchronized (lock){
                                //ConnectionType currentType = parentService.getLastConnectionType();
                                if(!dataTally.isFull()){ //because in between the first isFull() call and schedule(), the data might have been flushed(by the continous flushtimertask's execution).  Or while the current thread was blocked by the synchronization, the data could have been flushed (by the same reason as before) or the connection been disconnected
                                    return;
                                }
                                if(timer == null) { //If timer is null, that means the connection disconnected
                                    return;
                                }
                                /*if(!HelpMes.isConnectedType(currentType)){ //see above comment
                                    return;
                                }*/
                                Log.d1(CLASSNAME,"data frame is full, so calling _flush_data_()");
                                _flush_data_();
                            }
                        }
                    },0);
                }
            }
        });
    }

    /**
     * This class is supposed to provide a convenient and abstracted way to record
     * which battery's telemetry data has been received, and thus whether a frame is full or not
     */
    private class DataTally{
        long batTally;
        int numBatteries;
        public DataTally(){
        }

        /**
         * initialize the DataTally object to record information for numBatt amount of batteries
         * @param numBatt
         */
        public void init(int numBatt){
            numBatteries = numBatt;
            clear();
            if(numBatteries>62){
                throw new IllegalArgumentException("DataTally can only support up till 62 batteries.  Num batteries entered: "+numBatteries);
            }
        }

        /**
         * Clear the current frame
         */
        public void clear(){
            batTally = 0;
        }

        /**
         * Mark a battery as received
         * @param i Index correlating which battery to mark
         */
        public void tallyBattery(int i){
            int mark = 1<<i;
            batTally |= mark;
        }

        /**
         * Check if the current frame is full or not
         * @return true if the frame is full, false otherwise
         */
        public boolean isFull(){
            int allOne = (1<<numBatteries) - 1;
            boolean retValue = (batTally&allOne) == allOne;
            return retValue;
        }
    }


    private class TallyNetworkCallback<E> implements NetworkCallback<E>{
        private int NaN = -1;
        private int requestNum = 0;
        private int successfulRequests = 0;

        private final static int LOG_INTERVAL = 10*1000;
        private long lastLogTime;

        final Object lock = new Object();

        public TallyNetworkCallback(){
            zeroTallies();
        }

        @Override
        public void onEvent(NetworkCallbackEvent<E> event) {
            Exception err = event.getException();
            E response = event.getResponse();

            //DEBUG CODE
            String msg = "network response: ";
            if(err!=null){
                msg += "exception-"+err.toString();
            }else{
                msg +=  ((response!=null)?response.toString():"null");
            }
            Log.d1(CLASSNAME,msg);
            //DEBUG CODE

            synchronized (lock){
                requestNum++;
                if(err==null){
                    successfulRequests++;
                }
            }
        }
        public void zeroTallies(){
            synchronized (lock){
                requestNum = 0;
                successfulRequests = 0;
                lastLogTime = System.currentTimeMillis();
            }

        }

        /**
         * Returns a float between 0 to 1 indicating the success rate for telemetry data in the last LOG_INTERVAL seconds
         * or returns NaN if there were requests sent in that time frame
         * @return a float between 0 to 1 or NaN
         */
        public float getSuccessRate(){
            synchronized (lock){
                if(requestNum>0){
                    return ((float)(successfulRequests))/requestNum;
                }else{
                    return NaN;
                }

            }
        }

        /**
         * Should be called repeatingly by a timer
         */
        public void timerLog(){
            synchronized (lock){
                long currentTime = System.currentTimeMillis();
                if(currentTime-LOG_INTERVAL>lastLogTime){
                    lastLogTime = System.currentTimeMillis();
                    float rateF = getSuccessRate();
                    String rateStr =  rateF!=NaN ?  (int)(getSuccessRate()*100+0.5)+"%" : "NaN";
                    String prettyMsg = String.format(Locale.US,
                            "network success rate for telemetry data: %s",
                            rateStr);
                    if(getSuccessRate()>90){
                        Log.prettyI(prettyMsg);
                    }else{
                        Log.prettyW(prettyMsg);
                    }
                }
            }
        }
        /*private void setLastLogTime(long time){
            synchronized (lock){
                lastLogTime = time;
            }
        }
        private long getLastLogTime(){
            synchronized (lock){
                return lastLogTime;
            }
        }*/
    };



}
