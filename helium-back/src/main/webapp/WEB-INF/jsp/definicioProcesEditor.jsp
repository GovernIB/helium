<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="java.util.regex.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="idioma">ca</c:set>

<script src="<c:url value="/js/webutil.common.js"/>"></script>

<!--
<link rel="stylesheet" href="https://unpkg.com/bpmn-js@18.16.1/dist/assets/diagram-js.css" />
<link rel="stylesheet" href="https://unpkg.com/bpmn-js@18.16.1/dist/assets/bpmn-js.css" />
<link rel="stylesheet" href="https://unpkg.com/bpmn-js@18.16.1/dist/assets/bpmn-font/css/bpmn.css" />
<script src="https://unpkg.com/bpmn-js@18.16.1/dist/bpmn-modeler.production.min.js"></script>
-->


<%
InputStream is = getClass().getClassLoader().getResourceAsStream("static/designer/index.html");
String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);
Matcher js = Pattern.compile("<script[^>]*src=\"([^\"]+)\"").matcher(html);
Matcher css = Pattern.compile("<link[^>]*href=\"([^\"]+\\.css)\"").matcher(html);
String jsFile = js.find() ? js.group(1) : "";
String cssFile = css.find() ? css.group(1) : "";
%>

<link rel="stylesheet" href="<%=cssFile%>">
<div id="react-root"></div>

<script>
		const src = '<%=jsFile%>';
		const already = document.querySelector('script[src="' + src + '"]');
		if (!already) {
			const script = document.createElement('script');
			script.type = 'module';
			script.src = src;
			document.body.appendChild(script); // only runs once, ever
		} else if (window.mountRootApp) {
			window.mountRootApp(); // module already loaded, just remount
		}
	/*
	//$(document).ready(function () {
		// Aquest script es necessari per carregar el module de la app de react correctament i que no doni conflictes amb jquery
		debugger;
		const existing = document.querySelector(`script[src="<%=jsFile%>"]`);
		if (existing) existing.remove();
		//if (!existing) {
		const script = document.createElement('script');
		script.type = 'module';
		script.src = '<%=jsFile%>';
		//document.body.appendChild(script);
		document.getElementById('react-root').appendChild(script);
		//}
	//});
	*/
</script>
