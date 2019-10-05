/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintbackup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;

/**
 *
 * @author G-sta
 */
public class LogFile{
    
    private final File logFile;
    private final String logLocation = "src/paintbackup/logs.txt";
    
    private Formatter x;
    
    public LogFile() throws FileNotFoundException{
        logFile = new File(logLocation);
        prepLog();
    }
    
    private void prepLog() throws FileNotFoundException{
        x = new Formatter(logFile);
    }
    
    public void log(String logData){
        x.format("%s", logData);   
    }
    
    public void publish(){
        x.close();
    }
    
}
