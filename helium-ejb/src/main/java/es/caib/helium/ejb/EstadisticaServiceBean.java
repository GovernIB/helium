package es.caib.helium.ejb;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.comanda.model.server.monitoring.DimensioDesc;
import es.caib.comanda.model.server.monitoring.IndicadorDesc;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;

import es.caib.helium.logic.intf.service.EstadisticaService;

/**
 * EJB per a EstadisticaService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EstadisticaServiceBean extends AbstractServiceEjb<EstadisticaService> implements EstadisticaService {

	@Delegate
	private EstadisticaService delegateService;

	protected void setDelegateService(EstadisticaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean generarDadesExplotacio() {
		return delegateService.generarDadesExplotacio();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean generarDadesExplotacio(Date data) {
		return delegateService.generarDadesExplotacio(data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void generarDadesExplotacio(Date data, Date toDate) {
		delegateService.generarDadesExplotacio(data, toDate);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public RegistresEstadistics consultaDarreresEstadistiques() {
		return delegateService.consultaDarreresEstadistiques();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public RegistresEstadistics consultaEstadistiques(Date data) {
		return delegateService.consultaEstadistiques(data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public List<RegistresEstadistics> consultaEstadistiques(Date dataInici, Date dataFi) {
		return delegateService.consultaEstadistiques(dataInici, dataFi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public List<DimensioDesc> getDimensions() {
		return delegateService.getDimensions();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_COM"})
	public List<IndicadorDesc> getIndicadors() {
		return delegateService.getIndicadors();
	}

}
