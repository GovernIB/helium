<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:choose>
	<c:when test="${documentActual.signat}"><a href="<c:url value="/expedient/documentConsultar.html"><c:param name="processInstanceId" value="${instanciaProcesActual.id}"/><c:param name="docId" value="${documentActual.id}"/></c:url>"><img src="<c:url value="/img/rosette.png"/>" alt="Descarregar" title="Descarregar" border="0"/></a></c:when>
	<c:otherwise><a href="<c:url value="/expedient/documentConsultar.html"><c:param name="processInstanceId" value="${instanciaProcesActual.id}"/><c:param name="docId" value="${documentActual.id}"/></c:url>"><img src="<c:url value="/img/page_white_put.png"/>" alt="Descarregar" title="Descarregar" border="0"/></a></c:otherwise>
</c:choose>
