<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="com.mlo.book.*, java.util.ArrayList, java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Edit user(s)</title>
</head>
<body>
	Edit user data

	<form method="POST" action='librarydbforgoogleapps' name="editUser">
		<table>
			<tr>
				<td>ISBN:</td>
				<td>Title:</td>
				<td>Currently in possession of:</td>
			</tr>

			<%
				List<?> booksToEdit = null;
				Object temp = request.getAttribute("booksToEdit");
				if (temp instanceof List<?>) {
					booksToEdit = (List<?>) temp;
					if (!(null == booksToEdit)) {
						for (Object o : booksToEdit) {
							if (o instanceof Book) {
								Book b = (Book) o;
			%>
			
			<tr>
				<td><input type="text" value="<%=b.getIsbn()%>" name="isbn<%=b.getId()%>" /></td>
				<td><input type="text" value="<%=b.getTitle()%>" name="title<%=b.getId()%>" /></td>
				<td><input type="text" value="<%=b.getInPossessionOf()%>" name="inPossessionOf<%=b.getId()%>" /></td>
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
				value="Back"> <input type="hidden" name="page" value="editUser" />
		</p>
	</form>

</body>
</html>
