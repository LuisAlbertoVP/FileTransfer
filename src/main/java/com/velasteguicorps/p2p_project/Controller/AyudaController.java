package com.velasteguicorps.p2p_project.Controller;

import com.velasteguicorps.p2p_project.Model.Utilities.FxMessages;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Luis Velastegui
 */
public class AyudaController implements Initializable{

    @FXML
    private TreeView<String> treePrincipal;
     
    @FXML
    private VBox paneHome;

    @FXML
    private VBox paneEnvio;

    @FXML
    private VBox paneRecibir;

    @FXML
    private VBox paneCorreo;

    @FXML
    private TextField txtAsunto;

    @FXML
    private TextArea txtComentarios;

    
    @FXML
    void sendComentarios(ActionEvent event) {
        String correo = txtAsunto.getText();
        if(correo.length() > 0)
            FxMessages.getAlert().errorMessage("Ingrese un correo!");
        else{
            String comentario = txtComentarios.getText();
            if(comentario.length() > 0)
                FxMessages.getAlert().errorMessage("Escriba un comentario!");
            else{
                String asunto = correo.trim();
                String comments = comentario.trim();
                String myEmail = "luisvelastegui397@gmail.com";
                try{
                    sendEmail(myEmail, asunto, comments);
                    FxMessages.getAlert().informationMessage("Mensaje enviado!");
                } catch(MessagingException ex){
                    FxMessages.getAlert().errorMessage("No se pudo enviar email!");
                }
            }
        }
    }
    
    public void sendEmail(final String to, final String asunto, final String bodyText) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props,
          new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("correop2p@gmail.com", "maq_p2p-v1_by:luisVelastegui");
            }
        });
        MimeMessage message = new MimeMessage(session);
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(asunto);
        message.setText(bodyText);
        Transport.send(message);
    }
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> loadTreeView());
    }
    
    
    private void loadTreeView(){
        TreeItem<String> sobre = new TreeItem<> ("Sobre");
        TreeItem<String> info = new TreeItem<> ("Información");
        sobre.getChildren().add(info);
        
        TreeItem<String> ayuda = new TreeItem<> ("Ayuda");
        TreeItem<String> envio = new TreeItem<> ("Enviar archivos");
        TreeItem<String> guardar = new TreeItem<> ("Guardar archivos");
        ayuda.getChildren().addAll(envio, guardar);
        
        TreeItem<String> soporte = new TreeItem<> ("Soporte");
        TreeItem<String> comentarios = new TreeItem<> ("Enviar comentarios");
        soporte.getChildren().add(comentarios);
        
        TreeItem<String> root = new TreeItem<> ("Sistema P2P");
        root.getChildren().addAll(sobre, ayuda, soporte);
        
        treePrincipal.setRoot(root);
        treePrincipal.getRoot().setExpanded(true);
        treePrincipal.getRoot().getChildren().get(0).setExpanded(true);
        
        treePrincipal.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectPane(newValue.getValue()));
    }
    
    private void selectPane(final String value){
        switch(value){
            case "Información" : loadInfo(); break;
            case "Enviar archivos" : loadEnviar(); break;
            case "Guardar archivos" : loadRecibir(); break;
            case "Enviar comentarios" : loadComentarios(); break;
        }
    }
    
    private void loadInfo(){
        paneEnvio.setVisible(false);
        paneRecibir.setVisible(false);
        paneCorreo.setVisible(false);
        paneHome.setVisible(true);
    }
    
    private void loadEnviar(){
        paneHome.setVisible(false);
        paneRecibir.setVisible(false);
        paneCorreo.setVisible(false);
        paneEnvio.setVisible(true);
    }
    
    private void loadRecibir(){
        paneHome.setVisible(false);
        paneEnvio.setVisible(false);
        paneCorreo.setVisible(false);
        paneRecibir.setVisible(true);
    }
    
    private void loadComentarios(){
        paneHome.setVisible(false);
        paneEnvio.setVisible(false);
        paneRecibir.setVisible(false);
        paneCorreo.setVisible(true);
    }
    
}
