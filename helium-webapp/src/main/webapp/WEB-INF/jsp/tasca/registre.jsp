<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>${registre.etiqueta}</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
 
	<link href="<c:url value="/css/reset.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/common.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.js"/>"></script>

	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>
	<c:choose>
		<c:when test="${not tancarRegistre}">
			<form:form action="registre.html" cssClass="uniForm tascaForm zebraForm">
				<form:hidden path="id"/>
				<form:hidden path="registreId"/>
				<form:hidden path="entornId"/>
				<form:hidden path="index"/>
				<div class="inlineLabels">
					<c:forEach var="membre" items="${registre.registreMembres}">
						<c:set var="campRegistreActual" value="${membre}" scope="request"/>
						<c:import url="../common/campTasca.jsp"/>
					</c:forEach>
				</div>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,cancel</c:param>
					<c:param name="titles"><fmt:message key='tasca.reg.guardar' />,<fmt:message key='comuns.cancelar' /></c:param>
				</c:import>
			</form:form>
			<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>
		</c:when>
		<c:otherwise>
			<script type="text/javascript">
				var versio = $.browser.version;
// 				if($.browser.msie && versio.substring(0,2)=="7."){
				if($.browser.msie && parseInt(versio.substring(0,3))>=7){
					parent.location.reload(true);
				}else{
					parent.refresh();
				}
				
 				//
			</script>
		</c:otherwise>
	</c:choose>
</body>
</html>
