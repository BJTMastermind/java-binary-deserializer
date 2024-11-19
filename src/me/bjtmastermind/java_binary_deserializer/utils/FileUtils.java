package me.bjtmastermind.java_binary_deserializer.utils;

import java.io.FileWriter;

import com.thoughtworks.xstream.io.json.JsonWriter;

public class FileUtils {

    public static boolean writeToFile(String filename, String data) {
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(data);
            writer.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean writePPrintToFile(String filename, String data) {
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(data);
            writer.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean writeJSONToFile(String filename, String data) {
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(filename));
            writer.setValue(data);
            writer.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
