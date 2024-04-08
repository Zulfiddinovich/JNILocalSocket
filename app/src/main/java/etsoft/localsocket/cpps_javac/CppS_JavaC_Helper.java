package etsoft.localsocket.cpps_javac;

import static etsoft.localsocket.MainActivity.CppS_JavaC_Address;

import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import etsoft.localsocket.MessageListener;

/**
 * Author: Zukhriddin Kamolov
 * Date: 06-Apr-24, 8:13 AM.
 * Project: JNILocalSocket
 */

public class CppS_JavaC_Helper extends Thread {
    private static final String TAG = "CppS_JavaC_Helper";
    private Context mContext;
    private LocalSocket client = null;
    private int timeout = 30000;

    private MessageListener mListener;
    private BufferedReader is = null;
    public boolean isListening = false;

    public CppS_JavaC_Helper() {
        client = new LocalSocket();
    }

    @Override
    public void run() {
        while(client.isConnected() && isListening){
            try {
                is = new BufferedReader(new InputStreamReader(client.getInputStream()));
                Thread.sleep(500);
                String result = is.readLine();
                Log.d(TAG,"get DATA" + result);
                mListener.invoke(result);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,e.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This connects socket to server client
     * */
    public boolean connect(Context context) {
        mContext = context;
        if (!isListening) {
            Runnable runnable = () -> {
                try {
                    Log.d(TAG, "intoConnect");
                    sleep(300);
                    LocalSocketAddress address = new LocalSocketAddress(CppS_JavaC_Address, LocalSocketAddress.Namespace.ABSTRACT);
                    client.connect(address);
                    client.setSoTimeout(timeout);
                    isListening = client.isConnected();
                    Log.d(TAG, "is Connect?" + client.isConnected());
                    start();
                } catch (IOException e) {
                    Log.e(TAG, "Connect Failure" + e.getMessage());
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            };
            runnable.run();
        }
        return isListening;
    }

    /**
     * This listener sends coming messages to MainActivity from JavaClient (LocalSocket) to show
     * */
    public void setListener(MessageListener listener){
        mListener = listener;
    }


    /**
     * This closes Java socket client in next loop
     * */
    public void close(){
        isListening = false;
        stopServer();
    }

    /**
     * This starts C++ server socket
     * */
    public native void startServer();

    /**
     * This stops C++ server socket
     * */
    public native void stopServer();

}

