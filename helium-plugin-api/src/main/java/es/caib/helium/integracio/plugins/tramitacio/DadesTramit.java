/**
 * 
 */
package es.caib.helium.integracio.plugins.tramitacio;

import java.util.Date;
import java.util.List;


/**
 * 
 * @author Limit Tecnologies
 */
public class DadesTramit {

	protected String numero;
	protected long clauAcces;
	protected String identificador;
	protected long unitatAdministrativa;
	protected int versio;
	protected Date data;
	protected String idioma;
	protected String registreNumero;
	protected Date registreData;
	protected String preregistreTipusConfirmacio;
	protected String preregistreNumero;
	protected Date preregistreData;
	protected AutenticacioTipus autenticacioTipus;
	protected String tramitadorNif;
	protected String tramitadorNom;
	protected String interessatNif;
	protected String interessatNom;
	protected String representantNif;
	protected String representantNom;
	protected boolean signat;
	protected boolean avisosHabilitats;
	protected String avisosSms;
	protected String avisosEmail;
	protected boolean notificacioTelematicaHabilitada;
	protected List<DocumentTramit> documents;

	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public long getClauAcces() {
		return clauAcces;
	}
	public void setClauAcces(long clauAcces) {
		this.clauAcces = clauAcces;
	}
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public long getUnitatAdministrativa() {
		return unitatAdministrativa;
	}
	public void setUnitatAdministrativa(long unitatAdministrativa) {
		this.unitatAdministrativa = unitatAdministrativa;
	}
	public int getVersio() {
		return versio;
	}
	public void setVersio(int versio) {
		this.versio = versio;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}
	public Date getRegistreData() {
		return registreData;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}
	public String getPreregistreTipusConfirmacio() {
		return preregistreTipusConfirmacio;
	}
	public void setPreregistreTipusConfirmacio(String preregistreTipusConfirmacio) {
		this.preregistreTipusConfirmacio = preregistreTipusConfirmacio;
	}
	public String getPreregistreNumero() {
		return preregistreNumero;
	}
	public void setPreregistreNumero(String preregistreNumero) {
		this.preregistreNumero = preregistreNumero;
	}
	public Date getPreregistreData() {
		return preregistreData;
	}
	public void setPreregistreData(Date preregistreData) {
		this.preregistreData = preregistreData;
	}
	public AutenticacioTipus getAutenticacioTipus() {
		return autenticacioTipus;
	}
	public void setAutenticacioTipus(AutenticacioTipus autenticacioTipus) {
		this.autenticacioTipus = autenticacioTipus;
	}
	public String getTramitadorNif() {
		return tramitadorNif;
	}
	public void setTramitadorNif(String tramitadorNif) {
		this.tramitadorNif = tramitadorNif;
	}
	public String getTramitadorNom() {
		return tramitadorNom;
	}
	public void setTramitadorNom(String tramitadorNom) {
		this.tramitadorNom = tramitadorNom;
	}
	public String getInteressatNif() {
		return interessatNif;
	}
	public void setInteressatNif(String interessatNif) {
		this.interessatNif = interessatNif;
	}
	public String getInteressatNom() {
		return interessatNom;
	}
	public void setInteressatNom(String interessatNom) {
		this.interessatNom = interessatNom;
	}
	public String getRepresentantNif() {
		return representantNif;
	}
	public void setRepresentantNif(String representantNif) {
		this.representantNif = representantNif;
	}
	public String getRepresentantNom() {
		return representantNom;
	}
	public void setRepresentantNom(String representantNom) {
		this.representantNom = representantNom;
	}
	public boolean isSignat() {
		return signat;
	}
	public void setSignat(boolean signat) {
		this.signat = signat;
	}
	public boolean isAvisosHabilitats() {
		return avisosHabilitats;
	}
	public void setAvisosHabilitats(boolean avisosHabilitats) {
		this.avisosHabilitats = avisosHabilitats;
	}
	public String getAvisosSms() {
		return avisosSms;
	}
	public void setAvisosSms(String avisosSms) {
		this.avisosSms = avisosSms;
	}
	public String getAvisosEmail() {
		return avisosEmail;
	}
	public void setAvisosEmail(String avisosEmail) {
		this.avisosEmail = avisosEmail;
	}
	public boolean isNotificacioTelematicaHabilitada() {
		return notificacioTelematicaHabilitada;
	}
	public void setNotificacioTelematicaHabilitada(
			boolean notificacioTelematicaHabilitada) {
		this.notificacioTelematicaHabilitada = notificacioTelematicaHabilitada;
	}
	public List<DocumentTramit> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentTramit> documents) {
		this.documents = documents;
	}

	@Override
	public String toString() {
		return "DadesTramit [numero=" + numero + ", clauAcces=" + clauAcces + ", identificador=" + identificador + ", unitatAdministrativa=" + unitatAdministrativa + ", versio=" + versio + ", data=" + data + ", idioma=" + idioma + ", registreNumero=" + registreNumero + ", registreData=" + registreData + ", preregistreTipusConfirmacio=" + preregistreTipusConfirmacio + ", preregistreNumero=" + preregistreNumero + ", preregistreData=" + preregistreData + ", autenticacioTipus=" + autenticacioTipus + ", tramitadorNif=" + tramitadorNif + ", tramitadorNom=" + tramitadorNom + ", interessatNif=" + interessatNif + ", interessatNom=" + interessatNom + ", representantNif=" + representantNif + ", representantNom=" + representantNom + ", signat=" + signat + ", avisosHabilitats=" + avisosHabilitats
				+ ", avisosSms=" + avisosSms + ", avisosEmail=" + avisosEmail + ", notificacioTelematicaHabilitada=" + notificacioTelematicaHabilitada + ", documents=" + documents + "]";
	}
}
