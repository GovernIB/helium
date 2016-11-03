<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Tipus d'expedient: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function mostrarOcultar(img, objid) {
	var obj = document.getElementById(objid);
	if (obj.style.display=="none") {
		obj.style.display = "block";
		img.src = '<c:url value="/img/magnifier_zoom_out.png"/>';
	} else {
		obj.style.display = "none";
		img.src = '<c:url value="/img/magnifier_zoom_in.png"/>';
	}
}
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='defproc.info.confirmacio' />");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="info"/>
	</c:import>

	<h3 class="titol-tab titol-info">Informació del tipus d'expedient</h3>
	<dl class="form-info">
		<dt>Codi</dt><dd>${expedientTipus.codi}</dd>
		<dt>Títol</dt><dd>${expedientTipus.nom}</dd>
		<dt>Amb títol?</dt><dd><c:choose><c:when test="${expedientTipus.teTitol}">Si</c:when><c:otherwise>No</c:otherwise></c:choose></dd>
		<dt>Demana títol?</dt><dd><c:choose><c:when test="${expedientTipus.demanaTitol}">Si</c:when><c:otherwise>No</c:otherwise></c:choose></dd>
		<dt>Amb número?</dt><dd><c:choose><c:when test="${expedientTipus.teNumero}">Si</c:when><c:otherwise>No</c:otherwise></c:choose></dd>
		<dt>Demana número?</dt><dd><c:choose><c:when test="${expedientTipus.demanaNumero}">Si</c:when><c:otherwise>No</c:otherwise></c:choose></dd>
		<dt>Permet retroacció?</dt><dd><c:choose><c:when test="${expedientTipus.ambRetroaccio}">Si</c:when><c:otherwise>No</c:otherwise></c:choose></dd>
		<dt>Reindexa asíncronament?</dt><dd><c:choose><c:when test="${expedientTipus.reindexacioAsincrona}">Si</c:when><c:otherwise>No</c:otherwise></c:choose></dd>
		<c:if test="${not empty expedientTipus.responsableDefecteCodi}"><dt>Responsable per defecte</dt><dd>${responsableDefecte.nomSencer}</dd></c:if>
		<c:if test="${not empty definicioProcesInicial}"><dt>Definició de procés inicial</dt><dd>${definicioProcesInicial.jbpmName}</dd></c:if>
		<c:if test="${not empty expedientTipus.diesNoLaborables}"><dt>Dies no laborables</dt><dd>${expedientTipus.diesNoLaborables}</dd></c:if>
	</dl>
	<div style="clear: both"></div>
	<br/>
	<security:accesscontrollist domainObject="${entornActual}" hasPermission="16,32">
	
	<div class="missatgesGris"> 
		<h3 class="titol-tab titol-delegacio"><fmt:message key='defproc.info.import_dades' /> <img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="<fmt:message key='defproc.info.mos_ocul' />" title="<fmt:message key='defproc.info.mos_ocul' />" border="0" onclick="mostrarOcultar(this,'form-importar')"/></h3>
		<div id="form-importar" style="display:none">
			<form:form action="importar.html" cssClass="uniForm" enctype="multipart/form-data">
				<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
				<div class="inlineLabels">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="arxiu"/>
						<c:param name="type" value="file"/>
						<c:param name="label"><fmt:message key='defproc.info.arxiu_exp' /></c:param>
					</c:import>
					<c:import url="../common/formElement.jsp">
						<c:param name="type" value="buttons"/>
						<c:param name="values">submit</c:param>
						<c:param name="titles"><fmt:message key='defproc.info.importar' /></c:param>
					</c:import>
				</div>
			</form:form>
		</div>
	</div>

	<div class="missatgesGris">
		<form style="display: inline;" action="<c:url value="/expedientTipus/form.html"/>">
			<input type="hidden" name="id" value="${expedientTipus.id}"/>
			<button type="submit" class="submitButton">Modificar informació</button>
		</form>
		<form style="display: inline;" action="<c:url value="/expedientTipus/exportar.html"/>" method="post" style="display: inline">
			<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
			<button type="submit" class="submitButton"><fmt:message key='defproc.info.exportar_versio' /></button>
		</form>
		<form style="display: inline;" action="<c:url value="/expedientTipus/propagarPlantilles.html"/>" method="post" style="display: inline">
			<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
			<button type="submit" class="submitButton" title="<fmt:message key='exptipus.info.propagar.plantilles.info' />">
				<fmt:message key='exptipus.info.propagar.plantilles' />
			</button>
		</form>
		<form style="display: inline;" action="<c:url value="/expedientTipus/propagarConsultes.html"/>" method="post" style="display: inline">
			<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
			<button type="submit" class="submitButton" title="<fmt:message key='exptipus.info.propagar.consultes.info' />">
				<fmt:message key='exptipus.info.propagar.consultes' />
			</button>
		</form>
	</div>
 
	</security:accesscontrollist>
	<br /><br />
<!-- 	<div class="missatgesGris">
		<form action="<c:url value="/expedientTipus/exportar.html"/>" method="post" style="display: inline">
			<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
			<button type="submit" class="submitButton"><fmt:message key='defproc.info.exportar_versio' /></button>
		</form>
	</div> -->

</body>
</html>
