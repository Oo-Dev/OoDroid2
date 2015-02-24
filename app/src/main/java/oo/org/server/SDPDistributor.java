package oo.org.server;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Distributs session discription to clients,and they save it as .sdp file.
 * So a client should get connection to this server firstly,obtain the .sdp file then open the file with VLC.
 */
public class SDPDistributor extends Thread{
    private final static String TAG="SDPDistributor";

    /** Client wants sdp file */
    final static String REQUEST_SDP = "REQUEST SDP FILE";

    private String mSessionDiscription;
    
    protected boolean alive = false;
    
    /** Port used by default*/
    public final static int DISTRIBUTOR_DEFAULT_PORT = 25580;
    ServerSocket mDistributor;

    protected int mPort = DISTRIBUTOR_DEFAULT_PORT;

    
    public SDPDistributor(String sessionDiscription){
        this.mSessionDiscription = sessionDiscription;
    }

    @Override
    public void run() {
        alive = true;
        while(!Thread.interrupted()) {
            try {
                Log.d(TAG, "SDP distributor is listening on port " + mDistributor.getLocalPort());
                Socket client = mDistributor.accept();
                new WorkerThread(client).start();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
        Log.i(TAG, "SDP distributor stopped !");
    }

    public void stopServer() {
        try {
            if(!mDistributor.isClosed()) {
                Log.d("OoDroidActivity","SDP server is stopped");
                this.interrupt();
                mDistributor.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "close server error");
            e.printStackTrace();
        }
    }


    public void startServer() throws IOException {
        mDistributor = new ServerSocket(mPort);
        mDistributor.setSoTimeout(100);

        Log.d(TAG,"starting server : ");
        if(!this.isRunning()) {
            Log.d(TAG, "first");
            this.start();
        }
        else{
            if(this.isInterrupted())
                Log.d(TAG,"interrupted");
            else
                Log.d(TAG, "not interrupted");
            this.interrupt();
            if(this.isInterrupted())
                Log.d(TAG,"interrupted");
            else
                Log.d(TAG, "not interrupted");
        }
    }
    class WorkerThread extends Thread{
        
        private Socket mClient;
        
        public WorkerThread(final Socket client){
            this.mClient = client;
        }
        
        
        @Override
        public void run() {
            Log.i(TAG,"Connection from " + mClient.getInetAddress().getHostAddress());
            try {
                processRequest(new BufferedReader(new InputStreamReader(mClient.getInputStream())));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,"InputStream error");
            } catch (ClassNotFoundException e) {
                //TODO send back "Unknown request" error
                e.printStackTrace();
            }
        }
        
        void processRequest(BufferedReader bf) throws ClassNotFoundException{
            String line;
            try {
                while((line = bf.readLine()) == null){
                    switch (line){
                        case REQUEST_SDP:sendSDP(mClient.getOutputStream());break;
                        default:throw new ClassNotFoundException("Unknown request");
                    }
                }
            } catch (IOException e) {
                Log.i(TAG,"Client disconnected");
                e.printStackTrace();
            }
        }
        
        void sendSDP(OutputStream out) throws IOException {
            out.write(mSessionDiscription.getBytes());
        }
    }

    public int getPort() {
        return mPort;
    }

    public void setPort(int mPort) {
        this.mPort = mPort;
    }
    
    public boolean isRunning(){
        return alive;

    }
}
