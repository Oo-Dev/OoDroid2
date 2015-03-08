package oo.org.server;

import java.net.Socket;

public class ClientTask {

    private final static String TAG = "ClientTask";

    private static ClientManager mClientManager;
    private Socket mClient;
    
    private ClientRunnable clientThread;
    public ClientRunnable getClientRunnable() {
        return clientThread;
    }

    ClientTask(){
        clientThread = new ClientRunnable(this);
        mClientManager = ClientManager.getsInstance();
    }


    public Socket getClient() {
        return mClient;
    }
}
