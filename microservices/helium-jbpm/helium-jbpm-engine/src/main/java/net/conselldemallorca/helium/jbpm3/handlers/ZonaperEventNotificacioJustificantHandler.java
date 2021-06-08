package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Collections;

import es.caib.helium.api.dto.DetalleAvisoDto;
import es.caib.helium.api.dto.RespostaJustificantDetallRecepcioDto;
import es.caib.helium.api.dto.RespostaJustificantRecepcioDto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a interactuar amb el registre de sortida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings({"serial", "unused"})
public class ZonaperEventNotificacioJustificantHandler extends BasicActionHandler {

	private String numeroRegistre;
	private String varNumeroRegistre;
	private String detall;
	private String varDetall;
	private String varData;
	private String varTipoEstadoNotificacion;
	private String varFechaAcuseRecibo;
	private String varReferenciaRDSJustificanteClave;
	private String varReferenciaRDSJustificanteCodigo;
	private String varTipo;
	private String varDestinatario;
	private String varEnviado;
	private String varFechaEnvio;
	private String varConfirmarEnvio;
	private String varConfirmadoEnvio;

	public void execute(ExecutionContext executionContext) throws Exception {		
		boolean detalle = false;
		if (varDetall != null || !varDetall.isEmpty()) {
			String val = ((String)getValorOVariable(
					executionContext,
					detall,
					varDetall));
			detalle = (val != null && "true".equals(val));
		}
		
		String num = ((String)getValorOVariable(
				executionContext,
				numeroRegistre,
				varNumeroRegistre));
		
		if (detalle) {
			RespostaJustificantDetallRecepcioDto resposta = obtenirJustificantDetallRecepcio(num);
			logger.debug("RespostaJustificantDetallRecepcioDto: " + resposta);
			
			if (varData != null)
				executionContext.setVariable(
						varData,
						resposta.getData());
			if (varTipoEstadoNotificacion != null)
				executionContext.setVariable(
						varTipoEstadoNotificacion,
						resposta.getEstado().name());			
			if (varFechaAcuseRecibo != null)
				executionContext.setVariable(
						varFechaAcuseRecibo,
						resposta.getFechaAcuseRecibo());			
			if (varReferenciaRDSJustificanteClave != null)
				executionContext.setVariable(
						varReferenciaRDSJustificanteClave,
						resposta.getFicheroAcuseRecibo().getClave());
			if (varReferenciaRDSJustificanteCodigo != null)
				executionContext.setVariable(
						varReferenciaRDSJustificanteCodigo,
						resposta.getFicheroAcuseRecibo().getCodigo());
			
			// Última alerta
			if (!resposta.getAvisos().getAviso().isEmpty()) {
				Collections.sort(resposta.getAvisos().getAviso());
				DetalleAvisoDto detalleAviso = resposta.getAvisos().getAviso().get(0);
				
				if (varTipo != null)
					executionContext.setVariable(
							varTipo,
							detalleAviso.getTipo().name());				
				if (varDestinatario != null)
					executionContext.setVariable(
							varDestinatario,
							detalleAviso.getDestinatario());				
				if (varEnviado != null)
					executionContext.setVariable(
							varEnviado,
							detalleAviso.isEnviado());
				if (varConfirmarEnvio != null)
					executionContext.setVariable(
							varConfirmarEnvio,
							detalleAviso.isConfirmarEnvio());
				if (varConfirmadoEnvio != null)
					executionContext.setVariable(
							varConfirmadoEnvio,
							detalleAviso.getConfirmadoEnvio());				
				if (varFechaEnvio != null)
					executionContext.setVariable(
							varFechaEnvio,
							detalleAviso.getFechaEnvio());
			}			
		} else {
			RespostaJustificantRecepcioDto resposta = obtenirJustificantRecepcio(num);
			logger.debug("RespostaJustificantRecepcioDto: " + resposta);
			
			if (varData != null)
				executionContext.setVariable(
						varData,
						resposta.getData());
		}	
	}
	
	public void setNumeroRegistre(String numeroRegistre) {
		this.numeroRegistre = numeroRegistre;
	}
	public void setDetall(String detall) {
		this.detall = detall;
	}
	public void setVarData(String varData) {
		this.varData = varData;
	}
	public void setVarTipoEstadoNotificacion(String varTipoEstadoNotificacion) {
		this.varTipoEstadoNotificacion = varTipoEstadoNotificacion;
	}
	public void setVarFechaAcuseRecibo(String varFechaAcuseRecibo) {
		this.varFechaAcuseRecibo = varFechaAcuseRecibo;
	}
	public void setVarTipo(String varTipo) {
		this.varTipo = varTipo;
	}
	public void setVarDestinatario(String varDestinatario) {
		this.varDestinatario = varDestinatario;
	}
	public void setVarEnviado(String varEnviado) {
		this.varEnviado = varEnviado;
	}
	public void setVarFechaEnvio(String varFechaEnvio) {
		this.varFechaEnvio = varFechaEnvio;
	}
	public void setVarConfirmarEnvio(String varConfirmarEnvio) {
		this.varConfirmarEnvio = varConfirmarEnvio;
	}
	public void setVarConfirmadoEnvio(String varConfirmadoEnvio) {
		this.varConfirmadoEnvio = varConfirmadoEnvio;
	}
	public void setVarNumeroRegistre(String varNumeroRegistre) {
		this.varNumeroRegistre = varNumeroRegistre;
	}
	public void setVarDetall(String varDetall) {
		this.varDetall = varDetall;
	}
	public void setVarReferenciaRDSJustificanteClave(String varReferenciaRDSJustificanteClave) {
		this.varReferenciaRDSJustificanteClave = varReferenciaRDSJustificanteClave;
	}
	public void setVarReferenciaRDSJustificanteCodigo(String varReferenciaRDSJustificanteCodigo) {
		this.varReferenciaRDSJustificanteCodigo = varReferenciaRDSJustificanteCodigo;
	}
	
	private static final Log logger = LogFactory.getLog(ZonaperEventNotificacioJustificantHandler.class);
}
