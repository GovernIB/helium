<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma">ca</c:set>
<html>
<head>
	<title><spring:message code='comuns.modificar'/></title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/3.4.8/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
	<link href="<c:url value="/css/tascaForm.css"/>" rel="stylesheet"/>

	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>

	<style type="text/css">
		.carregant {margin: 1em 0 2em 0;text-align: center;}
		.col-xs-3 {width: 20%;}
		.col-xs-9 {width: 80%;}
		.pad-left-col-xs-3 {left: 20%;}
	</style>
</head>
<body>
	<form:form id="command" modelAttribute="modificarVariableCommand" action="" cssClass="form-horizontal form-tasca" method="post">
		<input type="hidden" id="procesId" name="procesId" value="${procesId}">
		<input type="hidden" id="varCodi" name="varCodi" value="${varCodi}">

		<c:set var="inline" value="${false}"/>
		<c:set var="isRegistre" value="${false}"/>
		<c:set var="isMultiple" value="${false}"/>

		<c:set var="command" value="${modificarVariableCommand}"/>

		<c:choose>
			<c:when test="${dada.campTipus != 'REGISTRE'}">
				<c:choose>
					<c:when test="${dada.campMultiple}">
						<c:set var="campErrorsMultiple"><form:errors path="${dada.varCodi}"/></c:set>
						<div class="multiple<c:if test="${not empty campErrorsMultiple}"> has-error</c:if>">
							<div class="row">
								<div class="col-xs-3">
									<label for="${dada.varCodi}" class="control-label col-xs-12">${dada.campEtiqueta}</label>
								</div>
								<div class="col-xs-9">
									<c:forEach var="membre" items="${command[dada.varCodi]}" varStatus="varStatusCab">
										<c:set var="inline" value="${true}"/>
										<c:set var="campCodi" value="${dada.varCodi}[${varStatusCab.index}]"/>
										<c:set var="campNom" value="${dada.varCodi}"/>
										<c:set var="campIndex" value="${varStatusCab.index}"/>
										<div class="col-xs-9 input-group-multiple">
											<c:set var="isMultiple" value="${true}"/>
											<%@ include file="campsTasca.jsp" %>
											<c:set var="isMultiple" value="${false}"/>
										</div>
									</c:forEach>
									<c:if test="${empty dada.multipleDades}">
										<c:set var="inline" value="${true}"/>
										<c:set var="campCodi" value="${dada.varCodi}[0]"/>
										<c:set var="campNom" value="${dada.varCodi}"/>
										<c:set var="campIndex" value="0"/>
										<div class="col-xs-9 input-group-multiple">
											<c:set var="isMultiple" value="${true}"/>
											<%@ include file="campsTasca.jsp" %>
											<c:set var="isMultiple" value="${false}"/>
										</div>
									</c:if>
									<div class="form-group col-xs-9 input-group-multiple">
										<div>
											<c:if test="${not empty dada.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p></c:if>
											<button id="button_add_var_mult_${campCodi}" type="button" class="btn btn-default pull-left btn_afegir btn_multiple"><spring:message code='comuns.afegir' /></button>
											<div class="clear"></div>
											<c:if test="${not empty campErrorsMultiple}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${dada.varCodi}"/></p></c:if>
										</div>
									</div>
								</div>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<c:set var="campCodi" value="${dada.varCodi}"/>
						<c:set var="campNom" value="${dada.varCodi}"/>
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
			<button class="btn btn-primary right" type="submit" name="accio" value="modificar_variable">
				<spring:message code='comuns.modificar' />
			</button>
		</div>
	</form:form>
</body>
</html>
