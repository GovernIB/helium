<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="campInline" value="${inline}"/>
<c:set var="obligatorio"><c:if test="${dada.required}"> data-required="true"</c:if></c:set>
<c:if test="${!dada.readOnly}">
	
<%-- 	<c:set var="campError"><c:choose><c:when test='${dada.campMultiple or isMultiple}'>${campNom}</c:when><c:otherwise>${campCodi}</c:otherwise></c:choose></c:set> --%>
	<c:set var="campErrors"><form:errors path="${campCodi}"/></c:set>
	<div class="form-group <c:if test='${dada.campMultiple or isMultiple}'> multiple_camp</c:if><c:if test="${not empty campErrors}"> has-error</c:if><c:if test="${tasca.validada}"> validada</c:if>">
		<label for="${dada.varCodi}" class="control-label<c:choose><c:when test='${inline}'> sr-only</c:when><c:otherwise> col-xs-3<c:if test="${dada.required}"> obligatori</c:if></c:otherwise></c:choose>">${dada.campEtiqueta}</label>
		<div class="controls<c:if test='${not inline}'> col-xs-9</c:if> <c:if test='${dada.campMultiple or isMultiple}'> multiple_camp</c:if>">



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
						<c:when test='${dada.campMultiple or isMultiple}'>
							<c:choose>
								<c:when test='${command[campNom][campIndex].class.name == "java.util.Date"}' ><fmt:formatDate value="${command[campNom][campIndex]}" var="formattedDate" type="date" pattern="dd/MM/yyyy" /></c:when>
								<c:otherwise><c:set var="formattedDate" value="${command[campNom][campIndex]}"/></c:otherwise>
							</c:choose>
							
							<input type="text" id="${campCodi}" name="${campNom}" class="form-control date" placeholder="dd/mm/yyyy" data-required="${dada.required}" value="${formattedDate}"/></c:when>
						<c:otherwise><form:input path="${campCodi}" id="${campCodi}" cssClass="date form-control" placeholder="dd/mm/yyyy" data-required="${dada.required}"/></c:otherwise>
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
								<select id="${tercodi}_anys" name="${tercodi}.anys" class="termini">
									<c:forEach var="opt" items="${listTerminis}">
										<option value="${opt.codi}" <c:if test="${opt.codi == command[campNom][campIndex].anys}"> selected</c:if>>${opt.valor}</option>
									</c:forEach>
								</select>
							</c:when>
							<c:otherwise><form:select disabled="${tasca.validada}" itemLabel="valor" itemValue="codi" items="${listTerminis}" path="${campCodi}.anys" id="${campCodi}_anys" cssClass="termini" /></c:otherwise>
						</c:choose>
					</div>
					<div class="tercmig">
	 					<label class="control-label label-term" for="${campCodi}_mesos"><spring:message code="common.camptasca.mesos"/></label>
 						<c:choose>
							<c:when test='${dada.campMultiple or isMultiple}'>
								<select id="${tercodi}_mesos" name="${tercodi}.mesos" class="termini">
									<c:forEach var="opt" items="${listTerminis}">
										<option value="${opt.codi}" <c:if test="${opt.codi == command[campNom][campIndex].mesos}"> selected</c:if>>${opt.valor}</option>
									</c:forEach>
								</select>
							</c:when>
							<c:otherwise><form:select disabled="${tasca.validada}" itemLabel="valor" itemValue="codi" items="${listTerminis}" path="${campCodi}.mesos" id="${campCodi}_mesos" cssClass="termini" /></c:otherwise>
						</c:choose>
	 				</div>
	 				<div class="tercpost">
	 					<label class="control-label label-term" for="${campCodi}_dies"><spring:message code="common.camptasca.dies"/></label>
	 					<c:set var="placeholderDies"><spring:message code='common.camptasca.dies'/></c:set>
 						<c:choose>
							<c:when test='${dada.campMultiple or isMultiple}'><input type="text" id="${tercodi}_dies" name="${tercodi}.dies" class="form-control" data-required="${dada.required}" value="${command[campNom][campIndex].dies}" placeholder="${placeholderDies}"/></c:when>
							<c:otherwise><form:input path="${campCodi}.dies" id="${campCodi}_dies" cssClass="form-control" placeholder="${placeholderDies}" data-required="${dada.required}"/></c:otherwise>
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
					<c:otherwise><form:checkbox path="${campCodi}" id="${campCodi}" data-required="${dada.required}" cssClass="checkbox"/></c:otherwise>
				</c:choose>
			</c:if>
			
<%-- ACCIO --------------------------------------------------------------------------------------%>					
			<c:if test="${dada.campTipus == 'ACCIO'}">
				<button  class="btn btn-primary pull-lef btn_accio" name="submit" type="submit" value="submit" data-action="${dada.jbpmAction}" data-confirmacio="<spring:message code='js.helforms.confirmacio' />">
					<spring:message code="common.camptasca.executar" />
				</button>
				
				<c:if test="${not empty campFocus}">
					<script>
						alert("campFocus: " + ${campFocus});
						campOnFocus(${dada.jbpmAction});
					</script>
				</c:if>
			</c:if>
			
