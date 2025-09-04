# Java Technologies – Lab 1 (Servlets)

## Teacher Task
> **Compulsory (1p)**  
> Create a servlet that receives a string and returns an HTML page containing the characters of that string presented as an ordered list.

> **Homework (2p)**  
> Create a servlet that receives two integers parameters, called numVertices and numEdges, and returns the adjacency matrix of a randomly generated graph with the specified number of vertices and edges.  
> The servlet invocation will be done using a simple HTML form and the response will be offered as an HTML table.  
> Write in the server log the following information about each request: the HTTP method used, the IP-address of the client, the user-agent, the client language(s) and the parameter of the request.  
> Invoke the servlet from a desktop application (Java, Python, .NET, etc.).  
> The response of the servlet will be, in this case, a simple text representing the adjacency matrix, in a format at your choice.

> **Bonus (2p)**  
> Consider a complete weighted graph, in which the weight of each edge is the sum of its vertex numbers.  
> Write an algorithm that iterates over all spanning trees of the graph in increasing order of their weight, (a spanning tree of a graph G is a subgraph that has the same vertices as G and its edges form a tree).  
> Create a servlet based on the algorithm above.  
> The servlet will receive as parameters the order of the graph and a number k and it will return the first k spanning trees of the graph, in increasing order of their weight.  
> The response of the servlet will be a textual representation of the spanning trees, in a format of your choice.  
> For larger graphs, the algorithm may take some time in order to create the response. Analyze the performance of the service by invoking the servlet repeatedly, in an asynchronous manner.  
> You may use Graph4J API.

---

## Solution Explanation

This project uses **embedded Jetty 11** (Servlet API 5.0, Jakarta namespace).  
Instead of packaging a WAR and deploying to GlassFish/Payara, the server is launched programmatically from a `main` method.  
This makes development easier and avoids issues with JDK 23 bytecode incompatibility.

The project implements three servlets:
1. **ListCharsServlet** – compulsory part (ordered list of characters).
2. **GraphServlet** – homework part (random adjacency matrix + HTML table, logs requests).
3. **SpanningTreesServlet** – bonus part (first k spanning trees by weight).

Static HTML (`index.html`) is served from resources to provide input forms.

---

## Compulsory Task

### Natural language explanation
The servlet reads a query parameter `s` from the request.  
It then builds an HTML page containing each character of the string as a list item (`<ol>`).  
Request details (method, IP, user-agent, languages, params) are logged to the server console.

### Code Snippet
[`ListCharsServlet.java`](src/main/java/ap/lab01/ListCharsServlet.java)
```java
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String s = req.getParameter("s");
    if (s == null) s = "";
    log(req, "s=" + s);

    resp.setContentType("text/html; charset=UTF-8");
    var out = resp.getWriter();
    out.println("<h3>Ordered list</h3><ol>");
    for (int i = 0; i < s.length(); i++) {
        out.println("<li>" + escape(s.charAt(i)) + "</li>");
    }
    out.println("</ol>");
}
```

### Explanation of referenced code
- `req.getParameter("s")`: gets the input string from the request.
- Iterates through characters with a `for` loop.
- Each character is escaped for HTML safety and written inside `<li>` tags.
- Logs the request using a helper method `log(req, ...)`.
- The response type is set to HTML.

---

## Homework Task

### Natural language explanation
This servlet receives two integers (`numVertices`, `numEdges`).  
It generates a random undirected simple graph with the specified number of vertices and edges.  
The adjacency matrix is printed in an HTML table with row/column headers.  
It also computes:
- `δ(G)`: minimum degree
- `Δ(G)`: maximum degree
- verifies ∑deg = 2m

Logs request metadata for every call.  
A special `format=txt` query outputs a plain-text adjacency matrix (used for the desktop client).

### Code Snippet
[`GraphServlet.java`](src/main/java/ap/lab01/GraphServlet.java)
```java
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    int n = parse(req.getParameter("numVertices"), 0);
    int m = parse(req.getParameter("numEdges"), -1);
    String format = req.getParameter("format");

    log(req, "n=" + n + ", m=" + m);

    int[][] A = generateGraph(n, m);

    if ("txt".equalsIgnoreCase(format)) {
        resp.setContentType("text/plain");
        writeTxt(resp.getWriter(), A);
    } else {
        resp.setContentType("text/html");
        writeHtml(resp.getWriter(), A);
    }
}
```
### Explanation of referenced code
- Parameters `numVertices`, `numEdges` are parsed from the request.
- A helper method `generateGraph` ensures the graph is connected (adds spanning tree edges, then extra edges randomly).
- If `format=txt` → adjacency matrix in plain text (desktop client use).
- Otherwise → prints styled HTML `<table>`.
- `log(req, ...)` writes request info to server logs.

