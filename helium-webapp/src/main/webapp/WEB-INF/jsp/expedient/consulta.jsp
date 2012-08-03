<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<c:set var="sessionCommand" value="${sessionScope.consultaExpedientsCommand}"/>
<html>
<head>
	<title><fmt:message key="expedient.consulta.cons_general"/></title>
	<meta name="titolcmp" content="<fmt:message key="comuns.consultes"/>" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/gisDwrService.js"/>"></script>
<script type="text/javascript">
// <![CDATA[
function refrescarEstats(element) {
	var estatActual = $("select#estat0").val();
	$.getJSON(
    		"consultaEstats.html?id=" + element.value,
    		{},
    		function(j) {
			    var options = '';
			    options += '<option value="">&lt;&lt; <fmt:message key="expedient.consulta.select.estat"/> &gt;&gt;</option>';
		        for (var i = 0; i < j.length; i++) {
		        	if (j[i].id == estatActual)
		        		options += '<option value="' + j[i].id + '" selected="selected">' + j[i].nom + '</option>';
		        	else
		        		options += '<option value="' + j[i].id + '">' + j[i].nom + '</option>';
		        }
		        $("select#estat0").html(options).attr('class', 'inlineLabels');
			});
}
function confirmarEsborrar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key="expedient.consulta.confirm.esborrar"/>");
}
function confirmarAnular(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key="expedient.consulta.confirm.anular"/>");
}
function obreVisorGis() {
    var sUrl;
	var piis = new Array();
	<c:forEach items="${piis}" var="pii">
		piis.push('${pii}');
	</c:forEach>
	gisDwrService.urlVisor(
			{callback: function(url) {
				sUrl = url;
			},
			async: false
		});
    gisDwrService.xmlExpedients(
    		piis,
    		{callback: function(sXML) {
    			var form = document.createElement("form");
    			form.setAttribute("method", "post");
    		    form.setAttribute("action", sUrl);
    		    form.setAttribute("target", "visor");
    		  
    			var input = document.createElement('input');
    		    input.type = 'hidden';
    		    input.name = 'xmlexpedients';
    		    input.value = sXML;

    			form.appendChild(input);
    			document.body.appendChild(form);
    		  
    		  	//note I am using a post.htm page since I did not want to make double request to the page 
    		    //it might have some Page_Load call which might screw things up.
    		    //window.open("post.htm", name, windowoption);
    			
    			form.submit();
    			document.body.removeChild(form);
    		},
			async: true
    	});
}
function clicCheckMassiu(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	$.get(	"massivaIds.html",
			{	expedientTipusId: "${command.expedientTipus.id}",
				expedientId: e.target.value,
				checked: e.target.checked});
}

function selTots(){
	
	if($("#selTots").is(":checked")){
		$("#registre input[type=checkbox]").attr("checked",true);
	}else{
		$("#registre input[type=checkbox]").attr("checked",false);
	}
}

