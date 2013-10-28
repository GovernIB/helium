<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
<head>
	<title><fmt:message key="index.inici" /></title>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
<script>
	<c:if test="${expedientConsultaCommand.consultaRealitzada}">
	var seleccioInicialitzada = false;
	var seleccioArray = [];
	var seleccioClass = "info";
	var taula;
	function obtenirSeleccioActual() {
		if (!seleccioInicialitzada) {
			$.ajax({
				url: "expedient/deseleccionar/-1",
				success: function (data) {
					seleccioArray = data;
				},
				async: false
			});
			seleccioInicialitzada = true;
		}
		return seleccioArray;
	}
	function modificarSeleccioIActualitzar(valor, afegir) {
		var url = (afegir) ? "expedient/seleccionar/" : "expedient/deseleccionar/";
		$.get(
			url + valor,
			function (data) {
				seleccioArray = data;
				actualitzarVistaSeleccio();
			});
	}
	function actualitzarVistaSeleccio() {
		$('#dades-carregant').hide();
		var seleccio = obtenirSeleccioActual();
		$('#tramitacioMassivaCount').html(seleccio.length);
		var totsSeleccionats = true;
		$("tbody tr", taula).each(function (index, row) {
			var checkbox = $("td:eq(0) input[type=checkbox]", row);
			var value = checkbox.attr("value");
			var index = seleccio.indexOf(parseInt(value));
			if (index == -1) {
				$(row).removeClass(seleccioClass);
				checkbox.removeAttr("checked");
				totsSeleccionats = false;
			} else {
				$(row).addClass(seleccioClass);
				checkbox.attr("checked", "checked");
			}
		});
		if (totsSeleccionats)
			$("thead input[type=checkbox]", taula).attr("checked", "checked");
		else
			$("thead input[type=checkbox]", taula).removeAttr("checked");
	}
	
	function formatDate(fechaSinFormato) {
		var date = fechaSinFormato.split(" ");
		var fecha = date[0].split("-");
		var hora = date[1].split(":");
		fechaFormateada = (fecha[2]+"/"+fecha[1]+"/"+fecha[0]+" " +hora[0]+":" +hora[1]);
		return fechaFormateada;
	}
	</c:if>
	

	function confirmarEsborrar(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm("<fmt:message key="expedient.consulta.confirm.esborrar"/>");
	}
	
	function fnRowCallback(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
		var numColumnes = $("td", nRow).size();
		var expedientId = aData[8];
		$("td:eq(" + (numColumnes - 1) + ")", nRow).html(
			'<div class="btn-group">' +
				'<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#">' + 
				 	'<i class="icon-cog icon-white"></i> Accions <span class="caret"></span>' + 
				 '</a>' + 
				 '<ul class="dropdown-menu">' + 
				 	'<li>' + 
				 		'<a href="expedient/' + expedientId + '"><i class="icon-folder-open"></i> Obrir</a>' + 
				 	'</li>' + 
				 	'<li>' + 
				 		'<a href="expedient/' + expedientId + '/stop"><i class="icon-stop"></i> Aturar</a>' + 
				 	'</li>' + 
				 	'<li>' + 
				 		'<a href="expedient/' + expedientId + '/suspend"><i class="icon-remove"></i> Anular</a>' + 
				 	'</li>' + 
				 	'<li>' + 
				 	'<a href="#" onclick="return confirmarEsborrar(event)">' +
				 	//'<a href="<c:url value="expedient/' + expedientId + '/delete"></c:url>" onclick="return confirmarEsborrar(event)">' +
			 			'<i class="icon-trash"></i> Esborrar' +
				 		'</a>' +
				 	'</li>' + 
				 '</ul>' + 
			'</div>');
		if (aData[6] === 'true')
			$("td:eq(1)", nRow).append(' <span class="label" title="Aturat">AT</span>');
		if (aData[7] === 'true')
			$("td:eq(1)", nRow).append('  <span class="label" title="Anulat">AN</span>');					
		if (!aData[5]) {
			if (aData[3])
				$("td:eq(" + (numColumnes - 2) + ")", nRow).html('Finalitzat');
			else
				$("td:eq(" + (numColumnes - 2) + ")", nRow).html('Iniciat');
		}
		
		$("td:eq(" + (numColumnes - 4) + ")", nRow).html(formatDate(aData[2]));
		
		if (!aData[5] && aData[3]) $("td:eq(" + (numColumnes - 2) + ")", nRow).html('Finalitzat');
		var oTable = $(taula).dataTable();
		if (oTable.fnSettings().aoColumns[0].bVisible) {
			var seleccio = obtenirSeleccioActual();
			var seleccionat = seleccio.indexOf(parseInt(aData[0])) != -1;
			if (seleccionat) {
				$(nRow).addClass(seleccioClass);
				$("td:eq(0)", nRow).html('<input type="checkbox" value="' + aData[0] + '" checked="checked"/>');
			} else {
				$("td:eq(0)", nRow).html('<input type="checkbox" value="' + aData[0] + '"/>');
			}
		}
	}
	
	$(document).ready(
		function() {
			<c:if test="${expedientConsultaCommand.consultaRealitzada}">
			
			<c:import url="import/dataTable.jsp">
				<c:param name="idTable" value="taulaDades"/>
				<c:param name="sAjaxSource" value="/v3/expedient/datatable"/>
			</c:import>
			
			// Gestiona els clics als checkboxes de cada fila
			$("tbody", taula).delegate(
				"input[type=checkbox]",
				"click",
				function () {
					modificarSeleccioIActualitzar(
							$(this).attr("value"),
							$(this).is(":checked"));
				}
			);
			// Gestiona el clic al checkbox de la capçalera
			$("thead input[type=checkbox]", taula).click(function() {
				var checked = $(this).is(":checked");
				var ids = [];
				$("tbody tr", taula).each(function (index, row) {
					ids.push($("td:eq(0) input[type=checkbox]", row).attr("value"));
				});
				modificarSeleccioIActualitzar(
						ids,
						checked);
			});
			</c:if>
			$("#nomesPendentsCheck").click(function() {
				$("input[name=nomesPendents]").val(!$("#nomesPendentsCheck").hasClass('active'));
				$(this).closest("form").submit();
			});
			$("#nomesAlertesCheck").click(function() {
				$("input[name=nomesAlertes]").val(!$("#nomesAlertesCheck").hasClass('active'));
			});
			$("#mostrarAnulatsCheck").click(function() {
				$("input[name=mostrarAnulats]").val(!$("#mostrarAnulatsCheck").hasClass('active'));
				$(".btn-primary").click();
			});
			$("#tramitacioMassivaActivar").click(function() {
				$("#tramitacioMassivaActivar").parent().addClass('hide');
				$("#tramitacioMassivaDesactivar").parent().removeClass('hide');
				var oTable = $(taula).dataTable();
				$("#tramitacioMassivaBtn").removeClass('disabled');
				oTable.fnSetColumnVis(0, true);
				$("input[name=tramitacioMassivaActivada]").val("true");
			});
			$("#tramitacioMassivaDesactivar").click(function() {
				$("#tramitacioMassivaDesactivar").parent().addClass('hide');
				$("#tramitacioMassivaActivar").parent().removeClass('hide');
				var oTable = $(taula).dataTable();
				$("#tramitacioMassivaBtn").addClass('disabled');
				oTable.fnSetColumnVis(0, false);
				$("input[name=tramitacioMassivaActivada]").val("false");
			});
			$('#filtresCollapsable').on('hide', function () {
				$('#filtresCollapse i').attr("class", "icon-chevron-down");
				$("input[name=filtreDesplegat]").val("false");
			});
			$('#filtresCollapsable').on('show', function () {
				$('#filtresCollapse i').attr("class", "icon-chevron-up");
				$("input[name=filtreDesplegat]").val("true");
			});
			$('select[name=expedientTipusId]').on('change', function () {
				$("select[name=estatText] option").each(function (index, option) {
		    		if (index > 1 && $("select[name=estatText] option").size() > 3)
						$(option).remove();
				});
				if ($(this).val()) {
					$.ajax({
					    url:'expedient/estatsPerTipus/' + $(this).val(),
					    type:'GET',
					    dataType: 'json',
					    success: function(json) {
					        $.each(json, function(i, value) {
					        	$('<option>').text(value.nom).attr('value', value.id).insertAfter($("select[name=estatText] option:eq(" + (i + 1) + ")"));
					        });
					    }
					});
				}
			});
			$('.datepicker').datepicker({language: 'ca', autoclose: true});
		}
	);
