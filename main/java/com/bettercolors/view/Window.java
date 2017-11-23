package com.bettercolors.view;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.*;
import javax.swing.text.*;

import javax.swing.event.*;
import javax.swing.GroupLayout.*;

public abstract class Window extends JFrame{
    
    public Window(String title, int width, int height) {
    	super(title);
    	setBounds((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-width/2,(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-height/2,width,height);
    	setResizable(false);
    	setVisible(false);
    }
    
    public void update(){
    	repaint();
    }

}