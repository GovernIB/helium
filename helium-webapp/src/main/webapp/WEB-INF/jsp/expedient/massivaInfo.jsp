<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<c:set var="sessionCommand" value="${sessionScope.expedientTipusConsultaDissenyCommandTE}"/>

<html>
<head>
	<title><fmt:message key="expedient.massiva.titol"/></title>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
var docPlantilla = {
	<c:forEach items="${documents}" var="document">${document.codi} : ${document.plantilla},
	</c:forEach>__none__ : false
}
            
function mostrarOcultar(img, objid) {
	var obj = document.getElementById(objid);
	if (obj.style.display=="none") {
		$("#"+objid).show("blind", 500);
		//obj.style.display = "block";
		img.src = '<c:url value="/img/magnifier_zoom_out.png"/>';
	} else {
		$("#"+objid).hide("blind", 500);
		//obj.style.display = "none";
		img.src = '<c:url value="/img/magnifier_zoom_in.png"/>';
	}
}
function confirmarCanviVersio(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	programacio();
	return confirm("<fmt:message key='expedient.massiva.confirm_canviar_versio_proces'/>");
}
function confirmarExecutarAccio(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	programacio();
	return confirm("<fmt:message key='expedient.massiva.confirm_exec_accio'/>");
}
function confirmarScript(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	programacio();
	return confirm("<fmt:message key='expedient.eines.confirm_executar_script_proces' />");
}
function confirmarAturar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	programacio();
	return confirm("<fmt:message key='expedient.massiva.confirm_aturar_tramitacio' />");
}
function confirmarModificarDocument(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	programacio();
	return confirm("<fmt:message key='expedient.eines.confirm_modificar_document' />");
}
function confirmarModificarVariables(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	programacio();
	return confirm("<fmt:message key='expedient.eines.confirm_modificar_variable' />");
}
function confirmarReindexar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	programacio();
	return confirm("<fmt:message key='expedient.eines.confirm_reindexar_expedients' />");
}
function programacio(){
	var inici = $("#inici").val();
	var correu = $("#correu").is(":checked") ? true : false;
	$("input[type='hidden'][name='inici']").each(function(){ $(this).val(inici); });
	$("input[type='hidden'][name='correu']").each(function(){ $(this).val(correu); });
}
function changeDoc(){
	var docId = $("#nom0").val();
	$("#docId").val(docId);
	
	var doc = "__none__";
	if (docId != "") {
	 	doc = $("#nom0 option[value='" + docId + "']").text();
	 	$("button[value='delete']").prop('disabled', false);
	 	$("button[value='subdoc']").prop('disabled', false);
	} else {
		$("button[value='delete']").prop('disabled', true);
		$("button[value='subdoc']").prop('disabled', true);
	}
	var plant = "docPlantilla." + doc;
	if (eval(plant)) {
		$("button[value='generar']").prop('disabled', false);
	} else {
		$("button[value='generar']").prop('disabled', true);
	}
}
function changeVar(){
	var varId = $("#var0").val();
	
	if (varId != "") {
	 	$("button[value='subvar']").prop('disabled', false);
	} else {
		$("button[value='subvar']").prop('disabled', true);
	}
}
function massiva(form){
	if("<%=request.getAttribute("javax.servlet.forward.request_uri")%>" == "/helium/expedient/massivaInfoTE.html" )
	{
			$("#massiva").attr("action","consultaDisseny.html");
	}
	else if("<%=request.getAttribute("javax.servlet.forward.request_uri")%>" == "/helium/expedient/massivaInfo.html"){
		$("#massiva").attr("action","consulta.html");
		$("#target").val("consulta");
	}
	$('<input type="hidden" name="targetConsulta" value="<%=request.getAttribute("javax.servlet.forward.request_uri")%>" />').appendTo(form);
}

