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
<script type="text/javascript" language="javascript">
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
	// ]]>
</script>
	</head>
	
	<body>
		<display:table name="llistat" id="consultaCamp" requestURI="" class="displaytag selectable">
			<display:column titleKey="consulta.camps.variable" sortable="false">
				${camps[consultaCamp_rowNum - 1].codiEtiqueta}
			</display:column>
			<display:column titleKey="comuns.tipus" sortable="false">
				${camps[consultaCamp_rowNum - 1].tipus}
			</display:column>
			<display:column titleKey="comuns.def_proces" sortable="false">
				${camps[consultaCamp_rowNum - 1].definicioProces.jbpmKey} v.${camps[consultaCamp_rowNum - 1].definicioProces.versio}
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
			</fieldset>
			
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">submit,cancel</c:param>
				<c:param name="titles"><fmt:message key='comuns.afegir' />,<fmt:message key='comuns.cancelar' /></c:param>
			</c:import>
		</form:form>
		
		<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>
	</body>
</html>