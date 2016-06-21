<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>


<c:choose>
	<c:when test="${not empty expedientTipus}">

		<div class="row well well-small">
			<div class="col-sm-10">
				<hel:inputSelect required="false" emptyOption="true" name="agrupacions" textKey="expedient.tipus.camp.llistat.agrupacio.seleccionada" placeholderKey="expedient.tipus.camp.llistat.agrupacio.seleccionada" optionItems="${agrupacions}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			</div>
			<div class="col-sm-2">
				<div class="dropdown">
					<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
					<ul class="dropdown-menu">
						<li><a href="${expedientTipus.id}/agrupacio/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.camp.llistat.agrupacio.boto.crear"/></a></li>
						<li style="display: none;"><a id="agrupacioUpdate" href="${expedientTipus.id}/agrupacio/update" data-toggle="modal" ><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.camp.llistat.agrupacio.boto.modificar"/></a></li>
						<li style="display: none;"><a id="agrupacioDelete" href="${expedientTipus.id}/variable/delete" data-rdt-link-ajax="true" data-confirm="<spring:message code="expedient.tipus.camp.llistat.agrupacio.boto.esborrar.confirm"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.tipus.camp.llistat.agrupacio.boto.esborrar"/></a></li>
					</ul>
				</div>
			</div>
		</div>

		<div class="botons-titol text-right">
			<a id="nou_camp" class="btn btn-default" href="${expedientTipus.id}/variable/new" data-toggle="modal" data-datatable-id="expedientTipusVariable"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.camp.llistat.accio.nova"/></a>
		</div>
		<table	id="expedientTipusVariable"
				data-toggle="datatable"
				data-url="${expedientTipus.id}/variable/datatable"
				data-paging-enabled="true"
				data-info-type="search"
				data-ordering="true"
				data-default-order="1"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="agrupacio" data-visible="false"/>
					<th data-col-name="codi" width="20%"><spring:message code="expedient.tipus.camp.llistat.columna.codi"/></th>
					<th data-col-name="etiqueta"><spring:message code="expedient.tipus.camp.llistat.columna.etiqueta"/></th>
					<th data-col-name="tipus"><spring:message code="expedient.tipus.camp.llistat.columna.tipus"/></th>
					<th data-col-name="multiple"><spring:message code="expedient.tipus.camp.llistat.columna.multiple"/></th>
					<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a data-toggle="modal" href="${expedientTipus.id}/variable/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
								<li><a href="${expedientTipus.id}/variable/{{:id}}/delete" data-confirm="<spring:message code="expedient.tipus.camp.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
								<li class="divider"></li>
								<li>
									{{if agrupacio == null}}
										<span class="fa fa-plus" style="margin-left:10px"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.agrupar"/>
										<br/>
										<c:forEach items="${agrupacions}" var="agrupacio">
											<a href="${expedientTipus.id}/variable/{{:id}}/agrupar/${agrupacio.codi}" 
												class="ajax-link"
												data-rdt-link-callback="refrescaTaula();">${agrupacio.valor}</a>
										</c:forEach>
									{{else}}
										<a href="${expedientTipus.id}/variable/{{:id}}/desagrupar"
												class="ajax-link"
											data-rdt-link-callback="refrescaTaula();"><span class="fa fa-minus" data-rdt-link-ajax="true"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.desagrupar"/>
									{{/if}}																		
								</li>
							</ul>
						</div>
					</script>
					</th>
				</tr>
			</thead>
		</table>

		
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
// <![CDATA[
$(document).ready(function() {
	
	// Botons de modificar i eliminar agrupacions
	$('#agrupacioUpdate,#agrupacioUpdate').click(function(e) {
		e.preventDefault();
		return false;
	});
	
	// Canvi en la selecció de les agrupacions
	$('#agrupacions').change(function() {
		var agrupacioId = $(this).val();
		if (agrupacioId != "") {
			$('#nou_camp').attr('href', '${expedientTipus.id}/variable/new?agrupacioId=' + agrupacioId);
			$('#agrupacioUpdate').attr('href', '${expedientTipus.id}/agrupacio/' + agrupacioId + '/update');
			$('#agrupacioDelete').attr('href', '${expedientTipus.id}/agrupacio/' + agrupacioId + '/delete');
			$('#agrupacioUpdate,#agrupacioDelete').closest('li').show();			
		} else {
			$('#nou_camp').attr('href', '${expedientTipus.id}/variable/new');
			$('#agrupacioUpdate,#agrupacioDelete').closest('li').hide();
		}
		refrescaTaula();
	});
	
	// Quan es repinta la taula actualitza els enllaços
	$('#expedientTipusVariable').on('draw.dt', function() {
		// Botons per agrupar o desagrupar
		$(".ajax-link").click(function(e) {
			var getUrl = $(this).attr('href');
			$.ajax({
				type: 'GET',
				url: getUrl,
				async: true,
				success: function(result) {
					if (result) {
						refrescaTaula();
					}
				}
			});
			e.stopImmediatePropagation();
			return false;
		});
	});		
});

function refrescaTaula() {
	var agrupacioId = $("#agrupacions").val();
	if (agrupacioId != "") {
		$('#expedientTipusVariable').webutilDatatable('refresh-url', '${expedientTipus.id}/variable/datatable?agrupacioId='+agrupacioId);		
	} else {
		$('#expedientTipusVariable').webutilDatatable('refresh-url', '${expedientTipus.id}/variable/datatable');
	}
}
// ]]>
</script>			
