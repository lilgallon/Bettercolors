/*
 * Copyright 2018-2020
 * - Bettercolors Contributors (https://github.com/N3ROO/Bettercolors) and
 * - Bettercolors Engine Contributors (https://github.com/N3ROO/BettercolorsEngine)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.nero.bettercolors.engine.io;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Filer {

    private final String FILENAME;

    /**
     * @param filename name of the file to use (from the settings directory).
     */
    public Filer(String filename){
        if(!filename.endsWith(".properties")){
            filename += ".properties";
        }

        if(!filename.startsWith("bc_") && !filename.startsWith("_bc")) {
            filename = "bc_" + filename;
        }

        FILENAME = filename;
    }

    /**
     * It writes all the options to the file. If only_absent is set to true,
     * it will only write the options that are not already in the file, and ignore the others.
     * @param options_to_write options to write on the file,
     * @param only_absents if sets to true, it ignore the options already written in the file.
     */
    public void write(Map<String, String> options_to_write, boolean only_absents){
        Map<String, String> options = readAll();
        if(options != null) {
            if(only_absents) {
                for(Map.Entry<String, String> option : options_to_write.entrySet()){
                    options.putIfAbsent(option.getKey(), option.getValue());
                }
            } else {
                options.putAll(options_to_write);
            }
        } else {
            options = options_to_write;
        }

        Properties prop = new Properties();
        OutputStream output = null;
        try {
            output = new FileOutputStream(getSettingsDirectory() + "\\" + FILENAME);
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
    public String read(String key) {
        Properties prop = new Properties();

        String value;
        InputStream input;
        try{
            input = new FileInputStream(getSettingsDirectory().toString() + "\\" + FILENAME);
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
     * @return all the properties with their values.
     */
    Map<String, String> readAll() {
        Properties prop = new Properties();

        Map<String, String> values = new HashMap<>();
        InputStream input;
        try{
            input = new FileInputStream(getSettingsDirectory().toString() + "\\" + FILENAME);
            prop.load(input);

            for(Object key_o : prop.keySet()){
                String key = (String) key_o;
                values.put(key, prop.getProperty(key));
            }
        } catch (IOException e) {
            return null;
        }

        try{
            input.close();
        } catch (IOException e) {
            return null;
        }
        return values;
    }

    /**
     * @return the settings directory ("config" folder in the minecraft game directory).
     */
    public static File getSettingsDirectory() {
        String settingsDir = System.getProperty("user.dir");
        if(settingsDir == null) {
            throw new IllegalStateException("user.dir==null");
        }
        File home = new File(settingsDir);
        File settingsDirectory = new File(home, "config");
        if(!settingsDirectory.exists()) {
            if(!settingsDirectory.mkdir()) {
                throw new IllegalStateException(settingsDirectory.toString());
            }
        }
        return settingsDirectory;
    }
}