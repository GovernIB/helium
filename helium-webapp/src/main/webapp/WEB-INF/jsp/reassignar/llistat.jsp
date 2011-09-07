<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display"%>

<html>
	<head>
		<title><fmt:message key='reassign.llistat.reassignacions' /></title>
		<meta name="titolcmp" content="<fmt:message key='comuns.configuracio' />" />
		<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>		
		<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
		<script type="text/javascript">
			// <![CDATA[
			function confirmar(e) {
				var e = e || window.event;
				e.cancelBubble = true;
				if (e.stopPropagation) e.stopPropagation();
				return confirm("<fmt:message key='reassign.llistat.confirmacio' />");
			}
			// ]]>
		</script>
	</head>
	
	<body>
		<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
			<display:column property="usuariOrigen" titleKey="reassign.llistat.usu_origen" sortable="true" url="/reassignar/form.html" paramId="id" paramProperty="id"/>
			<display:column property="usuariDesti" titleKey="reassign.llistat.usu_dest" sortable="true"/>
			<display:column titleKey="reassign.llistat.data_ini" sortable="true">
				<fmt:formatDate value="${registre.dataInici}" pattern="dd/MM/yyyy"/>
			</display:column>
			<display:column titleKey="reassign.llistat.data_fi" sortable="true">
				<fmt:formatDate value="${registre.dataFi}" pattern="dd/MM/yyyy"/>
			</display:column>
			<display:column titleKey="comuns.cancelar" sortable="false">
				<a href="<c:url value="/reassignar/cancelar.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.cancelar' />" title="<fmt:message key='comuns.cancelar' />" border="0"/></a>
			</display:column>
			<display:setProperty name="paging.banner.item_name"><fmt:message key='reassign.llistat.reassignacio' /></display:setProperty>
			<display:setProperty name="paging.banner.items_name"><fmt:message key='reassign.llistat.reassignacio' /></display:setProperty>
		</display:table>

		<script type="text/javascript">initSelectable();</script>

		<form action="form.html">
			<button type="submit" class="submitButton"><fmt:message key='reassign.llistat.nova' /></button>
		</form>
	</body>
</html>
