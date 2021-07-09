<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set var="tasca" value="${dada}"/>

<c:if test="${hiHaPendents gt 0 and tasca.open}">
	<c:if test="${contHiHaPendents == 0}">
	<td class="dadesTaulaTasca"><tr class="dadesTaulaTascaTr"><td class="dadesTaulaTascaTd" colspan="3">
	<table id="tasques-pendents-meves" class="dataTable table table-bordered table-hover">
		<thead>
			<tr>
				<th><spring:message code="expedient.tasca.columna.tasca"/></th>
				<th><spring:message code="expedient.tasca.columna.asignada_a"/></th>
				<th><spring:message code="expedient.tasca.columna.datcre"/></th>
				<th><spring:message code="expedient.tasca.columna.datlim"/></th>
				<th width="10%"></th>
			</tr>
		</thead>
		<tbody>
	</c:if>
	<c:set var="contHiHaPendents" value="${contHiHaPendents + 1}"/>	
	<tr id="table-tasca-${tasca.id}">
		<td>
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
					<span class="label label-warning" title="<spring:message code="enum.tasca.etiqueta.PD"/>">PD</span>
				</c:if>
				<c:if test="${tasca.completed}">
					<span class="label label-success" title="<spring:message code="enum.tasca.etiqueta.FI"/>">FI</span>
				</c:if>
				<c:if test="${tasca.agafada}">
					<span class="label label-default" title="<spring:message code="enum.tasca.etiqueta.AG"/>">AG</span>
				</c:if>
				<c:if test="${not tasca.completed and tasca.tascaTramitacioMassiva}">
					<span class="label label-default" title="<spring:message code="tasca.llistat.accio.tramitar_massivament"/>"><i class="fa fa-files-o"></i></span>
				</c:if>
			</div>
			<c:if test="${not empty tasca.errorFinalitzacio or not empty tasca.marcadaFinalitzar or not empty tasca.iniciFinalitzacio}">
				<a class="segon-pla-link" data-rdt-link-modal="true" href="<c:url value="/modal/v3/expedient/${expedient.id}/execucioInfo/${tasca.id}"/>">
				<span class="segon-pla-icona" id="spi-${tasca.id}">
				<c:choose>
					<c:when test="${not empty tasca.errorFinalitzacio}">
						<i class="fa fa-exclamation-circle fa-lg error" title="<spring:message code="error.finalitzar.tasca"/>: ${tasca.errorFinalitzacio}"></i>
					</c:when>
					<c:when test="${empty tasca.errorFinalitzacio and not empty tasca.marcadaFinalitzar and empty tasca.iniciFinalitzacio}">
						<i class="fa fa-clock-o fa-lg programada" title="<spring:message code="enum.tasca.etiqueta.marcada.finalitzar"/> ${tasca.marcadaFinalitzarFormat}"></i>
					</c:when>
					<c:when test="${empty tasca.errorFinalitzacio and not empty tasca.marcadaFinalitzar and not empty tasca.iniciFinalitzacio}">
						<i class="fa fa-circle-o-notch fa-spin fa-lg executant" title="<spring:message code="enum.tasca.etiqueta.execucio"/> ${tasca.iniciFinalitzacioFormat}"></i>
					</c:when>
				</c:choose>
				</span>
				</a>
			</c:if>
		</td>
		<td>${tasca.responsableString}</td>
		<td><fmt:formatDate value="${tasca.createTime}" pattern="dd/MM/yyyy HH:mm"/></td>
		<td><fmt:formatDate value="${tasca.dueDate}" pattern="dd/MM/yyyy"/></td>		 
		<td>
			<div class="btn-group">
				<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/> <span class="caret"></span></a>
				<ul id="dropdown-menu-${tasca.id}" class="dropdown-menu">
					<c:if test="${tasca.open and not tasca.suspended and tasca.assignee == dadesPersona.codi and tasca.assignadaUsuariActual}">
						<li><a id="tramitar-tasca-${tasca.id}" href="../../v3/tasca/${tasca.id}" class="icon" data-rdt-link-callback="recarregarLlistatTasques(${procesId},${tasca.id});" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true"><span class="fa fa-folder-open"></span> <spring:message code="tasca.llistat.accio.tramitar"/></a></li>
						<c:if test="${tasca.tascaTramitacioMassiva}">
							<li><a href="../../v3/tasca/${tasca.id}/massiva"><span class="fa fa-files-o"></span> <spring:message code="tasca.llistat.accio.tramitar_massivament"/></a></li>
						</c:if>
					</c:if>
					<c:if test="${tasca.open and not tasca.suspended and not tasca.agafada and not empty tasca.responsables and tasca.assignadaUsuariActual}">
						<li><a data-rdt-link-callback="agafar(${procesId},${tasca.id});" data-rdt-link-ajax=true class="icon" href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/agafar"/>"><span class="fa fa-chain"></span>&nbsp;<spring:message code="tasca.llistat.accio.agafar"/></a></li>
					</c:if>
					<c:if test="${tasca.open and not tasca.suspended and tasca.agafada and (tasca.assignadaUsuariActual or expedient.permisReassignment)}">
						<li><a data-rdt-link-ajax=true data-rdt-link-callback="alliberar(${procesId},${tasca.id});" href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/alliberar"/>" class="icon" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.alliberar"/>"><span class="fa fa-chain-broken"></span> <spring:message code="tasca.llistat.accio.alliberar"/></a></li>
					</c:if>
					<c:if test="${expedient.permisTaskAssign and tasca.open}">
						<li><a href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/reassignar"/>" class="icon" data-rdt-link-modal="true" data-rdt-link-callback="recarregarLlistatTasques(${procesId},${tasca.id});"><span class="fa fa-share-square-o"></span> <spring:message code="tasca.llistat.accio.reassignar"/></a></li>
					</c:if>
					<c:if test="${expedient.permisTaskManagement and tasca.open and not tasca.suspended}">
						<li><a href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/suspendre"/>" data-rdt-link-callback="recarregarLlistatTasques(${procesId},${tasca.id});" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.suspendre"/>"><span class="fa fa-pause"></span> <spring:message code="tasca.llistat.accio.suspendre"/></a></li>
					</c:if>
					<c:if test="${expedient.permisTaskManagement and tasca.suspended}">
						<li><a href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/reprendre"/>" data-rdt-link-callback="recarregarLlistatTasques(${procesId},${tasca.id});" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.reprendre"/>"><span class="fa fa-play"></span> <spring:message code="tasca.llistat.accio.reprendre"/></a></li>
					</c:if>
					<c:if test="${expedient.permisTaskManagement and not tasca.completed and not tasca.cancelled}">
						<li><a href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/cancelar"/>" data-rdt-link-callback="recarregarLlistatTasques(${procesId},${tasca.id});" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.cancelar"/>"><span class="fa fa-times"></span> <spring:message code="tasca.llistat.accio.cancelar"/></a></li>
					</c:if>
				</ul>
				<script type="text/javascript">
					$("#table-tasca-${tasca.id}").append('<ul class="dropdown-menu dropdown-menu-context" id="dropdown-menu-context-${tasca.id}" style="display:none">'+$("#table-tasca-${tasca.id}").find('.dropdown-menu').html()+'</ul>');
					$("#table-tasca-${tasca.id}").contextMenu({
					    menuSelector: "#dropdown-menu-context-${tasca.id}",
					    menuSelected: function (invokedOn, selectedMenu) {
					        // alert(selectedMenu.text() + " > " + invokedOn.text());
					    }
					});
					var position = $(".dropdown-menu.dropdown-menu-context").position();					
					$(".dropdown-menu.dropdown-menu-context").css({top: position.top-180});
					// Treu els botons d'accions per a les tasques on no hi hagi cap opció
					if ($('#dropdown-menu-${tasca.id}').find('li').length == 0) {
						$('#dropdown-menu-${tasca.id}').closest('.btn-group').remove();
					}
				</script>
			</div>
		</td>
	</tr>
	<c:if test="${contHiHaPendents == hiHaPendents}">
		</tbody>
	</table>
	
	<script type="text/javascript">
	// <![CDATA[			
	$(document).ready(function() {
		<c:if test="${procesId != inicialProcesInstanceId}">
			$('#${procesId}-titol-tasques').click();
			var icona = $('#${procesId}-titol-tasques').find('.icona-collapse');
			icona.toggleClass('fa-chevron-down');
			icona.toggleClass('fa-chevron-up');
		</c:if>
	});
	//]]>
	</script>
	</td></tr></td>
	</c:if>
