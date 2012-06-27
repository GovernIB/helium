<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Tipus d'expedient: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="<fmt:message key="comuns.disseny"/>" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="documents"/>
	</c:import>

	<div class="missatgesGris">
		<h4 class="titol-consulta"><fmt:message key="exptipus.docs.defproc"/></h4>
		<form style="display:inline">
			<input type="hidden" name="expedientTipusId" value="${param.expedientTipusId}"/>
			<select name="definicioProcesId" onchange="this.form.submit()">
				<option value="">&lt;&lt; <fmt:message key="exptipus.docs.select.seleccioni"/> &gt;&gt;</option>
				<c:forEach var="definicioProces" items="${definicionsProces}">
					<option value="${definicioProces.id}"<c:if test="${definicioProces.id == param.definicioProcesId}"> selected="selected"</c:if>>${definicioProces.jbpmName}</option>
				</c:forEach>
			</select>
		</form>
	</div>

	<c:if test="${not empty param.definicioProcesId}">
		<display:table name="llistat" id="registre" requestURI="" defaultsort="1" class="displaytag selectable">
			<display:column property="codi" titleKey="comuns.codi" sortable="true" url="/expedientTipus/documentForm.html?expedientTipusId=${param.expedientTipusId}&definicioProcesId=${param.definicioProcesId}" paramId="id" paramProperty="id"/>
			<display:column property="nom" titleKey="comuns.nom" sortable="true"/>
			<display:column titleKey="defproc.docllistat.es_plantillaq">
				<c:choose><c:when test="${registre.plantilla}"><fmt:message key='comuns.si' /></c:when><c:otherwise><fmt:message key='comuns.no' /></c:otherwise></c:choose>
			</display:column>
			<display:column titleKey="comuns.arxiu">
				<c:if test="${not empty registre.arxiuNom}">
					<a href="<c:url value="/expedientTipus/documentDownload.html"><c:param name="expedientTipusId" value="${param.expedientTipusId}"/><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/page_white_put.png"/>" alt="<fmt:message key='comuns.descarregar' />" title="<fmt:message key='comuns.descarregar' />" border="0"/></a>
				</c:if>
			</display:column>
		</display:table>
		<script type="text/javascript">initSelectable();</script>
	</c:if>

</body>
</html>
