package com.velasteguicorps.p2p_project.Model.VO;

/**
 *
 * @author Luis Velastegui
 */
public class Pc {
    private String hostAddress;
    private String hostName;
    private String port;
    private boolean indicator;
    

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String ip) {
        this.hostAddress = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }  

    public boolean getIndicator() {
        return indicator;
    }

    public void setIndicator(boolean indicator) {
        this.indicator = indicator;
    }
    
}
