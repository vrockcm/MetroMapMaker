package mmm.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import djf.components.AppDataComponent;
import djf.components.AppFileComponent;
import djf.components.AppWorkspaceComponent;
import static djf.settings.AppPropertyType.WORK_FILE_EXT;
import java.awt.List;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javax.imageio.ImageIO;
import javax.json.JsonException;
import mmm.data.AbsoluteText;
import mmm.data.MapData;
import mmm.data.Draggable;
import static mmm.data.Draggable.IMAGE;
import static mmm.data.Draggable.LINE;
import static mmm.data.Draggable.LINE_END;
import static mmm.data.Draggable.STATION;
import static mmm.data.Draggable.TEXT;
import mmm.data.DraggableImage;
import mmm.data.DraggableLineEnd;
import mmm.data.DraggableStation;
import mmm.data.DraggableText;
import mmm.data.LineWrap;
import mmm.gui.MapWorkspace;

/**
 * This class serves as the file management component for this application,
 * providing all I/O services.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class MapFiles implements AppFileComponent {
    // FOR JSON LOADING
    static final String JSON_BG_COLOR = "background_color";
    static final String JSON_BG_IMAGE = "background_image";
    static final String JSON_RED = "red";
    static final String JSON_GREEN = "green";
    static final String JSON_BLUE = "blue";
    static final String JSON_ALPHA = "alpha";
    static final String JSON_NODES = "nodes";
    static final String JSON_TYPE = "type";
    static final String JSON_LINE_END = "line_end";
    static final String JSON_NAME = "name";
    static final String JSON_IMAGE = "image";
    static final String JSON_TEXT = "text";
    static final String JSON_TEXT_FONT = "font";
    static final String JSON_TEXT_SIZE = "size";
    static final String JSON_TEXT_BOLD = "bold";
    static final String JSON_TEXT_ITALIC = "italic";
    static final String JSON_X = "x";
    static final String JSON_Y = "y";
    static final String JSON_START_X = "startx";
    static final String JSON_START_Y = "starty";
    static final String JSON_END_X = "endx";
    static final String JSON_END_Y = "endy";
    static final String JSON_CONTROL_X = "controlx";
    static final String JSON_CONTROL_Y = "controly";
    static final String JSON_LINE_LEFT = "left";
    static final String JSON_LINE_RIGHT = "right";
    static final String JSON_LINE_THICKNESS = "line_thickness";
    static final String JSON_STATION_TEXT_POSITION = "station_text_postion";
    static final String JSON_STATION_TEXT_ROTATE = "station_text_rotate";
    static final String JSON_STATION_RADIUS = "station_radius";
    static final String JSON_STATIONS = "stations";
    static final String JSON_CIRCULAR = "circular";
    static final String JSON_LINES = "lines";
    static final String JSON_TEXT_COLOR = "text_color";
    static final String JSON_COLOR = "color";
    static final String JSON_STATION_NAMES = "station_names"; 
    static final String DEFAULT_DOCTYPE_DECLARATION = "<!doctype html>\n";
    static final String DEFAULT_ATTRIBUTE_VALUE = "";
 
    /**
     * This method is for saving user work, which in the case of this
     * application means the data that together draws the logo.
     * 
     * @param data The data management component for this application.
     * 
     * @param filePath Path (including file name/extension) to where
     * to save the data to.
     * 
     * @throws IOException Thrown should there be an error writing 
     * out data to the file.
     */
    @Override
    public void saveData(AppDataComponent data, String filePath) throws IOException {
	// GET THE DATA
	MapData dataManager = (MapData)data;
	
	// FIRST THE BACKGROUND COLOR
	Color bgColor = dataManager.getBackgroundColor();
        String bgimage = dataManager.getBackgroundImage();
	JsonObject bgColorJson = makeJsonColorObject(bgColor);

	// NOW BUILD THE JSON OBJCTS TO SAVE
	JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
	ObservableList<Node> nodes = dataManager.getNodes();
	for (Node node : nodes) {
            if (node != null && node instanceof Group) {
                
                if(((Group)node).getChildren().get(0) instanceof DraggableStation){
                    node=((Group)node).getChildren().get(0);                   
                }
                else{
                    node=((Group)node).getChildren().get(1);   
                }                     
                DraggableStation n=(DraggableStation)node;
                String type = n.getShapeType();
                int x =(int) n.getCenterX();
                int y =(int) n.getCenterY();
                Color fillColor = (Color)(n.getFill());
                String stationName=n.StationName;
                String stationTextPos=n.StationTextPosition;
                int TextRotate=n.StationTextRotate;
                int Radius=(int)n.getRadius();
                JsonObject fillColorJson = makeJsonColorObject(fillColor);
                int Textpos=0;
                if(n.getGroup().getChildren().get(0) instanceof Text)
                   Textpos=0;
                if(n.getGroup().getChildren().get(1) instanceof Text)
                   Textpos=1;
                String FontFamily=((AbsoluteText)n.getGroup().getChildren().get(Textpos)).getFont().getFamily();
                double FontSize=((AbsoluteText)n.getGroup().getChildren().get(Textpos)).getFont().getSize();
                boolean bold=((AbsoluteText)n.getGroup().getChildren().get(Textpos)).isBold;
                boolean italic=((AbsoluteText)n.getGroup().getChildren().get(Textpos)).isItalic;
                JsonObject TextColorJson = makeJsonColorObject((Color)((AbsoluteText)n.getGroup().getChildren().get(Textpos)).getFill());
                JsonObject nodeJson = Json.createObjectBuilder()
		    .add(JSON_TYPE, type)
		    .add(JSON_X, x)
		    .add(JSON_Y, y)
		    .add(JSON_COLOR, fillColorJson)
		    .add(JSON_NAME, stationName)
		    .add(JSON_STATION_TEXT_POSITION,stationTextPos)
                    .add(JSON_STATION_TEXT_ROTATE,TextRotate) 
                    .add(JSON_TEXT_FONT, FontFamily)
                    .add(JSON_TEXT_SIZE, FontSize)
                    .add(JSON_TEXT_BOLD, bold)
                    .add(JSON_TEXT_COLOR,TextColorJson)
                    .add(JSON_TEXT_ITALIC, italic)    
                    .add(JSON_STATION_RADIUS,Radius).build();
	    arrayBuilder.add(nodeJson);
            }
            else if(node instanceof DraggableLineEnd)
            {   
                DraggableLineEnd n=(DraggableLineEnd)node;
                String type = n.getShapeType();
                int x =(int) n.getCenterX();
                int y =(int) n.getCenterY();
                JsonArrayBuilder Jsonstations=Json.createArrayBuilder();
                for(int i=0;i<n.stations.size();i++){
                    Jsonstations.add(n.stations.get(i).StationName);
                }
                String LineName=n.LineName;
                String FontFamily=((AbsoluteText)n.Textlink).getFont().getFamily();
                double FontSize=((AbsoluteText)n.Textlink).getFont().getSize();
                boolean bold=((AbsoluteText)n.Textlink).isBold;
                boolean italic=((AbsoluteText)n.Textlink).isItalic;
                JsonObject TextColorJson = makeJsonColorObject((Color)((AbsoluteText)n.Textlink).getFill());
                JsonObject shapeJson = Json.createObjectBuilder()
		    .add(JSON_TYPE, type)
		    .add(JSON_X, x)
		    .add(JSON_Y, y)
                    .add(JSON_STATIONS,Jsonstations)
                    .add(JSON_TEXT_FONT, FontFamily)
                    .add(JSON_TEXT_SIZE, FontSize)
                    .add(JSON_TEXT_BOLD, bold)
                    .add(JSON_TEXT_COLOR,TextColorJson)
                    .add(JSON_TEXT_ITALIC, italic)    
		    .add(JSON_NAME,LineName ).build();
                arrayBuilder.add(shapeJson);

            }
            else if(node instanceof LineWrap)
            {        
               LineWrap n=(LineWrap)node;
                String type = n.getShapeType();
                int startx = (int)n.getStartX();
                int starty =(int) n.getStartY();
                int endx =(int) n.getEndX();
                int endy = (int)n.getEndY();
                int controlx=(int)n.getControlX();
                int controly=(int)n.getControlY();
                String LineName=n.LineName;
                String left="";
                String right="";
                if(n.one instanceof DraggableStation)
                    left=((DraggableStation)n.one).StationName;
                else if(n.one instanceof DraggableLineEnd)
                    left=((DraggableLineEnd)n.one).LineName;
                if(n.two instanceof DraggableStation)
                    right=((DraggableStation)n.two).StationName;
                else if(n.two instanceof DraggableLineEnd)
                    right=((DraggableLineEnd)n.two).LineName;
                int LineThickness=(int)n.getStrokeWidth();
                Color LineColor=(Color)n.getStroke();
                JsonObject jsonLineColor;
                if(LineColor!=null)
                    jsonLineColor = makeJsonColorObject(LineColor);
                else
                     jsonLineColor = makeJsonColorObject(Color.BLACK);
                
                JsonObject shapeJson = Json.createObjectBuilder()
		    .add(JSON_TYPE, type)
		    .add(JSON_START_X, startx)
                    .add(JSON_START_Y, starty)
                    .add(JSON_END_X, endx)
                    .add(JSON_END_Y, endy)
                    .add(JSON_CONTROL_X, controlx)
                    .add(JSON_CONTROL_Y, controly)
		    .add(JSON_NAME, LineName)
                    .add(JSON_LINE_LEFT,left)
                    .add(JSON_LINE_RIGHT,right)
                    .add(JSON_LINE_THICKNESS,LineThickness)
		    .add(JSON_COLOR,jsonLineColor).build();
                arrayBuilder.add(shapeJson);

            }
            else if(node instanceof DraggableText)
            {                
                DraggableText n=(DraggableText)node;
                String type = n.getShapeType();
                Color fillColor=((Color)n.getFill());
                String Font=n.getFont().getFamily();
                double FontSize=n.getFont().getSize();
                int x =(int) n.getX();
                int y = (int)n.getY();
                boolean bold=n.isBold;
                boolean italic=n.isItalic;
                JsonObject fillColorJson = makeJsonColorObject(fillColor);
                JsonObject shapeJson = Json.createObjectBuilder()
		    .add(JSON_TYPE, type)
		    .add(JSON_X, x)
		    .add(JSON_Y, y)
                    .add(JSON_TEXT_FONT, Font)
                    .add(JSON_TEXT_SIZE, FontSize)
                    .add(JSON_TEXT_BOLD, bold)
                    .add(JSON_TEXT_ITALIC, italic)
                    .add(JSON_TEXT,n.getText())
		    .add(JSON_TEXT_COLOR, fillColorJson).build();
                arrayBuilder.add(shapeJson);

            }
            else if(node instanceof DraggableImage)
            {        
                DraggableImage n=(DraggableImage)node;
                String type = n.getShapeType();
                String image=(n.getpath());
                int x =(int) n.getX();
                int y =(int) n.getY();
                JsonObject shapeJson = Json.createObjectBuilder()
                        .add(JSON_TYPE, type)
                        .add(JSON_X, x)
                        .add(JSON_IMAGE,image)
                        .add(JSON_Y, y).build();
                arrayBuilder.add(shapeJson);
            }
	}      
	JsonArray nodesArray = arrayBuilder.build();
	// THEN PUT IT ALL TOGETHER IN A JsonObject
	JsonObject dataManagerJSO = Json.createObjectBuilder()
		.add(JSON_BG_COLOR, bgColorJson)
                .add(JSON_BG_IMAGE, bgimage)
		.add(JSON_NODES, nodesArray)
		.build();
	
	// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
	Map<String, Object> properties = new HashMap<>(1);
	properties.put(JsonGenerator.PRETTY_PRINTING, true);
	JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
	StringWriter sw = new StringWriter();
	JsonWriter jsonWriter = writerFactory.createWriter(sw);
	jsonWriter.writeObject(dataManagerJSO);
	jsonWriter.close();

	// INIT THE WRITER
	OutputStream os = new FileOutputStream(filePath);
	JsonWriter jsonFileWriter = Json.createWriter(os);
	jsonFileWriter.writeObject(dataManagerJSO);
	String prettyPrinted = sw.toString();
	PrintWriter pw = new PrintWriter(filePath);
	pw.write(prettyPrinted);
	pw.close();
    }
    
    private JsonObject makeJsonColorObject(Color color) {
	JsonObject colorJson = Json.createObjectBuilder()
		.add(JSON_RED, color.getRed())
		.add(JSON_GREEN, color.getGreen())
		.add(JSON_BLUE, color.getBlue())
		.add(JSON_ALPHA, color.getOpacity()).build();
	return colorJson;
    }
      
    /**
     * This method loads data from a JSON formatted file into the data 
     * management component and then forces the updating of the workspace
     * such that the user may edit the data.
     * 
     * @param data Data management component where we'll load the file into.
     * 
     * @param filePath Path (including file name/extension) to where
     * to load the data from.
     * 
     * @throws IOException Thrown should there be an error reading
     * in data from the file.
     */
    @Override
    public void loadData(AppWorkspaceComponent workspace,AppDataComponent data, String filePath) throws IOException {
	// CLEAR THE OLD DATA OUT
        MapWorkspace work=(MapWorkspace)workspace;
	MapData dataManager = (MapData)data;
	dataManager.resetData();
	
	// LOAD THE JSON FILE WITH ALL THE DATA
	JsonObject json = loadJSONFile(filePath);
	
	// LOAD THE BACKGROUND COLOR
	Color bgColor = loadColor(json, JSON_BG_COLOR);
        String bgImage = json.getString(JSON_BG_IMAGE);
	
	
	// AND NOW LOAD ALL THE SHAPES
	JsonArray jsonNodeArray = json.getJsonArray(JSON_NODES);
        if(!bgImage.isEmpty()){
            dataManager.setBackgroundColor(bgColor);
            dataManager.setBackgroundImage(bgImage);
        }
        else 
            dataManager.setBackgroundColor(bgColor);
        ArrayList<JsonObject> jsonValues = new ArrayList<JsonObject>();
        for (int i = 0; i < jsonNodeArray.size(); i++) {
            jsonValues.add(jsonNodeArray.getJsonObject(i));
        }
        ArrayList<String> classes = new ArrayList();
        classes.add(STATION);
        classes.add(LINE_END);
        classes.add(LINE);       
	Collections.sort(jsonValues, new Comparator<JsonObject>() {
            
            @Override
            public int compare(JsonObject o1, JsonObject o2) {
                String type1=o1.getString(JSON_TYPE);
                String type2=o2.getString(JSON_TYPE);
                Integer index1=classes.indexOf(type1);
                Integer index2=classes.indexOf(type2);
                return index1.compareTo(index2);
            }
        });
        for(int i=0;i<jsonValues.size();i++){
            Node node=loadNode(jsonValues.get(i),jsonValues,dataManager);
            if(node!=null){
            if(node instanceof DraggableLineEnd){
                dataManager.addNode(node);
                dataManager.addNode(((DraggableLineEnd)node).Textlink);
                if(!work.getLineSelecter().getItems().contains(((DraggableLineEnd) node).LineName)){
                    work.getLineSelecter().getItems().add(((DraggableLineEnd) node).LineName);
                    work.getLineSelecter().getSelectionModel().selectLast();
                }
            }
            else if(node instanceof DraggableStation){
                dataManager.addNode(((DraggableStation) node).getGroup());
                work.getStationSelecter().getItems().add(((DraggableStation) node).StationName);
                work.getStationSelecter().getSelectionModel().selectLast();
                work.getToStationSelecter().getItems().add(((DraggableStation) node).StationName);
                work.getToStationSelecter().getSelectionModel().selectLast();
                work.getFromStationSelecter().getItems().add(((DraggableStation) node).StationName);
                work.getFromStationSelecter().getSelectionModel().selectLast();
            }
            else{
                dataManager.addNode(node);
                if(node instanceof LineWrap)
                    node.toBack();
             }
           }
        }
        work.loadSelectedNodeSettings(dataManager.getDraggableLineEnd((String)work.getLineSelecter().getSelectionModel().getSelectedItem()));
    }
    
    private double getDataAsDouble(JsonObject json, String dataName) {
	JsonValue value = json.get(dataName);
	JsonNumber number = (JsonNumber)value;
	return number.bigDecimalValue().doubleValue();	
    }
    
    private Node loadNode(JsonObject jsonNode,ArrayList<JsonObject> jsonValues,MapData dataManager) {
	// FIRST BUILD THE PROPER SHAPE TYPE
	String type = jsonNode.getString(JSON_TYPE);
	if (type.equals(STATION)) {           
           String StationName = jsonNode.getString(JSON_NAME);	
	   DraggableStation node=new DraggableStation(StationName);
           node.setCenterX(jsonNode.getInt(JSON_X));
           node.setCenterY(jsonNode.getInt(JSON_Y));
           node.setFill(loadColor(jsonNode,JSON_COLOR));
           node.setRadius(jsonNode.getInt(JSON_STATION_RADIUS));
           node.setLabelLocation(jsonNode.getString(JSON_STATION_TEXT_POSITION));
           node.setRotate(jsonNode.getInt(JSON_STATION_TEXT_ROTATE));
           node.text.setFill(loadColor(jsonNode, JSON_TEXT_COLOR));
           boolean bold=jsonNode.getBoolean(JSON_TEXT_BOLD);
           boolean italic=jsonNode.getBoolean(JSON_TEXT_ITALIC);
           node.text.isBold=bold;
           node.text.isItalic=italic;
            if(bold==true && italic==true)
                node.text.setFont(Font.font(jsonNode.getString(JSON_TEXT_FONT), FontWeight.BOLD,FontPosture.ITALIC,  getDataAsDouble(jsonNode,JSON_TEXT_SIZE)));
            else if(bold==true && italic==false){
                node.text.setFont(Font.font(jsonNode.getString(JSON_TEXT_FONT), FontWeight.BOLD,FontPosture.REGULAR, getDataAsDouble(jsonNode,JSON_TEXT_SIZE)));
             }
            else if(bold==false && italic==true){
                node.text.setFont(Font.font(jsonNode.getString(JSON_TEXT_FONT), FontWeight.NORMAL, FontPosture.ITALIC, getDataAsDouble(jsonNode,JSON_TEXT_SIZE)));
             } 
            else{
                node.text.setFont(Font.font(jsonNode.getString(JSON_TEXT_FONT), FontWeight.NORMAL, FontPosture.REGULAR, getDataAsDouble(jsonNode,JSON_TEXT_SIZE)));
            }
            return node;
        }
        else if(type.equals(LINE_END)){
           String LineName = jsonNode.getString(JSON_NAME);	
           AbsoluteText text=new AbsoluteText(LineName);
	   DraggableLineEnd node=new DraggableLineEnd(LineName,text);
           node.setCenterX(jsonNode.getInt(JSON_X));
           node.setCenterY(jsonNode.getInt(JSON_Y));
           JsonArray jsonStationArray = jsonNode.getJsonArray(JSON_STATIONS);           
           for (int i = 0; i < jsonStationArray.size(); i++) {
                String jsonStation = jsonStationArray.getString(i);
                for(int j=0;j<dataManager.getNodes().size();j++){
                   if(dataManager.getNodes().get(j) instanceof Group){
                        if(((Group)dataManager.getNodes().get(j)).getChildren().get(0) instanceof DraggableStation){
                             if(((DraggableStation)((Group)dataManager.getNodes().get(j)).getChildren().get(0)).StationName.equals(jsonStation)){
                                node.stations.add(((DraggableStation)((Group)dataManager.getNodes().get(j)).getChildren().get(0)));                  
                                break;
                              }
                        }
                        else{
                            if(((DraggableStation)((Group)dataManager.getNodes().get(j)).getChildren().get(1)).StationName.equals(jsonStation)){
                                 node.stations.add(((DraggableStation)((Group)dataManager.getNodes().get(j)).getChildren().get(1)));
                                break;
                            }
                        }
                    } 
                }
            }
           boolean bold=jsonNode.getBoolean(JSON_TEXT_BOLD);
           boolean italic=jsonNode.getBoolean(JSON_TEXT_ITALIC);
           node.Textlink.isBold=bold;
           node.Textlink.isItalic=italic;
            if(bold==true && italic==true)
                node.Textlink.setFont(Font.font(jsonNode.getString(JSON_TEXT_FONT), FontWeight.BOLD,FontPosture.ITALIC,  getDataAsDouble(jsonNode,JSON_TEXT_SIZE)));
            else if(bold==true && italic==false){
                node.Textlink.setFont(Font.font(jsonNode.getString(JSON_TEXT_FONT), FontWeight.BOLD,FontPosture.REGULAR, getDataAsDouble(jsonNode,JSON_TEXT_SIZE)));
             }
            else if(bold==false && italic==true){
                node.Textlink.setFont(Font.font(jsonNode.getString(JSON_TEXT_FONT), FontWeight.NORMAL, FontPosture.ITALIC, getDataAsDouble(jsonNode,JSON_TEXT_SIZE)));
             } 
            else{
                node.Textlink.setFont(Font.font(jsonNode.getString(JSON_TEXT_FONT), FontWeight.NORMAL, FontPosture.REGULAR, getDataAsDouble(jsonNode,JSON_TEXT_SIZE)));
            }
            node.Textlink.setFill(loadColor(jsonNode, JSON_TEXT_COLOR));
            DraggableLineEnd ob1=null;
            for (Node n : dataManager.getNodes()) {
               if (n instanceof DraggableLineEnd && ((DraggableLineEnd)n).LineName.equals(LineName)) {
                   ob1=(DraggableLineEnd)n;
                   break;
               }
             }
            if(ob1!=null){
                ob1.Sister=node;
                node.Sister=ob1;
            }    
            return node;
        }
        else if(type.equals(LINE)){
           String LineName = jsonNode.getString(JSON_NAME);	
	   LineWrap node=new LineWrap(LineName); 
           node.setStartX(jsonNode.getInt(JSON_START_X));
           node.setStartY(jsonNode.getInt(JSON_START_Y));
           node.setEndX(jsonNode.getInt(JSON_END_X));
           node.setEndY(jsonNode.getInt(JSON_END_Y));
           
           String left=jsonNode.getString(JSON_LINE_LEFT);
           String right=jsonNode.getString(JSON_LINE_RIGHT);
           node.setFill(Color.TRANSPARENT);  
           Circle m=(Circle)dataManager.getTopNode((int)node.getStartX(),(int)node.getStartY());
           Circle m1=(Circle)dataManager.getTopNode((int)node.getEndX(),(int)node.getEndY());          
           node.startXProperty().bind(((Circle)dataManager.getTopNode((int)node.getStartX(),(int)node.getStartY())).centerXProperty());
           node.startYProperty().bind(((Circle)dataManager.getTopNode((int)node.getStartX(),(int)node.getStartY())).centerYProperty());
           node.endXProperty().bind(((Circle)dataManager.getTopNode((int)node.getEndX(),(int)node.getEndY())).centerXProperty());
           node.endYProperty().bind(((Circle)dataManager.getTopNode((int)node.getEndX(),(int)node.getEndY())).centerYProperty());
           if(m.getCenterX()==jsonNode.getInt(JSON_CONTROL_X) && m.getCenterY()==jsonNode.getInt(JSON_CONTROL_Y))
           {
              node.controlXProperty().bind(m.centerXProperty());
              node.controlYProperty().bind(m.centerYProperty());
           }
           else{
               node.setControlX(jsonNode.getInt(JSON_CONTROL_X));
                node.setControlY(jsonNode.getInt(JSON_CONTROL_Y));
           }           
           node.one=m;
           node.two=m1;
            node.setStrokeWidth(jsonNode.getInt(JSON_LINE_THICKNESS));
            node.setStroke(loadColor(jsonNode,JSON_COLOR));
            return node;
            }
         else if(type.equals(IMAGE)){
             String image = jsonNode.getString(JSON_IMAGE);
             DraggableImage node=new DraggableImage(image); 
             node.setX(getDataAsDouble(jsonNode,JSON_X));
             node.setY(getDataAsDouble(jsonNode,JSON_Y));
             return node;
         }
         else if(type.equals(TEXT)){
            String text = jsonNode.getString(JSON_TEXT);
            DraggableText node=new DraggableText(text);
            node.setX(getDataAsDouble(jsonNode, JSON_X));
            node.setY(getDataAsDouble(jsonNode, JSON_Y));
            boolean bold=jsonNode.getBoolean(JSON_TEXT_BOLD);
            boolean italic=jsonNode.getBoolean(JSON_TEXT_ITALIC);
            node.isBold=bold;
            node.isItalic=italic;
             if(bold==true && italic==true)
                 node.setFont(Font.font(jsonNode.getString(JSON_TEXT_FONT), FontWeight.BOLD,FontPosture.ITALIC,  getDataAsDouble(jsonNode,JSON_TEXT_SIZE)));
             else if(bold==true && italic==false){
                 node.setFont(Font.font(jsonNode.getString(JSON_TEXT_FONT), FontWeight.BOLD,FontPosture.REGULAR, getDataAsDouble(jsonNode,JSON_TEXT_SIZE)));
              }
             else if(bold==false && italic==true){
                 node.setFont(Font.font(jsonNode.getString(JSON_TEXT_FONT), FontWeight.NORMAL, FontPosture.ITALIC, getDataAsDouble(jsonNode,JSON_TEXT_SIZE)));
              } 
             else{
                 node.setFont(Font.font(jsonNode.getString(JSON_TEXT_FONT), FontWeight.NORMAL, FontPosture.REGULAR, getDataAsDouble(jsonNode,JSON_TEXT_SIZE)));
             }
             node.setFill(loadColor(jsonNode, JSON_TEXT_COLOR));
             return node;
             
         }
        return null;
    } 
   
    private Color loadColor(JsonObject json, String colorToGet) {
	JsonObject jsonColor = json.getJsonObject(colorToGet);
	double red = getDataAsDouble(jsonColor, JSON_RED);
	double green = getDataAsDouble(jsonColor, JSON_GREEN);
	double blue = getDataAsDouble(jsonColor, JSON_BLUE);
	double alpha = getDataAsDouble(jsonColor, JSON_ALPHA);
	Color loadedColor = new Color(red, green, blue, alpha);
	return loadedColor;
    }

    // HELPER METHOD FOR LOADING DATA FROM A JSON FORMAT
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
	InputStream is = new FileInputStream(jsonFilePath);
	JsonReader jsonReader = Json.createReader(is);
	JsonObject json = jsonReader.readObject();
	jsonReader.close();
	is.close();
	return json;
    }

    @Override
    public void exportData(AppWorkspaceComponent workspace,AppDataComponent data, String filePath) throws IOException {
       // GET THE DATA
	MapData dataManager = (MapData)data;
	// NOW BUILD THE JSON OBJCTS TO SAVE
        String MapName=filePath.replace(".\\export\\", "");
        MapName=MapName+" Metro";
	JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
	ObservableList<Node> nodes = dataManager.getNodes();
        ArrayList<Node> contain=new ArrayList<>();
	for (Node node : nodes) {
            if(node instanceof DraggableLineEnd){
               DraggableLineEnd n=(DraggableLineEnd)node;
               boolean once=true;
               for (int i=0;i<contain.size();i++) {
                    if(contain.get(i) instanceof DraggableLineEnd && ((DraggableLineEnd)contain.get(i)).LineName.equals(n.LineName)){
                        once=false;
                    }
                }    
                if(once==true){                    
                    JsonArrayBuilder Jsonstations=Json.createArrayBuilder();
                    for(int i=0;i<n.stations.size();i++){
                        Jsonstations.add(n.stations.get(i).StationName);
                    }
                    String LineName=n.LineName;
                    Color LineColor=null;
                    for (Node node2 : nodes) {
                        if(node2 instanceof LineWrap && ((LineWrap)node2).LineName.equals(n.LineName)){
                            LineColor=(Color)((LineWrap)node2).getStroke();
                        }                  
                    }
                    JsonObject LineColorJson = makeJsonColorObject(LineColor);
                    JsonObject shapeJson = Json.createObjectBuilder()
                        .add(JSON_NAME,LineName)
                        .add(JSON_CIRCULAR,((DraggableLineEnd) node).circular)
                        .add(JSON_COLOR, LineColorJson)
                        .add(JSON_STATION_NAMES,Jsonstations).build();
                    contain.add(node);
                    arrayBuilder.add(shapeJson);                 
               }
            }
        }
	JsonArray LineArray = arrayBuilder.build();       
        JsonArrayBuilder arrayBuilder1 = Json.createArrayBuilder();     
	for (Node node : nodes) {
            if(node instanceof Group){
                DraggableStation n;
                if(((Group)node).getChildren().get(0) instanceof DraggableStation)
                    n=(DraggableStation)((Group)node).getChildren().get(0);
                else
                    n=(DraggableStation)((Group)node).getChildren().get(1);
                JsonObject shapeJson = Json.createObjectBuilder()
                    .add(JSON_NAME,n.StationName)
                    .add(JSON_X,(int)n.getCenterX())
                    .add(JSON_Y,(int)n.getCenterY()).build();
                arrayBuilder1.add(shapeJson);                
            }                 
        }
	JsonArray StationArray = arrayBuilder1.build();   
	// THEN PUT IT ALL TOGETHER IN A JsonObject
	JsonObject dataManagerJSO = Json.createObjectBuilder()
		.add(JSON_NAME,MapName)
		.add(JSON_LINES, LineArray)
                .add(JSON_STATIONS, StationArray)
		.build();
	
	// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
	Map<String, Object> properties = new HashMap<>(1);
	properties.put(JsonGenerator.PRETTY_PRINTING, true);
	JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
	StringWriter sw = new StringWriter();
	JsonWriter jsonWriter = writerFactory.createWriter(sw);
	jsonWriter.writeObject(dataManagerJSO);
	jsonWriter.close();

        File file=new File(filePath+"\\"+MapName+".json");
        file.createNewFile();
	// INIT THE WRITER
	OutputStream os = new FileOutputStream(filePath+"\\"+MapName+".json");
	JsonWriter jsonFileWriter = Json.createWriter(os);
	jsonFileWriter.writeObject(dataManagerJSO);
	String prettyPrinted = sw.toString();
	PrintWriter pw = new PrintWriter(filePath+"\\"+MapName+".json");
	pw.write(prettyPrinted);
	pw.close();
        MapWorkspace work = (MapWorkspace)workspace;
	Pane canvas = work.getCanvas();
	WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
	file = new File(filePath+"\\"+MapName+".png");
	try {
	    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
	}
	catch(IOException ioe) {
	    ioe.printStackTrace();
	}
    }

    @Override
    public void importData(AppDataComponent data, String filePath) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
