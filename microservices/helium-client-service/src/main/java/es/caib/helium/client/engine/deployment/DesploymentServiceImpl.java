package es.caib.helium.client.engine.deployment;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import es.caib.helium.client.engine.model.WDeployment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DesploymentServiceImpl implements DeploymentService {
	
	private final String missatgeLog = "Cridant Engine Service - Deployment - ";

	private DeploymentFeignClient deploymentClient;

	@Override
	public WDeployment getDesplegament(String deploymentId) {

		log.debug(missatgeLog + " obtinguent desplegament amb id " + deploymentId);
		var responseEntity = deploymentClient.getDesplegament(deploymentId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<WDeployment> getDeployments(MultiValueMap<String, String> requestParams, Integer firstResult, Integer maxResults) {
		
		log.debug(missatgeLog + " obtinguent desplegaments amb parametres " + requestParams.toString());
		var responseEntity = deploymentClient.getDeployments(requestParams, firstResult, maxResults);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public Set<String> getResourceNames(String deploymentId) {

		log.debug(missatgeLog + " obtinguent resources names pel deploymentId " + deploymentId);
		var responseEntity = deploymentClient.getResourceNames(deploymentId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public byte[] getResource(String deploymentId, String resourceName) {
		
		log.debug(missatgeLog + " obtinguent resource pel deploymentId " + deploymentId + " amb resourceName " + resourceName);
		var responseEntity = deploymentClient.getResource(deploymentId, resourceName);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public WDeployment createDeployment(String deploymentName, String tenantId, MultipartFile deploymentFile) {

		log.debug(missatgeLog + " creant deploymentId " + deploymentName);
		var responseEntity = deploymentClient.createDeployment(deploymentName, tenantId, deploymentFile);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void updateDeploymentActions(String deploymentId, List<MultipartFile> handlers, MultipartFile deploymentFile) {

		log.debug(missatgeLog + " update deployment actions amb deploymentId " + deploymentId);
		deploymentClient.updateDeploymentActions(deploymentId, handlers, deploymentFile);
	}

	@Override
	public void esborrarDesplegament(String deploymentId) {

		log.debug(missatgeLog + " esborrar desplegament amb deploymentId " + deploymentId);
		deploymentClient.esborrarDesplegament(deploymentId);	
	}
}

	