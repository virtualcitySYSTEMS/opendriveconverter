/*******************************************************************************
 * IMPORTANT NOTICE: This software is neither free nor Open Source.
 * It has been privately developed and implemented by 
 * @author Bruno Willenborg, <a href=mailto:bruno.willenborg@gmx.de>bruno.willenborg@gmx.de</a>
 * @author Maximilian Sindram, <a href=mailto:master@geotrend.de>master@geotrend.de</a>
 * @author Thomas H. Kolbe, <a href=mailto:tk@acm.org>tk@acm.org</a>
 *
 * The full copyright 2015-2018 of this software is with the authors. 
 * Usage and extension of the software is allowed to licensed users only.
 *
 * The implementation is based on concepts and findings developed
 * at the Chair of Geoinformatics, Technical University of Munich (TUM),
 * all of which have been published in articles and technical reports.
 *******************************************************************************/

package de.vcs.utils.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogMessageFormatter extends Formatter {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss z");

    /*
     * (non-Javadoc)
     * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
     */
    @Override
    public String format(LogRecord record) {
        Date date = new Date(record.getMillis());
        // msg header
        String msgHeader = "[ " + dateFormat.format(date) + " ] "
                + record.getLevel();
        // msg without exception attached
        if (record.getThrown() == null) {
            // is loglevel LEQ than fine?
            if (record.getLevel().intValue() <= Level.FINE.intValue()) {
                return record.getMessage() + System.lineSeparator();
            } else {
                return msgHeader + getMsgSeparator(msgHeader) + record.getMessage()
                        + System.lineSeparator() + System.lineSeparator();
            }
        } else {
            // msg with exception
            StringBuilder stb = new StringBuilder();
            stb.append(msgHeader + getMsgSeparator(msgHeader) + record.getMessage()
                    + System.lineSeparator());
            // StackTrace class name
            stb.append(record.getThrown().getClass().getName());
            stb.append(System.lineSeparator());
            // StackTrace elements
            for (StackTraceElement stackElem : record.getThrown().getStackTrace()) {
                stb.append("  ");
                stb.append(stackElem.toString());
                stb.append(System.lineSeparator());
            }
            stb.append(System.lineSeparator());
            return stb.toString();
        }
    }

    private String getMsgSeparator(String msgHeader) {
        StringBuilder stb = new StringBuilder(" ");
        for (int i = 0; i < (79 - msgHeader.length()); i++) {
            stb.append("-");
        }
        stb.append(System.lineSeparator());
        return stb.toString();
    }
}
