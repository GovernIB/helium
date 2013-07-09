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

	private long totalRecords;
	private long totalDisplayRecords;
	private long echo;
	private String[] columnNames;
	private List<T> data;

	private String missatgeAlerta;



	public long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}
	public long getTotalDisplayRecords() {
		return totalDisplayRecords;
	}
	public void setTotalDisplayRecords(long totalDisplayRecords) {
		this.totalDisplayRecords = totalDisplayRecords;
	}
	public long getEcho() {
		return echo;
	}
	public void setEcho(long echo) {
		this.echo = echo;
	}
	public String[] getColumnNames() {
		return columnNames;
	}
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	public String getMissatgeAlerta() {
		return missatgeAlerta;
	}
	public void setMissatgeAlerta(String missatgeAlerta) {
		this.missatgeAlerta = missatgeAlerta;
	}

}
