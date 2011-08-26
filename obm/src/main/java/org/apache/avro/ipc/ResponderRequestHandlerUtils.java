package org.apache.avro.ipc;

import org.apache.avro.AvroRuntimeException;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * This is required to get insight into the package private <EM>static</EM> methods on {@link ResponderServlet} which are crucial for
 * the server side component of any HTTP based server
 * <p/>
 * <STRONG>Hackety.</STRONG>
 *
 * @author Josh Long
 */
abstract public class ResponderRequestHandlerUtils {

    /**
     * this is only useful to make the various package private methods on {@link HttpTransceiver} visible to other components.
     * <p/>
     * This code is taken almost verbatim from {@link ResponderServlet}
     */
    public static void handleRequest(MediaType mediaType, HttpServletRequest request, HttpServletResponse response, Responder responder) throws IOException, ServletException {
        response.setContentType(mediaType.toString());
        List<ByteBuffer> requestBufs = HttpTransceiver.readBuffers(request.getInputStream());
        try {
            List<ByteBuffer> responseBufs = responder.respond(requestBufs);
            response.setContentLength(HttpTransceiver.getLength(responseBufs));
            HttpTransceiver.writeBuffers(responseBufs, response.getOutputStream());
        } catch (AvroRuntimeException e) {
            throw new ServletException(e);
        }
    }
}
