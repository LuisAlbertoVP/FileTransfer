package com.velasteguicorps.p2p_project.Network.Server;

import com.velasteguicorps.p2p_project.Network.Network;
import com.velasteguicorps.p2p_project.MyController.Listener;

/**
 *
 * @author Luis Velastegui
 */
public abstract class Server extends Network{
 
    public Server(Listener control) {
        super(control);
    }
    
    public abstract void runServer();
    public abstract void closeServer();

}
