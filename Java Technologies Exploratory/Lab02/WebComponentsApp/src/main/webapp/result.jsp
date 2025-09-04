<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://jakarta.ee/jstl/core" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><title>Result</title></head>
<body>
<h2>Shuffled lines</h2>
<c:choose>
  <c:when test="${not empty sessionScope.lines}">
    <ul>
      <c:forEach var="line" items="${sessionScope.lines}">
        <li><c:out value="${line}"/></li>
      </c:forEach>
    </ul>
  </c:when>
  <c:otherwise>
    <p>No lines found in session.</p>
  </c:otherwise>
</c:choose>
<p><a href="input.jsp">Back</a></p>
</body></html>
