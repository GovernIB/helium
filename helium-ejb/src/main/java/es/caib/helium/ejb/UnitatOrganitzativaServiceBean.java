package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.ArbreDto;
import es.caib.helium.commons.dto.NivellAdministracioDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.ProvinciaDto;
import es.caib.helium.commons.dto.TipusViaDto;
import es.caib.helium.commons.dto.UnitatOrganitzativaDto;
import es.caib.helium.commons.dto.UnitatOrganitzativaFiltreDto;
import es.caib.helium.logic.intf.service.UnitatOrganitzativaService;

/**
 * Implementació de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class UnitatOrganitzativaServiceBean extends AbstractServiceEjb<UnitatOrganitzativaService> implements UnitatOrganitzativaService {

	@Delegate
	UnitatOrganitzativaService delegateService;

	protected void setDelegateService(UnitatOrganitzativaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public UnitatOrganitzativaDto create(UnitatOrganitzativaDto unitatOrganitzativa) {
		return delegateService.create(unitatOrganitzativa);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public UnitatOrganitzativaDto update(UnitatOrganitzativaDto unitatOrganitzativa) {
		return delegateService.update(unitatOrganitzativa);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public UnitatOrganitzativaDto delete(Long id) {
		return delegateService.delete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public UnitatOrganitzativaDto findById(Long id) {
		return delegateService.findById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> findByEntitat(String entitatCodi) {
		return delegateService.findByEntitat(entitatCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public UnitatOrganitzativaDto findByCodi(String unitatOrganitzativaCodi) {
		return delegateService.findByCodi(unitatOrganitzativaCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void synchronize(Long entitatId) {
		delegateService.synchronize(entitatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArbreDto<UnitatOrganitzativaDto> findTree(Long id){
		return delegateService.findTree(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> getObsoletesFromWS(Long entitatId) {
		return delegateService.getObsoletesFromWS(entitatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> getVigentsFromWebService(Long entidadId) {
		return delegateService.getVigentsFromWebService(entidadId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isFirstSincronization(Long entidadId) {
		return delegateService.isFirstSincronization(entidadId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> predictFirstSynchronization(Long entitatId) {
		return delegateService.predictFirstSynchronization(entitatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> findByEntitatAndFiltre(String entitatCodi, String filtre, boolean ambArrel, boolean nomesAmbBusties) {
		return delegateService.findByEntitatAndFiltre(entitatCodi, filtre, ambArrel, nomesAmbBusties);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> findByEntitatAndCodiUnitatSuperiorAndFiltre(String entitatCodi, String codiUnitatSuperior, String filtre, boolean ambArrel, boolean nomesAmbBusties) {
		return delegateService.findByEntitatAndCodiUnitatSuperiorAndFiltre(entitatCodi, codiUnitatSuperior, filtre, ambArrel, nomesAmbBusties);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public UnitatOrganitzativaDto getLastHistoricos(UnitatOrganitzativaDto uo) {
		return delegateService.getLastHistoricos(uo);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> getNewFromWS(Long entitatId) {
		return delegateService.getNewFromWS(entitatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> findByCodiAndDenominacioFiltre(String text) {
		return delegateService.findByCodiAndDenominacioFiltre(text);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<UnitatOrganitzativaDto> findAmbFiltrePaginat(UnitatOrganitzativaFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findAmbFiltrePaginat(filtreDto, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> findAll() {
		return delegateService.findAll();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> findByFiltre(
			String nivell,
			String provincia,
			String municipi,
			String nif,
			String nom,
			Boolean arrel) {
		return delegateService.findByFiltre(nivell, provincia, municipi, nif, nom, arrel);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void populateDadesExternesUO(UnitatOrganitzativaDto unitat, List<TipusViaDto> tipusViaList, List<ProvinciaDto> provincies) {
		delegateService.populateDadesExternesUO(unitat, tipusViaList, provincies);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<NivellAdministracioDto> nivellAdministracioFindAll() {
		return delegateService.nivellAdministracioFindAll();
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public UnitatOrganitzativaDto findByCodiExterna(String unitatOrganitzativaCodi) {
		return delegateService.findByCodiExterna(unitatOrganitzativaCodi);
	}

	/** Consutla amb el plugin per codi de UO i crea la unitat a la taula. */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public UnitatOrganitzativaDto consultaCrea(String codiUo) {
		return delegateService.consultaCrea(codiUo);
	}

}
