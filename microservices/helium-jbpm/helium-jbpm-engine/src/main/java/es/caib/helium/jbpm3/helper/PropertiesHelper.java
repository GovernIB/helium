package es.caib.helium.jbpm3.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class PropertiesHelper extends Properties {

    private static final String APPSERV_PROPS_PATH = "es.caib.helium.properties.path";
    private static PropertiesHelper instance = null;
    private boolean llegirSystem = true;

    public static PropertiesHelper getInstance() {
        return getProperties();
    }

    public PropertiesHelper() {
        super();
    }
    public PropertiesHelper(Resource resource) throws IOException {
        super();
        super.load(resource.getInputStream());
        if (instance == null) {
            instance = this;
        }
    }
    public static PropertiesHelper getProperties() {
        if (instance == null) {
            instance = new PropertiesHelper();
            String propertiesPath = System.getProperty(APPSERV_PROPS_PATH);
            if (propertiesPath != null) {
                instance.llegirSystem = false;
                log.info("Llegint les propietats de l'aplicació del path: " + propertiesPath);
                try {
                    if (propertiesPath.startsWith("classpath:")) {
                        instance.load(
                                PropertiesHelper.class.getClassLoader().getResourceAsStream(
                                        propertiesPath.substring("classpath:".length())));
                    } else if (propertiesPath.startsWith("file://")) {
                        FileInputStream fis = new FileInputStream(
                                propertiesPath.substring("file://".length()));
                        instance.load(fis);
                    } else {
                        FileInputStream fis = new FileInputStream(propertiesPath);
                        instance.load(fis);
                    }
                } catch (Exception ex) {
                    log.error("No s'han pogut llegir els properties", ex);
                }
            }
        }
        return instance;
    }

    public String getProperty(String key) {
        if (llegirSystem)
            return System.getProperty(key);
        else
            return super.getProperty(key);
    }
    public String getProperty(String key, String defaultValue) {
        String val = getProperty(key);
        return (val == null) ? defaultValue : val;
    }

    public boolean getAsBoolean(String key) {
        String value = getProperty(key);
        if (value != null)
            return new Boolean(getProperty(key)).booleanValue();
        else
            return false;
    }
    public int getAsInt(String key) {
        return new Integer(getProperty(key)).intValue();
    }
    public long getAsLong(String key) {
        return new Long(getProperty(key)).longValue();
    }
    public float getAsFloat(String key) {
        return new Float(getProperty(key)).floatValue();
    }
    public double getAsDouble(String key) {
        return new Double(getProperty(key)).doubleValue();
    }

    public Properties findByPrefix(String prefix) {
        Properties properties = new Properties();
        if (llegirSystem) {
            for (Object key: System.getProperties().keySet()) {
                if (key instanceof String) {
                    String keystr = (String)key;
                    if (prefix == null || keystr.startsWith(prefix)) {
                        properties.put(
                                keystr,
                                System.getProperty(keystr));
                    }
                }
            }
        } else {
            for (Object key: this.keySet()) {
                if (key instanceof String) {
                    String keystr = (String)key;
                    if (prefix == null || keystr.startsWith(prefix)) {
                        properties.put(
                                keystr,
                                getProperty(keystr));
                    }
                }
            }
        }
        return properties;
    }
    public Properties findAll() {
        return findByPrefix(null);
    }

    public boolean isLlegirSystem() {
        return llegirSystem;
    }

    private static final long serialVersionUID = 1L;

}
