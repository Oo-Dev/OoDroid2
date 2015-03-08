package oo.org.server;


import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;

public class SDPDistributor2 {
    
    private final static String TAG = "SDPDistributor2";
    
    /** Client wants sdp file */
    final static String REQUEST_SDP = "REQUEST SDP FILE";
    
    /** Is request listener is running */
    private boolean alive = false;

    static ClientManager sClientManager;
    private static String mSessionDiscription;
    
    /** Port used by default*/
    public final static int DISTRIBUTOR_DEFAULT_PORT = 25581;
    protected int mPort = DISTRIBUTOR_DEFAULT_PORT;
    
    SDPDistributor2.RequestListener mRequestListener;

    public void stopServer() {
        if(mRequestListener != null && !mRequestListener.isClosed()) {
            Log.d("OoDroidActivity", "SDP server is stopped");
            mRequestListener.close();
            mRequestListener = null;
            alive = false;
        }
    }


    public void startServer() throws IOException {
        if(mRequestListener == null)
            mRequestListener = new SDPDistributor2.RequestListener();
        if(!alive)
            mRequestListener.start();
        //mDistributor.setSoTimeout(100);
    }


    class RequestListener extends Thread implements Runnable{

        private final ServerSocket requestListener;

        public RequestListener() throws IOException {
            requestListener = new ServerSocket(mPort);
        }

        @Override
        public void run() {
            alive = true;
            while(!Thread.interrupted()) {
                try {
                    Log.d(TAG, "SDP distributor is listening on port " + requestListener.getLocalPort());
                    sClientManager.startDistribute(requestListener.accept());
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            Log.i(TAG, "SDP distributor stopped !");
        }

        public boolean isClosed(){
            return requestListener.isClosed();
        }

        public void close(){
            try {
                requestListener.close();
                this.interrupt();
            } catch (IOException e) {
                Log.e(TAG,"request listener of server close error");
                e.printStackTrace();
            }

        }
    }
    
    public SDPDistributor2(String sessionDiscription){
        this.mSessionDiscription = sessionDiscription;
        sClientManager = ClientManager.getInstance();
    }

    public static String getSessionDiscription(){
        return mSessionDiscription;
        
    }



}
