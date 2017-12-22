package test.com.bridge.clientbridge;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.DataFrame;
import io.swagger.client.model.InlineResponse2002;

import io.swagger.client.model.InlineResponse2003;
import io.swagger.client.model.InlineResponse2006;
import io.swagger.client.model.TrajectoryPoint;
import test.com.bridge.clientbridge.ModedRequestFuture.ReturnValue;

/**
 * Created by Amit on 8/8/2017.
 */

/**
 * A class to help make asynchronous api-calls to the PaaS server.
 * I created this class as a direct wrapper around the android-client's DefaultApi class.  So
 *      methods that exist in DefaultApi have identically named methods in this class representing
 *      the 'async' version of that method (and this class uses those DefaultApi methods internally).
 * Note: DefaultApi has 2 versions(aka 2 overloaded methods) for every end-point it exposes. One of
 *      those versions does not take listener arguments and is synchronous, while the other version takes
 *      listener arguments and is asynchronous.
 *      Thus one may ask why we need this class if DefaultApi already supports asynchronous method calls.
 *      The reason is because those asynchronous method do not support giving back Timeout Exceptions because
 *      they do not use futures.  However this class' asynchronous methods do support giving back Timeout Exceptions
 *      by using a modified version of futures (look at ModdedRequestFuture)
 *
 */
public class AsyncDefaultApi {
    private final static String CLASSNAME = AsyncDefaultApi.class.getSimpleName();
    private final static long TIMEOUT_TIME = 5000;
    private DefaultApi defaultApi;

    ThreadPoolExecutor threadPoolExecutor;

    public AsyncDefaultApi(DefaultApi api) {
        defaultApi = api;
        initializeThreadPoolExecutor();
    }

