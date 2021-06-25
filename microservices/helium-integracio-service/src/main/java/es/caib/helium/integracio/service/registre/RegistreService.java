package es.caib.helium.integracio.service.registre;

import java.util.Date;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.registre.RegistreAssentament;
import es.caib.helium.integracio.domini.registre.RespostaAnotacioRegistre;
import es.caib.helium.integracio.domini.registre.RespostaConsultaRegistre;
import es.caib.helium.integracio.excepcions.registre.RegistreException;

@Service
public interface RegistreService {

	public RespostaAnotacioRegistre registrarSortida(RegistreAssentament registreSortida, String aplicacioNom, String aplicacioVersio, Long entornId) throws RegistreException;
	public RespostaConsultaRegistre obtenirRegistreSortida(String numRegistre, String usuariCodi, String entitatCodi, Long entornId) throws RegistreException;

	// TODO pels mètodes inferiors a Helium 3.2 es crida a getSignaturaPlugin i no a getRegistrePluginRegWeb3. 
	// treure els mètodes si realment no es fan servir en aquest implementacio
	public Date obtenirDataJustificant(String numeroRegistre) throws RegistreException;
	public void anularRegistreSortida(String registreNumero, String usuari, String entitat, boolean anular) throws RegistreException;
	public RespostaAnotacioRegistre registrarEntrada(RegistreAssentament registreEntrada, String aplicacioNom, String aplicacioVersio) throws RegistreException;
	
}
