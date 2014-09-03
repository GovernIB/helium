<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="numColumnes" value="${3}"/>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title>${tasca.titol}</title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery.keyfilter.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/helium.tramitar.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
	<style>
		.alert {margin-top: 20px;}
		input, select, textarea {
			width: 100%;
		}
		.form-group {
			padding-right: 	15px;
			margin-left: 	10px !important;
			margin-bottom:	15px;
		}
		.form-group input, .form-group textarea {
			width: 100%;
		}
		
		.form-group li > .select2-container {
			width: 100%;
			padding-right: 20px;
		}
		
		.form-group .select2-container {
			width: calc(100% + 14px);
		}
		.form-group.condensed {
			margin-bottom: 0px;
		}
		.form-group.registre .btn_afegir{
 			margin-top: 10px; 
		}
		.registre table .colEliminarFila {
			width: 1px;
		}
		.registre table .opciones {
			text-align: center;
		}
		p.help-block {
			padding-top: 0;	
			margin-top: 4px !important;
		}
		.clear {
			clear: both;
		}
		.clearForm {
			clear: both;
			margin-bottom: 10px;
			border-bottom: solid 1px #EAEAEA;
		}
		.input-append {
			width: calc(100% - 27px);
		}
		.eliminarFila {
			padding: 4px 6px;
		}
		.tercpre {
			padding-left: 0px !important;
			padding-right: 8px !important;
		}
		.tercmig {
			padding-left: 4px !important;
			padding-right: 4px !important;
		}
		.tercpost {
			padding-left: 8px !important;
			padding-right: 0px !important;
		}
		.table {margin-bottom: 0px;}
		#tabnav .glyphicon {padding-right: 10px;}
	</style>
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
		<li class=""><a href="#tasca" data-toggle="tab">${pipellaIndex}. Tasca</a></li>
		<c:set var="pipellaIndex" value="${pipellaIndex + 1}"/>
		<c:if test="${not empty dades}">
			<li class="active"><a href="#dades" data-toggle="tab"><c:if test="${not tasca.validada}"><span class="glyphicon glyphicon-warning-sign"> </span></c:if>${pipellaIndex}. Dades</a></li>
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
		<div class="tab-pane" id="tasca">
			<%@ include file="campsTascaInfo.jsp" %>
		</div>
		<c:if test="${not empty dades}">
			<div class="tab-pane active" id="dades">
				<c:if test="${not tasca.validada}">
					<div class="alert alert-danger">
						<button class="close" data-dismiss="alert">×</button>
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
				<form:form onsubmit="return confirmar(this)" action="" cssClass="form-horizontal form-tasca" method="post" commandName="command">
					<c:forEach var="dada" items="${dades}" varStatus="varStatusMain">
						<c:set var="inline" value="${false}"/>
						<%@ include file="campsTasca.jsp" %>
						<%@ include file="campsTascaRegistre.jsp" %>
					</c:forEach>
					<div id="guardarValidarTarea">
						<%@ include file="campsTascaGuardarTasca.jsp" %>
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
</html>
