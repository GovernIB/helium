<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.List"%>
<c:set var="sessionCommand" value="${sessionScope.consultaExpedientsCommand}"/>

<html>
<head>
	<title><fmt:message key="expedient.consulta.cons_general"/></title>
	<meta name="titolcmp" content="<fmt:message key="comuns.consultes"/>" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/gisDwrService.js"/>"></script>
<script type="text/javascript">
// <![CDATA[
function refrescarEstats(element) {
	var estatActual = $("select#estat0").val();
	$.ajax({
	    url:"consultaEstats.html?id=" + element.value,
	    type:'GET',
	    dataType: 'json',
	    cache: false,
	    beforeSend: function(msg){
	    	var options = '<option value=""><fmt:message key="js.helforms.carreg_dades"/></option>';
	    	$("select#estat0").html(options).attr('class', 'inlineLabels');
		},
	    success: function(json) {
	    	var options = '';
		    options += '<option value="">&lt;&lt; <fmt:message key="expedient.consulta.select.estat"/> &gt;&gt;</option>';
	        for (var i = 0; i < json.length; i++) {
	        	if (json[i].id == estatActual)
	        		options += '<option value="' + json[i].id + '" selected="selected">' + json[i].nom + '</option>';
	        	else
	        		options += '<option value="' + json[i].id + '">' + json[i].nom + '</option>';
	        }
	        $("select#estat0").html(options).attr('class', 'inlineLabels');
	    },
	    error: function(jqXHR, textStatus, errorThrown) {
	    	console.log("Error al actualitzar la llista d'estats: [" + textStatus + "] " + errorThrown);

	    	var options = '<option value="">&lt;&lt; <fmt:message key="expedient.consulta.select.estat"/> &gt;&gt;</option>';
	    	$("select#estat0").html(options).attr('class', 'inlineLabels');
	    }
	});
	$.ajax({
	    url:"consultaPermis.html?id=" + element.value,
	    type:'GET',
	    dataType: 'json',
	    success: function(json) {
	    	if (json.permis == true) {
	    		$(".anulats").removeClass('ocult');
	        } else {
	        	$(".anulats").addClass('ocult');
	        	$(".anulats option:eq(0)").prop('selected', true);
	        }
	    },
	    error: function(jqXHR, textStatus, errorThrown) {
	    	console.log("Error al obtenir els permisos del tipus d'expedient: [" + textStatus + "] " + errorThrown);
	    }
	});
	$.ajax({
	    url:"consultaPermis.html?id=" + element.value,
	    type:'GET',
	    dataType: 'json',
	    success: function(json) {
	    	if (json.permis == true) {
	    		$(".anulats").removeClass('ocult');
	        } else {
	        	$(".anulats").addClass('ocult');
	        	$(".anulats option:eq(0)").prop('selected', true);
	        }
	    },
	    error: function(jqXHR, textStatus, errorThrown) {
	    	console.log("Error al obtenir els permisos del tipus d'expedient: [" + textStatus + "] " + errorThrown);
	    }
	});
}

function refrescarSelTots(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	if(e.value == '') {
		$("#ejecucionMasivaTotsTipus").hide();
	} else {
		$("#ejecucionMasivaTotsTipus").show();
	}
}

function confirmarEsborrar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key="expedient.consulta.confirm.esborrar"/>");
}

function confirmarAnular(e, registre) {
	var resposta="";
	$("#id").val(registre);
	var e = e || window.event;
	e.cancelBubble = true;

	var confirmaAnula = confirm("<fmt:message key="expedient.consulta.confirm.anular"/>"); 
 	if (confirmaAnula){	
 		resposta = prompt("Introdueix el motiu de l'anul·lació",'');
 		$("#motiu").val(resposta);

 		if(resposta != null){
 	 		document.forms["anularMot"].submit();
 	 	}
 	} 	
 	
 	if (e.stopPropagation) e.stopPropagation(); 		
}

$(function() {
	$( "#dialog-error-user" ).dialog({
		autoOpen: false,
		height: 120,
		width: 1000,
		modal: true,
		resizable: true
	});
	$( "#dialog-error-admin" ).dialog({
		autoOpen: false,
		height: 480,
		width: 1000,
		modal: true,
		resizable: true
	});
});

