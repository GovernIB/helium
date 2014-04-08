<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<c:set var="numColumnes" value="${3}"/>
<html>
<head>
	<title>${tasca.titol}</title>
	<script type="text/javascript" src="<c:url value="/js/jquery.keyfilter.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/helium.tramitar.js"/>"></script>
	<script type="text/javascript">
		// <![CDATA[		
			function confirmar(form) {
				$("table").each(function(){
					if ($(this).hasClass("hide")) {
						$(this).remove();
					}
				});
				return true;
			}
		// ]]>
	</script>
</head>
<body>
	<c:if test="${not empty dadesNomesLectura}">
		<c:import url="import/expedientDadesTaula.jsp">
			<c:param name="dadesAttribute" value="dadesNomesLectura"/>
			<c:param name="titol" value="Dades de referència"/>
			<c:param name="numColumnes" value="${numColumnes}"/>
			<c:param name="count" value="${fn:length(dadesNomesLectura)}"/>
			<c:param name="desplegat" value="${false}"/>
			<c:param name="desplegadorClass" value="agrupacio-desplegador"/>
		</c:import>
	</c:if>
	<c:if test="${not empty documentsNomesLectura}">
		<c:import url="import/expedientDadesTaula.jsp">
			<c:param name="dadesAttribute" value="documentsNomesLectura"/>
			<c:param name="titol" value="Documents de referència"/>
			<c:param name="numColumnes" value="${numColumnes}"/>
			<c:param name="count" value="${fn:length(documentsNomesLectura)}"/>
			<c:param name="desplegat" value="${false}"/>
			<c:param name="desplegadorClass" value="agrupacio-desplegador"/>
		</c:import>
	</c:if>
	<c:set var="pipellaIndex" value="${1}"/>
	<ul id="tabnav" class="nav nav-tabs">
		<c:if test="${not empty dades}">
			<li class="active <c:if test="${not tasca.validada}"> warn</c:if>"><a href="#dades" data-toggle="tab">${pipellaIndex}. Dades</a></li>
			<c:set var="pipellaIndex" value="${pipellaIndex + 1}"/>
		</c:if>
		<c:if test="${not empty documents}">
			<li class=""><a href="#documents" data-toggle="tab">${pipellaIndex}. Documents</a></li>
			<c:set var="pipellaIndex" value="${pipellaIndex + 1}"/>
		</c:if>
		<c:if test="${not empty signatures}">
			<li class=""><a href="#signatures" data-toggle="tab">${pipellaIndex}. Signatures</a></li>
			<c:set var="pipellaIndex" value="${pipellaIndex + 1}"/>
		</c:if>
	</ul>
	<div class="tab-content">
		<c:if test="${not empty dades}">
			<div class="tab-pane active" id="dades">
				<c:if test="${not tasca.validada}">
					<div class="missatge missatgesWarn">
						<c:choose>
							<c:when test="${empty tasca.formExtern}">
								<p><spring:message code='tasca.form.no_validades' /></p>
							</c:when>
							<c:otherwise>
								<p><spring:message code='tasca.form.compl_form' /></p>
							</c:otherwise>
						</c:choose>
					</div>
				</c:if>
				<c:set var="hiHaCampsReadOnly" value="${false}"/>
				<c:forEach var="camp" items="${dades}">
					<c:if test="${camp.readOnly}">
						<c:set var="hiHaCampsReadOnly" value="${true}"/>
					</c:if>
				</c:forEach>
				<c:set var="hiHaDocumentsReadOnly" value="${false}"/>
				<c:forEach var="document" items="${documents}">
					<c:if test="${document.readOnly}">
						<c:set var="hiHaDocumentsReadOnly" value="${true}"/>
					</c:if>
				</c:forEach>
				<c:if test="${hiHaCampsReadOnly or hiHaDocumentsReadOnly}">
					<div class="missatge missatgesBlau">
						<c:if test="${hiHaDocumentsReadOnly}">
							<c:forEach var="documenTasca" items="${documents}">
								<c:if test="${documenTasca.readOnly}">
									<h4 class="titol-missatge">
										${documenTasca.documentNom}&nbsp;&nbsp;
										<c:set var="tascaActual" value="${tasca}" scope="request"/>
										<c:set var="documentActual" value="${documenTasca.documentCodi}" scope="request"/>
										<c:set var="codiDocumentActual" value="${documenTasca.documentCodi}" scope="request"/>
										<c:import url="../common/iconesConsultaDocument.jsp"/>
									</h4><br/>
								</c:if>
							</c:forEach>
						</c:if>
						<c:if test="${hiHaCampsReadOnly}">
							<div class="form-horizontal form-tasca">
								<span class="titol-missatge"><fmt:message key='common.tascaro.dadesref' /></span>
								<form  id="commandReadOnly" name="commandReadOnly" action="form" method="post">
									<input type="hidden" id="id" name="id" value="${tasca.id}"/>
									<div class="inlineLabels">
										<c:forEach var="dada" items="${dades}" varStatus="varStatusMain">
											<c:if test="${dada.readOnly}">
												<div class="control-group">
													<label class="control-label" for="${dada.varCodi}">${dada.campEtiqueta} - ${dada.campTipus}</label>
													
													<c:set var="dada" value="${dada}"/>
													<c:set var="dada_multiple" value=""/>
													<%@ include file="campsTasca.jsp" %>
													<%@ include file="campsTascaRegistre.jsp" %>
												</div>
											</c:if>
										</c:forEach>
									</div>
								</form>
							</div>
						</c:if>
					</div>
				</c:if>
				<form:form onsubmit="return confirmar(this)" id="command" name="command" action="form" cssClass="form-horizontal form-tasca" method="post" commandName="command">
					<input type="hidden" id="id" name="id" value="${tasca.id}"/>
					<input type="hidden" id="helFinalitzarAmbOutcome" name="helFinalitzarAmbOutcome" value="@#@"/>
					<c:forEach var="dada" items="${dades}" varStatus="varStatusMain">
						<c:if test="${not dada.readOnly}">
							<div class="control-group fila_reducida">
								<label class="control-label" for="${dada.varCodi}">${dada.campEtiqueta} - ${dada.campTipus}</label>
								
								<c:set var="dada" value="${dada}"/>
								<%@ include file="campsTasca.jsp" %>
								<%@ include file="campsTascaRegistre.jsp" %>
							</div>
						</c:if>
					</c:forEach>
					<div id="guardarValidarTarea">
						<c:if test="${empty dades}">
							<%@ include file="campsTascaInfo.jsp" %>		
						</c:if>
						<c:if test="${not empty dades}">
							<div style="clear: both"></div>
							<%@ include file="campsTascaGuardarTasca.jsp" %>
						</c:if>
					</div>
				</form:form>
				<div class="hide" id="finalizarTarea">
					<%@ include file="campsTascaTramitacioTasca.jsp" %>
				</div>
			</div>
		</c:if>
		<c:if test="${not empty documents}">
			<div class="tab-pane" id="documents">
				<c:forEach var="document" items="${documents}">
				<div class="well well-small">
					<h4>${document.documentNom}</h4>
				</div>
				</c:forEach>
			</div>
		</c:if>
		<c:if test="${not empty signatures}">
			<div class="tab-pane" id="signatures">
				signatures
			</div>
		</c:if>
	</div>
</body>

<%!
private String toJavascript(String str) {
	if (str == null)
		return null;
	return str.replace("'", "\\'");
}
%>
