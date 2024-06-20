package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DocumentFinalitzarDto implements Serializable {

	private static final long serialVersionUID = -4395368227590555770L;
	private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	private Long documentStoreId;
	private Date dataCreacio;
	@SuppressWarnings("unused")
	private String dataCreacioStr;
	private String arxiuNom;
	private String documentCodi; //Codi del document a la DP o TE
	private boolean adjunt; //(Document / Adjunt)
//	En el cas dels tipus que no són per estat: Procés principal / Procés
//	Anotacio (Posar el número d'anotació) (avís vermell si no s'han mogut)
//	Notificació (Quan tenen una notificació)
//	Petició Pinbal 
	private String processInstanceId;
	private String processInstanceNom;
	
	boolean seleccionat;
	
	private Long annexAnotacioId;
	private String anotacioDesc;
	boolean anotacioAnnexNoMogut = true;
	boolean firmaInvalida;
	private Long notificacioId;
	private String notificacioDesc;
	private Long peticioPinbalId;
	private String peticioPinbalDesc;
	
	private String arxiuUuid;
	private ArxiuEstat arxiuEstat;
	
	public Long getDocumentStoreId() {
		return documentStoreId;
	}
	public void setDocumentStoreId(Long documentStoreId) {
		this.documentStoreId = documentStoreId;
	}
	public String getDocumentCodi() {
		return documentCodi;
	}
	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}
	public boolean isSeleccionat() {
		return seleccionat;
	}
	public void setSeleccionat(boolean seleccionat) {
		this.seleccionat = seleccionat;
	}
	public boolean isAnotacioAnnexNoMogut() {
		return anotacioAnnexNoMogut;
	}
	public void setAnotacioAnnexNoMogut(boolean anotacioAnnexNoMogut) {
		this.anotacioAnnexNoMogut = anotacioAnnexNoMogut;
	}
	public String getDataCreacioStr() {
		if (dataCreacio!=null) {
			return df.format(dataCreacio);
		} else {
			return "";
		}
	}
	public boolean isAdjunt() {
		return adjunt;
	}
	public void setAdjunt(boolean adjunt) {
		this.adjunt = adjunt;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getProcessInstanceNom() {
		return processInstanceNom;
	}
	public void setProcessInstanceNom(String processInstanceNom) {
		this.processInstanceNom = processInstanceNom;
	}
	public String getAnotacioDesc() {
		return anotacioDesc;
	}
	public void setAnotacioDesc(String anotacioDesc) {
		this.anotacioDesc = anotacioDesc;
	}
	public boolean isFirmaInvalida() {
		return firmaInvalida;
	}
	public void setFirmaInvalida(boolean firmaInvalida) {
		this.firmaInvalida = firmaInvalida;
	}
	public String getNotificacioDesc() {
		return notificacioDesc;
	}
	public void setNotificacioDesc(String notificacioDesc) {
		this.notificacioDesc = notificacioDesc;
	}
	public String getPeticioPinbalDesc() {
		return peticioPinbalDesc;
	}
	public void setPeticioPinbalDesc(String peticioPinbalDesc) {
		this.peticioPinbalDesc = peticioPinbalDesc;
	}
	public String getArxiuUuid() {
		return arxiuUuid;
	}
	public void setArxiuUuid(String arxiuUuid) {
		this.arxiuUuid = arxiuUuid;
	}
	public ArxiuEstat getArxiuEstat() {
		return arxiuEstat;
	}
	public void setArxiuEstat(ArxiuEstat arxiuEstat) {
		this.arxiuEstat = arxiuEstat;
	}
	public void setDataCreacioStr(String dataCreacioStr) {
		this.dataCreacioStr = dataCreacioStr;
	}
	public Long getAnnexAnotacioId() {
		return annexAnotacioId;
	}
	public void setAnnexAnotacioId(Long annexAnotacioId) {
		this.annexAnotacioId = annexAnotacioId;
	}
	public Long getNotificacioId() {
		return notificacioId;
	}
	public void setNotificacioId(Long notificacioId) {
		this.notificacioId = notificacioId;
	}
	public Long getPeticioPinbalId() {
		return peticioPinbalId;
	}
	public void setPeticioPinbalId(Long peticioPinbalId) {
		this.peticioPinbalId = peticioPinbalId;
	}
	@Override
	public String toString() {
		return "DocumentFinalitzarDto [documentStoreId=" + documentStoreId + ", documentCodi=" + documentCodi + ", arxiuNom=" + arxiuNom + ", seleccionat="
				+ seleccionat + ", arxiuUuid=" + arxiuUuid + ", arxiuEstat=" + arxiuEstat + "]";
	}
	
}