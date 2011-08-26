package org.springframework.http.converter.obm.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Josh Long
 */
public class DebugClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private Log log = LogFactory.getLog(getClass());
    private MediaType mediaType;

    public DebugClientHttpRequestInterceptor(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    private void debug(String title, HttpHeaders headers) {
        if (log.isDebugEnabled()) {
            log.debug("============== " + title + " ==============");
        }
        for (String k : headers.keySet()) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("%s = %s", k, headers.get(k)));
            }
        }
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().setAccept(Arrays.asList(this.mediaType));
        debug("Request: ", request.getHeaders());
        return execution.execute(request, body);
    }

}