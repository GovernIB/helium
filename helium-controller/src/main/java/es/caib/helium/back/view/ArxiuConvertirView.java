/**
 * 
 */
package es.caib.helium.back.view;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BarcodePDF417;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import es.caib.helium.logic.intf.service.AplicacioService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Vista per mostrar arxius
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuConvertirView extends ArxiuView {

	public static final String MODEL_ATTRIBUTE_CONVERSIONENABLED = "conversionEnabled";
	public static final String MODEL_ATTRIBUTE_OUTEXTENSION = "outputExtension";
	public static final String MODEL_ATTRIBUTE_ESTAMPA_MISSATGE = "estampa";
	public static final String MODEL_ATTRIBUTE_ESTAMPA_POSX = "estampaX";
	public static final String MODEL_ATTRIBUTE_ESTAMPA_POSY = "estampaY";
	public static final String MODEL_ATTRIBUTE_ESTAMPA_ROTATION = "estampaRotation";
	public static final String MODEL_ATTRIBUTE_REGISTRE_ENTITAT = "regEntitat";
	public static final String MODEL_ATTRIBUTE_REGISTRE_OFICINA = "regOficina";
	public static final String MODEL_ATTRIBUTE_REGISTRE_DATA = "regData";
	public static final String MODEL_ATTRIBUTE_REGISTRE_ENTRADA = "regEntrada";
	public static final String MODEL_ATTRIBUTE_REGISTRE_SORTIDA = "regSortida";

	@Autowired
	AplicacioService aplicacioService;


	@SuppressWarnings("rawtypes")
	public void render(
			Map model,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
		response.setHeader(HEADER_PRAGMA, "");
		response.setHeader(HEADER_EXPIRES, "");
		response.setHeader(HEADER_CACHE_CONTROL, "");
		byte[] data = (byte[])model.get(MODEL_ATTRIBUTE_DATA);
		String fileName = (String)model.get(MODEL_ATTRIBUTE_FILENAME);
		Boolean conversionEnabled = (Boolean)model.get(MODEL_ATTRIBUTE_CONVERSIONENABLED);
		boolean conversion = (conversionEnabled == null) ? getPropertyEnabled() : conversionEnabled.booleanValue();
		if (!getPropertyEnabled()) conversion = false;
		String outputExtension = (String)model.get(MODEL_ATTRIBUTE_OUTEXTENSION);
		String estampa = (String)model.get(MODEL_ATTRIBUTE_ESTAMPA_MISSATGE);
		String registreEntitat = (String)model.get(MODEL_ATTRIBUTE_REGISTRE_ENTITAT);
		String registreOficina = (String)model.get(MODEL_ATTRIBUTE_REGISTRE_OFICINA);
		String registreData = (String)model.get(MODEL_ATTRIBUTE_REGISTRE_DATA);
		String registreEntrada = (String)model.get(MODEL_ATTRIBUTE_REGISTRE_ENTRADA);
		String registreSortida = (String)model.get(MODEL_ATTRIBUTE_REGISTRE_SORTIDA);
		if (data != null) {
			if (conversion) {
				var documentConvertit = aplicacioService.convertFile(
						fileName,
						data,
						((outputExtension != null) ? outputExtension : getPropertyDefaultExtension()).toLowerCase());
				response.setContentType(documentConvertit.getMediaType());
				response.setHeader("Content-Disposition","attachment; filename=\"" + documentConvertit.getNom() + "\"");
				boolean estampar = documentConvertit.getExtensio().equalsIgnoreCase("pdf") && (estampa != null || registreEntitat != null);
				if (estampar) {
					PdfReader pdfReader = new PdfReader(documentConvertit.getContingut());
					PdfStamper pdfStamper = new PdfStamper(pdfReader, response.getOutputStream());
					for (int i = 0; i < pdfReader.getNumberOfPages(); i++) {
						PdfContentByte over = pdfStamper.getOverContent(i + 1);
						Rectangle pagina = over.getPdfDocument().getPageSize();
						float width = pagina.getWidth();
						float height = pagina.getHeight();
						if (estampa != null) {
							// S'afegeix la URL de validació de signatura amb codi de barres PDF417
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
						if (registreEntitat != null) {
							// S'afegeix el segell de registre
							float borderWidth = (float)0.8;
							float paddingBottom = 6;
							float totalWidth = 120;
							float marge = 20;
							Font registreFont = new Font(Font.HELVETICA, 6);
							PdfPTable table = new PdfPTable(2);
							PdfPCell cell = new PdfPCell();
							Paragraph parEntitat = new Paragraph(registreEntitat, registreFont);
							cell.addElement(parEntitat);
							Paragraph parOficina = new Paragraph(registreOficina, registreFont);
							cell.addElement(parOficina);
							cell.setColspan(2);
							cell.setPaddingBottom(paddingBottom);
							cell.setBorderWidth(borderWidth);
							table.addCell(cell);
							cell = new PdfPCell();
							Paragraph parData = new Paragraph("Data: " + ((registreData != null) ? registreData : ""), registreFont);
							cell.addElement(parData);
							cell.setColspan(2);
							cell.setPaddingBottom(paddingBottom);
							cell.setBorderWidth(borderWidth);
							table.addCell(cell);
							cell = new PdfPCell();
							Paragraph parEntrada = new Paragraph("ENTRADA", registreFont);
							cell.addElement(parEntrada);
							if (registreEntrada != null) {
								Paragraph parNumreg = new Paragraph(registreEntrada, registreFont);
								parNumreg.setAlignment(Element.ALIGN_CENTER);
								cell.addElement(parNumreg);
							}
							cell.setPaddingBottom(paddingBottom);
							cell.setBorderWidth(borderWidth);
							table.addCell(cell);
							cell = new PdfPCell();
							Paragraph parSortida = new Paragraph("SORTIDA", registreFont);
							cell.addElement(parSortida);
							if (registreSortida != null) {
								Paragraph parNumreg = new Paragraph(registreSortida, registreFont);
								parNumreg.setAlignment(Element.ALIGN_CENTER);
								cell.addElement(parNumreg);
							}
							cell.setPaddingBottom(paddingBottom);
							cell.setBorderWidth(borderWidth);
							table.addCell(cell);
							table.setTotalWidth(totalWidth);
					        table.writeSelectedRows(0, -1, width - totalWidth - marge, height - marge, over);
						}
					}
					pdfStamper.close();
				} else {
					response.getOutputStream().write(data);
				}
			} else {
				super.render(model, request, response);
			}
		}
		} catch (Exception ex) {
			logger.error("Error en la conversió del document", ex);
			throw new ServletException(ex);
		}
	}

	private boolean getPropertyEnabled() {
		return "true".equals(aplicacioService.getGlobalProperties().getProperty("app.conversio.actiu"));
	}
	private String getPropertyDefaultExtension() {
		return aplicacioService.getGlobalProperties().getProperty("app.conversio.default.extension");
	}

	private static final Log logger = LogFactory.getLog(ArxiuConvertirView.class);

}
