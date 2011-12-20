/**
 * 
 */
package net.conselldemallorca.helium.core.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.MimetypesFileTypeMap;

import net.conselldemallorca.helium.core.util.GlobalProperties;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.DocumentFormatRegistry;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;

/**
 * Utilitats per a conversió de documents amb OpenOffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OpenOfficeUtils {

	private DocumentConverter documentConverter;
	private DocumentFormatRegistry documentFormatRegistry;



	public void convertir(
			String arxiuNom,
			byte[] arxiuContingut,
			String extensioSortida,
			OutputStream sortida) throws Exception {
		convertir(
				arxiuNom,
				new ByteArrayInputStream(arxiuContingut),
				extensioSortida,
				sortida);
	}
	public void convertir(
			String arxiuNom,
			InputStream arxiuContingut,
			String extensioSortida,
			OutputStream sortida) throws Exception {
		DocumentFormat inputFormat = formatPerNomArxiu(arxiuNom);
		DocumentFormat outputFormat = getDocumentFormatRegistry().getFormatByFileExtension(extensioSortida);
		if (!outputFormat.getFileExtension().equals(inputFormat.getFileExtension())) {
			convert(
					arxiuContingut,
					inputFormat,
					sortida,
					outputFormat);
		} else {
			byte[] buffer = new byte[1024];
			int len = arxiuContingut.read(buffer);
			while (len >= 0) {
				sortida.write(buffer, 0, len);
				len = arxiuContingut.read(buffer);
			}
			arxiuContingut.close();
			sortida.close();
		}
	}
	public String nomArxiuConvertit(
			String arxiuNom,
			String extensioSortida) {
		DocumentFormat outputFormat = getDocumentFormatRegistry().getFormatByFileExtension(extensioSortida);
		int indexPunt = arxiuNom.lastIndexOf(".");
		if (indexPunt != -1) {
			String nom = arxiuNom.substring(0, indexPunt);
			return nom + "." + outputFormat.getFileExtension();
		} else {
			return arxiuNom + "." + outputFormat.getFileExtension();
		}
	}
	public String getArxiuMimeType(String nomArxiu) {
		DocumentFormat format = formatPerNomArxiu(nomArxiu);
		if (format == null)
			return new MimetypesFileTypeMap().getContentType(nomArxiu);
		else
			return format.getMimeType();
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
	private DocumentFormat formatPerNomArxiu(String fileName) {
		int indexPunt = fileName.lastIndexOf(".");
		if (indexPunt != -1) {
			String extensio = fileName.substring(indexPunt + 1);
			return getDocumentFormatRegistry().getFormatByFileExtension(extensio);
		}
		return null;
	}

	private DocumentFormatRegistry getDocumentFormatRegistry() {
		initOpenOfficeConnection();
		return documentFormatRegistry;
	}
	private DocumentConverter getDocumentConverter() {
		initOpenOfficeConnection();
		return documentConverter;
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

	private String getPropertyHost() {
		return GlobalProperties.getInstance().getProperty("app.conversio.openoffice.host");
	}
	private int getPropertyPort() {
		return Integer.parseInt(GlobalProperties.getInstance().getProperty("app.conversio.openoffice.port"));
	}

}
