/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintbackup;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 *
 * @author G-sta
 */
public class Tool extends ToggleButton {
    
    private final double DESC_WIDTH, DESC_HEIGHT;
    private final int BTN_WIDTH = 25;
    private String name;
    
    private boolean nameIsPath = false;
    
    /**
     * Basic Constructor
     */
    public Tool() {
        super();
        DESC_WIDTH = 0;
        DESC_HEIGHT = 0;
    }
    
    /**
     * Creates a tool/toggle button with name inside of it.
     * @param name 
     */
    public Tool(String name){
        super(name);
        this.name = name;
        DESC_WIDTH = 0;
        DESC_HEIGHT = 0;
    }
    
    /**
     * Creates a tool/toggle button with the name inside of it, resized
     * to the supplied width.
     * @param name
     * @param width 
     */
    public Tool(String name, int width){
        super();
        //this.name = name;
        setWidth(width);
        DESC_WIDTH = 0;
        DESC_HEIGHT = 0;
    }
    
    /**
     * Creates a tool/toggle button with its graphics set the image file path,
     * resized to the supplied width.
     * @param width
     * @param path 
     */
    public Tool(int width, String path) {
        super();
        DESC_WIDTH = 0;
        DESC_HEIGHT = 0;
        this.paint(width, path);
        //name = path;
        nameIsPath = true;
    }
    
    /**
     * Sets this tool's name to name.
     * @param name 
     */
    public void setName(String name){
        this.name = name;
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
    
    /**
     * Creates a pop up of the tool's description that pops up on mouse hover.
     * @param window
     * @param desc 
     */
    public void setDescription(Stage window, String desc){
        Popup popup = new Popup();
        this.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                Label label = new Label(desc);
                label.setWrapText(true);
                popup.getContent().add(label);
                popup.getScene().setFill(Color.DARKORANGE);
                popup.setX(getLayoutX() + getWidth() + 350);
                popup.setWidth(DESC_WIDTH);
                popup.setY(getLayoutY() + getHeight() + 75);
                popup.setHeight(DESC_HEIGHT);
                popup.show(window);
            }
        });
        this.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                popup.hide();
            }
        });
    }
            
}
