<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="expedient.tipus.regla.titol"/></title>
	<meta name="title" content="<spring:message code="expedient.tipus.regla.titol"/>"/>
	<meta name="screen" content="regles estats"/>
	<meta name="subtitle" content="${expedientTipus.nom}:${estat.nom}"/>
	<meta name="title-icon-class" content="fa fa-key"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>
	<hel:modalHead/>
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="regla/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.regla.accio.nou"/></a>
	</div>
	<table	id="estatRegles"
			data-toggle="datatable"
			data-url="regla/datatable"
			data-paging-enabled="false"
			data-ordering="false"
			data-default-order="1"
			data-ajax-request-type="GET"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"/>
				<th data-col-name="nom" width="15%"><spring:message code="expedient.tipus.regla.form.camp.nom"/></th>
				<th data-col-name="qui" data-template="#cellQuiTemplate" width="10%">
					<spring:message code="expedient.tipus.regla.form.camp.qui"/>
					<script id="cellQuiTemplate" type="text/x-jsrender">
					{{if qui == 'TOTHOM'}}
						<spring:message code="enum.regla.qui.TOTHOM"/>
					{{else qui == 'USUARI'}}
						<spring:message code="enum.regla.qui.USUARI"/>
					{{else qui == 'ROL'}}
						<spring:message code="enum.regla.qui.ROL"/>
					{{else}}
						{{>qui}}
					{{/if}}
					</script>
				</th>
				<th data-col-name="quiValor" data-template="#cellQuiValorTemplate" data-orderable="false" width="30%">
					<spring:message code="expedient.tipus.regla.form.camp.qui.valors"/>
					<script id="cellQuiValorTemplate" type="text/x-jsrender">
						{{for quiValor}}
							<span class="label label-default">{{>#data}}</span>
						{{/for}}
					</script>
				</th>
				<th data-col-name="que" data-template="#cellQueTemplate" width="10%">
					<spring:message code="expedient.tipus.regla.form.camp.que"/>
					<script id="cellQueTemplate" type="text/x-jsrender">
					{{if que == 'TOT'}}
						<spring:message code="enum.regla.que.TOT"/>
					{{else que == 'DADES'}}
						<spring:message code="enum.regla.que.DADES"/>
					{{else que == 'DOCUMENTS'}}
						<spring:message code="enum.regla.que.DOCUMENTS"/>
					{{else que == 'AGRUPACIO'}}
						<spring:message code="enum.regla.que.AGRUPACIO"/>
					{{else que == 'DADA'}}
						<spring:message code="enum.regla.que.DADA"/>
					{{else que == 'DOCUMENT'}}
						<spring:message code="enum.regla.que.DOCUMENT"/>
					{{else que == 'TERMINI'}}
						<spring:message code="enum.regla.que.TERMINI"/>
					{{else}}
						{{>que}}
					{{/if}}
					</script>
				</th>
				<th data-col-name="queValor" data-template="#cellQueValorTemplate" data-orderable="false" width="30%">
					<spring:message code="expedient.tipus.regla.form.camp.que.valors"/>
					<script id="cellQueValorTemplate" type="text/x-jsrender">
						{{for queValor}}
							<span class="label label-default">{{>#data}}</span>
						{{/for}}
					</script>
				</th>
				<th data-col-name="accio" data-template="#cellAccioTemplate" width="20%">
					<spring:message code="expedient.tipus.regla.form.camp.accio"/>
					<script id="cellAccioTemplate" type="text/x-jsrender">
					{{if accio == 'MOSTRAR'}}
						<spring:message code="enum.regla.accio.MOSTRAR"/>
					{{else accio == 'OCULTAR'}}
						<spring:message code="enum.regla.accio.OCULTAR"/>
					{{else accio == 'EDITAR'}}
						<spring:message code="enum.regla.accio.EDITAR"/>
					{{else accio == 'BLOQUEJAR'}}
						<spring:message code="enum.regla.accio.BLOQUEJAR"/>
					{{else accio == 'REQUERIR'}}
						<spring:message code="enum.regla.accio.REQUERIR"/>
					{{else}}
						{{>accio}}
					{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="5%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a data-toggle="modal" href="regla/{{:id}}"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="regla/{{:id}}/delete" data-rdt-link-ajax="true" data-confirm="<spring:message code="expedient.tipus.permis.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<a href="<c:url value="/v3/expedientTipus/${expedientTipus.id}?pipellaActiva=estats"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span> <spring:message code="comu.boto.tornar"/></a>

	<script>
		var filaMovem;

		$(document).ready(function() {
			$('#estatRegles').on('draw.dt', function() {
				// Posa la taula com a ordenable
				$("#estatRegles").tableDnD({
					onDragClass: "drag",
					onDrop: function(table, row) {
						var pos = row.rowIndex - 1;
						var id= obtenirId(pos);
						if (pos != filaMovem) {
							canviarPosicioRegla(id,pos);
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
		});

		function canviarPosicioRegla(id, pos) {
			// Canvia la ordenació sempre amb ordre ascendent
			var getUrl = '<c:url value="/v3/expedientTipus/${expedientTipus.id}/estat/${estat.id}/regla/"/>'+id+'/moure/'+pos;
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

		function obtenirId(pos){
			if(filaMovem==pos){
				var fila = filaMovem + 1;
			} else {
				if( filaMovem < pos){	//baixam elements
					var fila = filaMovem + (pos-filaMovem)+1;
				}else{					//pujam elements
					var fila = filaMovem - (filaMovem-pos)+1;
				}
			}
			id = $("#estatRegles tr:eq("+fila+")").attr("id");
			id2 = id.split("_");
			return id2[1] ;
		}
	</script>
</body>
</html>