---

## Bonus Task

### Natural language explanation
We model a complete weighted graph **Kₙ**, with `w(i,j) = i + j`.  
Every spanning tree corresponds to a **Prufer code**.  
For this weight model:

weight(tree) = C(n) + sum(PruferCode)
C(n) = n(n+1)/2


So ordering trees by weight is equivalent to ordering their Prufer codes by the sum of entries.

**Algorithm:**
1. Start with the minimal code `[1,1,...,1]`.
2. Use a priority queue (min-heap) keyed by sum.
3. Generate neighbors by incrementing code entries.
4. Decode Prufer code → spanning tree edges.
5. Stop after k trees.

### Code Snippet
[`SpanningTreesServlet.java`](src/main/java/ap/lab01/SpanningTreesServlet.java)
```java
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    int n = parse(req.getParameter("n"), 0);
    int k = parse(req.getParameter("k"), 0);

    List<Result> trees = firstKTrees(n, k);

    resp.setContentType("text/plain; charset=UTF-8");
    try (PrintWriter out = resp.getWriter()) {
        for (int i = 0; i < trees.size(); i++) {
            Result r = trees.get(i);
            out.printf("[%d] weight=%d  prufer=%s  edges=%s%n",
                       i+1, r.weight, Arrays.toString(r.code), edgesToString(r.edges));
        }
    }
}
```

### Explanation of referenced code

Reads n (graph order) and k (number of spanning trees requested).
Calls firstKTrees(n,k) → generates trees using Prufer codes.
Iterates through results and prints weight, code, and edges.
Output is plain text for easy readability.

---

### Server Bootstrap
## Natural language explanation

Instead of deploying WAR, we launch Jetty directly.
It serves:
static HTML resources (index.html with forms)

our three servlets

Code Snippet

ServerMain.java

public final class ServerMain {
public static void main(String[] args) throws Exception {
var server = new Server(8080);

    var resources = new ResourceHandler();
    resources.setWelcomeFiles(new String[]{"index.html"});
    resources.setResourceBase(ServerMain.class.getClassLoader()
        .getResource("webapp").toExternalForm());

    var ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
    ctx.setContextPath("/");
    ctx.addServlet(new ServletHolder(new ListCharsServlet()), "/list-chars");
    ctx.addServlet(new ServletHolder(new GraphServlet()), "/graph");
    ctx.addServlet(new ServletHolder(new SpanningTreesServlet()), "/k-spanning");

    server.setHandler(new HandlerList(resources, ctx));
    server.start();
    System.out.println("Jetty on http://localhost:8080/");
    server.join();
}
}

Explanation of referenced code

Creates a Jetty server on port 8080.

ResourceHandler → serves static HTML from src/main/resources/webapp.

ServletContextHandler → registers the three servlets at /list-chars, /graph, /k-spanning.

Starts Jetty and waits for connections.

Desktop Client (Homework part)
Natural language explanation

A small desktop program sends an HTTP request to /graph with format=txt and prints the plain-text adjacency matrix.
Demonstrates how a non-browser application can interact with the servlet.

Code Snippet

DesktopClient.java

HttpClient client = HttpClient.newHttpClient();
HttpRequest req = HttpRequest.newBuilder()
.uri(URI.create("http://localhost:8080/graph?numVertices=5&numEdges=6&format=txt"))
.build();
HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
System.out.println(resp.body());

Explanation of referenced code

Uses Java 11+ HttpClient.

Builds a GET request to the servlet endpoint.

Sends synchronously, retrieves plain-text response.

Prints adjacency matrix on the console.

How to Run
Start server
mvn clean package
java -jar target/ServletsEmbedded-1.0-SNAPSHOT.jar

Open browser

http://localhost:8080/

/list-chars?s=Hello

/graph?numVertices=5&numEdges=6

/k-spanning?n=5&k=10

Run desktop client
mvn exec:java -Dexec.mainClass="ap.lab01.client.DesktopClient"


---