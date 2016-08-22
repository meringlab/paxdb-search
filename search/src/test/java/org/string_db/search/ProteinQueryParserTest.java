package org.string_db.search;

import org.junit.Before;
import org.junit.Test;
import org.string_db.analysis.IndexConfig;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.fail;
import static org.string_db.analysis.IndexConfig.Fields.*;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class ProteinQueryParserTest {
    ProteinQueryParser cut = IndexConfig.getConfig().getParser();
    Map<String, String> testSpec;

    @Before
    public void setup() {
        testSpec = new LinkedHashMap();
        //test for id/external id:
        testSpec.put("123", ID + ":123");
        testSpec.put("9606.ENSP000023023", ID + ":9606.ensp000023023");
        testSpec.put("4932.YAL031W-A", ID + ":4932.yal031w-a");
        testSpec.put("6239.CE7X_3.1", ID + ":6239.ce7x_3.1");

        //else: all other fields
        testSpec.put("CDC", NAME + ":cdc " + PARTIAL_MATCH_NAME + ":cdc " +
                OTHER_NAMES + ":cdc " + PARTIAL_MATCH_OTHER_NAMES + ":cdc " + ANNOTATION + ":cdc");
        testSpec.put("BEST:GH24664:1", NAME + ":best:gh24664:1 " + PARTIAL_MATCH_NAME + ":best:gh24664:1 " +
                OTHER_NAMES + ":best:gh24664:1 " + PARTIAL_MATCH_OTHER_NAMES + ":best:gh24664:1 " + ANNOTATION + ":best:gh24664:1");

    }

    @Test
    public void test() throws Exception {
        for (Map.Entry<String, String> e : testSpec.entrySet()) {
            assertQueryEquals(e.getKey(), e.getValue());
        }
    }


    private void assertQueryEquals(String query, String result)
            throws Exception {
        String s = cut.parse(query).toString();
        if (!s.equals(result)) {
            fail("Query /" + query + "/ yielded /" + s + "/, expecting /" + result + "/");
        }
    }


}
