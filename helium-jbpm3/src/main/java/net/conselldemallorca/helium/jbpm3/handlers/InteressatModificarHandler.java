package net.conselldemallorca.helium.jbpm3.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.security.crypto.codec.Base64;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesEnviament;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Interessat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.PersonaInfo;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaNotificacio;

/**
 * Handler per crear un interessat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings({"serial", "unused"})
public class InteressatModificarHandler extends BasicActionHandler implements InteressatModificarHandlerInterface {

	
	private String codi;
	private String varCodi;
	
	private String nom;
	private String varNom;
	
	private String nif;
	private String varNif;
	
	private String llinatge1;  
	private String varLlinatge1; 
	
	private String llinatge2;  
	private String varLlinatge2;  
	
	private String tipus; 
	private String varTipus;
	
	private String email;  
	private String varEmail; 
	
	private String telefon;
	private String varTelefon;
	
	
	
	public void execute(ExecutionContext executionContext) {
		
		ExpedientDto expedient = getExpedientActual(executionContext);
		
		Interessat interessat = new Interessat();
		
		interessat.setCodi((String)getValorOVariable(
				executionContext,
				codi,
				varCodi));
		interessat.setNom((String)getValorOVariable(
				executionContext,
				nom,
				varNom));
		interessat.setNif((String)getValorOVariable(
				executionContext,
				nif,
				varNif));
		interessat.setLlinatge1((String)getValorOVariable(
				executionContext,
				llinatge1,
				varLlinatge1));
		interessat.setLlinatge2((String)getValorOVariable(
				executionContext,
				llinatge2,
				varLlinatge2));
		interessat.setTipus((String)getValorOVariable(
				executionContext,
				tipus,
				varTipus));
		interessat.setEmail((String)getValorOVariable(
				executionContext,
				email,
				varEmail));
		interessat.setTelefon((String)getValorOVariable(
				executionContext,
				telefon,
				varTelefon));
		
		interessat.setExpedientId(expedient.getId());
	
		interessatModificar(interessat);
		
	}


	public void setCodi(String codi) {
		this.codi = codi;
	}
	public void setVarCodi(String varCodi) {
		this.varCodi = varCodi;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public void setVarNom(String varNom) {
		this.varNom = varNom;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public void setVarNif(String varNif) {
		this.varNif = varNif;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public void setVarLlinatge1(String varLlinatge1) {
		this.varLlinatge1 = varLlinatge1;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	public void setVarLlinatge2(String varLlinatge2) {
		this.varLlinatge2 = varLlinatge2;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public void setVarTipus(String varTipus) {
		this.varTipus = varTipus;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setVarEmail(String varEmail) {
		this.varEmail = varEmail;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	public void setVarTelefon(String varTelefon) {
		this.varTelefon = varTelefon;
	}
	
	
}