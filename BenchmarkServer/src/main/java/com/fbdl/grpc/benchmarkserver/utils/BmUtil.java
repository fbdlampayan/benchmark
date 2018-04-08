package com.fbdl.grpc.benchmarkserver.utils;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author FBDL
 */
public class BmUtil {
    
    private static PropertiesConfiguration properties;
    private static final Logger LOG = LoggerFactory.getLogger(BmUtil.class);
    
    public static void loadProperties(String propertiesFilepath) throws ConfigurationException {
        properties = new PropertiesConfiguration(propertiesFilepath);
        properties.setReloadingStrategy(new FileChangedReloadingStrategy());
    }
    
    public static String getPropertyValue(String propertyKey) {
        String value = properties.getProperty(propertyKey).toString();
        if(value == null) {
            LOG.error("no value found for key: {}", propertyKey);
        }
        return value;
    }
    
    public static int getPropertyNumericValue(String propertyKey) {
        String value = getPropertyValue(propertyKey);
        int result = 0;
        try {
            result = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            LOG.error("Key {} has no valid numeric value",propertyKey);
        }
        return result;
    }

    public static void initializeProperties() {
        BmProperties.INSTANCE.setEdgeCertKey(getPropertyValue("bm.keypath"));
        BmProperties.INSTANCE.setEdgeCertPath(getPropertyValue("bm.certpath"));
        BmProperties.INSTANCE.setEdgePort(getPropertyNumericValue("bm.port"));
        BmProperties.INSTANCE.setEdgeThreads(getPropertyNumericValue("bm.threads"));
    }
}
