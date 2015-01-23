<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="numColumnes" value="${3}"/>
<style type="text/css">
div.procesDocument {
	color: white !important;
	background-color: #428bca !important;
	border-color: #357ebd !important;
	font-weight: bold;
}
div.procesDocument:hover {
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
.btnNouDocument {
	text-align:right;
}
#dataTables_new {padding-top: 5px;padding-bottom: 10px;}
</style>
<c:choose>
	<c:when test="${not empty documents}">
		<c:set var="procesFirst" value="${true}"/>
		<c:forEach items="${documents}" var="dadesProces" varStatus="procesosStatus">
			<c:set var="agrupacioFirst" value="${true}"/>
			<c:set var="proces" value="${dadesProces.key}"/>
			<div id="dataTable_documents_${proces.id}">
				<div id="dataTables_new">
					<div class="btnNouDocument">
						<a 	class="icon btn btn-default" 
							href="../../v3/expedient/${expedientId}/nouDocument?processInstanceId=${proces.id}" 
							data-rdt-link-modal="true" 
							data-rdt-link-callback="recargarPanel(${proces.id});"
							data-rdt-link-modal-min-height="180">
							<span class="fa fa-plus"></span>
							 <spring:message code="expedient.boto.nou_document"/>
						</a>
					</div>
				</div>
				<div class="panel panel-default">
					<div id="${proces.id}-titol-documents" class="panel-heading clicable procesDocument" data-processinstanceid="${proces.id}" data-toggle="collapse" data-target="#panel_document_${proces.id}" data-id="${proces.id}_documents" data-carrega="<c:if test='${!procesFirst}'>ajax</c:if>">
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
					<div id="panel_document_${proces.id}" class="panel-body collapse<c:if test="${procesFirst}"> in</c:if>">
						<c:choose>
							<c:when test="${not empty dadesProces.value && fn:length(dadesProces.value) > 0}">
								<c:set var="dadesAgrupacio" value="${dadesProces.value}" scope="request"/>
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
								<div class="well well-small"><spring:message code='expedient.document.proces.cap' /></div>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
			<c:set var="procesFirst" value="${false}"/>
			<script type="text/javascript">
				$('#dataTable_documents_${proces.id} .icon').heliumEvalLink({
					refrescarAlertes: true,
					refrescarPagina: false,
					alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>"
				});	
			</script>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.document.expedient.cap' /></div>
	</c:otherwise>
</c:choose>
