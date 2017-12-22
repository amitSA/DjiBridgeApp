/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.com.bridge.clientbridge;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * NOTE FROM AMIT: This class was modified from com.android.volley.toolbox.RequestFuture.
 * I modified it so that RequestFuture.get() does not throw any exceptions, rather it returns a
 * ReturnValue object which encapsulates both a response and exception object.
 *
 * The issue is with Volley's RequestFuture class, is that its get() method throws exceptions
 * while the caller may want to catch the exceptions just to return (not throw) the exception again.
 * Thus, since callers want to return exceptions rather than throw them, its more efficient for
 * the RequestFuture class to return them rather than throw them (b/c then the caller has to do an extra
 * step of catching exceptions)
 *
 * This class is used by AsyncDefaultApi's async-versions of DefaultApi's methods
 * (Internally AsyncDefaultApi calls DefaultApi's methods)
 */

/**
 * A Future that represents a Volley request.
 *
 * Used by providing as your response and error listeners. For example:
 * <pre>
 * RequestFuture&lt;JSONObject&gt; future = RequestFuture.newFuture();
 * MyRequest request = new MyRequest(URL, future, future);
 *
 * // If you want to be able to cancel the request:
 * future.setRequest(requestQueue.add(request));
 *
 * // Otherwise:
 * requestQueue.add(request);
 *
 * try {
 *   JSONObject response = future.get();
 *   // do something with response
 * } catch (InterruptedException e) {
 *   // handle the error
 * } catch (ExecutionException e) {
 *   // handle the error
 * }
 * </pre>
 *
 * @param <T> The type of parsed response this future expects.
 */
public class ModedRequestFuture<T> implements /*Future<T>,*/ Response.Listener<T>,
        Response.ErrorListener {
    private Request<?> mRequest;
    private boolean mResultReceived = false;
    private T mResult;
    private VolleyError mException;

    public static <E> ModedRequestFuture<E> newFuture() {
        return new ModedRequestFuture<E>();
    }

    private ModedRequestFuture() {}

    public void setRequest(Request<?> request) {
        mRequest = request;
    }

    //@Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (mRequest == null) {
            return false;
        }

        if (!isDone()) {
            mRequest.cancel();
            return true;
        } else {
            return false;
        }
    }

    //@Override
    public ReturnValue<T> get() throws InterruptedException{
        return doGet(null);
    }

    //@Override
    public ReturnValue<T> get(long timeout, TimeUnit unit)
            throws InterruptedException {
        return doGet(TimeUnit.MILLISECONDS.convert(timeout, unit));
    }
    /*TODO: just create one instance of the to-return ReturnValue at the top of the method, and change all the if statements to else-ifs so then I can just
     go about changing the to-return ReturnValue object based on if some condition is true, this would make the code look a bit less repetitive b/c now
     I am not creating a new ReturnValue object in every condition.  I am just setting the properties of the one to-return object which will be returned at the end */
    private synchronized ReturnValue<T> doGet(Long timeoutMs)
            throws InterruptedException {
        ReturnValue<T> returnAns = new ReturnValue<>(null,null);
        if (mException != null) {
            return new ReturnValue<T>(null,new ExecutionException(mException));
            //throw new ExecutionException(mException);
        }

        if (mResultReceived) {
            return new ReturnValue<T>(mResult,null);
            //return mResult;
        }

        if (timeoutMs == null) {
            wait(0);
        } else if (timeoutMs > 0) {
            wait(timeoutMs);
        }

        if (mException != null) {
            return new ReturnValue<T>(null,new ExecutionException(mException));
            //throw new ExecutionException(mException);
        }

        if (!mResultReceived) {
            return new ReturnValue<T>(null,new TimeoutException());
            //throw new TimeoutException();
        }

        return new ReturnValue<T>(mResult,null);
    }

    //@Override
    public boolean isCancelled() {
        if (mRequest == null) {
            return false;
        }
        return mRequest.isCanceled();
    }

    //@Override
    public synchronized boolean isDone() {
        return mResultReceived || mException != null || isCancelled();
    }

    @Override
    public synchronized void onResponse(T response) {
        mResultReceived = true;
        mResult = response;
        notifyAll();
    }

    @Override
    public synchronized void onErrorResponse(VolleyError error) {
        mException = error;
        notifyAll();
    }

    public ReturnValue getNulledReturnValue(){
        return new ReturnValue(null,null);
    }

    static public class ReturnValue <G> {

        private G result;
        private Exception exc;

        public ReturnValue(G result, Exception exc) {
            this.result = result;
            this.exc = exc;
        }
        public ReturnValue setResult(G result) {
            this.result = result;
            return this;
        }
        public ReturnValue setException(Exception exc) {
            this.exc = exc;
            return this;
        }
        public G getResult() {
            return result;
        }
        public Exception getException() {
            return exc;
        }
    }

}

