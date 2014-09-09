<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<c:set var="dadaActual" value="${dada}"/>
<c:set var="isRegistre" value="${true}"/>
<div class="form-group registre<c:if test="${not empty campErrors}"> has-error</c:if>">
	<label for="${dadaActual.varCodi}" class="control-label col-xs-3">
		${dadaActual.campEtiqueta} - ${dadaActual.campTipus}
	</label>
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
		<table id="table_mult_${varStatusMain.index}" class="table table-bordered table-condensed">
			<tr>
				<c:forEach var="membre" items="${registreCap}" varStatus="varStatusCab">
					<th <c:if test="${membre.required}"> data-required="true"</c:if>>
						${membre.campEtiqueta}<c:if test='${membre.required}'><i class='icon-asterisk'></i></c:if>
						<c:if test="${not empty membre.observacions}">
							<p class="help-block"><span class="label label-info">Nota</span> ${membre.observacions}</p>
						</c:if>
					</th>
				</c:forEach>
				<c:if test="${!dadaActual.readOnly && !tasca.validada}">
					<th class="colEliminarFila"></th>
				</c:if>
			</tr>
				
						
			<%-- TAULA MÚLTIPLE -------------------------------------------------------------------------------------------%>
				
			<c:if test="${dadaActual.campMultiple}">
				Multiple
				<%-- Comprovam si la taula és buida --> Només té una fila amb tots els camps buids --%>
				<c:set var="buida" value="${true}"/>
				<c:if test="${fn:length(command[dadaActual.varCodi]) > 1}"><c:set var="buida" value="${false}"/></c:if>
				<c:if test="${fn:length(dadaActual.multipleDades) == 1}">
					<c:forEach var="membre" items="${registreCap}">
						<c:if test="${not empty command[dadaActual.varCodi][0][membre.varCodi]}"><c:set var="buida" value="${false}"/></c:if>
					</c:forEach>
					<%--			
					<c:forEach var="multiplemembre" items="${dadaActual.multipleDades}" varStatus="varStatus">
						<c:forEach var="membre" items="${multiplemembre.registreDades}" varStatus="varStatusDadesCab">
							<c:if test="${not empty membre.text}"><c:set var="buida" value="${false}"/></c:if>
						</c:forEach>
					</c:forEach>
					--%>
				</c:if>
				
				<c:set var="mida" value="${fn:length(command[dadaActual.varCodi])}"/>
				<c:forEach var="i" begin="1" end="${mida}">
					<tr class="multiple">
						<c:forEach var="membre" items="${registreCap}">
							<td>
								<c:set var="inline" value="${true}"/>
								<c:set var="dada" value="${membre}"/>
								<c:set var="campCodi" value="${dadaActual.varCodi}[${i-1}].${membre.varCodi}"/>
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
				</c:forEach>
			</c:if>
			
			<%-- TAULA SIMPLE -------------------------------------------------------------------------------------------%>
			
			<c:if test="${!dadaActual.campMultiple && not empty dadaActual.registreDades}">
				Simple
		
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
				<%-- 		
				<c:forEach var="membre" items="${registreCap}">
					<td>${command[dadaActual.varCodi][i-1][membre.varCodi]}</td>
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
	 			--%>
			</c:if>				
		<%-- PEU DE TAULA ------------------------------------------------------------------------------------%>
		</table>	
					
		<c:if test="${not empty dadaActual.observacions}">
			<p class="help-block"><span class="label label-info">Nota</span> ${dadaActual.observacions}</p>
		</c:if>
		<c:if test="${!dadaActual.readOnly && !tasca.validada}">
			<div <c:if test="${not empty dadaActual.registreDades}"> class="hide"</c:if>>
				<button id="button_add_table_mult_${varStatusMain.index}"
					type="button" 
					class="btn pull-left btn_afegir"
					onclick="return addField('table_mult_${varStatusMain.index}')">
						<spring:message code='comuns.afegir' />
				</button>
			</div>
			<div class="clear"></div>
		</c:if>
	</div>
</div>
<div class="clearForm"></div>
<c:set var="isRegistre" value="${false}"/>
