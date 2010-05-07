<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<link href="<c:url value="/css/reset.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/login.css"/>" rel="stylesheet" type="text/css"/>
	<style type="text/css" media="screen">@import "<c:url value="/css/uni-form/uni-form.css"/>";</style>
	<title>Helium - Autenticació</title>
</head>
<body>
	<div id="vertical">
		<div id="horizontal">
			<h1><span>H</span>elium</h1>
			<form action="j_security_check" method="post" class="uniForm">
				<div class="ctrlHolder">
					<label for="j_username">Usuari</label>
					<input type="text" id="j_username" name="j_username" class="textInput"/>
				</div>
				<div class="ctrlHolder">
					<label for="j_password">Contrasenya</label>
					<input type="password" name="j_password" class="textInput"/>
				</div>
				<div class="buttonHolder">
					<button type="submit" name="submit" value="submit" class="submitButton">Entrar</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>
