<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code='expedient.iniciar.iniciar_expedient' />: ${expedientTipus.nom}</title>
	<meta name="capsaleraTipus" content="llistat"/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<script src="//code.jquery.com/ui/1.11.3/jquery-ui.js"></script>
	<link rel="stylesheet" href="//code.jquery.com/ui/1.11.3/themes/smoothness/jquery-ui.css">
	<script src="https://www.java.com/js/deployJava.js"></script>
	
	
	<hel:modalHead/>
	<link href="<c:url value="/css/tascaForm.css"/>" rel="stylesheet"/>
</head>
<body>
	<c:if test="${not empty tasca.tascaFormExternCodi}">
		<script type="text/javascript" src="<c:url value="/dwr/interface/formulariExternDwrService.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
		<div class="form-group">
			
			<div id="modal-botons-form-extern" class="pull-right form_extern">
				<a id="boto-formext" href="<c:url value="/v3/expedient/tasca/${expedientTipus.id}/${definicioProces.id}/${tasca.id}/formExtern"/>" class="btn btn-default"><span class="fa fa-pencil-square-o"></span>&nbsp;<spring:message code='tasca.form.obrir_form' /></a>
			</div>
			
			<script type="text/javascript">
				// <![CDATA[
					function recargarPanel (tag, correcte) {
						if (correcte) {
							location.reload();
						}
					}
				//]]>
			</script>
		
		</div>
	</c:if>
	
	<form:form id="command" name="command" action="../../iniciarForm/${expedientTipus.id}/${definicioProces.id}" cssClass="form-horizontal form-tasca" method="post">
		<form:hidden path="id"/>
		<form:hidden path="entornId"/>
		<form:hidden path="expedientTipusId"/>
		<form:hidden path="definicioProcesId"/>
		<c:forEach var="dada" items="${dades}" varStatus="varStatusMain">
			<c:set var="inline" value="${false}"/>
			<c:set var="isRegistre" value="${false}"/>
			<c:set var="isMultiple" value="${false}"/>
			<c:choose>
				<c:when test="${dada.campTipus != 'REGISTRE'}">
					<c:choose>
						<c:when test="${dada.campMultiple}">
							<c:set var="campErrorsMultiple"><form:errors path="${dada.varCodi}"/></c:set>
							<div class="multiple<c:if test="${not empty campErrorsMultiple}"> has-error</c:if>">	
								<label for="${dada.varCodi}" class="control-label col-xs-3<c:if test="${dada.required}"> obligatori</c:if>">${dada.campEtiqueta}</label>
								<c:forEach var="membre" items="${command[dada.varCodi]}" varStatus="varStatusCab">
									<c:set var="inline" value="${true}"/>
									<c:set var="campCodi" value="${dada.varCodi}[${varStatusCab.index}]"/>
									<c:set var="campNom" value="${dada.varCodi}"/>
									<c:set var="campIndex" value="${varStatusCab.index}"/>
									<div class="col-xs-9 input-group-multiple <c:if test="${varStatusCab.index != 0}">pad-left-col-xs-3</c:if>">
										<c:set var="isMultiple" value="${true}"/>
										<%@ include file="../campsTasca.jsp" %>
										<c:set var="isMultiple" value="${false}"/>
									</div>
								</c:forEach>
								<c:if test="${empty dada.multipleDades}">
									Buit!!
									<c:set var="inline" value="${true}"/>
									<c:set var="campCodi" value="${dada.varCodi}[0]"/>
									<c:set var="campNom" value="${dada.varCodi}"/>
									<c:set var="campIndex" value="0"/>
									<div class="col-xs-9 input-group-multiple">
										<c:set var="isMultiple" value="${true}"/>
										<%@ include file="../campsTasca.jsp" %>
										<c:set var="isMultiple" value="${false}"/>
									</div>
								</c:if>
								<c:if test="${!dada.readOnly && !tasca.validada}">
									<div class="form-group">
										<div class="col-xs-9 pad-left-col-xs-3">
											<c:if test="${not empty dada.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p></c:if>
											<button id="button_add_var_mult_${campCodi}" type="button" class="btn btn-default pull-left btn_afegir btn_multiple"><spring:message code='comuns.afegir' /></button>
											<div class="clear"></div>
											<c:if test="${not empty campErrorsMultiple}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${dada.varCodi}"/></p></c:if>
										</div>
									</div>
								</c:if>
							</div>
						</c:when>
						<c:otherwise>
							<c:set var="campCodi" value="${dada.varCodi}"/>
							<c:set var="campNom" value="${dada.varCodi}"/>
							<%@ include file="../campsTasca.jsp" %>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<%@ include file="../campsTascaRegistre.jsp" %>
				</c:otherwise>
			</c:choose>
			<c:if test="${not varStatusMain.last}"><div class="clearForm"></div></c:if>
		</c:forEach>
		<div id="modal-botons">
			<button type="submit" class="botons-iniciar modal-tancar btn btn-default" name="submit" value="cancel">
				<spring:message code='comuns.cancelar' />
			</button>			
			<button type="submit" id="iniciar" name="accio" class="botons-iniciar btn btn-primary" value="iniciar">
				<spring:message code='comuns.iniciar' />
			</button>
			<script type="text/javascript">
			// <![CDATA[
		        $(".botons-iniciar").click(function(e) {
					var e = e || window.event;
					e.cancelBubble = true;
					if (e.stopPropagation) e.stopPropagation();
					var accio = $(this).attr('value');
					if (accio.indexOf('cancel') == 0 || accio.indexOf('guardar') == 0) {
						return true;
					}
					$("table").each(function(){
						if ($('#command').hasClass("hide")) {
							$('#command').remove();
						}
					});
					<c:choose>
						<c:when test="${not ((expedientTipus.teNumero and expedientTipus.demanaNumero) or (expedientTipus.teTitol and expedientTipus.demanaTitol))}">
							return confirm("<spring:message code='expedient.iniciar.confirm_iniciar' />");
						</c:when>
						<c:otherwise>
							return true;
						</c:otherwise>
					</c:choose>
				});
			// ]]>
			</script>
		</div>
	</form:form>
</body>
</html>