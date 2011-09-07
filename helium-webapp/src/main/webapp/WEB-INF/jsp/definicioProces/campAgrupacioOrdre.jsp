<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
	<head>
		<title><fmt:message key='defproc.agrupordre.assignar' />: ${agrupacio.nom}</title>
		<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
		<c:import url="../common/formIncludes.jsp"/>
		<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
		<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
		<script type="text/javascript">
			// <![CDATA[
			function confirmar(e) {
				var e = e || window.event;
				e.cancelBubble = true;
				if (e.stopPropagation) e.stopPropagation();
				return confirm("<fmt:message key='defproc.agrupordre.confirmacio' />");
			}
			// ]]>
		</script>
	</head>
	
	<body>
		<c:import url="../common/tabsDefinicioProces.jsp">
			<c:param name="tabActiu" value="agrupacions"/>
		</c:import>
		
		<display:table name="camps" id="registre" requestURI="" class="displaytag selectable">
			<display:column property="etiqueta" titleKey="comuns.etiqueta" sortable="true"/>
			<display:column>
				<a href="<c:url value="/definicioProces/campAgrupacioOrdrePujar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/><c:param name="agrupacioCodi" value="${agrupacio.codi}"/></c:url>"><img src="<c:url value="/img/famarrow_up.png"/>" alt="<fmt:message key='comuns.amunt' />" title="<fmt:message key='comuns.amunt' />" border="0"/></a>
				<a href="<c:url value="/definicioProces/campAgrupacioOrdreBaixar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/><c:param name="agrupacioCodi" value="${agrupacio.codi}"/></c:url>"><img src="<c:url value="/img/famarrow_down.png"/>" alt="<fmt:message key='comuns.avall' />" title="<fmt:message key='comuns.avall' />" border="0"/></a>
			</display:column>
			<display:column>
		    	<a href="<c:url value="/definicioProces/campAgrupacioOrdreDelete.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/><c:param name="agrupacioCodi" value="${agrupacio.codi}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		    </display:column>
		</display:table>
		
		<form:form action="campAgrupacioOrdre.html" method="post" cssClass="uniForm">
			<input type="hidden" id="definicioProcesId" name="definicioProcesId" value="${param.definicioProcesId}" />
			<input type="hidden" id="agrupacioCodi" name="agrupacioCodi" value="${agrupacio.codi}" />
			<fieldset class="inlineLabels">
				<legend><fmt:message key='defproc.agrupordre.afegir_var' /></legend>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="id"/>
					<c:param name="type" value="select"/>
					<c:param name="items" value="variables"/>
					<c:param name="itemLabel" value="codiEtiqueta"/>
					<c:param name="itemValue" value="id"/>
					<c:param name="itemBuit">&lt;&lt; <fmt:message key='defproc.agrupordre.selec_var' /> &gt;&gt;</c:param>
					<c:param name="label"><fmt:message key='defproc.agrupordre.vars_tasca' /></c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,cancel</c:param>
					<c:param name="titles"><fmt:message key='defproc.agrupordre.afegeix_var' />, <fmt:message key='comuns.cancelar' /></c:param>
				</c:import>
			</fieldset>
		</form:form>
	</body>
</html>
