<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="com.mlo.book.Book" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Book(s) borrowed</title>
</head>
<body>

	<%if (!(null == request.getAttribute("borrowedISBNs"))) { %>
		Book(s) (ISBN = <%=request.getAttribute("borrowedISBNs") %>) were borrowed by <%=request.getAttribute("borrower") %>.
	<%}%>
	<%if (!(null == request.getAttribute("borrowedISBNs")) && (!(null == request.getAttribute("notBorrowedISBNs")))) { %>
		<br/><br/>
	<%}%>
	<%if (!(null == request.getAttribute("notBorrowedISBNs"))) { %>
		Book(s) (ISBN = <%=request.getAttribute("notBorrowedISBNs") %>) were already in the possession of <%=request.getAttribute("borrower") %> and have not been borrowed.
	<%}%>
	
	<form method="GET" action='librarydbforgoogleapps' name="borrow">
		<input type="submit" value="Back" name="back"> <input type="hidden"
			name="page" value="borrow" />
	</form>
</body>
</html>