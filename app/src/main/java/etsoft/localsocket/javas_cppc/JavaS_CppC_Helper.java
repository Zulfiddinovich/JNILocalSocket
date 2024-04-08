package etsoft.localsocket.javas_cppc;

import static etsoft.localsocket.MainActivity.JavaS_CppC_Address;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import etsoft.localsocket.MessageListener;

/**
 * Author: Zukhriddin Kamolov
 * Date: 06-Apr-24, 8:13 AM.
 * Project: JNILocalSocket
 */

public class JavaS_CppC_Helper extends Thread {

    private static final String TAG = "JavaS_CppC_Helper";

    public boolean isRunning = false;
    private MessageListener mListener;

    public JavaS_CppC_Helper() {
    }

    /**
    * It starts server for LocalSocket for Java/C++
    * */
    @Override
    public void run() {
        Log.i(TAG, "Server socket run . . . start");
        LocalServerSocket server = null;
        try {
            server = new LocalServerSocket(JavaS_CppC_Address);
            while (isRunning) {
                LocalSocket receiver = server.accept();
                if (receiver != null) {
                    InputStream inputStream = receiver.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String inputLine = null;
                    StringBuilder sb = new StringBuilder();
                    while (((inputLine = bufferedReader.readLine()) != null)) {
                        sb.append(inputLine);
                    }
                    StringBuilder finalSb = sb;
                    Thread.sleep(500);
                    mListener.invoke(finalSb.toString());

                    bufferedReader.close();
                    inputStreamReader.close();
                    inputStream.close();

                    // close this connected client socket every time,  otherwise receiver doesn`t read every time (reads only once at the end)
                    receiver.close();
                    sb = null;
                }
            }
        } catch (IOException e) {
            Log.e(getClass().getName(), e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    Log.e(getClass().getName(), e.getMessage());
                }
            }
        }
        Log.i(TAG, "Server socket run . . . end");
    }

    /**
     * This closes messaging in next loop
     * */
    public void close(){
        stopMessagingFromCpp();
        isRunning = false;
    }

    /**
    * This sends a message to C++ using LocalSocket
    * */
    private void sendMessageToCpp(String message){
        try{
            Log.i(TAG, "writeSocket, " + message);
            LocalSocket sender = new LocalSocket();
            sender.connect(new LocalSocketAddress(JavaS_CppC_Address));
            sender.getOutputStream().write(message.getBytes());
            sender.getOutputStream().close();
            sender.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This listener sends coming messages to MainActivity from C++ (LocalSocket) to show
     * */
    public void setListener(MessageListener listener){
        mListener = listener;
    }

    /**
     * This starts periodical messaging from C++ client to JavaServer
     */
    public native void startMessagingFromCpp();

    /** This forces to end the periodical messaging (from C++ client to JavaServer) loop*/
    public native void stopMessagingFromCpp();
}
