/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.ws.backofficeintegracio.Annex;
import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreEntrada;
import es.caib.distribucio.ws.backofficeintegracio.BackofficeIntegracio;
import es.caib.distribucio.ws.backofficeintegracio.Interessat;
import es.caib.distribucio.ws.client.BackofficeIntegracioWsClientFactory;
import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioAnnex;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioInteressat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.repository.AnotacioAnnexRepository;
import net.conselldemallorca.helium.v3.core.repository.AnotacioInteressatRepository;
import net.conselldemallorca.helium.v3.core.repository.AnotacioRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;

/**
 * Mètodes comuns per cridar WebService de Distribucio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DistribucioHelper {

	@Autowired
	AnotacioRepository anotacioRepository;
	@Autowired
	AnotacioAnnexRepository anotacioAnnexRepository;
	@Autowired
	AnotacioInteressatRepository anotacioInteressatRepository;
	@Autowired
	ExpedientTipusRepository expedientTipusRepository;
	@Autowired
	ExpedientRepository expedientRepository;

	
	/** Mètode estàtic per obtenir la instància del client del WS de Distribucio.
	 * 
	 * @return
	 * @throws Exception
	 * 				Pot llençar excepcions de tipus IOException de comunicació o si no està configurat llença una excepció.
	 */
	public BackofficeIntegracio getBackofficeIntegracioServicePort() throws Exception {
		
		String url = GlobalProperties.getInstance().getProperty("net.conselldemallorca.helium.distribucio.backofficeIntegracio.ws.url");
		String usuari = GlobalProperties.getInstance().getProperty("net.conselldemallorca.helium.distribucio.backofficeIntegracio.ws.username");
		String contrasenya = GlobalProperties.getInstance().getProperty("net.conselldemallorca.helium.distribucio.backofficeIntegracio.ws.password");

		if (url == null || "".equals(url.trim()))
			throw new Exception("No s'ha trobat la configuració per accedir al WS del backoffice de Distribucio (net.conselldemallorca.helium.distribucio.backofficeIntegracio.ws.url");
		
		return BackofficeIntegracioWsClientFactory.getWsClient(
				url,
				usuari,
				contrasenya);
	}

	/** Mètode per guardar a Helium la informació d'una anotació de registre consultada a Distribucio.
	 *  
	 * @param anotacio
	 */
	@Transactional
	public void guardarAnotacio(String distribucioId, AnotacioRegistreEntrada anotacioEntrada) {
		
		ExpedientTipus expedientTipus = null;
		Expedient expedient = null;
		if (anotacioEntrada.getProcedimentCodi() != null) {

			// Cerca el tipus d'expedient per aquell codi de procediment
			List<ExpedientTipus> expedientsTipus = expedientTipusRepository.findByDistribucioCodiProcediment(anotacioEntrada.getProcedimentCodi());
			if (!expedientsTipus.isEmpty()) {
				expedientTipus = expedientsTipus.get(0);
				
				// Cerca si hi ha cap expedient que coincideixi amb el número d'expedient
				if (anotacioEntrada.getExpedientNumero() != null) {
					expedient = expedientRepository.findByTipusAndNumero(expedientTipus, anotacioEntrada.getExpedientNumero());
				}
			}
		}
		// Crea l'anotació
		Anotacio anotacioEntity = Anotacio.getBuilder(
				distribucioId,
				new Date(),
				AnotacioEstatEnumDto.PENDENT,
				anotacioEntrada.getAssumpteTipusCodi(),
				anotacioEntrada.getData().toGregorianCalendar().getTime(),
				anotacioEntrada.getEntitatCodi(),
				anotacioEntrada.getIdentificador(),
				anotacioEntrada.getIdiomaCodi(),
				anotacioEntrada.getLlibreCodi(),
				anotacioEntrada.getOficinaCodi(),
				anotacioEntrada.getDestiCodi(),
				expedientTipus,
				expedient).
				aplicacioCodi(anotacioEntrada.getAplicacioCodi()).
				aplicacioVersio(anotacioEntrada.getAplicacioVersio()).
				assumpteCodiCodi(anotacioEntrada.getAssumpteCodiCodi()).
				assumpteCodiDescripcio(anotacioEntrada.getAssumpteCodiDescripcio()).
				assumpteTipusDescripcio(anotacioEntrada.getAssumpteTipusDescripcio()).
				docFisicaCodi(anotacioEntrada.getDocFisicaCodi()).
				docFisicaDescripcio(anotacioEntrada.getDocFisicaDescripcio()).
				entitatDescripcio(anotacioEntrada.getEntitatDescripcio()).
				expedientNumero(anotacioEntrada.getExpedientNumero()).
				exposa(anotacioEntrada.getExposa()).
	 			extracte(anotacioEntrada.getExtracte()).
	 			procedimentCodi(anotacioEntrada.getProcedimentCodi()).
				idiomaDescripcio(anotacioEntrada.getIdomaDescripcio()).
				llibreDescripcio(anotacioEntrada.getLlibreDescripcio()).
				observacions(anotacioEntrada.getObservacions()).
				oficinaDescripcio(anotacioEntrada.getOficinaDescripcio()).
				origenData(anotacioEntrada.getOrigenData() != null ? anotacioEntrada.getOrigenData().toGregorianCalendar().getTime() : null).
				origenRegistreNumero(anotacioEntrada.getOrigenRegistreNumero()).
				refExterna(anotacioEntrada.getRefExterna()).
				solicita(anotacioEntrada.getSolicita()).
				transportNumero(anotacioEntrada.getTransportNumero()).
				transportTipusCodi(anotacioEntrada.getTransportTipusCodi()).
				transportTipusDescripcio(anotacioEntrada.getTransportTipusDescripcio()).
				usuariCodi(anotacioEntrada.getUsuariCodi()).
				usuariNom(anotacioEntrada.getUsuariNom()).
				destiDescripcio(anotacioEntrada.getDestiDescripcio()).
				build();
		anotacioRepository.save(anotacioEntity);
				
		// Crea els interessats
		for (Interessat interessat: anotacioEntrada.getInteressats()) {
			anotacioEntity.getInteressats().add(
					crearInteressatEntity(
							interessat,
							anotacioEntity));
		}
		// Crea els annexos
		for (Annex annex: anotacioEntrada.getAnnexos()) {
			anotacioEntity.getAnnexos().add(
					crearAnnexEntity(
							annex,
							anotacioEntity));
		}	
	}

	private AnotacioInteressat crearInteressatEntity(
			Interessat interessat,
			Anotacio anotacio) {
		
		AnotacioInteressat representantEntity = null;
		if (interessat.getRepresentant() != null) {
			representantEntity = AnotacioInteressat.getBuilder(	
		 			interessat.getRepresentant().getTipus()).
		 			adresa (interessat.getRepresentant().getAdresa()).
		 			canal (interessat.getRepresentant().getCanal()).
		 			cp (interessat.getRepresentant().getCp()).
		 			documentNumero (interessat.getRepresentant().getDocumentNumero()).
		 			documentTipus (interessat.getRepresentant().getDocumentTipus()).
		 			email (interessat.getRepresentant().getEmail()).
		 			llinatge1 (interessat.getRepresentant().getLlinatge1()).
		 			llinatge2 (interessat.getRepresentant().getLlinatge2()).
		 			municipiCodi (interessat.getRepresentant().getMunicipiCodi()).
		 			nom (interessat.getRepresentant().getNom()).
		 			observacions (interessat.getRepresentant().getObservacions()).
		 			paisCodi (interessat.getRepresentant().getPaisCodi()).
		 			provinciaCodi (interessat.getRepresentant().getProvinciaCodi()).
		 			raoSocial (interessat.getRepresentant().getRaoSocial()).
		 			telefon (interessat.getRepresentant().getTelefon()).
		 			pais (interessat.getRepresentant().getPais()).
		 			provincia (interessat.getRepresentant().getProvincia()).
		 			municipi (interessat.getRepresentant().getMunicipi()).
		 			organCodi(interessat.getRepresentant().getOrganCodi()).
					build();
		}
		AnotacioInteressat interessatEntity = AnotacioInteressat.getBuilder(	
		 			interessat.getTipus()).
		 			adresa (interessat.getAdresa()).
		 			canal (interessat.getCanal()).
		 			cp (interessat.getCp()).
		 			documentNumero (interessat.getDocumentNumero()).
		 			documentTipus (interessat.getDocumentTipus()).
		 			email (interessat.getEmail()).
		 			llinatge1 (interessat.getLlinatge1()).
		 			llinatge2 (interessat.getLlinatge2()).
		 			municipiCodi (interessat.getMunicipiCodi()).
		 			nom (interessat.getNom()).
		 			observacions (interessat.getObservacions()).
		 			paisCodi (interessat.getPaisCodi()).
		 			provinciaCodi (interessat.getProvinciaCodi()).
		 			pais (interessat.getPais()).
		 			provincia (interessat.getProvincia()).
		 			municipi (interessat.getMunicipi()).		 			
		 			raoSocial (interessat.getRaoSocial()).
		 			telefon (interessat.getTelefon()).
		 			organCodi(interessat.getOrganCodi()).
		 			representant(representantEntity).
		 			anotacio(anotacio).
					build();
		return anotacioInteressatRepository.save(interessatEntity);
	}

	private AnotacioAnnex crearAnnexEntity(Annex annex, Anotacio anotacio) {
		
		AnotacioAnnex annexEntity = AnotacioAnnex.getBuilder(
				annex.getNom(),
				annex.getNtiFechaCaptura().toGregorianCalendar().getTime(),
				annex.getNtiOrigen(),
				annex.getNtiTipoDocumental(),
				annex.getSicresTipoDocumento(),
				annex.getTitol(),
				anotacio,
				annex.getNtiEstadoElaboracion()).
				contingut(annex.getContingut()).
				firmaContingut(annex.getFirmaContingut()).
				ntiTipoDocumental(annex.getNtiTipoDocumental()).
				sicresTipoDocumento(annex.getSicresTipoDocumento()).
				observacions(annex.getObservacions()).
				sicresValidezDocumento(annex.getSicresValidezDocumento()).
				tipusMime(annex.getTipusMime()).
				uuid(annex.getUuid()).
				firmaNom(annex.getFirmaNom()).
				build();
		return anotacioAnnexRepository.save(annexEntity);	
	}
}
