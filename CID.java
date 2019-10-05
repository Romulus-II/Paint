/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintbackup;

import javafx.scene.effect.ColorInput; 
import javafx.scene.paint.Color;

/**
 *
 * @author G-sta
 */
public class CID {
    
    private String id;
    
    public CID(Color inputColor){       
        double blueV = inputColor.getBlue() * 255;
        double redV = inputColor.getRed() * 255;
        double greenV = inputColor.getGreen() * 255;
        if (redV == blueV && blueV == greenV && redV < 78) {
            setColorName("BLACK");
        } else if (redV == blueV && blueV == greenV && redV < 205) {
            setColorName("Gray");
        } else if (redV == blueV && blueV == greenV && redV <= 255) {
            setColorName("WHITE");
        } else if (redV > blueV && redV > greenV) {
            setColorName("RED");
        } else if (blueV > redV && blueV > greenV) {
            setColorName("BLUE");
        } else if (greenV > blueV && greenV > redV) {
            setColorName("GREEN");
        } else if (greenV == redV) {
            setColorName("YELLOW");
        } else if (redV == blueV) {
            setColorName("PURPLE");
        } else if (greenV == blueV) {
            setColorName("TEAL");
        } else {
            setColorName("Color Not Found");
        }
    }
    
    
    
    private void setColorName(String name) {
        id = name;
    }
    
    public String getID(){
        return id;
    }
    
    
    
}
