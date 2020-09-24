package com.velasteguicorps.p2p_project.Network;

import java.net.SocketException;
import java.net.InetAddress;
import com.velasteguicorps.p2p_project.MyController.Listener;

/**
 *
 * @author Luis Velastegui
 */
public class Network {
    
    protected final Listener control;
    private NetworkConnection cnn;
    
    
    public Network(Listener control) {
        this.control = control;
        try{
            cnn = NetworkConnection.getNetworkConnection();
        }catch(SocketException ex){
            control.update().errorMessage("No se puede conectar a la red!");
        }
    }
    
    public String getHostAddress(){
        return cnn.getHostAddress();
    } 
    
    public String getHostName(){
        return cnn.getHostName();
    }
    
    public InetAddress getBroadcast(){
        return cnn.getBroadcast();
    }

    public Integer getTcpPort() {
        return cnn.getTcpPort();
    }

    public Integer getUdpPort() {
        return cnn.getUdpPort();
    }
    

}
