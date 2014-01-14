<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="com.mlo.book.Book" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Optimation Library System</title>
</head>
<body>

	<%if (!(null == request.getAttribute("newUser"))) { %>
		<%=request.getAttribute("newUser")%> selected.
	<%}%>
	
	<form method="GET" action='librarydbforgoogleapps' name="selectUser">
		<input type="submit" value="Back" name="back"> <input type="hidden"
			name="page" value="selectUser" />
	</form>
</body>
</html>