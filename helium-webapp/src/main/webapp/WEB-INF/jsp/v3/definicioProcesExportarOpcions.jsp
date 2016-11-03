<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

		<c:if test="${command != null}">
		
		<c:set var="tasquesErrors"><form:errors path="tasques"/></c:set>
		<c:set var="variablesErrors"><form:errors path="variables"/></c:set>
		<c:set var="documentsErrors"><form:errors path="documents"/></c:set>

		<div id="tasques" class="agrupacio">
			<div class="panel-heading clicable" data-toggle="collapse" data-target="#panel_tasques">
				<span class="marcador tots fa fa-check-square-o"></span>
				<span class="marcador algun fa fa-check-square-o text-muted" style="display: none;"></span>
				<span class="marcador cap fa fa-square-o" style="display: none;"></span>
				<spring:message code="definicio.proces.exportar.form.tasques"></spring:message>
				<span class="marcats">0</span>/<span class="total">0</span>
				<div class="pull-right">
					<span class="fa fa-chevron-down"></span>
					<span class="fa fa-chevron-up" style="display: none;"></span>
				</div>
			</div>
			<div id="panel_tasques" class="taula panel-body collapse <c:if test="${not empty tasquesErrors}"> has-error</c:if>">
				<c:if test="${not empty tasquesErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="tasques"/></p></c:if>
				<table id="tasques-taula"
						class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th><input type="checkbox" class="checkAll" checked="checked" data-sort="false"></th>
							<th><spring:message code="comuns.codi"/></th>
							<th><spring:message code="comuns.nom"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${tasques}" var="tasca" varStatus="procesosStatus">
						<tr class="row_checkbox">
							<td>
								<input type="checkbox" class="check" id="tasques_${tasca.jbpmName }" name="tasques" value="${tasca.jbpmName}" <c:if test="${inici or fn:contains(command.tasques, tasca.jbpmName)}">checked="checked"</c:if>/>
							</td>
							<td>${tasca.jbpmName}</td>
							<td>${tasca.nom}</td>
						</tr>
						</c:forEach>			
					</tbody>
				</table>
			</div>
		</div>

		<div id="variables" class="agrupacio">
			<div class="panel-heading clicable" data-toggle="collapse" data-target="#panel_variables">
				<span class="marcador tots fa fa-check-square-o"></span>
				<span class="marcador algun fa fa-check-square-o text-muted" style="display: none;"></span>
				<span class="marcador cap fa fa-square-o" style="display: none;"></span>
				<spring:message code="definicio.proces.exportar.form.variables"></spring:message>
				<span class="marcats">0</span>/<span class="total">0</span>
				<div class="pull-right">
					<span class="fa fa-chevron-down"></span>
					<span class="fa fa-chevron-up" style="display: none;"></span>
				</div>
			</div>
			<div id="panel_variables" class="taula panel-body collapse <c:if test="${not empty variablesErrors}"> has-error</c:if>">
				<c:if test="${not empty variablesErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="variables"/></p></c:if>
				<table id="variables-taula"
						class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th><input type="checkbox" class="checkAll" checked="checked" data-sort="false"></th>
							<th><spring:message code="comuns.codi"/></th>
							<th><spring:message code="definicio.proces.exportar.form.variables.titol"/></th>
							<th><spring:message code="definicio.proces.exportar.form.variables.tipus"/></th>
							<th><spring:message code="definicio.proces.exportar.form.variables.agrupacio"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${variables}" var="variable" varStatus="procesosStatus">
						<tr class="row_checkbox">
							<td>
								<input type="checkbox" class="check" id="variables_${variable.codi }" name="variables" value="${variable.codi}" <c:if test="${inici or fn:contains(command.variables, variable.codi)}">checked="checked"</c:if>/>
							</td>
							<td>${variable.codi}</td>
							<td>${variable.etiqueta}</td>
							<td>${variable.tipus}</td>
							<td><c:if test="${variable.agrupacio != null}">${variable.agrupacio.codi}</c:if></td>
						</tr>
						</c:forEach>			
					</tbody>
				</table>
			</div>
		</div>

		<div id="agrupacions" class="agrupacio">
			<div class="panel-heading clicable" data-toggle="collapse" data-target="#panel_agrupacions">
				<span class="marcador tots fa fa-check-square-o"></span>
				<span class="marcador algun fa fa-check-square-o text-muted" style="display: none;"></span>
				<span class="marcador cap fa fa-square-o" style="display: none;"></span>
				<spring:message code="definicio.proces.exportar.form.agrupacions"></spring:message>
				<span class="marcats">0</span>/<span class="total">0</span>
				<div class="pull-right">
					<span class="fa fa-chevron-down"></span>
					<span class="fa fa-chevron-up" style="display: none;"></span>
				</div>
			</div>
			<div id="panel_agrupacions" class="taula panel-body collapse">
				<table id="agrupacions-taula"
						class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th><input type="checkbox" class="checkAll" checked="checked" data-sort="false"></th>
							<th><spring:message code="comuns.codi"/></th>
							<th><spring:message code="comuns.nom"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${agrupacions}" var="agrupacio" varStatus="procesosStatus">
						<tr class="row_checkbox">
							<td>
								<input type="checkbox" class="check" id="agrupacions_${agrupacio.codi }" name="agrupacions" value="${agrupacio.codi}" <c:if test="${inici or fn:contains(command.agrupacions, agrupacio.codi)}">checked="checked"</c:if>/>
							</td>
							<td>${agrupacio.codi}</td>
							<td>${agrupacio.nom}</td>
						</tr>
						</c:forEach>			
					</tbody>
				</table>
			</div>
		</div>


		<div id="documents" class="agrupacio">
			<div class="panel-heading clicable" data-toggle="collapse" data-target="#panel_documents">
				<span class="marcador tots fa fa-check-square-o"></span>
				<span class="marcador algun fa fa-check-square-o text-muted" style="display: none;"></span>
				<span class="marcador cap fa fa-square-o" style="display: none;"></span>
				<spring:message code="definicio.proces.exportar.form.documents"></spring:message>
				<span class="marcats">0</span>/<span class="total">0</span>
				<div class="pull-right">
					<span class="fa fa-chevron-down"></span>
					<span class="fa fa-chevron-up" style="display: none;"></span>
				</div>
			</div>
			<div id="panel_documents" class="taula panel-body collapse <c:if test="${not empty documentsErrors}"> has-error</c:if>">
				<c:if test="${not empty documentsErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="documents"/></p></c:if>
				<table id="documents-taula"
						class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th><input type="checkbox" class="checkAll" checked="checked" data-sort="false"></th>
							<th><spring:message code="comuns.codi"/></th>
							<th><spring:message code="comuns.nom"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${documents}" var="document" varStatus="procesosStatus">
						<tr class="row_checkbox">
							<td>
								<input type="checkbox" class="check" id="documents_${document.codi }" name="documents" value="${document.codi}" <c:if test="${inici or fn:contains(command.documents, document.codi)}">checked="checked"</c:if>/>
							</td>
							<td>${document.codi}</td>
							<td>${document.nom}</td>
						</tr>
						</c:forEach>			
					</tbody>
				</table>
			</div>
		</div>

		<div id="terminis" class="agrupacio">
			<div class="panel-heading clicable" data-toggle="collapse" data-target="#panel_terminis">
				<span class="marcador tots fa fa-check-square-o"></span>
				<span class="marcador algun fa fa-check-square-o text-muted" style="display: none;"></span>
				<span class="marcador cap fa fa-square-o" style="display: none;"></span>
				<spring:message code="definicio.proces.exportar.form.terminis"></spring:message>
				<span class="marcats">0</span>/<span class="total">0</span>
				<div class="pull-right">
					<span class="fa fa-chevron-down"></span>
					<span class="fa fa-chevron-up" style="display: none;"></span>
				</div>
			</div>
			<div id="panel_terminis" class="taula panel-body collapse">
				<table id="terminis-taula"
						class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th><input type="checkbox" class="checkAll" checked="checked" data-sort="false"></th>
							<th><spring:message code="comuns.codi"/></th>
							<th><spring:message code="comuns.nom"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${terminis}" var="termini" varStatus="procesosStatus">
						<tr class="row_checkbox">
							<td>
								<input type="checkbox" class="check" id="terminis_${termini.codi }" name="terminis" value="${termini.codi}" <c:if test="${inici or fn:contains(command.terminis, termini.codi)}">checked="checked"</c:if>/>
							</td>
							<td>${termini.codi}</td>
							<td>${termini.nom}</td>
						</tr>
						</c:forEach>			
					</tbody>
				</table>
			</div>
		</div>

		<div id="accions" class="agrupacio">
			<div class="panel-heading clicable" data-toggle="collapse" data-target="#panel_accions">
				<span class="marcador tots fa fa-check-square-o"></span>
				<span class="marcador algun fa fa-check-square-o text-muted" style="display: none;"></span>
				<span class="marcador cap fa fa-square-o" style="display: none;"></span>
				<spring:message code="definicio.proces.exportar.form.accions"></spring:message>
				<span class="marcats">0</span>/<span class="total">0</span>
				<div class="pull-right">
					<span class="fa fa-chevron-down"></span>
					<span class="fa fa-chevron-up" style="display: none;"></span>
				</div>
			</div>
			<div id="panel_accions" class="taula panel-body collapse">
				<table id="accions-taula"
						class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th><input type="checkbox" class="checkAll" checked="checked" data-sort="false"></th>
							<th><spring:message code="comuns.codi"/></th>
							<th><spring:message code="comuns.nom"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${accions}" var="accio" varStatus="procesosStatus">
						<tr class="row_checkbox">
							<td>
								<input type="checkbox" class="check" id="accions_${accio.codi }" name="accions" value="${accio.codi}" <c:if test="${inici or fn:contains(command.accions, accio.codi)}">checked="checked"</c:if>/>
							</td>
							<td>${accio.codi}</td>
							<td>${accio.nom}</td>
						</tr>
						</c:forEach>			
					</tbody>
				</table>
			</div>
		</div>

	</c:if>
		