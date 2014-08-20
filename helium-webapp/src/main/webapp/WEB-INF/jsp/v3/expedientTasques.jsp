<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:choose>
	<c:when test="${not empty tasques}">
		<c:set var="hiHaPendentsMeves" value="${false}"/>
		<c:set var="hiHaNoPendents" value="${false}"/>
		<c:forEach var="tasca" items="${tasques}">
			<c:if test="${tasca.oberta}"><c:set var="hiHaPendentsMeves" value="${true}"/></c:if>
			<c:if test="${not tasca.oberta}"><c:set var="hiHaNoPendents" value="${true}"/></c:if>
		</c:forEach>
		<c:if test="${hiHaPendentsMeves}">
			<c:set var="cont" value="0"/>
			<table id="tasques-pendents-meves" class="dataTable table table-bordered table-hover">
				<thead>
					<tr>
						<th>Tasca</th>
						<th>Asignada</th>
						<th>Data creació</th>
						<th>Data límit</th>
						<th>Flags</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="tasca" items="${tasques}">
						<c:if test="${tasca.oberta}">
							<tr <c:if test="${tasca.responsableCodi == dadesPersona.codi and not tasca.suspesa}"> style="cursor: pointer"</c:if>>
								<td>
									${tasca.titol}
									<c:if test="${not empty tasca.responsables && not tasca.agafada}">
										<span class="fa fa-users"></span>
									</c:if>
									<div class="pull-right">
										<c:if test="${tasca.cancelada}">
											<span class="label label-danger" title="Cancel·lada">CA</span>
										</c:if>
										<c:if test="${tasca.suspesa}">
											<span class="label label-info" title="Suspesa">SU</span>
										</c:if>
										<c:if test="${tasca.oberta}">
											<span class="label label-warning" title="Oberta">OB</span>
										</c:if>
										<c:if test="${tasca.completed}">
											<span class="label label-success" title="Finalitzada">FI</span>
										</c:if>
										<c:if test="${tasca.responsableCodi == dadesPersona.codi}">
											<span class="label label-default" title="Agafada">AG</span>
										</c:if>
									</div>
								</td>
								<td>
									<c:if test="${not empty tasca.responsable}">
										${tasca.responsable.nomSencer}
									</c:if>
								</td>
								<td><fmt:formatDate value="${tasca.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></td>
								<td><fmt:formatDate value="${tasca.dataLimit}" pattern="dd/MM/yyyy"/></td>
								<td>
									<c:if test="${tasca.cancelada}">C</c:if>
									<c:if test="${tasca.suspesa}">S</c:if>
									<c:if test="${expedientLogIds[cont][2] eq tasca.id}">
										<c:if test="${expedientLogIds[cont][1] eq 'RETROCEDIT_TASQUES'}">R</c:if>
										<c:set var="cont" value="${cont + 1}"/>
									</c:if>
								</td>
								<td>
									<div class="btn-group">
										<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><span class="fa fa-cog"></span>&nbsp;Accions <span class="caret"></span></a>
										<ul class="dropdown-menu">
											<c:if test="${tasca.oberta and not tasca.suspesa}">
												<c:if test="${tasca.responsableCodi == dadesPersona.codi}">
													<li><a data-tramitar-modal="true" href="../../v3/expedient/${expedientId}/tasca/${tasca.id}/tramitar"><span class="fa fa-folder-open"></span> Tramitar</a></li>
													<li><a href="<c:url value="../../v3/expedient/${expedientId}/tasca/${tasca.id}/delegar"/>"><span class="fa fa-hand-o-right"></span> Delegar</a></li>
												</c:if>
												<c:if test="${not empty tasca.responsables && not tasca.agafada}">
													<li><a href="<c:url value="../../v3/expedient/${expedientId}/tasca/${tasca.id}/tascaAgafar"/>"><span class="fa fa-check"></span> Agafar</a></li>
												</c:if>
												<li><a data-reasignar-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/tasca/${tasca.id}/reassignar"/>"><span class="fa fa-share-square-o"></span> Reasignar</a></li>
												<li><a onclick="return confirmarSuspendre(event)" href="<c:url value="../../v3/expedient/${expedientId}/tasca/${tasca.id}/suspendre"/>"><span class="fa fa-pause"></span> Suspendre</a></li>
											</c:if>
											<c:if test="${tasca.suspesa}">
												<li><a onclick="return confirmarReprendre(event)" href="<c:url value="../../v3/expedient/${expedientId}/tasca/${tasca.id}/reprendre"/>"><span class="fa fa-play"></span> Reprendre</a></li>
											</c:if>
											<c:if test="${not tasca.cancelada}">
												<li><a onclick="return confirmarCancelar(event)" href="<c:url value="../../v3/expedient/${expedientId}/tasca/${tasca.id}/cancelar"/>"><span class="fa fa-times"></span> Cancelar</a></li>
											</c:if>
											<c:if test="${not empty tasca.responsables && tasca.responsableCodi == dadesPersona.codi and tasca.oberta}">
												<li><a onclick="return confirmarAlliberar(event)" href="<c:url value="../../v3/expedient/${expedientId}/tasca/${tasca.id}/tascaAlliberar"/>"><span class="icon-leaf"></span> <spring:message code="tasca.pllistat.alliberar"/></a></li>
											</c:if>													
										</ul>
									</div>
								</td>
							</tr>
						</c:if>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
		<c:if test="${hiHaNoPendents}">
			<div class="well well-sm">
				<h4>Tasques finalitzades</h4>
				<table class="table table-bordered">
					<thead>
						<tr>
							<th>Tasca</th>
							<th>Responsable</th>
							<th>Data creació</th>
							<th>Data finalització</th>
							<th>Estat</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="tasca" items="${tasques}">
							<c:if test="${not tasca.oberta}">
								<tr>
									<td>${tasca.titol}</td>
									<td>
										<c:choose>
											<c:when test="${not empty tasca.responsable}"><!--A-->${tasca.responsable.nomSencer}</c:when>
											<c:when test="${not empty tasca.responsables}"><!--P-->
												<c:forEach var="responsable" items="${tasca.responsables}" varStatus="status">
													${responsable.nomSencer}<c:if test="${not status.last}">, </c:if>
												</c:forEach>
											</c:when>
											<c:otherwise><!--O--></c:otherwise>
										</c:choose>
									</td>
									<td><fmt:formatDate value="${tasca.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></td>
									<td><fmt:formatDate value="${tasca.dataFi}" pattern="dd/MM/yyyy HH:mm"/></td>
									<td>${tasca.estat}</td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</c:if>
	</c:when>
	<c:otherwise>
		<div class="well well-small">No hi ha tasques per a mostrar</div>
	</c:otherwise>
