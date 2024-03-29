package paintbackup;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author G-sta
 */
public class CustomShape {

    /**
     *
     */
    protected double startX,

    /**
     *
     */
    startY;
    private final String name;

    /**
     *
     */
    protected Pane pane;
    
    /**
     *
     */
    protected Line line;    

    /**
     *
     */
    protected Circle circle;

    /**
     *
     */
    protected Ellipse ellipse;

    /**
     *
     */
    protected Rectangle square;

    /**
     *
     */
    protected Rectangle rect;

    /**
     *
     */
    protected Polygon diamond;

    /**
     *
     */
    protected Polygon right_triangle;    

    /**
     *
     */
    protected Polygon polygon;
    Circle point1, point2;

    /**
     *
     */
    protected Polygon arrow;
    
    /**
     *
     */
    protected double[] pointsX;

    /**
     *
     */
    protected double[] pointsY;
    
    /**
     * Base constructor used for any shape except polygon.
     * @param pane
     * @param name
     * @param x
     * @param y 
     */
    public CustomShape(Pane pane, String name, double x, double y){
        startX = x;
        startY = y;
        this.pane = pane;
        this.name = name;
        if(name.equals("line")){
            line = new Line(x, y, x, y);
        }else if(name.equals("circle")){
            circle = new Circle(startX, startY, 0);
        }else if(name.equals("ellipse")){
            ellipse = new Ellipse(startX, startY, 0, 0);
        }else if(name.equals("square")){
            square = new Rectangle(startX, startY, 0, 0);
        }else if(name.equals("rectangle")){
            rect = new Rectangle(startX, startY, 0, 0);
        }else if(name.equals("diamond")){
            diamond = new Polygon();
            pointsX = new double[4];
            pointsY = new double[4];
        }else if(name.equals("right_triangle")){
            right_triangle = new Polygon();
            pointsX = new double[3];
            pointsY = new double[3];
        }else if(name.equals("left_arrow")){
            arrow = new Polygon();
            pointsX = new double[7];
            pointsY = new double[7];
        }else if(name.equals("up_arrow")){
            arrow = new Polygon();
            pointsX = new double[7];
            pointsY = new double[7];
        }else if(name.equals("right_arrow")){
            arrow = new Polygon();
            pointsX = new double[7];
            pointsY = new double[7];
        }else if(name.equals("down_arrow")){
            arrow = new Polygon();
            pointsX = new double[7];
            pointsY = new double[7];
        }
    }
    
   /**
    * separate constructor for polygons
     * @param pane
     * @param sides
     * @param x
     * @param y
    */ 
    public CustomShape(Pane pane, int sides, double x, double y){
        startX = x;
        startY = y;
        name = "polygon";
        this.pane = pane;
        circle = new Circle(x, y, 0);
        polygon = new Polygon();
        pointsX = new double[sides];
        pointsY = new double[sides];
    }
    
    /**
     * Used for creating the rectangle for text box and select area.
     */
    public void initialize(){
        rect.setStroke(Color.BLACK);
        rect.setFill(Color.TRANSPARENT);
        pane.getChildren().add(rect);
    }
    /**
     * Used for creating shapes.
     * @param outline
     * @param fill
     * @param op
     * @param fp
     * @param gc 
     */
    public void initialize(String outline, String fill, ColorPicker op, ColorPicker fp, GraphicsContext gc){
        Color outline_color, fill_color;
        if(outline.equals("none")){
            outline_color = Color.TRANSPARENT;
        }else{
            outline_color = op.getValue();
        }
        if(fill.equals("none")){
            fill_color = Color.TRANSPARENT;
        }else{
            fill_color = fp.getValue();
        }
        outline(outline, outline_color, gc);
        fill(fill, fill_color);
        addToPane();
        
    }
    
