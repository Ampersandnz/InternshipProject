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
	Edit user data

	<form method="POST" action='librarydbforgoogleapps' name="editUser">
		<table>
			<tr>
				<td>Name:</td>
				<td>Email:</td>
			</tr>

			<%
				List<?> usersToEdit = null;
				Object temp = request.getAttribute("usersToEdit");
				if (temp instanceof List<?>) {
					usersToEdit = (List<?>) temp;
					if (!(null == usersToEdit)) {
						for (Object o : usersToEdit) {
							if (o instanceof User) {
								User u = (User) o;
			%>

			<tr>
				<td><input type="text" value="<%=u.getName()%>"
					name="name<%=u.getId()%>" /></td>
				<td><input type="text" value="<%=u.getEmail()%>"
					name="email<%=u.getId()%>" /></td>
			</tr>

			<%
				}
						}
					}
				}
			%>

		</table>

		<p>
			<input type="submit" name="save" value="Save"> <input
				type="reset" name="Reset"> <input type="submit" name="back"
				value="Back"> <input type="hidden" name="page"
				value="editUser" />
		</p>
	</form>

</body>
</html>
