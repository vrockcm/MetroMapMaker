package mmm.data;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import mmm.gui.MapWorkspace;
import djf.components.AppDataComponent;
import djf.AppTemplate;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import static mmm.data.MapState.SELECTING_NODE;

/**
 * This class serves as the data management component for this application.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class MapData implements AppDataComponent {
    // FIRST THE THINGS THAT HAVE TO BE SAVED TO FILES
    
    // THESE ARE THE SHAPES TO DRAW
    ObservableList<Node> nodes;
    
    // THE BACKGROUND COLOR
    Color backgroundColor;
    String backgroundImage;
    // AND NOW THE EDITING DATA   
    // THIS IS THE SHAPE CURRENTLY SELECTED
    Node selectedNode;

    // FOR FILL AND OUTLINE
    Color currentLineColor;
    double currentLineThickness;
    Color currentStationColor;
    double currentStationRadius;

    // CURRENT STATE OF THE APP
    MapState state;

    // THIS IS A SHARED REFERENCE TO THE APPLICATION
    AppTemplate app;
    
    // USE THIS WHEN THE SHAPE IS SELECTED
    Effect highlightedEffect;

    public static final String WHITE_HEX = "#FFFFFF";
    public static final String BLACK_HEX = "#000000";
    public static final String YELLOW_HEX = "#EEEE00";
    public static final Paint DEFAULT_BACKGROUND_COLOR = Paint.valueOf(WHITE_HEX);
    public static final Paint HIGHLIGHTED_COLOR = Paint.valueOf(YELLOW_HEX);
    public static final int HIGHLIGHTED_STROKE_THICKNESS = 3;

    /**
     * THis constructor creates the data manager and sets up the
     *
     *
     * @param initApp The application within which this data manager is serving.
     */
    public MapData(AppTemplate initApp) {
	// KEEP THE APP FOR LATER
	app = initApp;

	// NO SHAPE STARTS OUT AS SELECTED
	selectedNode = null;
        backgroundColor=Color.WHITE;
        backgroundImage="";
	// INIT THE COLORS
	currentLineThickness = 1;
	currentStationRadius = 10;
	currentLineColor= Color.BLACK;
        currentStationColor=Color.BLACK;
	// THIS IS FOR THE SELECTED SHAPE
	DropShadow dropShadowEffect = new DropShadow();
	dropShadowEffect.setOffsetX(0.0f);
	dropShadowEffect.setOffsetY(0.0f);
	dropShadowEffect.setSpread(20.0);
	dropShadowEffect.setColor(Color.LIGHTBLUE);
	dropShadowEffect.setBlurType(BlurType.GAUSSIAN);
	dropShadowEffect.setRadius(8);
	highlightedEffect = dropShadowEffect;
    }
    
    public ObservableList<Node> getNodes() {
	return nodes;
    }

    public Color getBackgroundColor() {
	return backgroundColor;
    }
    public String getBackgroundImage() {
	return backgroundImage;
    }
    public void setBackgroundImage(String filepath) {
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
	backgroundImage=filepath;
        if(!filepath.isEmpty()){
            BackgroundFill fill = new BackgroundFill(backgroundColor, null, null);
            BackgroundImage image=new BackgroundImage(new Image(filepath),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            BackgroundFill[] fills=new BackgroundFill[1];
            BackgroundImage[] images=new BackgroundImage[1];
            fills[0]=fill;
            images[0]=image;
            Background background = new Background(fills,images);	       
            workspace.getCanvas().setBackground(background);
        }
        else
        {
            setBackgroundColor(backgroundColor);
        }
    }
    public Color getLineColor() {
	return currentLineColor;
    }
    public Color getStationColor() {
	return currentStationColor;
    }
     public void setLineColor(Color color) {
	currentLineColor=color;
    }
    public double getcurrentLineThickness() {
	return currentLineThickness;
    }
     public void setStationColor(Color color) {
         if(selectedNode instanceof DraggableStation)
             ((DraggableStation)selectedNode).setFill(color);
	currentStationColor=color;
    }
    public double getcurrentStationRadius() {
	return currentStationRadius;
    }
    public void setcurrentStationRadius(int r) {
	currentStationRadius=r;
    }
    
    public void setNodes(ObservableList<Node> initNodes) {
	nodes = initNodes;
    }
    
    public void setBackgroundColor(Color initBackgroundColor) {
	backgroundColor = initBackgroundColor;
	MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
	Pane canvas = workspace.getCanvas();
	BackgroundFill fill = new BackgroundFill(backgroundColor, null, null);
        List<BackgroundFill> fills = new ArrayList<BackgroundFill>();
        fills.add(fill);
        Background background;
        if(!backgroundImage.isEmpty())
            background = new Background(fills,canvas.getBackground().getImages());
        else
            background = new Background(fill);
	canvas.setBackground(background);
    }
    public void setCurrentLineThickness(int initthickness,String name) {
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        currentLineThickness=initthickness;
        String LineName=name;
        for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext(); ) 
        {
            Node node = iterator.next();           
            if(node instanceof LineWrap){              
                   if(((LineWrap)node).LineName.equalsIgnoreCase(LineName))
                    {                       
                        ((LineWrap)node).setStrokeWidth(currentLineThickness);
                    }                       
            }              
        }
    }
      public void setStationRadius(int radius) {       
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        currentStationRadius=radius;
        for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext(); ) 
        {
            Node node = iterator.next();           
            if(node instanceof Group){              
                   if(((Group)node).getChildren().get(0) instanceof DraggableStation &&  ((DraggableStation)((Group)node).getChildren().get(0)).StationName.equals((String)workspace.getStationSelecter().getSelectionModel().getSelectedItem()))
                    {      
                        Transaction t=new Transaction(((DraggableStation)((Group)node).getChildren().get(0)),getBackgroundColor(),app,null);
                        ((DraggableStation)((Group)node).getChildren().get(0)).setRadius(currentStationRadius);
                        t.setAfterAction(((DraggableStation)((Group)node).getChildren().get(0)), getBackgroundColor(), app,null);
                        workspace.AddTransaction(t);
                        t.setSTATIONRADIUSCHANGE(true);
                        break;
                    }   
                   else if(((Group)node).getChildren().get(1) instanceof DraggableStation &&  ((DraggableStation)((Group)node).getChildren().get(1)).StationName.equals((String)workspace.getStationSelecter().getSelectionModel().getSelectedItem())){
                       Transaction t=new Transaction(((DraggableStation)((Group)node).getChildren().get(1)),getBackgroundColor(),app,null);
                       ((DraggableStation)((Group)node).getChildren().get(1)).setRadius(currentStationRadius);
                        t.setAfterAction(((DraggableStation)((Group)node).getChildren().get(1)), getBackgroundColor(), app,null);
                        workspace.AddTransaction(t);
                        t.setSTATIONRADIUSCHANGE(true);
                        break;
                   }
            }              
        }        
    }
    
    public void removeSelectedNode() {
	if (selectedNode != null) {
	    nodes.remove(selectedNode);
	    selectedNode = null;
	}
    }

    /**
     * This function clears out the HTML tree and reloads it with the minimal
     * tags, like html, head, and body such that the user can begin editing a
     * page.
     */
    @Override
    public void resetData() {
	setState(SELECTING_NODE);
	selectedNode = null;

	// INIT THE COLORS	
	nodes.clear();
        ((MapWorkspace)app.getWorkspaceComponent()).getStationSelecter().getItems().clear();
        ((MapWorkspace)app.getWorkspaceComponent()).getLineSelecter().getItems().clear();
	((MapWorkspace)app.getWorkspaceComponent()).getCanvas().getChildren().clear();
    }

    public void unhighlightShape(Node node) {
	node.setEffect(null);
    }
    
    public void highlightShape(Node node) {
	node.setEffect(highlightedEffect);
    }
    public Node initNewStation(String StationName) {
	// DESELECT THE SELECTED SHAPE IF THERE IS ONE
         MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
	if (selectedNode != null) {
	    unhighlightShape(selectedNode);
	    selectedNode = null;
	}
        DraggableStation newShape=new DraggableStation(StationName);
        newShape.setFill(currentStationColor);
	// ADD THE SHAPE TO THE CANVAS
	nodes.addAll(newShape.getGroup());	
        workspace.getStationSelecter().getItems().add(StationName);
        workspace.getToStationSelecter().getItems().add(StationName);
        workspace.getFromStationSelecter().getItems().add(StationName);
        workspace.getToStationSelecter().getSelectionModel().selectLast();
        workspace.getFromStationSelecter().getSelectionModel().selectLast();
        return newShape;
    }
    public void AddStationToLine(DraggableStation m,ArrayList<Node> imple,String name){
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();     
        String lineName;
        if(name.isEmpty())
            lineName=(String)workspace.getLineSelecter().getSelectionModel().getSelectedItem();
        else
            lineName=name;
        for(int i=0;i<nodes.size();i++){
            Node node = nodes.get(i);           
            if(node instanceof DraggableLineEnd && ((DraggableLineEnd)node).LineName.equalsIgnoreCase(lineName)){
                if(((DraggableLineEnd)node).stations.contains(m)){
                    break;
                }
               if(((DraggableLineEnd)node).stations.isEmpty()){
                 for(int j=0;j<nodes.size();j++){
                        Node node1 = nodes.get(j);
                        if(node1 instanceof LineWrap && ((LineWrap)node1).LineName.equals(lineName)){
                            if(((LineWrap)node1).one.equals(node) || ((LineWrap)node1).two.equals(node)){
                                if(imple==null){
                                    m.centerXProperty().set(((LineWrap)node1).startXProperty().add(((LineWrap)node1).endXProperty()).divide(2).doubleValue());
                                    m.centerYProperty().set(((LineWrap)node1).startYProperty().add(((LineWrap)node1).endYProperty()).divide(2).doubleValue());
                                    Circle temp=((LineWrap)node1).two;
                                    ((LineWrap)node1).setNodes(((LineWrap)node1).one, m);
                                    LineWrap line=new LineWrap(lineName);
                                    line.setStroke(currentLineColor);
                                    line.setFill(Color.TRANSPARENT);
                                    line.setStrokeWidth(currentLineThickness);
                                    line.setNodes(m, temp);
                                    nodes.add(line);
                                    line.toBack();
                                    temp.toFront();
                                    ((LineWrap)node1).toBack();
                                    m.toFront();
                                    m.toFront();
                                    ((DraggableLineEnd)node).stations.add(m);
                                    ((DraggableLineEnd)node).Sister.stations.add(m);
                                    break;
                                }
                                else
                                {
                                    nodes.remove(node1);
                                    ((DraggableLineEnd)node).stations.add(m);
                                    ((DraggableLineEnd)node).Sister.stations.add(m);
                                    break;
                                }
                            }     
                        }
                    }
                 break;
                }
               else{ 
                   Node closest=null;
                   Node Secondclosest=null;
                   int closestdistance=(int)Point2D.distance(((DraggableLineEnd)node).getCenterX(),((DraggableLineEnd)node).getCenterY()
                           , m.getCenterX(), m.getCenterY());
                   for(int j=0;j<((DraggableLineEnd)node).stations.size();j++){
                        DraggableStation node1 =((DraggableLineEnd)node).stations.get(j);  
                        int distance=(int)Point2D.distance(((DraggableStation)node1).getCenterX(),((DraggableStation)node1).getCenterY()
                           , m.getCenterX(), m.getCenterY());
                        if(closestdistance>distance){
                               closest=node1;
                               closestdistance=distance;
                        }
                        else if(distance==closestdistance){
                            Secondclosest=node1;
                        }
                    }
                   int distance=(int)Point2D.distance(((DraggableLineEnd)node).Sister.getCenterX(),((DraggableLineEnd)node).Sister.getCenterY()
                           , m.getCenterX(), m.getCenterY());
                   if(closestdistance>distance){
                               closest=((DraggableLineEnd)node).Sister;
                               closestdistance=distance;
                        }
                  
                   if(Secondclosest==null){
                       Secondclosest=node;
                   int Secondclosestdistance=(int)Point2D.distance(((DraggableLineEnd)node).getCenterX(),((DraggableLineEnd)node).getCenterY()
                           , m.getCenterX(), m.getCenterY());
                   for(int j=0;j<((DraggableLineEnd)node).stations.size();j++){
                        DraggableStation node1 =((DraggableLineEnd)node).stations.get(j);  
                        distance=(int)Point2D.distance(((DraggableStation)node1).getCenterX(),((DraggableStation)node1).getCenterY()
                           , m.getCenterX(), m.getCenterY());
                        if(Secondclosestdistance>distance && Secondclosestdistance>closestdistance && distance!=closestdistance){
                               Secondclosest=node1;
                               Secondclosestdistance=distance;
                        }
                    }
                   distance=(int)Point2D.distance(((DraggableLineEnd)node).Sister.getCenterX(),((DraggableLineEnd)node).Sister.getCenterY()
                           , m.getCenterX(), m.getCenterY());
                    if(Secondclosestdistance>distance && Secondclosestdistance>closestdistance && distance!=closestdistance){
                               Secondclosest=((DraggableLineEnd)node).Sister;
                               closestdistance=distance;
                        }
                   }
                   Node tempNode1=null;
                   Node tempNode2=null;
                   if(imple==null){
                        tempNode1=closest;
                        tempNode2=Secondclosest;
                   }
                   else
                   {
                       for(int j=0;j<imple.size();j++){
                           if(imple.get(j) instanceof LineWrap && ((LineWrap)imple.get(j)).one.equals(m)){
                               tempNode1=((LineWrap)imple.get(j)).two;
                           }
                           else if(imple.get(j) instanceof LineWrap && ((LineWrap)imple.get(j)).two.equals(m)){
                               tempNode2=((LineWrap)imple.get(j)).one;
                           }
                       }
                   }
                  if(tempNode1 instanceof DraggableLineEnd || tempNode2 instanceof DraggableLineEnd){
                      for(int j=0;j<nodes.size();j++){
                        Node node1 = nodes.get(j);
                        if(node1 instanceof LineWrap && ((LineWrap)node1).LineName.equals(lineName)){
                            if(((LineWrap)node1).two instanceof DraggableLineEnd){
                                if(imple==null){
                                    m.centerXProperty().set(((LineWrap)node1).startXProperty().add(((LineWrap)node1).endXProperty()).divide(2).doubleValue());
                                    m.centerYProperty().set(((LineWrap)node1).startYProperty().add(((LineWrap)node1).endYProperty()).divide(2).doubleValue());
                                    Circle temp=((LineWrap)node1).two;
                                    ((LineWrap)node1).setNodes(((LineWrap)node1).one, m);
                                    LineWrap line=new LineWrap(lineName);
                                    line.setStroke(currentLineColor);
                                    line.setFill(Color.TRANSPARENT);
                                    line.setStrokeWidth(currentLineThickness);
                                    line.setNodes(m,temp);
                                    nodes.add(line);
                                    line.toBack();
                                    m.toFront();
                                    line.toBack();
                                    temp.toFront();
                                    ((DraggableLineEnd)node).stations.add(m);
                                    ((DraggableLineEnd)node).Sister.stations.add(m);
                                    break; 
                                }
                                else{
                                     nodes.remove(node1);
                                      ((DraggableLineEnd)node).stations.add(m);
                                    ((DraggableLineEnd)node).Sister.stations.add(m);
                                    break;
                                }
                      }
                  }
               }
            }
            else if(tempNode1 instanceof DraggableStation && tempNode2 instanceof DraggableStation){
                      for(int j=0;j<nodes.size();j++){
                        Node node1 = nodes.get(j);
                        if(node1 instanceof LineWrap && ((LineWrap)node1).LineName.equals(lineName)){
                            if(((LineWrap)node1).two.equals(tempNode2) && ((LineWrap)node1).one.equals(tempNode1) && ((LineWrap)node1).one instanceof DraggableLineEnd==false ||
                                 ((LineWrap)node1).two.equals(tempNode1)&& ((LineWrap)node1).one.equals(tempNode2) && ((LineWrap)node1).one instanceof DraggableLineEnd==false){ 
                                if(imple==null){
                                    m.centerXProperty().set(((DraggableStation)tempNode1).centerXProperty().add(((DraggableStation)tempNode2).centerXProperty()).divide(2).doubleValue());
                                    m.centerYProperty().set(((DraggableStation)tempNode1).centerYProperty().add(((DraggableStation)tempNode2).centerYProperty()).divide(2).doubleValue());
                                    ((LineWrap)node1).setNodes((Circle)tempNode1, m);
                                    LineWrap line=new LineWrap(lineName);
                                    line.setStroke(currentLineColor);
                                    line.setFill(Color.TRANSPARENT);
                                    line.setStrokeWidth(currentLineThickness);
                                    line.setNodes(m,(Circle)tempNode2);
                                    nodes.add(line);
                                    line.toBack();
                                    m.toFront();
                                    tempNode1.toFront();
                                    tempNode2.toFront();
                                    ((DraggableLineEnd)node).stations.add(((DraggableLineEnd)node).stations.indexOf(tempNode1)+1,m);
                                    ((DraggableLineEnd)node).Sister.stations.add(((DraggableLineEnd)node).stations.indexOf(tempNode1)+1,m);
                                    break; 
                                } else{
                                        nodes.remove(node1);
                                        ((DraggableLineEnd)node).stations.add(((DraggableLineEnd)node).stations.indexOf(tempNode1),m);
                                        ((DraggableLineEnd)node).Sister.stations.add(((DraggableLineEnd)node).stations.indexOf(tempNode1),m);
                                        break;
                                    }
                                
                            }
                        }
                      }
                  }
                  break;
               }
                
            }
        }
        if(imple!=null){
            nodes.addAll(imple);
            MoveBackLine();
        }
    }
    public ArrayList<Node> RemoveStation(DraggableStation m,boolean BreakDisable){
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();   
        ArrayList<Node> trash=new ArrayList<>();
        String lineName=(String)workspace.getLineSelecter().getSelectionModel().getSelectedItem();
        for(int i=0;i<nodes.size();i++){
        Node node = nodes.get(i);           
        if(node instanceof DraggableLineEnd && ((DraggableLineEnd)node).LineName.equalsIgnoreCase(lineName) && BreakDisable==false || 
             node instanceof DraggableLineEnd && BreakDisable==true){
            if(((DraggableLineEnd)node).stations.contains(m)){
                for(int j=0;j<nodes.size();j++){
                        Node node1 = nodes.get(j);
                        boolean switchRemoval=false;
                        boolean breakRemove=false;
                        if(node1 instanceof LineWrap && ((LineWrap)node1).LineName.equals(lineName)  && BreakDisable==false && !trash.contains((LineWrap)node1) ||
                             node1 instanceof LineWrap && ((LineWrap)node1).LineName.equals(((DraggableLineEnd)node).LineName) && BreakDisable==true && !trash.contains((LineWrap)node1)){
                            if(((LineWrap)node1).one.equals(m)){
                                if(((DraggableLineEnd)node).stations.size()==1){
                                    trash.add(((LineWrap)node1).getCopy());
                                    ((LineWrap)node1).setNodes((Circle)node,((DraggableLineEnd)node).Sister);
                                    ((DraggableLineEnd)node).stations.remove(m);
                                    ((DraggableLineEnd)node).Sister.stations.remove(m); 
                                    switchRemoval=false;
                                }
                                else if(((DraggableLineEnd)node).stations.indexOf(m)==((DraggableLineEnd)node).stations.size()-1){
                                    if(((LineWrap)node1).two.equals(node)){ 
                                        trash.add(((LineWrap)node1).getCopy());
                                       ((LineWrap)node1).setNodes(((DraggableLineEnd)node).stations.get(((DraggableLineEnd)node).stations.indexOf(m)-1),(Circle)node);
                                       ((DraggableLineEnd)node).stations.remove(m);
                                       ((DraggableLineEnd)node).Sister.stations.remove(m); 
                                       switchRemoval=false;
                                    } 
                                    else
                                        break;
                                }
                                else if(((LineWrap)node1).one instanceof DraggableStation && ((LineWrap)node1).two instanceof DraggableStation && ((DraggableLineEnd)node).stations.indexOf(m)>0){
                                     trash.add(((LineWrap)node1).getCopy());
                                    ((LineWrap)node1).setNodes(((DraggableLineEnd)node).stations.get(((DraggableLineEnd)node).stations.indexOf(m)-1),((DraggableLineEnd)node).stations.get(((DraggableLineEnd)node).stations.indexOf(m)+1));
                                       ((DraggableLineEnd)node).stations.remove(m);
                                       ((DraggableLineEnd)node).Sister.stations.remove(m); 
                                       switchRemoval=false;
                                }
                                else if(((DraggableLineEnd)node).stations.indexOf(m)==0){
                                    switchRemoval=true;
                                    breakRemove=true;
                                }
                                
                                if(switchRemoval==true){ 
                                        trash.add(node1);
                                        m.setCenterX(m.getCenterX()+40);
                                        m.setCenterY(m.getCenterY()-20);
                                    if(breakRemove=false)
                                        break;
                                }
                              
                            }
                            else if(((LineWrap)node1).two.equals(m)){
                                if(((DraggableLineEnd)node).stations.size()>1){
                                    if(((DraggableLineEnd)node).stations.indexOf(m)==0){
                                        if(((LineWrap)node1).one.equals(node)){
                                            trash.add(((LineWrap)node1).getCopy());
                                           ((LineWrap)node1).setNodes((Circle)node,((DraggableLineEnd)node).stations.get(((DraggableLineEnd)node).stations.indexOf(m)+1));
                                           ((DraggableLineEnd)node).stations.remove(m);
                                           ((DraggableLineEnd)node).Sister.stations.remove(m); 
                                           switchRemoval=true;
                                        }                                  
                                    }
                                }
                                if(switchRemoval==false){                                
                                        trash.add(node1);
                                        m.setCenterX(m.getCenterX()-20);
                                        m.setCenterY(m.getCenterY()-40);
                                        if(breakRemove=false)
                                        break;
                                }
                            }
                            
                           
                        }
                }
            }
            else{ 
                if(BreakDisable==false)
                    break;   
            }
       }
    }  
       nodes.removeAll(trash);
       if(BreakDisable==true){
            nodes.remove(m.g);
            trash.add(m.g);
       }
       return trash;
    }
     public Node initNewLine(String LineName,Color color) {
	// DESELECT THE SELECTED SHAPE IF THERE IS ONE
        currentLineColor=color;
	if (selectedNode != null) {
	    unhighlightShape(selectedNode);
	    selectedNode = null;
	}
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();    
        AbsoluteText text1=new AbsoluteText(LineName);
        AbsoluteText text2=new AbsoluteText(LineName);
        DraggableLineEnd newShape1=new DraggableLineEnd(LineName,text1);
        DraggableLineEnd newShape2=new DraggableLineEnd(LineName,text2);       
        newShape1.Sister=newShape2;
        newShape2.Sister=newShape1;
        LineWrap line=new LineWrap(LineName);
        line.setStroke(color);
        line.setFill(Color.TRANSPARENT);
        line.setNodes(newShape1,newShape2);
        text1.xProperty().bind(newShape1.centerXProperty().subtract(20));
        text1.yProperty().bind(newShape1.centerYProperty().add(40));
        text2.xProperty().bind(newShape2.centerXProperty().add(20));
        text2.yProperty().bind(newShape2.centerYProperty().subtract(20));
	// ADD THE SHAPE TO THE CANVAS
	nodes.addAll(newShape1,text1,line,newShape2,text2);	
        newShape1.toFront();
        newShape2.toFront();
        workspace.getLineSelecter().getItems().add(LineName);
        return line;
    }
    public ArrayList<Node> RemoveLine(String name){   
        ArrayList<Node> trash=new ArrayList<>();
        for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext(); ) {
                Node node = iterator.next();          
            if(node instanceof DraggableLineEnd){
                if(((DraggableLineEnd)node).LineName.equalsIgnoreCase(name)){
                    iterator.remove();
                    trash.add(node);
                }
            }
            else if(node instanceof LineWrap){
                    if(((LineWrap)node).LineName.equalsIgnoreCase(name)){
                        iterator.remove();
                        trash.add(node);
                    }
                }                
             else if(node instanceof AbsoluteText){
                      if(((AbsoluteText)node).getText().equalsIgnoreCase(name)){
                        iterator.remove();
                        trash.add(node);
                    }
             }
        }
        return trash;
    }
    public Color EditLine(String oldname,String newName,Color color,boolean circular){
        Color oldColor=currentLineColor;
        currentLineColor=color;
        for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext(); ) 
        {
            Node node = iterator.next();          
            if(node instanceof LineWrap){
                    if(((LineWrap)node).LineName.equalsIgnoreCase(oldname)){
                       ((LineWrap) node).setStroke(color);
                       ((LineWrap)node).LineName=newName;
                        if(circular==true){
                            if(((LineWrap)node).two instanceof DraggableLineEnd){
                                ((LineWrap)node).setNodes(((LineWrap)node).one, ((DraggableLineEnd)((LineWrap)node).two).Sister);
                                ((DraggableLineEnd)((LineWrap)node).two).circular=true;
                                ((DraggableLineEnd)((LineWrap)node).two).Sister.circular=true;
                                ((DraggableLineEnd)((LineWrap)node).two).Sister.setFill(Color.TRANSPARENT);
                                ((DraggableLineEnd)((LineWrap)node).two).Sister.Textlink.setOpacity(0);
                            }
                         }
                        else{
                            if(((LineWrap)node).two instanceof DraggableLineEnd){                              
                                ((LineWrap)node).setNodes(((LineWrap)node).one,((DraggableLineEnd)((LineWrap)node).two).Sister);
                                ((DraggableLineEnd)((LineWrap)node).two).circular=false;
                                ((DraggableLineEnd)((LineWrap)node).two).Sister.circular=false;
                                ((DraggableLineEnd)((LineWrap)node).two).setFill(Color.CHARTREUSE);
                                ((DraggableLineEnd)((LineWrap)node).two).Textlink.setOpacity(1);
                            }
                        }
                    }
            }   
            else if(node instanceof DraggableLineEnd){
                    if(((DraggableLineEnd)node).LineName.equalsIgnoreCase(oldname)){
                       ((DraggableLineEnd)node).LineName=newName;                       
                    }
            }
             else if(node instanceof AbsoluteText){
                      if(((AbsoluteText)node).getText().equalsIgnoreCase(oldname)){
                        ((AbsoluteText)node).setText(newName);
                    }
             }
        }
        return oldColor;
    }
    public void LineEndOpacityEnable(String name,boolean m){     
         MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
         for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext(); ) 
        {
            Node node = iterator.next();           
            if(node instanceof DraggableLineEnd){
                if(((DraggableLineEnd)node).LineName.equalsIgnoreCase(name)){
                    if(m==true)
                       ((DraggableLineEnd)node).setOpacity(1.0);                   
                    
                }
                else
                        ((DraggableLineEnd)node).setOpacity(0.0);
             }          
        }
    }
    
    public Node getSelectedNode() {
	return selectedNode;
    }

    public void setselectedNode(Node initSelectedNode) {
	selectedNode = initSelectedNode;
    }

    public Node selectTopNode(int x, int y) {
	Node node = getTopNode(x, y);
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        if (node != null && node instanceof Draggable) {
	    ((Draggable)node).start(x, y);
	}
	if (node == selectedNode){
	    return node;
        }	
	if (selectedNode != null) {
	    unhighlightShape(selectedNode);
	}
	if (node != null) {
	    highlightShape(node);  
	}
        workspace.loadSelectedNodeSettings(node);
	selectedNode = node;
        return node;
    }

    public Node getTopNode(int x, int y) {
	for (int i = nodes.size() - 1; i >= 0; i--) {
	    Node node = (Node)nodes.get(i);
	    if (node.contains(x, y)) {
                if(node instanceof Group){
                    if(((Group) node).getChildren().get(0) instanceof DraggableStation)
                        return ((Group) node).getChildren().get(0);
                    else
                        return ((Group) node).getChildren().get(1);
                }
                else
                    return node;
	    }
	}
	return null;
    }

    public void addNode(Node nodeToAdd) {
	nodes.add(nodeToAdd);
    }

    public void removeNode(Node nodeToRemove) {
	nodes.remove(nodeToRemove);
    }

    public MapState getState() {
	return state;
    }

    public void setState(MapState initState) {
	state = initState;
    }

    public boolean isInState(MapState testState) {
	return state == testState;
    }
    
    public LineWrap getLineWrap(String name){
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
         for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext(); ) 
        {
            Node node = iterator.next();           
            if(node instanceof LineWrap){
                if(((LineWrap)node).LineName.equalsIgnoreCase(name)){
                   return (LineWrap)node;
                }
                
            }           
        }
         return null;
    }
    public LineWrap getLineWrap(DraggableStation m1, DraggableStation m2){
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
         for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext(); ) 
        {
            Node node = iterator.next();
            if(m2!=null){
                if(node instanceof LineWrap){
                    if(((LineWrap)node).one.equals(m1) && ((LineWrap)node).two.equals(m2) || ((LineWrap)node).one.equals(m2) && ((LineWrap)node).two.equals(m1)){
                       return (LineWrap)node;
                    } 
                }      
            }
            else
            {
                 if(node instanceof LineWrap){
                    if(((LineWrap)node).two.equals(m1)){
                       return (LineWrap)node;
                    } 
                } 
            }
            
        }
         return null;
    }
    public DraggableLineEnd getDraggableLineEnd(String name){
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
         for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext(); ) 
        {
            Node node = iterator.next();           
            if(node instanceof DraggableLineEnd){
                if(((DraggableLineEnd)node).LineName.equalsIgnoreCase(name)){
                   return (DraggableLineEnd)node;
                }              
            }           
        }
        return null;
    }  
    public void MoveBackLine(){
        for(int i =0;i<nodes.size();i++){
                if(nodes.get(i) instanceof LineWrap){
                    ((LineWrap)nodes.get(i)).toBack();
                }
            }
    }
    public boolean ContainsStations(DraggableStation m){
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
         for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext(); ) 
        {
            Node node = iterator.next();           
            if(node instanceof DraggableLineEnd){
                if(((DraggableLineEnd)node).stations.contains(m)){
                   return true;
                }              
            }           
        }
         return false;
    }
    public DraggableStation getStation(String name){
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
         for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext(); ) 
        {
            Node node = iterator.next();           
            if(node instanceof Group){
                if(((Group)node).getChildren().get(0) instanceof DraggableStation && ((DraggableStation)((Group)node).getChildren().get(0)).StationName.equals(name)){
                   return ((DraggableStation)((Group)node).getChildren().get(0));
                }
                else if(((Group)node).getChildren().get(1) instanceof DraggableStation && ((DraggableStation)((Group)node).getChildren().get(1)).StationName.equals(name)){
                   return ((DraggableStation)((Group)node).getChildren().get(1));
                }
            }           
        }
         return null;
    }
    public ArrayList<DraggableStation> getAdjacentNodes(DraggableStation m,Set<DraggableStation> set){
        ArrayList<DraggableStation> adjacent=new ArrayList<>();
        for(int i =0;i<nodes.size();i++){
            if(nodes.get(i) instanceof DraggableLineEnd){
                DraggableLineEnd temp=((DraggableLineEnd)nodes.get(i));
                if(temp.stations.contains(m)){
                            if(temp.stations.indexOf(m)!=0 && !adjacent.contains(temp.stations.get(temp.stations.indexOf(m)-1)) && !set.contains(temp.stations.get(temp.stations.indexOf(m)-1)))
                                adjacent.add(temp.stations.get(temp.stations.indexOf(m)-1));
                            if(temp.stations.indexOf(m)!=temp.stations.size()-1 && !adjacent.contains(temp.stations.get(temp.stations.indexOf(m)+1)) && !set.contains(temp.stations.get(temp.stations.indexOf(m)+1)))
                                adjacent.add(temp.stations.get(temp.stations.indexOf(m)+1));
                            if(temp.circular==true){
                                if(temp.stations.indexOf(m)==0 && !adjacent.contains(temp.stations.get(temp.stations.size()-1)) && !set.contains(temp.stations.get(temp.stations.size()-1)))
                                {
                                    adjacent.add(temp.stations.get(temp.stations.size()-1));
                                }
                                else if(temp.stations.indexOf(m)==temp.stations.size() && !adjacent.contains(temp.stations.get(0)) && !set.contains(temp.stations.get(0)))
                                {
                                    adjacent.add(temp.stations.get(0));
                                }
                            }
               }                   
           }
        }
        return adjacent;
    }
}
