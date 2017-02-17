<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

	<script type="text/javascript">
	// <![CDATA[
	    $(document).ready(function() {
	    	if ($('.alert-danger').length > 0) {
				$('#tancarBtn').show();
			} else {
				setTimeout(
						function() { 
			    			opener.refreshSignatures();
						}, 
						2000);				
			}
			$('#tancarBtn').click(function(e) {
		    	opener.refreshSignatures();
		    	e.preventDefault();
			})
	    });
	    
	    
	    
	//]]>
	</script>

	<button id="tancarBtn" class="btn btn-default" style="display:none;"><spring:message code='comu.boto.tancar' /></button>
	