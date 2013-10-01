/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.datatables;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Paràmetres enviats per Datatable per a fer una consulta
 * paginada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DatatablesInfo {

	private int displayStart;
	private int displayLength;
	private int columns;
	private String searchGlobal;
	private boolean regexGlobal;
	private int sortingCols;
	private long echo;
	private boolean[] searchable;
	private String[] search;
	private boolean[] regex;
	private boolean[] sortable;
	private int[] sortCol;
	private String[] sortDir;
	private String[] dataProp;
	private String[] property;
	public DatatablesInfo(HttpServletRequest request) {
		if (request.getParameter("iDisplayStart") != null)
			displayStart = Integer.parseInt(request.getParameter("iDisplayStart"));
		if (request.getParameter("iDisplayLength") != null)
			displayLength = Integer.parseInt(request.getParameter("iDisplayLength"));
		if (request.getParameter("iColumns") != null)
			columns = Integer.parseInt(request.getParameter("iColumns"));
		searchGlobal = request.getParameter("sSearch");
		if (request.getParameter("bRegex") != null)
			regexGlobal = Boolean.parseBoolean(request.getParameter("bRegex"));
		if (request.getParameter("iSortingCols") != null)
			sortingCols = Integer.parseInt(request.getParameter("iSortingCols"));
		if (request.getParameter("sEcho") != null)
			echo = Long.parseLong(request.getParameter("sEcho"));
		searchable = new boolean[columns];
		search = new String[columns];
		regex = new boolean[columns];
		sortable = new boolean[columns];
		sortCol = new int[columns];
		sortDir = new String[columns];
		dataProp = new String[columns];
		property = new String[columns];
		for (int i = 0; i < columns; i++) {
			if (request.getParameter("bSearchable_" + i) != null)
				searchable[i] = Boolean.parseBoolean(request.getParameter("bSearchable_" + i));
			search[i] = request.getParameter("sSearch_" + i);
			if (request.getParameter("bRegex_" + i) != null)
				regex[i] = Boolean.parseBoolean(request.getParameter("bRegex_" + i));
			if (request.getParameter("bSortable_" + i) != null)
				sortable[i] = Boolean.parseBoolean(request.getParameter("bSortable_" + i));
			if (request.getParameter("iSortCol_" + i) != null)
				sortCol[i] = Integer.parseInt(request.getParameter("iSortCol_" + i));
			sortDir[i] = request.getParameter("sSortDir_" + i);
			dataProp[i] = request.getParameter("mDataProp_" + i);
			property[i] = request.getParameter("aProp_" + i);
		}
	}
	public int getDisplayStart() {
		return displayStart;
	}
	public void setDisplayStart(int displayStart) {
		this.displayStart = displayStart;
	}
	public int getDisplayLength() {
		return displayLength;
	}
	public void setDisplayLength(int displayLength) {
		this.displayLength = displayLength;
	}
	public int getColumns() {
		return columns;
	}
	public void setColumns(int columns) {
		this.columns = columns;
	}
	public String getSearchGlobal() {
		return searchGlobal;
	}
	public void setSearchGlobal(String searchGlobal) {
		this.searchGlobal = searchGlobal;
	}
	public boolean isRegexGlobal() {
		return regexGlobal;
	}
	public void setRegexGlobal(boolean regexGlobal) {
		this.regexGlobal = regexGlobal;
	}
	public long getEcho() {
		return echo;
	}
	public void setEcho(long echo) {
		this.echo = echo;
	}
	public int getSortingCols() {
		return sortingCols;
	}
	public void setSortingCols(int sortingCols) {
		this.sortingCols = sortingCols;
	}
	public boolean[] getSearchable() {
		return searchable;
	}
	public void setSearchable(boolean[] searchable) {
		this.searchable = searchable;
	}
	public String[] getSearch() {
		return search;
	}
	public void setSearch(String[] search) {
		this.search = search;
	}
	public boolean[] getRegex() {
		return regex;
	}
	public void setRegex(boolean[] regex) {
		this.regex = regex;
	}
	public boolean[] getSortable() {
		return sortable;
	}
	public void setSortable(boolean[] sortable) {
		this.sortable = sortable;
	}
	public int[] getSortCol() {
		return sortCol;
	}
	public void setSortCol(int[] sortCol) {
		this.sortCol = sortCol;
	}
	public String[] getSortDir() {
		return sortDir;
	}
	public void setSortDir(String[] sortDir) {
		this.sortDir = sortDir;
	}
	public String[] getDataProp() {
		return dataProp;
	}
	public void setDataProp(String[] dataProp) {
		this.dataProp = dataProp;
	}
	public String[] getProperty() {
		return property;
	}
	public void setProperty(String[] property) {
		this.property = property;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
