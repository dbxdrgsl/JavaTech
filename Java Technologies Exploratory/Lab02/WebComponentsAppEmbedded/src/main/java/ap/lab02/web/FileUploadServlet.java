package ap.lab02.web;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.*;
import java.util.*;

@MultipartConfig
public class FileUploadServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");

        String sent   = req.getParameter("captcha");
        String expect = (String) req.getSession().getAttribute("captcha");

// debug
        req.getServletContext().log("CAPTCHA check: sent='" + sent + "' expect='" + expect + "'");

        if (expect == null || sent == null || !expect.trim().equals(sent.trim())) {
            resp.setStatus(400);
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<p>Invalid CAPTCHA.</p><p><a href='input.jsp'>Back</a></p>");
            return;
        }


        Part filePart = req.getPart("file");
        if (filePart == null || filePart.getSize() == 0) {
            resp.setStatus(400);
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<p>No file uploaded.</p><p><a href='input.jsp'>Back</a></p>");
            return;
        }

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(filePart.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        }
        Collections.shuffle(lines);
        req.getSession().setAttribute("lines", lines);

        // optional: invalidate used captcha to avoid reuse
        req.getSession().removeAttribute("captcha");

        req.getRequestDispatcher("/result.jsp").forward(req, resp);
    }
}
