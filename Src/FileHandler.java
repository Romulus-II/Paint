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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
    
    private final DynamicCanvas canvas;
    private final Stage window;
    private CustomMenu menu = null;
    
    private LogFile logger;
    
    private Thread thread;
    
    /**
     * This class is used to handle all things relating to files, including but
     * not limited to opening and saving files.
     * @param window
     * @param canvas
     * @param logger
     * @param action
     * @throws IOException 
     */
    public FileHandler(Stage window, DynamicCanvas canvas, LogFile logger, String action) throws IOException{
                
        this.window = window;
        
        this.canvas = canvas;
        
        this.logger = logger;
        thread = new Thread(logger);
        thread.start();
        
        if(action.equals("openPrevious")){
            try{
                File openFile = new File(System.getProperty("user.dir") + File.separator + "Untitled.png");
                Image img = new Image(openFile.toURI().toString());
                canvas.openImage(img);
            }catch(Exception e){
                System.out.println("Couldn't open file on startup");
                e.printStackTrace();
            }
        }
        
        try {
            //dummyFile = new File(".");
            String path = System.getProperty("user.dir");
            fileName = "Untitled";
            dummyFile = new File(path + File.separator + fileName + ".png");
            dummyFile = new File("Untitled.png");
            origExt = getFileExt(dummyFile);
            //ImageIO.write(canvas.screenshot(), getFileExt(dummyFile), dummyFile);
            //System.out.println("File successfully saved as " + dummyFile.toString());
            //System.out.println(getFileName());
        } catch (Exception ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(action.equals("openFromGallery")){
            open();
        }
    }
    /**
     * To be used to allow for the shutdown of menu's thread. Absolutely essential!!
     * @param menu 
     */
    public void setMenu(CustomMenu menu){
        this.menu = menu;
    }
    
    /**
     * Returns the DynamicCanvas Object called in constructor
     * 
     * @return DynamicCanvas
     */
    public DynamicCanvas getDynamicCanvas(){
        return canvas;
    }
    
    /**
     * Makes a FileChooser object with extensions .png, .jgp, and .ico, to be 
     * used with opening from and saving images to a user directory.
     * 
     * @return FileChooser 
     */
    private FileChooser makeFileChooser(){
        FileChooser f = new FileChooser();
        
        //Set extension filter
        f.getExtensionFilters().addAll(
            //one on top is selected 1st
            new FileChooser.ExtensionFilter("PNG Files", "*.png"),
            new FileChooser.ExtensionFilter("JPG Files", "*.jpg"),
            new FileChooser.ExtensionFilter("Icon Files", "*.ico")
        );
        
        return f;
    }
    
    /**
     * Returns file name of the save file path.
     * 
     * @return String
     */
    private String getFileName(){
        String name;
        if(selectedFile!=null){
            name = selectedFile.toString();
        }else{
            name = dummyFile.toString();
        }
        
        //Searches for actually file name from path
        int left_bound = name.lastIndexOf(File.separator) + 1;
        int right_bound = name.indexOf('.');
        
        try{
            return name.substring(left_bound, right_bound);
        } catch (Exception e){
            return name.substring(0, right_bound);
        }
    }
    
    /**
     * Returns the file extension;
     * 
     * @param file
     * @return fileExtension
     */
    public String getFileExt(File file){
        String name = file.toString();
        int leftBound = name.indexOf('.');
        leftBound++;
        return name.substring(leftBound, name.length());
    }
    
    /**
     * Allows user to browse through their directory for an image file.
     * 
     * Selected image file is then added to canvas.
     */
    private void open(){//Find a way to "refresh" application
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
                
                logger.logFile(selectedFile.toString(), "open");

                System.out.println("Opened file " + selectedFile.toString());
            } catch (FileNotFoundException ex) {
                System.out.println("File not found");
            }
        } catch (Exception e){
            System.out.println("User cancelled action");
        }
    }
    
    /**
     * Automatically saves file to user directory;
     */
    public void autosave(){
        
        try {
            ImageIO.write(canvas.screenshot(true), getFileExt(dummyFile), dummyFile);
            
            logger.logFile(dummyFile.toString(), "autosave");

            System.out.println("File automatically saved as " + dummyFile.toString());
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

                logger.logFile(selectedFile.toString(), "save");

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
                    
                    ImageIO.write(canvas.screenshot(true), getFileExt(selectedFile), selectedFile);
                    
                    logger.logFile(selectedFile.toString(), "save");                    

                    System.out.println("File successfully saved as " + selectedFile.toString());
                    canvas.fileSaved = true;
                    userSaved = true;
                    return true;
                } catch (IOException ex) {
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
    
    
    /**
     * Displays a warning about data loss, only if user tries to save file
     * as a different file type.
     */
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
     * to Save, Don't Save, or Cancel. 
     * 
     * If called from exit button, exits program if Save or Don't Save are pressed, 
     * but does nothing if Cancel is pressed.
     * 
     * If called from open button, allows user to open an image if Save or Don't 
     * Save are pressed, but does nothing if Cancel is pressed.
     * 
     * @param attemptToExitApp
     */
    public void showExitWarning(boolean attemptToExitApp){
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

            /*
                Save file and if called from exit will close app; if called 
                from open, will ask your to open an image
            */
            Button save_btn = new Button("Save");
            save_btn.setOnAction((ActionEvent e) -> {
                if(attemptToExitApp){
                    closeWarning(popupwindow, true, false, true);
                }else{
                    closeWarning(popupwindow, true, true, false);
                }
            });

            /*
                Don't save file and if called from exit will close app; if called 
                from open, will ask your to open an image
            */
            Button exit_btn = new Button("Don't Save");                                             
            exit_btn.setOnAction((ActionEvent e) -> {
                if(attemptToExitApp){
                    closeWarning(popupwindow, false, false, true);
                }else{
                    closeWarning(popupwindow, false, true, false);
                }
            });

            //Close popup
            Button cancel_btn = new Button("Cancel");


            ToolBar buttons = new ToolBar();
            buttons.getItems().addAll(save_btn, exit_btn, cancel_btn);
            cancel_btn.setOnAction((ActionEvent e) -> {
                System.out.println("User declined action.");
                popupwindow.close();
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
            if(attemptToExitApp){
                System.out.println("File successfully closed.");
                window.close();
            }else{
                open();
            }
        }
    }
    
    /**
     * Closes popupwindow, and if willSave, saves the project; if openImage,
     * allows user to open an image onto the canvas; if willShutdown,
     * completely shuts down the program.
     * @param popupwindow
     * @param willSave
     * @param openImage
     * @param willShutdown 
     */
    private void closeWarning(Stage popupwindow, boolean willSave, 
            boolean openImage, boolean willShutdown)
    {
        popupwindow.close();
        if(willSave){
            save();
        }
        if(openImage){
            open();
        }
        if(willShutdown){
            shutdown();
        }
    }
    
    /**
     * Closes Primary Stage, as well as stops all running threads.
     */
    public void shutdown(){
        System.out.println("File successfully closed.");
        canvas.packUpTools();
        window.close();
        autosave();
        //menu.scrap();
        thread.stop();
        try {
            logger.publish();
        } catch (IOException ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}


