<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="anys" required="true" rtexprvalue="true"%>
<%@ attribute name="mesos" required="true" rtexprvalue="true"%>
<%@ attribute name="dies" required="true" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<c:set var="campPathAnys" value="${anys}"/>
<c:set var="campPathMesos" value="${mesos}"/>
<c:set var="campPathDies" value="${dies}"/>
<c:set var="campErrorsAnys"><form:errors path="${campPathAnys}"/></c:set>
<c:set var="campErrorsMesos"><form:errors path="${campPathMesos}"/></c:set>
<c:set var="campErrorsDies"><form:errors path="${campPathDies}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose></c:set>
<c:set var="campClassRequired"><c:if test="${required}">obligatori</c:if></c:set>
<c:choose>
	<c:when test="${not inline}">
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<label class="control-label col-xs-4 ${campClassRequired}" for="${campPath}">${campLabelText}</label>
			<div class="col-xs-8">
				<form:input path="${campPath}" cssClass="form-control" id="${campPath}" disabled="${disabled}"/>
				<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
    		<label class="sr-only ${campClassRequired}" for="${campPath}">${campLabelText}</label>
    		<form:input path="${campPath}" cssClass="form-control" id="${campPath}" placeholder="${campPlaceholder}" disabled="${disabled}"/>
  		</div>
	</c:otherwise>
</c:choose>


<div id="durada">
			<c:import url="../common/formElement.jsp">
				<c:param name="property">dies</c:param>
				<c:param name="type" value="custom"/>
				<c:param name="label"><fmt:message key='defproc.termform.durada' /></c:param>
				<c:param name="content">
					<ul class="alternate alt_termini">
						<spring:bind path="anys">
							<li>
								<label for="anys" class="blockLabel">
									<span><fmt:message key='defproc.termform.anys' /></span>
									<select id="anys" name="anys" style="min-width:36px;">
										<c:forEach var="index" begin="0" end="12">
											<option value="${index}"<c:if test="${status.value==index}"> selected="selected"</c:if>>${index}</option>
										</c:forEach>
									</select>
								</label>
							</li>
						</spring:bind>
						<spring:bind path="mesos">
							<li>
								<label for="mesos" class="blockLabel">
									<span><fmt:message key='defproc.termform.mesos' /></span>
									<select id="mesos" name="mesos" style="min-width:36px;">
										<c:forEach var="index" begin="0" end="12">
											<option value="${index}"<c:if test="${status.value==index}"> selected="selected"</c:if>>${index}</option>
										</c:forEach>
									</select>
								</label>
							</li>
						</spring:bind>
						<spring:bind path="dies">
							<li>
								<label for="dies" class="blockLabel">
									<span><fmt:message key='defproc.termform.dies' /></span>
									<input id="dies" name="dies" value="${status.value}" class="textInput" style="min-width:36px;"/>
								</label>
							</li>
						</spring:bind>
					</ul>
				</c:param>
			</c:import>
			</div>
			<script type="text/javascript">canviPredef(document.getElementById('duradaPredefinida0'));</script>