<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%--<script src="<c:url value="/webjars/jquery/1.12.0/dist/jquery.min.js"/>"></script>--%>
<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
<script src="<c:url value="/webjars/datatables.net-select/1.1.0/js/dataTables.select.min.js"/>"></script>
<script src="<c:url value="/js/jsrender.min.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<%--<script slot="<c:url value="/js/bootbox.all.min.js"/>"></script>--%>

<%--Visualitzar detall i previsualització del document => Es troba a expedientPipelles.jsp--%>
<%--<script src="<c:url value="/js/expdoc-viewer.js"/>"></script>--%>

<script type="application/javascript">

	<%-- Paràmetres necessaris per al visor de la previsualització del document	--%>
	var urlDescarregaBase = '<c:url value="/nodeco/v3/expedient/${expedient.id}/document/"/>';
	var urlViewer = '<c:url value="/webjars/pdf-js/2.13.216/web/viewer.html"/>';
	var msgViewer = [];
	msgViewer['previs'] = '<spring:message code="expedient.document.previsualitzacio"/>';
	msgViewer['nom'] = '<spring:message code="expedient.document.info.nom"/>';
	msgViewer['error'] = '<spring:message code="expedient.document.previsualitzacio.error"/>';
	msgViewer['warning'] = '<spring:message code="expedient.document.previsualitzacio.warning"/>';


	$(document).ready(() => {
		<%-- Al recarregar la taula... --%>
		$("#expedientDocuments").on("draw.dt", () => {

			<%-- Si el primer document és inexistent fem la vora superior de 3px, ja que amb els 2px per defecte no es veu --%>
			let firstTr = $("#expedientDocuments tbody tr:first-child");
			if (firstTr.hasClass("no-data")) {
				firstTr.css("border-top", "dashed 3px #BBB");
			}

			<%-- Per cada fila... --%>
			$("#expedientDocuments tbody tr").each((index, element) => {
				if (!$(element).hasClass("no-data")) {
					<%-- Obrim el detall del document al fer clic a les cel·les 1-4 del document --%>
					$("td:gt(0):lt(4)", $(element)).click((event) => {
						if (event.target.tagName.toLowerCase() !== 'a' && (event.target.cellIndex === undefined || event.target.cellIndex === 0 || event.target.cellIndex > 4)) return;
						toggleDocDetails(element);
					}).css('cursor', 'pointer');
				} else {
					<%-- Eliminam els checks dels documents inexistents--%>
					$(element).find(".fa-square-o").remove();
				}
			});

			filtraDadesDoc();
		});

		// Plegar / Desplegar els detalls d'un document
		$("#expedientDocuments").on('click', 'div.card-header.pointer', (event) => {
			let cardHeader = $(event.currentTarget);
			toggleCard(cardHeader);
		});

		// Plegar / Desplegar la previasualització d'un document
		$("#expedientDocuments").on('click', '.previs-icon', (event) => {
			let viewer = $($(event.currentTarget).attr('href')).find('.viewer');
			if (viewer.length) {
				toggleViewer(viewer);
			}
		});

		<%-- Al seleccionar i deseleccionar, eliminarem els checks dels documents inexistents--%>
		$("#expedientDocuments").on('selectionchange.dt', () => {
			$("#expedientDocuments tbody tr.no-data").each((index, element) => {
				$(element).find(".fa-square-o").remove();
				$(element).find(".fa-check-square-o").remove();
			});
		});

		$('body').on('change', '#boto-tots', () => {
			opcionsVisualitzacioChanged('boto-tots')
		});
		$('body').on('change', '#boto-dpendents', () => {
			filtraDadesDoc();
		});

		$('body').on('keypress', "#searchDocuments", function (keyData) {
			if (keyData.which == 13) { //execute on keyenter
				filtraDadesDoc();
			}
		});
	});

	const opcionsVisualitzacioChanged = () => {
		let serverParams = {};
		serverParams['tots'] = $("#boto-tots").parent().hasClass("active");
		$('#expedientDocuments').webutilDatatable('refresh', serverParams);
	}

	const filtraDadesDoc = () => {
		const filtre = $("#searchDocuments").val().toLowerCase();
		const mostrarPendents = $("#boto-dpendents").parent().hasClass("active");

		$("#expedientDocuments tbody").removeHighlight();
		$("#expedientDocuments tr").show();

		if (filtre != '')
			$("#expedientDocuments tbody").highlight(filtre);

		let visibles = 0;
		$("#expedientDocuments tbody>tr").not(".datatable-dades-carregant,#expedientDocuments_noDades").each((index, fila) => {
			if (visualitzarFilaDoc(fila, filtre, mostrarPendents)) {
				visibles++;
				$(fila).show();
			} else {
				$(fila).hide();
			}
		});
		if (visibles > 0) {
			$("#expedientDocuments_info").text("Mostrant 1 a " + visibles + " de " + visibles + " resultats");
			$('#expedientDocuments_noDades').hide();
		} else {
			$("#expedientDocuments_info").text("Mostrant 0 resultats");
			if($('#expedientDocuments_noDades').length){
				$('#expedientDocuments_noDades').show();
			}else{
				$('<tr id="expedientDocuments_noDades"><td colspan="4">Sense dades</td></tr>').appendTo("#expedientDocuments tbody");
			}
		}
		$(".datatable-dades-carregant").hide();
	}

	const visualitzarFilaDoc = (fila, filtre, mostrarPendents) => {
		let mostrar = true;
		if (!mostrarPendents) {
			if ($(fila).hasClass("no-data")) {
				mostrar = false;
			}
		}
		if (mostrar && filtre != '') {
			if (!$(fila).find('td:eq(1)').text().toLowerCase().includes(filtre) &&
					!$(fila).find('td:eq(2)').text().toLowerCase().includes(filtre) &&
					!$(fila).find('td:eq(3)').text().toLowerCase().includes(filtre) &&
					!$(fila).find('td:eq(4)').text().toLowerCase().includes(filtre)) {
				mostrar = false;
			}
		}
		return mostrar;
	}

