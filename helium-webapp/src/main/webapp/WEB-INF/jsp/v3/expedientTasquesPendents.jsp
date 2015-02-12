<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:choose>
	<c:when test="${not empty tasques}">
		<tr class="table-pendents header">
			<td class="first nodata"></td>
			<td class="nodata"></td>
			<td class="maxcols"><spring:message code="expedient.tasca.columna.tasca.activa"/></td>
			<td class="datacol"><spring:message code="expedient.tasca.columna.asignada_a"/></td>			
			<td class="datacol"><spring:message code="expedient.tasca.columna.datcre"/></td>
			<td class="datacol"><spring:message code="expedient.tasca.columna.datlim"/></td>
			<td class="options"></td>
		</tr>
		<c:forEach var="tasca" items="${tasques}" varStatus="index">
			<tr id="table-pendents-${tasca.id}" class="table-pendents link-tramitacio-modal <c:if test="${index.last}">fin-table-pendents</c:if>">
				<%@ include file="expedientTascaPendent.jsp" %>
			</tr>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<tr class="table-pendents fin-table-pendents">
			<td id="td_nohiha" colspan="2"></td>
			<td colspan="100%" class="no-datacol">
				<div class="well-small"><spring:message code="expedient.tasca.activa.nohiha"/></div>
			</td>
		</tr>
	</c:otherwise>
</c:choose>
<style type="text/css">
	.table-pendents {
		background-color: rgba(0, 0, 0, 0);
	}
	.table-pendents.header td,.table-pendents.header td:HOVER {
		background-color: #428bca !important; color: white !important;padding-top: 4px;padding-bottom: 4px;padding-left: 8px;
	}
	.fin-table-pendents td.maxcols,.fin-table-pendents td.datacol,.fin-table-pendents td.options, .fin-table-pendents td.no-datacol {
		border-bottom: 2px solid #428bca !important;
	}	
	.table-pendents td.nodata,.table-pendents td.nodata:HOVER, .table-pendents.header td.nodata,.table-pendents.header td.nodata:HOVER {
		background-color: white !important;
		border: 0px solid !important;
	}
</style>

<script type="text/javascript">
	// <![CDATA[		
		function agafar(tascaId, correcte) {
			if (correcte) {
				var url = '<c:url value="/nodeco/v3/expedient/${expedientId}/tascaPendent/'+tascaId+'"/>';
				var panell = $("#table-pendents-"+tascaId);
				panell.load(url, function() {
					$('#dropdown-menu-'+tascaId+' #tramitar-tasca-'+tascaId).click();
				});
			}
		}

		function alliberar(tascaId, correcte) {
			if (correcte) {	
				var url = '<c:url value="/nodeco/v3/expedient/${expedientId}/tascaPendent/'+tascaId+'"/>';
				var panell = $("#table-pendents-"+tascaId);
				panell.load(url, function() {});
			}
		}

		$(document).ready(function() {
			$('[title]').tooltip({container: 'body'});
		});
	//]]>
</script>
