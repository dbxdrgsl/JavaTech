Java Technologies - Lab 2
[valid 2024-2025]
Web Components

Compulsory (1p)

Create a Web application (starting from the previous lab) containing the following components:

input.jsp: a page containing a form for uploading a text file.
FileUploadServlet: a servlet that reads the content of the file and stores into session a collection of its lines.
result.jsp: a page that displays the lines of the file shuffled.
Homework (2p)


Create a web filter that logs all requests received by input.jsp.
Create a web listener that reads two properties, named prelude and coda, specified as context init parameters, at the application start-up. These values should be stored in two attributes having application scope.
Create a web filter that decorates the response by adding the prelude at the beginning and the coda at the end.
Add a CAPTCHA facility to the input form.
The purpose of the application is to integrate various components, each having a specialized role.


Bonus (2p)

Consider the case in which the uploaded file is a graph represented in DIMACS format. (Various graph instances can be found at DIMACS challenges, such as graph coloring instances.

Using Graph4J API, create a greedy algorithm for coloring the edges of the graph, such that no two edges sharing a vertex have the same color. The algorithm should assign colors to edges one by one, using the smallest available color for each edge.
The result.jsp page should display the solution found by the algorithm.

Research idea: Create a package in Graph4J dedicated to edge-coloring algorithms.

Resources
Lab slides
Java file upload by example with Servlets and JSPs
JavaServer Pages Technology
JavaBeans Components
The Essentials of Filters
Custom Tags in JSP Pages
JavaServer Pages Standard Tag Library