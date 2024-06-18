package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DocumentFinalitzarDto implements Serializable {

	private static final long serialVersionUID = -4395368227590555770L;
	private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	private Long documentStoreId;
	private String varCodi;
	private Date dataCreacio;
	@SuppressWarnings("unused")
	private String dataCreacioStr;
	private String arxiuNom;
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
	private Long notificacioId;
	private String notificacioDesc;
	private Long peticioPinbalId;
	private String peticioPinbalDesc;
	
	private String arxiuUuid;
	
	public Long getDocumentStoreId() {
		return documentStoreId;
	}
	public void setDocumentStoreId(Long documentStoreId) {
		this.documentStoreId = documentStoreId;
	}
	public String getVarCodi() {
		return varCodi;
	}
	public void setVarCodi(String varCodi) {
		this.varCodi = varCodi;
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
		return "DocumentFinalitzarDto [documentStoreId=" + documentStoreId + ", varCodi=" + varCodi + ", arxiuNom=" + arxiuNom + ", seleccionat="
				+ seleccionat + ", annexAnotacioId=" + annexAnotacioId + "]";
	}
	
}