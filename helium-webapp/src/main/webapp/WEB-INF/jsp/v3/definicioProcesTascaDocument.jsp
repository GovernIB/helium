<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>


<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="definicio.proces.tasca.document.titol" arguments="${tasca.nom}"/></c:set>
<c:set var="baseUrl"><c:url value="/modal/v3/definicioProces/${jbpmKey}/${definicioProcesId}/tasca/${tasca.id}/document"></c:url></c:set>

<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>

	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>

	<hel:modalHead/>
</head>
<body>			
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
	</div>
	
	<form:form id="tasca-document-form" cssClass="well" action="${baseUrl}/new" enctype="multipart/form-data" method="post" commandName="definicioProcesTascaDocumentCommand">
		<input type="hidden" name="tascaId" id="inputTascaId" value="${definicioProcesTascaDocumentCommand.tascaId}"/>
		<div class="row">
			<div class="col-sm-6">
				<hel:inputSelect inline="true" required="true" emptyOption="true" name="documentId" textKey="definicio.proces.tasca.document.form.document" placeholderKey="definicio.proces.tasca.document.form.document.placeholder" optionItems="${documents}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			</div>
			<div class="col-sm-4">
				<div class="row">
					<div class="col-sm-6">
						<hel:inputCheckbox inline="true" name="required" textKey="definicio.proces.tasca.document.columna.required" />
					</div>
					<div class="col-sm-6">
						<hel:inputCheckbox inline="true" name="readOnly" textKey="definicio.proces.tasca.document.columna.readOnly" />				
					</div>
				</div>
			</div>
			<div class="col-sm-2 right" id="modal-botons">
				<button id="btnCreate" class="btn btn-primary right" type="submit" name="accio" value="crear">
					<span class="fa fa-plus"></span> <spring:message code='definicio.proces.tasca.document.accio.afegir' />
				</button>
			</div>
		</div>
	</form:form>
		
	<div style="height: 500px;">
		<table	id="tascaDocument"
				data-toggle="datatable"
				data-url="${baseUrl}/datatable"
				data-paging-enabled="false"
				data-ordering="true"
				data-info-type="search"
				data-default-order="4"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="document.codi" data-template="#celldefinicioProcesTascaDocument" width="30%">
						<spring:message code="definicio.proces.tasca.document.columna.document"/>
						<script id="celldefinicioProcesTascaDocument" type="text/x-jsrender">
							{{:document.codi}} / {{:document.nom}}
							{{if document.heretat }}
								<span class="label label-primary" title="<spring:message code="expedient.tipus.camp.llistat.codi.heretat"/>">R</span>
							{{/if}}
							{{if document.sobreescriu }}
								<span class="label label-warning" title="<spring:message code="expedient.tipus.camp.llistat.codi.sobreescriu"/>">S</span>
							{{/if}}
							{{if document.expedientTipus != null}}
								<span class="label label-info pull-right" title="Tipus Expedient">TE</span>
							{{else}}
								<span class="label label-warning pull-right" title="Definició de Procés">DP</span>
							{{/if}}
							
						</script>
					</th>
					
					
					<th data-col-name="required" data-template="#celldefinicioProcesTascaDocumentRequiredTemplate">
					<spring:message code="definicio.proces.tasca.document.columna.required"/>
						<script id="celldefinicioProcesTascaDocumentRequiredTemplate" type="text/x-jsrender">
						<div style="text-align: center;">
						<input type="checkbox" id="required_{{:id}}" {{:required ? 'checked' : ''}} data-documentid="{{:id}}" data-propietat="required"/>
						</div>
						</script>
					</th>
					<th data-col-name="readOnly" data-template="#celldefinicioProcesTascaDocumentReadOnlyTemplate">
					<spring:message code="definicio.proces.tasca.document.columna.readOnly"/>
						<script id="celldefinicioProcesTascaDocumentReadOnlyTemplate" type="text/x-jsrender">
						<div style="text-align: center;">
						<input type="checkbox"  id="readOnly_{{:id}}"{{:readOnly ? 'checked' : ''}} data-documentid="{{:id}}" data-propietat="readOnly"/>
						</div>
						</script>
					</th>
					<th data-col-name="order" data-visible="false"><spring:message code="definicio.proces.tasca.document.columna.ordre"/></th>
					<th data-col-name="id" width="100px" data-template="#cellTascaDocumentDeleteTemplate" data-orderable="false" width="10%">
						<script id="cellTascaDocumentDeleteTemplate" type="text/x-jsrender">
							<a href="${baseUrl}/{{:id}}/delete" class="btn btn-default ajax-delete" data-confirm="<spring:message code="definicio.proces.tasca.document.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a>
						</script>
					</th>
				</tr>
			</thead>
		</table>
		<span id="accioUpdateProcessant" style="display: none;">
			<span class="fa fa-spinner fa-spin fa-fw" title="<spring:message code="comu.processant"/>"></span><span class="sr-only">&hellip;</span>
		</span>
	</div>
	
	<script type="text/javascript">
	// <![CDATA[

	// Llistat d'identificadors de camps heretats
	var documentsHeretatsIds =  ${documentsHeretatsIds};
	//Llistat d'identificadors de camps que sobreescriuen
	var documentsSobreescriuenIds =  ${documentsSobreescriuenIds};

	// Funció per donar format als itemps de la select d'agrupacions depenent de la herència
	function formatDocumentSelectHerencia(item) {
		var res;
	    if(item.id && documentsHeretatsIds.indexOf(parseInt(item.id)) >= 0)
			res = item.text + " <span class='label label-primary'>R</span>";
		else if(item.id && documentsSobreescriuenIds.indexOf(parseInt(item.id)) >= 0)
			res = item.text + " <span class='label label-warning'>S</span>";
		else 
			res = item.text;
	    return res;
	  }
	            
	$(document).ready(function() {
		
		// Afegeix format si l'item de la agrupació està heretat
		$('#documentId').select2({
	        formatResult: formatDocumentSelectHerencia,
	        formatSelection: formatDocumentSelectHerencia
	    });
		
		// Quan es repinta la taula aplica la reordenació
		$('#tascaDocument').on('draw.dt', function() {
			// Posa la taula com a ordenable
			$("#tascaDocument").tableDnD({
		    	onDragClass: "drag",
		    	onDrop: function(table, row) {	        	
		        	var pos = row.rowIndex - 1;
		        	var id= obtenirId(pos);
		        	if (pos != filaMovem)
			        	canviarPosicioTascaDocument(id,pos);
		    	},
		    	onDragStart: function(table, row) {
		    			filaMovem = row.rowIndex-1;
				}
		    });
		    $("#tascaDocument tr").hover(function() {
		        $(this.cells[0]).addClass('showDragHandle');
		    }, function() {
		        $(this.cells[0]).removeClass('showDragHandle');
		    });	
		    // Si es modifica un checbox mostra el botó d'actualitzar
		    $("input[type=checkbox]", this).change(function() {
		    	updateCheckbox(this);
		    });
		    // botons d'esborrar
			$(".ajax-delete", this).click(function(e) {
				var getUrl = $(this).attr('href');
				$.ajax({
					type: 'GET',
					url: getUrl,
					async: true,
					success: function(result) {
						if (result) {
							refrescaDocuments();
							refrescaTaula();
						}
					},
					error: function(error) {
						console.log('Error:'+error);
					},
					complete: function() {
						webutilRefreshMissatges();
					}
				});
				e.stopImmediatePropagation();
				return false;				
			});
		  });			
	});
	
	/* Actualitza un valor del document de la tasca. */
	function updateCheckbox(checkbox) {
		var documentId = $(checkbox).data('documentid');
		var propietat = $(checkbox).data('propietat');
		var getUrl = '<c:url value="/v3/definicioProces/${jbpmKey}/${definicioProcesId}/tasca/${tasca.id}/document/"/>'+documentId+'/'+propietat;
		var spin = $("#accioUpdateProcessant")
			.clone()
			.show()
			.insertAfter(checkbox);
		$(checkbox).hide();
		$.ajax({
			type: 'POST',
			url: getUrl,
			data: {
				valor : $(checkbox).is(':checked')
			},
			async: true,
			success: function(result) {
			},
			error: function(error) {
				console.log('Error:'+error);
			},
			complete: function() {
				webutilRefreshMissatges();
				$(spin).remove();
				$(checkbox).show();
				
			}
		});
	}

	
	function canviarPosicioTascaDocument( id, pos) {
	  	// Canvia la ordenació sempre amb ordre ascendent
		$('#tascaDocument').DataTable().order([4, 'asc']);
		var getUrl = '<c:url value="/v3/definicioProces/${jbpmKey}/${definicioProcesId}/tasca/${tasca.id}/document/"/>'+id+'/moure/'+pos;
		$.ajax({
			type: 'GET',
			url: getUrl,
			async: true,
			success: function(result) {
				$('#tascaDocument').webutilDatatable('refresh');
			},
			error: function(e) {
				console.log("Error canviant l'ordre: " + e);
				$('#tascaDocument').webutilDatatable('refresh');
			}
		});	
	}

	function obtenirId(pos){
		if(filaMovem==pos){
			var fila = filaMovem + 1;
		}
		else{
			if( filaMovem < pos){	//baixam elements
				var fila = filaMovem + (pos-filaMovem)+1;
			}else{					//pujam elements
				var fila = filaMovem - (filaMovem-pos)+1;
			}
		}
		id = $("#tascaDocument tr:eq("+fila+")").attr("id");	
		id2 = id.split("_");
		return id2[1] ;
	}
		
	function refrescaDocuments() {
		var getUrl = '<c:url value="/v3/definicioProces/${jbpmKey}/${definicioProcesId}/tasca/${tasca.id}/document/select"/>';
		$.ajax({
			type: 'GET',
			url: getUrl,
			async: true,
			success: function(data) {
				$("#documentId option").each(function(){
				    $(this).remove();
				});
				$("#documentId").append($("<option/>"));
				for (i = 0; i < data.length; i++) {
					$("#documentId").append($("<option/>", {value: data[i].codi, text: data[i].valor}));
				}
				$("#documentId").val('').change();
			},
			error: function(e) {
				console.log("Error obtenint documents: " + e);
			}
		});
	}	
	function refrescaTaula() {
		$('#tascaDocument').webutilDatatable('refresh');
	}	
	// ]]>
	</script>	
</body>
		
