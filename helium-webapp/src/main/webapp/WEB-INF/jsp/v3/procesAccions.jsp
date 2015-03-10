<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script src="<c:url value="/js/helium.datatable.js"/>"></script>
<c:set var="numColumnes" value="${3}"/>
<style type="text/css">
div.procesaccio {
	color: white !important;
	background-color: #428bca !important;
	border-color: #357ebd !important;
	font-weight: bold;
}
div.procesaccio:hover {
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
#llistat_accions .accio_options {
	width: 100px;
}
#dataTables_new {padding-top: 5px;padding-bottom: 10px;}
</style>

<c:choose>
	<c:when test="${not empty accions}">
		<c:set var="procesFirst" value="${true}"/>
		<div id="llistat_accions">
			<c:forEach items="${accions}" var="dadesProces" varStatus="procesosStatus">
				<c:set var="agrupacioFirst" value="${true}"/>
				<c:set var="proces" value="${dadesProces.key}"/>
				<div id="dataTable_accio_${proces.id}">
					<div class="panel panel-default">
						<div id="${proces.id}-titol-accions" class="panel-heading clicable procesaccio" data-toggle="collapse" data-target="#panel_accio_${proces.id}" data-id="${proces.id}_accio" data-carrega="<c:if test='${!procesFirst}'>ajax</c:if>">
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
						<div id="panel_accio_${proces.id}" class="dataTable_accio panel-body collapse<c:if test="${procesFirst}"> in</c:if>">
							<c:choose>
								<c:when test="${not empty dadesProces.value && fn:length(dadesProces.value) > 0}">
									<c:set var="dadesAgrupacio" value="${dadesProces.value}" scope="request"/>
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
									<div class="well well-small"><spring:message code='expedient.accio.proces.cap' /></div>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
				<c:set var="procesFirst" value="${false}"/>
			</c:forEach>
		</div>
	</c:when>
	<c:otherwise>
		<div class="panel-body-no">
			<div class="well well-small"><spring:message code='expedient.accio.expedient.cap' /></div>			
	</c:otherwise>
</c:choose>