</script>
</head>
<body>

	<form:form action="" method="post" cssClass="well formbox" commandName="expedientConsultaCommand">
		<div class="page-header">
			Consulta d'expedients
			<form:hidden path="filtreDesplegat"/>
			<%--a id="filtresCollapse" class="btn btn-mini pull-right" href="#" title="Mostrar/ocultar camps del filtre" data-toggle="collapse" data-target="#filtresCollapsable"><i class="<c:choose><c:when test="${expedientConsultaCommand.filtreDesplegat}">icon-chevron-up</c:when><c:otherwise>icon-chevron-down</c:otherwise></c:choose>"></i></a--%>
		</div>
		<div id="filtresCollapsable" class="collapse<c:if test="${true or expedientConsultaCommand.filtreDesplegat}"> in</c:if>">
			<div class="row-fluid">
				<div class="span2">
					<c:set var="campPath" value="numero"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<spring:bind path="${campPath}">
							<input type="text" id="${campPath}" name="${campPath}" placeholder="Número"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span12">
						</spring:bind>
					</div>
				</div>
				<div class="span4">
					<c:set var="campPath" value="titol"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<spring:bind path="${campPath}">
							<input type="text" id="${campPath}" name="${campPath}" placeholder="Títol"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span12">
						</spring:bind>
					</div>
				</div>
				<div class="span3">
					<c:set var="campPath" value="expedientTipusId"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<c:choose>
						<c:when test="${not empty expedientTipusActual}">
							<input type="hidden" name="${campPath}" value="${expedientTipusActual.id}"/>
							<input type="text" name="${campPath}_actual" value="${expedientTipusActual.nom}" class="span12" disabled="disabled"/>
						</c:when>
						<c:otherwise>
							<form:select path="${campPath}" cssClass="span12">
								<option value="">Tipus d'expedient</option>
								<form:options items="${expedientTipusAccessibles}" itemLabel="nom" itemValue="id"/>
							</form:select>
						</c:otherwise>
					</c:choose>
				</div>
				<div class="span3">
					<c:set var="campPath" value="estatText"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<form:select path="${campPath}" cssClass="span12">
						<option value="">Estat</option>
						<form:option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.INICIAT%>">Iniciat</form:option>
						<form:options items="${estats}" itemLabel="nom" itemValue="id"/>
						<form:option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.FINALITZAT%>">Finalitzat</form:option>
					</form:select>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span4">
					<label>Data inici</label>
					<div class="row-fluid">
						<div class="span5 input-append date datepicker">
							<c:set var="campPath" value="dataIniciInicial"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<spring:bind path="${campPath}">
								<input type="text" id="${campPath}" name="${campPath}" placeholder="dd/mm/yyyy"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span11">
							</spring:bind>
							<span class="add-on"><i class="icon-calendar"></i></span>
						</div>
						<script>$("#${campPath}").mask("99/99/9999");</script>
						<div class="span5 offset1 input-append date datepicker">
							<c:set var="campPath" value="dataIniciFinal"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<spring:bind path="${campPath}">
								<input type="text" id="${campPath}" name="${campPath}" placeholder="dd/mm/yyyy"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span11">
							</spring:bind>
							<span class="add-on"><i class="icon-calendar"></i></span>
						</div>
						<script>$("#${campPath}").mask("99/99/9999");</script>
					</div>
				</div>
				<div class="span4">
					<label>Data fi</label>
					<div class="row-fluid">
							<c:set var="campPath" value="dataFiInicial"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<spring:bind path="${campPath}">
								<input type="text" id="${campPath}" name="${campPath}" placeholder="dd/mm/yyyy"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span10">
							</spring:bind>
							<span class="add-on"><i class="icon-calendar"></i></span>
						</div>
						<script>$("#${campPath}").mask("99/99/9999");</script>
						<div class="span5 offset1 input-append date datepicker">
							<c:set var="campPath" value="dataFiFinal"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<spring:bind path="${campPath}">
								<input type="text" id="${campPath}" name="${campPath}" placeholder="dd/mm/yyyy"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span10">
							</spring:bind>
							<span class="add-on"><i class="icon-calendar"></i></span>
						</div>
						<script>$("#${campPath}").mask("99/99/9999");</script>
					</div>
				</div>
				<c:choose>
					<c:when test="${globalProperties['app.georef.actiu']}">
						<c:choose>
							<c:when test="${globalProperties['app.georef.tipus']=='ref'}">
								<div class="span4">
									<label>Geoposició</label>
									<div class="row-fluid">
										<div class="span12">
											<c:set var="campPath" value="geoReferencia"/>
											<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
											<spring:bind path="${campPath}">
												<input type="text" id="${campPath}" name="${campPath}" placeholder="Codi georeferencial"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span12">
											</spring:bind>
										</div>
									</div>
								</div>
							</c:when>
							<c:otherwise>
								<div class="span4">
									<label>Geoposició</label>
									<div class="row-fluid">
										<div class="span6">
											<c:set var="campPath" value="geoPosX"/>
											<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
											<spring:bind path="${campPath}">
												<input type="text" id="${campPath}" name="${campPath}" placeholder="Coordenada X"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span12">
											</spring:bind>
										</div>
										<div class="span6">
											<c:set var="campPath" value="geoPosY"/>
											<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
											<spring:bind path="${campPath}">
												<input type="text" id="${campPath}" name="${campPath}" placeholder="Coordenada Y"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span12">
											</spring:bind>
										</div>
									</div>
								</div>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<div class="span4"></div>
					</c:otherwise>
				</c:choose>
			</div>
			<hr/>
