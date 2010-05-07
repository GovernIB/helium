<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Expedient: ${expedient.identificador}</title>
	<meta name="titolcmp" content="Consultes">
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function mostrarOcultar(img, objid) {
	var obj = document.getElementById(objid);
	if (obj.style.display=="none") {
		obj.style.display = "block";
		img.src = '<c:url value="/img/magnifier_zoom_out.png"/>';
	} else {
		obj.style.display = "none";
		img.src = '<c:url value="/img/magnifier_zoom_in.png"/>';
	}
}
function confirmarEsborrarProces(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquesta dada del procés?");
}
function confirmarEsborrarTasca(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquesta dada de la tasca?");
}
function confirmarModificar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Es podran modificar dades ja introduïdes. Voleu continuar?");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="dades"/>
	</c:import>

	<h3 class="titol-tab titol-dades-expedient">
		Dades del procés
	</h3>
	<div id="dades-proces">
<%
	net.conselldemallorca.helium.model.dto.InstanciaProcesDto instanciaProces = (net.conselldemallorca.helium.model.dto.InstanciaProcesDto)request.getAttribute("instanciaProces");
	request.setAttribute(
			"variablesProcesSenseAgrupar",
			getVariablesProcesSenseAgrupar(
					instanciaProces.getVarsComText(),
					instanciaProces.getCamps()));
