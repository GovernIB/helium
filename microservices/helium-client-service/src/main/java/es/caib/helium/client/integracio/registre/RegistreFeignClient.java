package es.caib.helium.client.integracio.registre;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.integracio.registre.model.RegistreAssentament;
import es.caib.helium.client.integracio.registre.model.RespostaAnotacioRegistre;
import es.caib.helium.client.integracio.registre.model.RespostaConsultaRegistre;

public interface RegistreFeignClient {

	@RequestMapping(method = RequestMethod.GET, value = RegistrePath.GET_DATA_JUSTIFICANT)
	public ResponseEntity<Date> getDataJustificant(
			@Valid @PathVariable("numeroRegistre") String numeroRegistre);

	@RequestMapping(method = RequestMethod.POST, value = RegistrePath.CREAR_REGISTRE_SORTIDA)
	public ResponseEntity<RespostaAnotacioRegistre> crearRegistreSortida(
			@Valid @RequestBody RegistreAssentament registre,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.GET, value = RegistrePath.GET_REGISTRE_OFICINA_NOM)
	public ResponseEntity<RespostaConsultaRegistre> getRegistreOficinaNom(
			@Valid @PathVariable("numeroRegistre") String numeroRegistre,
			@RequestParam("usuariCodi") String usuariCodi,
			@RequestParam("entitatCodi") String entitatCodi,
			@RequestParam("entornId") Long entornId);
}
