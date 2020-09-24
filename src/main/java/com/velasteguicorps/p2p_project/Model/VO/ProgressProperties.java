package com.velasteguicorps.p2p_project.Model.VO;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Luis Velastegui
 */
public class ProgressProperties implements Progress{

    private final StringProperty status;
    private final DoubleProperty progress;
    
    
    public ProgressProperties(String status, Double progress){
        this.status = new SimpleStringProperty(status);
        this.progress = new SimpleDoubleProperty(progress);
    }
    

    public StringProperty getStatusProperty() {
        return status;
    }

    public DoubleProperty getProgressProperty() {
        return progress;
    }
    

    @Override
    public void setStatus(String status) {
        this.status.set(status);
    }

    @Override
    public void setProgress(double progress) {
        this.progress.set(progress);
    }
}
