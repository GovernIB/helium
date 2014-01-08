<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<c:set var="datosVariable" value="${dada}"/>
<c:if test="${datosVariable.campTipus == 'REGISTRE'}">
	<c:set var="registreFiles" value="${tasca.varsComText[datosVariable.varCodi]}" scope="request"/>
	<div class="controls registre">
		<c:if test="${datosVariable.campMultiple && (not empty datosVariable.multipleDades[0].registreDades)}">
			<table id="table_mult_${varStatusMain.index}" class="<c:if test="${fn:length(registreFiles) < 1}"> hide </c:if>span11 displaytag selectable table table-bordered">
				<c:forEach var="multiplemembre" items="${datosVariable.multipleDades}" varStatus="varStatus">
					<tr class="fila_${varStatus.index}">
						<c:if test="${varStatus.first}">
							<c:forEach var="membre" items="${multiplemembre.registreDades}" varStatus="varStatusDadesCab">
								<th <c:if test="${membre.required}"> data-required="true"</c:if>>
									${membre.campEtiqueta}
								</th>
							</c:forEach>
								<c:if test="${!dada.readOnly && !tasca.validada}">
									<th></th>
								</c:if>
							</tr>
							<tr>
						</c:if>
						<c:forEach var="membre" items="${multiplemembre.registreDades}" varStatus="varStatusDades">
							<td>												
								<c:set var="dada" value="${membre}"/>
								<c:set var="dada_multiple" value="${datosVariable.varCodi}[${varStatus.index+1}]"/>
								<%@ include file="campsTasca.jsp" %>
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
				</c:forEach>
			</table>
		</c:if>
		<c:if test="${not empty registreFiles}">
			<div style="overflow:auto">
				<c:if test="${!datosVariable.campMultiple}">
					<c:set var="ocultarTabla" value="${false}"/>
					<c:if test="${dada.readOnly || tasca.validada}">
						<c:set var="ocultarTabla" value="${true}"/>
						<c:forEach 
								var="membre" 
								items="${datosVariable.registreDades}" 
								varStatus="varStatus"
							>
							<c:if test="${not empty membre.text || not empty membre.varValor}">
								<c:set var="ocultarTabla" value="${false}"/>
							</c:if>
						</c:forEach>
					</c:if>
					<c:if test="${!ocultarTabla}">
						<display:table id="table_mult_${varStatusMain.index}" class="span11 displaytag selectable table table-bordered" name="table_mult_${varStatusMain.index}" requestURI="">
							<c:forEach 
								var="membre" 
								items="${datosVariable.registreDades}" 
								varStatus="varStatus"
							>							
								<display:column title="<c:if test='${membre.required}'><i class='icon-asterisk' style='color: red'></i> </c:if>${membre.campEtiqueta}">
									<c:set var="dada" value="${membre}"/>
									<c:set var="dada_multiple" value="${datosVariable.varCodi}[${varStatus.index+1}]"/>
									<%@ include file="campsTasca.jsp" %>
								</display:column>
							</c:forEach>
							<c:if test="${!dada.readOnly && !tasca.validada}">
								<display:column class="opciones" >
									<button 
										class="btn icon-remove eliminarFila" 
										type="button" 
										value="<spring:message code='comuns.esborrar' />" 
										title="<spring:message code='comuns.esborrar' />">
									</button>
								</display:column>
							</c:if>
						</display:table>
					</c:if>
					<c:if test="${!dada.readOnly && !tasca.validada}">
						<div id="button_add_table_mult_${varStatusMain.index}" class="hide">
							<div style="clear: both"></div>
							<button							
								type="button" 
								class="btn pull-left btn_afegir"
								onclick="return addField('table_mult_${varStatusMain.index}')">
									<spring:message code='comuns.afegir' />
							</button>
						</div>
					</c:if>
				</c:if>
			</div>
		</c:if>
		<c:if test="${datosVariable.campMultiple || fn:length(registreFiles) < 1}">			
			<c:if test="${not empty datosVariable.registreDades}">
				<table id="table_mult_${varStatusMain.index}" class="hide togle span11 displaytag selectable table table-bordered">
					<tr>
						<c:forEach var="membre" items="${datosVariable.registreDades}" varStatus="varStatus">
							<th <c:if test="${membre.required}"> data-required="true"</c:if>>
								${membre.campEtiqueta}
							</th>
						</c:forEach>
						<th></th>
					</tr>
					<tr>
						<c:forEach var="membre" items="${datosVariable.registreDades}" varStatus="varStatus">
							<td>
								<c:set var="dada" value="${membre}"/>
								<%@ include file="campsTasca.jsp" %>
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
				</table>
				<c:if test="${!dada.readOnly && !tasca.validada}">
					<div style="clear: both"></div>
					<button id="button_add_table_mult_${varStatusMain.index}"
						type="button" 
						class="btn pull-left btn_afegir"
						onclick="return addField('table_mult_${varStatusMain.index}')">
							<spring:message code='comuns.afegir' />
					</button>
				</c:if>
			</c:if>
			
			<c:if test="${!dada.readOnly && !tasca.validada}">
				<div <c:if test="${not empty datosVariable.registreDades}"> class="hide"</c:if>>
					<div style="clear: both"></div>
					<button id="button_add_table_mult_${varStatusMain.index}"
						type="button" 
						class="btn pull-left btn_afegir"
						onclick="return addField('table_mult_${varStatusMain.index}')">
							<spring:message code='comuns.afegir' />
					</button>
				</div>
			</c:if>
		</c:if>		
		<div class="formHint">${datosVariable.observacions}</div>
	</div>
</c:if>