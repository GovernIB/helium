<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ page import="java.util.List" %>
<c:set var="sessionCommand" value="${sessionScope.expedientTipusConsultaDissenyCommandTE}"/>
<html>
<head>
	<title><fmt:message key="expedient.consulta.cons_disseny"/></title>
	<meta name="titolcmp" content="<fmt:message key="comuns.consultes"/>" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[

	function confirmarEsborrar(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm("<fmt:message key="expedient.consulta.confirm.esborrar"/>");
	}
	function confirmarAnular(e, registre) {
		var resposta="";
		$("#id").val(registre);
		var e = e || window.event;
		e.cancelBubble = true;
		var confirmaAnula = confirm("<fmt:message key="expedient.consulta.confirm.anular"/>"); 
	 	if (confirmaAnula){	
	 		resposta = prompt("<fmt:message key="expedient.consulta.anulacio.motiu"/>",'');
	 		$("#motiu").val(resposta);
	 		if(resposta != null){
		 		document.forms["anularMot"].submit();
		 	}
	 	}
	 	if (e.stopPropagation) e.stopPropagation();
	}

	function clicCheckMassiu(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		var ids = new Array();
		ids.push((e.target || e.srcElement).value);
		$.post("massivaIdsTE.html", {
			expedientTipusId: "${consulta.expedientTipus.id}",
			expedientId: ids,
			checked: (e.target || e.srcElement).checked
		});
	}
	function selTots(){
		var ch = $("#selTots:checked").val();
		var ids = new Array();
		$("#registre input[type='checkbox'][name='expedientId']").each(function() {
			ids.push($(this).val());
		}).attr("checked", (!ch) ? false : true);
		$.post("massivaIdsTE.html", {
			expedientTipusId: "${consulta.expedientTipus.id}",
			expedientId: ids,
			checked: (!ch) ? false : true
		});
	}

	$(document).ready(function() {
		$("#modal-params").dialog({
            autoOpen: false,
            height: 400,
            width: 600,
            modal: true,
            resizable: true,
            buttons: {
                <fmt:message key="comuns.tancar" />: function() {
                    $(this).dialog("close");
                },
				Generar: function() {
					$("#paramsCommand").submit();
                }
            }
        });
		<c:if test="${not empty paramsInforme}">
		$("button.submitButton[value='informe']").on(
			"click",
			function() {
				$("#modal-params").dialog("open");
				return false;
			}
		);</c:if>
	
	});

	function informeClick(){
		$("button.submitButton[value='informe']")[0].click();
	}
	
	//executa consulta quan es clica el botó enter a un camp del filtre
	$(document)[0].onkeypress = function(e) {
        var key = e.which||e.keyCode;  
        if(key == 13)   
        {
        	$("button.submitButton[value='submit']")[0].click();
        }
    };
	
	
// ]]>
</script>
</head>
<body>

	<div class="missatgesGris">
		<c:choose>
			<c:when test="${empty consulta}">
				<h4 class="titol-consulta"><fmt:message key="expedient.consulta.select.consula"/></h4>
				<form:form action="consultaDisseny.html" commandName="commandSeleccioConsulta" cssClass="uniForm">
					<input type="hidden" name="expedientTipId" id="expedientTipId" value="${consulta.expedientTipus.id}">
					<div class="inlineLabels col first">
						<input type="hidden" name="canviar" id="canviar" value="true"/>
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="expedientTipusId"/>
							<c:param name="type" value="select"/>
							<c:param name="items" value="expedientTipus"/>
							<c:param name="itemLabel" value="nom"/>
							<c:param name="itemValue" value="id"/>
							<c:param name="itemBuit">&lt;&lt; <fmt:message key="expedient.consulta.select.tipusexpedient"/> &gt;&gt;</c:param>
							<c:param name="label"><fmt:message key="expedient.consulta.tipusexpedient"/></c:param>
							<c:param name="onchange">this.form.submit()</c:param>
						</c:import>
						<c:if test="${not empty commandSeleccioConsulta.expedientTipusId}">
							<c:import url="../common/formElement.jsp">
								<c:param name="property" value="consultaId"/>
								<c:param name="type" value="select"/>
								<c:param name="items" value="consultes"/>
								<c:param name="itemLabel" value="nom"/>
								<c:param name="itemValue" value="id"/>
								<c:param name="itemBuit">&lt;&lt; <fmt:message key="expedient.consulta.select.consula"/> &gt;&gt;</c:param>
								<c:param name="label"><fmt:message key="expedient.consulta.consulta"/></c:param>
								<c:param name="onchange">this.form.submit()</c:param>
							</c:import>
						</c:if>
					</div>
				</form:form>
			</c:when>
			<c:otherwise>
				<h4 class="titol-consulta" style="display:inline">${consulta.nom}</h4>&nbsp;&nbsp;&nbsp;
				<form action="consultaDisseny.html" method="post" style="display:inline">
					<input type="hidden" name="canviar" id="canviar" value="true"/>
					<button type="submit" class="submitButton"><fmt:message key="expedient.consulta.canviar"/></button>
				</form>
			</c:otherwise>
		</c:choose>
	</div>

	<c:if test="${not empty consulta}">

		<form:form action="consultaDissenyResultat.html" commandName="commandFiltre" cssClass="uniForm">
			<input type="hidden" name="idsExp" id="idsExp" value="${sessionScope.consultaExpedientsIdsMassiusTE}">
			<input type="hidden" name="expedientTipusId" id="expedientTipusId" value="${consulta.expedientTipus.id}">
			<div class="inlineLabels col first">
				<c:forEach var="camp" items="${campsFiltre}">
					<c:set var="campActual" value="${camp}" scope="request"/>
					<c:set var="readonly" value="${false}" scope="request"/>
					<c:set var="required" value="${false}" scope="request"/>
					<c:import url="../common/campFiltre.jsp"/>
				</c:forEach>

				<c:if test="${empty consulta.informeNom}">
					<c:import url="../common/formElement.jsp">
						<c:param name="type" value="buttons"/>
						<c:param name="values"><security:accesscontrollist domainObject="${entornActual}" hasPermission="16,2">ejecucionMasivaTotsTipus,</security:accesscontrollist>submit,netejar</c:param>
						<c:param name="titles"><security:accesscontrollist domainObject="${entornActual}" hasPermission="16,2"><fmt:message key="expedient.consulta.massiva.accions.totsTipus.consulta"/>,</security:accesscontrollist><fmt:message key="expedient.consulta.consultar"/>,<fmt:message key="expedient.consulta.netejar"/></c:param>
					</c:import>
				</c:if>
				<c:if test="${not empty consulta.informeNom}">
					<c:choose>
						<c:when test="${fn:length(expedients.list) > 0}">
							<c:import url="../common/formElement.jsp">
								<c:param name="type" value="buttons"/>
								<c:param name="values"><security:accesscontrollist domainObject="${entornActual}" hasPermission="16,2">ejecucionMasivaTotsTipus,</security:accesscontrollist>informe,submit,netejar</c:param>
								<c:param name="titles"><security:accesscontrollist domainObject="${entornActual}" hasPermission="16,2"><fmt:message key="expedient.consulta.massiva.accions.totsTipus.consulta"/>,</security:accesscontrollist><fmt:message key="expedient.consulta.informe"/>,<fmt:message key="expedient.consulta.consultar"/>,<fmt:message key="expedient.consulta.netejar"/></c:param>
							</c:import>
						</c:when>
						<c:otherwise>
							<c:import url="../common/formElement.jsp">
								<c:param name="type" value="buttons"/>
								<c:param name="values"><security:accesscontrollist domainObject="${entornActual}" hasPermission="16,2">ejecucionMasivaTotsTipus,</security:accesscontrollist>submit,netejar</c:param>
								<c:param name="titles"><security:accesscontrollist domainObject="${entornActual}" hasPermission="16,2"><fmt:message key="expedient.consulta.massiva.accions.totsTipus.consulta"/>,</security:accesscontrollist><fmt:message key="expedient.consulta.consultar"/>,<fmt:message key="expedient.consulta.netejar"/></c:param>
							</c:import>
						</c:otherwise>
					</c:choose>
				</c:if>
			</div>
			<c:if test="${not empty expedients}">
				<div class="ctrlHolder" align="right">
					<c:set var="opp"><c:if test='${empty objectsPerPage}'>20</c:if><c:if test='${not empty objectsPerPage}'>${objectsPerPage}</c:if></c:set>
					<c:set var="copp" value="opp-llista-tipus"/>
					<c:if test="${not empty expedients}">
						<c:set var="copp" value="opp-llista-mas"/>
					</c:if>
					<select id="objectsPerPage" name="objectsPerPage" class="objectsPerPage<c:if test='${not empty consulta}'> ${copp}</c:if>">
						<option value="10"<c:if test='${opp == "10"}'> selected="selected"</c:if>>10</option>
						<option value="20"<c:if test='${opp == "20"}'> selected="selected"</c:if>>20</option>
						<option value="50"<c:if test='${opp == "50"}'> selected="selected"</c:if>>50</option>
						<option value="100"<c:if test='${opp == "100"}'> selected="selected"</c:if>>100</option>
						<option value="999999999"<c:if test='${opp == "999999999"}'> selected="selected"</c:if>>Tots</option>
					</select>
					<label for="objectsPerPage" class="objectsPerPage<c:if test='${not empty consulta}'> ${copp}</c:if>"><fmt:message key="comuns.objectsPerPage"/></label>
				</div>
			</c:if>
		</form:form>
			
		<c:if test="${not empty sessionScope.expedientTipusConsultaFiltreCommand or not empty sessionScope.expedientTipusConsultaFiltreCommandTE}">
			
			<c:if test="${not empty expedients}">
				<table >
					<tr id="disnyTR">
						<td>
							<label><fmt:message key="expedient.consulta.massiva.accions"/></label>
						</td>
						<td>
							<form id="massivaInfoForm" action="<c:url value="/expedient/massivaInfoTE.html"/>">
								<input type="hidden" id="massivaInfoTots" name="massivaInfoTots" value="0"/>
								<button type="button" onclick="$('#massivaInfoTots').val(0);$('#massivaInfoForm').submit()" class="submitButton"><fmt:message key="expedient.consulta.massiva.accions.sel"/></button>
								<button type="button" class="submitButton" onclick="$('#massivaInfoTots').val(1);$('#massivaInfoForm').submit()"><fmt:message key="expedient.consulta.massiva.accions.tots"/></button>
							</form>
						</td>
					</tr>
				</table>
				<display:table name="expedients" id="registre" requestURI="" class="displaytag selectable" export="${consulta.exportarActiu}" sort="external">
					<display:column title="<input id='selTots' type='checkbox' value='false' onclick='selTots(event)'>" style="${filaStyle}" media="html">
						<c:set var="expedientSeleccionat" value="${false}"/>
						<c:forEach var="eid" items="${sessionScope.consultaExpedientsIdsMassiusTE}" varStatus="status">
							<c:if test="${status.index gt 0 and eid == registre.expedient.id}"><c:set var="expedientSeleccionat" value="${true}"/></c:if>
						</c:forEach>
						<input type="checkbox" name="expedientId" value="${registre.expedient.id}"<c:if test="${expedientSeleccionat}"> checked="checked"</c:if> onclick="clicCheckMassiu(event)"/>
					</display:column>
					<display:column property="expedient.identificador" title="Expedient" url="/tasca/personaLlistat.html" sortable="true" sortProperty="expedient$identificador" paramId="exp"/>
					<c:choose>
						<c:when test="${empty campsInforme}">
							<display:column property="expedient.dataInici" title="Iniciat el" sortable="true" sortProperty="expedient$dataInici" format="{0,date,dd/MM/yyyy HH:mm}"/>
							<display:column property="expedient.tipus.nom" title="Tipus" sortable="true" sortProperty="expedient.tipus.nom"/>
							<display:column title="Estat" sortable="true" sortProperty="estat.nom">
								<c:if test="${registre.expedient.aturat}"><img src="<c:url value="/img/stop.png"/>" alt="Aturat" title="Aturat" border="0"/></c:if>
								<c:choose>
									<c:when test="${empty registre.expedient.dataFi}">
										<c:choose><c:when test="${empty registre.expedient.estat}"><fmt:message key="expedient.consulta.iniciat"/></c:when><c:otherwise>${registre.expedient.estat.nom}</c:otherwise></c:choose>
									</c:when>
									<c:otherwise><fmt:message key="expedient.consulta.finalitzat"/></c:otherwise>
								</c:choose>
							</display:column>
						</c:when>
						<c:otherwise>
							<c:forEach var="camp" items="${campsInforme}">
								<c:set var="clauCamp" value="${camp.codiPerInforme}"/>
								<c:choose>
									<c:when test="${registre.dadesExpedient[clauCamp].multiple}">
										<display:column title="${camp.etiqueta}">
											<table class="displaytag">
												<c:forEach var="text" items="${registre.dadesExpedient[clauCamp].valorMostrarMultiple}" varStatus="status">
													<tr><td>${text}</td></tr>
												</c:forEach>
											</table>
										</display:column>
									</c:when>
									<c:when test="${camp.tipus == 'DATE' && clauCamp == 'expedient$dataInici'}">
										<display:column property="dadesExpedient(${clauCamp}).valor" title="${camp.etiqueta}" sortable="true" sortProperty="expedient$dataInici" format="{0,date,dd/MM/yyyy HH:mm}"/>
									</c:when>
									<c:when test="${camp.tipus == 'DATE'}">
										<display:column property="dadesExpedient(${clauCamp}).valor" title="${camp.etiqueta}" sortable="true" sortProperty="${fn:replace(fn:replace(clauCamp,'/','.'),'%','$')}" format="{0,date,dd/MM/yyyy}"/>
									</c:when>
									<c:when test="${camp.tipus == 'INTEGER'}">
										<display:column property="dadesExpedient(${clauCamp}).valor" title="${camp.etiqueta}" sortable="true" sortProperty="${fn:replace(fn:replace(clauCamp,'/','.'),'%','$')}" format="{0,number,#}"/>
									</c:when>
									<c:when test="${camp.tipus == 'FLOAT'}">
										<display:column property="dadesExpedient(${clauCamp}).valor" title="${camp.etiqueta}" sortable="true" sortProperty="${fn:replace(fn:replace(clauCamp,'/','.'),'%','$')}" format="{0,number,#.#}"/>
									</c:when>
									<c:when test="${camp.tipus == 'PRICE'}">
										<display:column property="dadesExpedient(${clauCamp}).valor" title="${camp.etiqueta}" sortable="true" sortProperty="${fn:replace(fn:replace(clauCamp,'/','.'),'%','$')}" format="{0,number,#,###.00}"/>
									</c:when>
									<c:when test="${camp.tipus == 'SELECCIO' && clauCamp == 'expedient%estat'}">
										<display:column title="${camp.etiqueta}" sortable="true" sortProperty="${fn:replace(fn:replace(clauCamp,'/','.'),'%','$')}">
											<c:if test="${registre.expedient.aturat}"><img src="<c:url value="/img/stop.png"/>" alt="Aturat" title="Aturat" border="0"/></c:if>
											<c:choose>
												<c:when test="${empty registre.expedient.dataFi}">
													<c:choose><c:when test="${empty registre.expedient.estat}"><fmt:message key="expedient.consulta.iniciat"/></c:when><c:otherwise>${registre.expedient.estat.nom}</c:otherwise></c:choose>
												</c:when>
												<c:otherwise><fmt:message key="expedient.consulta.finalitzat"/></c:otherwise>
											</c:choose>
										</display:column>
									</c:when>
									<c:otherwise>
										<display:column property="dadesExpedient(${clauCamp}).valorMostrar" title="${camp.etiqueta}" sortable="true" sortProperty="${fn:replace(fn:replace(clauCamp,'/','.'),'%','$')}"/>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</c:otherwise>
					</c:choose>
					<security:accesscontrollist domainObject="${consulta.expedientTipus}" hasPermission="16,1">
						<display:column media="html">
							<a href="<c:url value="/expedient/info.html"><c:param name="id" value="${registre.expedient.processInstanceId}"/></c:url>"><img src="<c:url value="/img/information.png"/>" alt="<fmt:message key="comuns.informacio"/>" title="<fmt:message key="comuns.informacio"/>" border="0"/></a>
						</display:column>
					</security:accesscontrollist>
					<security:accesscontrollist domainObject="${consulta.expedientTipus}" hasPermission="16,2">
						<display:column media="html">
							<c:if test="${!registre.expedient.anulat}">
								<a href="javascript:void(0);" onclick="confirmarAnular(event, ${registre.expedient.id})"><img src="<c:url value="/img/delete.png"/>" alt="<fmt:message key="comuns.anular"/>" title="<fmt:message key="comuns.anular"/>" border="0"/></a>
							</c:if>
						</display:column>
					</security:accesscontrollist>
					<security:accesscontrollist domainObject="${consulta.expedientTipus}" hasPermission="16,8">
						<display:column media="html">
							<a href="<c:url value="/expedient/delete.html"><c:param name="id" value="${registre.expedient.id}"/></c:url>" onclick="return confirmarEsborrar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key="comuns.esborrar"/>" title="<fmt:message key="comuns.esborrar"/>" border="0"/></a>
						</display:column>
					</security:accesscontrollist>
					<display:setProperty name="export.csv" value="false" />
					<display:setProperty name="export.xml" value="false" />
					<display:setProperty name="export.decorated" value="false" />
					<display:setProperty name="export.excel.filename" value="informe_${consulta.codi}.xls" />
					<display:setProperty name="paging.banner.item_name">expedient</display:setProperty>
					<display:setProperty name="paging.banner.items_name">expedients</display:setProperty>
				</display:table>
				<script type="text/javascript">initSelectable();</script>
			</c:if>
		</c:if>
		<c:if test="${not empty paramsInforme}">
			<div id="modal-params" title="<fmt:message key="expedient.consulta.params.modal.titol"/>">
				<form:form action="consultaDissenyInformeParams.html" cssClass="uniForm" commandName="paramsCommand">
					<div class="inlineLabels">
						<c:forEach var="par" items="${paramsInforme}">
							<c:choose>
								<c:when test="${par.paramTipus == 'SENCER'}">
									<c:set var="campTipus" value="number"/>
									<c:set var="campKeyFilter">/[\d\-]/</c:set>
								</c:when>
								<c:when test="${par.paramTipus == 'FLOTANT'}">
									<c:set var="campTipus" value="number"/>
									<c:set var="campKeyFilter">/[\d\-\.]/</c:set>
								</c:when>
								<c:when test="${par.paramTipus == 'DATA'}">
									<c:set var="campTipus" value="date"/>
									<c:set var="campMask">{mask: '39/19/9999', autoTab: false}</c:set>
								</c:when>
								<c:when test="${par.paramTipus == 'BOOLEAN'}">
									<c:set var="campTipus" value="checkbox"/>
								</c:when>
								<c:otherwise>
									<c:set var="campTipus" value="text"/>
								</c:otherwise>
							</c:choose>
							<c:import url="../common/formElement.jsp">
								<c:param name="property" value="${par.campCodi}"/>
								<c:param name="required" value="false"/>
								<c:param name="type" value="${campTipus}"/>
								<c:param name="keyFilter" value="${campKeyFilter}"/>
								<c:param name="mask" value="${campMask}"/>
								<c:param name="label">${par.campDescripcio}</c:param>
							</c:import>
						</c:forEach>
					</div>
				</form:form>
			</div>
		</c:if>
	</c:if>
	<form:form  method="GET" name="anularMot" id="anularMot" action="/helium/expedient/dissenyAnular.html?id=${registreId}&motiu=${param.motiu}"  cssClass="uniForm">
		<input type="hidden" id="id" name="id" value=""></input>
		<input type="hidden" id="motiu" name="motiu" value=""></input>
	</form:form>
</body>
</html>
