<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
<script src="<c:url value="/webjars/datatables.net-select/1.1.0/js/dataTables.select.min.js"/>"></script>
<script src="<c:url value="/js/jsrender.min.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<%--<script src="<c:url value="/js/webutil.modal.js"/>"></script>--%>

<script type="application/javascript">
	$(document).ready(() => {
		<%-- Al recarregar la taula... --%>
		$("#expedientDades").on("draw.dt", () => {

			<%-- Si el primer document és inexistent fem la vora superior de 3px, ja que amb els 2px per defecte no es veu --%>
			let firstTr = $("#expedientDades tbody tr:first-child");
			if (firstTr.hasClass("no-data")) {
				firstTr.css("border-top", "dashed 3px #BBB");
			}

			<%-- Per cada fila... --%>
<%--			$("#expedientDades tbody tr").each((index, element) => {--%>
<%--				if ($(element).hasClass("no-data")) {--%>
<%--					&lt;%&ndash; Eliminam els checks dels documents inexistents&ndash;%&gt;--%>
<%--					$(element).find(".fa-square-o").remove();--%>
<%--				}--%>
<%--			})--%>
		});

		<%-- Al seleccionar i deseleccionar, eliminarem els checks dels documents inexistents--%>
		// $("#expedientDades").on('selectionchange.dt', () => {
		// 	console.log('selectionchange');
		// 	$("#expedientDades tbody tr.no-data").each((index, element) => {
		// 		$(element).find(".fa-square-o").remove();
		// 		$(element).find(".fa-check-square-o").remove();
		// 	});
		// });


		// Botó per tornar a dalt: scroll top
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
	});
