<%
String cap = (String) session.getAttribute("captcha");
if (cap == null || request.getParameter("regen") != null) {
  cap = String.valueOf(10000 + new java.util.Random().nextInt(90000));
  session.setAttribute("captcha", cap);
}
%>
<form action="upload" method="post" enctype="multipart/form-data">
  <input type="file" name="file" accept=".txt" required>
  CAPTCHA: <strong><%=cap%></strong>
  <input name="captcha" required>
  <button type="submit">Upload</button>
</form>
