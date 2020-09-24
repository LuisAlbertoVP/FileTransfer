package com.velasteguicorps.p2p_project.Model.VO;

import java.io.Serializable;

/**
 *
 * @author Luis Velastegui
 */
public class Transmition implements Serializable{
    
    private boolean hasNext;
    private byte[] bytes;

    
    public boolean hasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    } 
    
}
