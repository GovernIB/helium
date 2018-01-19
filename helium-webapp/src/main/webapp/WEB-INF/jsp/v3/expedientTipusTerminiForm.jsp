<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${!heretat}">
		<c:choose>
			<c:when test="${empty expedientTipusTerminiCommand.id}"><
				<c:set var="titol"><spring:message code="expedient.tipus.termini.form.titol.nou"/></c:set>
				<c:set var="formAction">new</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="titol"><spring:message code="expedient.tipus.termini.form.titol.modificar"/></c:set>
				<c:set var="formAction">update</c:set>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.tipus.termini.form.titol.visualitzar"/></c:set>
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
	<script type="text/javascript">
		$(document).ready(function() {
			$("#anys").select2({
			    width: 'resolve',
			    theme: "bootstrap",
			    allowClear: true,
			    minimumResultsForSearch: Infinity
			});
			$("#mesos").select2({
			    width: 'resolve',
			    theme: "bootstrap",
			    allowClear: true,
			    minimumResultsForSearch: Infinity
			});
			$(".terSel").on('select2-open', function() {
				var iframe = $('.modal-body iframe', window.parent.document);
				var height = $('html').height() + 30;
				iframe.height(height + 'px');
			});
			$(".terSel").on('select2-close', function() {
				var iframe = $('.modal-body iframe', window.parent.document);
				var height = $('html').height();
				iframe.height(height + 'px');
			});
			$(".enter").keyfilter(/^[-+]?[0-9]*$/);
			$("#duradaPredefinida").change(function(){
				if( $(this).prop('checked') ) {
					$("#anys").prop("disabled", false);
					$("#mesos").prop("disabled", false);
					$("#dies").prop("disabled", false);
				} else {
					$("#anys").prop("disabled", true);
					$("#mesos").prop("disabled", true);
					$("#dies").prop("disabled", true);
				}
			});
			$("#duradaPredefinida").change();
		});
	</script>
	<style type="text/css">
	.terSel {
		padding-left: 0px;
		padding-right: 0px;
	}
	</style>
</head>
<body>		
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="expedientTipusTerminiCommand">
		<div>
        					
			<input type="hidden" name="id" value="${expedientTipusTerminiCommand.id}"/>
			<hel:inputText required="true" name="codi" textKey="comuns.codi" />
			<hel:inputText required="true" name="nom" textKey="comuns.nom" />
			<hel:inputText name="descripcio" textKey="comuns.descripcio" />
			<hel:inputCheckbox name="duradaPredefinida" textKey="defproc.termform.durada_predef" comment="defproc.termform.si_no_esta"/>
			
			<%-- Termini - Inici --%>
			
			<c:set var="campErrorsAnys"><form:errors path="anys"/></c:set>
			<c:set var="campErrorsMesos"><form:errors path="mesos"/></c:set>
			<c:set var="campErrorsDies"><form:errors path="dies"/></c:set>
			
			<div class="form-group <c:if test="${not empty campErrorsAnys or not empty campErrorsMesos or not empty campErrorsDies}"> has-error</c:if>">
				<label for="durada" class="control-label col-xs-4"><spring:message code="defproc.termform.durada"/></label>
				<div class="controls col-xs-8">
					<div class="form-group termgrup row">
						<div class="termpre col-xs-4">
							<label class="control-label label-term" for="anys"><spring:message code="common.camptasca.anys"/></label>
							<form:select itemLabel="valor" itemValue="codi" items="${listTerminis}" path="anys" id="anys" cssClass="termini terSel col-xs-12" />
						</div>
						<div class="termmig col-xs-4">
		 					<label class="control-label label-term" for="mesos"><spring:message code="common.camptasca.mesos"/></label>
							<form:select itemLabel="valor" itemValue="codi" items="${listTerminis}" path="mesos" id="mesos" cssClass="termini terSel col-xs-12" />
		 				</div>
		 				<div class="termpost col-xs-4">
		 					<label class="control-label label-term" for="dies"><spring:message code="common.camptasca.dies"/></label>
							<form:input path="dies" id="dies" cssClass="form-control termini enter" />
		 				</div>
		 			</div>
					<c:if test="${not empty campErrorsAnys}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;${campErrorsAnys}</p></c:if>
					<c:if test="${not empty campErrorsMesos}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;${campErrorsMesos}</p></c:if>
					<c:if test="${not empty campErrorsDies}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;${campErrorsDies}</p></c:if>
				</div>
			</div>
			<%-- Termini - Fi --%>
			
			<hel:inputCheckbox name="laborable" textKey="defproc.termform.dies_lab" />
			<hel:inputCheckbox name="manual" textKey="defproc.termform.permet_ctrl" />
			<hel:inputText name="diesPrevisAvis" textKey="defproc.termform.dies_previs" comment="defproc.termform.es_generara"/>
			<hel:inputCheckbox name="alertaPrevia" textKey="defproc.termform.gen_alert_previa" />
			<hel:inputCheckbox name="alertaFinal" textKey="defproc.termform.gen_alert_final" />
			<hel:inputCheckbox name="alertaCompletat" textKey="defproc.termform.gen_alert_complet" />
		</div>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<c:if test="${!heretat}">
				<c:choose>
					<c:when test="${empty expedientTipusTerminiCommand.id}">
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
	</form:form>
</body>
</html>