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
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
    
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/campsProcesDwrService.js"/>"></script>
    
    
<script type="text/javascript">
$(document).ready(function() {
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
                
        	campsProcesDwrService.goToCampEstat(id, pos, {
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
	return confirm("Estau segur que voleu esborrar aquest estat?");
}

function obtenirId(pos){
	if(filaMovem==pos){
		
		var fila = filaMovem + 1;
		id = $("#registre tr:eq("+fila+") td:last div").attr("id");

	}
	else{
	
		if( filaMovem < pos){	//baixam elements
			var fila = filaMovem + (pos-filaMovem)+1;
			id = $("#registre tr:eq("+fila+") td:last div").attr("id");
		}else{					//pujam elements
			var fila = filaMovem - (filaMovem-pos)+1;
			id = $("#registre tr:eq("+fila+") td:last div").attr("id");	
		}
	}
	id2 = id.split("_");
	return id2[1] ;
}

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

// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="estats"/>
	</c:import>
	<display:table name="estats" id="registre" requestURI="" class="displaytag">
		
		<display:column property="codi" title="Codi" url="/expedientTipus/estatsForm.html?expedientTipusId=${expedientTipus.id}" paramId="id" paramProperty="id"/>
		<display:column property="nom" title="Nom"/>

		<display:column>
			<div id="estat_${registre.id}"></div>
			<a href="<c:url value="/expedientTipus/estatEsborrar.html"><c:param name="id" value="${registre.id}"/><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		</display:column>
	</display:table>

	<form:form action="estats.html" cssClass="uniForm">
		<fieldset class="inlineLabels">
			<legend>Afegir nou estat</legend>
			<input id="expedientTipus" name="expedientTipus" value="${param.expedientTipusId}" type="hidden"/>
			<input id="expedientTipusId" name="expedientTipusId" value="${param.expedientTipusId}" type="hidden"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="${true}"/>
				<c:param name="label">Codi</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="${true}"/>
				<c:param name="label">Nom</c:param>
			</c:import>
		</fieldset>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit</c:param>
			<c:param name="titles">Afegir</c:param>
		</c:import>
	</form:form>
	<security:accesscontrollist domainObject="${expedientTipus}" hasPermission="16,32">
		<div class="missatgesGris">
			<h3 class="titol-tab titol-delegacio"><fmt:message key="enumeracio.valors.import_dades"/> <img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="<fmt:message key="enumeracio.valors.mostrar_ocultar"/>" title="<fmt:message key="enumeracio.valors.mostrar_ocultar"/>" border="0" onclick="mostrarOcultar(this,'form-importar')"/></h3>
			<div id="form-importar" style="display:none">
				<form:form action="importarEstats.html" cssClass="uniForm" enctype="multipart/form-data" commandName="commandImportacio">
					<input type="hidden" name="expedientTipusId" value="${param.expedientTipusId}">
					<div class="inlineLabels">
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="arxiu"/>
							<c:param name="type" value="file"/>
							<c:param name="label"><fmt:message key="enumeracio.valors.arxiu_exp"/></c:param>
						</c:import>
						<c:import url="../common/formElement.jsp">
							<c:param name="type" value="buttons"/>
							<c:param name="values">submit</c:param>
							<c:param name="titles"><fmt:message key="enumeracio.valors.importar"/></c:param>
						</c:import>
					</div>
				</form:form>
			</div>
		</div>
	</security:accesscontrollist>
</body>
</html>
