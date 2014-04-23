<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<script type="text/javascript">
	// <![CDATA[
	$(document).ready(function(){
		$("#botoFiltres").click(function(){
			$("#filtres").slideToggle();
		}).toggle(function(){
			$(this).html('<span class="filtre">&nbsp;</span><fmt:message key="common.filtres.ocultar" />');
		}, function(){
			$(this).html('<span class="filtre">&nbsp;</span><fmt:message key="common.filtres.mostrar" />');
		});

		<c:if test="${not empty command.nom
						or not empty command.expedient
						or not empty command.numeroExpedient
						or not empty command.tipusExpedient
						or not empty command.dataCreacioInici
						or not empty command.dataCreacioFi
						or not empty command.prioritat
						or not empty command.dataLimitInici
						or not empty command.dataLimitFi}">
			$("#botoFiltres").click();
		</c:if>
	});
	// ]]>
</script>

<button id="botoFiltres" class="submitButtonImage"><span class="filtre">&nbsp;</span><fmt:message key='common.filtres.mostrar' /></button>

<div id="filtres" style="display: none;">
	<form:form action="${param.formulari}" cssClass="uniForm" method="post">
		<div class="inlineLabels col first">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="label"><fmt:message key='common.filtres.tasca' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="expedient"/>
				<c:param name="label"><fmt:message key='common.filtres.expedient' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="numeroExpedient"/>
				<c:param name="label"><fmt:message key="expedient.consulta.numero_llarg"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="tipusExpedient"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="tipusExp"/>
				<c:param name="itemLabel" value="nom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key='comuns.tipus_exp' /> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key='comuns.tipus_exp' /></c:param>
			</c:import>
		</div>
		<div class="inlineLabels col last">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="dataCreacioInici"/>
				<c:param name="type" value="custom"/>
				<c:param name="label"><fmt:message key='common.filtres.data_creacio' /></c:param>
				<c:param name="customClass">customField</c:param>
				<c:param name="content">
					<spring:bind path="dataCreacioInici">
						<label for="dataCreacioInici" class="blockLabel">
							<span><fmt:message key='common.filtres.entre' /></span>
							<input id="dataCreacioInici" name="dataCreacioInici" value="${status.value}" type="text" class="textInput"/>
							<script type="text/javascript">
								// <![CDATA[
								$(function() {
									$.datepicker.setDefaults($.extend({
										dateFormat: 'dd/mm/yy',
										changeMonth: true,
										changeYear: true
									}));
									$("#dataCreacioInici").datepicker();
								});
								// ]]>
							</script>
						</label>
					</spring:bind>
					<spring:bind path="dataCreacioFi">
						<label for="dataCreacioFi" class="blockLabel blockLabelLast">
							<span><fmt:message key='common.filtres.i' /></span>
							<input id="dataCreacioFi" name="dataCreacioFi" value="${status.value}" type="text" class="textInput"/>
							<script type="text/javascript">
								// <![CDATA[
								$(function() {
									$.datepicker.setDefaults($.extend({
										dateFormat: 'dd/mm/yy',
										changeMonth: true,
										changeYear: true
									}));
									$("#dataCreacioFi").datepicker();
								});
								// ]]>
							</script>
						</label>
					</spring:bind>
				</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="prioritat"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="prioritats"/>
				<c:param name="itemLabel" value="label"/>
				<c:param name="itemValue" value="value"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key='common.filtres.prioritat' /> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key='common.filtres.prioritat' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="dataLimitInici"/>
				<c:param name="type" value="custom"/>
				<c:param name="label"><fmt:message key='common.filtres.data_limit' /></c:param>
				<c:param name="customClass">customField</c:param>
				<c:param name="content">
					<spring:bind path="dataLimitInici">
						<label for="dataLimitInici" class="blockLabel">
							<span><fmt:message key='common.filtres.entre' /></span>
							<input id="dataLimitInici" name="dataLimitInici" value="${status.value}" type="text" class="textInput"/>
							<script type="text/javascript">
								// <![CDATA[
								$(function() {
									$.datepicker.setDefaults($.extend({
										dateFormat: 'dd/mm/yy',
										changeMonth: true,
										changeYear: true
									}));
									$("#dataLimitInici").datepicker();
								});
								// ]]>
							</script>
						</label>
					</spring:bind>
					<spring:bind path="dataLimitFi">
						<label for="dataLimitFi" class="blockLabel blockLabelLast">
							<span>i</span>
							<input id="dataLimitFi" name="dataLimitFi" value="${status.value}" type="text" class="textInput"/>
							<script type="text/javascript">
								// <![CDATA[
								$(function() {
									$.datepicker.setDefaults($.extend({
										dateFormat: 'dd/mm/yy',
										changeMonth: true,
										changeYear: true
									}));
									$("#dataLimitFi").datepicker();
								});
								// ]]>
							</script>
						</label>
					</spring:bind>
				</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">submit,clean</c:param>
				<c:param name="titles"><fmt:message key='common.filtres.consultar' />,<fmt:message key='common.filtres.netejar' /></c:param>
			</c:import>
		</div>
	</form:form>
</div>

<br/><br/>
<%--c:if test="${param.tasques != param.total}">
	<p class="aclaracio">
		<fmt:message key='common.filtres.mostrant' /> ${param.tasques} <fmt:message key='common.filtres.de' /> ${param.total} <fmt:message key='comuns.tasques' />
	</p>
	<br/>
</c:if--%>