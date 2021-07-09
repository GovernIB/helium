package es.caib.helium.logic.util;

import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.document.DocumentFormatRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * Classe per converir documents a PDF.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConvertirPDF {

	@Autowired
	private DocumentConverter documentConverter;
//	private DocumentFormatRegistry documentFormatRegistry;



	public byte[] render(byte[] contingut, String nom) throws Exception {
		Boolean conversionEnabled = "true".equalsIgnoreCase((String) GlobalPropertiesImpl.getInstance().get("app.conversio.portasignatures.actiu"));
		boolean conversion = (conversionEnabled == null) ? getPropertyEnabled() : conversionEnabled.booleanValue();
		if (!getPropertyEnabled()) conversion = false;
		ByteArrayInputStream inputStream = new ByteArrayInputStream(contingut);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		if (contingut != null) {
			if (conversion) {
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
//					convert(
//							inputStream,
//							inputFormat,
//							outputStream,
//							outputFormat);
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



//	private DocumentConverter getDocumentConverter() {
//		initOpenOfficeConnection();
//		return documentConverter;
//	}

//	private DocumentFormatRegistry getDocumentFormatRegistry() {
//		initOpenOfficeConnection();
//		return documentFormatRegistry;
//	}
//	private void initOpenOfficeConnection() {
//		if (documentFormatRegistry == null)
//			documentFormatRegistry = new DefaultDocumentFormatRegistry();
//		if (documentConverter == null) {
//			String host = getPropertyHost();
//			int port = getPropertyPort();
//			documentConverter = new StreamOpenOfficeDocumentConverter(
//					new SocketOpenOfficeConnection(host, port),
//					documentFormatRegistry);
//		}
//	}

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
		return "true".equals(GlobalPropertiesImpl.getInstance().getProperty("app.conversio.portasignatures.actiu"));
	}
	private String getPropertyHost() {
		return GlobalPropertiesImpl.getInstance().getProperty("app.conversio.openoffice.host");
	}
	private int getPropertyPort() {
		return Integer.parseInt(GlobalPropertiesImpl.getInstance().getProperty("app.conversio.openoffice.port"));
	}
	private String getPropertyOutputExtension() {
		return GlobalPropertiesImpl.getInstance().getProperty("app.conversio.portasignatures.extension");
	}

}
