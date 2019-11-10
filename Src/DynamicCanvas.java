/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintbackup;

import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Stack;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 *
 * @author G-sta
 */
public class DynamicCanvas extends Canvas{
    
    /**
     *
     */
    protected Canvas canvas;

    /**
     *
     */
    protected Pane drawingPane;

    /**
     *
     */
    protected GraphicsContext ctx;

    /**
     *
     */
    protected ColorPicker colorPicker; 
    
    /**
     *
     */
    protected ColorPicker fillPicker,

    /**
     *
     */
    outlinePicker;
    
    /**
     *
     */
    protected String outline = "solid";

    /**
     *
     */
    protected String fill = "none";

    /**
     *
     */
    protected Label colorName = new Label("Black");

    /**
     *
     */
    protected Label outlineName = new Label("BLACK");

    /**
     *
     */
    protected Label fillName = new Label("WHITE");

    /**
     *
     */
    protected double width,

    /**
     *
     */
    height;

    /**
     *
     */
    protected boolean shapeSelected = false;
    private CustomShape shape;
    
    private Stack<Image> actions = new Stack();
    private Stack<Image> undoneActions = new Stack();
    private Image tempImage;
           
    /**
     *
     */
    protected StringProperty toolName = new SimpleStringProperty();
    
    ArrayList<Tool> tools = new ArrayList<>();
    /*Readjust*/
    //[0] = select rectangle
    //[1] = select free form
    //[2] = select copy
    
    //[3] = pencil
    //[4] = bucket
    //[5] = textbox
    //[6] = eraser
    //[7] = color grabber
    //[8] = zoom tool
    
    //[9] = line
    //[10] = circle
    //[11] = ellipse
    //[12] = square
    //[13] = rectangle
    //[14] = diamond
    //[15] = right_triangle
    //[16] = polygon
    
    private double startX, startY, endX, endY;
    private int zoom;
        
    private final Anchor resizeHoriz, resizeVert, resizeDiag;
    private final int ANCHOR_WIDTH = 10;
    
    private int tool;

    /**
     *
     */
    protected Color tempColor = Color.TRANSPARENT;
    private double shapeX, shapeY = 0, radius = 0, shapeWidth = 0, shapeHeight = 0;
    private double[] pointsX, pointsY;

    /**
     *
     */
    protected int n;
    
    private boolean areaSelected = false;
    private ImageView selectedImage;
    private Rectangle selector, newSelector;

    /**
     *
     */
    protected String text = "";
        
    boolean fileSaved = true;
    
    /**
     * Creates a canvas and drawing pane with the given dimensions.
     * @param x width
     * @param y height
     */
    public DynamicCanvas(double x, double y) {
        canvas = new Canvas(x, y);
        ctx = canvas.getGraphicsContext2D();
        width = x;
        height = y;
        zoom = 0;
        initDraw(ctx);
        drawingPane = new Pane(canvas);
        drawingPane.setPrefHeight(canvas.getHeight() + ANCHOR_WIDTH);
        
        resizeHoriz = new Anchor(canvas, "horizontal", width, height/2, ANCHOR_WIDTH);
        resizeVert = new Anchor(canvas, "vertical", width/2, height, ANCHOR_WIDTH);
        resizeDiag = new Anchor(canvas, "diagonal", width, height, ANCHOR_WIDTH);
        drawingPane.getChildren().addAll(resizeHoriz, resizeVert, resizeDiag);
        resizeHoriz.enableDrag(resizeVert, resizeDiag, drawingPane);
        resizeVert.enableDrag(resizeHoriz, resizeDiag, drawingPane);
        resizeDiag.enableDrag(resizeHoriz, resizeVert, drawingPane);
    }
    
