/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmm.data;

import djf.AppTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import jtps.jTPS_Transaction;
import mmm.gui.MapWorkspace;

/**
 *
 * @author Vrockcm
 */
public class Transaction implements jTPS_Transaction  {
    AppTemplate app;
    Color backgroundColor;
    Node node;
    Node Afternode;
    boolean circular;
    boolean Aftercircular; 
    int CenterX;
    int CenterY;
    Color FillColor;
    Color StationFillColor;
    int ControlX;
    int ControlY;
    int LineThickness;
    String FontFamily;
    double FontSize;
    boolean bold;
    boolean italic;
    int Radius;  
    String textposition;
    int TextRotate;
    String LineName;
    
    
    //After The Action takes place
    Color AfterbackgroundColor;
    int AfterCenterX;
    int AfterCenterY;
    Color AfterFillColor;
    Color AfterStationFillColor;
    int AfterControlX;
    int AfterControlY;
    int AfterLineThickness;
    String AfterFontFamily;
    double AfterFontSize;
    boolean Afterbold;
    boolean Afteritalic;
    int AfterRadius;  
    String Aftertextposition;
    int AfterTextRotate;
    String AfterLineName;
    ArrayList<Node> AfterRemoval;
    //TYPES OF TRANSACTIONS
    boolean LineCreate;
    boolean StationCreate;
    boolean LINETHICKNESSCHANGE;
    boolean STATIONCOLORCHANGE;
    boolean SNAPCHANGE;
    boolean MOVELABEL;
    boolean ROTATELABEL;
    boolean STATIONRADIUSCHANGE;
    boolean MOVEELEMENT;
    boolean REMOVELEMENT;
    boolean CHANGEFONT;
    boolean CHANGEFONTCOLOR;
    boolean EDITLINE;
    
    
    public Transaction(Node nodeA,Color Backgroundcolor,AppTemplate initApp,ArrayList<Node> trash) {
            backgroundColor=Backgroundcolor;
            node=nodeA;
            app=initApp;
            if(trash!=null)
                AfterRemoval=trash;
            else
                AfterRemoval=new ArrayList<Node>();
           // LineCreate=CreateLine;
            //StationCreate=CreateStation;
            if(node instanceof DraggableStation){
                CenterX=(int)((DraggableStation)node).getCenterX();
                CenterY=(int)((DraggableStation)node).getCenterY();
                StationFillColor=(Color)((DraggableStation)node).getFill();
                Radius=(int)((DraggableStation)node).getRadius(); 
                FontFamily=((DraggableStation)node).text.getFont().getFamily();
                FontSize=((DraggableStation)node).text.getFont().getSize();
                bold=((DraggableStation)node).text.isBold;
                italic=((DraggableStation)node).text.isItalic;
                FillColor=(Color)((DraggableStation)node).text.getFill();
                textposition=((DraggableStation)node).StationTextPosition;
                TextRotate=((DraggableStation)node).StationTextRotate;
            }
            else if(node instanceof DraggableText){
                FontFamily=((DraggableText)node).getFont().getFamily();
                FontSize=((DraggableText)node).getFont().getSize();
                bold=((DraggableText)node).isBold;
                italic=((DraggableText)node).isItalic;
                CenterX=(int)((DraggableText)node).getX();
                CenterY=(int)((DraggableText)node).getY();
                FillColor=(Color)((DraggableText)node).getFill();
            }
            else if(node instanceof DraggableLineEnd){
               CenterX=(int)((DraggableLineEnd)node).getCenterX();
               CenterY=(int)((DraggableLineEnd)node).getCenterY();
               LineName=((DraggableLineEnd)node).LineName;
               circular=((DraggableLineEnd)node).circular;
            }
            else if(node instanceof LineWrap){
                ControlX=(int)((LineWrap)node).getControlX();
                ControlY=(int)((LineWrap)node).getControlY();
                LineThickness=(int)((LineWrap)node).getStrokeWidth();
                FillColor=(Color)((LineWrap)node).getStroke();
                LineName=((LineWrap)node).LineName;
            }
            else if(node instanceof AbsoluteText){
                FontFamily=((AbsoluteText)node).getFont().getFamily();
                FontSize=((AbsoluteText)node).getFont().getSize();
                bold=((AbsoluteText)node).isBold;
                italic=((AbsoluteText)node).isItalic;
                FillColor=(Color)((AbsoluteText)node).getFill();
            }
            else if(node instanceof DraggableImage){
                CenterX=(int)((DraggableImage)node).getX();
                CenterY=(int)((DraggableImage)node).getY();
            }
    }
    @Override
    public void doTransaction() {
        MapWorkspace work = (MapWorkspace) app.getWorkspaceComponent();
       MapData dataManager = (MapData) app.getDataComponent();
       dataManager.setBackgroundColor(AfterbackgroundColor);
       if(node==null && Afternode!=null){
                 dataManager.getNodes().addAll(AfterRemoval);
                 AfterRemoval.clear();
        }
       if(node!=null && Afternode==null && !AfterRemoval.isEmpty()){
            if(node instanceof DraggableStation){
                ArrayList<Node> temp=dataManager.RemoveStation((DraggableStation)node,true);
                AfterRemoval.clear();
                AfterRemoval.addAll(temp);
                work.getToStationSelecter().getItems().remove(((DraggableStation)node).StationName);
                work.getFromStationSelecter().getItems().remove(((DraggableStation)node).StationName);
            }
        }
        else if(node==null && Afternode==null && !AfterRemoval.isEmpty()){     //LINE REMOVAL
            dataManager.getNodes().removeAll(AfterRemoval);
        } 
       else if( LINETHICKNESSCHANGE==true){
            dataManager.setCurrentLineThickness(AfterLineThickness,LineName);
        }
       else if(STATIONCOLORCHANGE==true){
           dataManager.setStationColor(AfterStationFillColor);
        }
       else if(SNAPCHANGE==true){
           ((Circle)node).setCenterX(AfterCenterX);
           ((Circle)node).setCenterY(AfterCenterY);
        }
       else if(MOVELABEL==true){
           ((DraggableStation)node).setLabelLocation(Aftertextposition);
        }
       else if(ROTATELABEL==true){
           ((DraggableStation)node).setRotate(AfterTextRotate);
        }
       else if(STATIONRADIUSCHANGE ==true){
           ((DraggableStation)node).setRadius(AfterRadius);
       }
       else if(MOVEELEMENT==true){
           if(node instanceof Draggable && node instanceof LineWrap==false)
           ((Draggable)node).setLocationAndSize(AfterCenterX,AfterCenterY , 0, 0);
           else if(node instanceof LineWrap){
               ((Draggable)node).setLocationAndSize(AfterControlX,AfterControlY , 0, 0);
           }
       }
       else if(REMOVELEMENT==true){
           dataManager.removeNode(node);
       }
       else if(CHANGEFONT==true){
           if(node instanceof DraggableStation){
               ((DraggableStation)node).text.setFont(Font.font(AfterFontFamily, Afterbold?FontWeight.BOLD:FontWeight.NORMAL,Afteritalic?FontPosture.ITALIC:FontPosture.REGULAR, AfterFontSize));
            }
            else if(node instanceof DraggableText){
                ((DraggableText)node).setFont(Font.font(AfterFontFamily, Afterbold?FontWeight.BOLD:FontWeight.NORMAL,Afteritalic?FontPosture.ITALIC:FontPosture.REGULAR,AfterFontSize));
            }
            else if(node instanceof DraggableLineEnd){
                ((DraggableLineEnd)node).Textlink.setFont(Font.font(AfterFontFamily, Afterbold?FontWeight.BOLD:FontWeight.NORMAL,Afteritalic?FontPosture.ITALIC:FontPosture.REGULAR,AfterFontSize));
            }
            else if(node instanceof AbsoluteText){
                ((AbsoluteText)node).setFont(Font.font(AfterFontFamily, Afterbold?FontWeight.BOLD:FontWeight.NORMAL,Afteritalic?FontPosture.ITALIC:FontPosture.REGULAR,AfterFontSize));
            }
       }
       else if(EDITLINE==true){
           dataManager.EditLine(AfterLineName,LineName, AfterFillColor, Aftercircular);
       }
       else if(node!=null && Afternode!=null){
            if(node instanceof DraggableStation && Afternode instanceof DraggableStation && !dataManager.ContainsStations(((DraggableStation)node))){
                        String lineName="";
                        ((DraggableStation)node).setCenterX(CenterX);
                        ((DraggableStation)node).setCenterY(CenterY);
                        for(int i=0;i<AfterRemoval.size();i++)
                        {
                            if(AfterRemoval.get(i) instanceof LineWrap){
                                lineName=((LineWrap)AfterRemoval.get(i)).LineName;
                                break;
                            }
                        }
                dataManager.AddStationToLine((DraggableStation)node,AfterRemoval,lineName); 
            }
            else
                AfterRemoval=dataManager.RemoveStation((DraggableStation)node,false); 
        }      
    }

