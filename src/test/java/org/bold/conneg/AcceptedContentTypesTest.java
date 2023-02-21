package org.bold.conneg;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class AcceptedContentTypesTest {

    /**
     * Default header sent by the RDF Browser Web extension (v1)
     */
    public static final String TEST_FIELD_1 = "text/turtle,application/n-triples;q=0.9,application/rdf+xml;q=0.7,application/trig,application/n-quads;q=0.9,application/ld+json;q=0.8,*/*;q=0.5";

    /**
     * Default header sent by the RDF Browser extension (v1.2.3)
     */
    public static final String TEST_FIELD_2 = "application/ld+json;q=1,application/n-quads;q=1,application/n-triples;q=1,application/rdf+xml;q=1,application/trig;q=1,text/turtle;q=1,text/n3;q=1,text/html;q=0.950,application/xhtml+xml;q=0.950,application/xml;q=0.850,image/avif;q=0.950,image/webp;q=0.950,*/*;q=0.750";

    @Test
    public void testParse1() {
        AcceptedContentTypes accepted = new AcceptedContentTypes(TEST_FIELD_1);

        List<String> ct = accepted.getContentTypes();

        assert ct.size() == 7;

        assert ct.indexOf("text/turtle") == 0 || ct.indexOf("text/turtle") == 1;
        assert ct.indexOf("*/*") == 6;

        assert ct.indexOf("application/rdf+xml") > ct.indexOf("application/n-tripes");
    }

    @Test
    public void testParse2() {
        AcceptedContentTypes accepted = new AcceptedContentTypes(TEST_FIELD_2);

        List<String> ct = accepted.getContentTypes();

        assert ct.size() == 13;

        assert ct.indexOf("text/turtle") < 7;
        assert ct.indexOf("*/*") == 12;

        assert ct.indexOf("text/html") > ct.indexOf("application/n-triples");
    }

}
