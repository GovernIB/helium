<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

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
			<ul id="dropdown-menu-${tasca.id}" class="dropdown-menu">
				<c:if test="${tasca.oberta and not tasca.suspesa}">
					<c:if test="${tasca.responsableCodi == dadesPersona.codi}">
						<li><a id="tramitar-tasca-${tasca.id}" href="../v3/expedient/${expedient.id}/tasca/${tasca.id}" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true"><span class="fa fa-folder-open"></span> <spring:message code="tasca.llistat.accio.tramitar"/></a></li>
						<c:if test="${tasca.tramitacioMassiva}">
							<li><a href="../v3/tasca/${tasca.id}/massiva"><span class="fa fa-files-o"></span> <spring:message code="tasca.llistat.accio.tramitar_massivament"/></a></li>
						</c:if>
					</c:if>
					<c:if test="${not tasca.agafada and not empty tasca.responsables and tasca.assignadaPersona}">
						<li><a data-rdt-link-ajax=true data-rdt-link-callback="agafar(${tasca.id});" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.agafar"/>" href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/agafar"><span class="fa fa-chain"></span>&nbsp;<spring:message code="tasca.llistat.accio.agafar"/></a></li>
					</c:if>
					<c:if test="${expedient.permisSupervision}"><li><a href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/suspendre" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.suspendre"/>"><span class="fa fa-pause"></span> <spring:message code="tasca.llistat.accio.suspendre"/></a></li></c:if>
				</c:if>
				<li><a href="<c:url value="../v3/expedient/${expedient.id}"/>" class="consultar-expedient"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.llistat.accio.consultar.expedient"/></a></li>
				<c:if test="${tasca.oberta}">
					<c:if test="${expedient.permisReassignment}"><li><a href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/reassignar" data-rdt-link-modal="true"><span class="fa fa-share-square-o"></span> <spring:message code="tasca.llistat.accio.reassignar"/></a></li></c:if>
				</c:if>
				<c:if test="${tasca.suspesa}">
					<c:if test="${expedient.permisSupervision}"><li><a href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/reprendre" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.reprendre"/>"><span class="fa fa-play"></span> <spring:message code="tasca.llistat.accio.reprendre"/></a></li></c:if>
				</c:if>
				<c:if test="${not tasca.completed and not tasca.cancelada}">
					<c:if test="${expedient.permisSupervision}"><li><a href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/cancelar" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.cancelar"/>"><span class="fa fa-times"></span> <spring:message code="tasca.llistat.accio.cancelar"/></a></li></c:if>
				</c:if>
				<c:if test="${not empty tasca.responsables and tasca.responsableCodi == dadesPersona.codi and tasca.oberta}">
					<c:if test="${expedient.permisSupervision}"><li><a data-rdt-link-ajax=true data-rdt-link-callback="alliberar(${tasca.id});" href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/alliberar" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.alliberar"/>"><span class="fa fa-chain-broken"></span> <spring:message code="tasca.llistat.accio.alliberar"/></a></li></c:if>
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

<script type="text/javascript">
	// <![CDATA[
		$(document).ready(function() {
			$('#dropdown-menu-${tasca.id} a').heliumEvalLink({
				refrescarAlertes: true,
				refrescarPagina: false,
				alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
				maximize: true
			});
			$('#dropdown-menu-context-${tasca.id} a').heliumEvalLink({
				refrescarAlertes: true,
				refrescarPagina: false,
				alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
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
