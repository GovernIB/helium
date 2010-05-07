/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.util.GlobalProperties;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.DocumentFormatRegistry;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BarcodePDF417;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

/**
 * Vista per mostrar arxius
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ArxiuConvertirView extends ArxiuView {

	public static final String MODEL_ATTRIBUTE_CONVERSIONENABLED = "conversionEnabled";
	public static final String MODEL_ATTRIBUTE_OUTEXTENSION = "outputExtension";
	public static final String MODEL_ATTRIBUTE_ESTAMPA = "estampa";
	public static final String MODEL_ATTRIBUTE_ESTAMPA_POSX = "estampaX";
	public static final String MODEL_ATTRIBUTE_ESTAMPA_POSY = "estampaY";
	public static final String MODEL_ATTRIBUTE_ESTAMPA_ROTATION = "estampaRotation";

	private DocumentConverter documentConverter;
	private DocumentFormatRegistry documentFormatRegistry;



	@SuppressWarnings("unchecked")
	public void render(
			Map model,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader(HEADER_PRAGMA, "");
		response.setHeader(HEADER_EXPIRES, "");
		response.setHeader(HEADER_CACHE_CONTROL, "");
		byte[] data = (byte[])model.get(MODEL_ATTRIBUTE_DATA);
		String fileName = (String)model.get(MODEL_ATTRIBUTE_FILENAME);
		Boolean conversionEnabled = (Boolean)model.get(MODEL_ATTRIBUTE_CONVERSIONENABLED);
		boolean conversion = (conversionEnabled == null) ? getPropertyEnabled() : conversionEnabled.booleanValue();
		if (!getPropertyEnabled()) conversion = false;
		String outputExtension = (String)model.get(MODEL_ATTRIBUTE_OUTEXTENSION);
		String estampa = (String)model.get(MODEL_ATTRIBUTE_ESTAMPA);
		if (data != null) {
			if (conversion) {
				DocumentFormat inputFormat = formatPerNomArxiu(fileName);
				if (inputFormat == null)
					throw new IllegalArgumentException("format d'entrada no suportat");
				DocumentFormatRegistry documentFormatRegistry = getDocumentFormatRegistry();
				DocumentFormat outputFormat = documentFormatRegistry.getFormatByFileExtension(
						((outputExtension != null) ? outputExtension : getPropertyDefaultExtension()).toLowerCase());
				if (outputFormat == null)
					throw new IllegalArgumentException("format de sortida no suportat");
				response.setContentType(outputFormat.getMimeType());
				response.setHeader("Content-Disposition","attachment; filename=\"" + nomArxiuConvertit(fileName, outputFormat) + "\"");
				if (estampa == null || !outputFormat.getFileExtension().equals("pdf")) {
					// No s'afegeix res al pdf
					if (!outputFormat.getFileExtension().equals(inputFormat.getFileExtension())) {
						convert(
								new ByteArrayInputStream(data),
								inputFormat,
								response.getOutputStream(),
								outputFormat);
					} else {
						response.getOutputStream().write(data);
					}
				} else {
					// S'afegeix el codi PDF417
					PdfReader pdfReader = null;
					if (!outputFormat.getFileExtension().equals(inputFormat.getFileExtension())) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						convert(
								new ByteArrayInputStream(data),
								inputFormat,
								baos,
								outputFormat);
						pdfReader = new PdfReader(baos.toByteArray());
					} else {
						pdfReader = new PdfReader(data);
					}
					PdfStamper pdfStamper = new PdfStamper(pdfReader, response.getOutputStream());
					for (int i = 0; i < pdfReader.getNumberOfPages(); i++) {
						PdfContentByte over = pdfStamper.getOverContent(i + 1);
						BarcodePDF417 pdf417 = new BarcodePDF417();
						pdf417.setText(estampa);
						Image img = pdf417.getImage();
						img.getWidth();
						img.setAbsolutePosition(
								(Float)model.get(MODEL_ATTRIBUTE_ESTAMPA_POSX),
								(Float)model.get(MODEL_ATTRIBUTE_ESTAMPA_POSY));
						img.setRotationDegrees((Float)model.get(MODEL_ATTRIBUTE_ESTAMPA_ROTATION));
						over.addImage(img);
						over.beginText();
						over.setFontAndSize(
								BaseFont.createFont(
										BaseFont.HELVETICA,
										BaseFont.WINANSI,
										BaseFont.EMBEDDED),
								8);
						float rotation = (Float)model.get(MODEL_ATTRIBUTE_ESTAMPA_ROTATION);
						float addToX = 0;
						float addToY = 0;
						if (rotation == 0) {
							addToY = -9;
						} else if (rotation == 90) {
							addToX = 9 + img.getHeight();
						} else if (rotation == 180) {
							addToY = -9;
						} else if (rotation == 270) {
							addToX = -9;
							addToY = img.getWidth();
						}
						over.showTextAligned(
								PdfContentByte.ALIGN_LEFT,
								estampa,
								(Float)model.get(MODEL_ATTRIBUTE_ESTAMPA_POSX) + addToX,
								(Float)model.get(MODEL_ATTRIBUTE_ESTAMPA_POSY) + addToY,
								(Float)model.get(MODEL_ATTRIBUTE_ESTAMPA_ROTATION));
						over.endText();
					}
					pdfStamper.close();
				}
			} else {
				super.render(model, request, response);
			}
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
	private DocumentFormat formatPerNomArxiu(String fileName) {
		int indexPunt = fileName.lastIndexOf(".");
		if (indexPunt != -1) {
			String extensio = fileName.substring(indexPunt + 1);
			return getDocumentFormatRegistry().getFormatByFileExtension(extensio);
		}
		return null;
	}
	private String nomArxiuConvertit(String fileName, DocumentFormat format) {
		int indexPunt = fileName.lastIndexOf(".");
		if (indexPunt != -1) {
			String nom = fileName.substring(0, indexPunt);
			return nom + "." + format.getFileExtension();
		} else {
			return fileName + "." + format.getFileExtension();
		}
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
	private boolean getPropertyEnabled() {
		return "true".equals(GlobalProperties.getInstance().getProperty("app.conversio.actiu"));
	}
	private String getPropertyHost() {
		return GlobalProperties.getInstance().getProperty("app.conversio.openoffice.host");
	}
	private int getPropertyPort() {
		return Integer.parseInt(GlobalProperties.getInstance().getProperty("app.conversio.openoffice.port"));
	}
	private String getPropertyDefaultExtension() {
		return GlobalProperties.getInstance().getProperty("app.conversio.default.extension");
	}

}
