/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintbackup;

import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author Gabriel Fragoso
 */
public class PaintBackUp extends Application implements EventHandler<ActionEvent> {
    
    protected Stage window;
    protected Stage popupwindow;
    final int WIN_WIDTH = 1050, WIN_HEIGHT = 700;
    final double CANVAS_WIDTH = WIN_WIDTH-250, CANVAS_HEIGHT = WIN_HEIGHT-200;
            
    Scene scene;
    ScrollPane scrollPane, canvasSP;
    StackPane root;   
    
    VBox main;   
    
    DynamicCanvas canvas;
    
    CustomMenu menu;
    CustomToolBar toolBar;
    
    MenuButton widthPicker;
   
    HBox fancyMenu = new HBox(); 
    
    String file_name = "Untitled";
    File selected_file;
    boolean changes_made = false;//Might remove
    boolean image_loaded = false;
    boolean file_saved = false;
    
    @Override
    public void start(Stage primaryStage) throws MalformedURLException, IOException, IOException {
        window = primaryStage;
        window.setTitle("Pain(t)!");
         
        //Menu
        //fancyMenu.setStyle("-fx-background-color: #008080");
        //fancyMenu.setStyle("-fx-text-color: #ffffff");
        
        
        //canvas
        canvas = new DynamicCanvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        
       // canvasSP = new ScrollPane(canvas.getPane());
        
        LogFile logger = new LogFile();
        
        FileHandler fh = new FileHandler(window, canvas, logger);
        
        
        menu = new CustomMenu(fh);
        //HBox menu = tempMenu.getMenuBars();
        menu.prefWidthProperty().bind(window.widthProperty());
        menu.setStyle("-fx-background-color: #008080");
        menu.setStyle("-fx-text-color: #ffffff");
        main = new VBox();        
        main.getChildren().addAll(menu);
        //main.getChildren().addAll(fancyMenu);
       
        
        
        //ToolBar
        toolBar = new CustomToolBar(canvas);
        toolBar.prefWidthProperty().bind(window.widthProperty());
        toolBar.setStyle("-fx-background-color: #00ced1");
        
        toolBar.width[0].setOnAction(this);
        toolBar.width[1].setOnAction(this);
        toolBar.width[2].setOnAction(this);
        toolBar.width[3].setOnAction(this);
        
     
        //canvasSP = new ScrollPane(canvas.getPane());
        //canvasSP.setPrefSize(canvas.getPane().getWidth(), canvas.getPane().getHeight());
      
      
        main.getChildren().addAll(toolBar, canvas.getPane());
        //main.getChildren().addAll(toolBar, canvasSP);
        
        
        scrollPane = new ScrollPane(main);
        
        root = new StackPane(scrollPane);
        //root = new StackPane(main);
        
        //scene
        scene = new Scene(root, WIN_WIDTH, WIN_HEIGHT, Color.WHITE);
        
        window.setScene(scene);    
        
        window.show();
    }

    @Override
    public void handle(ActionEvent event) {
        if(event.getSource()==toolBar.width[0]){
            canvas.setLineWidth(1);
        }else if(event.getSource()==toolBar.width[1]){
            canvas.setLineWidth(3);
        }else if(event.getSource()==toolBar.width[2]){
            canvas.setLineWidth(5);
        }else if(event.getSource()==toolBar.width[3]){
            canvas.setLineWidth(8);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}