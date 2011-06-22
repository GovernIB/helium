<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Consulta general</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.consultes' />" />
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

	<form:form action="consulta.html" cssClass="uniForm">
		<div class="inlineLabels col first">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="titol"/>
				<c:param name="label"><fmt:message key='expedient.consulta.titol' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="numero"/>
				<c:param name="label"><fmt:message key='expedient.consulta.numero' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="dataInici1"/>
				<c:param name="type" value="custom"/>
				<c:param name="label"><fmt:message key='expedient.consulta.datainici' /></c:param>
				<c:param name="customClass">customField</c:param>
				<c:param name="content">
					<spring:bind path="dataInici1">
						<label for="dataInici1" class="blockLabel">
							<span><fmt:message key='expedient.consulta.entre' /></span>
							<input id="dataInici1" name="dataInici1" value="${status.value}" type="text" class="textInput"/>
							<script type="text/javascript">
								// <![CDATA[
								$(function() {
									$.datepicker.setDefaults($.extend({
										dateFormat: 'dd/mm/yy',
										changeMonth: true,
										changeYear: true
									}));
									$("#dataInici1").datepicker();
								});
								// ]]>
							</script>
						</label>
					</spring:bind>
					<spring:bind path="dataInici2">
						<label for="dataInici2" class="blockLabel blockLabelLast">
							<span><fmt:message key='expedient.consulta.i' /></span>
							<input id="dataInici2" name="dataInici2" value="${status.value}" type="text" class="textInput"/>
							<script type="text/javascript">
								// <![CDATA[
								$(function() {
									$.datepicker.setDefaults($.extend({
										dateFormat: 'dd/mm/yy',
										changeMonth: true,
										changeYear: true
									}));
									$("#dataInici2").datepicker();
								});
								// ]]>
							</script>
						</label>
					</spring:bind>
				</c:param>
			</c:import>
		</div>
		<div class="inlineLabels col last">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="expedientTipus"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="expedientTipus"/>
				<c:param name="itemLabel" value="nom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit"><< <fmt:message key='expedient.consulta.select.tipusexpedient'/> >></c:param>
				<c:param name="label"><fmt:message key='expedient.consulta.tipusexpedient' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="estat"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="estats"/>
				<c:param name="itemLabel" value="tipusAmbNom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit"><< <fmt:message key='expedient.consulta.select.estat'/> >></c:param>
				<c:param name="label"><fmt:message key='expedient.consulta.estat' /></c:param>
			</c:import>
			<c:if test="${globalProperties['app.georef.actiu']}">
				<c:choose>
					<c:when test="${globalProperties['app.georef.tipus']=='ref'}">
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="geoReferencia"/>
							<c:param name="label"><fmt:message key='comuns.georeferencia.codi' /></c:param>
						</c:import>
					</c:when>
					<c:otherwise>
					
					
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="geoPosX"/>
							<c:param name="type" value="custom"/>
							<c:param name="label"><fmt:message key='comuns.georeferencia.coordenades' /></c:param>
							<c:param name="customClass">customField</c:param>
							<c:param name="content">
								<spring:bind path="geoPosX">
									<label for="geoPosX" class="blockLabel">
										<span><fmt:message key='comuns.georeferencia.coordX' /></span>
										<input id="geoPosX" name="geoPosX" value="${status.value}" type="text" class="textInput"/>
									</label>
								</spring:bind>
								<spring:bind path="geoPosY">
									<label for="geoPosY" class="blockLabel blockLabelLast">
										<span><fmt:message key='comuns.georeferencia.coordY' /></span>
										<input id="geoPosY" name="geoPosY" value="${status.value}" type="text" class="textInput"/>
									</label>
								</spring:bind>
							</c:param>
						</c:import>
					</c:otherwise>
				</c:choose>
			</c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">submit,clean</c:param>
				<c:param name="titles"><fmt:message key='expedient.consulta.consultar' />,<fmt:message key='expedient.consulta.netejar' /></c:param>
			</c:import>
		</div>
	</form:form><br/>

	<c:if test="${not empty sessionScope.consultaExpedientsCommand}">
		
		<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable" defaultsort="2" defaultorder="descending">
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
					<a href="<c:url value="/expedient/delete.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
				</security:accesscontrollist>
			</display:column>
		</display:table>
		<script type="text/javascript">initSelectable();</script>
	</c:if>

</body>
</html>
	