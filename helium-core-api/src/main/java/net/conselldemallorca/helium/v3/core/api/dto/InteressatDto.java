/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalTipus;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatDto {
	
	private Long id;
	private String codi;
	//private String nif;
	private String documentIdent;

	private String dir3Codi;
	private String nom;
	private String llinatge1;  
	private String llinatge2;  

	private String email;  
	private String telefon;
	private Long expedientId;
	private InteressatTipusEnumDto tipus;
	private String tipusNom;
	
	private Boolean entregaPostal;
	private EntregaPostalTipus entregaTipus;
	private String linia1;
	private String linia2;
	private String codiPostal;
	private Boolean entregaDeh;
	private Boolean entregaDehObligat;
	
	private String observacions;
	private String tipusDocIdent;
	private String codiDire;
	private String direccio;
	private String raoSocial;
    private Boolean es_representant = false;
	private String municipi;
	private String pais;
	private String provincia;
    private List<InteressatDto> representat; //només existeix quan es_representant=true
    private InteressatDto representant; //només existeix quan es_representant=false
    private String canalNotif; 
    private boolean teRepresentant;
    private Long representant_id;
    private boolean existeixenRepresentantsExpedient;
    private String municipiNom;
    private String paisNom;
    private String provinciaNom;
    private Long representantSeleccionatId;
    private boolean propagatArxiu = true;

	public InteressatTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(InteressatTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
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

	public Boolean getEntregaPostal() {
		return entregaPostal != null ? entregaPostal : false;
	}
	public void setEntregaPostal(Boolean entregaPostal) {
		this.entregaPostal = entregaPostal;
	}
	public EntregaPostalTipus getEntregaTipus() {
		return entregaTipus;
	}
	public void setEntregaTipus(EntregaPostalTipus entregaTipus) {
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
	public Boolean getEntregaDeh() {
		return entregaDeh != null ? entregaDeh : false;
	}
	public void setEntregaDeh(Boolean entregaDeh) {
		this.entregaDeh = entregaDeh;
	}
	public Boolean getEntregaDehObligat() {
		return entregaDehObligat != null ? entregaDehObligat : false;
	}
	public void setEntregaDehObligat(Boolean entregaDehObligat) {
		this.entregaDehObligat = entregaDehObligat;
	}
	public String getFullNom() {
		StringBuilder fullNom = new StringBuilder();
		if(nom!=null) {
			fullNom.append(nom);
		}
		if (llinatge1 != null)
			fullNom.append(" ").append(llinatge1);
		if (llinatge2 != null)
			fullNom.append(" ").append(llinatge2);
		if (raoSocial != null)
			fullNom.append(" ").append(raoSocial);
		return fullNom.toString();
	}
	
	public String getRepresentantFullNom() {
		if(!isEs_representant()  &&  representant!=null) {
			StringBuilder representantFullNom = new StringBuilder();
			if(representant.getNom()!=null) {
				representantFullNom.append(representant.getNom());
			}
			if (representant.getLlinatge1() != null)
				representantFullNom.append(" ").append(representant.getLlinatge1());
			if (representant.getLlinatge2() != null)
				representantFullNom.append(" ").append(representant.getLlinatge2());
			if (representant.getRaoSocial() != null)
				representantFullNom.append(" ").append(representant.getRaoSocial());
//			if(InteressatTipusEnumDto.ADMINISTRACIO.equals(representant.getTipus()))
//					representantFullNom.append(" ").append(representant.getDocumentIdent());
			return representantFullNom.toString();
		}
		return null;
	}
	
	public boolean isTeRepresentant() {
		if(representant!=null)
			return true;
		else
			return false;
	}
	
	public boolean getTeRepresentant() {
		if(representant!=null)
			return true;
		else
			return false;
	}

	public void setTeRepresentant(boolean teRepresentant) {
		this.teRepresentant = teRepresentant;
	}

	public boolean isExisteixenRepresentantsExpedient() {
		return existeixenRepresentantsExpedient;
	}
	public void setExisteixenRepresentantsExpedient(boolean existeixenRepresentantsExpedient) {
		this.existeixenRepresentantsExpedient = existeixenRepresentantsExpedient;
	}
	public String getFullInfo() {
		String codiDocument;
		if (tipus != null && InteressatTipusEnumDto.ADMINISTRACIO.equals(tipus))
			codiDocument = (dir3Codi!=null ? dir3Codi+ " - " : "" )+ documentIdent;
		else {
			if(es_representant) {
				return documentIdent + " - " + getFullNom() + " (Representant)";
			} else {
				if(representant!=null) {//Si té representant indiquem quin és
					return documentIdent + " - " + getFullNom() + " (Representat per " + representant.getDocumentIdent() + ")";
				} else {
					codiDocument = documentIdent;
				}			
			}
		}
			
		return codiDocument + " - " + getFullNom();
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
	public Boolean getEs_representant() {
		return es_representant != null ? es_representant : false;
	}
	public void setEs_representant(Boolean es_representant) {
		this.es_representant = es_representant;
	}
	public void setEs_representant(boolean es_representant) {
		this.es_representant = es_representant;
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
	
	public List<InteressatDto> getRepresentat() {
		return representat;
	}
	public void setRepresentat(List<InteressatDto> representat) {
		this.representat = representat;
	}
	public InteressatDto getRepresentant() {
		return representant;
	}
	public void setRepresentant(InteressatDto representant) {
		this.representant = representant;
	}
	public String getCanalNotif() {
		return canalNotif;
	}
	public void setCanalNotif(String canalNotif) {
		this.canalNotif = canalNotif;
	}
	public Long getRepresentant_id() {
		if(representant!=null)
			return representant.getId();
		else
			return null;
	}
	public void setRepresentant_id(Long representant_id) {
		this.representant_id = representant_id;
	}
	public String getMunicipiNom() {
		return municipiNom;
	}
	public void setMunicipiNom(String municipiNom) {
		this.municipiNom = municipiNom;
	}
	public String getPaisNom() {
		return paisNom;
	}
	public void setPaisNom(String paisNom) {
		this.paisNom = paisNom;
	}
	public String getProvinciaNom() {
		return provinciaNom;
	}
	public void setProvinciaNom(String provinciaNom) {
		this.provinciaNom = provinciaNom;
	}
	public Long getRepresentantSeleccionatId() {
		return representantSeleccionatId;
	}
	public void setRepresentantSeleccionatId(Long representantSeleccionatId) {
		this.representantSeleccionatId = representantSeleccionatId;
	}
	public String getTipusNom() {
		return tipusNom;
	}
	public void setTipusNom(String tipusNom) {
		this.tipusNom = tipusNom;
	}
	public boolean isPropagatArxiu() {
		return propagatArxiu;
	}
	public void setPropagatArxiu(boolean propagatArxiu) {
		this.propagatArxiu = propagatArxiu;
	}
	

}
