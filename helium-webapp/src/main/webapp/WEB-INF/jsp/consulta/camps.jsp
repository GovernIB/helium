<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key='comuns.variables' /> <c:choose><c:when test="${param.tipus=='FILTRE'}"><fmt:message key='consulta.camps.del_filtre' /></c:when><c:otherwise><fmt:message key='consulta.camps.de_linforme' /></c:otherwise></c:choose> <fmt:message key='consulta.camps.de_consulta' /> ${consulta.nom}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<c:import url="../common/formIncludes.jsp"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/campsProcesDwrService.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.DOMWindow.js"/>"></script>
<script type="text/javascript">
	// <![CDATA[
	var info = null;
	function carregarCamps(obj) {
		DWRUtil.removeAllOptions("campCodi0");
		DWRUtil.addOptions("campCodi0", [{id:"", nom:"<< <fmt:message key='consulta.camps.selec_var' /> >>"}], "id", "nom");
    	campsProcesDwrService.llistaCampsPerProces(
    		document.getElementById("id").value,
			obj.value,
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
	<c:if test="${param.tipus == 'INFORME'}">
		<div style="text-align: right; margin: 0 0 2px 0">
			<!-- a href="#codiXml" class="mostrarCodiXml"><img src="<c:url value="/img/page_white_code.png"/>" alt="<fmt:message key='consulta.camps.mostrarxml' />" title="<fmt:message key='consulta.camps.mostrarxml' />" border="0"/></a-->
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
			<a href="<c:url value="/consulta/campFiltrePujar.html"><c:param name="consultaId" value="${param.id}"/><c:param name="id" value="${consultaCamp.id}"/><c:param name="tipus" value="${param.tipus}"/></c:url>">
				<img src="<c:url value="/img/famarrow_up.png"/>" alt="<fmt:message key='comuns.amunt' />" title="<fmt:message key='comuns.amunt' />" border="0"/>
			</a>
			<a href="<c:url value="/consulta/campFiltreBaixar.html"><c:param name="consultaId" value="${param.id}"/><c:param name="id" value="${consultaCamp.id}"/><c:param name="tipus" value="${param.tipus}"/></c:url>">
				<img src="<c:url value="/img/famarrow_down.png"/>" alt="<fmt:message key='comuns.avall' />" title="<fmt:message key='comuns.avall' />" border="0"/>
			</a>
		</display:column>
		<display:column>
			<a href="<c:url value="/consulta/campDelete.html"><c:param name="consultaId" value="${param.id}"/><c:param name="id" value="${consultaCamp.id}"/><c:param name="tipus" value="${param.tipus}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		</display:column>
	</display:table>
	
	<form:form action="camps.html" method="post" cssClass="uniForm">
		<fieldset class="inlineLabels">
			<legend><fmt:message key='consulta.camps.afegir_var' /></legend>
			<input type="hidden" name="id" id="id" value="${param.id}" />
			<input type="hidden" name="tipus" id="tipus" value="${param.tipus}" />
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
</c:forEach></pre><br/><br/><pre style="padding: 1em">${report}</pre>
		</div>
	</c:if>

</body>
</html>