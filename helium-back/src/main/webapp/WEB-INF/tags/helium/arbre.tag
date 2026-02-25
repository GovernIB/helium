<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ attribute name="id" required="true"%>
<%@ attribute name="arbre" required="true" type="java.lang.Object"%>
<%@ attribute name="atributId" required="true"%>
<%@ attribute name="atributNom" required="true"%>
<%@ attribute name="seleccionatId"%>
<%@ attribute name="changedCallback"%>
<%@ attribute name="readyCallback"%>
<%@ attribute name="deselectAllCallback"%>
<%@ attribute name="fillsAtributInfoCondition"%>
<%@ attribute name="fillsAtributInfoText"%>
<%@ attribute name="fulles" type="java.lang.Object"%>
<%@ attribute name="fullesAtributId"%>
<%@ attribute name="fullesAtributNom"%>
<%@ attribute name="fullesAtributPare"%>
<%@ attribute name="fullesIcona"%>
<%@ attribute name="fullesAtributInfo"%>
<%@ attribute name="fullesAtributInfoText"%>
<%@ attribute name="fullesAtributCssClassCondition"%> <!-- Name of the boolean attribute of the leaf, if not empty and true, the css class: fullesAtributCssClass will be applied -->
<%@ attribute name="fullesAtributInfo2Condition"%> <!-- Name of the boolean attribute of the leaf, if not empty and true, text specified by: fullesAtributInfo2Text will be displayed  -->
<%@ attribute name="fullesAtributInfo2Text"%> 
<%@ attribute name="isArbreSeleccionable" type="java.lang.Boolean"%>
<%@ attribute name="isFullesSeleccionable" type="java.lang.Boolean"%>
<%@ attribute name="isOcultarCounts" type="java.lang.Boolean"%>
<%@ attribute name="isError" type="java.lang.Boolean"%>
<%@ attribute name="height" required="false" rtexprvalue="true"%>
<%@ attribute name="isCheckBoxEnabled" type="java.lang.Boolean"%>
<%@ attribute name="isEnviarConeixementActiu" type="java.lang.Boolean"%>
<%@ attribute name="isFavoritsPermes" type="java.lang.Boolean"%>
<%@ attribute name="isMostrarPermisosBustiaPermes" type="java.lang.Boolean"%>
<c:if test="${empty isArbreSeleccionable and empty isFullesSeleccionable}"><c:set var="isArbreSeleccionable" value="${true}"/><c:set var="isFullesSeleccionable" value="${true}"/></c:if>
<c:if test="${empty isOcultarCounts}"><c:set var="isOcultarCounts" value="${false}"/></c:if>
<c:if test="${empty isError}"><c:set var="isError" value="${false}"/></c:if>
<c:if test="${empty isCheckBoxEnabled}"><c:set var="isCheckBoxEnabled" value="${false}"/></c:if>
<c:if test="${empty isEnviarConeixementActiu}"><c:set var="isEnviarConeixementActiu" value="${false}"/></c:if>
<c:if test="${empty isFavoritsPermes}"><c:set var="isFavoritsPermes" value="${false}"/></c:if>
<c:if test="${empty isMostrarPermisosBustiaPermes}"><c:set var="isMostrarPermisosBustiaPermes" value="${false}"/></c:if>
<div id="${id}" class="well" style="width: 100%; overflow: auto; <c:if test="${not empty height}">height: ${height}; </c:if><c:if test="${isError}">margin-bottom:10px; border-color: #A94442</c:if>">
	<c:if test="${not empty arbre and not empty arbre.arrel}">
		<c:set var="arrel" value="${arbre.arrel}"/>
		<ul>
			<li id="${arbre.arrel.dades[atributId]}" class="jstree-open" data-jstree='{"icon":"fa fa-home fa-lg"<c:if test="${not empty seleccionatId and arbre.arrel.dades[atributId] == seleccionatId}">, "selected": true</c:if>}'>
				<c:if test="${!empty fillsAtributInfoCondition && arbre.arrel.dades[fillsAtributInfoCondition]}">${fillsAtributInfoText}</c:if>
				<small>${arbre.arrel.dades[atributNom]}<c:if test="${not isOcultarCounts and arbre.arrel.mostrarCount}"> <span class="badge">${arbre.arrel.count}</span></c:if></small>
				<hel:arbreFills pare="${arbre.arrel}" fills="${arbre.arrel.fills}" atributId="${atributId}" atributNom="${atributNom}" seleccionatId="${seleccionatId}" fulles="${fulles}" 
				fullesIcona="${fullesIcona}" fullesAtributId="${fullesAtributId}" fullesAtributNom="${fullesAtributNom}" fullesAtributPare="${fullesAtributPare}" 
				fullesAtributInfo="${fullesAtributInfo}" fullesAtributInfoText="${fullesAtributInfoText}" isOcultarCounts="${isOcultarCounts}" fullesAtributCssClassCondition="${fullesAtributCssClassCondition}" 
				fillsAtributInfoCondition="${fillsAtributInfoCondition}" fillsAtributInfoText="${fillsAtributInfoText}"
				nivell="0"/>
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
					//changeCheckbox(true);
					//parent.deselect_all.call(this, obj, supress_event, prevent_open);
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
		"plugins": ["conditionalselect", "conditionalhover", "search", ${isCheckBoxEnabled} ? "checkbox" : "", "crrm", "sort"],
		"core": {
			"check_callback": true
		},
		"search" : {
			"case_insensitive": false
		},
		"checkbox": {
		    "three_state": false, //Indicating if checkboxes should cascade down and have an undetermined state
		    "two_state" : true,
			"cascade": "down"
		}
	})<c:if test="${not empty readyCallback}">
	.on('ready.jstree', function (e, data) {
		return ${readyCallback}(e, data);
	})</c:if>
	.on('after_open.jstree', function (e, data) {
		// var iframe = $('.modal-body iframe', window.parent.document);
		// var height = $('html').height();
		// iframe.height(height + 'px');
		changeCheckbox(false);
		paintSelectedNodes();
	})
	.on('after_close.jstree', function (e, data) {
		// var iframe = $('.modal-body iframe', window.parent.document);
		// var height = $('html').height();
		// iframe.height(height + 'px');
	})<c:if test="${not empty changedCallback}">
	.on('changed.jstree', function (e, data) {
		//console.log('>>> changed.jstree');
		if (data.action != undefined && data.action == "deselect_all" && ${isFavoritsPermes})
			${readyCallback}(e, data);
		return ${changedCallback}(e, data);
	})</c:if><c:if test="${not empty deselectAllCallback}">
	.on('deselect_all.jstree', function (e, data) {
		//console.log('>>> deselect_all.jstree');
		//return ${changedCallback}(e, data);
	})</c:if>
	.on('ready.jstree click', function (e, data) {
		changeCheckbox(false);
	});
	

	
	function changeCheckbox(removeAllSelected) {
		if (${isCheckBoxEnabled}) {
			$('.jstree-anchor')
			.find('i.jstree-checkbox')
			.removeClass('jstree-icon jstree-checkbox')
			.addClass('fa fa-square-o'); // adding the fa non-checked checkbox class
			if (removeAllSelected) {
				$('.jstree-anchor')
				.find('i.fa-check-square-o')
				.removeClass('i.fa-check-square-o')
				.addClass('fa fa-square-o');
				
				$('.jstree-anchor').removeClass('jstree-clicked-coneixement');
		    	perConeixement = [];
			}
		}
    }
</script>