/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.EntornService;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;

/**
 * Implementació dels mètodes de EntornService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("entornServiceV3")
public class EntornServiceImpl implements EntornService {

	@Resource
	private EntornRepository entornRepository;

	@Autowired
	private UsuariActualHelper usuariActualHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource(name = "permisosHelperV3") 
	private PermisosHelper permisosHelper;
	@Resource 
	private EntornHelper entornHelper;



	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public EntornDto create(
			EntornDto entorn) {
		logger.debug("Creant nou entorn (entorn=" + entorn + ")");
		Entorn entity = new Entorn();
		entity.setId(entorn.getId());
		entity.setCodi(entorn.getCodi());
		entity.setNom(entorn.getNom());
		entity.setDescripcio(entorn.getDescripcio());
		entity.setActiu(true);
		return conversioTipusHelper.convertir(
				entornRepository.save(entity),
				EntornDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public EntornDto update(
			EntornDto entorn) {
		logger.debug("Modificant entorn (entorn=" + entorn + ")");
		Entorn entity = comprovarEntorn(entorn.getId());
		entity.setNom(entorn.getNom());
		entity.setDescripcio(entorn.getDescripcio());
		return conversioTipusHelper.convertir(
				entity,
				EntornDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(
			Long entornId) {
		logger.debug("Esborrant entorn (entornId=" + entornId + ")");
		Entorn entorn = comprovarEntorn(entornId);
		entornRepository.delete(entorn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<EntornDto> findPerDatatable(
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant entorns per la datatable (" +
				"filtre=" + filtre + ", " +
				"paginacioParams=" + paginacioParams + ")");
		PaginaDto<EntornDto> pagina = paginacioHelper.toPaginaDto(
				entornRepository.findByFiltrePaginat(
						filtre == null || "".equals(filtre),
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				EntornDto.class);
		// Afegeix el contador de permisos
		List<Long> ids = new ArrayList<Long>();
		for (EntornDto dto: pagina.getContingut()) {
			ids.add(dto.getId());
		}
		Map<Long, List<PermisDto>> permisos = permisosHelper.findPermisos(
				ids,
				Entorn.class);
		for (EntornDto dto: pagina.getContingut()) {
			if (permisos.get(dto.getId()) != null) {
				dto.setPermisCount(permisos.get(dto.getId()).size());
			}
		}
		return pagina;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public EntornDto findOne(Long entornId) {
		logger.debug("Consultant entorn amb id (entornId=" + entornId + ")");
		Entorn entorn = comprovarEntorn(entornId);
		return conversioTipusHelper.convertir(
				entorn,
				EntornDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public EntornDto findAmbCodi(String entornCodi) {
		logger.debug("Consultant entorn amb codi (entornCodi=" + entornCodi + ")");
		return conversioTipusHelper.convertir(
				entornRepository.findByCodi(entornCodi),
				EntornDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<EntornDto> findActiusAmbPermisAcces() {
		String usuariActual = usuariActualHelper.getUsuariActual();
		logger.debug("Consulta d'entorns amb accés per a l'usuari actual ("
				+ "usuariActual=" + usuariActual + ")");
		return usuariActualHelper.findEntornsActiusPermesos(usuariActual);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<EntornDto> findActiusAll() {
		logger.debug("Consulta dels entorns actius");
		return conversioTipusHelper.convertirList(
				entornRepository.findByActiuTrue(),
				EntornDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void permisUpdate(
			Long entornId,
			PermisDto permis) throws NoTrobatException {
		logger.debug(
				"Creant permis per a l'entorn(" +
				"entornId=" + entornId + ", " +
				"permis=" + permis + ")");
		comprovarEntorn(entornId);
		permisosHelper.updatePermis(
				entornId,
				Entorn.class,
				permis);
   		usuariActualHelper.netejarCacheUsuari(permis.getPrincipalNom());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void permisDelete(
			Long entornId,
			Long permisId) throws NoTrobatException {
		logger.debug(
				"Esborrant permis de l'entorn(" +
				"entornId=" + entornId + ", " +
				"permisId=" + permisId + ")");
		comprovarEntorn(entornId);
		PermisDto permisDto = permisFindById(entornId, permisId);
		permisosHelper.deletePermis(
				entornId,
				Entorn.class,
				permisId);
		usuariActualHelper.netejarCacheUsuari(permisDto.getPrincipalNom());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PermisDto> permisFindAll(
			Long entornId) {
		logger.debug(
				"Consultant permisos de l'entorn (" +
				"entornId=" + entornId + ")");
		comprovarEntorn(entornId);
		return permisosHelper.findPermisos(
				entornId,
				Entorn.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PermisDto permisFindById(
			Long entornId,
			Long permisId) {
		logger.debug(
				"Consultant un permis donat el seu id (" +
				"entornId=" + entornId + ", " +
				"permisId=" + permisId + ")");
		comprovarEntorn(entornId);
		List<PermisDto> permisos = permisosHelper.findPermisos(
				entornId,
				Entorn.class);
		for (PermisDto permis: permisos) {
			if (permis.getId().equals(permisId)) {
				return permis;
			}
		}
		throw new NoTrobatException(PermisDto.class, permisId);
	}



	private Entorn comprovarEntorn(
			Long entornId) {
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null) {
			throw new NoTrobatException(
					Entorn.class,
					entornId);
		}
		return entorn;
	}

	private static final Logger logger = LoggerFactory.getLogger(EntornServiceImpl.class);

}
