package ap.lab02.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;

public class DecoratorFilter implements Filter {
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        CharResponseWrapper wrapper = new CharResponseWrapper(resp);
        chain.doFilter(request, wrapper);

        String prelude = String.valueOf(req.getServletContext().getInitParameter("prelude"));
        String coda    = String.valueOf(req.getServletContext().getInitParameter("coda"));
        if (prelude == null) prelude = "";
        if (coda == null) coda = "";

        String body = wrapper.getCapture();
        if (body == null) body = "";

        byte[] bytes = (prelude + System.lineSeparator() + body + System.lineSeparator() + coda)
                .getBytes(response.getCharacterEncoding());
        resp.setContentLength(bytes.length);
        try (ServletOutputStream os = resp.getOutputStream()) { os.write(bytes); }
    }

    static class CharResponseWrapper extends HttpServletResponseWrapper {
        private final StringWriter capture = new StringWriter();
        private PrintWriter writer;
        private ServletOutputStream output;

        CharResponseWrapper(HttpServletResponse response) { super(response); }

        @Override public PrintWriter getWriter() {
            if (writer == null) writer = new PrintWriter(capture, true);
            return writer;
        }
        @Override public ServletOutputStream getOutputStream() {
            if (output == null) output = new ServletOutputStream() {
                private final Writer w = capture;
                @Override public boolean isReady() { return true; }
                @Override public void setWriteListener(WriteListener writeListener) { }
                @Override public void write(int b) throws IOException { w.write(b); }
                @Override public void write(byte[] b, int off, int len) throws IOException {
                    w.write(new String(b, off, len));
                }
            };
            return output;
        }
        String getCapture() { return capture.toString(); }
    }
}
