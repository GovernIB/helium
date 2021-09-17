<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set var="modalPrefix" value=""/>
<c:if test="${retrocedirModalPath}">
	<c:set var="modalPrefix" value="../../"/>
</c:if>
<c:choose>
<c:when test="${true}">
<tr class="tasques-pendents" id="tasques-pendents-${expedient.id}">
	<c:choose>
		<c:when test="${not empty tasques}">
			<td colspan="8" style="background-color:#eee; padding-left: 30px">
				<table class="table table-striped table-bordered table-condensed" id="table-tasques-pendents-${expedient.id}">
					<thead>
						<tr>
							<th><spring:message code="expedient.tasca.columna.tasca.activa"/></th>
							<th><spring:message code="expedient.tasca.columna.asignada_a"/></th>			
							<th><spring:message code="expedient.tasca.columna.datcre"/></th>
							<th><spring:message code="expedient.tasca.columna.datlim"/></th>
							<c:if test="${not expedient.aturat}">
								<th width="10%"></th>
							</c:if>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="tasca" items="${tasques}" varStatus="index">
							<tr id="fila-tasca-${tasca.id}">
								<td class="columna-info-tasca">
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
											<c:choose>
												<c:when test="${tasca.assignadaUsuariActual}"><a href="${modalPrefix}../v3/tasca/${tasca.id}/massiva"><span class="label label-default" title="<spring:message code="tasca.llistat.accio.tramitar_massivament"/>"><i class="fa fa-files-o"></i></span></a></c:when>
												<c:otherwise><span class="label label-default" title="<spring:message code="tasca.llistat.accio.tramitar_massivament"/>"><i class="fa fa-files-o"></i></span></c:otherwise>
											</c:choose>
										</c:if>
										<c:if test="${not empty tasca.errorFinalitzacio or not empty tasca.marcadaFinalitzar or not empty tasca.iniciFinalitzacio}">
											<div class="pull-right">
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
											</div>
										</c:if>
									</div>
								</td>
								<td class="columna-info-tasca">
									<c:if test="${not empty tasca.responsable || not empty tasca.responsables}">
										<p>
											<span class="fa fa-user" title="<spring:message code="tasca.llistat.etiqueta.usuaris"/>"></span>
											<c:choose>
												<c:when test="${not empty tasca.responsable}"><span class="label label-default assignment">${tasca.responsable.nomSencer}</span></c:when>
												<c:otherwise><c:forEach var="usuari" items="${tasca.responsables}"><span class="label label-default assignment">${usuari.nomSencer}</span></c:forEach></c:otherwise>
											</c:choose>
										</p>
									</c:if>
									<c:if test="${not tasca.agafada && not empty tasca.grups}">
										<span class="fa fa-users" title="<spring:message code="tasca.llistat.etiqueta.grups"/>"></span>
										<c:forEach var="grup" items="${tasca.grups}"><span class="label label-default assignment agrup" data-grup="${grup}">${grup}</span></c:forEach>
									</c:if>
								</td>
								<td class="columna-info-tasca"><fmt:formatDate value="${tasca.createTime}" pattern="dd/MM/yyyy HH:mm"/></td>
								<td class="columna-info-tasca"><fmt:formatDate value="${tasca.dueDate}" pattern="dd/MM/yyyy"/></td>
								<c:if test="${not expedient.aturat}">
									<td class="columna-accio-tasca">
										<c:if test='${not tasca.completed}'>
											<div class="btn-group">
												<a class="btn btn-sm btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/> <span class="caret"></span></a>
												<ul id="dropdown-menu-tasca-${tasca.id}" class="dropdown-menu">
													<c:if test="${tasca.open and not tasca.suspended and tasca.assignee == dadesPersona.codi and tasca.assignadaUsuariActual}">
														<li><a id="tramitar-tasca-${tasca.id}" href="${modalPrefix}../v3/tasca/${tasca.id}" class="icon" data-rdt-link-callback="refrescarPanell(${expedient.id},${tasca.id},false);" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true"><span class="fa fa-folder-open"></span> <spring:message code="tasca.llistat.accio.tramitar"/></a></li>
														<c:if test="${tasca.tascaTramitacioMassiva}">
															<li><a href="${modalPrefix}../v3/tasca/${tasca.id}/massiva"><span class="fa fa-files-o"></span> <spring:message code="tasca.llistat.accio.tramitar_massivament"/></a></li>
														</c:if>
													</c:if>
													<c:if test="${tasca.open and not tasca.suspended and not tasca.agafada and (not empty tasca.responsables or not empty tasca.grups) and tasca.assignadaUsuariActual}">
														<li><a data-rdt-link-ajax=true data-rdt-link-callback="refrescarPanell(${expedient.id},${tasca.id},true);" href="<c:url value="${modalPrefix}../v3/expedient/${expedient.id}/tasca/${tasca.id}/agafar"/>" class="icon tasca-accio-agafar" data-tasca-id="${tasca.id}"><span class="fa fa-chain"></span>&nbsp;<spring:message code="tasca.llistat.accio.agafar"/></a></li>
													</c:if>
													<c:if test="${tasca.open and not tasca.suspended and tasca.agafada and (tasca.assignadaUsuariActual or expedient.permisReassignment)}">
														<li><a data-rdt-link-ajax=true data-rdt-link-callback="refrescarPanell(${expedient.id},${tasca.id},false);" href="<c:url value="${modalPrefix}../v3/expedient/${expedient.id}/tasca/${tasca.id}/alliberar"/>" class="icon tasca-accio-alliberar" data-tasca-id="${tasca.id}" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.alliberar"/>"><span class="fa fa-chain-broken"></span> <spring:message code="tasca.llistat.accio.alliberar"/></a></li>
													</c:if>
													<c:if test="${expedient.permisTaskAssign and tasca.open}">
														<li><a href="<c:url value="${modalPrefix}../v3/expedient/${expedient.id}/tasca/${tasca.id}/reassignar"/>" class="icon" data-rdt-link-modal="true" data-rdt-link-callback="refrescarPanell(${expedient.id},${tasca.id},false);"><span class="fa fa-share-square-o"></span> <spring:message code="tasca.llistat.accio.reassignar"/></a></li>
													</c:if>
													<c:if test="${expedient.permisTaskManagement and tasca.open and not tasca.suspended}">
														<li><a href="<c:url value="${modalPrefix}../v3/expedient/${expedient.id}/tasca/${tasca.id}/suspendre"/>" data-rdt-link-callback="refrescarPanell(${expedient.id},${tasca.id},false);" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.suspendre"/>"><span class="fa fa-pause"></span> <spring:message code="tasca.llistat.accio.suspendre"/></a></li>
													</c:if>
													<c:if test="${expedient.permisTaskManagement and tasca.suspended}">
														<li><a href="<c:url value="${modalPrefix}../v3/expedient/${expedient.id}/tasca/${tasca.id}/reprendre"/>" data-rdt-link-callback="refrescarPanell(${expedient.id},${tasca.id},false);" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.reprendre"/>"><span class="fa fa-play"></span> <spring:message code="tasca.llistat.accio.reprendre"/></a></li>
													</c:if>
													<c:if test="${expedient.permisTaskManagement and not tasca.completed and not tasca.cancelled}">
														<li><a href="<c:url value="${modalPrefix}../v3/expedient/${expedient.id}/tasca/${tasca.id}/cancelar"/>" data-rdt-link-callback="refrescarPanell(${expedient.id},${tasca.id},false);" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.cancelar"/>"><span class="fa fa-times"></span> <spring:message code="tasca.llistat.accio.cancelar"/></a></li>
													</c:if>
												</ul>
												<script type="text/javascript">
													$("#fila-tasca-${tasca.id}").append('<ul class="dropdown-menu" id="dropdown-menu-context-${tasca.id}" style="display:none">'+$("#fila-tasca-${tasca.id}").find('.dropdown-menu').html()+'</ul>');
													$("#fila-tasca-${tasca.id}").contextMenu({
													    menuSelector: "#dropdown-menu-context-${tasca.id}",
													});
													$("#fila-tasca-${tasca.id} td.columna-info-tasca").click(function() {
														$('#tramitar-tasca-${tasca.id}').click();
													});
												</script>
											</div>
										</c:if>
									</td>
								</c:if>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</td>
		</c:when>
		<c:otherwise>
			<td colspan="8" style="background-color:#eee; padding-left: 30px">
				<div class="well-small"><spring:message code="expedient.tasca.activa.nohiha"/></div>
			</td>
		</c:otherwise>
	</c:choose>
