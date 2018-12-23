/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmm.data;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Vrockcm
 */
public class DraggableImage extends ImageView implements Draggable{
    double startX;
    double startY;
    String file_path;
    
     public DraggableImage() {
	setX(0.0);
	setY(0.0);	
	setOpacity(1.0);
	startX = 0.0;
	startY = 0.0;
        file_path="";
    }
    
    public DraggableImage(String path) {
	setX(0.0);
	setY(0.0);	
	setOpacity(1.0);
	startX = 0.0;
	startY = 0.0;
        file_path=path;
        setImage(new Image(path));
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
	xProperty().set(newX);
	yProperty().set(newY);
	startX = x;
	startY = y;
    }
    
    public String cT(double x, double y) {
	return "(x,y): (" + x + "," + y + ")";
    }
    
    @Override
    public void size(int x, int y) {

    }
    
    @Override
    public void setLocationAndSize(double initX, double initY, double initWidth, double initHeight) {
	xProperty().set(initX);
	yProperty().set(initY);
    }
    @Override
    public String getShapeType() {
        return IMAGE;
    }

    @Override
    public double getWidth() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getHeight() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   public Node Copy(){
        DraggableImage copy=new DraggableImage(this.getpath());
        copy.setImage(this.getImage());
        copy.xProperty().set(this.xProperty().get());
	copy.yProperty().set(this.yProperty().get());
        return copy;
    }
   public String getpath()
   {
       return file_path;
   }
        
}
