<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="campInline" value="${inline}"/>
<c:set var="obligatorio"><c:if test="${dada.required}"> data-required="true"</c:if></c:set>
	<c:set var="campErrors"><form:errors path="${campCodi}"/></c:set>
	<div class="form-group <c:if test='${dada.campMultiple or isMultiple}'> multiple_camp</c:if><c:if test="${not empty campErrors}"> has-error</c:if><c:if test="${tasca.validada}"> validada</c:if><c:if test="${not empty tasca.tascaFormExternCodi}"> formext</c:if>">
		<label for="${dada.varCodi}" class="control-label<c:choose><c:when test='${inline}'> sr-only</c:when><c:otherwise><c:if test="${dada.required}"> obligatori</c:if></c:otherwise></c:choose>" <c:if test='${not inline}'> style="width: ${ampleLabel}; float: left; padding-right: 11px;"</c:if>>${dada.campEtiqueta}</label>
		<div class="controls<c:if test='${not inline}'> like-cols</c:if> <c:if test='${dada.campMultiple or isMultiple}'> multiple_camp</c:if> <c:if test="${!dada.required}"> no-obligatori</c:if>" <c:if test='${not inline}'> style="width: ${ampleInput};"</c:if>>

<%-- VARIABLES SENZILLES ----------------------------------------------------------------------------%>
<%---------------------------------------------------------------------------------------------------%>
			
<%-- STRING -------------------------------------------------------------------------------------%>
			<c:if test="${dada.campTipus == 'STRING'}">
				<c:choose>
					<c:when test='${dada.campMultiple or isMultiple}'><input type="text" id="${campCodi}" name="${campNom}" class="form-control" data-required="${dada.required}" value="${command[campNom][campIndex]}"/></c:when>
					<c:otherwise><form:input path="${campCodi}" cssClass="form-control" id="${campCodi}" data-required="${dada.required}" /></c:otherwise>
				</c:choose>
			</c:if>
			
<%-- TEXTAREA -----------------------------------------------------------------------------------%>
			<c:if test="${dada.campTipus == 'TEXTAREA'}">
				<c:choose>
					<c:when test='${dada.campMultiple or isMultiple}'><textarea id="${campCodi}" name="${campNom}" class="form-control" data-required="${dada.required}" rows="6">${command[campNom][campIndex]}</textarea></c:when>
					<c:otherwise><form:textarea path="${campCodi}" cssClass="form-control" id="${campCodi}" data-required="${dada.required}" rows="6"/></c:otherwise>
				</c:choose>
			</c:if>
			
<%-- INTEGER, FLOAT i PRICE ---------------------------------------------------------------------%>
			<c:if test="${dada.campTipus == 'INTEGER' or dada.campTipus == 'FLOAT' or dada.campTipus == 'PRICE'}">
			<c:set var="tipusnum"><c:choose><c:when test="${dada.campTipus == 'INTEGER'}">enter</c:when><c:when test="${dada.campTipus == 'FLOAT'}">float</c:when><c:when test="${dada.campTipus == 'PRICE'}">price</c:when></c:choose></c:set>
				<c:choose>
					<c:when test='${dada.campMultiple or isMultiple}'><input type="text" id="${campCodi}" name="${campNom}" class="form-control text-right ${tipusnum}" data-required="${dada.required}" value="${command[campNom][campIndex]}"/></c:when>
					<c:otherwise><form:input path="${campCodi}" cssClass="form-control text-right ${tipusnum}" id="${campCodi}" data-required="${dada.required}"/></c:otherwise>
				</c:choose>
			</c:if>
			
<%-- DATE ---------------------------------------------------------------------------------------%>		
			<c:if test="${dada.campTipus == 'DATE'}">
				<div class="input-group">
					<c:choose>
						<c:when test='${(dada.campMultiple or isMultiple)}'>
								<fmt:formatDate value="${command[campNom][campIndex]}" var="formattedDate" type="date" pattern="dd/MM/yyyy" />
								<input type="text" id="${campCodi}" name="${campNom}" class="form-control date" placeholder="dd/mm/aaaa" data-required="${dada.required}" value="${formattedDate}"/>
						</c:when>
						<c:otherwise>
								<c:set var="formattedDate" value="${command[campNom]}"/>
								<form:input path="${campCodi}" id="${campCodi}" cssClass="date form-control" placeholder="dd/mm/aaaa" data-required="${dada.required}"/>
						</c:otherwise>
					</c:choose>
					<span class="input-group-addon btn_date"><span class="fa fa-calendar"></span></span>
				</div>
			</c:if>
			
