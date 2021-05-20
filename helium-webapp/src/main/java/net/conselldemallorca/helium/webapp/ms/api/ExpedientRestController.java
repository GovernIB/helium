package net.conselldemallorca.helium.webapp.ms.api;

import net.conselldemallorca.helium.core.api.WorkflowBridgeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/api/expedients")
public class ExpedientRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

//	public List<ExpedientDto> findExpedientsConsultaGeneral(
//			Long entornId,
//			String titol,
//			String numero,
//			Date dataInici1,
//			Date dataInici2,
//			Long expedientTipusId,
//			Long estatId,
//			boolean nomesIniciats,
//			boolean nomesFinalitzats);
//
//	public List<ExpedientDto> findExpedientsConsultaDadesIndexades(
//			Long entornId,
//			String expedientTipusCodi,
//			Map<String, Object> filtreValors);
//
//	public ExpedientDto getExpedientAmbEntornITipusINumero(
//			Long entornId,
//			String expedientTipusCodi,
//			String numero);
//
//	public void expedientRelacionar(
//			Long expedientIdOrigen,
//			Long expedientIdDesti);
//
//	public void expedientAturar(
//			String processInstanceId,
//			String motiu);
//
//	public void expedientReprendre(
//			String processInstanceId);
//
//	public void expedientModificarEstat(
//			String processInstanceId,
//			String estatCodi);
//
//	public void expedientModificarComentari(
//			String processInstanceId,
//			String comentari);
//
//	public void expedientModificarNumero(
//			String processInstanceId,
//			String numero);

	private static final Log logger = LogFactory.getLog(ExpedientRestController.class);
}
