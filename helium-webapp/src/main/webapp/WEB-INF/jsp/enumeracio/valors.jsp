<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><c:choose><c:when test="${empty command.id}"><fmt:message key="enumeracio.form.crear_nova"/></c:when><c:otherwise><fmt:message key="enumeracio.form.modificar"/></c:otherwise></c:choose></title>
	<meta name="titolcmp" content="<fmt:message key="comuns.disseny"/>" />
	<c:import url="../common/formIncludes.jsp"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>
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
                
        	campsProcesDwrService.goToValors(id, pos, {
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
	return confirm("<fmt:message key="enumeracio.valors.confirmacio"/>");
}
// ]]>
</script>
</head>

<body>
	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
		<display:column property="codi" titleKey="comuns.codi" sortable="true" url="/enumeracio/valorsForm.html?enumeracioId=${registre.enumeracio.id}" paramId="id" paramProperty="id"/>
		<display:column property="nom" titleKey="comuns.titol" sortable="true"/>
<%-- 		<display:column> --%>
<%-- 			<a href="<c:url value="/enumeracio/valorsPujar.html"><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_up.png"/>" alt="<fmt:message key="comuns.amunt"/>" title="<fmt:message key="comuns.amunt"/>" border="0"/></a> --%>
<%-- 			<a href="<c:url value="/enumeracio/valorsBaixar.html"><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_down.png"/>" alt="<fmt:message key="comuns.avall"/>" title="<fmt:message key="comuns.avall"/>" border="0"/></a> --%>
<%-- 		</display:column> --%>
		<display:column>
			<div id="valors_${registre.id}"></div>
			<a href="<c:url value="/enumeracio/deleteValors.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key="comuns.esborrar"/>" title="<fmt:message key="comuns.esborrar"/>" border="0"/></a>
		</display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

	<form:form action="valors.html" cssClass="uniForm" method="post">
		<div class="inlineLabels">
			<form:hidden path="enumeracioId"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key="comuns.codi"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key="comuns.titol"/></c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><fmt:message key="comuns.afegir"/>,<fmt:message key="comuns.cancelar"/></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key="comuns.camps_marcats"/> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key="comuns.camp_oblig"/>" title="<fmt:message key="comuns.camp_oblig"/>" border="0"/> <fmt:message key="comuns.son_oblig"/></p>
	<br/>
	<div class="missatgesGris">
		<h3 class="titol-tab titol-delegacio"><fmt:message key="enumeracio.valors.import_dades"/> <img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="<fmt:message key="enumeracio.valors.mostrar_ocultar"/>" title="<fmt:message key="enumeracio.valors.mostrar_ocultar"/>" border="0" onclick="mostrarOcultar(this,'form-importar')"/></h3>
		<div id="form-importar" style="display:none">
			<form:form action="importar.html" cssClass="uniForm" enctype="multipart/form-data" commandName="commandImportacio">
				<input type="hidden" name="id" value="${command.enumeracioId}"/>
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

</body>
</html>
