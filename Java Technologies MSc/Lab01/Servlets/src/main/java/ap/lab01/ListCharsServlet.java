package ap.lab01;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="ListCharsServlet", urlPatterns={"/list-chars"})
public class ListCharsServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String s = req.getParameter("s");
        if (s == null) s = "";
        logRequest(req, "s=" + s);

        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Chars</title></head><body>");
        out.println("<h3>Ordered list of characters</h3><ol>");
        for (int i = 0; i < s.length(); i++) out.println("<li>" + escape(s.charAt(i)) + "</li>");
        out.println("</ol><p><a href='index.html'>Back</a></p></body></html>");
    }

    private static String escape(char c){ return switch (c) {
        case '<' -> "&lt;"; case '>' -> "&gt;"; case '&' -> "&amp;"; case '"' -> "&quot;"; default -> String.valueOf(c);
    };}

    private void logRequest(HttpServletRequest req, String params){
        String method = req.getMethod();
        String ip = req.getRemoteAddr();
        String ua = req.getHeader("User-Agent");
        String langs = req.getHeader("Accept-Language");
        getServletContext().log(String.format("[List] %s ip=%s ua=%s langs=%s params=%s", method, ip, ua, langs, params));
    }
}
