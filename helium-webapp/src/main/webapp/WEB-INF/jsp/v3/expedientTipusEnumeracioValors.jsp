<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>


<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="expedient.tipus.enumeracio.form.titol.llistat"/>${enumeracio.nom}</c:set>
<c:set var="baseUrl"><c:url value="/modal/v3/expedientTipus/${expedientTipusId}/enumeracio/${enumeracio.id}/valor"></c:url></c:set>

<html>
<head>
	<title>${titol}</title>
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
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
	</div>
	
	<form:form id="validacio-form" cssClass="form-horizontal" action="#" enctype="multipart/form-data" method="post" commandName="expedientTipusEnumeracioValorCommand" style='${mostraCreate || mostraUpdate ? "":"display:none;"}'>
		<div class="inlineLabels">        
			<input type="hidden" name="id" id="inputValidacioId" value="${expedientTipusEnumeracioValorCommand.id}"/>
			<hel:inputText required="true" name="codi" textKey="expedient.tipus.enumeracio.valors.form.camp.codi" />
			<hel:inputText required="true" name="nom"  textKey="expedient.tipus.enumeracio.valors.form.camp.nom" />
		</div>

		<div id="modal-botons" class="well">
			<button id="btnCancelar" name="submit" value="cancel" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></button>
			<button id="btnCreate" style='${mostraCreate ? "":"display:none;"}' class="btn btn-primary right" type="submit" name="accio" value="crear">
				<span class="fa fa-plus"></span> <spring:message code='comu.boto.crear' />
			</button>
			<button id="btnUpdate" style='${mostraUpdate ? "":"display:none;"}' class="btn btn-primary right" type="submit" name="accio" value="modificar">
				<span class="fa fa-pencil"></span> <spring:message code='comu.boto.modificar' />
			</button>	
		</div>

	</form:form>
	
	<div class="botons-titol text-right">
		<button id="btnNew" class="btn btn-default" style='${mostraCreate || mostraUpdate ? "display:none;" : ""}'><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.enumeracio.valors.form.titol.nou"/></button>
	</div>	
	<div style="height: 500px;">
		<table	id="campValidacio"
				data-toggle="datatable"
				data-url="${baseUrl}/datatable"
				data-paging-enabled="false"
				data-ordering="true"
				data-default-order="3"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="codi"><spring:message code="expedient.tipus.enumeracio.valors.llistat.columna.codi"/></th>
					<th data-col-name="nom"><spring:message code="expedient.tipus.enumeracio.valors.llistat.columna.titol"/></th>
					<th data-col-name="ordre"><spring:message code="expedient.tipus.enumeracio.valors.llistat.columna.ordre"/></th>
					<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="${baseUrl}/{{:id}}/update" class="validacioUpdate" data-validacioid="{{:id}}"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
								<li><a href="${baseUrl}/{{:id}}/delete" data-confirm="<spring:message code="expedient.tipus.enumeracio.valors.llistat.confirm.esborra"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
							</ul>
						</div>
					</script>
					</th>
				</tr>
			</thead>
		</table>
	</div>
	
	<script type="text/javascript">
	// <![CDATA[
	            
	$(document).ready(function() {
		// Accions del formulari
		$('#btnCancelar').click(function(e){
			e.preventDefault();
			cancelaFormulari();
		})
		$('#btnNew').click(function(){
			mostraFormulariNew();
		})
		
		// Quan es repinta la taula aplica la reordenació
		$('#campValidacio').on('draw.dt', function() {
			// Posa la taula com a ordenable
			$("#campValidacio").tableDnD({
		    	onDragClass: "drag",
		    	onDrop: function(table, row) {	        	
		    		debugger;
		    		var pos = row.rowIndex - 1;
		    		var rutaOrdenacio = $("a", row)[0].href;
		    		var rDesde = rutaOrdenacio.indexOf("valor/")+6;
		    		var rHasta = rutaOrdenacio.indexOf("/update");
		        	var id= rutaOrdenacio.substring(rDesde, rHasta);
		        	canviarPosicioValidacio(id,pos);
		    	},
		    	onDragStart: function(table, row) {
		    			filaMovem = row.rowIndex-1;
				}
		    });
		    $("#campValidacio tr").hover(function() {
		        $(this.cells[0]).addClass('showDragHandle');
		    }, function() {
		        $(this.cells[0]).removeClass('showDragHandle');
		    });
		  });			
	});
	
	function cancelaFormulari() {
		$('#validacio-form').hide(300);
		$('#btnNew').show();
		$('#validacio-form').attr('action','');
	}
	
	function mostraFormulariNew() {
		$('#btnNew').hide();
		$('#btnCreate').show();
		$('#btnUpdate').hide();
		resetFormulari();
		$("#inputValidacioId").val("");
		$("#codi").val("");
		$("#nom").val("");
		$('#validacio-form').attr('action','${baseUrl}/new');
	}
	
	function mostraFormulariUpdate(id) {
		$('#btnNew').hide();
		$('#btnCreate').hide();
		$('#btnUpdate').show();
		resetFormulari();
		$('#validacio-form').attr('action','${baseUrl}/'+id+'/update');
		// Copia els valors
		$("#inputValidacioId").val(id);
		$("#expressio").val($("#campValidacio tr[id='row_"+id+"'] td:nth-child(1)").text());
		$("#missatge").val($("#campValidacio tr[id='row_"+id+"'] td:nth-child(2)").text());
	}
	
	function canviarPosicioValidacio( id, pos) {
	  	// Canvia la ordenació sempre amb ordre ascendent
	  	debugger;
		$('#campValidacio').DataTable().order([3, 'asc']);
		var getUrl = '<c:url value="/v3/expedientTipus/${expedientTipusId}/enumeracio/${enumeracio.id}/valor/"/>'+id+'/moure/'+pos;
		$.ajax({
			type: 'GET',
			url: getUrl,
			async: true,
			success: function(result) {
				$('#campValidacio').webutilDatatable('refresh');
			},
			error: function(e) {
				console.log("Error canviant l'ordre: " + e);
				$('#campValidacio').webutilDatatable('refresh');
			}
		});	
	}
	
	function resetFormulari() {
		$('#validacio-form').trigger('reset').show(300);
		$('#validacio-form .help-block').remove();
		$('#validacio-form .has-error').removeClass('has-error');
	}
	// ]]>
	</script>	
</body>
		
