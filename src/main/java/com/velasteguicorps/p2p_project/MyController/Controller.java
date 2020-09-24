package com.velasteguicorps.p2p_project.MyController;

import com.velasteguicorps.p2p_project.Model.VO.MyFile;
import com.velasteguicorps.p2p_project.Model.VO.Pc;

/**
 *
 * @author Luis Velastegui
 */
public interface Controller extends Messages {
    void addInputElement(MyFile myFile);
    void addPc(Pc pc);
    void removePc(Pc pc);
    void updateChat(String chat);
}