</c:choose>

<div id="tasca-tramitacio-modal"></div>
<div id="tasca-reasignar-modal"></div>

<script type="text/javascript">
// <![CDATA[
	function confirmarSuspendre(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm("Estau segur que voleu suspendre aquesta tasca?");
	}
	function confirmarReprendre(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm("Estau segur que voleu reprendre aquesta tasca?");
	}
	function confirmarCancelar(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm("Estau segur que voleu cancel·lar aquesta tasca? Aquesta acció no es podrà desfer.");
	}
	function confirmarAlliberar(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm("Estau segur que voleu alliberar aquesta tasca?");
	}

	$('#tasques-pendents-meves a').click(function() {
		if ($(this).data('tramitar-modal')) {
			$('#tasca-tramitacio-modal').heliumModal({
				modalUrl: $(this).attr('href'),
				refrescarTaula: false,
				refrescarAlertes: false,
				refrescarPagina: false,
				adjustWidth: false,
				adjustHeight: true,
				maximize: true,
				alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
				valignTop: true,
				buttonContainerId: 'formFinalitzar',
			});
			return false;
		} else if ($(this).data('reasignar-modal')) {
			$('#tasca-reasignar-modal').heliumModal({
				modalUrl: $(this).attr('href'),
				refrescarTaula: false,
				refrescarAlertes: true,
				refrescarPagina: false,
				adjustWidth: false,
				adjustHeight: true,
				maximize: true,
				alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
				valignTop: true,
				buttonContainerId: 'formReasignar'
			});
			return false;
		} else {
			return true;
		}
	});
//]]>
</script>
