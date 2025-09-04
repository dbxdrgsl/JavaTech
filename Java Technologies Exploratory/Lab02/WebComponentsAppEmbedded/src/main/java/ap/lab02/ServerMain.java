package ap.lab02;

import ap.lab02.filter.DecoratorFilter;
import ap.lab02.filter.LoggingFilter;
import ap.lab02.listener.AppListener;
import ap.lab02.web.FileUploadServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import org.apache.jasper.servlet.JspServlet;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;

import jakarta.servlet.DispatcherType;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.EnumSet;

public final class ServerMain {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        Server server = new Server(port);

        // Use WebAppContext for JSPs
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");

        // Base resource: src/main/resources/webapp on classpath
        webapp.setBaseResource(Resource.newClassPathResource("webapp"));

        // Temp dir for compiled JSP classes
        File scratch = new File("target/jsp");
        scratch.mkdirs();
        webapp.setAttribute("javax.servlet.context.tempdir", scratch);

        // right after baseResource / tempdir
        webapp.setAttribute(
                "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/jakarta\\.servlet\\.jsp\\.jstl.*|.*/.*taglibs.*"
        );


        // ClassLoader so Jasper can load generated classes
        ClassLoader parent = ServerMain.class.getClassLoader();
//        webapp.setClassLoader(new URLClassLoader(new URL[0], parent));

        // Init params used elsewhere
        webapp.setInitParameter("prelude", "=== PRELUDE ===");
        webapp.setInitParameter("coda",    "=== CODA ===");

        // Enable Jasper (JSP) in embedded Jetty
        var inits = new java.util.ArrayList<ContainerInitializer>();
        inits.add(new ContainerInitializer(new JettyJasperInitializer(), null));
        webapp.setAttribute("org.eclipse.jetty.containerInitializers", inits);
        webapp.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());

        // Map JSP servlet
        ServletHolder jsp = new ServletHolder("jsp", JspServlet.class);
        jsp.setInitParameter("fork", "false");
        jsp.setInitParameter("xpoweredBy", "false");
        jsp.setInitParameter("compilerTargetVM", "17");
        jsp.setInitParameter("compilerSourceVM", "17");
        jsp.setInitOrder(0);
        webapp.addServlet(jsp, "*.jsp");

        // Upload servlet
        webapp.addServlet(new ServletHolder(new FileUploadServlet()), "/upload");

        // Filters
        webapp.addFilter(new FilterHolder(new LoggingFilter()), "/input.jsp", EnumSet.of(DispatcherType.REQUEST));
        webapp.addFilter(new FilterHolder(new DecoratorFilter()), "/*",
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE));

        // Listener
        webapp.addEventListener(new AppListener());

        server.setHandler(webapp);
        server.start();
        System.out.println("Running at http://localhost:" + port + "/input.jsp");
        server.join();
    }
}
