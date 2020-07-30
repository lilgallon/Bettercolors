package dev.nero.bettercolors.engine.io;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class RawFiler extends Filer {

    /**
     * @param filename name of the file to use (from the settings directory)
     */
    public RawFiler(String filename) {
        super(filename);
    }

    /**
     * Writes the given line to the file
     * @param lineToWrite (should not have \n in it)
     * @param append if true, appends the line to the file, otherwise, overrides everything
     */
    public void write(String lineToWrite, boolean append) {
        try {
            FileWriter writer = new FileWriter(getCompeletePath(), append);
            writer.write((append ? "\n" : "") + lineToWrite.replaceAll("\n", ""));
            writer.close();
        } catch (IOException e) {
            System.out.println("Failed to write in " + getCompeletePath());
            e.printStackTrace();
        }
    }

    /**
     * Erases the given line from the file
     * @param lineToErase (should not have \n in it)
     * @return true if the line was found and erased
     */
    public boolean erase(String lineToErase) {
        ArrayList<String> lines = readAll();

        if (lines.contains(lineToErase)) {
            lines.remove(lineToErase);

            // Override what's written
            write(lines.get(0), false);
            lines.remove(0);

            // Append the rest
            for(String line : lines) {
                write(line, true);
            }

            return true;
        } else {
            System.out.println("Could not find:");
            System.out.println(lineToErase);
            System.out.println("In " + getCompeletePath());
            return false;
        }
    }

    /**
     * Reads all the lines of the file (returns null if an error occurred)
     */
    public ArrayList<String> readAll() {
        try {
            return new ArrayList<>(Files.readAllLines(Paths.get(getCompeletePath()), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to read file " + getCompeletePath());
            return null;
        }
    }
}
