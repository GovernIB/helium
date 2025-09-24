package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.comanda.DimensioDesc;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.IndicadorDesc;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.RegistresEstadistics;

/**
 * Servei per manteniment d'estadístiques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 * 
 */
public interface EstadisticaService {
	boolean generarDadesExplotacio();
	boolean generarDadesExplotacio(Date data); 
	void generarDadesExplotacio(Date data, Date toDate);

	RegistresEstadistics consultaDarreresEstadistiques();
	RegistresEstadistics consultaEstadistiques(Date data);
	List<RegistresEstadistics> consultaEstadistiques(Date dataInici, Date dataFi);

	List<DimensioDesc> getDimensions();
	List<IndicadorDesc> getIndicadors();
}
