<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Tipus d'expedient: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="sistra"/>
	</c:import>

	<h3 class="titol-tab titol-variables-tascadef">
		<fmt:message key='exptipus.sistra.mapeigs.vars_documents' />
	</h3>
	<display:table name="mapeigSistras" id="registre" requestURI="" class="displaytag">
		<display:column property="codiHelium" title="Codi Helium" sortable="true"/>
		<display:column property="codiSistra" title="Codi Sistra" sortable="true"/>
		<display:column>
			<a href="<c:url value="/expedientTipus/mapeigSistraEsborrarDocument.html"><c:param name="id" value="${registre.id}"/><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		</display:column>
	</display:table>

	<form:form action="documents.html" cssClass="uniForm">
		<fieldset class="inlineLabels">
			<legend>Afegir nou mapeig</legend>
			<input id="expedientTipusId" name="expedientTipusId" value="${param.expedientTipusId}" type="hidden"/>
			<div id="camps_accio">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="codiHelium"/>
					<c:param name="required" value="${true}"/>
					<c:param name="type" value="select"/>
					<c:param name="items" value="codisProces"/>
					<c:param name="itemLabel" value="codi"/>
					<c:param name="itemValue" value="codi"/>
					<c:param name="itemBuit">&lt;&lt; <fmt:message key='exptipus.sistra.selec_codi' /> &gt;&gt;</c:param>
					<c:param name="label"><fmt:message key='exptipus.sistra.codihelium' /></c:param>
				</c:import>
			</div>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codiSistra"/>
				<c:param name="required" value="${true}"/>
				<c:param name="label"><fmt:message key='exptipus.sistra.codisistra' /></c:param>
			</c:import>
		</fieldset>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit</c:param>
			<c:param name="titles">Afegir</c:param>
		</c:import>
	</form:form>

</body>
</html>
