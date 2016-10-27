package net.conselldemallorca.helium.webapp.v3.passarelafirma;

import java.util.Properties;

/**
 * Bean amb informació d'un plugin de firma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PassarelaFirmaPlugin {

	long pluginId;
	String nom;
	String descripcioCurta;
	String classe;
	Properties properties;

	public PassarelaFirmaPlugin() {
	}

	public PassarelaFirmaPlugin(
			long pluginId,
			String nom,
			String descripcioCurta,
			String classe,
			Properties properties) {
		this.pluginId = pluginId;
		this.nom = nom;
		this.descripcioCurta = descripcioCurta;
		this.classe = classe;
		this.properties = properties;

	}

	public long getPluginId() {
		return (pluginId);
	}
	public void setPluginId(long pluginId) {
		this.pluginId = pluginId;
	}
	public String getNom() {
		return (nom);
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDescripcioCurta() {
		return (descripcioCurta);
	}
	public void setDescripcioCurta(String descripcioCurta) {
		this.descripcioCurta = descripcioCurta;
	}
	public String getClasse() {
		return (classe);
	}
	public void setClasse(String classe) {
		this.classe = classe;
	}
	public Properties getProperties() {
		return (properties);
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}
