package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.api.dto.ExpedientDto;
import org.jbpm.graph.exe.ExecutionContext;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.Interessat;

/**
 * Handler per crear un interessat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings({"serial", "unused"})
public class InteressatCrearHandler extends BasicActionHandler implements InteressatCrearHandlerInterface {


	private String codi;
	private String varCodi;
	
	private String nom;
	private String varNom;
	
	private String nif;
	private String varNif;

	private String dir3Codi;
	private String varDir3Codi;
	
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
	
	private String entregaPostal;
	private String varEntregaPostal;
	
	private String entregaTipus;
	private String varEntregaTipus;
	
	private String linia1;
	private String varLinia1;
	
	private String linia2;
	private String varLinia2;
	
	private String codiPostal;
	private String varCodiPostal;
	
	private String entregaDeh;
	private String varEntregaDeh;
	
	private String entregaDehObligat;
	private String varEntregaDehObligat;
	
	
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
		Boolean entregaPostalVal = getValorOVariableBoolean(
				executionContext,
				entregaPostal,
				varEntregaPostal);
		interessat.setEntregaPostal(entregaPostalVal != null? entregaPostalVal.booleanValue() : false);
		interessat.setEntregaTipus((String)getValorOVariable(
				executionContext,
				entregaTipus,
				varEntregaTipus));
		interessat.setLinia1((String)getValorOVariable(
				executionContext,
				linia1,
				varLinia1));
		interessat.setLinia2((String)getValorOVariable(
				executionContext,
				linia2,
				varLinia2));
		Boolean entregaDehVal = getValorOVariableBoolean(
				executionContext,
				entregaDeh,
				varEntregaDeh);
		interessat.setEntregaDeh(entregaDehVal != null ? entregaDehVal.booleanValue() : false);
		Boolean entregaDehObligatVal = getValorOVariableBoolean(
				executionContext,
				entregaDehObligat,
				varEntregaDehObligat);
		interessat.setEntregaDehObligat(entregaDehObligatVal != null ? entregaDehObligatVal.booleanValue() : false);
		interessat.setCodiPostal((String)getValorOVariable(
				executionContext, 
				codiPostal, 
				varCodiPostal));
		interessat.setExpedientId(expedient.getId());
	
		interessatCrear(interessat);
		
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
	public String getDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(String dir3Codi) {
		this.dir3Codi = dir3Codi;
	}
	public String getVarDir3Codi() {
		return varDir3Codi;
	}
	public void setVarDir3Codi(String varDir3Codi) {
		this.varDir3Codi = varDir3Codi;
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
	public void setEntregaPostal(String entregaPostal) {
		this.entregaPostal = entregaPostal;
	}
	public void setVarEntregaPostal(String varEntregaPostal) {
		this.varEntregaPostal = varEntregaPostal;
	}
	public void setEntregaTipus(String entregaTipus) {
		this.entregaTipus = entregaTipus;
	}
	public void setVarEntregaTipus(String varEntregaTipus) {
		this.varEntregaTipus = varEntregaTipus;
	}
	public void setLinia1(String linia1) {
		this.linia1 = linia1;
	}
	public void setVarLinia1(String varLinia1) {
		this.varLinia1 = varLinia1;
	}
	public void setLinia2(String linia2) {
		this.linia2 = linia2;
	}
	public void setCodiPostal(String codiPostal) {
		this.codiPostal = codiPostal;
	}
	public void setVarCodiPostal(String varCodiPostal) {
		this.varCodiPostal = varCodiPostal;
	}
	public void setVarLinia2(String varLinia2) {
		this.varLinia2 = varLinia2;
	}
	public void setEntregaDeh(String entregaDeh) {
		this.entregaDeh = entregaDeh;
	}
	public void setVarEntregaDeh(String varEntregaDeh) {
		this.varEntregaDeh = varEntregaDeh;
	}
	public void setEntregaDehObligat(String entregaDehObligat) {
		this.entregaDehObligat = entregaDehObligat;
	}
	public void setVarEntregaDehObligat(String varEntregaDehObligat) {
		this.varEntregaDehObligat = varEntregaDehObligat;
	}
}