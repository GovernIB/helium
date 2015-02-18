<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script src="<c:url value="/js/helium.datatable.js"/>"></script>
<c:set var="numColumnes" value="${3}"/>
<style type="text/css">
div.procesTasca {
	color: white !important;
	background-color: #428bca !important;
	border-color: #357ebd !important;
	font-weight: bold;
}
div.procesTasca:hover {
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
.tasca_options {min-width: 110px;}
.tasca_options i, .options a {
	padding-right: 2px;
	padding-left: 2px;			
}
.dataTable_tasca {
	border: 0 none;
}
.dataTable_tasca {
	padding:0 !important;
}
.registre_a_retrocedir {background: #ffffcc;}
.dadesTaulaTasca {
 	width: 100%;
}
.tauladades {padding-bottom: 10px}
.dataTable_tasca > .panel.panel-default > .panel-body-grup > .table-bordered > tbody > tr > td {
	border: 0 none !important;
	padding: 0px;
	width: 100%;
	height: 0px;
}
#llistat_tasques >div > .panel.panel-default {
	margin-bottom: 10px;
	border: 0px;
}
.dataTable_tasca > .panel.panel-default > .panel-body-grup > .table-bordered > tbody > tr > td.dadesTaulaTascaTd {
	height: auto;
}

.dataTable_tasca > .panel.panel-default > .panel-body-grup > .table-bordered > tbody > tr {
	height: 0px;
	display: none;
}
.dataTable_tasca > .panel.panel-default > .panel-body-grup > .table-bordered > tbody > tr.dadesTaulaTascaTr {
	height: auto;
	display: table-row;
}
.dataTable_tasca .dadesTaulaTascaTd {
	padding: 15px !important;
}
.panel-body-no {
	padding-left: 15px;
    padding-right: 15px;
    padding-top: 15px;
	border: 1px solid #ddd;
	background-color: #fff;
    border-bottom-left-radius: 4px;
    border-bottom-right-radius: 4px;
    box-shadow: 0 1px 1px rgba(0, 0, 0, 0.05);
}

.dataTable_tasca .dadesTaulaTascaTd > .panel.panel-default{
	margin-bottom: 0px;
}
</style>
<c:choose>
	<c:when test="${not empty tasques}">
		<c:set var="procesFirst" value="${true}"/>
		<div id="llistat_tasques">
			<c:forEach items="${tasques}" var="dadesProces" varStatus="procesosStatus">
				<c:set var="agrupacioFirst" value="${true}"/>
				<c:set var="proces" value="${dadesProces.key}"/>
				<div id="dataTable_tasca_${proces.id}">
					<div class="panel panel-default">
						<div id="${proces.id}-titol-tasques" class="panel-heading clicable procesTasca" data-toggle="collapse" data-target="#panel_tasca_${proces.id}" data-id="${proces.id}_tasca" data-carrega="<c:if test='${!procesFirst}'>ajax</c:if>">
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
						<div id="panel_tasca_${proces.id}" class="dataTable_tasca panel-body collapse<c:if test="${procesFirst}"> in</c:if>">
							<c:choose>
								<c:when test="${not empty dadesProces.value && fn:length(dadesProces.value) > 0}">
									<c:set var="dadesAgrupacio" value="${dadesProces.value}" scope="request"/>
									<c:set var="procesId" value="${proces.id}" scope="request"/>
									<c:set var="hiHaPendents" value="0"/>
									<c:set var="hiHaNoPendents" value="0"/>
									<c:forEach var="tasca" items="${dadesProces.value}">
										<c:if test="${tasca.open}"><c:set var="hiHaPendents" value="${hiHaPendents + 1}"/></c:if>
										<c:if test="${not tasca.open}"><c:set var="hiHaNoPendents" value="${hiHaNoPendents + 1}"/></c:if>
									</c:forEach>
									<c:set var="hiHaPendents" value="${hiHaPendents}" scope="request"/>
									<c:set var="hiHaNoPendents" value="${hiHaNoPendents}" scope="request"/>
									<c:set var="contHiHaPendents" value="0" scope="request"/>
									<c:set var="contHiHaNoPendents" value="0" scope="request"/>
									<c:set var="count" value="${fn:length(dadesProces.value)}"/>
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
									<div class="panel-body-no">
										<div class="well well-small"><spring:message code='expedient.tasca.proces.cap' /></div>
									</div>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
				<c:set var="procesFirst" value="${false}"/>
				<script type="text/javascript">
					$('#panel_tasca_${proces.id} .icon').heliumEvalLink({
						refrescarAlertes: true,
						refrescarPagina: false,
						alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>"
					});	
					$('#dropdown-menu-context-${proces.id} a').heliumEvalLink({
						refrescarAlertes: true,
						refrescarPagina: false,
						alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>"
					});						
					$(document).ready(function() {
						$('#${proces.id}-titol-tasques').click( function() {
							var icona = $(this).find('.icona-collapse');
							icona.toggleClass('fa-chevron-down');
							icona.toggleClass('fa-chevron-up');
						});
					});
				</script>
			</c:forEach>
		</div>
	</c:when>
	<c:otherwise>
		<div class="panel-body-no">
			<div class="well well-small"><spring:message code='expedient.tasca.expedient.cap' /></div>			
	</c:otherwise>
</c:choose>
