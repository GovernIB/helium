<%@ page language="java" contentType="application/xml; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<data>
<c:forEach var="camp" items="${instanciaProces.camps}">
	<c:if test="${camp.tipus == 'DATE'}"><c:set var="inici" value="${instanciaProces.variables[camp.codi]}"/><event start="<%=formatData((java.util.Date)pageContext.getAttribute("inici"))%>" title="${camp.etiqueta}" thumbnail="../img/calendar.png">&lt;br/&gt;<fmt:formatDate value="${inici}" pattern="dd/MM/yyyy"/></event></c:if>
</c:forEach>
<c:forEach var="doc" items="${instanciaProces.documents}">
	<c:if test="${not empty instanciaProces.varsDocuments[doc.codi]}">
		<c:set var="instanciaProcesActual" value="${instanciaProces}" scope="request"/>
		<c:set var="documentActual" value="${instanciaProces.varsDocuments[doc.codi]}" scope="request"/>
		<c:set var="codiDocumentActual" value="${doc.codi}" scope="request"/>
		<c:set var="inici" value="${instanciaProces.varsDocuments[doc.codi].dataDocument}"/><event start="<%=formatData((java.util.Date)pageContext.getAttribute("inici"))%>" title="${doc.nom}" thumbnail="../img/calendar.png">&lt;br/&gt;<c:set var="iconesDoc"><c:import url="../common/iconesConsultaDocument.jsp"/></c:set><fmt:formatDate value="${inici}" pattern="dd/MM/yyyy"/> <c:out value="${iconesDoc}"/></event>
	</c:if>
</c:forEach>
<c:forEach var="iniciat" items="${terminisIniciats}">
	<c:set var="inici" value="${iniciat.dataInici}"/><c:set var="fi" value="${iniciat.dataFi}"/><event start="<%=formatData((java.util.Date)pageContext.getAttribute("inici"))%>" end="<%=formatData((java.util.Date)pageContext.getAttribute("fi"))%>" isDuration="true" title="${iniciat.termini.nom}">&lt;br/&gt;<fmt:formatDate value="${inici}" pattern="dd/MM/yyyy"/> - <fmt:formatDate value="${fi}" pattern="dd/MM/yyyy"/></event>
</c:forEach>
</data>
<%!
	private String formatData(java.util.Date data) {
		if (data == null)
			return "";
		//java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd yyyy '00:00:00' 'GMT'Z", java.util.Locale.UK);
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd yyyy", java.util.Locale.UK);
		return sdf.format(data);
	}
%>