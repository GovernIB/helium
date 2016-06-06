/**
 * 
 */
package net.conselldemallorca.helium.core.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.activation.MimetypesFileTypeMap;

import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.DocumentFormatRegistry;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;

/**
 * Utilitats per a conversió de documents amb OpenOffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class OpenOfficeUtils {

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
		logger.info("Conversió OpenOffice (" +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut.available() + "bytes, " +
				"extensioSortida=" + extensioSortida + ")");
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
			final InputStream in,
			final DocumentFormat inputFormat,
			final OutputStream out,
			final DocumentFormat outputFormat) throws Exception {
		final String host = getPropertyHost();
		final int port = getPropertyPort();
		final OpenOfficeConnection connection = new SocketOpenOfficeConnection(host, port);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			Future<String> future = executor.submit(new Callable<String>() {
				@Override
				public String call() throws Exception {
					connection.connect();
					DocumentConverter converter = new StreamOpenOfficeDocumentConverter(
							connection,
							getDocumentFormatRegistry());
					converter.convert(
							in,
							inputFormat,
							out,
							outputFormat);
					return "Ok";
			    }
			});
			if (getPropertyTimeout() != -1)
				future.get(getPropertyTimeout(), TimeUnit.SECONDS);
			else
				future.get();
		} catch (TimeoutException e) {
			throw new SistemaExternTimeoutException(
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					"(Conversió OpenOffice)", 
					e);
		} finally {
			if (connection.isConnected())
				connection.disconnect();
		}
		executor.shutdownNow();
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
		if (documentFormatRegistry == null)
			documentFormatRegistry = new DefaultDocumentFormatRegistry();
		return documentFormatRegistry;
	}

	private String getPropertyHost() {
		return GlobalProperties.getInstance().getProperty("app.conversio.openoffice.host");
	}
	private int getPropertyPort() {
		return Integer.parseInt(GlobalProperties.getInstance().getProperty("app.conversio.openoffice.port"));
	}
	private int getPropertyTimeout() {
		String timeout = GlobalProperties.getInstance().getProperty("app.conversio.openoffice.timeout");
		if (timeout == null)
			return -1;
		else
			return Integer.parseInt(timeout);
	}
	private static final Logger logger = LoggerFactory.getLogger(OpenOfficeUtils.class);
}
