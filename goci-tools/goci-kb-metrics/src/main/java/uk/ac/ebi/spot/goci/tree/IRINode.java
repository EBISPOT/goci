package uk.ac.ebi.spot.goci.tree;

import org.semanticweb.owlapi.model.IRI;

import java.util.HashSet;
import java.util.Set;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 08/08/12
 */
public class IRINode {
    private IRI iri;
    private String label;
    private Set<IRINode> childNodes;

    private int count = 0;

    public IRINode(IRI iri) {
        this(iri, "unknown");
    }

    public IRINode(IRI iri, String label) {
        this.iri = iri;
        this.label = label;
        this.childNodes = new HashSet<IRINode>();
    }

    public IRI getIRI() {
        return iri;
    }

    public String getLabel() {
        return label;
    }

    public Set<IRINode> getChildNodes() {
        return childNodes;
    }

    public void addChildNode(IRINode iriNode) {
        childNodes.add(iriNode);
    }

    public int getUsageCount() {
        return count;
    }

    public void setUsageCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IRINode iriNode = (IRINode) o;

        if (iri != null ? !iri.equals(iriNode.iri) : iriNode.iri != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return iri != null ? iri.hashCode() : 0;
    }
}
