<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<c:set var="numColumnes" value="${3}"/>
<%--c:set var="hiHaDadesReadOnly" value="${false}"/>
<c:set var="countReadOnly" value="${0}"/>
<c:forEach var="dada" items="${dades}">
	<c:if test="${dada.readOnly}">
		<c:set var="countReadOnly" value="${countReadOnly + 1}"/>
		<c:set var="hiHaDadesReadOnly" value="${true}"/>
	</c:if>
</c:forEach>
<c:if test="${hiHaDadesReadOnly}">
	<c:import url="import/expedientDadesTaula.jsp">
		<c:param name="dadesAttribute" value="dades"/>
		<c:param name="titol" value="Dades de referència"/>
		<c:param name="numColumnes" value="${3}"/>
		<c:param name="count" value="${countReadOnly}"/>
		<c:param name="condicioCamp" value="readOnly"/>
	</c:import>
</c:if--%>
<html>
<head>
	<title>${tasca.titol}</title>
	<script type="text/javascript" src="<c:url value="/js/jquery.keyfilter.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
<script>
	$(document).ready(function() {
		$("i.agrupacio-desplegador").click(function() {
			var taula = $(this).parent().parent().parent().parent().parent();
			$('tbody', taula).toggleClass('hide');
			$(this).removeClass('icon-chevron-up');
			$(this).removeClass('icon-chevron-down');
			if ($('tbody', taula).hasClass('hide'))
				$(this).addClass('icon-chevron-down');
			else
				$(this).addClass('icon-chevron-up');
		});
	});
</script>
	<script type="text/javascript">
	// <![CDATA[
		
		function confirmar(form) {
			$("table").each(function(){
				if ($(this).hasClass("hide")) {
					$(this).remove();
				}
			});
			return true;
		}
	    function verificarSignatura(element) {
			var amplada = 800;
			var alcada = 600;
			$('<iframe id="verificacio" src="' + element.href + '"/>').dialog({
				title: "<spring:message code='tasca.form.verif_signa' />",
				autoOpen: true,
				modal: true,
				autoResize: true,
				width: parseInt(amplada),
				height: parseInt(alcada)
			}).width(amplada - 30).height(alcada - 30);
			return false;
		}
		function infoRegistre(docId) {
			var amplada = 600;
			var alcada = 200;
			$('<div>' + $("#registre_" + docId).html() + '</div>').dialog({
				title: "<spring:message code='tasca.form.info_reg' />",
				autoOpen: true,
				modal: true,
				width: parseInt(amplada),
				height: parseInt(alcada)
			}).width(amplada - 30).height(alcada - 30);
			return false;
		}
		function canviTermini(input) {
			var campId = input.id.substring(0, input.id.lastIndexOf("_"));
			var anys = document.getElementById(campId + "_anys").value;
			var mesos = document.getElementById(campId + "_mesos").value;
			var dies = document.getElementById(campId + "_dies").value;
			if (!anys.empty() && !mesos.empty() && !dies.empty()) {
				$(campId).val(anys + "/" + mesos + "/" + dies);
			} else {
				$(campId).val("");
			}
		}
		function editarRegistre(campId, campCodi, campEtiqueta, numCamps, index) {
			var amplada = 686;
			var alcada = 64 * numCamps + 80;
			var url = "registre.html?id=${tasca.id}&registreId=" + campId;
			if (index != null)
				url = url + "&index=" + index;
			$('<iframe id="' + campCodi + '" src="' + url + '" frameborder="0" marginheight="0" marginwidth="0"/>').dialog({
				title: campEtiqueta,
				autoOpen: true,
				modal: true,
				autoResize: true,
				width: parseInt(amplada),
				height: parseInt(alcada)
			}).width(amplada - 30).height(alcada - 30);
			return false;
		}

		function refresh() {
			$('form#command :button[name="submit"]').attr("name", "sbmt");
			$('form#command').submit();
		}

		function campOnFocus(camp) {
			$('form#command :input[name="helCampFocus"]').val("" + $(window).scrollTop() + "#${param.id}");
		}

		function addField(idTable){   
			tabla = $('#'+idTable);
			if(tabla.hasClass( "hide" )){
				tabla.removeClass( "hide" );

				if(tabla.hasClass( "togle" )){
					$('#button_add_'+idTable).hide();
				}
			} else {
		    	tr = $('tr:last', tabla);
		    	var newTr = tr.clone();
		    	limpiarFila(newTr);
	    		newTr.find(':input').each(function(indice,valor) {
			    	if (this.getAttribute("id") != null) {
		    			var id = this.getAttribute("id");
		    			var id_lim = id.substr(0, id.indexOf("["));
		    			var id_fin = id.substr(id.lastIndexOf("["), id.lastIndexOf("]"));
				    	var i = 1;
		    			while (document.getElementById(id_lim+"["+i+"]"+id_fin)) {
			    			i = i+1;
			    		}
			    		this.setAttribute("id", id_lim+"["+i+"]"+id_fin);
			    		this.setAttribute("name", id_lim+"["+i+"]"+id_fin);
		    		}
		    	});
		    	newTr.appendTo(tabla);
			}
		}

		function accioCampExecutar(elem, field) {
			if (confirm("<spring:message code='js.helforms.confirmacio' />")) {
				var fieldField = document.getElementById("helAccioCamp");
				if (fieldField == null) {
					newField = document.createElement('input');
					newField.setAttribute("id", "helAccioCamp");
					newField.setAttribute("name", "helAccioCamp");
					newField.setAttribute("type", "hidden");
					newField.setAttribute("value", field);
					elem.form.appendChild(newField);
				}
				return true;
			}
			return false;
		}

		$(".eliminarFila").live('click', function (){
			if($(this).closest('table').find('tr').index() < 2) {
			    var newTr = $(this).closest('tr');
		    	limpiarFila(newTr);
		    	
		    	$(this).closest('table').addClass( "hide" );

				if($(this).closest('table').hasClass( "togle" )){
					$('#button_add_'+$(this).closest('table').attr('id')).show();
				}
			} else {
	        	$(this).closest('tr').remove();
			}
	    });

		function limpiarFila(tr) {
			tr.find(':input').each(function() {
			    switch(this.type) {
			        case 'password':
			        case 'text':
			        case 'textarea':
			        case 'file':
			        case 'select-one':
			        case 'select-multiple':
			            $(this).val('');
			            break;
			        case 'checkbox':
			        case 'radio':
			            this.checked = false;
			    }
			});
		}
		var submitAction;
		function saveAction(element, action) {
			submitAction = action;
			if ($.browser.msie && $.browser.version.substr(0,1) <= 7) {
				element.innerHTML = action;
				var $submits = document.getElementsByName("submit");
				for (var i = 0; i < $submits.length; i++) {
				    if ($submits[i] != element) {
				        $submits[i].name = $submits[i].name + i;
				    }
				}
			}
		}
	// ]]>
	</script>
