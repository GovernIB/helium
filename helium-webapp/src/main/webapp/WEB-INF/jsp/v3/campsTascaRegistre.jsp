<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<c:set var="dadaActual" value="${dada}"/>
<c:set var="registre" value="${true}"/>
<c:if test="${dada.campTipus == 'REGISTRE'}">
	<div class="form-group registre<c:if test="${not empty campErrors}"> has-error</c:if>">
		<label for="${dada.varCodi}" class="control-label">
			${dada.campEtiqueta} - ${dada.campTipus}
		</label>
		<div class="controls registre">
		
			<c:set var="nomReg" value="command.${dadaActual.varCodi}" />
			
			<%-- Primer registre, que utilitzam per a definir la capçalera de la taula --%>
			<c:choose>
				<c:when test="${dada.campMultiple}">
					<c:set var="registreCap" value="${dada.multipleDades[0].registreDades}"/>
				</c:when>
				<c:otherwise>
					<c:set var="registreCap" value="${dada.registreDades}"/>
				</c:otherwise>
			</c:choose>
	
			<%-- CAPÇALERA TAULA ------------------------------------------------------------------------------------------%>
<%-- Simple: 		<table id="table_mult_${varStatusMain.index}" class="hide togle table table-bordered table-condensed"> --%>
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
					<c:if test="${!dada.readOnly && !tasca.validada}">
						<th class="colEliminarFila"></th>
					</c:if>
				</tr>
				
						
			<%-- TAULA MÚLTIPLE -------------------------------------------------------------------------------------------%>
				
				<c:if test="${dada.campMultiple}">
				
					<%-- Comprovam si la taula és buida --> Només té una fila amb tots els camps buids --%>
					<c:set var="buida" value="${true}"/>
					<c:if test="${fn:length(command[dadaActual.varCodi]) > 1}"><c:set var="buida" value="${false}"/></c:if>
					<c:if test="${fn:length(dada.multipleDades) == 1}">
						<c:forEach var="membre" items="${registreCap}">
								<c:if test="${not empty command[dadaActual.varCodi][0][membre.varCodi]}"><c:set var="buida" value="${false}"/></c:if>
						</c:forEach>
<%--			
						<c:forEach var="multiplemembre" items="${dada.multipleDades}" varStatus="varStatus">
							<c:forEach var="membre" items="${multiplemembre.registreDades}" varStatus="varStatusDadesCab">
								<c:if test="${not empty membre.text}"><c:set var="buida" value="${false}"/></c:if>
							</c:forEach>
						</c:forEach>
--%>
					</c:if>
							
<%--
								<td>												
									<c:set var="dada" value="${membre}"/>
									<c:set var="dada_multiple" value="${dadaActual.varCodi}[${varStatus.index+1}]"/>
									<%@ include file="campsTasca.jsp" %>
									<c:set var="dada_multiple" value=""/>
								</td>
--%>
				
					<c:set var="mida" value="${fn:length(command[dadaActual.varCodi])}"/>
					<c:forEach var="i" begin="1" end="${mida}">
						<tr>
							<c:forEach var="membre" items="${registreCap}">
							<td>${command[dadaActual.varCodi][i-1][membre.varCodi]}</td>
							</c:forEach>
							<c:if test="${!dada.readOnly && !tasca.validada}">
								<td class="opciones">
									<button 
										class="btn icon-remove eliminarFila" 
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
			
				<c:if test="${!dada.campMultiple && not empty dada.registreDades}">
			
					<c:set var="ocultar_button_mult" value="${false}"/>
			
					<%-- Comprovam si el registre és buid --%>
					<c:set var="buida" value="${true}"/>
					<c:forEach var="membre" items="${registreCap}">
							<c:if test="${not empty command[dadaActual.varCodi][membre.varCodi]}"><c:set var="buida" value="${false}"/></c:if>
					</c:forEach>
				
					<tr>
						<c:forEach var="membre" items="${registreCap}">
							<td>
								${command[dadaActual.varCodi][membre.varCodi]}
<%--					
							<c:set var="dada" value="${membre}"/>
							<c:set var="dada_multiple" value="${dadaActual.varCodi}"/>
							<%@ include file="campsTasca.jsp" %>
							<c:set var="dada_multiple" value=""/>
--%>
							</td>
						</c:forEach>
						<c:if test="${!dada.readOnly && !tasca.validada}">
							<td class="opciones">
								<button 
									class="btn icon-remove eliminarFila" 
									type="button" 
									value="<spring:message code='comuns.esborrar' />" 
									title="<spring:message code='comuns.esborrar' />">
								</button>
							</td>
						</c:if>
					</tr>
	<%-- 			<c:forEach var="membre" items="${registreCap}"> --%>
	<%-- 			<td>${command[dadaActual.varCodi][i-1][membre.varCodi]}</td> --%>
	<%-- 			</c:forEach> --%>
	<%-- 			<c:if test="${!dada.readOnly && !tasca.validada}"> --%>
	<!-- 				<td class="opciones"> -->
	<!-- 					<button  -->
	<!-- 						class="btn icon-remove eliminarFila"  -->
	<!-- 						type="button"  -->
	<%-- 						value="<spring:message code='comuns.esborrar' />"  --%>
	<%-- 						title="<spring:message code='comuns.esborrar' />"> --%>
	<!-- 					</button> -->
	<!-- 				</td> -->
	<%-- 			</c:if> --%>
				</c:if>
					
			<%-- PEU DE TAULA ------------------------------------------------------------------------------------%>
			</table>	
						
			<c:if test="${not empty dadaActual.observacions}">
				<p class="help-block"><span class="label label-info">Nota</span> ${dadaActual.observacions}</p>
			</c:if>
			<c:if test="${!dadaActual.readOnly && !tasca.validada}">
<%-- 			<c:set var="ocultar_button_mult" value="${true}"/> --%>
				<div <c:if test="${not empty dadaActual.registreDades || ocultar_button_mult}"> class="hide"</c:if>>
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
</c:if>
<c:set var="registre" value="${false}"/>