<!-- 		</div> -->
		<div class="row-fluid">
			<div class="span6">
				<form:hidden path="nomesPendents"/>
				<form:hidden path="nomesAlertes"/>
				<form:hidden path="mostrarAnulats"/>
				<form:hidden path="tramitacioMassivaActivada"/>
				<div class="btn-group">
					<a id="nomesPendentsCheck" href="#" title="Només amb tasques pendents" class="btn<c:if test="${expedientConsultaCommand.nomesPendents}"> active</c:if>" data-toggle="button"><i class="icon-time"></i></a>
					<a id="nomesAlertesCheck" href="#" title="Només amb alertes" class="btn<c:if test="${expedientConsultaCommand.nomesAlertes}"> active</c:if>" data-toggle="button"><i class="icon-warning-sign"></i></a>
					<a id="mostrarAnulatsCheck" href="#" title="Mostrar anulats" class="btn<c:if test="${expedientConsultaCommand.mostrarAnulats}"> active</c:if>" data-toggle="button"><i class="icon-remove"></i></a>
				</div>
				<%--div class="btn-group">
					<a id="tramitacioMassivaCheck" class="btn<c:if test="${expedientConsultaCommand.tramitacioMassivaActivada}"> active</c:if>" href="#" data-toggle="button" title="Activar/desactivar tramitació massiva"><i class="icon-check"></i></a>
					<button id="tramitacioMassivaBtn" class="btn<c:if test="${not expedientConsultaCommand.tramitacioMassivaActivada}"> disabled</c:if>">Tramitació massiva <span id="tramitacioMassivaCount" class="badge">&nbsp;</span></button>
				</div--%>
				<div class="btn-group">
					<button id="tramitacioMassivaBtn" class="btn<c:if test="${not expedientConsultaCommand.tramitacioMassivaActivada}"> disabled</c:if>">Tramitació massiva <span id="tramitacioMassivaCount" class="badge">&nbsp;</span></button>
					<button class="btn dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>
					<ul class="dropdown-menu">
						<li<c:if test="${expedientConsultaCommand.tramitacioMassivaActivada}"> class="hide"</c:if>><a id="tramitacioMassivaActivar" href="#"><i class="icon-ok-circle"></i> Activar</a></li>
						<li<c:if test="${not expedientConsultaCommand.tramitacioMassivaActivada}"> class="hide"</c:if>><a id="tramitacioMassivaDesactivar" href="#"><i class="icon-ban-circle"></i> Desactivar</a></li>
					</ul>
				</div>
			</div>
			<div class="span6">
				<input type="hidden" name="consultaRealitzada" value="true"/>
				<button class="btn btn-primary pull-right">Consultar</button>
				<a class="btn pull-right" href="expedient/filtre/netejar" style="margin-right:.6em">Netejar</a>
			</div>
		</div>
	</form:form>

	<c:if test="${expedientConsultaCommand.consultaRealitzada}">
		<table id="taulaDades" class="table table-striped table-bordered">
			<thead>
				<tr>
					<th data-property="id" width="4%" <c:if test="${not expedientConsultaCommand.tramitacioMassivaActivada}">data-visible=false</c:if>><input type="checkbox"/></th>
					<th data-property="identificador" data-visible=true>Expedient</th>
					<th data-property="dataInici" data-sorting="desc" data-visible=true>Iniciat el</th>
					<th data-property="dataFi" data-visible=false>Finalitzat el</th>
					<th data-property="tipus.nom" data-visible=true>Tipus</th>
					<th data-property="estat.nom" data-visible=true>Estat</th>
					<th data-property="aturat" data-visible=false>Aturat</th>
					<th data-property="anulat" data-visible=false>Anulat</th>
					<th data-property="id" data-sortable="false" width="10%" data-visible=true></th>
				</tr>
				<tr id="dades-carregant">
					<td colspan="9" style="margin-top: 2em; text-align: center"><i class="icon-spinner icon-2x icon-spin"></i></td>
				</tr>
			</thead>
		</table>
	</c:if>

</body>
</html>
