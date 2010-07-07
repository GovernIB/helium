<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<c:if test="${not empty campTascaActual}">
	<c:set var="campActual" value="${campTascaActual.camp}" scope="request"/>
	<c:set var="readOnly" value="${campTascaActual.readOnly}" scope="request"/>
	<c:set var="required" value="${campTascaActual.required}" scope="request"/>
</c:if>
<c:if test="${not empty campRegistreActual}">
	<c:set var="campActual" value="${campRegistreActual.membre}" scope="request"/>
	<c:set var="readOnly" value="${false}" scope="request"/>
	<c:set var="required" value="${campRegistreActual.obligatori}" scope="request"/>
</c:if>
<c:set var="codiActual" value="${campActual.codi}" scope="request"/>
<c:set var="valorActual" value="${command[codiActual]}" scope="request"/>
<c:set var="valorDominiActual" value="${tasca.valorsDomini[codiActual]}" scope="request"/>
<c:set var="valorTextActual" value="${tasca.varsComText[codiActual]}" scope="request"/>
<c:set var="extraParams">
	<c:choose>
		<c:when test="${not empty tasca.id}">taskId:${tasca.id},processInstanceId:${tasca.processInstanceId},definicioProcesId:${tasca.definicioProces.id},campCodi:'${codiActual}',valors:function(){return canvisSelectValorsAddicionals}</c:when>
		<c:otherwise>processInstanceId:${instanciaProces.id},definicioProcesId:${instanciaProces.definicioProces.id},campCodi:'${codiActual}'</c:otherwise>
	</c:choose>
