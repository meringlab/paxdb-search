package org.pax_db.api.rest;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class AcceptHeaderParser {

    private static final Pattern version = Pattern.compile(".+/.+;version=(\\d+).*");

    private final int defaultVersion;
    private static final Logger log = Logger.getLogger(AcceptHeaderParser.class);

    public AcceptHeaderParser(int defaultVersion) {
        this.defaultVersion = defaultVersion;
    }


    public int parse(String h) {
        if (h == null || h.trim().isEmpty()) {
            return defaultVersion;
        }
        h = h.replaceAll("\\s+", "");
        final Matcher matcher = version.matcher(h);
        if (matcher.matches()) {
            try {
                return Integer.valueOf(matcher.group(1));
            } catch (NumberFormatException e) {
                log.error("parsing header: " + h);
            }
        }
        return defaultVersion;
    }
}
