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
<style>
		.reproForm {
			margin: 2px;
		}
		.repro-group {
			margin-right: 0px !important;
			margin-bottom: 6px;
		}
		.repros {
			z-index: 1000;
		    float: right;
		    bottom: 5px;
		    position: fixed;
		    right: 15px;
		}	
		.repros ul.dropdown-menu {
			width: 250px;
		}
		li.flex {
    		display: flex;
		}
		li.flex > a {
			width: 100%;
		}	
		.borrarRepro {
			position: absolute;
		    right: 5px;
		    padding: 0px 5px;
		}
	</style>

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
	
	<c:set var="ampleLabel">130px</c:set>
	<c:set var="ampleInput">calc(100% - ${ampleLabel})</c:set>
	<c:set var="comptadorCols">0</c:set>
	<c:forEach var="entry" items="${dadesMap}">
	
	<c:choose>
	    <c:when test="${not empty entry.key}">
	    	<fieldset>
				<legend>${entry.key.nom}</legend>
	    	</fieldset>
	    
	    	<!-- 
			<div class="panel panel-default">
				<div id="-grup-25361-titol" class="panel-heading clicable grup tauladades" data-toggle="collapse" data-target="#-grup-${entry.key.id}-dades" aria-expanded="true">
				${entry.key.nom}
				<div class="pull-right"><span class="icona-collapse fa fa-chevron-up"></span></div>
				</div>
				<div id="-grup-${entry.key.id}-dades" class="clear panel-body-grup collapse in" aria-expanded="true" style="padding: 0.75rem !important;">
	    	 -->
	    </c:when>
	    <c:otherwise>
	    </c:otherwise>
	</c:choose>
	
	<div class="row">
		<!------------------------->
    <c:forEach var="dada" items="${entry.value}" varStatus="varStatusMain">
        <c:set var="ampleCols">${dada.ampleCols}</c:set>
        <c:set var="buitCols">${dada.buitCols}</c:set>
        <c:set var="buitAbsCols">${buitCols < 0 ? -buitCols : buitCols}</c:set>
                <c:set var="ampleBuit">${buitAbsCols + ampleCols}</c:set>

                <c:set var="comptadorCols">${comptadorCols + ampleBuit}</c:set>

                <c:if test="${comptadorCols > 12}">
                    <c:set var="comptadorCols">${buitAbsCols}</c:set>

                    <!--tanquem row i la tornem a obrir per a la següent fila-->
                    </div>
                    <div class="row">
                        <!------------------------->
                </c:if>

                <!-- si tenim el buit menor que 0, l'offset va al davant del camp -->
                <c:if test="${buitCols < 0}">
                    <div class="col-xs-${buitAbsCols}"></div>
                </c:if>

                <div class="col-xs-${ampleCols}" title="ampleCols-${ampleCols} buitCols-${buitCols} buitAbsCols-${buitAbsCols} comptadorCols-${comptadorCols}">
                    <c:set var="inline" value="${false}" />
                    <c:set var="isRegistre" value="${false}" />
                    <c:set var="isMultiple" value="${false}" />
                    <c:choose>
                        <c:when test="${dada.campTipus != 'REGISTRE'}">
                            <c:choose>
                                <c:when test="${dada.campMultiple}">
                                    <c:set var="campErrorsMultiple">
                                        <form:errors path="${dada.varCodi}" />
                                    </c:set>
                                    <label for="${dada.varCodi}" class="control-label<c:if test=" ${dada.required}">
                                        obligatori</c:if>" style="width: ${ampleLabel}; float:
                                        left;">${dada.campEtiqueta}</label>
                                    <div class="like-cols multiple<c:if test=" ${not empty campErrorsMultiple}">
                                        has-error</c:if>" style="width: ${ampleInput};">
                                        <c:forEach var="membre" items="${command[dada.varCodi]}"
                                            varStatus="varStatusCab">
                                            <c:set var="inline" value="${true}" />
                                            <c:set var="campCodi" value="${dada.varCodi}[${varStatusCab.index}]" />
                                            <c:set var="campNom" value="${dada.varCodi}" />
                                            <c:set var="campIndex" value="${varStatusCab.index}" />
                                            <div class="input-group-multiple <c:if test=" ${varStatusCab.index !=0}">
                                                pad-left-col-xs-3</c:if>">
                                                <c:set var="isMultiple" value="${true}" />
                                                <%@ include file="campsTasca.jsp" %>
                                                <c:set var="isMultiple" value="${false}" />
                                            </div>
                                        </c:forEach>
                                        <c:if test="${empty dada.multipleDades}">
                                            Buit!!
                                            <c:set var="inline" value="${true}" />
                                            <c:set var="campCodi" value="${dada.varCodi}[0]" />
                                            <c:set var="campNom" value="${dada.varCodi}" />
                                            <c:set var="campIndex" value="0" />
                                            <div class="input-group-multiple">
                                                <c:set var="isMultiple" value="${true}" />
                                                <%@ include file="campsTasca.jsp" %>
                                                <c:set var="isMultiple" value="${false}" />
                                            </div>
                                        </c:if>
                                        <c:if test="${!dada.readOnly && !tasca.validada}">
                                            <div class="form-group">
                                                <div class="pad-left-col-xs-3">
                                                    <c:if test="${not empty dada.observacions}">
                                                        <p class="help-block"><span class="label label-info">Nota</span>
                                                            ${dada.observacions}</p>
                                                    </c:if>
                                                    <button id="button_add_var_mult_${campCodi}" type="button"
                                                        class="btn btn-default pull-left btn_afegir btn_multiple">
                                                        <spring:message code='comuns.afegir' /></button>
                                                    <div class="clear"></div>
                                                    <c:if test="${not empty campErrorsMultiple}">
                                                        <p class="help-block"><span
                                                                class="fa fa-exclamation-triangle"></span>&nbsp;
                                                            <form:errors path="${dada.varCodi}" />
                                                        </p>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:if>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <!-- campTasca ${dada.varCodi} -->
                                    <c:set var="campCodi" value="${dada.varCodi}" />
                                    <c:set var="campNom" value="${dada.varCodi}" />
                                    <%@ include file="campsTasca.jsp" %>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <%@ include file="campsTascaRegistre.jsp" %>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${not varStatusMain.last}">
                        <div class="clearForm"></div>
                    </c:if>
                </div>

                <!-- si el buit es major que 0, l'offset va després del camp -->
                <c:if test="${buitCols > 0}">
                    <div class="col-xs-${buitAbsCols}"></div>
                </c:if>
                
                <c:if test="${comptadorCols == 12}">
                    <c:set var="comptadorCols">0</c:set>

                    <!--tanquem row i la tornem a obrir per a la següent fila-->
                    </div>
                    <div class="row">
                        <!------------------------->
                </c:if>
                
            </c:forEach>
            <br>
           </div>
	</div>

