<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<c:if test="${dada.campTipus == 'REGISTRE'}">
	<div class="controls registre">
		<c:if test="${dada.campMultiple}">
			<table id="table_mult_${varStatusMain.index}" class="<c:if test="${fn:length(dada.multipleDades) < 1}"> hide </c:if>span11 displaytag selectable table table-bordered">
				<c:forEach var="multiplemembre" items="${dada.multipleDades}" varStatus="varStatus">
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
								<c:set var="dada_multiple" value="${dada.varCodi}[${varStatus.index+1}]"/>
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
				<c:if test="${!dada.campMultiple}">
					<c:set var="ocultarTabla" value="${false}"/>
					<c:if test="${dada.readOnly || tasca.validada}">
						<c:set var="ocultarTabla" value="${true}"/>
						<c:forEach 
								var="membre" 
								items="${dada.registreDades}" 
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
								items="${dada.registreDades}" 
								varStatus="varStatus"
							>							
								<display:column title="<c:if test='${membre.required}'><i class='icon-asterisk' style='color: red'></i> </c:if>${membre.campEtiqueta}">
									<c:set var="dada" value="${membre}"/>
									<c:set var="dada_multiple" value="${dada.varCodi}[${varStatus.index+1}]"/>
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
		<c:set var="ocultar_button_mult" value="${false}"/>
		<c:if test="${dada.campMultiple || fn:length(dada.multipleDades) < 1}">			
			<c:if test="${not empty dada.registreDades}">
				<table id="table_mult_${varStatusMain.index}" class="hide togle span11 displaytag selectable table table-bordered">
					<tr>
						<c:forEach var="membre" items="${dada.registreDades}" varStatus="varStatus">
							<th <c:if test="${membre.required}"> data-required="true"</c:if>>
								${membre.campEtiqueta}
							</th>
						</c:forEach>
						<th></th>
					</tr>
					<tr>
						<c:forEach var="membre" items="${dada.registreDades}" varStatus="varStatus">
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
					<c:set var="ocultar_button_mult" value="${true}"/>
					<button id="button_add_table_mult_${varStatusMain.index}"
						type="button" 
						class="btn pull-left btn_afegir"
						onclick="return addField('table_mult_${varStatusMain.index}')">
							<spring:message code='comuns.afegir' />
					</button>
				</c:if>
			</c:if>
			
			<c:if test="${!dada.readOnly && !tasca.validada}">
				<div <c:if test="${not empty dada.registreDades || ocultar_button_mult}"> class="hide"</c:if>>
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
		<br/>
		<div class="formHint">${dada.observacions}</div>
	</div>
</c:if>