    @Override
    public void undoTransaction() {
        MapWorkspace work = (MapWorkspace) app.getWorkspaceComponent();
        MapData dataManager = (MapData) app.getDataComponent();
        dataManager.setBackgroundColor(backgroundColor);
        if(node==null && Afternode!=null){
             if(Afternode instanceof LineWrap){
                 AfterRemoval.add(Afternode);
                 AfterRemoval.add(((LineWrap)Afternode).one);
                 AfterRemoval.add(((DraggableLineEnd)((LineWrap)Afternode).one).Textlink);
                 AfterRemoval.add(((LineWrap)Afternode).two);
                 AfterRemoval.add(((DraggableLineEnd)((LineWrap)Afternode).two).Textlink);
                 dataManager.getNodes().removeAll(AfterRemoval);
             }
             if(Afternode instanceof DraggableStation){
                 AfterRemoval.add(((DraggableStation)Afternode).g);
                 dataManager.getNodes().removeAll(AfterRemoval);
                 work.getToStationSelecter().getItems().remove(((DraggableStation)Afternode).StationName);
                 work.getFromStationSelecter().getItems().remove(((DraggableStation)Afternode).StationName);
             }
             if(Afternode instanceof DraggableImage){
                 AfterRemoval.add(((DraggableImage)Afternode));
                 dataManager.getNodes().removeAll(AfterRemoval);
             }
             if(Afternode instanceof DraggableText){
                 AfterRemoval.add(((DraggableText)Afternode));
                 dataManager.getNodes().removeAll(AfterRemoval);
             }
        }
        else if(node!=null && Afternode==null && !AfterRemoval.isEmpty()){
            if(node instanceof DraggableStation){
                for (Iterator<Node> iterator = AfterRemoval.iterator(); iterator.hasNext(); ) 
                {
                    Node tempnode = iterator.next();           
                    if(tempnode instanceof Group){
                        ((DraggableStation)node).setCenterX(CenterX);
                        ((DraggableStation)node).setCenterY(CenterY);
                        String lineName="";
                        for(int i=0;i<AfterRemoval.size();i++)
                        {
                            if(AfterRemoval.get(i) instanceof LineWrap){
                                lineName=((LineWrap)AfterRemoval.get(i)).LineName;
                                break;
                            }
                        }
                        if(lineName.isEmpty()){
                            dataManager.addNode(tempnode);
                        }
                        else
                            dataManager.AddStationToLine((DraggableStation)node, AfterRemoval,lineName);
                        work.getToStationSelecter().getItems().add(((DraggableStation)node).StationName);
                        work.getFromStationSelecter().getItems().add(((DraggableStation)node).StationName);
                        break;
                    }                  
                }
            }
            else
                dataManager.getNodes().addAll(AfterRemoval);
        }
        else if(node==null && Afternode==null && !AfterRemoval.isEmpty()){     //LINE REMOVAL
            dataManager.getNodes().addAll(AfterRemoval);
            dataManager.MoveBackLine();
        }       
        else if(LINETHICKNESSCHANGE==true){
            dataManager.setCurrentLineThickness(LineThickness,LineName);
        }
        else if(STATIONCOLORCHANGE==true){
            dataManager.setStationColor(StationFillColor);
        }
        else if(SNAPCHANGE==true){
           ((Circle)node).setCenterX(CenterX);
           ((Circle)node).setCenterY(CenterY);
        }
        else if(MOVELABEL==true){
           ((DraggableStation)node).setLabelLocation(textposition);
        }
        else if(ROTATELABEL==true){
           ((DraggableStation)node).setRotate(TextRotate);
        }
        else if(STATIONRADIUSCHANGE ==true){
           ((DraggableStation)node).setRadius(Radius);
       }
         else if(MOVEELEMENT==true){
           if(node instanceof Draggable && node instanceof LineWrap==false)
           ((Draggable)node).setLocationAndSize(CenterX,CenterY , 0, 0);
           else if(node instanceof LineWrap){
               ((Draggable)node).setLocationAndSize(ControlX,ControlY , 0, 0);
           }               
       }
        else if(REMOVELEMENT==true){
           dataManager.addNode(node);
       }
        else if(CHANGEFONT==true){
           if(node instanceof DraggableStation){
               ((DraggableStation)node).text.setFont(Font.font(FontFamily, bold?FontWeight.BOLD:FontWeight.NORMAL,italic?FontPosture.ITALIC:FontPosture.REGULAR, FontSize));
            }
            else if(node instanceof DraggableText){
                ((DraggableText)node).setFont(Font.font(FontFamily, bold?FontWeight.BOLD:FontWeight.NORMAL,italic?FontPosture.ITALIC:FontPosture.REGULAR,FontSize));
            }
            else if(node instanceof DraggableLineEnd){
                ((DraggableLineEnd)node).Textlink.setFont(Font.font(FontFamily, bold?FontWeight.BOLD:FontWeight.NORMAL,italic?FontPosture.ITALIC:FontPosture.REGULAR,FontSize));
            }
            else if(node instanceof AbsoluteText){
                ((AbsoluteText)node).setFont(Font.font(FontFamily, bold?FontWeight.BOLD:FontWeight.NORMAL,italic?FontPosture.ITALIC:FontPosture.REGULAR,FontSize));
            }
       }
        else if(CHANGEFONTCOLOR==true){
                if(node instanceof DraggableText){
                 ((DraggableText)node).setFill(FillColor);
            } 
            else if(node instanceof DraggableLineEnd){
                 ((DraggableLineEnd)node).Textlink.setFill(FillColor);
            }
            else if(node instanceof DraggableStation){
                 ((DraggableStation)node).text.setFill(FillColor);
            }
            else if(node instanceof AbsoluteText && node instanceof DraggableText==false){
                 ((AbsoluteText)node).setFill(FillColor);
             }
        }
         else if(EDITLINE==true){
           dataManager.EditLine(LineName,AfterLineName, FillColor, circular);
       }
        else if(node!=null && Afternode!=null){
            if(node instanceof DraggableStation && Afternode instanceof DraggableStation && !dataManager.ContainsStations(((DraggableStation)node))){
                        String lineName="";
                        ((DraggableStation)node).setCenterX(CenterX);
                        ((DraggableStation)node).setCenterY(CenterY);
                        for(int i=0;i<AfterRemoval.size();i++)
                        {
                            if(AfterRemoval.get(i) instanceof LineWrap){
                                lineName=((LineWrap)AfterRemoval.get(i)).LineName;
                                break;
                            }
                        }
                dataManager.AddStationToLine((DraggableStation)node,AfterRemoval,lineName); 
            }
            else
                AfterRemoval=dataManager.RemoveStation((DraggableStation)node,false); 
        }
       
    }
     public void setAfterAction(Node newnode,Color Backgroundcolor,AppTemplate initApp,ArrayList<Node> trash){    
            AfterbackgroundColor=Backgroundcolor;
            app=initApp;
            Afternode=newnode;
            if(trash!=null)
                AfterRemoval=trash;
            if(node instanceof DraggableStation){
                AfterCenterX=(int)((DraggableStation)node).getCenterX();
                AfterCenterY=(int)((DraggableStation)node).getCenterY();
                AfterStationFillColor=(Color)((DraggableStation)node).getFill();
                AfterRadius=(int)((DraggableStation)node).getRadius(); 
                AfterFontFamily=((DraggableStation)node).text.getFont().getFamily();
                AfterFontSize=((DraggableStation)node).text.getFont().getSize();
                Afterbold=((DraggableStation)node).text.isBold;
                Afteritalic=((DraggableStation)node).text.isItalic;
                AfterFillColor=(Color)((DraggableStation)node).text.getFill();
                Aftertextposition=((DraggableStation)node).StationTextPosition;
                AfterTextRotate=((DraggableStation)node).StationTextRotate;
            }
            else if(node instanceof DraggableText){
                AfterFontFamily=((DraggableText)node).getFont().getFamily();
                AfterFontSize=((DraggableText)node).getFont().getSize();
                Afterbold=((DraggableText)node).isBold;
                Afteritalic=((DraggableText)node).isItalic;
                AfterCenterX=(int)((DraggableText)node).getX();
                AfterCenterY=(int)((DraggableText)node).getY();
                AfterFillColor=(Color)((DraggableText)node).getFill();
            }
            else if(node instanceof DraggableLineEnd){
               AfterCenterX=(int)((DraggableLineEnd)node).getCenterX();
               AfterCenterY=(int)((DraggableLineEnd)node).getCenterY();
               AfterLineName=((DraggableLineEnd)node).LineName;
               Aftercircular=((DraggableLineEnd)node).circular;
            }
            else if(node instanceof LineWrap){
               AfterControlX=(int)((LineWrap)node).getControlX();
               AfterControlY=(int)((LineWrap)node).getControlY();
               AfterLineThickness=(int)((LineWrap)node).getStrokeWidth();
               AfterLineName=((LineWrap)node).LineName;
               AfterFillColor=(Color)((LineWrap)node).getStroke();
            }
            else if(node instanceof AbsoluteText){
                AfterFontFamily=((AbsoluteText)node).getFont().getFamily();
                AfterFontSize=((AbsoluteText)node).getFont().getSize();
                Afterbold=((AbsoluteText)node).isBold;
                Afteritalic=((AbsoluteText)node).isItalic;
                AfterFillColor=(Color)((AbsoluteText)node).getFill();
            }
             else if(node instanceof DraggableImage){
                AfterCenterX=(int)((DraggableImage)node).getX();
                AfterCenterY=(int)((DraggableImage)node).getY();
            }
     }

