package com.onboarding.notifications.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SimpleTemplateRenderer implements TemplateRenderer {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{([^}]+)}}");

    @Override
    public String render(String template, Map<String, Object> variables) {
        if (template == null || variables == null) return template;
        String result = template;
        Matcher m = PLACEHOLDER.matcher(result);
        while (m.find()) {
            String key = m.group(1).trim();
            Object value = variables.get(key);
            if (value != null) {
                result = result.replace("{{" + key + "}}", value.toString());
            }
        }
        return result;
    }
}
