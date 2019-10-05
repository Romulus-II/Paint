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
public class Timer{

    private FileHandler fh;
    private final Clock clock;
    
    
    private int timesAutoSaved = 0;
    
    
    private int minutes, seconds;
    private StringProperty time;
    
    //Checks if timer is done
    private boolean complete = false;
    

    public Timer(FileHandler fh, Label label) {
        this.fh = fh;
        
        time = new SimpleStringProperty();
        
        minutes = 2;
        seconds = 3;
        
        clock = new Clock();
        
        label.textProperty().bind(time);
    }
    
    public int getMinutes(){return minutes;}
    
    public int getSeconds(){return seconds;}
            
    class Clock{
        
        private Timeline clock;
        
        private Clock() {
            
            final Duration PROBE_FREQ = Duration.seconds(1);
            
            //thread = new Thread(this);
            
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
        }    
        
        
    }
    
    //Decreases timer by a second and returns formatted string of reamaining time
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
