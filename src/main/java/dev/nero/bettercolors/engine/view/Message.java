package dev.nero.bettercolors.engine.view;

import java.awt.*;

public class Message {

    public String text;
    public Color color;
    public Boolean newline;

    public Message(String text, Color color, Boolean newline){
        this.text = text;
        this.color = color;
        this.newline = newline;
    }
}
