/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintbackup;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author Gabriel Fragoso
 */
public class PaintBackUp extends Application{// implements EventHandler<ActionEvent> {

    /**
     *
     */
    private double xOffset = 0; 
    private double yOffset = 0;
    
    protected Stage window;
    private final int WIN_WIDTH = 1050, WIN_HEIGHT = 700;
    private final double CANVAS_WIDTH = WIN_WIDTH-250, CANVAS_HEIGHT = WIN_HEIGHT-200;
            
    private Scene scene;
    private ScrollPane scrollPane, canvasSP;
    //private StackPane root;   
    private VBox root;
    
    private VBox main;   
    
    private DynamicCanvas canvas;
    
    private CustomMenu menu;
    private CustomToolBar toolBar;
        
    private FileHandler fh;
    private LogFile logger;
    private Thread thread;
    
    @Override
    public void start(Stage primaryStage) throws FileNotFoundException, IOException{
        Stage opener = new Stage();
        
        final int WIDTH = 500, HEIGHT = 250;
                
        Button[] button = new Button[3];
        
        button[0] = new Button();
        try{
            Image img = new Image("/images/new_proj.png", WIDTH/4, HEIGHT/2, false, false);
            button[0].setGraphic(new ImageView(img));
        }catch(NullPointerException e){
            System.out.println(e + " happened");
        }
        button[0].setOnAction((ActionEvent event) -> {
            try {
                openProject(primaryStage, false, false);
            } catch (IOException ex) {
                Logger.getLogger(PaintBackUp.class.getName()).log(Level.SEVERE, null, ex);
            }
            opener.close();
        });
        button[1] = new Button();
        try{
            Image img = new Image("/images/open_prev.png", WIDTH/4, HEIGHT/2, false, false);
            button[1].setGraphic(new ImageView(img));
        }catch(NullPointerException e){
            System.out.println(e + " happened");
        }
        button[1].setOnAction((ActionEvent event) -> {
            try {
                openProject(primaryStage, true, false);
            } catch (IOException ex) {
                Logger.getLogger(PaintBackUp.class.getName()).log(Level.SEVERE, null, ex);
            }
            opener.close();
        });
        
        button[2] = new Button();
        button[2].setOnAction((ActionEvent event) -> {
            try {
                openProject(primaryStage, false, true);
            } catch (IOException ex) {
                Logger.getLogger(PaintBackUp.class.getName()).log(Level.SEVERE, null, ex);
            }
            opener.close();
        });
        try{
            Image img = new Image("/images/from_gallery2.png", WIDTH/4, HEIGHT/2, false, false);
            button[2].setGraphic(new ImageView(img));
        }catch(NullPointerException e){
            System.out.println(e + " happened");
        }
        
        HBox buttons = new HBox();
        buttons.setStyle("-fx-background-color: #008080");
        buttons.setAlignment(Pos.CENTER);
        for(int i = 0; i < button.length; i++){
            button[i].setPrefSize(WIDTH/4, HEIGHT/2);
            button[i].setAlignment(Pos.CENTER);
        }
        
        Label[] label = new Label[3];
        label[0] = new Label("New Project");
        label[1] = new Label("Previous");
        label[2] = new Label("My Gallery");
        
        HBox labels = new HBox();
        labels.setStyle("-fx-background-color: #008080");
        labels.setAlignment(Pos.CENTER);
        for(int i = 0; i < label.length; i++){
            label[i].setPrefWidth(WIDTH/4);
            label[i].setFont(new Font("Verdana", 20));
            label[i].setAlignment(Pos.CENTER);
            label[i].wrapTextProperty().setValue(true);
            label[i].setTextFill(Color.WHITE);
        }
        
        
        HBox main = new HBox();
        main.setStyle("-fx-background-color: #008080");
        main.setAlignment(Pos.CENTER);
        VBox[] place = new VBox[3];
        
        for(int i = 0; i < place.length; i++){
            place[i] = new VBox();
            place[i].getChildren().addAll(button[i], label[i]);
            main.getChildren().add(place[i]);
        }  
        
                
        Scene scene = new Scene(main, WIDTH, HEIGHT, Color.DARKGRAY);
        
        opener.setScene(scene);
        opener.show();
    }
      
    
    /**
     * Main Project Run
     * 
     * @param primaryStage
     * @param openingProj
     * @param openingFromGallery
     * @throws MalformedURLException
     * @throws IOException
     * @throws IOException 
     */
    private void openProject(Stage primaryStage, boolean openingProj, boolean openingFromGallery) 
            throws MalformedURLException, IOException, IOException 
    {
     
        final double TOOLS_HEIGHT = WIN_HEIGHT-CANVAS_HEIGHT-150;
        
        window = primaryStage;
        //window.initStyle(StageStyle.UNDECORATED);
        window.setTitle("Pain(t)!");
         
        GetToolDescriptions toolDescriptions = new GetToolDescriptions();
        
        //canvas
        canvas = new DynamicCanvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        
       // canvasSP = new ScrollPane(canvas.getPane());
        
        logger = new LogFile(canvas);
        /*thread = new Thread(logger);
        thread.start();*/
        
        
        if(openingProj){
            fh = new FileHandler(window, canvas, logger, "openPrevious");
        }else if(openingFromGallery){
            fh = new FileHandler(window, canvas, logger, "openFromGallery");
        }else{
            fh = new FileHandler(window, canvas, logger, "");
        }
        
        
        menu = new CustomMenu(fh);
        //HBox menu = tempMenu.getMenuBars();
        menu.prefWidthProperty().bind(window.widthProperty());
        menu.setStyle("-fx-background-color: #008080");
        menu.setStyle("-fx-text-color: #ffffff");
        main = new VBox();        
        main.getChildren().addAll(menu);
        //main.getChildren().addAll(fancyMenu);
       
        
        
        //ToolBar
        toolBar = new CustomToolBar(window, canvas, toolDescriptions);
        toolBar.prefWidthProperty().bind(window.widthProperty());
        toolBar.setStyle("-fx-background-color: #00ced1");
        
        //canvasSP = new ScrollPane(canvas.getPane());
        //canvasSP.set
        //canvasSP.setPrefWidth(canvas.getPane().getWidth());
        //canvasSP.setPrefHeight(canvas.getPane().getHeight());
      
        main.getChildren().addAll(toolBar);//, canvas.getPane());
        //main.getChildren().addAll(toolBar, canvasSP);
        
        
        scrollPane = new ScrollPane(canvas.getPane());
        scrollPane.setPrefHeight(WIN_HEIGHT-TOOLS_HEIGHT);
        
        
        CustomTitle title = new CustomTitle(window, canvas);
        //main.getChildren().add(title.getTitle());
        
        title.getTitle().setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        title.getTitle().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                window.setX(event.getScreenX() - xOffset);
                window.setY(event.getScreenY() - yOffset);
            }
        });
        
        
        root = new VBox();
        
        StackPane content = new StackPane(scrollPane);
        //root = new StackPane(main);
        root.getChildren().addAll(main, content);
        
        //scene
        scene = new Scene(root, WIN_WIDTH, WIN_HEIGHT, Color.WHITE);
        
        window.setScene(scene);    
        window.show();
        
        
        window.setOnCloseRequest(e -> {
            try {
                shutdown();
            } catch (IOException ex) {
                Logger.getLogger(PaintBackUp.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
    }
   
    /**
     * Closes main window and stops all running threads.
     */
    private void shutdown() throws IOException{
        fh.shutdown();
    }
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}