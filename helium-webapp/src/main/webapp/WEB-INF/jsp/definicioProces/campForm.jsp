<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><fmt:message key='comuns.def_proces' />: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<c:import url="../common/formIncludes.jsp"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function disable(blocid) {
	$("#" + blocid).find("input,select,textarea").attr("disabled", "disabled");
}
function enable(blocid) {
	$("#" + blocid).find("input,select,textarea").removeAttr("disabled");
}
function canviTipus(input) {
	if (input.value == "SELECCIO" || input.value == "SUGGEST") {
		enable("camps_consulta");
		disable("camps_accio");
	} else if (input.value == "ACCIO") {
		enable("camps_accio");
		disable("camps_consulta");
	} else if(input.value!="SELECCIO" && input.value!="SUGGEST"){
		$("#enumeracio0").val("");
		$("#enumeracio0").trigger('click');
		disable("camps_accio");
		disable("camps_consulta");
		
		
	} else {
		$("#enumeracio0").val(null);
		$("#enumeracio0").trigger('click');
		disable("camps_accio");
		disable("camps_consulta");
	}
}
function mostraDomini() {
	divD = document.getElementById("params_domini");
    divD.style.display = "";
    divC = document.getElementById("params_consulta");
    divC.style.display = "none";
}
function mostraConsulta() {	
	divD = document.getElementById("params_domini");
    divD.style.display = "none";
    divC = document.getElementById("params_consulta");
    divC.style.display = "";
}
function mostraEnumeracio() {
	divD = document.getElementById("params_domini");
    divD.style.display = "none";
    divC = document.getElementById("params_consulta");
    divC.style.display = "none";
}

// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="camps"/>
	</c:import>
	
	<form:form action="campForm.html" cssClass="uniForm">
		<div class="inlineLabels col first">
			<h3><fmt:message key='defproc.campform.dades_camp' /></h3>
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<form:hidden path="definicioProces"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='comuns.codi' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="tipus"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="tipusCamp"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key='defproc.campform.selec_tipus' /> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key='comuns.tipus' /></c:param>
				<c:param name="onchange">canviTipus(this)</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="etiqueta"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='comuns.etiqueta' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="observacions"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label"><fmt:message key='defproc.campform.observacions' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="agrupacio"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="agrupacions"/>
				<c:param name="itemLabel" value="nom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key='defproc.campform.selec_agrup' /> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key='defproc.campform.agrup_var' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="multiple"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='comuns.multiple' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="ocult"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.campform.ocult' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="ignored"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.campform.ignored' /></c:param>
			</c:import>
		</div>
		<div class="inlineLabels col last">
			<h3><fmt:message key='defproc.campform.dades_cons' /></h3>
			<div id="camps_consulta">
				<div id="enumeracio">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="enumeracio"/>
						<c:param name="type" value="select"/>
						<c:param name="items" value="enumeracions"/>
						<c:param name="itemLabel" value="nom"/>
						<c:param name="itemValue" value="id"/>
						<c:param name="itemBuit">&lt;&lt; <fmt:message key='defproc.campform.selec_enum' /> &gt;&gt;</c:param>
						<c:param name="label"><fmt:message key='defproc.campform.enumeracio' /></c:param>
						<c:param name="onchange">mostraEnumeracio()</c:param>
					</c:import>
				</div>
				<div id="domini">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="domini"/>
						<c:param name="type" value="select"/>
						<c:param name="items" value="dominis"/>
						<c:param name="itemLabel" value="nom"/>
						<c:param name="itemValue" value="id"/>
						<c:param name="itemBuit">&lt;&lt; <fmt:message key='defproc.campform.selec_domini' /> &gt;&gt;</c:param>
						<c:param name="label"><fmt:message key='defproc.campform.domini' /></c:param>
						<c:param name="onchange">mostraDomini()</c:param>
					</c:import> 
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="dominiIntern"/>
						<c:param name="type" value="checkbox"/>
						<c:param name="label"><fmt:message key="defproc.campform.domini_intern"/></c:param>
						<c:param name="onclick">mostraDomini()</c:param>
					</c:import>
				</div>
				<div id="consulta">					
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="consulta"/>
						<c:param name="type" value="select"/>
						<c:param name="items" value="consultes"/>
						<c:param name="itemLabel" value="nom"/>
						<c:param name="itemValue" value="id"/>
						<c:param name="itemBuit">&lt;&lt; <fmt:message key='defproc.campform.selec_consulta' /> &gt;&gt;</c:param>
						<c:param name="label"><fmt:message key='defproc.campform.consultes.tipus' /></c:param>
						<c:param name="onchange">mostraConsulta()</c:param>
					</c:import>
				</div>
				<div id="params_domini" <c:if test="${empty command.domini}"> style="display:none;"</c:if>>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="dominiId"/>
						<c:param name="label"><fmt:message key='defproc.campform.id_domini' /></c:param>
					</c:import>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="dominiParams"/>
						<c:param name="type" value="textarea"/>
						<c:param name="label"><fmt:message key='defproc.campform.param_domini' /></c:param>
					</c:import>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="dominiCampValor"/>
						<c:param name="label"><fmt:message key='defproc.campform.camp_valor' /></c:param>
					</c:import>	
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="dominiCampText"/>
						<c:param name="type" value="textarea"/>
						<c:param name="label"><fmt:message key='defproc.campform.camp_text' /></c:param>						
					</c:import>
				</div>
				<div id="params_consulta" <c:if test="${empty command.consulta}">style="display:none;"</c:if>>		
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="consultaParams"/>
						<c:param name="type" value="textarea"/>
						<c:param name="label"><fmt:message key='defproc.campform.param_consulta' /></c:param>
					</c:import>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="consultaCampValor"/>
						<c:param name="label"><fmt:message key='defproc.campform.camp_valor' /></c:param>
					</c:import>	
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="consultaCampText"/>
						<c:param name="type" value="textarea"/>
						<c:param name="label"><fmt:message key='defproc.campform.camp_text' /></c:param>
					</c:import>
				</div>
				<div id="cache_text">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="dominiCacheText"/>
						<c:param name="type" value="checkbox"/>
						<c:param name="label"><fmt:message key="defproc.campform.cache_text"/></c:param>
					</c:import>
				</div>
			</div>
			<h3><fmt:message key='defproc.campform.dades_accio' /></h3>
			<div id="camps_accio">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="jbpmAction"/>
					<c:param name="type" value="select"/>
					<c:param name="items" value="accionsJbpm"/>
					<c:param name="itemBuit">&lt;&lt; <fmt:message key='defproc.campform.selec_hand' /> &gt;&gt;</c:param>
					<c:param name="label"><fmt:message key='defproc.campform.handler' /></c:param>
				</c:import>
			</div>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}"><fmt:message key='comuns.crear' />,<fmt:message key='comuns.cancelar' /></c:when><c:otherwise><fmt:message key='comuns.modificar' />,<fmt:message key='comuns.cancelar' /></c:otherwise></c:choose></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>

	<script type="text/javascript">$(document).ready(canviTipus(document.getElementById("tipus0")));</script>

</body>
</html>
