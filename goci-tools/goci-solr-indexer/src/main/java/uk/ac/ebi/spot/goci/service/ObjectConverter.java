package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Document;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

/**
 * Generic wrapper class to convert any model objects into the corresponding Document object types.  The document type
 * parameter must be a class that contains a constructor that takes the model object as it's only parameter.
 *
 * @author Tony Burdett
 * @date 14/01/15
 */
@Service
public class ObjectConverter {
    private Collection<DocumentEnrichmentService> documentEnrichmentServices;
    private Map<Class, Collection<DocumentEnrichmentService>> documentToEnricherMap;

    @Autowired
    public ObjectConverter(Collection<DocumentEnrichmentService> documentEnrichmentServices) {
        this.documentEnrichmentServices = documentEnrichmentServices;
        this.documentToEnricherMap = new HashMap<>();
    }

    @PostConstruct
    public void inspectDocumentEnrichmentServices() {
        for (DocumentEnrichmentService documentEnrichmentService : documentEnrichmentServices) {
            Optional<Class<?>> documentType = getDocumentType(documentEnrichmentService);
            if (documentType.isPresent()) {
                if (!documentToEnricherMap.containsKey(documentType.get())) {
                    documentToEnricherMap.put(documentType.get(), new HashSet<>());
                }
                documentToEnricherMap.get(documentType.get()).add(documentEnrichmentService);
            }
        }
    }

    public <O, D extends Document<O>> D convert(O object, Class<D> documentType) {
        try {
            // generate document by creating a new instance and passing the object to the constructor
            D document = documentType.getDeclaredConstructor(object.getClass()).newInstance(object);

            // enrich using any available enrichment services
            // noinspection unchecked
            documentToEnricherMap.keySet()
                    .stream()
                    .filter(enricherType -> matches(enricherType, documentType))
                    .map(documentToEnricherMap::get)
                    .flatMap(Collection::stream)
                    .sorted(new Comparator<DocumentEnrichmentService>() {
                        @Override public int compare(DocumentEnrichmentService des1, DocumentEnrichmentService des2) {
                            return des1.getPriority() - des2.getPriority();
                        }
                    })
                    .forEach(documentEnrichmentService -> documentEnrichmentService.doEnrichment(document, object));

            // and return
            return document;
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Class<?>> getDocumentType(DocumentEnrichmentService serviceToTest) {
        Type[] types = serviceToTest.getClass().getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = ((ParameterizedType) type);
                if (parameterizedType.getRawType().equals(DocumentEnrichmentService.class)) {
                    Type[] typeArgs = parameterizedType.getActualTypeArguments();
                    for (Type typeArg : typeArgs) {
                        if (typeArg instanceof Class) {
                            return Optional.of((Class) typeArg);
                        }
                        else if (typeArg instanceof ParameterizedType) {
                            // only recurse to one level of abstraction, that's more than enough!
                            return Optional.of((Class) ((ParameterizedType) typeArg).getRawType());
                        }
                    }
                    if (typeArgs.length == 1 && typeArgs[0] instanceof Class) {
                        return Optional.of((Class) typeArgs[0]);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private boolean matches(Class<?> inferredDocumentType, Class<?> documentType) {
        return inferredDocumentType.isAssignableFrom(documentType);
    }
}