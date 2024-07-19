package net.conselldemallorca.helium.integracio.plugins.unitat;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaDto;


/**
 * Implementació de proves del plugin d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatsOrganiquesPluginMock implements UnitatsOrganiquesPlugin {

	@Override
	public UnitatOrganitzativaDto findUnidad(
			String pareCodi, 
			Timestamp fechaActualizacion,
			Timestamp fechaSincronizacion) throws MalformedURLException {
		return new UnitatOrganitzativaDto ("A04003003", "Govern de les Illes Balears","EA9999999", "A04003003","V", null);
	}

	@Override
	public List<UnitatOrganitzativaDto> findAmbPare(
			String pareCodi, 
			Timestamp fechaActualizacion,
			Timestamp fechaSincronizacion) throws SistemaExternException {
		
		List<UnitatOrganitzativaDto> unitats = new ArrayList<UnitatOrganitzativaDto>();
		
		
		final String CODI_UNITAT_ARREL = "A04003003";
		final String CODI_UNITAT_SUPERIOR = "A04026906"; // Conselleria de Turisme, Cultura i Esports
		
		final String CODI_UNITAT_TO_SPLIT = "A04003746";
		final String CODI_UNITAT_TO_MERGE1 = "A04027399";
		final String CODI_UNITAT_TO_MERGE2 = "A04005876";
		final String CODI_UNITAT_TO_SUBSTITUTE = "A04027025";
		final String CODI_UNITAT_TO_CUMULATIVE_CHANGES = "A04027057 ";
		final String CODI_UNITAT_TO_PROPS_CHANGED = "A04027066";		
		
		//TEST
		
		
		//SPLIT
		unitats.add(new UnitatOrganitzativaDto(CODI_UNITAT_TO_SPLIT, "Denominacio 1", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<String>(Arrays.asList("A99999901", "A99999902"))));
		unitats.add(new UnitatOrganitzativaDto("A99999901", "Denominacio 1", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
		unitats.add(new UnitatOrganitzativaDto("A99999902", "Denominacio 2", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
		
		//MERGE
		unitats.add(new UnitatOrganitzativaDto(CODI_UNITAT_TO_MERGE1, "Denominacio 3" , CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<String>(Arrays.asList("A99999903"))));
		unitats.add(new UnitatOrganitzativaDto(CODI_UNITAT_TO_MERGE2, "Denominacio 4", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<String>(Arrays.asList("A99999903"))));
		unitats.add(new UnitatOrganitzativaDto("A99999903", "Denominacio 5", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
		
		//SUBSTITUTION
		unitats.add(new UnitatOrganitzativaDto(CODI_UNITAT_TO_SUBSTITUTE, "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<String>(Arrays.asList("A99999904"))));
		unitats.add(new UnitatOrganitzativaDto("A99999904", "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
		
		//CUMULATIVE CHANGES
		unitats.add(new UnitatOrganitzativaDto(CODI_UNITAT_TO_CUMULATIVE_CHANGES, "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<String>(Arrays.asList("A99999905"))));
		unitats.add(new UnitatOrganitzativaDto("A99999905", "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<String>(Arrays.asList("A99999906"))));
		unitats.add(new UnitatOrganitzativaDto("A99999906", "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
		
		//PROPS CHANGED
		unitats.add(new UnitatOrganitzativaDto(CODI_UNITAT_TO_PROPS_CHANGED, "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", new ArrayList<String>()));
		
		//NEW
		unitats.add(new UnitatOrganitzativaDto("A99999901", "Denominacio 1", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
		unitats.add(new UnitatOrganitzativaDto("A99999902", "Denominacio 2", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
		

		//SUBSTITUTION BY ITSLEF
		unitats.add(new UnitatOrganitzativaDto("A04005872", "Consorcio Escuela de Hosteleria de Les Illes Balears", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", null));
		unitats.add(new UnitatOrganitzativaDto("A04005872", "Consorcio Escuela de Hosteleria de Les Illes Balears " + new Date().getTime(), CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));

		
		return unitats;
	}

	@Override
	public UnitatOrganitzativaDto unitatsOrganitzativesFindByCodi(String codi) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
