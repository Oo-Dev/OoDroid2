package oo.org.server;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Distributs session discription to clients,and they save it as .sdp file.
 * So client should connect this server firstly,obtain the .sdp file then open this file with VLC.
 */
public class SDPDistributor extends Thread{
    private final static String TAG="SDPDistributor";
    private String mSessionDiscription;
    public final static int DISTRIBUTOR_DEFAULT_PORT = 25580;
    ServerSocket distributor;
    
    public SDPDistributor(String sessionDiscription) throws IOException {
        this.mSessionDiscription = sessionDiscription;
        distributor = new ServerSocket(DISTRIBUTOR_DEFAULT_PORT);
    }

    @Override
    public void run() {
        Log.d(TAG, "SDP distributor started");
        try {
            distributor.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
