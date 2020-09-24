package com.velasteguicorps.p2p_project.Model.Utilities;

import java.io.File;

/**
 *
 * @author Luis Velastegui
 */
public class Os {
    
    private static Os os;
    private final String name;
    private String path;
    private String separator;
    
    
    private Os(){
        name = System.getProperty("os.name");
        setPath();
    }
            
    
    public static Os getDetail(){
        if(os == null)
            os = new Os();
        return os;
    }
    
    
    private void setPath(){
        if(name.contains("Windows")){
            path = "C:\\FileSharing";
            separator = "\\";
        }
        if(name.contains("Linux") || name.contains("Mac")){
            path = "/home/" + System.getProperty("user.name") + "/FileSharing";
            separator = "/";
        }
        createDirectory();
    }
    
    public String getName(){
        return name;
    }
    
    public String getPath(){
        return path;
    }
    
    public String getSeparator(){
        return separator;
    }
    
    private void createDirectory(){
        File file = new File(path);
        if(!file.exists())
            file.mkdir();
    }
    
}
