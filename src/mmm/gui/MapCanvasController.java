package mmm.gui;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.shape.Shape;
import mmm.data.MapData;
import mmm.data.Draggable;
import mmm.data.MapState;
import static mmm.data.MapState.DRAGGING_NOTHING;
import djf.AppTemplate;
import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import mmm.data.DraggableLineEnd;
import mmm.data.DraggableStation;
import mmm.data.DraggableText;
import static mmm.data.MapState.ADDING_STATION;
import static mmm.data.MapState.REMOVING_STATION;
import static mmm.data.MapState.SELECTING_NODE;
import mmm.data.Transaction;

/**
 * This class responds to interactions with the rendering surface.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class MapCanvasController {

    AppTemplate app;
    Transaction draggedNode;
    public MapCanvasController(AppTemplate initApp) {
        app = initApp;
    }

    /**
     * Respond to mouse presses on the rendering surface, which we call canvas,
     * but is actually a Pane.
     */
    public void processCanvasMousePress(int x, int y) {
        MapData dataManager = (MapData) app.getDataComponent();
        MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
        if (dataManager.isInState(SELECTING_NODE)) {
            // SELECT THE TOP SHAPE
            Node node = dataManager.selectTopNode(x, y);
            Scene scene = app.getGUI().getPrimaryScene();

            // AND START DRAGGING IT
            if (node != null) {
                draggedNode=new Transaction(dataManager.getSelectedNode(),dataManager.getBackgroundColor(),app,null);
                draggedNode.setMOVEELEMENT(true);
                scene.setCursor(Cursor.MOVE);
                dataManager.setState(MapState.DRAGGING_NODE);
                app.getGUI().updateToolbarControls(false);
                if(node instanceof Text && node instanceof DraggableText==false){
                   dataManager.LineEndOpacityEnable(((Text)node).getText(),true);
                   scene.setCursor(Cursor.DEFAULT);
                   dataManager.setState(MapState.DRAGGING_NOTHING);
                } 
                if(node instanceof DraggableLineEnd && node.getOpacity()==0.0){
                    scene.setCursor(Cursor.DEFAULT);
                    dataManager.setState(MapState.DRAGGING_NOTHING);
                }
            }
            else {                     
                dataManager.LineEndOpacityEnable("",false);
                scene.setCursor(Cursor.DEFAULT);
                dataManager.setState(DRAGGING_NOTHING);
                app.getWorkspaceComponent().reloadWorkspace(dataManager);
            }
        }
        else if(dataManager.isInState(ADDING_STATION)){
            Node node = dataManager.getTopNode(x, y);
            Scene scene = app.getGUI().getPrimaryScene();
            if(node!=null && node instanceof DraggableStation){
                Transaction t=new Transaction(node,dataManager.getBackgroundColor(),app,null);
                dataManager.AddStationToLine((DraggableStation)node,null,"");    
                t.setAfterAction(node,dataManager.getBackgroundColor(),app,null);
                workspace.AddTransaction(t);
            }
            else
            {
                scene.setCursor(Cursor.DEFAULT);
                dataManager.setState(MapState.DRAGGING_NOTHING);
            }
            
        }
        else if(dataManager.isInState(REMOVING_STATION)){
            Node node = dataManager.getTopNode(x, y);
            Scene scene = app.getGUI().getPrimaryScene();
            if(node!=null && node instanceof DraggableStation){
                Transaction t=new Transaction(node,dataManager.getBackgroundColor(),app,null);
                ArrayList<Node> trash=dataManager.RemoveStation((DraggableStation)node,false);
                t.setAfterAction(node,dataManager.getBackgroundColor(),app,trash);
                workspace.AddTransaction(t);
            }
            else
            {
                scene.setCursor(Cursor.DEFAULT);
                dataManager.setState(MapState.DRAGGING_NOTHING);
            }
            
        }
        workspace.reloadWorkspace(dataManager);
        
     }
    

    /**
     * Respond to mouse dragging on the rendering surface, which we call canvas,
     * but is actually a Pane.
     */
    public void processCanvasMouseDragged(int x, int y) {      
        MapData dataManager = (MapData) app.getDataComponent();
         if (dataManager.isInState(MapState.DRAGGING_NODE)) {
            Draggable selectedDraggableShape = (Draggable) dataManager.getSelectedNode();
            selectedDraggableShape.drag(x, y);
            app.getGUI().updateToolbarControls(false);
            
        }      
    }

    /**
     * Respond to mouse button release on the rendering surface, which we call canvas,
     * but is actually a Pane.
     */
    public void processCanvasMouseRelease(int x, int y) {
         MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
        MapData dataManager = (MapData) app.getDataComponent();
        if (dataManager.isInState(MapState.DRAGGING_NODE)) {
            dataManager.setState(SELECTING_NODE);
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.DEFAULT);
            app.getGUI().updateToolbarControls(false);
            draggedNode.setAfterAction(dataManager.getSelectedNode(),dataManager.getBackgroundColor(),app,null);
            workspace.AddTransaction(draggedNode);
            
        } else if (dataManager.isInState(MapState.DRAGGING_NOTHING)) {
            dataManager.setState(SELECTING_NODE);
        }
    }
}
