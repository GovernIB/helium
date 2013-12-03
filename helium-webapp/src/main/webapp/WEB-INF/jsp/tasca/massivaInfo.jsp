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
function confirmarModificarVariables(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	programacio();
	return confirm("<fmt:message key='expedient.eines.confirm_modificar_variable' />");
}
function confirmarReassignar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	programacio();
	return confirm("<fmt:message key='expedient.eines.confirm_reassignar_expedients' />");
}
function programacio(){
	var inici = $("#inici").val();
	var correu = $("#correu").is(":checked") ? true : false;
	$("input[type='hidden'][name='inici']").each(function(){ $(this).val(inici); });
	$("input[type='hidden'][name='correu']").each(function(){ $(this).val(correu); });
}
function changeVar(){
	var varId = $("#var0").val();
	
	if (varId != "") {
	 	$("button[value='subvar']").prop('disabled', false);
	} else {
		$("button[value='subvar']").prop('disabled', true);
	}
}

function changeTipus() {
	var tipus = $("#tipusExpressio").val();
	if (tipus == "user") {
		$("#duser").show();
		$("#dgrup").hide();
		$("#dexpr").hide();
	} else if (tipus == "grup") {
		$("#duser").hide();
		$("#dgrup").show();
		$("#dexpr").hide();
	} else {
		$("#duser").hide();
		$("#dgrup").hide();
		$("#dexpr").show();
	} 
}