</script>
<style>
	#expedientDades {border-collapse: collapse !important; margin-bottom: 15px !important;}
	#expedientDades tbody tr td {vertical-align: middle;}
	.table-bordered>tbody>tr>td {max-width: 155px;}
	.no-data {color: #bbb; border: dashed 2px; cursor: default !important;}
	.btn-top {position: fixed; z-index: 1000; right: 15px; bottom: 50px; background-color: #FFF; padding: 0 5px 0 5px; border-radius: 5px; cursor: pointer; opacity: 0.1;}
	.doc-icon {color: #337ab7;}
	.nodoc-icon {margin-right: 12px;}
	.adjustIcon {top:-2px;}
	.extensionIcon {color: white; position: relative; left: -26px; top: 5px; font-size: 9px; font-weight: bold; margin-right: -12px;}
	.doc-error {color: indianred;}
	.doc-error-icon {top: 3px;}
	.label-doc {float: right; line-height: 16px;}
	.doc-details {background-color: #EFF2F5;}
	.doc-details td {padding: 0px;}
	.pill-link {position: relative !important; display: block !important; padding: 5px 10px !important; margin-right: 2px !important;}
	.text-center {text-align: center !important;}
</style>

<c:url var="urlDatatable" value="/v3/expedient/${expedient.id}/dada/datatable"/>

<table	id="expedientDades"
		  data-toggle="datatable"
		  data-url="${urlDatatable}"
		  data-ajax-request-type="POST"
		  data-paging-enabled="false"
		  data-ordering="true"
		  data-default-order="4"
		  data-info-type="search+button"
		  data-rowcolid-nullclass="no-data"
		  data-selection-enabled="false"
<%--		  data-selection-url="${expedient.id}/dada/selection"--%>
<%--		  data-selection-counter="#descarregarCount"--%>
		  data-botons-template="#tableButtonsDadesTemplate"
		  class="table table-striped table-bordered table-hover">
	<thead>
	<tr>
		<th data-col-name="id" data-visible="false"/>
		<th data-col-name="campId" data-visible="false"/>
		<th data-col-name="campCodi" data-visible="false"/>
<%--		<th data-col-name="required" data-visible="false"/>--%>
<%--		<th data-col-name="ocult" data-visible="false"/>--%>
<%--		<th data-col-name="multiple" data-visible="false"/>--%>
<%--		<th data-col-name="editable" data-visible="false"/>--%>
		<th data-col-name="tipus" data-orderable="true" width="1%" data-template="#cellTipusTemplate" data-class="text-center">
			<script id="cellTipusTemplate" type="text/x-jsrender">
				{{if tipus == null}}
					<span class="fa fa-2x fa-file-o nodoc-icon"></span>
				{{else}}
					{{if tipus == "STRING"}}<span class="fa-stack fa-1x"><span class="fa fa-square-o fa-stack-2x"></span><span class="adjustIcon fa fa-stack-1x">T</span></span>{{/if}}
					{{if tipus == "INTEGER"}}<span class="fa-stack fa-1x"><span class="fa fa-square-o fa-stack-2x"></span><span class="adjustIcon fa fa-stack-1x">99</span></span>{{/if}}
					{{if tipus == "FLOAT"}}<span class="fa-stack fa-1x"><span class="fa fa-square-o fa-stack-2x"></span><span class="adjustIcon fa fa-stack-1x">9,9</span></span>{{/if}}
					{{if tipus == "BOOLEAN"}}<span class="fa fa-2x fa-check-square-o"></span>{{/if}}
					{{if tipus == "TEXTAREA"}}<span class="fa-stack fa-1x"><span class="fa fa-square-o fa-stack-2x"></span><span class="adjustIcon fa fa-stack-1x fa-bars"></span></span>{{/if}}
					{{if tipus == "DATE"}}<span class="fa fa-2x fa-calendar" style="font-size:1.6em;"></span>{{/if}}
					{{if tipus == "PRICE"}}<span class="fa fa-2x fa-eur"></span>{{/if}}
					{{if tipus == "TERMINI"}}<span class="fa fa-2x fa-clock-o"></span>{{/if}}
					{{if tipus == "SELECCIO"}}<span class="fa fa-2x fa-chevron-down"></span>{{/if}}
					{{if tipus == "SUGGEST"}}<span class="fa fa-2x fa-chevron-circle-down"></span>{{/if}}
					{{if tipus == "REGISTRE"}}<span class="fa fa-2x fa-table"></span>{{/if}}
					{{if tipus == "ACCIO"}}<span class="fa fa-2x fa-bolt"></span>{{/if}}
				{{/if}}
			</script>
		</th>
		<th data-col-name="nom" data-orderable="true" width="20%"><spring:message code="expedient.tipus.document.llistat.columna.nom"/></th>
		<th data-col-name="valor" data-orderable="false" width="70%"  data-template="#cellValorTemplate">
			<spring:message code="expedient.nova.data.valor"/>
			<script id="cellValorTemplate" type="text/x-jsrender">
				{{if valor.registre}}
					<ul class="list-group">
						<li class="list-group-item d-flex justify-content-between border-0">
							<c:forEach var="header" items="${valor.valorHeader}">
								<div class="d-flex flex-column text-dark font-weight-bold text-sm <c:if test="${header.value}">obligatori</c:if>">${header.key}</div>
							</c:forEach>
						</li>
						<c:forEach var="fila" items="${valor.valorBody}">
							<li class="list-group-item d-flex justify-content-between border-0">
								<c:forEach var="cela" items="${fila}">
									<div class="d-flex flex-column text-sm">${cela}</div>
								</c:forEach>
							</li>
						</c:forEach>
					</ul>
				{{else}}
					{{if valor.multiple}}
						<ul class="list-group">
							<c:forEach var="fila" items="${valor.valorMultiple}">
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-sm">${fila}</div>
								</li>
							</c:forEach>
						</ul>
					{{else}}
						{{:valor.valorSimple}}
					{{/if}}
				{{/if}}
			</script>
		</th>
		<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="5%">
			<script id="cellAccionsTemplate" type="text/x-jsrender">
			{{if id == null}}
				<a class="btn btn-default" href="${expedient.id}/dada/{{:campCodi}}/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.boto.nova_dada"/></a>
			{{else}}
				<div data-document-id="{{:id}}" <%--data-arxivat="{{:arxivat}}" data-psigna="{{psignaInfo}}"--%> class="dropdown accionsDocument">
					<button class="btn btn-primary" data-toggle="dropdown" style="width:100%;"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
					<ul class="dropdown-menu dropdown-menu-right">
						{{if !error && editable}}
							{{if editable}}<li><a data-toggle="modal" href="${expedient.id}/dada/{{:id}}/update"><span class="fa fa-pencil fa-fw"></span>&nbsp;<spring:message code="comuns.modificar"/></a></li>{{/if}}
							{{if editable}}<li><a href="${expedient.id}/dada/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.llistat.document.confirm_esborrar"/>"><span class="fa fa-trash-o fa-fw"></span>&nbsp;<spring:message code="comuns.esborrar"/></a></li>{{/if}}
						{{/if}}
					</ul>
				</div>
			{{/if}}
			</script>
		</th>
	</tr>
	</thead>
</table>
<div class="btn-top">
	<span class="fa fa-arrow-up"></span>
</div>
<script id="tableButtonsDadesTemplate" type="text/x-jsrender">
	<div class="botons-titol text-right">
		<a id="boto-ocults" href="#" class="btn btn-default<c:if test="${ambOcults}"> active</c:if>">
			<c:choose>
				<c:when test="${ambOcults}"><span class="fa fa-check-square-o"></span></c:when>
				<c:otherwise><span class="fa fa-square-o"></span></c:otherwise>
			</c:choose>
			<spring:message code='expedient.dada.mostrar.ocultes'/>
		</a>
		<a id="nova_dada" class="btn btn-default" href="${expedient.id}/dada/new" data-toggle="modal" data-adjust-height="false" data-height="350" data-datatable-id="expedientDades"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.boto.nova_dada"/></a>
	</div>
</script>
<script id="rowhrefTemplateDades" type="text/x-jsrender">
	{{if id == null}}
		${expedient.id}/dada/{{:codi}}/new
	{{else}}
		${expedient.id}/dada/{{:id}}/update
	{{/if}}
</script>
