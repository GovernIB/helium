<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="width_iframe">
	<c:choose>
		<c:when test="${not empty width}">${width}px</c:when>
		<c:otherwise>100%</c:otherwise>
	</c:choose>
</c:set>
<c:set var="height_iframe">
	<c:choose>
		<c:when test="${not empty height}">${height}px</c:when>
		<c:otherwise>390px</c:otherwise>
	</c:choose>
</c:set>
<html>
<head>
	<title>${tittle}</title>
	<hel:modalHead/>
	<style>
		body {background-image: none;padding-top: 0px;}
		iframe {width: 100%;height: ${height_iframe};}
	</style>
</head>
<body>
	<div>
		<iframe id="preview-frame" src="${url}" name="preview-frame" frameborder="0"></iframe>
	</div>
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.tancar"/></button>
	</div>
</body>
</html>
