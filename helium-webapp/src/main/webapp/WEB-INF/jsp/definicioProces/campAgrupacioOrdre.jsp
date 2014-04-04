<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
	<head>
		<title><fmt:message key='defproc.agrupordre.assignar' />: ${agrupacio.nom}</title>
		<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
		<c:import url="../common/formIncludes.jsp"/>
		<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
		<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/campsProcesDwrService.js"/>"></script>
		<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
		<script type="text/javascript">
			// <![CDATA[
			            
			$(document).ready(function() {
	
				    // Initialise the table
				    //$("#registre").tableDnD();
				
				//$("#modifVar").hide();
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
			        	campsProcesDwrService.goToCampAgrupacio(id, pos, {
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
			            
			function confirmar(e) {
				var e = e || window.event;
				e.cancelBubble = true;
				if (e.stopPropagation) e.stopPropagation();
				return confirm("<fmt:message key='defproc.agrupordre.confirmacio' />");
			}
			
			$(".func").click(function(event){
				confirmar(this.dataset['id']);
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
			
			// ]]>
		</script>
	</head>
	<body>
		<c:import url="../common/tabsDefinicioProces.jsp">
			<c:param name="tabActiu" value="agrupacions"/>
		</c:import>
		
		<display:table name="camps" id="registre" requestURI="" class="displaytag selectable">
			<display:column property="etiqueta" titleKey="comuns.etiqueta" sortable="true"/>
<%-- 			<display:column> --%>
<%-- 				<a href="<c:url value="/definicioProces/campAgrupacioOrdrePujar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/><c:param name="agrupacioCodi" value="${agrupacio.codi}"/></c:url>"><img src="<c:url value="/img/famarrow_up.png"/>" alt="<fmt:message key='comuns.amunt' />" title="<fmt:message key='comuns.amunt' />" border="0"/></a> --%>
<%-- 				<a href="<c:url value="/definicioProces/campAgrupacioOrdreBaixar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/><c:param name="agrupacioCodi" value="${agrupacio.codi}"/></c:url>"><img src="<c:url value="/img/famarrow_down.png"/>" alt="<fmt:message key='comuns.avall' />" title="<fmt:message key='comuns.avall' />" border="0"/></a> --%>
<%-- 			</display:column> --%>
			<display:column>
				<div id="campAgrupacio_${registre.id}"></div>
		    	<a href="<c:url value="/definicioProces/campAgrupacioOrdreDelete.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/><c:param name="agrupacioCodi" value="${agrupacio.codi}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		    </display:column>
		</display:table>
		
		<form:form action="campAgrupacioOrdre.html" method="post" cssClass="uniForm">
			<input type="hidden" id="definicioProcesId" name="definicioProcesId" value="${param.definicioProcesId}" />
			<input type="hidden" id="agrupacioCodi" name="agrupacioCodi" value="${agrupacio.codi}" />
			<fieldset class="inlineLabels">
				<legend><fmt:message key='defproc.agrupordre.afegir_var' /></legend>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="id"/>
					<c:param name="type" value="select"/>
					<c:param name="items" value="variables"/>
					<c:param name="itemLabel" value="codiEtiqueta"/>
					<c:param name="itemValue" value="id"/>
					<c:param name="itemBuit">&lt;&lt; <fmt:message key='defproc.agrupordre.selec_var' /> &gt;&gt;</c:param>
					<c:param name="label"><fmt:message key='defproc.agrupordre.vars_tasca' /></c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,cancel</c:param>
					<c:param name="titles"><fmt:message key='defproc.agrupordre.afegeix_var' />, <fmt:message key='comuns.cancelar' /></c:param>
				</c:import>
			</fieldset>
		</form:form>
	</body>
</html>
