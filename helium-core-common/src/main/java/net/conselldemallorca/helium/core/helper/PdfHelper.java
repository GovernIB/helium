package net.conselldemallorca.helium.core.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfBorderArray;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.star.style.HorizontalAlignment;

import es.caib.plugins.arxiu.api.DocumentEstat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;

@Component
public class PdfHelper {

	private Font frutiger6 = FontFactory.getFont("Frutiger", 7);
	private Font frutiger6Link = FontFactory.getFont("Frutiger", 8, new BaseColor(23, 118, 227));
	private Font frutiger7 = FontFactory.getFont("Frutiger", 8, Font.BOLD, new BaseColor(255, 255, 255));
	private Font frutiger11TitolBold = FontFactory.getFont("Frutiger", 12, Font.BOLD);
	private Font frutiger9TitolBold = FontFactory.getFont("Frutiger", 10, Font.BOLD);
	private Font frutiger10Italic = FontFactory.getFont("Frutiger", 11, Font.ITALIC, new BaseColor(160, 160, 160));
	private SimpleDateFormat sdtTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	@Resource private PluginHelper		pluginHelper;
	@Resource private MessageHelper		messageHelper;
	@Resource private ExpedientHelper	expedientHelper;
	@Resource(name = "documentHelperV3")
	private DocumentHelperV3 documentHelper;
	
	public Document inicialitzarDocument(
			ByteArrayOutputStream out,
			String titol,
			String subtitolOpcional,
			boolean apaisat) throws DocumentException {
		
		HeaderPageEvent headerEvent = new HeaderPageEvent();
		com.itextpdf.text.Document pdfGenerat = null;
		
		if (apaisat) {
			pdfGenerat = new com.itextpdf.text.Document(PageSize.A4.rotate(), 36, 36, 35 + headerEvent.getTableHeight(), 36);
		} else {
			pdfGenerat = new com.itextpdf.text.Document(PageSize.A4, 36, 36, 35 + headerEvent.getTableHeight(), 36);
		}
		
		PdfWriter writer = PdfWriter.getInstance(pdfGenerat, out);
		writer.setPageEvent(headerEvent);
		
		pdfGenerat.open();
		pdfGenerat.addAuthor("Helium");
		pdfGenerat.addCreationDate();
		pdfGenerat.addCreator("iText JAVA library");
				
		crearTitol(pdfGenerat, titol, subtitolOpcional);
		
		return pdfGenerat;
	}
	
	private void crearTitol(
			Document index,
			String titol,
			String subtitolOpcional) throws DocumentException {
		
		PdfPTable titolIntroduccioTable = new PdfPTable(1);
		titolIntroduccioTable.setWidthPercentage(100);
		
		PdfPCell titolIntroduccioCell = new PdfPCell();
		titolIntroduccioCell.setBorder(Rectangle.NO_BORDER);

		Paragraph titolParagraph = new Paragraph(titol, frutiger11TitolBold);
		titolParagraph.setAlignment(Element.ALIGN_CENTER);
		titolParagraph.add(Chunk.NEWLINE);
		Paragraph subTitolParagraph = new Paragraph(subtitolOpcional, frutiger9TitolBold);
		subTitolParagraph.setAlignment(Element.ALIGN_CENTER);
		subTitolParagraph.add(Chunk.NEWLINE);
		
		titolIntroduccioCell.addElement(titolParagraph);
		titolIntroduccioCell.addElement(subTitolParagraph);
		
		titolIntroduccioTable.addCell(titolIntroduccioCell);
		titolIntroduccioTable.setSpacingAfter(10f);
		index.add(titolIntroduccioTable);
	}
	
	public void crearTaulaDocuments(
			Document index, 
			Expedient expedient,
			boolean isRelacio) {

		logger.debug("Generant la taula amb els documents de l'expedient [expedientId=" + expedient.getId() + "]");
		
		try {
			float [] pointColumnWidths = new float[] {4f, 14f, 20f, 9f, 10f, 10f, 10f, 8f, 8f, 7f};
			PdfPTable taulaDocuments = new PdfPTable(10);
			
			taulaDocuments.setWidthPercentage(100f);
			taulaDocuments.setWidths(pointColumnWidths);
			
			crearCapsaleraTaula(taulaDocuments, isRelacio);
			
			crearContingutTaula(taulaDocuments, expedient, isRelacio);
			
			index.add(taulaDocuments);
			if (!isRelacio)
				index.add(Chunk.NEXTPAGE);
		} catch (Exception ex) {
			logger.error("Hi ha hagut un error generant la taula dels documents", ex);
		}
	}
	
