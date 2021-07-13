package org.bold.gsp;

import org.eclipse.rdf4j.model.IRI;

public interface GraphStoreListener {

    void graphRetrieved(IRI graphName, Long opTime);

    void graphReplaced(IRI graphName, Long opTime);

    void graphDeleted(IRI graphName, Long opTime);

    void graphExtended(IRI graphName, Long opTime);

}
