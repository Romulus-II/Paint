package paintbackup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author G-sta
 */
public class CustomMenu extends MenuBar{
    
    private HBox menubars;
    
    private MenuItem openFile, save, saveAs, exitApp, zoomIn, zoomOut;
    private MenuItem about, releaseNotes, toolDescription;
    
    private DynamicCanvas canvas;
    
    private FileHandler fh;
    
    private boolean timerShown = true;
    
    public CustomMenu(FileHandler fh){
        
        this.fh = fh;
        
        canvas = fh.getDynamicCanvas();
        
        //Autosave timer
        Label time = new Label();
        Timer timer = new Timer(fh, time);
        Menu clock = new Menu("", time);
        
        Menu fill = new Menu();
        fill.setDisable(true);
        
        
        //Declare sub-menus and add to main menu
        Menu file = new Menu("File");
        openFile = new MenuItem("Open");
        save = new MenuItem("Save");
        saveAs = new MenuItem("Save As");
        exitApp = new MenuItem("Exit");
        file.getItems().addAll(openFile, save, saveAs,exitApp);
        
        openFile.setOnAction((ActionEvent event) -> {
            boolean cond = fh.open();
            if(cond){timer.reset();}
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
            fh.exitApp();
        });
        exitApp.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
       
        
        
        Menu test = new Menu("Test");
        MenuItem warning = new MenuItem("Warning");
        test.getItems().add(warning);
        warning.setOnAction((ActionEvent event) -> {
            GetApproval ga = new GetApproval();
            System.out.println("Started runnable");
            ga.run();
            System.out.println("Exited runnable");
        });
        
        
        Menu edit = new Menu("Edit");
        MenuItem properties = new MenuItem("Properties");
        edit.getItems().add(properties);
        
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
        zoomOut.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.ALT_DOWN));
        
        
        Menu image = new Menu("Image");
        MenuItem rotate = new MenuItem("Rotate");
        MenuItem flip = new MenuItem("Flip");
        image.getItems().addAll(rotate, flip);
        
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
        
        
        getMenus().addAll(file, edit, view, image, help, fill, clock);
        
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
        
    }
    
    /**
     * Returns the physical menu component that will be used.
     * 
     * @return HBox
     */
    public HBox getMenuBars(){
        return menubars;
    }
    
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
            close.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    //fh.getLogFile().close();
                    popupwindow.close();
                }
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
    
    private void showToolDescriptions(){
        try{
            final int WIDTH = 550;
            final int HEIGHT = 500;
            
            GridPane desc = new GridPane();
            final int numRows = 20;
            
            for(int i = 0; i < numRows; i++){
                desc.add(new Separator(), 1, i);
            }
            //Read in Release Notes and apply to label
            /*File help_file = new File("Tool_Descriptions.txt");
            StringBuffer contents = new StringBuffer();
            BufferedReader reader = null;
            
            reader = new BufferedReader(new FileReader(help_file));
            String text = null;
            // repeat until all lines is read
            int i = 0;
            while ((reader.readLine()) != null) {              
                Label temp = new Label(reader.readLine());
                desc.add(temp, 2 , i);
                i++;
            }
            reader.close();
            System.out.println("Opened Tool Description");
            */    
            desc.add(getImageView("/undo.png"), 0, 0);
            desc.add(new Label("Undo: Undo the last action"), 2, 0);
            desc.add(getImageView("/redo.png"), 0, 1);
            desc.add(new Label("Redo: Redo the last undone action"), 2, 1);
            desc.add(getImageView("/select_rect.png"), 0, 2);
            desc.add(new Label("Select: Select an area of the canvas to be moved"), 2, 2);
            desc.add(new Label("Move"), 0, 3);
            desc.add(new Label("Move: Move selected area of the canvas"), 2, 3);
            desc.add(getImageView("/color_pencil.png"), 0, 4);
            desc.add(new Label("Pencil: Free Draw lines"), 2, 4);
            desc.add(getImageView("/txt_box.png"), 0, 5);
            desc.add(new Label("Text Box: Enter text and place it on mouse click"), 2, 5);
            desc.add(getImageView("/eraser.png"), 0, 6);
            desc.add(new Label("Eraser: Erase your mistakes"), 2, 6);
            desc.add(getImageView("/color_grabber.png"), 0, 7);
            desc.add(new Label("Color Grabber: Get color value of selected point"), 2, 7);
            desc.add(getImageView("/zoom.png"), 0, 8);
            desc.add(new Label("Zoom: Zooms in on mouse click"), 2, 8);
            desc.add(getImageView("/line.png"), 0, 9);
            desc.add(new Label("Line: Click and drag to make a line"), 2, 9);
            desc.add(getImageView("/circle.png"), 0, 10);
            desc.add(new Label("Circle: Click and drag to make a circle"), 2, 10);
            desc.add(getImageView("/ellipse.png"), 0, 11);
            desc.add(new Label("Ellipse: Click and drag to make an ellipse"), 2, 11);
            desc.add(getImageView("/square.png"), 0, 12);
            desc.add(new Label("Square: Click and drag to make a square"), 2, 12);
            desc.add(getImageView("/rectangle.png"), 0, 13);
            desc.add(new Label("Rectangle: Click and drag to make a rectangle"), 2, 13);
            desc.add(getImageView("/diamond.png"), 0, 14);
            desc.add(new Label("Diamond: Click and drag to make a diamond"), 2, 14);
            desc.add(getImageView("/right_triangle.png"), 0, 15);
            desc.add(new Label("Triangle: Click and drag to make a right-triangle"), 2, 15);
            desc.add(getImageView("/free_draw_polygon.png"), 0, 16);
            desc.add(new Label("Polygon: Enter number of sides, then click and drag to make an n polygon"), 2, 16);
            desc.add(new Label("Width"), 0, 17);
            desc.add(new Label("Width: Set line/shape width"), 2, 17);
            desc.add(new Label("Fill"), 0, 18);
            desc.add(new Label("Fill: Designate if shapes are filled or not"), 2, 18);
            desc.add(new Label("Outline"), 0, 19);
            desc.add(new Label("Outline: Designate if shapes are outlined or not"), 2, 19);
            
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
    
    private ImageView getImageView(String path){
        final int BTN_WIDTH = 25;
        Image img = new Image(path, BTN_WIDTH, BTN_WIDTH, false, false);
        ImageView icon = new ImageView(img);
        return icon;
    }
}

/*class Task extends Thread{
    
    DynamicCanvas canvas;
    
    File file;
    
    FileHandler fh;
    
    public Task(DynamicCanvas canvas, FileHandler fh, File file){
        this.canvas = canvas;
        this.fh = fh;
        this.file = file;
    }
    
    public void run() {
        try {
            ImageIO.write(canvas.screenshot(false), fh.getFileExt(file), file);
            System.out.println("File automatically saved as " + file.toString());
            //canvas.fileSaved = true;
        } catch (Exception ex) {
            Logger.getLogger(PaintBackUp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Could not sutomatically save file");
        }
    }
}*/