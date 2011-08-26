package org.springframework.http.converter.obm.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

import java.io.IOException;

public class DebuggingResponseExtractor implements ResponseExtractor<Object> {
    private Log log = LogFactory.getLog(getClass());

    @Override
    public Object extractData(ClientHttpResponse response) throws IOException {
        debug("Response: ", response.getHeaders());
        return null;
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
}