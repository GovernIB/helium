<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<c:if test="${not empty campTascaActual}">
	<c:set var="campActual" value="${campTascaActual.camp}" scope="request"/>
	<c:set var="readOnly" value="${campTascaActual.readOnly}" scope="request"/>
	<c:set var="required" value="${campTascaActual.required}" scope="request"/>
</c:if>
<c:choose>
	<c:when test="${not empty campActual.definicioProces}"><c:set var="codiActual" value="${campActual.definicioProces.jbpmKey}_${campActual.codi}" scope="request"/></c:when>
	<c:otherwise><c:set var="codiActual" value="${campActual.codi}" scope="request"/></c:otherwise>
</c:choose>
<c:set var="valorActual" value="${command[codiActual]}" scope="request"/>
<c:set var="extraParams">definicioProcesId:${campActual.definicioProces.id},campCodi:'${campActual.codi}',valors:function(){return canvisSelectValorsAddicionals}</c:set>
<c:choose>
	<c:when test="${campActual.tipus == 'STRING'}">
		<c:import url="common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="label">${campActual.etiqueta}</c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'INTEGER'}">
		<c:import url="common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="type" value="custom"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="content">
				<spring:bind path="${codiActual}[0]">
					<label for="${codiActual}0" class="blockLabel"><fmt:message key='common.campfiltre.entre' />
						<input id="${codiActual}0" name="${codiActual}" value="${status.value}" type="text" class="inputText" style="text-align:right"/>
					</label>
				</spring:bind>
				<spring:bind path="${codiActual}[1]">
					<label for="${codiActual}1" class="blockLabel blockLabelLast"><fmt:message key='common.campfiltre.i' />
						<input id="${codiActual}1" name="${codiActual}" value="${status.value}" type="text" class="inputText" style="text-align:right"/>
					</label>
				</spring:bind>
				<script type="text/javascript">
					// <![CDATA[
					$(function() {$("#${codiActual}0").keyfilter(/[\d\-]/);});
					$(function() {$("#${codiActual}1").keyfilter(/[\d\-]/);});
					// ]]>
				</script>
			</c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'FLOAT'}">
		<c:import url="common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="type" value="custom"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="content">
				<spring:bind path="${codiActual}[0]">
					<label for="${codiActual}0" class="blockLabel"><fmt:message key='common.campfiltre.entre' />
						<input id="${codiActual}0" name="${codiActual}" value="${status.value}" type="text" class="inputText" style="text-align:right"/>
					</label>
				</spring:bind>
				<spring:bind path="${codiActual}[1]">
					<label for="${codiActual}1" class="blockLabel blockLabelLast"><fmt:message key='common.campfiltre.i' />
						<input id="${codiActual}1" name="${codiActual}" value="${status.value}" type="text" class="inputText" style="text-align:right"/>
					</label>
				</spring:bind>
				<script type="text/javascript">
					// <![CDATA[
					$(function() {$("#${codiActual}0").keyfilter(/[\d\-\.]/);});
					$(function() {$("#${codiActual}1").keyfilter(/[\d\-\.]/);});
					// ]]>
				</script>
			</c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'DATE'}">
		<c:import url="common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="type" value="custom"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="content">
				<c:choose>
					<c:when test="${not fn:contains(codiActual, '$')}"><c:set var="codiActualJquery" value="${codiActual}"/></c:when>
					<c:otherwise><c:set var="codiActualJquery" value="${fn:replace(codiActual,'$','_')}"/></c:otherwise>
				</c:choose>
				<spring:bind path="${codiActual}[0]">
					<div class="span5 input-append date datepicker">
						<label for="${codiActual}0" class="blockLabel"><fmt:message key='common.campfiltre.entre' />
							<input id="${codiActualJquery}0" name="${codiActual}" value="${status.value}" type="text" class="span4"/>
							<span class="add-on" onclick="$('#${codiActualJquery}0').focus()"><i class="icon-calendar"></i></span>
							<script type="text/javascript">
								// <![CDATA[
									$("#${codiActualJquery}0").mask("99/99/9999");
									$("#${codiActualJquery}0").datepicker({language: 'ca', autoclose: true});
								// ]]>
							</script>
						</label>
					</div>
				</spring:bind>						
				<spring:bind path="${codiActual}[1]">
					<div class="span5 input-append date datepicker">
						<label for="${codiActual}1" class="blockLabel blockLabelLast"><fmt:message key='common.campfiltre.i' />
							<input id="${codiActualJquery}1" name="${codiActual}" value="${status.value}" type="text" class="span4"/>
							<span class="add-on" onclick="$('#${codiActualJquery}1').focus()"><i class="icon-calendar"></i></span>
							<script type="text/javascript">
								// <![CDATA[
									$("#${codiActualJquery}1").mask("99/99/9999");
									$("#${codiActualJquery}1").datepicker({language: 'ca', autoclose: true});
								// ]]>
							</script>
						</label>
					</div>
				</spring:bind>
			</c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'PRICE'}">
		<c:import url="common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="type" value="custom"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="content">
				<spring:bind path="${codiActual}[0]">
					<label for="${codiActual}0" class="blockLabel"><fmt:message key='common.campfiltre.entre' />
						<input id="${codiActual}0" name="${codiActual}" value="${status.value}" type="text" class="inputText" style="text-align:right"/>
					</label>
				</spring:bind>
				<spring:bind path="${codiActual}[1]">
					<label for="${codiActual}1" class="blockLabel blockLabelLast"><fmt:message key='common.campfiltre.i' />
						<input id="${codiActual}1" name="${codiActual}" value="${status.value}" type="text" class="inputText" style="text-align:right"/>
					</label>
				</spring:bind>
				<script type="text/javascript">
					// <![CDATA[
					$(function() {$("#${codiActual}0").setMask({mask:'99,999.999.999.999',type:'reverse'});});
					$(function() {$("#${codiActual}1").setMask({mask:'99,999.999.999.999',type:'reverse'});});
					// ]]>
				</script>
			</c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'BOOLEAN'}">
		<c:import url="common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="type" value="select"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="items" value="valorsBoolea"/>
			<c:param name="itemLabel" value="valor"/>
			<c:param name="itemValue" value="codi"/>
			<c:param name="itemBuit">&lt;&lt; <fmt:message key='common.campfiltre.item_buit' /> &gt;&gt;</c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'TEXTAREA'}">
		<c:import url="common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="label">${campActual.etiqueta}</c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'SELECCIO'}">
		<c:choose>
			<c:when test="${codiActual == 'expedient$estat'}">
				<c:import url="common/formElement.jsp">
					<c:param name="property" value="${codiActual}"/>
					<c:param name="type" value="select"/>
					<c:param name="label">${campActual.etiqueta}</c:param>
					<c:param name="items" value="estats"/>
					<c:param name="itemLabel" value="nom"/>
					<c:param name="itemValue" value="codi"/>
					<c:param name="itemBuit">&lt;&lt; <fmt:message key='expedient.consulta.select.estat'/> &gt;&gt;</c:param>
				</c:import>
			</c:when>
			<c:otherwise>
				<c:import url="common/formElement.jsp">
					<c:param name="property">${codiActual}</c:param>
					<c:param name="type" value="select"/>
					<c:param name="label">${campActual.etiqueta}</c:param>
					<c:param name="selectUrl"><c:url value="/v3/domini/consultaExpedient"/></c:param>
					<c:param name="selectExtraParams">${extraParams},tipus:'select'</c:param>
					<c:param name="onchange">canviSelectTasca(this.id, this.name, '${fn:substringBefore(codiActual, '_')}_');</c:param>
				</c:import>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:when test="${campActual.tipus == 'SUGGEST'}">
		<c:set var="multipleSuggestText" value="${valorsPerSuggest[codiActual]}" scope="request"/>
		<c:import url="common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="type" value="suggest"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="suggestUrl"><c:url value="/v3/domini/consultaExpedient"/></c:param>
			<c:param name="suggestExtraParams">${extraParams},tipus:'suggest'</c:param>
			<c:param name="suggestText"><c:if test="${not empty tasca.valorsDomini[codiActual]}">${tasca.valorsDomini[codiActual].valor}</c:if></c:param>
			<c:param name="multipleSuggestText">multipleSuggestText</c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'TERMINI'}">
		<c:import url="common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="required">${required}</c:param>
			<c:param name="type" value="custom"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="content">
				<spring:bind path="${codiActual}">
					<c:set var="anys" value="${fn:split(status.value,'/')[0]}"/>
					<label for="${codiActual}_anys" class="blockLabel">
						<%--span>Anys</span--%>
						<select id="${codiActual}_anys" name="${codiActual}_anys" onchange="canviTermini(this)">
							<c:forEach var="index" begin="0" end="12">
								<option value="${index}"<c:if test="${anys==index}"> selected="selected"</c:if>>${index}</option>
							</c:forEach>
						</select>
					</label>
					<c:set var="mesos" value="${fn:split(status.value,'/')[1]}"/>
					<label for="${codiActual}_mesos" class="blockLabel">
						<%--span>Mesos</span--%>
						<select id="${codiActual}_mesos" name="${codiActual}_mesos" onchange="canviTermini(this)">
							<c:forEach var="index" begin="0" end="12">
								<option value="${index}"<c:if test="${mesos==index}"> selected="selected"</c:if>>${index}</option>
							</c:forEach>
						</select>
					</label>
					<c:set var="dies" value="${fn:split(status.value,'/')[2]}"/>
					<label for="${codiActual}_dies" class="blockLabel">
						<%--span>Dies</span--%>
						<input id="${codiActual}_dies" name="${codiActual}_dies" value="${dies}" class="textInput" onchange="canviTermini(this)"/>
					</label>
					<form:hidden path="${codiActual}"/>
				</spring:bind>
			</c:param>
		</c:import>
	</c:when>
	<c:otherwise>
		<c:import url="common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="required">${required}</c:param>
			<c:param name="label">${campActual.etiqueta}</c:param>
		</c:import>
	</c:otherwise>
</c:choose>
<%!
private String toJavascript(String str) {
	if (str == null)
		return null;
	return str.replace("'", "\\'");
//		replace("{", "").
//		replace("}", "").
//		replace("#", "");
}
%>