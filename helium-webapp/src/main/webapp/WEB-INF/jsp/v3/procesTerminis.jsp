<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="numColumnes" value="${3}"/>
<style type="text/css">
div.procesTermini {
	color: white !important;
	background-color: #428bca !important;
	border-color: #357ebd !important;
	font-weight: bold;
}
div.procesTermini:hover {
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
.termini_options {min-width: 110px;}
.termini_options i, .options a {
	padding-right: 2px;
	padding-left: 2px;			
}
.dataTable_termini .panel.panel-default {
	border: 0 none;
	margin-bottom: 1px;
}
</style>
<c:choose>
	<c:when test="${not empty terminis}">
		<c:set var="procesFirst" value="${true}"/>
		<c:forEach items="${terminis}" var="dadesProces" varStatus="procesosStatus">
			<c:set var="agrupacioFirst" value="${true}"/>
			<c:set var="proces" value="${dadesProces.key}"/>
			<div id="dataTable_termini_${proces.id}">
				<div class="panel panel-default">
				
					<c:if test="${ expedient.tipus.tipus == 'FLOW'}">
						<div id="${proces.id}-titol-termini" class="panel-heading clicable procesTermini" data-processinstanceid="${proces.id}" data-toggle="collapse" data-target="#panel_termini_${proces.id}" data-id="${proces.id}_termini" data-carrega="<c:if test='${!procesFirst}'>ajax</c:if>">
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
					</c:if>
					<div id="panel_termini_${proces.id}" class="dataTable_termini ${expedient.tipus.tipus == 'FLOW' ? 'panel-body' : '' } collapse<c:if test="${procesFirst}"> in</c:if>">
						<c:choose>
							<c:when test="${not empty dadesProces.value && fn:length(dadesProces.value) > 0}">
								<c:set var="dadesAgrupacio" value="${dadesProces.value}" scope="request"/>
								<c:set var="iniciats_termini" value="${iniciats[proces.id]}" scope="request"/>
								<c:set var="procesId" value="${proces.id}" scope="request"/>								
								<c:set var="count" value="${fn:length(dadesProces.value)}"/>
								<c:import url="import/expedientTaula.jsp">
									<c:param name="dadesAttribute" value="dadesAgrupacio"/>
									<c:param name="numColumnes" value="${numColumnes}"/>
									<c:param name="count" value="${count}"/>
									<c:param name="desplegat" value="${true}"/>
									<c:param name="mostrarCapsalera" value="${false}"/>
								</c:import>
								<c:set var="agrupacioFirst" value="${false}"/>
							</c:when>
							<c:otherwise>
								<div class="well well-small"><spring:message code='expedient.termini.proces.cap' /></div>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
			<c:set var="procesFirst" value="${false}"/>
			<script type="text/javascript">
				$('#panel_termini_${proces.id} .icon').heliumEvalLink({
					refrescarAlertes: true,
					refrescarPagina: false,
					alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>"
				});	
			</script>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.termini.expedient.cap' /></div>
	</c:otherwise>
</c:choose>
