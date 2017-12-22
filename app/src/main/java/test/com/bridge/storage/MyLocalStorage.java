
package test.com.bridge.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;

import test.com.bridge.utils.HelpMes;
import test.com.bridge.utils.Log;

/**
 * Created by Amit on 7/17/2017.
 */

/**
 * This class is used to store encrypted key-value pairs and plain-text key value pairs to persistent memory.
 * For the encrypted pairs, the values are encrypted using AES, and the keys are stored using an android implemented KeyStore.
 * Thus I believe that encrypted pairs are fairly safe security wise.
 *
 * Note: encrypted key-value pairs and decrypted key-value pairs are stored in separate files,
 * thus you can have encrypted key value pairs and plain key value pairs that have the same name but map to different values.
 * However, all encrypted pairs must have unique keys and all plain pairs must have unique keys (obviously)
 *
 */
public class MyLocalStorage {
    private static String CLASSNAME = MyLocalStorage.class.getSimpleName();

    static private final String SP_PLAIN_FILENAME = "_plain_sharedpref_filename";
    static private final String SP_CRYPT_FILENAME = "_crypt_sharedpref_filename";
    static private final String KS_FILENAME = "_keystore_filename_";  // the keystore's file's relative path

    //TODO NOTE: is it ok to hold a shared preference object initialized on a file for an extending period of time (the creation of this object tills it is deallocated, which will be once the program ends)
    //Answer: I think it is ok, because the documentation says that only one instance of SharedPreference is created for every file anyways.
    //        And its the SharedPreference's editor object that acquires the resources to write to a file
    private SharedPreferences plainPref; //This sp will be used to store regular(meaningful-text) keyvalue pairs
    private SharedPreferences cryptPref;// This sp will be used to store keyvalue pairs where the value is encrypted

    private String ks_path;       // the keystore's file's absolute path

    private static MyLocalStorage instance;
    private MyLocalStorage(Context c){
        Context app = c.getApplicationContext();
        File rootAppFile = app.getFilesDir();
        String rootAppPath = rootAppFile.getAbsolutePath(); //getting the root file path of this application's private files

        ks_path = rootAppPath + "/" + KS_FILENAME;  //the keystore file name needs to be an absolute filename
        plainPref = app.getSharedPreferences(SP_PLAIN_FILENAME,Context.MODE_PRIVATE);
        cryptPref = app.getSharedPreferences(SP_CRYPT_FILENAME,Context.MODE_PRIVATE);
        Log.d2(CLASSNAME,"rootAppPath: " + rootAppPath);

    }

