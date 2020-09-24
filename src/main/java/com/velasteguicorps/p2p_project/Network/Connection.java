package com.velasteguicorps.p2p_project.Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Luis Velastegui
 */
public class Connection {
    
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    
    public void createConnection(Socket socket) throws IOException{
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }
    
    public void createConnection(String ip, int port) throws IOException{
        socket = new Socket(ip, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    public int readInt() throws IOException{
        return in.readInt();
    }
    
    public boolean readBoolean() throws IOException{
        return in.readBoolean();
    }
    
    public Object readObject() throws IOException, ClassNotFoundException{
        return in.readObject();
    }

    public void writeInt(int value) throws IOException{
        out.writeInt(value);
        out.flush();
    }
    
    public void writeBoolean(boolean value) throws IOException{
        out.writeBoolean(value);
        out.flush();
    }
    
    public void writeObject(Object value) throws IOException{
        out.writeObject(value);
        out.flush();
        out.reset();
    }
  
    public void closeConnection(){
        if(socket != null){
            if(!socket.isClosed()){
                if(out != null){
                    try{
                        out.close();
                    }catch(IOException ex){}
                }
                if(in != null){
                    try{
                        in.close();
                    }catch(IOException ex){}
                }
                if(socket != null){
                    try{
                        socket.close();
                    }catch(IOException ex){}
                }
            }
        }
    }
}
