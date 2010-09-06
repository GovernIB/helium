<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%--

	Paràmetres dels camps:
		- property
		- type
		- required
		- label
		- labelKey
		- labelMulti
		- comment
		- iterateOn

		- items
		- itemLabel
		- itemValue
		- itemBuit

		- selectUrl
		- selectExtraParams

		- checkAsText

		- suggestUrl
		- suggestText
		- suggestExtraParams

		- fileUrl
		- fileExists

		- onclick
		- onchange

		- mask
		- keyfilter

		- multipleAddUrl
		- multipleRemoveUrl

--%>
<c:if test="${not empty param.property}"><c:set var="errorsCamp"><form:errors path="${param.property}"/></c:set></c:if>
<c:set var="required" value="${false}"/>
<c:if test="${param.required == 'true'}"><c:set var="required" value="${true}"/></c:if>
<c:set var="hiHaPerIterar" value="${not empty param.iterateOn and fn:length(requestScope[param.iterateOn]) gt 0}"/>
<c:choose>
	<c:when test="${hiHaPerIterar}"><c:set var="repeticions" value="${fn:length(requestScope[param.iterateOn]) - 1}"/></c:when>
	<c:otherwise><c:set var="repeticions" value="${0}"/></c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${param.type != 'buttons'}">
		<c:forEach begin="${0}" end="${repeticions}" varStatus="varStatus">
		<c:set var="inputId" value="${param.property}${varStatus.index}"/>
		<c:set var="inputName" value="${param.property}"/>
		<c:choose>
			<c:when test="${hiHaPerIterar}"><c:set var="bindPath" value="${param.property}[${varStatus.index}]"/></c:when>
			<c:otherwise><c:set var="bindPath" value="${param.property}"/></c:otherwise>
		</c:choose>
		<spring:bind path="${bindPath}">
			<c:choose>
				<c:when test="${param.type == 'hidden'}">
					<input id="${inputId}" name="${inputName}" value="${status.value}" type="hidden"/>
				</c:when>
				<c:otherwise>
					<div class="ctrlHolder<c:if test="${not empty errorsCamp}"> error</c:if>">
						<c:if test="${not empty errorsCamp}"><p class="errorField"><strong>${errorsCamp}</strong></p></c:if>
						<c:set var="labelText"><c:if test="${required}"><em><img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/></em></c:if><c:choose><c:when test="${not empty param.labelKey}"><fmt:message key="${param.labelKey}"/></c:when><c:when test="${not empty param.label}">${param.label}</c:when></c:choose></c:set>
						<c:if test="${varStatus.index gt 0}"><c:set var="labelText" value=""/></c:if>
						<c:choose>
							<c:when test="${param.inputOnly == 'true'}"></c:when>
							<c:when test="${param.type == 'multicheck' || param.type == 'custom'}">
								<p class="label">${labelText}</p>
							</c:when>
							<c:otherwise>
								<label for="${inputId}">${labelText}</label>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${param.type == 'static'}">
								<span class="staticField"><c:choose><c:when test="${not empty param.staticText}">${param.staticText}</c:when><c:otherwise>${status.value}</c:otherwise></c:choose></span>
								<input id="${inputId}" name="${inputName}" value="${status.value}" type="hidden"/>
							</c:when>
							<c:when test="${param.type == 'password'}">
								<input id="${inputId}" name="${inputName}" value="" type="password" class="textInput" onclick="${param.onclick}" onchange="${param.onchange}"<c:if test="${not empty param.disabled}"> disabled="disabled"</c:if>/>
							</c:when>
							<c:when test="${param.type == 'number'}">
								<input id="${inputId}" name="${inputName}" value="${status.value}" type="text" class="textInput" onclick="${param.onclick}" onchange="${param.onchange}"<c:if test="${not empty param.disabled}"> disabled="disabled"</c:if> style="text-align:right"/>
							</c:when>
							<c:when test="${param.type == 'checkbox'}">
								<input type="hidden" name="_${status.expression}" value="on"/>
								<c:choose>
									<c:when test="${not empty param.checkAsText}"><input id="${inputId}" name="${inputName}"<c:if test="${status.value == 'on'}">checked="checked"</c:if> type="checkbox" onclick="${param.onclick}" onchange="${param.onchange}"<c:if test="${not empty param.disabled}"> disabled="disabled"</c:if>/></c:when>
									<c:otherwise><input id="${inputId}" name="${inputName}"<c:if test="${status.value}">checked="checked"</c:if> type="checkbox" onclick="${param.onclick}" onchange="${param.onchange}"<c:if test="${not empty param.disabled}"> disabled="disabled"</c:if>/></c:otherwise>
								</c:choose>
							</c:when>
							<c:when test="${param.type == 'textarea'}">
								<textarea id="${inputId}" name="${inputName}" onclick="${param.onclick}" onchange="${param.onchange}"<c:if test="${not empty param.disabled}"> disabled="disabled"</c:if>>${status.value}</textarea>
							</c:when>
							<c:when test="${param.type == 'date'}">
								<input id="${inputId}" name="${inputName}" value="${status.value}" type="text" class="textInput" onclick="${param.onclick}" onchange="${param.onchange}"<c:if test="${not empty param.disabled}"> disabled="disabled"</c:if>/>
								<c:if test="${empty param.includeCalendar or param.includeCalendar}">
									<script type="text/javascript">
										// <![CDATA[
										$(function() {
											$.datepicker.setDefaults($.extend({
												dateFormat: 'dd/mm/yy',
												changeMonth: true,
												changeYear: true
											}));
											$("#${inputId}").datepicker(/*{firstDay: 1, minDate: new Date(2010, 1 - 1, 1)}*/);
										});
										// ]]>
									</script>
								</c:if>
							</c:when>
							<c:when test="${param.type == 'select'}">
								<c:set var="items" value="${requestScope[param.items]}"/>
								<select id="${inputId}" name="${inputName}" onclick="${param.onclick}" onchange="${param.onchange}"<c:if test="${not empty param.disabled}"> disabled="disabled"</c:if>>
									<c:if test="${not empty param.itemBuit}"><option value="">${param.itemBuit}</option></c:if>
									<c:forEach var="item" items="${items}">
										<c:choose>
											<c:when test="${not empty param.itemLabel && not empty param.itemValue}">
												<option value="${item[param.itemValue]}"<c:if test="${item[param.itemValue]==status.value}"> selected="selected"</c:if>>${item[param.itemLabel]}</option>
											</c:when>
											<c:otherwise>
												<option value="${item}"<c:if test="${item==status.value}"> selected="selected"</c:if>>${item}</option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</select>
								<c:if test="${not empty param.selectUrl}">
									<script type="text/javascript">
										// <![CDATA[
										function initSelect_${inputId}() {
											initSelect(
													"${inputId}",
													"${status.value}",
													"${param.selectUrl}",
													<c:choose><c:when test="${empty param.selectExtraParams}">null</c:when><c:otherwise>{${param.selectExtraParams}}</c:otherwise></c:choose>);
										}
										initSelect_${inputId}();
										// ]]>
									</script>
								</c:if>
							</c:when>
							<c:when test="${param.type == 'custom'}">
								<div class="multiField<c:if test="${not empty param.customClass}"> ${param.customClass}</c:if>">${param.content}</div>
							</c:when>
							<c:when test="${param.type == 'multicheck'}">
								<c:set var="items" value="${requestScope[param.items]}"/>
								<div class="multiField">
									<c:if test="${not empty param.itemBuit}"><label for="${inputId}0" class="inlineLabel"><input type="checkbox" id="${inputId}" name="${inputName}" value="" onclick="${param.onclick}" onchange="${param.onchange}"<c:if test="${not empty param.disabled}"> disabled="disabled"</c:if>/>${param.itemBuit}</label></c:if>
									<c:forEach var="item" items="${items}" varStatus="mcStatus">
										<c:set var="found" value="${false}"/>
										<c:forEach var="value" items="${status.value}"><c:if test="${value[param.itemValue] == item[param.itemValue]}"><c:set var="found" value="${true}"/></c:if></c:forEach>
										<label for="${inputId}${mcStatus.index}" class="inlineLabel"><input type="checkbox" id="${inputId}${mcStatus.index}" name="${inputName}" value="${item[param.itemValue]}"<c:if test="${found}"> checked="checked"</c:if> onclick="${param.onclick}" onchange="${param.onchange}"/>${item[param.itemLabel]}</label>
									</c:forEach>
								</div>
							</c:when>
							<c:when test="${param.type == 'file'}">
								<c:set var="hiHaArxiu" value="${param.fileExists=='true'}"/>
								<script type="text/javascript">
								// <![CDATA[
								function mostrarAmagarFile_${inputId}() {
									var el = document.getElementById('fileInput_${inputId}');
									if (el.style.display == '') {
										el.style.display = 'none';
									} else {
										el.style.display = '';
										document.getElementById('${inputId}_deleted').value = 'deleted';
									}
									el = document.getElementById('iconsFileInput_${inputId}');
									if (el.style.display == '')
										el.style.display = 'none';
									else
										el.style.display = '';
									return false;
								}
								// ]]>
								</script>
								<div id="iconsFileInput_${inputId}" class="iconsFileInput"<c:if test="${not hiHaArxiu}"> style="display:none"</c:if>>
									<a href="${param.fileUrl}"><img src="<c:url value="/img/page_white_put.png"/>" alt="Descarregar" title="Descarregar" border="0"/></a>
									<a href="#" onclick="return mostrarAmagarFile_${inputId}()"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
								</div>
								<div id="fileInput_${inputId}"<c:if test="${hiHaArxiu}"> style="display:none"</c:if>>
									<input id="${inputId}" name="${inputName}" type="file" class="fileUpload" onclick="${param.onclick}" onchange="${param.onchange}"<c:if test="${not empty param.disabled}"> disabled="disabled"</c:if>/>
									<input id="${inputId}_deleted" name="${inputName}_deleted" type="hidden"/>
								</div>
							</c:when>
							<c:when test="${param.type == 'suggest'}">
								<c:set var="suggestText" value="${param.suggestText}"/>
								<c:if test="${not empty param.iterateOn}"><c:set var="suggestText" value="${requestScope[param.multipleSuggestText][varStatus.index]}"/></c:if>
								<input id="${inputId}" name="${inputName}" value="${status.value}" type="hidden"/>
								<input name="suggest_${inputId}" id="suggest_${inputId}" type="text" class="textInput"<c:if test="${not empty suggestText}"> value="${suggestText}"</c:if><c:if test="${not empty param.disabled}"> disabled="disabled"</c:if>/>
								<img id="suggest_${inputId}_info" src="<c:url value="/img/page_white_magnify.png"/>" title="Suggest" alt="Suggest" class="suggestImgInfo"/>
								<img id="suggest_${inputId}_delete" src="<c:url value="/img/page_white_delete.png"/>" title="Suggest" alt="Suggest" class="suggestImgEsborrar"/>
								<script type="text/javascript">
									// <![CDATA[
									$(document).ready(function(){
										initSuggest(
												"${inputId}",
												"${param.suggestUrl}",
												findValue,
												<c:choose><c:when test="${empty param.suggestExtraParams}">null</c:when><c:otherwise>{${param.suggestExtraParams}}</c:otherwise></c:choose>)
									});
									// ]]>
								</script>
							</c:when>
							<c:otherwise>
								<input id="${inputId}" name="${inputName}" value="${status.value}" type="text" class="textInput" onclick="${param.onclick}" onchange="${param.onchange}"<c:if test="${not empty param.disabled}"> disabled="disabled"</c:if>/>
							</c:otherwise>
						</c:choose>
						<c:if test="${not empty param.mask || not empty param.keyfilter}">
							<script type="text/javascript">
								// <![CDATA[
								$(function() {
									<c:if test="${not empty param.mask}">$("#${inputId}").setMask(${param.mask});</c:if>
									<c:if test="${not empty param.keyfilter}">$("#${inputId}").keyfilter(${param.keyfilter});</c:if>
								});
								// ]]>
							</script>
						</c:if>
						<c:if test="${repeticions gt 0 and not empty param.multipleIcons}">
							<button type="submit" class="submitButton" name="submit" value="multipleRemove" onclick="multipleRemove(this, '${varStatus.index}', '${inputName}')">-</button>
						</c:if>
						<c:if test="${varStatus.last and not empty param.multipleIcons}">
							<button type="submit" class="submitButton" name="submit" value="multipleAdd" onclick="multipleAdd(this, '${inputName}')">+</button>
						</c:if>
						<c:if test="${not empty param.comment and varStatus.last}"><p class="formHint">${param.comment}</p></c:if>
					</div>
				</c:otherwise>
			</c:choose>
		</spring:bind>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<div class="buttonHolder">
			<c:set var="buttonValues" value="${fn:split(param.values,',')}"/>
			<c:set var="buttonTitles" value="${fn:split(param.titles,',')}"/>
			<c:forTokens var="values" items="${param.values}" delims="," varStatus="status">
				<c:choose>
					<c:when test="${value == 'reset'}"><button type="reset" class="resetButton" onclick="${param.onclick}">${buttonTitles[status.index]}</button></c:when>
					<c:otherwise><button type="submit" class="submitButton" name="submit" value="${buttonValues[status.index]}" onclick="saveAction(this, '${buttonValues[status.index]}');${param.onclick}">${buttonTitles[status.index]}</button></c:otherwise>
				</c:choose>
			</c:forTokens>
		</div>
	</c:otherwise>
</c:choose>
