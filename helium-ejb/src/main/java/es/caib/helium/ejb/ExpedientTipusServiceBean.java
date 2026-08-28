package es.caib.helium.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.*;

import es.caib.helium.commons.dto.ConsultaCampDto.TipusConsultaCamp;
import es.caib.helium.commons.dto.ExpedientDto.EstatTipusDto;
import es.caib.helium.commons.dto.MapeigSistraDto.TipusMapeig;
import es.caib.helium.commons.dto.regles.EstatAccioDto;
import es.caib.helium.commons.dto.regles.EstatReglaDto;
import es.caib.helium.commons.exception.ExportException;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.commons.exportacio.EstatExportacio;
import es.caib.helium.commons.exportacio.ExpedientTipusExportacio;
import es.caib.helium.commons.exportacio.ExpedientTipusExportacioCommandDto;
import es.caib.helium.logic.intf.service.ExpedientTipusService;

/**
 * Servei per a gestionar els tipus d'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientTipusServiceBean extends AbstractServiceEjb<ExpedientTipusService> implements ExpedientTipusService {

	@Delegate
	ExpedientTipusService delegateService;

	protected void setDelegateService(ExpedientTipusService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto create(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny,
			List<Long> sequenciesValor) {
		return delegateService.create(
				entornId,
				expedientTipus,
				sequenciesAny,
				sequenciesValor);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto update(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny,
			List<Long> sequenciesValor,
			boolean actualitzarContingutManual) {
		return delegateService.update(
				entornId,
				expedientTipus,
				sequenciesAny,
				sequenciesValor,
				actualitzarContingutManual);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto updateIntegracioForms(
			Long entornId,
			Long expedientTipusId,
			String url,
			String usuari,
			String contrasenya) {
		return delegateService.updateIntegracioForms(
				entornId,
				expedientTipusId,
				url,
				usuari,
				contrasenya);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(
			Long entornId,
			Long expedientTipusId) {
		delegateService.delete(entornId, expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusExportacio exportar(
			Long entornId,
			Long expedientTipusId,
			ExpedientTipusExportacioCommandDto command) {
		return delegateService.exportar(entornId, expedientTipusId, command);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto importar(
			Long entornId,
			Long expedientTipusId,
			ExpedientTipusExportacioCommandDto command,
			ExpedientTipusExportacio importacio) {
		return delegateService.importar(entornId, expedientTipusId, command, importacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public int refrescaProcessExpedients(
			Long entornId,
			ExpedientTipusExportacioCommandDto command,
			ExpedientTipusExportacio importacio) {
		return delegateService.refrescaProcessExpedients(entornId, command, importacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findAmbEntornPermisConsultar(
			Long entornId) throws NoTrobatException {
		return delegateService.findAmbEntornPermisConsultar(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findAmbId(Long expedientTipusId) throws NoTrobatException {
		return delegateService.findAmbId(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findAmbIdPermisConsultar(
			Long entornId,
			Long expedientTipusId) {
		return delegateService.findAmbIdPermisConsultar(
				entornId,
				expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findAmbEntornPermisDissenyar(
			Long entornId) throws NoTrobatException {
		return delegateService.findAmbEntornPermisDissenyar(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findAmbEntornPermisAnotacio(
			Long entornId) throws NoTrobatException {
		return delegateService.findAmbEntornPermisAnotacio(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findAmbEntornPermisExecucioScript(
			Long entornId) throws NoTrobatException {
		return delegateService.findAmbEntornPermisExecucioScript(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findAmbIdPermisDissenyar(
			Long entornId,
			Long expedientTipusId) {
		return delegateService.findAmbIdPermisDissenyar(
				entornId,
				expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findAmbIdPermisDissenyarDelegat(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		return delegateService.findAmbIdPermisDissenyarDelegat(
				entornId,
				expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findAmbEntornPermisCrear(
			Long entornId) throws NoTrobatException {
		return delegateService.findAmbEntornPermisCrear(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findAmbEntorn(
			Long entornId,
			boolean comprovarPermisos) throws NoTrobatException {
		return delegateService.findAmbEntorn(entornId, comprovarPermisos);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findAmbCodiPerValidarRepeticio(
			Long entornId,
			String codi) {
		return delegateService.findAmbCodiPerValidarRepeticio(
				entornId,
				codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findAmbCodi(
			Long entornId,
			String codi) {
		return delegateService.findAmbCodiPerValidarRepeticio(
				entornId,
				codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ExpedientTipusDto> findPerDatatable(
			Long entornId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findPerDatatable(
				entornId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findHeretables(Long entornId) {
		return delegateService.findHeretables(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findHeretats(Long expedientTipusId) {
		return delegateService.findHeretats(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void permisUpdate(
			Long entornId,
			Long expedientTipusId,
			Long unitatOrganitzativaId,
			PermisDto permis,
			boolean entornAdmin) throws NoTrobatException, PermisDenegatException {
		delegateService.permisUpdate(entornId,
				expedientTipusId,
				unitatOrganitzativaId,
				permis,
				entornAdmin);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void permisDelete(
			Long entornId,
			Long expedientTipusId,
			Long permisId,
			boolean entornAdmin,
			String unitatOrganitzativaCodi) throws NoTrobatException, PermisDenegatException {
		delegateService.permisDelete(
				entornId,
				expedientTipusId,
				permisId,
				entornAdmin,
				unitatOrganitzativaCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PermisDto> permisFindAll(
			Long entornId,
			Long expedientTipusId) {
		return delegateService.permisFindAll(
				entornId,
				expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PermisDto permisFindById(
			Long entornId,
			Long expedientTipusId,
			Long permisId,
			String unitatOrganitzativaCodi) throws NoTrobatException, PermisDenegatException {
		return delegateService.permisFindById(
				entornId,
				expedientTipusId,
				permisId,
				unitatOrganitzativaCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EnumeracioDto> enumeracioFindAll(Long expedientTipusId, boolean incloureGlobals) {
		return delegateService.enumeracioFindAll(expedientTipusId, incloureGlobals);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> consultaFindAll(Long expedientTipusId) {
		return delegateService.consultaFindAll(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void definicioProcesDelete(Long id) throws NoTrobatException, PermisDenegatException {
		delegateService.definicioProcesDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesDto> definicioFindAll(Long expedientTipusId)
			throws NoTrobatException, PermisDenegatException {
		return delegateService.definicioFindAll(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean definicioProcesSetInicial(Long expedientTipusId, Long id) {
		return delegateService.definicioProcesSetInicial(expedientTipusId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> definicioProcesFindJbjmKey(Long entornId, Long expedientTipusId, boolean herencia, boolean incloureGlobals) {
		return delegateService.definicioProcesFindJbjmKey(entornId, expedientTipusId, herencia, incloureGlobals);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void definicioProcesIncorporar(Long expedientTipusId, Long id, boolean sobreescriure, boolean tasques) throws ExportException {
		delegateService.definicioProcesIncorporar(expedientTipusId, id, sobreescriure, tasques);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatDto estatSortidaAfegir(Long estatId, Long sortidaId) {
		return delegateService.estatSortidaAfegir(estatId, sortidaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void estatSortidaDelete(Long estatId, Long sortidaId) {
		delegateService.estatSortidaDelete(estatId, sortidaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EstatDto> estatSortidaFindAll(Long estatId) {
		return delegateService.estatSortidaFindAll(estatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DominiDto> dominiFindAll(Long expedientTipusId, boolean incloureGlobals) {
		return delegateService.dominiFindAll(expedientTipusId, incloureGlobals);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EstatDto> estatFindAll(Long expedientTipusId, boolean ambHerencia)
			throws NoTrobatException, PermisDenegatException {
		return delegateService.estatFindAll(expedientTipusId, ambHerencia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatDto estatFindAmbId(Long expedientTipusId, Long estatId) {
		return delegateService.estatFindAmbId(expedientTipusId, estatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatDto estatFindAmbCodi(Long expedientTipusId, String codi) {
		return delegateService.estatFindAmbCodi(expedientTipusId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatDto estatCreate(Long expedientTipusId, EstatDto estat) {
		return delegateService.estatCreate(expedientTipusId, estat);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatDto estatUpdate(EstatDto estat) {
		return delegateService.estatUpdate(estat);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<EstatDto> estatFindPerDatatable(Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.estatFindPerDatatable(expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void estatDelete(Long estatId) throws NoTrobatException, PermisDenegatException {
		delegateService.estatDelete(estatId);

	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean estatMoure(Long estatId, int posicio) throws NoTrobatException {
		return delegateService.estatMoure(estatId, posicio);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public boolean estatMoureOrdre(Long estatId, int posicio, String ordre) throws NoTrobatException {
        return delegateService.estatMoureOrdre(estatId, posicio, ordre);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public int getEstatSeguentOrdre(Long expedientTipusId) throws NoTrobatException {
        return delegateService.getEstatSeguentOrdre(expedientTipusId);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public List<EstatExportacio> estatExportacio(Long expedientTipusId, boolean ambPermisos) throws NoTrobatException {
        return delegateService.estatExportacio(expedientTipusId, ambPermisos);
    }

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EstatDto> estatGetAvancar(long expedientId) {
		return delegateService.estatGetAvancar(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EstatDto> estatGetRetrocedir(long expedientId) {
		return delegateService.estatGetRetrocedir(expedientId);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public List<PermisDto> estatPermisFindAll(Long estatId) {
        return delegateService.estatPermisFindAll(estatId);
    }

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PermisDto estatPermisFindById(Long estatId, Long permisId) throws NoTrobatException, PermisDenegatException {
		return delegateService.estatPermisFindById(estatId, permisId);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public void estatPermisUpdate(Long estatId, PermisDto permis) throws NoTrobatException, PermisDenegatException {
        delegateService.estatPermisUpdate(estatId, permis);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public void estatPermisDelete(Long estatId, Long permisId) throws NoTrobatException, PermisDenegatException {
        delegateService.estatPermisDelete(estatId, permisId);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public List<EstatReglaDto> estatReglaFindAll(Long estatId) {
        return delegateService.estatReglaFindAll(estatId);
    }
    @Override
    @RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EstatReglaDto> estatReglaFindAllByExpedientTipus(Long expedientTipusId) {
    	return delegateService.estatReglaFindAllByExpedientTipus(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatReglaDto estatReglaFindById(Long expedientTipusId, Long reglaId) {
		return delegateService.estatReglaFindById(expedientTipusId, reglaId);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public EstatReglaDto estatReglaFindByNom(Long expedientTipusId, Long estatId, String nom) {
        return delegateService.estatReglaFindByNom(expedientTipusId, estatId, nom);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public EstatReglaDto estatReglaCreate(Long expedientTipusId, Long estatId, EstatReglaDto reglaDto) throws NoTrobatException, PermisDenegatException {
        return delegateService.estatReglaCreate(expedientTipusId, estatId, reglaDto);
    }

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatReglaDto estatReglaUpdate(Long expedientTipusId,Long estatId, EstatReglaDto reglaDto) throws NoTrobatException, PermisDenegatException {
		return delegateService.estatReglaUpdate(expedientTipusId, estatId, reglaDto);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void estatReglaDelete(Long expedientTipusId, Long estatId, Long reglaId) throws NoTrobatException, PermisDenegatException {
		delegateService.estatReglaDelete(expedientTipusId, estatId, reglaId);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public boolean estatReglaMoure(Long reglaId, int posicio) {
        return delegateService.estatReglaMoure(reglaId, posicio);
    }

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<EstatAccioDto> estatAccioEntradaFindPerDatatable(Long estatId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.estatAccioEntradaFindPerDatatable(estatId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EstatAccioDto> estatAccioEntradaFindAll(Long estatId) throws NoTrobatException {
		return delegateService.estatAccioEntradaFindAll(estatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EstatAccioDto> estatAccioSortidaFindAll(Long estatId) throws NoTrobatException {
		return delegateService.estatAccioSortidaFindAll(estatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void estatAccionsDeleteAll(Long estatId) throws NoTrobatException, PermisDenegatException {
		delegateService.estatAccionsDeleteAll(estatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatAccioDto estatAccioEntradaAfegir(Long estatId, Long accioId)
			throws NoTrobatException, PermisDenegatException {
		return delegateService.estatAccioEntradaAfegir(estatId, accioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void estatAccioEntradaDelete(Long estatId, Long estatAccioId) {
		delegateService.estatAccioEntradaDelete(estatId, estatAccioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean estatAccioEntradaMoure(Long estatAccioId, int posicio) {
		return delegateService.estatAccioEntradaMoure(estatAccioId, posicio);
	}
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<EstatAccioDto> estatAccioSortidaFindPerDatatable(Long estatId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.estatAccioSortidaFindPerDatatable(estatId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatAccioDto estatAccioSortidaAfegir(Long estatId, Long accioId)
			throws NoTrobatException, PermisDenegatException {
		return delegateService.estatAccioSortidaAfegir(estatId, accioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void estatAccioSortidaDelete(Long estatId, Long estatAccioId) {
		delegateService.estatAccioSortidaDelete(estatId, estatAccioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean estatAccioSortidaMoure(Long estatAccioId, int posicio) {
		return delegateService.estatAccioSortidaMoure(estatAccioId, posicio);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReassignacioDto reassignacioCreate(Long expedientTipusId, ReassignacioDto reassignacio) throws PermisDenegatException {
		return delegateService.reassignacioCreate(expedientTipusId, reassignacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReassignacioDto reassignacioUpdate(ReassignacioDto reassignacio) throws NoTrobatException, PermisDenegatException {
		return delegateService.reassignacioUpdate(reassignacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reassignacioDelete(Long reassignacioReassignacioId) throws NoTrobatException, PermisDenegatException {
		delegateService.reassignacioDelete(reassignacioReassignacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReassignacioDto reassignacioFindAmbId(Long id) throws NoTrobatException {
		return delegateService.reassignacioFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ReassignacioDto> reassignacioFindPerDatatable(
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.reassignacioFindPerDatatable(
				expedientTipusId,
				filtre,
				paginacioParams);
	}

	@Override
	@PermitAll
	public PaginaDto<RecursDto> recursFindPerDatatable(
		Long expedientTipusId,
		String filtre,
		PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.recursFindPerDatatable(
			expedientTipusId,
			filtre,
			paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto consultaCreate(Long expedientTipusId, ConsultaDto consulta) throws PermisDenegatException {
		return delegateService.consultaCreate(expedientTipusId, consulta);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto consultaUpdate(ConsultaDto consulta, boolean actualitzarContingut) throws NoTrobatException, PermisDenegatException {
		return delegateService.consultaUpdate(consulta, actualitzarContingut);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void consultaDelete(Long consultaId) throws NoTrobatException, PermisDenegatException {
		delegateService.consultaDelete(consultaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto consultaFindAmbId(Long id) throws NoTrobatException {
		return delegateService.consultaFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ConsultaDto> consultaFindPerDatatable(Long entornId, Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.consultaFindPerDatatable(entornId, expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> consultaFindRelacionadesAmbDefinicioProces(Long entornId, Long expedientTipusId,
			String jbpmKey, int versio) {
		return delegateService.consultaFindRelacionadesAmbDefinicioProces(entornId, expedientTipusId, jbpmKey, versio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaDto consultaFindAmbCodiPerValidarRepeticio(Long tipusExpedientId, String codi)
			throws NoTrobatException {
		return delegateService.consultaFindAmbCodiPerValidarRepeticio(tipusExpedientId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean consultaMourePosicio(Long id, int posicio) {
		return delegateService.consultaMourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaCampDto consultaCampCreate(Long consultaId, ConsultaCampDto consultaCamp)
			throws PermisDenegatException {
		return delegateService.consultaCampCreate(consultaId, consultaCamp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void consultaCampDelete(Long id) throws NoTrobatException, PermisDenegatException {
		delegateService.consultaCampDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ConsultaCampDto> consultaCampFindPerDatatable(Long consultaId, TipusConsultaCamp tipus,
			String filtre, PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.consultaCampFindPerDatatable(consultaId, tipus, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean consultaCampMourePosicio(Long id, int posicio) {
		return delegateService.consultaCampMourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaCampDto> consultaCampFindCampAmbConsultaIdAndTipus(Long consultaId, TipusConsultaCamp tipus) {
		return delegateService.consultaCampFindCampAmbConsultaIdAndTipus(consultaId, tipus);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaCampDto consultaCampUpdate(ConsultaCampDto consultaCamp)
			throws NoTrobatException, PermisDenegatException {
		return delegateService.consultaCampUpdate(consultaCamp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ConsultaCampDto consultaCampFindAmbTipusICodiPerValidarRepeticio(Long consultaId, TipusConsultaCamp tipus,
			String codi) throws NoTrobatException {
		return delegateService.consultaCampFindAmbTipusICodiPerValidarRepeticio(consultaId, tipus, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<String> mapeigFindCodiHeliumAmbTipus(Long expedientTipusId, TipusMapeig tipus) {
		return delegateService.mapeigFindCodiHeliumAmbTipus(expedientTipusId, tipus);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<TipusMapeig, Long> mapeigCountsByTipus(Long expedientTipusId) {
		return delegateService.mapeigCountsByTipus(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<MapeigSistraDto> mapeigFindPerDatatable(Long expedientTipusId, TipusMapeig tipus,
			PaginacioParamsDto paginacioParams) {
		return delegateService.mapeigFindPerDatatable(expedientTipusId, tipus, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public MapeigSistraDto mapeigCreate(Long expedientTipusId, MapeigSistraDto mapeig) throws PermisDenegatException {
		return delegateService.mapeigCreate(expedientTipusId, mapeig);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public MapeigSistraDto mapeigUpdate(MapeigSistraDto mapeig) throws NoTrobatException, PermisDenegatException {
		return delegateService.mapeigUpdate(mapeig);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mapeigDelete(Long mapeigId) throws NoTrobatException, PermisDenegatException {
		delegateService.mapeigDelete(mapeigId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public MapeigSistraDto mapeigFindAmbCodiHeliumPerValidarRepeticio(Long expedientTipusId, String codiHelium) {
		return delegateService.mapeigFindAmbCodiHeliumPerValidarRepeticio(expedientTipusId, codiHelium);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public MapeigSistraDto mapeigFindAmbCodiSistraPerValidarRepeticio(Long expedientTipusId, TipusMapeig tipusMapeig, String codiSistra) {
		return delegateService.mapeigFindAmbCodiSistraPerValidarRepeticio(expedientTipusId, tipusMapeig, codiSistra);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MapeigSistraDto> mapeigFindAll(Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		return delegateService.mapeigFindAll(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void consultaCampCols(Long id, String propietat, int valor)
			throws NoTrobatException, PermisDenegatException {
		delegateService.consultaCampCols(id, propietat, valor);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto updateIntegracioTramits(
			boolean sistraActiu,
			Long entornId,
			Long expedientTipusId,
			String tramitCodi,
			boolean notificacionsActivades,
			String notificacioOrganCodi,
			String notificacioOficinaCodi,
			String notificacioUnitatAdministrativa,
			String notificacioCodiProcediment,
			String notificacioAvisTitol,
			String notificacioAvisText,
			String notificacioAvisTextSms,
			String notificacioOficiTitol,
			String notificacioOficiText) {
		return delegateService.updateIntegracioTramits(
				sistraActiu,
				entornId,
				expedientTipusId,
				tramitCodi,
				notificacionsActivades,
				notificacioOrganCodi,
				notificacioOficinaCodi,
				notificacioUnitatAdministrativa,
				notificacioCodiProcediment,
				notificacioAvisTitol,
				notificacioAvisText,
				notificacioAvisTextSms,
				notificacioOficiTitol,
				notificacioOficiText);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PersonaDto> personaFindAll(Long entornId, Long expedientTipusId) throws Exception {
		return delegateService.personaFindAll(entornId, expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto updateMetadadesNti(
			Long entornId,
			Long expedientTipusId,
			boolean actiu,
			String organo,
			String clasificacion,
			String serieDocumental,
			boolean arxiuActiu,
			boolean procedimentComu) {
		return delegateService.updateMetadadesNti(
				entornId,
				expedientTipusId,
				actiu,
				organo,
				clasificacion,
				serieDocumental,
				arxiuActiu,
				procedimentComu);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto updateIntegracioPinbal(
			Long entornId,
			Long expedientTipusId,
			boolean pinbalActiu,
			String pinbalNifCif) {
		return delegateService.updateIntegracioPinbal(
				entornId,
				expedientTipusId,
				pinbalActiu,
				pinbalNifCif);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto updateIntegracioNotib(
			Long expedientTipusId,
			String notibEmisor,
			String notibCodiProcediment,
			boolean notibActiu) {

		return delegateService.updateIntegracioNotib(
				expedientTipusId,
				notibEmisor,
				notibCodiProcediment,
				notibActiu);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto updateIntegracioDistribucio(
			Long entornId,
			Long expedientTipusId,
			boolean actiu,
			String codiProcediment,
			String codiAssumpte,
			boolean procesAuto,
			boolean sistra,
			Boolean presencial,
			boolean enviarCorreuAnotacions) {
		return delegateService.updateIntegracioDistribucio(entornId, expedientTipusId, actiu, codiProcediment, codiAssumpte, procesAuto, sistra, presencial, enviarCorreuAnotacions);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findPerDistribucio(String codiProcediment, String codiAssumpte) {
		return delegateService.findPerDistribucio(codiProcediment, codiAssumpte);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientTipusDto findPerDistribucioValidacio(String codiProcediment, String codiAssumpte) {
		return delegateService.findPerDistribucioValidacio(codiProcediment, codiAssumpte);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusEstadisticaDto> findEstadisticaByFiltre(
			Integer anyInicial,
			Integer anyFinal,
			Long entornId,
			Long expedientTipusId,
			Boolean anulats,
			String numero,
			String titol,
			EstatTipusDto estatTipus,
			Long estatId,
			Boolean aturat,
			Boolean comprovarPermisos) {
		return delegateService.findEstadisticaByFiltre(
				anyInicial,
				anyFinal,
				entornId,
				expedientTipusId,
				anulats,
				numero,
				titol,
				estatTipus,
				estatId,
				aturat,
				comprovarPermisos);
	}

	@Override
	public PaginaDto<ExpedientTipusDto> findTipologiesByFiltrePaginat(
			Long entornId,
			ExpedientTipusFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findTipologiesByFiltrePaginat(entornId, filtreDto, paginacioParams);

	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getManualAjuda(Long expedientTipusId) {
		return delegateService.getManualAjuda(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PermisDto> permisFindAllByExpedientTipusProcedimentComu(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		return delegateService.permisFindAllByExpedientTipusProcedimentComu(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean tePermisLecturaSobreUnitatOrganitzativaOrParents(Long expedientId, String unitatOrganitzativaCodi)
			throws NoTrobatException, PermisDenegatException {
		return delegateService.tePermisLecturaSobreUnitatOrganitzativaOrParents(expedientId, unitatOrganitzativaCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findAmbEntornPermisAdmin(Long entornId) throws NoTrobatException {
		return delegateService.findAmbEntornPermisAdmin(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesDto> definicioFindDefinicionsProcDarreraVersio(ExpedientTipusDto expedientTipus,
			EntornDto entornActual) {
		return delegateService.definicioFindDefinicionsProcDarreraVersio(expedientTipus, entornActual);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findAmbCodiPerValidarRepeticioTotsEntorns(String codi) throws NoTrobatException {
		return delegateService.findAmbCodiPerValidarRepeticioTotsEntorns(codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean arxiuCheckSerieDocumental(
			String serieDocumental,
			String organ,
			String clasificacio) throws SistemaExternException {
		return delegateService.arxiuCheckSerieDocumental(serieDocumental, organ, clasificacio);
	}

}
