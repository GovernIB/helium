<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set var="titol"><spring:message code="unitat.organitzativa.detall.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
</head>
<body>
	<c:if test="${not empty unitatOrganitzativaDto}">
		<h4 class="capsalera"><fmt:message key='unitat.organitzativa.detall.dades.basiques' /></h4>
		<dl class="dl-horizontal">
			
			<dt><spring:message code="unitat.organitzativa.codi"/></dt>
			<dd>${unitatOrganitzativaDto.codi}</dd>
			
			<dt><spring:message code="unitat.organitzativa.denominacio"/></dt>
			<dd>${unitatOrganitzativaDto.denominacio}</dd>
		
			
			<dt><spring:message code="unitat.organitzativa.estat"/></dt>
			<dd>
				<span title="<spring:message code="unitat.organitzativa.estat.enum.${unitatOrganitzativaDto.estat}"/>">
					<c:choose>
							<c:when test="${unitatOrganitzativaDto.estat == 'V'}">
								<span class="fa fa-check-circle"></span>
							</c:when>
							<c:when test="${unitatOrganitzativaDto.estat == 'A'}">
								<span class="fa fa-xmark"></span>
							</c:when>
							<c:when test="${unitatOrganitzativaDto.estat == 'E'}">
								<span class="fa fa-file-o"></span>
							</c:when>
							<c:when test="${unitatOrganitzativaDto.estat == 'T'}">
								<span class="fa fa-check"></span>
							</c:when>							
					</c:choose>
			<spring:message code="unitat.organitzativa.estat.enum.${unitatOrganitzativaDto.estat}"/>
			</dd>
			
			<dt><spring:message code="unitat.organitzativa.nif.cif"/></dt>
			<dd>${unitatOrganitzativaDto.nifCif}</dd>
			
			<dt><spring:message code="unitat.organitzativa.llistat.unitat.arrel"/></dt>
			<dd>${unitatArrel.codiAndNom}</dd>
			
			<dt><spring:message code="unitat.organitzativa.unitat.superior"/></dt>
			<dd>${unitatSuperior.codiAndNom}</dd>
		
			
		</dl>
		
		<h4 class="capsalera"><fmt:message key='unitat.organitzativa.detall.dades.direccio' /></h4>
		<dl class="dl-horizontal">
			<dt><spring:message code="unitat.organitzativa.detall.adressa"/></dt>
			<dd>${unitatOrganitzativaDto.adressa}</dd>
			
			<dt><spring:message code="unitat.organitzativa.detall.codi.postal"/></dt>
			<dd>${unitatOrganitzativaDto.codiPostal}</dd>	
			
			<dt><spring:message code="unitat.organitzativa.detall.municipi"/></dt>
			<dd>${unitatOrganitzativaDto.nomLocalitat}</dd>
			
			<dt><spring:message code="unitat.organitzativa.detall.provincia"/></dt>
			<dd>${unitatOrganitzativaDto.nomProvincia}</dd>
			
			<dt><spring:message code="unitat.organitzativa.detall.pais"/></dt>
			<dd>${unitatOrganitzativaDto.nomPais}</dd>	
		</dl>
		
	</c:if>
	<div id="modal-botons">
		<a href="<c:url value="/unitatOrganitzativa"/>" class="btn btn-default modal-tancar" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>