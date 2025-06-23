<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="expedient.document.pendent.portasignatures.titol"/></c:set>

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

</style>	
</head>
<body>
	<c:if test="${potFirmar}">
		<c:set var="formActionCancelar">
			<c:url value="/v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/portasignaturesCancelarEnviament/${psignaPendentActual.documentId}"/>
		</c:set>
		<c:set var="formActionReintentar">
			<c:url value="/v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/psignaReintentar"/>
		</c:set>
		
		<form:form 	cssClass="form-horizontal" action="${formActionCancelar}" enctype="multipart/form-data" method="get" commandName="psingaReintentarCancelarCommand">
			<div class="modal-body">
				<input type="hidden" name="id" value="${documentExpedientEnviarPortasignaturesCommand.id}"/> 
				<input type="hidden" name="id" value="${document.processInstanceId}"/>
				<input type="hidden" name="psignaId" value="${psignaPendentActual.documentId}"/>

				<div class="container" style="width: 100%;">
					<div class="row">
					
						<div class="col-sm-6">
							<ul class="list-group">
							  	<li class="list-group-item"><strong><spring:message code="common.icones.doc.psigna.id"/>
							  		</strong><span class="pull-right" id="psignaDocumentId">${psignaPendentActual.documentId}</span>
							  	</li>
							  	<li class="list-group-item">
							  		<strong><spring:message code="common.icones.doc.psigna.data.enviat"/></strong>
							  		<span class="pull-right" id="psignaDataEnviat"><fmt:formatDate value="${psignaPendentActual.dataEnviat}" pattern="dd/MM/yyyy HH:mm"/></span></li>
							  	
							  	<li class="list-group-item">
		 					  		<strong><spring:message code="common.icones.doc.psigna.estat"/></strong>
		 					  		<c:choose>
		 							<c:when test="${psignaPendentActual.estat == 'PROCESSAT' && psignaPendentActual.error}">
		 								<span class="pull-right" id="psignaEstat">REBUTJAT</span>
		 							</c:when>
		 							<c:when test="${psignaPendentActual.estat != 'PROCESSAT' && psignaPendentActual.error && not empty psignaPendentActual.errorProcessant}">
		 								<span class="pull-right" id="psignaEstat">PENDENT</span>
		 							</c:when>
		 							<c:otherwise>
		 								<span class="pull-right" id="psignaEstat">${psignaPendentActual.estat}</span>
		 							</c:otherwise>
		 							</c:choose>
		 					  	</li>
								<c:if test="${not empty psignaPendentActual.motiuRebuig}">
									<li class="list-group-item">
										<strong><spring:message code="common.icones.doc.psigna.motiu.rebuig"/></strong><span class="pull-right">${psignaPendentActual.motiuRebuig}</span>
									</li>
								</c:if>
		 					  	<li class="list-group-item">
		 					  		<strong><spring:message code="common.icones.doc.psigna.data.proces.primer"/></strong>
		 					  		<span class="pull-right" id="psignaDataEnviat"><fmt:formatDate value="${psignaPendentActual.dataProcessamentPrimer}" pattern="dd/MM/yyyy HH:mm"/>
		 					  		</span>
		 					  	</li>
		 					  	<li class="list-group-item">
		 					  		<strong><spring:message code="common.icones.doc.psigna.data.proces.darrer"/></strong>
		 					  		<span class="pull-right" id="psignaDataEnviat"><fmt:formatDate value="${psignaPendentActual.dataProcessamentDarrer}" pattern="dd/MM/yyyy HH:mm"/>
		 					  		</span>
		 					  	</li>
		 					  	<c:if test="${not empty token }">
			 					  	<li class="list-group-item">
			 					  		<strong><spring:message code="common.icones.doc.psigna.node.flux"/></strong>
			 					  		<span class="pull-right" id="psignaToken">${token.nodeName}
			 					  		</span>
			 					  	</li>
		 					  	</c:if>	
		 					</ul>		 					
						</div>						
						
						<c:if test="${not empty urlFluxFirmes}">
							<div class="col-sm-6" style="height:100%;">
								<div class="blocks_container">
									<iframe width="100%" height="300px" frameborder="0" allowtransparency="true" src="${urlFluxFirmes}"></iframe>
								</div>
							</div>
						</c:if>
					</div>
					
					<c:if test="${psignaPendentActual.error && not empty psignaPendentActual.errorProcessant}">
						<div class="row">
							<div class="col-sm-12">
								<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
									<div class="panel panel-default">
										<div class="panel-heading" role="tab" id="headingOne">
											<h4 class="panel-title">
												<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
													<strong><spring:message code="common.icones.doc.psigna.error.processant"/></strong>
												</a>
											</h4>
										</div>
										<div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
											<div class="panel-body panell-error">
												<c:out value="${psignaPendentActual.errorProcessant}" escapeXml="true"/>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</c:if>

				</div>
				
			</div>
			
			<div id="modal-botons" class="well">
				<c:if test="${potFirmar}">
					<c:if test="${psignaPendentActual.estat != 'PROCESSAT' && psignaPendentActual.error && not empty psignaPendentActual.errorProcessant}"> 	
						<button type="submit" name="btnReintentarProcessament" class="btn btn-primary right"><span class="fa fa-pencil-square-o"></span> <spring:message code="common.icones.doc.psigna.reintentar"/></button>
					</c:if>
					<c:if test="${psignaPendentActual.estat != 'PROCESSAT' && expedient.permisDocManagement}">
						<button type="submit" name="btnCancelarEnviament" class="btn btn-default"><span class="fa fa-times"></span> <spring:message code="expedient.document.portasignatures.cancelar.enviament"/></button>
					</c:if>
				</c:if>	
				<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
			</div>
		</form:form>
	<div class="flux_container"></div>
	</c:if>
	
	
	<script type="text/javascript">
		// <![CDATA[		        
		var tokenId = ${psignaPendentActual.tokenId != null ? psignaPendentActual.tokenId : "null"};
		
		$(document).ready(function() {
			
   			//<c:if test="${heretat}">
			webutilDisableInputs($('#psingaReintentarCancelarCommand'));
			//</c:if>
			
			$('button[name=btnCancelarEnviament]').click(function(e) {
				let confirmMsg = "Esteu segur que voleu cancel·lar la petició de firma del document?";
				if (tokenId != null) {
					confirmMsg = confirmMsg + " Si confirmeu es cancel·larà la petició al Portafirmes i el flux associat continuarà com si s'hagués rebutjat.";
				}
				if (confirm(confirmMsg)) {
					$('#psingaReintentarCancelarCommand').attr('action', "${formActionCancelar}");
					return true;
				} else {
					e.preventDefault();
					e.stopPropagation();
					return false;
				}
			});
			
			$('button[name=btnReintentarProcessament]').click(function(e) {
				$('#psingaReintentarCancelarCommand').attr('action', "${formActionReintentar}");				 				
			})
		});
		
		// ]]>
	</script>			
		
</body>
</html>