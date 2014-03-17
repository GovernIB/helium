<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<style>
body .modal {
	width: 98%;
	left: 1%;
	margin: auto auto auto auto;
}
body .modal-body {
}
</style>
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
			<table class="dataTable table table-bordered">
				<thead>
					<tr>
						<th>Tasca</th>
						<th>Asginada</th>
						<th>Data creació</th>
						<th>Data límit</th>
						<th>Prioritat</th>
						<th>Flags</th>
						<th>Estat</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="tasca" items="${tasques}">
						<c:if test="${tasca.oberta}">
							<tr <c:if test="${tasca.responsableCodi == dadesPersona.codi and tasca.oberta and not tasca.suspesa}"> style="cursor: pointer"</c:if>>
								<td>${tasca.titol}</td>
								<td>
									<c:choose>
										<c:when test="${not empty tasca.responsable}"><!--A-->${tasca.responsable.nomSencer}</c:when>
										<c:when test="${not empty tasca.responsables}"><!--P-->
<%-- 											<c:forEach var="responsable" items="${tasca.responsables}" varStatus="status"> --%>
<%-- 												<!--P${status.index}-->${responsable.nomSencer}<c:if test="${not status.last}">, </c:if> --%>
<%-- 											</c:forEach> --%>
										</c:when>
										<c:otherwise><!--O--></c:otherwise>
									</c:choose>
								</td>
								<td><fmt:formatDate value="${tasca.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></td>
								<td><fmt:formatDate value="${tasca.dataLimit}" pattern="dd/MM/yyyy"/></td>
								<td>${tasca.prioritat}</td>
								<td>
									<c:if test="${tasca.cancelada}">C</c:if>
									<c:if test="${tasca.suspesa}">S</c:if>
									<c:if test="${expedientLogIds[cont][2] eq tasca.id}">
										<c:if test="${expedientLogIds[cont][1] eq 'RETROCEDIT_TASQUES'}">R</c:if>
										<c:set var="cont" value="${cont + 1}"/>
									</c:if>
								</td>
								<td>${tasca.estat}</td>
								<td>
									<c:choose>
										<c:when test="${tasca.responsableCodi == dadesPersona.codi}">
											<div class="btn-group">
												<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon-cog icon-white"></i> Accions <span class="caret"></span></a>
												<ul class="dropdown-menu">
													<c:if test="${tasca.oberta and not tasca.suspesa}">
														<li><a class="link-tramitacio-modal tramitar" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/tramitar"/>"><i class="icon-folder-open"></i> Tramitar</a></li>
														<li><a href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/delegar"/>"><i class="icon-hand-right"></i> Delegar</a></li>
														<li>
															<c:import url="utils/modalDefinir.jsp">
																<c:param name="sAjaxSource" value="/helium/v3/expedient/${expedientId}/tasca/${tasca.id}/reassignar"/>
																<c:param name="modalId" value="reassignar_${tasca.id}"/>
																<c:param name="refrescarAlertes" value="true"/>
																<c:param name="refrescarPagina" value="false"/>							
																<c:param name="refrescarTaula" value="false"/>							
																<c:param name="refrescarTaulaId" value="false"/>
																<c:param name="icon" value="icon-share"/>
																<c:param name="texto" value="Reassignar"/>
															</c:import>
														</li>
														<li><a onclick="return confirmarSuspendre(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/suspendre"/>"><i class="icon-pause"></i> Suspendre</a></li>
													</c:if>
													<c:if test="${tasca.suspesa}">
														<li><a onclick="return confirmarReprendre(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/reprendre"/>"><i class="icon-play"></i> Reprendre</a></li>
													</c:if>
													<c:if test="${not tasca.cancelada}">
														<li><a onclick="return confirmarCancelar(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/cancelar"/>"><i class="icon-remove"></i> Cancelar</a></li>
													</c:if>
													<c:if test="${tasca.agafada and tasca.oberta}">
														<li><a onclick="return confirmarAlliberar(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/tascaAlliberar"/>"><i class="icon-leaf"></i> <spring:message code="tasca.pllistat.alliberar"/></a></li>
													</c:if>														
												</ul>
											</div>
										</c:when>
										<c:when test="${not empty tasca.responsables}">
											<div class="btn-group">
												<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon-cog icon-white"></i> Accions <span class="caret"></span></a>
												<ul class="dropdown-menu">
													<c:if test="${tasca.oberta and not tasca.suspesa}">
														<li><a href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/tascaAgafar"/>"><i class="icon-signin"></i> Agafar</a></li>
														<li>
															<c:import url="utils/modalDefinir.jsp">
																<c:param name="sAjaxSource" value="/helium/v3/expedient/${expedientId}/tasca/${tasca.id}/reassignar"/>
																<c:param name="modalId" value="reassignar_${tasca.id}"/>
																<c:param name="refrescarAlertes" value="true"/>
																<c:param name="refrescarPagina" value="false"/>							
																<c:param name="refrescarTaula" value="false"/>							
																<c:param name="refrescarTaulaId" value="false"/>
																<c:param name="icon" value="icon-share"/>
																<c:param name="texto" value="Reassignar"/>
															</c:import>
														</li>
														<li><a onclick="return confirmarSuspendre(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/suspendre"/>"><i class="icon-pause"></i> Suspendre</a></li>
													</c:if>
													<c:if test="${tasca.suspesa}">
														<li>
															<a onclick="return confirmarReprendre(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/reprendre"/>"><i class="icon-play"></i> Reprendre</a>
														</li>
													</c:if>
													<c:if test="${not tasca.cancelada}">
														<li><a onclick="return confirmarCancelar(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/cancelar"/>"><i class="icon-remove"></i> Cancelar</a></li>
													</c:if>
													<c:if test="${tasca.agafada and tasca.oberta}">
														<li><a onclick="return confirmarAlliberar(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/tascaAlliberar"/>"><i class="icon-leaf"></i> <spring:message code="tasca.pllistat.alliberar"/></a></li>
													</c:if>	
												</ul>
											</div>
										</c:when>
										<c:otherwise>
											<div class="btn-group">
												<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon-cog icon-white"></i> Accions <span class="caret"></span></a>
												<ul class="dropdown-menu">
													<c:if test="${tasca.oberta and not tasca.suspesa}">
														<li>
															<c:import url="utils/modalDefinir.jsp">
																<c:param name="sAjaxSource" value="/helium/v3/expedient/${expedientId}/tasca/${tasca.id}/reassignar"/>
																<c:param name="modalId" value="reassignar_${tasca.id}"/>
																<c:param name="refrescarAlertes" value="true"/>
																<c:param name="refrescarPagina" value="false"/>							
																<c:param name="refrescarTaula" value="false"/>							
																<c:param name="refrescarTaulaId" value="false"/>
																<c:param name="icon" value="icon-share"/>
																<c:param name="texto" value="Reassignar"/>
															</c:import>
														</li>
														<li><a onclick="return confirmarSuspendre(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/suspendre"/>"><i class="icon-pause"></i> Suspendre</a></li>
													</c:if>
													<c:if test="${tasca.suspesa}">
														<li><a onclick="return confirmarReprendre(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/reprendre"/>"><i class="icon-play"></i> Reprendre</a></li>
													</c:if>
													<c:if test="${not tasca.cancelada}">
														<li><a onclick="return confirmarCancelar(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/cancelar"/>"><i class="icon-remove"></i> Cancelar</a></li>
													</c:if>
													<c:if test="${tasca.agafada and tasca.oberta}">
														<li><a onclick="return confirmarAlliberar(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/tascaAlliberar"/>"><i class="icon-leaf"></i> <spring:message code="tasca.pllistat.alliberar"/></a></li>
													</c:if>	
												</ul>
											</div>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:if>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
		<c:if test="${hiHaNoPendents}">
			<div class="well well-small">
				    <div class="page-header">
				    <h4>Tasques finalitzades</h4>
				    </div>
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

