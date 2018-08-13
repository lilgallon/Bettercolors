package com.bettercolors.view;

import java.awt.Toolkit;
import javax.swing.*;

public abstract class AbstractWindow extends JFrame{
    
    public AbstractWindow(String title, int width, int height) {
    	super(title);
    	setBounds((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-width/2,(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-height/2,width,height);
    	setResizable(true);
    	setVisible(false);
    }
    
    public void update(){
    	repaint();
    }

    public void toggle(){ setVisible(!isVisible());}
}