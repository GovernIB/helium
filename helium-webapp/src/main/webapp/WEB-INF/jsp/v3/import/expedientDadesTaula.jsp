<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="paramDades" value="${requestScope[param.dadesAttribute]}"/>
<c:set var="paramCount" value="${param.count}"/>
<c:if test="${not empty param.titol}"><c:set var="paramTitol" value="${param.titol}"/></c:if>
<c:set var="paramNumColumnes" value="${3}"/>
<c:if test="${not empty param.numColumnes}"><c:set var="paramNumColumnes" value="${param.numColumnes}"/></c:if>
<c:set var="paramDesplegat" value="${true}"/>
<c:if test="${not empty param.desplegat}"><c:set var="paramDesplegat" value="${param.desplegat}"/></c:if>
<c:set var="paramMostrarCapsalera" value="${true}"/>
<c:if test="${not empty param.mostrarCapsalera}"><c:set var="paramMostrarCapsalera" value="${param.mostrarCapsalera}"/></c:if>
<c:if test="${not empty param.condicioCamp}"><c:set var="paramCondicioCamp" value="${param.condicioCamp}"/></c:if>
<c:if test="${not empty param.condicioValor}"><c:set var="paramCondicioValor" value="${param.condicioValor}"/></c:if>
<c:if test="${not empty param.condicioEmpty}"><c:set var="paramCondicioEmpty" value="${param.condicioEmpty}"/></c:if>
<c:if test="${not empty param.desplegadorClass}"><c:set var="paramDesplegadorClass" value="${param.desplegadorClass}"/></c:if>

<table class="table table-bordered">
	<colgroup>
		<c:forEach begin="0" end="${paramNumColumnes - 1}">
			<col width="${100 / paramNumColumnes}%"/>
		</c:forEach>
	</colgroup>
	<c:if test="${paramMostrarCapsalera}">
		<thead>
			<tr>
				<th colspan="${paramNumColumnes}">
					${paramTitol} <c:if test="${not empty paramCount}"><span class="badge">${paramCount}</span></c:if>
					<div class="pull-right accordion-toggle">
						<c:choose>
							<c:when test="${paramDesplegat}"><i class="<c:if test="${not empty paramDesplegadorClass}">${paramDesplegadorClass} </c:if>icon-chevron-up"></i></c:when>
							<c:otherwise><i class="<c:if test="${not empty paramDesplegadorClass}">${paramDesplegadorClass} </c:if>icon-chevron-down"></i></c:otherwise>
						</c:choose>
					</div>
				</th>
			</tr>
		</thead>
	</c:if>
	<tbody<c:if test="${not paramDesplegat}"> class="hide"</c:if>>
		<c:set var="index" value="${0}"/>
		<c:set var="posicioOffset" value="${0}"/>
		<c:forEach var="dada" items="${paramDades}">
			<c:set var="posicioActual" value="${(index + posicioOffset) % paramNumColumnes}"/>
			<c:set var="condicioValor" value="${true}"/>
			<c:choose>
				<c:when test="${not empty paramCondicioCamp and not empty paramCondicioEmpty}">
					<c:set var="condicioValor" value="${empty dada[paramCondicioCamp]}"/>
				</c:when>
				<c:when test="${not empty paramCondicioCamp and empty paramCondicioValor}">
					<c:set var="condicioValor" value="${dada[paramCondicioCamp]}"/>
				</c:when>
				<c:when test="${not empty paramCondicioCamp and not empty paramCondicioValor}">
					<c:set var="condicioValor" value="${dada[paramCondicioCamp] == paramCondicioValor}"/>
				</c:when>
			</c:choose>
			<c:if test="${condicioValor}">
				<c:if test="${posicioActual == 0}"><tr></c:if>
				<c:if test="${dada.campTipusRegistre}">
					<c:if test="${posicioActual > 0}"><td colspan="${paramNumColumnes - posicioActual}">&nbsp;</td></tr><tr></c:if>
					<c:set var="posicioOffset" value="${posicioOffset + (paramNumColumnes - posicioActual) - 1}"/>
					<c:set var="posicioActual" value="${0}"/>
				</c:if>
				<td<c:if test="${dada.campTipusRegistre}"> colspan="${paramNumColumnes}"</c:if><c:if test="${not empty dada.error}"> style="background-color:#f2dede"</c:if>>
					<address>
						${dada.campEtiqueta}<br/>
						<c:if test="${not empty dada.varValor}">
							<c:choose>
								<c:when test="${dada.campTipusRegistre}">
									<c:set var="registreFilesTaula" value="${dada.dadesRegistrePerTaula}"/>
									<table class="table table-bordered table-condensed marTop6">
										<thead>
											<tr>
												<c:forEach var="dadaFila0" items="${registreFilesTaula[0].registreDades}">
													<th>${dadaFila0.campEtiqueta}</th>
												</c:forEach>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="registreFila" items="${registreFilesTaula}">
												<tr>
													<c:forEach var="registreDada" items="${registreFila.registreDades}">
														<td>${registreDada.text}</td>
													</c:forEach>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${not empty dada.error}"><strong><i class="icon-warning-sign" title="${dada.error}"></i></strong></c:when>
										<c:otherwise><strong>${dada.text}</strong></c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</c:if>
					</address>
				</td>
				<c:if test="${(index == paramCount - 1) and posicioActual != (paramNumColumnes - 1) and not dada.campTipusRegistre}"><td colspan="${paramNumColumnes - posicioActual - 1}">&nbsp;</td></c:if>
				<c:if test="${(index == paramCount - 1) or dada.campTipusRegistre or (index != 0 and posicioActual == (paramNumColumnes - 1))}"></tr></c:if>
				<c:set var="index" value="${index + 1}"/>
			</c:if>
		</c:forEach>
	</tbody>
</table>