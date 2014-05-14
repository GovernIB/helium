/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.core.model.dao.AreaDao;
import net.conselldemallorca.helium.core.model.dao.AreaTipusDao;
import net.conselldemallorca.helium.core.model.dao.CarrecDao;
import net.conselldemallorca.helium.core.model.dao.DominiDao;
import net.conselldemallorca.helium.core.model.dao.EntornDao;
import net.conselldemallorca.helium.core.model.dao.EnumeracioDao;
import net.conselldemallorca.helium.core.model.dao.EnumeracioValorsDao;
import net.conselldemallorca.helium.core.model.dao.EstatDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientTipusDao;
import net.conselldemallorca.helium.core.model.dao.MapeigSistraDao;
import net.conselldemallorca.helium.core.model.exception.NotFoundException;
import net.conselldemallorca.helium.core.model.exportacio.AreaExportacio;
import net.conselldemallorca.helium.core.model.exportacio.AreaTipusExportacio;
import net.conselldemallorca.helium.core.model.exportacio.CarrecExportacio;
import net.conselldemallorca.helium.core.model.exportacio.DominiExportacio;
import net.conselldemallorca.helium.core.model.exportacio.EntornExportacio;
import net.conselldemallorca.helium.core.model.exportacio.EnumeracioExportacio;
import net.conselldemallorca.helium.core.model.exportacio.EstatExportacio;
import net.conselldemallorca.helium.core.model.exportacio.ExpedientTipusExportacio;
import net.conselldemallorca.helium.core.model.exportacio.MapeigSistraExportacio;
import net.conselldemallorca.helium.core.model.hibernate.Area;
import net.conselldemallorca.helium.core.model.hibernate.AreaTipus;
import net.conselldemallorca.helium.core.model.hibernate.Carrec;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusDomini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;
import net.conselldemallorca.helium.core.model.hibernate.Persona;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar els entorns
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class EntornService {

	private EntornDao entornDao;
	private AreaTipusDao areaTipusDao;
	private AreaDao areaDao;
	private CarrecDao carrecDao;
	private DominiDao dominiDao;
	private EnumeracioDao enumeracioDao;
	private EnumeracioValorsDao enumeracioValorsDao;
	private ExpedientTipusDao expedientTipusDao;
	private EstatDao estatDao;
	private MapeigSistraDao mapeigSistraDao;
	private MessageSource messageSource;

	@Secured({"ROLE_ADMIN", "ROLE_USER", "AFTER_ACL_READ"})
	public Entorn getById(Long id) {
		return entornDao.getById(id, false);
	}
	@Secured({"ROLE_ADMIN", "AFTER_ACL_READ"})
	@CacheEvict(value = "entornsUsuariActual", allEntries=true)
	public Entorn create(Entorn entity) {
		Entorn saved = entornDao.saveOrUpdate(entity);
		return saved;
	}
	@Secured({"ROLE_ADMIN", "ROLE_USER", "AFTER_ACL_READ"})
	@CacheEvict(value = "entornsUsuariActual", allEntries=true)
	public Entorn update(Entorn entity) {
		return entornDao.merge(entity);
	}
	@Secured({"ROLE_ADMIN"})
	@CacheEvict(value = "entornsUsuariActual", allEntries=true)
	public void delete(Long id) {
		Entorn vell = getById(id);
		if (vell != null) {
			entornDao.delete(id);
		}
	}
	@Secured({"ROLE_ADMIN", "AFTER_ACL_COLLECTION_READ"})
	public List<Entorn> findAll() {
		return entornDao.findAll();
	}
	@Secured({"ROLE_ADMIN"})
	public int countAll() {
		return entornDao.findAll().size();
	}
	@Secured({"ROLE_ADMIN", "AFTER_ACL_COLLECTION_READ"})
	public List<Entorn> findPagedAndOrderedAll(
			String sort,
			boolean asc,
			int firstRow,
			int maxResults) {
		return entornDao.findPagedAndOrderedAll(
				new String[] {sort},
				asc,
				firstRow,
				maxResults);
	}

	public Entorn findAmbCodi(String codi) {
		return entornDao.findAmbCodi(codi);
	}
	@Secured({"ROLE_ADMIN"})
	public List<Persona> findMembresEntorn(Long entornId) {
		return entornDao.findMembresEntorn(entornId);
	}

	
	public List<Persona> findTotsMembresEntorn(Long entornId) {
		return entornDao.findTotsMembresEntorn(entornId);
	}
	
	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public List<Entorn> findActius() {
		return entornDao.findActius();
	}


	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public EntornExportacio exportar(Long entornId) {
		EntornExportacio entornExportacio = new EntornExportacio();
		// Afegeix els tipus d'àrea
		List<AreaTipus> areesTipus = areaTipusDao.findAmbEntorn(entornId);
		List<AreaTipusExportacio> areesTipusDto = new ArrayList<AreaTipusExportacio>();
		for (AreaTipus areaTipus: areesTipus) {
			AreaTipusExportacio dto = new AreaTipusExportacio(
					areaTipus.getCodi(),
					areaTipus.getNom());
			dto.setDescripcio(areaTipus.getDescripcio());
			areesTipusDto.add(dto);
		}
		entornExportacio.setAreesTipus(areesTipusDto);
		// Afegeix les àrees
		List<Area> arees = areaDao.findAmbEntorn(entornId);
		List<AreaExportacio> areesDto = new ArrayList<AreaExportacio>();
		for (Area area: arees) {
			AreaExportacio dto = new AreaExportacio(
					area.getCodi(),
					area.getNom(),
					area.getTipus().getCodi());
			dto.setDescripcio(area.getDescripcio());
			if (area.getPare() != null)
				dto.setPare(area.getPare().getCodi());
			areesDto.add(dto);
		}
		entornExportacio.setArees(areesDto);
		// Afegeix els carrecs
		List<Carrec> carrecs = carrecDao.findAmbEntorn(entornId);
		List<CarrecExportacio> carrecsDto = new ArrayList<CarrecExportacio>();
		for (Carrec carrec: carrecs) {
			CarrecExportacio dto = new CarrecExportacio(
					carrec.getCodi(),
					carrec.getNomHome(),
					carrec.getNomDona(),
					carrec.getTractamentHome(),
					carrec.getTractamentDona());
			dto.setDescripcio(carrec.getDescripcio());
			//System.out.println(">>> Area pel carrec " + carrec.getCodi() + ": " + carrec.getArea().getCodi());
			dto.setAreaCodi(carrec.getArea().getCodi());
			carrecsDto.add(dto);
		}
		entornExportacio.setCarrecs(carrecsDto);
		// Afegeix els dominis
		List<Domini> dominis = dominiDao.findAmbEntornITipusExpONull(entornId, null);
		List<DominiExportacio> dominisDto = new ArrayList<DominiExportacio>();
		for (Domini domini: dominis) {
			DominiExportacio dto = new DominiExportacio(
					domini.getCodi(),
					domini.getNom(),
					domini.getTipus().toString());
			dto.setDescripcio(domini.getDescripcio());
			dto.setUrl(domini.getUrl());
			dto.setJndiDatasource(domini.getJndiDatasource());
			dto.setSql(domini.getSql());
			dto.setCacheSegons(domini.getCacheSegons());
			dominisDto.add(dto);
		}
		entornExportacio.setDominis(dominisDto);
		// Afegeix les enumeracions
		List<Enumeracio> enumeracions = enumeracioDao.findAmbEntornSenseTipusExp(entornId);
		List<EnumeracioExportacio> enumeracionsDto = new ArrayList<EnumeracioExportacio>();
		for (Enumeracio enumeracio: enumeracions) {
			EnumeracioExportacio dto = new EnumeracioExportacio(
					enumeracio.getCodi(),
					enumeracio.getNom(),
					enumeracioValorsDao.findAmbEnumeracioOrdenat(enumeracio.getId()));
			enumeracionsDto.add(dto);
		}
		entornExportacio.setEnumeracions(enumeracionsDto);
		// Afegeix els tipus d'expedient
		List<ExpedientTipus> expedientsTipus = expedientTipusDao.findAmbEntorn(entornId);
		List<ExpedientTipusExportacio> expedientsTipusDto = new ArrayList<ExpedientTipusExportacio>();
		for (ExpedientTipus expedientTipus: expedientsTipus) {
			ExpedientTipusExportacio dto = new ExpedientTipusExportacio(
					expedientTipus.getCodi(),
					expedientTipus.getNom());
			dto.setTeNumero(expedientTipus.getTeNumero());
			dto.setTeTitol(expedientTipus.getTeTitol());
			dto.setDemanaNumero(expedientTipus.getDemanaNumero());
			dto.setDemanaTitol(expedientTipus.getDemanaTitol());
			dto.setExpressioNumero(expedientTipus.getExpressioNumero());
			dto.setReiniciarCadaAny(expedientTipus.isReiniciarCadaAny());
			dto.setSistraTramitCodi(expedientTipus.getSistraTramitCodi());
			/*dto.setSistraTramitMapeigCamps(expedientTipus.getSistraTramitMapeigCamps());
			dto.setSistraTramitMapeigDocuments(expedientTipus.getSistraTramitMapeigDocuments());
			dto.setSistraTramitMapeigAdjunts(expedientTipus.getSistraTramitMapeigAdjunts());*/
			dto.setFormextUrl(expedientTipus.getFormextUrl());
			dto.setFormextUsuari(expedientTipus.getFormextUsuari());
			dto.setFormextContrasenya(expedientTipus.getFormextContrasenya());
			List<EstatExportacio> estats = new ArrayList<EstatExportacio>();
			for (Estat estat: expedientTipus.getEstats()) {
				estats.add(new EstatExportacio(estat.getCodi(), estat.getNom(), estat.getOrdre()));
			}
			dto.setEstats(estats);
			List<MapeigSistraExportacio> mapeigs = new ArrayList<MapeigSistraExportacio>();
			for (MapeigSistra mapeig : expedientTipus.getMapeigSistras()){
				mapeigs.add(new MapeigSistraExportacio(mapeig.getCodiHelium(), mapeig.getCodiSistra(), mapeig.getTipus()));
			}
			dto.setMapeigSistras(mapeigs);
			List<DominiExportacio> dominisExp = new ArrayList<DominiExportacio>();
			for (Domini domini : expedientTipus.getDominis()){
				DominiExportacio dtoExp = new DominiExportacio(
						domini.getCodi(),
						domini.getNom(),
						domini.getTipus().toString());
				dtoExp.setDescripcio(domini.getDescripcio());
				dtoExp.setUrl(domini.getUrl());
				dtoExp.setJndiDatasource(domini.getJndiDatasource());
				dtoExp.setSql(domini.getSql());
				dtoExp.setCacheSegons(domini.getCacheSegons());
				dominisExp.add(dtoExp);
			}
			dto.setDominis(dominisExp); 
			List<EnumeracioExportacio> enumeracionsExp = new ArrayList<EnumeracioExportacio>();
			for (Enumeracio enumeracio: expedientTipus.getEnumeracions()) {
				EnumeracioExportacio dtoExp = new EnumeracioExportacio(
						enumeracio.getCodi(),
						enumeracio.getNom(),
						enumeracioValorsDao.findAmbEnumeracioOrdenat(enumeracio.getId()));
				enumeracionsExp.add(dtoExp);
			}
			dto.setEnumeracions(enumeracionsExp);
			
			expedientsTipusDto.add(dto);
		}
		entornExportacio.setExpedientsTipus(expedientsTipusDto);
		return entornExportacio;
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public void importar(Long entornId, EntornExportacio exportacio) {
		Entorn entorn = entornDao.getById(entornId, false);
		if (entorn == null)
			throw new NotFoundException( getMessage("error.entornService.entornNoTrobat", new Object[]{entornId}) );
		// Crea els tipus d'area
		Map<String, AreaTipus> areesTipus = new HashMap<String, AreaTipus>();
		for (AreaTipusExportacio areaTipus: exportacio.getAreesTipus()) {
			AreaTipus nova = areaTipusDao.findAmbEntornICodi(
					entornId,
					areaTipus.getCodi());
			if (nova == null) {
				nova = new AreaTipus(
						areaTipus.getCodi(),
						areaTipus.getNom(),
						entorn);
			} else {
				nova.setNom(areaTipus.getNom());
			}
			nova.setDescripcio(areaTipus.getDescripcio());
			areaTipusDao.saveOrUpdate(nova);
			areesTipus.put(areaTipus.getCodi(), nova);
		}
		// Crea les àrees
		Map<String, Area> arees = new HashMap<String, Area>();
		for (AreaExportacio area: exportacio.getArees()) {
			Area nova = areaDao.findAmbEntornICodi(
					entornId,
					area.getCodi());
			if (nova == null) {
				nova = new Area(
						area.getCodi(),
						area.getNom(),
						entorn);
			} else {
				nova.setNom(area.getNom());
			}
			nova.setDescripcio(area.getDescripcio());
			nova.setTipus(areesTipus.get(area.getTipus()));
			areaDao.saveOrUpdate(nova);
			arees.put(area.getCodi(), nova);
		}
		// Actualitza les arees pare
		for (AreaExportacio area: exportacio.getArees()) {
			Area nova = arees.get(area.getCodi());
			if (area.getPare() != null) {
				nova.setPare(arees.get(area.getPare()));
			} else {
				nova.setPare(null);
			}
		}
		// Crea els càrrecs
		for (CarrecExportacio carrec: exportacio.getCarrecs()) {
			Carrec nou = carrecDao.findAmbEntornICodi(
					entornId,
					carrec.getCodi());
			if (nou == null) {
				nou = new Carrec(
						carrec.getCodi(),
						carrec.getNomHome(),
						carrec.getNomDona(),
						carrec.getTractamentHome(),
						carrec.getTractamentDona(),
						entorn);
			} else {
				nou.setNomHome(carrec.getNomHome());
				nou.setNomDona(carrec.getNomDona());
				nou.setTractamentHome(carrec.getTractamentHome());
				nou.setTractamentDona(carrec.getTractamentDona());
			}
			nou.setDescripcio(carrec.getDescripcio());
			nou.setArea(arees.get(carrec.getAreaCodi()));
			carrecDao.saveOrUpdate(nou);
		}
		// Crea els dominis
		for (DominiExportacio domini: exportacio.getDominis()) {
			Domini nou = dominiDao.findAmbEntornICodi(
					entornId,
					domini.getCodi());
			if (nou == null) {
				nou = new Domini(
						domini.getCodi(),
						domini.getNom(),
						entorn);
			} else {
				nou.setNom(domini.getNom());
			}
			nou.setDescripcio(domini.getDescripcio());
			nou.setTipus(TipusDomini.valueOf(domini.getTipus()));
			nou.setCacheSegons(domini.getCacheSegons());
			nou.setSql(domini.getSql());
			nou.setJndiDatasource(domini.getJndiDatasource());
			nou.setUrl(domini.getUrl());
			dominiDao.saveOrUpdate(nou);
		}
		// Crea les enumeracions
		for (EnumeracioExportacio enumeracio: exportacio.getEnumeracions()) {
			Enumeracio nova = enumeracioDao.findAmbEntornSenseTipusExpICodi(
					entornId,
					enumeracio.getCodi());
			if (nova == null) {
				nova = new Enumeracio(
						entorn,
						enumeracio.getCodi(),
						enumeracio.getNom());
			} else {
				nova.setNom(enumeracio.getNom());
			}
			
			for (EnumeracioValors enumValors : enumeracio.getValors()) {
				EnumeracioValors novaEnumValors = new EnumeracioValors();
				novaEnumValors.setCodi(enumValors.getCodi());
				novaEnumValors.setNom(enumValors.getNom());
				novaEnumValors.setEnumeracio(nova);
				nova.addEnumeracioValors(novaEnumValors);
			}
			
			enumeracioDao.saveOrUpdate(nova);
		}
		// Crea els tipus d'expedient
		for (ExpedientTipusExportacio expedientTipus: exportacio.getExpedientsTipus()) {
			ExpedientTipus nou = expedientTipusDao.findAmbEntornICodi(
					entornId, 
					expedientTipus.getCodi());
			if (nou == null) {
				nou = new ExpedientTipus(
						expedientTipus.getCodi(),
						expedientTipus.getNom(),
						entorn);
			} else {
				nou.setNom(expedientTipus.getNom());
			}
			nou.setTeNumero(expedientTipus.getTeNumero());
			nou.setTeTitol(expedientTipus.getTeTitol());
			nou.setDemanaNumero(expedientTipus.getDemanaNumero());
			nou.setDemanaTitol(expedientTipus.getDemanaTitol());
			nou.setExpressioNumero(expedientTipus.getExpressioNumero());
			nou.setReiniciarCadaAny(expedientTipus.isReiniciarCadaAny());
			nou.setSistraTramitCodi(expedientTipus.getSistraTramitCodi());
			/*nou.setSistraTramitMapeigCamps(expedientTipus.getSistraTramitMapeigCamps());
			nou.setSistraTramitMapeigDocuments(expedientTipus.getSistraTramitMapeigDocuments());
			nou.setSistraTramitMapeigAdjunts(expedientTipus.getSistraTramitMapeigAdjunts());*/
			nou.setFormextUrl(expedientTipus.getFormextUrl());
			nou.setFormextUsuari(expedientTipus.getFormextUsuari());
			nou.setFormextContrasenya(expedientTipus.getFormextContrasenya());
			expedientTipusDao.saveOrUpdate(nou);
			// Crea els estats del tipus d'expedient.
			for (EstatExportacio estat: expedientTipus.getEstats()) {
				Estat enou = null;
				if (nou.getId() != null) {
					enou = estatDao.findAmbExpedientTipusICodi(
						nou.getId(),
						estat.getCodi());
				}
				if (enou == null) {
					enou = new Estat(
							nou,
							estat.getCodi(),
							estat.getNom());
				} else {
					enou.setNom(estat.getNom());
				}
				enou.setOrdre(estat.getOrdre());
				estatDao.saveOrUpdate(enou);
			}
			// Crea els mapejos del tipus d'expedient.
			for (MapeigSistraExportacio mapeig: expedientTipus.getMapeigSistras()) {
				MapeigSistra mnou = null;
				if (nou.getId() != null) {
					mnou = mapeigSistraDao.findAmbExpedientTipusICodi(
						nou.getId(),
						mapeig.getCodiHelium());
				}
				if (mnou == null) {
					mnou = new MapeigSistra(
							nou,
							mapeig.getCodiHelium(),
							mapeig.getCodiSistra(),
							mapeig.getTipus());
				} else {
					mnou.setCodiSistra(mapeig.getCodiSistra());
					mnou.setTipus(mapeig.getTipus());
				}
				mapeigSistraDao.saveOrUpdate(mnou);
			}
			// Crea els dominis del tipus d'expedient.
			for (DominiExportacio domini: expedientTipus.getDominis()) {
				Domini dnou = dominiDao.findAmbEntornICodi(
						entornId,
						domini.getCodi());
				if (dnou == null) {
					dnou = new Domini(
							domini.getCodi(),
							domini.getNom(),
							entorn);
				} else {
					dnou.setNom(domini.getNom());
				}
				dnou.setExpedientTipus(nou);
				dnou.setDescripcio(domini.getDescripcio());
				dnou.setTipus(TipusDomini.valueOf(domini.getTipus()));
				dnou.setCacheSegons(domini.getCacheSegons());
				dnou.setSql(domini.getSql());
				dnou.setJndiDatasource(domini.getJndiDatasource());
				dnou.setUrl(domini.getUrl());
				dominiDao.saveOrUpdate(dnou);
			}
			// Crea les enumeracions del tipus d'expedient.
			for (EnumeracioExportacio enumeracio: expedientTipus.getEnumeracions()) {
				Enumeracio nova = enumeracioDao.findAmbEntornSenseTipusExpICodi(
						entornId,
						enumeracio.getCodi());
				if (nova == null) {
					nova = new Enumeracio(
							entorn,
							enumeracio.getCodi(),
							enumeracio.getNom());
				} else {
					nova.setNom(enumeracio.getNom());
				}
				nova.setExpedientTipus(nou);
				
				for (EnumeracioValors enumValors : enumeracio.getValors()) {
					EnumeracioValors novaEnumValors = new EnumeracioValors();
					novaEnumValors.setCodi(enumValors.getCodi());
					novaEnumValors.setNom(enumValors.getNom());
					novaEnumValors.setEnumeracio(nova);
					nova.addEnumeracioValors(novaEnumValors);
				}
				
				enumeracioDao.saveOrUpdate(nova);
			}
		}
	}



	@Autowired
	public void setEntornDao(EntornDao entornDao) {
		this.entornDao = entornDao;
	}
	@Autowired
	public void setAreaTipusDao(AreaTipusDao areaTipusDao) {
		this.areaTipusDao = areaTipusDao;
	}
	@Autowired
	public void setAreaDao(AreaDao areaDao) {
		this.areaDao = areaDao;
	}
	@Autowired
	public void setCarrecDao(CarrecDao carrecDao) {
		this.carrecDao = carrecDao;
	}
	@Autowired
	public void setDominiDao(DominiDao dominiDao) {
		this.dominiDao = dominiDao;
	}
	@Autowired
	public void setEnumeracioDao(EnumeracioDao enumeracioDao) {
		this.enumeracioDao = enumeracioDao;
	}
	@Autowired
	public void setEnumeracioValorsDao(EnumeracioValorsDao enumeracioValorsDao) {
		this.enumeracioValorsDao = enumeracioValorsDao;
	}
	@Autowired
	public void setExpedientTipusDao(ExpedientTipusDao expedientTipusDao) {
		this.expedientTipusDao = expedientTipusDao;
	}
	@Autowired
	public void setEstatDao(EstatDao estatDao) {
		this.estatDao = estatDao;
	}
	@Autowired
	public void setMapeigSistraDao(MapeigSistraDao mapeigSistraDao) {
		this.mapeigSistraDao = mapeigSistraDao;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	protected String getMessage(String key, Object[] vars) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}

	protected String getMessage(String key) {
		return getMessage(key, null);
	}

}
