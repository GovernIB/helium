package es.caib.helium.service.helper;

import java.util.Calendar;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.commons.dto.PeticioPinbalEstatEnum;
import es.caib.helium.commons.dto.ScspRespostaPinbal;
import es.caib.helium.logic.intf.dto.engine.WNode.WNodeType;
import es.caib.helium.logic.intf.dto.engine.WToken;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import es.caib.helium.persistence.entity.PeticioPinbal;
import es.caib.helium.persistence.repository.PeticioPinbalRepository;

//@Component
public class ConsultaPinbalHelper {

	@Resource private PeticioPinbalRepository peticioPinbalRepository;
	@Resource private ExceptionHelper exceptionHelper;
	@Resource private DocumentHelperV3 documentHelperV3;
	@Resource private PluginHelper pluginHelper;
	@Resource private WorkflowEngineApi workflowEngineApi;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ScspRespostaPinbal tractamentPeticioAsincronaPendentPinbal(Long peticioPinbalId) {
		
		PeticioPinbal pi = peticioPinbalRepository.findById(peticioPinbalId).orElse(null);
		ScspRespostaPinbal resultat = new ScspRespostaPinbal();
		
		try {
			// Consulta l'estat
			resultat = pluginHelper.consultaEstatPeticioPinbal(pi.getPinbalId());
			if (PeticioPinbalEstatEnum.TRAMITADA.equals(resultat.getEstatAsincron())) {
				documentHelperV3.crearActualitzarDocument(
						null,
						pi.getDocument().getProcessInstanceId(),
						pi.getDocument().getCodiDocument(),
						Calendar.getInstance().getTime(),
						resultat.getJustificant().getNom(),
						resultat.getJustificant().getContingut(),
						null,
						null,
						null,
						null);
				
				// Segons el resultat de la resposta avança l'expedient
				if (pi.getTokenId() != null) {
					WToken token = workflowEngineApi.getTokenById(pi.getTokenId().toString());
					if (token!=null) {
						if (token.getToken().getNode().getNodeType().equals(WNodeType.State)) {
							if (pi.getTransicioOK() != null && token.getToken().getNode().getLeavingTransition(pi.getTransicioOK()) != null) {
								workflowEngineApi.signalToken(pi.getTokenId().longValue(), pi.getTransicioOK());								
							} else {
								workflowEngineApi.signalToken(pi.getTokenId(), null);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			resultat.setEstatAsincron(PeticioPinbalEstatEnum.ERROR_PROCESSANT);
			String error = ex.getMessage() + ": " + exceptionHelper.getMissageFinalCadenaExcepcions(ex);
			if (error!=null && error.length()>4000) {
				error = error.substring(0, 4000);
			}
			resultat.setErrorProcessament(error);
		}
		
		//Actualitzam la registre de la BBDD
		pi.setEstat(resultat.getEstatAsincron());
		pi.setErrorProcessament(resultat.getErrorProcessament());
		if (pi.getDataProcessamentPrimer()==null) {
			pi.setDataProcessamentPrimer(Calendar.getInstance().getTime());
		} else {
			pi.setDataProcessamentDarrer(Calendar.getInstance().getTime());
		}
		
		//Retornam el objecte amb la informació per si es vol utilitzar per mostrar-la per pantalla
		return resultat;
	}
}
