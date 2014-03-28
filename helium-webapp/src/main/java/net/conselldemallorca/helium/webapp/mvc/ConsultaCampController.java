/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per la gestió dels camps de les consultes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ConsultaCampController extends BaseController {

	private DissenyService dissenyService;

	@Autowired
	public ConsultaCampController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService  = dissenyService;
	}

	@ModelAttribute("command")
	public ConsultaCamp populateCommand(
			HttpServletRequest request) {
		return new ConsultaCamp();
	}

	@RequestMapping(value = "/consulta/camps", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Consulta consulta = dissenyService.getConsultaById(id);
			model.addAttribute("llistat", dissenyService.findCampsConsulta(id, tipus));
			model.addAttribute("camps", dissenyService.findCampsPerCampsConsulta(
					id,
					tipus,
					false));
			model.addAttribute("consulta", consulta);
			model.addAttribute(
					"definicionsProces",
					dissenyService.findDarreresAmbExpedientTipusEntorn(
							entorn.getId(),
							consulta.getExpedientTipus().getId(),
							true));
			return "consulta/camps";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/consulta/camps", method = RequestMethod.POST)
	public String getForm(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus,
//			@RequestParam(value = "informeContingut", required = false) final MultipartFile multipartFile,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ConsultaCamp command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (("submit".equals(submit)) || (submit.length() == 0)) {
				command.setId(null);
				command.setTipus(tipus);
				Consulta consulta = dissenyService.getConsultaById(id);
//				consulta.setInformeNom(null);
//				consulta.setInformeContingut(null);
//				if (multipartFile != null && multipartFile.getSize() > 0) {
//					try {
//						consulta.setInformeContingut(multipartFile.getBytes());
//						consulta.setInformeNom(multipartFile.getOriginalFilename());
//					} catch (Exception ignored) {}
//				}
				command.setConsulta(consulta);
				
				try {
					dissenyService.createConsultaCamp(command);
					missatgeInfo(request, getMessage("info.camp.consulta.afegit") );
					status.setComplete();
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
					logger.error("No s'ha pogut afegir el camp a la consulta", ex);
					return "redirect:/consulta/camps.html?id=" + id + "&tipus=" + tipus;
				}
				return "redirect:/consulta/camps.html?id=" + id + "&tipus=" + tipus;
			} else {
				return "redirect:/consulta/llistat.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/consulta/campDelete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "consultaId", required = true) Long consultaId,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.deleteConsultaCamp(id);
				missatgeInfo(request, getMessage("info.camp.consulta.esborrat") );
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.esborrar.camp.consulta"), ex.getLocalizedMessage());
				logger.error("No s'ha pogut esborrar el registre", ex);
			}
			return "redirect:/consulta/camps.html?id=" + consultaId + "&tipus=" + tipus;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/consulta/campFiltrePujar")
	public String pujarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus,
			@RequestParam(value = "consultaId", required = true) Long consultaId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.goUpConsultaCamp(id);
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.canviar.ordre.camp.consulta"), ex.getLocalizedMessage());
				logger.error("No s'ha pogut canviar l'ordre del camp de la consulta", ex);
			}
			return "redirect:/consulta/camps.html?id=" + consultaId + "&tipus=" + tipus;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/consulta/campFiltreBaixar")
	public String baixarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus,
			@RequestParam(value = "consultaId", required = true) Long consultaId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.goDownConsultaCamp(id);
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.canviar.ordre.camp.consulta"), ex.getLocalizedMessage());
				logger.error("No s'ha pogut canviar l'ordre del camp de la consulta", ex);
			}
			return "redirect:/consulta/camps.html?id=" + consultaId + "&tipus=" + tipus;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec"));
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/consulta/reportDownload")
	public String downloadAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "consultaId", required = true) Long consultaId,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				Consulta consulta = dissenyService.getConsultaById(consultaId);
				List<Camp> camps = dissenyService.findCampsPerCampsConsulta(
						consultaId,
						tipus,
						false);
				/*List<ConsultaCamp> campsConsulta = dissenyService.findCampsConsulta(consultaId, tipus);
				List<String> fieldNames = new ArrayList<String>();
				for (ConsultaCamp camp: campsConsulta) {
					String definicioProces = camp.getDefprocJbpmKey();
					String codiVariable = camp.getCampCodi();
					if (definicioProces!=null && codiVariable!=null)
						fieldNames.add(definicioProces + "/"+ codiVariable);
					else if (codiVariable!=null)
						fieldNames.add(codiVariable);
				}*/
				String jasperReport = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
								"<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\" " + 
									"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + 
									"xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports " + 
									"http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\" " + 
									"name=\"report_basic\" language=\"groovy\" pageWidth=\"842\" pageHeight=\"595\" " + 
									"orientation=\"Landscape\" columnWidth=\"802\" leftMargin=\"20\" " +
									"rightMargin=\"20\" topMargin=\"20\" bottomMargin=\"20\">" +
									"\n<property name=\"ireport.zoom\" value=\"1.0\"/>" +
									"\n<property name=\"ireport.x\" value=\"0\"/>" +
									"\n<property name=\"ireport.y\" value=\"0\"/>";
				for (Camp camp: camps) {
					jasperReport = jasperReport + "\n<field name=\"" + camp.getCodiPerInforme() +"\" class=\"net.conselldemallorca.helium.report.FieldValue\"/>";
				}
				jasperReport = jasperReport + 
					"\n<title>" +
						"\n<band height=\"30\" splitType=\"Stretch\">" +
							"\n<staticText>" +
								"\n<reportElement x=\"0\" y=\"0\" width=\"750\" height=\"26\"/>" +
								"\n<textElement>" +
									"\n<font size=\"18\" isBold=\"true\"/>" +
								"\n</textElement>" +
								"\n<text>" + consulta.getNom() +"</text>" +
							"\n</staticText>" +
						"\n</band>" +
					"\n</title>" +
					"\n<pageHeader>" +
						"\n<band height=\"30\" splitType=\"Stretch\"/> " +
					"\n</pageHeader>" +
					"\n<columnHeader>" +
						"\n<band height=\"25\" splitType=\"Stretch\">";
				int widthField = 0;
				if (camps.size()>0) widthField = 800/camps.size();
				int xPosition = 0;
				for (Camp camp: camps) {
					jasperReport = jasperReport + 
							"\n<staticText>" + 
								"\n<reportElement x=\""+xPosition+"\" y=\"2\" width=\""+widthField+"\" height=\"20\"/>" +
								"\n<textElement/>" +
								"\n<text><![CDATA[" + camp.getEtiqueta() + "]]></text>" +
							"\n</staticText>";
					xPosition = xPosition + widthField;
				}
				
				jasperReport = jasperReport +
							"\n</band>" +
						"\n</columnHeader>" +
						"\n<detail>" +
							"\n<band height=\"24\" splitType=\"Stretch\">";
				
				xPosition = 0;
				for (Camp camp: camps) {
					jasperReport = jasperReport + 		
						"\n<textField>" +
							"\n<reportElement x=\""+xPosition+"\" y=\"4\" width=\""+widthField+"\" height=\"20\"/>" +
							"\n<textElement/>" +
							"\n<textFieldExpression><![CDATA[$F{"+camp.getCodiPerInforme()+"}]]></textFieldExpression>" +
						"\n</textField>";
					xPosition = xPosition + widthField;
				}
				
				jasperReport=jasperReport +
						"\n</band>" +
					"\n</detail>" +
					"\n<columnFooter>" +
						"\n<band height=\"25\" splitType=\"Stretch\"/>" +
					"\n</columnFooter>" +
					"\n<pageFooter>" +
						"\n<band height=\"30\" splitType=\"Stretch\"/>" +
					"\n</pageFooter>" +
				 "\n</jasperReport>";
				
				String nomInforme = consulta.getInformeNom();
				if (nomInforme==null) nomInforme = "report_"+consulta.getCodi()+".jrxml";
				byte[] byteArray = jasperReport.getBytes();
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_FILENAME,
						nomInforme);
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_DATA,
						byteArray);
				return "arxiuView";
			} catch (Exception e) {
				e.printStackTrace();
				return "redirect:/consulta/llistat.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				ExpedientTipus.class,
				new ExpedientTipusTypeEditor(dissenyService));
	}



	private static final Log logger = LogFactory.getLog(ConsultaCampController.class);

}
