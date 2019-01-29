package pubUtils.template;

import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import pubUtils.StringHelper;

public class First2Lower implements TemplateMethodModelEx {
    @SuppressWarnings("rawtypes")
    public Object exec(List arguments) throws TemplateModelException {
        return StringHelper.first2Lower(arguments.get(0).toString());
    }
}