</tr>
<script type="text/javascript">
	$(document).ready(function() {
		$('tr#tasques-pendents-${expedient.id}.tasques-pendents .dropdown-menu a').heliumEvalLink({
			refrescarAlertes: true,
			refrescarPagina: false,
			dataTable: $("#taulaDades"),
			alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
			maximize: true
		});
		$("a.segon-pla-link").heliumEvalLink({
			refrescarPagina: false,
			refrescarAlertes: false,
			refrescarTaula: false	
		});
		$('.agrup').popover({
			html: true,
			trigger: 'manual',
			content: function() {
				if (this.cache) return this.cache;

				let strPersones = $.ajax({
					url: '/heliumback/v3/expedient/grup/' + $(this).data('grup') + "/persones",
					dataType: 'json',
					async: false
				}).responseText;

				if (strPersones.startsWith("Grup"))
					return this.cache = strPersones;

				let contingut = ""; //"<ul>";
				let jsonPersones = JSON.parse(strPersones);
				for (var i = 0; i < jsonPersones.length; i++) {
					contingut += "<span class='fa fa-user'/> " + jsonPersones[i]["nom"] + "<br/>";
				}
				//contingut += "</ul>";

				return this.cache = contingut;
			}
		}).click(function (e) {
			$(this).popover('toggle');
		})
	});
</script>
</c:when>
<c:otherwise>
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
	.table-pendents {
		cursor: pointer;
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
			$('[title]').tooltip({container: 'body'});
		});
	//]]>
</script>
</c:otherwise>
</c:choose>
<script type="text/javascript">
	$(document).ready(function() {
		// Treu els botons d'accions per a les tasques on no hi hagi cap opció
		$('table#table-tasques-pendents-${expedient.id} .columna-accio-tasca').find('.btn-group').each(function() {
			if ( $(this).find('li').length == 0)
				$(this).remove();
		})
	});
</script>
