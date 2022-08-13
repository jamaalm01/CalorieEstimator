package com.example.chaitanya.imtocal;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Chaitanya on 8/2/2016.
 */
public class DebugActivity extends AppCompatActivity {
    String LOG_TAG = DebugActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DebugActivityFragment())
                    .commit();;
        }*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }


    public static class DebugActivityFragment extends Fragment {
        String LOG_TAG = DebugActivityFragment.class.getSimpleName();
        Button btnViewAll,btnClearAll,btnSocConn;
        //TextView showDBContents_textView ;
        ListView showDBContents_listView;
        ArrayAdapter<String> debugAdapter;
        Context context;

        public DebugActivityFragment() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            context = this.getActivity();
            Log.d(LOG_TAG,"About to create view");
            View debugActivityView =  inflater.inflate(R.layout.fragment_debug_activity, container, false);
            btnViewAll = (Button) debugActivityView.findViewById(R.id.button_showDBContents);
            btnClearAll = (Button) debugActivityView.findViewById(R.id.button_clearDBcontents);
            btnSocConn = (Button) debugActivityView.findViewById(R.id.button_socConn);
            showDBContents_listView = (ListView) debugActivityView.findViewById(R.id.listView_showData);

            viewDBContents();
            clearDBandProjContents();
            regSocConn();
            return debugActivityView;
        }
        private void regSocConn(){
            btnSocConn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //btnSocConn.setBackgroundColor(Color.BLACK);
                    boolean isSocConn = GlobalClass.getInstance().isSocConn();
                    if(isSocConn){
                        btnSocConn.setTextColor(Color.GREEN);

                    }else {
                        btnSocConn.setTextColor(Color.RED);
                        Toast.makeText(context,"Socket Disconnected ! will try to connect!",Toast.LENGTH_LONG).show();
                        GlobalClass.getInstance().socketClient.start_thread();
                    }


                }
            });
        }
        private void clearDBandProjContents() {
            btnClearAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Cursor cursor_result = new DatabaseHelper.DbTaskViewAll(context).execute("").get();
                        if(cursor_result.getCount() == 0){
                            debugAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_showdb,R.id.list_item_showdb_textview,new ArrayList<String>());
                            showDBContents_listView.setAdapter(debugAdapter);
                            Toast.makeText(context,"No data found", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String mainFolderPath =  Environment.getExternalStorageDirectory()+ "/ImToCal";
                        Log.d(LOG_TAG,"About to delete proj folders");
                        while(cursor_result.moveToNext()){
                            File projDir = new File(mainFolderPath+"/"+cursor_result.getString(1));
                            Log.d(LOG_TAG,"about to delete: "+projDir.getPath());
                            deleteRecursive(projDir);
                        }
                        Log.d(LOG_TAG,"About to clear database");
                        Integer val =  new DatabaseHelper.DbTaskDeleteAll(context).execute("").get();
                        Log.d(LOG_TAG,"after delete data, val: "+val);
                        cursor_result.close();
                        debugAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_showdb,R.id.list_item_showdb_textview,new ArrayList<String>());
                        showDBContents_listView.setAdapter(debugAdapter);
                        Toast.makeText(context,"Data cleared", Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public boolean deleteRecursive(File fileOrDirectory) {
            if (fileOrDirectory.isDirectory()) {
                for (File child : fileOrDirectory.listFiles()) {
                    if(!deleteRecursive(child))
                        Log.d(LOG_TAG,"Unable to delete: "+child.getPath());
                }
            }
            return fileOrDirectory.delete();
        }

        private void viewDBContents() {
            btnViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Cursor cursor_result = new DatabaseHelper.DbTaskViewAll(context).execute("").get();
                        if(cursor_result.getCount() == 0){
                            debugAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_showdb,R.id.list_item_showdb_textview,new ArrayList<String>());
                            showDBContents_listView.setAdapter(debugAdapter);
                            Toast.makeText(context,"No data found", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ArrayList<String> arrayList = new ArrayList<String>();
                        StringBuffer buffer;
                        while (cursor_result.moveToNext()){
                            buffer  = new StringBuffer();
                            buffer.append("Id : "+cursor_result.getString(0)+"\n");
                            buffer.append("ProjectID : "+cursor_result.getString(1)+"\n");
                            buffer.append("Status : "+cursor_result.getString(2)+"\n");
                            buffer.append("OrigImName : "+cursor_result.getString(3)+"\n");
                            arrayList.add(buffer.toString());
                        }

                        //Log.d(LOG_TAG,"Result :" +buffer.toString());
                        //showDBContents_textView.setText(buffer.toString(), TextView.BufferType.SPANNABLE);

                        debugAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_showdb,R.id.list_item_showdb_textview,arrayList);
                        showDBContents_listView.setAdapter(debugAdapter);
                        cursor_result.close();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }
            });

        }

    }
}
