<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ attribute name="pare" required="true" type="java.lang.Object"%>
<%@ attribute name="fills" required="true" type="java.lang.Object"%>
<%@ attribute name="atributId" required="true"%>
<%@ attribute name="atributNom" required="true"%>
<%@ attribute name="seleccionatId"%>
<%@ attribute name="fulles" type="java.lang.Object"%>
<%@ attribute name="fullesAtributId"%>
<%@ attribute name="fullesAtributNom"%>
<%@ attribute name="fullesAtributPare"%>
<%@ attribute name="fullesIcona"%>
<%@ attribute name="isOcultarCounts" type="java.lang.Boolean"%>
<ul>
	<c:forEach var="fill" items="${fills}">
		<li id="${fill.dades[atributId]}" data-jstree='{"icon":"fa fa-folder fa-lg"<c:if test="${not empty seleccionatId and fill.dades[atributId] == seleccionatId}">, "selected": true</c:if>}'>
			${fill.dades[atributNom]}<c:if test="${not isOcultarCounts and fill.mostrarCount}"> <span class="badge">${fill.count}</span></c:if>
			<hel:arbreFills pare="${fill}" fills="${fill.fills}" atributId="${atributId}" atributNom="${atributNom}" seleccionatId="${seleccionatId}"  fulles="${fulles}" fullesIcona="${fullesIcona}" fullesAtributId="${fullesAtributId}" fullesAtributNom="${fullesAtributNom}" fullesAtributPare="${fullesAtributPare}" isOcultarCounts="${isOcultarCounts}"/>
		</li>
	</c:forEach>
	<c:forEach var="fulla" items="${fulles}">
		<c:if test="${fulla[fullesAtributPare] == pare.dades[atributId]}">
			<li id="${fulla[fullesAtributId]}" data-jstree='{"icon":"${fullesIcona}"}'>${fulla[fullesAtributNom]}</li>
		</c:if>
	</c:forEach>
</ul>
