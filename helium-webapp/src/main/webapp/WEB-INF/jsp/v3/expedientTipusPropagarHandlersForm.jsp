<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:set var="titol"><spring:message code="expedient.tipus.propagar.handlers.form.titol" arguments="${expedientTipus.codi},${expedientTipus.nom}"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
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
	<style type="text/css">
		.panel {
			margin-bottom: 0px;
		}
		.panel-heading {
			padding : 5px 5px;
		}
	</style>
</head>
<body>		
	<form:form id="propagar-form" cssClass="form-horizontal" action="propagarHandlers" enctype="multipart/form-data" method="post" commandName="command" style="min-height: 500px;">
	
		<div class="inlineLabels">
			<script type="text/javascript">
				// <![CDATA[
				            
				// Opcions comunes per totes les taules del document
				$.extend( true, $.fn.dataTable.defaults, {
				    columnDefs: [ {
				    	targets: 0,
				    	orderable: false
				    } ],
					paging : false,
				    searching: false,
				    oLanguage: {
					      sInfo: ""
				    }
				});				

				$(document).ready( function() {					
					$('table').DataTable();
					// Event per seleccionar o des seleccionar totes les entrades
					$('.checkAll').change(function(){
						$(this).closest('.dataTables_wrapper').find('.check').prop('checked', $(this).is(':checked')).change();
						updateMarcador($(this).closest('.agrupacio'));
					});
					$('.tots').click(function(e) {
						$(this).closest('.agrupacio').find('.checkAll').prop('checked', false).change();
						e.stopPropagation();
					});
					$('.algun, .cap').click(function(e) {
						$(this).closest('.agrupacio').find('.checkAll').prop('checked', true).change();
						e.stopPropagation();
					});
					// marca el checkbox si es clica sobre la línia de la taula
					$('.row_checkbox').click(function(e) {
						if (e.target.type != 'checkbox'){
							var $checkbox =$('.check', this);
							$checkbox.prop('checked', !$checkbox.is(':checked')).change();
						  }						
					});
					// si canvia un check comprova què fer amb el checkAll
					$('.check').change(function() {
						updateMarcador($(this).closest('.agrupacio'))
					});					
					// Per mostrar o ocultar el contingut de les taules
					$('.agrupacio').on('show.bs.collapse', function () {
						$(this).find('.fa-chevron-down').hide();
						$(this).find('.fa-chevron-up').show();
					});
					$('.agrupacio').on('hidden.bs.collapse', function () {
						$(this).find('.fa-chevron-up').hide();
						$(this).find('.fa-chevron-down').show();
					});
					// actualitza els comptadors
					$('.agrupacio').each(function(){
						if ($(this).find('.taula').length > 0)
							updateMarcador($(this));
					});		
					// Expandeix els panells que continguin errors
					$('.has-error').closest('.agrupacio').find('.clicable').click();

					// Neteja els errors en fer submit
					$('#propagar-form').submit(function() {
						$('.has-error').removeClass('has-error');
						$('p.help-block').remove();
					});
				}); 				
				
				// Actualitza la icona del marcador de selecció del costat del títol de la agrupació
				function updateMarcador( $agrupacio ) {
					// amaga els marcadors
					$agrupacio.find('.marcador').hide();
					var total = $agrupacio.find('.check').length;
					var marcats = $agrupacio.find('.check:checked').length;
					
					if (total == marcats && marcats > 0)
						$agrupacio.find('.tots').show();
					else if(marcats == 0)
						$agrupacio.find('.cap').show();
					else
						$agrupacio.find('.algun').show();
					$agrupacio.find('.marcats').text(marcats);
					$agrupacio.find('.total').text(total);
					// botó de checkall					
					$agrupacio.find('.checkAll').prop('checked', total == marcats);
				}
				// ]]>
			</script>			
		</div>
	
		<div class="alert alert-info">
			<span class="fa fa-info-circle"></span>
			<spring:message code="expedient.tipus.propagar.handlers.form.info" />
		</div>
		
		<c:forEach var="definicio" items="${definicions}">
			
			<div id="${definicio.jbpmKey}" class="agrupacio">
				<div class="panel-heading clicable" data-toggle="collapse" data-target="#panel_${definicio.jbpmKey}">
					<span class="marcador tots fa fa-check-square-o"></span>
					<span class="marcador algun fa fa-check-square-o text-muted" style="display: none;"></span>
					<span class="marcador cap fa fa-square-o" style="display: none;"></span>
					${definicio.etiqueta}
					<c:if test="${expedientTipus.jbpmProcessDefinitionKey == definicio.jbpmKey }">
						(*<spring:message code="expedient.tipus.definicioProces.llistat.columna.inicial"></spring:message>) 
					</c:if>
					[<span class="marcats">0</span>/<span class="total">0</span>]
					<div class="pull-right">
						<span class="fa fa-chevron-down" style="display: none;"></span>
						<span class="fa fa-chevron-up" style="display: inline-block;"></span>
					</div>
				</div>
				<div id="panel_${definicio.jbpmKey}" class="taula collapse in">
				<c:choose>
				    <c:when test="${fn:length(definicio.listIdAmbEtiqueta) > 0}">
						<table id="${definicio.jbpmKey}-taula"
								class="table table-striped table-bordered table-hover">
							<thead>
								<tr>
									<th><input type="checkbox" class="checkAll" checked="checked" data-sort="false" readonly="readonly"></th>
									<th><spring:message code="definicio.proces.detall.camp.versio"/></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="idAmbEtiqueta" items="${definicio.listIdAmbEtiqueta}" >
									<tr class="row_checkbox">
										<td>
											<input type="checkbox" class="check" id="${definicio.jbpmKey}_${idAmbEtiqueta.id}" name="definicionsSeleccionades" value="${idAmbEtiqueta.id}" <c:if test="${inici or fn:contains(definicionsSeleccionades, idAmbEtiqueta.id)}">checked="checked"</c:if> />
										</td>
										<td>${idAmbEtiqueta.etiqueta}</td>
									</tr>
								</c:forEach>			
							</tbody>
						</table>
				    </c:when>    
				    <c:otherwise>
				    	<div class="alert alert-warning">
							<span class="fa fa-info-circle"></span>
							<spring:message code="expedient.tipus.propagar.handlers.cap.versio.anterior" arguments="${definicio.etiqueta}"/>
						</div>						
				    </c:otherwise>
				</c:choose>
				</div>
			</div>
			
		</c:forEach>
	
	
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true">
				<spring:message code="comu.boto.cancelar"/>
			</button>
			<button type="submit" class="btn btn-success right">
				<span class="fa fa-cog"></span> <spring:message code="expedient.tipus.propagar.handlers.form.propagar"/>
			</button>
		</div>
	</form:form>

</body>
</html>