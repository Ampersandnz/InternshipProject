<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="com.mlo.user.User, java.util.ArrayList, java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Optimation Library System</title>
</head>
<body>
	Make user(s) admin

	<form method="POST" action='librarydbforgoogleapps' name="makeAdmin">
		<table>

			<%
				List<?> usersToMakeAdmin = null;
				Object temp = request.getAttribute("usersToMakeAdmin");
				if (temp instanceof List<?>) {
					usersToMakeAdmin = (List<?>) temp;
					if (!(null == usersToMakeAdmin)) {
						for (Object o : usersToMakeAdmin) {
							if (o instanceof User) {
								User u = (User) o;
			%>

			<tr>
				<td>Name:</td>
				<td><%=u.getName()%></td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><input type="password" name="password<%=u.getId()%>"></td>
				<td>(defaults to System Password)</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>

			<%
				}
						}
			%>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>System password:</td>
				<td><input type="password" name="systemPassword"></td>
			</tr>
			<tr>
				<td><input type="submit" name="save" value="Save"> <input
					type="reset" name="Reset"> <input type="submit" name="back"
					value="Back"> <input type="hidden" name="page"
					value="makeAdmin" /></td>
			</tr>
		</table>
		<%
			}
			}
		%>
	</form>

</body>
</html>
