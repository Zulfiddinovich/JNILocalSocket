package etsoft.localsocket.javas_javac;


import static etsoft.localsocket.MainActivity.JavaS_JavaC_Address;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import etsoft.localsocket.MessageListener;

/**
 * Author: Zukhriddin Kamolov
 * Date: 06-Apr-24, 8:13 AM.
 * Project: JNILocalSocket
 */
public class JavaServer extends Thread {
    private final static String TAG = "JavaServer";
    public boolean isRunning = false;
    private MessageListener mListener;
    private LocalServerSocket mServer;
    private LocalSocket mClient;

    public JavaServer() { /* */ }

    /**
     * It starts server foe LocalSocket for Java/C++
     * */
    @Override
    public void run() {
        Log.i(TAG, "Server socket run . . . start");
        try {
            mServer = new LocalServerSocket(JavaS_JavaC_Address);
            mClient = mServer.accept();
            while (isRunning) {
                sleep(1000);
                sendMessageToClient("hello");
            }
            Log.d(TAG, "run: server closed");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (mServer != null) {
                try {
                    mServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
    * This closes in next loop
    * */
    public void close(){
        try {
            isRunning = false;
            mServer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This sends a message to JavaClient using LocalSocket
     * */
    public void sendMessageToClient(String message){
        if (mClient != null) {
            try {
                Log.i(TAG, "sendMessageToClient, " + message);
                PrintWriter os = new PrintWriter(mClient.getOutputStream());
                os.println(message);
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This listener sends coming messages to MainActivity from JavaServer (LocalSocket) to show
     * */
    public void setListener(MessageListener listener){
        mListener = listener;
    }
}
