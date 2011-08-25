package org.springframework.remoting.avro;

import org.apache.avro.ipc.ResponderRequestHandlerUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.avro.AvroHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Simple implementation of {@link org.springframework.web.HttpRequestHandler} that adapts {@link org.apache.avro.ipc.ResponderServlet}
 *
 * @author Josh Long
 */
public class AvroServiceExporter extends AbstractAvroExporter implements HttpRequestHandler {

	private MediaType mediaType = AvroHttpMessageConverter.MEDIA_TYPE;

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Assert.notNull(this.mediaType, "the 'mediaType' property should not be null");
		ResponderRequestHandlerUtils.handleRequest(this.mediaType, request, response, getResponder());
	}
}
