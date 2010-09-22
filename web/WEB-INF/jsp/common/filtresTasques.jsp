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
			$(this).html('<span class="filtre">&nbsp;</span>Ocultar filtre');
		}, function(){
			$(this).html('<span class="filtre">&nbsp;</span>Mostrar filtre');
		});

		<c:if test="${not empty command.nom
						or not empty command.expedient
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

<button id="botoFiltres" class="submitButtonImage"><span class="filtre">&nbsp;</span>Mostrar filtre</button>

<div id="filtres" style="display: none;">
	<form:form action="${param.formulari}" cssClass="uniForm" method="post">
		<div class="inlineLabels col first">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="label">Tasca</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="expedient"/>
				<c:param name="label">Expedient</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="tipusExpedient"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="tipusExp"/>
				<c:param name="itemLabel" value="nom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit" value="&lt;&lt; Tipus d'expedient &gt;&gt;"/>
				<c:param name="label">Tipus d'expedient</c:param>
			</c:import>
		</div>
		<div class="inlineLabels col last">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="dataCreacioInici"/>
				<c:param name="type" value="custom"/>
				<c:param name="label">Data de creació</c:param>
				<c:param name="customClass">customField</c:param>
				<c:param name="content">
					<spring:bind path="dataCreacioInici">
						<label for="dataCreacioInici" class="blockLabel">
							<span>Entre</span>
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
							<span>i</span>
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
				<c:param name="itemBuit" value="&lt;&lt; Prioritat &gt;&gt;"/>
				<c:param name="label">Prioritat</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="dataLimitInici"/>
				<c:param name="type" value="custom"/>
				<c:param name="label">Data límit</c:param>
				<c:param name="customClass">customField</c:param>
				<c:param name="content">
					<spring:bind path="dataLimitInici">
						<label for="dataLimitInici" class="blockLabel">
							<span>Entre</span>
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
				<c:param name="titles">Consultar,Netejar</c:param>
			</c:import>
		</div>
	</form:form>
</div>

<br/><br/>

<c:if test="${param.tasques != param.total}">
	<p class="aclaracio">
		S'estan mostrant ${param.tasques} de ${param.total} tasques
	</p>
	<br/>
</c:if>