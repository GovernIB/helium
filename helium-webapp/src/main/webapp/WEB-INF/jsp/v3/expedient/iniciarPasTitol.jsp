<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><spring:message code='expedient.iniciar.iniciar_expedient' />: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="Nou expedient"/>
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
				$('button.submitButton').remove();
				return true;
			}
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			if ("cancel" == submitAction)
				return true;
			return confirm("<spring:message code='expedient.iniciar.confirm_iniciar' />");
		}
		function canviAny(element) {
			$('#nomesRefrescar').val('true');
			$('#command').submit();
		}
		// ]]>
	</script>
</head>
<body>
	<h3 class="titol-tab titol-dades-tasca"><spring:message code='expedient.iniciar.iniciar_expedient' />: ${expedientTipus.nom}</h3>
	<form:form action="iniciarPasTitol" id="command" name="command" cssClass="form-horizontal form-tasca" onsubmit="return confirmar(event)">
		<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
		<c:if test="${not empty definicioProcesId}"><input type="hidden" name="definicioProcesId" value="${definicioProcesId}"/></c:if>
		<c:if test="${expedientTipus.teNumero and expedientTipus.demanaNumero}">
			<div class="control-group fila_reducida">
				<label class="control-label" for="numero"><spring:message code='expedient.consulta.numero' /></label>
				<div class="controls">
					<input type="number" id="numero" name="numero" value="${numero}" class="span11" style="text-align:right" data-required="true"/>
					<script>
						$("#numero").keyfilter(/^[-+]?[0-9]*$/);
					</script>
				</div>
			</div>
		</c:if>
		<c:if test="${expedientTipus.teTitol and expedientTipus.demanaTitol}">
			<div class="control-group fila_reducida">
				<label class="control-label" for="titol"><spring:message code='expedient.consulta.titol' /></label>
				<div class="controls">
					<textarea id="titol" name="titol" class="span11" style="text-align:right" data-required="true"></textarea>
					<script>
						$("#titol").keyfilter(/^[-+]?[0-9]*$/);
					</script>	
				</div>
			</div>
		</c:if>
		<c:if test="${expedientTipus.seleccionarAny}">
			<div class="control-group fila_reducida">
				<label class="control-label" for="any"><spring:message code='expedient.iniciar.canvi_any' /></label>
				<div class="controls">
					<select id="any" name="any" class="span11" onchange="canviAny(this)"></select>
					<script>
						$.ajax({
						    url: 'camp/any/valorsSeleccio',
						    type: 'GET',
						    dataType: 'json',
						    success: function(json) {
						    	$.each(json, function(i, value) {
						        	$('select#any').append($('<option>').text(value.text).attr('value', value.codi));
						        });
						    }
						});
					</script>
				</div>
			</div>
			<input type="hidden" id="nomesRefrescar" name="nomesRefrescar"/>
		</c:if>
		<br/>
		<div style="clear: both"></div>
		<div class="pull-right">
			<button type="button" class="btn" name="submit" value="cancel" onclick="location='iniciar'">
				<spring:message code='comuns.cancelar' />
			</button>				
			<button type="submit" class="btn btn-primary" name="submit" value="submit">
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
