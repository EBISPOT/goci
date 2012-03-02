package uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome;

import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 29/02/12
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */
abstract class ChromosomeRenderlet implements Renderlet{

    public String getDisplayName(){
        return getName();
    }

    public String getDescription() {
        return ("This is a renderlet displaying " + getDisplayName());
    }

    public boolean canRender(RenderletNexus nexus, Object owlEntity) {
        return false;
    }

    public String render(RenderletNexus nexus, Object owlEntity) {
        return null;
    }

    protected abstract URL getSVGFile();
}
