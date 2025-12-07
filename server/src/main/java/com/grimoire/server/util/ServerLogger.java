package com.grimoire.server.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ServerLogger {
    private static final Logger LOGGER = Logger.getLogger("GrimoireServer");
    private static boolean configured = false;

    public static void configure() {
        if (configured) return;
        
        try {
            File logDir = new File(".logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            FileHandler fileHandler = new FileHandler(".logs/server.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(true);
            LOGGER.setLevel(java.util.logging.Level.ALL);
            
            configured = true;
        } catch (IOException e) {
            System.err.println("Failed to configure server logger: " + e.getMessage());
        }
    }

    public static Logger get() {
        if (!configured) configure();
        return LOGGER;
    }
    
    public static void logError(String message, Throwable t) {
        get().log(java.util.logging.Level.SEVERE, message, t);
    }
    
    public static void logInfo(String message) {
        get().info(message);
    }
}
