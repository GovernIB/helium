package net.conselldemallorca.helium.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BarcodePDF417;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

/**
 * Classe per converir documents a PDF.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PdfUtils {

	public static final int BARCODE_POSITION_TOP = 0;
	public static final int BARCODE_POSITION_BOTTOM = 1;
	public static final int BARCODE_POSITION_LEFT = 2;
	public static final int BARCODE_POSITION_RIGHT = 3;

	public static final int REGISTRE_POSITION_TOP_RIGHT = 0;
	public static final int REGISTRE_POSITION_BOTTOM_RIGHT = 1;
	public static final int REGISTRE_POSITION_BOTTOM_LEFT = 2;
	public static final int REGISTRE_POSITION_TOP_LEFT = 3;

	private static final float MARGE = 10;

	private OpenOfficeUtils openOfficeUtils;



	public void estampar(
			String arxiuNom,
			byte[] arxiuContingut,
			boolean segellSignatura,
			String urlComprovacioSignatura,
			boolean segellRegistre,
			String registreNumero,
			String registreData,
			String registreOficina,
			boolean registreEsEntrada,
			OutputStream output,
			String outputExtension) throws Exception {
		String extensioOrigen = getArxiuExtensio(arxiuNom);
		PdfReader pdfReader = null;
		if (!outputExtension.equalsIgnoreCase(extensioOrigen)) {
			if (isArxiuConvertiblePdf(arxiuNom)) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				getOpenOfficeUtils().convertir(
						arxiuNom,
						arxiuContingut,
						outputExtension,
						baos);
				if (outputExtension.equalsIgnoreCase("pdf"))
					pdfReader = new PdfReader(baos.toByteArray());
			} else {
				throw new Exception("L'arxiu '" + arxiuNom + "' no es pot convertir a format PDF");
			}
		} else {
			if (outputExtension.equalsIgnoreCase("pdf"))
				pdfReader = new PdfReader(arxiuContingut);
		}
		if (outputExtension.equalsIgnoreCase("pdf") && (segellSignatura || segellRegistre)) {
			PdfStamper pdfStamper = new PdfStamper(pdfReader, output);
			for (int i = 0; i < pdfReader.getNumberOfPages(); i++) {
				PdfContentByte over = pdfStamper.getOverContent(i + 1);
				if (segellSignatura) {
					estamparBarcodePdf417(
							over,
							urlComprovacioSignatura,
							BARCODE_POSITION_LEFT);
				}
				if (segellRegistre) {
					estamparSegellRegistre(
							over,
							registreNumero,
							registreData,
							registreOficina,
							registreEsEntrada,
							REGISTRE_POSITION_TOP_RIGHT);
				}
			}
			pdfStamper.close();
		}
	}

	private void estamparBarcodePdf417(
			PdfContentByte contentByte,
			String url,
			int posicio) throws Exception {
		float paddingUrl = 5;
		// Calcula les dimensions de la pàgina i la taula
		Rectangle page = contentByte.getPdfDocument().getPageSize();
		float pageWidth = page.getWidth();
		float pageHeight = page.getHeight();
		if (posicio == BARCODE_POSITION_TOP || posicio == BARCODE_POSITION_BOTTOM) {
			float ampladaTaulaMax = pageWidth - (2 * MARGE);
			// Crea la cel·la del codi de barres
			BarcodePDF417 pdf417 = new BarcodePDF417();
			pdf417.setText(url);
			Image img = pdf417.getImage();
			PdfPCell pdf417Cell = new PdfPCell(img);
			pdf417Cell.setBorder(0);
			pdf417Cell.setFixedHeight(img.getHeight());
			float imgCellWidth = img.getWidth();
			// Crea la cel·la amb la url
			Font urlFont = new Font(Font.HELVETICA, 6);
			Chunk urlChunk = new Chunk(url, urlFont);
			Phrase urlPhrase = new Phrase(urlChunk);
			PdfPCell urlCell = new PdfPCell(urlPhrase);
			urlCell.setPadding(0);
			urlCell.setBorder(0);
			urlCell.setFixedHeight(img.getHeight());
			urlCell.setUseAscender(true);
			urlCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			urlCell.setPaddingLeft(paddingUrl);
			float urlWidth = urlChunk.getWidthPoint() + 5;
			float urlCellWidth = (imgCellWidth + urlWidth > ampladaTaulaMax) ? ampladaTaulaMax - imgCellWidth : urlWidth;
			// Estampa el codi de barres en la posició elegida
			PdfPTable table = new PdfPTable(2);
			table.addCell(pdf417Cell);
			table.addCell(urlCell);
			float ampladaTaula = imgCellWidth + urlCellWidth;
			table.setWidths(new float[]{img.getWidth(), ampladaTaula - img.getWidth()});
			table.setTotalWidth(ampladaTaula);
			if (posicio == BARCODE_POSITION_TOP) {
				table.writeSelectedRows(0, -1, (pageWidth / 2) - (ampladaTaula / 2), pageHeight - MARGE, contentByte);
			} else {
				table.writeSelectedRows(0, -1, (pageWidth / 2) - (ampladaTaula / 2), MARGE + img.getHeight(), contentByte);
			}
		} else {
			float ampladaTaulaMax = pageHeight - (2 * MARGE);
			// Crea la cel·la del codi de barres
			BarcodePDF417 pdf417 = new BarcodePDF417();
			pdf417.setText(url);
			Image img = pdf417.getImage();
			PdfPCell pdf417Cell = new PdfPCell(img);
			pdf417Cell.setBorder(1);
			pdf417Cell.setFixedHeight(img.getWidth());
			pdf417Cell.setRotation(90);
			float imgCellWidth = img.getWidth();
			// Crea la cel·la amb la url
			Font urlFont = new Font(Font.HELVETICA, 6);
			Chunk urlChunk = new Chunk(url, urlFont);
			Phrase urlPhrase = new Phrase(urlChunk);
			PdfPCell urlCell = new PdfPCell(urlPhrase);
			urlCell.setPadding(0);
			urlCell.setBorder(0);
			urlCell.setUseAscender(true);
			urlCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			urlCell.setPaddingBottom(paddingUrl);
			urlCell.setRotation(90);
			float urlWidth = urlChunk.getWidthPoint() + 5;
			float urlCellWidth = (imgCellWidth + urlWidth > ampladaTaulaMax) ? ampladaTaulaMax - imgCellWidth : urlWidth;
			urlCell.setFixedHeight(urlCellWidth);
			// Estampa el codi de barres en la posició elegida
			PdfPTable table = new PdfPTable(1);
			table.addCell(urlCell);
			table.addCell(pdf417Cell);
			table.setWidths(new float[]{img.getHeight()});
			table.setTotalWidth(img.getHeight());
			float ampladaTaula = imgCellWidth + urlCellWidth;
			if (posicio == BARCODE_POSITION_LEFT) {
				table.writeSelectedRows(0, -1, MARGE, pageHeight - (pageHeight / 2) + (ampladaTaula / 2), contentByte);
			} else {
				table.writeSelectedRows(0, -1, pageWidth - img.getHeight() - MARGE , pageHeight - (pageHeight / 2) + (ampladaTaula / 2), contentByte);
			}
		}
	}
	private void estamparSegellRegistre(
			PdfContentByte contentByte,
			String registreNumero,
			String registreData,
			String registreOficina,
			boolean registreEntrada,
			int posicio) {
		float borderWidth = (float)0.8;
		float paddingBottom = 6;
		float totalWidth = 120;
		float marge = 10;
		Font registreFont = new Font(Font.HELVETICA, 6);
		PdfPTable table = new PdfPTable(2);
		PdfPCell cell = new PdfPCell();
		Paragraph parEntitat = new Paragraph(
				getRegistreEntitat(),
				registreFont);
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
		if (registreEntrada) {
			Paragraph parNumreg = new Paragraph(registreNumero, registreFont);
			parNumreg.setAlignment(Element.ALIGN_CENTER);
			cell.addElement(parNumreg);
		}
		cell.setPaddingBottom(paddingBottom);
		cell.setBorderWidth(borderWidth);
		table.addCell(cell);
		cell = new PdfPCell();
		Paragraph parSortida = new Paragraph("SORTIDA", registreFont);
		cell.addElement(parSortida);
		if (!registreEntrada) {
			Paragraph parNumreg = new Paragraph(registreNumero, registreFont);
			parNumreg.setAlignment(Element.ALIGN_CENTER);
			cell.addElement(parNumreg);
		}
		cell.setPaddingBottom(paddingBottom);
		cell.setBorderWidth(borderWidth);
		table.addCell(cell);
		table.setTotalWidth(totalWidth);
		Rectangle pagina = contentByte.getPdfDocument().getPageSize();
		float width = pagina.getWidth();
		float height = pagina.getHeight();
		if (posicio == REGISTRE_POSITION_TOP_RIGHT) {
			table.writeSelectedRows(0, -1, width - totalWidth - marge, height - marge, contentByte);
		} else if (posicio == REGISTRE_POSITION_BOTTOM_RIGHT) {
			table.writeSelectedRows(0, -1, width - totalWidth - marge, table.getTotalHeight() + marge, contentByte);
		} else if (posicio == REGISTRE_POSITION_BOTTOM_LEFT) {
			table.writeSelectedRows(0, -1, marge, table.getTotalHeight() + marge, contentByte);
		} else if (posicio == REGISTRE_POSITION_TOP_LEFT) {
			table.writeSelectedRows(0, -1, marge, height - marge, contentByte);
		}
	}

	public static void main(String[] args) {
		try {
			PdfUtils utils = new PdfUtils();
			byte[] fileContent = null;
			File inputFile = new File("c:\\tmp\\in.pdf");
			FileInputStream fin = new FileInputStream(inputFile);
			fileContent = new byte[(int)inputFile.length()];
			fin.read(fileContent);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			utils.estampar(
					"in.pdf",
					fileContent,
					true,
					"http://www.google.es/search?hl=ca&client=firefox-a&rls=org.mozilla%3Aca%3Aofficial&q=itext+pdfpcell+width&aq=f&aqi=&aql=&oq=",
					//"Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
					true,
					"69/2011",
					"23 Gen 2011",
					"Departament de programacio",
					true,
					baos,
					"pdf");
			FileOutputStream fos = new FileOutputStream("c:\\tmp\\out.pdf");
			fos.write(baos.toByteArray());
			fos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private OpenOfficeUtils getOpenOfficeUtils() {
		if (openOfficeUtils == null)
			openOfficeUtils = new OpenOfficeUtils();
		return openOfficeUtils;
	}
	private String getArxiuExtensio(String arxiuNom) {
		if (arxiuNom == null)
			return null;
		int indexPunt = arxiuNom.lastIndexOf(".");
		if (indexPunt != -1) {
			return arxiuNom.substring(indexPunt + 1);
		} else {
			return null;
		}
	}
	private String getRegistreEntitat() {
		return GlobalProperties.getInstance().getProperty("app.registre.segell.entitat");
	}

	private String[] extensionsConvertiblesPdf = {
			"pdf", "odt", "sxw", "rtf", "doc", "wpd", "txt", "ods",
			"sxc", "xls", "csv", "tsv", "odp", "sxi", "ppt"};
	private boolean isArxiuConvertiblePdf(String arxiuNom) {
		String extensio = getArxiuExtensio(arxiuNom);
		if (extensio != null) {
			for (int i = 0; i < extensionsConvertiblesPdf.length; i++) {
				if (extensio.equalsIgnoreCase(extensionsConvertiblesPdf[i]))
					return true;
			}
		}
		return false;
	}

}
