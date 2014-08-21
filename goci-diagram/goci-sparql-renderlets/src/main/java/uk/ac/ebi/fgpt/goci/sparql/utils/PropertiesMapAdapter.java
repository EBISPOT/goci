package uk.ac.ebi.fgpt.goci.sparql.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by dwelter on 19/05/14.
 */
public class PropertiesMapAdapter {
    public Properties properties;
    public Map<String, String> prefixMappings;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void init() {
        Map<String, String> prefixMappings = new HashMap<String, String>();
        getLog().debug("Initializing prefixMap using properties: " + properties);
        for (String prefix : getProperties().stringPropertyNames()) {
            String namespace = getProperties().getProperty(prefix);
            prefixMappings.put(prefix, namespace);
            getLog().debug("Next prefix mapping: " + prefix + " = " + namespace);
        }
        this.prefixMappings = Collections.unmodifiableMap(prefixMappings);
    }

    public Map<String, String> getPropertyMap() {
        return prefixMappings;
    }
}
