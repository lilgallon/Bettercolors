package com.bettercolors.view;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractWindow extends JFrame{
    
    public AbstractWindow(String title, int width, int height) {
    	super(title);
    	setBounds((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-width/2,(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-height/2,width,height);
    	setIconImage(new ImageIcon(this.getClass().getResource("/images/bettercolors_symbol.png")).getImage());
    	setResizable(true);
    	setVisible(false);
    }
    
    public void update(){
    	repaint();
    }

    public void toggle(){ setVisible(!isVisible());}
}