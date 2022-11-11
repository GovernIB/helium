<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<html>
<head>
<script>

	document.cookie = 'JSESSIONID=; path=/helium; expires=' + new Date(0).toUTCString();
    domini = window.location.hostname;
    if (domini.includes('.caib.es')) {
    	domini = '.caib.es';
    }
	var cookies = document.cookie.split("; ");
	for (var c = 0; c < cookies.length; c++) {
		if (cookies[c].startsWith('es.caib.loginModule')) {
			entorn = cookies[c].split('=')[0].split('/')[1];
			document.cookie = 'es.caib.loginModule/' + entorn + '=; path=/; expires=' + new Date(0).toUTCString() + '; domain=' + domini;
		}		
	}

	// Redirigeix a la pàgina inicial.
	window.location="/helium";
</script>
</head>
<body>
</body>
</html>
