/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintbackup;

import java.util.ArrayList;
import javafx.geometry.Bounds;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;

/**
 *
 * @author G-sta
 */
public class Tool extends ToggleButton {
    
    //private final double DESC_WIDTH, DESC_HEIGHT;
    private final int BTN_WIDTH = 25;
    private String name;
    
    private boolean nameIsPath = false;
    //private StackPane description;
    
    public Tool() {
        super();
        //DESC_WIDTH = 0;
        //DESC_HEIGHT = 0;
    }
    
    public Tool(String name){
        super(name);
        this.name = name;
    }
    
    public Tool(String name, int width){
        super();
        this.name = name;
        //setWidth(width);
    }
    
    public Tool(int b, String path) {
        super();
        //DESC_WIDTH = 0;
        //DESC_HEIGHT = 0;
        this.paint(b, path);
        name = path;
        nameIsPath = true;
    }
    
    public Tool(boolean isShape) {
        super();
        //description = new StackPane();
        /*if(isShape){
            DESC_WIDTH = 100;
            DESC_HEIGHT = 50;
        }else{
            DESC_WIDTH = 250;
            DESC_HEIGHT = 150;
        }*/
        //description.setPrefSize(DESC_WIDTH, DESC_HEIGHT);
    }
    
    /**
     * Sets the button graphics to image and adjusts the dimensions to width.
     * 
     * @param width
     * @param imagePath 
     */
    public void paint(int width, String imagePath){
        this.setPrefWidth(width);
        this.setPrefHeight(width);
        try{
            Image img = new Image(imagePath, width-5, width-5, false, false);
            ImageView icon = new ImageView(img);
            this.setGraphic(new ImageView(img));
        }catch(NullPointerException e){
            System.out.println(e + " happened");
        }
    }
    
    /**
     * Returns a String of the name of tool.
     * 
     * @return name
     */
    public String getName(){
        if(nameIsPath)
        {
            int rightBound = name.indexOf(".");
            return name.substring(1, rightBound);
        }        
        else
        {
            return name;//.toLowerCase();
        }
        //Find a way to convert '_' in name to spaces (' ')
    }
    
    
    
    
    /*public void setDescription(String desc){
        Popup popup = new Popup();
        popup.getContent().add(description);
        this.hoverProperty().addListener((obs, oldVal, newValue) -> {
            if(newValue){
                Bounds bnds = description.localToScreen(description.getLayoutBounds());
                double x = bnds.getMinX() - (description.getWidth() / 2) + (description.getWidth());
                double y = bnds.getMinY() - description.getHeight();
                popup.show(description, x, y);
            }

        });
    }*/
            
}
