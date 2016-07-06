<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${empty expedientTipusDocumentCommand.id}"><
		<c:set var="titol"><spring:message code="expedient.tipus.document.form.titol.nou"/></c:set>
		<c:set var="formAction">new</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.tipus.document.form.titol.modificar"/></c:set>
		<c:set var="formAction">update</c:set>
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
	
	<style type="text/css">
		.btn-file {position: relative; overflow: hidden;}
		.btn-file input[type=file] {position: absolute; top: 0; right: 0; min-width: 100%; min-height: 100%; font-size: 100px; text-align: right; filter: alpha(opacity = 0); opacity: 0; outline: none; background: white; cursor: inherit; display: block;}
	</style>
</head>
<body>		
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="expedientTipusDocumentCommand">
		<div>
        
					
			<input type="hidden" name="id" value="${expedientTipusDocumentCommand.id}"/>
			<hel:inputText required="true" name="codi" textKey="expedient.tipus.document.form.camp.codi" />
			<hel:inputText required="true" name="nom" textKey="expedient.tipus.document.form.camp.nom" />
			<hel:inputTextarea name="descripcio" textKey="expedient.tipus.document.form.camp.descripcio" />
			
			<c:choose>
				<c:when test="${empty arxiuContingut}">
					<div class="form-group">
						<label class="control-label col-xs-4" for="arxiuNom"><spring:message code='expedient.tipus.document.form.camp.arxiu' /></label>
				        <div class="col-xs-8 arxiu">
				            <div class="input-group">
				                <form:input path="arxiuNom" cssClass="form-control" />
				                <span class="input-group-btn">
				                    <span class="btn btn-default btn-file">
				                        <spring:message code='expedient.tipus.document.form.camp.arxiu' />… <input type="file" id="arxiuContingut" name="arxiuContingut" value="${arxiuContingut}">
				                    </span>
				                </span>
				            </div>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					UN ARXIU PUJAT
				</c:otherwise>
			</c:choose>
			
			

<%-- 			<hel:inputFile name="arxiuContingut" required="false" textKey="expedient.tipus.document.form.camp.arxiu" /> --%>
			
			
			<hel:inputCheckbox name="plantilla" textKey="expedient.tipus.document.form.camp.plantilla" />
			
			<hel:inputText name="convertirExtensio" textKey="expedient.tipus.document.form.camp.gen_ext" />
			<hel:inputCheckbox name="adjuntarAuto" textKey="expedient.tipus.document.form.camp.adj_auto" />
			<hel:inputSelect name="campId" textKey="expedient.tipus.document.form.camp.camp_data" required="false" emptyOption="true" placeholderKey="expedient.tipus.document.form.camp.camp_data.buit" optionItems="${camps}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			<hel:inputText name="extensionsPermeses" textKey="expedient.tipus.document.form.camp.ext_perm" />
			<hel:inputText name="contentType" textKey="expedient.tipus.document.form.camp.ctype" />
			<hel:inputText name="custodiaCodi" textKey="expedient.tipus.document.form.camp.codi_custodia" />
			<hel:inputText name="tipusDocPortasignatures" textKey="expedient.tipus.document.form.camp.tipus_doc" />
			
		</div>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<c:choose>
				<c:when test="${empty expedientTipusDocumentCommand.id}">
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
	
		</div>

	</form:form>
	<script type="text/javascript">
		// <![CDATA[
           $(document).on('change', '.btn-file :file', function() {
			var input = $(this),
			numFiles = input.get(0).files ? input.get(0).files.length : 1,
			label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
			input.trigger('fileselect', [numFiles, label]);
		});
		
		$(document).ready( function() {
			$('.btn-file :file').on('fileselect', function(event, numFiles, label) {
				var input = $(this).parents('.input-group').find(':text'),
				log = numFiles > 1 ? numFiles + ' files selected' : label;
				if( input.length ) {
					input.val(log);
				} else {
					if( log )
						alert(log);
				}
			});
			$('#arxiuNom').on('click', function() {
				$('input[name=arxiuContingut]').click();
			});
		}); 
		// ]]>
	</script>	
</body>
</html>