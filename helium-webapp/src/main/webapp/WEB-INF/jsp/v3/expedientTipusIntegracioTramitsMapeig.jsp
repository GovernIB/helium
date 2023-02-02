<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>


<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="expedient.tipus.integracio.tramits.mapeig.llistat.titol.${tipus}"/></c:set>
<c:set var="baseUrl"><c:url value="/modal/v3/expedientTipus/${expedientTipusId}/integracioTramits/mapeig/${tipus}"></c:url></c:set>

<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
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
	
	<form:form id="mapeig-form" cssClass="form-horizontal" action="#" enctype="multipart/form-data" method="post" commandName="expedientTipusIntegracioTramitsMapeigCommand" 
		style='${mostraCreate || mostraUpdate ? "":"display:none;"}'>
		<div class="inlineLabels">        
			<input type="hidden" name="expedientTipusId" id="inputExpedientTipusId" value="${expedientTipusIntegracioTramitsMapeigCommand.expedientTipusId}"/>
			<input type="hidden" name="tipus" id="inputTipus" value="${expedientTipusIntegracioTramitsMapeigCommand.tipus}"/>
			<input type="hidden" name="id" id="inputParametreId" value="${expedientTipusIntegracioTramitsMapeigCommand.id}"/>
			<c:if test="${tipus != 'Adjunt'}">
				<hel:inputSelect required="true" name="codiHelium" textKey="expedient.tipus.integracio.tramits.mapeig.form.camp.codiHelium" optionItems="${variables}" optionTextAttribute="codi" optionValueAttribute="valor" />
			</c:if>
			<hel:inputText required="true" name="codiSistra" textKey="expedient.tipus.integracio.tramits.mapeig.form.camp.codiSistra" />
			<c:if test="${tipus != 'Adjunt'}">
				<hel:inputCheckbox name="evitarSobreescriptura" textKey="expedient.tipus.integracio.tramits.mapeig.form.camp.evitarSobreescriptura" info="expedient.tipus.integracio.tramits.mapeig.form.camp.evitarSobreescriptura.comment"/>
			</c:if>
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
		<button id="btnNew" class="btn btn-default" style='${mostraCreate || mostraUpdate ? "display:none;" : ""}'><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.integracio.tramits.mapeig.llistat.accio.crear"/></button>
	</div>	
	<div style="height: 500px;">
		<table	id="mapeigSistra"
				data-toggle="datatable"
				data-url="${baseUrl}/datatable"
				data-paging-enabled="false"
				data-ordering="true"
				data-default-order="${ tipus != 'Adjunt' ? 1 : 2}"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="codiHelium" data-visible="${tipus != 'Adjunt'}"><spring:message code="expedient.tipus.integracio.tramits.mapeig.llistat.columna.codiHelium"/></th>
					<th data-col-name="codiSistra"><spring:message code="expedient.tipus.integracio.tramits.mapeig.llistat.columna.codiSistra"/></th>
					<c:if test="${tipus != 'Adjunt'}">
						<th data-col-name="evitarSobreescriptura" data-template="#cellevitarSobreescripturaTemplate">
							<spring:message code="expedient.tipus.integracio.tramits.mapeig.llistat.columna.evitarSobreescriptura"/>
								<script id="cellevitarSobreescripturaTemplate" type="text/x-jsrender">
								{{if evitarSobreescriptura }}
									<div style="text-align: center;">
										<spring:message code="comu.check"></spring:message>
									</div>
								{{/if}}
							</script>
						</th>
					</c:if>	
					<th data-col-name="id" data-template="#cellParametreTemplate" data-orderable="false" width="10%">
						<script id="cellParametreTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="${baseUrl}/{{:id}}/update" class="mapeigUpdate" data-mapeigid="{{:id}}"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
								<li><a href="${baseUrl}/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.integracio.tramits.mapeig.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
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
		
		$('#mapeigSistra').on('draw.dt', function() {
		    // Modifica l'enllaç update per carregar adaptar la vista a l'update
		    $(".mapeigUpdate").click(function(){
				var mapeigId = $(this).data('mapeigid');
				mostraFormulariUpdate(mapeigId);
				return false;
		    });
		  });			
	});
	
	function cancelaFormulari() {
		$('#mapeig-form').hide(300);
		$('#btnNew').show();
		$('#mapeig-form').attr('action','');
	}
	
	function mostraFormulariNew() {
		$('#btnNew').hide();
		$('#btnCreate').show();
		$('#btnUpdate').hide();
		resetFormulari();
		$('#mapeig-form').attr('action','${baseUrl}/new');
	}
	
	function mostraFormulariUpdate(id) {
		$('#btnNew').hide();
		$('#btnCreate').hide();
		$('#btnUpdate').show();
		resetFormulari();
		$('#mapeig-form').attr('action','${baseUrl}/'+id+'/update');
		// Copia els valors
		$("#inputParametreId").val(id);
		$("#codiHelium").val($("#mapeigSistra tr[id='row_"+id+"'] td:nth-child(1)").text()).change();
		$("#codiSistra").val($("#mapeigSistra tr[id='row_"+id+"'] td:nth-child(${ tipus != 'Adjunt' ? 2 : 1})").text());
		if ($("#mapeigSistra tr[id='row_"+id+"'] td:nth-child(3)").text().includes('✓')) {
			$('#evitarSobreescriptura').prop('checked', 'checked');
		} else {
			$('#evitarSobreescriptura').removeProp('checked');
		}		
	}
		
	function resetFormulari() {
		$('#mapeig-form').trigger('reset').show(300);
		$('#mapeig-form .help-block').remove();
		$('#mapeig-form .has-error').removeClass('has-error');
	}
	// ]]>
	</script>	
</body>
		
