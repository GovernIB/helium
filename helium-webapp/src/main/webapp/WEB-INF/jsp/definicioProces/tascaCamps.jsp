<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	
	<title><fmt:message key='comuns.def_proces' />: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<input id="regId" name="registreId" value="${registre.id}" type="hidden">
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/campsProcesDwrService.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
  
<script type="text/javascript">

$(document).ready(function() {
	
	    // Initialise the table
	    //$("#registre").tableDnD();
	
	$("#modifVar").hide();
	// Inicialitza la taula
    $("#registre").tableDnD({
    	onDragClass: "drag",
    	onDrop: function(table, row) {
        	$("#registre tr:even").removeClass("odd");
        	$("#registre tr:not(:first)").addClass("even");
        	$("#registre tr:odd").removeClass("even");
        	$("#registre tr:odd").addClass("odd");
        	
        	var pos = row.rowIndex - 1;
        	var id= obtenirId(pos);
        	campsProcesDwrService.goToCampTasca(id, pos, {
				callback: function() {
				},
				async: false
			});
    	},
    	onDragStart: function(table, row) {
    			filaMovem = row.rowIndex-1;
			
		}
    });
    $("#registre tr").hover(function() {
        $(this.cells[0]).addClass('showDragHandle');
    }, function() {
        $(this.cells[0]).removeClass('showDragHandle');
    });	
  	$("#registre tr").each(function(){
  	  	$(this).find("td:first").css("padding-left", "22px");
  	});
});
</script>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='defproc.tascacamps.confirmacio' />");
}


function obtenirId(pos){
	if(filaMovem==pos){
		
		var fila = filaMovem + 1;
		id = $("#registre tr:eq("+fila+") td div").attr("id");

	}
	else{
	
		if( filaMovem < pos){	//baixam elements
			var fila = filaMovem + (pos-filaMovem)+1;
			id = $("#registre tr:eq("+fila+") td div").attr("id");
		}else{					//pujam elements
			var fila = filaMovem - (filaMovem-pos)+1;
			id = $("#registre tr:eq("+fila+") td div").attr("id");	
		}
		
	}
	id2 = id.split("_");
	return id2[1] ;
}


function actualitzar(tascaId, campId, registre){
	$("#registreId").val(registre);
	var rf="";
	var wt="";
	var rq="";
	var ro="";
	rf = $("#rf_"+registre).is(":checked");
  	wt = $("#wt_"+registre).is(":checked");
 	rq = $("#rq_"+registre).is(":checked");
  	ro = $("#ro_"+registre).is(":checked");
	
	campsProcesDwrService.updateCampTasca(tascaId, campId, rf, wt, rq, ro,
	{
		callback: function() {
			$("#mostrarBoto_"+registre).hide()
		},
		async: false
	});
}

function mostrarDesar(registreId){
	$("#mostrarBoto_"+registreId).show();
}



$(".func").click(function(event){
	confirmar(this.dataset['id']);
});


// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="tasques"/>
	</c:import>

	<h3 class="titol-tab titol-variables-tascadef">
		<fmt:message key='defproc.tascacamps.vars_tasca' /> ${tasca.nom}

	</h3>
	<display:table name="campsTasca" id="registre" requestURI="" class="displaytag">
		<display:column property="camp.codiEtiqueta" title="Variable" style="width:300px"/>
		<display:column titleKey="comuns.readfrom" style="width:100px">
				<c:choose>
					 <c:when test="${registre.readFrom}">
					 	<input type="checkbox" id="rf_${registre.id}" checked="checked" onclick="mostrarDesar(${registre.id})">
					 </c:when>
					 <c:otherwise>
					 		<input type="checkbox" id="rf_${registre.id}" onclick="mostrarDesar(${registre.id})">
					 </c:otherwise>
				</c:choose>
		</display:column>
		<display:column titleKey="comuns.writeto" style="width:100px">
				<c:choose>
					 <c:when test="${registre.writeTo}">
					 	<input type="checkBox" id="wt_${registre.id}" checked="checked" onclick="mostrarDesar(${registre.id})">
					 </c:when>
					 <c:otherwise>
					 	<input type="checkBox" id="wt_${registre.id}" onclick="mostrarDesar(${registre.id})">
					 </c:otherwise>
				</c:choose>
		</display:column>
		<display:column titleKey="comuns.required" style="width:100px">
				<c:choose>
					 <c:when test="${registre.required}">
					 	<input type="checkBox" id="rq_${registre.id}" checked="checked" onclick="mostrarDesar(${registre.id})">
					 </c:when>
					 <c:otherwise>
					 	<input type="checkBox" id="rq_${registre.id}" onclick="mostrarDesar(${registre.id})">
					 </c:otherwise>
				</c:choose>
		</display:column>
		<display:column titleKey="comuns.readonly" style="width:100px">
				<c:choose>
					 <c:when test="${registre.readOnly}">
					 	<input type="checkBox" id="ro_${registre.id}" checked="checked" onclick="mostrarDesar(${registre.id})">
					 </c:when>
					 <c:otherwise>
					 		<input type="checkBox" id="ro_${registre.id}" value="" onclick="mostrarDesar(${registre.id})">
					 </c:otherwise>
				</c:choose>
		</display:column>
		
		<display:column>
			<a href="<c:url value="/definicioProces/tascaCampEsborrar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>" class="func" data-id="event"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		</display:column>
		
		<display:column>
			<div id="mostrarBoto_${registre.id}" style="display:none">
				<input id="actualitzar_${registre.id}" type="button" value="<fmt:message key='comuns.desar' />" onclick="actualitzar(${tasca.id} ,${registre.camp.id}, ${registre.id})">
			</div>
		</display:column>
		
	</display:table>

	<form:form id="afegVar" action="tascaCamps.html" cssClass="uniForm">
		<fieldset id="fieldS" class="inlineLabels">
			<legend><fmt:message key='defproc.tascacamps.afegir_var' /></legend>
			<input id="definicioProcesId" name="definicioProcesId" value="${param.definicioProcesId}" type="hidden"/>
			<input id="registreId" name="registreId" value="${registre.id}" type="hidden">
			<form:hidden path="tascaId"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="campId"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="camps"/>
				<c:param name="itemLabel" value="codiEtiqueta"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key='defproc.tascacamps.selec_var' /> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key='defproc.tascacamps.variable' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="readFrom"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.tascacamps.llegir_del' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="writeTo"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.tascacamps.escriure_cap' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="required"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.tascacamps.obligatoria' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="readOnly"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.tascacamps.nomes_lectura' /></c:param>
			</c:import>
		</fieldset>
		
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><fmt:message key='comuns.afegir' />,<fmt:message key='comuns.tornar' /></c:param>
		</c:import>
	</form:form>
	
	<form:form id="modifVar" action="tascaCamps.html" cssClass="uniForm">
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><fmt:message key='comuns.modificar' />,<fmt:message key='comuns.cancelar' /></c:param>
		</c:import>
	</form:form>
	
	<form:form name="actualitzarProp" id="actualitzarProp" cssClass="uniForm">
		<input type="hidden" id="rf_${registre.id}" name="rf_${registre.id}" value=""></input>
		<input type="hidden" id="wt_${registre.id}" name="wt_${registre.id}" value=""></input>
		<input type="hidden" id="rq_${registre.id}" name="rq_${registre.id}" value=""></input>
		<input type="hidden" id="ro_${registre.id}" name="ro_${registre.id}" value=""></input>
	</form:form>
	
 </body>
<script type="text/javascript">

</script>

</html>
