<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="com.mlo.book.Book, com.mlo.user.User, java.util.ArrayList, java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Book list</title>
</head>
<body>

	<% 
	List<?> allBooks = null;
	List<?> allUsers = null;
	
	Object temp = request.getAttribute("allBooks");
	if (temp instanceof List<?>) {
		allBooks = (List<?>) temp;
	%>

	<form method="POST" action='Controller' name="showall">
		Books: 
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
				for (Object o : allBooks) {
					if (o instanceof Book) {
						Book b = (Book) o;
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

		<% 
		temp = request.getAttribute("allUsers");
		if (temp instanceof List<?>) {
			allUsers = (List<?>) temp;
		%>
		
		Users:
		<table>
			<tr>
				<td>Name:</td>
				<td>|</td>
				<td>Email:</td>
				<td>|</td>
				<td>Total: <%=allUsers.size() %></td>
			</tr>

			<%
			if (!(null == allUsers)) {
				for (Object o : allUsers) {
					if (o instanceof User) {
						User u = (User) o;
			%>
			<tr>
				<td><%=u.getName()%></td>
				<td>|</td>
				<td><%=u.getEmail()%></td>
			</tr>

			<%
				}
				}
				}
			}
			%>

		</table>

	</form>
</body>
</html>
