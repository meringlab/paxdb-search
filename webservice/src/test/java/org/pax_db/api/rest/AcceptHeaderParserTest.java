package org.pax_db.api.rest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class AcceptHeaderParserTest {

    AcceptHeaderParser parser = new AcceptHeaderParser(3);
    private final int DEFAULT = 3;

    @Test
    public void
    test_return_default_version_if_none_specified
            () {
        assertEquals(DEFAULT, parser.parse(null));
        assertEquals(DEFAULT, parser.parse(""));
        assertEquals(DEFAULT, parser.parse("application/json"));
        assertEquals(DEFAULT, parser.parse("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
    }

    @Test
    public void
    test_detect_version
            () {
        assertEquals(1, parser.parse("application/vnd.paxdb.search+xml ;version=1"));
        assertEquals(2, parser.parse("application/vnd.paxdb.search+json;version=2"));
        assertEquals(1, parser.parse("application/xml;version=1"));
    }


    @Test
    public void
    test_handle_multiple_types
            () {
        assertEquals(1, parser.parse("text/html,application/xhtml+xml;q=0.9,application/xml;version=1,*/*;q=0.8"));
    }


    @Test
    public void
    test_handle_whitespace
            () {
        assertEquals(2, parser.parse("application/json;version= 2"));
        assertEquals(2, parser.parse("application/json;version = 2"));
        assertEquals(2, parser.parse("application/json; version =2"));
        assertEquals(2, parser.parse("application/json ; version = 2"));
    }


}
