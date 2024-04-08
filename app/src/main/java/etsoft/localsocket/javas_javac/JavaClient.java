package etsoft.localsocket.javas_javac;

import static etsoft.localsocket.MainActivity.JavaS_JavaC_Address;

import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import etsoft.localsocket.MessageListener;

/**
 * Author: Zukhriddin Kamolov
 * Date: 06-Apr-24, 8:13 AM.
 * Project: JNILocalSocket
 */
public class JavaClient extends Thread {
    private static final String TAG = "JavaClient";
    private Context mContext;
    private LocalSocket mClient;
    private int timeout = 30000;
    private BufferedReader is = null;
    private MessageListener mListener;

    public boolean isListening;

    public JavaClient() {
        mClient = new LocalSocket();
        isListening = false;
    }

    @Override
    public void run() {
        try {
            // client can listens a long time
            while (mClient.isConnected() && isListening) {
                is = new BufferedReader(new InputStreamReader(mClient.getInputStream()));
                String result = is.readLine();
                Log.d(TAG, "get message " + result);
                mListener.invoke(result);
            }
            is.close();
            mClient.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    /**
    * This connects client to server socket
    * */
    public boolean connect(Context context) {
        mContext = context;
        if (!isListening) {
            if (!mClient.isConnected()) {
                Runnable runnable = () -> {
                    try {
                        Log.d(TAG, "into Connect");
                        sleep(300);  // wait until server starts
                        mClient.connect(new LocalSocketAddress(JavaS_JavaC_Address, LocalSocketAddress.Namespace.ABSTRACT));
                        mClient.setSoTimeout(timeout);
                        Log.d(TAG, "is Connect?" + mClient.isConnected());
                        isListening = true;

//                        // initial message, can be listened only if server is reading
//                        PrintWriter os = new PrintWriter(mClient.getOutputStream());
//                        os.write("connected");
//                        os.flush();

                        start(); // starts listening a message
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Connect Failure" + e.getMessage());
                    }
                };
                runnable.run();
            } else Toast.makeText(this.mContext, "This thread consumed", Toast.LENGTH_SHORT).show();
        }
        return isListening;
    }

    /**
     * This sends a message to JavaServer using LocalSocket
     * */
    public void sendMessageToServer(String message) {
        try{
            Log.i(TAG, "sendMessageToServer, " + message);
            PrintWriter os = new PrintWriter(mClient.getOutputStream());
            os.write(message);
            os.flush();

            OutputStream a = mClient.getOutputStream();
            a.write("acs".getBytes());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This listener sends coming messages to MainActivity from JavaClient (LocalSocket) to show
     * */
    public void setListener(MessageListener listener){
        mListener = listener;
    }

    /**
    * Forces to close in next loop
    * */
    public void close(){
        isListening = false;
    }

}
