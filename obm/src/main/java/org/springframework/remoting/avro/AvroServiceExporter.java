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
package org.springframework.remoting.avro;

import org.apache.avro.ipc.ResponderRequestHandlerUtils;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p/>
 * Simple implementation of {@link org.springframework.web.HttpRequestHandler} that adapts {@link org.apache.avro.ipc.ResponderServlet}
 * <p/>
 * This can be registered in any servlet container through the standard Spring web machinery.
 * <p/>
 * You do not need a ful {@link org.springframework.web.servlet.DispatcherServlet}, a {@link org.springframework.web.context.support.HttpRequestHandlerServlet}
 * will suffice, although it lacks the mapping support in the {@link org.springframework.web.servlet.DispatcherServlet}.
 *
 * @author Josh Long
 * @see org.springframework.remoting.caucho.HessianServiceExporter
 */
public class AvroServiceExporter extends AbstractAvroExporter implements HttpRequestHandler {

    private MediaType mediaType = new MediaType("avro", "binary");

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Assert.notNull(this.mediaType, "the 'mediaType' property should not be null");
        ResponderRequestHandlerUtils.handleRequest(this.mediaType, request, response, getResponder());
    }
}
