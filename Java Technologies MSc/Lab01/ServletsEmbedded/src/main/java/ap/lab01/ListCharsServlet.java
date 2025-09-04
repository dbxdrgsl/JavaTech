package ap.lab01;

import jakarta.servlet.http.*;
import java.io.*;

public class ListCharsServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String s = req.getParameter("s"); if (s==null) s="";
        log(req, "s="+s);
        resp.setContentType("text/html; charset=UTF-8");
        var out = resp.getWriter();
        out.println("<!doctype html><meta charset='utf-8'><h3>Ordered list</h3><ol>");
        for (int i=0;i<s.length();i++) out.println("<li>"+escape(s.charAt(i))+"</li>");
        out.println("</ol><p><a href='/'>Back</a></p>");
    }
    private String escape(char c) {
        if (c == '<') return "&lt;";
        if (c == '>') return "&gt;";
        if (c == '&') return "&amp;";
        if (c == '"') return "&quot;";
        return String.valueOf(c);
    }
    private void log(HttpServletRequest r,String p){ System.out.printf("[List] %s ip=%s ua=%s langs=%s params=%s%n",
            r.getMethod(), r.getRemoteAddr(), r.getHeader("User-Agent"), r.getHeader("Accept-Language"), p); }
}
