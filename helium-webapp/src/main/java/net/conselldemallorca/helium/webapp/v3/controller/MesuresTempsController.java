package net.conselldemallorca.helium.webapp.v3.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.IntervalEventDto;
import net.conselldemallorca.helium.v3.core.api.dto.MesuraTemporalDto;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

/**
 * Controlador per a la pàgina inicial (index).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/mesuresTemps")
public class MesuresTempsController extends BaseController {

	@Resource
	private AdminService adminService;

	// Variables exportació
	private XSSFWorkbook wb;
	private XSSFCellStyle headerStyle;
	private XSSFCellStyle cellStyle;
	private XSSFCellStyle style;
	private XSSFCellStyle dStyle;
	private XSSFFont bold;
	private XSSFCellStyle cellGreyStyle;
	private XSSFCellStyle greyStyle;
	private XSSFCellStyle dGreyStyle;
	private XSSFFont greyFont;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		return "v3/mesuresTemps";
	}
		
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public String mesuresTemps(HttpServletRequest request, String familia) {
		DecimalFormat df = new DecimalFormat( "####0.00" );
		DecimalFormat df2 = new DecimalFormat( "####0.000" );
		Map mjson = new LinkedHashMap();
		
		List<MesuraTemporalDto> mesures = adminService.mesuraTemporalFindByFamilia(familia, false);
		Set<String> llistatFamilies = new HashSet<String>();
		llistatFamilies.addAll(adminService.mesuraTemporalFindFamiliesAll());
		
		if (adminService.isStatisticActive()) {			
			List<MesuraTemporalDto> listStatistics = adminService.getHibernateStatistics(familia, false);
			if (!listStatistics.isEmpty()) {
				mesures.addAll(listStatistics);
			}
			llistatFamilies.add("sql_helium");
			llistatFamilies.add("sql_jbpm");
		}
		
		Map mfamilies = new LinkedHashMap();
		for (String fam: llistatFamilies) {
			mfamilies.put(fam, getMessage("temps.familia." + fam));
		}
		
		if (mesures.isEmpty()) {
			mjson.put("familia", mfamilies);
		} else {
			JSONArray lclaus = new JSONArray();
			JSONArray ltipus = new JSONArray();
			JSONArray ltasca = new JSONArray();
			JSONArray lnom = new JSONArray();
			JSONArray ldarrers = new JSONArray();
			JSONArray lmitjes = new JSONArray();
			JSONArray lminimes = new JSONArray();
			JSONArray lmaximes = new JSONArray();
			JSONArray lnumMesures = new JSONArray();
			JSONArray lperiodes = new JSONArray();
			Map mseries = new LinkedHashMap();
			
			for (MesuraTemporalDto mesura: mesures) {
				lclaus.add(mesura.getClau());
				ltipus.add(mesura.getTipusExpedient());
				ltasca.add(mesura.getTasca());
				lnom.add(mesura.getNom());
				ldarrers.add(mesura.getDarrera() < 1 ? "-" : mesura.getDarrera());
				lmitjes.add(df.format(mesura.getMitja()) + " ms");
				lminimes.add(mesura.getMinima() + " ms");
				lmaximes.add(mesura.getMaxima() + " ms");
				lnumMesures.add(mesura.getNumMesures());
				lperiodes.add(df2.format(mesura.getPeriode()) + " ex/m");
				
				JSONArray lseries = new JSONArray();
				for (IntervalEventDto event: mesura.getEvents()) {
					List  levents = new LinkedList();
					levents.add(event.getDate().getTime());
					levents.add(event.getDuracio());
					lseries.add(levents);
				}
				Map mserie = new LinkedHashMap();
				mserie.put("label", mesura.getClau());
				mserie.put("data", lseries);
				mseries.put(mesura.getClau(), mserie);
			}
			
			mjson.put("clau", lclaus);
			mjson.put("tipus", ltipus);
			mjson.put("tasca", ltasca);
			mjson.put("nom", lnom);
			mjson.put("darrera", ldarrers);
			mjson.put("mitja", lmitjes);
			mjson.put("minima", lminimes);
			mjson.put("maxima", lmaximes);
			mjson.put("numMesures", lnumMesures);
			mjson.put("periode", lperiodes);
			mjson.put("familia", mfamilies);
			mjson.put("series", mseries);
		}
		
		String json = JSONValue.toJSONString(mjson);
		return json;
	}

	@RequestMapping(value = "export", method = RequestMethod.GET)
	public void mesuresTempsExport(HttpServletRequest request, HttpServletResponse response) {
		
		logger.debug("[TEMPS] >>>>> Inici generació de document Excel amb els temps d'execució.");
		
		logger.debug("[TEMPS] >>>>>>>> Obtenció de mesures de temps Helium... ");
		List<MesuraTemporalDto> mesures = adminService.mesuraTemporalFindByFamilia(null, true);
		int numMesures = mesures.size();
		logger.debug("[TEMPS] >>>>>>>> Obtenció de mesures de temps Helium... " + numMesures + " mesures.");
		
		logger.debug("[TEMPS] >>>>>>>> Obtenció de mesures de temps Hibernate...");
		mesures.addAll(adminService.getHibernateStatistics("", true));
		logger.debug("[TEMPS] >>>>>>>> Obtenció de mesures de temps Hibernate..." + (mesures.size() - numMesures) + " mesures.");

		wb = new XSSFWorkbook();

		bold = wb.createFont();
		bold.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		bold.setColor(IndexedColors.WHITE.getIndex());
		greyFont = wb.createFont();
		greyFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());

		cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
		cellStyle.setWrapText(true);
		cellGreyStyle = wb.createCellStyle();
		cellGreyStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
		cellGreyStyle.setWrapText(true);
		cellGreyStyle.setFont(greyFont);

		headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(XSSFCellStyle.FINE_DOTS);
		headerStyle.setFillBackgroundColor(IndexedColors.BLUE_GREY.getIndex());
		headerStyle.setFont(bold);

		style = wb.createCellStyle();
		greyStyle = wb.createCellStyle();
		greyStyle.setFont(greyFont);

		DataFormat format = wb.createDataFormat();
		dStyle = wb.createCellStyle();
		dStyle.setDataFormat(format.getFormat("0.00"));

		dGreyStyle = wb.createCellStyle();
		dGreyStyle.setFont(greyFont);
		dGreyStyle.setDataFormat(format.getFormat("0.00"));

		// GENERAL
		XSSFSheet sheet = wb.createSheet("Mesures de temps");
		sheet.setColumnWidth(0, 15000);
		sheet.setColumnWidth(1, 3000);
		sheet.setColumnWidth(2, 3000);
		sheet.setColumnWidth(3, 3000);
		sheet.setColumnWidth(4, 3000);
		sheet.setColumnWidth(5, 3000);
		sheet.setColumnWidth(6, 3000);
		sheet.setColumnWidth(7, 3000);

		createHeader(sheet);

		int rowNum = 1;

		for (MesuraTemporalDto mesura: mesures) {
			try {
				XSSFRow xlsRow = sheet.createRow(rowNum++);
				int colNum = 0;

				XSSFCell cell = xlsRow.createCell(colNum++);
				cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(mesura.getNom())));
				cell.setCellStyle(style);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getDarrera());
				cell.setCellStyle(style);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getMinima());
				cell.setCellStyle(style);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getMaxima());
				cell.setCellStyle(style);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getNumMesures());
				cell.setCellStyle(style);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getMitja());
				cell.setCellStyle(dStyle);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getPeriode());
				cell.setCellStyle(dStyle);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getMitja() * mesura.getNumMesures());
				cell.setCellStyle(dStyle);

				// Series
				if ("Consultas Helium".equals(mesura.getClau()) || "Consultas Jbpm".equals(mesura.getClau())) {

					int rowNumSeries = 0;
					XSSFSheet sheetSeries;
					try {
						String llibre = mesura.getClau().replaceAll("[]*/\\?:()]+", "-");
						if (llibre.length() > 31) {
							llibre = llibre.substring(0, 14) + "..." + llibre.substring(llibre.length() - 14, llibre.length());
						}
						sheetSeries = wb.createSheet(llibre);
					} catch (Exception e) {
						sheetSeries = wb.createSheet("Mesures " + rowNum);
					}

					sheetSeries.setColumnWidth(0, 30000);
					sheetSeries.setColumnWidth(1, 3000);
					sheetSeries.setColumnWidth(2, 3000);
					sheetSeries.setColumnWidth(3, 3000);
					sheetSeries.setColumnWidth(4, 3000);

					xlsRow = sheetSeries.createRow(rowNumSeries++);

					cell = xlsRow.createCell(0);
					cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(getMessage("temps.clau"))));
					cell.setCellStyle(headerStyle);

					cell = xlsRow.createCell(1);
					cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(getMessage("temps.minima"))));
					cell.setCellStyle(headerStyle);

					cell = xlsRow.createCell(2);
					cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(getMessage("temps.maxima"))));
					cell.setCellStyle(headerStyle);

					cell = xlsRow.createCell(3);
					cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(getMessage("temps.numMesures"))));
					cell.setCellStyle(headerStyle);

					cell = xlsRow.createCell(4);
					cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(getMessage("temps.mitja"))));
					cell.setCellStyle(headerStyle);

					List<MesuraTemporalDto> listStatistics = new ArrayList<MesuraTemporalDto>();
					if ("Consultas Helium".equals(mesura.getClau())) {
						listStatistics = adminService.getHibernateStatistics("sql_helium", true);
					} else if ("Consultas Jbpm".equals(mesura.getClau())) {
						listStatistics = adminService.getHibernateStatistics("sql_jbpm", true);
					}
					for (MesuraTemporalDto mesuraStats : listStatistics) {
						xlsRow = sheetSeries.createRow(rowNumSeries++);

						cell = xlsRow.createCell(0);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(mesuraStats.getClau());

						cell = xlsRow.createCell(1);
						cell.setCellValue(mesuraStats.getMinima());

						cell = xlsRow.createCell(2);
						cell.setCellValue(mesuraStats.getMaxima());

						cell = xlsRow.createCell(3);
						cell.setCellValue(mesuraStats.getNumMesures());

						cell = xlsRow.createCell(4);
						cell.setCellValue(mesuraStats.getMitja());
					}
				}
			} catch (Exception e) {
				logger.error("Mesura de temps: No s'ha pogut crear la línia: " + mesura.getNom(), e);
			}
		}
		
		logger.debug("OK");

		// PER TIPUS EXPEDIENT
		List<MesuraTemporalDto> mesuresTipusExpedient = adminService.mesuraTemporalFindByTipusExpedient();
		sheet = wb.createSheet("Mesures Tipus Expedient");
		sheet.setColumnWidth(0, 15000);
		sheet.setColumnWidth(1, 3000);
		sheet.setColumnWidth(2, 3000);
		sheet.setColumnWidth(3, 3000);
		sheet.setColumnWidth(4, 3000);
		sheet.setColumnWidth(5, 3000);
		sheet.setColumnWidth(6, 3000);
		sheet.setColumnWidth(7, 3000);

		createHeader(sheet);

		rowNum = 1;

		for (MesuraTemporalDto mesura: mesuresTipusExpedient) {
			try {
				XSSFRow xlsRow = sheet.createRow(rowNum++);
				int colNum = 0;

				String nom = mesura.getNomTE();
				XSSFCellStyle st = style;
				XSSFCellStyle dSt = dStyle;
				if (mesura.getDetall() != null) {
					nom = " |---" + nom;
					st = greyStyle;
					dSt = dGreyStyle;
				}

				XSSFCell cell = xlsRow.createCell(colNum++);
				cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(nom)));
				cell.setCellStyle(st);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getDarrera());
				cell.setCellStyle(st);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getMinima());
				cell.setCellStyle(st);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getMaxima());
				cell.setCellStyle(st);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getNumMesures());
				cell.setCellStyle(st);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getMitja());
				cell.setCellStyle(dSt);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getPeriode());
				cell.setCellStyle(dSt);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getMitja() * mesura.getNumMesures());
				cell.setCellStyle(dSt);
			} catch (Exception e) {
				logger.error("Mesura de temps: No s'ha pogut crear la línia de tipus expedient: " + mesura.getNom(), e);
			}
		}

		logger.debug("OK");
		
		// PER TASCA
		List<MesuraTemporalDto> mesuresTasca = adminService.mesuraTemporalFindByTasca();
		sheet = wb.createSheet("Mesures Tasca");
		sheet.setColumnWidth(0, 20000);
		sheet.setColumnWidth(1, 3000);
		sheet.setColumnWidth(2, 3000);
		sheet.setColumnWidth(3, 3000);
		sheet.setColumnWidth(4, 3000);
		sheet.setColumnWidth(5, 3000);
		sheet.setColumnWidth(6, 3000);
		sheet.setColumnWidth(7, 3000);

		createHeader(sheet);

		rowNum = 1;

		for (MesuraTemporalDto mesura: mesuresTasca) {
			try {
				XSSFRow xlsRow = sheet.createRow(rowNum++);
				int colNum = 0;

				String nom = mesura.getNom();
				XSSFCellStyle st = style;
				XSSFCellStyle dSt = dStyle;
				if (mesura.getDetall() != null) {
					nom = " |---" + nom;
					st = greyStyle;
					dSt = dGreyStyle;
				}

				XSSFCell cell = xlsRow.createCell(colNum++);
				cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(nom)));
				cell.setCellStyle(st);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getDarrera());
				cell.setCellStyle(st);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getMinima());
				cell.setCellStyle(st);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getMaxima());
				cell.setCellStyle(st);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getNumMesures());
				cell.setCellStyle(st);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getMitja());
				cell.setCellStyle(dSt);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getPeriode());
				cell.setCellStyle(dSt);

				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(mesura.getMitja() * mesura.getNumMesures());
				cell.setCellStyle(dSt);
			} catch (Exception e) {
				logger.error("Mesura de temps: No s'ha pogut crear la línia de tasca: " + mesura.getNom(), e);
			}
		}
		
		logger.debug("OK");
		logger.debug("[TEMPS] >>>>> Finalitzada generació de document Excel amb els temps d'execució.");

		try {   
			response.setHeader("Content-disposition", "attachment; filename=mesuresTemps.xls");
			wb.write( response.getOutputStream() );
		} catch (Exception e) {
			logger.error("Mesures temporals: No s'ha pogut realitzar la exportació.");
		}
	}

	private void createHeader(XSSFSheet sheet) {
		int rowNum = 0;
		int colNum = 0;

		// Capçalera
		XSSFRow xlsRow = sheet.createRow(rowNum++);

		XSSFCell cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(getMessage("temps.clau"))));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(getMessage("temps.darrera"))));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(getMessage("temps.minima"))));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(getMessage("temps.maxima"))));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(getMessage("temps.numMesures"))));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(getMessage("temps.mitja"))));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(getMessage("temps.periode"))));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(getMessage("temps.pes"))));
		cell.setCellStyle(headerStyle);
	}
	
	private static final Log logger = LogFactory.getLog(MesuresTempsController.class);
}
