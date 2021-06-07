package es.caib.helium.integracio.service.registre;

import java.util.Date;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.registre.RegistreAssentament;
import es.caib.helium.integracio.domini.registre.RespostaAnotacioRegistre;
import es.caib.helium.integracio.domini.registre.RespostaConsultaRegistre;
import es.caib.helium.integracio.excepcions.registre.RegistreException;

@Service
public interface RegistreService {

	public RespostaAnotacioRegistre registrarSortida(RegistreAssentament registreSortida, String aplicacioNom, String aplicacioVersio) throws RegistreException;
	public RespostaConsultaRegistre obtenirRegistreSortida(String numRegistre, String usuariCodi, String entitatCodi) throws RegistreException;

	public Date getJustificantDataRecepcio(String numeroRegistre) throws RegistreException;
}
