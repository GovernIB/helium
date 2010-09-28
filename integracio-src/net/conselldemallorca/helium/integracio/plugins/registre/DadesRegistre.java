/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

import java.io.Serializable;

/**
 * Classe que representa les dades per fer operacions amb el registre
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DadesRegistre implements Serializable {

	private String numero;
	private String any;

	private String dataEntrada;
	private String horaEntrada;
	private String dataSortida;
	private String horaSortida;
	private String oficina;
	private String oficinaFisica;
	private String data;
	private String tipus;
	private String idioma;
	private String remitentEntitat1;
	private String remitentEntitat2;
	private String remitentAltres;
	private String destinatariEntitat1;
	private String destinatariEntitat2;
	private String destinatariAltres;
	private String procedenciaBalears;
	private String procedenciaFora;
	private String destiBalears;
	private String destiFora;
	private String sortida1;
	private String sortida2;
	private String entrada1;
	private String entrada2;
	private String destinatari;
	private String remitent;
	private String idiomaExtracte;
	private String extracte;



	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getRemitentEntitat1() {
		return remitentEntitat1;
	}
	public void setRemitentEntitat1(String remitentEntitat1) {
		this.remitentEntitat1 = remitentEntitat1;
	}
	public String getRemitentEntitat2() {
		return remitentEntitat2;
	}
	public void setRemitentEntitat2(String remitentEntitat2) {
		this.remitentEntitat2 = remitentEntitat2;
	}
	public String getRemitentAltres() {
		return remitentAltres;
	}
	public void setRemitentAltres(String remitentAltres) {
		this.remitentAltres = remitentAltres;
	}
	public String getDestinatariEntitat1() {
		return destinatariEntitat1;
	}
	public void setDestinatariEntitat1(String destinatariEntitat1) {
		this.destinatariEntitat1 = destinatariEntitat1;
	}
	public String getDestinatariEntitat2() {
		return destinatariEntitat2;
	}
	public void setDestinatariEntitat2(String destinatariEntitat2) {
		this.destinatariEntitat2 = destinatariEntitat2;
	}
	public String getDestinatariAltres() {
		return destinatariAltres;
	}
	public void setDestinatariAltres(String destinatariAltres) {
		this.destinatariAltres = destinatariAltres;
	}
	public String getDestiBalears() {
		return destiBalears;
	}
	public void setDestiBalears(String destiBalears) {
		this.destiBalears = destiBalears;
	}
	public String getDestiFora() {
		return destiFora;
	}
	public void setDestiFora(String destiFora) {
		this.destiFora = destiFora;
	}
	public String getAny() {
		return any;
	}
	public void setAny(String any) {
		this.any = any;
	}
	public String getDataEntrada() {
		return dataEntrada;
	}
	public void setDataEntrada(String dataEntrada) {
		this.dataEntrada = dataEntrada;
	}
	public String getHoraEntrada() {
		return horaEntrada;
	}
	public void setHoraEntrada(String horaEntrada) {
		this.horaEntrada = horaEntrada;
	}
	public String getDataSortida() {
		return dataSortida;
	}
	public void setDataSortida(String dataSortida) {
		this.dataSortida = dataSortida;
	}
	public String getHoraSortida() {
		return horaSortida;
	}
	public void setHoraSortida(String horaSortida) {
		this.horaSortida = horaSortida;
	}
	public String getOficina() {
		return oficina;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public String getOficinaFisica() {
		return oficinaFisica;
	}
	public void setOficinaFisica(String oficinaFisica) {
		this.oficinaFisica = oficinaFisica;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	public String getProcedenciaBalears() {
		return procedenciaBalears;
	}
	public void setProcedenciaBalears(String procedenciaBalears) {
		this.procedenciaBalears = procedenciaBalears;
	}
	public String getProcedenciaFora() {
		return procedenciaFora;
	}
	public void setProcedenciaFora(String procedenciaFora) {
		this.procedenciaFora = procedenciaFora;
	}
	public String getEntrada1() {
		return entrada1;
	}
	public void setEntrada1(String entrada1) {
		this.entrada1 = entrada1;
	}
	public String getEntrada2() {
		return entrada2;
	}
	public void setEntrada2(String entrada2) {
		this.entrada2 = entrada2;
	}
	public String getSortida1() {
		return sortida1;
	}
	public void setSortida1(String sortida1) {
		this.sortida1 = sortida1;
	}
	public String getSortida2() {
		return sortida2;
	}
	public void setSortida2(String sortida2) {
		this.sortida2 = sortida2;
	}
	public String getRemitent() {
		return remitent;
	}
	public void setRemitent(String remitent) {
		this.remitent = remitent;
	}
	public String getDestinatari() {
		return destinatari;
	}
	public void setDestinatari(String destinatari) {
		this.destinatari = destinatari;
	}
	public String getIdiomaExtracte() {
		return idiomaExtracte;
	}
	public void setIdiomaExtracte(String idiomaExtracte) {
		this.idiomaExtracte = idiomaExtracte;
	}
	public String getExtracte() {
		return extracte;
	}
	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}



	private static final long serialVersionUID = 1L;

}
