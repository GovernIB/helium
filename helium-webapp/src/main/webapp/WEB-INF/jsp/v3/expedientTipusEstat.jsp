<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>
<style>
	.table-bordered>tbody>tr>td { max-width: 140px; }
	.selector-contenidor { display: flex; }
	.taula-contenidor {
		margin: 0px;
		overflow: hidden;
		background-color: #f9f9f9;
		border-radius: 8px;
		mask-image: linear-gradient(to left, rgba(255,255,255,0), rgba(255,255,255,1) 50%);
		-webkit-mask-image: linear-gradient(to left, rgba(255,255,255,0), rgba(255,255,255,1) 50%);
		cursor: pointer;
	}
	.taula-contenidor table { border: none; }
	.taula-contenidor table tr:first-child{ border-top: none; }
	.taula-contenidor table tr:first-child td { border-top: none; }
	tr.selected { font-weight: bold; background-color: yellowgreen; }
	.out-border { flex-grow: 1; box-shadow: 0 1px 5px rgb(21 34 53 / 30%); border: none; border-radius: 8px; margin: 10px; opacity: 50%; }
	.out-border.not-selected:hover { opacity: 100%; box-shadow: 0 6px 18px 0 rgb(21 34 53 / 30%); }
	.out-border.selected { opacity: 100%; box-shadow: 0 6px 18px 0 rgb(102 175 233 / 90%);  }

</style>

<c:url var="urlDatatable" value="/v3/expedientTipus/${expedientTipus.id}/estats/datatable"/>
<c:choose>
	<c:when test="${not empty expedientTipus}">

		<table	id="expedientTipusEstat"
				data-toggle="datatable"
				data-url="${urlDatatable}"
				data-paging-enabled="false"
				data-info-type="search+button"
				data-rowhref-toggle="modal"
				data-rowhref-template="#rowhrefTemplateEstats" 
				data-botons-template="#tableButtonsEstatTemplate"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<c:if test="${expedientTipus.tipus == 'ESTAT'}"><th data-col-name="ordre" data-orderable="false" width="10%"><spring:message code="comuns.ordre"/></th></c:if>
					<th data-col-name="codi" data-orderable="false" width="20%" data-template="#cellExpedientTipusEstatCodiTemplate">
					<spring:message code="expedient.tipus.camp.llistat.columna.codi"/>
						<script id="cellExpedientTipusEstatCodiTemplate" type="text/x-jsrender">
								{{if heretat }}
									<span class="dada-heretada">{{:codi}}</span> 
									<span class="label label-primary herencia" title="<spring:message code="expedient.tipus.estat.llistat.codi.heretat"/>">R</span>
								{{else}}
									{{:codi}}
									{{if sobreescriu }}
										<span class="label label-warning herencia" title="<spring:message code="expedient.tipus.estat.llistat.codi.sobreescriu"/>">S</span>
									{{/if}}
								{{/if}}
						</script>
					</th>
					<th data-col-name="nom" data-orderable="false"><spring:message code="comuns.nom"/></th>
					<c:if test="${expedientTipus.tipus == 'ESTAT'}">
					<th data-col-name="permisCount" data-template="#cellPermisosTemplate" data-orderable="false" width="5%">
						<script id="cellPermisosTemplate" type="text/x-jsrender">
							<a href="${expedientTipus.id}/estat/{{:id}}/permisos" data-maximized="true" data-toggle="" class="btn btn-default"><span class="fa fa-key"></span>&nbsp;<spring:message code="expedient.tipus.estat.llistat.accio.permisos"/>&nbsp;<span class="badge">{{:permisCount}}</span></a>
						</script>
					</th>
					<th data-col-name="reglesCount" data-template="#cellReglesTemplate" data-orderable="false" width="5%">
						<script id="cellReglesTemplate" type="text/x-jsrender">
							<a href="${expedientTipus.id}/estat/{{:id}}/regles" data-maximized="true" data-toggle="" class="btn btn-default"><span class="fa fa-eye-slash"></span>&nbsp;<spring:message code="expedient.tipus.estat.llistat.accio.regles"/>&nbsp;<span class="badge">{{:reglesCount}}</span></a>
						</script>
					</th>
					<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="13%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
							<a href="${expedientTipus.id}/estat/{{:id}}/accions" data-maximized="true" data-toggle="modal" class="btn btn-default">
								<span class="badge">
										<span class="fa fa-sign-in"/> {{:accionsEntradaCount}}
								</span>
								<span class="fa fa-bolt"></span>&nbsp;<spring:message code="expedient.tipus.estat.llistat.accio.accions"/>&nbsp;
								<span class="badge">
										<span class="fa fa-sign-out"/> {{:accionsSortidaCount}}
								</span>
							</a>
						</script>
					</th>
					</c:if>
					<th data-col-name="id" data-template="#cellEstatsTemplate" data-orderable="false" width="5%">
						<script id="cellEstatsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								{{if heretat}}
									<li><a data-toggle="modal" href="${expedientTipus.id}/estat/{{:id}}/update"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.visualitzar"/></a></li>
								{{else}}
									<li><a data-toggle="modal" href="${expedientTipus.id}/estat/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
									<c:if test="${expedientTipus.tipus == 'ESTAT'}">
										<li><a data-toggle="modal" href="${expedientTipus.id}/estat/{{:id}}/accions"><span class="fa fa-bolt"></span>&nbsp;<spring:message code="expedient.tipus.estat.llistat.accio.accions"/></a></li>
									</c:if>
									<li><a href="${expedientTipus.id}/estat/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.estat.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
								{{/if}}
							</ul>
						</div>
					</script>
					</th>
					<th data-col-name="accionsEntradaCount" data-visible="false"/>
					<th data-col-name="accionsSortidaCount" data-visible="false"/>
					<th data-col-name="sobreescriu" data-visible="false"/>
					<th data-col-name="heretat" data-visible="false"/>
				</tr>
			</thead>
		</table>
		<script id="tableButtonsEstatTemplate" type="text/x-jsrender">
			<div class="botons-titol text-right">
				<a id="nou_camp" class="btn btn-default" href="${expedientTipus.id}/estat/new" data-toggle="modal" data-datatable-id="expedientTipusEstat"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.estat.nou"/></a>
				<a id="importar_dades" class="btn btn-info" href="${expedientTipus.id}/estat/importar" data-toggle="modal" data-datatable-id="expedientTipusEstat"><span class="fa fa-sign-in"></span>&nbsp;<spring:message code="comu.boto.importar.dades"/></a>
				<a id="exportar_dades" class="btn btn-info" href="${expedientTipus.id}/estat/exportar"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.boto.exportar.dades"/></a>
			</div>
		</script>
		<script id="rowhrefTemplateEstats" type="text/x-jsrender">${expedientTipus.id}/estat/{{:id}}/update</script>

		<div id="modal-selector-ordre" class="modal fade" tabindex="-1" role="dialog">
			<div id="modal-dialog" class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title"><spring:message code="expedient.tipus.estat.llistat.seleccionar.ordre"/></h4>
					</div>
					<div id="selector-ordre" class="modal-body">

					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></button>
						<button type="button" class="btn btn-primary" id="assigna-ordre-btn"><spring:message code="expedient.tipus.estat.llistat.assignar.ordre"/></button>
					</div>
				</div>
			</div>
		</div>

	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>
