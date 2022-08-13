package com.example.chaitanya.imtocal;

import android.app.Application;
import android.os.Environment;

import java.io.File;

/**
 * Created by Chaitanya on 8/8/2016.
 */
public class GlobalClass extends Application {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private String tempCamImPath = Environment.getExternalStorageDirectory()+ File.separator+"image.jpg";
    private String mainFolderPath = Environment.getExternalStorageDirectory()+ File.separator+"ImToCal";
    private String incomingFolderPath = mainFolderPath+File.separator+"Incoming Files";
    private boolean isSocConn = false;
    private boolean tcpFlag =false;
    private static GlobalClass singleton;
    public static GlobalClass getInstance(){
        return singleton;
    }

    public SocketClient socketClient;
    //public BackGroundThread backGroundThread;
    @Override
    public void onCreate() {
        super.onCreate();
        singleton =this;
        socketClient = new SocketClient();
        //backGroundThread = new BackGroundThread();
        //backGroundThread.runBackGroundThread();
    }



    public boolean isSocConn() {
        return isSocConn;
    }

    public void setSocConn(boolean socConn) {
        isSocConn = socConn;
    }

    public String getIncomingFolderPath() {
        return incomingFolderPath;
    }

    public String getMainFolderPath() {
        return mainFolderPath;
    }

    public String getTempCamImPath() {
        return tempCamImPath;
    }

}
