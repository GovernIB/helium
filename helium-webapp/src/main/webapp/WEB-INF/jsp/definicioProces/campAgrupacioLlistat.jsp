<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
	<title><fmt:message key='comuns.def_proces' />: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>
	    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/campsProcesDwrService.js"/>"></script>
	
	
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='defproc.agrupllistat.confirmacio' />");
}

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
                
        	campsProcesDwrService.goToCampAgrupacioLlista(id, pos, {
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
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="agrupacions"/>
	</c:import>

	<display:table name="agrupacions" id="registre" requestURI="" class="displaytag selectable">
		<display:column property="codi" titleKey="comuns.codi" url="/definicioProces/campAgrupacioForm.html?definicioProcesId=${param.definicioProcesId}" paramId="id" paramProperty="id"/>
		<display:column property="nom" titleKey="comuns.nom"/>
<%-- 		<display:column> --%>
<%-- 			<a href="<c:url value="/definicioProces/campAgrupacioPujar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_up.png"/>" alt="<fmt:message key='comuns.amunt' />" title="<fmt:message key='comuns.amunt' />" border="0"/></a> --%>
<%-- 			<a href="<c:url value="/definicioProces/campAgrupacioBaixar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_down.png"/>" alt="<fmt:message key='comuns.avall' />" title="<fmt:message key='comuns.avall' />" border="0"/></a> --%>
<%-- 		</display:column> --%>
	    <display:column>
	    	<form action="campAgrupacioOrdre.html">
				<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
				<input type="hidden" name="agrupacioCodi" value="${registre.codi}"/>
				<button type="submit" class="submitButton"><fmt:message key='comuns.variables' />&nbsp;(${fn:length(registre.camps)})</button>
			</form>
	    </display:column>
		<display:column>
			<div id="campAgrupacioLlista_${registre.id}"></div>
	    	<a href="<c:url value="/definicioProces/campAgrupacioDelete.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
	    </display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

	<form action="<c:url value="/definicioProces/campAgrupacioForm.html"/>">
		<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
		<button type="submit" class="submitButton"><fmt:message key='defproc.agrupllistat.nova' /></button>
	</form>

</body>
</html>
