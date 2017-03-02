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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.api.WToken;
import net.conselldemallorca.helium.core.api.WorkflowEngineApi;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.dto.TokenDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTokenService;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;

/**
 * Servei per gestionar els tokens dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientTokenServiceImpl implements ExpedientTokenService {

	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private RegistreRepository registreRepository;

	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private ExpedientHelper expedientHelper;



	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TokenDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		logger.debug("Consultant tokens d'una instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ")");
		List<WToken> jbpmTokens = new ArrayList<WToken>();
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TOKEN_READ,
						ExtendedPermission.ADMINISTRATION});
		if (expedient != null){
			Map<String, WToken> tokens = workflowEngineApi.getAllTokens(processInstanceId);
			for (String tokenName: tokens.keySet()) {
				jbpmTokens.add(tokens.get(tokenName));
			}
			Collections.sort(
					jbpmTokens,
					new Comparator<WToken>() {
						@Override
					    public int compare(WToken o1, WToken o2) {
					        return o1.getId().compareTo(o2.getId());
					    }
					});
		}
		return conversioTipusHelper.convertirList(jbpmTokens, TokenDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public boolean canviarEstatActiu(
			Long expedientId,
			String processInstanceId,
			Long tokenId,
			boolean activar) {
		logger.debug("Canviant l'estat actiu del token (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"tokenId=" + tokenId + ", " +
				"activar=" + activar + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TOKEN_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		if( expedient!= null)
			return workflowEngineApi.tokenActivar(tokenId, activar);
		else
			return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<String> findArrivingNodeNames(
			Long expedientId,
			String processInstanceId,
			String tokenId) {
		logger.debug("Consultant els noms dels nodes que arriben al node del token (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"tokenId=" + tokenId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TOKEN_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		if( expedient!= null)
			return workflowEngineApi.findArrivingNodeNames(tokenId);
		else
			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public TokenDto findById(
			Long expedientId,
			String processInstanceId,
			String tokenId) {
		logger.debug("Consultant informació del token amb id (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"tokenId=" + tokenId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TOKEN_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		if( expedient != null)
			return conversioTipusHelper.convertir(workflowEngineApi.getTokenById(tokenId), TokenDto.class);
		else
			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void retrocedir(
			Long expedientId,
			String processInstanceId,
			String tokenId,
			String nodeName,
			boolean cancelTasks) {
		logger.debug("Retrocedint el token fins al node (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"tokenId=" + tokenId + ", " +
				"nodeName=" + nodeName + ", " +
				"cancelTasks=" + cancelTasks + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TOKEN_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		if( expedient != null){
			WToken token = workflowEngineApi.getTokenById(tokenId);
			if (token == null)
				throw new NoTrobatException(WToken.class, tokenId);
			
			String nodeNameVell = token.getNodeName();
			workflowEngineApi.tokenRedirect(new Long(tokenId).longValue(), nodeName, cancelTasks, true, false);
			
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

	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);

}
