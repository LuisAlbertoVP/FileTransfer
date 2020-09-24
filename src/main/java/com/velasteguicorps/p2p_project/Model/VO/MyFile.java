package com.velasteguicorps.p2p_project.Model.VO;

import java.io.Serializable;

/**
 *
 * @author Luis Velastegui
 */
public class MyFile implements Serializable {
    
    private String path;
    private String name;
    private double size;
    private String unit;
    private long length;
    private final Transmition trasmition;
    private transient Progress progress;

    
    public MyFile() {
        trasmition = new Transmition();
    }

    
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }  
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSize() {
        return size;
    }
    
    public void setSize(double size) {
        double sizeKb = size / 1000;    
        if(sizeKb < 1000){
            this.size = sizeKb;
            this.unit = "kb";
        }else{
            double sizeMb = sizeKb / 1000;
            if(sizeMb < 1000){
                this.size = sizeMb;
                this.unit = "mb";
            }else{
                double sizeGb = sizeMb / 1000;
                if(sizeGb < 1000){
                    this.size = sizeGb;
                    this.unit = "gb";
                }
            }
        }    
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public Transmition getTrasmition() {
        return trasmition;
    } 
    
    public Progress getProgress() {
        return progress;
    }
    
    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    
    @Override
    public String toString() {
        return "Nombre: " + name + "\nTamaño: " + String.format("%.1f", size) + " "+unit;
    }


}
