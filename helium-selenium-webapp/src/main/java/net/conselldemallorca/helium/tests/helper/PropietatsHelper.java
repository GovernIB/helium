package net.conselldemallorca.helium.tests.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/** Classe helper per carregar i consultar les propietats. Si no es passa cap ruta
 * inicial es carreguen les propietats del WEB-INF/application.properties.
 */
@Component
public class PropietatsHelper
  implements EnvironmentAware
{
  @Autowired
  Environment env;
  private Properties properties = new Properties();
  
  public String getProperty(String key, String defaultValue)
  {
    return this.properties.getProperty(key, defaultValue);
  }
  
  public Integer getPropertyInt(String key, int defaultValue)
  {
    int ret;
    try
    {
      ret = Integer.parseInt(this.properties.getProperty(key, Integer.toString(defaultValue)));
    }
    catch (Exception e)
    {
      ret = defaultValue;
    }
    return Integer.valueOf(ret);
  }
  
  public Boolean getPropertyBool(String key, boolean defaultValue)
  {
    boolean ret;
    try
    {
      ret = Boolean.parseBoolean(this.properties.getProperty(key, Boolean.toString(defaultValue)));
    }
    catch (Exception e)
    {
      ret = defaultValue;
    }
    return Boolean.valueOf(ret);
  }
  
  public void setEnvironment(Environment environment)
  {
	  
    this.env = environment;
    
    InputStream is;
    String propertiesPath = this.env.getProperty("properties.path");
    try {
        if (propertiesPath != null) 
        	// Propietat passada per paràmetre
        	is = new FileInputStream(new File(propertiesPath));
        else {
        	propertiesPath = "/application.properties";
        	is = this.getClass().getResourceAsStream(propertiesPath);
        }
    	// Carrega el fitxer de propietats
        this.properties.load(is);
    } catch (Exception e) {
        System.err.println("Error carregant el fitxer de propietats: " + propertiesPath);
    	e.printStackTrace();
    }
  }
  
  public String getHeliumUrl()
  {
    return getProperty("app.base.url", "http://localhost:8080/helium");
  }
}
