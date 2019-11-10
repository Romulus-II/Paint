/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintbackup;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author G-sta
 */
public class CustomTitle {
    
    private final int TITLE_HEIGHT = 15;
    
    private final StackPane title;
    
    private final Stage window;
    private final DynamicCanvas canvas;
    
    /**
     *
     * @param window
     * @param canvas
     */
    public CustomTitle(Stage window, DynamicCanvas canvas){
        this.window = window;
        this.canvas = canvas;
        
        title = new StackPane();
        title.setPrefSize(window.getWidth(), TITLE_HEIGHT);
        
        HBox left = new HBox();
        Button save = new Button();
        customizeButton(save, TITLE_HEIGHT, "/images/save.png");
        Button undo = new Button();
        customizeButton(undo, TITLE_HEIGHT, "/images/undo.png");
        Button redo = new Button();
        customizeButton(redo, TITLE_HEIGHT, "/images/redo.png");
        left.getChildren().addAll(save, undo, redo);
        
        HBox right = new HBox();
    } 
    
    /**
     *
     * @return
     */
    public StackPane getTitle(){
        return title;
    }
    
    /**
     * Defers to the other customizeButton(), using width for both width & height. 
     * @param button
     * @param width
     * @param path 
     */
    private void customizeButton(Button button, int width, String path){
        customizeButton(button, width, width, path);
    }
    /**
     * Adjusts an image's graphics and size;
     * @param button
     * @param width
     * @param height
     * @param path The path of where the image file is located.
     */
    private void customizeButton(Button button, int width, int height, String path){
        button.setPrefSize(TITLE_HEIGHT, TITLE_HEIGHT);
        try{
            Image img = new Image("/images/redo.png", TITLE_HEIGHT, TITLE_HEIGHT, false, false);
            ImageView icon = new ImageView(img);
            button.setGraphic(new ImageView(img));
        }catch(NullPointerException e){
            System.out.println(e + " happened");
        }
        button.setOnAction((ActionEvent event) -> {
            canvas.redo();
        });
    }
}
