<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
	<head>
		<title>Assignar variables: ${agrupacio.nom}</title>
		<meta name="titolcmp" content="Disseny"/>
		<c:import url="../common/formIncludes.jsp"/>
		<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
		<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
		<script type="text/javascript">
			// <![CDATA[
			function confirmar(e) {
				var e = e || window.event;
				e.cancelBubble = true;
				if (e.stopPropagation) e.stopPropagation();
				return confirm("Estau segur que voleu esborrar aquest camp d'aquesta agrupació?");
			}
			// ]]>
		</script>
	</head>
	
	<body>
		<c:import url="../common/tabsDefinicioProces.jsp">
			<c:param name="tabActiu" value="agrupacions"/>
		</c:import>
		
		<display:table name="camps" id="registre" requestURI="" class="displaytag selectable">
			<display:column property="etiqueta" title="Etiqueta" sortable="true"/>
			<display:column>
				<a href="<c:url value="/definicioProces/campAgrupacioOrdrePujar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/><c:param name="agrupacioCodi" value="${agrupacio.codi}"/></c:url>"><img src="<c:url value="/img/famarrow_up.png"/>" alt="Amunt" title="Amunt" border="0"/></a>
				<a href="<c:url value="/definicioProces/campAgrupacioOrdreBaixar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/><c:param name="agrupacioCodi" value="${agrupacio.codi}"/></c:url>"><img src="<c:url value="/img/famarrow_down.png"/>" alt="Avall" title="Avall" border="0"/></a>
			</display:column>
			<display:column>
		    	<a href="<c:url value="/definicioProces/campAgrupacioOrdreDelete.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/><c:param name="agrupacioCodi" value="${agrupacio.codi}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
		    </display:column>
		</display:table>
		
		<form:form action="campAgrupacioOrdre.html" method="post" cssClass="uniForm">
			<input type="hidden" id="definicioProcesId" name="definicioProcesId" value="${param.definicioProcesId}" />
			<input type="hidden" id="agrupacioCodi" name="agrupacioCodi" value="${agrupacio.codi}" />
			<fieldset class="inlineLabels">
				<legend>Afegir variable</legend>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="id"/>
					<c:param name="type" value="select"/>
					<c:param name="items" value="variables"/>
					<c:param name="itemLabel" value="codiEtiqueta"/>
					<c:param name="itemValue" value="id"/>
					<c:param name="itemBuit" value="<< Seleccioni una variable >>"/>
					<c:param name="label">Variables de la tasca</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,cancel</c:param>
					<c:param name="titles">Afegeix variable, Cancel·lar</c:param>
				</c:import>
			</fieldset>
		</form:form>
	</body>
</html>
