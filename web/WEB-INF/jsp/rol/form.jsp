<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
	<head>
		<title>
			<c:choose>
				<c:when test="${empty command.codi}">Crear nou rol</c:when>
				<c:otherwise>Modificar rol</c:otherwise>
			</c:choose>
		</title>
		<meta name="titolcmp" content="Configuració"/>
		<c:import url="../common/formIncludes.jsp"/>
		<script type="text/javascript">
			// <![CDATA[
			// ]]>
		</script>
	</head>
	
	<body>
		<form:form action="form.html" cssClass="uniForm" enctype="multipart/form-data">
			<input type="hidden" name="id" value="${command.codi}" />
			<div class="inlineLabels">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="codi"/>
					<c:param name="required" value="true"/>
					<c:param name="label">Codi</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="descripcio"/>
					<c:param name="type" value="textarea"/>
					<c:param name="required" value="true"/>
					<c:param name="label">Descripció</c:param>
				</c:import>
			</div>
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">submit,cancel</c:param>
				<c:param name="titles"><c:choose><c:when test="${empty command.codi}">Crear,Cancel·lar</c:when><c:otherwise>Modificar,Cancel·lar</c:otherwise></c:choose></c:param>
			</c:import>
		</form:form>
		
		<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>
	</body>
</html>
