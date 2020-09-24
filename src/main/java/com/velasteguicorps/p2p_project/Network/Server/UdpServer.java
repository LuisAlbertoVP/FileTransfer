package com.velasteguicorps.p2p_project.Network.Server;

import com.velasteguicorps.p2p_project.Model.VO.Pc;
import com.velasteguicorps.p2p_project.Network.Client.UdpClient;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.velasteguicorps.p2p_project.MyController.Listener;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 *
 * @author Luis Velastegui
 */
public class UdpServer extends Server{

    private final ExecutorService pool;
    private DatagramSocket serverSocket;
    private DatagramSocket clientSocket;
    
    
    public UdpServer(Listener control) {
        super(control);
        initializeUdpServer();
        pool = Executors.newFixedThreadPool(2);
    }
    
    
    private void initializeUdpServer(){
        try{
            serverSocket = new DatagramSocket(null); 
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(getUdpPort())); 
        }
        catch(SocketException ex) {
           control.getApplicationThread().run(() ->
                control.update().errorMessage(
                        "Problemas al inicializar servidor Udp! \nSolución: Reinicie el programa"));
        }
    }
    
    @Override
    public void runServer() {
        //Listen new requests
        pool.execute(() -> {
            while(!serverSocket.isClosed()){
                try{
                    byte datosRecivir[] = new byte[1024];
                    DatagramPacket paqueteRecibir = new DatagramPacket(datosRecivir, datosRecivir.length);
                    serverSocket.receive(paqueteRecibir);
                    clientRequestProcess(paqueteRecibir, datosRecivir);
                }catch(IOException ex) {
                    if(!serverSocket.isClosed())
                        control.getApplicationThread().run(() ->
                            control.update().errorMessage("Problemas al escuchar a un cliente!"));
                }
            }
        });
        //Notify to pc the connection
        pool.execute(() -> sendFirstRequest(new UdpClient(control)));
    }
    
    @Override 
    public void closeServer(){
        if(clientSocket != null)
            clientSocket.close();
        if(serverSocket != null)
            serverSocket.close();
        pool.shutdownNow();
    }
    
    private void clientRequestProcess(final DatagramPacket paqueteRecibir, final byte [] datosRecivir){
        try{
            HashMap<String, String> map = deserialize(datosRecivir);
            if(map.containsKey("PC")){
                addPc(map.get("PC"));
                String data = getHostAddress() + "/" + getHostName() + "/" + getTcpPort(); 
                byte[] datosEnviar = data.getBytes();
                paqueteRecibir.setData(datosEnviar);
                serverSocket.send(paqueteRecibir);
            }
            if(map.containsKey("Mensaje") && control.getAttributes().isChatEnabled())
                updateChat(map.get("Mensaje"));
            if(map.containsKey("Salida"))
                removePc(map.get("Salida"));
        }catch(IOException | ClassNotFoundException ex) {
            control.getApplicationThread().run(() ->
                control.update().errorMessage("Problemas al procesar solicitudes!"));
        }
    } 
    
    private HashMap<String, String> deserialize(byte[] bytes) throws IOException, ClassNotFoundException{
        ByteArrayInputStream array = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(array);
        return (HashMap<String, String>)in.readObject();
    }
    
    private void addPc(final String datos){
        control.getApplicationThread().run(() -> {
            String [] array = datos.trim().split("/");
            Pc pc = new Pc();
            pc.setHostAddress(array[0]);
            pc.setHostName(array[1]);
            pc.setPort(array[2]);
            control.update().addPc(pc);
        });
    }
    
    private void updateChat(final String datos){
        control.getApplicationThread().run(() -> control.update().updateChat(datos.trim() + "\n" + 
                "---------------------------------------" + "\n"));
    }
    
    private void removePc(final String datos){
        control.getApplicationThread().run(() -> {
            String[] array = datos.trim().split("/");
            Pc pc = new Pc();
            pc.setHostAddress(array[0]);
            pc.setHostName(array[1]);
            pc.setPort(array[2]);
            control.update().removePc(pc);
        });
    }
    
    private void sendFirstRequest(final UdpClient client){
        try{  
            String data = getHostAddress() + "/" + getHostName() + "/" + getTcpPort(); 
            //Send request to all servers
            clientSocket = client.sendRequest("PC");
            //Listen requests from all servers 
            while(!clientSocket.isClosed()){
                byte datosRecivir[] = new byte[1024];
                DatagramPacket paqueteRecibir = new DatagramPacket(datosRecivir, datosRecivir.length);
                clientSocket.receive(paqueteRecibir);
                String msj = new String(datosRecivir).trim(); 
                if(!data.equals(msj)){
                    String [] array = msj.trim().split("/");
                    Pc pc = new Pc();
                    pc.setHostAddress(array[0]);
                    pc.setHostName(array[1]);
                    pc.setPort(array[2]);
                    control.getApplicationThread().run(() -> {
                        control.update().addPc(pc);
                    });
                }
            }
        }catch(IOException | NullPointerException ex){
            if(!clientSocket.isClosed())
                control.getApplicationThread().run(() ->
                    control.update().errorMessage("Error al conectarse a una PC"));
        }
    }
}
