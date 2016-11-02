<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Tipus d'expedient: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function mostrarOcultar(objid) {
	var obj = document.getElementById(objid);
	if (obj.style.display=="none") {
		obj.style.display = "block";
	} else {
		obj.style.display = "none";
	}
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="sistra"/>
	</c:import>


	
	<form:form action="sistra.html" cssClass="uniForm">
		<div class="inlineLabels">
			<form:hidden path="expedientTipusId"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="actiu"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Activar tràmits</c:param>
				<c:param name="onclick">mostrarOcultar('infoIntegracio')</c:param>
			</c:import>
			<div id="infoIntegracio"<c:if test="${not command.actiu}"> style="display:none"</c:if>>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="codiTramit"/>
					<c:param name="required" value="${true}"/>
					<c:param name="label">Identificador del tràmit</c:param>
				</c:import> 
			
				<div class="ctrlHolder" id="variables">
					<label for="infoMapeigCamps0">Mapeig de variables</label>
			   		<div class="btnCorto" onclick="document.variablesForm.submit();">
						<button type="button" class="submitButton"><fmt:message key='comuns.variables' />&nbsp;(${numMapeigVariables})</button>
					</div>
				</div>
				
				<div class="ctrlHolder" id="documents">
					<label for="infoMapeigDocuments0">Mapeig de documents</label>
			   		<div class="btnCorto" onclick="document.documentsForm.submit();">
						<button type="button" class="submitButton"><fmt:message key='comuns.documents' />&nbsp;(${numMapeigDocuments})</button>
					</div>
				</div>
				<div class="ctrlHolder" id="adjunts.html">
					<label for="infoMapeigAdjunts0">Mapeig de documents com adjunts</label>
					<div class="btnCorto" onclick="document.adjuntsForm.submit();">
						<button type="button" class="submitButton"><fmt:message key='comuns.adjunts' />&nbsp;(${numMapeigAdjunts})</button>
					</div>
				</div>
				
			</div>
			
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="notificacionsActivades"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Activar Notificacions</c:param>
				<c:param name="onclick">mostrarOcultar('infoNotificacions')</c:param>
			</c:import>
			<div id="infoNotificacions"<c:if test="${not command.notificacionsActivades}"> style="display:none"</c:if>>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="notificacioOrganCodi"/>
					<c:param name="required" value="${true}"/>
					<c:param name="label">Codi d'òrgan</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="notificacioOficinaCodi"/>
					<c:param name="required" value="${true}"/>
					<c:param name="label">Codi d'oficina</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="notificacioUnitatAdministrativa"/>
					<c:param name="required" value="${true}"/>
					<c:param name="label">Unitat administrativa</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="notificacioCodiProcediment"/>
					<c:param name="required" value="${true}"/>
					<c:param name="label">Codi de procediment</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="notificacioAvisTitol"/>
					<c:param name="label">Títol d'avís</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="notificacioAvisText"/>
					<c:param name="type" value="textarea"/>
					<c:param name="label">Text d'avís</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="notificacioAvisTextSms"/>
					<c:param name="type" value="textarea"/>
					<c:param name="label">Text SMS d'avís</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="notificacioOficiTitol"/>
					<c:param name="label">Títol d'ofici</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="notificacioOficiText"/>
					<c:param name="type" value="textarea"/>
					<c:param name="label">Text d'ofici</c:param>
				</c:import>
			</div>
		
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit</c:param>
			<c:param name="titles">Guardar</c:param>
		</c:import>
	
	</form:form>
	
	<form name="variablesForm" action="sistraVariables.html">
		<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
	</form>
	<form name="documentsForm" action="sistraDocuments.html">
		<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
	</form>
	<form name="adjuntsForm" action="sistraAdjunts.html">
		<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
	</form>
	
</body>
</html>
