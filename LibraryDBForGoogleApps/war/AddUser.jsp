<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Optimation Library System</title>
</head>
<body>
	New user entry

	<form method="POST" action='librarydbforgoogleapps' name="add">
		<table>
			<tr>
				<td>Name:</td>
				<td><input type="text" name="name"></td>
			</tr>
			<tr>
				<td>Email:</td>
				<td><input type="text" name="email"></td>
			</tr>
			<tr>
				<td><input type="submit" name="save" value="Save"> <input
					type="reset" name="Reset"> <input type="submit"
					name="back" value="Back"> <input type="hidden" name="page" value="addUser" />
				</td>
			</tr>
		</table>
	</form>

</body>
</html>
