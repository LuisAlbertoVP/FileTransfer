package com.velasteguicorps.p2p_project.MyController;

/**
 *
 * @author Luis Velastegui
 */
public interface Listener {
    ApplicationThread getApplicationThread();
    Attributes getAttributes();
    Controller update();
}