    /**
     * Initializes the member variable ThreadPoolExecutor object.  Its an android class that basically
     * acts as a thread pool and allows me to posts runnables that will be executed by the next available thread
     * in the pool
     */
    private void initializeThreadPoolExecutor(){
        int corePoolSize = 4;
        int maximumPoolSize = corePoolSize;
        long keepAliveTime = 30 * 1000;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,unit,new LinkedBlockingDeque<Runnable>());
        threadPoolExecutor.allowCoreThreadTimeOut(true); //This is so that all threads will be stopped if idle for keepAliveTime miliseconds
        //..in the situation that ThreadPoolExecutor.shutdown() isn't called when the app is being idle, then these threads will still be stopped

    }

    /**
     * Async wrapper method around DefaultApi.userInfoV1() method.
     * @param username this will be passed directly to DefaultApi.userInfoV1()
     * @param responseListener its callback method will be called if a response is received (indicating an successful operation)
     * @param errorListener its callback method will be called if an error occurred in the request
     */
    public void userInfoV1(final String username, final Response.Listener<InlineResponse2002> responseListener, final Response.ErrorListener errorListener){
        final ModedRequestFuture<InlineResponse2002> future = ModedRequestFuture.newFuture();
        final Response.ErrorListener wrapperErrorListener = getWrapperErrorListener(future);
        final Response.Listener<InlineResponse2002>wrapperResponseListener = getWrapperResponseListener(future);
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                defaultApi.userInfoV1(username,wrapperResponseListener,wrapperErrorListener);
                ReturnValue<InlineResponse2002> retValue = new ReturnValue<>(null,null);
                try {
                    retValue = future.get(defaultApi.getInvoker().getConnectionTimeout(),TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    retValue.setException(e);
                }finally {
                    if(retValue.getException()==null){
                        responseListener.onResponse(retValue.getResult());
                    }else{
                        //see @commentA
                        errorListener.onErrorResponse(new VolleyError(retValue.getException()));
                    }
                }
            }
        });
    }

    /**
     * Async wrapper method around DefaultApi.sessionStatusV1() method.
     * @param username
     * @param vehicleId
     * @param responseListener
     * @param errorListener
     */
    public void sessionStatusV1(final String username, final long vehicleId, final Response.Listener<InlineResponse2006> responseListener, final Response.ErrorListener errorListener){
        final ModedRequestFuture<InlineResponse2006> future = ModedRequestFuture.newFuture();
        final Response.ErrorListener wrapperErrorListener = getWrapperErrorListener(future);
        final Response.Listener<InlineResponse2006>wrapperResponseListener = getWrapperResponseListener(future);
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                defaultApi.sessionStatusV1(username,vehicleId,wrapperResponseListener,wrapperErrorListener);
                ReturnValue<InlineResponse2006> retValue = new ReturnValue<>(null,null);
                try {
                    retValue = future.get(defaultApi.getInvoker().getConnectionTimeout(),TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    retValue.setException(e);
                }finally {
                    if(retValue.getException()==null){
                        responseListener.onResponse(retValue.getResult());
                    }else{
                        //see @commentA
                        errorListener.onErrorResponse(new VolleyError(retValue.getException()));
                    }
                }
            }
        });
    }

    /**
     * Async wrapper method around DefaultApi.vehicleStatusV1() method.
     * @param username
     * @param vehicleId
     * @param responseListener
     * @param errorListener
     */
    public void vehicleStatusV1(final String username, final long vehicleId, final Response.Listener<InlineResponse2003> responseListener, final Response.ErrorListener errorListener){
        final ModedRequestFuture<InlineResponse2003> future = ModedRequestFuture.newFuture();
        final Response.ErrorListener wrapperErrorListener = getWrapperErrorListener(future);
        final Response.Listener<InlineResponse2003>wrapperResponseListener = getWrapperResponseListener(future);
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                defaultApi.vehicleStatusV1(username,vehicleId,wrapperResponseListener,wrapperErrorListener);
                ReturnValue<InlineResponse2003> retValue = new ReturnValue<>(null,null);
                try {
                    retValue = future.get(defaultApi.getInvoker().getConnectionTimeout(),TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    retValue.setException(e);
                }finally {
                    if(retValue.getException()==null){
                        responseListener.onResponse(retValue.getResult());
                    }else{
                        //see @commentA
                        errorListener.onErrorResponse(new VolleyError(retValue.getException()));
                    }
                }
            }
        });
    }


    /**
     * Async wrapper method around DefaultApi.dataAddV1() method.
     * @param username this will be passed directly to DefaultApi.dataAddV1()
     * @param sessionId this will be passed directly to DefaultApi.dataAddV1()
     * @param dataFrame this will be passed directly to DefaultApi.dataAddV1()
     * @param responseListener its callback method will be called if a response is received (indicating an successful operation)
     * @param errorListener its callback method will be called if an error occurred in the request
     */
    public void dataAddV1(final String username, final Long sessionId, final DataFrame dataFrame, final Response.Listener<String> responseListener, final Response.ErrorListener errorListener){
        final ModedRequestFuture<String> future = ModedRequestFuture.newFuture();
        final Response.ErrorListener wrapperErrorListener = getWrapperErrorListener(future);
        final Response.Listener<String>wrapperResponseListener = getWrapperResponseListener(future);

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                defaultApi.dataAddV1(username,sessionId,dataFrame,wrapperResponseListener,wrapperErrorListener);
                ReturnValue<String> retValue = new ReturnValue<>(null,null);
                try {
                    retValue = future.get(defaultApi.getInvoker().getConnectionTimeout(),TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    retValue.setException(e);
                }finally {
                    if(retValue.getException()==null){
                        responseListener.onResponse(retValue.getResult());
                    }else{
                        //see @commentA
                        errorListener.onErrorResponse(new VolleyError(retValue.getException()));
                    }
                }
            }
        });
    }

    /**
     * The AsyncDefaultApi wrapper around DefaultAPi's trajectorySetV1()
     *
     * Note: This method should be unit tested
     * @param username
     * @param trajectoryPoint
     * @param vehicleId
     * @param responseListener
     * @param errorListener
     */
    public void trajectorySetV1(final String username, final TrajectoryPoint trajectoryPoint, final Long vehicleId, final Response.Listener<String> responseListener, final Response.ErrorListener errorListener){
        final ModedRequestFuture<String> future = ModedRequestFuture.newFuture();
        final Response.ErrorListener wrapperErrorListener = getWrapperErrorListener(future);
        final Response.Listener<String>wrapperResponseListener = getWrapperResponseListener(future);

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                defaultApi.trajectorySetV1(username,trajectoryPoint,vehicleId,wrapperResponseListener,wrapperErrorListener);
                ReturnValue<String> retValue = new ReturnValue<>(null,null);
                try {
                    retValue = future.get(defaultApi.getInvoker().getConnectionTimeout(),TimeUnit.SECONDS);  //DefaultApi takes the request from here!!!
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    retValue.setException(e);
                }finally {
                    if(retValue.getException()==null){
                        responseListener.onResponse(retValue.getResult());
                    }else{
                        //see @commentA
                        errorListener.onErrorResponse(new VolleyError(retValue.getException()));
                    }
                }
            }
        });
    }

    /*NOTE: you will notice that defaultApi.trajectoryRemoveV1() takes in a TrajectorPoint object which
            I am setting as null.  I talked to Jason and he said that the remove trajectory request should
            just remove all trajectories (so there is no need to specify a trajectory).

            However, Jason also said that it has not been implemented yet on the server side.
            So the client needs to regenerated once the server and .yaml file support this
    */

    /** Read above note^
     *
     *
     * @param username
     * @param vehicleId
     * @param responseListener
     * @param errorListener
     */
    public void trajectoryRemoveV1(final String username, final Long vehicleId, final Response.Listener<String> responseListener, final Response.ErrorListener errorListener){
        final ModedRequestFuture<String> future = ModedRequestFuture.newFuture();
        final Response.ErrorListener wrapperErrorListener = getWrapperErrorListener(future);
        final Response.Listener<String>wrapperResponseListener = getWrapperResponseListener(future);

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                defaultApi.trajectoryRemoveV1(username,null,vehicleId,wrapperResponseListener,wrapperErrorListener);
                ReturnValue<String> retValue = new ReturnValue<>(null,null);
                try {
                    retValue = future.get(defaultApi.getInvoker().getConnectionTimeout(),TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    retValue.setException(e);
                }finally {
                    if(retValue.getException()==null){
                        responseListener.onResponse(retValue.getResult());
                    }else{
                        //see @commentA
                        errorListener.onErrorResponse(new VolleyError(retValue.getException()));
                    }
                }
            }
        });
    }





    /**
     * Helper method to create a Response.ErrorListener object that will be used in the asynchronous end-point methods this class defines
     * @param future The future object that will be used in the callback method
     * @param <E> Generic type of future object
     * @return The created Response.ErrorListener
     */
    private <E> Response.ErrorListener getWrapperErrorListener(final ModedRequestFuture<E> future){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                future.onErrorResponse(error);
            }
        };
    }
    /**
     * Helper method to create a Response.Listener object that will be used in the asynchronous end-point methods this class defines
     * @param future The future object that will be used in the callback method
     * @param <E> Generic type of future object and returned object
     * @return The created Response.Listener
     */
    private <E> Response.Listener<E> getWrapperResponseListener(final ModedRequestFuture<E> future){
        return new Response.Listener<E>() {
            @Override
            public void onResponse(E response) {
                future.onResponse(response);
            }
        };
    }

    /**
     * Method to execute any runnable asynchronously
     *
     * Note: Really, this method was just conveniently made so that I can execute a bunch of GET Requests together in one runnable
     * without having to create an asynchronous version for each of them in this class (see ClientDispatcher.getUserDataRunnable() and how that method is used internally)
     * Eventually, I should never call this method and create a separate async method in this class for all of those GET Requests
     * @param r the runnable to execute asynchronously
     */
    public void execute(Runnable r){
        threadPoolExecutor.execute(r);
    }

    /**
     * Call this method when you are done using this instance and want it to deallocate its resources
     */
    public void ceaseSelf(){
        stopThreadPool();
    }
    private void stopThreadPool(){
        threadPoolExecutor.shutdown();
    }

}

//(NOTE: After 2 weeks from writing the below comment: the below comment really doesn't matter anymore, there's way biger stuff you should probably do m8)
/*@commentA
    TODO: NOTE: this new VolleyError object is created with a java api Exception object that was created (possibly) with a volley error object
    This chain of Exception objects seems like unnecessary overhead.  However, it might be a necessary piece of overhead
    based on the current way the client is set up (or maybe I can avoid this chain?) b/c its possible that the future object just returns a timeout exception
    and not an object that is wrapping a volley object. Hmm, is it even worth it to optimise this? Only optimise it
    if you can think of a solution that would improve the architectural model of this part of the program and/or does not require much code changes
 */

