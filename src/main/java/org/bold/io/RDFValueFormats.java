package org.bold.io;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;

import java.nio.charset.Charset;
import java.util.*;

/**
 * Container for data types that are also content formats, which includes:
 * <ul>
 *     <li>rdf:HTML, see <href a="https://www.w3.org/TR/rdf-schema/#ch_html">definition</href> in RDF Schema 1.1</li>
 *     <li>rdf:XMLLiteral, see <href a="https://www.w3.org/TR/rdf-schema/#ch_xmlliteral">definition</href> in RDF Schema 1.1</li>
 *     <li>rdf:JSON, see <href a="https://www.w3.org/TR/json-ld11/#the-rdf-json-datatype">definition</href> in JSON-LD 1.1</li>
 * </ul>
 */
public class RDFValueFormats {

    public static final RDFFormat HTML = new RDFFormat(
            "HTML", Arrays.asList("text/html"), Charset.defaultCharset(), Arrays.asList("html"), RDF.HTML,
            false, false, false);

    public static final RDFFormat XML = new RDFFormat(
            "XML", Arrays.asList("text/xml"), Charset.defaultCharset(), Arrays.asList("xml"), RDF.XMLLITERAL,
            false, false, false);

    public static final RDFFormat JSON = new RDFFormat(
            "JSON", Arrays.asList("application/json"), Charset.defaultCharset(), Arrays.asList("json"),
            // note: rdf:JSON was added later to the RDF namespace, it is not included in RDF4J (yet)
            SimpleValueFactory.getInstance().createIRI(RDF.NAMESPACE, "JSON"),
            false, false, false);

    public static final RDFFormat TXT = new RDFFormat(
            "TXT", Arrays.asList("text/plain"), Charset.defaultCharset(), Arrays.asList("txt"), null,
            false, false, false);

    private static final Collection<RDFFormat> formats = Arrays.asList(HTML, XML, JSON, TXT);

    static {
        RDFWriterRegistry registry = RDFWriterRegistry.getInstance();

        registry.add(new RDFValueWriterFactory(HTML));
        registry.add(new RDFValueWriterFactory(XML));
        registry.add(new RDFValueWriterFactory(JSON));
        registry.add(new RDFValueWriterFactory(TXT));
    }

    public static RDFFormat getFormatForMediaType(String mediaType) {
        for (RDFFormat f : formats) {
            if (f.getDefaultMIMEType().equals(mediaType)) return f;
        }

        return null;
    }

}
