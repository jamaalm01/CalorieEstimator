package com.example.jamaal.imtocal;

import android.util.Log;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.IOException;

/**
 * Created by Chaitanya on 8/23/2016.
 */
public class BackGroundThread {

    public final String LOG_TAG = BackGroundThread.class.getSimpleName();
    // A hardcoded path to a folder you are monitoring .
    public String FOLDER = GlobalClass.getInstance().getIncomingFolderPath();

    public void runBackGroundThread(){
        // The monitor will perform polling on the folder every 5 seconds
        final long pollingInterval = 5 * 1000;

        File folder = new File(FOLDER);

        if (!folder.exists()) {
            // Test to see if monitored folder exists
            throw new RuntimeException("Directory not found: " + FOLDER);
        }

        FileAlterationObserver observer = new FileAlterationObserver(folder);
        FileAlterationMonitor monitor = new FileAlterationMonitor(pollingInterval);
        FileAlterationListener listener = new FileAlterationListenerAdaptor() {
            // Is triggered when a file is created in the monitored folder
            @Override
            public void onFileCreate(File file) {
                try {
                    // "file" is the reference to the newly created file
                    Log.d(LOG_TAG,"File created: "
                            + file.getCanonicalPath());
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }

            // Is triggered when a file is deleted from the monitored folder
            @Override
            public void onFileDelete(File file) {
                try {
                    // "file" is the reference to the removed file
                    Log.d(LOG_TAG,"File removed: " + file.getCanonicalPath());
                    // "file" does not exists anymore in the location
                    Log.d(LOG_TAG,"File still exists in location: " + file.exists());
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        };

        observer.addListener(listener);
        monitor.addObserver(observer);
        try {
            monitor.start();
        }catch(Exception ex){
                ex.printStackTrace();
        }
    }
}