package ap.lab02.listener;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppListener implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        String prelude = ctx.getInitParameter("prelude");
        String coda = ctx.getInitParameter("coda");
        ctx.setAttribute("prelude", prelude != null ? prelude : "");
        ctx.setAttribute("coda", coda != null ? coda : "");
        ctx.log("[AppListener] prelude/coda loaded");
    }
}
