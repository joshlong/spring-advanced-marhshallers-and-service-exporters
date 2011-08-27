package org.springframework.util;


import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * A jetty configuration callback that knows how to register Spring MVC and the {@link org.springframework.web.servlet.DispatcherServlet}
 *
 * @author Josh Long
 */
public class DispatcherServletJettyConfigurationCallback implements EndpointTestUtils.JettyContextConfigurationCallback {

    private Class<?> configurationClass;

    private String servletPrefix;

    public DispatcherServletJettyConfigurationCallback(Class<?> configurationClass) {
        this(configurationClass, null);
    }

    public DispatcherServletJettyConfigurationCallback(Class<?> configurationClass, String servletPrefix) {
        Assert.notNull(configurationClass, "the configuration class can't be null!");
        this.configurationClass = configurationClass;
        this.servletPrefix = StringUtils.hasText(servletPrefix) ? servletPrefix : "/*";
        Assert.isTrue(this.configurationClass.getAnnotation(Configuration.class) != null,
                             "the configuration class must have the " + Configuration.class.getName() + " annotation on it");
    }

    @Override
    public void configure(Context context) throws Exception {
        ServletHolder holder = new ServletHolder(DispatcherServlet.class);
        holder.setInitParameter("contextClass", AnnotationConfigWebApplicationContext.class.getName());
        holder.setInitParameter("contextConfigLocation", configurationClass.getName());
        holder.setInitOrder(1);
        context.addServlet(holder, this.servletPrefix);
    }
}