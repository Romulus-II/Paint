/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintbackup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

/**
 *
 * @author G-sta
 */
public class LogFile extends Task{
    
    private File logFile;
    private BufferedWriter logger;
    
    private final String tab = "   ";
    
    private long startTime, stopTime;
    
    private DynamicCanvas canvas;
    
    private int toolSelected = 15000;
    private long toolSelectedTime, toolUnselectedTime;
    
    private String toolName = "";
    private Tool tool;
    
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList timesUsed = new ArrayList();
    private ArrayList duration = new ArrayList();
    //private int[] timesUsed;
    private double timeUsed = 0;
    
    private String fileName;
    
    
    private boolean opened, saved, autosaved;
        
    /**
     * Appends all of the usage data to a separate txt file.
     * @param canvas
     * @throws FileNotFoundException 
     */
    public LogFile(DynamicCanvas canvas) throws FileNotFoundException{
        this.canvas = canvas;
        try {
            logFile = new File("Log.txt");
            logger = new BufferedWriter(new FileWriter(logFile, true));
            
            logger.write(getDate().toString());
            logger.newLine();
            logger.newLine();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            Logger.getLogger(LogFile.class.getName()).log(Level.SEVERE, null, e);
        }
        opened = false;
        saved = false;
        autosaved = false;
        System.out.println("Preparing to log user action...");
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Returns a date representation of today's date.
     * @return 
     */
    private Date getDate(){
        long millis=System.currentTimeMillis();
        Date date = new Date(millis);
        return date;
    }
    
    /**
     * Logs the opening, or saving of a file (i.e. the paint project).
     * @param file
     * @param action 
     */
    public void logFile(String file, String action){
        fileName = file;
        if(action.equals("open"))
        {
            //System.out.println("Preparing to log opening of file...");
            opened = true;
        }
        else if(action.equals("save"))
        {
            saved = true;
        }
        else if(action.equals("autosave"))
        {
            autosaved = true;
        }
    }
    
    /**
     * Writes a summary of the usage of each tool onto the log file.
     */
    public void getSummary(){
        String logistics = new String();
        for(int i = 0; i < name.size(); i++){
            logistics = (name.get(i) + " was used " + timesUsed.get(i) + 
                    " times, for a total duration of " + 
                    getTimeUsed((long) duration.get(i)));
            try {
                logger.write(logistics);
                logger.newLine();
            } catch (IOException ex) {
                Logger.getLogger(LogFile.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    /**
     * Writes the final information and formats the log file for next use.
     * (To be called when thread is stopped).
     * @throws IOException 
     */
    public void publish() throws IOException{
        checkTool();
        logger.newLine();
        //getSummary();
        String divider = "";
        for(int i = 0; i < 150; i++){
            divider = divider + "_";
        }
        logger.write(divider);
        logger.newLine();
        logger.flush();
        logger.close();
    }
    
    /**
     * Returns a string representation of how long the tool was used in minutes and seconds.
     * @param time
     * @return timeUsed
     */
    public String getTimeUsed(long time){
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        //time = Math.round(time);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
        for(int i = 0; i < (int) minutes; i++){
            seconds-=60;
        }
        return (minutes + " minutes and " + seconds + " seconds");
    }
    
    /**
     * Documents all of the information for the tool (name, time used, number
     * of times used).
     * @param t
     * @param time 
     */
    public void generateData(Tool t, long time){
        /*if(name.contains(t.getName())){
            int i = name.indexOf(t.getName());
            duration.set(i, (long) duration.get(i) + time);
            timesUsed.set(i, (int) timesUsed.get(i) + time);
        }else{*/
            name.add(t.getName());
            duration.add(time);
            timesUsed.add(1);
        //}
    }
    
    @Override
    protected Void call() throws Exception {
        int max = 10000000;
        for(int a = 0; a < max; a++){
            if(opened){
                System.out.println("Logged file opening");
                logger.write(tab + "Opened " + fileName + " on " + getDate());
                logger.newLine();
                opened = false;
            }else if(saved){
                System.out.println("Logged file saving");
                logger.write(tab + "Saved " + fileName + " on " + getDate());
                logger.newLine();
                saved = false;
            }else if(autosaved){
                System.out.println("Logged file autosaving");
                logger.write(tab + "Automatically saved " + fileName + " on " + getDate());
                logger.newLine();
                autosaved = false;
            }
            checkTool();       
            Thread.sleep(1000);
            if(a == max-1){
                a = 0;
            }
        }
        return null;
    }
    
    /**
     * Checks to see if the current log tool matches the currently selected tool, 
     * and if no, sets current tool to match selected tool.
     * @throws IOException 
     */
    public void checkTool() throws IOException{
        if(toolSelected != canvas.getSelectedTool()){
            if(toolSelectedTime == 0){
                toolSelected = canvas.getSelectedTool();
                if(toolSelected!=15000){
                    toolSelectedTime = System.currentTimeMillis();
                    toolName = canvas.getTool().getName();
                    tool = canvas.getTool();
                    System.out.println("User switched to " + canvas.getTool().getName());
                }
            }else{
                long timeSelected = (System.currentTimeMillis()-toolSelectedTime);
                System.out.println("Logged Tool Use");
                logger.write(tab + "User used " + toolName + 
                        " for " + getTimeUsed(timeSelected));
                logger.newLine();
                generateData(tool, timeSelected);
                if(canvas.getSelectedTool() == 15000){
                    toolSelectedTime = 0;
                }else{
                    toolSelectedTime = System.currentTimeMillis();
                    toolSelected = canvas.getSelectedTool();
                    tool = canvas.getTool();
                    toolName = canvas.getTool().getName();
                    System.out.println("User switched to " + toolName);
                }
            }
        }
    }
    
}


