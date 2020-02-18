package de.vcs.utils.log;

import org.citydb.config.project.global.LogLevel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class ODRLogger {

    private static ODRLogger instance;
    private final static Logger LOGGER = Logger.getLogger("SunPotLogger");
    private static Handler consoleHandler;
    private static Handler fileHandler;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss");
    private static ODRLogger spl = ODRLogger.getInstance();

    private ODRLogger() {
        try {
            Date today = new Date();
            fileHandler = new FileHandler(
                    dateFormat.format(today) + "_odr_log.txt");
            fileHandler.setFormatter(new LogMessageFormatter());
        } catch (SecurityException | IOException e) {
            spl.log(LogLevel.ERROR, "InputOutputException", e);
        }
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(fileHandler);
        consoleHandler = new StreamHandler(System.out,
                new LogMessageFormatter());
        LOGGER.addHandler(consoleHandler);
        consoleHandler.setLevel(Level.ALL);
        LOGGER.setLevel(Level.ALL);
    }

    public static ODRLogger getInstance() {
        if (instance == null) {
            instance = new ODRLogger();
        }
        return ODRLogger.instance;
    }

    public void log(LogLevel level, String msg) {
        LOGGER.log(translateLogLevel(level), msg);
    }

    public void log(LogLevel level, String msg, Throwable thrown) {
        LOGGER.log(translateLogLevel(level), msg, thrown);
    }

    public void log(Throwable thrown) {
        log(LogLevel.ERROR, thrown.getMessage(), thrown);
    }

    public void debug(String msg) {
        log(LogLevel.DEBUG, msg);
    }

    public void info(String msg) {
        log(LogLevel.INFO, msg);
    }

    public void warn(String msg) {
        log(LogLevel.WARN, msg);
    }

    public void error(String msg) {
        log(LogLevel.ERROR, msg);
    }

    public void setLevel(LogLevel level) {
        consoleHandler.setLevel(translateLogLevel(level));
        fileHandler.setLevel(translateLogLevel(level));
        LOGGER.setLevel(translateLogLevel(level));
    }

    private Level translateLogLevel(LogLevel logLevel) {
        if (logLevel == LogLevel.DEBUG) {
            return Level.FINE;
        }
        if (logLevel == LogLevel.WARN) {
            return Level.WARNING;
        }
        if (logLevel == LogLevel.INFO) {
            return Level.INFO;
        } else {
            return Level.SEVERE;
        }
    }
}
