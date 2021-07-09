package es.caib.helium.logic.intf.util;

import java.util.Properties;

public interface GlobalProperties {

    public String getProperty(String key);
    public String getProperty(String key, String defaultValue);
    public boolean getAsBoolean(String key);
    public int getAsInt(String key);
    public long getAsLong(String key);
    public float getAsFloat(String key);
    public double getAsDouble(String key);
    public Properties findByPrefix(String prefix);
    public Properties findAll();
    public boolean isLlegirSystem();

    public Object setProperty(String key, String value);

}
