<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title><fmt:message key='common.filtres.expedient' />: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.consultes' />"/>
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

$(document).ready(function() {
	$("#ambOcults").click(function() {
		var checked = $('#ambOcults').prop('checked');
		var location = "<c:url value="/expedient/dades.html"><c:param name="id" value="${param.id}"/></c:url>&ambOcults=" + checked;
		window.location.href = location;
	});
});
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="dades"/>
	</c:import>

	<h3 class="titol-tab titol-dades-expedient">
		<fmt:message key='expedient.dada.dades_proces' />
	</h3>
	<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16">
		<div class="form-group" style="text-align: right; padding-bottom: 5px;">
			<label class="control-label col-xs-4" for="ambOcults">
				<fmt:message key="expedient.dada.ocults"/>
				<input type="checkbox" <c:if test="${ambOcults}">checked="checked"</c:if> class="span12" id="ambOcults"/>
			</label>
		</div>
	</security:accesscontrollist>
			
	<div id="dades-proces">
<%
Boolean ambOcults = (Boolean)request.getAttribute("ambOcults");
	InstanciaProcesDto instanciaProces = (InstanciaProcesDto)request.getAttribute("instanciaProces");
	request.setAttribute(
	"variablesProcesSenseAgrupar",
	getVariablesProcesSenseAgrupar(
			instanciaProces.getCamps(),
			instanciaProces.getVarsComText(),
			ambOcults));
%>
		<c:if test="${not empty variablesProcesSenseAgrupar}">
			<display:table name="variablesProcesSenseAgrupar" id="codi" class="displaytag">
				<c:set var="found" value="${false}"/>
				<c:forEach var="camp" items="${instanciaProces.camps}">
					<c:if test="${camp.codi == codi}"><c:set var="found" value="${true}"/><c:set var="campActual" value="${camp}"/></c:if>
				</c:forEach>
				<display:column title="Variable">
					<c:if test="${found && campActual.ocult}"><img src="/helium/img/eye-close.png" alt="Ocult" title="Ocult" border="0">&nbsp;&nbsp;</c:if><c:choose><c:when test="${found}">${campActual.etiqueta}</c:when><c:otherwise>${codi}</c:otherwise></c:choose>
				</display:column>
				<display:column title="Valor">
					<c:set var="esRegistre" value="${false}"/>
					<c:choose>
						<c:when test="${campActual.tipus == 'REGISTRE'}">
							<c:set var="registres" value="${instanciaProces.varsComText[codi]}" scope="request"/>
							<display:table name="registres" id="reg" class="displaytag">
								<c:forEach var="membre" items="${campActual.registreMembres}" varStatus="varStatus">
									<c:if test="${membre.llistar}">
										<display:column title="${membre.membre.etiqueta}">${reg[varStatus.index]}</display:column>
									</c:if>
								</c:forEach>
							</display:table>
						</c:when>
						<c:otherwise>${instanciaProces.varsComText[codi]}</c:otherwise>
					</c:choose>
					<c:if test="${param.classes}"> (class=<%=instanciaProces.getVariableClassAsString((String)(pageContext.getAttribute("codi")))%>)</c:if>
				</display:column>
				<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
					<display:column>
						<a href="<c:url value="/expedient/dadaModificar.html"><c:param name="id" value="${instanciaProces.id}"/><c:param name="var" value="${codi}"/></c:url>" onclick="return confirmarModificar(event)"><img src="<c:url value="/img/page_white_edit.png"/>" alt="Editar" title="Editar" border="0"/></a>
					</display:column>
					<display:column>
						<a href="<c:url value="/expedient/dadaProcesEsborrar.html"><c:param name="id" value="${instanciaProces.id}"/><c:param name="var" value="${codi}"/></c:url>" onclick="return confirmarEsborrarProces(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
					</display:column>
				</security:accesscontrollist>
			</display:table>
		</c:if>
		<c:forEach var="agrupacio" items="${instanciaProces.agrupacions}">
			<c:set var="agrupacioBuida" value="${true}"/>
			<c:forEach var="campAgrupacio" items="${agrupacio.camps}">
				<c:if test="${not empty instanciaProces.varsComText[campAgrupacio.codi] and not instanciaProces.varsOcultes[campAgrupacio.codi]}"><c:set var="agrupacioBuida" value="${false}"/></c:if>
			</c:forEach>
			<c:if test="${not agrupacioBuida}">
				<div class="missatgesGris">
					<h4 class="titol-missatge">
						${agrupacio.nom}
						<img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="Mostrar/Ocultar" title="Mostrar/Ocultar" border="0" onclick="mostrarOcultar(this,'dades-agrup-${agrupacio.codi}')"/>
					</h4>
					<div id="dades-agrup-${agrupacio.codi}" style="display:none">
