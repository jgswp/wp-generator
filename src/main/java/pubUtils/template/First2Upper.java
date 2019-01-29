package pubUtils.template;

import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import pubUtils.StringHelper;

public class First2Upper implements TemplateMethodModelEx {
    @SuppressWarnings("rawtypes")
    public Object exec(List arguments) throws TemplateModelException {
        String name = arguments.get(0).toString();
        if (name.charAt(0) == '_') {
            name = name.substring(1);
        }
        boolean isOk = (name.length() > 1 && !Character.isUpperCase(name.charAt(1)));
        if (isOk) {
            return StringHelper.first2Upper(name);
        }
        return name;
    }
}
