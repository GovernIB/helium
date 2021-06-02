package es.caib.helium.integracio.service.arxiu;

import org.springframework.stereotype.Service;

import com.netflix.servo.util.Strings;

import es.caib.helium.integracio.domini.arxiu.ExpedientArxiu;
import es.caib.helium.integracio.excepcions.arxiu.ArxiuServiceException;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.IArxiuPlugin;
import lombok.Setter;

@Service
public class ArxiuServiceCaibImpl implements ArxiuService {

	@Setter
	private IArxiuPlugin api;
	
	@Override
	public Expedient getExpedientByUuId(String uuId) {
		// TODO Auto-generated method stub

		return api.expedientDetalls(uuId, null);
	}

	@Override
	public boolean crearExpedient(ExpedientArxiu expedientArxiu) throws ArxiuServiceException {
		
		try {
			var expedient = new Expedient();
			expedient.setNom(this.treureCaractersEstranys(expedientArxiu.getIdentificador()));
			expedient.setIdentificador(expedientArxiu.getArxiuUuid());
			var metadades = new ExpedientMetadades();
			metadades.setDataObertura(expedientArxiu.getNtiDataObertura());
			metadades.setClassificacio(expedientArxiu.getNtiClassificacio());
			metadades.setEstat(expedientArxiu.isExpedientFinalitzat() ? ExpedientEstat.TANCAT : ExpedientEstat.OBERT);
			metadades.setOrgans(expedientArxiu.getNtiOrgans());
			metadades.setInteressats(expedientArxiu.getNtiInteressats());
			metadades.setSerieDocumental(expedientArxiu.getSerieDocumental());
			expedient.setMetadades(metadades);
			return  api.expedientCrear(expedient) != null;
		} catch (Exception e) {
			throw new ArxiuServiceException("Error al crear l'expedient " + expedientArxiu.getIdentificador() + " a l'arxiu", e);
		}
	}
	
	private String treureCaractersEstranys(String nom) {
		
		if (Strings.isNullOrEmpty(nom)) {
			return null;
		}
		var nomRevisat = nom.trim().replace("&", "&amp;").replaceAll("[~\"#%*:<\n\r\t>/?/|\\\\ ]", "_");
		// L'Arxiu no admet un punt al final del nom #1418
		if (nomRevisat.endsWith(".")) {
			nomRevisat = nomRevisat.substring(0, nomRevisat.length() - 1) + "_";
		}
		return nomRevisat;
	}
	
//	toArxiuExpedient(
//			String nom,
//			List<String> ntiOrgans,
//			Date ntiDataObertura,
//			String ntiClassificacio,
//			boolean expedientFinalitzat,
//			List<String> ntiInteressats,
//			String serieDocumental,
//			String arxiuUuid) {
	
//	toArxiuExpedient(
//			expedient.getIdentificador(),
//			Arrays.asList(obtenirNtiOrgano(expedient)),
//			expedient.getDataInici(),
//			obtenirNtiClasificacion(expedient),
//			false,
//			null,
//			obtenirNtiSerieDocumental(expedient),
//			expedient.getArxiuUuid()));
//
}
