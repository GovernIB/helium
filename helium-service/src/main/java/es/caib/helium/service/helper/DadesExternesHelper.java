package es.caib.helium.service.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import es.caib.helium.commons.dto.ComunitatAutonomaDto;
import es.caib.helium.commons.dto.IntegracioAccioTipusEnumDto;
import es.caib.helium.commons.dto.IntegracioParametreDto;
import es.caib.helium.commons.dto.MunicipiDto;
import es.caib.helium.commons.dto.NivellAdministracioDto;
import es.caib.helium.commons.dto.PaisDto;
import es.caib.helium.commons.dto.ProvinciaDto;
import es.caib.helium.commons.dto.TipusViaDto;
import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.commons.plugins.dadesext.ComunitatAutonoma;
import es.caib.helium.commons.plugins.dadesext.Municipi;
import es.caib.helium.commons.plugins.dadesext.NivellAdministracio;
import es.caib.helium.commons.plugins.dadesext.Pais;
import es.caib.helium.commons.plugins.dadesext.Provincia;
import es.caib.helium.commons.plugins.dadesext.TipusVia;


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
