/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.conselldemallorca.helium.integracio.plugins.registre.DadesRegistre;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a interactuar amb el registre de sortida.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class RegistreSortidaHandler extends AbstractHeliumActionHandler {

	private String dataSortida;
	private String varDataSortida;
	private String horaSortida;
	private String varHoraSortida;
	private String oficina;
	private String varOficina;
	private String oficinaFisica;
	private String varOficinaFisica;
	private String data;
	private String varData;
	private String tipus;
	private String varTipus;
	private String idioma;
	private String varIdioma;
	private String destinatariEntitat1;
	private String varDestinatariEntitat1;
	private String destinatariEntitat2;
	private String varDestinatariEntitat2;
	private String destinatariAltres;
	private String varDestinatariAltres;
	private String destiBalears;
	private String varDestiBalears;
	private String destiFora;
	private String varDestiFora;
	private String entrada1;
	private String varEntrada1;
	private String entrada2;
	private String varEntrada2;
	private String remitent;
	private String varRemitent;
	private String idiomaExtracte;
	private String varIdiomaExtracte;
	private String extracte;
	private String varExtracte;

	private String varNumero;
	private String varAny;
	private String varNumeroAny;



	public void execute(ExecutionContext executionContext) throws Exception {
		if (!getPluginRegistreService().isRegistreActiu())
			throw new JbpmException("El plugin de registre no està configurat");
		String[] resultat = this.getPluginRegistreService().registrarSortida(
				getDadesRegistreSortida(executionContext));
		if (varNumero != null && !"".equals(varNumero))
			executionContext.setVariable(varNumero, resultat[0]);
		if (varAny != null && !"".equals(varAny))
			executionContext.setVariable(varAny, resultat[1]);
		if (varNumeroAny != null && !"".equals(varNumeroAny))
			executionContext.setVariable(varNumeroAny, resultat[0] + "/" + resultat[1]);
	}



	public void setDataSortida(String dataSortida) {
		this.dataSortida = dataSortida;
	}
	public void setVarDataSortida(String varDataSortida) {
		this.varDataSortida = varDataSortida;
	}
	public void setHoraSortida(String horaSortida) {
		this.horaSortida = horaSortida;
	}
	public void setVarHoraSortida(String varHoraSortida) {
		this.varHoraSortida = varHoraSortida;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public void setVarOficina(String varOficina) {
		this.varOficina = varOficina;
	}
	public void setOficinaFisica(String oficinaFisica) {
		this.oficinaFisica = oficinaFisica;
	}
	public void setVarOficinaFisica(String varOficinaFisica) {
		this.varOficinaFisica = varOficinaFisica;
	}
	public void setData(String data) {
		this.data = data;
	}
	public void setVarData(String varData) {
		this.varData = varData;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public void setVarTipus(String varTipus) {
		this.varTipus = varTipus;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	public void setVarIdioma(String varIdioma) {
		this.varIdioma = varIdioma;
	}
	public void setDestinatariEntitat1(String destinatariEntitat1) {
		this.destinatariEntitat1 = destinatariEntitat1;
	}
	public void setVarDestinatariEntitat1(String varDestinatariEntitat1) {
		this.varDestinatariEntitat1 = varDestinatariEntitat1;
	}
	public void setDestinatariEntitat2(String destinatariEntitat2) {
		this.destinatariEntitat2 = destinatariEntitat2;
	}
	public void setVarDestinatariEntitat2(String varDestinatariEntitat2) {
		this.varDestinatariEntitat2 = varDestinatariEntitat2;
	}
	public void setDestinatariAltres(String destinatariAltres) {
		this.destinatariAltres = destinatariAltres;
	}
	public void setVarDestinatariAltres(String varDestinatariAltres) {
		this.varDestinatariAltres = varDestinatariAltres;
	}
	public void setDestiBalears(String destiBalears) {
		this.destiBalears = destiBalears;
	}
	public void setVarDestiBalears(String varDestiBalears) {
		this.varDestiBalears = varDestiBalears;
	}
	public void setDestiFora(String destiFora) {
		this.destiFora = destiFora;
	}
	public void setVarDestiFora(String varDestiFora) {
		this.varDestiFora = varDestiFora;
	}
	public void setEntrada1(String entrada1) {
		this.entrada1 = entrada1;
	}
	public void setVarEntrada1(String varEntrada1) {
		this.varEntrada1 = varEntrada1;
	}
	public void setEntrada2(String entrada2) {
		this.entrada2 = entrada2;
	}
	public void setVarEntrada2(String varEntrada2) {
		this.varEntrada2 = varEntrada2;
	}
	public void setRemitent(String remitent) {
		this.remitent = remitent;
	}
	public void setVarRemitent(String varRemitent) {
		this.varRemitent = varRemitent;
	}
	public void setIdiomaExtracte(String idiomaExtracte) {
		this.idiomaExtracte = idiomaExtracte;
	}
	public void setVarIdiomaExtracte(String varIdiomaExtracte) {
		this.varIdiomaExtracte = varIdiomaExtracte;
	}
	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}
	public void setVarExtracte(String varExtracte) {
		this.varExtracte = varExtracte;
	}

	public void setVarNumero(String varNumero) {
		this.varNumero = varNumero;
	}
	public void setVarAny(String varAny) {
		this.varAny = varAny;
	}
	public void setVarNumeroAny(String varNumeroAny) {
		this.varNumeroAny = varNumeroAny;
	}



	private DadesRegistre getDadesRegistreSortida(ExecutionContext executionContext) {
		DadesRegistre dades = new DadesRegistre();
		Date ara = new Date();
		if (	(dataSortida == null || "".equals(dataSortida)) &&
				(varDataSortida == null || "".equals(varDataSortida))) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			dades.setDataSortida(df.format(ara));
		} else {
			dades.setDataSortida((String)getValorOVariable(executionContext, dataSortida, varDataSortida));
		}
		if (	(horaSortida == null || "".equals(horaSortida)) &&
				(varHoraSortida == null || "".equals(varHoraSortida))) {
			DateFormat df = new SimpleDateFormat("HH:mm");
			dades.setHoraSortida(df.format(ara));
		} else {
			dades.setHoraSortida((String)getValorOVariable(executionContext, horaSortida, varHoraSortida));
		}
		dades.setOficina((String)getValorOVariable(executionContext, oficina, varOficina));
		dades.setOficinaFisica((String)getValorOVariable(executionContext, oficinaFisica, varOficinaFisica));
		dades.setData((String)getValorOVariable(executionContext, data, varData));
		dades.setTipus((String)getValorOVariable(executionContext, tipus, varTipus));
		dades.setIdioma((String)getValorOVariable(executionContext, idioma, varIdioma));
		dades.setDestinatariEntitat1((String)getValorOVariable(executionContext, destinatariEntitat1, varDestinatariEntitat1));
		dades.setDestinatariEntitat2((String)getValorOVariable(executionContext, destinatariEntitat2, varDestinatariEntitat2));
		dades.setDestinatariAltres((String)getValorOVariable(executionContext, destinatariAltres, varDestinatariAltres));
		dades.setDestiBalears((String)getValorOVariable(executionContext, destiBalears, varDestiBalears));
		dades.setDestiFora((String)getValorOVariable(executionContext, destiFora, varDestiFora));
		dades.setEntrada1((String)getValorOVariable(executionContext, entrada1, varEntrada1));
		dades.setEntrada2((String)getValorOVariable(executionContext, entrada2, varEntrada2));
		dades.setRemitent((String)getValorOVariable(executionContext, remitent, varRemitent));
		dades.setIdiomaExtracte((String)getValorOVariable(executionContext, idiomaExtracte, varIdiomaExtracte));
		dades.setExtracte((String)getValorOVariable(executionContext, extracte, varExtracte));
		return dades;
	}

}
