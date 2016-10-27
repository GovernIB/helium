<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<style>
	body>div {
		padding: 15px 0px 0px 0px !important;
	}
	body.loading {
    	overflow: hidden;   
	}
	body.loading .rmodal {
    	display: block;
	}
	.rmodal {
	    display:    none;
	    position:   fixed;
	    z-index:    1000;
	    top:        0;
	    left:       0;
	    height:     100%;
	    width:      100%;
	    background: rgba( 255, 255, 255, .8 ) 
	                url('<c:url value="/img/ajax-loader.gif"/>') 
	                50% 50% 
	                no-repeat;
	}
</style>
<%--
<div class="modal-dialog modal-lg">

    <!-- Modal content-->
    <div class="modal-content">
		<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title"><spring:message code="tasca.signa.signar.passarela"/></h4>
		</div>
--%>		
		<c:url value='/v3/tasca/${tascaId}/document/${documentCodi}/firmaPassarela' var="urlform"/>
		<form:form id="formFirma${passarelaFirmaEnviarCommand.documentId}" action="${urlform}" method="post" cssClass="form-horizontal" commandName="passarelaFirmaEnviarCommand" role="form">
			<input type="hidden" id="documentId" name="documentId" value="${passarelaFirmaEnviarCommand.documentId}"/>
			<div class="modal-body" style="height: 300px;">
				<hel:inputText name="motiu" textKey="passarelafirma.form.camp.motiu" required="true"/>
				<hel:inputText name="lloc" textKey="passarelafirma.form.camp.lloc" required="true"/>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default dismiss" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></button>
				<button type="submit" class="btn btn-primary"><span class="fa fa-play"></span> <spring:message code="passarelafirma.form.iniciar"/></button>
			</div>
		</form:form>
		<div class="rmodal"></div>
<%--
	</div>
</div>
--%>
	<script type="text/javascript">
	// <![CDATA[
		$('.dismiss').click(function(e){
			window.parent.$('#dismiss').click();
		});
		
		$('#formFirma${passarelaFirmaEnviarCommand.documentId}').submit(function() {
			$('body').addClass("loading");
			return true;
// 		    $.ajax({ // create an AJAX call...
// 		        data: $(this).serialize(),
// 		        type: $(this).attr('method'),
// 		        url: $(this).attr('action'),
// 		        success: function(response) {
// 		            $('#modalPassarela${passarelaFirmaEnviarCommand.documentId}').html(response);
// 		        },
// 		        error: function(response) {
// 		        	$('#modalPassarela${passarelaFirmaEnviarCommand.documentId}').modal('hide');
// 		        	alert("S'ha produït un error al realitzar la connexió amb la passarel·la de firma");
// 		        }
// 		    });
// 		    return false;
		});
	//]]>
	</script>
