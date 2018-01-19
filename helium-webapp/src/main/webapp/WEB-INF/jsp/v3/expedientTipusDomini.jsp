<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<c:url var="urlDatatable" value="/v3/expedientTipus/${expedientTipus.id}/dominis/datatable"/>

<c:choose>
	<c:when test="${not empty expedientTipus}">

		<table	id="expedientTipusDomini"
				data-toggle="datatable"
				data-url="${urlDatatable}"
				data-paging-enabled="true"
				data-info-type="search+button"
				data-ordering="true"
				data-default-order="2"
				data-rowhref-toggle="modal"
				data-rowhref-template="#rowhrefTemplateDominis" 
				data-botons-template="#tableButtonsDominiTemplate"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="tipus" data-visible="false"/>
					<th data-col-name="codi" width="20%" data-template="#cellExpedientTipusDominiCodiTemplate">
					<spring:message code="expedient.tipus.enumeracio.llistat.columna.codi"/>
						<script id="cellExpedientTipusDominiCodiTemplate" type="text/x-jsrender">
								{{if heretat }}
									<span class="dada-heretada">{{:codi}}</span> 
									<span class="label label-primary" title="<spring:message code="expedient.tipus.domini.llistat.codi.heretat"/>">R</span>
								{{else}}
									{{:codi}}
									{{if sobreescriu }}
										<span class="label label-warning" title="<spring:message code="expedient.tipus.domini.llistat.codi.sobreescriu"/>">S</span>
									{{/if}}
								{{/if}}
						</script>
					</th>
					<th data-col-name="nom"><spring:message code="comuns.nom"/></th>
					<th data-col-name="tipus" data-template="#cellTipusDomTemplate" data-orderable="false"><spring:message code="comuns.tipus"/>
						<script id="cellTipusDomTemplate" type="text/x-jsrender">
							{{if tipus == 'CONSULTA_SQL'}}
								<spring:message code="expedient.tipus.domini.tipus.sql"/>
							{{else}}
								<spring:message code="expedient.tipus.domini.tipus.ws"/>
							{{/if}}
						</script>
					</th>
					<th data-col-name="id" data-template="#cellDominiTemplate" data-orderable="false" width="10%">
						<script id="cellDominiTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								{{if heretat}}
									<li><a data-toggle="modal" href="${expedientTipus.id}/domini/{{:id}}/update"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.visualitzar"/></a></li>
								{{else}}
									<li><a data-toggle="modal" data-callback="callbackModalDominis()" href="${expedientTipus.id}/domini/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
									<li><a href="${expedientTipus.id}/domini/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.camp.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
								{{/if}}
							</ul>
						</div>
					</script>
					</th>
					<th data-col-name="sobreescriu" data-visible="false"/>
					<th data-col-name="heretat" data-visible="false"/>
				</tr>
			</thead>
		</table>
		<script id="tableButtonsDominiTemplate" type="text/x-jsrender">
			<div class="botons-titol text-right">
				<a id="nou_camp" class="btn btn-default" href="${expedientTipus.id}/domini/new" data-toggle="modal" data-callback="callbackModalDominis()" data-datatable-id="expedientTipusDomini"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.domini.nou"/></a>
			</div>
		</script>
		<script id="rowhrefTemplateDominis" type="text/x-jsrender">${expedientTipus.id}/domini/{{:id}}/update</script>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>
<script>
$(document).ready(function() {
	$('#expedientTipusDomini').on('draw.dt', function() {
		// Refresca els missatges
		webutilRefreshMissatges();		
	});
});

function callbackModalDominis() {
	refrescaTaula();
}

function refrescaTaula() {
	$('#expedientTipusDomini').webutilDatatable('refresh');
}
</script>