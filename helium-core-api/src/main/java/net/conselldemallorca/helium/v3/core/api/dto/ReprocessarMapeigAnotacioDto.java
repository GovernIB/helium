package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReprocessarMapeigAnotacioDto implements Serializable {

	private static final long serialVersionUID = 7886427551884321139L;
	
	private boolean reprocessarMapeigVariables		= true;
	private boolean reprocessarMapeigDocuments		= true;
	private boolean reprocessarMapeigAdjunts		= true;
	private boolean reprocessarMapeigInteressats 	= true;
	private List<Long> idsAnotacions;
	
	public boolean isReprocessarMapeigVariables() {
		return reprocessarMapeigVariables;
	}
	public void setReprocessarMapeigVariables(boolean reprocessarMapeigVariables) {
		this.reprocessarMapeigVariables = reprocessarMapeigVariables;
	}
	public boolean isReprocessarMapeigDocuments() {
		return reprocessarMapeigDocuments;
	}
	public void setReprocessarMapeigDocuments(boolean reprocessarMapeigDocuments) {
		this.reprocessarMapeigDocuments = reprocessarMapeigDocuments;
	}
	public boolean isReprocessarMapeigAdjunts() {
		return reprocessarMapeigAdjunts;
	}
	public void setReprocessarMapeigAdjunts(boolean reprocessarMapeigAdjunts) {
		this.reprocessarMapeigAdjunts = reprocessarMapeigAdjunts;
	}
	public List<Long> getIdsAnotacions() {
		return idsAnotacions;
	}
	public void setIdsAnotacions(List<Long> idsAnotacions) {
		if (idsAnotacions!=null) this.idsAnotacions = idsAnotacions; else this.idsAnotacions = new ArrayList<Long>();
	}
	public boolean isReprocessarMapeigInteressats() {
		return reprocessarMapeigInteressats;
	}
	public void setReprocessarMapeigInteressats(boolean reprocessarMapeigInteressats) {
		this.reprocessarMapeigInteressats = reprocessarMapeigInteressats;
	}
	
}