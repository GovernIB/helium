package net.conselldemallorca.helium.integracio.plugins.unitat;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import es.caib.dir3caib.ws.api.unidad.UnidadTF;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaDto;


/**
 * Implementació de proves del plugin d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatsOrganiquesPluginDir3 implements UnitatsOrganiquesPlugin {


	private String getServiceUrl() {
		return GlobalProperties.getInstance().getProperty(
				"app.unitats.organiques.dir3.plugin.service.url");
	}
	
	private String getServiceUsername() {
		return GlobalProperties.getInstance().getProperty(
				"app.unitats.organiques.dir3.plugin.service.username");
	}
	private String getServicePassword() {
		return GlobalProperties.getInstance().getProperty(
				"app.unitats.organiques.dir3.plugin.service.password");
	}
	private boolean isLogMissatgesActiu() {
		return GlobalProperties.getInstance().getAsBoolean(
				"app.unitats.organiques.dir3.plugin.service.log.actiu");
	}
	private Integer getServiceTimeout() {
		String key = "app.unitats.organiques.dir3.plugin.service.connect.timeout";
		if (GlobalProperties.getInstance().getProperty(key) != null)
			return GlobalProperties.getInstance().getAsInt(key);
		else
			return null;
	}
	private String getServiceCercaUrl() {
		String serviceUrl = GlobalProperties.getInstance().getProperty(
				"aapp.unitats.organiques.dir3.plugin.service.cerca.url");
		if (serviceUrl == null) {
			serviceUrl = GlobalProperties.getInstance().getProperty(
					"app.unitats.organiques.dir3.plugin.service.url");
		}
		return serviceUrl;
	}
	
	@Override
	public UnitatOrganitzativaDto findUnidad(
			String pareCodi, 
			Timestamp fechaActualizacion, 
			Timestamp fechaSincronizacion) throws MalformedURLException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
		UnidadRest unidad = getUnitatsOrganitzativesRestClient().obtenerUnidad(
				pareCodi,
				fechaActualizacion != null ? dateFormat.format(fechaActualizacion) : null,
				fechaSincronizacion != null ? dateFormat.format(fechaSincronizacion) : null, false);
		if (unidad != null) {
			return toUnitatOrganitzativa(unidad);
		} else {
			return null;
		}

	}
	
	@Override
	public List<UnitatOrganitzativaDto> findAmbPare(
			String pareCodi,
			Timestamp fechaActualizacion,
			Timestamp fechaSincronizacion) throws SistemaExternException {
		try {
			List<UnitatOrganitzativaDto> unitatOrganitzativaDto = new ArrayList<UnitatOrganitzativaDto>();
			 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
			List<UnidadRest> unidades = getUnitatsOrganitzativesRestClient().obtenerArbolUnidades(
					pareCodi,
					fechaActualizacion != null ? dateFormat.format(fechaActualizacion) : null,
					fechaSincronizacion != null ? dateFormat.format(fechaSincronizacion) : null, false);

            if (unidades != null) {
                for (UnidadRest unidad : unidades) {
                	unitatOrganitzativaDto.add(toUnitatOrganitzativa(unidad));
                }
            }
			return unitatOrganitzativaDto;
			
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les unitats organitzatives via WS (" +
					"pareCodi=" + pareCodi + ")",
					ex);
		}
	}

	private UnitatOrganitzativaDto toUnitatOrganitzativaDto(UnidadTF unidad) {
		UnitatOrganitzativaDto unitat = new UnitatOrganitzativaDto(
				unidad.getCodigo(),
				unidad.getDenominacion(),
				unidad.getCodigo(), // CifNif
				unidad.getFechaAltaOficial(),
				unidad.getCodigoEstadoEntidad(),
				unidad.getCodUnidadSuperior(),
				unidad.getCodUnidadRaiz(),
				unidad.getCodigoAmbPais(),
				unidad.getCodAmbComunidad(),
				unidad.getCodAmbProvincia(),
				unidad.getCodPostal(),
				unidad.getDescripcionLocalidad(),
				unidad.getCodigoTipoVia(), 
				unidad.getNombreVia(), 
				unidad.getNumVia(),
				unidad.getHistoricosUO());
		
		return unitat;
	}
	
	private UnitatsOrganitzativesRestClient getUnitatsOrganitzativesRestClient() {
		UnitatsOrganitzativesRestClient unitatsOrganitzativesRestClient = new UnitatsOrganitzativesRestClient(
				getServiceUrl(),
				getServiceUsername(),
				getServicePassword());

		return unitatsOrganitzativesRestClient;
	}
	
	private UnitatOrganitzativaDto toUnitatOrganitzativa(UnidadRest unidad) {
		UnitatOrganitzativaDto unitat = new UnitatOrganitzativaDto(
				unidad.getCodigo(),
				unidad.getDenominacionCooficial() != null ? unidad.getDenominacionCooficial() : unidad.getDenominacion(),
				unidad.getCodigo(), // CifNif
				unidad.getFechaAltaOficial(),
				unidad.getCodigoEstadoEntidad(),
				unidad.getCodUnidadSuperior(),
				unidad.getCodUnidadRaiz(),
				unidad.getCodigoAmbPais(),
				unidad.getCodAmbComunidad(),
				unidad.getCodAmbProvincia(),
				unidad.getCodPostal(),
				unidad.getDescripcionLocalidad(),
				unidad.getCodigoTipoVia(), 
				unidad.getNombreVia(), 
				unidad.getNumVia(),
				unidad.getHistoricosUO());
		return unitat;
	}

}
