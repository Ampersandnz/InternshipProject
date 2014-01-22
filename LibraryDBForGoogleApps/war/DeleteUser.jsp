<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="com.mlo.book.Book"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Optimation Library System</title>
</head>
<body>

	User(s)
	<%=request.getAttribute("deletedNames")%>
	successfully removed from library system.
	<form method="POST" action='librarydbforgoogleapps' name="deleteUser">
		<input type="submit" value="Back" name="back"> <input
			type="hidden" name="page" value="deleteUser" />
	</form>
</body>
</html>
