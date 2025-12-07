package com.grimoire.client.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ClientLogger {
    private static final Logger LOGGER = Logger.getLogger("GrimoireClient");
    private static boolean configured = false;

    public static void configure() {
        if (configured) return;
        
        try {
            File logDir = new File(".logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            FileHandler fileHandler = new FileHandler(".logs/client.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(false); // Don't spam console
            LOGGER.setLevel(java.util.logging.Level.ALL);
            
            configured = true;
        } catch (IOException e) {
            System.err.println("Failed to configure logger: " + e.getMessage());
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
