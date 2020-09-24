package com.velasteguicorps.p2p_project.Model.Utilities;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 *
 * @author Luis Velastegui
 */
public class FxMessages {
    
    private static final String INF_TITTLE = "Información";
    private static final String INF_HEAD = "Mensaje de Información";
    private static final AlertType INF_ALERT = AlertType.INFORMATION;
    
    private static final String WARN_TITTLE = "Advertencia";
    private static final String WARN_HEAD = "Mensaje de Advertencia";
    private static final AlertType WARN_ALERT = AlertType.WARNING;
    
    private static final String ERROR_TITTLE = "Error";
    private static final String ERROR_HEAD = "Mensaje de Error";
    private static final AlertType ERROR_ALERT = AlertType.ERROR;
    
    private static FxMessages instance;
    
    private FxMessages(){}
    
    public static FxMessages getAlert(){
        if(instance == null)
            instance = new FxMessages();
        return instance;
    }
    
    public void informationMessage(final String message){
       if(Platform.isFxApplicationThread())
            alert(INF_ALERT, INF_TITTLE, INF_HEAD, message);
       else
            Platform.runLater(() -> alert(INF_ALERT, INF_TITTLE, INF_HEAD, message));
    }
    
    public void warningMessage(final String message){
        if(Platform.isFxApplicationThread())
            alert(WARN_ALERT, WARN_TITTLE, WARN_HEAD, message);
        else
            Platform.runLater(() -> alert(WARN_ALERT, WARN_TITTLE, WARN_HEAD, message));
    }
    
    public void errorMessage(final String message){
        if(Platform.isFxApplicationThread())
            alert(ERROR_ALERT, ERROR_TITTLE, ERROR_HEAD, message);
        else
            Platform.runLater(() -> alert(ERROR_ALERT, ERROR_TITTLE, ERROR_HEAD, message));
    }
    
    private void alert(final AlertType type, final String tittle, 
            final String header, final String message){
        Alert dialog = new Alert(type);
        dialog.setTitle(tittle);
        dialog.setHeaderText(header);
        dialog.setContentText(message);
        dialog.showAndWait();
    }
}
