package paintbackup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author G-sta
 */
public class CustomMenu extends MenuBar{
    
    private final MenuItem openFile, save, saveAs, exitApp, zoomIn, zoomOut;
    private final MenuItem about, releaseNotes, toolDescription;
    
    private DynamicCanvas canvas;
    
    private final FileHandler fh;
    
    private boolean timerShown = true;
    
    private final Thread thread;
    
    /**
     * Stops the logging thread
     */
    public void scrap(){thread.stop();}
    
    /**
     * Creates a menu which acts as a mediator, allowing the user to interact
     * with the file and project.
     * @param fh 
     */
    public CustomMenu(FileHandler fh){
        this.fh = fh;
        
        canvas = fh.getDynamicCanvas();

    //Autosave timer
        Label time = new Label();
        Timer timer = new Timer(fh, time);
        thread = new Thread(timer);
        time.textProperty().bind(timer.getTime());
        Menu clock = new Menu("", time);
        
        Menu fill = new Menu();
        fill.setDisable(true);
        
    //File
        Menu file = new Menu("File");
        openFile = new MenuItem("Open");
        save = new MenuItem("Save");
        saveAs = new MenuItem("Save As");
        exitApp = new MenuItem("Exit");
        file.getItems().addAll(openFile, save, saveAs,exitApp);
        
        openFile.setOnAction((ActionEvent event) -> {
            fh.showExitWarning(false);
        });
        openFile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        save.setOnAction((ActionEvent event) -> {
              boolean cond = fh.save();
            if(cond){timer.reset();}
        });
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveAs.setOnAction((ActionEvent event) -> {
            boolean cond = fh.saveAs();
            if(cond){timer.reset();}
        });
        saveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.ALT_DOWN,
                KeyCombination.CONTROL_DOWN));
        exitApp.setOnAction((ActionEvent event) -> {
            fh.showExitWarning(true);
        });
        exitApp.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
       
    //View
        Menu view = new Menu("View");
        //Add a resetZoom
        MenuItem showTime = new MenuItem("Hide Timer");
        
        zoomIn = new MenuItem("Zoom In");
        zoomOut = new MenuItem("Zoom Out");
        view.getItems().addAll(showTime, zoomIn, zoomOut);
        
        zoomIn.setOnAction((ActionEvent event) -> {
            canvas.zoomIn();
        });
        zoomIn.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        zoomOut.setOnAction((ActionEvent event) -> {
            canvas.zoomOut();
        });
        zoomOut.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.ALT_DOWN));
        
    //Help 
        Menu help = new Menu("Help");
        about = new MenuItem("About");
        about.setOnAction((ActionEvent event) -> {
            showAbout();
        });
        releaseNotes = new MenuItem("Release Notes");
        releaseNotes.setOnAction((ActionEvent event) -> {
            showReleaseNotes();
        });
        toolDescription = new MenuItem("Tool Description");
        toolDescription.setOnAction((ActionEvent event) -> {
            showToolDescriptions();
        });
        help.getItems().addAll(about, releaseNotes, toolDescription);
        
        
        getMenus().addAll(file, view, help, fill, clock);
        
        showTime.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                if(timerShown){
                    getMenus().remove(clock);
                    showTime.setText("Show Timer");
                    timerShown = false;
                }else{
                    getMenus().add(clock);
                    showTime.setText("Hide Timer");
                    timerShown = true;
                }
            }
        });
        
        //Starts the timer thread
        thread.start();
        
    }
    
    
    /**
     * Shows a basic description of this application.
     */
    private void showAbout(){
        try{
            Stage popupwindow=new Stage();
            popupwindow.initModality(Modality.APPLICATION_MODAL);
            popupwindow.setTitle("About Paint(t)");
            
            Label title = new Label("Pain(t)");
            Label label = new Label("Version 0.4.0");
            Label desc = new Label("This is a form of torture inflicted "
                    + "upon CS students at Valparaiso University.");
            desc.setWrapText(true);

            Button close = new Button("Ok");
            close.setOnAction((ActionEvent e) -> {
                popupwindow.close();
            });

            VBox layout= new VBox();

            layout.getChildren().addAll(title, label, desc, close);
            layout.setAlignment(Pos.CENTER);

            Scene scene1= new Scene(layout, 300, 250);
            popupwindow.setScene(scene1);
            popupwindow.show();

        } catch (Exception ex) {
            Logger.getLogger(PaintBackUp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Opens a pop up containing the release notes.
     */
    private void showReleaseNotes(){
        try{
            final int WIDTH = 550;
            final int HEIGHT = 500;
            
            //Read in Release Notes and apply to label
            File help_file = new File("Release_Notes.txt");
            StringBuffer contents = new StringBuffer();
            BufferedReader reader = null;

            reader = new BufferedReader(new FileReader(help_file));
            String text = null;
            // repeat until all lines is read
            while ((text = reader.readLine()) != null) {
              contents.append(text).append(System.getProperty("line.separator"));
            }
            reader.close();
            System.out.println("Opened Release Notes");
                      
            Stage popupwindow = new Stage();
            popupwindow.initModality(Modality.APPLICATION_MODAL);
            popupwindow.setTitle("Release Notes");

            Label label = new Label(contents.toString());

            VBox layout= new VBox(10);           
            
            Button exit_btn = new Button("Ok");
            exit_btn.setOnAction((ActionEvent event) -> {
                popupwindow.close();
            });
            
            layout.getChildren().addAll(label, exit_btn);
            layout.setAlignment(Pos.CENTER);
            exit_btn.setAlignment(Pos.BOTTOM_RIGHT);
            
            ScrollPane root = new ScrollPane(layout);

            Scene scene= new Scene(root, WIDTH, HEIGHT);
            popupwindow.setScene(scene);
            popupwindow.show();

        } catch (IOException ex) {
            Logger.getLogger(PaintBackUp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Opens a pop up displaying Tool Descriptions.
     */
    private void showToolDescriptions(){
        try{
            final int WIDTH = 550;
            final int HEIGHT = 500;
            
            GridPane desc = new GridPane();
            final int numRows = 25;
            
            for(int i = 0; i < numRows; i++){
                desc.add(new Separator(), 1, i);
            }

            //Read in Release Notes and apply to label            
            try{
                Scanner x = new Scanner(new File("Descriptions.txt"));
                int index = 0;
                while(x.hasNext()){ // while loop keeps going until it reaches the end of the file
                    String line = x.nextLine();

                    int sep = line.indexOf("%");
                    String name = line.substring(0, sep);
                    String description = line.substring(sep+1);
                    
                    if(name.contains("/")){
                        desc.add(getImageView(name), 0, index);
                    }else{
                        desc.add(new Label(name), 0, index);
                    }
                    desc.add(new Label(description), 2, index);
                    index++;
                }
                
                x.close();
            }
            catch(FileNotFoundException e){
                System.out.println("Could not find file");
                e.printStackTrace();
            }
            catch(Exception e){
                System.out.println("Could not find file");
            }
            
            System.out.println("Opened Tool Description");
            Stage popupwindow = new Stage();
            popupwindow.initModality(Modality.APPLICATION_MODAL);
            popupwindow.setTitle("Tool Descriptions");

            VBox layout= new VBox(10);           
            
            Button exit_btn = new Button("Ok");
            exit_btn.setOnAction((ActionEvent event) -> {
                popupwindow.close();
            });
            
            desc.add(exit_btn, 0, numRows);
            
            layout.getChildren().addAll(desc);
            layout.setAlignment(Pos.BOTTOM_RIGHT);
            
            ScrollPane root = new ScrollPane(layout);

            Scene scene= new Scene(root, WIDTH, HEIGHT);
            popupwindow.setScene(scene);
            popupwindow.show();

        } catch (Exception ex) {
            Logger.getLogger(PaintBackUp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Returns an ImageView of the provided image and formats it to fit the
     * global button width.
     * @param path
     * @return icon
     */
    private ImageView getImageView(String path){
        final int BTN_WIDTH = 25;
        Image img = new Image(path, BTN_WIDTH, BTN_WIDTH, false, false);
        ImageView icon = new ImageView(img);
        return icon;
    }
}