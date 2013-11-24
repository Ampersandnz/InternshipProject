<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="com.mlo.book.*, java.util.ArrayList"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Book list</title>
</head>
<body>

	<form method="GET" action='Controller' name="showall">
		<table>
			<tr>
				<td></td>
				<td>ISBN</td>
				<td>|</td>
				<td>Title</td>
				<td>|</td>
				<td>In Library</td>
				<td>|</td>
				<td>In Possession of</td>
			</tr>

			<%
				ArrayList<Book> allBooks = (ArrayList<Book>) request
							.getAttribute("allBooks");
					if (!(null == allBooks)) {
						for (Book s : allBooks) {
			%>
			<tr>
				<td><input type="checkbox" name="book<%=s.getId()%>" /></td>
				<td><%=s.getIsbn()%></td>
				<td>|</td>
				<td><%=s.getTitle()%></td>
				<td>|</td>
				<td><%=s.getInLibrary()%></td>
				<td>|</td>
				<td><%=s.getInPossessionOf()%></td>
			</tr>

			<%
				}
				}
			%>

		</table>

		<p>
			<input type="submit" name="add" value="Add new book to library" />&nbsp; <input
				type="submit" name="delete" value="Delete book from system" />&nbsp; <input
				type="submit" name="borrow" value="Borrow book from library" />&nbsp; <input
				type="submit" name="return" value="Return borrowed book to library" />&nbsp; <input
				type="reset" value="Clear selection" /> <input type="hidden" name="page"
				value="mainList" />
		</p>
	</form>



</body>
</html>
