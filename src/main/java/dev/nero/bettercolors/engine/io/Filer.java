package dev.nero.bettercolors.engine.io;

import java.io.File;

public class Filer {

    protected final String FILENAME;

    /**
     * @param filename name of the file to use (from the settings directory).
     */
    public Filer(String filename){
        if(!filename.startsWith("bc_") && !filename.startsWith("_bc")) {
            filename = "bc_" + filename;
        }

        FILENAME = filename;
    }

    public String getCompeletePath() {
        return getSettingsDirectory().toString() + "\\" + FILENAME;
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
