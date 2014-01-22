<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="com.mlo.user.User"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Optimation Library System</title>
</head>
<body>
	Admin account login

	<form method="POST" action='librarydbforgoogleapps' name="adminSelect">
		<table>

			<%
				Object userId = request.getAttribute("userId");
			%>

			<tr>
				<td>Password:</td>
				<td><input type="password" name="userPassword"></td>
			</tr>
			<tr>
				<td><input type="submit" name="save" value="Save"> <input
					type="reset" name="Reset"> <input type="submit" name="back"
					value="Back"> <input type="hidden" name="page"
					value="adminSelect" /> <input type="hidden" name="userId"
					value=<%=userId%> /></td>
			</tr>
		</table>
	</form>
</body>
</html>