    /**
     * Returns pane binded to canvas that will be used for adding shapes.
     * @return pane 
     */
    public Pane getPane(){return drawingPane;}
    /**
     * Returns the ColorPicker used to determine colors used.
     * @return 
     */
    public ColorPicker getColorPicker(){return colorPicker;}
    /**
     * Returns the selected tool as an integer representation of its index in
     * the fixed ArrayList of tools.
     * 
     * @return tool id
     */
    public int getSelectedTool(){//Returns index of selected tool
        for(int i = 0; i < tools.size(); i++){
            if(tools.get(i).isSelected()){
                //toolName.setValue(tools.get(i).getName());
                return i;
            }
        }
        //toolName.setValue("");
        return 15000;
    }
    /**
     * Returns a tool
     * @return tool 
     */
    public Tool getTool(){
        return tools.get(getSelectedTool());
    }
    /**
     * Deselects all of the canvas tools. (Used for logging purposes)
     */
    public void packUpTools(){
        for(int i = 0; i < tools.size(); i++){
            tools.get(i).setSelected(false);
        }
    }
    
    /**
     * Readjusts the anchors in comparison to the canvas' new dimensions.
     */
    private void fullRefractor(){
        resizeHoriz.refractor();
        resizeVert.refractor();
        resizeDiag.refractor();
    }
    /**
     * Sets all of the Color Labels of the GUI equal the color pickers' values.
     */
    /*public void setColorLabel(){
        CID color = new CID(colorPicker.getValue());
        colorName.setText(color.getID()); 
        CID fillLabel = new CID(fillPicker.getValue());
        fillName.setText(fillLabel.getID());
        CID outlineLabel = new CID(outlinePicker.getValue());
        outlineName.setText(outlineLabel.getID());
    }*/
    
