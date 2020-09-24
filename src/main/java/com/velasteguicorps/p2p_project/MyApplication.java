package com.velasteguicorps.p2p_project;

import com.velasteguicorps.p2p_project.Controller.PrincipalController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Luis Velastegui
 */
public class MyApplication extends Application{
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PrincipalScene.fxml"));
        Parent root = (Parent)loader.load(); 
        PrincipalController controller = loader.getController();
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setTitle("File Sharing 1.0");
        stage.setScene(scene);
        
        stage.setOnCloseRequest((event) ->{
            controller.updateListViewOnExit();
        });
        
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon/icon.png")));
        stage.show();
    }
}
