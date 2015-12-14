<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ attribute name="id" required="true"%>
<%@ attribute name="arbre" required="true" type="java.lang.Object"%>
<%@ attribute name="atributId" required="true"%>
<%@ attribute name="atributNom" required="true"%>
<%@ attribute name="seleccionatId"%>
<%@ attribute name="changedCallback"%>
<%@ attribute name="fulles" type="java.lang.Object"%>
<%@ attribute name="fullesAtributId"%>
<%@ attribute name="fullesAtributNom"%>
<%@ attribute name="fullesAtributPare"%>
<%@ attribute name="fullesIcona"%>
<%@ attribute name="isArbreSeleccionable" type="java.lang.Boolean"%>
<%@ attribute name="isFullesSeleccionable" type="java.lang.Boolean"%>
<%@ attribute name="isOcultarCounts" type="java.lang.Boolean"%>
<%@ attribute name="isError" type="java.lang.Boolean"%>
<c:if test="${empty isArbreSeleccionable and empty isFullesSeleccionable}"><c:set var="isArbreSeleccionable" value="${true}"/><c:set var="isFullesSeleccionable" value="${true}"/></c:if>
<c:if test="${empty isOcultarCounts}"><c:set var="isOcultarCounts" value="${false}"/></c:if>
<c:if test="${empty isError}"><c:set var="isError" value="${false}"/></c:if>
<div id="${id}" class="well" style="overflow: auto;<c:if test="${isError}">margin-bottom:10px; border-color: #A94442</c:if>">
	<c:if test="${not empty arbre and not empty arbre.arrel}">
		<c:set var="arrel" value="${arbre.arrel}"/>
		<ul>
			<li id="${arbre.arrel.dades[atributId]}" class="jstree-open" data-jstree='{"icon":"fa fa-home fa-lg"<c:if test="${not empty seleccionatId and arbre.arrel.dades[atributId] == seleccionatId}">, "selected": true</c:if>}'>
				${arbre.arrel.dades[atributNom]}
				<hel:arbreFills pare="${arbre.arrel}" fills="${arbre.arrel.fills}" atributId="${atributId}" atributNom="${atributNom}" seleccionatId="${seleccionatId}" fulles="${fulles}" fullesIcona="${fullesIcona}" fullesAtributId="${fullesAtributId}" fullesAtributNom="${fullesAtributNom}" fullesAtributPare="${fullesAtributPare}" isOcultarCounts="${isOcultarCounts}"/>
			</li>
		</ul>
	</c:if>
</div>
<script>
	(function ($) {
		$.jstree.defaults.conditionalselect = function () { return true; };
		$.jstree.defaults.conditionalhover = function () { return true; };
		$.jstree.plugins.conditionalselect = function (options, parent) {
			this.select_node = function (obj, supress_event, prevent_open) {
				if (this.settings.conditionalselect.call(this, this.get_node(obj))) {
					parent.select_node.call(this, obj, supress_event, prevent_open);
				} else {
					parent.deselect_all.call(this, obj, supress_event, prevent_open);
				}
			};
		};
		$.jstree.plugins.conditionalhover = function (options, parent) {
			this.hover_node = function (obj, supress_event, prevent_open) {
				if (this.settings.conditionalhover.call(this, this.get_node(obj))) {
					parent.hover_node.call(this, obj, supress_event, prevent_open);
				}
			};
		};
	})(jQuery);
	$('#${id}').jstree({
		"conditionalselect": function(node) {
			<c:choose>
				<c:when test="${isArbreSeleccionable and isFullesSeleccionable}">return true;</c:when>
				<c:when test="${not isArbreSeleccionable and isFullesSeleccionable}">return node.icon.indexOf('${fullesIcona}') != -1;</c:when>
				<c:when test="${isArbreSeleccionable and not isFullesSeleccionable}">return node.icon.indexOf('${fullesIcona}') == -1;</c:when>
				<c:when test="${not isArbreSeleccionable and not isFullesSeleccionable}">return false;</c:when>
			</c:choose>
		},
		"conditionalhover": function(node) {
			<c:choose>
				<c:when test="${isArbreSeleccionable and isFullesSeleccionable}">return true;</c:when>
				<c:when test="${not isArbreSeleccionable and isFullesSeleccionable}">return node.icon.indexOf('${fullesIcona}') != -1;</c:when>
				<c:when test="${isArbreSeleccionable and not isFullesSeleccionable}">return node.icon.indexOf('${fullesIcona}') == -1;</c:when>
				<c:when test="${not isArbreSeleccionable and not isFullesSeleccionable}">return false;</c:when>
			</c:choose>
		},
		"plugins": ["conditionalselect", "conditionalhover"]
	})
	.on('after_open.jstree', function (e, data) {
		var iframe = $('.modal-body iframe', window.parent.document);
		var height = $('html').height();
		iframe.height(height + 'px');
	})
	.on('after_close.jstree', function (e, data) {
		var iframe = $('.modal-body iframe', window.parent.document);
		var height = $('html').height();
		iframe.height(height + 'px');
	})<c:if test="${not empty changedCallback}">
	.on('changed.jstree', function (e, data) {
		return ${changedCallback}(e, data);
	})
	.on('deselect_all.jstree', function (e, data) {
		return ${changedCallback}(e, data);
	})</c:if>;	
</script>