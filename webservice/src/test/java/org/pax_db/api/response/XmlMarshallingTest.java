package org.pax_db.api.response;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pax_db.api.search.Hits;
import org.pax_db.api.search.SearchResponse;
import org.pax_db.api.search.SearchResultId;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class XmlMarshallingTest {
    static JAXBContext searchCtx;
    static JAXBContext speciesCtx;
    static JAXBContext proteinsCtx;
    static JAXBContext proteinCtx;
    private static JAXBContext datasetCtx;

    static SearchResponse searchResponse;


    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void setup() throws JAXBException {
        searchCtx = JAXBContext.newInstance(SearchResponse.class);

        ArrayList<Hits> results = new ArrayList<Hits>();
        ArrayList<SearchResultId> hits = new ArrayList<SearchResultId>();
        hits.add(new SearchResultId("4235", "CDC42"));
        hits.add(new SearchResultId("7777", "CDC3"));
        results.add(new Hits("cdc", 4932L, hits, 2, 1, 10));

        hits = new ArrayList<SearchResultId>();
        hits.add(new SearchResultId("111", "ALD2"));
        hits.add(new SearchResultId("222", "POP7"));
        hits.add(new SearchResultId("333", "RPM2"));
        results.add(new Hits("p53", 4932L, hits, 3, 1, 10));
        searchResponse = new SearchResponse(results, "20ms");
    }


    @Test
    public void testSearchResponseMarshalling
            () throws JAXBException {
        StringWriter writer = new StringWriter();

        Marshaller marshaller = searchCtx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(searchResponse, writer);
        String xml = writer.toString();
        assertTrue("search not properly marshalled:" + xml,
                xml.contains("query=\"cdc\"")
                        && xml.contains("CDC3")
                        && xml.contains("query=\"p53\"")
                        && xml.contains("ALD2")
        );
//        SearchResponse r = (SearchResponse) searchCtx.createUnmarshaller().unmarshal(new StringReader(xml));
//        assertEquals("cdc", r.getResults().get(0).getQuery());
//        assertEquals("p53", r.getResults().get(1).getQuery());
    }

}
