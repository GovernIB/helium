<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>
<head>
	<title><spring:message code='expedient.iniciar.iniciar_expedient' />: ${expedientTipus.nom}</title>
	<meta name="capsaleraTipus" content="llistat"/>
	<script type="text/javascript" src="<c:url value="/js/jquery.keyfilter.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
	<script type="text/javascript">
		// <![CDATA[
		function confirmar(e) {
			if ($('#nomesRefrescar').val() == 'true') {
				$('#submit').remove();
				return true;
			}
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			
			return confirm("<spring:message code='expedient.iniciar.confirm_iniciar' />");
		}

		$(document).ready(function() {
			$('select[name=any]').on('change', function () {
				if ($(this).val()) {
					$.ajax({
					    url:'canviAny/' + $(this).val() + '/${entornId}/${expedientTipus.id}',
					    type:'GET',
					    dataType: 'json',
					    success: function(json) {
					    	$("#numero").val(json);
					    }
					});
				}
			});	
		});	
		// ]]>
	</script>
</head>
<body>
	<h3 class="titol-tab titol-dades-tasca"><spring:message code='expedient.iniciar.iniciar_expedient' />: ${expedientTipus.nom}</h3>
	<form:form method="post" action="form" id="command" name="command" cssClass="form-horizontal form-tasca" onsubmit="return confirmar(event)" commandName="expedientInicioPasTitolCommand">
		<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
		<input type="hidden" name="entornId" value="${entornId}"/>
		<input type="hidden" name="responsableCodi" value="${responsableCodi}"/>
		<c:if test="${not empty definicioProcesId}">
			<input type="hidden" name="definicioProcesId" value="${definicioProcesId}"/>
		</c:if>
		<c:if test="${expedientTipus.teNumero and expedientTipus.demanaNumero}">
			<div class="control-group fila_reducida">				
				<c:set var="campPath" value="numero"/>
				<label data-required="true" class="control-label" data-required="true" for="${campPath}"><spring:message code='expedient.consulta.numero' /></label>
				<div class="controls">
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<spring:bind path="${campPath}">
							<input type="text" id="${campPath}" name="${campPath}" placeholder="<spring:message code='expedient.consulta.numero' />"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span11">
						</spring:bind>
						${campErrors}
					</div>
				</div>
			</div>
		</c:if>
		<c:if test="${expedientTipus.teTitol and expedientTipus.demanaTitol}">
			<div class="control-group fila_reducida">			
				<c:set var="campPath" value="titol"/>
				<label data-required="true" class="control-label" data-required="true" for="${campPath}"><spring:message code='expedient.consulta.titol' /></label>
				<div class="controls">
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<spring:bind path="${campPath}">
							<textarea type="text" id="${campPath}" name="${campPath}" placeholder="<spring:message code='expedient.consulta.titol' />"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span11"></textarea>
						</spring:bind>
						${campErrors}
					</div>
				</div>
			</div>
		</c:if>
		<c:if test="${expedientTipus.seleccionarAny}">
			<div class="control-group fila_reducida">
				<c:set var="campPath" value="any"/>
				<label class="control-label" for="${campPath}"><spring:message code='expedient.iniciar.canvi_any' /></label>
				<div class="controls">
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<form:select id="any" name="any" path="${campPath}" cssClass="span11">
						<form:options items="${anysSeleccionables}" itemLabel="valor" itemValue="codi"/>
					</form:select>
					${campErrors}
				</div>
			</div>
		</c:if>
		<br/>
		<div style="clear: both"></div>
		<div class="pull-right">
			<button type="button" class="btn" id="cancelar" name="accio" value="cancel" onclick="location='iniciar'">
				<spring:message code='comuns.cancelar' />
			</button>				
			<button type="submit" class="btn btn-primary" id="submit" name="accio" value="submit">
				<spring:message code='comuns.iniciar' />
			</button>
		</div>
	</form:form>

	<p class="aclaracio"><spring:message code='comuns.camps_marcats' /> <i class='icon-asterisk'></i> <spring:message code='comuns.son_oblig' /></p>
	
	<script>	
		$( '[data-required="true"]' )
			.closest(".control-group")
			.children("label")
			.prepend("<i class='icon-asterisk'></i> ");
	</script>
</body>
</html>
