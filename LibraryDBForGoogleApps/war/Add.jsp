<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add book</title>
</head>
<body>
	New book entry

	<form method="GET" action='librarydbforgoogleapps' name="add">
		<table>
			<tr>
				<td>ISBN:</td>
				<td><input type="text" name="isbn"></td>
			</tr>
			<tr>
				<td>Title:</td>
				<td><input type="text" name="title"></td>
			</tr>
			<tr>
				<td>Currently in possession of:</td>
				<td><input type="text" name="inPossessionOf"></td>
			</tr>
			<tr>
				<td><input type="submit" name="save" value="Save"> <input
					type="reset" name="Reset"> <input type="submit"
					name="back" value="Back"> <input type="hidden" name="page" value="add" />
				</td>
			</tr>
		</table>
	</form>

</body>
</html>
