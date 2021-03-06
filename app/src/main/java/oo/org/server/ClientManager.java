package oo.org.server;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientManager {

    private final ThreadPoolExecutor mClientThreadPool;
    private final BlockingQueue<Runnable> mClientQueue;
    
    private final static int CORE_POOL_SIZE = 8;
    private final static int MAX_POOL_SIZE = 8;
    private final static int KEEP_ALIVE_TIME = 1;
    private final static TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private static ClientManager sInstance = null;

    static {
        // singleton mode
        sInstance = new ClientManager();
    }
    
    public static ClientManager getInstance() {
        return sInstance;
    }
    
    public void startDistribute(Socket mClient){
        sInstance.mClientThreadPool.execute(new ClientRunnable(mClient));
    }

    private ClientManager(){
        mClientQueue = new LinkedBlockingQueue<Runnable>();

        mClientThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mClientQueue);
    }
}