</c:if>

<c:if test="${hiHaNoPendents gt 0 and not tasca.open}">
	<c:if test="${contHiHaNoPendents == 0}">
	<td class="dadesTaulaTasca" colspan="5"><tr class="dadesTaulaTascaTr"><td class="dadesTaulaTascaTd td-finalitzats" colspan="5">
	<div class="panel panel-default">
		<div id="${procesId}-titol-tasques-finalitzats" class="panel-heading clicable grup tauladades" data-toggle="collapse" data-target="#${procesId}-tasques-finalitzats">
			<spring:message code="expedient.tasca.grup.finalitzades"/>
			<span class="badge">${hiHaNoPendents}</span>
			<div class="pull-right"><span class="icona-collapse fa fa-chevron-down"></span></div>
		</div>
		<div id="${procesId}-tasques-finalitzats" class="clear collapse panel-body-grup">
		<table class="table table-bordered">
			<thead>
				<tr>
					<th><spring:message code="expedient.tasca.columna.tasca"/></th>
					<th><spring:message code="expedient.tasca.columna.responsable"/></th>
					<th><spring:message code="expedient.tasca.columna.datcre"/></th>
					<th><spring:message code="expedient.tasca.columna.datfi"/></th>
				</tr>
			</thead>
			<tbody>
		</c:if>
		<c:set var="contHiHaNoPendents" value="${contHiHaNoPendents + 1}"/>	
		<tr>
			<td>${tasca.titol}</td>
			<td>
				<c:choose>
					<c:when test="${not empty tasca.responsable}">${tasca.responsable.nomSencer}</c:when>
					<c:when test="${not empty tasca.responsables}">
						<c:forEach var="responsable" items="${tasca.responsables}" varStatus="status">
							${responsable.nomSencer}<c:if test="${not status.last}">, </c:if>
						</c:forEach>
					</c:when>
					<c:otherwise></c:otherwise>
				</c:choose>
			</td>
			<td><fmt:formatDate value="${tasca.createTime}" pattern="dd/MM/yyyy HH:mm"/></td>
			<td><fmt:formatDate value="${tasca.endTime}" pattern="dd/MM/yyyy HH:mm"/></td>
		</tr>
		<c:if test="${contHiHaNoPendents == hiHaNoPendents}">
			</tbody>
		</table>
		</div>
	</div>
	<script type="text/javascript">
	// <![CDATA[			
	$(document).ready(function() {
		$('#${procesId}-tasques-finalitzats').on('shown.bs.collapse', function() {
			$('#${procesId}-titol-tasques-finalitzats .icona-collapse').toggleClass('fa-chevron-down');
			$('#${procesId}-titol-tasques-finalitzats .icona-collapse').toggleClass('fa-chevron-up');
		});
		$('#${procesId}-tasques-finalitzats').on('hidden.bs.collapse', function() {
			$('#${procesId}-titol-tasques-finalitzats .icona-collapse').toggleClass('fa-chevron-down');
			$('#${procesId}-titol-tasques-finalitzats .icona-collapse').toggleClass('fa-chevron-up');
		});
	});
	$("a.segon-pla-link").heliumEvalLink({
		refrescarPagina: false,
		refrescarAlertes: false,
		refrescarTaula: false	
	});
	//]]>
	</script>
	</td></tr></td>
	</c:if>
</c:if>

