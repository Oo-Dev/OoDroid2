package oo.org.server;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientRunnable implements Runnable{

    /** Thread to handle client request*/
    private ClientTask mClientTask;

    private Socket mClient;
    private final static String TAG = "ClientRunnable";

    @Override
    public void run() {
        Log.i(TAG, "Connection from " + mClient.getInetAddress().getHostAddress());
        try {
            processRequest(new InputStreamReader(mClient.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"InputStream error");
        } catch (ClassNotFoundException e) {
            //TODO send back "Bad request" error
            e.printStackTrace();
        }
        Log.i(TAG,"Connect end");
        try {
            mClient.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"Client close error");
        }
    }

    void processRequest(InputStreamReader reader) throws ClassNotFoundException{
        String lineString;
        try {
            lineString = new BufferedReader(reader).readLine();
            Log.d(TAG,"request message is " + lineString);
            switch (lineString){
                case SDPDistributor2.REQUEST_SDP:Log.d(TAG,"SDP file is requested");sendSDP(mClient.getOutputStream());break;
                default:throw new ClassNotFoundException("Unknown request");//TODO return error info
            }
            //}
        } catch (IOException e) {
            Log.i(TAG,"Client disconnected");
            e.printStackTrace();
        }
    }

    void sendSDP(OutputStream out) throws IOException {
        PrintWriter pw = new PrintWriter(out);
        pw.println(SDPDistributor2.getSessionDiscription());
        pw.flush();
        out.close();
        //pw.close();

    }

    ClientRunnable(ClientTask clientTask){
        mClientTask = clientTask;
        mClient = mClientTask.getClient();

    }
}
