<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<div class="form-tasca">
	<div class="control-group fila_reducida">
		<label class="control-label"><fmt:message key='comuns.titol' /></label>
		<div class="controls">
			<label class="control-label-value">${tasca.titol}</label>
		</div>
	</div>
	
	<c:if test="${empty seleccioMassiva}">
		<div class="control-group fila_reducida">	
			<label class="control-label"><fmt:message key='tasca.info.expedient' /></label>
			<div class="controls">
				<label class="control-label-value">${tasca.expedientIdentificador}</label>
			</div>
		</div>
	</c:if>
		
	<div class="control-group fila_reducida">	
		<label class="control-label"><fmt:message key='comuns.tipus_exp' /></label>
		<div class="controls">
			<label class="control-label-value">${tasca.expedientTipusNom}</label>
		</div>
	</div>
	
	<div class="control-group fila_reducida">	
		<label class="control-label"><fmt:message key='tasca.info.data_creacio' /></label>
		<div class="controls">
			<label class="control-label-value"><fmt:formatDate value="${tasca.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></label>
		</div>
	</div>
	
	<div class="control-group fila_reducida">	
		<label class="control-label"><fmt:message key='tasca.info.prioritat' /></label>
		<div class="controls">
			<label class="control-label-value">
				<c:choose>
					<c:when test="${tasca.prioritatOrdinal == 0}"><fmt:message key='tasca.info.m_alta' /></c:when>
					<c:when test="${tasca.prioritatOrdinal == 1}"><fmt:message key='tasca.info.alta' /></c:when>
					<c:when test="${tasca.prioritatOrdinal == 2}"><fmt:message key='tasca.info.normal' /></c:when>
					<c:when test="${tasca.prioritatOrdinal == 3}"><fmt:message key='tasca.info.baixa' /></c:when>
					<c:when test="${tasca.prioritatOrdinal == 4}"><fmt:message key='tasca.info.m_baixa' /></c:when>
					<c:otherwise>${tasca.prioritatOrdinal}</c:otherwise>
				</c:choose>
			</label>
		</div>
	</div>
	<div class="control-group fila_reducida">	
		<c:if test="${not empty tasca.dueDate}">
			<label class="control-label"><fmt:message key='tasca.info.data_limit' /></label>
			<div class="controls">
				<label class="control-label-value"><fmt:formatDate value="${tasca.dueDate}" pattern="dd/MM/yyyy HH:mm"/></label>
			</div>
		</c:if>
	</div>
</div>
	<br style="clear: both"/><br/>

	<c:if test="${not tasca.delegada}">
		<c:if test="${tasca.delegable}">
			<div class="missatgesGris">
				<h3 class="titol-tab titol-delegacio"><fmt:message key='tasca.info.del_tasca' /> <img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="<fmt:message key='tasca.info.mos_ocul' />" title="<fmt:message key='tasca.info.mos_ocul' />" border="0" onclick="mostrarOcultar(this,'form-delegacio')"/></h3>
				<div id="form-delegacio" style="display:none">
<%-- 					<form:form action="delegacioCrear.html" cssClass="uniForm" onsubmit="return confirmarDelegar(event)"> --%>
<!-- 						<div class="inlineLabels"> -->
<%-- 							<form:hidden path="taskId"/> --%>
							
<%-- 							<c:import url="../common/formElement.jsp"> --%>
<%-- 								<c:param name="required" value="true"/> --%>
<%-- 								<c:param name="property" value="actorId"/> --%>
<%-- 								<c:param name="type" value="select"/> --%>
<%-- 								<c:param name="items" value="destinataris"/> --%>
<%-- 								<c:param name="itemLabel" value="nomSencer"/> --%>
<%-- 								<c:param name="itemValue" value="codi"/> --%>
<%-- 								<c:param name="itemBuit">&lt;&lt; <fmt:message key="tasca.delegar.select"/> &gt;&gt;</c:param> --%>
<%-- 								<c:param name="label"><fmt:message key='tasca.info.destinatari'/></c:param> --%>
<%-- 							</c:import> --%>
								
							
<%-- 							<c:import url="../common/formElement.jsp"> --%>
<%-- 								<c:param name="property" value="comentari"/> --%>
<%-- 								<c:param name="type" value="textarea"/> --%>
<%-- 								<c:param name="label"><fmt:message key='tasca.info.comentari' /></c:param> --%>
<%-- 							</c:import> --%>
<%-- 							<c:import url="../common/formElement.jsp"> --%>
<%-- 								<c:param name="property" value="supervisada"/> --%>
<%-- 								<c:param name="type" value="checkbox"/> --%>
<%-- 								<c:param name="label"><fmt:message key='tasca.info.supervisarq' /></c:param> --%>
<%-- 								<c:param name="checkAsText" value="off"/> --%>
<%-- 							</c:import> --%>
<%-- 							<c:import url="../common/formElement.jsp"> --%>
<%-- 								<c:param name="type" value="buttons"/> --%>
<%-- 								<c:param name="values">submit</c:param> --%>
<%-- 								<c:param name="titles"><fmt:message key='tasca.info.delegar' /></c:param> --%>
<%-- 							</c:import> --%>
<!-- 						</div> -->
<%-- 					</form:form> --%>
				</div>
			</div>
		</c:if>
	</c:if>
