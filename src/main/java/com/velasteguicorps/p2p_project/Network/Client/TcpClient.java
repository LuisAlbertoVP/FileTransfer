package com.velasteguicorps.p2p_project.Network.Client;

import com.velasteguicorps.p2p_project.Model.VO.MyFile;
import com.velasteguicorps.p2p_project.Model.VO.Pc;
import com.velasteguicorps.p2p_project.Model.VO.Transmition;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import com.velasteguicorps.p2p_project.MyController.Listener;
import com.velasteguicorps.p2p_project.Network.Connection;

/**
 *
 * @author Luis Velastegui
 */

public class TcpClient extends Client{
  
    private static final AtomicBoolean OFF = new AtomicBoolean();
    private static final AtomicBoolean CANCEL = new AtomicBoolean();
    private static TcpClient instance;
    private static ExecutorService pool = Executors.newCachedThreadPool();
    private static Connection cnn;
    
    
    private TcpClient(Listener control){
        super(control);
        cnn = new Connection();
    }

    public static TcpClient setRequestPool(Listener control){
        if(instance == null)
            instance = new TcpClient(control);
        return instance;
    }
    
    public void closeRequestPool(){
        OFF.set(true);
        cnn.closeConnection();
        pool.shutdownNow();
    }
    
    public void cancel(){
        CANCEL.set(true);
        pool.shutdownNow();
        pool = Executors.newCachedThreadPool();
    }

    public void sendFiles(final ArrayList<Pc> pcs, final ArrayList<MyFile> listFiles){
        CANCEL.set(false);
        pool.execute(() -> iteratePc(pcs, listFiles));  
    }
    
    private synchronized void iteratePc(final ArrayList<Pc> pcs, final ArrayList<MyFile> listFiles){
        boolean cancel = false;
        int nPc = pcs.size();
        int cont = 0;
        while(cont < nPc && cancel == false){
            Pc pc = pcs.get(cont);
            try {
                cnn.createConnection(pc.getHostAddress(), Integer.parseInt(pc.getPort()));
                cancel = sendFiles(listFiles, pc.getHostAddress());
            }catch(IOException ex) {
                if(OFF.get() == false)
                    errorMessage("Error al conectarse a: " + pc.getHostAddress() + "!");
            }finally{
                cnn.closeConnection();
            }
            cont++;
        }
    }
    
    private boolean sendFiles(final ArrayList<MyFile> listFiles, final String ip) throws IOException{
        boolean cancel = false;
        int nFiles = listFiles.size();
        int cont = 0;
        sendListSize(nFiles);
        while(cont < nFiles && cancel == false){
            MyFile myFile = listFiles.get(cont);
            cancel = sendFile(myFile);
            cont++;
        }
        if(cancel == false){
            if(cont == nFiles)
                informationMessage("Archivos enviados satisfactoriamente a: " + ip);
        }else{
            if((cont - 1) == 1)
                warningMessage("Se ha enviado 1 archivo!");
            else if((cont - 1) > 0)
                warningMessage("Se han enviado " + (cont - 1)  + " archivos!");
        }
        return cancel;
    }
    
    private void sendListSize(final int nFiles) throws IOException{
       cnn.writeInt(nFiles);
    }
    
    private boolean sendFile(final MyFile myFile) throws IOException{
        cnn.writeObject(myFile);
        return sendContentFile(myFile);
    }
    
    private boolean sendContentFile(final MyFile myFile) throws IOException{
        boolean cancel = false;
        try(BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(myFile.getPath()))){
            byte[] bytes = (myFile.getLength()<20000000)?new byte[(int)myFile.getLength()]:new byte[20000000];
            double size = (double)myFile.getLength();
            boolean loop = true;
            int length;
            double tot = 0.0;
            setStatus(myFile, "En cola");
            Transmition transmition = myFile.getTrasmition();
            while((length = buffer.read(bytes, 0, bytes.length)) != -1 && 
                    loop == true && CANCEL.get() == false)
            {
                byte[] chunk = new byte[length];
                for(int i =0; i< chunk.length;i++)
                    chunk[i] = bytes[i];
                cnn.writeObject(createTransmition(transmition, true, chunk));
                loop = cnn.readBoolean();
                tot = tot + (double)length;
                upgradeProgress(myFile, tot/size);
            }
            cnn.writeObject(createTransmition(transmition, false, null));
            if(size == tot){
                if(tot < 1.0)
                    upgradeProgress(myFile, 1.0);
                setStatus(myFile, "Archivo enviado!");
            }else{
                cancel = true;
                setStatus(myFile, "Cancelado!");     
            }
        }
        return cancel;
    } 
    
    private Transmition createTransmition(Transmition transmition, boolean next, byte[] bytes){
        transmition.setHasNext(next);
        transmition.setBytes(bytes);
        return transmition;
    }
    
    private void setStatus(final MyFile myFile, final String state){
        control.getApplicationThread().run(() -> myFile.getProgress().setStatus(state));
    }
    
    private void upgradeProgress(final MyFile myFile, final double progress) {
        control.getApplicationThread().run(() -> myFile.getProgress().setProgress(progress));
    }
    
    private void informationMessage(final String message){
        control.getApplicationThread().run(() -> control.update().informationMessage(message));
    }
    
    private void warningMessage(final String message){
        control.getApplicationThread().run(() -> control.update().warningMessage(message));
    }
    
    private void errorMessage(final String message){
        control.getApplicationThread().run(() -> control.update().errorMessage(message));
    }
    
}
