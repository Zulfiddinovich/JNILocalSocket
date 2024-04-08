package etsoft.localsocket;
/**
 * Author: Zukhriddin Kamolov
 * Date: 05-Apr-24, 2:00 PM.
 * Project: JNILocalSocket
 */
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import etsoft.localsocket.cpps_javac.CppS_JavaC_Helper;
import etsoft.localsocket.databinding.ActivityMainBinding;
import etsoft.localsocket.javas_cppc.JavaS_CppC_Helper;
import etsoft.localsocket.javas_javac.JavaClient;
import etsoft.localsocket.javas_javac.JavaServer;
import etsoft.localsocket.ui.adapter.RVAdapter;
import etsoft.localsocket.uidata.SocketData;

public class MainActivity extends Activity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("jni_socket");
    }

    ActivityMainBinding binding;
    RVAdapter adapter;
    ArrayList<SocketData> list = new ArrayList<>();
    int messageCounter = 0;
    public static String JavaS_CppC_Address = "JavaS_CppC_Address";
    public static String CppS_JavaC_Address = "CppS_JavaC_Address";
    public static String JavaS_JavaC_Address = "JavaS_JavaC_Address";
    JavaS_CppC_Helper javaS_CppC_Helper;
    CppS_JavaC_Helper cppS_JavaC_Helper;
    JavaServer javaS_JavaC_SHelper;
    JavaClient javaS_JavaC_CHelper;
    ConnectionType type;
    MessageListener messageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initRecyclerView();
        clicks();
        messageListener = ( message -> {
            runOnUiThread(() -> {
                showMessage(message);
            });
        });


        makeDirectory("localSocket");
    }

    private void makeDirectory(String appDir) {
        File externalLocation = getFileStreamPath(appDir).getAbsoluteFile();
        Log.d("TAG", "makeDirectory complete2 : " + externalLocation);
    }


    private void showMessage(String message) {
        switch (type){
            case JavaS_JavaC:
                list.add(new SocketData(++messageCounter, "Java ->", "Java", message));
                break;

            case JavaS_CppC:
                list.add(new SocketData(++messageCounter, "Java ->", "C++", message));

                break;
            case CppS_JavaC:
                list.add(new SocketData(++messageCounter, "C++ ->", "Java", message));
                break;
        }
        submitList(list);
        setRvVisibility(!list.isEmpty());
    }

    private void initRecyclerView() {
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recyclerVW.setLayoutManager(lm);
        adapter = new RVAdapter();
        binding.recyclerVW.setAdapter(adapter);
    }

    private void clicks() {
        binding.startJavasJavac.setOnClickListener(view -> {
            closeAndSetType(ConnectionType.JavaS_JavaC);


            javaS_JavaC_SHelper = new JavaServer();
            javaS_JavaC_SHelper.isRunning = true;
            javaS_JavaC_SHelper.start();
//            javaS_JavaC_SHelper.setListener(messageListener);

            javaS_JavaC_CHelper = new JavaClient();
            javaS_JavaC_CHelper.setListener(messageListener);
            javaS_JavaC_CHelper.connect(this);
        });

//        binding.javasToJavacSend.setOnClickListener(view -> {
//            if (javaS_JavaC_SHelper != null) javaS_JavaC_SHelper.sendMessageToClient("hello");
//            else showToast("not Connected yet");
//        });

//        binding.javacToJavasSend.setOnClickListener(view -> {
//            if (javaS_JavaC_CHelper != null) javaS_JavaC_CHelper.sendMessageToServer("hello");
//            else showToast("not Connected yet");
//        });




        binding.startJavasCc.setOnClickListener(view -> {
            closeAndSetType(ConnectionType.JavaS_CppC);

            javaS_CppC_Helper = new JavaS_CppC_Helper();
            if(!javaS_CppC_Helper.isAlive()){
                javaS_CppC_Helper.isRunning = true;
                javaS_CppC_Helper.start();
                javaS_CppC_Helper.setListener(messageListener);
                javaS_CppC_Helper.startMessagingFromCpp();
            } else javaS_CppC_Helper.startMessagingFromCpp();
        });

        binding.csJavac.setOnClickListener(view -> {
            closeAndSetType(ConnectionType.CppS_JavaC);

            cppS_JavaC_Helper = new CppS_JavaC_Helper();
            cppS_JavaC_Helper.setListener(messageListener);
            cppS_JavaC_Helper.startServer();
            cppS_JavaC_Helper.connect(this);
        });

        binding.stop.setOnClickListener(view -> {
            switch (type){
                case JavaS_JavaC:
                    closeAndSetType(ConnectionType.JavaS_JavaC);
                    break;
                case JavaS_CppC:
                    closeAndSetType(ConnectionType.JavaS_CppC);
                    break;
                case CppS_JavaC:
                    closeAndSetType(ConnectionType.CppS_JavaC);
                    break;
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void closeAndSetType(ConnectionType connectionType) {
        // 1
        if (javaS_CppC_Helper != null) javaS_CppC_Helper.close();

        // 2
        if (cppS_JavaC_Helper != null) cppS_JavaC_Helper.close();

        // 3
        if (javaS_JavaC_SHelper != null) {
            javaS_JavaC_CHelper.close();
            javaS_JavaC_SHelper.close();
        }

        type = connectionType;
    }

    private void submitList(List<SocketData> list) {
        adapter.submitList(new ArrayList<>(list));
        binding.recyclerVW.smoothScrollToPosition(list.size()-1);
    }

    private void setRvVisibility(boolean bool) {
        if (bool) {
            binding.noDataTv.setVisibility(View.INVISIBLE);
            binding.recyclerVW.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerVW.setVisibility(View.INVISIBLE);
            binding.noDataTv.setVisibility(View.VISIBLE);
        }
    }

}
