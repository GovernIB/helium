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
    	campsProcesDwrService.goToSignaturaTasca(id, pos, {
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
	return confirm("<fmt:message key='defproc.tascafirmes.confirmacio' />");
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
			$("#mostrarBoto_"+registre).hide();
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

	<h3 class="titol-tab titol-signatures-tascadef">
		<fmt:message key='defproc.tascafirmes.signa_tasca' /> ${tasca.nom}
	</h3>
	<display:table name="tascaFirmes" id="registre" requestURI="" class="displaytag">
		<display:column property="document.nom" titleKey="defproc.tascafirmes.document"/>
		<display:column titleKey="comuns.propietats">
			<c:if test="${registre.required}">rq</c:if>
		</display:column>
		<display:column property="order" titleKey="comuns.ordre"/>
<%-- 		<display:column> --%>
<%-- 			<a href="<c:url value="/definicioProces/tascaFirmaPujar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_up.png"/>" alt="<fmt:message key='comuns.amunt' />" title="<fmt:message key='comuns.amunt' />" border="0"/></a> --%>
<%-- 			<a href="<c:url value="/definicioProces/tascaFirmaBaixar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_down.png"/>" alt="<fmt:message key='comuns.avall' />" title="<fmt:message key='comuns.avall' />" border="0"/></a> --%>
<%-- 		</display:column> --%>
		<display:column>
			<a href="<c:url value="/definicioProces/tascaFirmaEsborrar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		</display:column>
		<display:column>
			<div id="mostrarBoto_${registre.id}" style="display:none">
				<input id="actualitzar_${registre.id}" type="button" value="<fmt:message key='comuns.desar' />">
			</div>
		</display:column>
	</display:table>

	<form:form action="tascaFirmes.html" cssClass="uniForm">
		<fieldset class="inlineLabels">
			<legend><fmt:message key='defproc.tascafirmes.afegir_signa' /></legend>
			<input id="definicioProcesId" name="definicioProcesId" value="${param.definicioProcesId}" type="hidden"/>
			<form:hidden path="tascaId"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="documentId"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="documents"/>
				<c:param name="itemLabel" value="codiNom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key='defproc.tascafirmes.selec_doc' /> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key='defproc.tascafirmes.document' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="required"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.tascafirmes.obligatoria' /></c:param>
			</c:import>
		</fieldset>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><fmt:message key='comuns.afegir' />,<fmt:message key='comuns.tornar' /></c:param>
		</c:import>
	</form:form>

</body>
</html>