<%
es.caib.helium.persist.entity.CampAgrupacio agrupacio = (es.caib.helium.persist.entity.CampAgrupacio)pageContext.getAttribute("agrupacio");
	request.setAttribute(
	"campsAgrupacio",
	getCampsAgrupacioNoBuits(
			agrupacio.getCamps(),
			instanciaProces.getVarsComText(),
			ambOcults));
%>
						<display:table name="campsAgrupacio" id="campAgrup" class="displaytag">
							<display:column title="Variable">
								<c:set var="found" value="${false}"/>
								<c:forEach var="camp" items="${instanciaProces.camps}">
									<c:if test="${camp.codi == campAgrup.codi}"><c:set var="found" value="${true}"/><c:set var="campActual" value="${camp}"/></c:if>
								</c:forEach>
								<c:if test="${found && campActual.ocult}"><img src="/helium/img/eye-close.png" alt="Ocult" title="Ocult" border="0">&nbsp;&nbsp;</c:if><c:choose><c:when test="${found}">${campActual.etiqueta}</c:when><c:otherwise>${campAgrup.codi}</c:otherwise></c:choose>
							</display:column>
							<display:column title="Valor">
								<c:forEach var="camp" items="${instanciaProces.camps}">
									<c:if test="${camp.codi == campAgrup.codi}"><c:set var="campActual" value="${camp}"/></c:if>
								</c:forEach>
								<c:choose>
									<c:when test="${campActual.tipus == 'REGISTRE'}">
										<c:set var="registres" value="${instanciaProces.varsComText[campAgrup.codi]}" scope="request"/>
										<display:table name="registres" id="reg" class="displaytag">
											<c:forEach var="membre" items="${campActual.registreMembres}" varStatus="varStatus">
												<c:if test="${membre.llistar}">
													<display:column title="${membre.membre.etiqueta}">${reg[varStatus.index]}</display:column>
												</c:if>
											</c:forEach>
										</display:table>
									</c:when>
									<c:otherwise>${instanciaProces.varsComText[campAgrup.codi]}</c:otherwise>
								</c:choose>
								<c:if test="${param.classes}"> <c:set var="campAgrupCodi" value="${campAgrup.codi}"/>(class=<%=instanciaProces.getVariableClassAsString((String)(pageContext.getAttribute("campAgrupCodi")))%>)</c:if>
							</display:column>
							<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
								<display:column>
									<a href="<c:url value="/expedient/dadaModificar.html"><c:param name="id" value="${instanciaProces.id}"/><c:param name="var" value="${campAgrup.codi}"/></c:url>" onclick="return confirmarModificar(event)"><img src="<c:url value="/img/page_white_edit.png"/>" alt="Editar" title="Editar" border="0"/></a>
								</display:column>
								<display:column>
									<a href="<c:url value="/expedient/dadaProcesEsborrar.html"><c:param name="id" value="${instanciaProces.id}"/><c:param name="var" value="${campAgrup.codi}"/></c:url>" onclick="return confirmarEsborrarProces(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
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
				<span class="nova-variable"></span><fmt:message key='expedient.dada.afegir_dada_proces' />
			</button>
		</form>
	</security:accesscontrollist>

	<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="2,16,128">
	<br/>
	<c:set var="mostrarTasques" value="${false}"/>
	<c:forEach var="tasca" items="${tasques}">
		<c:if test="${tasca.completed}"><c:set var="mostrarTasques" value="${true}"/></c:if>
	</c:forEach>
	<c:if test="${mostrarTasques}">
		<h3 class="titol-tab titol-dades-tasques amb-lupa">
			<fmt:message key='expedient.dada.dades_tasques' />
			<img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="Mostrar/Ocultar" title="Mostrar/Ocultar" border="0" onclick="mostrarOcultar(this,'dades-tasques')"/>
		</h3>
		<div id="dades-tasques" style="display:none">
			<c:forEach var="tasca" items="${tasques}">
				<c:if test="${tasca.completed}">
					<div class="missatgesGris">
						<h4 class="titol-missatge">
							${tasca.nom}
							<img src="<c:url value="/img/magnifier_zoom_out.png"/>" alt="Mostrar/Ocultar" title="Mostrar/Ocultar" border="0" onclick="mostrarOcultar(this,'dades-tasca-${tasca.id}')"/>
						</h4>
						<div id="dades-tasca-${tasca.id}">
							<c:if test="${not empty pageScope.tasca.variableKeys}">
								<display:table name="pageScope.tasca.variableKeys" id="codi" class="displaytag">
									<display:column title="Variable">
										<c:set var="found" value="${false}"/>
										<c:forEach var="camp" items="${tasca.camps}">
											<c:if test="${camp.camp.codi == codi}"><c:set var="found" value="${true}"/><c:set var="campActual" value="${camp.camp}"/></c:if>
										</c:forEach>
										<c:choose><c:when test="${found}">${campActual.etiqueta}</c:when><c:otherwise>${codi}</c:otherwise></c:choose>
									</display:column>
									<display:column title="Valor">
										<c:set var="esRegistre" value="${false}"/>
										<c:forEach var="camp" items="${tasca.camps}">
											<c:if test="${camp.camp.codi == codi and camp.camp.tipus == 'REGISTRE'}"><c:set var="esRegistre" value="${true}"/><c:set var="campActual" value="${camp.camp}"/></c:if>
										</c:forEach>
										<c:choose>
											<c:when test="${esRegistre}">
												<c:set var="registres" value="${tasca.varsComText[codi]}" scope="request"/>
												<display:table name="registres" id="reg" class="displaytag">
													<c:forEach var="membre" items="${campActual.registreMembres}" varStatus="varStatus">
														<c:if test="${membre.llistar}">
															<display:column title="${membre.membre.etiqueta}">${reg[varStatus.index]}</display:column>
														</c:if>
													</c:forEach>
												</display:table>
											</c:when>
											<c:otherwise>${tasca.varsComText[codi]}</c:otherwise>
										</c:choose>
									</display:column>
									<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
										<display:column>
											<a href="<c:url value="/expedient/dadaModificar.html"><c:param name="taskId" value="${tasca.id}"/><c:param name="var" value="${codi}"/></c:url>" onclick="return confirmarModificar(event)"><img src="<c:url value="/img/page_white_edit.png"/>" alt="Editar" title="Editar" border="0"/></a>
										</display:column>
										<display:column>
											<a href="<c:url value="/expedient/dadaTascaEsborrar.html"><c:param name="taskId" value="${tasca.id}"/><c:param name="var" value="${codi}"/></c:url>" onclick="return confirmarEsborrarTasca(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
										</display:column>
									</security:accesscontrollist>
								</display:table>
							</c:if>
						</div>
						<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
							<form action="<c:url value="/expedient/dadaCrear.html"/>">
								<input type="hidden" name="taskId" value="${tasca.id}"/>
								<button class="submitButtonImage" type="submit">
									<span class="nova-variable"></span><fmt:message key='expedient.dada.afegir_dada_tasca' />
								</button>
							</form>
						</security:accesscontrollist>
					</div>
				</c:if>
			</c:forEach>
		</div>
	</c:if>
	</security:accesscontrollist>

