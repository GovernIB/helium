<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>


<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="expedient.tipus.consulta.params.llistat.titol" arguments="${consulta.nom}"/></c:set>
<c:set var="baseUrl"><c:url value="/modal/v3/expedientTipus/${expedientTipusId}/consulta/${consulta.id}/parametre"></c:url></c:set>

<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
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
	
	<form:form id="parametre-form" cssClass="form-horizontal" action="#" enctype="multipart/form-data" method="post" commandName="expedientTipusConsultaParamCommand" 
		style='${mostraCreate || mostraUpdate ? "":"display:none;"}'>
		<div class="inlineLabels">        
			<input type="hidden" name="expedientTipusId" id="inputExpedientTipusId" value="${expedientTipusConsultaParamCommand.expedientTipusId}"/>
			<input type="hidden" name="consultaId" id="inputConsultaId" value="${expedientTipusConsultaParamCommand.consultaId}"/>
			<input type="hidden" name="tipus" id="inputTipus" value="${expedientTipusConsultaParamCommand.tipus}"/>
			<input type="hidden" name="id" id="inputParametreId" value="${expedientTipusConsultaParamCommand.id}"/>
			<hel:inputText required="true" name="campCodi" textKey="expedient.tipus.consulta.params.form.camp.codi" />
			<hel:inputText required="true" name="campDescripcio" textKey="expedient.tipus.consulta.params.form.camp.descripcio" />
			<hel:inputSelect required="true" name="paramTipus" textKey="expedient.tipus.consulta.params.form.camp.tipus" optionItems="${tipusParameteres}" />
		</div>

		<div id="modal-botons" class="well" style="text-align: right;">
			<button id="btnCancelar" name="submit" value="cancel" class="btn btn-default right"><spring:message code="comu.boto.cancelar"/></button>
			<button id="btnCreate" style='${mostraCreate ? "":"display:none;"}' class="btn btn-primary right" type="submit" name="accio" value="crear">
				<span class="fa fa-plus"></span> <spring:message code='comu.boto.crear' />
			</button>
			<button id="btnUpdate" style='${mostraUpdate ? "":"display:none;"}' class="btn btn-primary right" type="submit" name="accio" value="modificar">
				<span class="fa fa-pencil"></span> <spring:message code='comu.boto.modificar' />
			</button>	
		</div>

	</form:form>
	
	<div class="botons-titol text-right">
		<button id="btnNew" class="btn btn-default" style='${mostraCreate || mostraUpdate ? "display:none;" : ""}'><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.consulta.params.llistat.accio.crear"/></button>
	</div>	
	<div style="height: 500px;">
		<table	id="consultaParametre"
				data-toggle="datatable"
				data-url="${baseUrl}/datatable"
				data-paging-enabled="false"
				data-ordering="true"
				data-default-order="1"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="campCodi"><spring:message code="expedient.tipus.consulta.params.llistat.columna.codi"/></th>
					<th data-col-name="campDescripcio"><spring:message code="expedient.tipus.consulta.params.llistat.columna.descripcio"/></th>
					<th data-col-name="paramTipus"><spring:message code="expedient.tipus.consulta.params.llistat.columna.tipus"/></th>
					<th data-col-name="id" data-template="#cellParametreTemplate" data-orderable="false" width="10%">
						<script id="cellParametreTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="${baseUrl}/{{:id}}/update" class="parametreUpdate" data-parametreid="{{:id}}"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
								<li><a href="${baseUrl}/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.consulta.params.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
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
		
		$('#consultaParametre').on('draw.dt', function() {
		    // Modifica l'enllaç update per carregar adaptar la vista a l'update
		    $(".parametreUpdate").click(function(){
				var parametreId = $(this).data('parametreid');
				mostraFormulariUpdate(parametreId);
				return false;
		    });
		  });			
	});
	
	function cancelaFormulari() {
		$('#parametre-form').hide(300);
		$('#btnNew').show();
		$('#parametre-form').attr('action','');
	}
	
	function mostraFormulariNew() {
		$('#btnNew').hide();
		$('#btnCreate').show();
		$('#btnUpdate').hide();
		resetFormulari();
		$('#parametre-form').attr('action','${baseUrl}/new');
	}
	
	function mostraFormulariUpdate(id) {
		$('#btnNew').hide();
		$('#btnCreate').hide();
		$('#btnUpdate').show();
		resetFormulari();
		$('#parametre-form').attr('action','${baseUrl}/'+id+'/update');
		// Copia els valors
		$("#inputParametreId").val(id);
		$("#campCodi").val($("#consultaParametre tr[id='row_"+id+"'] td:nth-child(1)").text());
		$("#campDescripcio").val($("#consultaParametre tr[id='row_"+id+"'] td:nth-child(2)").text());
		$("#paramTipus").val($("#consultaParametre tr[id='row_"+id+"'] td:nth-child(3)").text()).change();
	}
		
	function resetFormulari() {
		$('#parametre-form').trigger('reset').show(300);
		$('#parametre-form .help-block').remove();
		$('#parametre-form .has-error').removeClass('has-error');
	}
	// ]]>
	</script>	
</body>
		
