package net.conselldemallorca.helium.webapp.ms.api;

import lombok.Data;
import net.conselldemallorca.helium.core.api.WorkflowBridgeService;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/api/terminis")
public class TerminiRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

	@RequestMapping(value="/iniciat", method = RequestMethod.GET)
	@ResponseBody
	public TerminiIniciatDto getIniciat(
			HttpServletRequest request,
			@RequestParam(value = "processDefinitionId", required = true) String processDefinitionId,
			@RequestParam(value = "processInstanceId", required = true) String processInstanceId,
			@RequestParam(value = "terminiCodi", required = true) String terminiCodi) {

		return workflowBridgeService.getTerminiIniciatAmbProcessInstanceITerminiCodi(
				processDefinitionId,
				processInstanceId,
				terminiCodi);
	}

	@RequestMapping(value="/{terminiId}/cancelar", method = RequestMethod.POST)
	@ResponseBody
	public void cancelar(
			HttpServletRequest request,
			@PathVariable("terminiId") Long terminiId,
			@RequestBody Date data) {

		workflowBridgeService.terminiCancelar(
				terminiId,
				data);
	}

	@RequestMapping(value="/{terminiId}/configurar", method = RequestMethod.POST)
	@ResponseBody
	public void configurar(
			HttpServletRequest request,
			@PathVariable("terminiId") Long terminiId,
			@RequestBody TerminiConfigurar terminiConfigurar) {

		workflowBridgeService.configurarTerminiIniciatAmbDadesWf(
				terminiId,
				terminiConfigurar.getTaskInstanceId(),
				terminiConfigurar.getTimerId());
	}

	@RequestMapping(value="/calcular", method = RequestMethod.POST)
	@ResponseBody
	public Date calcularFi(
			HttpServletRequest request,
			@RequestBody TerminiCalcul terminiCalcul) {

		return workflowBridgeService.terminiCalcularDataFi(
				terminiCalcul.getInici(),
				terminiCalcul.getAnys(),
				terminiCalcul.getMesos(),
				terminiCalcul.getDies(),
				terminiCalcul.isLaborable(),
				terminiCalcul.getProcessInstanceId());
	}

//	@RequestMapping(value="/{dominiId}/test", method = RequestMethod.POST, consumes="application/json", produces = "application/json")
//	@ResponseBody
//	public ResponseEntity<Object> testS(
//			HttpServletRequest request,
//			@PathVariable Long dominiId,
//			Model model,
//			@RequestBody Cmd params,
//			BindingResult bindingResult) {
//		String[] codis = params.getCodi();
//		String[] tipus = params.getTipusParam();
//		String[] values = params.getPar();
//		Map<String, Object> parametres = new HashMap<String, Object>();
//		for(int i = 0; i<codis.length; i++) {
//			if(tipus[i].equals("string")){
//			    parametres.put(codis[i],values[i].toString());
//			}
//			if(tipus[i].equals("int")){
//			    parametres.put(codis[i],Long.parseLong(values[i]));
//			}
//			if(tipus[i].equals("float")){
//			    parametres.put(codis[i],Double.parseDouble(values[i]));
//			}
//			if(tipus[i].equals("boolean")){
//			    parametres.put(codis[i],Boolean.parseBoolean(values[i]));
//			}
//			if(tipus[i].equals("date")){
//			    String[] dataSplit = values[i].split("/");
//			    Calendar data = new GregorianCalendar();
//			    data.set(Integer.parseInt(dataSplit[2]),Integer.parseInt(dataSplit[1]),Integer.parseInt(dataSplit[0]));
//			    parametres.put(codis[i],data);
//			}
//			if(tipus[i].equals("price")){
//			    String dat = values[i];
//			    BigDecimal datBDecimal = new BigDecimal(new Double(dat));
//			    parametres.put(codis[i], datBDecimal);
//			}
//		}
//		try {
//			return new ResponseEntity<Object>(AjaxHelper.generarAjaxFormOk(dissenyService.consultaDomini(dominiId,params.getCodiDomini(), parametres)),HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<Object>(AjaxHelper.generarAjaxFormErrors(e.getLocalizedMessage(), bindingResult),HttpStatus.BAD_REQUEST);
//		}
//	}
	
//	@InitBinder
//	public void initBinder(WebDataBinder binder) {
//	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
//	}

	@Data
	public class TerminiCalcul {
		private Date inici;
		private int anys;
		private int mesos;
		private int dies;
		private boolean laborable;
		private String processInstanceId;
	}

	@Data
	public class TerminiConfigurar {
		private String taskInstanceId;
		private Long timerId;
	}

	private static final Log logger = LogFactory.getLog(TerminiRestController.class);
}
