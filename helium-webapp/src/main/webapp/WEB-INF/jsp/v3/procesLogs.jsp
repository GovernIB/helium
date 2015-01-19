<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="numColumnes" value="${3}"/>
<style type="text/css">
div.procesLog {
	color: white !important;
	background-color: #428bca !important;
	border-color: #357ebd !important;
	font-weight: bold;
}
div.procesLog:hover {
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
.log_options {min-width: 110px;}
.log_options i, .options a {
	padding-right: 2px;
	padding-left: 2px;			
}
.dataTable_log .panel.panel-default {
	border: 0 none;
	margin-bottom: 1px;
}
.registre_a_retrocedir {background: #ffffcc;}
.btnLogDetall {
	text-align:right;
}
</style>
<c:choose>
	<c:when test="${not empty logs}">
		<c:set var="procesFirst" value="${true}"/>
		<div id="dataTables_new">
			<div class="btnLogDetall">
				<button type="button" onclick='recargarRegistro()' class="icon btn btn-default">
					<c:choose>
						<c:when test="${param.tipus_retroces == 0}">
							<input type="hidden" id="tipus_retroces" name="tipus_retroces" value="1"/>
							<spring:message code="expedient.log.tipus.tasca"/>
						</c:when>
						<c:otherwise>
							<input type="hidden" id="tipus_retroces" name="tipus_retroces" value="0"/>
							<spring:message code="expedient.log.tipus.detall"/>
						</c:otherwise>
					</c:choose>
				</button>
			</div>
		</div>
		<div id="spinner"><div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div></div>
		<div id="registro_tasques">
			<c:forEach items="${logs}" var="dadesProces" varStatus="procesosStatus">
				<c:set var="agrupacioFirst" value="${true}"/>
				<c:set var="proces" value="${dadesProces.key}"/>
				<div id="dataTable_log_${proces.id}">
					<div class="panel panel-default">
						<div id="${proces.id}-titol-log" class="panel-heading clicable procesLog" data-toggle="collapse" data-target="#panel_log_${proces.id}" data-id="${proces.id}_logs" data-carrega="<c:if test='${!procesFirst}'>ajax</c:if>">
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
						<div id="panel_log_${proces.id}" class="dataTable_log panel-body collapse<c:if test="${procesFirst}"> in</c:if>">
							<c:choose>
								<c:when test="${not empty dadesProces.value && fn:length(dadesProces.value) > 0}">
									<c:set var="dadesAgrupacio" value="${dadesProces.value}" scope="request"/>
									<c:set var="procesId" value="${proces.id}" scope="request"/>			
									<c:set var="isAdmin" value="${isAdmin}" scope="request"/>	
									<c:set var="tipus_retroces" value="${tipus_retroces}" scope="request"/>
									<c:set var="count" value="${fn:length(dadesProces.value)}"/>
									<c:set var="numBloquejos" value="${0}"/>
									<c:forEach var="log" items="${dadesProces.value}">
										<c:if test="${log.estat == 'BLOCAR'}"><c:set var="numBloquejos" value="${numBloquejos + 1}"/></c:if>
									</c:forEach>
									<c:set var="numBloquejos" value="${numBloquejos}" scope="request"/>
									<c:import url="import/expedientDadesTaula.jsp">
										<c:param name="dadesAttribute" value="dadesAgrupacio"/>
										<c:param name="numColumnes" value="${numColumnes}"/>
										<c:param name="count" value="${count}"/>
										<c:param name="desplegat" value="${true}"/>
										<c:param name="mostrarCapsalera" value="${false}"/>
									</c:import>
									<c:set var="agrupacioFirst" value="${false}"/>
								</c:when>
								<c:otherwise>
									<div class="well well-small"><spring:message code='expedient.log.proces.cap' /></div>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
				<c:set var="procesFirst" value="${false}"/>
				<script type="text/javascript">
					$('#panel_log_${proces.id} .icon').heliumEvalLink({
						refrescarAlertes: true,
						refrescarPagina: false,
						alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>"
					});	
					$(document).ready(function() {
						$('#panel_log_${proces.id}').on('shown.bs.collapse', function() {
							$('#${proces.id}-titol-log .icona-collapse').toggleClass('fa-chevron-down');
							$('#${proces.id}-titol-log .icona-collapse').toggleClass('fa-chevron-up');
						});
						$('#panel_log_${proces.id}').on('hidden.bs.collapse', function() {
							$('#${proces.id}-titol-log .icona-collapse').toggleClass('fa-chevron-down');
							$('#${proces.id}-titol-log .icona-collapse').toggleClass('fa-chevron-up');
						});
					});
				</script>
			</c:forEach>
		</div>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.log.expedient.cap' /></div>
	</c:otherwise>
</c:choose>
