package com.example.hp.imageprocessing;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by HP on 28/9/2017.
 */

public class stackTrace {

    public static String trace(Exception ex) {
        StringWriter outStream = new StringWriter();
        ex.printStackTrace(new PrintWriter(outStream));
        return outStream.toString();
    }
}
