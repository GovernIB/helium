<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Integració. Formulari</title>
</head>
<body>

	<script>
		/** Afegeix una línia a la taula per introduir el codi i valor de variables. */
		function afegirLinia() {
			var table = document.getElementById("taulaParametres");
			var row = table.insertRow();
			var cell1 = row.insertCell(0);
			var cell2 = row.insertCell(1);
			cell1.innerHTML = "<input type='text' name='codi' value='[codi]'/>";
			cell2.innerHTML = "<input type='text' name='valor'/>";
		}
	</script>
	
	
	<h2>Integració. Formulari extern <i>${formId}</i></h2>
	<c:if test="${resultat != null }">
		<h2 style="color:green;">Resultat:
			<br/>${resultat}
		</h2>
	</c:if>	
 	<c:if test="${peticio != null}">
		<table border="1">
			<tr>
				<td>Codi</td>
				<td>${peticio.codi}</td>
			</tr>
			<tr>
				<td>TaskId</td>
				<td>${peticio.taskId}</td>
			</tr>
		</table>
		<h2>Formulari amb els valors</h2>
		<form action="" method="POST">
			<p>formIdValue : <input type="text" name="formIdValue" value="${formIdValue}" /></p>
			<p>endPointAddress : <input type="text" name="endPointAddress" value="${endPointAddress}" size="80"/><a href="${endPointAddress }?wsdl">?wsdl</a></p>
			<table border="1" id="taulaParametres">
				<thead>
					<tr>
						<th>Codi</th>
						<th>Valor</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="valor" items="${peticio.valors}" varStatus="status">
					<% 
						int i = 0; 
					%>
					<tr>
						<td><input type="hidden" name="codi" value="${valor.codi}" id="codi_<%=i%>"> ${valor.codi}</td>
						<td><input type="text" name="valor" value="${valor.valor}" id="valor_<%=i%>" /></td>
					</tr>
					<% 
						i++; 
					%>
					</c:forEach>
				</tbody>
			</table>
			<a href="#" onClick="afegirLinia();">afegir</a>
			<br/>
			<c:if test="${tancar != true }">
				<button type="submit">Acceptar</button>
			</c:if>	
		</form>
	</c:if>
	<c:if test="${peticio == null }">
		<h2>No s'ha pogut recuperar la petició per aquest identificador de formulari</h2>
	</c:if>
</body>
</html>