<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
<script src="<c:url value="/webjars/datatables.net-select/1.1.0/js/dataTables.select.min.js"/>"></script>
<script src="<c:url value="/js/jsrender.min.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/expdoc-viewer.js"/>"></script>
<%--<script slot="<c:url value="/js/bootbox.all.min.js"/>"></script>--%>
<script type="application/javascript">
	var urlDescarrebaBase = '<c:url value="/v3/expedient/${expedient.id}/document/"/>';
	var urlViewer = '<c:url value="/webjars/pdf-js/2.13.216/web/viewer.html"/>';
	var msgViewer = [];
	msgViewer['previs'] = '<spring:message code="expedient.document.previsualitzacio"/>';
	msgViewer['nom'] = '<spring:message code="expedient.document.info.nom"/>';
	msgViewer['error'] = '<spring:message code="expedient.document.previsualitzacio.error"/>';
	msgViewer['warning'] = '<spring:message code="expedient.document.previsualitzacio.warning"/>';

	$(document).ready(() => {
		$("#expedientDocuments").on("draw.dt", () => {
			let firstTr = $("#expedientDocuments tbody tr:first-child");
			if (firstTr.hasClass("no-data")) {
				firstTr.css("border-top", "dashed 3px #BBB");
			}
			$("#expedientDocuments tbody tr").each((index, element) => {
				if (!$(element).hasClass("no-data")) {
					$(element).click((e) => {
						const documentId = $(element).find(".accionsDocument").first().data("documentId");
						const documentNom = escape($(element).find("td:eq(1)").text().trim());
						const documentArxivat = $(element).find(".accionsDocument").first().data("arxivat");
						showViewer(e, documentId, documentNom);
					})
				}
			})
		});

		//scroll top
		$('.btn-top').on('click', () => {
			$([document.documentElement, document.body]).animate({
				scrollTop: 0
			}, 200);
		});
		$(document).scroll(() => {
			var scrollTop = $(document).scrollTop();
			var opacity = 0.4 + scrollTop / 1500;
			if (opacity > 0.9)
				opacity = 0.9;
			$('.btn-top').css({
				opacity: opacity
			});
		});

		$('body').on('click', 'button.modal-tancar', (e) => {
			alert('tancant modal');
		})
	});

	// const esborrarSignatura = (id, correcte) => {
	// 	if (correcte) {
	// 		$("#signatura_" + id).remove();
	// 		$("#signatura_" + id).remove();
	// 	}
	// }

	<%--const confirmPsigna(psignaInfo)--%>
	<%--bootbox.confirm({--%>
	<%--	size: 'large',--%>
	<%--	title: '<spring:message code="expedient.document.pendent.psigna"/>',--%>
	<%--	message: '',--%>
	<%--	callback: function(result) {--%>

	<%--	}--%>
	<%--});--%>

