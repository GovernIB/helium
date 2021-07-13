package es.caib.helium.logic.util;

import es.caib.helium.logic.intf.util.GlobalProperties;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.document.DocumentFormatRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * Classe per converir documents a PDF.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConvertirPDF {

	@Autowired
	private DocumentConverter documentConverter;
	@Autowired
	private GlobalProperties globalProperties;

	public byte[] render(byte[] contingut, String nom) throws Exception {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(contingut);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		if (contingut != null) {
			if (getPropertyEnabled()) {
				DocumentFormat inputFormat = formatPerNomArxiu(nom);
				if (inputFormat == null)
					throw new IllegalArgumentException("format d'entrada no suportat");
				DocumentFormatRegistry documentFormatRegistry = documentConverter.getFormatRegistry();
				DocumentFormat outputFormat = documentFormatRegistry.getFormatByExtension(getPropertyOutputExtension().toLowerCase());
				if (outputFormat == null)
					throw new IllegalArgumentException("format de sortida no suportat");
				// No s'afegeix res al pdf.
				if (!outputFormat.getExtension().equals(inputFormat.getExtension())) {
					documentConverter.convert(inputStream)
							.to(outputStream)
							.as(outputFormat)
							.execute();
				} else {
					return contingut;
				}
			}
		}
		return outputStream.toByteArray();
	}

	public String getArxiuMimeType(String nomArxiu) {
		DocumentFormat format = formatPerNomArxiu(nomArxiu);
		return format.getMediaType();
	}


	private DocumentFormat formatPerNomArxiu(String fileName) {
		int indexPunt = fileName.lastIndexOf(".");
		if (indexPunt != -1) {
			String extensio = fileName.substring(indexPunt + 1);
			return documentConverter.getFormatRegistry().getFormatByExtension(extensio);
		}
		return null;
	}

	@SuppressWarnings("unused")
	private String nomArxiuConvertit(String fileName, DocumentFormat format) {
		int indexPunt = fileName.lastIndexOf(".");
		if (indexPunt != -1) {
			String nom = fileName.substring(0, indexPunt);
			return nom + "." + format.getExtension();
		} else {
			return fileName + "." + format.getExtension();
		}
	}

	private boolean getPropertyEnabled() {
		return "true".equals(globalProperties.getProperty("es.caib.helium.conversio.portasignatures.actiu"));
	}
	private String getPropertyOutputExtension() {
		return globalProperties.getProperty("es.caib.helium.conversio.portasignatures.extension");
	}

}
