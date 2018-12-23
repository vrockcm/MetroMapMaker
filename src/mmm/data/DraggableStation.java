/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmm.data;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 *
 * @author Vrockcm
 */
public class DraggableStation extends Circle implements Draggable{
    double startX;
    double startY;
    public AbsoluteText text;
    Group g;
    public String StationName;
    public String StationTextPosition;
    public int StationTextRotate; //can be 90 or 0s
    
     public DraggableStation(String name) {
	setCenterX(0.0);
	setCenterY(0.0);	
	setOpacity(1.0);
        setRadius(10);
	startX = 0.0;
	startY = 0.0;
        StationName=name;
        text=new AbsoluteText(name);
        text.xProperty().bind(this.centerXProperty().add(40));
        text.yProperty().bind(this.centerYProperty().subtract(40));
        StationTextPosition="top-right";
        StationTextRotate=0;
        g=new Group();
        g.getChildren().addAll(this,text);
    }
    @Override
    public MapState getStartingState() {
	return MapState.SELECTING_NODE;
    }
    
    @Override
    public void start(int x, int y) {
	startX = x;
	startY = y;
    }
    
    @Override
    public void drag(int x, int y) {
	double diffX = x - startX;
	double diffY = y - startY;
	double newX = getX() + diffX;
	double newY = getY() + diffY;
	centerXProperty().set(newX);
	centerYProperty().set(newY);
	startX = x;
	startY = y;
    }
   
    @Override
    public void size(int x, int y) {
        
    }
    
    @Override
    public void setLocationAndSize(double initX, double initY, double initWidth, double initHeight) {
	centerXProperty().set(initX);
	centerYProperty().set(initY);
    }
    @Override
    public String getShapeType() {
        return STATION;
    }

    @Override
    public double getWidth() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getHeight() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   //public Node Copy(){
        //DraggableImage copy=new DraggableImage(this.getpath());
        //copy.setImage(this.getImage());
        //copy.xProperty().set(this.xProperty().get());
	//copy.yProperty().set(this.yProperty().get());
        //return copy;
  //  }
    @Override
    public double getX() {
       return getCenterX();
    }

    @Override
    public double getY() {
       return getCenterY();
    }
    public void editStationText(String newName){
        
        text.setText(newName);
    }
    public Group getGroup(){
        return g;
    }
    public void toggleLabelLocation(){
        if(StationTextPosition.equals("top-right"))
        {
            StationTextPosition="top-left";
             text.xProperty().bind(this.centerXProperty().subtract(text.getFont().getSize()*8));
             text.yProperty().bind(this.centerYProperty().subtract(40));
        }
        else if(StationTextPosition.equals("top-left"))
        {
            StationTextPosition="bottom-left";
             text.xProperty().bind(this.centerXProperty().subtract(text.getFont().getSize()*8));
             text.yProperty().bind(this.centerYProperty().add(40));
        }
        else  if(StationTextPosition.equals("bottom-left"))
        {
            StationTextPosition="bottom-right";
             text.xProperty().bind(this.centerXProperty().add(40));
             text.yProperty().bind(this.centerYProperty().add(40));
        }
        else  if(StationTextPosition.equals("bottom-right"))
        {
            StationTextPosition="top-right";
              text.xProperty().bind(this.centerXProperty().add(40));
        text.yProperty().bind(this.centerYProperty().subtract(40));
        }
    }
     public void setLabelLocation(String location){
        if(location.equals("top-left"))
        {
            StationTextPosition="top-left";
             text.xProperty().bind(this.centerXProperty().subtract(text.getFont().getSize()*8));
             text.yProperty().bind(this.centerYProperty().subtract(40));
        }
        else if(location.equals("bottom-left"))
        {
            StationTextPosition="bottom-left";
             text.xProperty().bind(this.centerXProperty().subtract(text.getFont().getSize()*8));
             text.yProperty().bind(this.centerYProperty().add(40));
        }
        else  if(location.equals("bottom-right"))
        {
            StationTextPosition="bottom-right";
             text.xProperty().bind(this.centerXProperty().add(40));
             text.yProperty().bind(this.centerYProperty().add(40));
        }
        else  if(location.equals("top-right"))
        {
            StationTextPosition="top-right";
              text.xProperty().bind(this.centerXProperty().add(40));
        text.yProperty().bind(this.centerYProperty().subtract(40));
        }
    }
    public void toggleRotate(){
        if(StationTextRotate==0){
            StationTextRotate=90;
            text.setRotate(-90);
        }
        else if(StationTextRotate==90){
            StationTextRotate=0;
            text.setRotate(0);
        }
    }
    public void setRotate(int degree){
        if(degree==0){
            StationTextRotate=0;
            text.setRotate(0);
        }
        else if(degree==90){
            StationTextRotate=90;
            text.setRotate(-90);
        }
    }
}