%>
		<c:if test="${not empty variablesProcesSenseAgrupar}">
			<display:table name="variablesProcesSenseAgrupar" id="codi" class="displaytag">
				<display:column title="Variable">
					<c:set var="found" value="${false}"/>
					<c:forEach var="camp" items="${instanciaProces.camps}">
						<c:if test="${camp.codi == codi}"><c:set var="found" value="${true}"/><c:set var="campActual" value="${camp}"/></c:if>
					</c:forEach>
					<c:choose><c:when test="${found}">${campActual.etiqueta}</c:when><c:otherwise>${codi}</c:otherwise></c:choose>
				</display:column>
				<display:column title="Valor">${instanciaProces.varsComText[codi]}</display:column>
				<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
					<display:column>
						<a href="<c:url value="/expedient/dadaModificar.html"><c:param name="id" value="${instanciaProces.id}"/><c:param name="var" value="${codi}"/></c:url>" onclick="return confirmarModificar(event)"><img src="<c:url value="/img/page_white_edit.png"/>" alt="Editar" title="Editar" border="0"/></a>
					</display:column>
					<display:column>
						<a href="<c:url value="/expedient/dadaProcesEsborrar.html"><c:param name="id" value="${instanciaProces.id}"/><c:param name="var" value="${codi}"/></c:url>" onclick="return confirmarEsborrarProces(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
					</display:column>
				</security:accesscontrollist>
			</display:table>
		</c:if>
		<c:forEach var="agrupacio" items="${instanciaProces.agrupacions}">
			<c:set var="agrupacioBuida" value="${true}"/>
			<c:forEach var="campAgrupacio" items="${agrupacio.camps}">
				<c:if test="${not empty instanciaProces.varsComText[campAgrupacio.codi]}"><c:set var="agrupacioBuida" value="${false}"/></c:if>
			</c:forEach>
			<c:if test="${not agrupacioBuida}">
				<div class="missatgesGris">
					<h4 class="titol-missatge">
						${agrupacio.nom}
						<img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="Mostrar/Ocultar" title="Mostrar/Ocultar" border="0" onclick="mostrarOcultar(this,'dades-agrup-${agrupacio.codi}')"/>
					</h4>
					<div id="dades-agrup-${agrupacio.codi}" style="display:none">
						<c:set var="campsAgrupacio" value="${agrupacio.camps}" scope="request"/>
						<display:table name="campsAgrupacio" id="campAgrup" class="displaytag">
							<display:column title="Variable">
								<c:set var="found" value="${false}"/>
								<c:forEach var="camp" items="${instanciaProces.camps}">
									<c:if test="${camp.codi == campAgrup.codi}"><c:set var="found" value="${true}"/><c:set var="campActual" value="${camp}"/></c:if>
								</c:forEach>
								<c:choose><c:when test="${found}">${campActual.etiqueta}</c:when><c:otherwise>${campAgrup.codi}</c:otherwise></c:choose>
							</display:column>
							<display:column title="Valor">${instanciaProces.varsComText[campAgrup.codi]}</display:column>
							<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
								<display:column>
									<a href="<c:url value="/expedient/dadaModificar.html"><c:param name="id" value="${instanciaProces.id}"/><c:param name="var" value="${campAgrup.codi}"/></c:url>" onclick="return confirmarModificar(event)"><img src="<c:url value="/img/page_white_edit.png"/>" alt="Editar" title="Editar" border="0"/></a>
								</display:column>
								<display:column>
									<a href="<c:url value="/expedient/dadaProcesEsborrar.html"><c:param name="id" value="${instanciaProces.id}"/><c:param name="var" value="${campAgrup.codi}"/></c:url>" onclick="return confirmarEsborrarProces(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
								</display:column>
							</security:accesscontrollist>
						</display:table>
					</div>
				</div>
			</c:if>
		</c:forEach>
	</div>
	<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
		<form action="<c:url value="/expedient/dadaCrear.html"/>">
			<input type="hidden" name="id" value="${instanciaProces.id}"/>
			<button class="submitButtonImage" type="submit">
				<span class="nova-variable"></span>Afegir nova dada al procés
			</button>
		</form>
	</security:accesscontrollist>
	<br/>

	<c:set var="mostrarTasques" value="${false}"/>
	<c:forEach var="tasca" items="${tasques}">
		<c:if test="${tasca.completed}"><c:set var="mostrarTasques" value="${true}"/></c:if>
	</c:forEach>
	<c:if test="${mostrarTasques}">
		<h3 class="titol-tab titol-dades-tasques amb-lupa">
			Dades de les tasques
			<img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="Mostrar/Ocultar" title="Mostrar/Ocultar" border="0" onclick="mostrarOcultar(this,'dades-tasques')"/>
		</h3>
		<div id="dades-tasques" style="display:none">
			<c:forEach var="tasca" items="${tasques}">
				<c:if test="${tasca.completed}">
					<div class="missatgesGris">
						<h4 class="titol-missatge">
							${tasca.nom}
							<img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="Mostrar/Ocultar" title="Mostrar/Ocultar" border="0" onclick="mostrarOcultar(this,'dades-tasca-${tasca.id}')"/>
						</h4>
						<div id="dades-tasca-${tasca.id}" style="display:none">
							<display:table name="pageScope.tasca.variableKeys" id="codi" class="displaytag">
								<display:column title="Variable">
									<c:set var="found" value="${false}"/>
									<c:forEach var="camp" items="${tasca.camps}">
										<c:if test="${camp.camp.codi == codi}"><c:set var="found" value="${true}"/><c:set var="campActual" value="${camp.camp}"/></c:if>
									</c:forEach>
									<c:choose><c:when test="${found}">${campActual.etiqueta}</c:when><c:otherwise>${codi}</c:otherwise></c:choose>
								</display:column>
								<display:column title="Valor">${tasca.varsComText[codi]}</display:column>
								<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
									<display:column>
										<a href="<c:url value="/expedient/dadaModificar.html"><c:param name="taskId" value="${tasca.id}"/><c:param name="var" value="${codi}"/></c:url>" onclick="return confirmarModificar(event)"><img src="<c:url value="/img/page_white_edit.png"/>" alt="Editar" title="Editar" border="0"/></a>
									</display:column>
									<display:column>
										<a href="<c:url value="/expedient/dadaTascaEsborrar.html"><c:param name="taskId" value="${tasca.id}"/><c:param name="var" value="${codi}"/></c:url>" onclick="return confirmarEsborrarTasca(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
									</display:column>
								</security:accesscontrollist>
							</display:table>
						</div>
						<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
							<form action="<c:url value="/expedient/dadaCrear.html"/>">
								<input type="hidden" name="taskId" value="${tasca.id}"/>
								<button class="submitButtonImage" type="submit">
									<span class="nova-variable"></span>Afegir nova dada a la tasca
								</button>
							</form>
						</security:accesscontrollist>
					</div>
				</c:if>
			</c:forEach>
		</div>
	</c:if>

</body>
</html>

<%!
public java.util.List<String> getVariablesProcesSenseAgrupar(
		java.util.Map<String, Object> varsComText,
		java.util.Set<net.conselldemallorca.helium.model.hibernate.Camp> camps) {
	java.util.List<String> resposta = new java.util.ArrayList<String>();
	for (String codi: varsComText.keySet()) {
		boolean trobat = false;
		for (net.conselldemallorca.helium.model.hibernate.Camp camp: camps) {
			if (camp.getCodi().equals(codi)) {
				if (camp.getAgrupacio() == null)
					resposta.add(codi);
				trobat = true;
				break;
			}
		}
		if (!trobat)
			resposta.add(codi);
	}
	return resposta;
}
%>