function getTasques() {
	var defProcId = $("#defProcId0").val();
	var tasques = '<option value="">&lt;&lt; Seleccioni una tasca &gt;&gt;</option>\n';
	if (defProcId != "") {
		$.ajax({
			url: "/helium/expedient/getTasques.html",
			dataType: 'json',
			data: {definicioProces: defProcId},
			async: false,
			success: function(data){
				for (var i = 0; i < data.length; i++) {
					tasca = data[i];
					tasques += '<option value="' + tasca.id + '">' + tasca.nom + '</option>\n';
				}
			}
		})
		//.done(function() { console.log( "second success" ); })
		.fail(function( jqxhr, textStatus, error ) {
			var err = textStatus + ', ' + error;
			console.log( "Request Failed: " + err);
		})
		//.always(function() {
		//	$("body").removeClass("loading");
		//});
	}
	$("#tasca0").html(tasques);
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
	changeTipus();
	//getTasques();
});
// ]]>
</script>
</head>
<body>
	<div class="missatgesBlau">
		<h3 class="titol-tab titol-massiva"><fmt:message key="tasca.info.massiva"/> <img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="<fmt:message key="tasca.info.mos_ocul"/>" title="<fmt:message key="tasca.info.mos_ocul"/>" border="0" onclick="mostrarOcultar(this,'info-massiva')"/></h3>
		<div id="info-massiva" style="display:none">
			<display:table name="tasques" id="registre" requestURI="" class="displaytag selectable">
				<display:column property="titol" titleKey="tasca.pllistat.tasca"/>
				<display:column sortProperty="expedientTitol" titleKey="tasca.pllistat.expedient" sortable="true">
					<a href="<c:url value="/expedient/info.html"><c:param name="id" value="${registre.expedientProcessInstanceId}"/></c:url>">${registre.expedientTitol}</a>
				</display:column>
				<display:column property="expedientTipusNom" titleKey="comuns.tipus_exp" sortable="true"/>
				<display:column property="dataCreacio" titleKey="tasca.pllistat.creada_el" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true"/>
				<display:column titleKey="tasca.pllistat.prioritat" sortable="true">
					<c:choose>
						<c:when test="${registre.prioritat == 2}"><fmt:message key='tasca.pllistat.m_alta' /></c:when>
						<c:when test="${registre.prioritat == 1}"><fmt:message key='tasca.pllistat.alta' /></c:when>
						<c:when test="${registre.prioritat == 0}"><fmt:message key='tasca.pllistat.normal' /></c:when>
						<c:when test="${registre.prioritat == -1}"><fmt:message key='tasca.pllistat.baixa' /></c:when>
						<c:when test="${registre.prioritat == -2}"><fmt:message key='tasca.pllistat.m_baixa' /></c:when>
						<c:otherwise>${registre.prioritat}</c:otherwise>
					</c:choose>
				</display:column>
				<c:choose>
					<c:when test="${not empty terminisIniciats[registre_rowNum - 1] and terminisIniciats[registre_rowNum - 1].estat == 'NORMAL'}"><c:set var="estilData">color:white;background-color:green</c:set></c:when>
					<c:when test="${not empty terminisIniciats[registre_rowNum - 1] and terminisIniciats[registre_rowNum - 1].estat == 'AVIS'}"><c:set var="estilData">color:white;background-color:orange</c:set></c:when>
					<c:when test="${not empty terminisIniciats[registre_rowNum - 1] and terminisIniciats[registre_rowNum - 1].estat == 'CADUCAT'}"><c:set var="estilData">color:white;background-color:red</c:set></c:when>
					<c:otherwise><c:set var="estilData"></c:set></c:otherwise>
				</c:choose>
				<display:column property="dataLimit" titleKey="tasca.pllistat.data_limit" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true" style="${estilData}"/>
			</display:table>
			<form action="consultaTasques.html" class="uniForm">
				<input type="hidden" name="id" value="${param.id}"/>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit</c:param>
					<c:param name="titles"><fmt:message key="common.tabstasca.massiva.canvi"/></c:param>
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
			<h3 class="titol-tab titol-canvi-versio mass"><fmt:message key="expedient.eines.reassignar.expedients"/></h3>
			<form:form action="massivaReassignar.html" cssClass="" commandName="reassignarCommand" onsubmit="return confirmarReassignar(event)">
				<input type="hidden" id="ver_inici" name="inici">
				<input type="hidden" id="ver_correu" name="correu">
				<div class="inlineLabels">
					<div class="ctrlHolder">
						<label for="tipusExpressio"><fmt:message key='expedient.eines.reassignar.tipus'/></label>
						<select id="tipusExpressio" name="tipusExpressio" onclick="" onchange="javascript:changeTipus()">
							<option value="user"><fmt:message key='filtre.expressio.usuari'/></option>
							<option value="grup"><fmt:message key='filtre.expressio.grup'/></option>
							<option value="expr"><fmt:message key='filtre.expressio.expressio'/></option>
						</select>
					</div>
					<div id="duser">
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="usuari"/>
							<c:param name="required" value="true"/>
							<c:param name="type" value="suggest"/>
							<c:param name="label"><fmt:message key='filtre.expressio.usuari' /></c:param>
							<c:param name="suggestUrl"><c:url value="/persona/suggest.html"/></c:param>
							<c:param name="suggestText">${usuari}</c:param>
							<c:param name="classHolder">reamass</c:param>
						</c:import>
					</div>
					<div id="dgrup">
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="grup"/>
							<c:param name="required" value="true"/>
							<c:param name="type" value="suggest"/>
							<c:param name="label"><fmt:message key='filtre.expressio.grup' /></c:param>
							<c:param name="suggestUrl"><c:url value="/area/suggest.html"/></c:param>
							<c:param name="suggestText">${grup}</c:param>
							<c:param name="classHolder">reamass</c:param>
						</c:import>
					</div>
					<div id="dexpr">
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="expression"/>
							<c:param name="required" value="true"/>
							<c:param name="label"><fmt:message key='filtre.expressio.expressio' /></c:param>
						</c:import>
					</div>
				</div>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit</c:param>
					<c:param name="titles"><fmt:message key="comuns.reassignar"/></c:param>
				</c:import>
			</form:form>
		</div>
		<div class="inlineLabels uniForm col last">
			<div class="ctrlHolder" style="height:45px;">
				<label for="correu"><fmt:message key="expedient.massiva.correu"/></label>
				<input id="correu" name="correu" type="checkbox"  <c:if test="${not empty param.correu and param.correu == 'true'}">checked="checked"</c:if>/>
			</div>
		</div>
	</div>
</body>
</html>
