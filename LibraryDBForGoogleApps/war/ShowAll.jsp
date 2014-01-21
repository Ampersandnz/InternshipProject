<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="com.mlo.book.Book, com.mlo.user.User, java.util.ArrayList, java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Optimation Library System</title>
</head>
<body>

	<form method="POST" action='librarydbforgoogleapps'
		name="showall_books">

		<%
			List<?> allBooks = null;
			List<?> allUsers = null;

			Object temp = request.getAttribute("allBooks");
			if (temp instanceof List<?>) {
				allBooks = (List<?>) temp;
		%>

		Books:
		<table>
			<tr>
				<td>&nbsp;</td>
				<td>ISBN:</td>
				<td>|</td>
				<td>Title:</td>
				<td>|</td>
				<td>Currently in possession of:</td>
				<td>|</td>
				<td>Total: <%=allBooks.size()%></td>
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
		
			
			<%
				Object o = request.getAttribute("selectedUser");
				if (o instanceof User) {
			%>
			
					<input type="submit" name="borrowBook" value="Borrow book(s) from library" /> 
					<input type="submit" name="returnBook" value="Return borrowed book(s) to library" /> 
			
			<%
					User user = (User) o;
					if (user.getIsAdmin()) {
			%>
			
			<input type="submit" name="addBook" value="Add new book to library" /> 
			<input type="submit" name="deleteBook" value="Delete book(s) from library" />
			<input type="submit" name="editBook" value="Edit book(s)" /> 
			
			<%
					}
			%>
			
			<input type="reset" value="Clear selection" /> 
			<input type="hidden" name="page" value="mainList" />
			
			<%
				}
			%>
			
		</p>
	</form>

	<form method="POST" action='librarydbforgoogleapps'
		name="showall_users">
		<%
			temp = request.getAttribute("allUsers");
			if (temp instanceof List<?>) {
				allUsers = (List<?>) temp;
		%>

		Users:
		<table>
			<tr>
				<td>&nbsp;</td>
				<td>Name:</td>
				<td>|</td>
				<td>Email:</td>
				<td>|</td>
				<td>Admin?</td>
				<td>|</td>
				<td>Total: <%=allUsers.size()%></td>
			</tr>

			<%
				if (!(null == allUsers)) {
						for (Object ob : allUsers) {
							if (ob instanceof User) {
								User u = (User) ob;
			%>
			<tr>
				<td><input type="checkbox" name="user<%=u.getId()%>" /></td>
				<td><%=u.getName()%></td>
				<td>|</td>
				<td><%=u.getEmail()%></td>
				<td>|</td>
				<td><%=u.getIsAdmin()%></td>
			</tr>

			<%
							}
						}
					}
				}
			%>

		</table>

		<p>
		
			<%
				if (o instanceof User) {
					User user = (User) o;
					if (user.getIsAdmin()) {
			%>
			
			<input type="submit" name="addUser" value="Add new user to system" />
			<input type="submit" name="deleteUser" value="Delete user(s) from system" /> 
			<input type="submit" name="editUser" value="Edit user(s)" /> 
				
			<%
					}
				}
			%>
			
			<input type="submit" name="selectUser" value="Select user" /> 
			<input type="submit" name="makeAdmin" value="Make user(s) admin" /> 
			<input type="reset" value="Clear selection" /> 
			<input type="hidden" name="page" value="mainList" />

		</p>
	</form>
	<% if (o instanceof User) {
		User user = (User) o;
		%>
				Currently selected user: <%=user.toString()%>
	<%
				}
	%>
</body>
</html>
