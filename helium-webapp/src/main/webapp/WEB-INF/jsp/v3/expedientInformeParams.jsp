<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
	<head>
		<title><spring:message code="expedient.consulta.params.modal.titol"/></title>	
		<meta name="decorator" content="senseCapNiPeus"/>
		<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
		<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
		<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
		<script src="<c:url value="/js/select2.min.js"/>"></script>
		<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
		<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
		
		<script src="<c:url value="/js/moment.js"/>"></script>
		<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
		<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
		<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
		
		<style>
			.select2-container-fix {
				padding-left: 15px !important;
				padding-right: 	0px !important;    
				margin-right: -10px;
			}
			.select2-container-fix .form-group{
				padding-left: 0px !important;
				padding-right: 	0px !important;
				margin-right: 	0px !important;
			}
			.form-group {
				padding-right: 	0px;
				margin-left: 	-15px !important;
				margin-right: 	15px !important;
				margin-bottom:	15px;
			}
			.col-xs-9 .form-group {margin-bottom:0px;}
			.controls {
				padding-right: 0 !important;
			}
			.form-group input, .form-group textarea {
				width: 100%;
			}
			.form-group.registre .btn_afegir{
	 			margin-top: 10px; 
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
			/*#tabnav .glyphicon {padding-right: 10px;}*/
		</style>
	</head>
	<body>		
		<form:form method="post" cssClass="well_mod form-horizontal form-tasca" commandName="expedientInformeParametrosCommand">
	 		<div class="control-group fila_reducida">
				<c:forEach var="camp" items="${campsInformeParams}">
					<c:set var="campErrors"><form:errors path="${camp.varCodi}"/></c:set>
					<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
						<label for="${camp.varCodi}" class="control-label col-xs-3<c:if test="${camp.required}"> obligatori</c:if>">${camp.campEtiqueta}</label>
						<div class="controls col-xs-9">
							<c:set var="required" value="${false}"/>
							<c:choose>
								<%-- STRING -------------------------------------------------------------------------------------%>
								<c:when test="${camp.campTipus == 'STRING'}">
									<form:input path="${camp.varCodi}" cssClass="form-control" id="${camp.varCodi}" data-required="${camp.required}" />
								</c:when>
								
								<%-- INTEGER ------------------------------------------------------------------------------------%>
								<c:when test="${camp.campTipus == 'INTEGER'}">
									<form:input path="${camp.varCodi}" cssClass="form-control text-right enter" id="${camp.varCodi}" data-required="${camp.required}"/>
								</c:when>
					
								<%-- FLOAT --------------------------------------------------------------------------------------%>
								<c:when test="${camp.campTipus == 'FLOAT'}">
									<form:input path="${camp.varCodi}" cssClass="form-control text-right float" id="${camp.varCodi}" data-required="${camp.required}"/>
								</c:when>
					
								<%-- DATE ---------------------------------------------------------------------------------------%>		
								<c:when test="${camp.campTipus == 'DATE'}">
									<div class="input-group">
										<form:input path="${camp.varCodi}" id="${camp.varCodi}" cssClass="date form-control" placeholder="dd/mm/aaaaa" data-required="${camp.required}"/>
										<span class="input-group-addon btn_date"><span class="fa fa-calendar"></span></span>
									</div>
								</c:when>
								<%-- BOOLEAN ---------------------------------------------------------------------------------------%>
								<c:when test="${camp.campTipus == 'BOOLEAN'}">
									<div class="select2-container-fix">		
										<hel:inputSelect inline="true" name="${camp.varCodi}" placeholder="${camp.campEtiqueta}" optionItems="${valorsBoolea}" optionValueAttribute="codi" optionTextAttribute="valor"/>
									</div>
								</c:when>
							</c:choose>
						</div>
					</div>
				</c:forEach>
			</div>
			<div id="modal-botons" class="well">
				<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
				<button type="submit" class="btn btn-primary"><span class="icon-download-alt"></span>&nbsp;<spring:message code="expedient.consulta.informe"/></button>					
			</div>
		</form:form>
	</body>
</html>