    /**
     * Binds a tool to canvas, making it available for use.
     * 
     * @param t 
     */
    public void configureTool(Tool t){
        t.setCursor(Cursor.HAND);
        tools.add(t);
    }
    /**
     * Used to "save" all of the data of the shape that was created. Used for 
     * fill() and outline().
     * @param x
     * @param y
     * @param width
     * @param height
     * @param radius 
     */
    private void setParameters(double x, double y, double width, double height, double radius){
        shapeX = x;
        shapeY = y;
        shapeWidth = width;
        shapeHeight = height;
        this.radius = radius;
    }
    /**
     * Sets Canvas Graphics Context Line Width equals to w.
     * 
     * @param w 
     */
    public void setLineWidth(double w){ ctx.setLineWidth(w);}
    /**
     * Pushes a screenshot of the canvas onto the stack of actions that can be
     * undone.
     * @param img A screenshot of the canvas.
     */
    private void addToStack(Image img){
        actions.push(img);
    }
    /**
     * 
     */
    public void undo(){
        if(!actions.empty()){
            
            System.out.print(actions.size() + " ");
            
            tempImage = actions.pop();
            undoneActions.push(tempImage);
            if(!actions.empty()){
                
                //openImage(actions.peek());
                openImage(actions.pop());
                fullRefractor();
                System.out.println("Action Undone");
                //ctx.drawImage(actions.peek(), 0, 0, canvas.getWidth(), canvas.getHeight());
            }else{
                System.out.println("Nothing to undo");
                addToStack(tempImage);
                //actions.push(tempImage);
                undoneActions.pop();
            }
        }
        fileSaved = false;    
    }
    /**
     * Redoes a previously undone action.
     */
    public void redo(){
        if(!undoneActions.empty()){
            
            
            
            tempImage = undoneActions.pop();
            openImage(tempImage);
            ctx.drawImage(tempImage, 0, 0, canvas.getWidth(), canvas.getHeight());
            //actions.push(tempImage);
            //addToStack(tempImage);
            fullRefractor();
        }else{
            System.out.println("Nothing to redo");
        }
        fileSaved = false;
    }
    /**
     * Makes canvas appear 2 times larger.
     */
    public void zoomIn(){
        canvas.setWidth(canvas.getWidth()*2);
        canvas.setHeight(canvas.getHeight()*2);
        drawingPane.setPrefSize(canvas.getWidth()+ANCHOR_WIDTH, canvas.getHeight()+ANCHOR_WIDTH);
        ctx.drawImage(actions.peek(), 0, 0, canvas.getWidth(), canvas.getHeight());
        fullRefractor();
        undoneActions.clear();
        zoom++;
    }
    /**
     * Makes canvas appear 2 times smaller.
     */
    public void zoomOut(){
        canvas.setWidth(canvas.getWidth()/2);
        canvas.setHeight(canvas.getHeight()/2);
        drawingPane.setPrefSize(canvas.getWidth()+ANCHOR_WIDTH, canvas.getHeight()+ANCHOR_WIDTH);
        ctx.drawImage(actions.peek(), 0, 0, canvas.getWidth(), canvas.getHeight());
        fullRefractor();
        undoneActions.clear();
        zoom--;
    }
    /**
     * Resizes the canvas to the given dimensions, usually called when an image is opened.
     * @param width
     * @param height 
     */
    private void resizeCanvas(double width, double height){
        this.width = (int) width;
        this.height = (int) height;
        canvas.setWidth(width);
        canvas.setHeight(height);
        drawingPane.setPrefSize(width + ANCHOR_WIDTH, height + ANCHOR_WIDTH);
        ctx.drawImage(actions.peek(), 0, 0, width, height);
        fullRefractor();
        addToStack(screenshot());
        fileSaved = false;
    }
    private Image screenshot(){//Screenshots for stack
        WritableImage tempImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, tempImage);
        ImageView imgView = new ImageView(tempImage);
        return imgView.getImage();
    }
    /**
     * Takes a snapshot of the whole canvas and if willSave is true, then also
     * adds the snapshot to the action stack. This allows the action to be 
     * undone.
     * 
     * @param willSave
     * @return RenderedImage
     */
    public RenderedImage screenshot(boolean willSave){
        try{
            int zoomIndex = zoom;
            while(zoom < 0){
                zoomIn();
            }
            while(zoom > 0){
                zoomOut();
            }
            WritableImage writableImage = new WritableImage(
                    (int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, writableImage);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            //------
            while(zoom < zoomIndex){
                zoomIn();
            }
            while(zoom > zoomIndex){
                zoomOut();
            }
            return renderedImage;
        }catch(Exception e){
            System.out.println("Could not take snapshot of canvas");
            return null;
        }
    }
    
    /**
     * Opens image onto the Canvas and adjusts the Canvas's dimensions to match
     * those of the image.
     * 
     * @param img 
     */
    public void openImage(Image img){
        double x = img.getWidth();
        double y = img.getHeight();
        zoom = 0;
        width = (int) x;
        height = (int) y;
        canvas.setWidth(x);
        canvas.setHeight(y);
        drawingPane.setPrefSize(canvas.getWidth()+ANCHOR_WIDTH, canvas.getHeight()+ANCHOR_WIDTH);
        ctx.drawImage(img, 0, 0, x, y);
        addToStack(screenshot());
        fullRefractor();
        fileSaved = false;
    }
    
    /**
     * Fills the last shape made if tool hasn't been switched, and allows for
     * all proceeding shapes to be filled in the same way.
     * 
     * @param willScreenshot 
     */
    public void fillShape(boolean willScreenshot){
        if(!shapeSelected){return;}
        if(tool!=getSelectedTool()){
            shapeSelected = false;
            return;
        }
        ctx.beginPath();
        if(fill.equals("none")){
            ctx.setFill(Color.TRANSPARENT);
        }else if(fill.equals("solid")){
            ctx.setFill(fillPicker.getValue());
        }
        switch(tool){
            case 10:                
                ctx.arc(shapeX+radius, shapeY+radius, radius, radius, 0, 360);
                ctx.fill();
                ctx.setStroke(tempColor);
                ctx.strokeArc(startX, startY, radius*2, radius*2, 0, 361, ArcType.CHORD);
                break;
            case 11:
                ctx.fillOval(shapeX, shapeY, shapeWidth, shapeHeight);
                ctx.setStroke(tempColor);
                ctx.strokeOval(startX, startY, shapeWidth, shapeHeight);
                break;
            case 12:
                ctx.fillRect(shapeX, shapeY, shapeWidth, shapeHeight);
                ctx.setStroke(tempColor);
                ctx.strokeRect(shapeX, shapeY, shapeWidth, shapeHeight);
                break;
            case 13:
                ctx.fillRect(shapeX, shapeY, shapeWidth, shapeHeight);
                ctx.setStroke(tempColor);
                ctx.strokeRect(shapeX, shapeY, shapeWidth, shapeHeight);
                break;
            case 14:
                ctx.fillPolygon(pointsX, pointsY, 5);
                ctx.setStroke(tempColor);
                ctx.strokePolygon(pointsX, pointsY, 5);
                break;
            case 15:
                ctx.fillPolygon(pointsX, pointsY, 4);
                ctx.setStroke(tempColor);
                ctx.strokePolygon(pointsX, pointsY, 4);
                break;
            case 16:
                ctx.fillPolygon(pointsX, pointsY, n+1);
                ctx.setStroke(tempColor);
                ctx.strokePolygon(pointsX, pointsY, n+1);
                break;
            case 17:
            case 18:
            case 19:
            case 20:
                ctx.fillPolygon(pointsX, pointsY, 7);
                ctx.setStroke(tempColor);
                ctx.strokePolygon(pointsX, pointsY, 7);
                break;
            default:
                break;
        }
        ctx.closePath();
        tempColor = fillPicker.getValue();
        if(willScreenshot){addToStack(screenshot());}
    }
    
    /**
     * Outlines the last shape made if tool hasn't been switched, and allows for
     * all proceeding shapes to be filled in the same way.
     * 
     * @param willScreenshot 
     */
    public void fillOutline(boolean willScreenshot){
        if(!shapeSelected){return;}
        if(tool!=getSelectedTool()){
            shapeSelected = false;
            return;
        }
        ctx.beginPath();
        if(outline.equals("none")){
            ctx.setStroke(Color.TRANSPARENT);
        }else if(outline.equals("solid")){
            ctx.setStroke(outlinePicker.getValue());
        }
        switch(tool){
            case 9:
                ctx.moveTo(startX, startY);
                ctx.lineTo(endX, endY);
                ctx.stroke();
            case 10:                
                ctx.strokeArc(shapeX, shapeY, radius*2, radius*2, 0, 360, ArcType.CHORD);
                break;
            case 11:
                ctx.strokeOval(shapeX, shapeY, shapeWidth, shapeHeight);
                break;
            case 12:
                ctx.strokeRect(shapeX, shapeY, shapeWidth, shapeHeight);
                break;
            case 13:
                ctx.strokeRect(shapeX, shapeY, shapeWidth, shapeHeight);
                break;
            case 14:
                ctx.strokePolygon(pointsX, pointsY, 5);
                break;
            case 15:
                ctx.strokePolygon(pointsX, pointsY, 4);
                break;
            case 16:
                ctx.strokePolygon(pointsX, pointsY, n+1);
                break;
            case 17:
            case 18:
            case 19:
            case 20:
                ctx.strokePolygon(pointsX, pointsY, 7);
                break;
            default:
                break;
        }
        ctx.closePath();
        tempColor = outlinePicker.getValue();
        if(willScreenshot){addToStack(screenshot());}
    }
    
    /**
     * Creates all ColorPickers and sets all event handlers.
     * @param ctx 
     */
    private void initDraw(GraphicsContext ctx){        
        colorPicker = new ColorPicker();
        colorPicker.setValue(Color.WHITE);
        
        fillPicker = new ColorPicker();
        fillPicker.setValue(Color.WHITE);
        outlinePicker = new ColorPicker();
        outlinePicker.setValue(Color.BLACK);
        
        ctx.beginPath();
        ctx.setFill(Color.WHITE);
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        ctx.closePath();
        colorPicker.setValue(Color.BLACK);
        //ctx.setFill(colorPicker.getValue());
        ctx.setFill(fillPicker.getValue());
        //ctx.setStroke(colorPicker.getValue());
        ctx.setStroke(outlinePicker.getValue());
        ctx.setLineWidth(1);
        addToStack(screenshot());
        
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>(){
 
            @Override
            public void handle(MouseEvent event) {
                startX = event.getX();
                startY = event.getY();
                int toolSelected = getSelectedTool(); 
                Paint tempColor;
                Image selectedImg;
                PixelReader reader;
                WritableImage newImage;
                switch(toolSelected){
                    case 0://Select Area
                        selector = new Rectangle(event.getX(), event.getY(), 0, 0);
                        selector.setFill(Color.TRANSPARENT);
                        selector.setStroke(Color.BLACK);
                        drawingPane.getChildren().add(selector);
                        break;
                    case 1://Move Area (Must select Area 1st
                        if(!areaSelected){
                            System.out.println("nothing happnes");
                            return;
                        }
                        ctx.beginPath();
                        tempColor = ctx.getFill();
                        ctx.setFill(Color.WHITE);
                        //Set the selected area to a blank space
                        ctx.fillRect(selector.getX(), selector.getY(), selector.getWidth(), selector.getHeight());
                        ctx.setFill(tempColor);
                        ctx.closePath();  
                        //Create a new image
                        newSelector = new Rectangle(selector.getX(), selector.getY(),
                                selector.getWidth(), selector.getHeight());        
                        drawingPane.getChildren().add(newSelector);                     
                        selectedImg = actions.peek();                       
                        reader = selectedImg.getPixelReader();
                        newImage = new WritableImage(reader, 
                                (int) selector.getX(), (int) selector.getY(), 
                                (int) selector.getWidth(), (int) selector.getHeight());
                        selectedImage = new ImageView(newImage);
                        selectedImage.setX(selector.getX());
                        selectedImage.setY(selector.getY());
                        
                        drawingPane.getChildren().add(selectedImage);
                        
                        
                        break;
                    case 2://Copy Area (Must select Area 1st
                        if(!areaSelected){
                            System.out.println("nothing happnes");
                            return;
                        }
                        //Create a new image
                        newSelector = new Rectangle(selector.getX(), selector.getY(),
                                selector.getWidth(), selector.getHeight());        
                        drawingPane.getChildren().add(newSelector);                     
                        selectedImg = actions.peek();                       
                        reader = selectedImg.getPixelReader();
                        newImage = new WritableImage(reader, 
                                (int) selector.getX(), (int) selector.getY(), 
                                (int) selector.getWidth(), (int) selector.getHeight());
                        selectedImage = new ImageView(newImage);
                        selectedImage.setX(selector.getX());
                        selectedImage.setY(selector.getY());
                        
                        drawingPane.getChildren().add(selectedImage);
                        
                        
                        break;
                    case 3://Pencil
                        ctx.beginPath();
                        ctx.moveTo(event.getX(), event.getY());
                        double width = ctx.getLineWidth();
                        Paint tempFill = ctx.getFill();
                        //ctx.setFill(colorPicker.getValue());
                        ctx.setFill(fillPicker.getValue());
                        ctx.fillRect(event.getX()-(width)/2, event.getY()-(width/2), width, width);
                        ctx.setFill(tempFill);
                        //ctx.setStroke(colorPicker.getValue());
                        ctx.setStroke(outlinePicker.getValue());
                        ctx.stroke();
                        break;
                    case 5://Text Box
                        shape = new CustomShape(drawingPane, "rectangle", event.getX(), event.getY());
                        shape.initialize();
                        break;
                    case 6://Eraser
                        ctx.beginPath();
                        double tempStroke = ctx.getLineWidth();
                        ctx.moveTo(event.getX(), event.getY());
                        ctx.setLineWidth(ctx.getLineWidth()*4);
                        ctx.setStroke(Color.WHITE);
                        ctx.fillRect(event.getX()-(ctx.getLineWidth())/2, 
                                event.getY()-(ctx.getLineWidth()/2), ctx.getLineWidth(), ctx.getLineWidth());
                        ctx.stroke();
                        ctx.setLineWidth(tempStroke);
                        //ctx.setStroke(colorPicker.getValue());
                        ctx.setStroke(outlinePicker.getValue());
                        break;
                    case 7://Color Grabber
                        Image can = actions.peek();
                        PixelReader pixelReader = can.getPixelReader();
                        //colorPicker.setValue(pixelReader.getColor((int) event.getX(), (int) event.getY())); 
                        if (event.getButton() == MouseButton.SECONDARY) {
                            System.out.println("Right button clicked with color grabber");
                            fillPicker.setValue(pixelReader.getColor((int) event.getX(), (int) event.getY()));
                        }else{
                            System.out.println("Left button clicked with color grabber");
                            outlinePicker.setValue(pixelReader.getColor((int) event.getX(), (int) event.getY()));
                        }
                        //setColorLabel();
                        ctx.drawImage(can, 0, 0, canvas.getWidth(), canvas.getHeight());
                        break;
                    case 8:
                        if (event.getButton() == MouseButton.SECONDARY) {
                            zoomOut();
                        }else{
                            zoomIn();
                        }
                        break;
                    case 9:
                        ctx.beginPath();
                        ctx.moveTo(event.getX(), event.getY());
                        shape = new CustomShape(drawingPane, "line", event.getX(), event.getY());
                        shape.initialize(outline, fill, outlinePicker, fillPicker, ctx);
                        break;
                    case 10:
                        shape = new CustomShape(drawingPane, "circle", event.getX(), event.getY());
                        shape.initialize(outline, fill, outlinePicker, fillPicker, ctx);
                        break;
                    case 11:
                        shape = new CustomShape(drawingPane, "ellipse", event.getX(), event.getY());
                        shape.initialize(outline, fill, outlinePicker, fillPicker, ctx);
                        break;
                    case 12:
                        shape = new CustomShape(drawingPane, "square", event.getX(), event.getY());
                        shape.initialize(outline, fill, outlinePicker, fillPicker, ctx);
                        break;
                    case 13:
                        shape = new CustomShape(drawingPane, "rectangle", event.getX(), event.getY());
                        shape.initialize(outline, fill, outlinePicker, fillPicker, ctx);
                        break;
                    case 14:
                        shape = new CustomShape(drawingPane, "diamond", event.getX(), event.getY());
                        shape.initialize(outline, fill, outlinePicker, fillPicker, ctx);
                        break;
                    case 15:
                        shape = new CustomShape(drawingPane, "right_triangle", event.getX(), event.getY());
                        shape.initialize(outline, fill, outlinePicker, fillPicker, ctx);
                        break;
                    case 16://N Polygon
                        if(n < 1){
                            System.out.println("Cannot construct polygon with " + n + " sides");
                            return;
                        }
                        shape = new CustomShape(drawingPane, n, event.getX(), event.getY());
                        shape.initialize(outline, fill, outlinePicker, fillPicker, ctx);
                        break;
                    case 17:
                        shape = new CustomShape(drawingPane, "left_arrow", event.getX(), event.getY());
                        shape.initialize(outline, fill, outlinePicker, fillPicker, ctx);
                        break;
                    case 18:
                        shape = new CustomShape(drawingPane, "up_arrow", event.getX(), event.getY());
                        shape.initialize(outline, fill, outlinePicker, fillPicker, ctx);
                        break;
                    case 19:
                        shape = new CustomShape(drawingPane, "right_arrow", event.getX(), event.getY());
                        shape.initialize(outline, fill, outlinePicker, fillPicker, ctx);
                        break;
                    case 20:
                        shape = new CustomShape(drawingPane, "down_arrow", event.getX(), event.getY());
                        shape.initialize(outline, fill, outlinePicker, fillPicker, ctx);
                        break;
                    default:
                        break;
                }
            }
        });
        
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, 
                new EventHandler<MouseEvent>(){
 
            @Override
            public void handle(MouseEvent event) {
                int toolSelected = getSelectedTool(); 
                switch(toolSelected){
                    case 0:
                        //selectedArea.adjust(event.getX(), event.getY());
                        selector.setWidth(event.getX()-startX);
                        selector.setHeight(event.getY()-startY);
                        //shape.adjust(event.getX(), event.getY());
                        break;
                    case 1:
                    case 2:
                        if(!areaSelected){
                            return;
                        }
                        selectedImage.setX(event.getX());
                        selectedImage.setY(event.getY());
                        newSelector.setX(event.getX());
                        newSelector.setY(event.getY());
                        break;
                    case 3:
                        ctx.lineTo(event.getX(), event.getY());
                        ctx.setStroke(outlinePicker.getValue());
                        ctx.stroke();
                        break;
                    case 5:
                        shape.adjust(event.getX(), event.getY());
                        break;
                    case 6:
                        double tempStroke = ctx.getLineWidth();
                        ctx.setLineWidth(ctx.getLineWidth()*4);
                        ctx.lineTo(event.getX(), event.getY());
                        ctx.setStroke(Color.WHITE);
                        ctx.stroke();
                        ctx.setLineWidth(tempStroke);
                        ctx.setStroke(outlinePicker.getValue());
                        break;
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                        shape.adjust(event.getX(), event.getY());
                        break;
                    default:
                        break;
                }
                fileSaved = false;
            }
        });
 
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, 
                new EventHandler<MouseEvent>(){
 
            @Override
            public void handle(MouseEvent event) {
                shapeSelected = true;
                tool = getSelectedTool();
                tempColor = outlinePicker.getValue();
                undoneActions.clear();
                fileSaved = false;
                int toolSelected = getSelectedTool(); 
                switch(toolSelected){
                    case 0:
                        areaSelected = true;
                        drawingPane.getChildren().remove(selector);
                        break;
                    case 1:
                    case 2:
                        if(!areaSelected){
                            return;
                        }
                        ctx.drawImage(selectedImage.getImage(), event.getX(), event.getY(),
                                selector.getWidth(), selector.getHeight());
                        drawingPane.getChildren().removeAll(selectedImage, newSelector);
                        break;
                    case 5:
                        shape.pane.getChildren().remove(shape.rect);
                        ctx.beginPath();
                        ctx.setFill(fillPicker.getValue());
                        ctx.setFont(Font.font ("Verdana", shape.rect.getHeight()));
                        ctx.fillText(text, startX, startY+shape.rect.getHeight());
                        
                        ctx.closePath();
                        break;
                    case 7:
                        ctx.closePath();
                        break;
                    case 9:
                        endX = event.getX();
                        endY = event.getY();
                        shape.pane.getChildren().remove(shape.line);
                        fillOutline(true);
                        break;
                    case 10:
                        ctx.beginPath();
                        ctx.setStroke(outlinePicker.getValue());
                        ctx.strokeArc(startX, startY, shape.circle.getRadius()*2, 
                                shape.circle.getRadius()*2, 0, 361, ArcType.CHORD);
                        setParameters(startX, startY, 0, 0, shape.circle.getRadius());
                        shape.pane.getChildren().remove(shape.circle);
                        fillOutline(false);
                        fillShape(false);
                        break;
                    case 11:
                        ctx.beginPath();
                        ctx.setStroke(outlinePicker.getValue());
                        ctx.setFill(Color.TRANSPARENT);
                        ctx.strokeOval(startX, startY, 
                            event.getX()-startX, event.getY()-startY);
                        setParameters(startX, startY, event.getX()-startX, event.getY()-startY, 0);
                        shape.pane.getChildren().remove(shape.ellipse);
                        fillOutline(false);
                        fillShape(false);
                        break;
                    case 12:
                        ctx.beginPath();
                        ctx.setStroke(outlinePicker.getValue());
                        ctx.setFill(Color.TRANSPARENT);
                        ctx.strokeRect(startX, startY, shape.square.getWidth(), shape.square.getHeight());
                        setParameters(startX, startY, shape.square.getWidth(), shape.square.getHeight(), 0);
                        shape.pane.getChildren().remove(shape.square);
                        fillOutline(false);
                        fillShape(false);
                        break;
                    case 13:
                        ctx.beginPath();
                        ctx.setStroke(outlinePicker.getValue());
                        ctx.setFill(Color.TRANSPARENT);
                        ctx.strokeRect(startX, startY, shape.rect.getWidth(), shape.rect.getHeight());
                        setParameters(startX, startY, shape.rect.getWidth(), shape.rect.getHeight(), 0);
                        shape.pane.getChildren().remove(shape.rect);
                        fillOutline(false);
                        fillShape(false);
                        break;
                    case 14:
                        ctx.beginPath();
                        pointsX = new double[5];
                        pointsY = new double[5];
                        for(int i = 0; i < pointsX.length-1; i++){
                            pointsX[i] = shape.pointsX[i];
                            pointsY[i] = shape.pointsY[i];
                        }
                        pointsX[4] = pointsX[0];
                        pointsY[4] = pointsY[0];
                        ctx.beginPath();
                        ctx.strokePolygon(pointsX, pointsY, 5);
                        shape.pane.getChildren().remove(shape.diamond);
                        fillOutline(false);
                        fillShape(false);
                        break;
                    case 15:
                        ctx.beginPath();
                        pointsX = new double[4];
                        pointsY = new double[4];
                        for(int i = 0; i < pointsX.length-1; i++){
                            pointsX[i] = shape.pointsX[i];
                            pointsY[i] = shape.pointsY[i];
                        }
                        pointsX[3] = pointsX[0];
                        pointsY[3] = pointsY[0];
                        ctx.beginPath();
                        ctx.strokePolygon(pointsX, pointsY, 4);
                        shape.pane.getChildren().remove(shape.right_triangle);
                        fillOutline(false);
                        fillShape(false);
                        break;
                    case 16:
                        ctx.beginPath();
                        pointsX = new double[n+1];
                        pointsY = new double[n+1];
                        for(int i = 0; i < pointsX.length-1; i++){
                            pointsX[i] = shape.pointsX[i];
                            pointsY[i] = shape.pointsY[i];
                        }
                        pointsX[n] = pointsX[0];
                        pointsY[n] = pointsY[0];
                        ctx.strokePolygon(pointsX, pointsY, n+1);
                        shape.pane.getChildren().remove(shape.polygon);
                        fillOutline(false);
                        fillShape(false);
                        break;
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                        ctx.beginPath();
                        pointsX = new double[8];
                        pointsY = new double[8];
                        for(int i = 0; i < pointsX.length-1; i++){
                            pointsX[i] = shape.pointsX[i];
                            pointsY[i] = shape.pointsY[i];
                        }
                        pointsX[7] = pointsX[0];
                        pointsY[7] = pointsY[0];
                        ctx.strokePolygon(pointsX, pointsY, 8);
                        shape.pane.getChildren().remove(shape.arrow);
                        fillOutline(false);
                        fillShape(false);
                        break;
                    default:
                        break;
                }
                ctx.closePath();
                if(toolSelected==1 || toolSelected==2){
                    areaSelected = false;
                }
                if(toolSelected!=0){
                    System.out.println("Added an image to a stack: " + actions.size());
                    addToStack(screenshot());
                }
            }
        });
        
    }
    
}