<div id="tramitacio-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h3></h3>
	</div>
	<div class="modal-body"></div>
	<div class="modal-footer">
		<button id="modal-button-tancar" class="btn pull-left" data-dismiss="modal" aria-hidden="true">Tancar</button>
	</div>
</div>
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

function canviTitolModal(titol) {
	$('#tramitacio-modal h3').html(titol);
}

function addHtmlPeuModal(html, id) {
	if (($('#'+id).length > 0)) {
		$('#'+id).remove();
	} 
	$('#tramitacio-modal .modal-footer').append(html);
}

function autoResize(element) {
	var elementHeight = element.contentWindow.document.body.offsetHeight;
	element.style.height = elementHeight + 'px';
	var taraModal = $('#tramitacio-modal .modal-header').height() + $('#tramitacio-modal .modal-footer').height();
	var maxBodyHeight = $(document).height() - taraModal - 100;
	if (elementHeight > maxBodyHeight) {
		$('#tramitacio-modal .modal-body').css('max-height', maxBodyHeight + 'px');
	} else {
		var afegir = 15 + 15; // Padding del body de la modal
		$('#tramitacio-modal .modal-body').css('max-height', elementHeight + afegir + 'px');
	}
}
$(document).ready(
	function() {
		$('.link-tramitacio-modal').click(function(e) {
			e.preventDefault();
			var url = $(this).attr('href');
			$('#tramitacio-modal .modal-body').html('<iframe width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + url + '" onload="autoResize(this)"></iframe>');
			$('#tramitacio-modal').modal('show');
			$('#tramitacio-modal').css('top', '1%');
		});
		
		$('.dataTable').addClass('table-hover');
		$('.dataTable > tbody > tr > td:not(:last-child)').click(function(event) {
			event.stopPropagation();			
			if ($('ul a:first', $(this).parent()).hasClass('tramitar')) {
				$('ul a:first', $(this).parent()).click();
			}
		});
	}
);

//]]>
</script>

<c:if test="${tasca.validada}">		
	<script type="text/javascript">
	// <![CDATA[
	function confirmarIniciar(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm("Estau segur que voleu iniciar aquest termini?");
	}
	// ]]>
	</script>
</c:if>
