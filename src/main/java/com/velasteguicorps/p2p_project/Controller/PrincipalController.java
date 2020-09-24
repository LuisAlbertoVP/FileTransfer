package com.velasteguicorps.p2p_project.Controller;

import com.velasteguicorps.p2p_project.MyController.*;
import com.velasteguicorps.p2p_project.Network.*;
import com.velasteguicorps.p2p_project.Model.Utilities.*;
import com.velasteguicorps.p2p_project.Model.VO.*;
import com.velasteguicorps.p2p_project.Network.Client.*;
import com.velasteguicorps.p2p_project.Network.Server.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Luis Velastegui
 */
public class PrincipalController implements Initializable {
    
    private TcpServer tcpServer;
    private UdpServer udpServer;
    private TcpClient tcpClient;
    
    
    @FXML
    private Button buttonSend;
    
    @FXML
    private MenuItem contextSendAnyFiles;

    @FXML
    private MenuItem contextSendAllFiles;
    
    @FXML
    private MenuItem delSendFilesAny;

    @FXML
    private MenuItem delSendFilesAll;

    @FXML
    private MenuItem delReceiveFilesAny;

    @FXML
    private MenuItem delReceiveFilesAll;
    
    @FXML
    private TableView<MyFile> tableOutputFiles;

    @FXML
    private TableColumn<MyFile, String> tabOutFile;

    @FXML
    private TableColumn<MyFile, Double> tabOutProgress;

    @FXML
    private TableColumn<MyFile, String> tabOutStatus;

    @FXML
    private TableView<MyFile> tableInputFiles;

    @FXML
    private TableColumn<MyFile, String> tabInFile;

    @FXML
    private TableColumn<MyFile, Double> tabInProgress;

    @FXML
    private TableColumn<MyFile, String> tabInStatus;

    @FXML
    private TextField txtRutaDestino;

    @FXML
    private CheckBox checkTransfer;

    @FXML
    private Label labelPort;

    @FXML
    private CheckBox checkChat;

    @FXML
    private Label labelIP;

    @FXML
    private ListView<Pc> listPcConectadas;

    @FXML
    private Label labelHelp;
    
    @FXML
    private TextArea txtChat;

    @FXML
    private TextField txtMensaje;

    
    @FXML
    void openFiles(ActionEvent event) throws IOException{
        FileChooser fileChooser = new FileChooser();     
        List<File> listFiles = fileChooser.showOpenMultipleDialog(buttonSend.getScene().getWindow());
        if(listFiles != null){
            for(File file : listFiles){
                MyFile myFile = new MyFile();
                myFile.setPath(file.getPath());
                myFile.setName(file.getName());
                myFile.setLength(file.length());
                myFile.setSize((double)Files.size(file.toPath()));
                myFile.setProgress(new ProgressProperties("En espera...", 0.0));
                tableOutputFiles.getItems().add(myFile);    
            }           
        }
    }
    
    @FXML
    void openDirectory(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        File selectedDirectory = chooser.showDialog(txtRutaDestino.getScene().getWindow());
        if(selectedDirectory != null)
            txtRutaDestino.setText(selectedDirectory.getPath());
    }

    @FXML
    void sendFiles(ActionEvent event) {  
        if(!tableOutputFiles.getItems().isEmpty()){         
            if(buttonSend == event.getSource()){
                ObservableList<Pc> listPc = listPcConectadas.getItems();
                sendFiles(listPc, tableOutputFiles.getItems());
            }
            else{
                ObservableList<Pc> listPc = listPcConectadas.getSelectionModel().getSelectedItems();
                if(!listPc.isEmpty()){
                    if(contextSendAnyFiles == event.getSource()){
                        ObservableList<MyFile> listFiles = tableOutputFiles.getSelectionModel().getSelectedItems();
                        if(!listFiles.isEmpty()) 
                            sendFiles(listPc, listFiles);
                        else
                            listener.update().errorMessage("Seleccione archivos para enviar!");
                    }      
                    if(contextSendAllFiles == event.getSource())
                        sendFiles(listPc, tableOutputFiles.getItems());           
                }else
                    listener.update().errorMessage("Seleccione una Pc a enviar!");
            }
        }else
            listener.update().errorMessage("No hay archivos para enviar!");
    }
    