<%-- TERMINI ------------------------------------------------------------------------------------%>
			<c:if test="${dada.campTipus == 'TERMINI'}">
				<c:set var="tercodi"><c:choose><c:when test='${dada.campMultiple or isMultiple}'>${campNom}[${campIndex}]</c:when><c:otherwise>${campCodi}</c:otherwise></c:choose></c:set>
				<c:if test='${dada.campMultiple or isMultiple}'><input type="hidden" id="${campCodi}" name="${campNom}"/></c:if>
				<div class="form-group termgrup">
					<div class="tercpre">
						<label class="control-label label-term" for="${campCodi}_anys"><spring:message code="common.camptasca.anys"/></label>
						<c:choose>
							<c:when test='${dada.campMultiple or isMultiple}'>
								<select id="${tercodi}_anys" name="${tercodi}[0]" class="termini">
									<c:forEach var="opt" items="${listTerminis}">
										<option value="${opt.codi}" <c:if test="${opt.codi == command[campNom][campIndex][0]}"> selected</c:if>>${opt.valor}</option>
									</c:forEach>
								</select>
							</c:when>
							<c:otherwise><form:select disabled="${tasca.validada}" itemLabel="valor" itemValue="codi" items="${listTerminis}" path="${campCodi}[0]" id="${campCodi}_anys" cssClass="termini" /></c:otherwise>
						</c:choose>
					</div>
					<div class="tercmig">
	 					<label class="control-label label-term" for="${campCodi}_mesos"><spring:message code="common.camptasca.mesos"/></label>
 						<c:choose>
							<c:when test='${dada.campMultiple or isMultiple}'>
								<select id="${tercodi}_mesos" name="${tercodi}[1]" class="termini">
									<c:forEach var="opt" items="${listTerminis}">
										<option value="${opt.codi}" <c:if test="${opt.codi == command[campNom][campIndex][1]}"> selected</c:if>>${opt.valor}</option>
									</c:forEach>
								</select>
							</c:when>
							<c:otherwise><form:select disabled="${tasca.validada}" itemLabel="valor" itemValue="codi" items="${listTerminis}" path="${campCodi}[1]" id="${campCodi}_mesos" cssClass="termini" /></c:otherwise>
						</c:choose>
	 				</div>
	 				<div class="tercpost">
	 					<label class="control-label label-term" for="${campCodi}_dies"><spring:message code="common.camptasca.dies"/></label>
	 					<c:set var="placeholderDies"><spring:message code='common.camptasca.dies'/></c:set>
 						<c:choose>
							<c:when test='${dada.campMultiple or isMultiple}'><input type="text" id="${tercodi}_dies" name="${tercodi}[2]" class="form-control" data-required="${dada.required}" value="${command[campNom][campIndex][2]}" placeholder="${placeholderDies}"/></c:when>
							<c:otherwise><form:input path="${campCodi}[2]" id="${campCodi}_dies" cssClass="form-control" placeholder="${placeholderDies}" data-required="${dada.required}"/></c:otherwise>
						</c:choose>
	 				</div>
	 			</div>
			</c:if>
			
<%-- BOOLEAN ------------------------------------------------------------------------------------%>					
			<c:if test="${dada.campTipus == 'BOOLEAN'}">
				<c:choose>
					<c:when test='${dada.campMultiple or isMultiple}'>
						<div class="inputcheck">
							<input type="hidden" id="${campCodi}" name="${campNom}" value="${command[campNom][campIndex]}"/>
							<input type="checkbox" class="checkbox checkboxmul" data-required="${dada.required}" <c:if test="${command[campNom][campIndex]}">checked</c:if>/>
						</div>
					</c:when>
					<c:otherwise><form:checkbox path="${campCodi}" id="${campCodi}" data-required="${dada.required}" style="max-width: 27px;" cssClass="checkbox"/></c:otherwise>
				</c:choose>
			</c:if>
			
<%-- ACCIO --------------------------------------------------------------------------------------%>					
			<c:if test="${!bloquejarEdicioTasca and dada.campTipus == 'ACCIO'}">
				<button  class="btn btn-primary pull-lef btn_accio tasca-boto" name="accio" type="submit" value="accio" data-action="${dada.jbpmAction}" data-confirmacio="<spring:message code='js.helforms.confirmacio' />">
					<spring:message code="common.camptasca.executar" />
				</button>
				
				<c:if test="${not empty campFocus}">
					<script>
						campOnFocus(${dada.jbpmAction});
					</script>
				</c:if>
			</c:if>
			
