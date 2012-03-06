package uk.ac.ebi.fgpt.goci.pussycat.renderlet.trait;

import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 06/03/12
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */

abstract class TraitRenderlet implements Renderlet {
    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getDisplayName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canRender(RenderletNexus nexus, Object renderingEntity) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String render(RenderletNexus nexus, Object renderingEntity) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
