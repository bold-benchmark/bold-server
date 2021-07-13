package org.bold.conneg;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class AcceptedContentTypesTest {

    public static final String TEST_FIELD = "text/turtle,application/n-triples;q=0.9,application/rdf+xml;q=0.7,application/trig,application/n-quads;q=0.9,application/ld+json;q=0.8,*/*;q=0.5";

    @Test
    public void testParse() {
        AcceptedContentTypes accepted = new AcceptedContentTypes(TEST_FIELD);

        List<String> ct = accepted.getContentTypes();

        assert ct.size() == 7;

        assert ct.indexOf("text/turtle") == 0 || ct.indexOf("text/turtle") == 1;
        assert ct.indexOf("*/*") == 6;

        assert ct.indexOf("application/rdf+xml") > ct.indexOf("application/n-tripes");
    }

}