function alertaErrorUser(e, desc) {
	var e = e || window.event;
	e.cancelBubble = true;
	
	var text = desc + "<br/><br/>Póngase en contacto con el responsable del expediente.";
	$("#dialog-error-user").html(text);
	$("#dialog-error-user").data('title.dialog', 'Error en la ejecución del expediente'); 
	$("#dialog-error-user").dialog( "open" );
	if (e.stopPropagation) e.stopPropagation();
	
	return false;
}

function alertaErrorAdmin(e, id, desc, full) {
	var e = e || window.event;
	e.cancelBubble = true;

	var text = desc + "<br/><br/>Póngase en contacto con el responsable del expediente.";
	$("#dialog-error-admin").html(text+"<br/><br/>"+full+$("#dialog-error-admin").html());
	$("#processInstanceId").val(id);
	$("#dialog-error-admin").data('title.dialog', 'Error en la ejecución del expediente'); 
	$("#dialog-error-admin").dialog( "open" );

	if (e.stopPropagation) e.stopPropagation();
	
	return false;
}

function confirmarDesAnular(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key="expedient.consulta.confirm.desanular"/>");	
}

function obreVisorGis() {
    var sUrl;
	var piis = new Array();
	<c:forEach items="${piis}" var="pii">
		piis.push('${pii}');
	</c:forEach>
	gisDwrService.urlVisor(
			{callback: function(url) {
				sUrl = url;
			},
			async: false
		});
    gisDwrService.xmlExpedients(
    		piis,
    		{callback: function(sXML) {
    			var form = document.createElement("form");
    			form.setAttribute("method", "post");
    		    form.setAttribute("action", sUrl);
    		    form.setAttribute("target", "visor");
    		  
    			var input = document.createElement('input');
    		    input.type = 'hidden';
    		    input.name = 'xmlexpedients';
    		    input.value = sXML;

    			form.appendChild(input);
    			document.body.appendChild(form);
    		  
    		  	//note I am using a post.htm page since I did not want to make double request to the page 
    		    //it might have some Page_Load call which might screw things up.
    		    //window.open("post.htm", name, windowoption);
    			
    			form.submit();
    			document.body.removeChild(form);
    		},
			async: true
    	});
}
function clicCheckMassiu(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	var ids = new Array();
	ids.push((e.target || e.srcElement).value);
	$.post("massivaIds.html", {
		expedientTipusId: "${command.expedientTipus.id}",
		expedientId: ids,
		checked: (e.target || e.srcElement).checked
	});
}
function selTots(){
	var ch = $("#selTots:checked").val();
	var ids = new Array();
	$("#registre input[type='checkbox'][name='expedientId']").each(function() {
		ids.push($(this).val());
	}).attr("checked", (!ch) ? false : true);
	$.post("massivaIds.html", {
		expedientTipusId: "${command.expedientTipus.id}",
		expedientId: ids,
		checked: (!ch) ? false : true
	});
}

