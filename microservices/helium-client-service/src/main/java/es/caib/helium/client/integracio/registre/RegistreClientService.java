package es.caib.helium.client.integracio.registre;

import es.caib.helium.client.integracio.registre.model.RegistreAssentament;
import es.caib.helium.client.integracio.registre.model.RespostaAnotacioRegistre;
import es.caib.helium.client.integracio.registre.model.RespostaConsultaRegistre;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public interface RegistreClientService {

	public Date getDataJustificant(String numeroRegistre);

	public RespostaAnotacioRegistre crearRegistreSortida(RegistreAssentament registre, Long entornId);
	
	public RespostaConsultaRegistre getRegistreOficinaNom(String numeroRegistre, String usuariCodi, String entitatCodi, Long entornId);
}
