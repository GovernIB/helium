<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Tipus d'expedient: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="<fmt:message key="comuns.disseny"/>" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
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
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key="enumeracio.valors.confirmacio"/>");
}
// ]]>
</script>
</head>

<body>
	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="enum"/>
	</c:import>

	<display:table name="llistat" id="registre" requestURI="" class="displaytag">
		<display:column property="codi" titleKey="comuns.codi" sortable="true" url="/expedientTipus/enumeracioValorsForm.html?expedientTipusId=${expedientTipus.id}&enumeracioId=${registre.enumeracio.id}" paramId="id" paramProperty="id"/>
		<display:column property="nom" titleKey="comuns.titol" sortable="true"/>
		<display:column property="ordre" titleKey="comuns.ordre"/>
		<display:column>
			<a href="<c:url value="/expedientTipus/enumeracioValorsPujar.html"><c:param name="id" value="${registre.id}"/><c:param name="enumeracioId" value="${registre.enumeracio.id}"/></c:url>"><img src="<c:url value="/img/famarrow_up.png"/>" alt="<fmt:message key="comuns.amunt"/>" title="<fmt:message key="comuns.amunt"/>" border="0"/></a>
			<a href="<c:url value="/expedientTipus/enumeracioValorsBaixar.html"><c:param name="id" value="${registre.id}"/><c:param name="enumeracioId" value="${registre.enumeracio.id}"/></c:url>"><img src="<c:url value="/img/famarrow_down.png"/>" alt="<fmt:message key="comuns.avall"/>" title="<fmt:message key="comuns.avall"/>" border="0"/></a>
		</display:column>
		<display:column>
			<a href="<c:url value="/expedientTipus/enumeracioValorsEsborrar.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key="comuns.esborrar"/>" title="<fmt:message key="comuns.esborrar"/>" border="0"/></a>			
			</display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>
	
	<form:form action="enumeracioValors.html" cssClass="uniForm" method="post">
		<div class="inlineLabels">
			<form:hidden path="enumeracioId"/>
			<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
			<c:if test="${not empty command.id}">
				<form:hidden path="id"/>
			</c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key="comuns.codi"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key="comuns.titol"/></c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><fmt:message key="comuns.afegir"/>,<fmt:message key="comuns.cancelar"/></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key="comuns.camps_marcats"/> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key="comuns.camp_oblig"/>" title="<fmt:message key="comuns.camp_oblig"/>" border="0"/> <fmt:message key="comuns.son_oblig"/></p>

	<security:accesscontrollist domainObject="${expedientTipus}" hasPermission="16,32">
		<br/>
		<div class="missatgesGris">
			<h3 class="titol-tab titol-delegacio"><fmt:message key="enumeracio.valors.import_dades"/> <img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="<fmt:message key="enumeracio.valors.mostrar_ocultar"/>" title="<fmt:message key="enumeracio.valors.mostrar_ocultar"/>" border="0" onclick="mostrarOcultar(this,'form-importar')"/></h3>
			<div id="form-importar" style="display:none">
				<form:form action="enumeracioImportar.html" cssClass="uniForm" enctype="multipart/form-data" commandName="commandImportacio">
					<input type="hidden" name="id" value="${command.enumeracioId}"/>
					<div class="inlineLabels">
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="arxiu"/>
							<c:param name="type" value="file"/>
							<c:param name="label"><fmt:message key="enumeracio.valors.arxiu_exp"/></c:param>
						</c:import>
						<c:import url="../common/formElement.jsp">
							<c:param name="type" value="buttons"/>
							<c:param name="values">submit</c:param>
							<c:param name="titles"><fmt:message key="enumeracio.valors.importar"/></c:param>
						</c:import>
					</div>
				</form:form>
			</div>
		</div>
	</security:accesscontrollist>

</body>
</html>
