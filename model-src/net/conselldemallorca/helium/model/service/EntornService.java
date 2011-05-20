/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.model.dao.AreaDao;
import net.conselldemallorca.helium.model.dao.AreaTipusDao;
import net.conselldemallorca.helium.model.dao.CarrecDao;
import net.conselldemallorca.helium.model.dao.DominiDao;
import net.conselldemallorca.helium.model.dao.EntornDao;
import net.conselldemallorca.helium.model.dao.EnumeracioDao;
import net.conselldemallorca.helium.model.dao.EstatDao;
import net.conselldemallorca.helium.model.dao.ExpedientTipusDao;
import net.conselldemallorca.helium.model.exception.NotFoundException;
import net.conselldemallorca.helium.model.exportacio.AreaExportacio;
import net.conselldemallorca.helium.model.exportacio.AreaTipusExportacio;
import net.conselldemallorca.helium.model.exportacio.CarrecExportacio;
import net.conselldemallorca.helium.model.exportacio.DominiExportacio;
import net.conselldemallorca.helium.model.exportacio.EntornExportacio;
import net.conselldemallorca.helium.model.exportacio.EnumeracioExportacio;
import net.conselldemallorca.helium.model.exportacio.EstatExportacio;
import net.conselldemallorca.helium.model.exportacio.ExpedientTipusExportacio;
import net.conselldemallorca.helium.model.hibernate.Area;
import net.conselldemallorca.helium.model.hibernate.AreaTipus;
import net.conselldemallorca.helium.model.hibernate.Carrec;
import net.conselldemallorca.helium.model.hibernate.Domini;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.model.hibernate.Estat;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.hibernate.Persona;
import net.conselldemallorca.helium.model.hibernate.Domini.TipusDomini;
import net.conselldemallorca.helium.security.acl.AclServiceDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar els entorns
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Service
public class EntornService {

	private EntornDao entornDao;
	private AreaTipusDao areaTipusDao;
	private AreaDao areaDao;
	private CarrecDao carrecDao;
	private DominiDao dominiDao;
	private EnumeracioDao enumeracioDao;
	private ExpedientTipusDao expedientTipusDao;
	private EstatDao estatDao;
	private AclServiceDao aclServiceDao;



	@Secured({"ROLE_ADMIN", "ROLE_USER", "AFTER_ACL_READ"})
	public Entorn getById(Long id) {
		return entornDao.getById(id, false);
	}
	@Secured({"ROLE_ADMIN", "AFTER_ACL_READ"})
	public Entorn create(Entorn entity) {
		Entorn saved = entornDao.saveOrUpdate(entity);
		aclServiceDao.createAcl(new ObjectIdentityImpl(Entorn.class, saved.getId()));
		return saved;
	}
	@Secured({"ROLE_ADMIN", "ROLE_USER", "AFTER_ACL_READ"})
	public Entorn update(Entorn entity) {
		return entornDao.merge(entity);
	}
	@Secured({"ROLE_ADMIN"})
	public void delete(Long id) {
		Entorn vell = getById(id);
		if (vell != null) {
			ObjectIdentity oid = new ObjectIdentityImpl(Entorn.class, id);
			aclServiceDao.deleteAcl(oid, true);
			entornDao.delete(id);
		}
	}
	@Secured({"ROLE_ADMIN", "AFTER_ACL_COLLECTION_READ"})
	public List<Entorn> findAll() {
		return entornDao.findAll();
	}
	@Secured({"ROLE_ADMIN"})
	public int countAll() {
		return entornDao.getCountAll();
	}
	@Secured({"ROLE_ADMIN", "AFTER_ACL_COLLECTION_READ"})
	public List<Entorn> findPagedAndOrderedAll(
			String sort,
			boolean asc,
			int firstRow,
			int maxResults) {
		return entornDao.findPagedAndOrderedAll(sort, asc, firstRow, maxResults);
	}

	public Entorn findAmbCodi(String codi) {
		return entornDao.findAmbCodi(codi);
	}
	@Secured({"ROLE_ADMIN"})
	public List<Persona> findMembresEntorn(Long entornId) {
		return entornDao.findMembresEntorn(entornId);
	}

	/*@Secured({"ROLE_ADMIN"})
	public void afegirMembre(Long entornId, Long personaId, Set<Permission> permisos) {
		Entorn entorn = getById(entornId);
		if (entorn == null)
			throw new NotFoundException("No s'ha trobat l'entorn amb id:" + entornId);
		Persona persona = personaDao.getById(personaId, false);
		if (persona == null)
			throw new NotFoundException("No s'ha trobat la persona amb id:" + personaId);
		if (!entorn.getMembres().contains(persona))
			entorn.addMembre(persona);
		if (permisos != null) {
			aclServiceDao.deleteAclEntriesForSid(
					new PrincipalSid(persona.getCodi()),
					entorn.getId(),
					Entorn.class);
			for (Permission perm: permisos) {
				aclServiceDao.addAclEntry(
						new PrincipalSid(persona.getCodi()),
						entorn.getId(),
						Entorn.class,
						perm,
						true);
			}
		}
	}
	@Secured({"ROLE_ADMIN"})
	public void esborrarMembre(Long entornId, Long personaId) {
		Entorn entorn = getById(entornId);
		if (entorn == null)
			throw new NotFoundException("No s'ha trobat l'entorn amb id:" + entornId);
		Persona persona = personaDao.getById(personaId, false);
		if (persona == null)
			throw new NotFoundException("No s'ha trobat la persona amb id:" + entornId);
		// Esborra la persona com a membre
		entorn.removeMembre(persona);
		// Esborra tots els permisos d'aquesta persona per aquest entorn
		aclServiceDao.deleteAclEntriesForSid(
				new PrincipalSid(persona.getCodi()),
				entorn.getId(),
				Entorn.class);
	}
	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public Acl permisosMembresEntorn(Long entornId) {
		ObjectIdentity oid = new ObjectIdentityImpl(Entorn.class, entornId);
		try {
			return aclServiceDao.readAclById(oid);
		} catch (NotFoundException ex) {
			return aclServiceDao.createAcl(oid);
		}
	}*/

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public List<Entorn> findActius() {
		return entornDao.findActius();
	}

