package mmm.gui;

import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import static mmm.MapProperties.*;
import mmm.data.MapData;
import static mmm.data.MapData.BLACK_HEX;
import static mmm.data.MapData.WHITE_HEX;
import mmm.data.MapState;
import djf.ui.AppYesNoCancelDialogSingleton;
import djf.ui.AppMessageDialogSingleton;
import djf.ui.AppGUI;
import djf.AppTemplate;
import djf.components.AppDataComponent;
import djf.components.AppWorkspaceComponent;
import static djf.settings.AppStartupConstants.FILE_PROTOCOL;
import static djf.settings.AppStartupConstants.PATH_IMAGES;
import java.util.Iterator;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import static mmm.css.MapStyle.*;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import jtps.jTPS;
import mmm.data.DraggableImage;
import mmm.data.DraggableLineEnd;
import mmm.data.DraggableStation;
import mmm.data.DraggableText;
import mmm.data.LineWrap;
import mmm.data.Transaction;

/**
 * This class serves as the workspace component for this application, providing
 * the user interface controls for editing work.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class MapWorkspace extends AppWorkspaceComponent {
    // HERE'S THE APP
    AppTemplate app;

    // IT KNOWS THE GUI IT IS PLACED INSIDE
    AppGUI gui;

    // HAS ALL THE CONTROLS FOR EDITING
    VBox editToolbar;
    public static jTPS jTPS= new jTPS(); 
    // FIRST ROW
     VBox row1;
        //FIRST ROW 1 BOX 1  
    HBox row1Box1;
    Label MetroLineLabel;
    ComboBox<String> LineSelector;
    Button LineEdit;
    
        //FIRST ROW 1 BOX 2
    HBox row1Box2;
    Button AddLineButton;
    Button RemoveLineButton;
    Button AddStationButton;
    Button RemoveStationButton;
    Button ListAllStationsButton;
        //FIRST ROW 1 BOX 3
    Slider LineThicknessSlider;
    
    // SECOND ROW
    VBox row2;
        // ROW 2 BOX 1 
    HBox row2Box1;
    Label MetroStationsLabel;
    ComboBox<String> StationSelector;
    ColorPicker StationColorPicker;
        // ROW 2 BOX 2
    HBox row2Box2;
    Button AddStationToCanvasButton;
    Button RemoveStationFromCanvasButton;
    Button SnapButton;
    Button MoveLabelButton;
    Button RotateLabelButton;
        // ROW 2 BOX 3
    Slider StationRadiusSlider;
    
    // THIRD ROW
    HBox row3;
        // ROW 3 BOX 1 
    VBox row3Box1;
    ComboBox<String> ToStationSelector;
    ComboBox<String> FromStationSelector;
    
    VBox row3Box2;
    Button FindRouteButton;
    
    // FORTH ROW
    VBox row4;
        // ROW 4 BOX 1 
    HBox row4Box1;
    Label DecorLabel;
    ColorPicker backgroundColorPicker;
        // ROW 4 BOX 2 
    HBox row4Box2;
    Button SetImageBackgroundButton;
    Button AddImageButton;
    Button AddLabelButton;
    Button RemoveElementButton;
    
    // FIFTH ROW
    VBox row5;
         // ROW 5 BOX 1 
    HBox row5Box1;
    Label FontLabel;
    ColorPicker FontColorPicker;
        // ROW 5 BOX 2 
    HBox row5Box2;
    Button BoldButton;
    Button ItalicButton;
    ComboBox<Double> FontSizeSelector;
    ComboBox<String> FontFamilySelector;
        
    // SIXTH ROW
    VBox row6;
         // ROW 6 BOX 1 
    HBox row6Box1;
    Label NavigationLabel;
    CheckBox GridCheckBox;
        // ROW 6 BOX 2 
    HBox row6Box2;
    Button ZoomInButton;
    Button ZoomOutButton;
    Button MapSizeIncButton;
    Button MapSizeDecButton;
  
    // THIS IS WHERE WE'LL RENDER OUR DRAWING, NOTE THAT WE
    // CALL THIS A CANVAS, BUT IT'S REALLY JUST A Pane
    Pane canvas;
    
    // HERE ARE THE CONTROLLERS
    MapCanvasController canvasController;
    MapProcessController mapController;    

    // HERE ARE OUR DIALOGS
    AppMessageDialogSingleton messageDialog;
    AppYesNoCancelDialogSingleton yesNoCancelDialog;
    
    // FOR DISPLAYING DEBUG STUFF
    Text debugText;

    /**
     * Constructor for initializing the workspace, note that this constructor
     * will fully setup the workspace user interface for use.
     *
     * @param initApp The application this workspace is part of.
     *
     * @throws IOException Thrown should there be an error loading application
     * data for setting up the user interface.
     */
    public MapWorkspace(AppTemplate initApp) {
	// KEEP THIS FOR LATER
	app = initApp;

	// KEEP THE GUI FOR LATER
	gui = app.getGUI();

        // LAYOUT THE APP
        initLayout();
        
        // HOOK UP THE CONTROLLERS
        initControllers();
        
        // AND INIT THE STYLE FOR THE WORKSPACE
        initStyle();    
    }
    
    /**
     * Note that this is for displaying text during development.
     */
    public void setDebugText(String text) {
	debugText.setText(text);
    }
    
    // ACCESSOR METHODS FOR COMPONENTS THAT EVENT HANDLERS
    // MAY NEED TO UPDATE OR ACCESS DATA FROM
    
    public ComboBox getLineSelecter(){
        return LineSelector;
    }
    
    public ComboBox getStationSelecter(){
        return StationSelector;
    }
    
    public ComboBox getToStationSelecter(){
        return ToStationSelector;
    }
    public ComboBox getFromStationSelecter(){
        return FromStationSelector;
    }
    public ColorPicker getStationColorPicker() {
	return StationColorPicker;
    }
    public ColorPicker getFontColorPicker() {
	return FontColorPicker;
    }
    
    public ColorPicker getBackgroundColorPicker() {
	return backgroundColorPicker;
    }
    
    public Slider getLineThicknessSlider() {
	return LineThicknessSlider;
    }
    public Slider getStationRadiusSlider() {
	return StationRadiusSlider;
    }
    public void AddTransaction(Transaction m){
        jTPS.addTransaction(m);
        app.getGUI().disableUndo(false);
    }

    public Pane getCanvas() {
	return canvas;
    }
        
    // HELPER SETUP METHOD
    private void initLayout() {
	// THIS WILL GO IN THE LEFT SIDE OF THE WORKSPACE
	editToolbar = new VBox();
	editToolbar.setPrefWidth(470);
	// ROW 1
	row1 = new VBox();
        row1Box1 = new HBox();
        row1Box1.setSpacing(10);
        MetroLineLabel = new Label("Metro Lines");
        LineSelector = new ComboBox<>();
        row1Box1.getChildren().addAll(MetroLineLabel,LineSelector);
	LineEdit = gui.initChildButton(row1Box1,"Edit Line", LINE_EDIT_TOOLTIP.toString(), false);
	LineEdit.setStyle("-fx-background-radius: 10em;");
        row1Box2 = new HBox();
        row1Box2.setSpacing(10);
        AddLineButton = gui.initChildButton(row1Box2, PLUS_ICON.toString(), PLUS_LINE_TOOLTIP.toString(), false);
        AddLineButton.setMaxHeight(10);
        AddLineButton.setMaxWidth(20);
        
	RemoveLineButton = gui.initChildButton(row1Box2, MINUS_ICON.toString(), MINUS_LINE_TOOLTIP.toString(), false);
	AddStationButton = gui.initChildButton(row1Box2, "Add Station", ADD_STATION_TO_LINE_TOOLTIP.toString(), false);
        RemoveStationButton = gui.initChildButton(row1Box2,"Remove Station", REMOVE_STATION_FROM_LINE_TOOLTIP.toString(), false);
        ListAllStationsButton = gui.initChildButton(row1Box2, LIST_ICON.toString(), LIST_TOOLTIP.toString(), false);
        LineThicknessSlider = new Slider(1, 10, 1);     
        
	// ROW 2
	row2 = new VBox();
        row2Box1 = new HBox();
        row2Box1.setSpacing(10);
        MetroStationsLabel = new Label("Metro Stations");
	StationSelector = new ComboBox<>();
	StationColorPicker = new ColorPicker(Color.valueOf(BLACK_HEX));
        row2Box1.getChildren().addAll(MetroStationsLabel,StationSelector,StationColorPicker);
        
        row2Box2 = new HBox();
        row2Box2.setSpacing(10);
        AddStationToCanvasButton = gui.initChildButton(row2Box2, PLUS_ICON.toString(), PLUS_STATION_TOOLTIP.toString(), false);
        RemoveStationFromCanvasButton = gui.initChildButton(row2Box2, MINUS_ICON.toString(), MINUS_STATION_TOOLTIP.toString(), false);
        SnapButton = gui.initChildButton(row2Box2,"Snap", SNAP_TOOLTIP.toString(), false);
        MoveLabelButton = gui.initChildButton(row2Box2,"Move Label", MOVE_LABEL_TOOLTIP.toString(), false);
        RotateLabelButton = gui.initChildButton(row2Box2,ROTATE_ICON.toString(), ROTATE_TOOLTIP.toString(), false);

        StationRadiusSlider = new Slider(10,20,10);
        
	// ROW 3
	row3 = new HBox();
        row3.setSpacing(10);
        row3Box1 = new VBox();
        row3Box1.setSpacing(10);
        ToStationSelector = new ComboBox<>();
        FromStationSelector = new ComboBox<>();
        row3Box1.getChildren().addAll(FromStationSelector,ToStationSelector);
        row3Box2 = new VBox();
        FindRouteButton = gui.initChildButton(row3Box2,ROUTE_ICON.toString(), ROUTE_TOOLTIP.toString(), false);
        FindRouteButton.setMinHeight(70);
        FindRouteButton.setMinWidth(100);
        
        // ROW 4
        row4 = new VBox();
        row4Box1 = new HBox();
        DecorLabel = new Label("Decor");
        Region region1 = new Region();
        HBox.setHgrow(region1, Priority.ALWAYS);
	backgroundColorPicker = new ColorPicker(Color.valueOf(WHITE_HEX));
        row4Box1.getChildren().addAll( DecorLabel,region1,backgroundColorPicker);
        row4Box2 = new HBox();
        row4Box2.setSpacing(10);
        SetImageBackgroundButton = gui.initChildButton(row4Box2,"Set Image Background", "Set Image as Background", false);
        AddImageButton = gui.initChildButton(row4Box2,"Add Image", "Add Image to Map", false);
        AddLabelButton = gui.initChildButton(row4Box2,"Add Label", "Add Label to Map", false);
        RemoveElementButton = gui.initChildButton(row4Box2,"Remove Element", "Remove Image or Text Overlay", false);
       
	// ROW 5
	row5 = new VBox();
        row5Box1 = new HBox();
        Region region2 = new Region();
        HBox.setHgrow(region2, Priority.ALWAYS);
	FontLabel = new Label("Font");
	FontColorPicker = new ColorPicker(Color.valueOf(BLACK_HEX));
        row5Box1.getChildren().addAll( FontLabel,region2,FontColorPicker);
        row5Box2 = new HBox();
        row5Box2.setSpacing(10);
        BoldButton=gui.initChildButton(row5Box2,BOLD_ICON.toString(),BOLD_TOOLTIP.toString(), false);
        ItalicButton=gui.initChildButton(row5Box2,ITALIC_ICON.toString(),ITALIC_TOOLTIP.toString(), false);
        FontSizeSelector = new ComboBox<>();
        FontSizeSelector.getItems().addAll(8.0,9.0,10.0,12.0,14.0,16.0,18.0,20.0,22.0,24.0,26.0,28.0,30.0,32.0,34.0,36.0,38.0,40.0,42.0,44.0);
        FontFamilySelector = new ComboBox<>(FXCollections.observableList(Font.getFamilies()));
        FontFamilySelector.setDisable(false);
        FontFamilySelector.getSelectionModel().select(2);
        FontSizeSelector.getSelectionModel().select(7);
        FontSizeSelector.setDisable(false);        
	row5Box2.getChildren().addAll(FontFamilySelector,FontSizeSelector);
	
	// ROW 6
	row6 = new VBox();
        row6Box1 = new HBox();
        Region region3 = new Region();
        HBox.setHgrow(region3, Priority.ALWAYS);
	NavigationLabel = new Label("Navigation");
	GridCheckBox = new CheckBox("Show Grid");
        row6Box1.getChildren().addAll(NavigationLabel,region3,GridCheckBox);
        row6Box2 = new HBox();
        row6Box2.setSpacing(10);
        ZoomInButton = gui.initChildButton(row6Box2,ZOOM_IN_ICON.toString(),ZOOM_IN_TOOLTIP.toString(),false);
        ZoomInButton.setMinWidth(70);
        ZoomInButton.setMinHeight(70);
        ZoomOutButton = gui.initChildButton(row6Box2,ZOOM_OUT_ICON.toString(),ZOOM_OUT_TOOLTIP.toString(),false);
        ZoomOutButton.setMinWidth(70);
        ZoomOutButton.setMinHeight(70);
        MapSizeIncButton =  gui.initChildButton(row6Box2,INCREASE_SIZE_ICON.toString(),INCREASE_SIZE_TOOLTIP.toString(),false);
        MapSizeIncButton.setMinWidth(70);
        MapSizeIncButton.setMinHeight(70);
        MapSizeDecButton =  gui.initChildButton(row6Box2,DECREASE_SIZE_ICON.toString(),DECREASE_SIZE_TOOLTIP.toString(),false);
        MapSizeDecButton.setMinWidth(70);
        MapSizeDecButton.setMinHeight(70);
	
	row1.getChildren().addAll(row1Box1,row1Box2,LineThicknessSlider);
	row2.getChildren().addAll(row2Box1,row2Box2,StationRadiusSlider);
        row3.getChildren().addAll(row3Box1,row3Box2);
        row4.getChildren().addAll(row4Box1,row4Box2);
        row5.getChildren().addAll(row5Box1,row5Box2);
        row6.getChildren().addAll(row6Box1,row6Box2);
        
        
        
	// NOW ORGANIZE THE EDIT TOOLBAR
	editToolbar.getChildren().add(row1);
	editToolbar.getChildren().add(row2);
	editToolbar.getChildren().add(row3);
	editToolbar.getChildren().add(row4);
	editToolbar.getChildren().add(row5);
	editToolbar.getChildren().add(row6);
	
	// WE'LL RENDER OUR STUFF HERE IN THE CANVAS
	canvas = new Pane();
	debugText = new Text();
	canvas.getChildren().add(debugText);
	debugText.setX(100);
	debugText.setY(100);
	
	// AND MAKE SURE THE DATA MANAGER IS IN SYNCH WITH THE PANE
	MapData data = (MapData)app.getDataComponent();
	data.setNodes(canvas.getChildren());

	// AND NOW SETUP THE WORKSPACE
	workspace = new BorderPane();
	((BorderPane)workspace).setCenter(canvas);
        ((BorderPane)workspace).setLeft(editToolbar);
        editToolbar.toFront();
    }
    
    // HELPER SETUP METHOD
    private void initControllers() {
	// MAKE THE EDIT CONTROLLER
	mapController = new MapProcessController(app);
	
	// NOW CONNECT THE BUTTONS TO THEIR HANDLERS
	LineEdit.setOnAction(e->{
	    mapController.processEditLine();
	});
        LineSelector.getSelectionModel().selectedItemProperty().addListener(e -> {
            mapController.processSelectLine();
        });
	AddLineButton.setOnAction(e->{          
	    mapController.processAddLine();
	});
        
        RemoveLineButton.setOnAction(e->{
	    mapController.processRemoveLine();
	});

	AddStationButton.setOnAction(e->{ 
	    mapController.processAddStationToLine();
	});
	RemoveStationButton.setOnAction(e->{ 
	    mapController.processRemoveStationFromLine();
	});
	ListAllStationsButton.setOnAction(e->{ 
	    mapController.processListAll();
	});
	LineThicknessSlider.setOnMouseReleased(e-> {          
                mapController.processSelectLineThickness();
	});
	AddStationToCanvasButton.setOnAction(e->{ 
	    mapController.processAddStation();
	});
        RemoveStationFromCanvasButton.setOnAction(e->{ 
	    mapController.processRemoveStation();
	});
        
        StationColorPicker.setOnAction(e->{
	   mapController.processChangeStationColor();
	});
        
        SnapButton.setOnAction(e->{ 
	    mapController.processSnapToGrid();
	});
        
        MoveLabelButton.setOnAction(e->{ 
	    mapController.processMoveLabel();
	});
        
        RotateLabelButton.setOnAction(e->{ 
	    mapController.processRotateLabel();
	});
        
        StationRadiusSlider.setOnMouseReleased(e-> {          
                mapController.processSelectStationRadius();
	});
        
        FindRouteButton.setOnAction(e->{ 
	    mapController.processFindRoute();
	});
        
        SetImageBackgroundButton.setOnAction(e->{ 
	    mapController.processSetImageBackground();
	});
       
        AddImageButton.setOnAction(e->{ 
	    mapController.processAddImage();
	});
        
        AddLabelButton.setOnAction(e->{ 
	    mapController.processAddLabel();
	});
        
        RemoveElementButton.setOnAction(e->{ 
	    mapController.processRemoveElement();
	});
        
        FontColorPicker.setOnAction(e->{
	   mapController.processChangeFontColor();
	}); 
        
         BoldButton.setOnAction(e->{
            mapController.processBoldText();
            
	});
        ItalicButton.setOnAction(e->{
            mapController.processItalicText();
            
	});
        
        FontFamilySelector.getSelectionModel().selectedItemProperty().addListener(e -> {
	    mapController.processChangeFontFamily();
	});
        FontSizeSelector.getSelectionModel().selectedItemProperty().addListener(e -> {
	    mapController.processChangeFontSize();
	});
        
        GridCheckBox.selectedProperty().addListener(e -> {
	    mapController.processShowGrid();
	});
        ZoomInButton.setOnAction(e->{
            mapController.processZoomIn();
            
	});
        ZoomOutButton.setOnAction(e->{
            mapController.processZoomOut();
            
	});
        MapSizeIncButton.setOnAction(e->{
            mapController.processIncreaseMapSize();
            
	});
        MapSizeDecButton.setOnAction(e->{
            mapController.processDecreaseMapSize();
            
	});               
        backgroundColorPicker.setOnAction(e->{
	   mapController.processChangeBackgroundColor();
	});
       gui.getWindow().addEventFilter(KeyEvent.KEY_PRESSED,
                e ->{ 
               if(e.getCode() == KeyCode.D){
                   canvas.setTranslateX(canvas.getTranslateX()+10);
                   canvas.getParent().layout();
               }
               else if(e.getCode() == KeyCode.A){
                   canvas.setTranslateX(canvas.getTranslateX()-10);
                   canvas.getParent().layout();
               }
               else if(e.getCode() == KeyCode.W){
                   canvas.setTranslateY(canvas.getTranslateY()-10);
                   canvas.getParent().layout();
               }
               else if(e.getCode() == KeyCode.S){
                   canvas.setTranslateY(canvas.getTranslateY()+10);
                   canvas.getParent().layout();
               }
            }
          );
        
        
	// MAKE THE CANVAS CONTROLLER	
	canvasController = new MapCanvasController(app);
	canvas.setOnMousePressed(e->{
	    canvasController.processCanvasMousePress((int)e.getX(), (int)e.getY());
	});
	canvas.setOnMouseReleased(e->{
	    canvasController.processCanvasMouseRelease((int)e.getX(), (int)e.getY());
	});
	canvas.setOnMouseDragged(e->{
	    canvasController.processCanvasMouseDragged((int)e.getX(), (int)e.getY());
	});
    }

    // HELPER METHOD
    public void loadSelectedNodeSettings(Node node) {
        MapData data = (MapData)app.getDataComponent();
	if (node != null) {
            if(node instanceof DraggableLineEnd){
                for (Iterator<Node> iterator = data.getNodes().iterator(); iterator.hasNext(); ) 
                {
                    Node n = iterator.next();          
                    if(n instanceof LineWrap){
                            if(((LineWrap)n).LineName.equalsIgnoreCase(((DraggableLineEnd)node).LineName)){
                               LineEdit.setStyle("-fx-background-color: "+(((Color)((LineWrap)n).getStroke()).toString().replace("0x", "#")));
                               LineSelector.getSelectionModel().select(((DraggableLineEnd)node).LineName);
                               data.setLineColor((Color)((LineWrap)n).getStroke());
                               data.setCurrentLineThickness((int)((LineWrap)n).getStrokeWidth(),LineSelector.getSelectionModel().getSelectedItem());
                               MoveLabelButton.setDisable(true);
                               RotateLabelButton.setDisable(true);
                               SnapButton.setDisable(false);
                               FontColorPicker.setValue((Color)((DraggableLineEnd)node).Textlink.getFill());
                               FontFamilySelector.getSelectionModel().select(((DraggableLineEnd)node).Textlink.getFont().getFamily());
                               FontSizeSelector.getSelectionModel().select(((DraggableLineEnd)node).Textlink.getFont().getSize());
                               break;
                            }
                    }   
                }
            }
            else if(node instanceof LineWrap){
                LineEdit.setStyle("-fx-background-color: "+(((Color)((LineWrap)node).getStroke()).toString().replace("0x", "#")));
                LineSelector.getSelectionModel().select(((LineWrap)node).LineName);
                data.setLineColor((Color)((LineWrap)node).getStroke());
                data.setCurrentLineThickness((int)((LineWrap)node).getStrokeWidth(),LineSelector.getSelectionModel().getSelectedItem());   
                LineThicknessSlider.setValue((int)((LineWrap)node).getStrokeWidth());
                MoveLabelButton.setDisable(true);
                RotateLabelButton.setDisable(true);
                SnapButton.setDisable(true);
                FontColorPicker.setDisable(true);
                FontFamilySelector.setDisable(true);
                FontSizeSelector.setDisable(true);
                BoldButton.setDisable(true);
                ItalicButton.setDisable(true);
            }
            else if(node instanceof DraggableStation){
                StationSelector.getSelectionModel().select(((DraggableStation) node).StationName);
                StationColorPicker.setValue((Color)((DraggableStation) node).getFill());
                data.setcurrentStationRadius((int) ((DraggableStation) node).getRadius());
                StationRadiusSlider.setValue(((DraggableStation) node).getRadius());
                MoveLabelButton.setDisable(false);
                RotateLabelButton.setDisable(false);
                SnapButton.setDisable(false);
                FontColorPicker.setDisable(false);
                FontFamilySelector.setDisable(false);
                FontSizeSelector.setDisable(false);
                BoldButton.setDisable(false);
                ItalicButton.setDisable(false);
                FontColorPicker.setValue((Color)((DraggableStation)node).text.getFill());
                FontFamilySelector.getSelectionModel().select(((DraggableStation)node).text.getFont().getFamily());
                FontSizeSelector.getSelectionModel().select(((DraggableStation)node).text.getFont().getSize());
            }
            else if(node instanceof DraggableText){
                MoveLabelButton.setDisable(true);
                RotateLabelButton.setDisable(true);
                SnapButton.setDisable(true);
                FontColorPicker.setDisable(false);
                FontFamilySelector.setDisable(false);
                FontSizeSelector.setDisable(false);
                BoldButton.setDisable(false);
                ItalicButton.setDisable(false);
                FontColorPicker.setValue((Color)((DraggableText)node).getFill());
                FontFamilySelector.getSelectionModel().select(((DraggableText)node).getFont().getFamily());
                FontSizeSelector.getSelectionModel().select(((DraggableText)node).getFont().getSize());
            }
             else if(node instanceof DraggableImage){                
                MoveLabelButton.setDisable(true);
                RotateLabelButton.setDisable(true);
                SnapButton.setDisable(true);
                FontColorPicker.setDisable(true);
                FontFamilySelector.setDisable(true);
                FontSizeSelector.setDisable(true);
                BoldButton.setDisable(true);
                ItalicButton.setDisable(true);
            }
	}
    }

    /**
     * This function specifies the CSS style classes for all the UI components
     * known at the time the workspace is initially constructed. Note that the
     * tag editor controls are added and removed dynamicaly as the application
     * runs so they will have their style setup separately.
     */
    public void initStyle() {
	// NOTE THAT EACH CLASS SHOULD CORRESPOND TO
	// A STYLE CLASS SPECIFIED IN THIS APPLICATION'S
	// CSS FILE
	canvas.getStyleClass().add(CLASS_RENDER_CANVAS);
	// COLOR PICKER STYLE
	//fillColorPicker.getStyleClass().add(CLASS_BUTTON);
	//outlineColorPicker.getStyleClass().add(CLASS_BUTTON);
	backgroundColorPicker.getStyleClass().add(CLASS_BUTTON);
	
	editToolbar.getStyleClass().add(CLASS_EDIT_TOOLBAR);
	row1.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
	row2.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
	row3.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
	MetroLineLabel.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
	
        LineSelector.getStyleClass().add(CLASS_COMBOBOX_CONTROL);
        StationSelector.getStyleClass().add(CLASS_COMBOBOX_CONTROL);
        ToStationSelector.getStyleClass().add(CLASS_COMBOBOX_ROUTE_CONTROL);
        FromStationSelector.getStyleClass().add(CLASS_COMBOBOX_ROUTE_CONTROL);
        FontFamilySelector.getStyleClass().add(CLASS_COMBOBOX_CONTROL);
        
	row4.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
	MetroStationsLabel.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
	row5.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
	DecorLabel.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
	row6.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
	FontLabel.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
        NavigationLabel.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
    }

    /**
     * This function reloads all the controls for editing logos
     * the workspace.
     */
    @Override
    public void reloadWorkspace(AppDataComponent data) {
	MapData dataManager = (MapData)data;
	/*if (dataManager.isInState(MapState.STARTING_RECTANGLE)) {
	    selectionToolButton.setDisable(false);
	    removeButton.setDisable(true);
	    rectButton.setDisable(true);
	    ellipseButton.setDisable(false);
	}
	else if (dataManager.isInState(MapState.STARTING_ELLIPSE)) {
	    selectionToolButton.setDisable(false);
	    removeButton.setDisable(true);
	    rectButton.setDisable(false);
	    ellipseButton.setDisable(true);
	}
	else if (dataManager.isInState(MapState.SELECTING_SHAPE) 
		|| dataManager.isInState(MapState.DRAGGING_SHAPE)
		|| dataManager.isInState(MapState.DRAGGING_NOTHING)) {
	    boolean shapeIsNotSelected = dataManager.getSelectedShape() == null;
	    selectionToolButton.setDisable(true);
	    removeButton.setDisable(shapeIsNotSelected);
	    rectButton.setDisable(false);
	    ellipseButton.setDisable(false);
	    moveToFrontButton.setDisable(shapeIsNotSelected);
	    moveToBackButton.setDisable(shapeIsNotSelected);
	}
	
	removeButton.setDisable(dataManager.getSelectedShape() == null);
	backgroundColorPicker.setValue(dataManager.getBackgroundColor());*/
    }
    
    @Override
    public void resetWorkspace() {
        // WE ARE NOT USING THIS, THOUGH YOU MAY IF YOU LIKE
    }
      
    @Override
    public void UndoNode() {
       MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
       jTPS.undoTransaction();   
        if(jTPS.mostRecentTransaction==-1)
        {
            app.getGUI().disableUndo(true);
        }
            app.getGUI().disableRedo(false);
    }

    @Override
    public void RedoNode() {
       MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent(); 
       workspace.jTPS.doTransaction();
        if(workspace.jTPS.mostRecentTransaction==jTPS.transactions.size()-1)    
           app.getGUI().disableRedo(true);   
        app.getGUI().disableUndo(false);
    }
    
}