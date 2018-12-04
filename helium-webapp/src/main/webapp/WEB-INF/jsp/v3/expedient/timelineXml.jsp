<%@ page language="java" contentType="application/xml; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<data>
<c:forEach var="camp" items="${instanciaProces.camps}"><c:if test="${not empty instanciaProces.variables[camp.codi]}">
	<c:if test="${camp.tipus == 'DATE'}"><c:set var="inici" value="${instanciaProces.variables[camp.codi]}"/><event start="<%=formatData((java.util.Date)pageContext.getAttribute("inici"))%>" title="${camp.etiqueta}" thumbnail="../../img/calendar.png">&lt;br/&gt;<fmt:formatDate value="${inici}" pattern="dd/MM/yyyy"/></event></c:if>
</c:if></c:forEach>
<c:forEach var="documentCodi" items="${instanciaProces.documentKeys}">
	<c:if test="${not empty instanciaProces.varsDocuments[documentCodi]}">
		<c:set var="instanciaProcesActual" value="${instanciaProces}" scope="request"/>
		<c:set var="documentActual" value="${instanciaProces.varsDocuments[documentCodi]}" scope="request"/>
		<c:set var="codiDocumentActual" value="${documentCodi}" scope="request"/>
		<c:set var="documentNom"><c:choose><c:when test="${instanciaProces.varsDocuments[documentCodi].adjunt}">${instanciaProces.varsDocuments[documentCodi].adjuntTitol}</c:when><c:otherwise>${instanciaProces.varsDocuments[documentCodi].documentNom}</c:otherwise></c:choose></c:set>
		<c:set var="inici" value="${instanciaProces.varsDocuments[documentCodi].dataDocument}"/><event start="<%=formatData((java.util.Date)pageContext.getAttribute("inici"))%>" title="${documentNom}" thumbnail="../img/calendar.png">&lt;br/&gt;<c:set var="iconesDoc"><c:import url="../../common/iconesConsultaDocument.jsp"/></c:set><fmt:formatDate value="${inici}" pattern="dd/MM/yyyy"/> <c:out value="${iconesDoc}"/></event>
	</c:if>
</c:forEach>
<c:forEach var="iniciat" items="${terminisIniciats}">
	<c:set var="inici" value="${iniciat.dataInici}"/><c:set var="fi" value="${iniciat.dataFi}"/><event start="<%=formatData((java.util.Date)pageContext.getAttribute("inici"))%>" end="<%=formatData((java.util.Date)pageContext.getAttribute("fi"))%>" isDuration="true" title="${iniciat.termini.nom}">&lt;br/&gt;<fmt:formatDate value="${inici}" pattern="dd/MM/yyyy"/> - <fmt:formatDate value="${fi}" pattern="dd/MM/yyyy"/></event>
</c:forEach>
</data>
<%!
	private String formatData(java.util.Date data) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd yyyy", java.util.Locale.UK);
		return sdf.format(data);
	}
%>