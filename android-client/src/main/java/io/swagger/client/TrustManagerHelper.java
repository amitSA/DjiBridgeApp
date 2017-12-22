package io.swagger.client;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Justin Ku on 8/11/2017.
 */

public class TrustManagerHelper {
    static public SSLSocketFactory getCustomSSLSocketFactory(Context c) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, KeyManagementException {
        TrustManagerFactory tmf = null;
        Context appContext = c.getApplicationContext();
        tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());

        // initialises the TMF with the default trust store.
        tmf.init((KeyStore) null);

        // Get the default trust manager
        X509TrustManager defaultTm = null;
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                defaultTm = (X509TrustManager) tm;
                break;
            }
        }

        InputStream myKeys = appContext.getResources().openRawResource(R.raw.prog_dev_stage_certs);

        // Do the same with your trust store
        KeyStore myTrustStore = KeyStore.getInstance("BKS");


        myTrustStore.load(myKeys, "2819".toCharArray());

        myKeys.close();

        tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(myTrustStore);

        // Get the default trust manager
        X509TrustManager myTm = null;
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                myTm = (X509TrustManager) tm;
                break;
            }
        }

        // Wrap it in own class.
        final X509TrustManager finalDefaultTm = defaultTm;
        final X509TrustManager finalMyTm = myTm;
        X509TrustManager customTm = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                // If you're planning to use client-cert auth,
                // merge results from "defaultTm" and "myTm".
                return joinArrays(X509Certificate.class,finalDefaultTm.getAcceptedIssuers(), finalMyTm.getAcceptedIssuers());
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
                try {
                    finalMyTm.checkServerTrusted(chain, authType);
                } catch (CertificateException e) {
                    // This will throw another CertificateException if this fails too.
                    finalDefaultTm.checkServerTrusted(chain, authType);
                }
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
                // If you're planning to use client-cert auth,
                // do the same as checking the server.
                finalDefaultTm.checkClientTrusted(chain, authType);
            }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{customTm}, null);


        return sslContext.getSocketFactory();
    }

    private static <E> E[] joinArrays(Class<E> elementType, E [] ... arrays) {
        int totSize = 0;
        for (int i = 0; i < arrays.length; i++) {
            totSize += arrays[i].length;
        }
        E[] solution = (E[]) Array.newInstance(elementType, totSize);
        for (int all = 0, i = 0; i < arrays.length; i++) {
            for (int j = 0; j < arrays[i].length; j++, all++) {
                solution[all] = arrays[i][j];
            }
        }
        return solution;
    }
}

/*X509Certificate [] answer = new X509Certificate[array1.length+array2.length];
        for(int i = 0; i < array1.length; i++){
            answer[i] = array1[i];
        }
        for(int i = array1.length; i < answer.length; i++){
            answer[i] = array2[i-array1.length];
        }
        return answer;*
    }*/
