<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>{
	"sEcho": ${pagina.echo},
	"iTotalRecords": ${pagina.totalRecords},
	"iTotalDisplayRecords": ${pagina.totalDisplayRecords},
	"aaData": [
		<c:forEach var="dada" items="${pagina.data}" varStatus="dadaStatus">[
			<c:forEach var="columna" items="${pagina.columnNames}" varStatus="columnaStatus">
				<c:set var="valorText" value=""/>
				<c:choose><c:when test="${fn:contains(columna, '.')}"><c:set var="valorText" value="${dada[fn:split(columna, '.')[0]][fn:split(columna, '.')[1]]}"/></c:when><c:otherwise><c:set var="valorText" value="${dada[columna]}"/></c:otherwise></c:choose>
				<c:choose>
					<c:when test="${valorText.class.name == 'java.util.Date' or valorText.class.name == 'java.sql.Timestamp'}">"<fmt:formatDate value="${valorText}" pattern="dd/MM/yyyy HH:mm"/>"</c:when>
					<c:otherwise>"${valorText}"</c:otherwise>
				</c:choose><c:if test="${not columnaStatus.last}">,</c:if>
			</c:forEach>
		]<c:if test="${not dadaStatus.last}">,</c:if></c:forEach>
	]
}