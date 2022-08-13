package com.example.jamaal.imtocal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements SocketClient.UpdateListener{

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    public static final String PREFS_NAME = "MyPrefsFile";
    private ArrayAdapter<String> listItemAdapter;
    ImageView imgTakenPhoto;
    private static final int CAM_REQUEST = 1313;
    private static final int GAL_REQUEST = 1314;
    GlobalClass globalClass;
    File mainFolder;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG,"MainActivityFragment created!");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Log.d(LOG_TAG,"Is external Storage writable: "+isExternalStorageWritable());
        Log.d(LOG_TAG,"Is external Storage readable: "+isExternalStorageReadable());
        globalClass = (GlobalClass)this.getActivity().getApplicationContext();
        createMainFolder();
    }

    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = this.getActivity();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        imgTakenPhoto = (ImageView) rootView.findViewById(R.id.uploadImView);
        ArrayList<String> itemArrList = new ArrayList<String>();
        itemArrList.add(getString(R.string.camera_action));
        itemArrList.add(getString(R.string.gallery_action));
        itemArrList.add(getString(R.string.results_action));

        listItemAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_mainactivity,R.id.list_item_mainactivity_textview,itemArrList);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_mainactivity);
        listView.setAdapter(listItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String itemStr = listItemAdapter.getItem(position);
                if(itemStr.equals(getString(R.string.camera_action))) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //File photoFile = new File(globalClass.getTempCamImPath());
                    File photoFile = new File(Environment.getExternalStorageDirectory()+ File.separator+"image.jpg");
                    if(photoFile.exists())
                        photoFile.delete();
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
                    startActivityForResult(cameraIntent, CAM_REQUEST);
                }
                else if(itemStr.equals(getString(R.string.gallery_action))) {
                    imgTakenPhoto.setImageResource(0);
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GAL_REQUEST);
                }
                else if(itemStr.equals("Results")){
                    startActivity(new Intent(getActivity(),ShowProgressActivity.class));
                }
             }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAM_REQUEST && resultCode == Activity.RESULT_OK){
            if (data == null) {
                Log.e(LOG_TAG,"no data taken!!");
                //return;
            }

            Intent intent_askDetails = new Intent(this.getActivity(),AskDetailsActivity.class);
            intent_askDetails.putExtra("REQUEST_TYPE","CAM");
            intent_askDetails.putExtra("URI_VAL","");
            startActivity(intent_askDetails);

        }
        if(requestCode == GAL_REQUEST  && resultCode == Activity.RESULT_OK){

            if (data == null) {
                Log.e(LOG_TAG,"no data taken!!");
                return;
            }
            Intent intent_askDetails = new Intent(this.getActivity(),AskDetailsActivity.class);
            intent_askDetails.putExtra("REQUEST_TYPE","GAL");
            intent_askDetails.putExtra("URI_VAL",data.getData().toString());
            startActivity(intent_askDetails);

        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void createMainFolder(){
        //mainFolder = new File(Environment.getExternalStorageDirectory()+ File.separator+"ImToCal");
        mainFolder = new File(globalClass.getMainFolderPath());
        if(!mainFolder.exists())
            mainFolder.mkdirs();
        else
            Log.d(LOG_TAG,mainFolder.getPath()+" already exists!!");
        File incomingFolder = new File(globalClass.getIncomingFolderPath());
        if(!incomingFolder.exists())
            incomingFolder.mkdir();
        else
            Log.d(LOG_TAG,incomingFolder.getPath()+" already exists!!");
    }

    @Override
    public void projInfoReceived(String projID) {
        Log.d(LOG_TAG,"projID to be updated: "+projID);
        String projFolder=GlobalClass.getInstance().getMainFolderPath()+File.separator+"PROJ_"+projID;
        //String imfilepath = projFolder+File.separator+"IMG_"+projID+"box.jpg";
        String xmlfilepath = projFolder+File.separator+"opInfo_"+projID+".xml";
        if(!checkForFiles(projID,"xml")) {
            Log.d(LOG_TAG,"xml: "+xmlfilepath+" doesn't exist");
            return;
        }
        try{
            Log.d(LOG_TAG,"Check 1");
            File xmlfile = new File(xmlfilepath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlfile);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("PROJ-OP-INFO");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Log.d(LOG_TAG,"Check "+temp);
                Node nNode = nList.item(temp);
                Log.d(LOG_TAG,"\nCurrent Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String projName = eElement.getElementsByTagName("PROJ-ID").item(0).getTextContent();
                    Log.d(LOG_TAG,"PROJ-ID : " + projName);
                    Boolean isFood = Boolean.parseBoolean(eElement.getElementsByTagName("IS-FOOD").item(0).getTextContent());
                    Log.d(LOG_TAG,"IS-FOOD : "+isFood);
                    String status="Done";
                    String opString ="";
                    if(!isFood){
                        opString="NOTFOOD";
                        new DatabaseHelper.DbTaskUpdate(this.getActivity()).doInBackground(projName,status,"",opString);
                        Intent intent_resultspage = new Intent(context,ResultPageActivity.class);
                        intent_resultspage.putExtra("PROJ_DIR",projName);
                        startActivity(intent_resultspage);
                        return;
                    }

                    String opImgName = eElement.getElementsByTagName("OP-IMG-NAME").item(0).getTextContent();
                    Log.d(LOG_TAG,"OP-IMG-NAME : " +opImgName );
                    if(!checkForFiles(projID,"jpg")) {
                        Log.d(LOG_TAG,opImgName+" not found!! not doing anything!");
                        return;
                    }
                    int plno = Integer.parseInt(eElement.getElementsByTagName("NO-ITEMS").item(0).getTextContent());

                    //int plno = Integer.parseInt(childNode.getAttributes().getNamedItem("no").getTextContent());
                    opString = "op,"+plno;
                    for (int pl=0;pl<plno;pl++){
                        Node childNode = eElement.getElementsByTagName("FOOD-ITEM").item(pl);
                        if(childNode.getNodeType() == Node.ELEMENT_NODE){
                            Element cElement = (Element) childNode;
                            String label_no =  cElement.getElementsByTagName("LABEL-NO").item(0).getTextContent();
                            String class_label = cElement.getElementsByTagName("CLASS-LABEL").item(0).getTextContent();
                            String calories = cElement.getElementsByTagName("CALORIES").item(0).getTextContent();

                            Log.d(LOG_TAG,"LABEL-NO : " +label_no);
                            Log.d(LOG_TAG,"CLASS-LABEL : " +class_label );
                            Log.d(LOG_TAG,"CALORIES : " +calories );
                            opString=opString+","+label_no+","+class_label+","+calories;
                        }
                    }
                    new DatabaseHelper.DbTaskUpdate(this.getActivity()).doInBackground(projName,status,opImgName,opString);
                    Intent intent_resultspage = new Intent(context,ResultPageActivity.class);
                    intent_resultspage.putExtra("PROJ_DIR",projName);
                    startActivity(intent_resultspage);
                    /*
                    NotificationManager NM;
                    NM=(NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notify=new Notification(android.R.drawable.stat_notify_more,"ImToCal",System.currentTimeMillis());
                    PendingIntent pending=PendingIntent.getActivity(getActivity().getApplicationContext(), 0, new Intent(),0);
                    notify. //.setLatestEventInfo(getActivity().getApplicationContext(), projName, projName+" is completed!",pending);
                    NM.notify(0, notify);
                    */
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }


}
    public boolean checkForFiles(String projID, String type){
        String incomingFolderPath =GlobalClass.getInstance().getIncomingFolderPath();
        if( new File(incomingFolderPath).exists()) {
            String imfile_conf = incomingFolderPath+File.separator+"IMG_"+projID+"box_completed.txt";
            String xmlfile_conf = incomingFolderPath+File.separator+"opInfo_"+projID+"_completed.txt";
            if(type.equals("xml")) {
                if (new File(xmlfile_conf).exists()) {
                    return true;
                }
            }else if(type.equals("jpg")) {
                if (new File(imfile_conf).exists()) {
                    return true;
                }
            }
        }
        return false;
    }

}
