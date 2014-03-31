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
									<c:if test="${tasca.cancelada}">
										<i class="btn-small btn-danger pull-right" style="margin-right: .3em">CA</i>
									</c:if>
									<c:if test="${tasca.suspesa}">
										<i class="btn-small btn-info pull-right" style="margin-right: .3em">SU</i>
									</c:if>
									<c:if test="${tasca.oberta}">
										<i class="btn-small btn-warning pull-right" style="margin-right: .3em">OB</i>
									</c:if>
									<c:if test="${tasca.completed}">
										<i class="btn-small btn-succes pull-right" style="margin-right: .3em">FI</i>
									</c:if>
									<c:if test="${tasca.responsableCodi == dadesPersona.codi}">
										<i class="btn-small btn-inverse pull-right" style="margin-right: .3em">AG</i>
									</c:if>
									<c:if test="${not empty tasca.responsables && not tasca.agafada}">
										<i class="icon-group" style="margin-right: .3em"/>
									</c:if>
									${tasca.titol}
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
										<a class="btn btn-success dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon-cog icon-white"></i> Accions <span class="caret"></span></a>
										<ul class="dropdown-menu">
											<c:if test="${tasca.oberta and not tasca.suspesa}">
												<c:if test="${tasca.responsableCodi == dadesPersona.codi}">
													<li><a data-tramitar-modal="true" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/tramitar"/>"><i class="icon-folder-open"></i> Tramitar</a></li>
													<li><a href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/delegar"/>"><i class="icon-hand-right"></i> Delegar</a></li>
												</c:if>
												<c:if test="${not empty tasca.responsables && not tasca.agafada}">
													<li><a href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/tascaAgafar"/>"><i class="icon-signin"></i> Agafar</a></li>
												</c:if>
												<li><a data-reasignar-modal="true" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/reassignar"/>"><i class="icon-share"></i> Reasignar</a></li>
												<li><a onclick="return confirmarSuspendre(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/suspendre"/>"><i class="icon-pause"></i> Suspendre</a></li>
											</c:if>
											<c:if test="${tasca.suspesa}">
												<li><a onclick="return confirmarReprendre(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/reprendre"/>"><i class="icon-play"></i> Reprendre</a></li>
											</c:if>
											<c:if test="${not tasca.cancelada}">
												<li><a onclick="return confirmarCancelar(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/cancelar"/>"><i class="icon-remove"></i> Cancelar</a></li>
											</c:if>
											<c:if test="${not empty tasca.responsables && tasca.responsableCodi == dadesPersona.codi and tasca.oberta}">
												<li><a onclick="return confirmarAlliberar(event)" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/tascaAlliberar"/>"><i class="icon-leaf"></i> <spring:message code="tasca.pllistat.alliberar"/></a></li>
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

<div id="tramitacio-modal" class="modal modal-max hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h3></h3>
	</div>
	<div class="modal-body"></div>
	<div class="modal-footer">
		<button id="modal-button-tancar" class="btn pull-left" data-dismiss="modal" aria-hidden="true">Tancar</button>
	</div>
</div>
<div id="tasca-tramitacio-modal"></div>

<div id="reasignar-modal" class="modal modal-max hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h3></h3>
	</div>
	<div class="modal-body"></div>
	<div class="modal-footer">
		<button id="modal-button-tancar" class="btn pull-left" data-dismiss="modal" aria-hidden="true">Tancar</button>
	</div>
</div>
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

/*function canviTitolModal(titol) {
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
}*/
	/*$('.link-tramitacio-modal').click(function(e) {
		e.preventDefault();
		var url = $(this).attr('href');
		$('#tramitacio-modal .modal-body').html('<iframe width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + url + '" onload="autoResize(this)"></iframe>');
		$('#tramitacio-modal').modal('show');
		$('#tramitacio-modal').css('top', '1%');
	});*/
	
	//$('.dataTable').addClass('table-hover');
	/*$('.dataTable > tbody > tr > td:not(:last-child)').click(function(event) {
		event.stopPropagation();			
		if ($('ul a:first', $(this).parent()).hasClass('tramitar')) {
			$('ul a:first', $(this).parent()).click();
		}
	});*/
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
				valignTop: true,
				buttonContainerId: 'formFinalitzar',
			});
			return false;
		} 
		if ($(this).data('reasignar-modal')) {
			$('#tasca-reasignar-modal').heliumModal({
				modalUrl: $(this).attr('href'),
				refrescarTaula: false,
				refrescarAlertes: true,
				refrescarPagina: false,
				adjustWidth: false,
				adjustHeight: true,
				maximize: true,
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
