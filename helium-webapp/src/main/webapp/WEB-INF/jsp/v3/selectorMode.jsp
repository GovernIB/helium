<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<style type="text/css">
</style>


<form class="well">
	<div class="row">
		<div class="col-sm-12">
			<div class="form-group">
				<label class="control-label col-xs-1 " for="agrupacions"><spring:message code="expedient.tipus.mode.titol"/></label>
				<div class="controls col-xs-2" style="margin-top: -10px; margin-left: 10px">
					<div class="radio">
						<form:radiobutton path="opcioMode" value="tipusExpedient" /><spring:message code="expedient.tipus.mode.te"/> 
					</div>
					<div class="radio">
						<form:radiobutton path="opcioMode" value="definicioProces" /><spring:message code="expedient.tipus.mode.dp"/>
					</div>
				</div>

				<div class="controls col-xs-4">
					<hel:inputSelect required="false" emptyOption="true" name="agrupacions" textKey="expedient.tipus.mode.dp" placeholderKey="expedient.tipus.camp.llistat.agrupacio.seleccionada.placeholder" optionItems="${agrupacions}" optionValueAttribute="codi" optionTextAttribute="valor"/>
				</div>

				<div class="controls col-xs-4">
					<hel:inputSelect required="false" emptyOption="true" name="agrupacions" textKey="expedient.tipus.mode.dp.versio" placeholderKey="expedient.tipus.camp.llistat.agrupacio.seleccionada.placeholder" optionItems="${agrupacions}" optionValueAttribute="codi" optionTextAttribute="valor"/>
				</div>
			</div>

			<script>
				
			</script>
		</div>
	</div>
</form>