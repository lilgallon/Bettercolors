package com.bettercolors.io;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Filer {

    private String _filename;

    public Filer(String filename){
        if(!filename.endsWith(".properties")){
            filename += ".properties";
        }
        _filename = filename;
    }

    public void write(Map<String, String> options_to_write, boolean only_absents){
        Map<String, String> options = readAll();
        if(options != null) {
            if(only_absents){
                for(Map.Entry<String, String> option : options_to_write.entrySet()){
                    options.putIfAbsent(option.getKey(), option.getValue());
                }
            }else {
                options.putAll(options_to_write);
            }
        }else {
            options = options_to_write;
        }

        Properties prop = new Properties();
        OutputStream output = null;
        try {
            output = new FileOutputStream(getSettingsDirectory() + "\\" + _filename);
            for(Map.Entry<String, String> option : options.entrySet()){
                prop.setProperty(option.getKey(), option.getValue());
            }
            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param key the key of the property
     * @return the specific value associated to the key
     */
    public String read(String key){
        Properties prop = new Properties();

        String value;
        InputStream input;
        try{
            input = new FileInputStream(getSettingsDirectory().toString() + "\\" + _filename);
            prop.load(input);
            value = prop.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try{
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return value;
    }

    /**
     * @return  all the properties with their values.
     */
    public Map<String, String> readAll(){
        Properties prop = new Properties();

        Map<String, String> values = new HashMap<>();
        InputStream input;
        try{
            input = new FileInputStream(getSettingsDirectory().toString() + "\\" + _filename);
            prop.load(input);

            for(Object keyo : prop.keySet()){
                String key = (String) keyo;
                values.put(key, prop.getProperty(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try{
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return values;
    }

    private static File getSettingsDirectory() {
        // String settingsDir = System.getProperty("user.home");
        String settingsDir = System.getProperty("user.dir");
        if(settingsDir == null) {
            throw new IllegalStateException("user.dir==null");
        }
        File home = new File(settingsDir);
        File settingsDirectory = new File(home, "bettercolors");
        if(!settingsDirectory.exists()) {
            if(!settingsDirectory.mkdir()) {
                throw new IllegalStateException(settingsDirectory.toString());
            }
        }
        return settingsDirectory;
    }
}