	/*@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public List<Entorn> findAccessibles() {
		List<Entorn> entorns = entornDao.findActius();
		List<Entorn> resposta = new ArrayList<Entorn>();
		for (Entorn entorn: entorns) {
			if (isAccessible(entorn))
				resposta.add(entorn);
		}
		return resposta;
	}
	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public Entorn getByIdIfAccessible(Long id) {
		Entorn entorn = entornDao.getById(id, false);
		if (entorn != null && entorn.isActiu() && isAccessible(entorn))
			return entorn;
		return null;
	}*/

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
		List<Domini> dominis = dominiDao.findAmbEntorn(entornId);
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
		List<Enumeracio> enumeracions = enumeracioDao.findAmbEntorn(entornId);
		List<EnumeracioExportacio> enumeracionsDto = new ArrayList<EnumeracioExportacio>();
		for (Enumeracio enumeracio: enumeracions) {
			EnumeracioExportacio dto = new EnumeracioExportacio(
					enumeracio.getCodi(),
					enumeracio.getNom(),
					enumeracio.getValors());
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
			dto.setSistraTramitMapeigCamps(expedientTipus.getSistraTramitMapeigCamps());
			dto.setSistraTramitMapeigDocuments(expedientTipus.getSistraTramitMapeigDocuments());
			dto.setSistraTramitMapeigAdjunts(expedientTipus.getSistraTramitMapeigAdjunts());
			dto.setFormextUrl(expedientTipus.getFormextUrl());
			dto.setFormextUsuari(expedientTipus.getFormextUsuari());
			dto.setFormextContrasenya(expedientTipus.getFormextContrasenya());
			List<EstatExportacio> estats = new ArrayList<EstatExportacio>();
			for (Estat estat: expedientTipus.getEstats()) {
				estats.add(new EstatExportacio(estat.getCodi(), estat.getNom(), estat.getOrdre()));
			}
			dto.setEstats(estats);
			expedientsTipusDto.add(dto);
		}
		entornExportacio.setExpedientsTipus(expedientsTipusDto);
		return entornExportacio;
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public void importar(Long entornId, EntornExportacio exportacio) {
		Entorn entorn = entornDao.getById(entornId, false);
		if (entorn == null)
			throw new NotFoundException("No s'ha trobat l'entorn amb id:" + entornId);
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
			Enumeracio nova = enumeracioDao.findAmbEntornICodi(
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
			nova.setValors(enumeracio.getValors());
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
			nou.setSistraTramitMapeigCamps(expedientTipus.getSistraTramitMapeigCamps());
			nou.setSistraTramitMapeigDocuments(expedientTipus.getSistraTramitMapeigDocuments());
			nou.setSistraTramitMapeigAdjunts(expedientTipus.getSistraTramitMapeigAdjunts());
			nou.setFormextUrl(expedientTipus.getFormextUrl());
			nou.setFormextUsuari(expedientTipus.getFormextUsuari());
			nou.setFormextContrasenya(expedientTipus.getFormextContrasenya());
			expedientTipusDao.saveOrUpdate(nou);
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
	public void setExpedientTipusDao(ExpedientTipusDao expedientTipusDao) {
		this.expedientTipusDao = expedientTipusDao;
	}
	@Autowired
	public void setEstatDao(EstatDao estatDao) {
		this.estatDao = estatDao;
	}
	@Autowired
	public void setAclServiceDao(AclServiceDao aclServiceDao) {
		this.aclServiceDao = aclServiceDao;
	}



	/*private boolean isAccessible(Entorn entorn) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Set<Sid> sids = new HashSet<Sid>();
		sids.add(new PrincipalSid(auth.getName()));
		for (GrantedAuthority ga: auth.getAuthorities()) {
			sids.add(new GrantedAuthoritySid(ga.getAuthority()));
		}
		try {
			Acl acl = aclServiceDao.readAclById(new ObjectIdentityImpl(Entorn.class, entorn.getId()));
			boolean grantedRead = false;
			try {
				grantedRead = acl.isGranted(
						new Permission[]{ExtendedPermission.READ},
						(Sid[])sids.toArray(new Sid[sids.size()]),
						false);
			} catch (NotFoundException ex) {}
			boolean grantedAdmin = false;
			try {
				grantedAdmin = acl.isGranted(
						new Permission[]{ExtendedPermission.ADMINISTRATION},
						(Sid[])sids.toArray(new Sid[sids.size()]),
						false);
			} catch (NotFoundException ex) {}
			return grantedRead || grantedAdmin;
		} catch (NotFoundException ex) {
			return false;
		}
	}
	private boolean isAccessible(Entorn entorn) {
		return isAccessible(entorn, null);
	}
	private boolean isAccessible(Entorn entorn, String usuari) {
		Sid sid = null;
		if (usuari == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			sid = new PrincipalSid(auth.getName());
		} else {
			sid = new PrincipalSid(usuari);
		}
		try {
			Acl acl = aclServiceDao.readAclById(new ObjectIdentityImpl(Entorn.class, entorn.getId()));
			return acl.isGranted(
					new Permission[]{ExtendedPermission.READ},
					new Sid[]{sid},
					false);
		} catch (org.springframework.security.acls.NotFoundException ignored) {}
		return false;
	}*/

}
