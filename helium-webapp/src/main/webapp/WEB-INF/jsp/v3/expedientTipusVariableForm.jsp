<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>



<c:choose>
	<c:when test="${!heretat}">
		<c:choose>
			<c:when test="${empty campCommand.id}"><
				<c:set var="titol"><spring:message code="expedient.tipus.camp.form.titol.nou"/></c:set>
				<c:set var="formAction">new</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="titol"><spring:message code="expedient.tipus.camp.form.titol.modificar"/></c:set>
				<c:set var="formAction">update</c:set>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.tipus.camp.form.titol.visualitzar"/></c:set>
		<c:set var="formAction">none</c:set>		
	</c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
</head>
<body>		
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="campCommand">
		<div>        
			<input type="hidden" name="id" value="${campCommand.id}"/>
			<hel:inputText required="true" name="codi" textKey="expedient.tipus.camp.form.camp.codi" />
			<hel:inputSelect required="true" emptyOption="true" name="tipus" textKey="expedient.tipus.camp.form.camp.tipus" placeholderKey="expedient.tipus.camp.form.camp.tipus" optionItems="${tipusCamp}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			<hel:inputText required="true" name="etiqueta" textKey="expedient.tipus.camp.form.camp.etiqueta" />
			<hel:inputTextarea name="observacions" textKey="expedient.tipus.camp.form.camp.observacions" />
			<hel:inputSelect required="false" emptyOption="true" name="agrupacioId" textKey="expedient.tipus.camp.form.camp.agrupacio" placeholderKey="expedient.tipus.camp.form.camp.agrupacio" optionItems="${agrupacions}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			<hel:inputCheckbox name="multiple" textKey="expedient.tipus.camp.form.camp.multiple" />
			<hel:inputCheckbox name="ocult" textKey="expedient.tipus.camp.form.camp.ocult" />
			<hel:inputCheckbox name="ignored" textKey="expedient.tipus.camp.form.camp.ignored" />
		</div>
		
		<fieldset id="dadesConsulta" class="dades consulta" style="display:none;">
			<legend><spring:message code="expedient.tipus.camp.form.fieldset.consulta"></spring:message></legend>
			<hel:inputSelect emptyOption="true" name="enumeracioId" textKey="expedient.tipus.camp.form.camp.enumeracio" placeholderKey="expedient.tipus.camp.form.camp.enumeracio" optionItems="${enumeracions}" optionValueAttribute="id" optionTextAttribute="nom"/>
			<hel:inputSelect emptyOption="true" name="dominiId" textKey="expedient.tipus.camp.form.camp.domini" placeholderKey="expedient.tipus.camp.form.camp.domini" optionItems="${dominis}" optionValueAttribute="id" optionTextAttribute="nom"/>
			<hel:inputCheckbox name="dominiIntern" textKey="expedient.tipus.camp.form.camp.dominiIntern" />
			<hel:inputSelect emptyOption="true" name="consultaId" textKey="expedient.tipus.camp.form.camp.consulta" placeholderKey="expedient.tipus.camp.form.camp.consulta" optionItems="${consultes}" optionValueAttribute="id" optionTextAttribute="nom"/>
			<div id="parametresDomini" class="parametres domini">
				<h4>Paràmetres del domini</h4>
				<hel:inputText name="dominiIdentificador" textKey="expedient.tipus.camp.form.camp.dominiIdentificador" />
				<hel:inputTextarea name="dominiParams" textKey="expedient.tipus.camp.form.camp.dominiParametres" />
				<hel:inputText name="dominiCampValor" textKey="expedient.tipus.camp.form.camp.dominiCampValor" />
				<hel:inputTextarea name="dominiCampText" textKey="expedient.tipus.camp.form.camp.dominiCampText" />
			</div>
			<div id="parametresConsulta" class="parametres consulta">
				<h4>Paràmetres de la consulta</h4>
				<hel:inputTextarea name="consultaParams" textKey="expedient.tipus.camp.form.camp.consultaParametres" />
				<hel:inputText name="consultaCampValor" textKey="expedient.tipus.camp.form.camp.consultaCampValor" />
				<hel:inputTextarea name="consultaCampText" textKey="expedient.tipus.camp.form.camp.consultaCampText" />
			</div>
			<hel:inputCheckbox name="dominiCacheText" textKey="expedient.tipus.camp.form.camp.dominiCacheText" />
		</fieldset>
		
		<fieldset id="dadesAccio" class="dades accio" style="display:none;">
			<legend><spring:message code="expedient.tipus.camp.form.fieldset.accio"></spring:message></legend>
			<c:if test="${empty campCommand.definicioProcesId}">
				<hel:inputSelect required="true" name="defprocJbpmKey" textKey="expedient.tipus.accio.form.accio.defprocJbpmKey" emptyOption="true" placeholderKey="expedient.tipus.accio.form.accio.defprocJbpmKey.placeholder" optionItems="${definicionsProces}" />
				<hel:inputText required="true" name="jbpmAction" textKey="expedient.tipus.accio.form.accio.jbpmAction" />
			</c:if>
			<c:if test="${not empty campCommand.definicioProcesId}">
				<input type="hidden" name="defprocJbpmKey" value="${expedientTipusAccioCommand.defprocJbpmKey}" />
				<hel:inputSelect emptyOption="true" required="true" name="jbpmAction" textKey="expedient.tipus.accio.form.accio.jbpmAction" optionItems="${handlers}" placeholderKey="expedient.tipus.camp.form.camp.jbpmAction.placeholder"/>
			</c:if>
		</fieldset>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<c:if test="${!heretat}">
				<c:choose>
					<c:when test="${empty campCommand.id}">
						<button class="btn btn-primary right" type="submit" name="accio" value="crear">
							<span class="fa fa-plus"></span> <spring:message code='comu.boto.crear' />
						</button>
					</c:when>
					<c:otherwise>
						<button class="btn btn-primary right" type="submit" name="accio" value="modificar">
							<span class="fa fa-pencil"></span> <spring:message code='comu.boto.modificar' />
						</button>
					</c:otherwise>
				</c:choose>
			</c:if>
		</div>
		
	<script type="text/javascript">
		// <![CDATA[
		$(document).ready(function() {
			
			canviTipus();
			$('#tipus').change(function() {
				canviTipus();
			});
			
			canviDadesConsulta();
			$('#enumeracioId').change(function() {
				$('#dominiId').val('').trigger('change.select2');
				$('#dominiIntern').prop('checked', false);
				$('#consultaId').val('').trigger('change.select2');
				canviDadesConsulta();
			});	
			$('#dominiId').change(function() {
				$('#enumeracioId').val('').trigger('change.select2');
				$('#dominiIntern').prop('checked', false);
				$('#consultaId').val('').trigger('change.select2');
				canviDadesConsulta();
			});	
			$('#dominiIntern').change(function() {
				$('#enumeracioId').val('').trigger('change.select2');
				$('#dominiId').val('').trigger('change.select2');
				$('#consultaId').val('').trigger('change.select2');
				canviDadesConsulta();
			});	
			$('#consultaId').change(function() {
				$('#enumeracioId').val('').trigger('change.select2');
				$('#dominiId').val('').trigger('change.select2');
				$('#dominiIntern').prop('checked', false);
				canviDadesConsulta();
			});	
		});

		function disable(sel) {
			$(sel).find("input,select,textarea").prop('disabled', true);
			$(sel).hide();
		}
		function enable(sel) {
			$(sel).find("input,select,textarea").prop('disabled', false);
			$(sel).show();
		}

		function canviTipus() {
			var tipus = $('#tipus').val();
			disable('div .dades');
			if (tipus == "SELECCIO" || tipus == "SUGGEST") {
				enable('div .dades.consulta');
			} else if (tipus == "ACCIO") {
				enable('div .dades.accio');
			}			
		}
		
		function canviDadesConsulta() {
			disable('div .parametres');
			if ($('#dominiId').val() != '' || $('#dominiIntern').is(':checked')) {
				enable('div .parametres.domini');
			} else if ($('#consultaId').val() != '') {
				enable('div .parametres.consulta');
			}					
		}		
		// ]]>
	</script>			

	</form:form>
</body>
</html>