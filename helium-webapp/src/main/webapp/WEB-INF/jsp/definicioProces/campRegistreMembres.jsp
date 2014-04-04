<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key='comuns.def_proces' />: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/campsProcesDwrService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    
    <c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
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
        	campsProcesDwrService.goToCampRegistreMembres(id, pos, {
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
           
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='defproc.campreg.confirmacio' />");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="camps"/>
	</c:import>

	<h3 class="titol-tab titol-variables-tascadef">
		<fmt:message key='defproc.campreg.camps_reg' /> ${registre.etiqueta}
	</h3>
	<display:table name="membres" id="registre" requestURI="" class="displaytag">
		<display:column property="membre.codiEtiqueta" titleKey="defproc.campreg.variable"/>
		<display:column property="membre.tipus" titleKey="comuns.tipus"/>
		<display:column titleKey="defproc.campreg.obligatori"><c:choose><c:when test="${registre.obligatori}"><fmt:message key='comuns.si' /></c:when><c:otherwise><fmt:message key='comuns.no' /></c:otherwise></c:choose></display:column>
		<display:column titleKey="defproc.campreg.mostrar_llista"><c:choose><c:when test="${registre.llistar}"><fmt:message key='comuns.si' /></c:when><c:otherwise><fmt:message key='comuns.no' /></c:otherwise></c:choose></display:column>
		<display:column property="ordre" titleKey="comuns.ordre"/>
		<display:column>
			<a href="<c:url value="/definicioProces/campRegistreMembrePujar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_up.png"/>" alt="<fmt:message key='comuns.amunt' />" title="<fmt:message key='comuns.amunt' />" border="0"/></a>
			<a href="<c:url value="/definicioProces/campRegistreMembreBaixar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_down.png"/>" alt="<fmt:message key='comuns.avall' />" title="<fmt:message key='comuns.avall' />" border="0"/></a>
		</display:column>
		<display:column>
			<div id="campRegistreMembre_${registre.id}"></div>
			<a href="<c:url value="/definicioProces/campRegistreMembreEsborrar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		</display:column>
	</display:table>

	<form:form action="campRegistreMembres.html" cssClass="uniForm">
		<fieldset class="inlineLabels">
			<legend><fmt:message key='defproc.campreg.afegir_var' /></legend>
			<input id="definicioProcesId" name="definicioProcesId" value="${param.definicioProcesId}" type="hidden"/>
			<form:hidden path="registreId"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="membreId"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="camps"/>
				<c:param name="itemLabel" value="codiEtiqueta"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key='defproc.campreg.selec_var' /> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key='defproc.campreg.variable' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="obligatori"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.campreg.obligatori' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="llistar"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.campreg.mostrar_llista' /></c:param>
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
