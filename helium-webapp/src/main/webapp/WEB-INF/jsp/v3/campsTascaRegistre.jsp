<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<c:set var="dadaActual" value="${dada}"/>
<c:set var="isRegistre" value="${true}"/>
<c:set var="campErrorsReg"><form:errors path="${dadaActual.varCodi}"/></c:set>
<div class="form-group registre<c:if test="${not empty campErrorsReg}"> has-error</c:if>">
	<label for="${dadaActual.varCodi}" class="control-label col-xs-3<c:if test="${dada.required}"> obligatori</c:if>">${dadaActual.campEtiqueta}</label>
	<div class="controls col-xs-9 registre">	
		<c:set var="nomReg" value="command.${dadaActual.varCodi}" />
		<%-- Primer registre, que utilitzam per a definir la capçalera de la taula --%>
		<c:choose>
			<c:when test="${dadaActual.campMultiple}">
				<c:set var="registreCap" value="${dadaActual.multipleDades[0].registreDades}"/>
			</c:when>
			<c:otherwise>
				<c:set var="registreCap" value="${dadaActual.registreDades}"/>
			</c:otherwise>
		</c:choose>
	
		<%-- CAPÇALERA TAULA ------------------------------------------------------------------------------------------%>
		<div class="registre_taula">
		<table id="table_mult_${varStatusMain.index}" class="table table-bordered table-condensed" data-registre-id="${dadaActual.campId}">
			<thead>
			<tr>
				<c:forEach var="membre" items="${registreCap}" varStatus="varStatusCab">
					<th <c:if test="${membre.required}"> data-required="true"</c:if>>
						<label class="col-xs-3 <c:if test='${membre.required}'>control-label obligatori</c:if>">${membre.campEtiqueta}</label>
						<c:if test="${not empty membre.observacions}">
							<p class="help-block"><span class="label label-info">Nota</span> ${membre.observacions}</p>
						</c:if>
					</th>
				</c:forEach>
				<c:if test="${!dadaActual.readOnly && !tasca.validada}">
					<th class="colEliminarFila"></th>
				</c:if>
			</tr>				
			</thead>	
			<%-- TAULA MÚLTIPLE -------------------------------------------------------------------------------------------%>
			<c:if test="${dadaActual.campMultiple}">
				<%-- Comprovam si la taula és buida --> Només té una fila amb tots els camps buids --%>
				<c:set var="buida" value="${true}"/>
				<c:if test="${fn:length(command[dadaActual.varCodi]) > 1}"><c:set var="buida" value="${false}"/></c:if>
				<c:if test="${fn:length(dadaActual.multipleDades) == 1}">
					<c:forEach var="membre" items="${registreCap}">
						<c:if test="${not empty command[dadaActual.varCodi][0][membre.varCodi]}"><c:set var="buida" value="${false}"/></c:if>
					</c:forEach>
				</c:if>
				
				<c:set var="mida" value="${fn:length(command[dadaActual.varCodi])}"/>
				<tbody>
				<c:forEach var="i" begin="1" end="${mida}">
					<tr class="multiple">
						<c:forEach var="membre" items="${registreCap}" varStatus="status">
							<c:set var="campErrorLinia" value='<%= TascaFormValidatorHelper.getErrorField((org.springframework.validation.Errors)session.getAttribute("tascaError"), (net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto)pageContext.getAttribute("dadaActual"), (javax.servlet.jsp.jstl.core.LoopTagStatus)pageContext.getAttribute("status")) %>'/>
							<c:if test="${not empty campErrorLinia}"><c:set var="errorLinia"><spring:message code="${campErrorLinia}"/></c:set></c:if>
							<td>
								<c:set var="inline" value="${true}"/>
								<c:set var="dada" value="${membre}"/>
								<c:set var="campCodi" value="${dadaActual.varCodi}[${i-1}].${membre.varCodi}"/>
								<%@ include file="campsTasca.jsp" %>
								<c:set var="campCodi" value=""/>
								<c:if test="${status.index == 0 and not empty campErrorLinia}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;${errorLinia}</p></c:if>
							</td>
						</c:forEach>
						<c:if test="${!dadaActual.readOnly && !tasca.validada}">
							<td class="opciones">
								<button 
									class="btn fa fa-times eliminarFila" 
									type="button" 
									value="<spring:message code='comuns.esborrar' />" 
									title="<spring:message code='comuns.esborrar' />">
								</button>
							</td>
						</c:if>
					</tr>
				</c:forEach>
				</tbody>
			</c:if>
			
			<%-- TAULA SIMPLE -------------------------------------------------------------------------------------------%>
			<c:if test="${!dadaActual.campMultiple && not empty dadaActual.registreDades}">
				<%-- Comprovam si el registre és buid --%>
				<c:set var="buida" value="${true}"/>
				<c:forEach var="membre" items="${registreCap}">
					<c:if test="${not empty command[dadaActual.varCodi][membre.varCodi]}"><c:set var="buida" value="${false}"/></c:if>
				</c:forEach>
				<tr>
					<c:forEach var="membre" items="${registreCap}">
						<td>								
							<c:set var="inline" value="${true}"/>
							<c:set var="dada" value="${membre}"/>
							<c:set var="campCodi" value="${dadaActual.varCodi}.${membre.varCodi}"/>
							<%@ include file="campsTasca.jsp" %>
							<c:set var="campCodi" value=""/>
						</td>
					</c:forEach>
					<c:if test="${!dadaActual.readOnly && !tasca.validada}">
						<td class="opciones">
							<button 
								class="btn fa fa-times eliminarFila" 
								type="button" 
								value="<spring:message code='comuns.esborrar' />" 
								title="<spring:message code='comuns.esborrar' />">
							</button>
						</td>
					</c:if>
				</tr>
			</c:if>				
		<%-- PEU DE TAULA ------------------------------------------------------------------------------------%>
		</table>	
		</div>					
		<c:if test="${not empty dadaActual.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dadaActual.observacions}</p></c:if>
		<c:if test="${not empty campErrorsReg}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${dadaActual.varCodi}"/></p></c:if>
		<c:if test="${!dadaActual.readOnly && !tasca.validada}">
			<div <c:if test="${not empty dadaActual.registreDades}"> class="hide"</c:if>>
				<button id="button_add_table_mult_${varStatusMain.index}"
					type="button" 
					class="btn btn-default pull-left btn_afegir"
					onclick="return addField('table_mult_${varStatusMain.index}')">
						<spring:message code='comuns.afegir' />
				</button>
			</div>
			<div class="clear"></div>
		</c:if>
	</div>
</div>
<c:set var="isRegistre" value="${false}"/>
