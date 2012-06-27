<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Comprovació de ROLs</title>
</head>
<body>
	<form>
		Rol a comprovar: <input type="text" name="rol"<c:if test="${not empty param.rol}"> value="${param.rol}"</c:if>/>
		<input type="submit" value="Comprovar"/>
	</form>
	<c:if test="${not empty param.rol}">
		<p>isUserInRole(${param.rol}) = <%=request.isUserInRole(request.getParameter("rol"))%></p>
	</c:if>
	<br/>
	<br/>
	<p>Recordau que per a comprovar els rols HEL_USER i HEL_ADMIN heu de posar ROLE_USER i ROLE_ADMIN</p>
</body>
</html>