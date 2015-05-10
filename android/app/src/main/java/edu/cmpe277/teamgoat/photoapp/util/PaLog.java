package edu.cmpe277.teamgoat.photoapp.util;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by srkarra on 5/9/2015.
 */
public class PaLog {

    public static void info(String msg) {
        Log.i(IDs.BASE_LOGGING_TAG, msg);
    }

    public static void debug(String msg) {
        Log.d(IDs.BASE_LOGGING_TAG, msg);
    }

    public static void error(String msg) {
        Log.e(IDs.BASE_LOGGING_TAG, msg);
    }

    public static void error(Exception e) {
        Log.e(IDs.BASE_LOGGING_TAG, getStackTrace(e));
    }

    public static void error(String msg, Exception e) {
        error(msg);
        error(e);
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
