package com.example.jamaal.imtocal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class ShowProgressActivity extends AppCompatActivity {
    String LOG_TAG = ShowProgressActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG,"onCreate");
        setContentView(R.layout.activity_show_progress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ShowProgressActivityFragment extends Fragment {
        String LOG_TAG = ShowProgressActivityFragment.class.getSimpleName();
        ListView listView_showPA;
        Context context;


        public ShowProgressActivityFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.d(LOG_TAG,"About to create view!");
            context = this.getActivity();
            View view_showProgress = inflater.inflate(R.layout.fragment_show_progress, container, false);
            listView_showPA = (ListView) view_showProgress.findViewById(R.id.listView_showProgress);
            showProgressItems();
            return view_showProgress;
        }

        private void showProgressItems() {
            final Context context1 = this.getActivity();
            Log.d(LOG_TAG,"about to show progress itesm!");
            Cursor cursor_result = null;
            try {
                cursor_result = new DatabaseHelper.DbTaskViewAll(this.getActivity()).execute("").get();
                if(cursor_result.getCount() == 0){
                    Toast.makeText(this.getActivity(),"No data found", Toast.LENGTH_SHORT).show();
                    return ;
                }
                //String mainFolderPath =  Environment.getExternalStorageDirectory()+ "/ImToCal";
                String mainFolderPath = GlobalClass.getInstance().getMainFolderPath();
                int cursorRows = cursor_result.getCount();
                final String[] projNameArr = new String[cursorRows];
                final String[] dispNameArr = new String[cursorRows];
                String[] imagePathArr = new String[cursorRows];
                final String[] statusArr = new String[cursorRows];
                //String[] opImgName = new String[cursorRows];
                //String[] opLineArr = new String[cursorRows];
                int cnt = 0;

                while(cursor_result.moveToNext()){

                    String projName = cursor_result.getString(1);
                    String status = cursor_result.getString(2);
                    String imName = cursor_result.getString(3);
                    String dispName = cursor_result.getString(7);
                    /*if(status.equals("done")){
                        opImgName[cnt] = cursor_result.getString(5);
                        opLineArr[cnt] = cursor_result.getString(6);
                    }*/
                    projNameArr[cnt] = projName;
                    dispNameArr[cnt] = dispName;
                    statusArr[cnt] = status;
                    imagePathArr[cnt] = mainFolderPath+"/"+projName+"/"+imName.replace("IMG","THUMB");
                    cnt++;
                }
                CustomListAdapter customListAdapter_showPA = new CustomListAdapter(this.getActivity(),projNameArr,imagePathArr,statusArr,dispNameArr);
                listView_showPA.setAdapter(customListAdapter_showPA);
                listView_showPA.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        String selectedItem = projNameArr[+position];
                        String status = statusArr[+position];

                        if(status.equals("started")) {
                            Toast.makeText(context, selectedItem +" is processing! Please wait!!", Toast.LENGTH_SHORT).show();
                        }else if(status.equals("Done")){
                            Intent intent_resultspage = new Intent(context1,ResultPageActivity.class);
                            intent_resultspage.putExtra("PROJ_DIR",selectedItem);
                            startActivity(intent_resultspage);
                        }
                    }
                });
                cursor_result.close();
                return ;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    /**
     * Created by Chaitanya on 8/3/2016.
     */
    public static class CustomListAdapter extends ArrayAdapter<String> {

        private final Activity context;
        private final String[] projNameArr;
        private final String[] dispNameArr;
        private final String[] imagePathArr;
        private final String[] statusArr;

        public CustomListAdapter(Activity context, String[] projNameArr, String[] imagePathArr, String[] statusArr, String[] dispNameArr) {
            super(context, R.layout.list_item_showpa, projNameArr);
            // TODO Auto-generated constructor stub

            this.context = context;
            this.projNameArr = projNameArr;
            this.dispNameArr = dispNameArr;
            this.imagePathArr = imagePathArr;
            this.statusArr = statusArr;
        }

        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.list_item_showpa, null, true);

            TextView txtTitle = (TextView) rowView.findViewById(R.id.textView_showpa);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView_showpa);
            TextView extratxt = (TextView) rowView.findViewById(R.id.textView_showpa_1);

            txtTitle.setText(dispNameArr[position]);
            //txtTitle.setText(projNameArr[position]);
            File imgFile = new File(imagePathArr[position]);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                Bitmap bitmap_re = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                imageView.setImageBitmap(bitmap_re);
            }
            //imageView.setImageResource(imagePathArr[position]);
            extratxt.setText("Status: " + statusArr[position]);
            return rowView;

        }



    }
}