<script>
	var filaMovem;

	$(document).ready(function() {
		$('#expedientTipusEstat').on('draw.dt', function() {
			// Posa la taula com a ordenable
			$("#expedientTipusEstat").tableDnD({
				onDragClass: "drag",
				onDrop: function(table, row) {
					var pos = row.rowIndex - 1;
					if (pos != filaMovem) {
						<c:choose>
						<c:when test="${expedientTipus.tipus == 'FLOW'}">
					var id= obtenirId(pos);
						canviarPosicioEstat(id,pos);
						</c:when>
						<c:when test="${expedientTipus.tipus == 'ESTAT'}">
						canviarPosicioEstatPerEstats(pos);
						</c:when>
						</c:choose>
						$('tr').off('click');
						$('td').off('click');
					}
				},
				onDragStart: function(table, row) {
						filaMovem = row.rowIndex-1;
				}
			});
			$("#expedientTipusEstat tr").hover(function() {
				$(this.cells[0]).addClass('showDragHandle');
			}, function() {
				$(this.cells[0]).removeClass('showDragHandle');
			});
		});
		$('#exportar_dades').click(function(event) {
			webutilDownloadAndRefresh($(this).attr('href'), event);
		});

		$("#selector-ordre").on("click", ".out-border", function() {
			$(".out-border").removeClass("selected").addClass("not-selected");
			$(this).removeClass("not-selected").addClass("selected");
		})

		$("#assigna-ordre-btn").click(function() {
			let ordreSelected = $(".out-border.selected");
			if (!ordreSelected) {
				return;
			}
			let id = $("div.selector-contenidor").data("estatid");
			let pos = $("div.selector-contenidor").data("pos");
			let ordre = ordreSelected.data("ordre");
			canviarPosicioPerEstat(id, pos, ordre);
		});

		$("#modal-selector-ordre").on("hidden.bs.modal", function () {
			$('#expedientTipusEstat').webutilDatatable('refresh');
		})
	});

	const canviarPosicioEstatPerEstats = (pos) => {
		// const filaModuga = getPosicioFilaMoguda(pos);
		const ordreCodis = getOrdresCodis(pos);
		const id = obtenirId(pos);

		if (ordreCodis.length == 2 || (ordreCodis.length == 3 && (ordreCodis[2].ordre - ordreCodis[0].ordre != 0))) {
			// Denanar a l'usuari quin ordre assignar
			demanarOrdreUsuari(ordreCodis, id, pos);
		} else {
			canviarPosicioPerEstat(id, pos, 'auto');
		}
	}
	const getOrdresCodis = (posfila) => {
		let ordres = [];
		if (posfila > 0) {
			ordres.push(getOrdreCodiFila(posfila - 1));
		}
		ordres.push(getOrdreCodiFila(posfila));
		if (posfila < $("#expedientTipusEstat tbody tr:visible").length - 1) {
			ordres.push(getOrdreCodiFila(posfila + 1));
		}
		return ordres;
	}
	const getOrdreCodiFila = (posfila) => {
		let fila = $("#expedientTipusEstat tr:eq("+getPosicioFilaMoguda(posfila)+")");
		let ordre = fila.find("td:eq(0)").text().trim();
		let codi = fila.find("td:eq(1)").text().trim();
		return {ordre: ordre, codi: codi};
	}
	const demanarOrdreUsuari = (ordreCodis, id, pos) => {
		let midaOrdres = ordreCodis.length;
		let contenidor = $('<div class="selector-contenidor" data-estatid="' + id + '" data-pos="' + pos + '"></div>');
		generaTaula(getPrimerOrdre(ordreCodis, pos), midaOrdres == 3 ? "1-1-2" : pos == 0 ? "1-1" : "9-9").appendTo(contenidor);
		generaTaula(getSegonOrdre(ordreCodis, pos), midaOrdres == 3 ? "1-2-2" : pos == 0 ? "1-2" : "8-9").appendTo(contenidor);
		if (midaOrdres == 3) {
			$("#modal-dialog").addClass("modal-lg");
			generaTaula(getTercerOrdre(ordreCodis), "1-2-3").appendTo(contenidor);
		} else {
			$("#modal-dialog").removeClass("modal-lg");
					}
		$("#selector-ordre").empty();
		$("#selector-ordre").append(contenidor);
		$(".modal-footer button").prop("disabled", false);

		$("#modal-selector-ordre").modal();
				}
	const getPrimerOrdre = (ordreCodi, pos) => {
		let nouOrdreCodi = [];
		const first = pos == 0;
		if (ordreCodi.length == 2 && first) {
			nouOrdreCodi.push({ordre: ordreCodi[1].ordre, codi: ordreCodi[0].codi});
			nouOrdreCodi.push({ordre: ordreCodi[1].ordre, codi: ordreCodi[1].codi});
		} else if (ordreCodi.length == 2) {
			const ordreFilaMoguda = parseInt($("#expedientTipusEstat tbody tr:eq(" + pos + ") td:eq(0)").text().trim());
			const ordreOrigenRepetits = $("#expedientTipusEstat tbody tr:visible td:first-child").filter(function() {
				let ordreActual = parseInt($(this).text().trim());
				return ordreFilaMoguda === ordreActual;
			});
			const repetit = ordreOrigenRepetits.length > 1;
			const ordre = repetit ? parseInt(ordreCodi[0].ordre) : parseInt(ordreCodi[0].ordre) - 1;
			nouOrdreCodi.push({ordre: ordre, codi: ordreCodi[0].codi});
			nouOrdreCodi.push({ordre: ordre, codi: ordreCodi[1].codi});
		} else {
			nouOrdreCodi.push({ordre: ordreCodi[0].ordre, codi: ordreCodi[0].codi});
			nouOrdreCodi.push({ordre: ordreCodi[0].ordre, codi: ordreCodi[1].codi});
			nouOrdreCodi.push({ordre: ordreCodi[2].ordre, codi: ordreCodi[2].codi});
		}
		return nouOrdreCodi;
	}
	const getSegonOrdre = (ordreCodi, pos) => {
		const first = pos == 0;
		let nouOrdreCodi = [];
		if (ordreCodi.length == 2 && first) {
			nouOrdreCodi.push({ordre: ordreCodi[1].ordre, codi: ordreCodi[0].codi});
			nouOrdreCodi.push({ordre: parseInt(ordreCodi[1].ordre) + 1, codi: ordreCodi[1].codi});
		} else if (ordreCodi.length == 2) {
			const ordreFilaMoguda = parseInt($("#expedientTipusEstat tbody tr:eq(" + pos + ") td:eq(0)").text().trim());
			const ordreOrigenRepetits = $("#expedientTipusEstat tbody tr:visible td:first-child").filter(function() {
				let ordreActual = parseInt($(this).text().trim());
				return ordreFilaMoguda === ordreActual;
			});
			const repetit = ordreOrigenRepetits.length > 1;
			const ordre = repetit ? parseInt(ordreCodi[0].ordre) : parseInt(ordreCodi[0].ordre) - 1;
			nouOrdreCodi.push({ordre: ordre, codi: ordreCodi[0].codi});
			nouOrdreCodi.push({ordre: ordre + 1, codi: ordreCodi[1].codi});
		} else {
			nouOrdreCodi.push({ordre: ordreCodi[0].ordre, codi: ordreCodi[0].codi});
			nouOrdreCodi.push({ordre: ordreCodi[2].ordre, codi: ordreCodi[1].codi});
			nouOrdreCodi.push({ordre: ordreCodi[2].ordre, codi: ordreCodi[2].codi});
		}
		return nouOrdreCodi;
	}
	const getTercerOrdre = (ordreCodi) => {
		let nouOrdreCodi = [];
		nouOrdreCodi.push({ordre: ordreCodi[0].ordre, codi: ordreCodi[0].codi});
		nouOrdreCodi.push({ordre: parseInt(ordreCodi[0].ordre) + 1, codi: ordreCodi[1].codi});
		nouOrdreCodi.push({ordre: parseInt(ordreCodi[2].ordre) + 1, codi: ordreCodi[2].codi});
		return nouOrdreCodi;
	}
	const generaTaula = (ordreCodi, ordre) => {
		let outborder = $('<div class="out-border not-selected" data-ordre="' + ordre + '"></div>');
		let contenidor = $('<div class="taula-contenidor"></div>');
		let taula = $('<table class="table table-striped table-bordered dataTable no-footer"></table>');
		taula.append($('<tr class="odd"><td>' + ordreCodi[0].ordre + '</td><td>' + ordreCodi[0].codi + '</td></tr>'));
		taula.append($('<tr class="even selected"><td>' + ordreCodi[1].ordre + '</td><td>' + ordreCodi[1].codi + '</td></tr>'));
		if (ordreCodi.length == 3) {
			taula.append($('<tr class="odd"><td>' + ordreCodi[2].ordre + '</td><td>' + ordreCodi[2].codi + '</td></tr>'));
		}
		contenidor.append(taula);
		outborder.append(contenidor);
		return outborder;
	}

	const obtenirId = (pos) => {
		return $("#expedientTipusEstat tr:eq("+getPosicioFilaMoguda(pos)+")").attr("id").split("_")[1];
	}
	const getPosicioFilaMoguda = (pos) => {
		if (filaMovem == pos) {
			return filaMovem + 1;
		} else {
			if ( filaMovem < pos) {	//baixam elements
				return filaMovem + (pos - filaMovem) + 1;
			} else {					//pujam elements
				return filaMovem - (filaMovem - pos) + 1;
			}
		}
	}



	const canviarPosicioPerEstat = (id, pos, ordre) => {
		var getUrl = '<c:url value="/v3/expedientTipus/${expedientTipusId}/estat/"/>'+id+'/moure/'+pos+'/ordre/'+ordre;
		$.ajax({
			type: 'GET',
			url: getUrl,
			async: true,
			complete: function() {
				webutilRefreshMissatges();
				$("#modal-selector-ordre").modal('hide');
				$('#expedientTipusEstat').webutilDatatable('refresh');
			}
		});
	}
	const canviarPosicioEstat = (id, pos) => {
		// Canvia la ordenació sempre amb ordre ascendent
		// $('#campValidacio').DataTable().order([3, 'asc']);
		var getUrl = '<c:url value="/v3/expedientTipus/${expedientTipusId}/estat/"/>'+id+'/moure/'+pos;
		$.ajax({
			type: 'GET',
			url: getUrl,
			async: true,
			complete: function() {
				webutilRefreshMissatges();
				$('#expedientTipusEstat').webutilDatatable('refresh');
			}
		});
	}
</script>