<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Tipus d'expedient: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny'/>" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/campsProcesDwrService.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.DOMWindow.js"/>"></script>
	
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

        	var id = $("tr:has(.showDragHandle) td:last").html();
        	var pos = row.rowIndex - 1;
                
        	//campsProcesDwrService.goToCampRegistre(id, pos, {
			//	callback: function() {
			//		var rows = table.rows;
			//        for (var i = 1; i < rows.length; i++) {
			//	        rows[i].cells[4].innerHTML = rows[i].rowIndex - 1; 
			//        }
			//	},
			//	async: false
			//});
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
	var info = null;
	function carregarCamps(obj) {
		DWRUtil.removeAllOptions("campCodi0");
		DWRUtil.addOptions("campCodi0", [{id:"", nom:"<< <fmt:message key='consulta.camps.selec_var' /> >>"}], "id", "nom");
    	campsProcesDwrService.llistaCampsPerProces(
    		document.getElementById("id").value,
			obj.value,
			document.getElementById("tipus").value,
			{
				callback: function(dades) {
					DWRUtil.addOptions("campCodi0", dades, "0", "1");
					info = dades;
				},
				async: false
			});
	}

	function obtenirVersio(obj) {
		var idx = (obj.selectedIndex - 1);
		document.getElementById("defprocVersio").value = ""+info[idx][2];
	}

	function confirmar(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm("<fmt:message key='consulta.camps.confirmacio' />");
	}
	function removeAllOptions(ele) {
		  ele = getElementById(ele);
		  if (ele == null) return;
		  if (useOptions) {
		    ele.options.length = 0;
		  }
		  else {
		    while (ele.childNodes.length > 0) {
		      ele.removeChild(ele.firstChild);
		    }
		  }
		};
	// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="consultes"/>
	</c:import>

	<c:if test="${param.tipus == 'INFORME'}">
		<div style="text-align: right; margin: 0 0 2px 0">
<%-- 			<a href="#codiXml" class="mostrarCodiXml"><img src="<c:url value="/img/page_white_code.png"/>" alt="<fmt:message key='consulta.camps.mostrarxml' />" title="<fmt:message key='consulta.camps.mostrarxml' />" border="0"/></a> --%>
			<a href="<c:url value="/consulta/reportDownload.html"><c:param name="consultaId" value="${param.id}"/><c:param name="id" value="${consultaCamp.id}"/><c:param name="tipus" value="${param.tipus}"/></c:url>"><img src="<c:url value="/img/page_white_code.png"/>" alt="<fmt:message key='consulta.camps.mostrarxml' />" title="<fmt:message key='consulta.camps.mostrarxml' />" border="0"/></a>
		</div>
	</c:if>
	<display:table name="llistat" id="consultaCamp" requestURI="" class="displaytag selectable">
		<display:column titleKey="consulta.camps.variable" sortable="false">
			${camps[consultaCamp_rowNum - 1].codiEtiqueta}
		</display:column>
		<display:column titleKey="comuns.tipus" sortable="false">
			${camps[consultaCamp_rowNum - 1].tipus}
		</display:column>
		<display:column titleKey="comuns.def_proces" sortable="false">
			<c:if test="${not empty camps[consultaCamp_rowNum - 1].definicioProces}">
			${camps[consultaCamp_rowNum - 1].definicioProces.jbpmKey} v.${camps[consultaCamp_rowNum - 1].definicioProces.versio}
			</c:if>
		</display:column>
		<display:column>
			<a href="<c:url value="/expedientTipus/consultaCampFiltrePujar.html"><c:param name="consultaId" value="${param.id}"/><c:param name="id" value="${consultaCamp.id}"/><c:param name="tipus" value="${param.tipus}"/><c:param name="expedientTipusId" value="${param.expedientTipusId}"/></c:url>">
				<img src="<c:url value="/img/famarrow_up.png"/>" alt="<fmt:message key='comuns.amunt' />" title="<fmt:message key='comuns.amunt' />" border="0"/>
			</a>
			<a href="<c:url value="/expedientTipus/consultaCampFiltreBaixar.html"><c:param name="consultaId" value="${param.id}"/><c:param name="id" value="${consultaCamp.id}"/><c:param name="tipus" value="${param.tipus}"/><c:param name="expedientTipusId" value="${param.expedientTipusId}"/></c:url>">
				<img src="<c:url value="/img/famarrow_down.png"/>" alt="<fmt:message key='comuns.avall' />" title="<fmt:message key='comuns.avall' />" border="0"/>
			</a>
		</display:column>
		<display:column>
			<a href="<c:url value="/expedientTipus/consultaCampsDelete.html"><c:param name="consultaId" value="${param.id}"/><c:param name="id" value="${consultaCamp.id}"/><c:param name="tipus" value="${param.tipus}"/><c:param name="expedientTipusId" value="${param.expedientTipusId}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		</display:column>
	</display:table>
	
	<form:form action="consultaCamps.html" method="post" cssClass="uniForm">
		<fieldset class="inlineLabels">
			<legend><fmt:message key='consulta.camps.afegir_var' /></legend>
			<input type="hidden" name="id" id="id" value="${param.id}" />
			<input type="hidden" name="tipus" id="tipus" value="${param.tipus}" />
			<input type="hidden" name="expedientTipusId" id="expedientTipusId" value="${param.expedientTipusId}" />
			<input type="hidden" name="defprocVersio" id="defprocVersio" value="" />
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="defprocJbpmKey"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="definicionsProces"/>
				<c:param name="itemLabel" value="jbpmKey"/>
				<c:param name="itemValue" value="jbpmKey"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key='consulta.camps.selec_def' /> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key='comuns.def_proces' /></c:param>
				<c:param name="onchange" value="carregarCamps(this)"/>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="campCodi"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value=""/>
				<c:param name="itemLabel" value=""/>
				<c:param name="itemValue" value=""/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key='consulta.camps.selec_def' /> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key='consulta.camps.variable' /></c:param>
				<c:param name="onchange" value="obtenirVersio(this)"/>
			</c:import>
			<script type="text/javascript">carregarCamps(document.getElementById('defprocJbpmKey0'))</script>
		</fieldset>
		
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><fmt:message key='comuns.afegir' />,<fmt:message key='comuns.cancelar' /></c:param>
		</c:import>
	</form:form>
	
	<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>

	<c:if test="${param.tipus == 'INFORME'}">
		<script type="text/javascript">
			$('.mostrarCodiXml').openDOMWindow({
				eventType: 'click',
				width: 640,
				height: 480,
				loader: 1,
				loaderHeight: 32,
				loaderWidth: 32,
				windowPadding: 0,
				draggable: 1});
		</script>
		<div id="codiXml" style="display:none">
<pre style="padding: 1em"><c:forEach var="camp" items="${llistat}" varStatus="status">
&lt;field name="${camps[status.index].definicioProces.jbpmKey}/${camps[status.index].codi}" class="net.conselldemallorca.helium.report.FieldValue"/&gt;
</c:forEach></pre>
		</div>
	</c:if>

</body>
</html>