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
		<span class="fa fa-users" title="<spring:message code="enum.tasca.etiqueta.grup"/>"></span>
	</c:if>
	<div class="pull-right">
		<c:if test="${tasca.cancelled}">
			<span class="label label-danger" title="<spring:message code="enum.tasca.etiqueta.CA"/>">CA</span>
		</c:if>
		<c:if test="${tasca.suspended}">
			<span class="label label-info" title="<spring:message code="enum.tasca.etiqueta.SU"/>">SU</span>
		</c:if>
		<c:if test="${tasca.open}">
			<span class="label label-warning" title="<spring:message code="enum.tasca.etiqueta.PD"/>"></span>
		</c:if>
		<c:if test="${tasca.completed}">
			<span class="label label-success" title="<spring:message code="enum.tasca.etiqueta.FI"/>">FI</span>
		</c:if>
		<c:if test="${tasca.agafada}">
			<span class="label label-default" title="<spring:message code="enum.tasca.etiqueta.AG"/>">AG</span>
		</c:if>
		<c:if test="${not tasca.completed and tasca.tascaTramitacioMassiva}">
			<span class="<c:if test="${tasca.assignadaUsuariActual}">tramitar_massivament</c:if> label label-default" title="<spring:message code="tasca.llistat.accio.tramitar_massivament"/>"><i class="fa fa-files-o"></i></span>
		</c:if>
	</div> 
</td>
<td class="datacol">${tasca.responsableString}</td>
<td class="datacol"><fmt:formatDate value="${tasca.createTime}" pattern="dd/MM/yyyy HH:mm"/></td>
<td class="datacol"><fmt:formatDate value="${tasca.dueDate}" pattern="dd/MM/yyyy"/></td>
<td class="options">
	<c:if test='${not tasca.completed}'>
		<div class="btn-group">
			<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/> <span class="caret"></span></a>
			<ul id="dropdown-menu-${tasca.id}" class="dropdown-menu">
				<c:if test="${tasca.open and not tasca.suspended}">
					<c:if test="${tasca.assignee == dadesPersona.codi}">
						<li><a id="tramitar-tasca-${tasca.id}" href="../v3/expedient/${expedient.id}/tasca/${tasca.id}" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true"><span class="fa fa-folder-open"></span> <spring:message code="tasca.llistat.accio.tramitar"/></a></li>
						<c:if test="${tasca.tascaTramitacioMassiva}">
							<li><a href="../v3/tasca/${tasca.id}/massiva"><span class="fa fa-files-o"></span> <spring:message code="tasca.llistat.accio.tramitar_massivament"/></a></li>
						</c:if>
					</c:if>
					<c:if test="${not tasca.agafada and not empty tasca.responsables and tasca.assignadaUsuariActual}">
						<li><a data-rdt-link-ajax=true data-rdt-link-callback="agafar(${tasca.id});" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.agafar"/>" href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/agafar"><span class="fa fa-chain"></span>&nbsp;<spring:message code="tasca.llistat.accio.agafar"/></a></li>
					</c:if>
					<c:if test="${expedient.permisSupervision}"><li><a href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/suspendre" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.suspendre"/>"><span class="fa fa-pause"></span> <spring:message code="tasca.llistat.accio.suspendre"/></a></li></c:if>
				</c:if>
				<li><a href="<c:url value="../v3/expedient/${expedient.id}"/>" class="consultar-expedient"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.llistat.accio.consultar.expedient"/></a></li>
				<c:if test="${tasca.open}">
					<c:if test="${expedient.permisReassignment}"><li><a href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/reassignar" data-rdt-link-modal="true"><span class="fa fa-share-square-o"></span> <spring:message code="tasca.llistat.accio.reassignar"/></a></li></c:if>
				</c:if>
				<c:if test="${tasca.suspended}">
					<c:if test="${expedient.permisSupervision}"><li><a href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/reprendre" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.reprendre"/>"><span class="fa fa-play"></span> <spring:message code="tasca.llistat.accio.reprendre"/></a></li></c:if>
				</c:if>
				<c:if test="${not tasca.completed and not tasca.cancelled}">
					<c:if test="${expedient.permisSupervision}"><li><a href="../v3/expedient/${expedient.id}/tasca/${tasca.id}/cancelar" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.cancelar"/>"><span class="fa fa-times"></span> <spring:message code="tasca.llistat.accio.cancelar"/></a></li></c:if>
				</c:if>
				<c:if test="${not empty tasca.responsables and tasca.assignee == dadesPersona.codi and tasca.open}">
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
		$(".tramitar_massivament").click(function() {
			$("#table-pendents-${tasca.id} td").unbind("click");
			window.location='../v3/tasca/${tasca.id}/massiva';
		});	
		$("#table-pendents-${tasca.id} td").click(function() {
			if ($('#tramitar-tasca-${tasca.id}').length > 0 && !$(this).hasClass('options'))
				$('#tramitar-tasca-${tasca.id}').click();
		});			
	});
	
	var maxcol = $("#taulaDades thead th").length - 6;
	if($("#taulaDades thead th ").find('input[type="checkbox"]').is(':hidden')) {
		$(".first").hide();
		$("#td_nohiha").attr('colspan', 1);
	} else {
		$("#td_nohiha").attr('colspan', 2);
	}
	$(".maxcols").attr('colspan', maxcol);
	$(".datacol").attr('colspan', (maxcol > 7) ? 2 : 1);
</script>