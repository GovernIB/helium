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

<%
InputStream is = getClass().getClassLoader().getResourceAsStream("static/designer/index.html");
String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);
Matcher js = Pattern.compile("<script[^>]*src=\"([^\"]+)\"").matcher(html);
Matcher css = Pattern.compile("<link[^>]*href=\"([^\"]+\\.css)\"").matcher(html);
String jsFile = js.find() ? js.group(1) : "";
String cssFile = css.find() ? css.group(1) : "";

String baseHelium = es.caib.helium.commons.utils.GlobalProperties.getInstance().getProperty(es.caib.helium.commons.config.PropertyConfig.PROP_BASE_URL);
%>

<script>
	window.__APP_CONFIG__ = {
		baseUrl: "<%=baseHelium%>",
		returnUrl: '${returnUrl}',
		entornId: '${entornId}',
		expedientTipusId: '${expedientTipusId}',
		definicioProcesEtiqueta: '${definicioProces.etiqueta}',
		definicioProcesId: '',//'${definicioProces.id}',
		hasStartTask: ${(empty definicioProces)? false : definicioProces.hasStartTask},
		isNew: ${empty definicioProces}
	};
	document.title = "Disseny definició de procés";
</script>

<link rel="stylesheet" href="<%=cssFile%>">
<div id="react-root" style="min-height: 500px;"></div>

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
</script>
