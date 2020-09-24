package com.velasteguicorps.p2p_project.Network;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Luis Velastegui
 */
public class NetworkConnection {

    private static NetworkConnection instance;
    private String hostAddress;
    private String hostName;
    private InetAddress broadcast;
    private Integer tcpPort;
    private static final Integer UDPPORT = 12345;
    
    
    private NetworkConnection() throws SocketException{
        Enumeration<NetworkInterface> n;      
        n = NetworkInterface.getNetworkInterfaces();
        while (n.hasMoreElements()){
            NetworkInterface e = n.nextElement();
            setHost(e);
            setBroadcast(e);
        }
        setTcpPort();
    }
    
    public static NetworkConnection getNetworkConnection() throws SocketException{
        if(instance == null)
            instance = new NetworkConnection();
        return instance;
    }
            
    private void setHost(NetworkInterface e){
        Enumeration<InetAddress> a = e.getInetAddresses();
        while (a.hasMoreElements()){
            InetAddress addr = a.nextElement();
            if (addr == null)
                continue;
            if (addr.isSiteLocalAddress()){
                hostAddress = addr.getHostAddress();
                hostName = addr.getHostName();
            }
        }
    }
    
    private void setBroadcast(NetworkInterface e){
        for (InterfaceAddress interfaceAddress : e.getInterfaceAddresses()) {
            InetAddress addr = interfaceAddress.getBroadcast();
            if (addr == null)
                continue;
            broadcast = addr;
        }
    }
    
    private void setTcpPort() {
        tcpPort = ThreadLocalRandom.current().nextInt(12346, 13000);
    }
    
    public String getHostAddress(){
        return hostAddress;
    }
    
    public String getHostName(){
        return hostName;
    }
    
    public InetAddress getBroadcast(){
        return broadcast;
    }

    public Integer getTcpPort() {
        return tcpPort;
    }

    public Integer getUdpPort() {
        return UDPPORT;
    }
  
}
