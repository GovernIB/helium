package net.conselldemallorca.helium.core.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.dadesext.ComunitatAutonoma;
import net.conselldemallorca.helium.integracio.plugins.dadesext.DadesExternesPlugin;
import net.conselldemallorca.helium.integracio.plugins.dadesext.Municipi;
import net.conselldemallorca.helium.integracio.plugins.dadesext.NivellAdministracio;
import net.conselldemallorca.helium.integracio.plugins.dadesext.Pais;
import net.conselldemallorca.helium.integracio.plugins.dadesext.Provincia;
import net.conselldemallorca.helium.integracio.plugins.dadesext.TipusVia;
import net.conselldemallorca.helium.v3.core.api.dto.ComunitatAutonomaDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.MunicipiDto;
import net.conselldemallorca.helium.v3.core.api.dto.NivellAdministracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaisDto;
import net.conselldemallorca.helium.v3.core.api.dto.ProvinciaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TipusViaDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;

@Component
public class DadesExternesHelper {

	@Resource
	private MonitorIntegracioHelper monitorIntegracioHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PluginHelper pluginHelper;
	
	public List<PaisDto> dadesExternesPaisosFindAll() {

		String accioDescripcio = "Consulta de tots els paisos";
		long t0 = System.currentTimeMillis();
		
		try {
			List<Pais> paisos =  pluginHelper.getDadesExternesPlugin().paisFindAll();
			IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
					new IntegracioParametreDto(
							"paisos",
							paisos.size())
			};
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);			
			return conversioTipusHelper.convertirList(paisos, PaisDto.class);
					
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes. No s'ha pogut obtenir la llista de països: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw tractarExcepcioEnSistemaExtern(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					errorDescripcio, 
					ex);	
		}
	}
	
	public List<ComunitatAutonomaDto> dadesExternesComunitatsFindAll() {

		String accioDescripcio = "Consulta de totes les comunitats";
		long t0 = System.currentTimeMillis();
		try {
			List<ComunitatAutonoma> comunitats = pluginHelper.getDadesExternesPlugin().comunitatFindAll();
			IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
					new IntegracioParametreDto(
							"comunitats",
							comunitats.size())
			};
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);			
			return conversioTipusHelper.convertirList(comunitats, ComunitatAutonomaDto.class);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes. No s'ha pogut obtenir la llista de comunitats autònomes: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw tractarExcepcioEnSistemaExtern(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					errorDescripcio, 
					ex);	
		}
	}
	
	
	public List<ProvinciaDto> dadesExternesProvinciesFindAll() throws SistemaExternException {
		String accioDescripcio = "Consulta de totes les provincies";
		long t0 = System.currentTimeMillis();
		try {
			List<Provincia> provincies =  pluginHelper.getDadesExternesPlugin().provinciaFindAll();
			IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
					new IntegracioParametreDto(
							"provincies",
							provincies.size())
			};
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);	
//			List<CatProvinciaTF> catProvincies = getCatalegService().obtenerCatProvincia();
//			if (catProvincies != null) {
//				for (CatProvinciaTF catProvincia: catProvincies) {
//					Provincia provincia = new Provincia(
//							catProvincia.getCodigoProvincia(), 
//							catProvincia.getCodigoComunidadAutonoma(), 
//							catProvincia.getDescripcionProvincia());
//					provincies.add(provincia);
//				}
//			}
			return conversioTipusHelper.convertirList(provincies, ProvinciaDto.class);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes. No s'ha pogut obtenir la llista de provincies: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw tractarExcepcioEnSistemaExtern(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					errorDescripcio, 
					ex);	
		}
	}
	
	public List<MunicipiDto> dadesExternesMunicipisFindAmbProvincia(String provinciaCodi) {

		String accioDescripcio = "Consulta dels municipis d'una província";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("provinciaCodi", provinciaCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<Municipi> municipis =  pluginHelper.getDadesExternesPlugin().municipiFindByProvincia(provinciaCodi);
			IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
					new IntegracioParametreDto(
							"municipis_per_provincia",
							municipis.size())
			};
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);				
			return conversioTipusHelper.convertirList(municipis,MunicipiDto.class);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes. No s'ha pogut obtenir la llista de municipis per provincia: "+provinciaCodi +" "+ ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw tractarExcepcioEnSistemaExtern(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					errorDescripcio, 
					ex);	
		}
	}
	
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi) throws SistemaExternException {
		
		String accioDescripcio = "Consulta de municipis";
		long t0 = System.currentTimeMillis();
		try {
			List<Municipi> municipis =  pluginHelper.getDadesExternesPlugin().municipiFindByProvincia(provinciaCodi);
			IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
					new IntegracioParametreDto(
							"municipis",
							municipis.size())
			};
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);				
			return   conversioTipusHelper.convertirList(municipis, MunicipiDto.class);

		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes. No s'ha pogut obtenir la llista de municipis:  "+ ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw tractarExcepcioEnSistemaExtern(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					errorDescripcio, 
					ex);	
		}
	}
	
	public List<ProvinciaDto> dadesExternesProvinciesFindAmbComunitat(String comunitatCodi) {

		String accioDescripcio = "Consulta de les províncies d'una comunitat";
		long t0 = System.currentTimeMillis();
		try {
			List<Provincia> provincies =  pluginHelper.getDadesExternesPlugin().provinciaFindByComunitat(comunitatCodi);
			IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
					new IntegracioParametreDto(
							"comunitatCodi",
							comunitatCodi)
			};
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);				
			return conversioTipusHelper.convertirList(provincies,ProvinciaDto.class);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes. No s'ha pogut obtenir la llista de provincies per comunitat: " +comunitatCodi +" " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw tractarExcepcioEnSistemaExtern(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					errorDescripcio, 
					ex);	
		}
	}

	
	public List<NivellAdministracioDto> dadesExternesNivellsAdministracioAll() {

		String accioDescripcio = "Consulta de nivells d'administració";
		long t0 = System.currentTimeMillis();
		try {
			List<NivellAdministracio> nivellAdministracio =  pluginHelper.getDadesExternesPlugin().nivellAdministracioFindAll();
			IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
					new IntegracioParametreDto(
							"nivellAdministracio",
							nivellAdministracio.size())
			};
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);				
			return conversioTipusHelper.convertirList(nivellAdministracio,NivellAdministracioDto.class);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes. No s'ha pogut obtenir la llista de nivells d'administracions:  "+ ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw tractarExcepcioEnSistemaExtern(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					errorDescripcio, 
					ex);	
		}
	}
	
	public List<TipusViaDto> dadesExternesTipusViaFindAll() {

		String accioDescripcio = "Consulta dels tipus de via";
		long t0 = System.currentTimeMillis();
		
		try {
			List<TipusVia> tipusVia =  pluginHelper.getDadesExternesPlugin().tipusViaFindAll();
			IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
					new IntegracioParametreDto(
							"tipusVia",
							tipusVia.size())
			};
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);			
			return conversioTipusHelper.convertirList(tipusVia, TipusViaDto.class);
					
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes. No s'ha pogut obtenir la llista de tipus de via: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw tractarExcepcioEnSistemaExtern(
					MonitorIntegracioHelper.INTCODI_DADES_EXTERNES,
					errorDescripcio, 
					ex);	
		}
	}	
	
	private SistemaExternException tractarExcepcioEnSistemaExtern(
			String sistemaExtern,
			String missatge,
			Throwable ex) {
		return SistemaExternException.tractarSistemaExternException(
				null,
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null,
				sistemaExtern,
				missatge,
				ex);
	}
	
}
