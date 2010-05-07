<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Consultes de disseny</title>
	<meta name="titolcmp" content="Consultes">
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquest expedient?");
}
// ]]>
</script>
</head>
<body>

	<div class="missatgesGris">
		<c:choose>
			<c:when test="${empty consulta or param.canviar=='true'}">
				<h4 class="titol-consulta">Seleccioni una consulta</h4>
				<form:form action="consultaDisseny.html" commandName="commandSeleccioConsulta" cssClass="uniForm">
					<div class="inlineLabels col first">
						<input type="hidden" name="canviForm" id="canviForm" value="true"/>
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="expedientTipusId"/>
							<c:param name="type" value="select"/>
							<c:param name="items" value="expedientTipus"/>
							<c:param name="itemLabel" value="nom"/>
							<c:param name="itemValue" value="id"/>
							<c:param name="itemBuit" value="<< Seleccioni un tipus d'expedient >>"/>
							<c:param name="label">Tipus d'expedient</c:param>
							<c:param name="onchange">this.form.submit()</c:param>
						</c:import>
						<c:if test="${not empty commandSeleccioConsulta.expedientTipusId}">
							<c:import url="../common/formElement.jsp">
								<c:param name="property" value="consultaId"/>
								<c:param name="type" value="select"/>
								<c:param name="items" value="consultes"/>
								<c:param name="itemLabel" value="nom"/>
								<c:param name="itemValue" value="id"/>
								<c:param name="itemBuit" value="<< Seleccioni una consulta >>"/>
								<c:param name="label">Consulta</c:param>
								<c:param name="onchange">this.form.submit()</c:param>
							</c:import>
						</c:if>
					</div>
				</form:form>
			</c:when>
			<c:otherwise>
				<h4 class="titol-consulta" style="display:inline">${consulta.nom}</h4>&nbsp;&nbsp;&nbsp;
				<form action="consultaDisseny.html" method="post" style="display:inline"><input type="hidden" name="canviar" id="canviar" value="true"/><button type="submit" class="submitButton">Canviar</button></form>
			</c:otherwise>
		</c:choose>
	</div>

	<c:if test="${not empty camps}">
		<form:form action="consultaDissenyResultat.html" commandName="commandFiltre" cssClass="uniForm">
			<div class="inlineLabels col first">
				<c:forEach var="camp" items="${camps}">
					<c:set var="campActual" value="${camp}" scope="request"/>
					<c:set var="readonly" value="${false}" scope="request"/>
					<c:set var="required" value="${false}" scope="request"/>
					<c:import url="../common/campFiltre.jsp"/>
				</c:forEach>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,netejar</c:param>
					<c:param name="titles">Consultar,Netejar</c:param>
				</c:import>
			</div>
		</form:form>
		<c:if test="${not empty sessionScope.expedientTipusConsultaFiltreCommand}">
			<display:table name="expedients" id="registre" requestURI="" class="displaytag selectable" defaultsort="2" defaultorder="descending">
				<display:column property="identificador" title="Expedient" sortable="true" url="/expedient/info.html" paramId="id" paramProperty="processInstanceId"/>
				<display:column property="dataInici" title="Iniciat el" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true"/>
				<display:column property="tipus.nom" title="Tipus"/>
				<display:column title="Estat">
					<c:if test="${registre.aturat}"><img src="<c:url value="/img/stop.png"/>" alt="Aturat" title="Aturat" border="0"/></c:if>
					<c:choose>
						<c:when test="${empty registre.dataFi}">
							<c:choose><c:when test="${empty registre.estat}">Iniciat</c:when><c:otherwise>${registre.estat.nom}</c:otherwise></c:choose>
						</c:when>
						<c:otherwise>Finalitzat</c:otherwise>
					</c:choose>
				</display:column>
				<display:column>
					<security:accesscontrollist domainObject="${registre.tipus}" hasPermission="16,8">
						<a href="<c:url value="/expedient/delete.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
					</security:accesscontrollist>
				</display:column>
			</display:table>
			<script type="text/javascript">initSelectable();</script>
		</c:if>
	</c:if>

</body>
</html>
