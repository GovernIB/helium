<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:choose>
	<c:when test="${not empty tasques}">
		<c:set var="cont" value="0"/>
		<table class="dataTable table table-bordered table-pendents">
			<thead>
				<tr>
					<th>Tasca</th>
					<th>Responsable</th>
					<th>Data creació</th>
					<th>Data límit</th>
					<th>Prioritat</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="tasca" items="${tasques}">
					<tr <c:if test="${tasca.responsableCodi == dadesPersona.codi}"> class="link-tramitacio-modal" style="cursor: pointer"</c:if>>
						<td>${tasca.titol}</td>
						<td>
							<c:choose>
								<c:when test="${not empty tasca.responsable}"><!--A-->${tasca.responsable.nomSencer}</c:when>
								<c:when test="${not empty tasca.responsables}"><!--P-->
									<c:forEach var="responsable" items="${tasca.responsables}" varStatus="status">
										<!--P${status.index}-->${responsable.nomSencer}<c:if test="${not status.last}">, </c:if>
									</c:forEach>
								</c:when>
								<c:otherwise><!--O--></c:otherwise>
							</c:choose>
						</td>
						<td><fmt:formatDate value="${tasca.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></td>
						<td><fmt:formatDate value="${tasca.dataLimit}" pattern="dd/MM/yyyy"/></td>
						<td>${tasca.prioritat}</td>
						<td class="hide"><li><a class="tramitar" href="<c:url value="/v3/expedient/${expedientId}/tasca/${tasca.id}/tramitar"/>">Tramitar</a></li></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
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
			var url = $('.link-tramitacio-modal').find(".tramitar").attr("href");
			$('#tramitacio-modal .modal-body').html('<iframe width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + url + '" onload="autoResize(this)"></iframe>');
			$('#tramitacio-modal').modal('show');
			$('#tramitacio-modal').css('top', '1%');
		});
		
		$('.table-pendents').addClass('table-hover');
		$('.table-pendents > tbody > tr > td:not(:last-child)').click(function(event) {
			event.stopPropagation();			
			$('.link-tramitacio-modal').click();
		});
	}
);

//]]>
</script>
