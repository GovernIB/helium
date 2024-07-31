/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.tipus;


/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Interessat {

	private Long id;
	private String codi;
//	private String nif; //Si falla el handler veuere aquí
	private String documentIdent;

	private String dir3Codi;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String tipus;
	private String email;
	private String telefon;
	private String codiDesti;
	private Long expedientId;
	private boolean entregaPostal;
	private String entregaTipus;
	private String linia1;
	private String linia2;
	private String codiPostal;
	private boolean entregaDeh;
	private boolean entregaDehObligat;
	
	private String observacions;
	private String tipusDocIdent;
	private String codiDire;
	private String direccio;
	private String raoSocial;
    private boolean es_representant = false;
	private String municipiCodi;
	private String paisCodi;
	private String provinciaCodi;
	private String municipi;
	private String pais;
	private String provincia;
    private Interessat representat; //només existeix quan es_representant=true
    private Interessat representant; //només existeix quan es_representant=false

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
//	public String getNif() {
//		return nif;
//	}
//	public void setNif(String nif) {
//		this.nif = nif;
//	}
	public String getDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(String dir3Codi) {
		this.dir3Codi = dir3Codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	public String getCodiDesti() {
		return codiDesti;
	}
	public void setCodiDesti(String codiDesti) {
		this.codiDesti = codiDesti;
	}
	public boolean isEntregaPostal() {
		return entregaPostal;
	}
	public void setEntregaPostal(boolean entregaPostal) {
		this.entregaPostal = entregaPostal;
	}
	public String getEntregaTipus() {
		return entregaTipus;
	}
	public void setEntregaTipus(String entregaTipus) {
		this.entregaTipus = entregaTipus;
	}
	public String getLinia1() {
		return linia1;
	}
	public void setLinia1(String linia1) {
		this.linia1 = linia1;
	}
	public String getLinia2() {
		return linia2;
	}
	public void setLinia2(String linia2) {
		this.linia2 = linia2;
	}
	public String getCodiPostal() {
		return codiPostal;
	}
	public void setCodiPostal(String codiPostal) {
		this.codiPostal = codiPostal;
	}
	public boolean isEntregaDeh() {
		return entregaDeh;
	}
	public void setEntregaDeh(boolean entregaDeh) {
		this.entregaDeh = entregaDeh;
	}
	public boolean isEntregaDehObligat() {
		return entregaDehObligat;
	}
	public void setEntregaDehObligat(boolean entregaDehObligat) {
		this.entregaDehObligat = entregaDehObligat;
	}
	public String getDocumentIdent() {
		return documentIdent;
	}
	public void setDocumentIdent(String documentIdent) {
		this.documentIdent = documentIdent;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public String getTipusDocIdent() {
		return tipusDocIdent;
	}
	public void setTipusDocIdent(String tipusDocIdent) {
		this.tipusDocIdent = tipusDocIdent;
	}
	public String getCodiDire() {
		return codiDire;
	}
	public void setCodiDire(String codiDire) {
		this.codiDire = codiDire;
	}
	public String getDireccio() {
		return direccio;
	}
	public void setDireccio(String direccio) {
		this.direccio = direccio;
	}
	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(String raoSocial) {
		this.raoSocial = raoSocial;
	}
	public boolean isEs_representant() {
		return es_representant;
	}
	public boolean getEs_representant() {
		return es_representant;
	}
	public void setEs_representant(Boolean es_representant) {
		this.es_representant = es_representant;
	}
	public String getMunicipiCodi() {
		return municipiCodi;
	}
	public void setMunicipiCodi(String municipiCodi) {
		this.municipiCodi = municipiCodi;
	}
	public String getPaisCodi() {
		return paisCodi;
	}
	public void setPaisCodi(String paisCodi) {
		this.paisCodi = paisCodi;
	}
	public String getProvinciaCodi() {
		return provinciaCodi;
	}
	public void setProvinciaCodi(String provinciaCodi) {
		this.provinciaCodi = provinciaCodi;
	}
	public String getMunicipi() {
		return municipi;
	}
	public void setMunicipi(String municipi) {
		this.municipi = municipi;
	}
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public Interessat getRepresentat() {
		return representat;
	}
	public void setRepresentat(Interessat representat) {
		this.representat = representat;
	}
	public Interessat getRepresentant() {
		return representant;
	}
	public void setRepresentant(Interessat representant) {
		this.representant = representant;
	}
	

}
