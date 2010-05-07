/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc.util;

import java.util.List;

import org.displaytag.properties.SortOrderEnum;

/**
 * Classe amb informació per a la paginació
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("unchecked")
public class PaginatedList implements org.displaytag.pagination.PaginatedList {

	private List list;
	private int fullListSize;
	private int objectsPerPage;
	private int pageNumber;
	private String searchId;
	private String sortCriterion;
	private SortOrderEnum sortDirection;



	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public int getFullListSize() {
		return fullListSize;
	}
	public void setFullListSize(int fullListSize) {
		this.fullListSize = fullListSize;
	}
	public int getObjectsPerPage() {
		return objectsPerPage;
	}
	public void setObjectsPerPage(int objectsPerPage) {
		this.objectsPerPage = objectsPerPage;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public String getSearchId() {
		return searchId;
	}
	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}
	public String getSortCriterion() {
		return sortCriterion;
	}
	public void setSortCriterion(String sortCriterion) {
		this.sortCriterion = sortCriterion;
	}
	public SortOrderEnum getSortDirection() {
		return sortDirection;
	}
	public void setSortDirection(SortOrderEnum sortDirection) {
		this.sortDirection = sortDirection;
	}

	public void setSortDirectionAsc() {
		this.sortDirection = SortOrderEnum.ASCENDING;
	}
	public void setSortDirectionDesc() {
		this.sortDirection = SortOrderEnum.DESCENDING;
	}

}
