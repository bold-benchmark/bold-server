package org.bold.conneg;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.bold.conneg.parser.AcceptHeaderBaseListener;
import org.bold.conneg.parser.AcceptHeaderLexer;
import org.bold.conneg.parser.AcceptHeaderParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds an list of content types, ordered by client preferences
 */
public class AcceptedContentTypes {

    private class AcceptedContentTypesListener extends AcceptHeaderBaseListener {

        @Override
        public void enterMediaRange(AcceptHeaderParser.MediaRangeContext ctx) {
            String type = (ctx.type() != null) ? ctx.type().getText() : "*";
            String subtype = (ctx.subtype() != null) ? ctx.subtype().getText() : "*";

            String contentType = String.format("%s/%s", type, subtype);

            Float q = 1f;

            for (AcceptHeaderParser.ParameterContext p : ctx.parameter()) {
                String parameterName = p.TOKEN(0).getText();

                if (parameterName.equals("q")) {
                    try {
                        q = Float.parseFloat(p.QVALUE().getText());
                    } catch (NumberFormatException e) {
                        q = 0f;
                        log.info("The requested content-type set in accept header has no valid 'q' parameter. Assuming 0.", e);
                    }
                }
            }

            preferences.put(contentType, q);
        }

    }

    private final Logger log = LoggerFactory.getLogger(AcceptedContentTypes.class);

    /**
     * ordered list of conte types, set by client preference parameters
     */
    private List<String> contentTypes = new ArrayList<>();

    /**
     * client preferences
     */
    private Map<String, Float> preferences = new HashMap<>();

    public AcceptedContentTypes(String headerField) {
        if (headerField == null) return;

        CharStream in = CharStreams.fromString(headerField);
        AcceptHeaderLexer lexer = new AcceptHeaderLexer(in);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AcceptHeaderParser parser = new AcceptHeaderParser(tokens);

        try {
            ParseTree ast = parser.accept();
            ParseTreeWalker.DEFAULT.walk(new AcceptedContentTypesListener(), ast);
        } catch (RuntimeException e) {
            log.info("The provided accept header cannot be part. Assuming empty.", e);
        }

        sort();
    }

    public List<String> getContentTypes() {
        return contentTypes;
    }

    private void sort() {
        contentTypes = new ArrayList<>(preferences.keySet());

        contentTypes.sort((ct1, ct2) -> {
            float q1 = preferences.get(ct1);
            float q2 = preferences.get(ct2);

            return Math.round(1000 * (q2 - q1));
        });
    }

}
