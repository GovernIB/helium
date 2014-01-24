package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;

public interface ReassignacioUsuarisService {

	public abstract List<ReassignacioDto> llistaReassignacions();

	public abstract List<ReassignacioDto> llistaReassignacions(Long expedientTipusId);

	public abstract List<ReassignacioDto> llistaReassignacionsMod(Long id);

	public abstract void createReassignacio(String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId);

	public abstract void updateReassignacio(Long id, String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId);

	public abstract void deleteReassignacio(Long id);

	public abstract ReassignacioDto findReassignacioById(Long id);
}