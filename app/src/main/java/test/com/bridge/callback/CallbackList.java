package test.com.bridge.callback;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Amit on 8/9/2017.
 */

/**
 * This class represents a generic list of EventCallback elements.
 * It is useful when you want a class to hold a List of registered callbacks
 * @param <T> A class that implements one of the interfaces that extends EventCallback
 * @param <E> A class that <T> can receive as a generic type.
 *           NOTE: Technically I shouldn't have to specify E as a generic type.  But I can't think of any way of extracting a class's generic type and storing that in another generic type
 *           (I would be trying to extract the 'E' type from 'T'.  Because any T type has to have an E type.  The issue is I don't know how to do this extraction,
 *           so the user just has to redundantly specify the E type in instance creation). see @commentA at bottom
 */
public class CallbackList <T extends EventCallback<E>,E> {

    private static String CLASSNAME = CallbackList.class.getSimpleName();

    private List<T> callbackList;


    /**
     * Creates and returns a new CallbackList
     */
    public CallbackList(){
        callbackList = new LinkedList<T>();
    }

    /**
     * Add another <T extends EventCallback<E>> element to the list.
     * @param e element to add
     */
    public synchronized void add(T e){
        if(e==null){
            return;
        }
        callbackList.add(e);
    }

    /**
     * Remove a <T extends EventCallback<E> element from the list if it currently exists in the list
     * @param e The callback to remove
     */
    public synchronized void remove(T e){
        callbackList.remove(e);
    }

    /**
     * Call the onEvent() methods of all the <T extends EventCallback<E>> objects in the list
     * @param e The event object that will be passed to all the callbacks onEvent() method
     */
    public synchronized void triggerEvents(E e){
        Iterator<T> iterator = callbackList.listIterator();
        while(iterator.hasNext()){
            iterator.next().onEvent(e);
        }
    }
}

/*
@commentA:
Basically, I want to declare this class like this

public class CallbackList <T extends EventCallback<E>> {

}

Now I only have to initialize instances with one generic type rather than two
Java could use some pattern matching technique to figure out what type E was. But Java does not
 understand what the f I'm doing, so the above code does not compile.


*/