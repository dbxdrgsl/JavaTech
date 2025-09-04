package ap.lab05.report;

import ap.lab05.core.ImageItem;
import ap.lab05.core.RepoException;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ReportGenerator {
    private final Configuration cfg;
    public ReportGenerator() {
        cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/");
        cfg.setDefaultEncoding("UTF-8");
    }
    public void generate(Path outHtml, List<ImageItem> items) throws RepoException {
        try {
            Template tpl = cfg.getTemplate("report.ftl");
            Map<String,Object> model = new HashMap<>();
            model.put("items", items);
            try (Writer w = new FileWriter(outHtml.toFile())) { tpl.process(model, w); }
        } catch (Exception e) {
            throw new RepoException("report failed: " + outHtml, e);
        }
    }
}
