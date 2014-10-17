<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code='comuns.modificar'/></title>
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
</script>
</head>
<body>		
	<form:form action="documentModificarMas" cssClass="uniForm" enctype="multipart/form-data" method="post">
		<c:set var="inline" value="${false}"/>
			<c:set var="isRegistre" value="${false}"/>
			<c:set var="isMultiple" value="${false}"/>
			<c:choose>
				<c:when test="${dada.campTipus != 'REGISTRE'}">
					<c:choose>
						<c:when test="${dada.campMultiple}">
							<div class="multiple">
								<label for="${dada.varCodi}" class="control-label col-xs-3<c:if test="${dada.required}"> obligatori</c:if>">${dada.campEtiqueta} - ${dada.campTipus}</label>
								<c:forEach var="membre" items="${dada.multipleDades}" varStatus="varStatusCab">
									<c:set var="inline" value="${true}"/>
									<c:set var="campCodi" value="${dada.varCodi}[${varStatusCab.index}]"/>
									<div class="col-xs-9 input-group-multiple <c:if test="${varStatusCab.index != 0}">pad-left-col-xs-3</c:if>">
										<c:set var="isMultiple" value="${true}"/>
										<%@ include file="campsTasca.jsp" %>
										<c:set var="isMultiple" value="${false}"/>
									</div>
								</c:forEach>
								<c:if test="${empty dada.multipleDades}">
									<c:set var="inline" value="${true}"/>
									<c:set var="campCodi" value="${dada.varCodi}[0]"/>
									<div class="col-xs-9 input-group-multiple">
										<c:set var="isMultiple" value="${true}"/>
										<%@ include file="campsTasca.jsp" %>
										<c:set var="isMultiple" value="${false}"/>
									</div>
								</c:if>
								<c:if test="${!dada.readOnly && !tasca.validada}">
									<div class="form-group">
										<div class="col-xs-9 pad-left-col-xs-3">
											<c:if test="${not empty dada.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p></c:if>
											<button id="button_add_var_mult_${campCodi}" type="button" class="btn btn-default pull-left btn_afegir btn_multiple"><spring:message code='comuns.afegir' /></button>
										</div>
									</div>
								</c:if>
							</div>
						</c:when>
						<c:otherwise>
							<c:set var="campCodi" value="${dada.varCodi}"/>
							<%@ include file="campsTasca.jsp" %>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<%@ include file="campsTascaRegistre.jsp" %>
				</c:otherwise>
			</c:choose>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
			<button type="submit" class="btn btn-primary"><span class="fa fa-stop"></span>&nbsp;<spring:message code="expedient.accio.aturar.boto.aturar"/></button>
		</div>
	</form:form>
</body>
</html>