$(document).ready(function(){
	var d = new Date();
	var dia = ("0" + d.getDate()).slice(-2);
    var mes = ("0" + (d.getMonth() + 1)).slice(-2);
    var any = d.getFullYear();
    var hora = ("0" + d.getHours()).slice(-2);
    var minuts = ("0" + d.getMinutes()).slice(-2);
    var ara = dia + '/' + mes + '/' + any + ' ' + hora + ":" + minuts
    
    //$('#inici').val(ara);
	$('#inici').datetimepicker({defaultValue: ara});
	$("button[value='delete']").prop('disabled', true);
	$("button[value='generar']").prop('disabled', true);
	$("button[value='subdoc']").prop('disabled', true);
	$("button[value='subvar']").prop('disabled', true);
	//programacio();
});
// ]]>
</script>
</head>
<body>

	<div class="missatgesBlau">
		<h3 class="titol-tab titol-massiva"><fmt:message key="expedient.massiva.info"/> <img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="<fmt:message key="expedient.massiva.mos_ocul"/>" title="<fmt:message key="expedient.massiva.mos_ocul"/>" border="0" onclick="mostrarOcultar(this,'info-massiva')"/></h3>
		<div id="info-massiva" style="display:none">
			<display:table name="expedients" id="registre" requestURI="" class="displaytag selectable">
				<c:set var="filaStyle" value=""/>
				<c:if test="${registre.anulat}"><c:set var="filaStyle" value="text-decoration:line-through"/></c:if>
				<display:column property="identificador" title="Expedient" style="${filaStyle}"/>
				<display:column property="dataInici" title="Iniciat el" format="{0,date,dd/MM/yyyy HH:mm}" style="${filaStyle}"/>
				<display:column property="tipus.nom" title="Tipus" style="${filaStyle}"/>
				<display:column title="Estat" style="${filaStyle}" sortProperty="estat.nom">
					<c:if test="${registre.aturat}"><img src="<c:url value="/img/stop.png"/>" alt="Aturat" title="Aturat" border="0"/></c:if>
					<c:choose>
						<c:when test="${empty registre.dataFi}">
							<c:choose><c:when test="${empty registre.estat}"><fmt:message key="expedient.consulta.iniciat"/></c:when><c:otherwise>${registre.estat.nom}</c:otherwise></c:choose>
						</c:when>
						<c:otherwise><fmt:message key="expedient.consulta.finalitzat"/></c:otherwise>
					</c:choose>
				</display:column>
			</display:table>
			<form id="massiva" action="consulta.html" class="uniForm" onclick="javascript:massiva(this)">
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit</c:param>
					<c:param name="titles"><fmt:message key="expedient.massiva.canvi"/></c:param>
				</c:import>
			</form>
		</div>
	</div>
	<div class="uniForm">
		<div class="inlineLabels col first">
			<div class="ctrlHolder" style="height:45px;">
				<label for="inici"><fmt:message key="expedient.consulta.datahorainici"/></label>
				<input id="inici" name="inici" type="text" class="textInput" <c:if test="${not empty param.inici}">value="${param.inici}"</c:if>/>
			</div>
			<h3 class="titol-tab titol-canvi-versio mass"><fmt:message key="expedient.massiva.actualitzar"/></h3>
			<form:form action="massivaCanviVersio.html" cssClass="" commandName="canviVersioProcesCommand" onsubmit="return confirmarCanviVersio(event)" onclick="javascript:massiva(this)">
				<input type="hidden" id="ver_inici" name="inici">
				<input type="hidden" id="ver_correu" name="correu">
				<div class="inlineLabels">
					<c:set var="definicionsProces" value="${definicioProces.jbpmIdsAmbDescripcio}" scope="request"/>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="definicioProcesId"/>
						<c:param name="type" value="select"/>
						<c:param name="items" value="definicionsProces"/>
						<c:param name="itemLabel" value="descripcio"/>
						<c:param name="itemValue" value="jbpmId"/>
						<c:param name="label">${definicioProces.jbpmKey}<%--(<fmt:message key="expedient.massiva.proces.principal"/>) --%></c:param>
					</c:import>
					<c:if test="${not empty subDefinicioProces}">
						<div class="ctrlHolder">
							<h4 style="font-weight: bold;"><fmt:message key="expedient.massiva.subprocessos"/>:</h4>
						</div>
						<c:forEach var="subProces" items="${subDefinicioProces}">
							<c:set var="subDefinicionsProces" value="${subProces.jbpmIdsAmbDescripcio}" scope="request"/>
							<c:import url="../common/formElement.jsp">
								<c:param name="property" value="subprocesId"/>
								<c:param name="type" value="select"/>
								<c:param name="items" value="subDefinicionsProces"/>
								<c:param name="itemLabel" value="descripcio"/>
								<c:param name="itemValue" value="jbpmId"/>
								<c:param name="itemBuit">&lt;&lt; <fmt:message key='expedient.consulta.select.def_proces'/> &gt;&gt;</c:param>
								<c:param name="label">${subProces.jbpmKey}</c:param>
							</c:import>
						</c:forEach>
					</c:if>
				</div>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit</c:param>
					<c:param name="titles"><fmt:message key="comuns.canviar_versio"/></c:param>
				</c:import>
			</form:form>
	
			<h3 class="titol-tab titol-canvi-versio mass"><fmt:message key="expedient.massiva.executarScriptMas"/></h3>
			<c:set var="tePermisExecutarScript" value="${false}"/>
			<security:accesscontrollist domainObject="${instanciaProces.expedient.tipus}" hasPermission="16,2"><c:set var="tePermisAccions" value="${true}"/></security:accesscontrollist>
			<c:if test="${tePermisAccions}">
				<form:form action="scriptMas.html" cssClass="uniForm" commandName="scriptCommandMas" onsubmit="return confirmarScript(event)" onclick="javascript:massiva(this)">
						
						<div class="inlineLabels">
							<input type="hidden" name="id" value="${instanciaProces.id}"/>
							<input type="hidden" id="scr_inici" name="inici">
							<input type="hidden" id="scr_correu" name="correu">
							<c:import url="../common/formElement.jsp">
								<c:param name="property">script</c:param>
								<c:param name="required">${true}</c:param>
								<c:param name="type" value="textarea"/>
								<c:param name="label"><fmt:message key='expedient.eines.script' /></c:param>
							</c:import>
						</div>
						<c:import url="../common/formElement.jsp">
							<c:param name="type" value="buttons"/>
							<c:param name="values">submit</c:param>
							<c:param name="titles"><fmt:message key='comuns.executar' /></c:param>
						</c:import>
					</form:form>
			</c:if>
		
			<h3 class="titol-tab titol-canvi-versio mass"><fmt:message key='expedient.massiva.aturar' /></h3>
			<form:form action="aturarMas.html" cssClass="uniForm" commandName="aturarCommandMas" onsubmit="return confirmarAturar(event)" onclick="javascript:massiva(this)">
				<div class="inlineLabels">
					<input type="hidden" name="id" value="${instanciaProces.id}"/>
					<input type="hidden" id="atu_inici" name="inici">
					<input type="hidden" id="atu_correu" name="correu">
					<c:import url="../common/formElement.jsp">
						<c:param name="property">motiu</c:param>
						<c:param name="required">${true}</c:param>
						<c:param name="type" value="textarea"/>
						<c:param name="label"><fmt:message key='expedient.eines.motiu' /></c:param>
					</c:import>
				</div>
					<c:import url="../common/formElement.jsp">
						<c:param name="type" value="buttons"/>
						<c:param name="values">submit</c:param>
						<c:param name="titles"><fmt:message key='comuns.aturar' /></c:param>
					</c:import>
			</form:form>
		</div>
		<div class="inlineLabels uniForm col last">
			<c:set var="hiHaAccions" value="${false}"/>
			<c:if test="${not empty instanciaProces.definicioProces.accions}"><c:set var="hiHaAccions" value="${fn:length(instanciaProces.definicioProces.accions) > 0}"/></c:if>
			
			<div class="ctrlHolder" style="height:45px;">
				<label for="correu"><fmt:message key="expedient.massiva.correu"/></label>
				<input id="correu" name="correu" type="checkbox"  <c:if test="${not empty param.correu and param.correu == 'true'}">checked="checked"</c:if>/>
			</div>
			
			<h3 class="titol-tab titol-canvi-versio mass"><fmt:message key="expedient.massiva.accions"/></h3>
			<c:if test="${hiHaAccions}">
				<c:set var="hiHaAccionsPubliques" value="${false}"/>
				<c:forEach var="accio" items="${instanciaProces.definicioProces.accions}">
					<c:if test="${not accio.oculta}">
						<c:set var="hiHaAccions" value="${true}"/>
						<c:if test="${accio.publica}"><c:set var="hiHaAccionsPubliques" value="${true}"/></c:if>
					</c:if>
				</c:forEach>
				<c:set var="tePermisAccions" value="${false}"/>
				<security:accesscontrollist domainObject="${instanciaProces.expedient.tipus}" hasPermission="16,2"><c:set var="tePermisAccions" value="${true}"/></security:accesscontrollist>
				<c:if test="${hiHaAccionsPubliques || (hiHaAccions && tePermisAccions)}">
					<form:form action="massivaExecutarAccio.html" cssClass="" commandName="execucioAccioCommand" onsubmit="return confirmarExecutarAccio(event)" onclick="javascript:massiva(this)">
						<input type="hidden" id="acc_inici" name="inici">
						<input type="hidden" id="acc_correu" name="correu">
						<div class="inlineLabels">
							<c:set var="accio" value="${instanciaProces.definicioProces.accions}" scope="request"/>
							<c:import url="../common/formElement.jsp">
								<c:param name="property" value="accioId"/>
								<c:param name="type" value="select"/>
								<c:param name="items" value="accio"/>
								<c:param name="itemLabel" value="nom"/>
								<c:param name="itemValue" value="codi"/>
								<c:param name="label"><fmt:message key="expedient.massiva.exec_accio"/></c:param>
							</c:import>
						</div>
						<c:import url="../common/formElement.jsp">
							<c:param name="type" value="buttons"/>
							<c:param name="values">submit</c:param>
							<c:param name="titles"><fmt:message key='comuns.executar' /></c:param>
						</c:import>
					</form:form>
				</c:if>
			</c:if>
			<c:if test="${not hiHaAccions}">
				<br>
				<fmt:message key="expedient.document.info.senseaccions"/>
				<br>
			</c:if>
			
			<h3 class="titol-tab titol-canvi-versio mass"><fmt:message key='expedient.massiva.modificar_variables' /></h3>
			<form:form action="dadaModificarMas.html" method="GET" cssClass="uniForm" commandName="modificarVariablesMasCommand" onsubmit="return confirmarModificarVariables(event)" onclick="javascript:massiva(this)">
				<div class="inlineLabels">
					<input type="hidden" name="id" value="${instanciaProces.id}"/>
					<input type="hidden" id="var_inici" name="inici"/>
					<input type="hidden" id="var_correu" name="correu"/>
					<input type="hidden" id="var_start" name="start" value="true"/>