<%-- SUGGEST ------------------------------------------------------------------------------------%>
			<c:if test="${dada.campTipus == 'SUGGEST'}">
				<c:set var="urlConsultaInicial"><c:url value='/v3/domini/consulta/inicial'/>/${tasca.id}/${dada.campId}</c:set>
				<c:set var="urlConsultaLlistat"><c:url value='/v3/domini/consulta'/>/${tasca.id}/${dada.campId}</c:set>
				<c:set var="placeholder"><spring:message code='js.helforms.selec_valor'/></c:set>
				<c:choose>
					<c:when test='${dada.campMultiple or isMultiple}'><input type="text" id="${campCodi}" name="${campNom}" class="form-control suggest" <c:if test="${disabled}">disabled </c:if>value="${command[campNom][campIndex]}" data-placeholder="${placeholder}" data-urlconsultainicial="${urlConsultaInicial}" data-urlconsultallistat="${urlConsultaLlistat}"/></c:when>
					<c:otherwise><form:input path="${campCodi}" cssClass="form-control suggest" id="${campCodi}" disabled="${disabled}" data-placeholder="${placeholder}" data-urlconsultainicial="${urlConsultaInicial}" data-urlconsultallistat="${urlConsultaLlistat}"/></c:otherwise>
				</c:choose>
			</c:if>
			
<%-- SELECCIO ------------------------------------------------------------------------------------%>					
			<c:if test="${dada.campTipus == 'SELECCIO'}">
				<c:set var="urlSelectInicial"><c:url value='/v3/expedient/tasca'/>/${tasca.id}/camp/${dada.campId}/valorSeleccioInicial</c:set>
				<c:set var="urlSelectLlistat"><c:url value='/v3/expedient/tasca'/>/${tasca.id}/camp/${dada.campId}/valorsSeleccio</c:set>
				<c:set var="placeholder"><spring:message code='js.helforms.selec_valor'/></c:set>
				<c:choose>
					<c:when test='${dada.campMultiple or isMultiple}'><input type="text" id="${campCodi}" name="${campNom}" class="seleccio" data-required="${dada.required}" data-campid="${dada.campId}" data-placeholder="${placeholder}" value="${command[campNom][campIndex]}" data-urlselectinicial="${urlSelectInicial}" data-urlselectllistat="${urlSelectLlistat}"/></c:when>
					<c:otherwise><form:input path="${campCodi}" id="${campCodi}" cssClass="seleccio" data-required="${dada.required}" data-campid="${dada.campId}" data-placeholder="${placeholder}" data-urlselectinicial="${urlSelectInicial}" data-urlselectllistat="${urlSelectLlistat}"/></c:otherwise>
				</c:choose>
			</c:if>
			
<%-- Fi VARIABLES SENZILLES -------------------------------------------------------------------------%>
<%---------------------------------------------------------------------------------------------------%>

<%-- VARIABLES MULTIPLES ----------------------------------------------------------------------------%>
<%---------------------------------------------------------------------------------------------------%>
			<c:if test="${(dada.campMultiple and isRegistre) or isMultiple}">
<!-- 				<span class="input-group-addon btn btn_eliminar"> -->
					<button  
						class="btn btn_eliminar fa fa-times" 
						type="button" 
						value="<spring:message code='comuns.esborrar' />" 
						title="<spring:message code='comuns.esborrar' />">
					</button>
<!-- 				</span> -->
<%-- 				<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campCodi}"/></p></c:if> --%>
			</c:if>
			<c:if test="${not inline and not empty dada.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p></c:if>
			<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campCodi}"/></p></c:if>				
		</div>	<%-- Fi div controls--%>
<!-- 		<div class="clear"></div> -->
	</div>	<%-- Fi div form-group--%>

	<c:if test="${dada.campMultiple and isRegistre}">
		<div class="form-group condensed">
			<c:if test="${not empty dada.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p></c:if>
			<button id="button_add_var_mult_${campCodi}" type="button" class="btn pull-left btn_multiple"><spring:message code='comuns.afegir' /></button>
		</div>
	</c:if>
<%-- Fi VARIABLES MULTIPLES -------------------------------------------------------------------------%>
<%---------------------------------------------------------------------------------------------------%>
</c:if>

<c:if test="${dada.readOnly}">
	<div class="controls">
		<label class="control-label-value"><c:out value="${dada.text}"/></label>
	</div>
	
	<c:if test="${dada.campTipus == 'TERMINI' && not empty dada.varValor}">
		<c:out value="${dada.varValor.anys}/${dada.varValor.mesos}/${dada.varValor.dies}"/>
	</c:if>	
</c:if>
<c:set var="campCodi" value=""/>
<c:set var="campInline" value="${false}"/>
<c:set var="campErrors" value=""/>