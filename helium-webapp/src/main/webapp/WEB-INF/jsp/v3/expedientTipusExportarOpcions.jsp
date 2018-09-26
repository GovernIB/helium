<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

		<c:if test="${command != null}">

		<input type="hidden" name="id" value="${command.id}" />
		<input type="hidden" name="entornId" value="${command.entornId}" />

		<c:set var="variablesErrors"><form:errors path="variables"/></c:set>
		<c:set var="documentsErrors"><form:errors path="documents"/></c:set>
		<c:set var="consultesErrors"><form:errors path="consultes"/></c:set>
		<c:set var="definicionsProcesErrors"><form:errors path="definicionsProces"/></c:set>

		<div id="estats" class="agrupacio">
			<div class="panel-heading clicable" data-toggle="collapse" data-target="#panel_estats">
				<span class="marcador tots fa fa-check-square-o"></span>
				<span class="marcador algun fa fa-check-square-o text-muted" style="display: none;"></span>
				<span class="marcador cap fa fa-square-o" style="display: none;"></span>
				<spring:message code="expedient.tipus.exportar.form.estats"></spring:message>
				<span class="marcats">0</span>/<span class="total">0</span>
				<div class="pull-right">
					<span class="fa fa-chevron-down"></span>
					<span class="fa fa-chevron-up" style="display: none;"></span>
				</div>
			</div>
			<div id="panel_estats" class="taula panel-body collapse">
				<table id="estats-taula"
						class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th><input type="checkbox" class="checkAll" checked="checked" data-sort="false" readonly="readonly"></th>
							<th><spring:message code="comuns.codi"/></th>
							<th><spring:message code="comuns.nom"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${estats}" var="estat" varStatus="procesosStatus">
						<tr class="row_checkbox">
							<td>
								<input type="checkbox" class="check" id="estats_${estat.codi }" name="estats" value="${estat.codi}" <c:if test="${inici or fn:contains(command.estats, estat.codi)}">checked="checked"</c:if> />
							</td>
							<td>${estat.codi}</td>
							<td>${estat.nom}</td>
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
				<spring:message code="expedient.tipus.exportar.form.variables"></spring:message>
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
							<th><spring:message code="expedient.tipus.exportar.form.variables.titol"/></th>
							<th><spring:message code="expedient.tipus.exportar.form.variables.tipus"/></th>
							<th><spring:message code="expedient.tipus.exportar.form.variables.agrupacio"/></th>
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
				<spring:message code="expedient.tipus.exportar.form.agrupacions"></spring:message>
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

		<div id="definicions" class="agrupacio">
			<div class="panel-heading clicable" data-toggle="collapse" data-target="#panel_definicions">
				<span class="marcador tots fa fa-check-square-o"></span>
				<span class="marcador algun fa fa-check-square-o text-muted" style="display: none;"></span>
				<span class="marcador cap fa fa-square-o" style="display: none;"></span>
				<spring:message code="expedient.tipus.exportar.form.definicions"></spring:message>
				<span class="marcats">0</span>/<span class="total">0</span>
				<div class="pull-right">
					<span class="fa fa-chevron-down"></span>
					<span class="fa fa-chevron-up" style="display: none;"></span>
				</div>
			</div>
			<div id="panel_definicions" class="taula panel-body collapse <c:if test="${not empty definicionsProcesErrors}"> has-error</c:if>">
				<c:if test="${not empty definicionsProcesErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="definicionsProces"/></p></c:if>
				<table id="definicions-taula"
						class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th><input type="checkbox" class="checkAll" checked="checked" data-sort="false"></th>
							<th><spring:message code="comuns.nom"/></th>
							<th><spring:message code="expedient.tipus.exportar.form.definicions.versio"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${definicions}" var="definicio" varStatus="procesosStatus">
						<tr class="row_checkbox">
							<td>
								<input type="checkbox" class="check" id="definicions_${definicio.jbpmKey }" name="definicionsProces" value="${definicio.jbpmKey}" <c:if test="${inici or fn:contains(command.definicionsProces, definicio.jbpmKey)}">checked="checked"</c:if>/>
							</td>
							<td>${definicio.jbpmKey}</td>
							<td>
								<c:if test="${definicionsVersions != null}">
									<select class="form-control" name="definicionsVersions['${definicio.jbpmKey}']" <c:if test="${not inici and not fn:contains(command.definicionsProces, definicio.jbpmKey)}">disabled="disabled"</c:if>>
											<option value="${darreresVersions[definicio.jbpmKey]}">
												<spring:message code="expedient.tipus.definicioProces.incorporar.versio.placeholder"/>
											</option>
											<c:forEach var="versio" items="${definicionsVersions[definicio.jbpmKey]}">
												<option
												 	<c:if test="${command.definicionsVersions[definicio.jbpmKey] == versio}">selected</c:if>
													value="${versio}">${versio}</option>
											</c:forEach>
									</select>
								</c:if>
								<c:if test="${definicionsVersions == null}">
									${definicio.versio}
								</c:if>
							</td>
						</tr>
						</c:forEach>			
					</tbody>
				</table>
			</div>
		</div>
		
		<div id="integracions" class="agrupacio">
			<div class="panel-heading clicable" data-toggle="collapse" data-target="#panel_integracions">
				<spring:message code="expedient.tipus.exportar.form.integracions"></spring:message>
				<div class="pull-right">
					<span class="fa fa-chevron-down"></span>
					<span class="fa fa-chevron-up" style="display: none;"></span>
				</div>
			</div>
			<div id="panel_integracions" class="taula panel-body collapse">
				<hel:inputCheckbox name="integracioSistra" textKey="expedient.tipus.exportar.form.integracions.sistra" labelSize="6"/>
				<hel:inputCheckbox name="integracioForms" textKey="expedient.tipus.exportar.form.integracions.forms" labelSize="6"/>
			</div>
		</div>
		
		<div id="enumeracions" class="agrupacio">
			<div class="panel-heading clicable" data-toggle="collapse" data-target="#panel_enumeracions">
				<span class="marcador tots fa fa-check-square-o"></span>
				<span class="marcador algun fa fa-check-square-o text-muted" style="display: none;"></span>
				<span class="marcador cap fa fa-square-o" style="display: none;"></span>
				<spring:message code="expedient.tipus.exportar.form.enumeracions"></spring:message>
				<span class="marcats">0</span>/<span class="total">0</span>
				<div class="pull-right">
					<span class="fa fa-chevron-down"></span>
					<span class="fa fa-chevron-up" style="display: none;"></span>
				</div>
			</div>
			<div id="panel_enumeracions" class="taula panel-body collapse">
				<table id="enumeracions-taula"
						class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th><input type="checkbox" class="checkAll" checked="checked" data-sort="false"></th>
							<th><spring:message code="comuns.codi"/></th>
							<th><spring:message code="comuns.nom"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${enumeracions}" var="enumeracio" varStatus="procesosStatus">
						<tr class="row_checkbox">
							<td>
								<input type="checkbox" class="check" id="enumeracions_${enumeracio.codi }" name="enumeracions" value="${enumeracio.codi}" <c:if test="${inici or fn:contains(command.enumeracions, enumeracio.codi)}">checked="checked"</c:if>/>
							</td>
							<td>${enumeracio.codi}</td>
							<td>${enumeracio.nom}</td>
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
				<spring:message code="expedient.tipus.exportar.form.documents"></spring:message>
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
				<spring:message code="expedient.tipus.exportar.form.terminis"></spring:message>
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
				<spring:message code="expedient.tipus.exportar.form.accions"></spring:message>
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

		<div id="dominis" class="agrupacio">
			<div class="panel-heading clicable" data-toggle="collapse" data-target="#panel_dominis">
				<span class="marcador tots fa fa-check-square-o"></span>
				<span class="marcador algun fa fa-check-square-o text-muted" style="display: none;"></span>
				<span class="marcador cap fa fa-square-o" style="display: none;"></span>
				<spring:message code="expedient.tipus.exportar.form.dominis"></spring:message>
				<span class="marcats">0</span>/<span class="total">0</span>
				<div class="pull-right">
					<span class="fa fa-chevron-down"></span>
					<span class="fa fa-chevron-up" style="display: none;"></span>
				</div>
			</div>
			<div id="panel_dominis" class="taula panel-body collapse">
				<table id="dominis-taula"
						class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th><input type="checkbox" class="checkAll" checked="checked" data-sort="false"></th>
							<th><spring:message code="comuns.codi"/></th>
							<th><spring:message code="comuns.nom"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${dominis}" var="domini" varStatus="procesosStatus">
						<tr class="row_checkbox">
							<td>
								<input type="checkbox" class="check" id="dominis_${domini.codi }" name="dominis" value="${domini.codi}" <c:if test="${inici or fn:contains(command.dominis, domini.codi)}">checked="checked"</c:if>/>
							</td>
							<td>${domini.codi}</td>
							<td>${domini.nom}</td>
						</tr>
						</c:forEach>			
					</tbody>
				</table>
			</div>
		</div>

		<div id="consultes" class="agrupacio">
			<div class="panel-heading clicable" data-toggle="collapse" data-target="#panel_consultes">
				<span class="marcador tots fa fa-check-square-o"></span>
				<span class="marcador algun fa fa-check-square-o text-muted" style="display: none;"></span>
				<span class="marcador cap fa fa-square-o" style="display: none;"></span>
				<spring:message code="expedient.tipus.exportar.form.consultes"></spring:message>
				<span class="marcats">0</span>/<span class="total">0</span>
				<div class="pull-right">
					<span class="fa fa-chevron-down"></span>
					<span class="fa fa-chevron-up" style="display: none;"></span>
				</div>
			</div>
			<div id="panel_consultes" class="taula panel-body collapse <c:if test="${not empty consultesErrors}"> has-error</c:if>">
				<c:if test="${not empty consultesErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="consultes"/></p></c:if>
				<table id="consultes-taula"
						class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th><input type="checkbox" class="checkAll" checked="checked" data-sort="false"></th>
							<th><spring:message code="comuns.codi"/></th>
							<th><spring:message code="comuns.nom"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${consultes}" var="consulta" varStatus="procesosStatus">
						<tr class="row_checkbox">
							<td>
								<input type="checkbox" class="check" id="consultes_${consulta.codi }" name="consultes" value="${consulta.codi}" <c:if test="${inici or fn:contains(command.consultes, consulta.codi)}">checked="checked"</c:if>/>
							</td>
							<td>${consulta.codi}</td>
							<td>${consulta.nom}</td>
						</tr>
						</c:forEach>			
					</tbody>
				</table>
			</div>
		</div>

		<!-- Herència de tipus d'expedients -->		
		<c:if test="${not empty command.expedientTipusPare}">
		<div id="tasques" class="agrupacio">
			<div class="panel-heading clicable" data-toggle="collapse" data-target="#panel_tasques">
				<spring:message code="expedient.tipus.exportar.form.herencia" arguments="${command.expedientTipusPare}"></spring:message>
				<div class="pull-right">
					<span class="fa fa-chevron-down"></span>
					<span class="fa fa-chevron-up" style="display: none;"></span>
				</div>
			</div>
			<div id="panel_tasques" class="taula panel-body collapse">
				<hel:inputCheckbox name="tasquesHerencia" textKey="expedient.tipus.exportar.form.herencia.tasques" labelSize="6"/>
			</div>
		</div>
		</c:if>
		

	</c:if>
		