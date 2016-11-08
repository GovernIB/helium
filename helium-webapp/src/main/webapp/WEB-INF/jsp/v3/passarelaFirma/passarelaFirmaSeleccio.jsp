<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<style>
	body>div {padding: 15px 0px 0px 0px !important;}
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
		<c:url value='/v3/firmapassarela/showsignaturemodule' var="urlpass"/>
		<div class="modal-body" style="height: 300px;">
			<div class="well" style="max-width: 400px; margin: 0 auto 10px;">
				<c:forEach items="${plugins}" var="plugin">
					<a href="${urlpass}/${plugin.pluginId}/${signaturesSetId}" class="btn btn-large btn-block btn-primary btn-plugin">
						<b>${plugin.nom}</b><br />
						<small><i>${plugin.descripcioCurta}</i></small>
					</a>
				</c:forEach>
			</div>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-default dismiss" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></button>
		</div>
		<script type="text/javascript">
			$('.dismiss').click(function(e){
				window.parent.$('#dismiss').click();
			});
		</script>
<%--
	</div>
	<script type="text/javascript">
	// <![CDATA[
		$('.btn-plugin').click(function() {
		    $.get(
		    		$(this).data("href"),
		    		function(data) {
		    			$('#modalPassarela${documentId} .modal-body').html(data);
		        	}).fail( function() {
		        		$('#modalPassarela${documentId}').modal('hide');
		        		alert("S'ha produït un error al realitzar la connexió amb la passarel·la de firma");
		        	});
		});
	//]]>
	</script>
</div>
--%>