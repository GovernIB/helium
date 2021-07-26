package es.caib.helium.client.integracio.arxiu;

import es.caib.helium.client.integracio.arxiu.model.DocumentArxiu;
import es.caib.helium.client.integracio.arxiu.model.Expedient;
import es.caib.helium.client.integracio.arxiu.model.ExpedientArxiu;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;


public interface ArxiuFeignClient {
	
	//TODO REVISAR ESTIGUI CORRECTE L'OBJECTE
	@RequestMapping(method = RequestMethod.GET, value = ArxiuPath.GET_EXPEDIENT_BY_UUID)
	public ResponseEntity<Expedient> getExpedientsByUuId(
			@PathVariable("uuId") String uuId, 
			@RequestParam("entornId") Long entornId); 
	
	@RequestMapping(method = RequestMethod.POST, value = ArxiuPath.POST_EXPEDIENT)
	public ResponseEntity<Void> postExpedient(@RequestBody ExpedientArxiu expedient, 
			@RequestParam("entornId") Long entornId);

	@RequestMapping(method = RequestMethod.PUT, value = ArxiuPath.PUT_EXPEDIENT)
	public ResponseEntity<Void> putExpedient(@RequestBody ExpedientArxiu expedient, 
			@RequestParam("entornId") Long entornId);

	@RequestMapping(method = RequestMethod.DELETE, value = ArxiuPath.DELETE_EXPEDIENT)
	public ResponseEntity<Void> deleteExpedient(
			@PathVariable("uuId") String uuId,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.DELETE, value = ArxiuPath.DELETE_EXPEDIENT)
	public ResponseEntity<Void> tancarExpedient(
			@PathVariable String arxiuUuId,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.POST, value = ArxiuPath.OBRIR_EXPEDIENT)
	public ResponseEntity<Void> obrirExpedient(
			@PathVariable String arxiuUuId,
			@RequestParam("entornId") Long entornId);
	
	//TODO EL DOCUMENT ÉS UN OBJECTE BASTANT COMPLEXT. VEURE SI PASAR-LO TOT O IMPORTAR
//	@RequestMapping(method = RequestMethod.GET, value = ArxiuPath.GET_DOCUMENT)
//	public ResponseEntity<Document> getDocument(@PathVariable("uuId") String uuId,
//			@RequestParam("versio") String versio,
//			@RequestParam("ambContingut") boolean ambContingut,
//			@RequestParam("isSignat") boolean isSignat,
//			@RequestParam("entornId") Long entornId);
	
	
	@RequestMapping(method = RequestMethod.POST, value = ArxiuPath.POST_DOCUMENT)
	public ResponseEntity<Void> postDocument(
			@Valid @RequestBody DocumentArxiu document,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.PUT, value = ArxiuPath.PUT_DOCUMENT)
	public ResponseEntity<Void> putDocument(
			@Valid @RequestBody DocumentArxiu document, 
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.DELETE, value = ArxiuPath.DELETE_DOCUMENT)
	public ResponseEntity<Void> deleteDocument(
			@PathVariable("uuId") String uuId,
			@RequestParam("entornId") Long entornId);
}