</body>
</html>

<%!public java.util.List<String> getVariablesProcesSenseAgrupar(
		java.util.Set<es.caib.helium.persist.entity.Camp> camps,
		java.util.Map<String, Object> varsComText,
		Boolean ambOcults) {
	java.util.List<String> resposta = new java.util.ArrayList<String>();
	for (String codi: varsComText.keySet()) {
		boolean trobat = false;
		for (es.caib.helium.persist.entity.Camp camp: camps) {
			if (camp.getCodi().equals(codi)) {
				if (camp.getAgrupacio() == null && (ambOcults || !camp.isOcult()))
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
public java.util.List<es.caib.helium.persist.entity.Camp> getCampsAgrupacioNoBuits(
		java.util.List<es.caib.helium.persist.entity.Camp> campsAgrupacio,
		java.util.Map<String, Object> varsComText,
		Boolean ambOcults) {
	java.util.List<es.caib.helium.persist.entity.Camp> resposta = new java.util.ArrayList<es.caib.helium.persist.entity.Camp>();
	for (es.caib.helium.persist.entity.Camp camp: campsAgrupacio) {
		if (varsComText.containsKey(camp.getCodi()) && (ambOcults || !camp.isOcult()))
			resposta.add(camp);
	}
	return resposta;
}%>
