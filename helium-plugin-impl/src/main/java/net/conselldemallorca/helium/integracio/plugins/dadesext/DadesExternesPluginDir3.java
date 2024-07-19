package net.conselldemallorca.helium.integracio.plugins.dadesext;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.dir3caib.ws.api.catalogo.CatComunidadAutonomaTF;
import es.caib.dir3caib.ws.api.catalogo.CatEntidadGeograficaTF;
import es.caib.dir3caib.ws.api.catalogo.CatLocalidadTF;
import es.caib.dir3caib.ws.api.catalogo.CatNivelAdministracion;
import es.caib.dir3caib.ws.api.catalogo.CatPais;
import es.caib.dir3caib.ws.api.catalogo.CatProvinciaTF;
import es.caib.dir3caib.ws.api.catalogo.CatTipoVia;
import es.caib.dir3caib.ws.api.catalogo.Dir3CaibObtenerCatalogosWs;
import es.caib.dir3caib.ws.api.catalogo.Dir3CaibObtenerCatalogosWsService;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;

/**
 * Implementació del plugin de dades externes que consulta la informació
 * a DIR3CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesExternesPluginDir3 implements DadesExternesPlugin {
//	public DadesExternesPluginDir3() {
//		super();
//	}
//	public DadesExternesPluginDir3(String propertyKeyBase, Properties properties) {
//		super(propertyKeyBase, properties);
//	}
	@Override
	public List<Pais> paisFindAll() throws SistemaExternException {
		try {
			List<Pais> paisos = new ArrayList<Pais>();
			List<CatPais> catPaisos = getCatalegService().obtenerCatPais();
			if (catPaisos != null) {
				for (CatPais catPais: catPaisos) {
					Pais pais = new Pais(
							catPais.getCodigoPais(), 
							catPais.getAlfa2Pais(), 
							catPais.getAlfa3Pais(), 
							catPais.getDescripcionPais());
					paisos.add(pais);
				}
			}
			return paisos;
		} catch (Exception ex) {
			LOGGER.error(
					"No s'han pogut consultar els paisos",
					ex);
			throw new SistemaExternException(
					"No s'han pogut consultar els paisos",
					ex);
		}
	}

	@Override
	public List<ComunitatAutonoma> comunitatFindAll() throws SistemaExternException {
		try {
			List<ComunitatAutonoma> comunitats = new ArrayList<ComunitatAutonoma>();
			List<CatComunidadAutonomaTF> catComunitats = getCatalegService().obtenerCatComunidadAutonoma();
			if (catComunitats != null) {
				for (CatComunidadAutonomaTF catComunitat: catComunitats) {
					ComunitatAutonoma comunitat = new ComunitatAutonoma(
							catComunitat.getCodigoComunidad(), 
							catComunitat.getCodigoPais(), 
							catComunitat.getDescripcionComunidad());
					comunitats.add(comunitat);
				}
			}
			return comunitats;
		} catch (Exception ex) {
			LOGGER.error(
					"No s'han pogut consultar les comunitats",
					ex);
			throw new SistemaExternException(
					"No s'han pogut consultar les comunitats",
					ex);
		}
	}

	@Override
	public List<Provincia> provinciaFindAll() throws SistemaExternException {
		try {
			List<Provincia> provincies = new ArrayList<Provincia>();
			List<CatProvinciaTF> catProvincies = getCatalegService().obtenerCatProvincia();
			if (catProvincies != null) {
				for (CatProvinciaTF catProvincia: catProvincies) {
					Provincia provincia = new Provincia(
							catProvincia.getCodigoProvincia(), 
							catProvincia.getCodigoComunidadAutonoma(), 
							catProvincia.getDescripcionProvincia());
					provincies.add(provincia);
				}
			}
			return provincies;
		} catch (Exception ex) {
			LOGGER.error(
					"No s'han pogut consultar les provincies",
					ex);
			throw new SistemaExternException(
					"No s'han pogut consultar les provincies",
					ex);
		}
	}

	@Override
	public List<Provincia> provinciaFindByComunitat(
			String comunitatCodi) throws SistemaExternException {
		List<Provincia> provinciesComunitat = new ArrayList<Provincia>();
		Long comunitatCodiNum = null;
		comunitatCodiNum = Long.parseLong(comunitatCodi);
		if (comunitatCodiNum != null) {
			List<Provincia> provincies = provinciaFindAll();
			for (Provincia provincia: provincies) {
				if (comunitatCodiNum.equals(provincia.getCodiComunitat()))
					provinciesComunitat.add(provincia);
			}
		}
		return provinciesComunitat;
	}

	@Override
	public List<Municipi> municipiFindByProvincia(
			String provinciaCodi) throws SistemaExternException {
		try {
			List<Municipi> municipisProvincia = new ArrayList<Municipi>();
			if (provinciaCodi != null && !provinciaCodi.isEmpty()) {
				List<Municipi> municipis = municipiFindAll();
				for (Municipi municipi: municipis) {
					if (provinciaCodi.equals(municipi.getCodiProvincia()))
						municipisProvincia.add(municipi);
				}
			}
			return municipisProvincia;
		} catch (Exception ex) {
			LOGGER.error(
					"No s'han pogut consultar els municipis de la província " + provinciaCodi,
					ex);
			throw new SistemaExternException(
					"No s'han pogut consultar els municipis de la província " + provinciaCodi,
					ex);
		}
	}

	@Override
	public List<EntitatGeografica> entitatGeograficaFindAll() throws SistemaExternException {
		try {
			List<EntitatGeografica> entitatsGeografiques = new ArrayList<EntitatGeografica>();
			List<CatEntidadGeograficaTF> catEntitatsGeografiques = getCatalegService().obtenerCatEntidadGeografica();
			if (catEntitatsGeografiques != null) {
				for (CatEntidadGeograficaTF catEntitatGeografica: catEntitatsGeografiques) {
					EntitatGeografica entitatGeografica = new EntitatGeografica(
							catEntitatGeografica.getCodigoEntidadGeografica(), 
							catEntitatGeografica.getDescripcionEntidadGeografica());
					entitatsGeografiques.add(entitatGeografica);
				}
			}
			return entitatsGeografiques;
		} catch (Exception ex) {
			LOGGER.error(
					"No s'han pogut consultar les entitats geografiques",
					ex);
			throw new SistemaExternException(
					"No s'han pogut consultar les entitats geografiques",
					ex);
		}
	}

	@Override
	public List<NivellAdministracio> nivellAdministracioFindAll() throws SistemaExternException {
		try {
			List<NivellAdministracio> nivellsAdministracio = new ArrayList<NivellAdministracio>();
			List<CatNivelAdministracion> catNivellsAdministracio = getCatalegService().obtenerCatNivelAdministracion();
			if (catNivellsAdministracio != null) {
				for (CatNivelAdministracion catNivellAdministracio: catNivellsAdministracio) {
					NivellAdministracio nivellAdministracio = new NivellAdministracio(
							catNivellAdministracio.getCodigoNivelAdministracion(), 
							catNivellAdministracio.getDescripcionNivelAdministracion());
					nivellsAdministracio.add(nivellAdministracio);
				}
			}
			return nivellsAdministracio;
		} catch (Exception ex) {
			LOGGER.error(
					"No s'han pogut consultar els nivells d'administració",
					ex);
			throw new SistemaExternException(
					"No s'han pogut consultar els nivells d'administració",
					ex);
		}
	}

	@Override
	public List<TipusVia> tipusViaFindAll() throws SistemaExternException {
		try {
			List<TipusVia> tipusVia = new ArrayList<TipusVia>();
			List<CatTipoVia> catTiposVia = getCatalegService().obtenerCatTipoVia();
			if (catTiposVia != null) {
				for (CatTipoVia catTipoVia: catTiposVia) {
					TipusVia tipoVia = new TipusVia(
							catTipoVia.getCodigoTipoVia(), 
							catTipoVia.getDescripcionTipoVia());
					tipusVia.add(tipoVia);
				}
			}
			return tipusVia;
		} catch (Exception ex) {
			LOGGER.error(
					"No s'han pogut consultar els tipus de via",
					ex);
			throw new SistemaExternException(
					"No s'han pogut consultar els tipus de via",
					ex);
		}
	}



	private List<Municipi> municipis = null;
	private Long municipisDataActualitzacio = 0L;
	private List<Municipi> municipiFindAll() throws MalformedURLException {
		// Tornem a per la petició per obtenir els municipis, únicament si:
		// 1. Encara no s'han consultat
		// 2. Si ja fa més d'un dia (86400000ms) que s'ha consultat
		if (municipis == null || (municipisDataActualitzacio < System.currentTimeMillis() - 86400000)) {
			List<Municipi> municipis = new ArrayList<Municipi>();
			List<CatLocalidadTF> catLocalitats = getCatalegService().obtenerCatLocalidad();
			if (catLocalitats != null) {
				for (CatLocalidadTF catLocalitat: catLocalitats) {
					Municipi localitat = new Municipi(
							catLocalitat.getCodigoLocalidad(), 
							catLocalitat.getCodigoEntidadGeografica(), 
							catLocalitat.getCodigoProvincia(), 
							catLocalitat.getDescripcionLocalidad());
					municipis.add(localitat);
				}
			}
			Collections.sort(municipis);
			this.municipis = municipis;
			municipisDataActualitzacio = System.currentTimeMillis();
			return municipis;
		} else {
			return this.municipis;
		}
	}

	private Dir3CaibObtenerCatalogosWs getCatalegService() throws MalformedURLException {
		Dir3CaibObtenerCatalogosWs client = null;
		URL url = new URL(getServiceUrl() + "?wsdl");
		Dir3CaibObtenerCatalogosWsService service = new Dir3CaibObtenerCatalogosWsService(
				url,
				new QName(
						"http://catalogo.ws.dir3caib.caib.es/",
						"Dir3CaibObtenerCatalogosWsService"));
		client = service.getDir3CaibObtenerCatalogosWs();
		BindingProvider bp = (BindingProvider)client;
		bp.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				getServiceUrl());
		String username = getUsername();
		if (username != null && !username.isEmpty()) {
			bp.getRequestContext().put(
					BindingProvider.USERNAME_PROPERTY,
					username);
			bp.getRequestContext().put(
					BindingProvider.PASSWORD_PROPERTY,
					getPassword());
		}
		return client;
	}
	

	private String getServiceUrl() {
		String url = GlobalProperties.getInstance().getProperty(
				"app.dadesext.dir3.plugin.service.url");
		return url;
	}
	private String getUsername() {
		String username =GlobalProperties.getInstance().getProperty(
				"app.dadesext.dir3.plugin.service.url.username");
		if (username != null) {
			return username;
		} else {
			return GlobalProperties.getInstance().getProperty(
					"app.unitats.organiques.dir3.plugin.service.username");
		}
	}
	private String getPassword() {
		String password = GlobalProperties.getInstance().getProperty(
				"app.dadesext.dir3.plugin.service.url.password");
		if (password != null) {
			return password;
		} else {
			return GlobalProperties.getInstance().getProperty(
					"app.unitats.organiques.dir3.plugin.service.password");
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DadesExternesPluginDir3.class);


}
