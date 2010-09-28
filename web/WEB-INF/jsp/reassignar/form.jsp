<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
	<head>
		<title><c:choose><c:when test="${empty command.id}">Crear nova reassignació</c:when><c:otherwise>Modificar reassignació</c:otherwise></c:choose></title>
		<meta name="titolcmp" content="Configuració"/>
		<c:import url="../common/formIncludes.jsp"/>
		<script type="text/javascript">
			// <![CDATA[
			function mostrarAmagarAuth() {
				var el = document.getElementById('dadesAuth');
				if (el.style.display == '')
					el.style.display = 'none';
				else
					el.style.display = '';
			}
			// ]]>
		</script>
	</head>
	
	<body>
		<form:form action="form.html" cssClass="uniForm">
			<div class="inlineLabels">
				<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
				
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="usuariOrigen"/>
					<c:param name="required" value="true"/>
					<c:param name="type" value="suggest"/>
					<c:param name="label">Usuari d'origen</c:param>
					<c:param name="suggestUrl"><c:url value="/persona/suggest.html"/></c:param>
					<c:param name="suggestText">${command.usuariOrigen}</c:param>
				</c:import>
				
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="usuariDesti"/>
					<c:param name="required" value="true"/>
					<c:param name="type" value="suggest"/>
					<c:param name="label">Usuari destí</c:param>
					<c:param name="suggestUrl"><c:url value="/persona/suggest.html"/></c:param>
					<c:param name="suggestText">${command.usuariDesti}</c:param>
				</c:import>
				
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="dataInici"/>
					<c:param name="required" value="true"/>
					<c:param name="type" value="date"/>
					<c:param name="label">Data d'inici</c:param>
				</c:import>
				
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="dataFi"/>
					<c:param name="required" value="true"/>
					<c:param name="type" value="date"/>
					<c:param name="label">Data fi</c:param>
				</c:import>
				
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,cancel</c:param>
					<c:param name="titles"><c:choose><c:when test="${empty command.id}">Crear,Cancel·lar</c:when><c:otherwise>Modificar,Cancel·lar</c:otherwise></c:choose></c:param>
				</c:import>
			</div>
		</form:form>

		<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>
	</body>
</html>