</script>
<style>
	#expedientDocuments {border-collapse: collapse !important; margin-bottom: 15px !important;}
	#expedientDocuments tbody tr td {vertical-align: middle;}
	.table-bordered>tbody>tr>td {max-width: 155px;}
	.no-data {color: #bbb; border: dashed 2px; cursor: default !important;}
	.doc-icon {color: #337ab7;}
	.nodoc-icon {margin-right: 12px;}
	.adjuntIcon {top:-10px; left:-7px;}
	.extensionIcon {color: white; position: relative; left: -26px; top: 5px; font-size: 9px; font-weight: bold; margin-right: -12px;}
	.obligatori {background-position: right 4px !important; padding-right: 10px !important;}
	.doc-error {color: indianred;}
	.doc-error-icon {top: 3px;}
	.label-doc {float: right; line-height: 16px; margin-right: 3px;}
	.doc-details {background-color: #EFF2F5;}
	.doc-details td {padding: 0px;}
	.pill-link {position: relative !important; display: block !important; padding: 5px 10px !important; margin-right: 2px !important;}
	.highlight {background-color: #fff34d; color: black; padding:1px 2px;}
	.doc-bloquejat {font-size: 24px; color: #AAAAAA;}
</style>

<c:url var="urlDatatable" value="/v3/expedient/${expedient.id}/document/datatable"/>

<table	id="expedientDocuments"
		  data-toggle="datatable"
		  data-url="${urlDatatable}"
		  data-ajax-request-type="POST"
		  data-paging-enabled="false"
		  data-ordering="true"
		  data-default-order="25"
		  data-info-type="button"
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
		<th data-col-name="docError" data-visible="false"/>
		<th data-col-name="adjunt" data-visible="false"/>
		<th data-col-name="signat" data-visible="false"/>
		<th data-col-name="signUrlVer" data-visible="false"/>
		<th data-col-name="arxiuActiu" data-visible="false"/>
		<th data-col-name="ntiCsv" data-visible="false"/>
		<th data-col-name="registrat" data-visible="false"/>
		<th data-col-name="docValid" data-visible="false"/>
		<th data-col-name="notificable" data-visible="false"/>
		<th data-col-name="psActiu" data-visible="false"/>
		<th data-col-name="psPendent" data-visible="false"/>
		<th data-col-name="psError" data-visible="false"/>
		<th data-col-name="psEstat" data-visible="false"/>
		<th data-col-name="psDocId" data-visible="false"/>
		<th data-col-name="ntiActiu" data-visible="false"/>
		<th data-col-name="arxiuUuid" data-visible="false"/>
		<th data-col-name="expUuid" data-visible="false"/>
		<th data-col-name="notificat" data-visible="false"/>
		<th data-col-name="anotacioId" data-visible="false"/>
		<th data-col-name="anotacioIdf" data-visible="false"/>
		<th data-col-name="extensio" data-visible="false"/>
		<th data-col-name="editable" data-visible="false"/>
		<th data-col-name="obligatori" data-visible="false"/>
		<th data-col-name="nom" data-orderable="true" width="54%" data-template="#cellNomTemplate">
			<spring:message code="expedient.tipus.document.llistat.columna.nom"/>
			<script id="cellNomTemplate" type="text/x-jsrender">
				{{if id == null}}
					<span class="fa fa-2x fa-file-o nodoc-icon"></span>
					<span {{if obligatori}}class="obligatori"{{/if}}>{{:nom}}</span>
				{{else error}}
					<span class="fa-stack fa-1x no-doc" title="{{:docError}}">
						<span class="fa fa-file fa-stack-2x doc-error"></span>
						<span class="fa fa-warning fa-inverse fa-stack-1x doc-error-icon"></span>
					</span>
					{{if !editable}}
						<span class="fa fa-lock pull-right doc-bloquejat" title="Document bloquejat en aquest estat"></span>
					{{/if}}
				{{else}}
					<a href="${expedient.id}/document/{{:id}}/descarregar" title="<spring:message code="expedient.document.descarregar"/>">
						<span class="fa-stack fa-1x doc-icon">
							<span class="fa fa-file fa-stack-2x"></span>
							{{if adjunt}}<span class="adjuntIcon fa fa-stack-1x fa-inverse fa-paperclip"></span>{{/if}}
						</span>
						<span class="extensionIcon">{{:extensio}}</span>
					</a>
					<span {{if obligatori}}class="obligatori"{{/if}}>{{:nom}}</span>
					{{if !editable}}
						<span class="fa fa-lock pull-right doc-bloquejat" title="Document bloquejat en aquest estat"></span>
					{{/if}}
					<%--NTI/Arxiu--%>
					{{if ntiActiu}}
						<a href="${expedient.id}/proces/${expedient.processInstanceId}/document/{{:id}}/metadadesNti" data-toggle="modal">
						<span id="nti_{{:id}}" class="label label-info label-doc">
							{{if arxiuActiu}}
								<spring:message code="expedient.info.etiqueta.arxiu"/>
								{{if arxiuUuid == null}}
									<span class="fa fa-warning text-danger" title="<spring:message code='expedient.document.arxiu.error.uuidnoexistent' />"></span>
								{{/if}}
							{{else}}
								<spring:message code="expedient.info.etiqueta.nti"/>
							{{/if}}
						</span>
						</a>
					{{/if}}
					<%--Notificable--%>
					{{if notificable && !notificat}}<span id="notificable_{{:id}}" class="label label-default label-doc" title="<spring:message code='expedient.document.notificable'/>"><span class="fa fa-paper-plane-o"></span></span>{{/if}}
					<%--Signat     --%>
					{{if signat}}<span id="signat_{{:id}}" class="label label-primary label-doc" title="<spring:message code='expedient.document.signat'/>"><span class="fa fa-certificate"></span></span>{{/if}}
					<%--Registrat  --%>
					{{if registrat}}<span id="signat_{{:id}}" class="label label-success label-doc" title="<spring:message code='expedient.document.registrat'/>"><span class="fa fa-book"></span></span>{{/if}}
					<%--Portafirmes--%>
					{{if psPendent}}
						{{if psError}}
							{{if psEstat == "PROCESSAT"}}
								<span id="psigna_{{:id}}" class="label label-danger label-doc" title="<spring:message code='expedient.document.rebutjat.psigna.error'/>"><spring:message code="expedient.document.info.etiqueta.psigna"/> <span class="fa fa-exclamation-triangle></span></span>
							{{else}}
								<span id="psigna_{{:id}}" class="label label-danger label-doc" title="<spring:message code='expedient.document.pendent.psigna.error'/>"><spring:message code="expedient.document.info.etiqueta.psigna"/> <span class="fa fa-exclamation-triangle></span></span>
							{{/if}}
						{{else}}
							<span id="psigna_{{:id}}" class="label label-warning label-doc" title="<spring:message code='expedient.document.pendent.psigna'/>"><spring:message code="expedient.document.info.etiqueta.psigna"/> <span class="fa fa-clock-o"></span></span>
						{{/if}}
					{{/if}}
					<%--Notificat  --%>
					{{if notificat}}<span id="notificat_{{:id}}" class="label label-warning label-doc" title="<spring:message code='expedient.document.notificat'/>"><spring:message code="expedient.document.info.etiqueta.notificat"/></span>{{/if}}
					<%--De anotacio--%>
					{{if anotacioId != null}}<span id="anotacio_{{:id}}" class="label label-warning label-doc" title="<spring:message code='expedient.document.info.etiqueta.anotacio.title' arguments='{{:anotacioIdf}}'/>"><spring:message code="expedient.document.info.etiqueta.anotacio"/></span>{{/if}}
					<%--Doc Invàlid--%>
					{{if !docValid}}
						<span class="label label-danger label-doc" title="<spring:message code='expedient.document.invalid' arguments='{{:docError}}'/>"><span class="fa fa-exclamation-triangle"></span></span>
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
		<th data-col-name="dataDocument" data-converter="date" data-orderable="true" width="10%"><spring:message code="expedient.document.data"/></th>
		<th data-col-name="dataCreacio" data-converter="datetimeminute" data-orderable="true" width="10%"><spring:message code="expedient.document.adjuntat"/></th>
		<th data-col-name="pinbalActiu" data-visible="false"></th>
		<th data-col-name="id" data-template="#cellDocumentAccionsTemplate" data-orderable="false" width="5%">
			<script id="cellDocumentAccionsTemplate" type="text/x-jsrender">
			{{if id == null}}
				{{if editable && ${expedient.permisDocManagement}}}
					{{if !pinbalActiu}}
						<a class="btn btn-default" href="${expedient.id}/document/{{:codi}}/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.boto.nou_document"/></a>
					{{/if}}

					<c:if test="${documentsPinbal == true}"> 
						{{if pinbalActiu}}
							<!-- Cas amb dropdown -->
							<div class="btn-group">
								<button class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
									<span class="fa fa-plus"></span> <spring:message code="expedient.boto.nou_document"/> <span class="caret"></span>
								</button>
								<ul class="dropdown-menu">
									<li>
										<a href="${expedient.id}/document/{{:codi}}/new"
										data-toggle="modal"
										data-datatable-id="expedientDocuments">
											<span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.boto.nou_document"/>
										</a>
									</li>
									<li>
										<a href="${expedient.id}/documentPinbal/new?codi={{:codi}}"
										data-toggle="modal">
											<span class="fa fa-file-text-o"></span>&nbsp;<spring:message code="expedient.boto.nou_documentPinbal"/>
										</a>
									</li>
								</ul>
							</div>
						{{/if}}
					</c:if>

				{{else}}
					<span class="fa fa-lock pull-right doc-bloquejat" title="Document bloquejat en aquest estat"></span>
				{{/if}}
			{{else}}
				<div data-document-id="{{:id}}" <%--data-arxivat="{{:arxivat}}" data-psigna="{{psignaInfo}}"--%> class="dropdown accionsDocument">
					<button class="btn btn-primary" data-toggle="dropdown" style="width:100%;"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
					<ul class="dropdown-menu dropdown-menu-right">
						{{if !error}}
							<%--Modificar  TODO: Si no està en estat definitiu ni pendent de firma (portafirmes)--%>
							{{if editable && ${expedient.permisDocManagement} && !signat}}<li><a data-toggle="modal" href="${expedient.id}/document/{{:id}}/update"><span class="fa fa-pencil fa-fw"></span>&nbsp;<spring:message code="comuns.modificar"/></a></li>{{/if}}
							<%--Borrar --%>
							{{if editable && ${expedient.permisDocManagement}}} 
								<li>				
									{{if signat && arxiuActiu}}
										<a href="<c:url value="/v3/expedient/${expedient.id}/proces/${expedient.processInstanceId}/document/{{:id}}/esborrar"/>" data-toggle="ajax" data-confirm="<spring:message code="expedient.document.firmat.esborrar.confirmacio"/>"><span class="fa fa-trash-o fa-fw"></span>&nbsp;<spring:message code="comuns.esborrar"/></a>
									{{else}}
										<a href="<c:url value="/v3/expedient/${expedient.id}/proces/${expedient.processInstanceId}/document/{{:id}}/esborrar"/>" data-toggle="ajax" data-confirm="<spring:message code="expedient.llistat.document.confirm_esborrar"/>"><span class="fa fa-trash-o fa-fw"></span>&nbsp;<spring:message code="comuns.esborrar"/></a>
									{{/if}}
								</li>
							{{/if}}
							<%--Notificar  --%>
							{{if notificable}}<li><a href="${expedient.id}/document/{{:id}}/notificar" data-toggle="modal"><span class="fa fa-paper-plane-o fa-fw"></span>&nbsp;<spring:message code="expedient.document.notificar"/></a></li>{{/if}}
							<%--Signat     --%>
							{{if signat}}
								{{if !arxiuActiu}}
									{{if signUrlVer != null}}
										<li><a href="{{:signUrlVer}}" target="_blank" onclick="event.stopPropagation()"><span class="fa fa-certificate fa-fw"></span>&nbsp;<spring:message code="expedient.document.signat.detalls"/></a></li>
									{{else}}
										<li><a href="${expedient.id}/document/{{:id}}/signatura/verificar?urlVerificacioCustodia={{:signUrlVer}}" data-toggle="modal" data-refrescar="false"><span class="fa fa-certificate fa-fw"></span>&nbsp;<spring:message code="expedient.document.signat.detalls"/></a></li>
									{{/if}}
									{{if editable}}
										<li><a href="${expedient.id}/document/{{:id}}/signatura/esborrar" data-toggle="ajax" data-confirm="<spring:message code="expedient.document.confirm_esborrar_signatures"/>"><span class="fa fa-ban fa-fw"></span>&nbsp;<spring:message code="comuns.esborrar"/></a></li>
									{{/if}}
								{{else}}
									{{if ntiCsv != null}}
										<li id="signatura-lnk"><a href="{{:signUrlVer}}" target="_blank" onclick="event.stopPropagation()"><span class="fa fa-certificate" data-refrescar="false"></span>&nbsp;<spring:message code="expedient.document.signat.detalls"/></a></li>
									{{else}}
										<li id="signatura-lnk"><a href="${expedient.id}/document/{{:id}}/signatura/verificarCsv" data-toggle="modal"><span class="fa fa-certificate"></span>&nbsp;<spring:message code="expedient.document.signat.detalls"/></a></li>
									{{/if}}
								{{/if}}
							{{/if}}
							<%--Registrat  --%>
							{{if registrat}}
								<li><a href="${expedient.id}/proces/${expedient.processInstanceId}/document/{{:id}}/registre/verificar" data-toggle="modal"><span class="fa fa-book fa-fw" data-refrescar="false"></span>&nbsp;<spring:message code="expedient.document.registrat.detalls"/></a></li>
							{{/if}}
							<%--Portafirmes--%>
							{{if psPendent}}
								<li><a href="${expedient.id}/proces/${expedient.processInstanceId}/document/{{:id}}/pendentSignatura" data-toggle="modal"><span class="fa fa-clock-o fa-fw" data-refrescar="false"></span>&nbsp;<spring:message code="expedient.document.psigna.reintentar"/></a></li>
							{{/if}}
							<%--NTI        --%>
							{{if ntiActiu}}
								<li><a href="${expedient.id}/proces/${expedient.processInstanceId}/document/{{:id}}/metadadesNti" data-toggle="modal"><span class="fa fa-bookmark fa-fw" data-refrescar="false"></span>&nbsp;<spring:message code="expedient.document.info.etiqueta.nti.detall"/></a></li>
							{{/if}}
							<%--De anotacio--%>
							{{if anotacioId != null}}
								<li><a href="../anotacio/{{:anotacioId}}" data-toggle="modal"><span class="fa fa-sign-out fa-fw" data-refrescar="false"></span>&nbsp;<spring:message code="expedient.document.info.anotacio.detall"/></a></li>
							{{/if}}
						{{/if}}

						<%--Desar a arxiu--%>
						{{if arxiuActiu && arxiuUuid == null}}
							{{if expUuid == null}}
								<li title="<spring:message code="expedient.document.signat.detalls"/>"><a class="disabled" disabled='disabled' href="#"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="expedient.document.arxiu.desar"/></a></li>
							{{else}}
								<li><a class="${expedientPare.arxiuUuid == null ? 'disabled' : ''}" href="${expedient.id}/document/{{:id}}/guardarDocumentArxiu"/>"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="expedient.document.arxiu.desar"/></a></li>
							{{/if}}
						{{/if}}

						<%--Firma en navegador --%>
						{{if editable && !signat && arxiuUuid != null && !psPendent}}
								<li><a href="${expedient.id}/proces/${expedient.processInstanceId}/document/{{:id}}/firmaPassarela" data-toggle="modal"><span class="fa fa-pencil-square-o fa-fw" data-refrescar="false"></span>&nbsp;<spring:message code="expedient.document.firmaPassarela"/></a></li>
						{{/if}}
						<%-- TODO: --%>
						<%--Firma en portafirmes --%>
						{{if editable && psActiu && !signat && arxiuUuid != null && !psPendent}}
								<li>
									<a 	href="${expedient.id}/proces/${expedient.processInstanceId}/document/{{:id}}/enviarPortasignatures"
														data-toggle="modal"
														data-rdt-link-callback="recargarPanel(${expedient.processInstanceId});"
														class="icon enviarPortasignatures">
														<span class="fa fa-envelope-o" /></span>
														<spring:message code='expedient.document.enviar.portasignatures' />
									</a>
								</li>
						{{/if}}
						<%--Enviar a viafirma?? --%>

						<%-- Exportació ENI --%>


						<%-- FI TODO --%>

						<%--Descarregar--%>
						{{if !signat}}
							<li><a href="${expedient.id}/document/{{:id}}/descarregar"><span class="fa fa-download fa-fw"></span>&nbsp;<spring:message code="expedient.document.descarregar"/></a></li>
						{{/if}}
						{{if signat}}
							<li>
								<a href="${expedient.id}/document/{{:id}}/descarregar/original">
									<span class="fa fa-download"></span>
									<spring:message code="comu.boto.descarregar"/>
								</a>
							</li>
						
							<li>
								<a href="${expedient.id}/document/{{:id}}/descarregar/imprimible">
									<span class="fa fa-print"></span>
									<spring:message code="expedient.document.descarregar.autentica.imprimible"/>
								</a>
							</li>
						{{/if}}
					</ul>
				</div>
			{{/if}}
			</script>
		</th>
	</tr>
	</thead>
</table>
<script id="tableButtonsDocumentsTemplate" type="text/x-jsrender">
	<div class="botons-titol text-right">
		<span style="padding-left: 5px">
			<span class="fa fa-search" style="position: absolute;float: left;padding-left: 10px;padding-top: 10px;"></span>
			<input id="searchDocuments" class="form-control" placeholder="<spring:message code="expedient.document.filtrar"/>" autocomplete="off" spellcheck="false" autocorrect="off" tabindex="1" style="padding-left: 35px; max-width: 165px;">
		</span>
		<div class="btn-group" data-toggle="buttons">
			<c:if test="${expedient.permisAdministration}">
				<label class="btn btn-default" title="<spring:message code='expedient.document.mostrar.tots'/>">
					<input type="checkbox" id="boto-tots" autocomplete="off"><span class="fa fa-globe"></span>
				</label>
			</c:if>
			<label class="btn btn-default active" title="<spring:message code='expedient.dada.mostrar.pendents'/>">
				<input type="checkbox" id="boto-dpendents" autocomplete="off" checked><span class="fa fa-plus"></span>
			</label>
		</div>
		<a class="btn btn-default" 
			href="../../v3/expedient/${expedientId}/document/notificarZip" "
			data-toggle="modal">
				<span class="fa fa-paper-plane"></span>
				<spring:message code="expedient.boto.notificar_zip"/>
		</a>
		<a id="descarregarZip" href="<c:url value="/v3/expedient/${expedient.id}/document/descarregar"/>" class="btn btn-default" title="<spring:message code="expedient.document.descarregar.zip"/>">
			<span class="fa fa-download"></span> <spring:message code="comu.boto.descarregar"/> <span id="descarregarCount" class="badge">&nbsp;</span>
		</a>
		<c:if test="${expedient.permisDocManagement}">
			<c:if test="${documentsPinbal==false}">
			<a	id="nou_document"
				class="btn btn-default"
				href="${expedient.id}/document/new"
				data-toggle="modal"
				data-datatable-id="expedientDocuments">
					<span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.boto.nou_document"/>
			</a>
			</c:if>
			<c:if test="${documentsPinbal==true}">

						<div class="btn-group">
							<button class="btn btn-default dropdown-toggle" data-toggle="dropdown">
								<span class="fa fa-plus"></span> <spring:message code="expedient.boto.nou_document"/> <span class="caret"></span>
							</button>
							<ul class="dropdown-menu">
								<li>
			<a	id="nou_document"
				href="${expedient.id}/document/new"
				data-toggle="modal"
				data-datatable-id="expedientDocuments">
					<span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.boto.nou_document"/>
			</a>
								</li>
								<c:if test="${isPinbalActiu}">
									<li>
										<a 	id="a_nou_document_${proces.id}"
											href="${expedient.id}/documentPinbal/new"
											data-toggle="modal">
											<span class="fa fa-file-text-o"></span>
											<spring:message code="expedient.boto.nou_documentPinbal"/>
										</a>
									</li>
								</c:if>
							</ul>
						</div>

			</c:if>
		</c:if>
	</div>
</script>
<script id="rowhrefTemplateDocuments" type="text/x-jsrender">
	{{if id == null}}
		${expedient.id}/document/{{:codi}}/new
	{{else}}
		${expedient.id}/document/{{:id}}/update
	{{/if}}
</script>
