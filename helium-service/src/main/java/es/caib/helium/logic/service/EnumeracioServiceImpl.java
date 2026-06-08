/**
 *
 */
package es.caib.helium.logic.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import es.caib.helium.logic.helper.*;
import es.caib.helium.persistence.entity.*;
import es.caib.helium.persistence.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.commons.dto.EnumeracioDto;
import es.caib.helium.commons.dto.ExpedientTipusEnumeracioValorDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.commons.exception.ValidacioException;
import es.caib.helium.commons.utils.MessageHelper;
import es.caib.helium.logic.intf.service.EnumeracioService;

/**
 * Implementació del servei per a gestionar enumeracions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class EnumeracioServiceImpl implements EnumeracioService {

	@Resource
	private EntornHelper entornHelper;
	@Resource
	private EntornRepository entornRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private EnumeracioRepository enumeracioRepository;
	@Resource
	private EnumeracioValorsRepository enumeracioValorsRepository;
	@Resource
	private CampRepository campRepository;

	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private ExpedientDadaHelper expedientDadaHelper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<EnumeracioDto> findPerDatatable(
			Long entornId,
			Long expedientTipusId,
			boolean incloureGlobals,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		logger.debug(
				"Consultant les enumeracions per datatable (" +
				"entornId=" + expedientTipusId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"incloureGlobals=" + incloureGlobals + ", " +
				"filtre=" + filtre + ")");

		ExpedientTipus expedientTipus = expedientTipusId != null? expedientTipusHelper.getExpedientTipusComprovantPermisDissenyDelegat(expedientTipusId) : null;

		// Determina si hi ha herència
		boolean ambHerencia = HerenciaHelper.ambHerencia(expedientTipus);

		Page<Enumeracio> page = enumeracioRepository.findByFiltrePaginat(
				entornId,
				expedientTipusId == null,
				expedientTipusId,
				incloureGlobals,
				filtre == null || "".equals(filtre),
				filtre,
				ambHerencia,
				paginacioHelper.toSpringDataPageable(paginacioParams));

		PaginaDto<EnumeracioDto> pagina = paginacioHelper.toPaginaDto(page, EnumeracioDto.class);

		// Llista d'enumeracions de la pàgina per consultar els seus valors
		Set<Long> enumeracionsIds = new HashSet<Long>(); // per guardar els ids de les enumeracions a mostrar
		// Llista d'heretats
		Set<Long> heretatsIds = new HashSet<Long>();
		// Llistat d'elements sobreescrits
		Set<String> sobreescritsCodis = new HashSet<String>();
		for (Enumeracio e : page.getContent()) {
			enumeracionsIds.add(e.getId());
			if (ambHerencia
					&& !expedientTipusId.equals(e.getExpedientTipus().getId()))
				heretatsIds.add(e.getId());
		}
		if (enumeracionsIds.isEmpty())
			enumeracionsIds.add(0L);
		if (ambHerencia) {
			for (Enumeracio e : enumeracioRepository.findSobreescrits(expedientTipus.getId()))
				sobreescritsCodis.add(e.getCodi());
		}
		if (pagina!=null) {
			// Consulta els valors per enumeració dins d'un Map<enumeracioId, valors count>
			Map<Long, Long> enumeracioValorsCountMap = new HashMap<Long, Long>();
			for(Object[] o : enumeracioValorsRepository.countValors(enumeracionsIds))
				enumeracioValorsCountMap.put((Long) o[0], (Long) o[1]);
			// Completa l'informació del dto
			for (EnumeracioDto dto : pagina.getContingut()) {
				// Comptador de valors
				dto.setNumValors(enumeracioValorsCountMap.containsKey(dto.getId()) ? enumeracioValorsCountMap.get(dto.getId()).intValue() : 0);
				if(ambHerencia) {
					// Sobreescriu
					if (sobreescritsCodis.contains(dto.getCodi()))
						dto.setSobreescriu(true);
					// Heretat
					if (heretatsIds.contains(dto.getId()) && ! dto.isSobreescriu())
						dto.setHeretat(true);
				}
			}
		}
		return pagina;
	}

	@Override
	@Transactional
	public EnumeracioDto create(
			Long entornId,
			Long expedientTipusId,
			EnumeracioDto enumeracio)
			throws PermisDenegatException {

		logger.debug(
				"Creant nova enumeracio (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"entornId =" + entornId + ", " +
				"enumeracio=" + enumeracio + ")");

		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				expedientTipusId == null);
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null)
			expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);

		Enumeracio entity = new Enumeracio();
		entity.setCodi(enumeracio.getCodi());
		entity.setNom(enumeracio.getNom());

		entity.setExpedientTipus(expedientTipus);
		entity.setEntorn(entorn);

		return conversioTipusHelper.convertir(
				enumeracioRepository.save(entity),
				EnumeracioDto.class);
	}

	@Override
	@Transactional
	public EnumeracioDto findAmbCodi(
			Long entornId,
			Long expedientTipusId,
			String codi) {
		EnumeracioDto ret = null;
		logger.debug(
				"Consultant l'enumeració per codi (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"codi = " + codi + ")");
		Enumeracio enumeracio;
		if (expedientTipusId != null)
			enumeracio = enumeracioRepository.findByExpedientTipusAndCodi(
					expedientTipusRepository.findById(expedientTipusId).orElse(null),
					codi);
		else
			enumeracio = enumeracioRepository.findByEntornAndCodi(
					entornRepository.findById(entornId).orElse(null),
					codi);
		if (enumeracio != null)
			ret = conversioTipusHelper.convertir(
					enumeracio,
					EnumeracioDto.class);
		return ret;
	}

	@Override
	@Transactional
	public void delete(Long enumeracioId) throws NoTrobatException, PermisDenegatException, ValidacioException {

		logger.debug(
				"Esborrant l'enumeració (" +
				"enumeracioId=" + enumeracioId +  ")");

		Enumeracio entity = enumeracioRepository.findById(enumeracioId).orElse(null);

		if (entity.getExpedientTipus() != null)
			expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(entity.getExpedientTipus().getId());
		else
			entornHelper.getEntornComprovantPermisos(entity.getEntorn().getId(), true, true);

		// Si l'enumerat esta associat a alguna variable no es pot eliminar
		if (entity.getCamps()!=null && entity.getCamps().size()>0) {
			throw new ValidacioException(messageHelper.getMessage("expedient.tipus.enumeracio.controller.eliminat.us"));
		}

		List<EnumeracioValors> valors = enumeracioValorsRepository.findByEnumeracioOrdenat(entity.getId());
		if (valors!=null) {
			for (int o=0; o<valors.size(); o++) {
				enumeracioValorsRepository.delete(valors.get(o));
			}
		}
		enumeracioValorsRepository.flush();

		enumeracioRepository.delete(entity);
		enumeracioRepository.flush();
	}

	@Override
	@Transactional
	public List<EnumeracioDto> findGlobals(Long entornId) throws NoTrobatException {
		logger.debug(
				"Consultant les enumeracions globals per entorn (" +
				"entornId=" + entornId +  ")");
		return conversioTipusHelper.convertirList(
				enumeracioRepository.findGlobals(entornId),
				EnumeracioDto.class);
	}

	@Override
	@Transactional
	public EnumeracioDto findAmbId(
			Long expedientTipusId,
			Long enumeracioId) throws NoTrobatException {
		logger.debug(
				"Consultant l'enumeracio amb id (" +
				"expedientTipusId=" + expedientTipusId + "," +
				"enumeracioId=" + enumeracioId +  ")");
		ExpedientTipus tipus = expedientTipusId != null?
									expedientTipusRepository.findById(expedientTipusId).orElse(null) : null;
		Enumeracio enumeracio = enumeracioRepository.findById(enumeracioId).orElse(null);
		if (enumeracio == null) {
			throw new NoTrobatException(Enumeracio.class, enumeracioId);
		}
		EnumeracioDto dto = conversioTipusHelper.convertir(
				enumeracio,
				EnumeracioDto.class);
		// Herencia
		if (tipus != null && tipus.getExpedientTipusPare() != null) {
			if (tipus.getExpedientTipusPare().getId().equals(enumeracio.getExpedientTipus().getId()))
				dto.setHeretat(true);
			else
				dto.setSobreescriu(enumeracioRepository.findByEntornAndExpedientTipusAndCodi(
						tipus.getEntorn(),
						tipus.getExpedientTipusPare(),
						enumeracio.getCodi()) != null);
		}
		return dto;
	}

	@Override
	@Transactional
	public EnumeracioDto update(EnumeracioDto enumeracio)
			throws NoTrobatException, PermisDenegatException {

		logger.debug(
				"Modificant l'enumeració existent (" +
				"enumeracio.id=" + enumeracio.getId() + ", " +
				"enumeracio =" + enumeracio + ")");

		Enumeracio entity = enumeracioRepository.findById(enumeracio.getId()).orElse(null);

		if (entity.getExpedientTipus() != null)
			expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(entity.getExpedientTipus().getId());
		else
			entornHelper.getEntornComprovantPermisos(entity.getEntorn().getId(), true, true);

		entity.setCodi(enumeracio.getCodi());
		entity.setNom(enumeracio.getNom());

		return conversioTipusHelper.convertir(
				enumeracioRepository.save(entity),
				EnumeracioDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientTipusEnumeracioValorDto> valorFindPerDatatable(
			Long enumeracioId,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {

		logger.debug(
				"Consultant els valors de enumeracio per datatable (" +
				"enumeracioId=" + enumeracioId + ", " +
				"filtre=" + filtre + ")");

		Page<EnumeracioValors> resultats = enumeracioValorsRepository.findByFiltrePaginat(
				enumeracioId,
				filtre == null || "".equals(filtre),
				filtre,
				paginacioHelper.toSpringDataPageable(paginacioParams));

		PaginaDto<ExpedientTipusEnumeracioValorDto> out = paginacioHelper.toPaginaDto(resultats, ExpedientTipusEnumeracioValorDto.class);

		return out;
	}


	@Override
	@Transactional(readOnly = true)
	public List <ExpedientTipusEnumeracioValorDto> valorsFind(
			Long enumeracioId) throws NoTrobatException {

		logger.debug(
				"Consultant els valors de enumeracio(" +
				"enumeracioId=" + enumeracioId);

		List<EnumeracioValors> resultats = enumeracioValorsRepository.findByEnumeracioIdOrderByOrdreAsc(
				enumeracioId);

		return conversioTipusHelper.convertirList(
				resultats,
				ExpedientTipusEnumeracioValorDto.class);
	}





	@Override
	@Transactional
	public ExpedientTipusEnumeracioValorDto valorsCreate(Long expedientTipusId, Long enumeracioId,
			Long entornId, ExpedientTipusEnumeracioValorDto enumeracio) throws PermisDenegatException {

		logger.debug(
				"Creant nou valor de enumeracio (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"enumeracioId =" + enumeracioId + ", " +
				"entornId =" + entornId + ", " +
				"document=" + enumeracio + ")");

		Enumeracio enumer = enumeracioRepository.findById(enumeracioId).orElse(null);

		//Es llançará un PermisDenegatException si escau
		if (expedientTipusId != null)
			expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
		else
			entornHelper.getEntornComprovantPermisos(entornId, true, true);

		EnumeracioValors entity = new EnumeracioValors();
		entity.setCodi(enumeracio.getCodi());
		entity.setNom(enumeracio.getNom());
		entity.setOrdre(enumeracioValorsRepository.getNextOrdre(enumeracioId));
		entity.setEnumeracio(enumer);

		return conversioTipusHelper.convertir(
				enumeracioValorsRepository.save(entity),
				ExpedientTipusEnumeracioValorDto.class);
	}

	@Override
	@Transactional
	public ExpedientTipusEnumeracioValorDto valorsUpsert(
			Long expedientTipusId,
			Long enumeracioId,
			Long entornId,
			ExpedientTipusEnumeracioValorDto enumeracio) throws PermisDenegatException {
		logger.debug(
				"Creant nou valor de enumeracio (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"enumeracioId =" + enumeracioId + ", " +
				"entornId =" + entornId + ", " +
				"document=" + enumeracio + ")");

		Enumeracio enumer = enumeracioRepository.findById(enumeracioId).orElse(null);

		//Es llançará un PermisDenegatException si escau
		if (expedientTipusId != null)
			expedientTipusHelper.getExpedientTipusComprovantPermisDisseny(expedientTipusId);
		else
			entornHelper.getEntornComprovantPermisos(entornId, true, true);

		EnumeracioValors entity = enumeracioValorsRepository.findByEnumeracioAndCodi(enumer, enumeracio.getCodi());
		if(entity == null) {
			entity = new EnumeracioValors();
			entity.setCodi(enumeracio.getCodi());
			entity.setOrdre(enumeracioValorsRepository.getNextOrdre(enumeracioId));
			entity.setEnumeracio(enumer);
		}
		entity.setNom(enumeracio.getNom());

		return conversioTipusHelper.convertir(
				enumeracioValorsRepository.save(entity),
				ExpedientTipusEnumeracioValorDto.class);
	}

	@Override
	@Transactional
	public void valorDelete(Long valorId) throws NoTrobatException, PermisDenegatException {
		EnumeracioValors valor = enumeracioValorsRepository.findById(valorId).orElse(null);
		if (valor == null)
			throw new NoTrobatException(EnumeracioValors.class, valorId);

		Long enumeracioId = valor.getEnumeracio().getId();
		//Es llançará un PermisDenegatException si escau
		if (valor.getEnumeracio().getExpedientTipus() != null)
			expedientTipusHelper.getExpedientTipusComprovantPermisDissenyDelegat(valor.getEnumeracio().getExpedientTipus().getId());
		else
			entornHelper.getEntornComprovantPermisos(valor.getEnumeracio().getEntorn().getId(), true, true);

		enumeracioValorsRepository.deleteById(valorId);
		enumeracioValorsRepository.flush();
		reordenarValorsEnumeracio(enumeracioId);
	}

	/** Funció per reasignar el valor d'ordre dins dels valors d'una enumeració. */
	private void reordenarValorsEnumeracio(
			Long enumeracioId) {
		List<EnumeracioValors> consultaCamps = enumeracioValorsRepository.findByEnumeracioOrdenat(
				enumeracioId);
		int i = 0;
		for (EnumeracioValors e : consultaCamps) {
			e.setOrdre(i);
			enumeracioValorsRepository.saveAndFlush(e);
			i++;
		}
	}

	@Override
	@Transactional
	public ExpedientTipusEnumeracioValorDto valorFindAmbId(Long valorId) throws NoTrobatException {
		logger.debug(
				"Consultant el valor de l'enumeracio amb id (" +
				"valorId=" + valorId +  ")");
		EnumeracioValors valor = enumeracioValorsRepository.findById(valorId).orElse(null);
		if (valor == null) {
			throw new NoTrobatException(EnumeracioValors.class, valorId);
		}
		return conversioTipusHelper.convertir(
				valor,
				ExpedientTipusEnumeracioValorDto.class);
	}

	@Override
	@Transactional
	public ExpedientTipusEnumeracioValorDto valorUpdate(ExpedientTipusEnumeracioValorDto enumeracioValor)
			throws NoTrobatException, PermisDenegatException {

		logger.debug(
				"Modificant el valor de l'enumeració existent (" +
				"enumeracioValor.id=" + enumeracioValor.getId() + ", " +
				"enumeracioValor =" + enumeracioValor + ")");

		EnumeracioValors entity = enumeracioValorsRepository.findById(enumeracioValor.getId()).orElse(null);
		if (entity == null)
			throw new NoTrobatException(EnumeracioValors.class, enumeracioValor.getId());

		//Es llançará un PermisDenegatException si escau
		Long expedientTipusId = entity.getEnumeracio().getExpedientTipus() != null ? entity.getEnumeracio().getExpedientTipus().getId() : null;
		if (expedientTipusId != null)
			expedientTipusHelper.getExpedientTipusComprovantPermisDissenyDelegat(expedientTipusId);
		else
			entornHelper.getEntornComprovantPermisos(entity.getEnumeracio().getEntorn().getId(), true, true);

		if(entity.getCodi().compareTo(enumeracioValor.getCodi()) != 0) {
			entity.setCodi(enumeracioValor.getCodi());
		}
		entity.setNom(enumeracioValor.getNom());
		entity.setOrdre(enumeracioValor.getOrdre());

		return conversioTipusHelper.convertir(
				enumeracioValorsRepository.save(entity),
				ExpedientTipusEnumeracioValorDto.class);
	}

	@Override
	@Transactional
	public ExpedientTipusEnumeracioValorDto valorFindAmbCodi(Long expedientTipusId, Long enumeracioId,
			String codi) throws NoTrobatException {

		logger.debug(
				"Consultant el valor de l'enumeració per codi (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"enumeracioId=" + enumeracioId + ", " +
				"codi = " + codi + ")");

		Enumeracio enumeracio = enumeracioRepository.findById(enumeracioId).orElse(null);

		return conversioTipusHelper.convertir(
				enumeracioValorsRepository.findByEnumeracioAndCodi(enumeracio, codi),
				ExpedientTipusEnumeracioValorDto.class);
	}

	@Override
	@Transactional
	public boolean valorMoure(Long valorId, int posicio) throws NoTrobatException {
		logger.debug(
				"Moguent el valor de l'enumerat (" +
				"valorId=" + valorId + ", " +
				"posicio=" + posicio + ")");
		boolean ret = false;
		EnumeracioValors camp = enumeracioValorsRepository.findById(valorId).orElse(null);
		if (camp == null)
			throw new NoTrobatException(EnumeracioValors.class, valorId);

		//Es llançará un PermisDenegatException si escau
		if (camp.getEnumeracio().getExpedientTipus() != null)
			expedientTipusHelper.getExpedientTipusComprovantPermisDissenyDelegat(camp.getEnumeracio().getExpedientTipus().getId());
		else
			entornHelper.getEntornComprovantPermisos(camp.getEnumeracio().getEntorn().getId(), true, true);

		if (camp != null && camp.getEnumeracio() != null) {
			List<EnumeracioValors> camps = enumeracioValorsRepository.findByEnumeracioIdOrderByOrdreAsc(camp.getEnumeracio().getId());
			if(posicio != camps.indexOf(camp)) {
				camps.remove(camp);
				camps.add(posicio, camp);
				int i = 0;
				for (EnumeracioValors c : camps) {
					c.setOrdre(i++);
					enumeracioValorsRepository.save(c);
				}
			}
		}
		return ret;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean valorInUse(Long valorId) throws NoTrobatException, PermisDenegatException {
		EnumeracioValors valor = enumeracioValorsRepository.findById(valorId).orElse(null);
		if (valor == null)
			throw new NoTrobatException(EnumeracioValors.class, valorId);
		Long enumeracioId = valor.getEnumeracio().getId();
		for(Camp camp : campRepository.findByEnumeracioId(enumeracioId)) {
			Integer count = expedientDadaHelper.countValueInUse(camp.getExpedientTipus().getId(), camp.getCodi(), valor.getCodi());
			if(count > 0) return true;
		}
		return false;
	}

	private static final Logger logger = LoggerFactory.getLogger(EnumeracioServiceImpl.class);
}
