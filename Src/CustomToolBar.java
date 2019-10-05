/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintbackup;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author G-sta
 */
public class CustomToolBar extends ToolBar{   
    final int BTN_WIDTH = 25, BTN_WIDTH_2 = 65, BTN_WIDTH_3 = 80;
    final private ToggleGroup group = new ToggleGroup();
        
    Button undo, redo;
    
    Tool[] outline_btn = new Tool[2];
    CustomMenuItem[] outline = new CustomMenuItem[2];
    ToggleGroup outlines = new ToggleGroup();
    Tool[] fill_btn = new Tool[2];
    CustomMenuItem[] fill = new CustomMenuItem[2];
    ToggleGroup fills = new ToggleGroup();
    MenuItem[] width = new MenuItem[4];
    
    MenuButton outlinePicker;
    MenuButton fillPicker;
    MenuButton orientation;
    MenuButton widthPicker;
    
    Label colorName;
    
    Tool[] select_btn = new Tool[2];
    Tool[] tool_btn = new Tool[6];
    Tool[] shape_btn = new Tool[12];//[8];
    
    GridPane select, tools, shapes;
    VBox toolsSection, shapesSection;
    
    
    public CustomToolBar() {
        super();
    }
    
    public CustomToolBar(DynamicCanvas canvas){
        
        undo = new Button();
        undo.setPrefSize(BTN_WIDTH_2, BTN_WIDTH_2);
        try{
            Image img = new Image("/undo.png", BTN_WIDTH_2-5, BTN_WIDTH_2-5, false, false);
            ImageView icon = new ImageView(img);
            undo.setGraphic(new ImageView(img));
        }catch(NullPointerException e){
            System.out.println(e + " happened");
        }
        undo.setOnAction((ActionEvent event) -> {
            canvas.undo();
        });
        redo = new Button();
        redo.setPrefSize(BTN_WIDTH_2, BTN_WIDTH_2);
        try{
            Image img = new Image("/redo.png", BTN_WIDTH_2-5, BTN_WIDTH_2-5, false, false);
            ImageView icon = new ImageView(img);
            redo.setGraphic(new ImageView(img));
        }catch(NullPointerException e){
            System.out.println(e + " happened");
        }
        redo.setOnAction((ActionEvent event) -> {
            canvas.redo();
        });
        
        select = new GridPane();
        
        Label selectLabel = new Label("Tools");
        select.add(selectLabel, 0, 2);
        
        select.setAlignment(Pos.CENTER);
        
        for(int i = 0; i < select_btn.length; i++){
            select_btn[i] = new Tool();
            select_btn[i].setToggleGroup(group);
            canvas.configureTool(select_btn[i]);
        }
        select_btn[0].paint(BTN_WIDTH, "/select_rect.png");
        select.add(select_btn[0], 0, 0);
        select_btn[1].setText("Move");
        select.add(select_btn[1], 0, 1);
        select_btn[1].setOnAction((ActionEvent event) -> {
            select_btn[0].setSelected(false);
        });
        
        tools = new GridPane();
        
        Label toolsLabel = new Label("Tools");
        //tools.add(toolsLabel, 1, 2);
        
        tools.setAlignment(Pos.CENTER);
        //getItems().addAll(tools, border);
        //getItems().add(tools); 
        
        for(int i = 0; i < tool_btn.length; i++){
            tool_btn[i] = new Tool();
            tool_btn[i].setToggleGroup(group);
            canvas.configureTool(tool_btn[i]);
        }
       //Pencil
        tool_btn[0].paint(BTN_WIDTH, "/color_pencil.png");
        tools.add(tool_btn[0], 0, 0);
       //Bucket
        tool_btn[1].paint(BTN_WIDTH, "/bucket.png");
        tools.add(tool_btn[1], 1, 0);
       //Text Box
        tool_btn[2].paint(BTN_WIDTH, "/txt_box.png");
        tools.add(tool_btn[2], 2, 0);
        tool_btn[2].setOnAction(new EventHandler<ActionEvent>() {
            //Popup to get textbox text;
            @Override
            public void handle(ActionEvent event){
                
                    Stage popupwindow=new Stage();

                    popupwindow.initModality(Modality.APPLICATION_MODAL);
                    popupwindow.setTitle("Textbox Thingy");

                    final int WIDTH = 300;
                    final int HEIGHT = 150;

                    Label label = new Label("Enter text:");
                    label.setAlignment(Pos.CENTER);
                    label.setWrapText(true);

                    TextField textField = new TextField();
                    textField.setAlignment(Pos.CENTER);
                    textField.prefWidth(WIDTH);
                    textField.prefHeight(HEIGHT/2);

                    Button setText = new Button("Create TextBox");
                    setText.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event){
                            canvas.text = textField.getText();
                            popupwindow.close();
                        }
                    });

                    Button cancel = new Button("Cancel");
                    cancel.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event){
                            tool_btn[2].setSelected(false);
                            popupwindow.close();
                        }
                    });

                    HBox buttons = new HBox();
                    buttons.getChildren().addAll(setText, cancel);

                    VBox layout = new VBox();

                    layout.getChildren().addAll(label, textField, buttons);
                    layout.setAlignment(Pos.CENTER);

                    Scene scene1= new Scene(layout, WIDTH, HEIGHT);
                    popupwindow.setScene(scene1);
                    popupwindow.showAndWait();
                }
            
        });
       //Eraser
        tool_btn[3].paint(BTN_WIDTH, "/eraser.png");
        tools.add(tool_btn[3], 0, 1);
       //Color Grabber
        tool_btn[4].paint(BTN_WIDTH, "/color_grabber.png");
        tools.add(tool_btn[4], 1, 1);
       //Zoom
        tool_btn[5].paint(BTN_WIDTH, "/zoom.png");
        tools.add(tool_btn[5], 2, 1);
               
        toolsSection = new VBox(); 
        toolsSection.getChildren().addAll(tools, toolsLabel);
        toolsLabel.setAlignment(Pos.CENTER);
        
                
        //Shapes
        shapes = new GridPane();
        
        Label shapesLabel = new Label("Shapes");
        shapesLabel.setAlignment(Pos.CENTER);
        //shapes.add(shapesLabel, 0, 2);
        
        shapes.setAlignment(Pos.CENTER);
        //getItems().addAll(shapes, border);
        
        for(int i = 0; i < shape_btn.length; i++){
            shape_btn[i] = new Tool();
            shape_btn[i].setToggleGroup(group);
            canvas.configureTool(shape_btn[i]);
        }
       //line
        shape_btn[0].paint(BTN_WIDTH, "/line.png");
        //shape_btn[0].setDescription("Line");
        shapes.add(shape_btn[0], 0, 0);
       //ellipse
        shape_btn[1].paint(BTN_WIDTH, "/circle.png");
        //shape_btn[1].setDescription("Circle");
        shapes.add(shape_btn[1], 1, 0);        
       //ellipse
        shape_btn[2].paint(BTN_WIDTH, "/ellipse.png");
        //shape_btn[2].setDescription("Ellipse");
        shapes.add(shape_btn[2], 2, 0);        
       //square
        shape_btn[3].paint(BTN_WIDTH, "/square.png");
        //shape_btn[3].setDescription("Square");
        shapes.add(shape_btn[3], 3, 0); 
       //rectangle
        shape_btn[4].paint(BTN_WIDTH, "/rectangle.png");
        //shape_btn[4].setDescription("Rectangle");
        shapes.add(shape_btn[4], 0, 1); 
       //Diamond
        shape_btn[5].paint(BTN_WIDTH, "/diamond.png");
        shapes.add(shape_btn[5], 1, 1);
       //Right-Triangle
        shape_btn[6].paint(BTN_WIDTH, "/right_triangle.png");
        shapes.add(shape_btn[6], 2, 1);
       //Polygon
        shape_btn[7].paint(BTN_WIDTH, "/free_draw_polygon.png");
        shapes.add(shape_btn[7], 3, 1);
        shape_btn[7].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                if(shape_btn[7].isSelected()){
                    Stage popupwindow=new Stage();

                    popupwindow.initModality(Modality.APPLICATION_MODAL);
                    popupwindow.setTitle("Polygon Wizard!");

                    final int WIDTH = 300;
                    final int HEIGHT = 150;

                    Label label = new Label("Enter the number of sides:");
                    label.setAlignment(Pos.CENTER);
                    label.setWrapText(true);

                    TextField numberField = new TextField();
                    NumberStringFilteredConverter converter = new NumberStringFilteredConverter();
                    final TextFormatter<Number> formatter = new TextFormatter<>(
                            converter,
                            0,
                            converter.getFilter()
                    );

                    numberField.setTextFormatter(formatter);

                    formatter.valueProperty().addListener((observable, oldValue, newValue) ->
                            System.out.println(newValue)
                    );


                    Button setText = new Button("Done");
                    setText.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event){
                            int n = Integer.parseInt(numberField.getText());
                            if(n < 31){
                                canvas.n = n;
                                popupwindow.close();
                            }else{
                                label.setText("Number is too large!");
                            }

                        }
                    });

                    Button cancel = new Button("Cancel");
                    cancel.setOnAction((ActionEvent event1) -> {
                        shape_btn[7].setSelected(false);
                        popupwindow.close();
                    });
                    HBox buttons = new HBox();
                    buttons.getChildren().addAll(setText, cancel);

                    VBox layout = new VBox();

                    layout.getChildren().addAll(label, numberField, buttons);
                    layout.setAlignment(Pos.CENTER);

                    Scene scene1= new Scene(layout, WIDTH, HEIGHT);
                    popupwindow.setScene(scene1);
                    popupwindow.showAndWait();
                }
            }
        });
        shape_btn[8].paint(BTN_WIDTH, "/left_arrow.png");
        shapes.add(shape_btn[8], 0, 3);
        shape_btn[9].paint(BTN_WIDTH, "/up_arrow.png");
        shapes.add(shape_btn[9], 1, 3);
        shape_btn[10].paint(BTN_WIDTH, "/right_arrow.png");
        shapes.add(shape_btn[10], 2, 3);
        shape_btn[11].paint(BTN_WIDTH, "/down_arrow.png");
        shapes.add(shape_btn[11], 3, 3);
        
        
        VBox shapesSection = new VBox();
        shapesSection.getChildren().addAll(shapes, shapesLabel);
        
        
        //Stroke and Fill
        outlinePicker = new MenuButton("Outline");
        outlinePicker.setPrefWidth(BTN_WIDTH_3);
        fillPicker = new MenuButton("Fill");
        fillPicker.setPrefWidth(BTN_WIDTH_3);
        orientation = new MenuButton("Flip");
        orientation.setPrefWidth(BTN_WIDTH_3);
        shapes.add(outlinePicker, 5, 0);
        shapes.add(fillPicker, 5, 1);
        
        //outline_btn[0] = new Tool(BTN_WIDTH, "/transparent.png");
        outline_btn[0] = new Tool("No outline");
        outline_btn[0].setToggleGroup(outlines);
        outline_btn[0].setOnAction((ActionEvent event) -> {
            canvas.outline = "none";
            if(canvas.shapeSelected) canvas.fillOutline(true);
        });
        outline[0] = new CustomMenuItem(outline_btn[0]);
        //outline_btn[1] = new Tool(BTN_WIDTH, "/solid-color.png");
        outline_btn[1] = new Tool("Solid color");
        outline_btn[1].setToggleGroup(outlines);
        outline_btn[1].setOnAction((ActionEvent event) -> {
            canvas.outline = "solid";
            if(canvas.shapeSelected) canvas.fillOutline(true);
        });
        outline[1] = new CustomMenuItem(outline_btn[1]);
        outlinePicker.getItems().addAll(outline[0], outline[1]);
        
        fill_btn[0] = new Tool("No Fill");
        fill_btn[0].setToggleGroup(fills);
        fill_btn[0].setOnAction((ActionEvent event) -> {
            canvas.fill = "none";
            if(canvas.shapeSelected) canvas.fillShape(true);
        });
        fill[0] = new CustomMenuItem(fill_btn[0]);
        fill_btn[1] = new Tool("Solid Color");
        fill_btn[1].setToggleGroup(fills);
        fill_btn[1].setOnAction((ActionEvent event) -> {
            canvas.fill = "solid";
            if(canvas.shapeSelected) canvas.fillShape(true);
        });
        fill[1] = new CustomMenuItem(fill_btn[1]);
        fillPicker.getItems().addAll(fill[0], fill[1]);
        
                
        //Width
        widthPicker = new MenuButton("Size");
                     
        width[0] = new MenuItem("1px");
        width[1] = new MenuItem("2px");
        width[2] = new MenuItem("5px");
        width[3] = new MenuItem("8px");
        widthPicker.getItems().addAll(width[0], width[1], width[2], width[3]);
        

        //ColorPicker & Label
        VBox colors = new VBox();
        colorName = canvas.colorName;
        canvas.colorPicker.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                canvas.setColorLabel();  
                if(canvas.shapeSelected){
                    if(canvas.tempColor!=canvas.colorPicker.getValue()){
                        canvas.fillOutline(false);
                        canvas.fillShape(true);
                    }
                }
            }
        });
        colors.getChildren().addAll(canvas.colorPicker, colorName);
        
        getItems().addAll(undo, redo, new Separator(), select, new Separator(),
                toolsSection, new Separator(), shapes, new Separator(), widthPicker, 
                new Separator(), colors);
    }
}