    private void sendFiles(final ObservableList<Pc> listPc, final ObservableList<MyFile> listFiles){
        tcpClient.sendFiles(new ArrayList<>(listPc), new ArrayList<>(listFiles));
    }

    @FXML
    void deleteItemsToSend(ActionEvent event) {
        if(delSendFilesAny == event.getSource()){
            ObservableList<Integer> listIndexFiles = tableOutputFiles.getSelectionModel().getSelectedIndices();
            if(!listIndexFiles.isEmpty()){
                for(int indice : listIndexFiles)
                    tableOutputFiles.getItems().remove(indice);
            }else
                listener.update().errorMessage("No hay archivos seleccionados!");
        }
        if(delSendFilesAll == event.getSource()){
            if(!tableOutputFiles.getItems().isEmpty())
                tableOutputFiles.getItems().clear();
            else
                listener.update().errorMessage("No hay archivos!");
        }
    }
    
    @FXML
    void deleteItemsToReceive(ActionEvent event) {
        if(delReceiveFilesAny == event.getSource()){
            ObservableList<Integer> listIndexFiles = tableInputFiles.getSelectionModel().getSelectedIndices();
            if(!listIndexFiles.isEmpty()){
                for(int indice : listIndexFiles)
                    tableInputFiles.getItems().remove(indice);
            }else
                listener.update().errorMessage("No hay archivos seleccionados!");
        }
        if(delReceiveFilesAll == event.getSource()){
            if(!tableInputFiles.getItems().isEmpty())
                tableInputFiles.getItems().clear();
            else
                listener.update().errorMessage("No hay archivos!");
        }
    }
    
