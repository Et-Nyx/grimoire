package com.grimoire.client.tui.framework;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerConfig {
    public static void setup() {
        try {
            FileHandler fileHandler = new FileHandler("tui-debug.log", false);
            fileHandler.setFormatter(new SimpleFormatter());
            
            Logger rootLogger = Logger.getLogger("");
            
            // Remove default handlers (ConsoleHandler) to prevent writing to stdout/stderr
            for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }
            
            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.ALL);
            
            // Silence Lanterna's internal logging if needed, or keep it for debug
            // Logger.getLogger("com.googlecode.lanterna").setLevel(Level.WARNING);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
