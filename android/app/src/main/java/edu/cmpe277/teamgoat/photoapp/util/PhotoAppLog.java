package edu.cmpe277.teamgoat.photoapp.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by srkarra on 4/25/2015.
 */
public class PhotoAppLog {

    private Logger log;

    public PhotoAppLog(Logger log) {
        this.log = log;
    }

    public void debug(String msg) {
        log(Level.FINER, msg);
    }

    public void info(String msg) {
        log(Level.INFO, msg);
    }

    public void error(String msg) {
        log(Level.SEVERE, msg);
    }

    public void error(Exception e) {
        log(Level.SEVERE, getStackTrace(e));
    }

    public void error (String msg, Exception e) {
        error(msg);
        error(e);
    }

    public void log(Level logLevel, String msg) {
        log.log(logLevel,msg);
    }


    /**
     * Returns the stack trace for any exception
     *
     * @param e Exception with a stack trace
     * @return String containing the stack trace
     */
    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

}
