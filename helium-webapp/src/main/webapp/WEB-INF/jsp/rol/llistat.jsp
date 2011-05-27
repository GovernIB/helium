<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
	<head>
		<title><fmt:message key='rol.llistat.rols' /></title>
		<meta name="titolcmp" content="<fmt:message key='comuns.configuracio' />" />
		<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
		<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    	<script type="text/javascript">
			// <![CDATA[
			function confirmar(e) {
				var e = e || window.event;
				e.cancelBubble = true;
				if (e.stopPropagation) e.stopPropagation();
				return confirm("<fmt:message key='rol.llistat.confirmacio' />");
			}
			// ]]>
		</script>
	</head>
	
	<body>
		<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
			<display:column property="codi" titleKey="comuns.codi" sortable="true" url="/rol/form.html" paramId="codi" paramProperty="codi" />
			<display:column property="descripcio" titleKey="comuns.descripcio" sortable="true" />
			<display:column>
				<a href="<c:url value="/rol/delete.html"><c:param name="codi" value="${registre.codi}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
			</display:column>
			
			<display:setProperty name="paging.banner.item_name"><fmt:message key='rol.llistat.rol' /></display:setProperty>
			<display:setProperty name="paging.banner.items_name"><fmt:message key='rol.llistat.rol' /></display:setProperty>
		</display:table>
		
		<script type="text/javascript">initSelectable();</script>
		
		<form action="form.html">
			<button type="submit" class="submitButton"><fmt:message key='rol.llistat.nou' /></button>
		</form>

</body>
</html>
