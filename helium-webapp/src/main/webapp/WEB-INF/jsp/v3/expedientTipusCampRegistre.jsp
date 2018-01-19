<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>


<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="expedient.tipus.campRegistre.llistat.titol" arguments="${camp.etiqueta}"/></c:set>
<c:set var="baseUrl"><c:url value="/modal/v3/${basicUrl}/variable/${camp.id}/campRegistre"></c:url></c:set>

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
	
	<form:form id="campRegistre-form" cssClass="form-horizontal" action="#" enctype="multipart/form-data" method="post" commandName="expedientTipusCampRegistreCommand" 
		style='${mostraCreate || mostraUpdate ? "":"display:none;"}'>
		<div class="inlineLabels">        
			<input type="hidden" name="id" id="inputCampRegistreId" value="${expedientTipusCampRegistreCommand.id}"/>
			<input type="hidden" name="registreId" id="inputMembreId" value="${expedientTipusCampRegistreCommand.registreId}"/>
			<hel:inputSelect required="true" emptyOption="true" name="membreId" textKey="expedient.tipus.consulta.vars.form.variable" placeholderKey="expedient.tipus.consulta.vars.form.variable.placeholder" optionItems="${variables}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			<hel:inputCheckbox name="obligatori" textKey="expedient.tipus.campRegistre.form.camp.obligatori" />
			<hel:inputCheckbox name="llistar" textKey="expedient.tipus.campRegistre.form.camp.llistar" />
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
		<c:if test="${!heretat}">
			<button id="btnNew" class="btn btn-default" style='${mostraCreate || mostraUpdate ? "display:none;" : ""}'><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.campRegistre.llistat.accio.crear"/></button>
		</c:if>
	</div>	
	<div style="height: 500px;">
		<table	id="campRegistre"
				data-toggle="datatable"
				data-url="${baseUrl}/datatable"
				data-paging-enabled="false"
				data-ordering="true"
				data-default-order="7"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="membreId" data-visible="false"/>
					<th data-col-name="membreCodi"><spring:message code="expedient.tipus.campRegistre.llistat.columna.membreCodi"/></th>
					<th data-col-name="membreEtiqueta"><spring:message code="expedient.tipus.campRegistre.llistat.columna.membreEtiqueta"/></th>
					<th data-col-name="membreTipus"><spring:message code="expedient.tipus.campRegistre.llistat.columna.membreTipus"/></th>
					<th data-col-name="obligatori" data-template="#cellexpedientTipusCampRegistreMultibleTemplate">
					<spring:message code="expedient.tipus.campRegistre.llistat.columna.obligatori"/>
						<script id="cellexpedientTipusCampRegistreMultibleTemplate" type="text/x-jsrender">
						{{if obligatori }}
							<spring:message code="comu.check"></spring:message>
						{{/if}}
						</script>
					</th>
					<th data-col-name="llistar" data-template="#cellexpedientTipusCampRegistreLlistarTemplate">
					<spring:message code="expedient.tipus.campRegistre.llistat.columna.llistar"/>
						<script id="cellexpedientTipusCampRegistreLlistarTemplate" type="text/x-jsrender">
						{{if llistar }}
							<spring:message code="comu.check"></spring:message>
						{{/if}}
						</script>
					</th>
					<th data-col-name="ordre"><spring:message code="expedient.tipus.campRegistre.llistat.columna.ordre"/></th>
					<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
						<c:if test="${!heretat}">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li><a href="${baseUrl}/{{:id}}/update" class="campRegistreUpdate" data-campregistreid="{{:id}}" data-membreId="{{:membreId}}"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
									<li><a href="${baseUrl}/{{:id}}/delete" class="campRegistreDelete" data-confirm="<spring:message code="expedient.tipus.campRegistre.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
								</ul>
							</div>
						</c:if>
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
		$('#campRegistre').on('draw.dt', function() {
			/* <c:if test="${!heretat}"> */
			// Posa la taula com a ordenable
			$("#campRegistre").tableDnD({
		    	onDragClass: "drag",
		    	onDrop: function(table, row) {	        	
		        	var pos = row.rowIndex - 1;
		        	var id= obtenirId(pos);
		        	if (pos != filaMovem)
		        		canviarPosicioCampRegistre(id,pos);
		    	},
		    	onDragStart: function(table, row) {
		    			filaMovem = row.rowIndex-1;
				}
		    });
			/* </c:if> */
		    $("#campRegistre tr").hover(function() {
		        $(this.cells[0]).addClass('showDragHandle');
		    }, function() {
		        $(this.cells[0]).removeClass('showDragHandle');
		    });	
		    // Modifica l'enllaç update per carregar adaptar la vista a l'update
		    $(".campRegistreUpdate").click(function(){
				var campRegistreId = $(this).data('campregistreid');
				var membreId = $(this).data('membreid');
				mostraFormulariUpdate(campRegistreId, membreId);
				return false;
		    });
			// Botons eliminar
			$(".campRegistreDelete").click(function(e) {
				var getUrl = $(this).attr('href');
				$.ajax({
					type: 'GET',
					url: getUrl,
					async: true,
					success: function(result) {
						$('#campRegistre').webutilDatatable('refresh');
						webutilRefreshMissatges();
						$('#btnCancelar').click();
					},
					error: function(error) {
						webutilRefreshMissatges();
						console.log('Error:'+error);
					}
				});
				e.stopImmediatePropagation();
				return false;
			});
		  });			
	});
	
	function cancelaFormulari() {
		$('#campRegistre-form').hide(300);
		$('#btnNew').show();
		$('#campRegistre-form').attr('action','');
	}
	
	function mostraFormulariNew() {
		refrescarVariables('');
		$('#btnNew').hide();
		$('#btnCreate').show();
		$('#btnUpdate').hide();
		resetFormulari();
		$('#campRegistre-form').attr('action','${baseUrl}/new');
	}
	
	function mostraFormulariUpdate(id, membreId) {
		resetFormulari();
		$('#btnNew').hide();
		$('#btnCreate').hide();
		$('#btnUpdate').show();
		$('#campRegistre-form').attr('action','${baseUrl}/'+id+'/update');
		// Copia els valors
		$("#inputCampRegistreId").val(id);
		$row = $("#campRegistre tr[id='row_"+id+"']");		
		$data = $("#campRegistre").dataTable().api().row( $row ).data();
		$("#obligatori").attr('checked', $data.obligatori);		
		$("#llistar").attr('checked', $data.llistar);		
		refrescarVariables(membreId);
	}
	
	function canviarPosicioCampRegistre( id, pos) {
	  	// Canvia la ordenació sempre amb ordre ascendent
		$('#campRegistre').DataTable().order([7, 'asc']);
		var getUrl = '<c:url value="${baseUrl}"/>/'+id+'/moure/'+pos;
		$.ajax({
			type: 'GET',
			url: getUrl,
			async: true,
			success: function(result) {
				$('#campRegistre').webutilDatatable('refresh');
			},
			error: function(e) {
				console.log("Error canviant l'ordre: " + e);
				$('#campRegistre').webutilDatatable('refresh');
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
		id = $("#campRegistre tr:eq("+fila+")").attr("id");	
		id2 = id.split("_");
		return id2[1] ;
	}
	
	function resetFormulari() {
		$('#campRegistre-form').trigger('reset').show(300);
		$('#campRegistre-form .help-block').remove();
		$('#campRegistre-form .has-error').removeClass('has-error');
	}
	
	function refrescarVariables(membreId) {
		var getUrl = '<c:url value="${baseUrl}/select"/>';
		$.ajax({
			type: 'GET',
			url: getUrl,
			data: {'membreId' : membreId},
			async: true,
			success: function(data) {
				$("#membreId option").each(function(){
				    $(this).remove();
				});
				$("#membreId").append($("<option/>"));
				for (i = 0; i < data.length; i++) {
					$("#membreId").append($("<option/>", {value: data[i].codi, text: data[i].valor}));
				}
				$("#membreId").val(membreId).change();
			},
			error: function(e) {
				console.log("Error obtenint variables: " + e);
			}
		});
	}	
	// ]]>
	</script>	
</body>
		
