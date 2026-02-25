<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>


<html>
<head>	
	<c:choose>
		<c:when test="${reglesTipusExpedient == true}">
			<title><spring:message code="expedient.tipus.estat.regla.titol"/></title>
			<meta name="title" content="<spring:message code="expedient.tipus.estat.regla.titol"/>"/>
			<meta name="title-icon-class" content="fa fa-key"/>
		</c:when>
		<c:otherwise>
			<title><spring:message code="expedient.tipus.regla.titol"/></title>
			<meta name="title" content="<spring:message code="expedient.tipus.regla.titol"/>"/>
			<meta name="screen" content="regles estats"/>
			<meta name="subtitle" content="${expedientTipus.nom}:${estat.nom}"/>
			<meta name="title-icon-class" content="fa fa-key"/>
		</c:otherwise>
	</c:choose>
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>
	<style>
		.label-accio {font-size: 13px; margin-top: 5px; margin-bottom: 5px; display: block;}
		.label-ocult {background-color: rgba(0,0,0,0); border: dashed 1px #ccc; color: #888;}
		
	    #estatRegles tr:not(.nodrag) { cursor: move; }
	    #estatRegles tr.nodrag { cursor: default; }
	    /* Feedback visual quan arrosseguem */
	    .drag { background-color: #f4f4f4 !important; opacity: 0.8; }
	</style>
	<hel:modalHead/>
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="regla/new" data-toggle="modal" data-maximized="true"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.regla.accio.nou"/></a>
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
				<th data-col-name="ordre" data-visible="false"/>
				<th data-col-name="id" data-visible="false"/>
				<th data-col-name="nom" width="15%" data-template="#cellEstatReglaNomTemplate">
				<spring:message code="expedient.tipus.regla.form.camp.nom"/>
					<script id="cellEstatReglaNomTemplate" type="text/x-jsrender">
								{{if heretat }}
									<span class="dada-heretada">{{:nom}}</span> 
									<span class="label label-primary herencia" title="<spring:message code="expedient.tipus.estat.llistat.codi.heretat"/>">R</span>
								{{else}}
									{{:nom}}
								{{/if}}
						</script>
				</th>
				<th data-col-name="qui" data-template="#cellQuiTemplate" width="10%">
					<spring:message code="expedient.tipus.regla.form.camp.qui"/>
					<script id="cellQuiTemplate" type="text/x-jsrender">
					{{if qui == 'TOTHOM'}}
						<span class="fa fa-globe"></span> <spring:message code="enum.regla.qui.TOTHOM"/>
					{{else qui == 'USUARI'}}
						<span class="fa fa-user"></span> <spring:message code="enum.regla.qui.USUARI"/>
					{{else qui == 'ROL'}}
						<span class="fa fa-users"></span> <spring:message code="enum.regla.qui.ROL"/>
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
<%--						<span class="fa fa-globe"></span> --%>
						<spring:message code="enum.regla.que.TOT"/>
					{{else que == 'DADES'}}
						<span class="fa fa-tags"></span>
						<spring:message code="enum.regla.que.DADES"/>
					{{else que == 'DOCUMENTS'}}
<%--						<span class="fa fa-clone"></span> --%>
						<spring:message code="enum.regla.que.DOCUMENTS"/>
					{{else que == 'TERMINIS'}}
<%--						<span class="fa fa-cogs"></span> --%>
						<spring:message code="enum.regla.que.TERMINIS"/>
					{{else que == 'AGRUPACIO'}}
<%--						<span class="fa fa-th"></span> --%>
						<spring:message code="enum.regla.que.AGRUPACIO"/>
					{{else que == 'DADA'}}
<%--						<span class="fa fa-tag"></span> --%>
						<spring:message code="enum.regla.que.DADA"/>
					{{else que == 'DOCUMENT'}}
<%--						<span class="fa fa-file-o"></span> --%>
						<spring:message code="enum.regla.que.DOCUMENT"/>
					{{else que == 'TERMINI'}}
<%--						<span class="fa fa-cog"></span> --%>
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
				<th data-col-name="accio" data-template="#cellAccioTemplate" style="min-width: 190px;">
					<spring:message code="expedient.tipus.regla.form.camp.accio"/>
					<script id="cellAccioTemplate" type="text/x-jsrender">
					{{if accio == 'MOSTRAR'}}
						<span class="label label-info label-accio"><span class="fa fa-eye"></span> <spring:message code="enum.regla.accio.MOSTRAR"/></span>
					{{else accio == 'OCULTAR'}}
						<span class="label label-ocult label-accio"><span class="fa fa-eye-slash"></span> <spring:message code="enum.regla.accio.OCULTAR"/></span>
					{{else accio == 'EDITAR'}}
						<span class="label label-success label-accio"><span class="fa fa-pencil-square-o"></span> <spring:message code="enum.regla.accio.EDITAR"/></span>
					{{else accio == 'BLOQUEJAR'}}
						<span class="label label-warning label-accio"><span class="fa fa-lock"></span> <spring:message code="enum.regla.accio.BLOQUEJAR"/></span>
					{{else accio == 'REQUERIR'}}
						<span class="label label-primary label-accio"><span class="fa fa-asterisk"></span> <spring:message code="enum.regla.accio.REQUERIR"/></span>
					{{else accio == 'REQUERIR_ENTRAR'}}
						<span class="label label-primary label-accio"><span class="fa fa-asterisk"></span> <spring:message code="enum.regla.accio.REQUERIR_ENTRAR"/></span>
					{{else accio == 'SIGNAT'}}
						<span class="label label-default label-accio"><span class="fa fa-asterisk"></span> <spring:message code="enum.regla.accio.SIGNAT"/></span>
					{{else accio == 'NOTIFICAT'}}
						<span class="label label-warning label-accio"><span class="fa fa-asterisk"></span> <spring:message code="enum.regla.accio.NOTIFICAT"/></span>
					{{else}}
						<span class="label label-default label-accio">{{>accio}}</span>
					{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="5%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								{{if heretat or ${heretat != null ? heretat : false} }}
									<li><a data-toggle="modal" href="regla/{{:id}}"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.visualitzar"/></a></li>
								{{else}}
									<li><a data-toggle="modal" data-maximized="true" href="regla/{{:id}}"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
									<li><a href="regla/{{:id}}/delete" data-rdt-link-ajax="true" data-confirm="<spring:message code="expedient.tipus.regla.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
								{{/if}}
							</ul>
						</div>
					</script>
				</th>
				<th data-col-name="heretat" data-orderable="false" width="5%" data-visible="false"></th>
			</tr>
		</thead>
	</table>
	<a href="<c:url value="/v3/expedientTipus/${expedientTipus.id}?pipellaActiva=estats"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span> <spring:message code="comu.boto.tornar"/></a>
	<script>
		var filaMovem;

		$(document).ready(function() {
			$('#estatRegles').on('draw.dt', function() {
				
				var table = $('#estatRegles').DataTable();
		        var rows = table.rows().nodes();
		        var data = table.rows().data();
		        
		     // Recorrem les dades per marcar les files heretades
		        data.each(function (rowData, index) {
		            if (rowData.heretat) {
		                $(rows[index]).addClass('nodrag fila-bloquejada');
		            }
		        });
		        
		     // Posa la taula com a ordenable
		        $("#estatRegles").tableDnD({
		            onDragClass: "drag",
		            // No permet moure les files amb classe 'nodrag'
		            onAllowDrop: function(draggedRow, dropTargetRow) {
		                // No permetem deixar anar res a sobre d'una fila heretada
		                return !$(dropTargetRow).hasClass('nodrag');
		            },
		            onDrop: function(table, row) {
		                var pos = row.rowIndex - 1;
		                var id = obtenirId(pos);
		                if (pos != filaMovem) {
		                    canviarPosicioRegla(id, pos);
		                }
		            },
		            onDragStart: function(table, row) {
		                // Si per algun motiu s'intenta arrossegar una heretada, es cancela
		                if($(row).hasClass('nodrag')) {
		                    return false;
		                }
		                filaMovem = row.rowIndex - 1;
		            }
				});
		     });
		});

		function canviarPosicioRegla(id, pos) {
			// Calculem quantes files heretades hi ha a dalt de tot
		    var numHeretades = $('#estatRegles tr.nodrag').length;
		    
		    // La posició real és la posició actual menys les heretades
		    var posicioRelativa = pos - numHeretades;

		    // Si per algun error la posició relativa és negativa, la posem a 0
		    if (posicioRelativa < 0) posicioRelativa = 0;
		    
			var getUrl = '<c:url value="/v3/expedientTipus/${expedientTipus.id}/estat/${estat.id}/regla/"/>' + id + '/moure/' + posicioRelativa;
			
			$.ajax({
				type: 'GET',
				url: getUrl,
				async: true,
				complete: function() {
					webutilRefreshMissatges();
					$('#estatRegles').webutilDatatable('refresh');
				}
			});
		}

		function obtenirId(pos) {
		    // Cercam la fila a la posició indicada (ajustant per l'index 1 del selector eq de jQuery)
		    var fila = $("#estatRegles tr").eq(pos + 1);
		    
		    // Intentam treure l'ID de l'atribut ID
		    var idAttr = fila.attr("id") || "";
		    var parts = idAttr.split("_");
		    
		    // Si el split no funciona, intenta cercar a la primera columna oculta (ordre)
		    return parts.length > 1 ? parts[1] : parts[0];
		}
	</script>
</body>
</html>
