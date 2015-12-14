/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.datatables;

import java.util.List;

/**
 * Informació per a mostrar una pàgina amb DataTables.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DatatablesPagina<T> {

	private long iTotalRecords;
	private long iTotalDisplayRecords;
	private long sEcho;
	private List<Object[]> aaData;
	private String missatgeAlerta;

	public long getiTotalRecords() {
		return iTotalRecords;
	}
	public void setiTotalRecords(long iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
	}
	public long getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}
	public void setiTotalDisplayRecords(long iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}
	public long getsEcho() {
		return sEcho;
	}
	public void setsEcho(long sEcho) {
		this.sEcho = sEcho;
	}
	public List<Object[]> getAaData() {
		return aaData;
	}
	public void setAaData(List<Object[]> aaData) {
		this.aaData = aaData;
	}
	public String getMissatgeAlerta() {
		return missatgeAlerta;
	}
	public void setMissatgeAlerta(String missatgeAlerta) {
		this.missatgeAlerta = missatgeAlerta;
	}

}
