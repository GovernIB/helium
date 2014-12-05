<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<style type="text/css">
	.btn-file {position: relative; overflow: hidden;}
	.btn-file input[type=file] {position: absolute; top: 0; right: 0; min-width: 100%; min-height: 100%; font-size: 100px; text-align: right; filter: alpha(opacity = 0); opacity: 0; outline: none; background: white; cursor: inherit; display: block;}
	.form-group {width: 100%;}
	.fila_reducida {width: 100%;}		
	.col-xs-4 {width: 20%;}		
	.col-xs-8 {width: 77%;}
	.col-xs-8 .form-group {margin-left: 0px;margin-right: 0px;}
	.col-xs-8 .form-group .col-xs-4 {padding-left: 0px;width: 15%;}
	.col-xs-8 .form-group .col-xs-8 {width: 85%;padding-left: 15px;padding-right: 0px;}
	#s2id_estatId {width: 100% !important;}
	.arxiu {margin-left: 20%;}
	h4.titol-missatge i {padding-left: 10px;}	
</style>

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
<c:forEach var="document" items="${documents}">
	<div class="well well-small">
		<c:url value="/v3/expedient/document/arxiuMostrar" var="downloadUrl"><c:param name="token" value="${document.tokenSignatura}"/></c:url>
		<form class="form-horizontal form-tasca" action="documentAdjuntar" enctype="multipart/form-data" method="post">
			<div class="inlineLabels">
				<input type="text" id="docId" name="document.id"/>
				<h4 class="titol-missatge">
					${document.documentNom}
		 			<c:if test="${document.plantilla || not empty document.arxiuNom}"> 
		 				<a href="<c:url value="../../../../v3/expedient/massiva/documentGenerarMas"><c:param name="docId" value="${documentExpedientCommand.docId}"/><c:param name="inici" value="${inici}"/><c:param name="correu" value="${correu}"/></c:url>">
		 					<i class="fa fa-file-text-o"></i>
		 				</a>
		 			</c:if>
	 				<c:if test="${downloadUrl != ''}">
						<a id="downloadUrl" href="${downloadUrl}">
							<i class="fa fa-download"></i>
						</a>
						<a id="removeUrl" href="#" onclick="return mostrarAmagarFile()">
							<i class="fa fa-times"></i>
						</a>
					</c:if>
				</h4>
				<div class="form-group">
					<div class="col-xs-8 arxiu">					
			            <div id="amagarFile" class="input-group <c:if test="${downloadUrl != ''}">hide</c:if>">
			                <span class="input-group-btn">
			                    <span class="btn btn-primary btn-file">
			                        <spring:message code='expedient.document.arxiu' />… <input type="file">
			                    </span>
			                </span>
			               <input id="contingut" name="contingut" readonly="readonly" class="form-control" />
			            </div>
					</div>
				</div>
			</div>
			<div id="modal-botons" class="well">
				<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
				<button class="btn btn-primary right" type="submit" name="accio" value="document_modificar">
					<spring:message code='comuns.modificar' />
				</button>
			</div>
		</form>
	</div>
</c:forEach>
	        
<script type="text/javascript">
	// <![CDATA[
	$(document).on('change', '.btn-file :file', function() {
		var input = $(this),
		numFiles = input.get(0).files ? input.get(0).files.length : 1,
		label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
		input.trigger('fileselect', [numFiles, label]);
	});
	
	$(document).ready( function() {
		$('.btn-file :file').on('fileselect', function(event, numFiles, label) {
			var input = $(this).parents('.input-group').find(':text'),
			log = numFiles > 1 ? numFiles + ' files selected' : label;
			if( input.length ) {
				input.val(log);
			} else {
				if( log )
					alert(log);
			}
		});
	}); 				

	function mostrarAmagarFile() {
		$("#amagarFile").removeClass("hide");
		$("#downloadUrl").hide();
		$("#removeUrl").hide();
	}
	// ]]>
</script>
