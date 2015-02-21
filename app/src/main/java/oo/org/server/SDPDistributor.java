package oo.org.server;

/**
 * Distributs session discription to clients,and they save it as .sdp file.
 * So client should connect this server firstly,obtain the .sdp file then open this file with VLC.
 */
public class SDPDistributor extends Thread{
    private String mSessionDiscription;
    
    public SDPDistributor(String sessionDiscription){
        this.mSessionDiscription = sessionDiscription;
        
    }

    @Override
    public void run() {

    }
}