</c:forEach>
</div>
	
	<!-- REPROS -->
	<c:if test="${ tasca.ambRepro && !tasca.validada }">
		<div class="btn-group repros dropup">
		  <button type="button" class="btn btn-info dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
		    <spring:message code='repro.texte.repros' /> <span class="caret"></span>
		  </button>
		  <ul class="dropdown-menu dropdown-menu-right">
			<li class="reproForm">
					<div class="form-group repro-group">
						<input id="nomRepro" name="nomRepro" type="text" class="form-control" placeholder="<spring:message code='repro.texte.nom' />">
					</div>
					<button id="guardarRepro" name="guardarRepro" value="guardar-repro" class="btn btn-primary" type="submit">
						<spring:message code='repro.texte.guardar' />
					</button>
			</li>
			<c:if test="${not empty repros}">
				<li role="separator" class="divider"></li>
			    <strong><li class="dropdown-header">---- <spring:message code='repro.texte.guardats' /> ----</li></strong>
			    <c:forEach var="repro" items="${repros}">
				    <li class="flex">
				    	<a id="repro-${repro.id}" href="<c:url value="/modal/v3/tasca/"/>${tasca.id}?reproId=${repro.id}">${repro.nom}</a>
				    	<button class="btn btn-danger borrarRepro" type="submit" data-reproid="${repro.id}"><i class="fa fa-trash-o" aria-hidden="true"></i></button>
				    </li>
			    </c:forEach>
		    </c:if>
		  </ul>
		</div>
	</c:if>
	<!-- FI REPROS -->
</form:form>

<script>
$('.dropdown-menu').find('.reproForm').click(function (e) {
					e.stopPropagation();
				});
			
				$('#guardarRepro').click(function(e) {
					var e = e || window.event;
					e.cancelBubble = true;
					if (e.stopPropagation) e.stopPropagation();
					if ($('#nomRepro').val() != '') {
						$('#command').attr('action','<c:url value="/v3/repro/"/>${tasca.expedientTipusId}/${tasca.definicioProcesId}/saveRepro');
						return true;
					} else {
						return false;
					}
				});
	
				$('.borrarRepro').click(function(e) {
					var e = e || window.event;
					e.cancelBubble = true;
					if (e.stopPropagation) e.stopPropagation();
					var reproId = $(this).data('reproid');
					$('#command').attr('action','<c:url value="/v3/repro/"/>${tasca.expedientTipusId}/${tasca.definicioProcesId}/deleteRepro/' + reproId);
					return true;
				});
</script>