/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalTipus;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDocumentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto;
import net.conselldemallorca.helium.webapp.v3.command.InteressatCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.InteressatCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.Interessat;

/**
 * Command que representa el formulari d'un entorn.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Interessat(groups = {Creacio.class, Modificacio.class})
public class InteressatCommand {

	private Long id;
	@NotEmpty(groups = {Creacio.class,  Modificacio.class})
	@Size(max = 64, groups = {Creacio.class,  Modificacio.class})
	private String codi;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String nom;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 9, groups = {Creacio.class, Modificacio.class})
	private String documentIdent;
	@Size(max = 9, groups = {Creacio.class, Modificacio.class})
	private String dir3Codi;
	private String llinatge1;  
	private String llinatge2;  
	private String representantSeleccionatId;
	private String email;  
	private Long expedientId;
	private String nifPersonaFisica;
	private String cifPersonaJuridica;
	private String cifOrganGestor;

	@NotNull(groups = { Creacio.class, Modificacio.class })
	private InteressatTipusEnumDto tipus;
	
	@NotNull(groups = { Creacio.class, Modificacio.class })
	private InteressatDocumentTipusEnumDto tipusDocIdent;
	
	private String canalNotif;
	
	private Boolean entregaPostal;
	private EntregaPostalTipus entregaTipus;
	@Size(max=50)
	private String linia1;
	@Size(max=50)
	private String linia2;
	@Size(max=100)
	private String direccio;
	@Size(max=5, groups = {Creacio.class, Modificacio.class})
	private String codiPostal;
	private Boolean entregaDeh;
	private Boolean entregaDehObligat;
	private String municipi;
	private String pais;
	private String provincia;
	private String observacions;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String raoSocial;
	private Boolean es_representant;
	private InteressatDto representant;
	private List<InteressatDto> representat;
	private String codiDire;
	
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
	public String getDocumentIdent() {
		return documentIdent;
	}
	public void setDocumentIdent(String documentIdent) {
		this.documentIdent = documentIdent;
	}
	public String getDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(String dir3Codi) {
		this.dir3Codi = dir3Codi;
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
	private String telefon; 

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
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public Boolean getEntregaPostal() {
		return entregaPostal;
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
		return entregaDeh;
	}
	public void setEntregaDeh(Boolean entregaDeh) {
		this.entregaDeh = entregaDeh;
	}
	public Boolean getEntregaDehObligat() {
		return entregaDehObligat;
	}
	public void setEntregaDehObligat(Boolean entregaDehObligat) {
		this.entregaDehObligat = entregaDehObligat;
	}
	public String getNifPersonaFisica() {
		return nifPersonaFisica;
	}
	public void setNifPersonaFisica(String nifPersonaFisica) {
		this.nifPersonaFisica = nifPersonaFisica;
	}
	public String getCifPersonaJuridica() {
		return cifPersonaJuridica;
	}
	public void setCifPersonaJuridica(String cifPersonaJuridica) {
		this.cifPersonaJuridica = cifPersonaJuridica;
	}
	public String getCifOrganGestor() {
		return cifOrganGestor;
	}
	public void setCifOrganGestor(String cifOrganGestor) {
		this.cifOrganGestor = cifOrganGestor;
	}

	public InteressatDocumentTipusEnumDto getTipusDocIdent() {
		return tipusDocIdent;
	}
	public void setTipusDocIdent(InteressatDocumentTipusEnumDto tipusDocIdent) {
		this.tipusDocIdent = tipusDocIdent;
	}
	public String getDireccio() {
		return direccio;
	}
	public void setDireccio(String direccio) {
		this.direccio = direccio;
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
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(String raoSocial) {
		this.raoSocial = raoSocial;
	}
	public Boolean getEs_representant() {
		return es_representant;
	}
	public void setEs_representant(Boolean es_representant) {
		this.es_representant = es_representant;
	}

	public InteressatDto getRepresentant() {
		return representant;
	}
	public void setRepresentant(InteressatDto representant) {
		this.representant = representant;
	}

	public List<InteressatDto> getRepresentat() {
		return representat;
	}
	public void setRepresentat(List<InteressatDto> representat) {
		this.representat = representat;
	}

	public String getCanalNotif() {
		return canalNotif;
	}
	public void setCanalNotif(String canalNotif) {
		this.canalNotif = canalNotif;
	}
	
	public String getCodiDire() {
		return codiDire;
	}
	public void setCodiDire(String codiDire) {
		this.codiDire = codiDire;
	}
	
	public String getRepresentantSeleccionatId() {
		return representantSeleccionatId;
	}
	public void setRepresentantSeleccionatId(String representantSeleccionatId) {
		this.representantSeleccionatId = representantSeleccionatId;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public interface Creacio {}
	public interface Modificacio {}

}
