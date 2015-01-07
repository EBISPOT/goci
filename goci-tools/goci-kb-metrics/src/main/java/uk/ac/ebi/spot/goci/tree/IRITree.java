package uk.ac.ebi.spot.goci.tree;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 08/08/12
 */
public class IRITree {
    private static final boolean suppressNoData = true;

    private IRINode rootNode;

    public IRINode getRootNode() {
        return rootNode;
    }

    public void setRootNode(IRINode rootNode) {
        this.rootNode = rootNode;
    }

    public void prettyPrint(OutputStream out) {
        PrintWriter writer = new PrintWriter(out);

        writer.println("+-----------------------------------+");
        writer.println("| KnowledgeBase Tree Metrics Report |");
        writer.println("+-----------------------------------+");
        writer.println();
        writer.println("  Stats follow...");
        writer.println();
        writer.println();

        recursivelyPrettyPrint(writer, getRootNode(), 0);

        writer.println("*************************************");
        writer.println();
        writer.println();
        writer.flush();
    }

    private void recursivelyPrettyPrint(PrintWriter writer, IRINode node, int indent) {
        if (suppressNoData && node.getUsageCount() == 0) {
            // just don't display these results
        }
        else {
            // create leading space
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < indent; i++) {
                line.append("\t");
            }

            // create class description
            line.append(node.getLabel());
            line.append(" (").append(node.getIRI()).append(")");

            // create trailing space - max depth of EFO tree is about 13
            for (int i = 15; i > indent; i--) {
                line.append("\t");
            }

            // add usage count
            line.append(node.getUsageCount());

            // create line
            writer.println(line.toString());

            // recurse
            indent++;
            for (IRINode childNode : node.getChildNodes()) {
                recursivelyPrettyPrint(writer, childNode, indent);
            }
        }
    }
}
