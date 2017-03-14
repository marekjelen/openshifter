package eu.mjelen.warden.templates;

import eu.mjelen.warden.templates.TemplateConfiguration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public interface Templates {

    default String template(String filename) {
        return template(filename, new HashMap<>());
    }

    default String template(String filename, Object context) {

        try {
            Template temp = TemplateConfiguration.common().getTemplate(filename);
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            temp.process(context, new OutputStreamWriter(result));
            return result.toString();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }

}
