<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page import="java.util.*, java.util.regex.*, net.conselldemallorca.helium.model.dto.*, net.conselldemallorca.helium.model.hibernate.*"%>

<%
	Map<String, String> campsMap = new HashMap<String, String>();
%>
<c:import url="/tasca/formRecurs.html" var="htmlFormRecurs"/>
<c:if test="${not empty tasca.camps}">
	<c:forEach var="camp" items="${tasca.camps}">
		<c:if test="${not camp.readOnly}">
			<c:set var="campTascaActual" value="${camp}" scope="request"/>
			<c:set var="codiActual" value="${camp.camp.codi}"/>
			<c:import url="../common/campTasca.jsp" var="htmlCamp">
				<c:param name="inputOnly" value="true"/>
			</c:import>
<%
	campsMap.put(
			(String)pageContext.getAttribute("codiActual"),
			(String)pageContext.getAttribute("htmlCamp"));
%>
		</c:if>
	</c:forEach>
</c:if>

<%
	TascaDto tasca = (TascaDto)request.getAttribute("tasca");
	String botons = (String)request.getAttribute("botons");
	String htmlFormRecurs = (String)pageContext.getAttribute("htmlFormRecurs");
	out.print(replaceBotons(replaceLabels(tasca, replaceInputs(tasca, htmlFormRecurs, campsMap)), botons));
%>
<%--!--helium:form-inici-->
<!--helium:form-label[]-->
<!--helium:form-input[]-->
<!--helium:form-botons-->
<!--helium:form-fi--%>

<%!
private String replaceLabels(TascaDto tasca, String text) {
	Map<String, String> replacements = new HashMap<String, String>();
	for (CampTasca campTasca : tasca.getCamps()) {
		replacements.put(
				campTasca.getCamp().getCodi(),
				"<label for=\"" + campTasca.getCamp().getCodi() + "0\">" + campTasca.getCamp().getEtiqueta() + "</label>");
	}
	return replaceTokens(text, "<!--helium:form-label\\[(.+?)\\]-->", replacements);
}
private String replaceInputs(TascaDto tasca, String text, Map<String, String> htmlInputs) {
	Map<String, String> replacements = new HashMap<String, String>();
	for (CampTasca campTasca : tasca.getCamps()) {
		String htmlInput = htmlInputs.get(campTasca.getCamp().getCodi());
		if (htmlInput != null)
			replacements.put(campTasca.getCamp().getCodi(),htmlInput);
	}
	return replaceTokens(text, "<!--helium:form-input\\[(.+?)\\]-->", replacements);
}
private String replaceBotons(String text, String botons) {
	return text.replaceAll("<!--helium:form-botons-->", botons);
}
private String replaceTokens(
		String text,
		String regexp,
		Map<String, String> replacements) {
	Pattern pattern = Pattern.compile(regexp);
	Matcher matcher = pattern.matcher(text);
	StringBuffer buffer = new StringBuffer();
	while (matcher.find()) {
		String replacement = replacements.get(matcher.group(1));
		if (replacement != null) {
			matcher.appendReplacement(buffer, "");
			buffer.append(replacement);
		}
	}
	matcher.appendTail(buffer);
	return buffer.toString();
}
%>