package org.pax_db.api.rest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pax_db.api.rest.search.SearchResourceRepository;
import org.pax_db.api.search.SearchResponse;
import org.string_db.search.ProteinHit;
import org.string_db.search.SearchResult;
import org.string_db.search.Searcher;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
@RunWith(JMock.class)
public class SearchResourceRepositoryTest {
    private final Mockery context = new Mockery();

    private final Searcher searcher = context.mock(Searcher.class);

    private final SearchResourceRepository repository = new SearchResourceRepository(searcher);

    @Test
    public void test_search() throws Exception {
        context.checking(new Expectations() {{
            one(searcher).search(Arrays.asList("dehydrogenase"), null, 1, 10);
            will(returnValue(Arrays.asList(new SearchResult("EDP09989",
                    Arrays.asList(new ProteinHit(407237L, "3055.EDP09989" , 1.0f, "<b>EDP09989</b>")), 1, 1, 10))));
        }});

        final SearchResponse res = (SearchResponse) repository.search("EDP09989", null, 1, 10);
        assertEquals("407237", res.getResults().get(0).getHits().get(0).getProteinId());
    }
}
