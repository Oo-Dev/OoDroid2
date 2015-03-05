package oo.org.server;


import java.net.ServerSocket;

public class SDPDistributor2 {
    
    private final static String TAG = "SDPDistributor2";
    
    /** Client wants sdp file */
    final static String REQUEST_SDP = "REQUEST SDP FILE";

    static ClientManager sClientManager;
    
    private static String mSessionDiscription;
    /** Port used by default*/
    public final static int DISTRIBUTOR_DEFAULT_PORT = 25581;
    ServerSocket mDistributor;

    protected int mPort = DISTRIBUTOR_DEFAULT_PORT;

    public SDPDistributor2(String sessionDiscription){
        this.mSessionDiscription = sessionDiscription;
    }

    public static String getSessionDiscription(){
        return mSessionDiscription;
        
    }



}
