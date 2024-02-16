<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:set var="titol"><spring:message code="unitat.arbre.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<link href="<c:url value="/css/jstree.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jstree.min.js"/>"></script>
	<script type="text/javascript">
		function contreu() {
 			var arbre = $('#arbreUnitats');
 			arbre.jstree('close_all');
 			arbre.jstree("deselect_all");
 			arbre.jstree("select_node", "ul > li:first");
  			var selectedNode = arbre.jstree("get_selected");
			arbre.jstree('close_all');
			arbre.jstree("open_node", selectedNode, false, true);
	
		}
		function obre() {
			var arbre = $('#arbreUnitats');
			var selectedNode = arbre.jstree("get_selected");
			arbre.jstree("open_all", selectedNode, 0, arbre);
		}
		function checkSelectedNodes() {
			// Se declara esta función para evitar error JS en el arbre.tag durante la llamada a la misma
		}
		function paintSelectedNodes() {
			// Se declara esta función para evitar error JS en el arbre.tag durante la llamada a la misma
		}
	</script>
</head>
<body >


	<div class="text-center" data-toggle="botons-titol" style="margin-bottom: 10px;">
		<button class="btn btn-default" onclick="$('#arbreUnitats').jstree('open_all');"><span class="fa fa-caret-square-o-down"></span> <spring:message code="unitat.arbre.expandeix"/></button>
 		<button class="btn btn-default" onclick="obre();"><span class="fa fa-caret-square-o-right"></span> <spring:message code="unitat.arbre.contreu.expandeixSeleccionat"/></button> 
		<button class="btn btn-default" onclick="contreu();"><span class="fa fa-caret-square-o-up"></span> <spring:message code="unitat.arbre.contreu"/></button>

	</div>
	<hel:arbre id="arbreUnitats" arbre="${arbreUnitatsOrganitzatives}" atributId="codi" atributNom="codiAndNom" height="500px" />
</body>
</html>