package es.caib.helium.logic.helper;

import es.caib.helium.logic.intf.dto.CampTipusDto;
import es.caib.helium.logic.intf.dto.ConsultaDominiDto;
import es.caib.helium.logic.intf.dto.DadaIndexadaDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.util.ExpedientCamps;
import es.caib.helium.persist.entity.Camp;
import es.caib.helium.persist.entity.Camp.TipusCamp;
import es.caib.helium.persist.entity.Consulta;
import es.caib.helium.persist.entity.ConsultaCamp;
import es.caib.helium.persist.entity.ConsultaCamp.TipusConsultaCamp;
import es.caib.helium.persist.entity.ConsultaCamp.TipusParamConsultaCamp;
import es.caib.helium.persist.entity.DefinicioProces;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.repository.CampRepository;
import es.caib.helium.persist.repository.ConsultaCampRepository;
import es.caib.helium.persist.repository.DefinicioProcesRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Helper per a gestionar els consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConsultaHelper {
	@Resource
	private VariableHelper variableHelper;	
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private CampRepository campRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ConsultaCampRepository consultaCampRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PluginHelper pluginHelper;

	/**
	 * Mètodes per a obtenir els camps de les consultes per tipus
	 */
	public List<TascaDadaDto> findCampsPerCampsConsulta(Consulta consulta, TipusConsultaCamp tipus) {
		List<TascaDadaDto> resposta = new ArrayList<TascaDadaDto>();
		List<ConsultaCamp> camps = null;
		
		if (tipus != null)
			camps = consultaCampRepository.findCampsConsulta(consulta.getId(), tipus);
		else
			camps = new ArrayList<ConsultaCamp>(consulta.getCamps());
		Map<Integer, ConsultaDominiDto> consultesDominis = new HashMap<Integer, ConsultaDominiDto>();
		for (ConsultaCamp camp: camps) {
			TascaDadaDto tascaDadaDto = null;
			DefinicioProces definicioProces = null;
			Camp campRes = null;
			if (camp.getCampCodi().startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
				// Camp expedient
				campRes = getCampExpedient(camp.getCampCodi());
			} else if (camp.getDefprocJbpmKey() != null) {
				// Definició de procés
				definicioProces = definicioProcesRepository.findByJbpmKeyAndVersio(
						camp.getDefprocJbpmKey(),
						camp.getDefprocVersio());
				if (definicioProces != null) {
					campRes = campRepository.findByDefinicioProcesAndCodi(
							definicioProces,
							camp.getCampCodi());
				}
			} else {
				// Tipus d'expedient
				if (consulta.getExpedientTipus() != null) {
					campRes = campRepository.findByExpedientTipusAndCodi(
							consulta.getExpedientTipus().getId(),
							camp.getCampCodi(),
							consulta.getExpedientTipus().getExpedientTipusPare() != null); 
				}
			}
			if (campRes != null) {
				tascaDadaDto = variableHelper.getTascaDadaDtoParaConsultaDisseny(campRes,tipus, consultesDominis);
			} else {
				tascaDadaDto = getTascaDadaPerCampsConsulta(camp);
			}
			if (definicioProces != null) {
				tascaDadaDto.setDefinicioProcesKey(definicioProces.getJbpmKey());
			}
			tascaDadaDto.setAmpleCols(camp.getAmpleCols());
			tascaDadaDto.setBuitCols(camp.getBuitCols());
			resposta.add(tascaDadaDto);
		}
		// Consulta el text als dominis
		variableHelper.consultaDominisAgrupats(consultesDominis);
		return resposta;
	}
	
	private TascaDadaDto getTascaDadaPerCampsConsulta(ConsultaCamp camp) {
		TascaDadaDto tascaDadaDto = null;
		if (TipusConsultaCamp.PARAM.equals(camp.getTipus())) {
			if (TipusParamConsultaCamp.SENCER.equals(camp.getParamTipus())) {
				tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.INTEGER, camp.getCampDescripcio() != null ? camp.getCampDescripcio() : camp.getCampCodi()));
			} else if (TipusParamConsultaCamp.FLOTANT.equals(camp.getParamTipus())) {
				tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.FLOAT, camp.getCampDescripcio() != null ? camp.getCampDescripcio() : camp.getCampCodi()));
			} else if (TipusParamConsultaCamp.DATA.equals(camp.getParamTipus())) {
				tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.DATE, camp.getCampDescripcio() != null ? camp.getCampDescripcio() : camp.getCampCodi()));
			} else if (TipusParamConsultaCamp.BOOLEAN.equals(camp.getParamTipus())) {
				tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.BOOLEAN, camp.getCampDescripcio() != null ? camp.getCampDescripcio() : camp.getCampCodi()));
			} else {
				tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.STRING, camp.getCampDescripcio() != null ? camp.getCampDescripcio() : camp.getCampCodi()));
			}
		} else {
			String description = camp.getCampDescripcio() == null ? camp.getCampCodi() : camp.getCampDescripcio();
			tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.STRING, description));
		}
		return tascaDadaDto;
	}
	
	private String getValueCampExpedient(Expedient expedient, String campCodi) {		
		String text = null;
		if (ExpedientCamps.EXPEDIENT_CAMP_ID.equals(campCodi)) {
			text = expedient.getId().toString();
		} else if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(campCodi)) {
			text = expedient.getNumeroIdentificador();
		} else if (ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(campCodi)) {
			text = expedient.getTitol();
		} else if (ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(campCodi)) {
			text = expedient.getComentari();
		} else if (ExpedientCamps.EXPEDIENT_CAMP_INICIADOR.equals(campCodi)) {
			PersonaDto persona = pluginHelper.personaFindAmbCodi(expedient.getIniciadorCodi());
			if (persona != null)
				text = persona.getNomSencer();
			else
				text = expedient.getIniciadorCodi();
		} else if (ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE.equals(campCodi)) {
			PersonaDto persona = pluginHelper.personaFindAmbCodi(expedient.getResponsableCodi());
			if (persona != null)
				text = persona.getNomSencer();
			else 
				text = expedient.getResponsableCodi();
		} else if (ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI.equals(campCodi)) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			text = df.format(expedient.getDataInici());
		} else if (ExpedientCamps.EXPEDIENT_CAMP_ESTAT_JSP.equals(campCodi)) {
			if (expedient.getDataFi() != null)
				text = messageHelper.getMessage("expedient.consulta.finalitzat");
			else if (expedient.getEstat() != null)
				text = expedient.getEstat().getNom();
			else
				text = messageHelper.getMessage("expedient.consulta.iniciat");
//			if (expedient.getEstat() != null)
//				text = expedient.getEstat().getNom();
//			else if (expedient.getDataFi() != null)
//				text = messageHelper.getMessage("expedient.consulta.finalitzat");
//			else
//				text = messageHelper.getMessage("expedient.consulta.iniciat");	
		}
		return text;
	}
	
	private Camp getCampExpedient(String campCodi) {
		Camp campExpedient = new Camp();
		if (ExpedientCamps.EXPEDIENT_CAMP_ID.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.id"));
		} else if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.numero"));
		} else if (ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.titol"));
		} else if (ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.comentari"));
		} else if (ExpedientCamps.EXPEDIENT_CAMP_INICIADOR.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.iniciador"));
		} else if (ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.responsable"));
		} else if (ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.DATE);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.data_ini"));
		} else if (ExpedientCamps.EXPEDIENT_CAMP_ESTAT.equals(campCodi)) {
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SELECCIO);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.estat"));
		} else {
			campExpedient = null;
		}
		return campExpedient;
	}

	public void revisarDadesExpedientAmbValorsEnumeracionsODominis(
			Map<String, DadaIndexadaDto> dadesExpedient,
			List<Camp> campsInforme, 
			Expedient expedient) {
		String dadaIndexadaClau = null;
		Map<Integer, ConsultaDominiDto> consultesDomini = new HashMap<Integer, ConsultaDominiDto>();
		for (Camp camp: campsInforme) {
			if (camp != null && (camp.getDefinicioProces() != null || camp.getExpedientTipus() != null)) {
				if (camp.getExpedientTipus() == null)
					// Definició de procés
					dadaIndexadaClau = camp.getDefinicioProces().getJbpmKey() + "/" + camp.getCodi();
				else
					// Tipus d'expedient
					dadaIndexadaClau = camp.getCodi();
				if (!dadesExpedient.containsKey(dadaIndexadaClau)) {
					dadesExpedient.put(dadaIndexadaClau, new DadaIndexadaDto(camp.getCodi(), camp.getEtiqueta()));
				}
				DadaIndexadaDto dadaIndexada = dadesExpedient.get(dadaIndexadaClau);
				if (camp.getEnumeracio() != null && camp.getDefinicioProces() != null) {
					String text;
					if (dadaIndexada.isMultiple()) {
						List<String> valorsMostrar = new ArrayList<String>();
						for (String valorIndex : dadaIndexada.getValorIndexMultiple()) {
							text = variableHelper.getTextPerCamp(
									camp, 
									valorIndex, 
									null, 
									null, 
									expedient.getProcessInstanceId(),
									consultesDomini,
									dadaIndexada);
							valorsMostrar.add(text);
						}
						dadaIndexada.setValorMostrarMultiple(valorsMostrar);
					} else {
						text = variableHelper.getTextPerCamp(
								camp,
								dadaIndexada.getValorIndex(),
								null, 
								null, 
								expedient.getProcessInstanceId(),
								consultesDomini,
								dadaIndexada);	
						dadaIndexada.setValorMostrar(text);
					}
				} 
			} else { 
				if (!dadesExpedient.containsKey(camp.getCodi())) {
					dadesExpedient.put(camp.getCodi(), new DadaIndexadaDto(camp.getCodi(), camp.getEtiqueta()));
				}
				dadesExpedient.get(camp.getCodi()).setValorMostrar(getValueCampExpedient(expedient, camp.getCodi()));
			}
		}
		variableHelper.consultaDominisAgrupats(consultesDomini);
	}
	
	public List<Camp> toListCamp(List<TascaDadaDto> listTascaDadaDto) {
		List<Camp> listCamp = new ArrayList<Camp>();
		for (TascaDadaDto tascaDadaDto : listTascaDadaDto) {
			Optional<Camp> optionalCamp = campRepository.findById(tascaDadaDto.getCampId());
			Camp camp = optionalCamp.isPresent() ? optionalCamp.get() : null;
			if (camp == null) {
				camp = new Camp(
						tascaDadaDto.getVarCodi(),
						conversioTipusHelper.convertir(tascaDadaDto.getCampTipus(), TipusCamp.class),
						tascaDadaDto.getCampEtiqueta()  == null ? tascaDadaDto.getVarCodi() : tascaDadaDto.getCampEtiqueta());
			}
			listCamp.add(camp);
		}
		return listCamp;
	}
}
