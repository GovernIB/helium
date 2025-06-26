package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEstadisticaDto;
import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.EntornService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusEstadisticaCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per al manteniment d'entorns.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "entornEstadisticaControllerV3")
@RequestMapping("/v3/estadistica")
public class ExpedientTipusEstadisticaController extends BaseController {

	@Resource
	private ExpedientTipusService expedientTipusService;
	@Autowired
	protected DissenyService dissenyService;
	@Resource(name="entornServiceV3")
	private EntornService entornService;
	
	private LinkedHashMap<String, Object> estadisticaPerEntorn = new LinkedHashMap<String, Object>();
	private LinkedHashMap<EntornDto, List<ExpedientTipusDto>> expTipusAgrupatsPerEntornTableData =  new LinkedHashMap<EntornDto, List<ExpedientTipusDto>>();


	/** Resposta GET i POST de la pàgina i formulari d'estadístiques.
	 * 
	 * @param request
	 * @param model
	 * @return Retorna com si fos una crida post de consulta.
	 */
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
	public String estadistiques(
			HttpServletRequest request,
			ExpedientTipusEstadisticaCommand filtreCommand,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) {
		
		if (accio != null && "netejar".equals(accio))
			filtreCommand = new ExpedientTipusEstadisticaCommand();
		
		// Torna a omplir el model
		model.addAttribute("expedientTipusEstadisticaCommand", filtreCommand);		
		// Valida l'accés a l'entorn
		Boolean potAdministrarEntorn = SessionHelper.getSessionManager(request).getPotAdministrarEntorn();
		if (potAdministrarEntorn != null && !potAdministrarEntorn) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"error.permis.administracio.entorn"));
			return "v3/estadisticaEntorns";

		}
		// Afegeix els diferents entorns
		PersonaDto persona = (PersonaDto)request.getSession().getAttribute("dadesPersona");
		List<EntornDto> entorns = (persona != null && persona.isAdmin())?entornService.findActiusAll():entornService.findActiusAmbPermisAdmin();
		
		Collections.sort(
				entorns, 
				new Comparator<EntornDto>() {
					public int compare(EntornDto e1, EntornDto e2) {
						int order = e1.getNom().compareTo(e2.getNom());
						return order;
					}
				}
			);
		model.addAttribute("entorns",entorns);
		
		ExpedientTipusDto expedientTipusSeleccionat=null;
		if(filtreCommand.getExpedientTipusId()!=null) {
			expedientTipusSeleccionat= expedientTipusService.findAmbId(filtreCommand.getExpedientTipusId());
		}		
		LinkedHashMap<EntornDto, List<ExpedientTipusDto>> expTipusAgrupatsPerEntorn = new LinkedHashMap<EntornDto, List<ExpedientTipusDto>>();	
		for(EntornDto entorn: entorns) {
				expTipusAgrupatsPerEntorn.put(entorn,  expedientTipusService.findAmbEntorn(entorn.getId(),
						(persona != null && persona.isAdmin())? false : true
						));
		}
		//ordenar valors del mapa (expedientTipus per nom)
		for (Map.Entry<EntornDto, List<ExpedientTipusDto>> entry : expTipusAgrupatsPerEntorn.entrySet()) {
			Collections.sort(entry.getValue(), new Comparator<ExpedientTipusDto>() {
		    	@Override
		    	public int compare(ExpedientTipusDto e1, ExpedientTipusDto e2) {
		    		int order = e1.getNom().compareTo(e2.getNom());
					return order;
		    	}
		    });
		}
		model.addAttribute("expTipusAgrupatsPerEntorn", expTipusAgrupatsPerEntorn);	
		// Estats
		model.addAttribute("estats", this.getEstatsModel(request, filtreCommand.getExpedientTipusId()));

		
		Boolean anulats = null;
		if (filtreCommand.getMostrarAnulats() != null) {
			if(filtreCommand.getMostrarAnulats().equals(MostrarAnulatsDto.NOMES_ANULATS))
				anulats = true;
			else if(filtreCommand.getMostrarAnulats().equals(MostrarAnulatsDto.NO))
				anulats = false;			
		}
		
		// Filtra els entorns
		List<EntornDto>	entornsFiltre = new ArrayList<EntornDto>();
		if (filtreCommand.getEntornId() != null || expedientTipusSeleccionat!=null) {
		// Cerca el dto de l'entorn
			for (EntornDto entornFiltre : entorns) {
				if ((entornFiltre.getId().equals(filtreCommand.getEntornId())) || (expedientTipusSeleccionat!=null && expedientTipusSeleccionat.getEntorn().getId().equals(entornFiltre.getId()))) {
					if(!entornsFiltre.contains(entornFiltre)) {
						entornsFiltre.add(entornFiltre);
						break;
					}
				}
			}
		} else {
			entornsFiltre.addAll(entorns);
		}
		
		//Reiniciem variables locals
		estadisticaPerEntorn = new LinkedHashMap<String, Object>();
		expTipusAgrupatsPerEntornTableData =  new LinkedHashMap<EntornDto, List<ExpedientTipusDto>>();

		for(EntornDto entornFiltre: entornsFiltre) {
			for(EntornDto entorn: expTipusAgrupatsPerEntorn.keySet()) {
				if(entorn.getId().equals(entornFiltre.getId())) {
					expTipusAgrupatsPerEntornTableData.put(entorn,  expedientTipusService.findAmbEntorn(entorn.getId(),
							(persona != null && persona.isAdmin())? false : true
							));
					break;
				}
			}
		}
		model.addAttribute("expTipusAgrupatsPerEntornTableData", expTipusAgrupatsPerEntornTableData);

		// Calcula les estadístiques per entorn
		for(EntornDto entorn: entornsFiltre) {
			List<ExpedientTipusEstadisticaDto> et = expedientTipusService.findEstadisticaByFiltre(
					filtreCommand.getAnyInicial(), 
					filtreCommand.getAnyFinal(), 
					entorn.getId(), 
					filtreCommand.getExpedientTipusId(), 
					anulats, 
					filtreCommand.getNumero(), 
					filtreCommand.getTitol(), 
					this.getEstatTipus(filtreCommand.getEstat()), 
					this.getEstatId(filtreCommand.getEstat()),
					filtreCommand.getAturat(),
					(persona != null && persona.isAdmin())? false : true);
				Map<String, Object> estadistica = this.calcEstadistiques(et);
				estadisticaPerEntorn.put(entorn.getCodi(), estadistica);
		}
		model.addAttribute("estadisticaPerEntorn", estadisticaPerEntorn);		
		
		return "v3/estadisticaEntorns";
	}
	
	/** Obté la llista de parelles codi valor pel model d'estats amb l'estat inicialitzat i finalitzat
	 * 
	 * @param request Petició per saber l'idioma dels missatges.
	 * @param expedientTipusId
	 * @return
	 */
	private List<ParellaCodiValorDto> getEstatsModel(HttpServletRequest request, Long expedientTipusId) {
		List<ParellaCodiValorDto> estats = new ArrayList<ParellaCodiValorDto>();
		// INICIAT
		estats.add(new ParellaCodiValorDto(EstatTipusDto.INICIAT.toString(), getMessage(
								request,
								"expedient.consulta.iniciat")));
		if (expedientTipusId != null 
				&& expedientTipusId > 0) {		
			// Estats tipus d'expedient
			for(EstatDto e : expedientTipusService.estatFindAll(expedientTipusId, true)){
				estats.add(new ParellaCodiValorDto(String.valueOf(e.getId()), e.getNom()));
			}
		}
		// FINALITZAT
		estats.add(new ParellaCodiValorDto(EstatTipusDto.FINALITZAT.toString(), getMessage(
				request,
				"expedient.consulta.finalitzat")));
		return estats;
	}
	
	/** Obté la llista de parelles codi valor pel model d'estats amb l'estat inicialitzat i finalitzat
	 * 
	 * @param request Petició per saber l'idioma dels missatges.
	 * @param expedientTipusId
	 * @return
	 */
	private List<ParellaCodiValorDto> getExpedientsTipusEntornModel(HttpServletRequest request, Long entornId) {
		List<ParellaCodiValorDto> entornSelValor = new ArrayList<ParellaCodiValorDto>();
		if(entornId==-1) {
//			for (Map.Entry<EntornDto, List<ExpedientTipusDto>> entry : expTipusAgrupatsPerEntornTableData.entrySet()) {
//				this.afegirParellesEntornTipusExp(entry.getKey().getId(), entornSelValor);
//			}
		} else {
			this.afegirParellesEntornTipusExp(entornId, entornSelValor);
		}
		return entornSelValor;
	}
	
	private void afegirParellesEntornTipusExp(long entornId, List<ParellaCodiValorDto> entornSelValor) {
		EntornDto entornSeleccionat = entornService.findOne(entornId);
		entornSelValor.add(new ParellaCodiValorDto(entornSeleccionat.getCodi(), entornSeleccionat.getNom()));
		List<ExpedientTipusDto> expTipusList = expedientTipusService.findAmbEntorn(entornId, false);
		List<ParellaCodiValorDto> expTipusPerEntorn = new ArrayList<ParellaCodiValorDto>();
		for(ExpedientTipusDto expTipus: expTipusList) {
			expTipusPerEntorn.add(new ParellaCodiValorDto(String.valueOf(expTipus.getId()), expTipus.getNom()));
		}
		entornSelValor.addAll(expTipusPerEntorn);
	}

	/** Mètode per consultar els estats per tipus d'expedient quan canviï la selecció del tipus d'expedient.
	 * 
	 * @param request
	 * @param expedientTipusId
	 * @return
	 */
	@RequestMapping(value = "/estatsPerTipus/{expedientTipusId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> estatsPerExpedientTipus(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId) {
		return this.getEstatsModel(request, expedientTipusId);
	}
	
	/** Mètode per consultar els expedientsTipus per entorn quan canviï la selecció de l'entorn.
	 * 
	 * @param request
	 * @param entornId
	 * @return
	 */
	@RequestMapping(value = "/expedientTipusPerEntorn/{entornId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> expedientTipusPerEntorn(
			HttpServletRequest request,
			@PathVariable Long entornId) {
		return this.getExpedientsTipusEntornModel(request, entornId);
	}


	/** Si estat és un identificador llavors retorna tipus CUSTOM, si no el corresponent a INICIAT o FINALITZAT.
	 * 
	 * @param estat Conté el text del tipus o l'id.
	 * @return
	 */
	private EstatTipusDto getEstatTipus(String estat) {
		EstatTipusDto estatTipus = null;
		if (estat != null && !estat.isEmpty())
			try {
				estatTipus = EstatTipusDto.valueOf(estat);
			} catch(Exception e) {
				estatTipus = EstatTipusDto.CUSTOM;
			}
		return estatTipus;
	}

	/** Si estat conté un identificador llavors el retorna, si no null.
	 * 
	 * @param estat
	 * @return
	 */
	private Long getEstatId(String estat) {
		Long estatId = null;
		if (estat != null && !estat.isEmpty())
			try {
				estatId = Long.parseLong(estat);
			} catch (Exception e) {
				estatId = null;
			}
		return estatId;
	}

	
	@RequestMapping(value = "excel", method = RequestMethod.POST)
	public void excel(
			HttpServletRequest request,
			HttpServletResponse response,
			ExpedientTipusEstadisticaCommand filtreCommand,
			BindingResult bindingResult,
			Model model) {
		
		generarExcel(request, response, estadisticaPerEntorn, expTipusAgrupatsPerEntornTableData);
	}
	
	/** Calcula les estadístiques i posa les dades en diferents entrades en un Map<String, Object>
	 * 
	 * @param et Dades per calcular les estadístiques per entorn.
	 * @return
	 */
	private Map<String, Object> calcEstadistiques(List<ExpedientTipusEstadisticaDto> et) {

		Map<String, Object> estadistica = new HashMap<String, Object>();
		
		TreeSet<String> anys = new TreeSet<String>();
		Map<String, String> titols = new HashMap<String, String>();  
		Map<String, Map<String, Object>> ete = new TreeMap<String, Map<String, Object>>();
		Map<String, Long> totalTipus = new  HashMap<String, Long>();
		for(ExpedientTipusEstadisticaDto element : et) {
				String nom = element.getCodi();
				if(!titols.containsKey(nom))
					titols.put(nom, element.getNom());
				if(!ete.containsKey(nom))
					ete.put(nom, new TreeMap<String,Object>());
				if(!anys.contains(element.getAnyInici()))
					anys.add(element.getAnyInici());
				if(!totalTipus.containsKey(nom)) {				
					totalTipus.put(nom, element.getN());
				}else {
					totalTipus.put(nom, totalTipus.get(nom) + element.getN());
				}
				ete.get(nom).put(element.getAnyInici(), element.getN());
		}
		
		Map<String, Object> totalAny =  totalPerAny(ete);
		estadistica =  new HashMap<String, Object>();
		estadistica.put("tableData", ete);
		estadistica.put("totalTipus", totalTipus);
		estadistica.put("anys", anys);
		estadistica.put("totalAny", totalAny);
		estadistica.put("titols", titols);
		return estadistica;
	}

	private Map<String, Object> totalPerAny(Map<String, Map<String, Object>> ete){
		Map<String, Object> totalAny = new HashMap<String, Object>();
		for(Map.Entry<String, Map<String, Object>> entry : ete.entrySet()) {
			for(Map.Entry<String, Object> any : entry.getValue().entrySet()) {
				
				if(!totalAny.containsKey(any.getKey())) {
					totalAny.put(any.getKey(), any.getValue());
				}else {
					totalAny.put(any.getKey(), ((Long)totalAny.get(any.getKey()) + ((Long) any.getValue())));
				}
			}
		}
		
		return totalAny;
	}
	
	private void generarExcel(
			HttpServletRequest request,
			HttpServletResponse response,
			LinkedHashMap<String, Object> estadistiLinkedHashMap, 
			LinkedHashMap<EntornDto, List<ExpedientTipusDto>> expTipusAgrupatsPerEntornTableData
			) {
		
		XSSFWorkbook wb;
		XSSFCellStyle cellStyle;
		XSSFCellStyle dStyle;
		XSSFFont bold;
		XSSFCellStyle cellGreyStyle;
		XSSFCellStyle greyStyle;
		XSSFCellStyle dGreyStyle;
		XSSFFont greyFont;
		wb = new XSSFWorkbook();
	
		bold = wb.createFont();
		bold.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		bold.setColor(IndexedColors.WHITE.getIndex());
		
		greyFont = wb.createFont();
		greyFont.setColor(IndexedColors.GREY_25_PERCENT.getIndex());
		greyFont.setCharSet(XSSFFont.ANSI_CHARSET);
		
		cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
		cellStyle.setWrapText(true);
		
		cellGreyStyle = wb.createCellStyle();
		cellGreyStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
		cellGreyStyle.setWrapText(true);
		cellGreyStyle.setFont(greyFont);
		
		greyStyle = wb.createCellStyle();
		greyStyle.setFont(greyFont);
	
		DataFormat format = wb.createDataFormat();
		dStyle = wb.createCellStyle();
		dStyle.setDataFormat(format.getFormat("0.00"));
	
		dGreyStyle = wb.createCellStyle();
		dGreyStyle.setFont(greyFont);
		dGreyStyle.setDataFormat(format.getFormat("0.00"));
			
		XSSFFont deaderBold;
		deaderBold = wb.createFont();
		deaderBold.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		deaderBold.setColor(IndexedColors.WHITE.getIndex());
		XSSFCellStyle headerStyle;
		headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(XSSFCellStyle.FINE_DOTS);
		headerStyle.setFillBackgroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
		headerStyle.setFont(deaderBold);
		
		XSSFFont boldFont = wb.createFont();
		boldFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		XSSFCellStyle boldText = wb.createCellStyle();
		boldText.setFont(boldFont);
		
		LinkedHashMap<EntornDto, Long> totalEntornsMap = new LinkedHashMap<EntornDto, Long>();
		LinkedHashMap<EntornDto, Integer> totalTipusExpedientEntornsMap = new LinkedHashMap<EntornDto, Integer>();
		long totalEntorns = 0;
		for (Map.Entry<EntornDto, List<ExpedientTipusDto>> entry : expTipusAgrupatsPerEntornTableData.entrySet()) {
			long totalEntorn = generarPagina(wb, headerStyle, boldText, entry.getKey(), entry.getValue(), entry.getValue().size());
			totalEntornsMap.put(entry.getKey(), totalEntorn);
			totalTipusExpedientEntornsMap.put(entry.getKey(), entry.getValue().size());
			totalEntorns += totalEntorn;
		}
		
		generarPaginaTotalEntorns(wb, headerStyle, boldText, totalEntornsMap, totalTipusExpedientEntornsMap, totalEntorns);
		
		try {
			String fileName = "Estadistica.xls";
			response.setHeader("Pragma", "");
			response.setHeader("Expires", "");
			response.setHeader("Cache-Control", "");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
			wb.write( response.getOutputStream() );
		} catch (Exception e) {
		}
	}
	
	@SuppressWarnings("unchecked")
	private long generarPagina(XSSFWorkbook wb,  XSSFCellStyle headerStyle, XSSFCellStyle boldText, EntornDto entorn, List<ExpedientTipusDto> expedientsTipus, Integer totatTipologies) {
		XSSFSheet sheet = wb.createSheet(entorn.getNom());
	
		int rowNum = 0;
		int colNum = 0;
		// Capçalera
		XSSFRow xlsRow = sheet.createRow(rowNum++);
		XSSFCell cell;
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize("Codi")));
		cell.setCellStyle(headerStyle);
		sheet.autoSizeColumn(colNum);
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize("Tipus d'expedient")));
		sheet.autoSizeColumn(colNum);
		cell.setCellStyle(headerStyle);
		Map<String, Object> estadistica = null;
		for(Map.Entry<String, Object> estEntry: estadisticaPerEntorn.entrySet()) {
			if(estEntry.getKey().equals(entorn.getCodi())) {
				estadistica=(Map<String, Object>) estEntry.getValue();
				break;
			}
		}
		long totalEntorn=0;
		if(estadistica!=null) {
			for (String any : (TreeSet<String>) estadistica.get("anys")) {
				sheet.autoSizeColumn(colNum);
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(any)));
				cell.setCellStyle(headerStyle);
			}
			
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize("Total tipus")));
			sheet.autoSizeColumn(colNum);
			cell.setCellStyle(headerStyle);
			
			TreeMap<String, Object> tableData = (TreeMap<String, Object>) estadistica.get("tableData");
			Map<String, Long> totalTipus = (Map<String, Long>) estadistica.get("totalTipus");
			Map<String, Long> totalAny = (Map<String, Long>) estadistica.get("totalAny");
			long total = 0;
			for(ExpedientTipusDto expTip : expedientsTipus) {
				if(!tableData.containsKey(expTip.getCodi()))
					continue;
				colNum = 0;
				xlsRow = sheet.createRow(rowNum++);
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(expTip.getCodi())));
				sheet.autoSizeColumn(colNum);
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(expTip.getNom())));
				sheet.autoSizeColumn(colNum);
				
				for (String any : (TreeSet<String>) estadistica.get("anys")) {
					Long t = (((Map<String,Object>) tableData.get(expTip.getCodi())).containsKey(any))? (Long) ((Map<String,Object>) tableData.get(expTip.getCodi())).get(any): new Long(0);
					sheet.autoSizeColumn(colNum);
					cell = xlsRow.createCell(colNum++);
					cell.setCellValue(t);
				}
				Long tt = (Long) totalTipus.get(expTip.getCodi());
				sheet.autoSizeColumn(colNum);
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(tt);
				cell.setCellStyle(boldText);
			}
				
			colNum = 0;
			xlsRow = sheet.createRow(rowNum++);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize("Total per any")));
			cell.setCellStyle(headerStyle);
			sheet.autoSizeColumn(colNum);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue("");
			cell.setCellStyle(headerStyle);
			sheet.autoSizeColumn(colNum);
			
			for (String any : (TreeSet<String>) estadistica.get("anys")) {
				Long t = totalAny.containsKey(any)? totalAny.get(any) : new Long(0);
				sheet.autoSizeColumn(colNum);
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(t);
				cell.setCellStyle(boldText);
				total += t.longValue();
			}
			
			sheet.autoSizeColumn(colNum);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(total);
			cell.setCellStyle(boldText);
			totalEntorn += total;
			for(int i=0; i<colNum; i++)
				sheet.autoSizeColumn(i);
			
			colNum = 0;
			xlsRow = sheet.createRow(rowNum++);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize("Nre. Tipologies")));
			cell.setCellStyle(headerStyle);
			sheet.autoSizeColumn(colNum);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(totatTipologies);
			cell.setCellStyle(boldText);
			sheet.autoSizeColumn(colNum);
		}
		return totalEntorn;
	}
	
	
	private void generarPaginaTotalEntorns(XSSFWorkbook wb, XSSFCellStyle headerStyle, XSSFCellStyle boldText, LinkedHashMap<EntornDto, Long> totalEntornsMap, LinkedHashMap<EntornDto, Integer> totalTipusExpedientEntornsMap, long totalEntorns) {
		XSSFSheet sheet = wb.createSheet("Resum per entorns");
		
		int rowNum = 0;
		int colNum = 0;
		// Capçalera
		XSSFRow xlsRow = sheet.createRow(rowNum++);
		XSSFCell cell;
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize("Codi")));
		cell.setCellStyle(headerStyle);
		sheet.autoSizeColumn(colNum);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize("Entorn")));
		sheet.autoSizeColumn(colNum);
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize("Total Entorn")));
		cell.setCellStyle(headerStyle);
		sheet.autoSizeColumn(colNum);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize("Nre. tipologies")));
		cell.setCellStyle(headerStyle);
		sheet.autoSizeColumn(colNum);
		
		Integer totalTipologies = 0;
			
		for(Map.Entry<EntornDto, Long> entry : totalEntornsMap.entrySet()) {
			colNum=0;
			xlsRow = sheet.createRow(rowNum++);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(entry.getKey().getCodi())));
			sheet.autoSizeColumn(colNum);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(entry.getKey().getNom())));
			sheet.autoSizeColumn(colNum);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(entry.getValue());
			cell.setCellStyle(boldText);
			sheet.autoSizeColumn(colNum);
			cell = xlsRow.createCell(colNum++);
			Integer totalTipus = totalTipusExpedientEntornsMap.get(entry.getKey());
			totalTipologies += totalTipus;
			cell.setCellValue(totalTipus);
			cell.setCellStyle(boldText);
			sheet.autoSizeColumn(colNum);
			
		}
			
		colNum = 0;
		xlsRow = sheet.createRow(rowNum++);
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize("Totals")));
		cell.setCellStyle(headerStyle);
		sheet.autoSizeColumn(colNum);
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue("");
		cell.setCellStyle(headerStyle);
		sheet.autoSizeColumn(colNum);
			
		sheet.autoSizeColumn(colNum);
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(totalEntorns);
		cell.setCellStyle(boldText);

		sheet.autoSizeColumn(colNum);
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(totalTipologies);
		cell.setCellStyle(boldText);

		for(int i=0; i<colNum; i++)
			sheet.autoSizeColumn(i);
	}
				
	@ModelAttribute("anulats")
	public List<ParellaCodiValorDto> populateAnulats(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.no"), MostrarAnulatsDto.NO));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.si"), MostrarAnulatsDto.SI));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.si.only"), MostrarAnulatsDto.NOMES_ANULATS));
		return resposta;
	}
	
	@ModelAttribute("aturats")
	public List<ParellaCodiValorDto> populateAturats(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.no"), false));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.si"), true));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "comu.totes"), null));
		return resposta;
	}
}
