package pubUtils.template;

import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class TabNameFormat implements TemplateMethodModelEx {
    @SuppressWarnings("rawtypes")
    public Object exec(List arguments) throws TemplateModelException {
        String string = arguments.get(0).toString();
        return string.toLowerCase();
    }
}