    @FXML
    void sendMessage(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)){
            if(checkChat.isSelected()){
                String mensaje = txtMensaje.getText();     
                if(mensaje.length() > 0){
                    if(mensaje.length() < 150){           
                        UdpClient client = new UdpClient(listener);
                        client.sendRequest("Mensaje", mensaje);
                        txtMensaje.setText("");
                    }else
                        listener.update().errorMessage("El mensaje no puede exceder los 150 caracteres");
                }else
                    listener.update().errorMessage("Ingrese un mensaje");
            }
        }
    }

    @FXML
    void openHelp(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AyudaScene.fxml"));
        Parent root = (Parent)loader.load();
        Stage stage = new Stage(StageStyle.UTILITY);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initOwner(labelHelp.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.centerOnScreen();
        stage.setTitle("Sobre Sistema P2P");
        stage.show();
    }

    @FXML
    void cancel(ActionEvent event) {
        tcpClient.cancel();
    }
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            txtRutaDestino.setText(Os.getDetail().getPath());
            setInformation();
            setListMode();
            setTableMode();
            setIP();
            runTcpServer();
            setRequestPool();
            runUdpServer();  
        });
    }    

    
    private void setInformation(){
        buttonSend.setTooltip(new Tooltip("Envia todos los archivos a todas las PC"));
        labelHelp.setTooltip(new Tooltip("Información acerca del programa"));
    }
    
    private void setListMode(){
        listPcConectadas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listPcConectadas.setCellFactory((call) -> new PcCell());
    }
       
    private class PcCell extends ListCell<Pc>{
        @Override
        protected void updateItem(Pc pc, boolean empty){
            super.updateItem(pc , empty);
            if (pc  == null || empty)  
                setGraphic(null);
            else{
                TextFlow textFlow = new TextFlow();
                Text text0 = new Text("HostAddres: "); 
                Text text1 = new Text(pc.getHostAddress());
                Text text2 = new Text("HostName: "); 
                Text text3 = new Text(pc.getHostName());
                if(pc.getIndicator()){
                    text0.setFill(Color.RED);
                    text2.setFill(Color.RED);
                }else{
                    text0.setFill(Color.BLUE);
                    text2.setFill(Color.BLUE);
                }
                Text newLine = new Text(System.lineSeparator());
                textFlow.getChildren().addAll(text0, text1, newLine, text2, text3);
                setGraphic(textFlow);
            }
        } 
    }
    
    private void setTableMode(){
        tableOutputFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        tabOutFile.setCellValueFactory((e) -> Bindings.concat(e.getValue().toString()));
        
        tabOutProgress.setCellValueFactory((e) -> {
            ProgressProperties progress = (ProgressProperties)e.getValue().getProgress();
            return progress.getProgressProperty().asObject();
        });
        
        tabOutProgress.setCellFactory(ProgressBarTableCell.<MyFile>forTableColumn());
        
        tabOutStatus.setCellValueFactory((e) -> {
            ProgressProperties progress = (ProgressProperties)e.getValue().getProgress();
            return progress.getStatusProperty();
        });
        
        
        tableInputFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        tabInFile.setCellValueFactory((e) -> Bindings.concat(e.getValue().toString()));
        
        tabInProgress.setCellValueFactory((e) -> {
            ProgressProperties progress = (ProgressProperties)e.getValue().getProgress();
            return progress.getProgressProperty().asObject();
        });
        
        tabInProgress.setCellFactory(ProgressBarTableCell.forTableColumn());
        
        tabInStatus.setCellValueFactory((e) -> {
            ProgressProperties progress = (ProgressProperties)e.getValue().getProgress();
            return progress.getStatusProperty();
        });
    }
    
    private void setIP(){
        Network n = new Network(listener);
        labelIP.setText(n.getHostAddress());
    }   

    private void runTcpServer() {
        Network n = new Network(listener);
        labelPort.setText(String.valueOf(n.getTcpPort()));
        tcpServer = new TcpServer(listener);  
        tcpServer.runServer();
    }
    
    private void setRequestPool() {
        tcpClient = TcpClient.setRequestPool(listener);
    }
    
    private void runUdpServer() {
        udpServer = new UdpServer(listener);
        udpServer.runServer();
    }

    
    
    public void updateListViewOnExit(){  
        tcpServer.closeServer(); 
        tcpClient.closeRequestPool();
        udpServer.closeServer();
        UdpClient client = new UdpClient(listener);
        //Send request to all servers   
        client.sendRequest("Salida");
    }
    
    private final Listener listener = new Listener(){
        @Override
        public ApplicationThread getApplicationThread() {
            return (runnable) -> Platform.runLater(runnable);
        }

        @Override
        public Attributes getAttributes(){
            return new Attributes(){
                @Override
                public boolean isTransferEnabled() {
                    return checkTransfer.isSelected();
                }

                @Override
                public String getPath() {
                    return txtRutaDestino.getText();
                }
                
                @Override
                public Progress getProgress() {
                    return new ProgressProperties("En espera...", 0.0);
                } 
                
                @Override
                public boolean isChatEnabled() {
                    return checkChat.isSelected();
                }
            };
        }
        
        @Override
        public Controller update() {
            return new Controller(){
                @Override
                public void informationMessage(String message) {
                    FxMessages.getAlert().informationMessage(message);
                }

                @Override
                public void warningMessage(String message) {
                    FxMessages.getAlert().warningMessage(message);
                }

                @Override
                public void errorMessage(String message) {
                    FxMessages.getAlert().errorMessage(message);
                }

                @Override
                public void addInputElement(MyFile myFile) {
                    tableInputFiles.getItems().add(myFile);
                }

                @Override
                public void addPc(Pc pc) {
                    if(labelIP.getText().equals(pc.getHostAddress()) && 
                            labelPort.getText().equals(pc.getPort()))
                        pc.setIndicator(true);
                    listPcConectadas.getItems().add(pc);
                }

                @Override
                public void removePc(Pc pc) {
                    int size = listPcConectadas.getItems().size();
                    for(int i = 0; i < size; i++){
                        if(listPcConectadas.getItems().get(i).getHostAddress().equals(pc.getHostAddress()) &&
                                listPcConectadas.getItems().get(i).getPort().equals(pc.getPort())){
                            listPcConectadas.getItems().remove(i);
                            break;
                        }
                    }
                }

                @Override
                public void updateChat(String chat) {
                    txtChat.appendText(chat);
                }
            };
        }
    };
}