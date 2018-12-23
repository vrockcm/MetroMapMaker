/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmm.data;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.QuadCurve;

/**
 *
 * @author Vrockcm
 */
public class LineWrap extends QuadCurve implements Draggable {
    
    public String LineName;
    double controlX;
    double controlY;
    double startX;
    double startY;
    public Circle one;
    public Circle two;
    
    public LineWrap(String name){
        LineName=name;
    } 
 @Override
    public MapState getStartingState() {
	return MapState.SELECTING_NODE;
    }
    
    @Override
    public void start(int x, int y) {
        startX= x - getControlX();
        startY= y - getControlY();
    }
    
    @Override
    public void drag(int x, int y) {
        this.controlXProperty().unbind();
        this.controlYProperty().unbind();
	controlX = x - startX;
        controlY = y - startY;
        setControlX(controlX);
        setControlY(controlY);         
    }
    
    public String cT(double x, double y) {
	return "(x,y): (" + x + "," + y + ")";
    }
    
    @Override
    public void size(int x, int y) {

    }
    
    @Override
    public void setLocationAndSize(double initX, double initY, double initWidth, double initHeight) {
         if(this.getControlX()==one.getCenterX())
            this.controlXProperty().bind(one.centerXProperty());
        else
            this.setControlX(initX);       
        if(this.getControlY()==one.getCenterY())
            this.controlYProperty().bind(one.centerYProperty());
        else
            this.setControlY(initY);
    }
    
    @Override
    public String getShapeType() {
        return LINE;
    }
    public String getLineName() {
        return LineName;
    }

    @Override
    public double getWidth() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getHeight() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getX() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getY() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public void setNodes(Circle node1,Circle node2){
        this.startXProperty().bind(node1.centerXProperty());
        this.startYProperty().bind(node1.centerYProperty());
        this.endXProperty().bind(node2.centerXProperty());
        this.endYProperty().bind(node2.centerYProperty());  
        this.controlXProperty().bind(node1.centerXProperty());
        this.controlYProperty().bind(node1.centerYProperty());
        one=node1;
        two=node2;
    }
    public LineWrap getCopy(){
        LineWrap copy=new LineWrap(this.LineName);
        copy.startXProperty().bind(one.centerXProperty());
        copy.startYProperty().bind(one.centerYProperty());
        copy.endXProperty().bind(two.centerXProperty());
        copy.endYProperty().bind(two.centerYProperty()); 
        copy.one=this.one;
        copy.two=this.two;
        if(this.getControlX()==one.getCenterX())
            copy.controlXProperty().bind(one.centerXProperty());
        else
            copy.setControlX(this.getControlX());       
        if(this.getControlY()==one.getCenterY())
            copy.controlYProperty().bind(one.centerYProperty());
        else
            copy.setControlY(this.getControlY());
        
        copy.setStrokeWidth(this.getStrokeWidth());
        copy.setStroke(this.getStroke());
        copy.setFill(Color.TRANSPARENT);
        return copy;
    }
    
}