	private void crearCapsaleraTaula(PdfPTable taulaDocuments, boolean isRelacio) throws NoSuchFileException, IOException {
		
		logger.debug("Generant la capçalera de la taula de documents");
		
//		if (!isRelacio)
//			taulaDocuments.addCell(crearCellaCapsalera("Nº"));

		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.exportacio.index.num")));
		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.exportacio.index.tipus")));
		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.exportacio.index.nom")));
		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.exportacio.index.data")));
		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.metadades.nti.camp.eni.tipus.doc")));
		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.metadades.nti.camp.eni.estat.elab")));
		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.metadades.nti.camp.eni.origen")));
		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.exportacio.index.firmat")));
		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.exportacio.index.estat")));
		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.exportacio.index.link")));
	}
	
	private void crearFilaCosTaula(
			int num,
			PdfPTable taulaDocuments,
			ExpedientDocumentDto documentExpedient,
			boolean isRelacio) throws NoSuchFileException, IOException {
		
		logger.debug("Generant la capçalera de la taula de documents");
		taulaDocuments.addCell(crearCellaCos(Integer.toString(num), Element.ALIGN_CENTER));
		if (documentExpedient.isAdjunt()) {
			taulaDocuments.addCell(crearCellaCos("Adjunt"));
		} else {
			taulaDocuments.addCell(crearCellaCos(documentExpedient.getDocumentNom()));
		}
		taulaDocuments.addCell(crearCellaCos(documentExpedient.getArxiuNom()));
		taulaDocuments.addCell(crearCellaCos(sdtTime.format(documentExpedient.getDataCreacio())));
		taulaDocuments.addCell(crearCellaCos(
				documentExpedient.getNtiTipoDocumental()!=null?documentExpedient.getNtiTipoDocumental().toString():""));
		taulaDocuments.addCell(crearCellaCos(
				documentExpedient.getNtiEstadoElaboracion()!=null?documentExpedient.getNtiEstadoElaboracion().toString():""));		
		taulaDocuments.addCell(crearCellaCos(
				documentExpedient.getNtiOrigen()!=null?documentExpedient.getNtiOrigen().toString():""));
		taulaDocuments.addCell(crearCellaCos(documentExpedient.isSignat()?"Sí":"No"));
		taulaDocuments.addCell(crearCellaCos(documentExpedient.getArxiuUuid()));
		if (documentExpedient.getSignaturaUrlVerificacio()!=null && !"".equals(documentExpedient.getSignaturaUrlVerificacio())) {
			taulaDocuments.addCell(crearCellaLink("Obre...", documentExpedient.getSignaturaUrlVerificacio()));
		} else {
			taulaDocuments.addCell(crearCellaCos(""));
		}
	}
	
	private void crearContingutTaula(
			PdfPTable taulaDocuments,
			Expedient expedient,
			boolean isRelacio) throws Exception {
		logger.debug("Generant la capçalera de la taula de documents");
		
		List<InstanciaProcesDto> arbreProcessos = expedientHelper.getArbreInstanciesProces(expedient.getProcessInstanceId());
		List<ExpedientDocumentDto> documentsIndex = new ArrayList<ExpedientDocumentDto>();
		
		for (InstanciaProcesDto instanciaProces : arbreProcessos) {
			List<ExpedientDocumentDto> documentsExpedientDto = documentHelper.findDocumentsPerInstanciaProces(instanciaProces.getId());
			if (documentsExpedientDto!=null) {
				for (ExpedientDocumentDto ed: documentsExpedientDto) {
					if (ed.getArxiuUuid()!=null && !"".equals(ed.getArxiuUuid())) {
						es.caib.plugins.arxiu.api.Document arxiuDocument = pluginHelper.arxiuDocumentInfo(
								ed.getArxiuUuid(),
								null,
								false,
								false);
						if (arxiuDocument!=null && DocumentEstat.DEFINITIU.equals(arxiuDocument.getEstat())) {
							ed.setArxiuUuid("Definitiu");
						} else {
							ed.setArxiuUuid("Esborrany");
						}
					} else if (ed.getReferenciaCustodia()!=null) {
						ed.setArxiuUuid("Custodiat");
					} else {
						ed.setArxiuUuid("Esborrany");
					}
					documentsIndex.add(ed);
				}
			}
		}
		
		int n=1;
		for (ExpedientDocumentDto documentExpedientDto: documentsIndex) {
			crearFilaCosTaula(n++, taulaDocuments, documentExpedientDto, isRelacio);
		}
	}
	
