package com.velasteguicorps.p2p_project.Network.Server;

import com.velasteguicorps.p2p_project.Model.Utilities.Os;
import com.velasteguicorps.p2p_project.Model.VO.MyFile;
import com.velasteguicorps.p2p_project.Model.VO.Transmition;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.velasteguicorps.p2p_project.MyController.Listener;
import com.velasteguicorps.p2p_project.Network.Connection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Luis Velastegui
 */
public class TcpServer  extends Server{

    private static final AtomicBoolean OFF = new AtomicBoolean();
    private static final ExecutorService POOL = Executors.newSingleThreadExecutor();
    private static final ExecutorService POOL_CLIENT = Executors.newCachedThreadPool();
    private static ServerSocket serverSocket;
    private static Connection cnn;

    
    public TcpServer(Listener control){
        super(control);
        cnn = new Connection();
        initializeTcpServer();
    }
    
    
    private void initializeTcpServer(){
        try{
            serverSocket = new ServerSocket(getTcpPort());
        }catch(IOException ex){
            control.getApplicationThread().run(() ->
                control.update().errorMessage(
                        "Problemas al inicializar servidor Tcp! \nSolución: Reinicie el programa"));
        }
    }
    
    @Override
    public void runServer(){
        POOL.execute(() -> {
            while(!serverSocket.isClosed()){
                try {    
                    //This Socket will be used and closed in other thread, don't use finally here!
                    Socket clientSocket = serverSocket.accept(); 
                    if(control.getAttributes().isTransferEnabled())
                        POOL_CLIENT.execute(() -> clientRequestProcess(clientSocket)); 
                } catch (IOException ex) {
                    //Do Nothing
                }
            }
        });
    }
    
    private synchronized void clientRequestProcess(final Socket clientSocket){
        MyFile error = null;
        int cont = 0;
        try{
            cnn.createConnection(clientSocket);
            int nFiles = cnn.readInt();
            boolean cancel = false;
            while(cont < nFiles && cancel == false){
                MyFile myFile = (MyFile)cnn.readObject();
                error = myFile;
                myFile.setPath(control.getAttributes().getPath()+Os.getDetail().getSeparator()+myFile.getName());
                myFile.setProgress(control.getAttributes().getProgress());
                addInputElement(myFile);
                cancel = writeFile(myFile);
                error = null;
                cont++;
            }  
        }catch(IOException | ClassNotFoundException ex){
            if(OFF.get() == false)
                setStatus(error, "Conexión perdida");
        }finally{
            cnn.closeConnection();
        }   
    }
    
    private void addInputElement(final MyFile myFile){
        control.getApplicationThread().run(() -> control.update().addInputElement(myFile));
    } 
    
    private boolean writeFile(final MyFile myFile) throws IOException, ClassNotFoundException{
        boolean cancel = false;
        final String path = myFile.getPath();      
        try(BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(createFile(path)))){
            double size = (double)myFile.getLength();
            boolean next = true;
            double tot = 0.0;
            setStatus(myFile, "Descargando");
            while(next == true && OFF.get() == false){
                Transmition transmition = (Transmition)cnn.readObject();
                if(transmition.hasNext()){
                    byte[] bytes = transmition.getBytes();
                    buffer.flush();
                    buffer.write(bytes, 0, bytes.length);
                    tot = tot + (double)bytes.length;
                    upgradeProgress(myFile, tot/size);     
                    cnn.writeBoolean(true);
                }else
                    next = false;
            }
            if(size == tot){
                if(tot < 1.0)
                    upgradeProgress(myFile, 1.0);
                setStatus(myFile, "Descarga completada!");   
            }else{
                cancel = true;
                setStatus(myFile, "Descarga cancelada!");
            }
        }
        return cancel;
    }
    
    private File createFile(final String path) throws IOException{
        File file = new File(path);
        if(!file.exists())
            file.createNewFile();
        else  
            control.getApplicationThread().run(() ->
                control.update().warningMessage("El archivo " + file.getName() + " se sobreescribio!"));
        return file;
    }
    
    private void setStatus(final MyFile myFile, final String state){
        control.getApplicationThread().run(() -> myFile.getProgress().setStatus(state));
    }
    
    private void upgradeProgress(final MyFile myFile, final double progress){
        control.getApplicationThread().run(() -> myFile.getProgress().setProgress(progress));
    }
    
    @Override 
    public void closeServer(){
        OFF.set(true);
        if(serverSocket != null){
            try{
                serverSocket.close();
            }catch(IOException ex){}
        }
        POOL_CLIENT.shutdownNow();
        POOL.shutdownNow();
    }
       
}
