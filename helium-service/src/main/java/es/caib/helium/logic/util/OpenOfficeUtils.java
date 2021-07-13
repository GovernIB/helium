/**
 * 
 */
package es.caib.helium.logic.util;

import es.caib.helium.logic.intf.exception.SistemaExternTimeoutException;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.activation.MimetypesFileTypeMap;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Utilitats per a conversió de documents amb OpenOffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class OpenOfficeUtils {

	@Autowired
	private DocumentConverter documentConverter;

	@Value("${es.caib.helium.conversio.timeout}")
	private Long timeout;

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
		DocumentFormat outputFormat = documentConverter.getFormatRegistry().getFormatByExtension(extensioSortida);
		if (!outputFormat.getExtension().equals(inputFormat.getExtension())) {
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
		DocumentFormat outputFormat = documentConverter.getFormatRegistry().getFormatByExtension(extensioSortida);
		int indexPunt = arxiuNom.lastIndexOf(".");
		if (indexPunt != -1) {
			String nom = arxiuNom.substring(0, indexPunt);
			return nom + "." + outputFormat.getExtension();
		} else {
			return arxiuNom + "." + outputFormat.getExtension();
		}
	}
	public String getArxiuMimeType(String nomArxiu) {
		DocumentFormat format = formatPerNomArxiu(nomArxiu);
		if (format == null)
			return new MimetypesFileTypeMap().getContentType(nomArxiu);
		else
			return format.getMediaType();
	}
	public String getArxiuExtensio(String nomArxiu) {
		int indexPunt = nomArxiu.lastIndexOf(".");
		if (indexPunt != -1) {
			return nomArxiu.substring(indexPunt + 1);
		} else {
			return null;
		}
	}

	private void convert(
			final InputStream in,
			final DocumentFormat inputFormat,
			final OutputStream out,
			final DocumentFormat outputFormat) throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Supplier<String> task = () -> {
			try {
				documentConverter
						.convert(in)
						.to(out)
						.as(outputFormat)
						.execute();
			} catch (OfficeException e) {
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
			}
			return "Ok";
		};
		var future = CompletableFuture.supplyAsync(task, executor);
			if (timeout != null)
				future.get(timeout, TimeUnit.SECONDS);
			else
				future.get();
		executor.shutdownNow();
	}

	private DocumentFormat formatPerNomArxiu(String fileName) {
		int indexPunt = fileName.lastIndexOf(".");
		if (indexPunt != -1) {
			String extensio = fileName.substring(indexPunt + 1);
			return documentConverter.getFormatRegistry().getFormatByExtension(extensio);
		}
		return null;
	}

	private static final Logger logger = LoggerFactory.getLogger(OpenOfficeUtils.class);
}
