package es.caib.helium.integracio.service.unitat;

import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.unitat.UnitatOrganica;
import es.caib.helium.integracio.excepcions.unitat.UnitatOrganicaException;

@Service
public interface UnitatsOrganiquesService {

	public UnitatOrganica consultaUnitat(String codi, Long entornId) throws UnitatOrganicaException;
	public List<UnitatOrganica> findAmbPare(String pareCodi, Long entornId) throws UnitatOrganicaException;
}
