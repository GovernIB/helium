<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<style>
div.proces {color: white !important;background-color: #428bca !important;border-color: #357ebd !important;font-weight: bold;}
div.proces:hover {background-color: #3071a9 !important;border-color: #285e8e !important;}
.panel-body {padding-bottom: 0px !important;}
.panel-body > table {overflow: hidden;}
.panel-body table:last-child {margin-bottom: 0px;}
.contingut-carregant-proces {margin: 1em 0 2em 0;text-align: center;}
.ocults {margin-top: 10px;padding-bottom: 5px;}
.btnNovaDada {padding-bottom: 10px;}
#contingut-dades {margin-top: 13px !important;}
#contingut-dades .left {float: left;}
#contingut-dades .right {float: right;}
#contingut-dades .clear{clear: both;}
.registre th{min-width: 160px;}
.registre th.colEliminarFila{min-width: 1px;}
.registre_taula{overflow: auto;}
</style>

<c:set var="numColumnes" value="${3}"/>
<c:choose>
	<c:when test="${not empty dades}">
		<c:if test="${expedient.permisDataManagement}">
			<div class="pull-left">
				<a id="boto-ocults" href="#" class="btn btn-default<c:if test="${ambOcults}"> active</c:if>">
					<c:choose>
						<c:when test="${ambOcults}"><span class="fa fa-check-square-o"></span></c:when>
						<c:otherwise><span class="fa fa-square-o"></span></c:otherwise>
					</c:choose>
					<spring:message code='expedient.dada.mostrar.ocultes'/>
				</a>
			</div>
		</c:if>
		<c:set var="procesFirst" value="${true}"/>
		<c:forEach items="${dades}" var="dadesProces" varStatus="procesosStatus">
			<c:set var="agrupacioFirst" value="${true}"/>
			<c:set var="proces" value="${dadesProces.key}"/>
			<c:if test="${expedient.permisDataManagement}">
				<div class="btnNovaDada right">
					<a id=""
						class="btn btn-default" 
						href="../../v3/expedient/${expedient.id}/proces/${proces.id}/dada/new" 
						data-rdt-link-modal="true" 
						data-rdt-link-callback="reestructura(${proces.id});"
						data-rdt-link-modal-min-height="350">
						<span class="fa fa-plus"></span>
						<spring:message code="expedient.boto.nova_dada"/>
					</a>
				</div>
			</c:if>
			<div class="clear"></div>
			<div class="panel panel-default">
				<div id="${proces.id}-titol" class="panel-heading clicable proces" data-toggle="collapse" data-target="#panel_${proces.id}" data-id="${proces.id}" data-carrega="<c:if test='${!procesFirst}'>ajax</c:if>">
					<c:choose>
						<c:when test="${proces.id == inicialProcesInstanceId}">
							<spring:message code='common.tabsexp.proc_princip'/>
						</c:when>
						<c:otherwise>${proces.titol}</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${totalsPerProces[proces] > 0}">
							<span class="badge general" data-nombre="${totalsPerProces[proces]}">${totalsPerProces[proces]}</span>
						</c:when>
						<c:otherwise>
							<span class="badge general"></span>
						</c:otherwise>
					</c:choose>
					<div class="pull-right">
						<c:choose>
							<c:when test="${procesFirst}"><span class="icona-collapse fa fa-chevron-up"></span></c:when>
							<c:otherwise><span class="icona-collapse fa fa-chevron-down"></span></c:otherwise>
						</c:choose>
					</div>
				</div>
				<div id="panel_${proces.id}" class="panel-body collapse<c:if test="${procesFirst}"> in</c:if>">
				<c:choose>
				<c:when test="${not empty dadesProces.value && fn:length(dadesProces.value) > 0}">
					<c:forEach var="dadesAgrupacioEntry" items="${dadesProces.value}" varStatus="agrupacioStatus">
						<c:set var="agrupacio" value="${dadesAgrupacioEntry.key}"/>
						<c:set var="count" value="${fn:length(dadesAgrupacioEntry.value)}"/>
						<c:set var="dadesAgrupacio" value="${dadesAgrupacioEntry.value}" scope="request"/>
						<c:if test="${count > 0}">
							<c:choose>
								<c:when test="${not empty agrupacio}">
									<c:import url="import/expedientTaula.jsp">
										<c:param name="id" value="${agrupacio.id}"/>
										<c:param name="dadesAttribute" value="dadesAgrupacio"/>
										<c:param name="titol" value="${agrupacio.nom}"/>
										<c:param name="numColumnes" value="${numColumnes}"/>
										<c:param name="count" value="${count}"/>
										<c:param name="desplegat" value="${agrupacioFirst}"/>
										<c:param name="desplegadorClass" value="agrupacio-desplegador"/>
										<c:param name="procesId" value="${proces.id}"/>
									</c:import>
								</c:when>
								<c:otherwise>
									<c:import url="import/expedientTaula.jsp">
										<c:param name="dadesAttribute" value="dadesAgrupacio"/>
										<c:param name="numColumnes" value="${numColumnes}"/>
										<c:param name="count" value="${totalsPerProces[proces]}"/>
										<c:param name="desplegat" value="${true}"/>
										<c:param name="mostrarCapsalera" value="${false}"/>
										<c:param name="procesId" value="${proces.id}"/>
									</c:import>
								</c:otherwise>
							</c:choose>
						</c:if>
						<c:set var="agrupacioFirst" value="${false}"/>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test='${procesFirst}'><div id="noData" class="well well-small"><spring:message code='expedient.dada.proces.cap' /></div></c:when>
						<c:otherwise><div class="contingut-carregant-proces"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div></c:otherwise>
					</c:choose>
				</c:otherwise>
				</c:choose>
				</div>
			</div>
			<c:set var="procesFirst" value="${false}"/>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>
<script>
var panell;

$(document).ready(function() {
	$('.proces').click(function() {
		var icona = $(this).find('.icona-collapse');
		icona.toggleClass('fa-chevron-down');
		icona.toggleClass('fa-chevron-up');
		if ($(this).data('carrega') == "ajax") {
			$(this).data('carrega', "")
			var id = $(this).data('id');
			panell = $('#panel_' + id);
			var ambOcults = "";
			if ($("#boto-ocults > span").length)
				ambOcults = $("#boto-ocults > span").hasClass("fa-check-square-o");
			panell.load('<c:url value="/nodeco/v3/expedient/${expedient.id}/proces/"/>' + id + '/dada', {"ambOcults": ambOcults}, updatePanell);
		}
	});
	$('#boto-ocults').click(function() {
		<c:choose>
			<c:when test="${ambOcults}">$('#contingut-dades').data('href', '<c:url value="/nodeco/v3/expedient/${expedient.id}/dada"/>');</c:when>
			<c:otherwise>$('#contingut-dades').data('href', '<c:url value="/nodeco/v3/expedient/${expedient.id}/dadaAmbOcults"/>');</c:otherwise>
		</c:choose>
		$('#contingut-dades').load(
			$('#contingut-dades').data('href'),
			function (responseText, textStatus, jqXHR) {
				if (textStatus == 'error') {
					modalAjaxErrorFunction(jqXHR, textStatus);
				}
			}
		);
	});

 	updateBadges();

 	$('.var-delete').heliumEvalLink({
		refrescarAlertes: true,
		refrescarPagina: false,
		ajaxRefrescarAlertes: true,
		alertesRefreshUrl: '<c:url value="/nodeco/v3/missatges"/>',
		callback: refrescarEstatExpedient()
	});
	$('.var-edit').heliumEvalLink({
		refrescarAlertes: true,
		refrescarPagina: false,
		ajaxRefrescarAlertes: true,
		alertesRefreshUrl: '<c:url value="/nodeco/v3/missatges"/>',
		callback: refrescarEstatExpedient()
	});
	$('.btnNovaDada a').heliumEvalLink({
		refrescarAlertes: true,
		refrescarPagina: false,
		ajaxRefrescarAlertes: true,
		alertesRefreshUrl: '<c:url value="/nodeco/v3/missatges"/>',
		callback: refrescarEstatExpedient()
	});
	refrescarEstatExpedient();
});

function updateBadges() {
	$(".badge-nombre").each(function(){
		var nombre = $(this).data("nombre");
		$(this).closest(".panel-body").prev(".clicable").find(".badge.general").html(nombre);
	});
}

function updatePanell() {
	updateBadges();
	
	$('.var-delete', panell).heliumEvalLink({
		refrescarAlertes: true,
		refrescarPagina: false,
		ajaxRefrescarAlertes: true,
		alertesRefreshUrl: '<c:url value="/nodeco/v3/missatges"/>',
		callback: refrescarEstatExpedient()
	});
	$('.var-edit', panell).heliumEvalLink({
		refrescarAlertes: true,
		refrescarPagina: false,
		ajaxRefrescarAlertes: true,
		alertesRefreshUrl: '<c:url value="/nodeco/v3/missatges"/>',
		callback: refrescarEstatExpedient()
	});
	$('.icon', panell).heliumEvalLink({
		refrescarAlertes: true,
		refrescarPagina: false,
		callback: refrescarEstatExpedient()
	});
}
</script>