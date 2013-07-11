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
                
        	campsProcesDwrService.goToCampConsLlistat(id, pos, {
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
	return confirm("<fmt:message key='consulta.llistat.confirmacio' />");
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
//]]>
</script>

</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="consultes"/>
	</c:import>
	
	<c:set var="tePermisosGestionar" value="${false}"/>
	<security:accesscontrollist domainObject="${expedientTipus}" hasPermission="16,32">
		<c:set var="tePermisosGestionar" value="${true}"/>
	</security:accesscontrollist>

	<display:table name="llistat" id="registre" requestURI="" class="displaytag">
		<c:choose>
			<c:when test="${tePermisosGestionar}">
				<display:column property="codi" titleKey="comuns.codi" sortable="false" url="/expedientTipus/consultaForm.html?expedientTipusId=${expedientTipus.id}" paramId="id" paramProperty="id"/>
			</c:when>
			<c:otherwise>
				<display:column property="codi" titleKey="comuns.codi" sortable="false"/>
			</c:otherwise>
		</c:choose>
		<display:column property="nom" titleKey="comuns.titol" sortable="false"/>
		<display:column property="expedientTipus.nom" titleKey="comuns.tipus_exp" sortable="false" />
		
		<display:column>
			<c:set var="numCampsFiltre" value="${0}"/>
	    	<c:forEach var="camp" items="${registre.camps}">
    			<c:if test="${camp.tipus=='FILTRE'}"><c:set var="numCampsFiltre" value="${numCampsFiltre+1}"/></c:if>
	    	</c:forEach>
	    	<form action="consultaCamps.html">
				<input type="hidden" name="id" value="${registre.id}"/>
				<input type="hidden" name="tipus" value="${tipusFiltre}"/>
				<input type="hidden" name="expedientTipusId" value="${param.expedientTipusId}"/>
				<button type="submit" class="submitButton"><fmt:message key='consulta.llistat.vars_filtre' />&nbsp;(${numCampsFiltre})</button>
			</form>
	    </display:column>
	    <display:column>
	    	<c:set var="numCampsInforme" value="${0}"/>
	    	<c:forEach var="camp" items="${registre.camps}">
    			<c:if test="${camp.tipus=='INFORME'}"><c:set var="numCampsInforme" value="${numCampsInforme+1}"/></c:if>
	    	</c:forEach>
	    	<form action="consultaCamps.html">
				<input type="hidden" name="id" value="${registre.id}"/>
				<input type="hidden" name="tipus" value="${tipusInforme}"/>
				<input type="hidden" name="expedientTipusId" value="${param.expedientTipusId}"/>
				<button type="submit" class="submitButton">Variables de l'informe&nbsp;(${numCampsInforme})</button>
			</form>
	    </display:column>
	    <%--display:column>
	    	<form action="subconsultes.html">
				<input type="hidden" name="id" value="${registre.id}"/>
				<button type="submit" class="submitButton">Subconsultes&nbsp;(${fn:length(registre.subConsultes)})</button>
			</form>
	    </display:column--%>
	    <display:column titleKey="entorn.llistat.actiu">
	     	<c:choose><c:when test="${registre.ocultarActiu}"><fmt:message key='comuns.no' /></c:when><c:otherwise><fmt:message key='comuns.si' /></c:otherwise></c:choose>
	    </display:column>
	    <c:if test="${tePermisosGestionar}">
			<display:column>
				<div id="consulta_${registre.id}"></div>
				<a href="<c:url value="/expedientTipus/consultaEsborrar.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/><c:param name="consultaId" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
			</display:column>
		</c:if>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

	<form action="<c:url value="/expedientTipus/consultaForm.html"/>">
		<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
		<button type="submit" class="submitButton"><fmt:message key='consulta.llistat.nova' /></button>
	</form>

</body>
</html>
