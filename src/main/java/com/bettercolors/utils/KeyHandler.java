package com.bettercolors.utils;

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class KeyHandler {
    private static HashMap<Integer, Boolean> keyStatus = new HashMap<>();

    public static boolean isKeyPressed(int keyCode) {
        synchronized (KeyHandler.class) {
            return keyStatus.getOrDefault(keyCode, false);
        }
    }

    public static void init() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ke -> {
            synchronized (KeyHandler.class) {
                switch (ke.getID()) {
                    case KeyEvent.KEY_PRESSED:
                        if(keyStatus.containsKey(ke.getKeyCode())){
                            keyStatus.replace(ke.getKeyCode(), true);
                        }else{
                            keyStatus.put(ke.getKeyCode(), true);
                        }
                        break;

                    case KeyEvent.KEY_RELEASED:
                        if(keyStatus.containsKey(ke.getKeyCode())){
                            keyStatus.replace(ke.getKeyCode(), false);
                        }else{
                            keyStatus.put(ke.getKeyCode(), false);
                        }
                        break;
                }
                return false;
            }
        });
    }
}