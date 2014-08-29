<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<style>
div.proces {
	color: white !important;
	background-color: #428bca !important;
	border-color: #357ebd !important;
	font-weight: bold;
}
div.proces:hover {
	background-color: #3071a9 !important;
	border-color: #285e8e !important;
}
.panel-body {
	padding-bottom: 0px !important;
}
.panel-body > table {
	overflow: hidden;
}
.panel-body table:last-child {
	margin-bottom: 0px;
}
.contingut-carregant-proces {
	margin: 1em 0 2em 0;
	text-align: center;
}
</style>

<c:set var="numColumnes" value="${3}"/>
<c:choose>
	<c:when test="${not empty dades}">
		<c:set var="procesFirst" value="${true}"/>
		<c:forEach items="${dades}" var="dadesProces" varStatus="procesosStatus">
			<c:set var="agrupacioFirst" value="${true}"/>
			<c:set var="proces" value="${dadesProces.key}"/>
			<div class="panel panel-default">
				<div id="${proces.id}-titol" class="panel-heading clicable proces" data-toggle="collapse" data-target="#panel_${proces.id}" data-id="${proces.id}" data-carrega="<c:if test='${!procesFirst}'>ajax</c:if>">
					<c:choose>
						<c:when test="${proces.id == inicialProcesInstanceId}">
							<spring:message code='common.tabsexp.proc_princip'/>
						</c:when>
						<c:otherwise>${proces.titol}</c:otherwise>
					</c:choose>
					<div class="pull-right">
						<c:choose>
							<c:when test="${procesFirst}"><span class="icona-collapse fa fa-chevron-up"></span></c:when>
							<c:otherwise><span class="icona-collapse fa fa-chevron-down"></i></c:otherwise>
						</c:choose>
					</div>
				</div>
				<div id="panel_${proces.id}" class="panel-body collapse<c:if test="${procesFirst}"> in</c:if>">
				<c:choose>
				<c:when test="${not empty dadesProces.value && fn:length(dadesProces.value) > 0}">
					<c:forEach items="${dadesProces.value}" var="dadesAgrupacioEntry" varStatus="agrupacioStatus">
						<c:set var="agrupacio" value="${dadesAgrupacioEntry.key}"/>
						<c:set var="count" value="${fn:length(dadesAgrupacioEntry.value)}"/>
						<c:set var="dadesAgrupacio" value="${dadesAgrupacioEntry.value}" scope="request"/>
						<c:if test="${count > 0}">
							<c:choose>
								<c:when test="${not empty agrupacio}">
									<c:import url="import/expedientDadesTaula.jsp">
										<c:param name="id" value="${agrupacio.id}"/>
										<c:param name="dadesAttribute" value="dadesAgrupacio"/>
										<c:param name="titol" value="${agrupacio.nom}"/>
										<c:param name="numColumnes" value="${numColumnes}"/>
										<c:param name="count" value="${count}"/>
	<%-- 													<c:param name="condicioCamp" value="agrupacioId"/> --%>
	<%-- 													<c:param name="condicioValor" value="${agrupacio.id}"/> --%>
										<c:param name="desplegat" value="${agrupacioFirst}"/>
										<c:param name="desplegadorClass" value="agrupacio-desplegador"/>
									</c:import>
								</c:when>
								<c:otherwise>
									<c:import url="import/expedientDadesTaula.jsp">
										<c:param name="dadesAttribute" value="dadesAgrupacio"/>
	<%--													<c:param name="titol" value="${agrupacio.nom}"/> --%>
										<c:param name="numColumnes" value="${numColumnes}"/>
										<c:param name="count" value="${count}"/>
	<%-- 													<c:param name="condicioCamp" value="agrupacioId"/> --%>
	<%-- 													<c:param name="condicioEmpty" value="${true}"/> --%>
										<c:param name="desplegat" value="${true}"/>
										<c:param name="mostrarCapsalera" value="${false}"/>
									</c:import>
								</c:otherwise>
							</c:choose>
						</c:if>
						<c:set var="agrupacioFirst" value="${false}"/>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<div class="contingut-carregant-proces"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
<%-- 					<div class="well well-small"><spring:message code='expedient.dada.proces.cap' /></div> --%>
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
$(document).ready(function() {
	$('.proces').click( function() {
		var icona = $(this).find('.icona-collapse');
		icona.toggleClass('fa-chevron-down');
		icona.toggleClass('fa-chevron-up');
		if ($(this).data('carrega') == "ajax") {
			$('#contingut-carregant').show();
			var id = $(this).data('id');
			var panell = $('#panel_' + id);
			panell.load('<c:url value="/nodeco/v3/expedient/${expedientId}/dades/"/>' + id);
// 					function() {
// 						$('#contingut-carregant').hide();
// 					});
		}
	});
});
</script>