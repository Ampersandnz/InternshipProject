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

	<form method="GET" action='Controller' name="add">
		<table>
			<tr>
				<td>Artist:</td>
				<td><input type="text" name="artist"></td>
			</tr>
			<tr>
				<td>Title:</td>
				<td><input type="text" name="title"></td>
			</tr>
			<tr>
				<td>Album:</td>
				<td><input type="text" name="album"></td>
			</tr>
			<tr>
				<td>Rating:</td>
				<td><input type="number" name="rating" min="0" max="5"></td>
			</tr>
			<tr>
				<td><input type="submit" name="save" value="save"> <input
					type="reset" name="reset"> <input type="submit"
					name="back" value="back"> <input type="hidden" name="page" value="add" />
				</td>
			</tr>
		</table>
	</form>

</body>
</html>
