package net.conselldemallorca.helium.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.DocumentFormatRegistry;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;

/**
 * Classe per converir documents a PDF.
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */
public class ConvertirPDF {

	private DocumentConverter documentConverter;
	private DocumentFormatRegistry documentFormatRegistry;



	public byte[] render(byte[] contingut, String nom) throws Exception {
		Boolean conversionEnabled = "true".equalsIgnoreCase((String)GlobalProperties.getInstance().get("app.conversio.portasignatures.actiu"));
		boolean conversion = (conversionEnabled == null) ? getPropertyEnabled() : conversionEnabled.booleanValue();
		if (!getPropertyEnabled()) conversion = false;
		ByteArrayInputStream inputStream = new ByteArrayInputStream(contingut);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		if (contingut != null) {
			if (conversion) {
				DocumentFormat inputFormat = formatPerNomArxiu(nom);
				if (inputFormat == null)
					throw new IllegalArgumentException("format d'entrada no suportat");
				DocumentFormatRegistry documentFormatRegistry = getDocumentFormatRegistry();
				DocumentFormat outputFormat = documentFormatRegistry.getFormatByFileExtension(getPropertyOutputExtension().toLowerCase());
				if (outputFormat == null)
					throw new IllegalArgumentException("format de sortida no suportat");
				// No s'afegeix res al pdf.
				if (!outputFormat.getFileExtension().equals(inputFormat.getFileExtension())) {
					try {
						convert(
								inputStream,
								inputFormat,
								outputStream,
								outputFormat);
					} catch (Exception e) {
						System.out.println("ERROR de conversio: " + e.getMessage());
					}
				} else {
					return contingut;
				}
			}
		}
		return outputStream.toByteArray();
	}

	public String getArxiuMimeType(String nomArxiu) {
		DocumentFormat format = formatPerNomArxiu(nomArxiu);
		return format.getMimeType();
	}



	private DocumentConverter getDocumentConverter() {
		initOpenOfficeConnection();
		return documentConverter;
	}

	private DocumentFormatRegistry getDocumentFormatRegistry() {
		initOpenOfficeConnection();
		return documentFormatRegistry;
	}

	private void initOpenOfficeConnection() {
		if (documentFormatRegistry == null)
			documentFormatRegistry = new DefaultDocumentFormatRegistry();
		if (documentConverter == null) {
			String host = getPropertyHost();
			int port = getPropertyPort();
			documentConverter = new StreamOpenOfficeDocumentConverter(
					new SocketOpenOfficeConnection(host, port),
					documentFormatRegistry);
		}
	}

	private DocumentFormat formatPerNomArxiu(String fileName) {
		int indexPunt = fileName.lastIndexOf(".");
		if (indexPunt != -1) {
			String extensio = fileName.substring(indexPunt + 1);
			return getDocumentFormatRegistry().getFormatByFileExtension(extensio);
		}
		return null;
	}

	@SuppressWarnings("unused")
	private String nomArxiuConvertit(String fileName, DocumentFormat format) {
		int indexPunt = fileName.lastIndexOf(".");
		if (indexPunt != -1) {
			String nom = fileName.substring(0, indexPunt);
			return nom + "." + format.getFileExtension();
		} else {
			return fileName + "." + format.getFileExtension();
		}
	}

	private void convert(
			InputStream in,
			DocumentFormat inputFormat,
			OutputStream out,
			DocumentFormat outputFormat) {
		getDocumentConverter().convert(
				in,
				inputFormat,
				out,
				outputFormat);
	}

	private boolean getPropertyEnabled() {
		return "true".equals(GlobalProperties.getInstance().getProperty("app.conversio.portasignatures.actiu"));
	}
	private String getPropertyHost() {
		return GlobalProperties.getInstance().getProperty("app.conversio.openoffice.host");
	}
	private int getPropertyPort() {
		return Integer.parseInt(GlobalProperties.getInstance().getProperty("app.conversio.openoffice.port"));
	}
	private String getPropertyOutputExtension() {
		return GlobalProperties.getInstance().getProperty("app.conversio.portasignatures.extension");
	}

}