    /**
     * Adds the appropriate shape to the canvas' drawing pane
     */
    private void addToPane(){
        switch(name){
            case "line":
                pane.getChildren().add(line);
                break;
            case "circle":
                pane.getChildren().add(circle);
                break;
            case "ellipse":
                pane.getChildren().add(ellipse);
                break;
            case "square":
                pane.getChildren().add(square);
                break;
            case "rectangle":
                pane.getChildren().add(rect);
                break;
            case "diamond":
                pane.getChildren().add(diamond);
                break;
            case "right_triangle":
                pane.getChildren().add(right_triangle);
                break;
            case "polygon":
                pane.getChildren().add(polygon);
                break;
            case "left_arrow":
            case "up_arrow":
            case "right_arrow":
            case "down_arrow":
                pane.getChildren().add(arrow);
                break;
        }
    }
    
    /**
     * Adjusts shape's dimensions so user can see the outcome.
     * @param x
     * @param y 
     */
    public void adjust(double x, double y){
        if(name.equals("line")){
            if (line == null) {
                return;
            }
            line.setEndX(x);
            line.setEndY(y);
        }else if(name.equals("circle")){
            if(circle == null) {
                return;
            }
            double widthX = (x-startX)/2;
            double widthY = (y-startY)/2;
            if(widthX < widthY) {
                circle.setRadius(widthX);
                circle.setCenterX(x-widthX);
                circle.setCenterY(startY+widthX);
            }else{
                circle.setRadius(widthY);
                circle.setCenterX(startX+widthY);
                circle.setCenterY(y-widthY);
            }
        }else if(name.equals("ellipse")){
            if (ellipse == null) {
                return;
            }
            double widthX = (x-startX)/2;
            double widthY = (y-startY)/2;
            ellipse.setCenterX(x-widthX);
            ellipse.setCenterY(y-widthY);
            ellipse.setRadiusX(widthX);
            ellipse.setRadiusY(widthY);
        }else if(name.equals("square")){
            if (square == null) {
                return;
            }
            double widthX = x-startX;
            double widthY = y-startY;
            if(x<y){
                square.setWidth(widthX);
                square.setHeight(widthX);
            }else{
                square.setWidth(widthY);
                square.setHeight(widthY);
            }
        }else if(name.equals("rectangle")){
            if (rect == null) {
                return;
            }
            rect.setWidth(x-startX);
            rect.setHeight(y-startY);
        }else if(name.equals("diamond")){
            if(diamond == null) {
                return;
            }
            double midX = startX + ((x - startX)/2);
            double midY = startY + ((y - startY)/2);
            diamond.getPoints().removeAll(new Double[]{
                pointsX[0], pointsY[0],
                pointsX[1], pointsY[1],
                pointsX[2], pointsY[2],
                pointsX[3], pointsY[3]
            });
            pointsX = new double[]{startX, midX, x, midX};
            pointsY = new double[]{midY, startY, midY, y};
            diamond.getPoints().addAll(new Double[]{        
                pointsX[0], pointsY[0],
                pointsX[1], pointsY[1],
                pointsX[2], pointsY[2],
                pointsX[3], pointsY[3]
            });
        }else if(name.equals("right_triangle")){
            if(right_triangle == null) {
                return;
            }
            right_triangle.getPoints().removeAll(new Double[]{
                pointsX[0], pointsY[0],
                pointsX[1], pointsY[1],
                pointsX[2], pointsY[2]
            });
            pointsX = new double[]{startX, startX, x};
            pointsY = new double[]{startY, y, y};
            right_triangle.getPoints().addAll(new Double[]{        
                pointsX[0], pointsY[0],
                pointsX[1], pointsY[1],
                pointsX[2], pointsY[2]
            });
        }else if(name.equals("polygon")){
            if(polygon == null || circle == null) {
                return;
            }
            Double[] points = new Double[pointsX.length*2];
            int index = 0;
            for(int i = 0; i < pointsX.length; i++){
                points[index] = pointsX[i];
                points[index+1] = pointsY[i];
                index += 2;
                
            }
            polygon.getPoints().removeAll(points);
        //Adjust the circle that we will base the polygon off of
            double widthX = (x-startX)/2;
            double widthY = (y-startY)/2;
            if(widthX < widthY) {
                circle.setRadius(widthX);
                circle.setCenterX(x-widthX);
                circle.setCenterY(startY+widthX);
            }else{
                circle.setRadius(widthY);
                circle.setCenterX(startX+widthY);
                circle.setCenterY(y-widthY);
            }
            startX = circle.getCenterX() - circle.getRadius();
            startY = circle.getCenterY() - circle.getRadius();
            double angle = ((360/pointsX.length)*Math.PI/180);
            double angleSeg = ((360/pointsX.length)*Math.PI/180);
        //Grab points from circle
            pointsX[0] = circle.getCenterX() + circle.getRadius();
            pointsY[0] = circle.getCenterY();    
            for(int i = 1; i < pointsX.length; i++){
                pointsX[i] = circle.getCenterX()+circle.getRadius()*Math.cos(angle);
                pointsY[i] = circle.getCenterY()+circle.getRadius()*Math.sin(angle);
                angle = angle + angleSeg;
            }
            index = 0;
            for(int i = 0; i < pointsX.length; i++){
                points[index] = pointsX[i];
                points[index+1] = pointsY[i];
                index += 2;
            }
            polygon.getPoints().addAll(points);
        }else if(name.equals("left_arrow")){
            if(arrow == null) {
                return;
            }
            /*if(x < startX){
                double tempX = x;
                x = startX;
                startX = tempX;
            }
            if(y < startY){
                double tempY = y;
                y = startY;
                startY = tempY;
            }*/
            arrow.getPoints().removeAll(new Double[]{
                pointsX[0], pointsY[0], pointsX[1], pointsY[1],
                pointsX[2], pointsY[2], pointsX[3], pointsY[3],
                pointsX[4], pointsY[4], pointsX[5], pointsY[5],
                pointsX[6], pointsY[6]
            });
            double halfWidth = ((x-startX)/2);
            double halfHeight = ((y-startY)/2);
            double quarterHeight = ((y-startY)/4);
            pointsX = new double[]{startX, startX+halfWidth, startX+halfWidth, x, 
                        x, startX+halfWidth, startX+halfWidth};
            pointsY = new double[]{startY+halfHeight, startY, startY+quarterHeight,
                        startY+quarterHeight, y-quarterHeight, y-quarterHeight, y};
            arrow.getPoints().addAll(new Double[]{
                pointsX[0], pointsY[0], pointsX[1], pointsY[1],
                pointsX[2], pointsY[2], pointsX[3], pointsY[3],
                pointsX[4], pointsY[4], pointsX[5], pointsY[5],
                pointsX[6], pointsY[6]
            });
        }else if(name.equals("up_arrow")){
            if(arrow == null) {
                return;
            }
            arrow.getPoints().removeAll(new Double[]{
                pointsX[0], pointsY[0], pointsX[1], pointsY[1],
                pointsX[2], pointsY[2], pointsX[3], pointsY[3],
                pointsX[4], pointsY[4], pointsX[5], pointsY[5],
                pointsX[6], pointsY[6]
            });
            double halfWidth = ((x-startX)/2);
            double quarterWidth = ((x-startX)/4);
            double halfHeight = ((y-startY)/2);
            pointsX = new double[]{startX+quarterWidth, startX+quarterWidth, 
                        startX, startX+halfWidth, x, x-quarterWidth, x-quarterWidth}; 
            pointsY = new double[]{y, y-halfHeight, y-halfHeight,
                        startY, y-halfHeight, y-halfHeight, y};
            arrow.getPoints().addAll(new Double[]{
                pointsX[0], pointsY[0], pointsX[1], pointsY[1],
                pointsX[2], pointsY[2], pointsX[3], pointsY[3],
                pointsX[4], pointsY[4], pointsX[5], pointsY[5],
                pointsX[6], pointsY[6]
            });
        }else if(name.equals("right_arrow")){
            if(arrow == null) {
                return;
            }
            arrow.getPoints().removeAll(new Double[]{
                pointsX[0], pointsY[0], pointsX[1], pointsY[1],
                pointsX[2], pointsY[2], pointsX[3], pointsY[3],
                pointsX[4], pointsY[4], pointsX[5], pointsY[5],
                pointsX[6], pointsY[6]
            });
            double halfWidth = ((x-startX)/2);
            double halfHeight = ((y-startY)/2);
            double quarterHeight = ((y-startY)/4);
            pointsX = new double[]{startX, startX+halfWidth, startX+halfWidth, x, 
                        x-halfWidth, x-halfWidth, startX};
            pointsY = new double[]{startY+quarterHeight, startY+quarterHeight, startY, 
                        startY+halfHeight, y, y-quarterHeight, y-quarterHeight};
            arrow.getPoints().addAll(new Double[]{
                pointsX[0], pointsY[0], pointsX[1], pointsY[1],
                pointsX[2], pointsY[2], pointsX[3], pointsY[3],
                pointsX[4], pointsY[4], pointsX[5], pointsY[5],
                pointsX[6], pointsY[6]
            });
        }else if(name.equals("down_arrow")){
            if(arrow == null) {
                return;
            }
            arrow.getPoints().removeAll(new Double[]{
                pointsX[0], pointsY[0], pointsX[1], pointsY[1],
                pointsX[2], pointsY[2], pointsX[3], pointsY[3],
                pointsX[4], pointsY[4], pointsX[5], pointsY[5],
                pointsX[6], pointsY[6]
            });
            double halfWidth = ((x-startX)/2);
            double quarterWidth = ((x-startX)/4);
            double halfHeight = ((y-startY)/2);
            pointsX = new double[]{startX, startX+quarterWidth, startX+quarterWidth, 
                        x-quarterWidth, x-quarterWidth, x, x-halfWidth}; 
            pointsY = new double[]{startY+halfHeight, startY+halfHeight, startY,
                        startY, startY+halfHeight, startY+halfHeight, y};
            arrow.getPoints().addAll(new Double[]{
                pointsX[0], pointsY[0], pointsX[1], pointsY[1],
                pointsX[2], pointsY[2], pointsX[3], pointsY[3],
                pointsX[4], pointsY[4], pointsX[5], pointsY[5],
                pointsX[6], pointsY[6]
            });
        }
    }
    
