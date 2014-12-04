<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<style type="text/css">
	.btn-file {position: relative; overflow: hidden;}
	.btn-file input[type=file] {position: absolute; top: 0; right: 0; min-width: 100%; min-height: 100%; font-size: 100px; text-align: right; filter: alpha(opacity = 0); opacity: 0; outline: none; background: white; cursor: inherit; display: block;}
	.form-group {width: 100%;}
	.fila_reducida {width: 100%;}		
	.col-xs-4 {width: 20%;}		
	.col-xs-8 {width: 77%;}
	.col-xs-8 .form-group {margin-left: 0px;margin-right: 0px;}
	.col-xs-8 .form-group .col-xs-4 {padding-left: 0px;width: 15%;}
	.col-xs-8 .form-group .col-xs-8 {width: 85%;padding-left: 15px;padding-right: 0px;}
	#s2id_estatId {width: 100% !important;}
	.arxiu {margin-left: 20%;}
	h4.titol-missatge i {padding-left: 10px;}	
</style>

<c:if test="${not empty documentsNomesLectura}">
	<c:import url="import/expedientDadesTaula.jsp">
		<c:param name="dadesAttribute" value="documentsNomesLectura"/>
		<c:param name="titol" value="Documents de referència"/>
		<c:param name="numColumnes" value="${numColumnes}"/>
		<c:param name="count" value="${fn:length(documentsNomesLectura)}"/>
		<c:param name="desplegat" value="${false}"/>
		<c:param name="desplegadorClass" value="agrupacio-desplegador"/>
	</c:import>
</c:if>
<c:forEach var="document" items="${documents}">
	<div class="well well-small">
		<h4>${document.documentNom}</h4>
	</div>
</c:forEach>
