package es.caib.helium.client.engine.deployment;

import java.util.List;
import java.util.Set;

import javax.ws.rs.QueryParam;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import es.caib.helium.client.engine.model.WDeployment;

public interface DeploymentFeignClient {
	
	@RequestMapping(method = RequestMethod.GET, value = DeploymentApiPath.GET_DESPLEGAMENT)
	public ResponseEntity<WDeployment> getDesplegament(@PathVariable String deploymentId);
	
	@RequestMapping(method = RequestMethod.GET, value = DeploymentApiPath.GET_DESPLEGAMENT)
	public ResponseEntity<List<WDeployment>> getDeployments(
            @RequestParam MultiValueMap<String, String> requestParams,
            @QueryParam("firstResult") Integer firstResult,
            @QueryParam("maxResults") Integer maxResults);

	@RequestMapping(method = RequestMethod.GET, value = DeploymentApiPath.GET_RESOURCE_NAMES)
	public ResponseEntity<Set<String>> getResourceNames(@PathVariable String deploymentId);
	
	@RequestMapping(method = RequestMethod.GET, value = DeploymentApiPath.GET_RESOURCE)
	public ResponseEntity<byte[]> getResource(
            @PathVariable String deploymentId,
            @PathVariable String resourceName);
	
	@RequestMapping(method = RequestMethod.POST, value = DeploymentApiPath.CREATE_DEPLOYMENT)
	public ResponseEntity<WDeployment> createDeployment(
            @RequestPart(value = "deploymentName") String deploymentName,
            @RequestPart(value = "tenantId", required = false) String tenantId,
            @RequestPart(value = "deploymentFile") MultipartFile deploymentFile);
	
	@RequestMapping(method = RequestMethod.PUT, value = DeploymentApiPath.UPDATE_DEPLOYMENT_ACTIONS)
	public ResponseEntity<Void> updateDeploymentActions(
            @PathVariable String deploymentId,
            @RequestPart(value = "handlers", required = false) List<MultipartFile> handlers,
            @RequestPart("deploymentFile") MultipartFile deploymentFile);
	
	@RequestMapping(method = RequestMethod.PUT, value = DeploymentApiPath.ESBORRAR_DESPLEGAMENT)
	public ResponseEntity<Void>  esborrarDesplegament(@PathVariable String deploymentId);
	

}
