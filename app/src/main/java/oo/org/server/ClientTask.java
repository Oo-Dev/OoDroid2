package oo.org.server;

public class ClientTask {

    private final static String TAG = "ClientTask";


    private ClientRunnable clientThread;//TODO uninitialized
    public ClientRunnable getClientRunnable() {
        return clientThread;
    }

    public void setClientRunnable(ClientRunnable clientThread) {
        this.clientThread = clientThread;
    }
    




}
