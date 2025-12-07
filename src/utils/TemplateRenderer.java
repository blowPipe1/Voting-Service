package utils;
import freemarker.template.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class TemplateRenderer {
    private static final Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);

    static {
        cfg.setClassForTemplateLoading(TemplateRenderer.class, "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
    }

    public static String processTemplate(String templateName, Map<String, Object> dataModel) throws IOException, TemplateException {
        Template template = cfg.getTemplate(templateName);
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return writer.toString();
    }
}