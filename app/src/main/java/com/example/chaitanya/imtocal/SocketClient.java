package com.example.chaitanya.imtocal;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Chaitanya on 8/10/2016.
 */
public class SocketClient implements AskDetailsActivityFragment.SendDataListener {
    private final String LOG_TAG = SocketClient.class.getSimpleName();
    private Socket socket;
    private static final int SERVERPORT = 1234;
    private static final String SERVER_IP = "ramawks81";
    //private static final String SERVER_IP = "10.0.0.4";
    DataInputStream din;
    DataOutputStream dout;
    UpdateListener updateListener;

    public void addUpdateListener(UpdateListener updateListener){
        this.updateListener = updateListener;
    }

    protected void start_thread() {
        new Thread(new ClientThread()).start();
    }


    public class sendDataTask extends AsyncTask<File,Void,Void>{

        @Override
        protected Void doInBackground(File... files) {
            try {

                File file = files[0];
                String filename = file.getName();
                Log.d(LOG_TAG, "File to be sent! " + filename);
                if (!file.exists()) {
                    Log.d(LOG_TAG, "File not found!");
                    return null;
                }
                dout.writeUTF("SEND");
                dout.writeUTF(filename);
                /*
                String msgFromServer = din.readUTF();
                if (msgFromServer.compareTo("File Already Exists") == 0) {
                    Log.d(LOG_TAG, "File already exists at server!");
                    return null;
                }*/
                Log.d(LOG_TAG,"Sending File ...");
                FileInputStream fin = new FileInputStream(file);
                int ch;
                do {
                    ch = fin.read();
                    dout.writeUTF(String.valueOf(ch));
                }
                while (ch != -1);
                fin.close();
                //Log.d(LOG_TAG, din.readUTF());
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }
    @Override
    public void sendDataTCPFiles(File file1, File file2) throws Exception {

        Log.d(LOG_TAG,"file1 name: "+file1.getPath());

        new sendDataTask().execute(file1);
        Log.d(LOG_TAG,"file2 name: "+file2.getPath());

        new sendDataTask().execute(file2);
    }
    public void TestCall(String projID){
        Log.d(LOG_TAG,"str: "+projID);
        updateListener.projInfoReceived(projID);
    }
    public interface UpdateListener{
        public void projInfoReceived(String projID);
    }
    final class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
                din = new DataInputStream(socket.getInputStream());
                dout = new DataOutputStream(socket.getOutputStream());
                boolean bSocIsClosed = socket.isClosed();
                boolean bSocIsBound = socket.isBound();
                Log.d(LOG_TAG,"bSocIsClosed: "+bSocIsClosed);
                Log.d(LOG_TAG,"bSocIsBound: "+bSocIsBound);
                String Command = "";
                while((!bSocIsClosed)&&bSocIsBound){
                    GlobalClass.getInstance().setSocConn(true);
                    Command = din.readUTF();
                    if(Command.contains("SEND")){
                        String projID = Command.split(",")[1];
                        Log.d(LOG_TAG,"About to receive data for ProjID: "+projID);
                        String filename = din.readUTF();
                        Log.d(LOG_TAG,"About to receive file: "+filename);
                        String projFolder=GlobalClass.getInstance().getMainFolderPath()+File.separator+"PROJ_"+projID;
                        if( new File(projFolder).exists())
                            Log.d(LOG_TAG,projFolder+" exists!");
                        File receivedFile = new File(projFolder+File.separator+filename);
                        if(receivedFile.exists()){
                            receivedFile.delete();
                        }
                        FileOutputStream fout=new FileOutputStream(receivedFile);
                        int ch;
                        String temp;
                        do
                        {
                            temp=din.readUTF();
                            ch=Integer.parseInt(temp);
                            if(ch!=-1)
                            {
                                fout.write(ch);
                            }
                        }while(ch!=-1);
                        fout.close();
                        File flagFile = new File(GlobalClass.getInstance().getIncomingFolderPath()+ File.separator+filename.split("\\.")[0]+"_completed.txt");
                        flagFile.createNewFile();
                        TestCall(projID);

                    }
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            GlobalClass.getInstance().setSocConn(false);

        }

    }
}
