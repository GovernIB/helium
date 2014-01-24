/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.OpenOfficeUtils;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;

import org.springframework.stereotype.Component;

/**
 * Helper per a convertir entre diferents formats de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConversioDocumentHelper {

	private OpenOfficeUtils openOfficeUtils;



	public void convertirArxiuPerVista(
			Document document,
			ArxiuDto arxiu) throws Exception {
		if (isActiuConversioVista()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			String extensioVista = getExtensioVista(document);
			getOpenOfficeUtils().convertir(
					arxiu.getNom(),
					arxiu.getContingut(),
					extensioVista,
					baos);
			arxiu.setNom(
					nomArxiuAmbExtensio(
							arxiu.getNom(),
							extensioVista));
			arxiu.setContingut(baos.toByteArray());
		}
	}

	public void convertir(
			String arxiuNom,
			byte[] arxiuContingut,
			String extensioSortida,
			OutputStream sortida) throws Exception {
		getOpenOfficeUtils().convertir(
				arxiuNom,
				arxiuContingut,
				extensioSortida,
				sortida);
	}



	private boolean isActiuConversioVista() {
		String actiuConversio = (String)GlobalProperties.getInstance().get("app.conversio.actiu");
		if (!"true".equalsIgnoreCase(actiuConversio))
			return false;
		String actiuConversioVista = (String)GlobalProperties.getInstance().get("app.conversio.vista.actiu");
		if (actiuConversioVista == null)
			actiuConversioVista = (String)GlobalProperties.getInstance().get("app.conversio.gentasca.actiu");
		return "true".equalsIgnoreCase(actiuConversioVista);
	}
	private String getExtensioVista(Document document) {
		String extensioVista = null;
		if (isActiuConversioVista()) {
			if (document.getConvertirExtensio() != null && document.getConvertirExtensio().length() > 0) {
				extensioVista = document.getConvertirExtensio();
			} else {
				extensioVista = (String)GlobalProperties.getInstance().get("app.conversio.vista.extension");
				if (extensioVista == null)
					extensioVista = (String)GlobalProperties.getInstance().get("app.conversio.gentasca.extension");
			}
		}
		return extensioVista;
	}
	private String nomArxiuAmbExtensio(String fileName, String extensio) {
		if (extensio == null || extensio.length() == 0)
			return fileName;
		int indexPunt = fileName.lastIndexOf(".");
		if (indexPunt != -1) {
			String nom = fileName.substring(0, indexPunt);
			return nom + "." + extensio;
		} else {
			return fileName + "." + extensio;
		}
	}

	private OpenOfficeUtils getOpenOfficeUtils() {
		if (openOfficeUtils == null)
			openOfficeUtils = new OpenOfficeUtils();
		return openOfficeUtils;
	}

}
