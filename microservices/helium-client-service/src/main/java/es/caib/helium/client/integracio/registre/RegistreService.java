package es.caib.helium.client.integracio.registre;

import java.util.Date;

import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.registre.model.RegistreAssentament;
import es.caib.helium.client.integracio.registre.model.RespostaAnotacioRegistre;
import es.caib.helium.client.integracio.registre.model.RespostaConsultaRegistre;

@Service
public interface RegistreService {

	public Date getDataJustificant(String numeroRegistre);

	public RespostaAnotacioRegistre crearRegistreSortida(RegistreAssentament registre, Long entornId);
	
	public RespostaConsultaRegistre getRegistreOficinaNom(String numeroRegistre, String usuariCodi, String entitatCodi, Long entornId);
}
