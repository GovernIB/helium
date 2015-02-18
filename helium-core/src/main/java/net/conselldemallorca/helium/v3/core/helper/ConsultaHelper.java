package net.conselldemallorca.helium.v3.core.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusParamConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaCampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;

import org.springframework.stereotype.Component;

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
	private AdminService adminService;
	@Resource(name="serviceUtilsV3")
	private ServiceUtils serviceUtils;
	@Resource
	private CampRepository campRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ConsultaCampRepository consultaCampRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;

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
		for (ConsultaCamp camp: camps) {
			TascaDadaDto tascaDadaDto = null;
			DefinicioProces definicioProces = null;
			Camp campRes = null;
			if (camp.getCampCodi().startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
				campRes = getCampExpedient(camp.getCampCodi());
			} else {
				definicioProces = definicioProcesRepository.findByJbpmKeyAndVersio(
						camp.getDefprocJbpmKey(),
						camp.getDefprocVersio());
				if (definicioProces != null) {
					campRes = campRepository.findByDefinicioProcesAndCodi(
							definicioProces,
							camp.getCampCodi());
				}
			}
			if (campRes != null) {
				tascaDadaDto = variableHelper.getTascaDadaDtoParaConsultaDisseny(campRes,tipus);
			} else {
				tascaDadaDto = getTascaDadaPerCampsConsulta(camp);
			}
			if (definicioProces != null) {
				tascaDadaDto.setDefinicioProcesKey(definicioProces.getJbpmKey());
			}
			resposta.add(tascaDadaDto);
		}
		return resposta;
	}
	
	private TascaDadaDto getTascaDadaPerCampsConsulta(ConsultaCamp camp) {
		TascaDadaDto tascaDadaDto = null;
		if (TipusConsultaCamp.PARAM.equals(camp.getTipus())) {
			if (TipusParamConsultaCamp.SENCER.equals(camp.getParamTipus())) {
				tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.INTEGER, camp.getCampCodi()));
			} else if (TipusParamConsultaCamp.FLOTANT.equals(camp.getParamTipus())) {
				tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.FLOAT, camp.getCampCodi()));
			} else if (TipusParamConsultaCamp.DATA.equals(camp.getParamTipus())) {
				tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.DATE, camp.getCampCodi()));
			} else if (TipusParamConsultaCamp.BOOLEAN.equals(camp.getParamTipus())) {
				tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.BOOLEAN, camp.getCampCodi()));
			} else {
				tascaDadaDto = (new TascaDadaDto(camp.getCampCodi(), CampTipusDto.STRING, camp.getCampCodi()));
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
			PersonaDto persona = adminService.findPersonaAmbCodi(expedient.getIniciadorCodi());
			if (persona != null)
				text = persona.getNomSencer();
			else
				text = expedient.getIniciadorCodi();
		} else if (ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE.equals(campCodi)) {
			PersonaDto persona = adminService.findPersonaAmbCodi(expedient.getResponsableCodi());
			if (persona != null)
				text = persona.getNomSencer();
			else 
				text = expedient.getResponsableCodi();
		} else if (ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI.equals(campCodi)) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			text = df.format(expedient.getDataInici());
		} else if (ExpedientCamps.EXPEDIENT_CAMP_ESTAT_JSP.equals(campCodi)) {
			if (expedient.getEstat() != null)
				text = expedient.getEstat().getNom();
			else if (expedient.getDataFi() != null)
				text = messageHelper.getMessage("expedient.consulta.finalitzat");
			else
				text = messageHelper.getMessage("expedient.consulta.iniciat");	
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
		}
		return campExpedient;
	}

	public void revisarDadesExpedientAmbValorsEnumeracionsODominis(
			Map<String, DadaIndexadaDto> dadesExpedient,
			List<Camp> campsInforme, 
			Expedient expedient) {
		String dadaIndexadaClau = null;
		for (Camp camp: campsInforme) {
			if (camp != null && camp.getDefinicioProces() != null) {
				dadaIndexadaClau = camp.getDefinicioProces().getJbpmKey() + "/" + camp.getCodi();
				if (!dadesExpedient.containsKey(dadaIndexadaClau)) {
					dadesExpedient.put(dadaIndexadaClau, new DadaIndexadaDto(camp.getCodi(), camp.getEtiqueta()));
				}
				DadaIndexadaDto dadaIndexada = dadesExpedient.get(dadaIndexadaClau);
				if (camp.getEnumeracio() != null && camp.getDefinicioProces() != null) {
					String text = variableHelper.getTextPerCamp(
							camp,
							dadaIndexada.getValorIndex(),
							null,
							null,
							expedient.getProcessInstanceId());	
					dadaIndexada.setValorMostrar(text);
				} 
			} else { 
				if (!dadesExpedient.containsKey(camp.getCodi())) {
					dadesExpedient.put(camp.getCodi(), new DadaIndexadaDto(camp.getCodi(), camp.getEtiqueta()));
				}
				dadesExpedient.get(camp.getCodi()).setValorMostrar(getValueCampExpedient(expedient, camp.getCodi()));
			}
		}
	}
	
	public List<Camp> toListCamp(List<TascaDadaDto> listTascaDadaDto) {
		List<Camp> listCamp = new ArrayList<Camp>();
		for (TascaDadaDto tascaDadaDto : listTascaDadaDto) {
			Camp camp = campRepository.findById(tascaDadaDto.getCampId());
			if (camp == null) {
				camp = new Camp(
						null,
						tascaDadaDto.getVarCodi(),
						conversioTipusHelper.convertir(tascaDadaDto.getCampTipus(), TipusCamp.class),
						tascaDadaDto.getCampEtiqueta()  == null ? tascaDadaDto.getVarCodi() : tascaDadaDto.getCampEtiqueta());
			}
			listCamp.add(camp);
		}
		return listCamp;
	}
}
