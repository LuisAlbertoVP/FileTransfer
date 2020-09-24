package com.velasteguicorps.p2p_project.Network.Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import com.velasteguicorps.p2p_project.MyController.Listener;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Luis Velastegui
 */
public class UdpClient extends Client {
    
    private static final Integer PORT = 12345;

    
    public UdpClient(Listener control) {
        super(control);
    }
    
    
    public DatagramSocket sendRequest(final String id, final String ... mensaje) {
        DatagramSocket clientSocket = null;
        try{
            InetAddress address = getBroadcast();
            clientSocket = new DatagramSocket();
            HashMap<String, String> map = new HashMap<>();
            if(id.equals("Mensaje"))
                map.put(id, getHostName() + "\n" + mensaje[0]);
            if(id.equals("PC") || id.equals("Salida"))
                map.put(id, getHostAddress() + "/" + getHostName() + "/" + getTcpPort());
            byte[] array = serialize(map);  
            DatagramPacket paqueteEnviar = new DatagramPacket(array, array.length, address, PORT);
            clientSocket.setBroadcast(true);
            clientSocket.send(paqueteEnviar);
        }catch(IOException ex){
            String error = "";
            switch(id){
                case "Mensaje" : error = "No se pudo enviar mensaje!"; break;
                case "PC" : error = "Error desconocido! \nReinicie el programa"; break;
            }
            final String message = error;
            control.getApplicationThread().run(() ->
                    control.update().errorMessage(message));
        }finally{
            if(!id.equals("PC")){
                if(clientSocket != null)
                    clientSocket.close();
            }
        }
        return clientSocket;
    }

    private byte[] serialize(HashMap<String, String> hash) throws IOException{
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        ObjectOutputStream object = new ObjectOutputStream(array);
        object.writeObject(hash);
        return array.toByteArray();
    }
}
