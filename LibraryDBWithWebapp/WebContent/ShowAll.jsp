<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="com.mlo.book.*, java.util.ArrayList, java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Book list</title>
</head>
<body>
	
	<% List<Book> allBooks = (List<Book>) request.getAttribute("allBooks");%>
	
	<form method="GET" action='Controller' name="showall">
		<table>
			<tr>
				<td></td>
				<td>ISBN:</td>
				<td>|</td>
				<td>Title:</td>
				<td>|</td>
				<td>Currently in possession of:</td>
				<td>|</td>
				<td>Total: <%=allBooks.size() %></td>
			</tr>

			<%
				if (!(null == allBooks)) {
					for (Book b : allBooks) {
			%>
			<tr>
				<td><input type="checkbox" name="book<%=b.getId()%>" /></td>
				<td><%=b.getIsbn()%></td>
				<td>|</td>
				<td><%=b.getTitle()%></td>
				<td>|</td>
				<td><%=b.getInPossessionOf()%></td>
			</tr>

			<%
				}
				}
			%>

		</table>

		<p>
			<input type="submit" name="borrow"
				value="Borrow book(s) from library" />&nbsp; <input type="submit"
				name="return" value="Return borrowed book(s) to library" />&nbsp; <input
				type="submit" name="add" value="Add new book to library" />&nbsp; <input
				type="submit" name="delete" value="Delete book(s) from system" />&nbsp;<input
				type="submit" name="edit" value="Edit book(s)" />&nbsp; <input
				type="reset" value="Clear selection" /> <input type="hidden"
				name="page" value="mainList" />
		</p>
	</form>
</body>
</html>
