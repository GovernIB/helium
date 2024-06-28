<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="decorator.menu.fluxes"/></title>
	<meta name="title" content="<spring:message code='decorator.menu.fluxes'/>"/>
	<meta name="screen" content="fluxosFirma">
	<meta name="title-icon-class" content="fa fa-sitemap"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="JavaScript:createOrUpdateFlux('NULL');">
			<span class="fa fa-plus"></span>&nbsp;<spring:message code="fluxosFirma.taula.boto.nou"/>
		</a>
	</div>
	
<!-- 	rdt-link-callback="webutilRefreshMissatges();" -->
	
	<table	id="fluxesFirmaUsuariDatatable"
			data-toggle="datatable"
			data-url="fluxeFirma/datatable"
			data-paging-enabled="false"
			data-info-type="search"
			data-rowhref-toggle="modal"			
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="fluxId" width="20%"><spring:message code="fluxosFirma.taula.camp.id"/></th>
				<th data-col-name="nom" width="60%"><spring:message code="fluxosFirma.taula.camp.nom"/></th>
				<th data-col-name="fluxId" width="20%" data-template="#cellAccionsTemplate">
					<script id="cellAccionsTemplate" type="text/x-jsrender">	
					<div id="btnAccionsFluxe" class="btn-group">
						<button class="btn btn-primary" data-toggle="dropdown">
							<span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span>
						</button>
 						<ul class="dropdown-menu">
							<li><a	id="accioModificarFluxe"
									data-maximized="true" 
									href="JavaScript:createOrUpdateFlux('{{:fluxId}}');"
									data-rdt-link-ajax="true">
										<spring:message code="fluxosFirma.taula.boto.modificar"/>
								</a>
							</li>
							<li><a	id="accioEliminarFluxe"
									data-maximized="true" 
									href="<c:url value="/v3/fluxeFirma/delete/{{:fluxId}}"/>" 
									data-rdt-link-ajax="true">
										<spring:message code="fluxosFirma.taula.boto.eliminar"/>
								</a>
							</li>
 						</ul>
					</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	
	<div id="modalProcesEstat" class="modal fade">
		<div class="modal-dialog" style="width: 60%; height: 85%">
			<div class="modal-content" style="height: 100%">
				<div class="modal-header" style="height: 10%">
					<button type="button" class="close" data-dismiss="modal" aria-label="<spring:message code="comu.boto.tancar"/>"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title"><span class="fa fa-list"></span> <spring:message code="fluxosFirma.taula.modal.title"></spring:message></h4>
				</div>
				<div class="modal-body" style="height: 80%"></div>
				<div class="modal-footer" style="height: 10%">
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></button>
				</div>
			</div>
		</div>
	</div>	
	
	<script type="text/javascript">
	// <![CDATA[
		$(document).ready(function() {});
		
		function portafibCallback(missatge, error) {
			$('#modalProcesEstat').modal('hide');
			$("#fluxesFirmaUsuariDatatable").dataTable().fnDraw();
			if (error) {
				webutilAlertaError(missatge, null);
			} else {
				webutilAlertaSuccess(missatge, null);
			}
		}
		
		function createOrUpdateFlux(fluxId) {
			$.ajax({
				type: 'GET',
				contentType: 'application/json; charset=utf-8',
				dataType: 'json',
				url: '<c:url value="/v3/fluxeFirma/createOrUpdateFlux/'+fluxId+'"/>',
				success: function(transaccioResponse, textStatus, XmlHttpRequest) {
					if (transaccioResponse != null && !transaccioResponse.error) {
						localStorage.setItem('transaccioId', transaccioResponse.idTransaccio);
						$('.content').addClass("hidden");
						var fluxIframe = '<div class="iframe_container" style="height: 100%;">' +
								'<iframe id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + transaccioResponse.urlRedireccio + '"></iframe>' +
								'</div>';
						$('#modalProcesEstat .modal-body').html(fluxIframe);
						$('#modalProcesEstat').modal({backdrop: 'static', keyboard: false});
					} else if (transaccioResponse != null && transaccioResponse.error) {
						let currentIframe = window.frameElement;
						var alertDiv = '<div class="alert alert-danger" role="alert">' +
								'<a class="close" data-dismiss="alert">×</a><span>' + transaccioResponse.errorDescripcio + '</span>' +
								'</div>';
						$('form').prev().find('.alert').remove();
						$('form').prev().prepend(alertDiv);
						webutilModalAdjustHeight();
					}
				},
				error: function(error) {
					if (error != null && error.responseText != null) {
						let currentIframe = window.frameElement;
						var alertDiv = '<div class="alert alert-danger" role="alert">' +
								'<a class="close" data-dismiss="alert">×</a><span>' + error.responseText + '</span>' +
								'</div>';
						$('form').prev().find('.alert').remove();
						$('form').prev().prepend(alertDiv);
					}
				}
			});
		}
	// ]]>
	</script>	

</body>
</html>
