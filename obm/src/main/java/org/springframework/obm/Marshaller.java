package org.springframework.obm;


import org.springframework.oxm.XmlMappingException;

import java.io.IOException;
import java.io.OutputStream;

public interface Marshaller <T> {

    boolean supports(Class<T> clazz);

   void marshal(T graph, OutputStream result) throws IOException, XmlMappingException;

}
