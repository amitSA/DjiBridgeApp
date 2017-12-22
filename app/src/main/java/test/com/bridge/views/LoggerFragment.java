package test.com.bridge.views;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

import test.com.bridge.R;
import test.com.bridge.callback.EventCallback.PrettyLogger ;
import test.com.bridge.callback.AllEvents.PrettyString;
import test.com.bridge.utils.HelpMes;
import test.com.bridge.utils.Log;

/**
 * Created by Amit on 8/15/2017.
 */

/**
 * This class implements PrettyLogger, such that whenever a Log.pretty...() method is made, this class's
 * onEvent(PrettyString) method will be called.
 *
 * This class is also a Fragment of MainActivity.
 * For any methods with the tag "@Override" please look at the android documentation for how that method works.
 * I will typically just describe what my subclass implementation is doing
 */
public class LoggerFragment extends BaseFragment implements PrettyLogger{

    private ScrollView scrollContainer;
    private TextView display;


    /**
     * Inflates R.layout.logger_frag and returns that inflated view object.
     * That view object will become the root view of this Fragment
     * @param inflator
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
        Log.d5(CLASS,"onCreateView() called");
        View view  = inflator.inflate(R.layout.logger_frag,container,false);

        display = (TextView) view.findViewById(R.id.log_panel);
        scrollContainer = (ScrollView) view.findViewById(R.id.scrollview_container);
        //display.setLineSpacing(0,lineMultiplier); //not using this anymore, so commented out
        return view;
    }

    /**
     * Called by the system after onCreateView(), once any previous states have been reinitialized to this view.
     * In this method, I add all previous pretty log requests to the view.
     * @param savedInstanceState
     */
    @Override
    public void onViewStateRestored(Bundle savedInstanceState){

        //This method will call our LoggerFragment's onEvent() method consecutively with all previous logs.
        //And then it will add our LoggerFragment to Log's internal list of PrettyLoggers
        Log.addWithAllPreviousLogs(this);

        super.onViewStateRestored(savedInstanceState);
    }

    /**
     * Adds a List of PrettyStrings to the UI of this fragment
     * @param strings
     */
    private void addToTextView(List<PrettyString> strings){
        Iterator<PrettyString> iterator = strings.iterator();
        while(iterator.hasNext()){
            addToTextView(iterator.next());
        }
    }

    /**
     * Adds a PrettyString to the UI of this fragment.  Here, you can define how a PrettyString
     * will be represented graphically (i.e. Choose the colors for every tag a PrettyString can have)
     * @param pretty
     */
    private void addToTextView(PrettyString pretty){
        int type = pretty.getType();
        SpannableString styleString =  new SpannableString(pretty.getString() /*+ HelpMes.NEW_LINE*/);
        ForegroundColorSpan color = null;
        if(type==android.util.Log.WARN){
            color = new ForegroundColorSpan(Color.rgb(206, 130, 0)); //Orange color
        }
        else if (type==android.util.Log.DEBUG){
            color = new ForegroundColorSpan(Color.rgb(0, 22, 196)); //Blue color
        }
        else if (type==android.util.Log.ERROR){
            color = new ForegroundColorSpan(Color.rgb(196, 22, 0)); //Dark-ish red color
        }
        else if (type==android.util.Log.INFO){
            color = new ForegroundColorSpan(Color.rgb(0, 128, 0)); //Greenish color (Before this was Log type was black)
        }
        styleString.setSpan(color,0,styleString.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        display.append(styleString);
        //float oldMultiplier = display.getLineSpacingMultiplier();
        //display.setLineSpacing(10,LINE_MULTIPLIER);
        display.append(HelpMes.NEW_LINE+HelpMes.NEW_LINE);
        //display.setLineSpacing(0,oldMultiplier);
        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                //scrollContainer.smoothScrollTo(0,scrollContainer.getBottom());
                 scrollContainer.fullScroll(View.FOCUS_DOWN);
            }
        });

    }

    /**
     * The implemented method inherited from PrettyLogger.  Every time a new Log.pretty...call is made
     * after this fragment has had its view created
     * @param event
     */
    @Override
    public void onEvent(final PrettyString event) {
        if(Looper.myLooper()==Looper.getMainLooper()){ //If we are already running on the UI Thread, then don't post to UI Thread
            addToTextView(event);
        }else{
            UIHandler.post(new Runnable() { //else post the method to run on the UI Thread
                @Override
                public void run() {
                    addToTextView(event);
                }
            });
        }
    }

    /**
     * Called when this LoggerFragment's view has been destroyed. Here we do any tasks that involve
     * releasing resources that depended on the view
     */
    @Override
    public void onDestroyView(){
        super.onDestroyView();

        //Remove the LoggerFragment as one of Log's registered PrettyLogger callbacks (b/c this LoggerFragment view has been destroyed, if this instance is ever re-initialized for use again, then a new view will be created)
        Log.removePrettyLogger(this);
    }
}
