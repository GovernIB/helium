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
.accio_options {min-width: 110px;}
.accio_options i, .options a {
	padding-right: 2px;
	padding-left: 2px;			
}
.dataTable_accio {
	border: 0 none;
}
.dataTable_accio {
	padding:0 !important;
}
.registre_a_retrocedir {background: #ffffcc;}
.dadesTaulaaccio {
 	width: 100%;
}
.tauladades {padding-bottom: 10px}
.dataTable_accio > .panel.panel-default > .panel-body-grup > .table-bordered > tbody > tr > td {
	border: 0 none !important;
	padding: 0px;
	width: 100%;
	height: 0px;
}
#llistat_accions >div > .panel.panel-default {
	margin-bottom: 10px;
	border: 0px;
}
.dataTable_accio > .panel.panel-default > .panel-body-grup > .table-bordered > tbody > tr > td.dadesTaulaaccioTd {
	height: auto;
}

.dataTable_accio > .panel.panel-default > .panel-body-grup > .table-bordered > tbody > tr {
	height: 0px;
	display: none;
}
.dataTable_accio > .panel.panel-default > .panel-body-grup > .table-bordered > tbody > tr.dadesTaulaaccioTr {
	height: auto;
	display: table-row;
}
.dataTable_accio .dadesTaulaaccioTd {
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

.dataTable_accio .dadesTaulaaccioTd > .panel.panel-default{
	margin-bottom: 0px;
}
</style>

<!-- 
<c:if test="${not empty accions}">
	<c:forEach var="accio" items="${accions}">
		<li><a href="../../v3/expedient/${expedient.id}/accio?accioId=${accio.id}"><span class="fa fa-bolt"></span> ${accio.nom}</a></li>
	</c:forEach>
</c:if>
-->
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
									<c:set var="hiHaPendents" value="0"/>
									<c:set var="hiHaNoPendents" value="0"/>
									<c:forEach var="accio" items="${dadesProces.value}">
										<c:if test="${accio.open}"><c:set var="hiHaPendents" value="${hiHaPendents + 1}"/></c:if>
										<c:if test="${not accio.open}"><c:set var="hiHaNoPendents" value="${hiHaNoPendents + 1}"/></c:if>
									</c:forEach>
									<c:set var="hiHaPendents" value="${hiHaPendents}" scope="request"/>
									<c:set var="hiHaNoPendents" value="${hiHaNoPendents}" scope="request"/>
									<c:set var="contHiHaPendents" value="0" scope="request"/>
									<c:set var="contHiHaNoPendents" value="0" scope="request"/>
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
									<div class="panel-body-no">
										<div class="well well-small"><spring:message code='expedient.accio.proces.cap' /></div>
									</div>
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
