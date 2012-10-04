<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display"%>

<html>
	<head>
		<title>Tipus d'expedient: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
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
	return confirm("<fmt:message key='defproc.info.confirmacio' />");
}
// ]]>
</script>
	</head>
	
	<body>
		
		<c:import url="../common/tabsExpedientTipus.jsp">
			<c:param name="tabActiu" value="redireccio"/>
		</c:import>
				<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
						<display:column property="usuariOrigen" titleKey="reassign.llistat.usu_origen" sortable="true" url="/expedientTipus/modificar.html" paramId="id" paramProperty="id" />
						<display:column property="usuariDesti" titleKey="reassign.llistat.usu_dest" sortable="true"/>
						<display:column titleKey="reassign.llistat.data_ini" sortable="true">
							<fmt:formatDate value="${registre.dataInici}" pattern="dd/MM/yyyy"/>
						</display:column>
						<display:column titleKey="reassign.llistat.data_fi" sortable="true">
							<fmt:formatDate value="${registre.dataFi}" pattern="dd/MM/yyyy"/>
						</display:column>
						<display:column titleKey="comuns.cancelar" sortable="false">
							<a href="<c:url value="/expedientTipus/cancelar.html"><c:param name="id" value="${registre.id}"/><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.cancelar' />" title="<fmt:message key='comuns.cancelar' />" border="0"/></a>
						</display:column>
						<display:setProperty name="paging.banner.item_name"><fmt:message key='reassign.llistat.reassignacio' /></display:setProperty>
						<display:setProperty name="paging.banner.items_name"><fmt:message key='reassign.llistat.reassignacio' /></display:setProperty>
				</display:table>
			<form>
				
			</form>
 		<script type="text/javascript">
 			initSelectable();
		</script>
		<br>
		<h2><fmt:message key='reassign.llistat.titol'/></h2>				
		<form:form action="redireccioLlistat.html" cssClass="uniForm">
			
			<div class="inlineLabels">
				<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
				<input type="hidden" id="expedientTipusId" name="expedientTipusId" value="${expedientTipus.id}">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="usuariOrigen"/>
					<c:param name="required" value="true"/>
					<c:param name="type" value="suggest"/>
					<c:param name="label"><fmt:message key='reassign.form.usu_origen' /></c:param>
					<c:param name="suggestUrl"><c:url value="/persona/suggest.html"/></c:param>
					<c:param name="suggestText">${command.usuariOrigen}</c:param>
				</c:import>
				
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="usuariDesti"/>
					<c:param name="required" value="true"/>
					<c:param name="type" value="suggest"/>
					<c:param name="label"><fmt:message key='reassign.form.usu_dest' /></c:param>
					<c:param name="suggestUrl"><c:url value="/persona/suggest.html"/></c:param>
					<c:param name="suggestText">${command.usuariDesti}</c:param>
				</c:import>
				
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="dataInici"/>
					<c:param name="required" value="true"/>
					<c:param name="type" value="date"/>
					<c:param name="label"><fmt:message key='reassign.form.data_ini' /></c:param>
				</c:import>
				
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="dataFi"/>
					<c:param name="required" value="true"/>
					<c:param name="type" value="date"/>
					<c:param name="label"><fmt:message key='reassign.form.data_fi' /></c:param>
				</c:import>
				
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,cancel</c:param>
					<c:param name="titles"><c:choose><c:when test="${empty command.id}"><fmt:message key='comuns.crear' />,<fmt:message key='comuns.cancelar' /></c:when><c:otherwise><fmt:message key='comuns.modificar' />,<fmt:message key='comuns.cancelar' /></c:otherwise></c:choose></c:param>
				</c:import>
			</div>
		</form:form>

		<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>
	</body>
</html>