package uk.ac.ebi.fgpt.goci.lang;

import uk.ac.ebi.fgpt.goci.model.GWASObject;

/**
 * A stubbing object that allows creation of a filter on a particular method call
 *
 * @author Tony Burdett
 * @date 03/06/14
 */
public class CallChain<T extends GWASObject> {
    private T template;

    public CallChain(T template) {
        this.template = template;
    }

    public <M> Argument<T, M> on(M methodCall) {
        return new Argument<T, M>();
    }
}

