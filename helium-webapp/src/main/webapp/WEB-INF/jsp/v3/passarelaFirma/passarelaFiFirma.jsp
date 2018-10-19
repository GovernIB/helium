<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

	<script type="text/javascript">
	// <![CDATA[
	    $(document).ready(function() {
	    	if ($('.alert-danger').length == 0) {
    			opener.refreshSignatures();
			}
			$('#tancarBtn').click(function(e) {
		    	window.close();
			})
	    });
	    // Quan es tanca la finestra refresca les signatures
	    $( window ).unload(function() {
	    	opener.refreshSignatures();
		});	    
	//]]>
	</script>

	<button id="tancarBtn" class="btn btn-default"><spring:message code='comu.boto.tancar' /></button>
	