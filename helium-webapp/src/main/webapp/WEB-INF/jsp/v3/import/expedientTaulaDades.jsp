<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<td id="cela-${procesId}-${dada.varCodi}"<c:if test="${dadaTipusRegistre or dada.campTipus == 'TEXTAREA'}"> colspan="${paramNumColumnes}"</c:if><c:if test="${dada.campOcult}"> class="campOcult"</c:if>>
	<address class="var_dades <c:if test="${not empty dada.error}">has-error</c:if> <c:if test="${dadaTipusRegistre}">var_registre</c:if>">
		<c:if test="${dada.campOcult}"><span class="fa fa-eye-slash"></span></c:if>
		<label class="<c:if test="${dada.required}">obligatori</c:if>">${dada.campEtiqueta}</label><br/>
		<c:choose>
			<c:when test="${dadaTipusRegistre}">
				<c:set var="registreFilesTaula" value="${dada.dadesRegistrePerTaula}"/>
				<table class="table table-bordered table-condensed marTop6">
					<thead>
						<tr>
							<c:forEach var="dadaFila0" items="${registreFilesTaula[0].registreDades}">
								<c:if test="${dadaFila0.llistar}">
									<th><label <c:if test='${dadaFila0.required}'> class="control-label obligatori"</c:if>>${dadaFila0.campEtiqueta}</label></th>
								</c:if>
							</c:forEach>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="registreFila" items="${registreFilesTaula}">
							<tr>
								<c:forEach var="registreDada" items="${registreFila.registreDades}">
									<c:if test="${registreDada.llistar}">
										<td>${registreDada.text}</td>
									</c:if>
								</c:forEach>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<c:if test="${not empty dada.error}">
					<p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<span>${dada.error}</span></p>
				</c:if>
			</c:when>
			<c:otherwise>
				<c:if test="${not empty dada.varValor}">
					<%-- ${dada.varValorClass} --%>
					<strong>${dada.textMultiple}</strong>
				</c:if>
				<c:if test="${not empty dada.error}">
					<p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<span>${dada.error}</span></p>
				</c:if>
			</c:otherwise>
		</c:choose>
	</address>
	<c:if test="${expedient.permisWrite}">
	<div class=var_botons>
		<a 	class="var-edit" 
			data-rdt-link-modal=true
			href='<c:url value="../../v3/expedient/${expedientId}/dades/${procesId}/edit/${dada.varCodi}"/>' 
			data-rdt-link-callback="reestructura(${procesId});"
			data-rdt-link-modal-min-height="300"
			title="<spring:message code='expedient.dada.modificar'/>">
			<span class="fa fa-pencil"></span>
		</a>
		<a 	class="var-delete" 
			data-rdt-link-confirm="<spring:message code='expedient.info.confirm.dada.esborrar'/>"
			data-rdt-link-ajax=true
			href='<c:url value="../../v3/expedient/${expedientId}/dades/${procesId}/delete/${dada.varCodi}"/>' 
			data-rdt-link-callback="reestructura(${procesId});"
			title="<spring:message code='expedient.dada.esborrar'/>">
			<span class="fa fa-trash-o"></span>
		</a>
	</div>
	</c:if>
</td>