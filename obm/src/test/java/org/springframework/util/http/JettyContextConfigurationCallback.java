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