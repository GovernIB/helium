<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>

<c:choose>
	<c:when test="${not empty expedientTipus}">

		<table	id="expedientEnumeracio"
				data-toggle="datatable"
				data-url="${expedientTipus.id}/enumeracio/datatable"
				data-paging-enabled="true"
				data-info-type="search+button"
				data-ordering="true"
				data-default-order="2"
				data-rowhref-toggle="modal"
				<c:if test="${expedientTipus.permisDesignAdmin}">
					data-rowhref-template="#rowhrefTemplateEnumeracions"
				</c:if> 
				data-botons-template="#tableButtonsEnumeracioTemplate"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="numValors" data-visible="false"/>
					<th data-col-name="codi" width="20%" data-template="#cellExpedientTipusEnumeracioCodiTemplate">
					<spring:message code="expedient.tipus.enumeracio.llistat.columna.codi"/>
						<script id="cellExpedientTipusEnumeracioCodiTemplate" type="text/x-jsrender">
								{{if heretat }}
									<span class="dada-heretada">{{:codi}}</span> 
									<span class="label label-primary" title="<spring:message code="expedient.tipus.enumeracio.llistat.codi.heretat"/>">R</span>
								{{else}}
									{{:codi}}
									{{if sobreescriu }}
										<span class="label label-warning" title="<spring:message code="expedient.tipus.enumeracio.llistat.codi.sobreescriu"/>">S</span>
									{{/if}}
								{{/if}}
						</script>
					</th>
					<th data-col-name="nom"><spring:message code="expedient.tipus.enumeracio.llistat.columna.titol"/></th>
					<th data-col-name="numValors" data-template="#cellValorsTemplate" data-orderable="false" width="13%">
						<script id="cellValorsTemplate" type="text/x-jsrender">
							<a data-toggle="modal" data-callback="callbackModalEnumerats()" href="${expedientTipus.id}/enumeracio/{{:id}}/valors" class="btn btn-default"><span class="fa fa-bars"></span>&nbsp;<spring:message code="expedient.tipus.enumeracio.llistat.boto.valors"/>&nbsp;<span class="badge">{{:numValors}}</span></a>
						</script>
					</th>
					<c:if test="${expedientTipus.permisDesignAdmin}">
						<th data-col-name="id" data-template="#cellEnumTemplate" data-orderable="false" width="10%">
							<script id="cellEnumTemplate" type="text/x-jsrender">
								<div class="dropdown">
									<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
									<ul class="dropdown-menu">
									{{if heretat}}
										<li><a data-toggle="modal" href="${expedientTipus.id}/enumeracio/{{:id}}/update"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.visualitzar"/></a></li>
									{{else}}
										<li><a data-toggle="modal" href="${expedientTipus.id}/enumeracio/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
										<li><a href="${expedientTipus.id}/enumeracio/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.enumeracio.llistat.confirm.esborra"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
									{{/if}}
									</ul>
								</div>
							</script>
						</th>
					</c:if>
					<th data-col-name="sobreescriu" data-visible="false"/>
					<th data-col-name="heretat" data-visible="false"/>
				</tr>
			</thead>
		</table>

		<script id="rowhrefTemplateEnumeracions" type="text/x-jsrender">${expedientTipus.id}/enumeracio/{{:id}}/update</script>	

		<script id="tableButtonsEnumeracioTemplate" type="text/x-jsrender">
			<c:if test="${expedientTipus.permisDesignAdmin}">
				<div class="botons-titol text-right">
					<a id="nou_camp" data-callback="callbackModalEnumerats()" class="btn btn-default" href="${expedientTipus.id}/enumeracio/new" data-toggle="modal" data-datatable-id="expedientEnumeracio"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.enumeracio.llistat.accio.nova"/></a>
				</div>
			</c:if>
		</script>
		
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
// <![CDATA[
            
$(document).ready(function() {
});

function refrescaTaula() {
	$('#expedientEnumeracio').webutilDatatable('refresh-url', '${expedientTipus.id}/enumeracio/datatable');
}

function callbackModalEnumerats() {
	refrescaTaula();
	// Refresca els missatges
	webutilRefreshMissatges();
}
// ]]>
</script>	