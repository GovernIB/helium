package es.caib.helium.client.integracio.arxiu;

import es.caib.helium.client.integracio.arxiu.model.ConsultaDocument;
import es.caib.helium.client.integracio.arxiu.model.ContingutArxiu;
import es.caib.helium.client.integracio.arxiu.model.Document;
import es.caib.helium.client.integracio.arxiu.model.DocumentArxiu;
import es.caib.helium.client.integracio.arxiu.model.Expedient;
import es.caib.helium.client.integracio.arxiu.model.ExpedientArxiu;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


public interface ArxiuFeignClient {

	String SERVEI = "-arxiu";

	//TODO REVISAR ESTIGUI CORRECTE L'OBJECTE
	@RequestMapping(method = RequestMethod.GET, value = ArxiuPath.GET_EXPEDIENT_BY_UUID)
	ResponseEntity<Expedient> getExpedientByUuId(
			@PathVariable("uuId") String uuId,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.POST, value = ArxiuPath.POST_EXPEDIENT)
	ResponseEntity<Void> postExpedient(@RequestBody ExpedientArxiu expedient,
			@RequestParam("entornId") Long entornId);

	@RequestMapping(method = RequestMethod.PUT, value = ArxiuPath.PUT_EXPEDIENT)
	ResponseEntity<Void> putExpedient(@RequestBody ExpedientArxiu expedient,
			@RequestParam("entornId") Long entornId);

	@RequestMapping(method = RequestMethod.DELETE, value = ArxiuPath.DELETE_EXPEDIENT)
	ResponseEntity<Void> deleteExpedient(
			@PathVariable("uuId") String uuId,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.DELETE, value = ArxiuPath.DELETE_EXPEDIENT)
	ResponseEntity<Void> tancarExpedient(
			@PathVariable String arxiuUuId,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.POST, value = ArxiuPath.OBRIR_EXPEDIENT)
	ResponseEntity<Void> obrirExpedient(
			@PathVariable String arxiuUuId,
			@RequestParam("entornId") Long entornId);

	@RequestMapping(method = RequestMethod.GET, value = ArxiuPath.GET_DOCUMENT)
	ResponseEntity<Document> getDocument(
			@PathVariable("uuId") String uuId,
			@SpringQueryMap ConsultaDocument consulta);

	@RequestMapping(method = RequestMethod.POST, value = ArxiuPath.POST_DOCUMENT)
	ResponseEntity<ContingutArxiu> postDocument(
			@RequestBody DocumentArxiu document,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.PUT, value = ArxiuPath.PUT_DOCUMENT)
	ResponseEntity<ContingutArxiu> putDocument(
			@RequestBody DocumentArxiu document,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.DELETE, value = ArxiuPath.DELETE_DOCUMENT)
	ResponseEntity<Void> deleteDocument(
			@PathVariable("uuId") String uuId,
			@RequestParam("entornId") Long entornId);
}
