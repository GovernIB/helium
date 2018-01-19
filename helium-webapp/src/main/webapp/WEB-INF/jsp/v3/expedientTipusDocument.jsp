<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="ambInfoPropiaText"><spring:message code="expedient.tipus.form.camp.ambInfoPropia"/></c:set>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>


<c:choose>
	<c:when test="${not empty expedientTipus || not empty definicioProcesId}">

		<c:if test="${not empty expedientTipus && empty definicioProcesId && !expedientTipus.ambInfoPropia}">
			<div class="alert alert-warning">
				<span class="fa fa-exclamation-triangle"></span>
				<spring:message code="expedient.tipus.ambInfoPropia.avis" arguments="${ambInfoPropiaText}"></spring:message>
			</div>
		</c:if>
		<c:if test="${not empty definicioProces && not empty definicioProces.expedientTipus && definicioProces.expedientTipus.ambInfoPropia}">
			<div class="alert alert-warning">
				<span class="fa fa-exclamation-triangle"></span>
				<spring:message code="definicio.proces.ambInfoPropia.avis" arguments="${ambInfoPropiaText}"></spring:message>
			</div>
		</c:if>

		<table	id="expedientTipusDocument"
				data-toggle="datatable"
				data-url="${baseUrl}/document/datatable"
				data-paging-enabled="true"
				data-info-type="search+button"
				data-ordering="true"
				data-default-order="1"
				data-rowhref-toggle="modal"
				data-rowhref-template="#rowhrefTemplateDocuments" 
				data-botons-template="#tableButtonsDocumentTemplate"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="codi" width="20%" data-template="#cellExpedientTipusDocumentCodiTemplate">
					<spring:message code="expedient.tipus.document.llistat.columna.codi"/>
						<script id="cellExpedientTipusDocumentCodiTemplate" type="text/x-jsrender">
								{{if heretat }}
									<span class="dada-heretada">{{:codi}}</span> 
									<span class="label label-primary" title="<spring:message code="expedient.tipus.document.llistat.codi.heretat"/>">R</span>
								{{else}}
									{{:codi}}
									{{if sobreescriu }}
										<span class="label label-warning" title="<spring:message code="expedient.tipus.document.llistat.codi.sobreescriu"/>">S</span>
									{{/if}}
								{{/if}}
						</script>
					</th>
					<th data-col-name="nom"><spring:message code="expedient.tipus.document.llistat.columna.nom"/></th>
					<th data-col-name="plantilla" data-template="#plantillaTemplate">
					<spring:message code="expedient.tipus.document.llistat.columna.plantilla"/>
						<script id="plantillaTemplate" type="text/x-jsrender">
							{{if plantilla }}
								<spring:message code="comu.check"></spring:message>
							{{/if}}
						</script>
					</th>
					<th data-col-name="arxiuNom" data-template="#arxiuTemplate">
					<spring:message code="expedient.tipus.document.llistat.columna.arxiu"/>
						<script id="arxiuTemplate" type="text/x-jsrender">
							{{if arxiuNom != null }}
								<div align="center">
									<a href="${baseUrl}/document/{{:id}}/download" class="nounderline deco-none" style="color: inherit;" ><span class="fa fa-file" title="<spring:message code="expedient.tipus.document.llistat.accio.descarregar"/>"></span></a>
								</div>
							{{/if}}
						</script>
					</th>
					<th data-col-name="id" data-template="#cellAccionsDocTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsDocTemplate" type="text/x-jsrender">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									{{if heretat}}
										<li><a data-toggle="modal" href="${baseUrl}/document/{{:id}}/update"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.visualitzar"/></a></li>
									{{else}}
										<li><a data-toggle="modal" data-callback="callbackModalDocuments()" href="${baseUrl}/document/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
									{{/if}}
									{{if arxiuNom != null }}
										<li><a href="${baseUrl}/document/{{:id}}/download" ><span class="fa fa-file"></span>&nbsp;<spring:message code="expedient.tipus.document.llistat.accio.descarregar"/></a></li>
									{{/if}}
									{{if !heretat}}
										<li><a href="${baseUrl}/document/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.document.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
									{{/if}}
								</ul>
							</div>
						</script>
					</th>
					<th data-col-name="plantilla" data-visible="false"/>
					<th data-col-name="sobreescriu" data-visible="false"/>
					<th data-col-name="heretat" data-visible="false"/>
				</tr>
			</thead>
		</table>

		<script id="rowhrefTemplateDocuments" type="text/x-jsrender">${baseUrl}/document/{{:id}}/update</script>	

		<script id="tableButtonsDocumentTemplate" type="text/x-jsrender">
			<div class="botons-titol text-right">
				<a id="nou_camp" class="btn btn-default" href="${baseUrl}/document/new" data-toggle="modal" data-callback="callbackModalDocuments()" data-datatable-id="expedientTipusDomini"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.document.llistat.accio.nova"/></a>
			</div>
		</script>
		
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
// <![CDATA[
            
$(document).ready(function() {
	$('#expedientTipusDocument').on('draw.dt', function() {
		// Refresca els missatges
		webutilRefreshMissatges();		
	});
});

function refrescaTaulaDocuments() {
	$('#expedientTipusDocument').webutilDatatable('refresh-url', '${baseUrl}/document/datatable');
}

function callbackModalDocuments() {
	refrescaTaulaDocuments();
}

// ]]>
</script>			