	public void crearTitolRelacio(Document index, String titolRelacio) throws DocumentException {
		PdfPTable titolRelacioTable = new PdfPTable(1);
		titolRelacioTable.setWidthPercentage(100);
		PdfPCell relacioTitolCell = new PdfPCell();
		relacioTitolCell.setBorder(Rectangle.BOTTOM);
		relacioTitolCell.setBorderColor(new BaseColor(160, 160, 160));
		Paragraph relacioTitol = new Paragraph(titolRelacio, frutiger10Italic);
		relacioTitol.add(Chunk.NEWLINE);
		relacioTitolCell.addElement(relacioTitol);
		titolRelacioTable.addCell(relacioTitolCell);
		index.add(titolRelacioTable);
	}	
	
	private PdfPCell crearCellaCapsalera(String titol) {
		PdfPCell titolCell = new PdfPCell();
		Paragraph titolParagraph = new Paragraph(titol, frutiger7);
		titolParagraph.setAlignment(Element.ALIGN_CENTER);
		titolCell.addElement(titolParagraph);
		titolCell.setPaddingBottom(6f);
		titolCell.setBackgroundColor(new BaseColor(166, 166, 166));
		titolCell.setBorderWidth((float) 0.5);
		return titolCell;
	}
	
	private PdfPCell crearCellaCos(String valor) {
		return crearCellaCos(valor, Element.ALIGN_LEFT);
	}
	
	private PdfPCell crearCellaCos(String valor, int align) {
		PdfPCell cosCell = new PdfPCell();
		Paragraph titolParagraph = new Paragraph(valor, frutiger6);
		titolParagraph.setAlignment(align);
		cosCell.addElement(titolParagraph);
		cosCell.setBorderWidth((float) 0.5);
		return cosCell;
	}
	
	private PdfPCell crearCellaLink(String valor, String URL) {
		PdfPCell cosCell = new PdfPCell();
		Paragraph titolParagraph = new Paragraph(valor, frutiger6Link);
		titolParagraph.setAlignment(Element.ALIGN_CENTER);
		cosCell.addElement(titolParagraph);
		cosCell.setBorderWidth((float) 0.5);
		cosCell.setCellEvent(new LinkInCell(URL));
		return cosCell;
	}
	
	private class LinkInCell implements PdfPCellEvent {
	    protected String url;
	    public LinkInCell(String url) {
	        this.url = url;
	    }
	    public void cellLayout(
	    		PdfPCell cell, 
	    		Rectangle position,
	    		PdfContentByte[] canvases) {
	    	PdfWriter writer = canvases[0].getPdfWriter();
	    	PdfAction action = new PdfAction(url);
	        PdfAnnotation link = PdfAnnotation.createLink(writer, position, PdfAnnotation.HIGHLIGHT_INVERT, action);
	        link.setBorder(new PdfBorderArray(0f, 0f, 0f));
	        writer.addAnnotation(link);
	    }
	}
	
	private class HeaderPageEvent extends PdfPageEventHelper {
		
		private PdfPTable header;
		private float tableHeight;	    
		
	    public float getTableHeight() {
			return tableHeight;
		}

		public void onEndPage(PdfWriter writer, Document index) {
			header.writeSelectedRows(
					0, 
					-1,
					index.left(),
					505 + ((index.topMargin() + tableHeight) / 2),
                    writer.getDirectContent());
	    }
		
		private HeaderPageEvent() {
			try {
				PdfPCell cellDireccio = new PdfPCell();
				header = new PdfPTable(2);
				header.setTotalWidth(523);
				header.setLockedWidth(true);
				Image logoCapsalera = null;
				
				String logoUrl = (String)GlobalProperties.getInstance().get("app.helium.logo");
				if (logoUrl!=null && !logoUrl.isEmpty()) {
					logoCapsalera = Image.getInstance(logoUrl);
				} else {
					byte[] logoBytes = IOUtils.toByteArray(getClass().getResourceAsStream("/net/conselldemallorca/helium/images/govern-logo.png"));
					logoCapsalera = Image.getInstance(logoBytes);
				}
				
				if (logoCapsalera != null) {
					logoCapsalera.scaleToFit(120f, 50f);
					PdfPCell cellLogo = new PdfPCell(logoCapsalera);
					cellLogo.setHorizontalAlignment(Element.ALIGN_LEFT);
					cellLogo.setBorder(Rectangle.NO_BORDER);
					header.addCell(cellLogo);
				}
	
				cellDireccio.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellDireccio.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cellDireccio.setBorder(Rectangle.NO_BORDER);
				header.addCell(cellDireccio);
				tableHeight = header.getTotalHeight();
			} catch (Exception ex) {
				logger.error("Hi ha hagut un error generant el header del document", ex);
			}
		}
	}
	
	private static final Log logger = LogFactory.getLog(PdfHelper.class);
}