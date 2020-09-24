package com.velasteguicorps.p2p_project.Network.Client;

import com.velasteguicorps.p2p_project.Network.Network;
import com.velasteguicorps.p2p_project.MyController.Listener;

/**
 *
 * @author Luis Velastegui
 */
public class Client extends Network {
    
    public Client(Listener control){
        super(control);
    }
}
