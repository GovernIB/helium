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
			<td class="maxcols"><spring:message code="expedient.tasca.columna.tasca"/></td>
			<td class="datacol"><spring:message code="expedient.tasca.columna.datcre"/></td>
			<td class="datacol"><spring:message code="expedient.tasca.columna.datlim"/></td>
			<td class="options"></td>
		</tr>
		<c:forEach var="tasca" items="${tasques}" varStatus="index">
			<tr id="table-pendents-${tasca.id}" class="table-pendents link-tramitacio-modal <c:if test="${index.last}">fin-table-pendents</c:if>">
				<td class="first nodata"></td>
				<td class="nodata"></td>
				<td class="maxcols">
					${tasca.titol}
					<c:if test="${not tasca.agafada && not empty tasca.responsables}">
						<span class="fa fa-users"></span>
					</c:if>
					<div class="pull-right">
						<c:if test="${tasca.cancelada}">
							<span class="label label-danger" title="<spring:message code="enum.tasca.etiqueta.CA"/>">CA</span>
						</c:if>
						<c:if test="${tasca.suspesa}">
							<span class="label label-info" title="<spring:message code="enum.tasca.etiqueta.SU"/>">SU</span>
						</c:if>
						<c:if test="${tasca.oberta}">
							<span class="label label-warning" title="<spring:message code="enum.tasca.etiqueta.PD"/>"></span>
						</c:if>
						<c:if test="${tasca.completed}">
							<span class="label label-success" title="<spring:message code="enum.tasca.etiqueta.FI"/>">FI</span>
						</c:if>
						<c:if test="${tasca.agafada}">
							<span class="label label-default" title="<spring:message code="enum.tasca.etiqueta.AG"/>">AG</span>
						</c:if>
						<c:if test="${not tasca.completed and tasca.tramitacioMassiva}">
							<a href="../v3/tasca/${tasca.id}/massiva"><span class="label label-default" title="<spring:message code="tasca.llistat.accio.tramitar_massivament"/>"><i class="fa fa-files-o"></i></span></a>
						</c:if>
					</div> 
				</td>
				<td class="datacol"><fmt:formatDate value="${tasca.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></td>
				<td class="datacol"><fmt:formatDate value="${tasca.dataLimit}" pattern="dd/MM/yyyy"/></td>
				<td class="options">
					<c:if test='${not tasca.completed}'>
						<div class="btn-group">
							<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/> <span class="caret"></span></a>
							<ul class="dropdown-menu">
								<c:if test="${tasca.oberta and not tasca.suspesa}">
									<c:if test="${tasca.responsableCodi == dadesPersona.codi}">
										<li><a href="../v3/expedient/${expedient.id}/tasca/${tasca.id}" class="icon" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true"><span class="fa fa-folder-open"></span> <spring:message code="tasca.llistat.accio.tramitar"/></a></li>
										<c:if test="${tasca.tramitacioMassiva}">
											<li><a href="..v3/tasca/${tasca.id}/massiva"><span class="fa fa-files-o"></span> <spring:message code="tasca.llistat.accio.tramitar_massivament"/></a></li>
										</c:if>
									</c:if>
									<c:if test="${not tasca.agafada and not empty tasca.responsables and tasca.assignadaPersona}">
										<li><a href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/agafar"/>"><span class="fa fa-chain"></span> Agafar</a></li>
									</c:if>
									<c:if test="${expedient.permisSupervision}"><li><a href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/suspendre" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.suspendre"/>" data-rdt-link-modal="true"><span class="fa fa-pause"></span> <spring:message code="tasca.llistat.accio.suspendre"/></a></li></c:if>
								</c:if>
								<c:if test="${tasca.oberta}">
									<c:if test="${expedient.permisReassignment}"><li><a href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/reassignar" class="icon" data-rdt-link-modal="true"><span class="fa fa-share-square-o"></span> <spring:message code="tasca.llistat.accio.reassignar"/></a></li></c:if>
								</c:if>
								<c:if test="${tasca.suspesa}">
									<c:if test="${expedient.permisSupervision}"><li><a href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/reprendre" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.reprendre"/>"><span class="fa fa-play"></span> <spring:message code="tasca.llistat.accio.reprendre"/></a></li></c:if>
								</c:if>
								<c:if test="${not tasca.completed and not tasca.cancelada}">
									<c:if test="${expedient.permisSupervision}"><li><a href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/cancelar" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.cancelar"/>"><span class="fa fa-times"></span> <spring:message code="tasca.llistat.accio.cancelar"/></a></li></c:if>
								</c:if>
								<c:if test="${not empty tasca.responsables and tasca.responsableCodi == dadesPersona.codi and tasca.oberta}">
									<c:if test="${expedient.permisSupervision}"><li><a href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/alliberar" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.alliberar"/>"><span class="fa fa-chain-broken"></span> <spring:message code="tasca.llistat.accio.alliberar"/></a></li></c:if>
								</c:if>											
							</ul>
							<script type="text/javascript">
								$("#table-pendents-${tasca.id}").append('<ul class="dropdown-menu" id="dropdown-menu-context-${tasca.id}" style="display:none">'+$("#table-pendents-${tasca.id}").find('.dropdown-menu').html()+'</ul>');
								$("#table-pendents-${tasca.id}").contextMenu({
								    menuSelector: "#dropdown-menu-context-${tasca.id}",
								    menuSelected: function (invokedOn, selectedMenu) {
								        // alert(selectedMenu.text() + " > " + invokedOn.text());
								    }
								});
							</script>
						</div>
					</c:if>
				</td>
			</tr>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<tr class="table-pendents fin-table-pendents">
			<td id="td_nohiha" colspan="2"></td>
			<td colspan="100%" class="no-datacol">
				<div class="well-small"><spring:message code="expedient.tasca.nohiha"/></div>
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
		$(document).ready(function() {
			$('.link-tramitacio-modal a').heliumEvalLink({
				refrescarAlertes: true,
				refrescarPagina: false,
				maximize: true
			});
		});
		var maxcol = $("#taulaDades thead th").length - 5;
		if($("#taulaDades thead th ").find('input[type="checkbox"]').is(':hidden')) {
			$(".first").hide();
			$("#td_nohiha").attr('colspan', 1);
		} else {
			$("#td_nohiha").attr('colspan', 2);
		}
		$(".maxcols").attr('colspan', maxcol);
		$(".datacol").attr('colspan', (maxcol > 7) ? 2 : 1);
	//]]>
</script>
