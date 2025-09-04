package ap.lab02.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.*;

@WebFilter("/*")
public class DecoratorFilter implements Filter {
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        CharResponseWrapper wrapper = new CharResponseWrapper(resp);
        chain.doFilter(request, wrapper);

        String prelude = String.valueOf(req.getServletContext().getAttribute("prelude"));
        String coda    = String.valueOf(req.getServletContext().getAttribute("coda"));

        String content = wrapper.getCapture();
        if (content == null) content = "";

        byte[] bytes = (prelude + System.lineSeparator() + content + System.lineSeparator() + coda).getBytes(response.getCharacterEncoding());
        resp.setContentLength(bytes.length);
        try (ServletOutputStream os = resp.getOutputStream()) {
            os.write(bytes);
        }
    }

    /** Capture writer output into a buffer. */
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
                private final OutputStream target = new WriterOutputStream(capture);
                @Override public boolean isReady() { return true; }
                @Override public void setWriteListener(WriteListener writeListener) { }
                @Override public void write(int b) throws IOException { target.write(b); }
            };
            return output;
        }

        String getCapture() { return capture.toString(); }
    }

    /** Minimal Writer→OutputStream adapter. */
    static class WriterOutputStream extends OutputStream {
        private final Writer writer;
        WriterOutputStream(Writer w) { this.writer = w; }
        @Override public void write(int b) throws IOException { writer.write(b); }
        @Override public void write(byte[] b, int off, int len) throws IOException {
            writer.write(new String(b, off, len));
        }
        @Override public void flush() throws IOException { writer.flush(); }
        @Override public void close() throws IOException { writer.close(); }
    }
}
