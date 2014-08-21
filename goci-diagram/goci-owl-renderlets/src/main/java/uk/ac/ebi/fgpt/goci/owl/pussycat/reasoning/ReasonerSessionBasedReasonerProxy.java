package uk.ac.ebi.fgpt.goci.owl.pussycat.reasoning;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;

import java.util.List;
import java.util.Set;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 02/08/12
 */
public class ReasonerSessionBasedReasonerProxy implements OWLReasoner {
    private ReasonerSession reasonerSession;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public ReasonerSessionBasedReasonerProxy(ReasonerSession reasonerSession) {
        this.reasonerSession = reasonerSession;
    }

    public ReasonerSession getReasonerSession() {
        return reasonerSession;
    }

    private OWLReasoner lazyloadReasoner() {
        if (!getReasonerSession().isReasonerInitialized()) {
            getLog().debug("Received a request that requires delegation to the reasoner, but the reasoner is not yet initialized.");
        }

        while (!getReasonerSession().isReasonerInitialized()) {
            getLog().debug("Waiting...");
            synchronized (this) {
                try {
                    wait(60000);
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(
                            "Could not load reasoner, interrupted whilst waiting for classification completion", e);
                }
            }
        }

        try {
            return getReasonerSession().getReasoner();
        }
        catch (OWLConversionException e) {
            throw new RuntimeException("Could not load reasoner (" + e.getMessage() + ")", e);
        }
    }

    @Override public String getReasonerName() {
        return lazyloadReasoner().getReasonerName();
    }

    @Override public Version getReasonerVersion() {
        return lazyloadReasoner().getReasonerVersion();
    }

    @Override public BufferingMode getBufferingMode() {
        return lazyloadReasoner().getBufferingMode();
    }

    @Override public void flush() {
        lazyloadReasoner().flush();
    }

    @Override public List<OWLOntologyChange> getPendingChanges() {
        return lazyloadReasoner().getPendingChanges();
    }

    @Override public Set<OWLAxiom> getPendingAxiomAdditions() {
        return lazyloadReasoner().getPendingAxiomAdditions();
    }

    @Override public Set<OWLAxiom> getPendingAxiomRemovals() {
        return lazyloadReasoner().getPendingAxiomRemovals();
    }

    @Override public OWLOntology getRootOntology() {
        return lazyloadReasoner().getRootOntology();
    }

    @Override public void interrupt() {
        lazyloadReasoner().interrupt();
    }

    @Override public void precomputeInferences(InferenceType... inferenceTypes)
            throws ReasonerInterruptedException, TimeOutException, InconsistentOntologyException {
        lazyloadReasoner().precomputeInferences(inferenceTypes);
    }

    @Override public boolean isPrecomputed(InferenceType inferenceType) {
        return lazyloadReasoner().isPrecomputed(inferenceType);
    }

    @Override public Set<InferenceType> getPrecomputableInferenceTypes() {
        return lazyloadReasoner().getPrecomputableInferenceTypes();
    }

    @Override public boolean isConsistent() throws ReasonerInterruptedException, TimeOutException {
        return lazyloadReasoner().isConsistent();
    }

    @Override public boolean isSatisfiable(OWLClassExpression classExpression)
            throws ReasonerInterruptedException, TimeOutException, ClassExpressionNotInProfileException,
            FreshEntitiesException, InconsistentOntologyException {
        return lazyloadReasoner().isSatisfiable(classExpression);
    }

    @Override public Node<OWLClass> getUnsatisfiableClasses()
            throws ReasonerInterruptedException, TimeOutException, InconsistentOntologyException {
        return lazyloadReasoner().getUnsatisfiableClasses();
    }

    @Override public boolean isEntailed(OWLAxiom owlAxiom)
            throws ReasonerInterruptedException, UnsupportedEntailmentTypeException, TimeOutException,
            AxiomNotInProfileException, FreshEntitiesException, InconsistentOntologyException {
        return lazyloadReasoner().isEntailed(owlAxiom);
    }

    @Override public boolean isEntailed(Set<? extends OWLAxiom> owlAxioms)
            throws ReasonerInterruptedException, UnsupportedEntailmentTypeException, TimeOutException,
            AxiomNotInProfileException, FreshEntitiesException, InconsistentOntologyException {
        return lazyloadReasoner().isEntailed(owlAxioms);
    }

    @Override public boolean isEntailmentCheckingSupported(AxiomType<?> axiomType) {
        return lazyloadReasoner().isEntailmentCheckingSupported(axiomType);
    }

    @Override public Node<OWLClass> getTopClassNode() {
        return lazyloadReasoner().getTopClassNode();
    }

    @Override public Node<OWLClass> getBottomClassNode() {
        return lazyloadReasoner().getBottomClassNode();
    }

