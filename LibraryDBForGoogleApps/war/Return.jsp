<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="com.mlo.book.Book" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Book(s) returned</title>
</head>
<body>

	<%if (!(null == request.getAttribute("returnedISBNs"))) { %>
		Book(s) (ISBN = <%=request.getAttribute("returnedISBNs") %>) were returned to library.
	<%}%>
	<%if (!(null == request.getAttribute("returnedISBNs")) && (!(null == request.getAttribute("notReturnedISBNs")))) { %>
		<br/><br/>
	<%}%>
	<%if (!(null == request.getAttribute("notReturnedISBNs"))) { %>
		Book(s) (ISBN = <%=request.getAttribute("notReturnedISBNs") %>) were already in the library and have not been returned.
	<%}%>
	
	<form method="GET" action='librarydbforgoogleapps' name="return_success">
		<input type="submit" value="Back" name="back"> <input type="hidden"
			name="page" value="return" />
	</form>
</body>
</html>