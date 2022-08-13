package com.example.chaitanya.imtocal;

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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class ResultPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);
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
    public static class ResultPageActivityFragment extends Fragment {

        public ResultPageActivityFragment() {
        }
        private final String LOG_TAG = ResultPageActivityFragment.class.getSimpleName();
        String projDir;
        private ArrayAdapter<String> listItemAdapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_result_page, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.textView_result_page);
            //ListView listView = (ListView) rootView.findViewById(R.id.listView_result_page);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView_result_page);
            projDir = this.getActivity().getIntent().getExtras().getString("PROJ_DIR");
            Log.d(LOG_TAG,"projDir :"+projDir);
            Cursor cursor_result = null;
            try {
                cursor_result = (Cursor) new DatabaseHelper.DbTaskViewProj(this.getActivity()).execute(projDir).get();

                int cursor_rows = cursor_result.getCount();
                if( cursor_rows == 0){
                    Toast.makeText(this.getActivity(),"No data found", Toast.LENGTH_SHORT).show();
                    return rootView;
                }
                cursor_result.moveToFirst();
                String projName = cursor_result.getString(1);
                String status = cursor_result.getString(2);
                String imName = cursor_result.getString(3);
                String inString = cursor_result.getString(4);
                String opImName = cursor_result.getString(5);
                String opString = cursor_result.getString(6);
                String dispName = cursor_result.getString(7);
                Toast.makeText(getContext(),dispName+ " is completed! ",Toast.LENGTH_LONG);
                Log.d(LOG_TAG,"op String: "+opString);
                String projDir = GlobalClass.getInstance().getMainFolderPath() + File.separator+projName;
                String opImPath = projDir+File.separator+opImName;
                String[] opStrArr = opString.split(",");
                ArrayList<String> itemArrList = new ArrayList<String>();
                String printString = projName;
                if(!dispName.equals(projName)){
                    printString = dispName+"("+projName+")";
                }
                //itemArrList.add(projName);
                if(opString.equals("NOTFOOD")) {
                    printString = printString+"\nNOTFOOD";
                    //itemArrList.add("NOTFOOD");
                }else{
                    int plno = Integer.parseInt(opStrArr[1]);
                    //label_no+","+class_label+","+calories
                    for (int i=0;i<plno;i++) {
                        int j=i+1;
                        printString= printString+"\nFood item: "+j+"\n Name: "+opStrArr[3*i+3]+"\n Calories: "+opStrArr[3*i+4];
                        //itemArrList.add("Food item: "+i+"\n Name: "+opStrArr[3*i+3]+"\n Calories: "+opStrArr[3*i+4]);
                    }
                    Bitmap bitmap = decodeSampledBitmapFromFile(opImPath,4096,4096);
                    imageView.setImageBitmap(bitmap);
                }
                textView.setText(printString);
                //itemArrList.add(printString);
                //listItemAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_resultpage,R.id.list_item_resultpage_textview,itemArrList);

                //listView.setAdapter(listItemAdapter);
                cursor_result.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return rootView;
        }
        public Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
        {
            //First decode with inJustDecodeBounds=true to check dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            // Calculate inSampleSize, Raw height and width of image
            int height = options.outHeight;
            int width = options.outWidth;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            int inSampleSize = 1;

            if (height > reqHeight)
            {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            }
            int expectedWidth = width / inSampleSize;

            if (expectedWidth > reqWidth)
            {
                //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
            options.inSampleSize = inSampleSize;
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path, options);
        }

    }
}