// ]]>
</script>
</head>
<body>

	<form:form action="consulta.html" cssClass="uniForm">
		<div class="inlineLabels col first">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="titol"/>
				<c:param name="label"><fmt:message key="expedient.consulta.titol"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="numero"/>
				<c:param name="label"><fmt:message key="expedient.consulta.numero"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="dataInici1"/>
				<c:param name="type" value="custom"/>
				<c:param name="label"><fmt:message key="expedient.consulta.datainici"/></c:param>
				<c:param name="customClass">customField</c:param>
				<c:param name="content">
					<spring:bind path="dataInici1">
						<label for="dataInici1" class="blockLabel">
							<span><fmt:message key="expedient.consulta.entre"/></span>
							<input id="dataInici1" name="dataInici1" value="${status.value}" type="text" class="textInput"/>
							<script type="text/javascript">
								// <![CDATA[
								$(function() {
									$.datepicker.setDefaults($.extend({
										dateFormat: 'dd/mm/yy',
										changeMonth: true,
										changeYear: true
									}));
									$("#dataInici1").datepicker();
								});
								// ]]>
							</script>
						</label>
					</spring:bind>
					<spring:bind path="dataInici2">
						<label for="dataInici2" class="blockLabel blockLabelLast">
							<span><fmt:message key="expedient.consulta.i"/></span>
							<input id="dataInici2" name="dataInici2" value="${status.value}" type="text" class="textInput"/>
							<script type="text/javascript">
								// <![CDATA[
								$(function() {
									$.datepicker.setDefaults($.extend({
										dateFormat: 'dd/mm/yy',
										changeMonth: true,
										changeYear: true
									}));
									$("#dataInici2").datepicker();
								});
								// ]]>
							</script>
						</label>
					</spring:bind>
				</c:param>
			</c:import>
		</div>
		<div class="inlineLabels col last">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="expedientTipus"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="expedientTipus"/>
				<c:param name="itemLabel" value="nom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key="expedient.consulta.select.tipusexpedient"/> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key="expedient.consulta.tipusexpedient"/></c:param>
				<c:param name="onchange">refrescarEstats(this)</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="estat"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="estats"/>
				<c:param name="itemLabel" value="nom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key="expedient.consulta.select.estat"/> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key="expedient.consulta.estat"/></c:param>
			</c:import>
			<c:if test="${globalProperties['app.georef.actiu']}">
				<c:choose>
					<c:when test="${globalProperties['app.georef.tipus']=='ref'}">
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="geoReferencia"/>
							<c:param name="label"><fmt:message key="comuns.georeferencia.codi"/></c:param>
						</c:import>
					</c:when>
					<c:otherwise>
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="geoPosX"/>
							<c:param name="type" value="custom"/>
							<c:param name="label"><fmt:message key="comuns.georeferencia.coordenades"/></c:param>
							<c:param name="customClass">customField</c:param>
							<c:param name="content">
								<spring:bind path="geoPosX">
									<label for="geoPosX" class="blockLabel">
										<span><fmt:message key="comuns.georeferencia.coordX"/></span>
										<input id="geoPosX" name="geoPosX" value="${status.value}" type="text" class="textInput"/>
									</label>
								</spring:bind>
								<spring:bind path="geoPosY">
									<label for="geoPosY" class="blockLabel blockLabelLast">
										<span><fmt:message key="comuns.georeferencia.coordY"/></span>
										<input id="geoPosY" name="geoPosY" value="${status.value}" type="text" class="textInput"/>
									</label>
								</spring:bind>
							</c:param>
						</c:import>
					</c:otherwise>
				</c:choose>
			</c:if>
			<security:accesscontrollist domainObject="${entornActual}" hasPermission="16">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="mostrarAnulats"/>
					<c:param name="type" value="checkbox"/>
					<c:param name="label">Mostrar anul·lats</c:param>
				</c:import>
			</security:accesscontrollist>
			<c:if test="${not command.massivaActiu}">
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,clean,massiva</c:param>
					<c:param name="titles"><fmt:message key="expedient.consulta.consultar"/>,<fmt:message key="expedient.consulta.netejar"/>,<fmt:message key="expedient.consulta.massiva.activar"/></c:param>
				</c:import>
			</c:if>
			<c:if test="${command.massivaActiu}">
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,clean,nomassiva</c:param>
					<c:param name="titles"><fmt:message key="expedient.consulta.consultar"/>,<fmt:message key="expedient.consulta.netejar"/>,<fmt:message key="expedient.consulta.massiva.desactivar"/></c:param>
				</c:import>
			</c:if>
		</div>
	</form:form><div style="clear:both"></div><br/>

	<c:if test="${not empty sessionCommand}">
		<c:if test="${globalProperties['app.georef.actiu'] && globalProperties['app.gis.plugin.actiu']}">
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">gis</c:param>
				<c:param name="titles"><fmt:message key="expedient.consulta.gis"/></c:param>
				<c:param name="onclick">obreVisorGis()</c:param>
			</c:import>
		</c:if>
		<%--div class="missatgesGris">
			<c:choose>
				<c:when test="${empty llistat}"><p><fmt:message key="expedient.consulta.notrobats"/></p></c:when>
				<c:when test="${fn:length(llistat) == 1}"><p><fmt:message key="expedient.consulta.trobatun"/></p></c:when>
				<c:otherwise><p><fmt:message key="expedient.consulta.trobats"><fmt:param value="${fn:length(llistat)}"/></fmt:message></p></c:otherwise>
			</c:choose>
		</div--%>
		<c:if test="${not empty llistat}">
			<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable" sort="external">
				<c:set var="filaStyle" value=""/>
				<input type="hidden" id="tots" value=""/>
				<c:if test="${registre.anulat}"><c:set var="filaStyle" value="text-decoration:line-through"/></c:if>
				<c:if test="${command.massivaActiu}">
					<display:column title="<input id='selTots' type='checkbox' value='false' onclick='selTots()'>Tots" style="${filaStyle}" >
						<c:set var="expedientSeleccionat" value="${false}"/>
						<c:set var="tots" value="${session.tots}"/>
						<c:forEach var="eid" items="${sessionScope.consultaExpedientsIdsMassius}" varStatus="status">
							<c:if test="${status.index gt 0 and eid == registre.id}"><c:set var="expedientSeleccionat" value="${true}"/></c:if>
						</c:forEach>
						<input type="checkbox" name="expedientId" value="${registre.id}"<c:if test="${expedientSeleccionat}"> checked="checked"</c:if> onclick="clicCheckMassiu(event)"/>
					</display:column>
				</c:if>
				<display:column property="identificador" title="Expedient" url="/tasca/personaLlistat.html" paramId="exp" paramProperty="identificador" sortable="true" style="${filaStyle}"/>
				<display:column property="dataInici" title="Iniciat el" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true" style="${filaStyle}"/>
				<display:column property="tipus.nom" title="Tipus" sortable="true" style="${filaStyle}"/>
				<display:column title="Estat" style="${filaStyle}" sortable="true" sortProperty="estat.nom">
					<c:if test="${registre.aturat}"><img src="<c:url value="/img/stop.png"/>" alt="Aturat" title="Aturat" border="0"/></c:if>
					<c:choose>
						<c:when test="${empty registre.dataFi}">
							<c:choose><c:when test="${empty registre.estat}"><fmt:message key="expedient.consulta.iniciat"/></c:when><c:otherwise>${registre.estat.nom}</c:otherwise></c:choose>
						</c:when>
						<c:otherwise><fmt:message key="expedient.consulta.finalitzat"/></c:otherwise>
					</c:choose>
				</display:column>
				<display:column>
					<security:accesscontrollist domainObject="${registre.tipus}" hasPermission="16,1">
						<a href="<c:url value="/expedient/info.html"><c:param name="id" value="${registre.processInstanceId}"/></c:url>"><img src="<c:url value="/img/information.png"/>" alt="<fmt:message key="comuns.informacio"/>" title="<fmt:message key="comuns.informacio"/>" border="0"/></a>
					</security:accesscontrollist>
				</display:column>
				<display:column>
					<security:accesscontrollist domainObject="${registre.tipus}" hasPermission="16,2">
						<c:if test="${!registre.anulat}">
							<a href="<c:url value="/expedient/anular.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmarAnular(event)"><img src="<c:url value="/img/delete.png"/>" alt="<fmt:message key="comuns.anular"/>" title="<fmt:message key="comuns.anular"/>" border="0"/></a>
						</c:if>
					</security:accesscontrollist>
				</display:column>
				<display:column>
					<security:accesscontrollist domainObject="${registre.tipus}" hasPermission="16,8">
						<a href="<c:url value="/expedient/delete.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmarEsborrar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key="comuns.esborrar"/>" title="<fmt:message key="comuns.esborrar"/>" border="0"/></a>
					</security:accesscontrollist>
				</display:column>
			</display:table>
			<script type="text/javascript">initSelectable();</script>
			<c:if test="${command.massivaActiu}">
				<form action="<c:url value="/expedient/massivaInfo.html"/>">
					<button type="submit" class="submitButton"><fmt:message key="expedient.consulta.massiva.accions"/></button>
				</form>
			</c:if>
		</c:if>
	</c:if>

</body>
</html>
