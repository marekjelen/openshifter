package eu.mjelen.warden.templates;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class TemplateConfiguration {

    private static Configuration common = new Configuration(Configuration.VERSION_2_3_25);

    static {
        common.setClassForTemplateLoading(TemplateConfiguration.class, "/");
        common.setDefaultEncoding("UTF-8");
        common.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        common.setLogTemplateExceptions(false);
    }

    public static Configuration common() {
        return common;
    }

}
