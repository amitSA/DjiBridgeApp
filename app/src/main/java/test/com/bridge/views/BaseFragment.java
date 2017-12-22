package test.com.bridge.views;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by Amit on 8/15/2017.
 */

/**
 * All Fragments in this application should inherit from BaseFragment.  It defines some methods
 * and fields that base class implementations can use for convenience.
 *
 * Also it defines the OnFragmentCreated interface
 *
 * Note: BaseFragment inherits from Fragment instead of support library Fragment b/c we are targeting devices running Android 3.0 or higher
 */
public class BaseFragment extends Fragment {
    protected final String CLASS = this.getClass().getSimpleName();
    protected Handler UIHandler;

    //I think its fine to change this to protected if needed in base class implementations
    private OnFragmentCreated fragmentCreatedListener;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Initializing UIHandler
        UIHandler = new Handler(Looper.getMainLooper());

    }

    /**
     * @pre this class's onViewStateRestored() method should be called by subclass’s after their implementation of onViewStateRestored().  Look at DisplayFragment for example
     * In this method we use the fragmentCreatedListener to alert that the Fragment has been fully created
     * and its view's have been initialized
     * @param savedInstanceState
     */
    @Override
    public void onViewStateRestored(Bundle savedInstanceState){
        super.onViewStateRestored(savedInstanceState);

        //At this point in code, the fragment should be fully initialized
        if(fragmentCreatedListener!=null){
            fragmentCreatedListener.onFragmentCreated(this);  //now this implementation of OnFragmentCreated will be called to indicate that this Fragment has finished creation
        }
    }

    /**
     * Sets the OnFragmentCreated listener object of this fragment.
     * Once the Fragment has been fully created (i.e. views have been initialized), the callback's onFragmentCreated() method will be called with
     *  this Fragment as the argument
     *
     * @param listener
     */
    public void setFragmentCreatedListener(OnFragmentCreated listener){
        fragmentCreatedListener = listener;
    }

    /**
     * Interface used by BaseFragments to notify when a fragment has been fully created (i.e. once it's view's have been fully initialized )
     */
    interface OnFragmentCreated{
        public void onFragmentCreated(Fragment fragment);
    }
}
