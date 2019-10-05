/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintbackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author G-sta
 */

public class FileHandler {
    
    private String fileName;
    private File selectedFile, dummyFile;
    private String origExt;
    
    //Check if the user clicked saveAs
    private boolean userSaved = false;
    
    private boolean imageLoaded = false;
    
    //Test variable to be used for if user changes datatypes
    boolean userSaidYes = false;
    
    
    private final DynamicCanvas canvas;
    private final Stage window;
    
    private final LogFile logger;
    
    public FileHandler(Stage window, DynamicCanvas canvas, LogFile logger){
        
        this.logger = logger;
        
        this.window = window;
        
        this.canvas = canvas;
        
        try {
            //dummyFile = new File(".");
            String path = System.getProperty("user.dir");
            fileName = "Untitled";
            dummyFile = new File(path + File.separator + fileName + ".png");
            //dummyFile = new File("Untitled.png");
            origExt = getFileExt(dummyFile);
            ImageIO.write(canvas.screenshot(false), getFileExt(dummyFile), dummyFile);
            System.out.println("File successfully saved as " + dummyFile.toString());
            //System.out.println(getFileName());
        } catch (IOException ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public LogFile getLogFile(){
        return logger;
    }
    public void close(){
        logger.publish();
    }
    
    /**
     * Returns the DynamicCanvas Object called in constructor
     * 
     * @return DynamicCanvas
     */
    public DynamicCanvas getDynamicCanvas(){
        return canvas;
    }
    
    private FileChooser makeFileChooser(){
        FileChooser f = new FileChooser();
        
        //Set extension filter
        f.getExtensionFilters().addAll(
            //one on top is selected 1st
            new FileChooser.ExtensionFilter("PNG Files", "*.png"),
            new FileChooser.ExtensionFilter("JPG Files", "*.jpg"),
            new FileChooser.ExtensionFilter("Icon Files", "*.ico"),
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        
        return f;
    }
    
    /**
     * Returns file name of the save file path.
     * 
     * @return String
     */
    private String getFileName(){
        String fileName = selectedFile.toString();
        
        //Searches for actually file name from path
        int left_bound = fileName.lastIndexOf(File.separator) + 1;
        int right_bound = fileName.indexOf('.');
        
        try{
            return fileName.substring(left_bound, right_bound);
        } catch (Exception e){
            return fileName.substring(0, right_bound);
        }
    }
    
    /**
     * Returns the file extension;
     * 
     * @param file
     * @return fileExtension
     */
    public String getFileExt(File file){
        String fileName = file.toString();
        int leftBound = fileName.indexOf('.');
        leftBound++;
        return fileName.substring(leftBound, fileName.length());
    }
    
    /**
     * Allows user to browse through their directory for an image file.
     * 
     * Selected image file is then added to canvas.
     * 
     * @return opened
     */
    public boolean open(){//Find a way to "refresh" application
        try{
            FileChooser fileChooser = makeFileChooser();

            //Show open file dialog
            selectedFile = fileChooser.showOpenDialog(window);
            try {
                FileInputStream inputStream = new FileInputStream(selectedFile);
                Image image = new Image(inputStream);                    

                origExt = getFileExt(selectedFile);
                
                canvas.openImage(image);

                imageLoaded = true;
                logger.log("User opened file " + selectedFile.toString());
                System.out.println("Opened file " + selectedFile.toString());
                
                return true;
            } catch (Exception ex) {
                System.out.println("File not found");
                return false;
            }
        } catch (Exception e){
            System.out.println("User cancelled action");
            return false;
        }
    }
    
    /**
     * Automatically saves file to user directory;
     */
    public void autosave(){
        
        try {
            ImageIO.write(canvas.screenshot(false), getFileExt(dummyFile), dummyFile);
            logger.log("Automatically saved file as " + selectedFile.toString());
            System.out.println("File automatically saved as " + dummyFile.toString());
            //canvas.fileSaved = true;
        } catch (IOException ex) {
            Logger.getLogger(PaintBackUp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Could not sutomatically save file");
        }
    }
    
    /**
     * If user has already assigned file name, automatically saves file to 
     * user directory. Else, defers to saveAs();
     * 
     * @return saved
     */
    public boolean save(){
        if(userSaved){
            try {
                ImageIO.write(canvas.screenshot(true), getFileExt(selectedFile), selectedFile);
                logger.log("Saved file as " + selectedFile.toString());
                System.out.println("File successfully saved as " + selectedFile.toString());
                canvas.fileSaved = true;
                return true;
            } catch (IOException ex) {
                Logger.getLogger(PaintBackUp.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Could not save file; file selected, but crashed");
                return false;
            }
        }else{
            System.out.println("No file selected");
            //If there file hasn't been saved or loaded, defers 
            //to the saveAs function to choose a directory
            return saveAs();
        }
    }
    
    /**
     * Allows user to assign current project a name and choose a save 
     * location in their directory.
     * 
     * Project is then saved to user directory.
     * 
     * @return saved
     */
    protected boolean saveAs(){
        try{
            FileChooser fileChooser = makeFileChooser();
            
            //Show save file dialog (user's input)
            File tempFile = fileChooser.showSaveDialog(window);
            System.out.println(tempFile.toString());
            //If the user choose a file
            if(tempFile != null)
            {
                try {
                    if(imageLoaded)
                    {
                        if(origExt.equals(getFileExt(tempFile)))
                        {
                            selectedFile = tempFile;
                        }
                        else
                        {
                            showAlterationWarning();
                            selectedFile = tempFile;
                        }
                    }
                    selectedFile = tempFile;
                    
                    ImageIO.write(canvas.screenshot(false), getFileExt(selectedFile), selectedFile);
                    logger.log("User saved file as " + selectedFile.toString());
                    System.out.println("File successfully saved as " + selectedFile.toString());
                    canvas.fileSaved = true;
                    return true;
                } catch (Exception ex) {
                    Logger.getLogger(PaintBackUp.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Could not save file");
                    return false;
                }
            }else{
                return false;
            }
        }catch (Exception e){
            System.out.println("User cancelled action.");
            return false;
        }
    }
    
    
    //If user saves file with a dif ext, show popup warning
    private void showAlterationWarning(){
        try{
            final int W = 300, H = 250;
            
            Stage popupwindow=new Stage();
            popupwindow.initModality(Modality.APPLICATION_MODAL);
            popupwindow.setTitle("About Paint(t)");
            
            Label label = new Label("Warning, image features/data loss"
                    + " may occur");
            label.setWrapText(true);

            Button close = new Button("OK");
            close.setOnAction((ActionEvent e) -> {
                //userSaidYes = true;
                popupwindow.close();
            });

            HBox buttons = new HBox();
            buttons.getChildren().addAll(close);
            buttons.setAlignment(Pos.CENTER);
            
            VBox layout= new VBox();
            

            layout.getChildren().addAll(label, buttons);
            layout.setAlignment(Pos.CENTER);

            Scene scene1= new Scene(layout, W, H);
            popupwindow.setScene(scene1);
            popupwindow.show();

        } catch (Exception ex) {
            Logger.getLogger(PaintBackUp.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }

    
    /**
     * Shows a pop up if program is unsaved, and asks user if they would like 
     * to Save, Don't Save, or Cancel 
     * 
     * Exits program if Save or Don't Save are pressed, but does nothing if 
     * Cancel is pressed
     */
    public void exitApp(){
        if(!canvas.fileSaved)
        {               
            Stage popupwindow=new Stage();

            popupwindow.initModality(Modality.APPLICATION_MODAL);
            popupwindow.setTitle("Warning!");

            final int WIDTH = 300;
            final int HEIGHT = 150;

            Label label = new Label("Do you want to save changes to " + getFileName() + "?");
            label.setPrefHeight(HEIGHT-50);
            label.setWrapText(true);

            //Save file and close popup and program
            Button save_btn = new Button("Save");
            save_btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    save();
                    System.out.println("File successfully closed.");
                    popupwindow.close();
                    window.close();
                }
            });

            //Close popup and program
            Button exit_btn = new Button("Don't Save");                                             
            exit_btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    System.out.println("File successfully closed.");
                    popupwindow.close();
                    window.close();
                }
            });

            //Close popup
            Button cancel_btn = new Button("Cancel");


            ToolBar buttons = new ToolBar();
            buttons.getItems().addAll(save_btn, exit_btn, cancel_btn);
            cancel_btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    System.out.println("User declined action.");
                    popupwindow.close();
                }
            });
            buttons.setPrefWidth(WIDTH);
            buttons.setPrefHeight(50);

            VBox layout= new VBox();

            layout.getChildren().add(label);
            layout.getChildren().add(buttons);
            layout.setAlignment(Pos.CENTER);

            Scene scene1= new Scene(layout, WIDTH, HEIGHT);
            popupwindow.setScene(scene1);
            popupwindow.showAndWait();
        }else{
            System.out.println("File successfully closed.");
            window.close();
        }
    }
}

