package com.onboarding.notifications.service;

import java.util.Map;

public interface TemplateRenderer {

    String render(String template, Map<String, Object> variables);
}
