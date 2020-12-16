package com.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ZhuYX
 * @date 2020/11/2 13:42
 */
public final class LoggerUtils {

    public static Logger defaultLog = LoggerFactory.getLogger(LoggerUtils.class);

    public static final String TRACE = "TRACE";
    public static final String INFO = "INFO";
    public static final String DEBUG = "DEBUG";
    public static final String WARN = "WARN";
    public static final String ERROR = "ERROR";

    public static void infoIfEnable(Logger log, String msg, Object... args) {
        Logger logger = getLogger(log);
        if (logger.isInfoEnabled()) {
            logger.info(msg, args);
        }
    }

    public static void warnIfEnable(Logger log, String msg, Object... args) {
        Logger logger = getLogger(log);
        if (logger.isWarnEnabled()) {
            logger.warn(msg, args);
        }
    }

    public static void errorIfEnable(Logger log, String msg, Throwable thr) {
        Logger logger = getLogger(log);
        if (logger.isErrorEnabled()) {
            logger.error(msg, thr);
        }
    }
    public static void errorIfEnable(Logger log, String msg, Object... args) {
        Logger logger = getLogger(log);
        if (logger.isErrorEnabled()) {
            logger.error(msg, args);
        }
    }


    private static Logger getLogger(Logger logger) {
        return logger == null ? defaultLog : logger;
    }

}
