package es.caib.helium.client.integracio.unitat;

import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.unitat.model.UnitatOrganica;

@Service
public interface UnitatClientService {

	
	public UnitatOrganica consultaUnitat(String codi, Long entornId);
	
	public List<UnitatOrganica> consultaArbre(String codi, Long entornId);
}
