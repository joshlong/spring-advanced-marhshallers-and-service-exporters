/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.util.http;


import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * A jetty configuration callback that knows how to register Spring MVC and the {@link org.springframework.web.servlet.DispatcherServlet}
 *
 * @author Josh Long
 */
public class DispatcherServletJettyConfigurationCallback implements JettyContextConfigurationCallback {

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