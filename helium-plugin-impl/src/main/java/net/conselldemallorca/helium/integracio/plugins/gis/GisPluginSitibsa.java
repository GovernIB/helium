package net.conselldemallorca.helium.integracio.plugins.gis;

import java.net.URL;
import java.util.List;

import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * Implementació de la interficie PersonesPlugin amb accés per JDBC.
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */

public class GisPluginSitibsa implements GisPlugin {
	
	public URL getUrlVisor() throws GisPluginException {
		try {
			String url = GlobalProperties.getInstance().getProperty("app.gis.plugin.sitibsa.url.visor");
			//if (url != null && url.length() > 0)
				return new URL(url);
			//return null;
		} catch (Exception ex) {
			throw new GisPluginException("No s'ha pogut obtenir la url", ex);
		}
	}

	public String getXMLExpedients(List<DadesExpedient> expedients) throws GisPluginException {
		try {
			if (expedients == null || expedients.size() == 0)
				return null;
			
			String urlBase = GlobalProperties.getInstance().getProperty("app.base.url");
	        
			Document document = DocumentHelper.createDocument();
	        Element root = document.addElement("msgExpedients");
	        Element exps = root.addElement("expedients");
	        Element estats = root.addElement("estats");
	        Element tipus = root.addElement("expedientsTipus");
	        
	        for (DadesExpedient exp : expedients){
	        	if (exp.getRefCatastral() != null && !exp.getRefCatastral().equals("")) {
			        Element expedient = exps.addElement("expedient");
			        expedient.addElement("refCatastral").addText(exp.getRefCatastral());
			        expedient.addElement("numero").addText(exp.getNumero() != null ? exp.getNumero() : " ");
			        expedient.addElement("titol").addText(exp.getTitol() != null ? exp.getTitol() : " ");
			        expedient.addElement("expedientTipusCodi").addText(exp.getExpedientTipusCodi() != null ? exp.getExpedientTipusCodi() : " ");
			        expedient.addElement("estatCodi").addText(exp.getEstatCodi() != null ? exp.getEstatCodi() : "INDEFINIT");
			        expedient.addElement("url").addText(urlBase + "/expedient/info.html?id=" + exp.getProcessInstanceId()); 
			        
			        if (exp.getExpedientTipusCodi() != null) {
				        // comprovam que no existeixi el tipus d'expedient en el document		        
				        Node node = document.selectSingleNode( "//msgExpedients/expedientsTipus/expedientTipus[contains(codi, '" + exp.getExpedientTipusCodi() + "')]" );
				        if (node == null || !node.hasContent()){
					        Element tipusExp = tipus.addElement("expedientTipus");
					        tipusExp.addElement("codi").addText(exp.getExpedientTipusCodi());
					        tipusExp.addElement("nom").addText(exp.getExpedientTipusNom());
				        }
				        
				        // comprovam que no existeixi l'estat en el document
				        if (exp.getEstatCodi() != null) {
					        node = document.selectSingleNode( "//msgExpedients/estats/estat[@expedientTipus='" + exp.getExpedientTipusCodi() +"' and contains(codi, '" + exp.getEstatCodi() + "')]" );
					        if (node == null || !node.hasContent()){
						        Element estat = estats.addElement("estat").addAttribute("expedientTipus", exp.getExpedientTipusCodi());
						        estat.addElement("codi").addText(exp.getEstatCodi());
						        estat.addElement("nom").addText(exp.getEstatNom());
					        }
				        } else {
				        	node = document.selectSingleNode( "//msgExpedients/estats/estat[@expedientTipus='" + exp.getExpedientTipusCodi() +"' and contains(codi, 'INDEFINIT')]" );
					        if (node == null || !node.hasContent()){
						        Element estat = estats.addElement("estat").addAttribute("expedientTipus", exp.getExpedientTipusCodi());
						        estat.addElement("codi").addText("INDEFINIT");
						        estat.addElement("nom").addText("Indefinit");
					        }
				        }
			        }
	        	}
			}
			
			return document.asXML();
		} catch (Exception ex) {
			throw new GisPluginException("No s'ha pogut trobar cap expedient", ex);
		}
	}

}
