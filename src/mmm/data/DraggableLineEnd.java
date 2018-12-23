/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmm.data;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import static mmm.data.Draggable.IMAGE;

/**
 *
 * @author Vrockcm
 */
public class DraggableLineEnd extends Circle implements Draggable {
    double startX;
    double startY;
    public AbsoluteText Textlink;
    public DraggableLineEnd Sister;
    public ArrayList<DraggableStation> stations;
    public String LineName;
    public boolean circular;

    public DraggableLineEnd(String name,AbsoluteText text) {
	setCenterX(20);
	setCenterY(20);	
        setRadius(10);
        setFill(Color.CHARTREUSE);
	setOpacity(0.0);
	startX = 0.0;
	startY = 0.0;   
        LineName=name;
        Textlink=text;
        text.xProperty().bind(this.centerXProperty().add(30));
        text.yProperty().bind(this.centerYProperty().subtract(30));
        stations=new ArrayList<>();
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
	centerXProperty() .set(newX);
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
        return LINE_END;
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
}
