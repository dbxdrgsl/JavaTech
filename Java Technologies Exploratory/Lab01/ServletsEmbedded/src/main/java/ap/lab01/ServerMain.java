package ap.lab01;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public final class ServerMain {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        var server = new Server(port);

        // Static files from resources/webapp
        var resources = new ResourceHandler();
        resources.setWelcomeFiles(new String[]{"index.html"});
        resources.setResourceBase(ServerMain.class.getClassLoader()
                .getResource("webapp").toExternalForm());

        // Servlets
        var ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ctx.setContextPath("/");
        ctx.addServlet(new ServletHolder(new ListCharsServlet()), "/list-chars");
        ctx.addServlet(new ServletHolder(new GraphServlet()), "/graph");
        ctx.addServlet(new ServletHolder(new SpanningTreesServlet()), "/k-spanning");

        server.setHandler(new HandlerList(resources, ctx));
        server.start();
        System.out.println("Jetty on http://localhost:" + port + "/");
        server.join();
    }
}