</script>
<style>
	.table-bordered>tbody>tr>td {max-width: 155px;}
	.no-data {color: #bbb; border: dashed 2px; cursor: default !important;}
	#expedientDocuments {border-collapse: collapse !important;}
	#expedientDocuments tbody tr td {vertical-align: middle;}
	.btn-top {position: fixed; z-index: 1000; right: 15px; bottom: 50px; background-color: #FFF; padding: 0 5px 0 5px; border-radius: 5px; cursor: pointer; opacity: 0.1;}
	.doc-icon {color: #337ab7;}
	.nodoc-icon {margin-right: 12px;}
	.adjuntIcon {top:-10px; left:-7px;}
	.extensionIcon {color: white; position: relative; left: -26px; top: 5px; font-size: 9px; font-weight: bold; margin-right: -12px;}
	.doc-error {color: indianred;}
	.doc-error-icon {top: 3px;}
	.label-doc {float: right; line-height: 16px;}
</style>

<c:url var="urlDatatable" value="/v3/expedient/${expedient.id}/document/datatable"/>

<table	id="expedientDocuments"
		  data-toggle="datatable"
		  data-url="${urlDatatable}"
		  data-paging-enabled="false"
		  data-ordering="true"
		  data-default-order="14"
		  data-info-type="search+button"
		  data-rowcolid-nullclass="no-data"
		  data-selection-enabled="true"
		  data-selection-url="${expedient.id}/document/selection"
		  data-selection-counter="#descarregarCount"
		  data-botons-template="#tableButtonsDocumentsTemplate"
		  class="table table-striped table-bordered table-hover">
	<thead>
	<tr>
		<th data-col-name="id" data-visible="false"/>
		<th data-col-name="codi" data-visible="false"/>
		<th data-col-name="error" data-visible="false"/>
		<th data-col-name="adjunt" data-visible="false"/>
		<th data-col-name="signat" data-visible="false"/>
		<th data-col-name="signaturaUrlVerificacio" data-visible="false"/>
		<th data-col-name="arxiuActiu" data-visible="false"/>
		<th data-col-name="ntiCsv" data-visible="false"/>
		<th data-col-name="registrat" data-visible="false"/>
		<th data-col-name="documentValid" data-visible="false"/>
		<th data-col-name="arxivat" data-visible="false"/>
		<th data-col-name="notificable" data-visible="false"/>
		<th data-col-name="psignaPendent" data-visible="false"/>
		<th data-col-name="psignaError" data-visible="false"/>
		<th data-col-name="ntiActiu" data-visible="false"/>
		<th data-col-name="notificat" data-visible="false"/>
		<th data-col-name="deAnotacio" data-visible="false"/>
		<th data-col-name="anotacioId" data-visible="false"/>
		<th data-col-name="anotacioIdentificador" data-visible="false"/>
		<th data-col-name="extensio" data-visible="false"/>
		<th data-col-name="editable" data-visible="false"/>
		<th data-col-name="nom" data-orderable="true" width="54%" data-template="#cellNomTemplate">
			<spring:message code="expedient.tipus.document.llistat.columna.nom"/>
			<script id="cellNomTemplate" type="text/x-jsrender">
				{{if id == null}}
					<span class="fa fa-2x fa-file-o nodoc-icon"></span>
					{{:nom}}
				{{else error}}
					<span class="fa-stack fa-1x no-doc" title="${document.error}">
						<span class="fa fa-file fa-stack-2x doc-error"></span>
						<span class="fa fa-warning fa-inverse fa-stack-1x doc-error-icon"></span>
					</span>
				{{else}}
					<span class="fa-stack fa-1x doc-icon">
						<span class="fa fa-file fa-stack-2x"></span>
						{{if adjunt}}<span class="adjuntIcon fa fa-stack-1x fa-inverse fa-paperclip"></span>{{/if}}
					</span>
					<span class="extensionIcon">{{:extensio}}</span>
					{{:nom}}
					<%--Notificable--%>
					{{if notificable}}<span class="label label-default label-doc" title="<spring:message code='expedient.document.notificable'/>"><span class="fa fa-paper-plane-o"></span></span>{{/if}}
					<%--Signat     --%>
					{{if signat}}
						{{if !arxiuActiu}}
							{{if signaturaUrlVerificacio != null}}
								<a href="{{:signaturaUrlVerificacio}}" target="_blank"><span class="fa fa-certificate"></span>&nbsp;<spring:message code="expedient.document.signat"/></a>
							{{else}}
								<a href="${expedient.id}/document/{{:id}}/signatura/verificar?urlVerificacioCustodia={{:signaturaUrlVerificacio}}" data-toggle="modal" data-refrescar="false"><span class="fa fa-certificate"></span>&nbsp;<spring:message code="expedient.document.signat"/></a>
							{{/if}}
						{{else}}
							{{if ntiCsv != null}}
								<a href="{{:signaturaUrlVerificacio}}" target="_blank"><span class="fa fa-certificate"></span>&nbsp;<spring:message code="expedient.document.signat"/></a>
							{{else}}
								<a href="${expedient.id}/document/{{:id}}/signatura/verificarCsv" data-toggle="modal" data-refrescar="false"><span class="fa fa-certificate"></span>&nbsp;<spring:message code="expedient.document.signat"/></a>
							{{/if}}
						{{/if}}
					{{/if}}
					<%--Registrat  --%>
					{{if registrat}}
						<a href="${expedient.id}/document/{{:id}}/registre/verificar" data-toggle="modal"><span class="fa fa-book" data-refrescar="false"></span>&nbsp;<spring:message code="expedient.document.registrat"/></a>
					{{/if}}
					<%--Portafirmes--%>
					{{if psignaPendent}}
					{{/if}}
					<%--NTI        --%>
					{{if ntiActiu}}
					{{/if}}
					<%--Notificat  --%>
					{{if notificat}}
					{{/if}}
					<%--De anotacio--%>
					{{if deAnotacio}}
					{{/if}}
					<%--Doc Invàlid--%>
					{{if !documentValid}}
						<span class="label label-danger label-doc" title="<spring:message code='expedient.document.invalid' arguments='${document.documentError}'/>"><span class="fa fa-exclamation-triangle></span></span>
					{{/if}}
				{{/if}}
			</script>
		</th>
		<th data-col-name="tipoDocumental" data-orderable="true" width="12%" data-template="#cellTipusTemplate">
			<spring:message code="expedient.metadades.nti.camp.eni.tipus.doc"/>
			<script id="cellTipusTemplate" type="text/x-jsrender">
				{{if tipoDocumental == 'RESOLUCIO'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.RESOLUCIO"/>
				{{else tipoDocumental == 'ACORD'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.ACORD"/>
				{{else tipoDocumental == 'CONTRACTE'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.CONTRACTE"/>
				{{else tipoDocumental == 'CONVENI'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.CONVENI"/>
				{{else tipoDocumental == 'DECLARACIO'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.DECLARACIO"/>
				{{else tipoDocumental == 'COMUNICACIO'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.COMUNICACIO"/>
				{{else tipoDocumental == 'NOTIFICACIO'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.NOTIFICACIO"/>
				{{else tipoDocumental == 'PUBLICACIO'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.PUBLICACIO"/>
				{{else tipoDocumental == 'JUSTIFICANT_RECEPCIO'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.JUSTIFICANT_RECEPCIO"/>
				{{else tipoDocumental == 'ACTA'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.ACTA"/>
				{{else tipoDocumental == 'CERTIFICAT'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.CERTIFICAT"/>
				{{else tipoDocumental == 'DILIGENCIA'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.DILIGENCIA"/>
				{{else tipoDocumental == 'INFORME'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.INFORME"/>
				{{else tipoDocumental == 'SOLICITUD'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.SOLICITUD"/>
				{{else tipoDocumental == 'DENUNCIA'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.DENUNCIA"/>
				{{else tipoDocumental == 'ALEGACIO'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.ALEGACIO"/>
				{{else tipoDocumental == 'RECURS'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.RECURS"/>
				{{else tipoDocumental == 'COMUNICACIO_CIUTADA'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.COMUNICACIO_CIUTADA"/>
				{{else tipoDocumental == 'FACTURA'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.FACTURA"/>
				{{else tipoDocumental == 'ALTRES_INCAUTATS'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.ALTRES_INCAUTATS"/>
				{{else tipoDocumental == 'ALTRES'}}
					<spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.ALTRES"/>
				{{/if}}
			</script>
		</th>
		<th data-col-name="dataCreacio" data-converter="date" data-orderable="true" width="10%"><spring:message code="expedient.document.data"/></th>
		<th data-col-name="dataDocument" data-converter="datetimeminute" data-orderable="true" width="10%"><spring:message code="expedient.document.adjuntat"/></th>
		<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="5%">
			<script id="cellAccionsTemplate" type="text/x-jsrender">
			{{if id == null}}
				<a class="btn btn-default" href="${expedient.id}/document/{{:codi}}/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.boto.nou_document"/></a>
			{{else}}
				<div data-document-id="{{:id}}" data-arxivat="{{:arxivat}}" data-psigna="{{psignaInfo}}" class="dropdown accionsDocument">
					<button class="btn btn-primary" data-toggle="dropdown" style="width:100%;"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
					<ul class="dropdown-menu dropdown-menu-right">
						{{if !error}}
							<%--Modificar  --%>
							{{if editable && !signat}}<li><a data-toggle="modal" href="${expedient.id}/document/{{:id}}/update"><span class="fa fa-pencil fa-fw"></span>&nbsp;<spring:message code="comuns.modificar"/></a></li>{{/if}}
							<%--Borrar     --%>
							{{if editable && (!signat || !arxiuActiu)}}<li><a href="${expedient.id}/document/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.llistat.document.confirm_esborrar"/>"><span class="fa fa-trash-o fa-fw"></span>&nbsp;<spring:message code="comuns.esborrar"/></a></li>{{/if}}
							<%--Notificar  --%>
							{{if notificable}}<li><a href="${expedient.id}/document/{{:id}}/notificar" data-toggle="modal"><span class="fa fa-paper-plane-o fa-fw"></span>&nbsp;<spring:message code="expedient.document.notificar"/></a></li>{{/if}}
							<%--Signat     --%>
							{{if signat}}
								{{if !arxiuActiu}}
									{{if signaturaUrlVerificacio != null}}
										<li><a href="{{:signaturaUrlVerificacio}}" target="_blank"><span class="fa fa-certificate fa-fw"></span>&nbsp;<spring:message code="expedient.document.signat.detalls"/></a></li>
									{{else}}
										<li><a href="${expedient.id}/document/{{:id}}/signatura/verificar?urlVerificacioCustodia={{:signaturaUrlVerificacio}}" data-toggle="modal" data-refrescar="false"><span class="fa fa-certificate fa-fw"></span>&nbsp;<spring:message code="expedient.document.signat.detalls"/></a></li>
									{{/if}}
									{{if editable}}
										<li><a href="${expedient.id}/document/{{:id}}/signatura/esborrar" data-toggle="ajax" data-confirm="<spring:message code="expedient.document.confirm_esborrar_signatures"/>"><span class="fa fa-ban fa-fw"></span>&nbsp;<spring:message code="comuns.esborrar"/></a></li>
									{{/if}}
								{{else}}
									{{if ntiCsv != null}}
										<li id="signatura-lnk"><a href="{{:signaturaUrlVerificacio}}" target="_blank"><span class="fa fa-certificate" data-refrescar="false"></span>&nbsp;<spring:message code="expedient.document.signat.detalls"/></a></li>
									{{else}}
										<li id="signatura-lnk"><a href="${expedient.id}/document/{{:id}}/signatura/verificarCsv" data-toggle="modal"><span class="fa fa-certificate"></span>&nbsp;<spring:message code="expedient.document.signat.detalls"/></a></li>
									{{/if}}
								{{/if}}
							{{/if}}
							<%--Registrat  --%>
							{{if registrat}}
								<li><a href="${expedient.id}/document/{{:id}}/registre/verificar" data-toggle="modal"><span class="fa fa-book fa-fw" data-refrescar="false"></span>&nbsp;<spring:message code="expedient.document.registrat.detalls"/></a></li>
							{{/if}}
						{{/if}}
						<%--Descarregar--%><li><a href="${expedient.id}/document/{{:id}}/descarregar"><span class="fa fa-download fa-fw"></span>&nbsp;<spring:message code="expedient.document.descarregar"/></a></li>
					</ul>
				</div>
			{{/if}}
			</script>
		</th>
	</tr>
	</thead>
</table>
<div class="panel panel-default" id="resum-viewer" style="display: none; width: 100%;" >
	<iframe id="container" class="viewer-padding" width="100%" height="540" frameBorder="0"></iframe>
</div>
<div class="btn-top">
	<span class="fa fa-arrow-up"></span>
</div>
<script id="tableButtonsDocumentsTemplate" type="text/x-jsrender">
	<div class="botons-titol text-right">
		<a id="descarregarZip" href="<c:url value="/v3/expedient/${expedient.id}/document/descarregarZip"/>" class="btn btn-default" title="<spring:message code="expedient.document.descarregar.zip"/>">
			<span class="fa fa-download"></span> <spring:message code="comu.boto.descarregar"/> <span id="descarregarCount" class="badge"></span>
		</a>
		<a id="nou_document" class="btn btn-default" href="${expedient.id}/document/new" data-toggle="modal" data-datatable-id="expedientTipusEstat"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.boto.nou_document"/></a>
	</div>
</script>
<script id="rowhrefTemplateDocuments" type="text/x-jsrender">
	{{if id == null}}
		${expedient.id}/document/{{:codi}}/new
	{{else}}
		${expedient.id}/document/{{:id}}/update
	{{/if}}
</script>