    @Override public NodeSet<OWLClass> getSubClasses(OWLClassExpression classExpression, boolean b)
            throws ReasonerInterruptedException, TimeOutException, FreshEntitiesException,
            InconsistentOntologyException, ClassExpressionNotInProfileException {
        return lazyloadReasoner().getSubClasses(classExpression, b);
    }

    @Override public NodeSet<OWLClass> getSuperClasses(OWLClassExpression classExpression, boolean b)
            throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException,
            ReasonerInterruptedException, TimeOutException {
        return lazyloadReasoner().getSuperClasses(classExpression, b);
    }

    @Override public Node<OWLClass> getEquivalentClasses(OWLClassExpression classExpression)
            throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException,
            ReasonerInterruptedException, TimeOutException {
        return lazyloadReasoner().getEquivalentClasses(classExpression);
    }

    @Override public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression classExpression)
            throws ReasonerInterruptedException, TimeOutException, FreshEntitiesException,
            InconsistentOntologyException {
        return lazyloadReasoner().getDisjointClasses(classExpression);
    }

    @Override public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
        return lazyloadReasoner().getTopObjectPropertyNode();
    }

    @Override public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
        return lazyloadReasoner().getBottomObjectPropertyNode();
    }

    @Override public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression,
                                                                                 boolean b)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getSubObjectProperties(owlObjectPropertyExpression, b);
    }

    @Override public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression,
                                                                                   boolean b)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getSuperObjectProperties(owlObjectPropertyExpression, b);
    }

    @Override public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getEquivalentObjectProperties(owlObjectPropertyExpression);
    }

    @Override public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getDisjointObjectProperties(owlObjectPropertyExpression);
    }

    @Override public Node<OWLObjectPropertyExpression> getInverseObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getInverseObjectProperties(owlObjectPropertyExpression);
    }

    @Override public NodeSet<OWLClass> getObjectPropertyDomains(OWLObjectPropertyExpression owlObjectPropertyExpression,
                                                                boolean b)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getObjectPropertyDomains(owlObjectPropertyExpression, b);
    }

    @Override public NodeSet<OWLClass> getObjectPropertyRanges(OWLObjectPropertyExpression owlObjectPropertyExpression,
                                                               boolean b)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getObjectPropertyRanges(owlObjectPropertyExpression, b);
    }

    @Override public Node<OWLDataProperty> getTopDataPropertyNode() {
        return lazyloadReasoner().getTopDataPropertyNode();
    }

    @Override public Node<OWLDataProperty> getBottomDataPropertyNode() {
        return lazyloadReasoner().getBottomDataPropertyNode();
    }

    @Override public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty owlDataProperty, boolean b)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getSubDataProperties(owlDataProperty, b);
    }

    @Override public NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty owlDataProperty, boolean b)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getSuperDataProperties(owlDataProperty, b);
    }

    @Override public Node<OWLDataProperty> getEquivalentDataProperties(OWLDataProperty owlDataProperty)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getEquivalentDataProperties(owlDataProperty);
    }

    @Override public NodeSet<OWLDataProperty> getDisjointDataProperties(OWLDataPropertyExpression owlDataPropertyExpression)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getDisjointDataProperties(owlDataPropertyExpression);
    }

    @Override public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty owlDataProperty, boolean b)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getDataPropertyDomains(owlDataProperty, b);
    }

    @Override public NodeSet<OWLClass> getTypes(OWLNamedIndividual owlNamedIndividual, boolean b)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getTypes(owlNamedIndividual, b);
    }

    @Override public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression classExpression, boolean b)
            throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException,
            ReasonerInterruptedException, TimeOutException {
        return lazyloadReasoner().getInstances(classExpression, b);
    }

    @Override public NodeSet<OWLNamedIndividual> getObjectPropertyValues(OWLNamedIndividual owlNamedIndividual,
                                                                         OWLObjectPropertyExpression owlObjectPropertyExpression)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getObjectPropertyValues(owlNamedIndividual, owlObjectPropertyExpression);
    }

    @Override public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual owlNamedIndividual,
                                                           OWLDataProperty owlDataProperty)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getDataPropertyValues(owlNamedIndividual, owlDataProperty);
    }

    @Override public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual owlNamedIndividual)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getSameIndividuals(owlNamedIndividual);
    }

    @Override public NodeSet<OWLNamedIndividual> getDifferentIndividuals(OWLNamedIndividual owlNamedIndividual)
            throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
            TimeOutException {
        return lazyloadReasoner().getDifferentIndividuals(owlNamedIndividual);
    }

    @Override public long getTimeOut() {
        return lazyloadReasoner().getTimeOut();
    }

    @Override public FreshEntityPolicy getFreshEntityPolicy() {
        return lazyloadReasoner().getFreshEntityPolicy();
    }

    @Override public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
        return lazyloadReasoner().getIndividualNodeSetPolicy();
    }

    @Override public void dispose() {
        lazyloadReasoner().dispose();
    }
}
