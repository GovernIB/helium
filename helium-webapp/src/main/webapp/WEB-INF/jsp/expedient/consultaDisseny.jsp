<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title><fmt:message key='expedient.consulta.cons_disseny' /></title>
	<meta name="titolcmp" content="<fmt:message key='comuns.consultes' />" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function confirmarEsborrar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquest expedient?");
}
function confirmarAnular(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu anul·lar aquest expedient?");
}
// ]]>
</script>
</head>
<body>

	<div class="missatgesGris">
		<c:choose>
			<c:when test="${empty consulta}">
				<h4 class="titol-consulta"><fmt:message key='expedient.consulta.select.consula' /></h4>
				<form:form action="consultaDisseny.html" commandName="commandSeleccioConsulta" cssClass="uniForm">
					<div class="inlineLabels col first">
						<input type="hidden" name="canviar" id="canviar" value="true"/>
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="expedientTipusId"/>
							<c:param name="type" value="select"/>
							<c:param name="items" value="expedientTipus"/>
							<c:param name="itemLabel" value="nom"/>
							<c:param name="itemValue" value="id"/>
							<c:param name="itemBuit">&lt;&lt; <fmt:message key='expedient.consulta.select.tipusexpedient'/> &gt;&gt;</c:param>
							<c:param name="label"><fmt:message key='expedient.consulta.tipusexpedient' /></c:param>
							<c:param name="onchange">this.form.submit()</c:param>
						</c:import>
						<c:if test="${not empty commandSeleccioConsulta.expedientTipusId}">
							<c:import url="../common/formElement.jsp">
								<c:param name="property" value="consultaId"/>
								<c:param name="type" value="select"/>
								<c:param name="items" value="consultes"/>
								<c:param name="itemLabel" value="nom"/>
								<c:param name="itemValue" value="id"/>
								<c:param name="itemBuit">&lt;&lt; <fmt:message key='expedient.consulta.select.consula'/> &gt;&gt;</c:param>
								<c:param name="label"><fmt:message key='expedient.consulta.consulta' /></c:param>
								<c:param name="onchange">this.form.submit()</c:param>
							</c:import>
						</c:if>
					</div>
				</form:form>
			</c:when>
			<c:otherwise>
				<h4 class="titol-consulta" style="display:inline">${consulta.nom}</h4>&nbsp;&nbsp;&nbsp;
				<form action="consultaDisseny.html" method="post" style="display:inline"><input type="hidden" name="canviar" id="canviar" value="true"/><button type="submit" class="submitButton"><fmt:message key='expedient.consulta.canviar' /></button></form>
			</c:otherwise>
		</c:choose>
	</div>

	<c:if test="${not empty campsFiltre}">
		<form:form action="consultaDissenyResultat.html" commandName="commandFiltre" cssClass="uniForm">
			<div class="inlineLabels col first">
				<c:forEach var="camp" items="${campsFiltre}">
					<c:set var="campActual" value="${camp}" scope="request"/>
					<c:set var="readonly" value="${false}" scope="request"/>
					<c:set var="required" value="${false}" scope="request"/>
					<c:import url="../common/campFiltre.jsp"/>
				</c:forEach>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,netejar</c:param>
					<c:param name="titles"><fmt:message key='expedient.consulta.consultar' />,<fmt:message key='expedient.consulta.netejar' /></c:param>
				</c:import>
			</div>
		</form:form>
		<c:if test="${not empty sessionScope.expedientTipusConsultaFiltreCommand}">
			<display:table name="expedients" id="registre" requestURI="" class="displaytag selectable" export="${consulta.exportarActiu}">
				<c:set var="filaStyle" value=""/>
				<c:if test="${registre.expedient.anulat}"><c:set var="filaStyle" value="text-decoration:line-through"/></c:if>
				<display:column property="expedient.identificador" title="Expedient" sortable="true" url="/tasca/personaLlistat.html" paramId="exp" paramProperty="expedient.identificador" style="${filaStyle}"/>
				<c:choose>
					<c:when test="${empty campsInforme}">
						<display:column property="expedient.dataInici" title="Iniciat el" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true" style="${filaStyle}"/>
						<display:column property="expedient.tipus.nom" title="Tipus" style="${filaStyle}"/>
						<display:column title="Estat" style="${filaStyle}">
							<c:if test="${registre.expedient.aturat}"><img src="<c:url value="/img/stop.png"/>" alt="Aturat" title="Aturat" border="0"/></c:if>
							<c:choose>
								<c:when test="${empty registre.expedient.dataFi}">
									<c:choose><c:when test="${empty registre.expedient.estat}"><fmt:message key='expedient.consulta.iniciat' /></c:when><c:otherwise>${registre.expedient.estat.nom}</c:otherwise></c:choose>
								</c:when>
								<c:otherwise><fmt:message key='expedient.consulta.finalitzat' /></c:otherwise>
							</c:choose>
						</display:column>
					</c:when>
					<c:otherwise>
						<c:forEach var="camp" items="${campsInforme}">
							<c:set var="tipusCamp" value="${camp.tipus}"/>
							<c:set var="clauCamp" value="${camp.definicioProces.jbpmKey}/${camp.codi}"/>
							<c:set var="valorCamp" value="${registre.dadesExpedient[clauCamp].valor}"/>
							<c:set var="textCamp" value="${registre.dadesExpedient[clauCamp].valorMostrar}"/>
							<c:choose>
								<c:when test="${tipusCamp == 'DATE'}">
									<display:column property="dadesExpedient(${clauCamp}).valor" title="${camp.etiqueta}" format="{0,date,dd/MM/yyyy}" sortable="true" style="${filaStyle}"/>
								</c:when>
								<c:when test="${tipusCamp == 'INTEGER'}">
									<display:column property="dadesExpedient(${clauCamp}).valor" title="${camp.etiqueta}" format="{0,number,#}" sortable="true" style="${filaStyle}"/>
								</c:when>
								<c:when test="${tipusCamp == 'FLOAT'}">
									<display:column property="dadesExpedient(${clauCamp}).valor" title="${camp.etiqueta}" format="{0,number,#.#}" sortable="true" style="${filaStyle}"/>
								</c:when>
								<c:when test="${tipusCamp == 'PRICE'}">
									<display:column property="dadesExpedient(${clauCamp}).valor" title="${camp.etiqueta}" format="{0,number,#,###.00}" sortable="true" style="${filaStyle}"/>
								</c:when>
								<c:otherwise>
									<display:column property="dadesExpedient(${clauCamp}).valorMostrar" title="${camp.etiqueta}" sortable="true" style="${filaStyle}"/>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				<security:accesscontrollist domainObject="${consulta.expedientTipus}" hasPermission="16,1">
					<display:column media="html">
						<a href="<c:url value="/expedient/info.html"><c:param name="id" value="${registre.expedient.processInstanceId}"/></c:url>"><img src="<c:url value="/img/information.png"/>" alt="<fmt:message key='comuns.informacio' />" title="<fmt:message key='comuns.informacio' />" border="0"/></a>
					</display:column>
				</security:accesscontrollist>
				<security:accesscontrollist domainObject="${consulta.expedientTipus}" hasPermission="16,2">
					<display:column media="html">
						<c:if test="${!registre.expedient.anulat}">
							<a href="<c:url value="/expedient/anular.html"><c:param name="id" value="${registre.expedient.id}"/></c:url>" onclick="return confirmarAnular(event)"><img src="<c:url value="/img/delete.png"/>" alt="<fmt:message key='comuns.anular' />" title="<fmt:message key='comuns.anular' />" border="0"/></a>
						</c:if>
					</display:column>
				</security:accesscontrollist>
				<security:accesscontrollist domainObject="${consulta.expedientTipus}" hasPermission="16,8">
					<display:column media="html">
						<a href="<c:url value="/expedient/delete.html"><c:param name="id" value="${registre.expedient.id}"/></c:url>" onclick="return confirmarEsborrar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
					</display:column>
				</security:accesscontrollist>
				<display:setProperty name="export.csv" value="false" />
				<display:setProperty name="export.xml" value="false" />
				<display:setProperty name="export.decorated" value="false" />
				<display:setProperty name="export.excel.filename" value="informe_${consulta.codi}.xls" />
			</display:table>
			<script type="text/javascript">initSelectable();</script>
			<c:if test="${not empty consulta.informeNom and not empty campsInforme}">
				<form action="<c:url value="consultaDissenyInforme.html"/>">
					<input type="hidden" name="caonsultaId" id="caonsultaId" value="${consulta.id}"/>
					<button type="submit" class="submitButton"><fmt:message key='expedient.consulta.informe' /></button>
				</form>
			</c:if>
		</c:if>
	</c:if>

</body>
</html>
