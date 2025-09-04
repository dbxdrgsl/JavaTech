<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%
  // simple CAPTCHA: 5-digit code kept in session
  String cap = (String) session.getAttribute("captcha");
  if (cap == null || request.getParameter("regen") != null) {
    cap = String.valueOf(10000 + new java.util.Random().nextInt(90000));
    session.setAttribute("captcha", cap);
  }
%>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><title>Upload</title></head>
<body>
<h2>Upload a text file</h2>
<form action="upload" method="post" enctype="multipart/form-data">
  <p><input type="file" name="file" accept=".txt" required></p>
  <p>CAPTCHA: <strong><%=cap%></strong></p>
  <p>Enter CAPTCHA: <input name="captcha" required></p>
  <p><button type="submit">Upload</button>
     <a href="input.jsp?regen=1">regen</a></p>
</form>
</body></html>
