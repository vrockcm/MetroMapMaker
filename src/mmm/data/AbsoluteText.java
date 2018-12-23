/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmm.data;

import javafx.scene.text.Text;

/**
 *
 * @author vrock
 */
public class AbsoluteText extends Text{
    
    public boolean isBold;
    public boolean isItalic;
    public AbsoluteText(String text){
        super(text);
        isBold=false;
        isItalic=false;
    }
    
}
