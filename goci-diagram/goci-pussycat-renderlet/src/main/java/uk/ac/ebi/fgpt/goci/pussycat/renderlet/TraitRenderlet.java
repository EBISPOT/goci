package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 06/03/12
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */

@ServiceProvider
public class TraitRenderlet implements Renderlet<OWLOntology, OWLIndividual> {

    /*
    * TraitRenderlet should retrieve the appropriate RGB colour for the trait being rendered from a hardcoded resource file
    *
    *
    * */

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
    public boolean canRender(RenderletNexus nexus, Object renderingContext, Object renderingEntity) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void render(RenderletNexus nexus, OWLOntology renderingContext, OWLIndividual renderingEntity) {

 /*       //get all the is_about individuals of this trait-assocation
         OWLDataFactory dataFactory = OWLManager.createOWLOntologyManager().getOWLDataFactory();

        OWLObjectProperty is_about = dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));
        OWLNamedIndividual[] related = individual.getObjectPropertyValues(is_about,ontology).toArray(new OWLNamedIndividual[0]);
        OWLClassExpression[] allTraits = null;

        System.out.println("No of is-about axioms: " + related.length);

        for(int k = 0; k < related.length; k++){
            OWLClassExpression[] allTypes = related[k].getTypes(ontology).toArray(new OWLClassExpression[0]);


//find the individual that is of type "experimental factor"
            for(int i = 0; i < allTypes.length; i++){
                OWLClass typeClass = allTypes[i].asOWLClass();

                if(typeClass.getIRI().equals(IRI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI))){
                    System.out.println("Found the trait   " + allTypes.length);
                    allTraits = allTypes;
                }
            }
        }

        OWLClass leaf = null;

        if(allTraits.length > 0){
            leaf = allTraits[0].asOWLClass();
            int largest = leaf.getSuperClasses(ontology).size();
            System.out.println(largest);
            for(int j = 1; j < allTraits.length; j++){
                OWLClass current = allTraits[j].asOWLClass();
                int parents = current.getSuperClasses(ontology).size();

                if(parents > largest){
                    System.out.println(parents + "\t" + largest);
                    largest = parents;
                    leaf = current;
                    System.out.println(largest);
                }
            }
        }

        OWLAnnotationProperty label = dataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

        for (OWLAnnotation annotation : leaf.getAnnotations(ontology, label)) {
            if (annotation.getValue() instanceof OWLLiteral) {
                OWLLiteral val = (OWLLiteral) annotation.getValue();
                name = val.getLiteral();
                System.out.println(name);
            }
        }            */

     }
}
