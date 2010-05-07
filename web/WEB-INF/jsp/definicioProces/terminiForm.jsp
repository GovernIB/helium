<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
	<title>Definició de procés: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="Disseny">
	<c:import url="../common/formIncludes.jsp"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="terminis"/>
	</c:import>

	<form:form action="terminiForm.html" cssClass="uniForm">
		<div class="inlineLabels">
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<form:hidden path="definicioProces"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Codi</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Nom</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="descripcio"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label">Descripció</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property">dies</c:param>
				<c:param name="required">true</c:param>
				<c:param name="type" value="custom"/>
				<c:param name="label">Durada</c:param>
				<c:param name="content">
					<spring:bind path="anys">
						<label for="anys" class="blockLabel">
							<span>Anys</span>
							<select id="anys" name="anys">
								<c:forEach var="index" begin="0" end="12">
									<option value="${index}"<c:if test="${status.value==index}"> selected="selected"</c:if>>${index}</option>
								</c:forEach>
							</select>
						</label>
					</spring:bind>
					<spring:bind path="mesos">
						<label for="mesos" class="blockLabel">
							<span>Mesos</span>
							<select id="mesos" name="mesos">
								<c:forEach var="index" begin="0" end="12">
									<option value="${index}"<c:if test="${status.value==index}"> selected="selected"</c:if>>${index}</option>
								</c:forEach>
							</select>
						</label>
					</spring:bind>
					<spring:bind path="dies">
						<label for="dies" class="blockLabel">
							<span>Dies</span>
							<input id="dies" name="dies" value="${status.value}" class="textInput"/>
						</label>
						<form:hidden path="${codiActual}"/>
					</spring:bind>
				</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="laborable"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">De dies laborables?</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="manual"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Control manual?</c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}">Crear,Cancel·lar</c:when><c:otherwise>Modificar,Cancel·lar</c:otherwise></c:choose></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>

</body>
</html>
