<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:set var="titol"><spring:message code="anotacio.llistat.reintentar.mapeig.titol" arguments="${fn:length(reprocessarMapeigAnotacioDto.idsAnotacions)}"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<script type="text/javascript">
	//<![CDATA[            
		$(document).ready(function() {});
	// ]]>
	</script>
</head>
<body>
	
	<form:form action="reprocessarMapeig" method="post" commandName="reprocessarMapeigAnotacioDto">
	
		<form:hidden path="idsAnotacions"/>
	
		<c:if test="${fn:length(reprocessarMapeigAnotacioDto.idsAnotacions)>0}">
			<div class="alert alert-info" role="alert">
				<span class="fa fa-info-circle"></span>
				<spring:message code="anotacio.llistat.reintentar.mapeig.title"/>
			</div>
			
			<div class="row">
				<div class="col-sm-11">
					<hel:inputCheckbox name="reprocessarMapeigVariables"	textKey="anotacio.llistat.reintentar.mapeig.vars"></hel:inputCheckbox>
					<hel:inputCheckbox name="reprocessarMapeigDocuments"	textKey="anotacio.llistat.reintentar.mapeig.docs"></hel:inputCheckbox>
					<hel:inputCheckbox name="reprocessarMapeigAdjunts"		textKey="anotacio.llistat.reintentar.mapeig.adjn"></hel:inputCheckbox>
				</div>
			</div>
			
			<div id="modal-botons" class="well">
				<button type="button" class="modal-tancar btn btn-default" name="submit" value="cancel">
					<spring:message code='comuns.cancelar' />
				</button>
				<button type="submit" name="submit" value="submit" class="btn btn-primary">
					<spring:message code="comu.boto.acceptar"/>
				</button>			
			</div>			
			
		</c:if>
		
		<c:if test="${fn:length(reprocessarMapeigAnotacioDto.idsAnotacions)==0}">
		
			<div class="alert alert-info" role="alert">
				<span class="fa fa-info-circle"></span>
				<spring:message code="anotacio.llistat.reintentar.mapeig.nosel"/>
			</div>
		
			<div id="modal-botons" class="well">
				<button type="button" class="modal-tancar btn btn-default" name="submit" value="cancel" data-modal-cancel="true">
					<spring:message code="comu.boto.tancar"/>
				</button>		
			</div>
		
		</c:if>

	</form:form>
</body>
</html>