<%-- SUGGEST ------------------------------------------------------------------------------------%>
			<c:if test="${dada.campTipus == 'SUGGEST'}">
				<c:choose>
					<c:when test="${not empty tasca and not tasca.inicial}">
						<c:set var="urlConsultaInicial"><c:url value="/v3/camp/${dada.campId}/tasca/${tasca.id}/valor"/></c:set>
						<c:set var="urlConsultaLlistat"><c:url value="/v3/camp/${dada.campId}/tasca/${tasca.id}/valors"/></c:set>
					</c:when>
					<c:otherwise>
					<c:if test="${not empty procesId}">
							<c:set var="urlConsultaInicial"><c:url value="/v3/camp/${dada.campId}/proces/${procesId}/valor"/></c:set>
							<c:set var="urlConsultaLlistat"><c:url value="/v3/camp/${dada.campId}/proces/${procesId}/valors"/></c:set>
						</c:if>
						<c:if test="${empty procesId}">
							<c:set var="urlConsultaInicial"><c:url value="/v3/camp/${dada.campId}/valor"/></c:set>
							<c:set var="urlConsultaLlistat"><c:url value="/v3/camp/${dada.campId}/valors"/></c:set>
						</c:if>
					</c:otherwise>
				</c:choose>
				<c:set var="placeholder"><spring:message code='js.helforms.selec_valor'/></c:set>
				<c:choose>
					<c:when test='${dada.campMultiple or isMultiple}'><input type="text" id="${campCodi}" name="${campNom}" class="form-control suggest" <c:if test="${disabled}">disabled </c:if>value="${command[campNom][campIndex]}" data-placeholder="${placeholder}" data-urlconsultainicial="${urlConsultaInicial}" data-urlconsultallistat="${urlConsultaLlistat}" data-campparams="${dada.campParamsConcatenats}"/></c:when>
					<c:otherwise><form:input path="${campCodi}" cssClass="form-control suggest" id="${campCodi}" disabled="${disabled}" data-placeholder="${placeholder}" data-urlconsultainicial="${urlConsultaInicial}" data-urlconsultallistat="${urlConsultaLlistat}" data-campparams="${dada.campParamsConcatenats}"/></c:otherwise>
				</c:choose>
			</c:if>
			
<%-- SELECCIO ------------------------------------------------------------------------------------%>					
			<c:if test="${dada.campTipus == 'SELECCIO'}">
				<c:choose>
					<c:when test="${not empty tasca and not tasca.inicial}">
						<c:set var="urlConsultaInicial"><c:url value="/v3/camp/${dada.campId}/tasca/${tasca.id}/valor"/></c:set>
						<c:set var="urlConsultaLlistat"><c:url value="/v3/camp/${dada.campId}/tasca/${tasca.id}/valors"/></c:set>
					</c:when>
					<c:otherwise>
						<c:if test="${not empty procesId}">
							<c:set var="urlConsultaInicial"><c:url value="/v3/camp/${dada.campId}/proces/${procesId}/valor"/></c:set>
							<c:set var="urlConsultaLlistat"><c:url value="/v3/camp/${dada.campId}/proces/${procesId}/valors"/></c:set>
						</c:if>
						<c:if test="${empty procesId}">
							<c:set var="urlConsultaInicial"><c:url value="/v3/camp/${dada.campId}/valor"/></c:set>
							<c:set var="urlConsultaLlistat"><c:url value="/v3/camp/${dada.campId}/valors"/></c:set>
						</c:if>
					</c:otherwise>
				</c:choose>
				<c:set var="placeholder"><spring:message code="js.helforms.selec_valor"/></c:set>
				<c:choose>
					<c:when test="${dada.campMultiple or isMultiple}">
						<input type="text" id="${campCodi}" name="${campNom}" class="seleccio" data-required="${dada.required}" data-campid="${dada.campId}" data-placeholder="${placeholder}" value="${command[campNom][campIndex]}" data-urlconsultainicial="${urlConsultaInicial}" data-urlconsultallistat="${urlConsultaLlistat}" data-campparams="${dada.campParamsConcatenats}"/>
					</c:when>
					<c:otherwise>
						<form:input path="${campCodi}" id="${campCodi}" cssClass="seleccio" data-required="${dada.required}" data-campid="${dada.campId}" data-placeholder="${placeholder}" data-urlconsultainicial="${urlConsultaInicial}" data-urlconsultallistat="${urlConsultaLlistat}" data-campparams="${dada.campParamsConcatenats}"/>
					</c:otherwise>
				</c:choose>
			</c:if>
			
<%-- Fi VARIABLES SENZILLES -------------------------------------------------------------------------%>
<%---------------------------------------------------------------------------------------------------%>

<%-- VARIABLES MULTIPLES ----------------------------------------------------------------------------%>
<%---------------------------------------------------------------------------------------------------%>
			<c:if test="${not tasca.validada and ((dada.campMultiple and isRegistre) or isMultiple)}">
					<button  
						class="btn btn_eliminar fa fa-times" 
						type="button" 
						value="<spring:message code='comuns.esborrar' />" 
						title="<spring:message code='comuns.esborrar' />">
					</button>
			</c:if>
			<c:if test="${not inline and not empty dada.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p></c:if>
			<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;${campErrors}</p></c:if>
		</div>	<%-- Fi div controls--%>
	</div>	<%-- Fi div form-group--%>

	<c:if test="${dada.campMultiple and isRegistre}">
		<div class="form-group condensed">
			<c:if test="${not empty dada.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p></c:if>
			<button id="button_add_var_mult_${campCodi}" type="button" class="btn pull-left btn_multiple"><spring:message code='comuns.afegir' /></button>
		</div>
	</c:if>
<%-- Fi VARIABLES MULTIPLES -------------------------------------------------------------------------%>
<%---------------------------------------------------------------------------------------------------%>
<%-- </c:if> --%>

<c:set var="campCodi" value=""/>
<c:set var="campInline" value="${false}"/>
<c:set var="campErrors" value=""/>
