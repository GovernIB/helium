<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<style>
	a, a:HOVER, a:FOCUS {text-decoration: none;}
	div.grup:hover {background-color: #e5e5e5 !important;border-color: #ccc !important;}
	div.grup .panel-body-grup {padding-bottom: 0px !important;}
	.panel-body-grup {margin: -1px;}
	.tableDocuments .extensionIcon {color: white !important;float: left;font-size: 12px;font-weight: bold;margin-left: 12px;margin-top: -20px;position: relative;}
	.tableDocuments .adjuntIcon {color: white !important; float: left; font-size: 20px; margin-left: 7px; margin-top: -51px; position: relative;}
	.tableDocuments .signature {margin-top: -5px;}
	.table.table-bordered {margin-bottom: 0px !important;}
	.tableDocuments .left {padding-left: 10px;padding-right: 10px;}
	.tableDocuments .right {padding-left: 3px;padding-right: 10px;width: 100%;}
	.tableDocumentsTd {font-size: 10px;}
	.nom_document {padding-left: 5px;}
	#contingut-dades .icon.modificar {padding-left: 5px;padding-right: 5px;}
	#contingut-dades .icon.registre {padding-right: 5px;}
	.icon, .icon:hover, .icon:focus {text-decoration: none;color: #2a6496;}
	.icon {color: #428bca;}
	.icon {background: none repeat scroll 0 0 rgba(0, 0, 0, 0);}
	.fa-stack-2x {font-size: 1.7em;margin-top: 2px;}
	.fa.fa-certificate.fa-stack-1x { margin-top: -1px;}
	#contingut-dades .campOcult {background-color: lightgray;color: white;}
	#contingut-dades .var_dades {float: left;width:calc(100% - 15px);}
	#contingut-dades .var_botons {float: right;color: #428bca;}
	#contingut-dades .var_botons span {display: block;padding-top: 5px;}
	#contingut-dades .var_botons span:hover {color: #3071a9}	
	#contingut-dades .var_registre .obligatori {
	  	background-position: right 7px;
	  	padding-right: 15px;
	}
 	#grup-default-dades label.obligatori {background-position: right 7px;padding-right: 15px;} 
</style>

<c:set var="grupId" value="grup-default"/>
<c:if test="${not empty param.id}"><c:set var="grupId" value="grup-${param.id}"/></c:if>
<c:set var="paramDades" value="${requestScope[param.dadesAttribute]}"/>
<c:set var="paramCount" value="${param.count}"/>
<c:if test="${not empty param.titol}"><c:set var="paramTitol" value="${param.titol}"/></c:if>
<c:set var="paramNumColumnes" value="${3}"/>
<c:if test="${not empty param.numColumnes}"><c:set var="paramNumColumnes" value="${param.numColumnes}"/></c:if>
<c:set var="paramDesplegat" value="${true}"/>
<c:if test="${not empty param.desplegat}"><c:set var="paramDesplegat" value="${param.desplegat}"/></c:if>
<c:set var="paramMostrarCapsalera" value="${true}"/>
<c:if test="${not empty param.mostrarCapsalera}"><c:set var="paramMostrarCapsalera" value="${param.mostrarCapsalera}"/></c:if>
<c:if test="${not empty param.condicioCamp}"><c:set var="paramCondicioCamp" value="${param.condicioCamp}"/></c:if>
<c:if test="${not empty param.condicioValor}"><c:set var="paramCondicioValor" value="${param.condicioValor}"/></c:if>
<c:if test="${not empty param.condicioEmpty}"><c:set var="paramCondicioEmpty" value="${param.condicioEmpty}"/></c:if>
<c:if test="${not empty param.desplegadorClass}"><c:set var="paramDesplegadorClass" value="${param.desplegadorClass}"/></c:if>
<c:if test="${not empty param.procesId}"><c:set var="procesId" value="${param.procesId}"/></c:if>


<div class="panel panel-default">
	<c:choose>
		<c:when test="${not empty paramCount and ambOcults}">
			<c:set var="nombre" value="${paramCount}"/>
		</c:when>
		<c:when test="${not empty paramCount}">
			<c:set var="nombre" value="${paramCount}"/>
		</c:when>
		<c:otherwise>
			<c:set var="nombre" value="${0}"/>
			<c:forEach var="dada" items="${paramDades}">
				<c:set var="nombre" value="${nombre + 1}"/>
			</c:forEach>
		</c:otherwise>
	</c:choose>
	<c:if test="${paramMostrarCapsalera}">
		<div id="${grupId}-titol" class="panel-heading clicable grup tauladades" data-toggle="collapse" data-target="#${grupId}-dades">
			${paramTitol}
			<span class="badge">${nombre}</span>
			<div class="pull-right"><span class="icona-collapse fa fa-chevron-<c:if test="${paramDesplegat}">up</c:if><c:if test="${not paramDesplegat}">down</c:if>"></span></div>
		</div>
	</c:if>
	<c:if test="${not paramMostrarCapsalera}">
		<span class="badge-nombre" data-nombre="${nombre}"></span>
	</c:if>
	<div id="${grupId}-dades" class="clear collapse panel-body-grup<c:if test="${paramDesplegat}"> in</c:if>">
		<table class="table table-bordered">
			<colgroup>
				<c:forEach begin="0" end="${paramNumColumnes - 1}">
					<col width="${100 / paramNumColumnes}%"/>
				</c:forEach>
			</colgroup>
			<tbody>
				<c:set var="index" value="${0}"/>
				<c:set var="posicioOffset" value="${0}"/>
				<c:forEach var="dada" items="${paramDades}">
					<c:set var="posicioActual" value="${(index + posicioOffset) % paramNumColumnes}"/>
					<c:set var="condicioValor" value="${true}"/>
					<c:choose>
						<c:when test="${not empty paramCondicioCamp and not empty paramCondicioEmpty}">
							<c:set var="condicioValor" value="${empty dada[paramCondicioCamp]}"/>
						</c:when>
						<c:when test="${not empty paramCondicioCamp and empty paramCondicioValor}">
							<c:set var="condicioValor" value="${dada[paramCondicioCamp]}"/>
						</c:when>
						<c:when test="${not empty paramCondicioCamp and not empty paramCondicioValor}">
							<c:set var="condicioValor" value="${dada[paramCondicioCamp] == paramCondicioValor}"/>
						</c:when>
					</c:choose>
					<c:if test="${condicioValor}">
						<c:if test="${posicioActual == 0}"><tr></c:if>
						<c:set var="dadaTipusRegistre" value="${false}"/>
						<c:if test="${fn:endsWith(dada.class.name, 'DadaDto')}">
							<c:set var="dadaTipusRegistre" value="${dada.campTipusRegistre}"/>
						</c:if>
						<c:if test="${dadaTipusRegistre}">
							<c:if test="${posicioActual > 0}"><td colspan="${paramNumColumnes - posicioActual}">&nbsp;</td></tr><tr></c:if>
							<c:set var="posicioOffset" value="${posicioOffset + (paramNumColumnes - posicioActual) - 1}"/>
							<c:set var="posicioActual" value="${0}"/>
						</c:if>
						<c:choose>
							<c:when test="${fn:endsWith(dada.class.name, 'DadaDto')}">
								<%@ include file="expedientTaulaDades.jsp" %>
							</c:when>
							<c:when test="${fn:endsWith(dada.class.name, 'TascaDocumentDto')}">
								<%@ include file="expedientTaulaTascaDocument.jsp" %>
							</c:when>
							<c:when test="${fn:endsWith(dada.class.name, 'DocumentDto')}">
								<%@ include file="expedientTaulaDocument.jsp" %>
							</c:when>
							<c:when test="${fn:endsWith(dada.class.name, 'TerminiDto')}">								
								<%@ include file="expedientTaulaTermini.jsp" %>
							</c:when>
							<c:when test="${fn:endsWith(dada.class.name, 'ExpedientLogDto')}">								
								<%@ include file="expedientTaulaLog.jsp" %>
							</c:when>
							<c:when test="${fn:endsWith(dada.class.name, 'ExpedientTascaDto')}">								
								<%@ include file="expedientTaulaTasca.jsp" %>
							</c:when>
							<c:when test="${fn:endsWith(dada.class.name, 'TokenDto')}">								
								<%@ include file="expedientTaulaToken.jsp" %>
							</c:when>
							<c:when test="${fn:endsWith(dada.class.name, 'AccioDto')}">								
								<%@ include file="expedientTaulaAccio.jsp" %>
							</c:when>
							<c:otherwise>[Tipus desconegut]</c:otherwise>
						</c:choose>
						<c:if test="${(index == paramCount - 1) and posicioActual != (paramNumColumnes - 1) and not dadaTipusRegistre}"><td colspan="${paramNumColumnes - posicioActual - 1}">&nbsp;</td></c:if>
						<c:if test="${(index == paramCount - 1) or dadaTipusRegistre or (index != 0 and posicioActual == (paramNumColumnes - 1))}"></tr></c:if>
						<c:set var="index" value="${index + 1}"/>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div class="clear"></div>
</div>

<script type="text/javascript">
// <![CDATA[			
$(document).ready(function() {
	$('#${grupId}-dades').on('shown.bs.collapse', function() {
		$('#${grupId}-titol .icona-collapse').toggleClass('fa-chevron-down');
		$('#${grupId}-titol .icona-collapse').toggleClass('fa-chevron-up');
	});
	$('#${grupId}-dades').on('hidden.bs.collapse', function() {
		$('#${grupId}-titol .icona-collapse').toggleClass('fa-chevron-down');
		$('#${grupId}-titol .icona-collapse').toggleClass('fa-chevron-up');
	});
	//$('.panel.panel-default').find('a').attr('target', 'BLANK');
	$('#${grupId}-dades address').find('a').attr('target', 'BLANK');

	$('[title]').tooltip({container: 'body'});
	
	$("table tr:last").each(function(){
		var cols = $(this).find("td").size();
		if (cols == 0)
			$(this).remove();
//		else if (cols < 3)
//			$(this).append("<td colspan='" + (3 - cols) + "'></td>");
	});
});

var desplegats;
var panell;

function updatePanell() {
	if (desplegats != null) {
		desplegats.each(function(){
			$("#" + $(this).attr("id")).addClass("in");
		});
	}
	desplegats = null;

	$('#' + panell.attr("id") + ' .var-delete').heliumEvalLink({
		refrescarAlertes: true,
		refrescarPagina: false,
		ajaxRefrescarAlertes: true,
		alertesRefreshUrl: '<c:url value="/nodeco/v3/missatges"/>'
	});
	$('#' + panell.attr("id") + ' .var-edit').heliumEvalLink({
		refrescarAlertes: true,
		refrescarPagina: false,
		ajaxRefrescarAlertes: true,
		alertesRefreshUrl: '<c:url value="/nodeco/v3/missatges"/>'
	});

	updateBadges();
}

function reestructura (proces, correcte) {
	if (correcte) {
		panell = $('#panel_' + proces);
		desplegats = panell.find(".collapse.in");
		var ambOcults = "";
		if ($("#ambOcults").length)
			ambOcults = $("#ambOcults").prop('checked');
		panell.load('<c:url value="/nodeco/v3/expedient/${expedientId}/dades/"/>' + proces, {"ambOcults": ambOcults}, updatePanell);
	}
};

// function refresca (proces) {
// 	alert(proces);
// }

function refrescarAlertas(e) {
	$.ajax({
		url: "<c:url value="/nodeco/v3/missatges"/>",
		async: false,
		timeout: 20000,
		success: function (data) {
			$('#contingut-alertes').html(data);
		}
	});
}
//]]>
</script>