</head>
<body>

	<c:if test="${not empty dadesNomesLectura}">
		<c:import url="import/expedientDadesTaula.jsp">
			<c:param name="dadesAttribute" value="dadesNomesLectura"/>
			<c:param name="titol" value="Dades de referència"/>
			<c:param name="numColumnes" value="${numColumnes}"/>
			<c:param name="count" value="${fn:length(dadesNomesLectura)}"/>
			<c:param name="desplegat" value="${false}"/>
			<c:param name="desplegadorClass" value="agrupacio-desplegador"/>
		</c:import>
	</c:if>
	<c:if test="${not empty documentsNomesLectura}">
		<c:import url="import/expedientDadesTaula.jsp">
			<c:param name="dadesAttribute" value="documentsNomesLectura"/>
			<c:param name="titol" value="Documents de referència"/>
			<c:param name="numColumnes" value="${numColumnes}"/>
			<c:param name="count" value="${fn:length(documentsNomesLectura)}"/>
			<c:param name="desplegat" value="${false}"/>
			<c:param name="desplegadorClass" value="agrupacio-desplegador"/>
		</c:import>
	</c:if>
	<c:set var="pipellaIndex" value="${1}"/>
	<ul id="tabnav" class="nav nav-tabs">
		<c:if test="${not empty dades}">
			<li class="active <c:if test="${not tasca.validada}"> warn</c:if>"><a href="#dades" data-toggle="tab">${pipellaIndex}. Dades</a></li>
			<c:set var="pipellaIndex" value="${pipellaIndex + 1}"/>
		</c:if>
		<c:if test="${not empty documents}">
			<li class=""><a href="#documents" data-toggle="tab">${pipellaIndex}. Documents</a></li>
			<c:set var="pipellaIndex" value="${pipellaIndex + 1}"/>
		</c:if>
		<c:if test="${not empty signatures}">
			<li class=""><a href="#signatures" data-toggle="tab">${pipellaIndex}. Signatures</a></li>
			<c:set var="pipellaIndex" value="${pipellaIndex + 1}"/>
		</c:if>
	</ul>
	<div class="tab-content">
		<c:if test="${not empty dades}">
			<div class="tab-pane active" id="dades">
				<c:if test="${not tasca.validada}">
					<div class="missatge missatgesWarn">
						<c:choose>
							<c:when test="${empty tasca.formExtern}">
								<p><spring:message code='tasca.form.no_validades' /></p>
							</c:when>
							<c:otherwise>
								<p><spring:message code='tasca.form.compl_form' /></p>
							</c:otherwise>
						</c:choose>
					</div>
				</c:if>
				<c:set var="hiHaCampsReadOnly" value="${false}"/>
				<c:forEach var="camp" items="${dades}">
					<c:if test="${camp.readOnly}">
						<c:set var="hiHaCampsReadOnly" value="${true}"/>
					</c:if>
				</c:forEach>
				<c:set var="hiHaDocumentsReadOnly" value="${false}"/>
				<c:forEach var="document" items="${documents}">
					<c:if test="${document.readOnly}">
						<c:set var="hiHaDocumentsReadOnly" value="${true}"/>
					</c:if>
				</c:forEach>
				<c:if test="${hiHaCampsReadOnly or hiHaDocumentsReadOnly}">
					<div class="missatge missatgesBlau">
						<c:if test="${hiHaDocumentsReadOnly}">
							<c:forEach var="documenTasca" items="${documents}">
								<c:if test="${documenTasca.readOnly}">
									<h4 class="titol-missatge">
										${documenTasca.documentNom}&nbsp;&nbsp;
										<c:set var="tascaActual" value="${tasca}" scope="request"/>
										<c:set var="documentActual" value="${documenTasca.documentCodi}" scope="request"/>
										<c:set var="codiDocumentActual" value="${documenTasca.documentCodi}" scope="request"/>
										<c:import url="../common/iconesConsultaDocument.jsp"/>
									</h4><br/>
								</c:if>
							</c:forEach>
						</c:if>
						<c:if test="${hiHaCampsReadOnly}">
							<div class="form-horizontal form-tasca">
								<span class="titol-missatge"><fmt:message key='common.tascaro.dadesref' /></span>
								<form  id="commandReadOnly" name="commandReadOnly" action="form" method="post">
									<input type="hidden" id="id" name="id" value="${tasca.id}"/>
									<div class="inlineLabels">
										<c:forEach var="dada" items="${dades}" varStatus="varStatusMain">
											<c:if test="${dada.readOnly}">
												<div class="control-group">
													<label class="control-label" for="${dada.varCodi}">${dada.campEtiqueta} - ${dada.campTipus}</label>
													
													<c:set var="dada" value="${dada}"/>
													<c:set var="dada_multiple" value=""/>
													<%@ include file="campsTasca.jsp" %>
													<%@ include file="campsTascaRegistre.jsp" %>
												</div>
											</c:if>
										</c:forEach>
									</div>
								</form>
							</div>
						</c:if>
					</div>
				</c:if>
				<form:form onsubmit="return confirmar(this)" id="command" name="command" action="form" cssClass="form-horizontal form-tasca" method="post" commandName="command">
					<input type="hidden" id="id" name="id" value="${tasca.id}"/>
					<input type="hidden" id="helFinalitzarAmbOutcome" name="helFinalitzarAmbOutcome" value="@#@"/>
					<c:forEach var="dada" items="${dades}" varStatus="varStatusMain">
						<c:if test="${not dada.readOnly}">
							<div class="control-group fila_reducida">
								<label class="control-label" for="${dada.varCodi}">${dada.campEtiqueta} - ${dada.campTipus}</label>
								
								<c:set var="dada" value="${dada}"/>
								<%@ include file="campsTasca.jsp" %>
								<%@ include file="campsTascaRegistre.jsp" %>
							</div>
						</c:if>
					</c:forEach>
					<div id="guardarValidarTarea">
						<c:if test="${empty dades}">
							<%@ include file="campsTascaInfo.jsp" %>		
						</c:if>
						<c:if test="${not empty dades}">
							<div style="clear: both"></div>
							<%@ include file="campsTascaGuardarTasca.jsp" %>
						</c:if>
					</div>
				</form:form>
				<div class="hide" id="finalizarTarea">
					<%@ include file="campsTascaTramitacioTasca.jsp" %>
				</div>
			</div>
		</c:if>
		<c:if test="${not empty documents}">
			<div class="tab-pane" id="documents">
				<c:forEach var="document" items="${documents}">
				<div class="well well-small">
					<h4>${document.documentNom}</h4>
				</div>
				</c:forEach>
			</div>
		</c:if>
		<c:if test="${not empty signatures}">
			<div class="tab-pane" id="signatures">
				signatures
			</div>
		</c:if>
	</div>
	<script>	
		$( '[data-required="true"]' )
			.closest(".control-group")
			.children("label")
			.prepend("<i class='icon-asterisk'></i> ");
	
		window.parent.canviTitolModal("${tasca.titol}");
		/*var html = $('#finalizarTarea').html();
		$('#finalizarTarea').remove();
		window.parent.addHtmlPeuModal(html,'formFinalitzar');*/
	</script>
</body>

<%!
private String toJavascript(String str) {
	if (str == null)
		return null;
	return str.replace("'", "\\'");
}
%>
