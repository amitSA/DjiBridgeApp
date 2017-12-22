package test.com.bridge.storage;

/**
 * Created by Amit on 8/8/2017.
 */

/**
 * This class is used to test out the features exported by MyLocalStorage
 */
public class UnitTests {
    private static String CLASSNAME = "storage-"+UnitTests.class.getSimpleName();
    /**
     * Debugging method to test out the features of the MyLocalStorage class.
     * Only used for debugging.
     * TODO: The unit tests can be improved and by just testing out more situtations
     */
    public static void testLocalStorage(){
        MyLocalStorage localStore = MyLocalStorage.getInstance();
        String key1 = "key1"; String val1 = "Rishi is CHANGED";  // key = "this is a key"
        String key2 = "key2"; String val2 = "Rishi hijo is a beep";  // key = "this is a key"
        //localStore.storeEncryptedKeyValue(key1,val1);
        //localStore.storeEncryptedKeyValue(key2,val2);
        String ret1 = localStore.getEncryptedValue(key1);
        String ret2 = localStore.getEncryptedValue(key2);
        test.com.bridge.utils.Log.d("-"+CLASSNAME,"key: " + key1 + "  value: " + toForm(ret1));
        test.com.bridge.utils.Log.d("-"+CLASSNAME,"key: " + key2 + "  value: " + toForm(ret2));

        String key5 = "key5"; String val5 = "Rishi is a v5 engine modified";  // key = "this is a key"
        String key6 = "key6"; String val6 = "Rishi is a v6 engine";  // key = "this is a key"
        localStore.storeKeyValue(key5,val5);
        localStore.storeKeyValue(key6,val6);
        String ret5 = localStore.getValue(key5);
        String ret6 = localStore.getValue(key6);
        test.com.bridge.utils.Log.d("-"+CLASSNAME,"key: " + key5 + "  value: " + toForm(ret5));
        test.com.bridge.utils.Log.d("-"+CLASSNAME,"key: " + key6 + "  value: " + toForm(ret6));
    }
    /**
     * Helper method used in testLocalStorage()
     */
    private static String toForm(String obj){
        return obj==null? "No value was found for this key" : obj;
    }

}
