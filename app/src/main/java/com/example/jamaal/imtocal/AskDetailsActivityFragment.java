package com.example.jamaal.imtocal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

public class AskDetailsActivityFragment extends Fragment implements AskDetailsActivityDialogFragment.DialogDataListener {


    private final String LOG_TAG = AskDetailsActivityFragment.class.getSimpleName();
    ImageView imageView_selIm ;
    Button button_next;
    AskDetailsActivityDialogFragment dialogFragment;
    boolean reqTypeCam;
    //String tempCamImPath = Environment.getExternalStorageDirectory()+ File.separator+"image.jpg";
    Uri uri_galreqType;
    ByteArrayOutputStream bos_common = new ByteArrayOutputStream();
    ByteArrayOutputStream bos_thumb = new ByteArrayOutputStream();
    Bitmap bitmap_cam;
    Bitmap bitmap_gal_re;
    private static final int DATA_REQUEST_CODE = 1014;
    private SendDataListener listener;
    GlobalClass globalClass;
    public AskDetailsActivityFragment() {
        // Required empty public constructor
    }
    public void setSendDataListener(SendDataListener listener){      this.listener = listener;    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view_askDetails =  inflater.inflate(R.layout.fragment_ask_details, container, false);

        imageView_selIm = (ImageView) view_askDetails.findViewById(R.id.imageView_askDetails);
        button_next = (Button) view_askDetails.findViewById(R.id.button_next);
        globalClass = (GlobalClass) this.getActivity().getApplicationContext();
        setSendDataListener(globalClass.socketClient);
        Log.d(LOG_TAG,"on CreateView!! ");

        String reqType = this.getActivity().getIntent().getExtras().getString("REQUEST_TYPE");
        if(reqType.equals("CAM")){
            reqTypeCam = true;
            Log.d(LOG_TAG,"req type is cam ");

            File photoFile = new File(globalClass.getTempCamImPath());
            bitmap_cam = decodeSampledBitmapFromFile(photoFile.getAbsolutePath(),4096,4096);
            Bitmap bitmap_re = adjustBitmapSize(bitmap_cam,4096,4096);
            bitmap_cam = adjustBitmapSize(bitmap_cam,512,512);
            imageView_selIm.setImageBitmap(bitmap_re);
        }
        else{
            reqTypeCam = false;
            Log.d(LOG_TAG,"req type is gal ");
            uri_galreqType = Uri.parse(this.getActivity().getIntent().getExtras().getString("URI_VAL"));
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(uri_galreqType);

                Object[] objects = new BitmapWorkerTask().execute(inputStream).get();
                bos_common = (ByteArrayOutputStream) objects[0];
                bitmap_gal_re = (Bitmap)objects[1];
                imageView_selIm.setImageBitmap(bitmap_gal_re);
                //bitmap_gal_re = adjustBitmapSize(bitmap_gal_re,227,227);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        buttonAction(inflater);
        return view_askDetails;
    }

    private void buttonAction(final LayoutInflater inflater) {
        final Fragment thisfragment = this;
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFragment = new AskDetailsActivityDialogFragment ();
                dialogFragment.setTargetFragment(thisfragment,DATA_REQUEST_CODE);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "Sample Fragment");
            }
        });
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

    public class BitmapWorkerTask extends AsyncTask<InputStream,Void,Object[]> {

        @Override
        protected Object[] doInBackground(InputStream... inputStreams) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStreams[0]);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
            Bitmap bitmap_re = adjustBitmapSize(bitmap,512,512);
            bitmap_re.compress(Bitmap.CompressFormat.JPEG,100,bos);
            bitmap = adjustBitmapSize(bitmap,4096,4096);
            Object[] objects = new Object[2];
            objects[0] = bos;
            objects[1] = bitmap;
            return objects;
        }
    }
    private Bitmap adjustBitmapSize(Bitmap bitmap, int reqwidth, int reqheight){

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if(width > reqwidth)
            width = reqwidth;
        if(height > reqheight)
            height = reqheight;
       Bitmap bitmap_re = Bitmap.createScaledBitmap(bitmap, width,height,true);

        Log.d(LOG_TAG,"Original   dimensions "+ bitmap.getWidth()+" "+bitmap.getHeight());
        Log.d(LOG_TAG,"Compressed dimensions "+ bitmap_re.getWidth()+" "+bitmap_re.getHeight());
        return bitmap_re;
    }
    @Override
    public void newDataReceived(DetailsProject detailsProject) {
        try {
            String dispName = detailsProject.getDispName();
            Log.d(LOG_TAG,dispName );
            Log.d(LOG_TAG, detailsProject.getPlateSize());
            Log.d(LOG_TAG, "" + detailsProject.getPlatesNo());
            String[] createProjResult = createProj();
            String projDirpath = createProjResult[0];
            String projDirname = createProjResult[1];
            if (projDirpath.equals(""))
                return;
            detailsProject.setProjectName(projDirname);
            String imName;
            //InputStream inputStream;
            Bitmap bitmap_thumb;
            if (reqTypeCam) {
                bitmap_cam.compress(Bitmap.CompressFormat.JPEG,100,bos_common);
                bitmap_thumb = Bitmap.createScaledBitmap(bitmap_cam,48,48,true);
                //inputStream = new ByteArrayInputStream(bos_common.toByteArray());
            } else {
                bitmap_thumb = Bitmap.createScaledBitmap(bitmap_gal_re,48,48,true);
                //inputStream = new ByteArrayInputStream(bos_common.toByteArray());
            }
            bitmap_thumb.compress(Bitmap.CompressFormat.JPEG,100,bos_thumb);
            imName = copyImageToProjDir(projDirpath);
            if(dispName.equals("")){
                dispName = projDirname;
            }
            new DatabaseHelper.DbTaskInsert(this.getActivity()).doInBackground(projDirname,"started",imName,detailsProject.getPlateSize()+","+detailsProject.getPlatesNo(),dispName);
            detailsProject.setImageName(imName);
            String xmlName = createXMLUserInfo(projDirpath,detailsProject);
            Log.d(LOG_TAG,"About to send !!!!!!!");
            if(listener!=null){
                Log.d(LOG_TAG,"About to send !!!!!!!, listener is not null");
                listener.sendDataTCPFiles(new File(projDirpath+"/"+imName),new File(projDirpath+"/"+xmlName));
            }
            Toast.makeText(getContext(),dispName+ " started processing!",Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
        imageView_selIm.setImageResource(0);
        this.getActivity().onBackPressed();
    }

    public String[] createProj(){
        int count = 0;
        while (true) {
            int rand = (int)(Math.random()*1000);
            String projDirName = "PROJ_"+ String.format("%04d",rand);
            Log.d(LOG_TAG,"proj dir name: "+projDirName);

            File projFolder = new File(globalClass.getMainFolderPath()+"/"+projDirName);
            Log.d(LOG_TAG,"proj Folder: "+projFolder.getPath());
            String[] returnString = new String[2];
            if(!projFolder.exists()){
                projFolder.mkdirs();
                returnString[0] = projFolder.getPath();
                returnString[1] = projDirName;
                return returnString  ;
            }
            count++;
            if (count > 5)
            {
                Log.d(LOG_TAG,"Unable to create any new project ! please delete some old projects");
                Toast.makeText(this.getActivity(),"Unable to create any new project ! please delete some old projects",Toast.LENGTH_LONG).show();
                return new String[]{"",""};
            }
        }
    }
    public String createXMLUserInfo(String projDirPath, DetailsProject detailsProject) throws Exception {
        String xmlFileName = "userInfo_"+projDirPath.split("PROJ_")[1]+".xml";
        Log.d(LOG_TAG,"xml file name: "+xmlFileName);
        String xmlFilePath = projDirPath+"/"+xmlFileName;
        File xmlFile = new File(xmlFilePath);
        Writer writer = new BufferedWriter(new FileWriter(xmlFile));
        writer.write(writeUsingXMLSerializer(detailsProject));
        writer.close();
        return xmlFileName;
    }
    public String copyImageToProjDir(String projDirPath ) throws IOException {

        String newImFileName = "IMG_"+ projDirPath.split("PROJ_")[1]+".jpg";
        Log.d(LOG_TAG,"new im file name: "+newImFileName);
        String imFilePath = projDirPath+"/"+newImFileName;
        FileOutputStream fos = new FileOutputStream (new File(imFilePath));
        try {
            bos_common.writeTo(fos);
        } catch(IOException ioe) {
            // Handle exception here
            ioe.printStackTrace();
        } finally {
            fos.close();
        }
        String thumbImFileName = "THUMB_"+ projDirPath.split("PROJ_")[1]+".jpg";
        Log.d(LOG_TAG,"thumb im file name: "+thumbImFileName);
        imFilePath = projDirPath+"/"+thumbImFileName;
        fos = new FileOutputStream (new File(imFilePath));
        try {
            bos_thumb.writeTo(fos);
        } catch(IOException ioe) {
            // Handle exception here
            ioe.printStackTrace();
        } finally {
            fos.close();
        }

        return newImFileName;
    }

    public static String writeUsingXMLSerializer(DetailsProject detailsProject) throws Exception {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        xmlSerializer.setOutput(writer);
        // start DOCUMENT
        xmlSerializer.startDocument("UTF-8", true);
        // open tag: <PROJ-INFO>
        xmlSerializer.startTag("", "PROJ-INFO");

        // open tag: <PROJ-NAME>
        xmlSerializer.startTag("", "PROJ-NAME");
        xmlSerializer.text(detailsProject.getProjectName());
        // close tag: </PROJ-NAME>
        xmlSerializer.endTag("", "PROJ-NAME");

        // open tag: <IMG-NAME>
        xmlSerializer.startTag("", "IMG-NAME");
        xmlSerializer.text(detailsProject.getImageName());
        // close tag: </content>
        xmlSerializer.endTag("", "IMG-NAME");

        // open tag: <PLATES-NO>
        xmlSerializer.startTag("", "PLATES-NO");
        xmlSerializer.text(""+detailsProject.getPlatesNo());
        // close tag: </PLATES-NO>
        xmlSerializer.endTag("", "PLATES-NO");

        // open tag: <PLATE-SIZE>
        xmlSerializer.startTag("", "PLATE-SIZE");
        xmlSerializer.text(detailsProject.getPlateSize());
        // close tag: </PLATE-SIZE>
        xmlSerializer.endTag("", "PLATE-SIZE");

        // close tag: </PROJ-INFO>
        xmlSerializer.endTag("", "PROJ-INFO");

        // end DOCUMENT
        xmlSerializer.endDocument();
        return writer.toString();
    }

    public interface SendDataListener {

        public void sendDataTCPFiles(File file1,File file2) throws Exception;
    }

}
