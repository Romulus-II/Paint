/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintbackup;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author G-sta
 */
public class Anchor extends Rectangle{
    
    private double x, y;
    private final double WIDTH;
    private String movement;
    
    private final Canvas canvas;    
    private GraphicsContext ctx;
    private Paint tempColor;
    
    
    private double startX, startY, newX, newY;
    
    /**
     * Creates an anchor object, which allows for easy resizing of the canvas.
     * @param canvas
     * @param movement
     * @param x
     * @param y
     * @param w 
     */
    public Anchor(Canvas canvas, String movement, double x, double y, double w){
        super(x, y, w, w);
        this.x = x;
        this.WIDTH = w;
        this.y = y - (WIDTH/2);
        this.canvas = canvas;
        ctx = canvas.getGraphicsContext2D();
        //ctx.setFill(Color.WHITE);
        this.movement = movement;
        this.setStroke(Color.BLACK);
        this.setFill(Color.WHITE);
        //enableDrag();
    }
    
    /**
     * Sets the anchor to automatically resize the pane, while readjusting
     * the position of the other 2 given anchors.
     * 
     * @param a
     * @param b
     * @param pane 
     */
    public void enableDrag(Anchor a, Anchor b, Pane pane) { //All 3 anchors are required for this step
        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                getScene().setCursor(Cursor.MOVE);
                tempColor = ctx.getFill();
                ctx.setFill(Color.WHITE);
            }
        });
        setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                getScene().setCursor(Cursor.HAND);
                ctx.setFill(tempColor);
            }
        });
        setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                startX = canvas.getWidth();
                startY = canvas.getHeight();
                newX = mouseEvent.getX();
                newY = mouseEvent.getY();
                if(movement.equals("horizontal")){
                    if (newX > 0) {
                        canvas.setWidth(newX);
                        setX(newX);
                        if(newX > startX){
                            ctx.fillRect(startX, 0, newX-startX, startY);
                        }
                        pane.setPrefWidth(newX+WIDTH);
                        //canvas.width = (newX);
                    }
                }else if(movement.equals("vertical")){
                    if (newY > 0) {
                        canvas.setHeight(newY);
                        setY(newY);
                        if(newY > startY){
                            ctx.fillRect(0, startY, startX, newY-startY);
                        }
                        pane.setPrefHeight(newY+WIDTH);
                        //canvas.height = (newY);
                    }
                }else if(movement.equals("diagonal")){
                    if(newX > 0) {
                        canvas.setWidth(newX);
                        canvas.setHeight(newY);
                        setX(newX);
                        setY(newY);
                        if(newX > startX){
                            ctx.fillRect(startX, 0, newX-startX, newY);
                        }    
                        if(newY > startY){
                            ctx.fillRect(0, startY, newX, newY-startY);
                        }
                        pane.setPrefSize(newX+WIDTH, newY+WIDTH);
                    }
                }
                //pane.setPrefSize(newX+WIDTH, newY+WIDTH);
                refractor(a);
                refractor(b);
            }
        });
        setOnMouseEntered(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
            if (!mouseEvent.isPrimaryButtonDown()) {
              getScene().setCursor(Cursor.HAND);
            }
          }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
            if (!mouseEvent.isPrimaryButtonDown()) {
              getScene().setCursor(Cursor.DEFAULT);
            }
          }
        });
    }
    
    /**
     * Adjusts position of given anchor based on this anchor.
     * @param a 
     */
    private void refractor(Anchor a){
        if(a.movement.equals("horizontal")){
            if(a.y != canvas.getHeight()/2){
                a.setY(canvas.getHeight()/2);
            }
            if(a.x != canvas.getWidth()){
                a.setX(canvas.getWidth());
            }
        }else if(a.movement.equals("vertical")){
            if(a.x != canvas.getWidth()/2){
                a.setX(canvas.getWidth()/2);
            }
            if(a.y != canvas.getHeight()){
                a.setY(canvas.getHeight());
            }
        }else if(a.movement.equals("diagonal")){
            if(a.y != canvas.getHeight()){
                a.setY(canvas.getHeight());
            }
            if(a.x != canvas.getWidth()){
                a.setX(canvas.getWidth());
            }
        }
    }

    /**
     * Adjust position after canvas change.
     */
    public void refractor(){
        if(movement.equals("horizontal")){
            if(y != canvas.getHeight()/2){
                setY(canvas.getHeight()/2);
            }
            if(x != canvas.getWidth()){
                setX(canvas.getWidth());
            }
        }else if(movement.equals("vertical")){
            if(x != canvas.getWidth()/2){
                setX(canvas.getWidth()/2);
            }
            if(y != canvas.getHeight()){
                setY(canvas.getHeight());
            }
        }else if(movement.equals("diagonal")){
            if(y != canvas.getHeight()){
                setY(canvas.getHeight());
            }
            if(x != canvas.getWidth()){
                setX(canvas.getWidth());
            }
        }
    }
    
}
