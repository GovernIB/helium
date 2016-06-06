/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
import net.conselldemallorca.helium.v3.core.api.dto.TokenDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.TokenService;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servei per gestionar els tokens dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class TokenServiceImpl implements TokenService{
	
	@Resource
	private JbpmHelper jbpmHelper;
	
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	@Resource
	private ExpedientRepository expedientRepository;
	
	@Resource
	private RegistreRepository registreRepository;
	
	@Resource
	private ExpedientHelper expedientHelper;

	@Transactional(readOnly=true)
	@Override
	public List<TokenDto> findTokensPerExpedient(Long expedientId, String processInstanceId) {
		List<JbpmToken> jbpmTokens = new ArrayList<JbpmToken>();
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				false,
				false,
				true);
		if (expedient != null){
			
			Map<String, JbpmToken> tokens = jbpmHelper.getAllTokens(processInstanceId);
			for (String tokenName: tokens.keySet()) {
				jbpmTokens.add(tokens.get(tokenName));
			}
			
			Collections.sort(jbpmTokens,new TokenComparator());
		}
		return conversioTipusHelper.convertirList(jbpmTokens, TokenDto.class);
	}
	
	private class TokenComparator implements Comparator<JbpmToken> {
    @Override
	    public int compare(JbpmToken o1, JbpmToken o2) {
	        return o1.getId().compareTo(o2.getId());
	    }
	}
	
	@Transactional
	@Override
	public boolean activar(Long expedientId, Long tokenId, boolean activar) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				false,
				false,
				true);
		if( expedient!= null)
			return jbpmHelper.tokenActivar(tokenId, activar);
		else
			return false;
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<String> findArrivingNodeNames(Long expedientId, String tokenId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				false,
				false,
				true);
		if( expedient!= null)
			return jbpmHelper.findArrivingNodeNames(tokenId);
		else
			return null;
	}

	@Transactional(readOnly=true)
	@Override
	public TokenDto findById(Long expedientId, String tokenId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				false,
				false,
				true);
		if( expedient != null)
			return conversioTipusHelper.convertir(jbpmHelper.getTokenById(tokenId), TokenDto.class);
		else
			return null;
	}
	
	@Transactional
	@Override
	public void tokenRetrocedir(Long expedientId, String tokenId,String nodeName,boolean cancelTasks) {
		
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				false,
				false,
				true);
		if( expedient != null){
			JbpmToken token = jbpmHelper.getTokenById(tokenId);
			if (token == null)
				throw new NoTrobatException(JbpmToken.class, tokenId);
			
			String nodeNameVell = token.getNodeName();
			jbpmHelper.tokenRedirect(new Long(tokenId).longValue(), nodeName, cancelTasks, true, false);
			
			crearRegistreRetrocedirToken(
					expedientId,
					token.getProcessInstanceId(),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					token.getFullName(),
					nodeNameVell,
					nodeName);
		}
	}
	
	private Registre crearRegistreRetrocedirToken(
			Long expedientId,
			String processInstanceId,
			String responsableCodi,
			String tokenName,
			String nodeOrigen,
			String nodeDesti) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.INSTANCIA_PROCES,
				processInstanceId);
		registre.setMissatge("Redirecció del token \"" + tokenName + "\": " + nodeOrigen + "->" + nodeDesti);
		return registreRepository.save(registre);
	}	
}
