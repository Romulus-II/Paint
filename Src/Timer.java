/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintbackup;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 *
 * @author G-sta
 */
public class Timer extends Task {

    private FileHandler fh;
    
    private Timeline clock;
    
    private int timesAutoSaved = 0;
    
    
    private int minutes, seconds;
    private StringProperty time;
    
    //Checks if timer is done
    private boolean complete = false;
    
    /**
     * Allows for the program to be able to have a set time interval for autosave.
     * @param fh
     * @param label 
     */
    public Timer(FileHandler fh, Label label) {
        this.fh = fh;
        
        time = new SimpleStringProperty();
        
        minutes = 2;
        seconds = 3;
        
        label.textProperty().bind(time);
    }
    
    /**
     * Returns a StringProperty to be used in the timer's label on MenuBar.
     * @return time StringProperty
     */
    public StringProperty getTime(){return time;}
    
    /**
     * Returns an integer representation of minutes.
     * @return minutes
     */
    public int getMinutes(){return minutes;}
    
    /**
     * Returns an integer representation of seconds.
     * @return 
     */
    public int getSeconds(){return seconds;}

    /**
     * Method to be called when a thread of timer is implemented.
     * @return Nothing.
     * @throws Exception 
     */
    @Override
    protected String call() throws Exception {
        final Duration PROBE_FREQ = Duration.seconds(1);
            
            
        clock = new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent event){
                        time.setValue(tickTock());
                        //label.setText(setTime());
                    }
                }
            ),
            new KeyFrame(
                PROBE_FREQ
            )
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
        return time.getValue();
            
    }
    
    /**
     * Decreases timer by a second and returns formatted string of reamaining time
    **/
    private String tickTock() {
        
        //If timer is done, "reset" timer(add 2 minutes)
        if(complete){
            
            timesAutoSaved++;
            System.out.println("Autosaved " + timesAutoSaved + " times");
            
            fh.autosave();
            
            reset();
            
        }

        if(minutes>=0){
            
            //Decrease timer by 1 second
            if(seconds>0)
            {
                seconds--;
                if(minutes==0 && seconds==0){
                    System.out.println("Timer is complete");
                    complete = true;
                }
            }
            else
            {
                seconds = 59;
                minutes--;

                
            }
            
            //Format string for return value
            String sec;
            if(seconds > 9)
            {
                sec = Integer.toString(seconds);
            }
            else
            {
                sec = ("0" + seconds);
            }
            time.setValue("0" + minutes + ":" + sec);
            
            return ("0" + minutes + ":" + sec);
        }
        else
        {
            System.out.println("Timer is complete");
            complete = true;
        }
        //"Base case" (If something goes terribly wrong)
        return "00:00";
    }
    
    
    /**
     * Reset timer back to starting time (2:00)
     */
    public void reset(){
        minutes = 2;
        seconds = 0;
        complete = false;
    }
        
}