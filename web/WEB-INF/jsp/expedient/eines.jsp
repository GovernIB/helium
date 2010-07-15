<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Expedient: ${expedient.identificador}</title>
	<meta name="titolcmp" content="Consultes"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.DOMWindow.js"/>"></script>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function confirmarAturar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu aturar la tramitació d'aquest expedient?");
}
function confirmarReprendre(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu reprendre la tramitació d'aquest expedient?");
}
function confirmarScript(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu executar aquest script en aquest procés?");
}

function confirmarCanviVersio(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu canviar la versió d'aquest procés?");
}

// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="eines"/>
	</c:import>

	<div>
		<c:choose>
			<c:when test="${not expedient.aturat}">
				<h3 class="titol-tab titol-aturar">
					<a href="#aturarForm" class="aturarLink">Aturar la tramitació de l'expedient</a>
				</h3>
				<script type="text/javascript">
					$('.aturarLink').openDOMWindow({
						eventType: 'click',
						width: 600,
						height: 260,
						loader: 1,
						loaderHeight: 32,
						loaderWidth: 32,
						windowPadding: 0,
						draggable: 1});
				</script>
				<div id="aturarForm" style="display:none">
					<h3 class="titol-tab titol-aturar">Aturar la tramitació de l'expedient</h3>
					<form:form action="aturar.html" cssClass="uniForm" commandName="aturarCommand" onsubmit="return confirmarAturar(event)">
						<div class="inlineLabels">
							<input type="hidden" name="id" value="${param.id}"/>
							<c:import url="../common/formElement.jsp">
								<c:param name="property">motiu</c:param>
								<c:param name="required">${true}</c:param>
								<c:param name="type" value="textarea"/>
								<c:param name="label">Motiu</c:param>
							</c:import>
						</div>
						<c:import url="../common/formElement.jsp">
							<c:param name="type" value="buttons"/>
							<c:param name="values">submit</c:param>
							<c:param name="titles">Aturar</c:param>
						</c:import>
					</form:form>
				</div>
			</c:when>
			<c:otherwise>
				<h3 class="titol-tab titol-reprendre">
					<a href="#reprendreForm" class="reprendreLink">Reprendre la tramitació de l'expedient</a>
				</h3>
				<script type="text/javascript">
					$('.reprendreLink').openDOMWindow({
						eventType: 'click',
						width: 600,
						height: 80,
						loader: 1,
						loaderHeight: 32,
						loaderWidth: 32,
						windowPadding: 0,
						draggable: 1});
				</script>
				<div id="reprendreForm" style="display:none">
					<h3 class="titol-tab titol-reprendre">Reprendre la tramitació de l'expedient</h3>
					<form:form action="reprendre.html" cssClass="uniForm" commandName="aturarCommand" onsubmit="return confirmarReprendre(event)">
						<input type="hidden" name="id" value="${param.id}"/>
						<c:import url="../common/formElement.jsp">
							<c:param name="type" value="buttons"/>
							<c:param name="values">submit</c:param>
							<c:param name="titles">Reprendre</c:param>
						</c:import>
					</form:form>
				</div>
			</c:otherwise>
		</c:choose>
		
		<h3 class="titol-tab titol-script">
			<a href="#scriptForm" class="scriptLink">Execució d'scripts</a>
		</h3>
		<script type="text/javascript">
			$('.scriptLink').openDOMWindow({
				eventType: 'click',
				width: 600,
				height: 260,
				loader: 1,
				loaderHeight: 32,
				loaderWidth: 32,
				windowPadding: 0,
				draggable: 1});
		</script>
		<div id="scriptForm" style="display:none">
			<h3 class="titol-tab titol-script">Execució d'scripts</h3>
			<form:form action="script.html" cssClass="uniForm" commandName="scriptCommand" onsubmit="return confirmarScript(event)">
				<div class="inlineLabels">
					<input type="hidden" name="id" value="${param.id}"/>
					<c:import url="../common/formElement.jsp">
						<c:param name="property">script</c:param>
						<c:param name="required">${true}</c:param>
						<c:param name="type" value="textarea"/>
						<c:param name="label">Script</c:param>
					</c:import>
				</div>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit</c:param>
					<c:param name="titles">Executar</c:param>
				</c:import>
			</form:form>
		</div>
	
		<h3 class="titol-tab titol-canvi-versio">
			<a href="#canviVersioForm" class="canviVersioLink">Canviar la versió del procés</a>
		</h3>
		<script type="text/javascript">
			$('.canviVersioLink').openDOMWindow({
				eventType: 'click',
				width: 600,
				height: 80,
				loader: 1,
				loaderHeight: 32,
				loaderWidth: 32,
				windowPadding: 0,
				draggable: 1});
		</script>
		<div id="canviVersioForm" style="display:none">
			<form:form action="canviVersio.html" cssClass="uniForm" commandName="canviVersioProcesCommand" onsubmit="return confirmarCanviVersio(event)">
				<div class="inlineLabels">
					<input type="hidden" name="id" value="${param.id}"/>
					<c:set var="definicionsProces" value="${definicioProces.jbpmIdsAmbDescripcio}" scope="request"/>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="definicioProcesId"/>
						<c:param name="type" value="select"/>
						<c:param name="items" value="definicionsProces"/>
						<c:param name="itemLabel" value="descripcio"/>
						<c:param name="itemValue" value="jbpmId"/>
						<c:param name="itemBuit" value="<< Darrera versió >>"/>
						<c:param name="label">Canviar a la versió</c:param>
					</c:import>
				</div>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit</c:param>
					<c:param name="titles">Canviar versió</c:param>
				</c:import>
			</form:form>
		</div>
	</div>

</body>
</html>
