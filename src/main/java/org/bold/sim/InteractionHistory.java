package org.bold.sim;

import org.bold.gsp.GraphStoreListener;
import org.eclipse.rdf4j.model.IRI;

import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

public class InteractionHistory extends Stack<InteractionHistory.Timeslot> implements History, GraphStoreListener {

    class Timeslot {

        private final long update;

        private long averageRetrieval = 0l;

        private long averageReplacement = 0l;

        private long averageDeletion = 0l;

        private long averageExtension = 0l;

        private int retrievals = 0;

        private int replacements = 0;

        private int deletions = 0;

        private int extensions = 0;

        Timeslot(Long up) {
            this.update = up;
        }

    }

    public InteractionHistory() {
        this.clear();
    }

    @Override
    public void timeIncremented(Long updateTime) {
        this.add(new Timeslot(updateTime));
    }

    @Override
    public void clear() {
        super.clear();
        this.add(new Timeslot(0l));
    }

    @Override
    public void graphRetrieved(IRI graphName, Long opTime) {
        Timeslot head = this.peek();
        head.retrievals++;
        head.averageRetrieval = avg(head.averageRetrieval, opTime);
    }

    @Override
    public void graphReplaced(IRI graphName, Long opTime) {
        Timeslot head = this.peek();
        head.replacements++;
        head.averageReplacement = avg(head.averageReplacement, opTime);
    }

    @Override
    public void graphDeleted(IRI graphName, Long opTime) {
        Timeslot head = this.peek();
        head.deletions++;
        head.averageDeletion = avg(head.averageDeletion, opTime);
    }

    @Override
    public void graphExtended(IRI graphName, Long opTime) {
        Timeslot head = this.peek();
        head.extensions++;
        head.averageExtension = avg(head.averageExtension, opTime);
    }

    private Long avg(Long l1, Long l2) {
        if (l1 == 0l) return l2;
        else if (l2 == 0l) return l1;
        else return (l1 + l2) / 2;
    }

    public void write(Writer w) throws IOException {
        w.append("# \"iteration\"\t\"time update\"\t\"nb get\"\t\"avg time get\"\t\"nb put\"\t\"avg time put\"\t\"nb delete\"\t\"avg time delete\"\t\"nb post\"\t\"avg time post\"\n");
        for (int iteration = 0; iteration < this.size(); iteration++) {
            Timeslot slot = this.get(iteration);
            w.append(String.format("%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\n", iteration, slot.update, slot.retrievals, slot.averageRetrieval, slot.replacements, slot.averageReplacement, slot.deletions, slot.averageDeletion, slot.extensions, slot.averageExtension));
        }
    }

}
