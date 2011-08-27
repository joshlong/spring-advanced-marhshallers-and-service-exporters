package org.springframework.util.http;

import org.mortbay.jetty.servlet.Context;

/**
 * <p> hook so that clients may configure a working jetty server's {@link Context}</P>
 * <P> the advantage in exposing this as a separate interface is that we can now
 * reuse the common recipes (such as for Spring MVC's {@link org.springframework.web.servlet.DispatcherServlet})
 * </p>
 *
 * @author Josh Long
 */
public interface JettyContextConfigurationCallback {
    void configure(Context c) throws Exception;
}