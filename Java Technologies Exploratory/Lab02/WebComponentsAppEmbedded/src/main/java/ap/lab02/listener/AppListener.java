package ap.lab02.listener;

import jakarta.servlet.*;

public class AppListener implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        // copy init params to attributes (application scope), if you want both
        ctx.setAttribute("prelude", ctx.getInitParameter("prelude"));
        ctx.setAttribute("coda", ctx.getInitParameter("coda"));
        ctx.log("[AppListener] prelude/coda loaded");
    }
}
