<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="numColumnes" value="${3}"/>

<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
<!-- <script src="<c:url value="/js/jquery/jquery.meio.mask.min.js"/>"></script> -->
<script src="<c:url value="/js/helium3Tasca.js"/>"></script>

<c:choose>
	<c:when test="${not empty tasca.tascaFormExternCodi}">
		<div class="alert alert-warning">
			<p>
				<span class="fa fa-warning"></span>
				<spring:message code="tasca.form.compl_form"/>
				<a id="boto-formext" href="<c:url value="/v3/tasca/${tasca.id}/formExtern"/>" class="btn btn-xs btn-default pull-right"><span class="fa fa-external-link"></span> <spring:message code="tasca.form.obrir_form"/></a>
			</p>
		</div>
	</c:when>
	<c:when test="${!tasca.validada}">
		<div class="alert alert-warning">
			<button type="button" class="close" data-dismiss="alert" aria-label="<spring:message code="comu.boto.tancar"/>"><span aria-hidden="true">&times;</span></button>
			<p>
				<span class="fa fa-warning"></span>
				<spring:message code="tasca.tramitacio.form.no.validat"/>
			</p>
		</div>
	</c:when>
</c:choose>
	
<!-- 	
<c:choose>
	<c:when test="${isModal}"><c:url var="tascaFormAction" value="/modal/v3/tasca/${tasca.id}"/></c:when>
	<c:otherwise><c:url var="tascaFormAction" value="/modal/v3/tasca/${tasca.id}"/></c:otherwise>
</c:choose>
 -->

<form:form onsubmit="return confirmar(this)" action="${tascaFormAction}" cssClass="form-horizontal form-tasca" method="post" commandName="command">
	<input type="hidden" id="tascaId" name="tascaId" value="${tasca.id}">
	
	<!-- 
	<spring:hasBindErrors name="command">
           <c:forEach items="${errors.allErrors}" var="error">
              - ${error} <br>
           </c:forEach>
	</spring:hasBindErrors>
	 -->
	
	<c:set var="ampleLabel">120px</c:set>
	<c:set var="ampleInput">calc(100% - ${ampleLabel})</c:set>
	<c:set var="comptadorCols">0</c:set>
	<div class="row">
	<c:forEach var="dada" items="${dades}" varStatus="varStatusMain">
		
		<c:set var="ampleCols">${dada.ampleCols}</c:set>
		<c:set var="buitCols">${dada.buitCols}</c:set>
		<c:set var="buitAbsCols">${buitCols < 0 ? -buitCols : buitCols}</c:set>
		<c:set var="ampleBuit">${buitAbsCols + ampleCols}</c:set>
		
		<c:set var="comptadorCols">${comptadorCols + ampleBuit}</c:set>
		
		<c:if test="${comptadorCols > 12}">
			<c:set var="comptadorCols">${comptadorCols - 12}</c:set>
			
			<!--tanquem row i la tornem a obrir per a la següent fila-->
			</div>
			<div class="row">
			<!------------------------->
		</c:if>
		
		<!-- si tenim el buit menor que 0, l'offset va al davant del camp -->
		<c:if test="${buitCols < 0}">
			<div class="col-md-${buitAbsCols}"></div>
		</c:if>
		
		<div class="col-md-${ampleCols}">
			<c:set var="inline" value="${false}"/>
			<c:set var="isRegistre" value="${false}"/>
			<c:set var="isMultiple" value="${false}"/>
			<c:choose>
				<c:when test="${dada.campTipus != 'REGISTRE'}">
					<c:choose>
						<c:when test="${dada.campMultiple}">
							<c:set var="campErrorsMultiple"><form:errors path="${dada.varCodi}"/></c:set>
							<label for="${dada.varCodi}" class="control-label<c:if test="${dada.required}"> obligatori</c:if>" style="width: ${ampleLabel}; float: left;">${dada.campEtiqueta}</label>
							<div class="like-cols multiple<c:if test="${not empty campErrorsMultiple}"> has-error</c:if>" style="width: ${ampleInput};">	
								<c:forEach var="membre" items="${command[dada.varCodi]}" varStatus="varStatusCab">
									<c:set var="inline" value="${true}"/>
									<c:set var="campCodi" value="${dada.varCodi}[${varStatusCab.index}]"/>
									<c:set var="campNom" value="${dada.varCodi}"/>
									<c:set var="campIndex" value="${varStatusCab.index}"/>
									<div class="input-group-multiple <c:if test="${varStatusCab.index != 0}">pad-left-col-xs-3</c:if>">
										<c:set var="isMultiple" value="${true}"/>
										<%@ include file="campsTasca.jsp" %>
										<c:set var="isMultiple" value="${false}"/>
									</div>
								</c:forEach>
								<c:if test="${empty dada.multipleDades}">
									Buit!!
									<c:set var="inline" value="${true}"/>
									<c:set var="campCodi" value="${dada.varCodi}[0]"/>
									<c:set var="campNom" value="${dada.varCodi}"/>
									<c:set var="campIndex" value="0"/>
									<div class="input-group-multiple">
										<c:set var="isMultiple" value="${true}"/>
										<%@ include file="campsTasca.jsp" %>
										<c:set var="isMultiple" value="${false}"/>
									</div>
								</c:if>
								<c:if test="${!dada.readOnly && !tasca.validada}">
									<div class="form-group">
										<div class="pad-left-col-xs-3">
											<c:if test="${not empty dada.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p></c:if>
											<button id="button_add_var_mult_${campCodi}" type="button" class="btn btn-default pull-left btn_afegir btn_multiple"><spring:message code='comuns.afegir' /></button>
											<div class="clear"></div>
											<c:if test="${not empty campErrorsMultiple}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${dada.varCodi}"/></p></c:if>
										</div>
									</div>
								</c:if>
							</div>
						</c:when>
						<c:otherwise>
							<!-- campTasca ${dada.varCodi} -->
							<c:set var="campCodi" value="${dada.varCodi}"/>
							<c:set var="campNom" value="${dada.varCodi}"/>
							<%@ include file="campsTasca.jsp" %>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<%@ include file="campsTascaRegistre.jsp" %>
				</c:otherwise>
			</c:choose>
			<c:if test="${not varStatusMain.last}"><div class="clearForm"></div></c:if>
		</div>
		
		<!-- si el buit es major que 0, l'offset va després del camp -->
		<c:if test="${buitCols > 0}">
			<div class="col-md-${buitAbsCols}"></div>
		</c:if>
	</c:forEach>
	</div>
</form:form>