    /**
     * @param fileName a absolute file name
     * @return if a file of 'filename' exists in the device's file system
     */
    private boolean fileExists(String fileName){
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * Initializes the passed in keystore with the contents of the specified keystore file (filename parameter)
     * @param keyStore the keystore's whose data is to be initialized by the specified keystore file
     * @param protection the char array that 'filename' was protected with
     * @param filename The name of the file that contains the contents of a keystore
     */
    private void createKeyStoreFile(KeyStore keyStore, char[] protection, String filename){
        try{
            FileOutputStream fout = new FileOutputStream(filename);
            keyStore.store(fout,protection);
        }catch(Exception e){
            e.printStackTrace();
            throw new IllegalStateException("Excepction in createKeyStoreFile().  Check stacktrace");
        }
    }

    /**
     * Returns the singleton MyLocalStorage instance, creating it if it does not currently exist.
     * @return the singleton MyLocalStorage instance
     */
    public static MyLocalStorage getInstance(){
        if(instance == null){
            instance = new MyLocalStorage(HelpMes.getApplicationContext());
        }
        return instance;
    }

    /**
     * Stores a key with an unencrypted value
     * @param key The key associated with the value
     * @param value The value to store
     */
    public void storeKeyValue(String key, String value){
        s_storeKeyValue(key,value,plainPref);
    }

    /**
     * Retrieves the value associated with this key.
     * @param key The key
     * @return The value associated with this key, or null if there is no value associated with this key
     */
    public String getValue(String key){
        return s_getValue(key,plainPref);
    }
    private void s_storeKeyValue(String key, String value, SharedPreferences sp){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,value).apply(); //Use apply() instead of commit() b/c apply performs asynchronously (However I could call commit in a newly created background thread if I wanted, just to be able to retrieve some return status)
    }
    private String s_getValue(String key, SharedPreferences sp){
        return sp.getString(key,null);
    }
    private void s_removeKey(String key, SharedPreferences sp){
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key).apply();
    }
    static private final String KEYSTORE_PASS = "_password__";
    static private final String KEYS_PASS = "_keys_password__";


    static private final String CONFIDKEY_ALIAS = "confidkey";
    static private final String INTEGKEY_ALIAS = "integkey";

    /**
     * Stores a key with an encrypted value
     * @param key The key associated with the value (should not be null)
     * @param value The value to store (should not be null)
     */
    public void storeEncryptedKeyValue(String key, String value){
        Log.d2(CLASSNAME,"entering MyLocalStorage::storeEncryptedKeyValue()");
        //Getting encrypted text
        AesCbcWithIntegrity.SecretKeys s_keys = null;
        String encryptedText = null;
        try{
            s_keys = AesCbcWithIntegrity.generateKey();
            AesCbcWithIntegrity.CipherTextIvMac cipherTextWrapper = AesCbcWithIntegrity.encrypt(value,s_keys);
            encryptedText = cipherTextWrapper.toString();
        }catch(Exception e){
            e.printStackTrace();
            throw new IllegalStateException("Exception in storeEncryptedKeyValue().  Check stacktrace");
        }
        Log.d2(CLASSNAME,"decryptedText: " + value);
        Log.d2(CLASSNAME,"encryptedText: " + encryptedText);
        //Storing encrypted text
        s_storeKeyValue(key,encryptedText,cryptPref);

        //Now we use the android built in KeyStore object to store the keys of the aes cypher used to encrypt
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            //KeyStore keyStore = KeyStore.getInstance("JCEKS");
            char [] ks_protection = KEYSTORE_PASS.toCharArray();
            KeyStore.ProtectionParameter key_protection = new KeyStore.PasswordProtection((key+KEYS_PASS).toCharArray());
            if(!fileExists(ks_path)){
                ks.load(null,null); //initializing this keystore object with an keystore object stored in disk
                createKeyStoreFile(ks,ks_protection,ks_path);
            }
            FileInputStream fin = new FileInputStream(ks_path);
            ks.load(fin,ks_protection);
            fin.close();

            javax.crypto.SecretKey confidKey = s_keys.getConfidentialityKey();
            javax.crypto.SecretKey integKey = s_keys.getIntegrityKey();

            KeyStore.SecretKeyEntry entry_confid_key = new KeyStore.SecretKeyEntry(confidKey);
            ks.setEntry(key+CONFIDKEY_ALIAS,entry_confid_key,key_protection);

            KeyStore.SecretKeyEntry entry_integ_key = new KeyStore.SecretKeyEntry(integKey);
            ks.setEntry(key+INTEGKEY_ALIAS,entry_integ_key,key_protection);

            FileOutputStream fout = new FileOutputStream(ks_path);
            ks.store(fout,ks_protection);

            fout.close();
        }
        catch(Exception e){
            e.printStackTrace();
            throw new IllegalStateException("Exception in storeEncryptedKeyValue().  Check stacktrace");
        }
        Log.d2(CLASSNAME,"exiting hijoo storeEncryptedKeyValue()");
    }

    /**
     * Retrieves and decrypts the encrypted value associated with this key
     * @param key The key
     * @return The decrypted value associated with this key, or null if there is no value associated with this key
     */
    public String getEncryptedValue(String key){
        Log.d2(CLASSNAME,"entering getEncryptedValue()");

        //Getting encrypted text
        String encryptedText = s_getValue(key,cryptPref);
        if(encryptedText==null){
            //In this case, no encrypted text was found for this key, therefore the client never stored an encrypted value for this key
            return null;
        }
        Log.d2(CLASSNAME,"get stored encrypted value: " + encryptedText);
        try{
            char [] ks_protection = KEYSTORE_PASS.toCharArray();
            KeyStore.ProtectionParameter key_protection = new KeyStore.PasswordProtection((key+KEYS_PASS).toCharArray());
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            //KeyStore keyStore = KeyStore.getInstance("JCEKS");
            FileInputStream fin = new FileInputStream(ks_path);
            keyStore.load(fin,ks_protection);
            fin.close();
            Log.d2(CLASSNAME,"number of enteries in loaded keystore: " + keyStore.size());

            KeyStore.SecretKeyEntry skEntryC = (KeyStore.SecretKeyEntry)keyStore.getEntry(key+CONFIDKEY_ALIAS,key_protection);
            javax.crypto.SecretKey confidKey = skEntryC.getSecretKey();

            KeyStore.SecretKeyEntry skEntryI = (KeyStore.SecretKeyEntry)keyStore.getEntry(key+INTEGKEY_ALIAS,key_protection);
            javax.crypto.SecretKey integKey = skEntryI.getSecretKey();

            AesCbcWithIntegrity.SecretKeys secretKeys = new AesCbcWithIntegrity.SecretKeys(confidKey,integKey);
            AesCbcWithIntegrity.CipherTextIvMac cipherTextWrapper = new AesCbcWithIntegrity.CipherTextIvMac(encryptedText);

            String decryptedText = AesCbcWithIntegrity.decryptString(cipherTextWrapper,secretKeys);

            Log.d2(CLASSNAME,"exiting getEncryptedValue()");
            return decryptedText;
        }
        catch (Exception e){
            e.printStackTrace();
            throw new IllegalStateException("Excepction in getEncryptedValue().  Check stacktrace");
        }
    }

    /**
     * same as storeEncryptedKeyValue(String,String) but one argument takes a resource id
     * @param resID An integer resource string id representing the key you want to associate with this value
     * @param value The value to store (should not be null)
     */
    public void storeEncryptedKeyValue(int resID, String value){
        storeEncryptedKeyValue(HelpMes.getStr(resID),value);
    }

    /**
     * same as getEncryptedValue(String) but one argument takes a resource id
     * @param resID An integer resource string id representing the key whose value you want to query for
     * @return same as getEncryptedValue(String)
     */
    public String getEncryptedValue(int resID){
        return getEncryptedValue(HelpMes.getStr(resID));
    }

    /**
     * Remove the specified encrypted key-value pair. Nothing happens if there is no pair with the specified key
     * @param key The key specifying the pair to remove. (Should be non-null)
     */
    public void removeEncryptedKey(String key){
        s_removeKey(key,cryptPref);
    }

    /**
     * Same as removeEncryptedKey(String) but argument takes a resource id
     * @param resourceID An integer id of a resource string specifying the key of the pair to remove
     */
    public void removeEncryptedKey(int resourceID){
        removeEncryptedKey(HelpMes.getStr(resourceID));
    }

    /**
     * Same as removeEncryptedKey(String) but this is done for a plain key-value pair
     * @param key The key specifying the pair to remove. (Should be non-null)
     */
    public void removeKey(String key){
        s_removeKey(key,plainPref);
    }

    /**
     * Same as removeEncryptedKey(int) but this is done for a plain key-value pair
     * @param resourceID An integer id of a resource string specifying the key of the pair to remove
     */
    public void removeKey(int resourceID){
        removeKey(HelpMes.getStr(resourceID));
    }

    /**
     * Same as storeKeyValue(String,String) but one argument takes an integer resource id
     * @param resID integer id of a resource string that is the key of the pair you want to store
     * @param value The value of the pair you want to store
     */
    public void storeKeyValue(int resID, String value){
        storeKeyValue(HelpMes.getStr(resID),value);
    }

    /**
     * Same as getValue(String) but argument takes an integer resource id
     * @param resID integer id of a resource string representing the key
     * @return same as getValue(String)
     */
    public String getValue(int resID){
        return getValue(HelpMes.getStr(resID));
    }





}