    /**
     * Applies the fill of this shape.
     * @param fill
     * @param c 
     */
    public void fill(String fill, Color c){
        switch(name){
            case "line":
                line.setFill(c);
                break;
            case "circle":
                circle.setFill(c);
                break;
            case "ellipse":
                ellipse.setFill(c);
                break;
            case "square":
                square.setFill(c);
                break;
            case "rectangle":
                rect.setFill(c);
                break;
            case "diamond":
                diamond.setFill(c);
                break;
            case "right_triangle":
                right_triangle.setFill(c);
                break;
            case "polygon":
                polygon.setFill(c);
                break;
            case "left_arrow":
            case "up_arrow":
            case "right_arrow":
            case "down_arrow":
                arrow.setFill(c);
                break;
        }
    }
    
    /**
     * Applies the outline of this shape.
     * @param outline
     * @param c
     * @param gc 
     */
    public void outline(String outline, Color c, GraphicsContext gc){
        switch(name){
            case "line":
                line.setStrokeWidth(gc.getLineWidth());
                line.setStroke(c);
                break;
            case "circle":
                circle.setStrokeWidth(gc.getLineWidth());
                circle.setStroke(c);
                break;
            case "ellipse":
                ellipse.setStrokeWidth(gc.getLineWidth());
                ellipse.setStroke(c);
                break;
            case "square":
                square.setStrokeWidth(gc.getLineWidth());
                square.setStroke(c);
                break;
            case "rectangle":
                rect.setStrokeWidth(gc.getLineWidth());
                rect.setStroke(c);
                break;
            case "diamond":
                diamond.setStrokeWidth(gc.getLineWidth());
                diamond.setStroke(c);
                break;
            case "right_triangle":
                right_triangle.setStrokeWidth(gc.getLineWidth());
                right_triangle.setStroke(c);
                break;
            case "polygon":
                polygon.setStrokeWidth(gc.getLineWidth());
                polygon.setStroke(c);
                break;
            case "left_arrow":
            case "up_arrow":
            case "right_arrow":
            case "down_arrow":
                arrow.setStrokeWidth(gc.getLineWidth());
                arrow.setStroke(c);
                break;
        }
    }

}