// ]]>
</script>
</head>
<body>
	<div id="dialog-error-user" title="Error" style="display:none">
	</div>
	<div id="dialog-error-admin" title="Error" style="display:none">
		<form method="POST" action="<c:url value="/expedient/limpiarTrazaError.html"/>">
			<input id="processInstanceId" name="processInstanceId" value="" type="hidden"/>
			<button type="submit" class="submitButton right"><fmt:message key="expedient.consulta.netejar"/></button>
		</form>
	</div>

	<form:form action="consulta.html" cssClass="uniForm">
		<div class="inlineLabels col first">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="titol"/>
				<c:param name="label"><fmt:message key="expedient.consulta.titol"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="numero"/>
				<c:param name="label"><fmt:message key="expedient.consulta.numero"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="dataInici1"/>
				<c:param name="type" value="custom"/>
				<c:param name="label"><fmt:message key="expedient.consulta.datainici"/></c:param>
				<c:param name="customClass">customField</c:param>
				<c:param name="content">
					<spring:bind path="dataInici1">
						<label for="dataInici1" class="blockLabel">
							<span><fmt:message key="expedient.consulta.entre"/></span>
							<input id="dataInici1" name="dataInici1" value="${status.value}" type="text" class="textInput"/>
							<script type="text/javascript">
								// <![CDATA[
								$(function() {
									$.datepicker.setDefaults($.extend({
										dateFormat: 'dd/mm/yy',
										changeMonth: true,
										changeYear: true
									}));
									$("#dataInici1").datepicker({firstDay: 1});
								});
								// ]]>
							</script>
						</label>
					</spring:bind>
					<spring:bind path="dataInici2">
						<label for="dataInici2" class="blockLabel blockLabelLast">
							<span><fmt:message key="expedient.consulta.i"/></span>
							<input id="dataInici2" name="dataInici2" value="${status.value}" type="text" class="textInput"/>
							<script type="text/javascript">
								// <![CDATA[
								$(function() {
									$.datepicker.setDefaults($.extend({
										dateFormat: 'dd/mm/yy',
										changeMonth: true,
										changeYear: true
									}));
									$("#dataInici2").datepicker({firstDay: 1});
								});
								// ]]>
							</script>
						</label>
					</spring:bind>
				</c:param>
			</c:import>
		</div>
		<div class="inlineLabels col last">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="expedientTipus"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="expedientTipus"/>
				<c:param name="itemLabel" value="nom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key="expedient.consulta.select.tipusexpedient"/> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key="expedient.consulta.tipusexpedient"/></c:param>
				<c:param name="onchange">
					refrescarEstats(this);
					<security:accesscontrollist domainObject="${entornActual}" hasPermission="16,2">
						refrescarSelTots(this);
					</security:accesscontrollist>
				</c:param>
			</c:import>
			
			<security:accesscontrollist domainObject="${entornActual}" hasPermission="16,2">
				<button id="ejecucionMasivaTotsTipus" style='<c:if test="${command.expedientTipus.id == null}">display: none;</c:if> float: right; margin-right: 10px' type="button" class="submitButton" onclick="location.href = '<c:url value="/expedient/massivaInfo.html?expedientTipusId="/>'+$('#expedientTipus0').val()"><fmt:message key="expedient.consulta.massiva.accions.totsTipus"/></button>
			</security:accesscontrollist>
			
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="estat"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="estats"/>
				<c:param name="itemLabel" value="nom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key="expedient.consulta.select.estat"/> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key="expedient.consulta.estat"/></c:param>
			</c:import>
			<c:if test="${globalProperties['app.georef.actiu']}">
				<c:choose>
					<c:when test="${globalProperties['app.georef.tipus']=='ref'}">
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="geoReferencia"/>
							<c:param name="label"><fmt:message key="comuns.georeferencia.codi"/></c:param>
						</c:import>
					</c:when>
					<c:otherwise>
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="geoPosX"/>
							<c:param name="type" value="custom"/>
							<c:param name="label"><fmt:message key="comuns.georeferencia.coordenades"/></c:param>
							<c:param name="customClass">customField</c:param>
							<c:param name="content">
								<spring:bind path="geoPosX">
									<label for="geoPosX" class="blockLabel">
										<span><fmt:message key="comuns.georeferencia.coordX"/></span>
										<input id="geoPosX" name="geoPosX" value="${status.value}" type="text" class="textInput"/>
									</label>
								</spring:bind>
								<spring:bind path="geoPosY">
									<label for="geoPosY" class="blockLabel blockLabelLast">
										<span><fmt:message key="comuns.georeferencia.coordY"/></span>
										<input id="geoPosY" name="geoPosY" value="${status.value}" type="text" class="textInput"/>
									</label>
								</spring:bind>
							</c:param>
						</c:import>
					</c:otherwise>
				</c:choose>
			</c:if>
			
			<c:set var="tePermis" value="${false}"/>
			<security:accesscontrollist domainObject="${entornActual}" hasPermission="16">
				<c:set var="tePermis" value="${true}"/>
			</security:accesscontrollist>
			<c:if test="${not empty command.expedientTipus}">
				<security:accesscontrollist domainObject="${command.expedientTipus}" hasPermission="16,2">
					<c:set var="tePermis" value="${true}"/>
				</security:accesscontrollist>
			</c:if>
			<c:if test="${tePermis == false}">
				<c:set var="visible" value=' ocult'/>
			</c:if>
			
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="mostrarAnulats"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="filtreAnulats"/>
				<c:param name="itemLabel" value="codi"/>
				<c:param name="itemLabelMsg" value="true"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="label"><fmt:message key="expedient.consulta.anulats.mostrar"/></c:param>
				<c:param name="classHolder" value="anulats${visible}"/>		
			</c:import>
			
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">submit,clean</c:param>
				<c:param name="titles"><fmt:message key="expedient.consulta.consultar"/>,<fmt:message key="expedient.consulta.netejar"/></c:param>
			</c:import>
				
		</div>
		<div class="ctrlHolder">
		<c:set var="opp"><c:if test='${empty objectsPerPage}'>20</c:if><c:if test='${not empty objectsPerPage}'>${objectsPerPage}</c:if></c:set>
		<c:set var="copp" value="opp-llista"/>
		<c:choose>
			<c:when test="${globalProperties['app.georef.actiu'] && globalProperties['app.gis.plugin.actiu']}">
				<c:set var="copp" value="opp-llista-gis"/>
			</c:when>
			<c:otherwise> 
				<c:if test="${command.massivaActiu}">
					<c:set var="copp" value="opp-llista-mas"/>
				</c:if>
			</c:otherwise>
		</c:choose>
		<c:if test="${not empty llistat}">
			<select id="objectsPerPage" name="objectsPerPage" class="objectsPerPage<c:if test='${not empty llistat}'> ${copp}</c:if>">
				<option value="10"<c:if test='${opp == "10"}'> selected="selected"</c:if>>10</option>
				<option value="20"<c:if test='${opp == "20"}'> selected="selected"</c:if>>20</option>
				<option value="50"<c:if test='${opp == "50"}'> selected="selected"</c:if>>50</option>
				<option value="100"<c:if test='${opp == "100"}'> selected="selected"</c:if>>100</option>
				<option value="999999999"<c:if test='${opp == "999999999"}'> selected="selected"</c:if>>Tots</option>
			</select>
			<label for="objectsPerPage" class="objectsPerPage<c:if test='${not empty llistat}'> ${copp}</c:if>"><fmt:message key="comuns.objectsPerPage"/></label>
		</c:if>
	</div>
	</form:form><div style="clear:both"></div><br/>

	<c:if test="${not empty sessionCommand}">
		<c:if test="${globalProperties['app.georef.actiu'] && globalProperties['app.gis.plugin.actiu']}">
			<div>
			<table>
				<tr id="consTR">
					<td>
						<c:import url="../common/formElement.jsp">
							<c:param name="type" value="buttons"/>
							<c:param name="values">gis</c:param>
							<c:param name="titles"><fmt:message key="expedient.consulta.gis"/></c:param>
							<c:param name="onclick">obreVisorGis()</c:param>
						</c:import>
					</td>
					<td>
						<c:if test="${command.massivaActiu}">
							<form action="<c:url value="/expedient/massivaInfo.html"/>">
								<button type="submit" class="submitButton"><fmt:message key="expedient.consulta.massiva.accions"/></button>
							</form>
						</c:if>
					</td>
				</tr>
			</table>
			</div>

		</c:if>
		<c:if test="${!globalProperties['app.gis.plugin.actiu']}">
			<c:if test="${command.massivaActiu}">
				<table>
					<tr id="consTR">
						<td>
							<label><fmt:message key="expedient.consulta.massiva.accions"/></label>
						</td>
						<td>
							<form id="massivaInfoForm" action="<c:url value="/expedient/massivaInfo.html"/>">
								<input type="hidden" id="massivaInfoTots" name="massivaInfoTots" value="0"/>
								<button type="button" onclick="$('#massivaInfoTots').val(0);$('#massivaInfoForm').submit()" class="submitButton"><fmt:message key="expedient.consulta.massiva.accions.sel"/></button>
								<button type="button" class="submitButton" onclick="$('#massivaInfoTots').val(1);$('#massivaInfoForm').submit()"><fmt:message key="expedient.consulta.massiva.accions.tots"/></button>
							</form>
						</td>
					</tr>
				</table>
			</c:if>
		</c:if>
		<br>
		<c:if test="${not empty llistat}">
			<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable" sort="external">
				<c:set var="filaStyle" value=""/>
				<input type="hidden" id="tots" value=""/>
				<c:if test="${registre.anulat}"><c:set var="filaStyle" value="text-decoration:line-through"/></c:if>
				<c:if test="${command.massivaActiu}">
					<display:column title="<input id='selTots' type='checkbox' value='false' onclick='selTots()'>" style="${filaStyle}" >
						<c:set var="expedientSeleccionat" value="${false}"/>
						<c:forEach var="eid" items="${sessionScope.consultaExpedientsIdsMassius}" varStatus="status">
							<c:if test="${status.index gt 0 and eid == registre.id}"><c:set var="expedientSeleccionat" value="${true}"/></c:if>
						</c:forEach>
						<input type="checkbox" name="expedientId" value="${registre.id}"<c:if test="${expedientSeleccionat}"> checked="checked"</c:if> onclick="clicCheckMassiu(event)"/>
					</display:column>
				</c:if>
				<display:column title="Expedient" sortProperty="identificador" url="/tasca/personaLlistat.html" paramId="exp" paramProperty="identificador" sortable="true" style="${filaStyle}">
					<c:if test="${registre.errorsIntegracions}"><img src="<c:url value="/img/exclamation.png"/>" alt="<fmt:message key="expedient.consulta.error.integracions"/>" title="<fmt:message key="expedient.consulta.error.integracions"/>" border="0"/></c:if>
					${registre.identificador}
				</display:column>
				<display:column property="dataInici" title="Iniciat el" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true" style="${filaStyle}"/>
				<display:column property="tipus.nom" title="Tipus" sortable="true" style="${filaStyle}"/>
				<display:column title="Estat" style="${filaStyle}" sortable="true" sortProperty="estat.nom">
					<c:if test="${registre.aturat}"><img src="<c:url value="/img/stop.png"/>" alt="Aturat" title="Aturat" border="0"/></c:if>
					<c:choose>
						<c:when test="${empty registre.dataFi}">
							<c:choose><c:when test="${empty registre.estat}"><fmt:message key="expedient.consulta.iniciat"/></c:when><c:otherwise>${registre.estat.nom}</c:otherwise></c:choose>
						</c:when>
						<c:otherwise><fmt:message key="expedient.consulta.finalitzat"/></c:otherwise>
					</c:choose>
				</display:column>
				<display:column>
					<security:accesscontrollist domainObject="${registre.tipus}" hasPermission="16,1,128">
						<a href="<c:url value="/expedient/info.html"><c:param name="id" value="${registre.processInstanceId}"/></c:url>"><img src="<c:url value="/img/information.png"/>" alt="<fmt:message key="comuns.informacio"/>" title="<fmt:message key="comuns.informacio"/>" border="0"/></a>
					</security:accesscontrollist>
					<security:accesscontrollist domainObject="${registre.tipus}" hasPermission="16,32">
						<c:set var="permisosAdmin" value="${true}"/>
						<c:if test="${not empty registre.errorDesc}">
							<a href="javascript:void(0);" onclick="return alertaErrorAdmin(event, ${registre.processInstanceId}, '${registre.errorDesc}', '${registre.errorFull}')"><img src="<c:url value="/img/mass_error.png"/>" alt="${registre.errorDesc}" title="${registre.errorDesc}" border="0"/></a>
						</c:if>
					</security:accesscontrollist>
					<security:accesscontrollist domainObject="${registre.tipus}" hasPermission="16,32">
						<c:if test="${not empty registre.errorDesc && !permisosAdmin}">
							<a href="javascript:void(0);" onclick="return alertaErrorUser(event, '${registre.errorDesc}')"><img src="<c:url value="/img/mass_error.png"/>" alt="${registre.errorDesc}" title="${registre.errorDesc}" border="0"/></a>
						</c:if>
					</security:accesscontrollist>
				</display:column>
				<display:column>
					<security:accesscontrollist domainObject="${registre.tipus}" hasPermission="16,2">
						<c:if test="${!registre.anulat}">
							<a href="javascript:void(0);" onclick="return confirmarAnular(event, ${registre.id})"><img src="<c:url value="/img/delete.png"/>" alt="<fmt:message key="comuns.anular"/>" title="<fmt:message key="comuns.anular"/>" border="0"/></a>
						</c:if>
						<c:if test="${registre.anulat}">
							<a href="<c:url value="/expedient/desanular.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmarDesAnular(event)"><img src="<c:url value="/img/arrow_undo.png"/>" alt="<fmt:message key="comuns.desanular"/>" title="<fmt:message key="comuns.desanular"/>" border="0"/></a>
						</c:if>
					</security:accesscontrollist>
				</display:column>
				<display:column>
					<security:accesscontrollist domainObject="${registre.tipus}" hasPermission="8">
						<a href="<c:url value="/expedient/delete.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmarEsborrar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key="comuns.esborrar"/>" title="<fmt:message key="comuns.esborrar"/>" border="0"/></a>
					</security:accesscontrollist>
				</display:column>
			</display:table>
			<script type="text/javascript">initSelectable();</script>
		</c:if>
	</c:if>
	<form:form  method="GET" name="anularMot" id="anularMot" action="/helium/expedient/anular.html?id=${registreId}&motiu=${param.motiu}"  cssClass="uniForm">
		<input type="hidden" id="id" name="id" value=""></input>
		<input type="hidden" id="motiu" name="motiu" value=""></input>
	</form:form>
</body>
</html>