<%-- 					<c:set var="variables" value="${instanciaProces.camps}" scope="request"/> --%>
						<c:import url="../common/formElement.jsp">
							<c:param name="property">var</c:param>
							<c:param name="type" value="select"/>
							<c:param name="items" value="variables"/>
							<c:param name="itemLabel" value="codi"/>
							<c:param name="itemValue" value="codi"/>
							<c:param name="itemBuit">&lt;&lt; <fmt:message key='expedient.consulta.select.variable'/> &gt;&gt;</c:param>
							<c:param name="label"><fmt:message key="expedient.eines.modificar_variables"/></c:param>
							<c:param name="onchange">changeVar()</c:param>
						</c:import>
						<c:import url="../common/formElement.jsp">
							<c:param name="type" value="buttons"/>
							<c:param name="values">subvar</c:param>
							<c:param name="titles"><fmt:message key='comuns.modificar'/></c:param>
						</c:import>
				</div>
			</form:form>
			
			<h3 class="titol-tab titol-canvi-versio mass"><fmt:message key="expedient.massiva.documents"/></h3>
			<c:if test="${not empty documents}">
				<form:form action="documentModificarMas.html" cssClass="uniForm" method="GET" commandName="documentCommandForm" onsubmit="return confirmarModificarDocument(event)" onclick="javascript:massiva(this)">
					<input type="hidden" name="id" value="${instanciaProces.id}"/>
					<input type="hidden" id="doc_inici" name="inici">
					<input type="hidden" id="doc_correu" name="correu">
					<input type="hidden" id="doc_start" name="start" value="true"/>
					<div class="inlineLabels">
						<input type="hidden" id="docId" name="docId"/>
						<c:set var="documents" value="${documents}" scope="request"/>
						<c:import url="../common/formElement.jsp">
							<c:param name="property">nom</c:param>
							<c:param name="type" value="select"/>
							<c:param name="items" value="documents"/>
							<c:param name="itemLabel" value="nom"/>
							<c:param name="itemValue" value="id"/>
							<c:param name="itemBuit">&lt;&lt; <fmt:message key='expedient.consulta.select.document'/> &gt;&gt;</c:param>
							<c:param name="label"><fmt:message key="expedient.massiva.documents"/></c:param>
							<c:param name="onchange">changeDoc()</c:param>
						</c:import>
					</div>
					<c:import url="../common/formElement.jsp">
						<c:param name="type" value="buttons"/>
						<c:param name="values">subdoc,generar,delete,adjunt</c:param>
						<c:param name="titles"><fmt:message key='comuns.modificar' />,<fmt:message key='tasca.doc.generar' />,<fmt:message key='comuns.esborrar' />,<fmt:message key='expedient.document.adjuntar_document_massiu' /></c:param>
					</c:import>
				</form:form>
			</c:if>
			<c:if test="${empty documents}">
			<br>
				<fmt:message key="expedient.document.info.sensedocuments"/>
			<br>
			</c:if>	
			
			<h3 class="titol-tab titol-canvi-versio mass"><fmt:message key='expedient.eines.reindexar.expedients' /></h3>
			<form:form action="reindexarMas.html" cssClass="uniForm" onsubmit="return confirmarReindexar(event)"  onclick="javascript:massiva(this)">
				<input type="hidden" id="idx_inici" name="inici">
				<input type="hidden" id="idx_correu" name="correu">
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit</c:param>
					<c:param name="titles"><fmt:message key='comuns.reindexar' /></c:param>
				</c:import>
			</form:form>
		</div>
	</div>
					
</body>


</html>
