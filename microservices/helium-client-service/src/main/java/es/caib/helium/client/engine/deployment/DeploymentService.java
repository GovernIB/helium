package es.caib.helium.client.engine.deployment;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import es.caib.helium.client.engine.model.WDeployment;

@Service
public interface DeploymentService {

	public WDeployment getDesplegament(@PathVariable String deploymentId);
	
	public List<WDeployment> getDeployments(MultiValueMap<String, String> requestParams, Integer firstResult, Integer maxResults);

	public Set<String> getResourceNames(String deploymentId);
	
	public byte[] getResource(String deploymentId, String resourceName);
	
	public WDeployment createDeployment(String deploymentName, String tenantId, MultipartFile deploymentFile);
	
	public void updateDeploymentActions(String deploymentId, List<MultipartFile> handlers, MultipartFile deploymentFile);
	
	public void esborrarDesplegament(String deploymentId);
}
