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
public class SeientRegistral implements Serializable {

	private String numero;
	private String any;
	private String data;
	private String hora;
	private String oficina;
	private String oficinaFisica;
	private RegistreFont remitent;
	private RegistreFont destinatari;
	private RegistreDocument document;



	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getAny() {
		return any;
	}
	public void setAny(String any) {
		this.any = any;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
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
	public RegistreFont getRemitent() {
		return remitent;
	}
	public void setRemitent(RegistreFont remitent) {
		this.remitent = remitent;
	}
	public RegistreFont getDestinatari() {
		return destinatari;
	}
	public void setDestinatari(RegistreFont destinatari) {
		this.destinatari = destinatari;
	}
	public RegistreDocument getDocument() {
		return document;
	}
	public void setDocument(RegistreDocument document) {
		this.document = document;
	}



	private static final long serialVersionUID = 1L;

}