    public void setEDITLINE(boolean EDITLINE) {
        this.EDITLINE = EDITLINE;
    }
     
    public void setAfterArray(ArrayList<Node> t){
        AfterRemoval=t;
    }
    public void setApp(AppTemplate app) {
        this.app = app;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setAfternode(Node Afternode) {
        this.Afternode = Afternode;
    }

    public void setCircular(boolean circular) {
        this.circular = circular;
    }

    public void setAftercircular(boolean Aftercircular) {
        this.Aftercircular = Aftercircular;
    }

    public void setCenterX(int CenterX) {
        this.CenterX = CenterX;
    }

    public void setCenterY(int CenterY) {
        this.CenterY = CenterY;
    }

    public void setFillColor(Color FillColor) {
        this.FillColor = FillColor;
    }

    public void setStationFillColor(Color StationFillColor) {
        this.StationFillColor = StationFillColor;
    }

    public void setControlX(int ControlX) {
        this.ControlX = ControlX;
    }

    public void setControlY(int ControlY) {
        this.ControlY = ControlY;
    }

    public void setLineThickness(int LineThickness) {
        this.LineThickness = LineThickness;
    }

    public void setFontFamily(String FontFamily) {
        this.FontFamily = FontFamily;
    }

    public void setFontSize(double FontSize) {
        this.FontSize = FontSize;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public void setRadius(int Radius) {
        this.Radius = Radius;
    }

    public void setTextposition(String textposition) {
        this.textposition = textposition;
    }

    public void setTextRotate(int TextRotate) {
        this.TextRotate = TextRotate;
    }

    public void setLineName(String LineName) {
        this.LineName = LineName;
    }

    public void setAfterbackgroundColor(Color AfterbackgroundColor) {
        this.AfterbackgroundColor = AfterbackgroundColor;
    }

    public void setAfterCenterX(int AfterCenterX) {
        this.AfterCenterX = AfterCenterX;
    }

    public void setAfterCenterY(int AfterCenterY) {
        this.AfterCenterY = AfterCenterY;
    }

    public void setAfterFillColor(Color AfterFillColor) {
        this.AfterFillColor = AfterFillColor;
    }

    public void setAfterStationFillColor(Color AfterStationFillColor) {
        this.AfterStationFillColor = AfterStationFillColor;
    }

    public void setAfterControlX(int AfterControlX) {
        this.AfterControlX = AfterControlX;
    }

    public void setAfterControlY(int AfterControlY) {
        this.AfterControlY = AfterControlY;
    }

    public void setAfterLineThickness(int AfterLineThickness) {
        this.AfterLineThickness = AfterLineThickness;
    }

    public void setAfterFontFamily(String AfterFontFamily) {
        this.AfterFontFamily = AfterFontFamily;
    }

    public void setAfterFontSize(double AfterFontSize) {
        this.AfterFontSize = AfterFontSize;
    }

    public void setAfterbold(boolean Afterbold) {
        this.Afterbold = Afterbold;
    }

    public void setAfteritalic(boolean Afteritalic) {
        this.Afteritalic = Afteritalic;
    }

    public void setAfterRadius(int AfterRadius) {
        this.AfterRadius = AfterRadius;
    }

    public void setAftertextposition(String Aftertextposition) {
        this.Aftertextposition = Aftertextposition;
    }

    public void setAfterTextRotate(int AfterTextRotate) {
        this.AfterTextRotate = AfterTextRotate;
    }

    public void setAfterLineName(String AfterLineName) {
        this.AfterLineName = AfterLineName;
    }

    public void setAfterRemoval(ArrayList<Node> AfterRemoval) {
        this.AfterRemoval = AfterRemoval;
    }

    public void setLineCreate(boolean LineCreate) {
        this.LineCreate = LineCreate;
    }

    public void setStationCreate(boolean StationCreate) {
        this.StationCreate = StationCreate;
    }

    public void setLINETHICKNESSCHANGE(boolean LINETHICKNESSCHANGE) {
        this.LINETHICKNESSCHANGE = LINETHICKNESSCHANGE;
    }

    public void setSTATIONCOLORCHANGE(boolean STATIONCOLORCHANGE) {
        this.STATIONCOLORCHANGE = STATIONCOLORCHANGE;
    }

    public void setSNAPCHANGE(boolean SNAPCHANGE) {
        this.SNAPCHANGE = SNAPCHANGE;
    }

    public void setMOVELABEL(boolean MOVELABEL) {
        this.MOVELABEL = MOVELABEL;
    }

    public void setROTATELABEL(boolean ROTATELABEL) {
        this.ROTATELABEL = ROTATELABEL;
    }

    public void setSTATIONRADIUSCHANGE(boolean STATIONRADIUSCHANGE) {
        this.STATIONRADIUSCHANGE = STATIONRADIUSCHANGE;
    }

    public void setMOVEELEMENT(boolean MOVEELEMENT) {
        this.MOVEELEMENT = MOVEELEMENT;
    }

    public void setREMOVELEMENT(boolean REMOVELEMENT) {
        this.REMOVELEMENT = REMOVELEMENT;
    }

    public void setCHANGEFONT(boolean CHANGEFONT) {
        this.CHANGEFONT = CHANGEFONT;
    }

    public void setCHANGEFONTCOLOR(boolean CHANGEFONTCOLOR) {
        this.CHANGEFONTCOLOR = CHANGEFONTCOLOR;
    }
 
     
}

