package mmm.gui;

import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import mmm.data.MapData;
import mmm.data.MapState;
import djf.AppTemplate;
import static djf.settings.AppPropertyType.LOAD_ERROR_MESSAGE;
import static djf.settings.AppPropertyType.LOAD_ERROR_TITLE;
import static djf.settings.AppPropertyType.LOAD_WORK_TITLE;
import static djf.settings.AppPropertyType.SAVE_UNSAVED_WORK_MESSAGE;
import static djf.settings.AppPropertyType.SAVE_UNSAVED_WORK_TITLE;
import static djf.settings.AppStartupConstants.PATH_IMAGES;
import static djf.settings.AppStartupConstants.PATH_WORK;
import djf.ui.AppMessageDialogSingleton;
import djf.ui.AppYesNoCancelDialogSingleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import mmm.data.AbsoluteText;
import mmm.data.DraggableImage;
import mmm.data.DraggableLineEnd;
import mmm.data.DraggableStation;
import mmm.data.DraggableText;
import mmm.data.LineWrap;
import mmm.data.Transaction;
import properties_manager.PropertiesManager;

/**
 * This class responds to interactions with other UI logo editing controls.
 * 
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class MapProcessController {
    AppTemplate app;
    MapData dataManager;
    MapWorkspace workspace;
    
    public MapProcessController(AppTemplate initApp) {
	app = initApp;
	dataManager = (MapData)app.getDataComponent();
    }
    public void processSelectLine(){
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        String name=workspace.LineSelector.getSelectionModel().getSelectedItem();
        LineWrap n=dataManager.getLineWrap(name);
        if(n!=null){
           workspace.loadSelectedNodeSettings(n);
        }
        
    }
   public void processEditLine(){
      MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
      Dialog<ButtonType> dialog = new Dialog<>();
      dialog.setTitle("Metro Map Maker - Metro Line Details");
      dialog.setHeaderText("Metro Line Details");
      dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
      VBox box=new VBox();  
      box.setSpacing(20);
      dialog.getDialogPane().setPrefSize(480, 320);
      dialog.setResizable(true);
      TextField LineName = new TextField(workspace.LineSelector.getSelectionModel().getSelectedItem());
      ColorPicker LineColorPicker = null;
      for(int i=0;i<dataManager.getNodes().size();i++){
          if(dataManager.getNodes().get(i) instanceof LineWrap){
              if(workspace.LineSelector.getSelectionModel().getSelectedItem().equals(((LineWrap)dataManager.getNodes().get(i)).getLineName())){
                  LineColorPicker=new ColorPicker((Color)((LineWrap)dataManager.getNodes().get(i)).getStroke());
                  break;
              }
          }
      }      
      if(LineColorPicker==null){
          LineColorPicker=new ColorPicker(dataManager.getLineColor());
      }
      LineName.setPromptText("Line Name");
      CheckBox Circular=new CheckBox("Circular");
      Circular.setSelected(false);
      box.getChildren().addAll(LineName,LineColorPicker,Circular);
      dialog.getDialogPane().setContent(box);
      Optional<ButtonType> result = dialog.showAndWait();
      Color oldLineColor;
      if (result.get() == ButtonType.OK){
           if(!LineName.getText().isEmpty()){
               Transaction t=new Transaction(dataManager.getDraggableLineEnd(workspace.LineSelector.getSelectionModel().getSelectedItem()),dataManager.getBackgroundColor(),app,null);
               if(Circular.isSelected()){                
                   oldLineColor= dataManager.EditLine(workspace.LineSelector.getSelectionModel().getSelectedItem(),LineName.getText(),LineColorPicker.getValue(),true);                   
               }
               else{
                  oldLineColor=dataManager.EditLine(workspace.LineSelector.getSelectionModel().getSelectedItem(),LineName.getText(),LineColorPicker.getValue(),false); 
               }
                workspace.LineSelector.getSelectionModel().getSelectedItem().replace(workspace.LineSelector.getSelectionModel().getSelectedItem(),LineName.getText());
                workspace.LineEdit.setStyle("-fx-background-color: "+LineColorPicker.getValue().toString().replace("0x", "#"));
                t.setAfterAction(dataManager.getDraggableLineEnd(workspace.LineSelector.getSelectionModel().getSelectedItem()),dataManager.getBackgroundColor(),app, null);
                workspace.AddTransaction(t);
                t.setEDITLINE(true);
                t.setFillColor(oldLineColor);
                t.setAfterFillColor(LineColorPicker.getValue());
           } 
      }
      else {
            dialog.close();
        }
      
   }
   public void processAddLine(){
      MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();     
      Dialog<ButtonType> dialog = new Dialog<>();
      dialog.setTitle("Metro Map Maker - Metro Line Details");
      dialog.setHeaderText("Metro Line Details");
      dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
      VBox box=new VBox();  
      box.setSpacing(20);
      dialog.getDialogPane().setPrefSize(480, 320);
      dialog.setResizable(true);
      TextField LineName = new TextField();
      ColorPicker LineColorPicker=new ColorPicker(dataManager.getLineColor());
      LineName.setPromptText("Line Name");
      box.getChildren().addAll(LineName,LineColorPicker);
      dialog.getDialogPane().setContent(box);
      Optional<ButtonType> result = dialog.showAndWait();
      if (result.get() == ButtonType.OK){
           if(!LineName.getText().isEmpty()){
               if(workspace.LineSelector.getItems().stream().anyMatch(LineName.getText()::equalsIgnoreCase))
               {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Line Duplicate Error");
                    alert.setHeaderText("Line Duplicate");
                    alert.setContentText("The Line name you entered already exists");
                    alert.showAndWait();
               }
               else {
                Transaction t=new Transaction(null,dataManager.getBackgroundColor(),app,null);
                Node returnNode=dataManager.initNewLine(LineName.getText(),LineColorPicker.getValue());
                t.setAfterAction(returnNode,dataManager.getBackgroundColor(),app, null);
                workspace.AddTransaction(t);
                workspace.LineEdit.setStyle("-fx-background-color: "+LineColorPicker.getValue().toString().replace("0x", "#"));
                workspace.LineSelector.getSelectionModel().selectLast();
               }
           }
        } else {
            dialog.close();
        }
      
   }
   public void processRemoveLine(){
       MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
       Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Line Removal Confirmation");
        alert.setHeaderText("Confimation");
        alert.setContentText("Are you sure you want to remove this line?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
           
           ArrayList<Node> trash=dataManager.RemoveLine((String)workspace.LineSelector.getSelectionModel().getSelectedItem());
           Transaction t=new Transaction(null,dataManager.getBackgroundColor(),app,trash);
           t.setAfterAction(null, dataManager.getBackgroundColor(), app, null);
           workspace.AddTransaction(t);
           workspace.getLineSelecter().getItems().remove(workspace.LineSelector.getSelectionModel().getSelectedItem());
           workspace.LineSelector.getSelectionModel().selectNext();
        } else {
            alert.close();
        }
       
   }
   public void processAddStationToLine(){
        Scene scene = app.getGUI().getPrimaryScene();
        scene.setCursor(Cursor.MOVE);
        dataManager.setState(MapState.ADDING_STATION);
          
   }
   public void processRemoveStationFromLine(){
       Scene scene = app.getGUI().getPrimaryScene();
       scene.setCursor(Cursor.MOVE);
       dataManager.setState(MapState.REMOVING_STATION);
   }
   public void processListAll(){
       workspace = (MapWorkspace)app.getWorkspaceComponent();
       String selected=(String)workspace.getLineSelecter().getSelectionModel().getSelectedItem();
       DraggableLineEnd m =dataManager.getDraggableLineEnd(selected);
       Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Metro Map Maker - Metro Line Stops");
        alert.setHeaderText(m.LineName+" Line Stops");
        String listString="";
        if(!m.stations.isEmpty()){
         for (Iterator<DraggableStation> iterator = m.stations.iterator(); iterator.hasNext(); ) 
        {
            DraggableStation node = iterator.next();           
            listString=listString+"\n \u2022 "+node.StationName;
        }
        }
        else
            listString="No stations on the Line";
        
        alert.setContentText(listString);
        alert.showAndWait();
   }
   public void processSelectLineThickness(){
        workspace = (MapWorkspace)app.getWorkspaceComponent();
        Transaction t=new Transaction(dataManager.getLineWrap((String)workspace.getLineSelecter().getSelectionModel().getSelectedItem()),dataManager.getBackgroundColor(),app,null);
        int lineThickness = (int)workspace.getLineThicknessSlider().getValue();
        dataManager.setCurrentLineThickness(lineThickness,(String)workspace.getLineSelecter().getSelectionModel().getSelectedItem());
        t.setAfterAction(dataManager.getLineWrap((String)workspace.getLineSelecter().getSelectionModel().getSelectedItem()),dataManager.getBackgroundColor(),app,null);
        t.setLINETHICKNESSCHANGE(true);
        workspace.AddTransaction(t);
       
    }
   public void processChangeStationColor(){
       workspace = (MapWorkspace)app.getWorkspaceComponent();
       if(dataManager.getSelectedNode() instanceof DraggableStation){
           Transaction t=new Transaction(dataManager.getSelectedNode(),dataManager.getBackgroundColor(),app,null);
           t.setAfterAction(dataManager.getSelectedNode(),dataManager.getBackgroundColor(),app,null);
           t.setSTATIONCOLORCHANGE(true);
           t.setAfterStationFillColor(workspace.getStationColorPicker().getValue());
           workspace.AddTransaction(t);
       }
       dataManager.setStationColor(workspace.getStationColorPicker().getValue());  
    }
   
   public void processAddStation(){
       workspace = (MapWorkspace)app.getWorkspaceComponent();
       TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Metro Map Maker - Add Station Details");
        dialog.setHeaderText("Station Details");
        dialog.setContentText("Please enter station name:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            if(workspace.StationSelector.getItems().stream().anyMatch(result.get()::equalsIgnoreCase))
               {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Station Duplicate Error");
                    alert.setHeaderText("Station Duplicate Found");
                    alert.setContentText("The Station name you entered already exists. You can use the same station for other lines.");
                    alert.showAndWait();
               }
            else{
                Transaction t=new Transaction(null,dataManager.getBackgroundColor(),app,null);
                Node returnNode=dataManager.initNewStation(result.get());
                t.setAfterAction(returnNode,dataManager.getBackgroundColor(),app, null);
                workspace.AddTransaction(t);
                workspace.getStationSelecter().getSelectionModel().selectLast();
            }
        }
        else
            dialog.close();
   }
   public void processRemoveStation(){
       workspace = (MapWorkspace)app.getWorkspaceComponent();
       String remove=workspace.StationSelector.getSelectionModel().getSelectedItem();
       Node node=null;
       for(int i=0;i<dataManager.getNodes().size();i++){
            if(dataManager.getNodes().get(i) instanceof Group){
                    if(((Group)dataManager.getNodes().get(i)).getChildren().get(0) instanceof DraggableStation){
                         if(((DraggableStation)((Group)dataManager.getNodes().get(i)).getChildren().get(0)).StationName.equals(remove)){
                            node=((Group)dataManager.getNodes().get(i)).getChildren().get(0);
                            break;
                          }
                    }
                    else{
                        if(((DraggableStation)((Group)dataManager.getNodes().get(i)).getChildren().get(1)).StationName.equals(remove)){
                            node=((Group)dataManager.getNodes().get(i)).getChildren().get(1);
                            break;
                        }
                    }
            }
       }   
       Transaction t=new Transaction(node,dataManager.getBackgroundColor(),app,null);
       ArrayList<Node> trash=dataManager.RemoveStation((DraggableStation)node,true); 
       t.setAfterArray(trash);
       t.setAfterAction(null,dataManager.getBackgroundColor(),app, null);
       workspace.AddTransaction(t);
       workspace.StationSelector.getItems().remove(remove);
       workspace.getToStationSelecter().getItems().remove(remove);
       workspace.getFromStationSelecter().getItems().remove(remove);
   }
   public void processSnapToGrid(){
      Node selected = dataManager.getSelectedNode();
      if(selected instanceof DraggableLineEnd || selected instanceof DraggableStation){
         // if(((Circle)selected).getCenterX()%30!=0 || ((Circle)selected).getCenterY()%30!=0){
          Transaction t=new Transaction(selected,dataManager.getBackgroundColor(),app,null);
          ((Circle)selected).setCenterX(Math.round((((Circle)selected).getCenterX() + 20)/ 40.0) * 40.0);
          ((Circle)selected).setCenterY(Math.round((((Circle)selected).getCenterY() + 20)/ 40.0) * 40.0);
          t.setAfterAction(selected, dataManager.getBackgroundColor(), app,null);
          workspace.AddTransaction(t);
          t.setSNAPCHANGE(true);
         // }
      } 
   }
   public void processMoveLabel(){
       if(dataManager.getSelectedNode() instanceof DraggableStation){
            Transaction t=new Transaction(dataManager.getSelectedNode(),dataManager.getBackgroundColor(),app,null);
            ((DraggableStation)dataManager.getSelectedNode()).toggleLabelLocation();
            t.setAfterAction((DraggableStation)dataManager.getSelectedNode(), dataManager.getBackgroundColor(), app,null);
            workspace.AddTransaction(t);
            t.setMOVELABEL(true);
       }
   }
   public void processRotateLabel(){
       workspace = (MapWorkspace)app.getWorkspaceComponent();
       if(dataManager.getSelectedNode()!=null){
         Transaction t=new Transaction(dataManager.getSelectedNode(),dataManager.getBackgroundColor(),app,null);
        ((DraggableStation)dataManager.getSelectedNode()).toggleRotate();
        t.setAfterAction((DraggableStation)dataManager.getSelectedNode(), dataManager.getBackgroundColor(), app,null);
        workspace.AddTransaction(t);
        t.setROTATELABEL(true);
       }
       else{
           String name=(String)workspace.getStationSelecter().getSelectionModel().getSelectedItem();
           for(int i=0;i<dataManager.getNodes().size();i++){
               if(dataManager.getNodes().get(i) instanceof DraggableStation && ((DraggableStation)dataManager.getNodes().get(i)).StationName.equals(name)){
                   Transaction t=new Transaction(dataManager.getSelectedNode(),dataManager.getBackgroundColor(),app,null);
                   ((DraggableStation)dataManager.getNodes().get(i)).toggleRotate();
                    t.setAfterAction((DraggableStation)dataManager.getSelectedNode(), dataManager.getBackgroundColor(), app,null);
                    workspace.AddTransaction(t);
                    t.setROTATELABEL(true);
                   break;
               }
           }
       }
   }
   public void processSelectStationRadius(){
    workspace = (MapWorkspace)app.getWorkspaceComponent();
    int StationRadius = (int)workspace.getStationRadiusSlider().getValue();
    dataManager.setStationRadius(StationRadius); 
   }
   public void processFindRoute(){
       workspace = (MapWorkspace)app.getWorkspaceComponent();
       String startString=(String)workspace.getFromStationSelecter().getSelectionModel().getSelectedItem();
       String endString=(String)workspace.getToStationSelecter().getSelectionModel().getSelectedItem();
       DraggableStation start=dataManager.getStation(startString);
       DraggableStation end=dataManager.getStation(endString);
      
       Alert alert = new Alert(AlertType.INFORMATION);
       alert.setTitle("Metro Map Maker - Route");
       alert.setHeaderText("Route from "+start.StationName+" to "+end.StationName);
       String listString="Origin: "+start.StationName
               + "\nDestination "+end.StationName; 
       if(dataManager.ContainsStations(start)==false || dataManager.ContainsStations(end)==false){      
        listString= listString+ "\n There is no connection between these stations.";     
        alert.setContentText(listString);
        alert.showAndWait();
       }
       else if(start.equals(end)){      
        listString= listString+ "\n Your origin and destination are the same.";     
        alert.setContentText(listString);
        alert.showAndWait();
       }
       else{
        Queue <DraggableStation> toVisit = new LinkedList<>();
        Set <DraggableStation> alreadyVisited = new HashSet<>();
        Map <DraggableStation, DraggableStation> path = new HashMap<>();
        alreadyVisited.add(start);
        toVisit.add(start);
        String Path="";
        path.put(start, null);
        while(!toVisit.isEmpty()){
            DraggableStation cur = toVisit.poll();
            if(cur == end)
             {
                     DraggableStation at = cur;
                     String correct="";
                     int totalStations=0;          
                     while(at != null)
                     {         
                         correct=at.StationName+","+correct ;
                         at = path.get(at);   
                         totalStations++;
                     }
                     String Journey="";
                     correct=correct.substring(0,correct.length()-1);
                     List<String> stringList = new ArrayList<String>(Arrays.asList(correct.split("\\s*,\\s*")));
                     String lines="";
                     for(int i =0;i<dataManager.getNodes().size();i++){
                         Node temp=dataManager.getNodes().get(i);
                         int count=0;
                         if(temp instanceof DraggableLineEnd){
                             for(int j=0;j<stringList.size();j++){
                                 DraggableStation station=dataManager.getStation(stringList.get(j));
                                 if(((DraggableLineEnd) temp).stations.contains(station)){
                                     count++;
                                 }                                
                             }
                        if(count!=0 && !lines.contains(((DraggableLineEnd)temp).LineName)){
                            lines=lines+((DraggableLineEnd) temp).LineName+"(Stops: "+count+")\n";
                        }
                       } 
                     }
                     LineWrap line=dataManager.getLineWrap(start,dataManager.getStation(stringList.get(1)));
                     String LineName="";
                     if(line==null){
                         LineName=dataManager.getLineWrap(end,null).LineName;
                     }
                     else
                        LineName= line.LineName;
                        Journey="\nBoard "+LineName+" at "+stringList.get(0); 
                        for(int i=1;i<totalStations-1;i++){
                            line=dataManager.getLineWrap(dataManager.getStation(stringList.get(i)),dataManager.getStation(stringList.get(i+1)));
                            String linename="";
                            if(line==null){
                                linename=dataManager.getLineWrap(end,null).LineName;
                            }
                            else
                                linename= line.LineName;                                  
                            if(!LineName.equals(linename)){
                                Journey=Journey+"\nTransfer to "+linename+" at "+stringList.get(i);
                                LineName=linename;
                            }                        
                            if(dataManager.getStation(stringList.get(i+1)).equals(end)){
                                break;
                            }
                        }
                        line=dataManager.getLineWrap(dataManager.getStation(stringList.get(totalStations-2)),end);
                            String linename="";
                            if(line==null){
                                 linename=dataManager.getLineWrap(end,null).LineName;
                            }
                            else
                                linename=line.LineName; 
                    Journey=Journey+"\nDisembark "+linename+" at "+stringList.get(totalStations-1);
                    listString=listString+"\n"+lines+"Total Stops "+totalStations+"\nEstimated Times: "+totalStations*3
                             +"\n"+ Journey;
                     break;
             }
             for(DraggableStation m : dataManager.getAdjacentNodes(cur,alreadyVisited))
             {
                 if(!alreadyVisited.contains(m))
                 {
                     path.put(m, cur);
                     alreadyVisited.add(m);
                     toVisit.add(m);
                 }
             }
         }
         alert.setContentText(listString);
         alert.showAndWait();
       }
   }
   public void processSetImageBackground(){
       AppYesNoCancelDialogSingleton yesNoDialog = AppYesNoCancelDialogSingleton.getSingleton();
        yesNoDialog.show("Set Image Background","Do you want an image background altogether?");
        workspace = (MapWorkspace)app.getWorkspaceComponent();
        Color bgColor=workspace.getBackgroundColorPicker().getValue();
        // AND NOW GET THE USER'S SELECTION
        String selection = yesNoDialog.getSelection();

        // IF THE USER SAID YES, THEN SAVE BEFORE MOVING ON
        if (selection.equals(AppYesNoCancelDialogSingleton.YES)) {
            FileChooser.ExtensionFilter imageFilter
        = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(imageFilter);
        fc.setInitialDirectory(new File(PATH_IMAGES));
	fc.setTitle("Add Image Background");
        File selectedFile = fc.showOpenDialog(app.getGUI().getWindow());
         if (selectedFile != null) {        
            dataManager.setBackgroundImage(selectedFile.toURI().toString());
            processShowGrid();
          }
        }
        else{
            dataManager.setBackgroundImage("");
            processShowGrid();
        }  
   }
   public void processAddImage(){
        PropertiesManager props = PropertiesManager.getPropertiesManager();    
        Scene scene = app.getGUI().getPrimaryScene();
	scene.setCursor(Cursor.DEFAULT);
	FileChooser.ExtensionFilter imageFilter
        = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(imageFilter);
        fc.setInitialDirectory(new File(PATH_IMAGES));
	fc.setTitle("Add Image");
        File selectedFile = fc.showOpenDialog(app.getGUI().getWindow());

        // ONLY OPEN A NEW FILE IF THE USER SAYS OK
        if (selectedFile != null) {
            try {
                Transaction t=new Transaction(null,dataManager.getBackgroundColor(),app,null);
                Image image = new Image(selectedFile.toURI().toString());
                DraggableImage di=new DraggableImage(selectedFile.toURI().toString());
                di.setImage(image);
                dataManager.addNode(di);
                t.setAfterAction(di, dataManager.getBackgroundColor(), app,null);
                workspace.AddTransaction(t);
            } catch (Exception e) {
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.show(props.getProperty(LOAD_ERROR_TITLE), props.getProperty(LOAD_ERROR_MESSAGE));
            }
	// CHANGE THE STATE	
        workspace = (MapWorkspace)app.getWorkspaceComponent();
	workspace.reloadWorkspace(dataManager);
        }
   }
   public void processAddLabel(){
       workspace=(MapWorkspace)app.getWorkspaceComponent();
        TextInputDialog dialog = new TextInputDialog("Text Input");
        dialog.setHeaderText("Text to be added:");
        dialog.setContentText("Please enter text:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            Transaction t=new Transaction(null,dataManager.getBackgroundColor(),app,null);
           DraggableText text = new DraggableText(result.get());
           text.setFont(Font.font("Arial", 20));
           dataManager.addNode(text);   
           t.setAfterAction(text, dataManager.getBackgroundColor(), app,null);
           workspace.AddTransaction(t);
        }
	workspace.reloadWorkspace(dataManager);
       
   }
   public void processRemoveElement(){
       Node selected=dataManager.getSelectedNode();
       if(selected instanceof DraggableImage || selected instanceof DraggableText){
           Transaction t=new Transaction(dataManager.getSelectedNode(),dataManager.getBackgroundColor(),app,null);                  
           dataManager.removeNode(selected);
           t.setAfterAction(dataManager.getSelectedNode(), dataManager.getBackgroundColor(), app,null);
           workspace.AddTransaction(t);
       }
   }
   public void setFont(Node selected,String FontFamily, double FontSize){
        if(selected instanceof DraggableText){
           Transaction t=new Transaction(selected,dataManager.getBackgroundColor(),app,null); 
           boolean bold=((DraggableText)selected).isBold;
           boolean italic=((DraggableText)selected).isItalic;
           if(bold==true && italic==true)
                ((DraggableText)selected).setFont(Font.font(FontFamily, FontWeight.BOLD,FontPosture.ITALIC, FontSize));
            else if(bold==true && italic==false){
                ((DraggableText)selected).setFont(Font.font(FontFamily, FontWeight.BOLD,FontPosture.REGULAR, FontSize));
             }
            else if(bold==false && italic==true){
                ((DraggableText)selected).setFont(Font.font(FontFamily, FontWeight.NORMAL, FontPosture.ITALIC, FontSize));
             } 
            else{
                ((DraggableText)selected).setFont(Font.font(FontFamily, FontWeight.NORMAL, FontPosture.REGULAR, FontSize));
            }
           t.setAfterAction(selected, dataManager.getBackgroundColor(),app,null);
           workspace.AddTransaction(t);
           t.setCHANGEFONT(true);
       } 
       else if(selected instanceof DraggableLineEnd){
           Transaction t=new Transaction(selected,dataManager.getBackgroundColor(),app,null); 
           boolean bold=((DraggableLineEnd)selected).Textlink.isBold;
           boolean italic=((DraggableLineEnd)selected).Textlink.isItalic;
           if(bold==true && italic==true)
                ((DraggableLineEnd)selected).Textlink.setFont(Font.font(FontFamily, FontWeight.BOLD,FontPosture.ITALIC, FontSize));
            else if(bold==true && italic==false){
                ((DraggableLineEnd)selected).Textlink.setFont(Font.font(FontFamily, FontWeight.BOLD,FontPosture.REGULAR, FontSize));
             }
            else if(bold==false && italic==true){
                ((DraggableLineEnd)selected).Textlink.setFont(Font.font(FontFamily, FontWeight.NORMAL, FontPosture.ITALIC, FontSize));
             } 
            else{
                ((DraggableLineEnd)selected).Textlink.setFont(Font.font(FontFamily, FontWeight.NORMAL, FontPosture.REGULAR, FontSize));
            }
           t.setAfterAction(selected, dataManager.getBackgroundColor(),app,null);
           workspace.AddTransaction(t);
           t.setCHANGEFONT(true);
       }   
       else if(selected instanceof DraggableStation){
           Transaction t=new Transaction(selected,dataManager.getBackgroundColor(),app,null); 
                boolean bold=((DraggableStation)selected).text.isBold;
                boolean italic=((DraggableStation)selected).text.isItalic;
                if(bold==true && italic==true)
                     ((DraggableStation)selected).text.setFont(Font.font(FontFamily, FontWeight.BOLD,FontPosture.ITALIC, FontSize));
                 else if(bold==true && italic==false){
                     ((DraggableStation)selected).text.setFont(Font.font(FontFamily, FontWeight.BOLD,FontPosture.REGULAR, FontSize));
                  }
                 else if(bold==false && italic==true){
                     ((DraggableStation)selected).text.setFont(Font.font(FontFamily, FontWeight.NORMAL, FontPosture.ITALIC, FontSize));
                  } 
                 else{
                     ((DraggableStation)selected).text.setFont(Font.font(FontFamily, FontWeight.NORMAL, FontPosture.REGULAR, FontSize));
                 }   
                t.setAfterAction(selected, dataManager.getBackgroundColor(),app,null);
           workspace.AddTransaction(t);
           t.setCHANGEFONT(true);
       }  
        else if(selected instanceof AbsoluteText && selected instanceof DraggableText==false){
            Transaction t=new Transaction(selected,dataManager.getBackgroundColor(),app,null); 
                boolean bold=((AbsoluteText)selected).isBold;
                boolean italic=((AbsoluteText)selected).isItalic;
                if(bold==true && italic==true)
                     ((AbsoluteText)selected).setFont(Font.font(FontFamily, FontWeight.BOLD,FontPosture.ITALIC, FontSize));
                 else if(bold==true && italic==false){
                     ((AbsoluteText)selected).setFont(Font.font(FontFamily, FontWeight.BOLD,FontPosture.REGULAR, FontSize));
                  }
                 else if(bold==false && italic==true){
                     ((AbsoluteText)selected).setFont(Font.font(FontFamily, FontWeight.NORMAL, FontPosture.ITALIC, FontSize));
                  } 
                 else{
                     ((AbsoluteText)selected).setFont(Font.font(FontFamily, FontWeight.NORMAL, FontPosture.REGULAR, FontSize));
                 }
                t.setAfterAction(selected, dataManager.getBackgroundColor(),app,null);
                workspace.AddTransaction(t);
                t.setCHANGEFONT(true);
       }
   }

   public void processBoldText(){
      Node selected=dataManager.getSelectedNode();
      workspace=(MapWorkspace)app.getWorkspaceComponent();
       if(selected instanceof DraggableText){
           
           ((DraggableText)selected).isBold=!((DraggableText)selected).isBold;   
           setFont(selected,((DraggableText)selected).getFont().getFamily(),((DraggableText)selected).getFont().getSize());                 
       } 
       else if(selected instanceof DraggableLineEnd){
           ((DraggableLineEnd)selected).Textlink.isBold=!((DraggableLineEnd)selected).Textlink.isBold;  
            setFont(selected,((DraggableLineEnd)selected).Textlink.getFont().getFamily(),((DraggableLineEnd)selected).Textlink.getFont().getSize());      
       }
       else if(selected instanceof DraggableStation){  
               ((DraggableStation)selected).text.isBold=!((DraggableStation)selected).text.isBold;
                setFont(selected,((DraggableStation)selected).text.getFont().getFamily(),((DraggableStation)selected).text.getFont().getSize()); 
        }
        else if(selected instanceof AbsoluteText && selected instanceof DraggableText==false){
               ((AbsoluteText)selected).isBold=!((AbsoluteText)selected).isBold;
                setFont(selected,((AbsoluteText)selected).getFont().getFamily(),((AbsoluteText)selected).getFont().getSize()); 
        }
   }
   public void processItalicText(){
      Node selected=dataManager.getSelectedNode();
      workspace=(MapWorkspace)app.getWorkspaceComponent();
       if(selected instanceof DraggableText){
           ((DraggableText)selected).isItalic=!((DraggableText)selected).isItalic;   
           setFont(selected,((DraggableText)selected).getFont().getFamily(),((DraggableText)selected).getFont().getSize());                 
       } 
       else if(selected instanceof DraggableLineEnd){
           ((DraggableLineEnd)selected).Textlink.isItalic=!((DraggableLineEnd)selected).Textlink.isItalic;  
            setFont(selected,((DraggableLineEnd)selected).Textlink.getFont().getFamily(),((DraggableLineEnd)selected).Textlink.getFont().getSize());      
       }
       else if(selected instanceof DraggableStation){  
                ((DraggableStation)selected).text.isItalic=!((DraggableStation)selected).text.isItalic;
                setFont(selected,((DraggableStation)selected).text.getFont().getFamily(),((DraggableStation)selected).text.getFont().getSize()); 
        }
       else if(selected instanceof AbsoluteText && selected instanceof DraggableText==false){
               ((AbsoluteText)selected).isItalic=!((AbsoluteText)selected).isItalic;
                 setFont(selected,((AbsoluteText)selected).getFont().getFamily(),((AbsoluteText)selected).getFont().getSize());
        }
   }
   public void processChangeFontFamily(){
       Node selected=dataManager.getSelectedNode();
       workspace=(MapWorkspace)app.getWorkspaceComponent();
       if(selected instanceof DraggableText){
            setFont(selected,workspace.FontFamilySelector.getSelectionModel().getSelectedItem(),((DraggableText)selected).getFont().getSize());
       } 
       else if(selected instanceof DraggableLineEnd){
           setFont(selected,workspace.FontFamilySelector.getSelectionModel().getSelectedItem(),((DraggableLineEnd)selected).Textlink.getFont().getSize());
       }
       else if(selected instanceof DraggableStation){
               setFont(selected,workspace.FontFamilySelector.getSelectionModel().getSelectedItem(),((DraggableStation)selected).text.getFont().getSize());
        }
       else if(selected instanceof AbsoluteText && selected instanceof DraggableText==false){
               setFont(selected,workspace.FontFamilySelector.getSelectionModel().getSelectedItem(),((AbsoluteText)selected).getFont().getSize());
        }
   }
   public void processChangeFontColor(){
       Node selected=dataManager.getSelectedNode();
       workspace=(MapWorkspace)app.getWorkspaceComponent();
       Transaction t=new Transaction(selected,dataManager.getBackgroundColor(),app,null); 
       if(selected instanceof DraggableText){
            ((DraggableText)selected).setFill((Color)workspace.getFontColorPicker().getValue());
       } 
       else if(selected instanceof DraggableLineEnd){
            ((DraggableLineEnd)selected).Textlink.setFill((Color)workspace.getFontColorPicker().getValue());
       }
       else if(selected instanceof DraggableStation){
            ((DraggableStation)selected).text.setFill((Color)workspace.getFontColorPicker().getValue());
       }
       else if(selected instanceof AbsoluteText && selected instanceof DraggableText==false){
               ((AbsoluteText)selected).setFill((Color)workspace.getFontColorPicker().getValue());
        }
       t.setAfterAction(selected, dataManager.getBackgroundColor(),app,null);
       workspace.AddTransaction(t);
       t.setCHANGEFONTCOLOR(true);
   }
   public void processChangeFontSize(){
       Node selected=dataManager.getSelectedNode();
       workspace=(MapWorkspace)app.getWorkspaceComponent();
       if(selected instanceof DraggableText){
            setFont(selected,((DraggableText)selected).getFont().getFamily(),workspace.FontSizeSelector.getSelectionModel().getSelectedItem());
       } 
       else if(selected instanceof DraggableLineEnd){
          setFont(selected,((DraggableLineEnd)selected).Textlink.getFont().getFamily(),workspace.FontSizeSelector.getSelectionModel().getSelectedItem());
       }
       else if(selected instanceof DraggableStation){
               setFont(selected,((DraggableStation)selected).text.getFont().getFamily(),workspace.FontSizeSelector.getSelectionModel().getSelectedItem());
        }
       else if(selected instanceof AbsoluteText && selected instanceof DraggableText==false){
               setFont(selected,((AbsoluteText)selected).getFont().getFamily(),workspace.FontSizeSelector.getSelectionModel().getSelectedItem());
        }      
   }
   public void processChangeBackgroundColor(){
       MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
	Color selectedColor = workspace.getBackgroundColorPicker().getValue();
        Transaction t=new Transaction(null,dataManager.getBackgroundColor(),app,null);
	if (selectedColor != null) {
	    dataManager.setBackgroundColor(selectedColor);
	    app.getGUI().updateToolbarControls(false);
	}
        processShowGrid();
        t.setAfterAction(null, dataManager.getBackgroundColor(),app,null);
        workspace.AddTransaction(t);
   }
   public void processShowGrid(){
       MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
       if(workspace.GridCheckBox.isSelected()){
            BackgroundFill fill = new BackgroundFill(dataManager.getBackgroundColor(), null, null);
            BackgroundImage image1=new BackgroundImage(new Image("file:/C:/Users/Vrockcm/Documents/NetBeansProjects/hw4/MetroMapMaker/images/G.png"),BackgroundRepeat.REPEAT,BackgroundRepeat.REPEAT,BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
            BackgroundFill[] fills=new BackgroundFill[1];
            fills[0]=fill;
            BackgroundImage[] images;
            if(!dataManager.getBackgroundImage().isEmpty()){
                BackgroundImage image2=new BackgroundImage(new Image(dataManager.getBackgroundImage()),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
                images=new BackgroundImage[2];               
                images[0]=image2;
                images[1]=image1;              
            }
            else
            {
                images =new BackgroundImage[1];               
                images[0]=image1;
            }
            Background background = new Background(fills,images);
            workspace.getCanvas().setBackground(background);
       }
       else
       {
          if(!dataManager.getBackgroundImage().isEmpty()){
                dataManager.setBackgroundImage(dataManager.getBackgroundImage());
            }
            else
            {
                dataManager.setBackgroundColor(dataManager.getBackgroundColor());
            }
       }
   }
   public void processZoomIn(){
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        workspace.getCanvas().setScaleX(workspace.getCanvas().getScaleX()+0.1);
        workspace.getCanvas().setScaleY(workspace.getCanvas().getScaleY()+0.1);
        workspace.getCanvas().getParent().layout();
   }
   public void processZoomOut(){
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        workspace.getCanvas().setScaleX(workspace.getCanvas().getScaleX()-0.1);
        workspace.getCanvas().setScaleY(workspace.getCanvas().getScaleY()-0.1);
        workspace.getCanvas().getParent().layout();
   }
   public void processDecreaseMapSize(){
       MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
       double width=workspace.getCanvas().getWidth()*0.1;
       double height=workspace.getCanvas().getHeight()*0.1;
       width=workspace.getCanvas().getWidth()-width;
       height=workspace.getCanvas().getHeight()-height;
       workspace.getCanvas().setMaxWidth(width);
       workspace.getCanvas().setMaxHeight(height);
       workspace.getCanvas().setMinWidth(width);
       workspace.getCanvas().setMinHeight(height);
       workspace.getCanvas().getParent().layout();
   }
   public void processIncreaseMapSize(){
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
       double width=workspace.getCanvas().getWidth()*0.1;
       double height=workspace.getCanvas().getHeight()*0.1;
       width=workspace.getCanvas().getWidth()+width;
       height=workspace.getCanvas().getHeight()+height;
       workspace.getCanvas().setMaxWidth(width);
       workspace.getCanvas().setMaxHeight(height);
       workspace.getCanvas().setMinWidth(width);
       workspace.getCanvas().setMinHeight(height);
       workspace.getCanvas().getParent().layout();
   }
}
