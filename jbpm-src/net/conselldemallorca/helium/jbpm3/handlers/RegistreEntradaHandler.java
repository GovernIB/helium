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
 * Handler per a interactuar amb el registre d'entrada.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class RegistreEntradaHandler extends AbstractHeliumActionHandler {

	private String dataEntrada;
	private String varDataEntrada;
	private String horaEntrada;
	private String varHoraEntrada;
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
	private String remitentEntitat1;
	private String varRemitentEntitat1;
	private String remitentEntitat2;
	private String varRemitentEntitat2;
	private String remitentAltres;
	private String varRemitentAltres;
	private String procedenciaBalears;
	private String varProcedenciaBalears;
	private String procedenciaFora;
	private String varProcedenciaFora;
	private String sortida1;
	private String varSortida1;
	private String sortida2;
	private String varSortida2;
	private String destinatari;
	private String varDestinatari;
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
		String[] resultat = getPluginRegistreService().registrarEntrada(
				getDadesRegistreEntrada(executionContext));
		if (varNumero != null && !"".equals(varNumero))
			executionContext.setVariable(varNumero, resultat[0]);
		if (varAny != null && !"".equals(varAny))
			executionContext.setVariable(varAny, resultat[1]);
		if (varNumeroAny != null && !"".equals(varNumeroAny))
			executionContext.setVariable(varNumeroAny, resultat[0] + "/" + resultat[1]);
	}



	public void setDataEntrada(String dataEntrada) {
		this.dataEntrada = dataEntrada;
	}
	public void setVarDataEntrada(String varDataEntrada) {
		this.varDataEntrada = varDataEntrada;
	}
	public void setHoraEntrada(String horaEntrada) {
		this.horaEntrada = horaEntrada;
	}
	public void setVarHoraEntrada(String varHoraEntrada) {
		this.varHoraEntrada = varHoraEntrada;
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
	public void setRemitentEntitat1(String remitentEntitat1) {
		this.remitentEntitat1 = remitentEntitat1;
	}
	public void setVarRemitentEntitat1(String varRemitentEntitat1) {
		this.varRemitentEntitat1 = varRemitentEntitat1;
	}
	public void setRemitentEntitat2(String remitentEntitat2) {
		this.remitentEntitat2 = remitentEntitat2;
	}
	public void setVarRemitentEntitat2(String varRemitentEntitat2) {
		this.varRemitentEntitat2 = varRemitentEntitat2;
	}
	public void setRemitentAltres(String remitentEntitatAltres) {
		this.remitentAltres = remitentEntitatAltres;
	}
	public void setVarRemitentAltres(String varRemitentAltres) {
		this.varRemitentAltres = varRemitentAltres;
	}
	public void setProcedenciaBalears(String procedenciaBalears) {
		this.procedenciaBalears = procedenciaBalears;
	}
	public void setVarProcedenciaBalears(String varProcedenciaBalears) {
		this.varProcedenciaBalears = varProcedenciaBalears;
	}
	public void setProcedenciaFora(String procedenciaFora) {
		this.procedenciaFora = procedenciaFora;
	}
	public void setVarProcedenciaFora(String varProcedenciaFora) {
		this.varProcedenciaFora = varProcedenciaFora;
	}
	public void setSortida1(String sortida1) {
		this.sortida1 = sortida1;
	}
	public void setVarSortida1(String varSortida1) {
		this.varSortida1 = varSortida1;
	}
	public void setSortida2(String sortida2) {
		this.sortida2 = sortida2;
	}
	public void setVarSortida2(String varSortida2) {
		this.varSortida2 = varSortida2;
	}
	public void setDestinatari(String destinatari) {
		this.destinatari = destinatari;
	}
	public void setVarDestinatari(String varDestinatari) {
		this.varDestinatari = varDestinatari;
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



	private DadesRegistre getDadesRegistreEntrada(ExecutionContext executionContext) {
		DadesRegistre dades = new DadesRegistre();
		Date ara = new Date();
		if (	(dataEntrada == null || "".equals(dataEntrada)) &&
				(varDataEntrada == null || "".equals(varDataEntrada))) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			dades.setDataEntrada(df.format(ara));
		} else {
			dades.setDataEntrada((String)getValorOVariable(executionContext, dataEntrada, varDataEntrada));
		}
		if (	(horaEntrada == null || "".equals(horaEntrada)) &&
				(varHoraEntrada == null || "".equals(varHoraEntrada))) {
			DateFormat df = new SimpleDateFormat("HH:mm");
			dades.setHoraEntrada(df.format(ara));
		} else {
			dades.setHoraEntrada((String)getValorOVariable(executionContext, horaEntrada, varHoraEntrada));
		}
		dades.setOficina((String)getValorOVariable(executionContext, oficina, varOficina));
		dades.setOficinaFisica((String)getValorOVariable(executionContext, oficinaFisica, varOficinaFisica));
		dades.setData((String)getValorOVariable(executionContext, data, varData));
		dades.setTipus((String)getValorOVariable(executionContext, tipus, varTipus));
		dades.setIdioma((String)getValorOVariable(executionContext, idioma, varIdioma));
		dades.setRemitentEntitat1((String)getValorOVariable(executionContext, remitentEntitat1, varRemitentEntitat1));
		dades.setRemitentEntitat2((String)getValorOVariable(executionContext, remitentEntitat2, varRemitentEntitat2));
		dades.setRemitentAltres((String)getValorOVariable(executionContext, remitentAltres, varRemitentAltres));
		dades.setProcedenciaBalears((String)getValorOVariable(executionContext, procedenciaBalears, varProcedenciaBalears));
		dades.setProcedenciaFora((String)getValorOVariable(executionContext, procedenciaFora, varProcedenciaFora));
		dades.setSortida1((String)getValorOVariable(executionContext, sortida1, varSortida1));
		dades.setSortida2((String)getValorOVariable(executionContext, sortida2, varSortida2));
		dades.setDestinatari((String)getValorOVariable(executionContext, destinatari, varDestinatari));
		dades.setIdiomaExtracte((String)getValorOVariable(executionContext, idiomaExtracte, varIdiomaExtracte));
		dades.setExtracte((String)getValorOVariable(executionContext, extracte, varExtracte));
		return dades;
	}

}
