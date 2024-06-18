<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:set var="titol"><spring:message code="finalitzar.expedient.titol" arguments="${expedientFinalitzarDto.expedient.titol}"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<script type="text/javascript">
	//<![CDATA[            
		$(document).ready(function() {
			$('#select-all-docs').on('click', function(event) {
				$('input[class=documentPerFirmarServidor]:checkbox').prop('checked', event.currentTarget.checked);
			});
			$('input[class=documentPerFirmarServidor]').on('change', function(event) {
				const total = $('input[class=documentPerFirmarServidor]:checkbox').length;
				const countSeleccionats = $('input[class=documentPerFirmarServidor]:checked').length;
				$('#select-all-docs').prop('checked', total == countSeleccionats);
			});
// 			$("#submitPrefinalitzar").on('click', function(event) {
// 				debugger;
// 				$("body").addClass("loading");
// 		        $.ajax({
// 		            url: '<c:url value="/prefinalitzar"/>',
// 		            type: 'post',
// 		            dataType: 'json',
// 		            data: {motiu: $("#motiuFinalitzar").val()},
// 		            success: function(data){
// 		            	debugger;
// 		            }
// 		        })
// 		        .fail(function( jqxhr, textStatus, error ) {
// 		             var err = textStatus + ', ' + error;
// 		             console.log( "Request Failed: " + err);
// 		        })
// 		        .always(function() {
// 		            $("body").removeClass("loading");
// 		        });		            
// 			});
		});		
	// ]]>
	</script>
</head>
<body>
	
	<form:form action="prefinalitzar" method="post" commandName="expedientFinalitzarDto">
	
		<form:hidden path="expedient.id"/>
		<form:hidden path="expedient.titol"/>
		<form:hidden path="expedient.arxiuActiu"/>
		<form:hidden path="accio"/>
		
		<c:if test="${expedientFinalitzarDto.documentsFinalitzar!=null && fn:length(expedientFinalitzarDto.documentsFinalitzar)>0}">
		
			<div class="alert alert-info" role="alert">
				<span class="fa fa-info-circle"></span>
				<spring:message code="finalitzar.expedient.info"/>
			</div>
		
			<table id="docsPrefinalitzar" class="table table-striped table-bordered table-hover dataTable no-footer">
				<thead>
					<tr role="row">
						<th width="45%">Titol</th>
						<th width="10%">Tipus</th>
						<th width="20%">Situacio</th>
						<th width="20%">Data creació</th>
						<th width="5%" style="padding-left: 14px;"><input type="checkbox" id="select-all-docs"></th>
					</tr>
				</thead>
				<tbody>
		
				<c:forEach var="doc" varStatus="status" items="${expedientFinalitzarDto.documentsFinalitzar}">
					<tr>
						<td>${doc.arxiuNom}</td>
						<td><c:if test="${doc.adjunt}">Adjunt</c:if><c:if test="${!doc.adjunt}">Document</c:if></td>
						<td>
							${doc.processInstanceNom}
							<c:if test="${not empty doc.arxiuUuid}">&nbsp;
								<span class="label label-info etiqueta-nti-arxiu">
									<spring:message code="expedient.info.etiqueta.arxiu"/>
								</span>
							</c:if>
							<c:if test="${not empty doc.annexAnotacioId}">&nbsp;
								<span class="label label-warning etiqueta-nti-arxiu">
									<spring:message code="expedient.document.info.etiqueta.anotacio"/>
								</span>
							</c:if>
							<c:if test="${not empty doc.notificacioId}">&nbsp;
								<span class="label label-warning etiqueta-nti-arxiu" title="">
									<spring:message code="expedient.document.info.etiqueta.notificat"/>
								</span>
							</c:if>
							<c:if test="${not empty doc.peticioPinbalId}">&nbsp;
								<span class="label label-success etiqueta-nti-arxiu" title="">
									PIN
								</span>
							</c:if>
						</td>
						<td>${doc.dataCreacioStr}</td>
						<td align="center">
							<input type="checkbox" class="documentPerFirmarServidor" name="documentsFinalitzar[${status.index}].seleccionat">
							<form:hidden path="documentsFinalitzar[${status.index}].documentStoreId"/>
							<form:hidden path="documentsFinalitzar[${status.index}].processInstanceId"/>
						</td>
					</tr>
				</c:forEach>
			
				</tbody>
			</table>
		
			<div class="control-group fila_reducida ocult">
				<div class="form-group">
					<label class="control-label col-xs-2">Motiu</label>
					<div class="controls col-xs-10">
						<textarea id="motiuFinalitzar" name="motiuFinalitzar" rows="5" style="width: 100%;"></textarea>
					</div>
				</div>
			</div>
		
		</c:if>
		
		<c:if test="${expedientFinalitzarDto.documentsFinalitzar==null || fn:length(expedientFinalitzarDto.documentsFinalitzar)==0}">
			<c:choose>
				<c:when test="${not expedientFinalitzarDto.expedient.arxiuActiu}">
					<div class="alert alert-info" role="alert">
						<span class="fa fa-info-circle"></span>
						<spring:message code="finalitzar.expedient.info3"/>
					</div>
				</c:when>
				<c:otherwise>
					<div class="alert alert-info" role="alert">
						<span class="fa fa-info-circle"></span>
						<spring:message code="finalitzar.expedient.info2"/>
					</div>				
				</c:otherwise>
			</c:choose>
		</c:if>
		
		<div id="modal-botons" class="well">
			<button type="button" class="modal-tancar btn btn-default" name="submit" value="cancel">
				<spring:message code='comuns.cancelar' />
			</button>
			<button type="submit" name="submit" value="submit" class="btn btn-primary">
				<span class="fa fa-pencil" onclick="$('#accio').val('firmar')"></span>&nbsp;
				<spring:message code="finalitzar.expedient.accio1"/>
			</button>			
			<button type="submit" name="submit" value="submit" class="btn btn-primary">
				<span class="fa fa-power-off" onclick="$('#accio').val('finalitzar')"></span>&nbsp;
				<spring:message code="finalitzar.expedient.accio2"/>
			</button>
		</div>
	
	</form:form>
</body>
</html>