</c:set>
<c:choose>
	<c:when test="${tasca.validada or readOnly}">
		<c:choose>
			<c:when test="${campActual.tipus == 'REGISTRE'}">
				<c:import url="../common/formElement.jsp">
					<c:param name="property">${codiActual}</c:param>
					<c:param name="required">${required}</c:param>
					<c:param name="type" value="custom"/>
					<c:param name="label">${campActual.etiqueta}</c:param>
					<c:param name="content">
						<c:set var="files" scope="request" value="${tasca.varsComText[codiActual]}"/>
						<c:if test="${not empty tasca.varsComText[codiActual]}">
							<display:table name="files" id="registre" requestURI="" class="displaytag selectable">
								<c:forEach var="membre" items="${campActual.registreMembres}" varStatus="varStatus">
									<c:if test="${membre.llistar}">
										<display:column title="${membre.membre.etiqueta}">${registre[varStatus.index]}</display:column>
									</c:if>
								</c:forEach>
							</display:table>
						</c:if>
					</c:param>
				</c:import>
			</c:when>
			<c:otherwise>
				<c:import url="../common/formElement.jsp">
					<c:param name="property">${codiActual}</c:param>
					<c:param name="required">${required}</c:param>
					<c:param name="type">static</c:param>
					<c:param name="label">${campActual.etiqueta}</c:param>
					<c:param name="staticText">${valorTextActual}&nbsp;</c:param>
				</c:import>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:when test="${campActual.tipus == 'STRING'}">
		<c:import url="../common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="required">${required}</c:param>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="comment">${campActual.observacions}</c:param>
			<c:param name="iterateOn"><c:if test="${campActual.multiple}">valorActual</c:if></c:param>
			<c:param name="multipleIcons"><c:if test="${campActual.multiple}">true</c:if></c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'INTEGER'}">
		<c:import url="../common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="required">${required}</c:param>
			<c:param name="type" value="number"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="comment">${campActual.observacions}</c:param>
			<c:param name="keyfilter">/[\d\-]/</c:param>
			<c:param name="iterateOn"><c:if test="${campActual.multiple}">valorActual</c:if></c:param>
			<c:param name="multipleIcons"><c:if test="${campActual.multiple}">true</c:if></c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'FLOAT'}">
		<c:import url="../common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="required">${required}</c:param>
			<c:param name="type" value="number"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="comment">${campActual.observacions}</c:param>
			<c:param name="keyfilter">/[\d\-\.]/</c:param>
			<c:param name="iterateOn"><c:if test="${campActual.multiple}">valorActual</c:if></c:param>
			<c:param name="multipleIcons"><c:if test="${campActual.multiple}">true</c:if></c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'DATE'}">
		<c:import url="../common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="required">${required}</c:param>
			<c:param name="type" value="date"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="comment">${campActual.observacions}</c:param>
			<c:param name="mask">{mask:'39/19/9999',autoTab:false}</c:param>
			<c:param name="iterateOn"><c:if test="${campActual.multiple}">valorActual</c:if></c:param>
			<c:param name="multipleIcons"><c:if test="${campActual.multiple}">true</c:if></c:param>
			<c:param name="includeCalendar" value="${empty param.iframe}"/>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'PRICE'}">
		<c:import url="../common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="required">${required}</c:param>
			<c:param name="type" value="number"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="comment">${campActual.observacions}</c:param>
			<c:param name="mask">{mask:'99,999.999.999.999',type:'reverse'}</c:param>
			<c:param name="iterateOn"><c:if test="${campActual.multiple}">valorActual</c:if></c:param>
			<c:param name="multipleIcons"><c:if test="${campActual.multiple}">true</c:if></c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'BOOLEAN'}">
		<c:import url="../common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="required">${required}</c:param>
			<c:param name="type" value="checkbox"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="comment">${campActual.observacions}</c:param>
			<c:param name="iterateOn"><c:if test="${campActual.multiple}">valorActual</c:if></c:param>
			<c:param name="multipleIcons"><c:if test="${campActual.multiple}">true</c:if></c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'TEXTAREA'}">
		<c:import url="../common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="required">${required}</c:param>
			<c:param name="type" value="textarea"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="comment">${campActual.observacions}</c:param>
			<c:param name="iterateOn"><c:if test="${campActual.multiple}">valorActual</c:if></c:param>
			<c:param name="multipleIcons"><c:if test="${campActual.multiple}">true</c:if></c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'SELECCIO'}">
		<c:import url="../common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="required">${required}</c:param>
			<c:param name="type" value="select"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="comment">${campActual.observacions}</c:param>
			<c:param name="selectUrl"><c:url value="/domini/consultaExpedient.html"/></c:param>
			<c:param name="selectExtraParams">${extraParams},tipus:'select'</c:param>
			<c:param name="iterateOn"><c:if test="${campActual.multiple}">valorActual</c:if></c:param>
			<c:param name="multipleIcons"><c:if test="${campActual.multiple}">true</c:if></c:param>
			<c:param name="onchange">canviSelectTasca(this.id, this.name);</c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'SUGGEST'}">
		<c:set var="multipleSuggestText" value="${valorsPerSuggest[codiActual]}" scope="request"/>
		<c:import url="../common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="required">${required}</c:param>
			<c:param name="type" value="suggest"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="comment">${campActual.observacions}</c:param>
			<c:param name="suggestUrl"><c:url value="/domini/consultaExpedient.html"/></c:param>
			<c:param name="suggestExtraParams">${extraParams},tipus:'suggest'</c:param>
			<c:param name="suggestText"><c:if test="${not empty tasca.valorsDomini[codiActual]}">${tasca.valorsDomini[codiActual].valor}</c:if></c:param>
			<c:param name="iterateOn"><c:if test="${campActual.multiple}">valorActual</c:if></c:param>
			<c:param name="multipleIcons"><c:if test="${campActual.multiple}">true</c:if></c:param>
			<c:param name="multipleSuggestText">multipleSuggestText</c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'TERMINI'}">
		<c:import url="../common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="required">${required}</c:param>
			<c:param name="type" value="custom"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="content">
				<spring:bind path="${codiActual}">
					<c:set var="anys" value="${fn:split(status.value,'/')[0]}"/>
					<label for="${codiActual}_anys" class="blockLabel">
						<%--span>Anys</span--%>
						<select id="${codiActual}_anys" name="${codiActual}_anys" onchange="canviTermini(this)">
							<c:forEach var="index" begin="0" end="12">
								<option value="${index}"<c:if test="${anys==index}"> selected="selected"</c:if>>${index}</option>
							</c:forEach>
						</select>
					</label>
					<c:set var="mesos" value="${fn:split(status.value,'/')[1]}"/>
					<label for="${codiActual}_mesos" class="blockLabel">
						<%--span>Mesos</span--%>
						<select id="${codiActual}_mesos" name="${codiActual}_mesos" onchange="canviTermini(this)">
							<c:forEach var="index" begin="0" end="12">
								<option value="${index}"<c:if test="${mesos==index}"> selected="selected"</c:if>>${index}</option>
							</c:forEach>
						</select>
					</label>
					<c:set var="dies" value="${fn:split(status.value,'/')[2]}"/>
					<label for="${codiActual}_dies" class="blockLabel">
						<%--span>Dies</span--%>
						<input id="${codiActual}_dies" name="${codiActual}_dies" value="${dies}" class="textInput" onchange="canviTermini(this)"/>
					</label>
					<form:hidden path="${codiActual}"/>
				</spring:bind>
			</c:param>
			<c:param name="comment">${campActual.observacions}</c:param>
			<c:param name="iterateOn"><c:if test="${campActual.multiple}">valorActual</c:if></c:param>
			<c:param name="multipleIcons"><c:if test="${campActual.multiple}">true</c:if></c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'REGISTRE'}">
		<c:import url="../common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="required">${required}</c:param>
			<c:param name="type" value="custom"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="content">
				<c:set var="files" scope="request" value="${tasca.varsComText[codiActual]}"/>
				<c:if test="${not empty tasca.varsComText[codiActual]}">
					<display:table name="files" id="registre" requestURI="" class="displaytag selectable">
						<c:forEach var="membre" items="${campActual.registreMembres}" varStatus="varStatus">
							<c:if test="${membre.llistar}">
								<display:column title="${membre.membre.etiqueta}">
									<c:if test="${varStatus.first}"><a href="#" onclick="return editarRegistre(${campActual.id}, '${codiActual}', '${campActual.etiqueta}', ${fn:length(campActual.registreMembres)}, ${registre_rowNum - 1})"></a></c:if>
									${registre[varStatus.index]}
								</display:column>
							</c:if>
						</c:forEach>
						<display:column style="width:16px">
							<a href="#" onclick="return esborrarRegistre(event, ${campActual.id}, ${registre_rowNum - 1})"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
						</display:column>
					</display:table>
					<script type="text/javascript">initSelectable();</script>
				</c:if>
				<c:if test="${campActual.multiple || fn:length(files) < 1}">
					<form action="#">
						<button style="font-size:11px;margin-top: 2px" type="submit" class="submitButton" onclick="return editarRegistre(${campActual.id}, '${codiActual}', '${campActual.etiqueta}', ${fn:length(campActual.registreMembres)})">Afegir</button>
					</form>
				</c:if>
				<div style="clear:both"></div>
			</c:param>
			<c:param name="comment">${campActual.observacions}</c:param>
		</c:import>
	</c:when>
	<c:when test="${campActual.tipus == 'ACCIO'}">
		<c:import url="../common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="required">${required}</c:param>
			<c:param name="type" value="custom"/>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="content">
				<button type="button" onclick="clickExecutarAccio('${campActual.jbpmAction}')">Executar</button>
			</c:param>
			<c:param name="comment">${campActual.observacions}</c:param>
			<c:param name="iterateOn"><c:if test="${campActual.multiple}">valorActual</c:if></c:param>
			<c:param name="multipleIcons"><c:if test="${campActual.multiple}">true</c:if></c:param>
		</c:import>
	</c:when>
	<c:otherwise>
		<c:import url="../common/formElement.jsp">
			<c:param name="property">${codiActual}</c:param>
			<c:param name="required">${required}</c:param>
			<c:param name="label">${campActual.etiqueta}</c:param>
			<c:param name="comment">${campActual.observacions}</c:param>
			<c:param name="iterateOn"><c:if test="${campActual.multiple}">valorActual</c:if></c:param>
			<c:param name="multipleIcons"><c:if test="${campActual.multiple}">true</c:if></c:param>
		</c:import>
	</c:otherwise>
</c:choose>
