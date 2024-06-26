package net.conselldemallorca.helium.core.helper;

import java.util.Calendar;

import javax.annotation.Resource;

import org.jbpm.graph.def.Node.NodeType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.model.hibernate.PeticioPinbal;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalEstatEnum;
import net.conselldemallorca.helium.v3.core.api.dto.ScspRespostaPinbal;
import net.conselldemallorca.helium.v3.core.repository.PeticioPinbalRepository;

@Component
public class ConsultaPinbalHelper {

	@Resource private PeticioPinbalRepository peticioPinbalRepository;
	@Resource private ExceptionHelper exceptionHelper;
	@Resource private DocumentHelperV3 documentHelperV3;
	@Resource private PluginHelper pluginHelper;
	@Resource private JbpmHelper jbpmDao;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ScspRespostaPinbal tractamentPeticioAsincronaPendentPinbal(Long peticioPinbalId) {
		
		PeticioPinbal pi = peticioPinbalRepository.findOne(peticioPinbalId);
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
					JbpmToken token = jbpmDao.getTokenById(pi.getTokenId().toString());
					if (token!=null) {
						if (token.getToken().getNode().getNodeType().equals(NodeType.State)) {
							if (pi.getTransicioOK() != null && token.getToken().getNode().getLeavingTransition(pi.getTransicioOK()) != null) {
								jbpmDao.signalToken(pi.getTokenId().longValue(), pi.getTransicioOK());								
							} else {
								jbpmDao.signalToken(pi.getTokenId(), null);
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
